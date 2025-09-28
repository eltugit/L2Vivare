package gr.sr.events.engine;

import gabriel.epicRaid.EpicRaidManager;
import gr.sr.events.Configurable;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventRewardSystem.EventRewards;
import gr.sr.events.engine.EventRewardSystem.PositionContainer;
import gr.sr.events.engine.EventRewardSystem.RewardItem;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.ConfigModel.InputType;
import gr.sr.events.engine.base.SpawnType;
import gr.sr.events.engine.base.RewardPosition.PositionType;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager.EventScheduleData;
import gr.sr.events.engine.main.MainEventManager.RegNpcLoc;
import gr.sr.events.engine.main.MainEventManager.State;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.main.base.MainEventInstanceTypeManager;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.mini.*;
import gr.sr.events.engine.mini.EventMode.FeatureCategory;
import gr.sr.events.engine.mini.EventMode.FeatureType;
import gr.sr.events.engine.mini.FeatureBase.FeatureInfo;
import gr.sr.events.engine.mini.ScheduleInfo.Day;
import gr.sr.events.engine.mini.ScheduleInfo.RunTime;
import gr.sr.events.engine.mini.features.AbstractFeature;
import gr.sr.events.engine.mini.features.AbstractFeature.FeatureConfig;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;
import l2r.gameserver.cache.HtmCache;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventManagement {
    private final Map<Integer, EventManagement.EditingInfo> _editingInfos = new ConcurrentHashMap();
    public static final int TRAINING_DUMMY_ID = 31691;
    private final String[] miniEventEditingPages = new String[]{"Maps", "Modes", "Rewards", "Matches", "Configs"};
    private final String[] mainEventEditingPages = new String[]{"Maps", "Rewards", "Configs", "Instances"};

    public EventManagement() {
    }

    public boolean commandRequiresConfirm(String command) {
        if (command.startsWith("remove_event_map")) {
            return true;
        } else if (command.startsWith("mini_edit_modes_delete")) {
            return true;
        } else if (command.startsWith("mini_edit_modes_clear")) {
            return true;
        } else if (command.startsWith("mini_edit_modes_scheduler_removetime")) {
            return true;
        } else if (command.startsWith("mini_edit_feature_remove")) {
            return true;
        } else if (command.startsWith("mini_abort_match")) {
            return true;
        } else if (command.startsWith("abort_current")) {
            return true;
        } else {
            return command.startsWith("remove_event_from_map");
        }
    }

    public void showMenu(PlayerEventInfo gm, boolean miniEvents) {
        if (SunriseLoader.loaded()) {
            String html;
            if (!miniEvents) {
                html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_menu.htm");
            } else {
                html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_menu_mini.htm");
            }

            if (html == null) {
                gm.sendMessage("HTML files (or at least eventmanage_menu.htm) are missing.");
            } else {
                html = html.replaceAll("%objectId%", String.valueOf(0));
                AbstractMainEvent event = EventManager.getInstance().getCurrentMainEvent();
                html = html.replaceAll("%event%", event == null ? "N/A" : event.getEventType().getAltTitle());
                html = html.replaceAll("%map%", event == null ? "N/A" : EventManager.getInstance().getMainEventManager().getMapName());
                String time = EventManager.getInstance().getMainEventManager().getTimeLeft(true);
                html = html.replaceAll("%time%", event == null ? "N/A" : time);
                html = html.replaceAll("%players%", String.valueOf(EventManager.getInstance().getMainEventManager().getPlayersCount()));
                html = html.replaceAll("%state%", EventManager.getInstance().getMainEventManager().getState().toString());
                html = html.replaceAll("%pauseName%", EventManager.getInstance().getMainEventManager().autoSchedulerPaused() ? "Unpause" : "Pause");
                String runStopButton = EventManager.getInstance().getMainEventManager().autoSchedulerEnabled() ? "<button value=\"Stop\" action=\"bypass admin_event_manage abort_auto_scheduler\" width=85 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">" : "<button value=\"Start\" action=\"bypass admin_event_manage restart_auto_scheduler\" width=85 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
                html = html.replaceAll("%runStop%", runStopButton);
                if (EventManager.getInstance().getMainEventManager().getState() == State.RUNNING) {
                    if (event != null) {
                        if (event.isWatching(gm)) {
                            html = html.replaceAll("%runEventAction%", "<button value=\"Stop watching\" action=\"bypass -h admin_event_manage watch_current_stop\" width=78 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                        } else {
                            html = html.replaceAll("%runEventAction%", "<button value=\"Watch event\" action=\"bypass -h admin_event_manage watch_current_menu\" width=78 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                        }
                    }
                } else {
                    html = html.replaceAll("%runEventAction%", "<button value=\"Skip delay\" action=\"bypass -h admin_event_manage skip_current\" width=78 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                }

                String params;
                if (miniEvents) {
                    StringBuilder tb = new StringBuilder();
                    boolean bg = false;
                    EventType[] var9 = EventType.values();
                    int var10 = var9.length;

                    for (int var11 = 0; var11 < var10; ++var11) {
                        EventType type = var9[var11];
                        if (EventConfig.getInstance().isEventAllowed(type) && !type.isRegularEvent() && type.allowEdits() && EventManager.getInstance().getMiniEvents().get(type) != null) {
                            Iterator var13 = ((Map) EventManager.getInstance().getMiniEvents().get(type)).entrySet().iterator();

                            while (var13.hasNext()) {
                                Entry<Integer, MiniEventManager> e = (Entry) var13.next();
                                if (((MiniEventManager) e.getValue()).getMode().isAllowed() && ((MiniEventManager) e.getValue()).getMode().isRunning()) {
                                    tb.append("<table width=270" + (bg ? " bgcolor=4f4f4f" : "") + "><tr>");
                                    tb.append("<td width=120><font color=ac9775>" + type.getHtmlTitle() + "</font></td><td width=130><font color=9f9f9f>" + ((MiniEventManager) e.getValue()).getMode().getVisibleName() + "</font>" + "</td>");
                                    tb.append("<td width=45><a action=\"bypass admin_event_manage mini_viewinfo " + e.getKey() + " " + type.getAltTitle() + "\"><font color=9f9f9f>View</font></a></td>");
                                    tb.append("<td width=45><a action=\"bypass -h admin_event_manage mini_stopevent " + e.getKey() + " " + type.getAltTitle() + "\"><font color=9f9f9f>Stop</font></a></td>");
                                    tb.append("<td width=30><center>");

                                    try {
                                        int count = ((MiniEventManager) e.getValue()).getRegisteredTeamsCount();
                                        tb.append("<font color=ac9775>" + count + "</font>");
                                    } catch (Exception var16) {
                                        SunriseLoader.debug("sent invalid bypass, event " + type.getAltTitle(), Level.WARNING);
                                        tb.append("<font color=B46F6B>N/A</font>");
                                    }

                                    tb.append("</center></td></tr></table>");
                                    bg = !bg;
                                }
                            }
                        }
                    }

                    params = tb.toString();
                    html = html.replaceAll("%miniEvents%", params);
                }

                String s = EventManager.getInstance().getMainEventManager().autoSchedulerEnabled() ? "<font color=74BE85>Enabled</font>" : "<font color=B46F6B>Disabled</font>";
                if (EventManager.getInstance().getMainEventManager().autoSchedulerPaused()) {
                    s = "<font color=D1A261>Paused</font>";
                }

                html = html.replaceAll("%auto_scheduler%", s);
                html = html.replaceAll("%auto_future%", EventManager.getInstance().getMainEventManager().getAutoSchedulerDelay());
                html = html.replaceAll("%auto_nextevent%", EventManager.getInstance().getMainEventManager().autoSchedulerEnabled() + "");
                html = html.replaceAll("%auto_nextmap%", EventManager.getInstance().getMainEventManager().autoSchedulerEnabled() + "");
                StringBuilder tb = new StringBuilder();
                if (EventManager.getInstance().getMainEventManager().getState() == State.IDLE) {
                    tb.append("<tr>");
                    tb.append("<td><font color=ac9887>Last event was:</font></td>");
                    if (EventManager.getInstance().getMainEventManager().getLastEventOrder() == null) {
                        tb.append("<td><font color=9f9f9f>" + EventManager.getInstance().getMainEventManager().getLastEventTime() + "</font></td>");
                    } else {
                        tb.append("<td><font color=9f9f9f>" + EventManager.getInstance().getMainEventManager().getLastEventTime() + " (" + EventManager.getInstance().getMainEventManager().getLastEventOrder().getAltTitle() + ")</font></td>");
                    }

                    tb.append("</tr>");
                }

                params = tb.toString();
                html = html.replaceAll("%additionalParams%", params);
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    public void watchCurrentMenu(PlayerEventInfo gm) {
        if (SunriseLoader.loaded()) {
            if (EventManager.getInstance().getMainEventManager().getState() != State.RUNNING) {
                gm.sendMessage("Event can be only watched when in running state.");
            } else {
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_watch_menu.htm");
                AbstractMainEvent event = EventManager.getInstance().getCurrentMainEvent();
                if (event.getInstances() == null) {
                    gm.sendMessage("Instances for the event haven't been initialized yet, please try it in a few seconds.");
                } else {
                    html = html.replaceAll("%event%", event == null ? "N/A" : event.getEventType().getAltTitle());
                    html = html.replaceAll("%map%", event == null ? "N/A" : EventManager.getInstance().getMainEventManager().getMapName());
                    StringBuilder tb = new StringBuilder();
                    tb.append("<table>");
                    InstanceData[] var5 = event.getInstances();
                    int var6 = var5.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        InstanceData inst = var5[var7];
                        tb.append("<tr>");
                        tb.append("<td><button value=\"" + inst.getName() + " (" + inst.getId() + ")\" action=\"bypass -h admin_event_manage watch_current_event " + inst.getId() + "\" width=230 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                    }

                    tb.append("</table>");
                    html = html.replaceAll("%instances%", tb.toString());
                    html = html.replaceAll("%title%", "Event Engine");
                    gm.sendPacket(html);
                    gm.sendStaticPacket();
                }
            }
        }
    }

    public void viewMiniEventInfo(PlayerEventInfo gm, int modeId, String type) {
        EventType eventType = EventType.getType(type);
        MiniEventManager event = EventManager.getInstance().getMiniEvent(eventType, modeId);
        if (!eventType.isRegularEvent() && event != null) {
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = eventType;
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = modeId;
            this.showModesMenu(gm);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    public void stopMiniEvent(PlayerEventInfo gm, int modeId, String type) {
        EventType eventType = EventType.getType(type);
        MiniEventManager event = EventManager.getInstance().getMiniEvent(eventType, modeId);
        if (!eventType.isRegularEvent() && event != null) {
            event.getMode().setAllowed(false);
            gm.sendMessage("Mode disabled.");
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    public void onBypass(PlayerEventInfo gm, String action) {
        try {
            if (!this._editingInfos.containsKey(gm.getPlayersId())) {
                this._editingInfos.put(gm.getPlayersId(), new EventManagement.EditingInfo());
            }

            if (action.equals("menu")) {
                this.showMenu(gm, false);
            } else if (action.equals("reload_html")) {
                //EventManager.getInstance().getHtmlManager().loadAdminHtmls();
                gm.sendMessage("Engine's HTML files successfuly reloaded.");
            } else if (action.equals("reload_stats")) {
                EventStatsManager.getInstance().reload();
                gm.sendMessage("Statistics successfuly reloaded.");
            } else if (action.equals("debug_sign")) {
                this.gmDebug(gm);
            } else {
                int id;
                int regTime;
                if (action.equals("regall")) {
                    if (EventManager.getInstance().getMainEventManager().getState() != State.REGISTERING) {
                        gm.sendMessage("The event must be in registering phase.");
                        return;
                    }

                    id = 0;
                    PlayerEventInfo[] var37 = CallBack.getInstance().getOut().getAllPlayers();
                    id = var37.length;

                    for (regTime = 0; regTime < id; ++regTime) {
                        PlayerEventInfo player = var37[regTime];
                        if (EventManager.getInstance().getMainEventManager().registerPlayer(player)) {
                            player.screenMessage("GM has registered you to the event.", "Event Engine", true);
                            ++id;
                        }
                    }

                    gm.sendMessage(id + " players have been registered to the event.");
                } else {
                    String s;
                    if (action.equals("back")) {
                        s = this.getGoBackPage(gm);
                        if (s != null) {
                            this.onBypass(gm, s);
                        }
                    } else if (action.equals("menu_mini")) {
                        this.showMenu(gm, true);
                    } else if (action.startsWith("new_event_runtime_edit")) {
                        this.showNewEventMenu(gm, EventType.getType(action.substring(10)), true);
                    } else {
                        StringTokenizer st;
                        if (action.startsWith("new_event")) {
                            st = new StringTokenizer(action);
                            st.nextToken();
                            if (st.hasMoreTokens()) {
                                this.showNewEventMenu(gm, EventType.getType(action.substring(10)), false);
                            } else {
                                this.showNewEventMenu(gm, (EventType) null, false);
                            }
                        } else {
                            String event;
                            String value;
                            EventType type;
                            if (action.startsWith("event_start")) {
                                s = action.substring(12);
                                String[] splitted = s.split(";");
                                type = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
                                regTime = Integer.parseInt(splitted[0].trim());
                                event = splitted[1].trim();
                                value = splitted[2].trim();
                                int runTime = Integer.parseInt(splitted[3].trim());
                                if(!EpicRaidManager.getInstance().isStarted()) {
                                    EventManager.getInstance().getMainEventManager().startEvent(gm, type, regTime, event, value, runTime);
                                }
                            } else if (action.startsWith("abort_current")) {
                                EventManager.getInstance().getMainEventManager().abort(gm, false);
                                this.showMenu(gm, false);
                            } else if (action.startsWith("skip_current")) {
                                EventManager.getInstance().getMainEventManager().skipDelay(gm);
                            } else if (action.startsWith("watch_current_menu")) {
                                this.watchCurrentMenu(gm);
                            } else if (action.startsWith("watch_current_event")) {
                                EventManager.getInstance().getMainEventManager().watchEvent(gm, Integer.parseInt(action.substring(20)));
                            } else if (action.startsWith("watch_current_stop")) {
                                EventManager.getInstance().getMainEventManager().stopWatching(gm);
                            } else if (action.startsWith("abort_auto_scheduler")) {
                                EventManager.getInstance().getMainEventManager().abortAutoScheduler(gm);
                                this.showMenu(gm, false);
                            } else if (action.startsWith("pause_auto_scheduler")) {
                                if (!EventManager.getInstance().getMainEventManager().autoSchedulerPaused()) {
                                    EventManager.getInstance().getMainEventManager().pauseAutoScheduler(gm);
                                } else {
                                    EventManager.getInstance().getMainEventManager().unpauseAutoScheduler(gm, true);
                                }

                                this.showMenu(gm, false);
                            } else if (action.startsWith("restart_auto_scheduler")) {
                                EventManager.getInstance().getMainEventManager().restartAutoScheduler(gm);
                                this.showMenu(gm, false);
                            } else if (action.startsWith("config_auto_scheduler")) {
                                this.showGlobalConfigMenu(gm, "Scheduler", 0);
                            } else if (action.startsWith("abort_current")) {
                                EventManager.getInstance().getMainEventManager().abort(gm, false);
                            } else if (action.startsWith("skip_current")) {
                                EventManager.getInstance().getMainEventManager().skipDelay(gm);
                            } else {
                                String action3;
                                if (action.startsWith("globalconfig_menu")) {
                                    st = new StringTokenizer(action);
                                    st.nextToken();
                                    action3 = st.nextToken();
                                    id = Integer.valueOf(st.nextToken());
                                    this.showGlobalConfigMenu(gm, action3, id);
                                } else {
                                    String key;
                                    if (action.startsWith("globalconfig_showinfo")) {
                                        st = new StringTokenizer(action);
                                        st.nextToken();
                                        action3 = st.nextToken();
                                        id = Integer.valueOf(st.nextToken());
                                        key = st.nextToken();
                                        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigKeyShown.equals(key)) {
                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigKeyShown = "";
                                        } else {
                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigKeyShown = key;
                                        }

                                        this.showGlobalConfigMenu(gm, action3, id);
                                    } else if (action.startsWith("globalconfig_edit")) {
                                        st = new StringTokenizer(action);
                                        st.nextToken();
                                        action3 = st.nextToken();
                                        this.showGlobalConfigEditation(gm, action3);
                                    } else {
                                        String param;
                                        if (action.startsWith("globalconfig_setvalue")) {
                                            st = new StringTokenizer(action);
                                            st.nextToken();
                                            action3 = st.nextToken();
                                            param = "";

                                            while (st.hasMoreTokens()) {
                                                param = param + st.nextToken();
                                                if (st.hasMoreTokens()) {
                                                    param = param + " ";
                                                }
                                            }

                                            if (param.length() == 0) {
                                                gm.sendMessage("Wrong value.");
                                                return;
                                            }

                                            this.setGlobalConfigValue(gm, action3, param);
                                        } else if (action.startsWith("eventlang_menu")) {
                                            this.showLanguageSelectMenu(gm);
                                        } else if (action.startsWith("set_language")) {
                                            s = action.substring(13);
                                            LanguageEngine.setLanguage(s);
                                            gm.sendMessage("Language successfully set to: " + s);
                                            this.showLanguageSelectMenu(gm);
                                        } else if (action.startsWith("reload_global_configs")) {
                                            EventConfig.getInstance().loadGlobalConfigs();
                                            this.showGlobalConfigMenu(gm, "Core", 1);
                                            gm.sendMessage("Global Configs successfully reloaded from database.");
                                        } else if (action.startsWith("reload_configs")) {
                                            Configurable ev = EventManager.getInstance().getEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent);
                                            if (ev != null) {
                                                ev.clearConfigs();
                                                ev.loadConfigs();
                                            }

                                            this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                        } else if (action.startsWith("expand_configmodel")) {
                                            st = new StringTokenizer(action);
                                            st.nextToken();
                                            action3 = st.nextToken();
                                            param = st.nextToken();
                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory = action3;
                                            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown.equals(param)) {
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown = "";
                                            } else {
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown = param;
                                            }

                                            if (action3.equals("InstanceType")) {
                                                this.showEditMainInstanceTypeWindow(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType);
                                            } else if (action3.equals("Event")) {
                                                this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                            } else if (action3.equals("MapConfig")) {
                                                this.showMapEditationEvents(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                            }
                                        } else if (action.startsWith("eventorder_menu")) {
                                            this.showEventOrderMenu(gm, (String) null);
                                        } else if (action.startsWith("eventorder_move")) {
                                            st = new StringTokenizer(action);
                                            st.nextToken();
                                            action3 = st.nextToken();
                                            param = st.nextToken();
                                            this.moveEventInOrder(gm, action3, param);
                                        } else {
                                            if (action.startsWith("mini_")) {
                                                s = action.substring(5);
                                                if (s.startsWith("menu")) {
                                                    this.showMiniEventMenu(gm);
                                                    this.setGoBackPage(gm, action);
                                                } else if (s.startsWith("edit_event")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    id = Integer.parseInt(st.nextToken());
                                                    key = st.hasMoreTokens() ? st.nextToken() : null;
                                                    event = st.hasMoreTokens() ? st.nextToken() : null;
                                                    if (event == null) {
                                                        this.showEditMiniEventMenu(gm, id, key);
                                                    } else {
                                                        type = EventType.getType(event);
                                                        this.showEditMiniEventMenu(gm, type.getId(), key);
                                                    }

                                                    this.setGoBackPage(gm, action);
                                                } else if (s.startsWith("viewinfo")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    id = Integer.parseInt(st.nextToken());
                                                    key = st.nextToken();
                                                    this.viewMiniEventInfo(gm, id, key);
                                                } else if (s.startsWith("stopevent")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    id = Integer.parseInt(st.nextToken());
                                                    key = st.nextToken();
                                                    this.stopMiniEvent(gm, id, key);
                                                } else if (s.startsWith("set_active_mode")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
                                                        gm.sendMessage("Event Modes engine is not implemented for Main events yet.");
                                                        return;
                                                    }

                                                    param = st.nextToken();
                                                    key = st.nextToken();
                                                    this.setModeId(gm, key);
                                                    if (param.equals("menu")) {
                                                        this.showModesMenu(gm);
                                                    } else if (param.equals("reward")) {
                                                        this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
                                                    } else if (param.equals("match")) {
                                                        this.showMatches(gm);
                                                    }
                                                } else if (s.startsWith("modes_menu_compact")) {
                                                    this.showCompactModesMenu(gm);
                                                } else if (s.startsWith("modes_menu")) {
                                                    this.showModesMenu(gm);
                                                } else if (s.startsWith("edit_modes_menu")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    if (st.hasMoreTokens()) {
                                                        id = Integer.parseInt(st.nextToken());
                                                    } else {
                                                        id = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId;
                                                    }

                                                    this.showEditModeWindow(gm, id);
                                                } else if (s.startsWith("edit_modes_maps_menu")) {
                                                    this.showEditModeMaps(gm);
                                                } else if (s.startsWith("edit_modes_map")) {
                                                    this.allowDisallowMapMode(gm, s.substring(15));
                                                    this.showEditModeMaps(gm);
                                                } else if (s.startsWith("edit_modes_new")) {
                                                    this.createNewModeMenu(gm);
                                                } else if (s.startsWith("create_mode")) {
                                                    this.createNewMode(gm, s.substring(12));
                                                } else if (s.startsWith("edit_modes_delete")) {
                                                    this.deleteMode(gm);
                                                    this.showModesMenu(gm);
                                                } else if (s.startsWith("edit_modes_enabledisable")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    boolean b = false;
                                                    if (st.hasMoreTokens()) {
                                                        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = Integer.parseInt(st.nextToken());
                                                        b = true;
                                                    }

                                                    this.enableDisableMode(gm);
                                                    if (!b) {
                                                        this.showEditModeWindow(gm);
                                                    } else {
                                                        this.showCompactModesMenu(gm);
                                                    }
                                                } else if (s.startsWith("edit_modes_generatefile")) {
                                                    this.createRegFile(gm);
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_clear")) {
                                                    this.clearEvent(gm);
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_set_name")) {
                                                    this.setModeName(gm, s.substring(20), false);
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_vis_name")) {
                                                    this.setModeName(gm, s.substring(20), true);
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_set_npcid")) {
                                                    this.setModeNpcId(gm, s.substring(21));
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_set_info")) {
                                                    this.showEditModeWindow(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_menu")) {
                                                    this.showEditModeSchedulerMenu(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_edit")) {
                                                    this.showEditModeSchedulerTime(gm, Integer.parseInt(s.substring(26)));
                                                } else if (s.startsWith("edit_modes_scheduler_removeday")) {
                                                    this.schedulerRemoveDay(gm, s.substring(31));
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_addday")) {
                                                    this.schedulerAddDay(gm, s.substring(28));
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_set_from")) {
                                                    this.schedulerSetHour(gm, s.substring(30), true);
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_set_to")) {
                                                    this.schedulerSetHour(gm, s.substring(28), false);
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_removetime")) {
                                                    this.scheduleRemoveTime(gm);
                                                    this.showEditModeSchedulerMenu(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_newtime")) {
                                                    this.schedulerAddNewTime(gm);
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_refresh1")) {
                                                    this.schedulerRefresh(gm);
                                                    this.showEditModeSchedulerTime(gm);
                                                } else if (s.startsWith("edit_modes_scheduler_refresh2")) {
                                                    this.schedulerRefresh(gm);
                                                    this.showEditModeSchedulerMenu(gm);
                                                } else if (s.startsWith("edit_feature_showinfo")) {
                                                    this.setActiveShowedFeature(gm, s.substring(22));
                                                    this.showNewMiniEventFeatureMenu(gm, "Default");
                                                } else if (s.startsWith("edit_feature_setcategory")) {
                                                    this.setActiveFeatureCategory(gm, s.substring(25));
                                                    this.showNewMiniEventFeatureMenu(gm, "Default");
                                                } else if (s.startsWith("edit_feature_add")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    param = st.hasMoreTokens() ? st.nextToken() : "Default";
                                                    this.showNewMiniEventFeatureMenu(gm, param);
                                                } else if (s.startsWith("edit_feature_remove")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    param = st.nextToken();
                                                    this.removeFeature(gm, param);
                                                    this.showModesMenu(gm);
                                                } else if (s.startsWith("edit_featureconfig_set ")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    param = st.nextToken();
                                                    key = st.nextToken();
                                                    StringBuilder tb = new StringBuilder();

                                                    while (st.hasMoreTokens()) {
                                                        tb.append(st.nextToken() + " ");
                                                    }

                                                    value = tb.toString();
                                                    this.setFeatureConfigValue(gm, param, key, value.substring(0, value.length() - 1));
                                                    this.showEditFeatureConfig(gm, param, key);
                                                } else if (s.startsWith("edit_featureconfig ")) {
                                                    st = new StringTokenizer(s);
                                                    st.nextToken();
                                                    this.showEditFeatureConfig(gm, st.nextToken(), st.nextToken());
                                                } else if (s.startsWith("edit_feature ")) {
                                                    this.showEditFeature(gm, s.substring(13));
                                                } else if (s.startsWith("show_matches")) {
                                                    this.showMatches(gm);
                                                } else if (s.startsWith("edit_match")) {
                                                    this.editMatch(gm, Integer.parseInt(s.substring(11)));
                                                } else if (s.startsWith("watch_match")) {
                                                    this.watchMatch(gm);
                                                    this.editMatch(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMatch);
                                                } else if (s.startsWith("abort_match")) {
                                                    this.abortMatch(gm);
                                                    this.showMatches(gm);
                                                } else if (s.startsWith("show_team_members")) {
                                                    this.showTeamMembers(gm, Integer.parseInt(s.substring(18)));
                                                } else if (s.startsWith("manual_match")) {
                                                    action3 = s.substring(13);
                                                    if (action3.startsWith("menu")) {
                                                        this.showManualMatchMenu(gm);
                                                    } else if (!action3.startsWith("add_team")) {
                                                        if (action3.startsWith("rem_team")) {
                                                            try {
                                                                id = Integer.parseInt(action3.substring(9));
                                                            } catch (Exception var11) {
                                                                id = 0;
                                                            }

                                                            if (id != 0 || gm.getTarget() != null && !gm.getTarget().isPlayer()) {
                                                                Iterator var32 = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.entrySet().iterator();

                                                                while (var32.hasNext()) {
                                                                    Entry<Integer, List<PlayerEventInfo>> element = (Entry) var32.next();
                                                                    Iterator var9 = ((List) element.getValue()).iterator();

                                                                    while (var9.hasNext()) {
                                                                        PlayerEventInfo player = (PlayerEventInfo) var9.next();
                                                                        if (id == 0 && player.getPlayersId() == gm.getTarget().getObjectId() || player.getPlayersId() == id) {
                                                                            ((List) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.get(element.getKey())).remove(player);
                                                                            gm.sendMessage("Done. Player " + player.getPlayersName() + " has been removed successfully.");
                                                                            this.showManualMatchMenu(gm);
                                                                            return;
                                                                        }
                                                                    }
                                                                }

                                                                gm.sendMessage("Yout target is not in the list of went offline.");
                                                                return;
                                                            }

                                                            gm.sendMessage("You must target player which you want to remove from the event.");
                                                            return;
                                                        }

                                                        if (action3.startsWith("set_map_menu")) {
                                                            this.showManualMatchSetMapMenu(gm);
                                                        } else if (action3.startsWith("set_map")) {
                                                            this.setManualMatchMap(gm, Integer.parseInt(action3.substring(8)));
                                                            this.showManualMatchMenu(gm);
                                                        } else if (action3.startsWith("start")) {
                                                            this.startManualMatch(gm);
                                                        } else if (!action3.startsWith("abort") && action3.startsWith("clear")) {
                                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.clear();
                                                            this.showManualMatchMenu(gm);
                                                        }
                                                    } else {
                                                        if (gm.getTarget() != null && (gm.getTarget().isPlayer() || gm.getTarget().isSummon())) {
                                                            if (gm.getTarget().getEventInfo() != null && !gm.getTarget().getEventInfo().isRegistered()) {
                                                                id = Integer.parseInt(action3.substring(9));
                                                                if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.get(id) == null) {
                                                                    ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.put(id, new LinkedList());
                                                                }

                                                                ((List) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.get(id)).add(gm.getTarget().getEventInfo());
                                                                gm.sendMessage("Done. Player " + gm.getTarget().getName() + " has been added.");
                                                                this.showManualMatchMenu(gm);
                                                                return;
                                                            }

                                                            gm.sendMessage("Your target is already registered to another event.");
                                                            return;
                                                        }

                                                        gm.sendMessage("You must target player or his servitor which you want to add into the event.");
                                                        return;
                                                    }
                                                }
                                            } else if (action.startsWith("set_available")) {
                                                type = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
                                                if (EventConfig.getInstance().isEventAllowed(type)) {
                                                    this.removeAvailableEvent(gm);
                                                } else {
                                                    this.addAvailableEvent(gm);
                                                }

                                                if (type.isRegularEvent()) {
                                                    this.showEditEventMenu(gm, type.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection);
                                                } else {
                                                    this.showEditMiniEventMenu(gm, type, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection);
                                                }
                                            } else if (action.startsWith("event_configs_menu_page")) {
                                                id = Integer.parseInt(action.substring(24));
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage = id;
                                                this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                            } else if (action.startsWith("show_configs_menu")) {
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage = 1;
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingConfigCategory = "General";
                                                this.showConfigsMenu(gm, action.substring(18), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                            } else if (action.startsWith("show_configs_category")) {
                                                st = new StringTokenizer(action);
                                                st.nextToken();
                                                action3 = st.nextToken();
                                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingConfigCategory = action3;
                                                this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                            } else if (action.startsWith("show_config")) {
                                                s = action.substring(12);
                                                this.showConfig(gm, s);
                                            } else if (action.startsWith("set_config")) {
                                                st = new StringTokenizer(action);
                                                st.nextToken();
                                                action3 = st.nextToken();
                                                if (st.hasMoreTokens()) {
                                                    param = st.nextToken();
                                                } else {
                                                    param = "";
                                                }

                                                this.setConfig(gm, action3, param, false);
                                                this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                            } else {
                                                int mapId;
                                                if (action.startsWith("remove_multiadd_config_value")) {
                                                    st = new StringTokenizer(action);
                                                    st.nextToken();
                                                    mapId = Integer.parseInt(st.nextToken());
                                                    param = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory;
                                                    key = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown;
                                                    if (param.equals("InstanceType")) {
                                                        AbstractMainEvent ev = EventManager.getInstance().getMainEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent);
                                                        if (ev == null) {
                                                            gm.sendMessage("This event doesn't exist.");
                                                            return;
                                                        }

                                                        MainEventInstanceType instance = ev.getInstanceType(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType);
                                                        ((ConfigModel) instance.getConfigs().get(key)).removeMultiAddValueIndex(mapId);
                                                        MainEventInstanceTypeManager.getInstance().updateInstanceType(instance);
                                                        this.showEditMainInstanceTypeWindow(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType);
                                                    } else if (param.equals("Event")) {
                                                        SunriseLoader.debug("removing on index " + mapId + " of config " + key);
                                                        EventConfig.getInstance().removeConfigMultiAddValue(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, key, mapId);
                                                        this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                                    } else if (param.equals("MapConfig")) {
                                                        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                        if (map != null) {
                                                            EventConfig.getInstance().removeMapConfigMultiAddValue(map, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, key, mapId);
                                                            this.showMapEditationEvents(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                        } else {
                                                            gm.sendMessage("This map doesn't exist.");
                                                        }
                                                    }
                                                } else if (action.startsWith("addto_config")) {
                                                    st = new StringTokenizer(action);
                                                    st.nextToken();
                                                    action3 = st.nextToken();
                                                    param = st.nextToken();
                                                    this.setConfig(gm, action3, param, true);
                                                    this.showConfigsMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage);
                                                } else if (action.startsWith("event_gm_message_all ")) {
                                                    this.eventGmMessage(gm, action.substring(21), false);
                                                    this.eventGmMessageMenu(gm);
                                                } else if (action.startsWith("event_gm_message_menu")) {
                                                    this.eventGmMessageMenu(gm);
                                                } else if (action.startsWith("edit_main_instance_type ")) {
                                                    id = Integer.parseInt(action.substring(24));
                                                    this.showEditMainInstanceTypeWindow(gm, id);
                                                } else if (action.startsWith("delete_main_instance_type")) {
                                                    id = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType;
                                                    this.deleteMainInstanceType(gm, id);
                                                    this.showEditEventInstancesMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent);
                                                } else if (action.startsWith("edit_main_instance_type_set_name")) {
                                                    s = action.substring(33);
                                                    mapId = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType;
                                                    this.editMainInstanceType(gm, mapId, "set_name", s, false);
                                                    this.showEditMainInstanceTypeWindow(gm, mapId);
                                                } else if (action.startsWith("create_new_main_instance")) {
                                                    this.showEditMainInstanceTypeWindow(gm, -1);
                                                } else if (action.startsWith("edit_main_instance_type_set_config")) {
                                                    id = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType;
                                                    st = new StringTokenizer(action);
                                                    st.nextToken();
                                                    param = st.nextToken();
                                                    if (st.hasMoreTokens()) {
                                                        key = st.nextToken();
                                                    } else {
                                                        key = "";
                                                    }

                                                    this.editMainInstanceType(gm, id, param, key, false);
                                                    this.showEditMainInstanceTypeWindow(gm, id);
                                                } else if (action.startsWith("edit_main_instance_type_addto_config")) {
                                                    id = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType;
                                                    st = new StringTokenizer(action);
                                                    st.nextToken();
                                                    param = st.nextToken();
                                                    key = st.nextToken();
                                                    this.editMainInstanceType(gm, id, param, key, true);
                                                    this.showEditMainInstanceTypeWindow(gm, id);
                                                } else if (action.startsWith("edit_event ")) {
                                                    st = new StringTokenizer(action);
                                                    st.nextToken();
                                                    action3 = st.hasMoreTokens() ? st.nextToken() : null;
                                                    param = st.hasMoreTokens() ? st.nextToken() : null;
                                                    this.showEditEventMenu(gm, action3, param);
                                                    this.setGoBackPage(gm, action);
                                                } else if (action.startsWith("edit_events_menu")) {
                                                    this.showEditEventsMenu(gm);
                                                    this.setGoBackPage(gm, action);
                                                } else if (!action.startsWith("tournament_menu ")) {
                                                    if (action.startsWith("edit_event_map")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        mapId = Integer.parseInt(st.nextToken());
                                                        id = Integer.valueOf(st.nextToken());
                                                        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap != mapId) {
                                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage = "Info";
                                                        }

                                                        this.showMapEditation(gm, mapId, id, (EventType) null);
                                                    } else if (action.startsWith("edit_map_add_event_menu")) {
                                                        this.showMapEditationAddEvent(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                    } else if (action.startsWith("set_active_editing_map_event")) {
                                                        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapEvent = EventType.getType(action.substring(29));
                                                        this.showMapEditationEvents(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                    } else if (action.startsWith("edit_map_page")) {
                                                        s = action.substring(14);
                                                        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage = s;
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("show_map_status")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        mapId = Integer.valueOf(st.nextToken());
                                                        param = st.hasMoreTokens() ? st.nextToken() : "NULL";
                                                        if (!param.equalsIgnoreCase("NULL")) {
                                                            this.showMapStatus(gm, mapId, param);
                                                        } else {
                                                            gm.sendMessage("No status available cause there are no events assigned with this map.");
                                                        }
                                                    } else if (action.startsWith("config_event_map")) {
                                                        this.showConfigMenu(gm, action.substring(17));
                                                    } else if (action.startsWith("set_map_config")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        action3 = st.nextToken();
                                                        param = st.nextToken();
                                                        if (st.hasMoreElements()) {
                                                            key = st.nextToken();
                                                        } else {
                                                            key = "";
                                                        }

                                                        this.setMapConfig(gm, action3, param, key, false);
                                                        this.showMapEditationEvents(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                    } else if (action.startsWith("addto_map_config")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        action3 = st.nextToken();
                                                        param = st.nextToken();
                                                        if (st.hasMoreTokens()) {
                                                            key = st.nextToken();
                                                        } else {
                                                            key = "";
                                                        }

                                                        this.setMapConfig(gm, action3, param, key, true);
                                                        this.showMapEditationEvents(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                    } else if (action.startsWith("expand_spawn_info")) {
                                                        id = Integer.parseInt(action.substring(18));
                                                        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn == id) {
                                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn = 0;
                                                        } else {
                                                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn = id;
                                                            this.showExpandSpawnEffect(gm, id);
                                                        }

                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("filter_event_spawns")) {
                                                        this.filterSpawns(gm, action.substring(20));
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("sort_map_spawns")) {
                                                        this.sortSpawns(gm, action.substring(16));
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("remove_event_map")) {
                                                        this.removeMap(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
                                                        this.showMiniEventMenu(gm);
                                                    } else if (action.startsWith("create_event_map")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        action3 = null;
                                                        if (st.hasMoreTokens()) {
                                                            action3 = st.nextToken();
                                                        }

                                                        type = action3 == null ? null : EventType.getType(action3);
                                                        this.showMapEditation(gm, 0, 0, type);
                                                    } else if (action.startsWith("set_map_name ")) {
                                                        this.setMapName(gm, action.substring(13));
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("set_map_desc ")) {
                                                        this.setMapDesc(gm, action.substring(13));
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("save_map")) {
                                                        boolean b = this.saveMap(gm);
                                                        action3 = this.getGoBackPage(gm);
                                                        if (action3 != null) {
                                                            this.onBypass(gm, action3);
                                                        } else if (b) {
                                                            this.showEditEventsMenu(gm);
                                                        } else {
                                                            this.showMiniEventMenu(gm);
                                                        }
                                                    } else if (action.startsWith("add_event_to_map ")) {
                                                        this.addAvailableEvent(gm, action.substring(17));
                                                        this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                    } else if (action.startsWith("remove_event_from_map")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        boolean b;
                                                        if (st.hasMoreTokens()) {
                                                            param = st.nextToken();
                                                            b = true;
                                                        } else {
                                                            param = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle();
                                                            b = false;
                                                        }

                                                        this.removeAvailableEvent(gm, param);
                                                        this.saveMap(gm);
                                                        if (b) {
                                                            this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                        } else if (EventType.getType(param).isRegularEvent()) {
                                                            this.showEditEventsMenu(gm);
                                                        } else {
                                                            this.showMiniEventMenu(gm);
                                                        }
                                                    } else if (action.startsWith("filter_menu")) {
                                                        this.showFilterMenu(gm);
                                                    } else if (action.startsWith("edit_event_reward_menu")) {
                                                        st = new StringTokenizer(action);
                                                        st.nextToken();
                                                        action3 = st.nextToken();
                                                        param = null;
                                                        RewardPosition pos;
                                                        if (st.hasMoreTokens()) {
                                                            pos = RewardPosition.getPosition(st.nextToken());
                                                        } else {
                                                            pos = RewardPosition.None;
                                                        }

                                                        if (st.hasMoreTokens()) {
                                                            param = st.nextToken();
                                                        }

                                                        this.showRewardsEditation(gm, action3, pos, param);
                                                    } else if (action.startsWith("edit_event_reward ")) {
                                                        this.showRewardEditation(gm, Integer.parseInt(action.substring(18)));
                                                    } else if (action.startsWith("edit_event_reward_new")) {
                                                        this.addEventReward(gm);
                                                    } else if (action.startsWith("show_add_position_window_help")) {
                                                        this.showAddPositionToRewardedWindowHelp(gm);
                                                    } else if (action.startsWith("show_add_position_window")) {
                                                        this.showAddPositionToRewardedWindow(gm);
                                                    } else if (!action.startsWith("show_edit_position_window")) {
                                                        RewardPosition pos;
                                                        if (action.startsWith("add_rewarded_position")) {
                                                            st = new StringTokenizer(action);
                                                            st.nextToken();
                                                            pos = RewardPosition.getPosition(st.nextToken());
                                                            if (st.hasMoreTokens()) {
                                                                param = st.nextToken();
                                                                if (st.hasMoreTokens()) {
                                                                    param = param + "-" + st.nextToken();
                                                                }
                                                            } else {
                                                                param = null;
                                                            }

                                                            this.addPositionToRewarded(gm, pos, param);
                                                        } else if (action.startsWith("remove_rewarded_position")) {
                                                            st = new StringTokenizer(action);
                                                            st.nextToken();
                                                            pos = RewardPosition.getPosition(st.nextToken());
                                                            if (pos == null) {
                                                                gm.sendMessage("Select a position first from the list above.");
                                                                this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
                                                                return;
                                                            }

                                                            param = st.hasMoreTokens() ? st.nextToken() : null;
                                                            this.removePositionFromRewarded(gm, pos, param);
                                                        } else if (action.startsWith("edit_event_reward_")) {
                                                            s = action.substring(18);
                                                            if (s.startsWith("remove")) {
                                                                this.removeReward(gm);
                                                                this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
                                                            } else if (s.startsWith("save")) {
                                                                this.saveReward(gm);
                                                                this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
                                                            } else if (s.startsWith("set_item_id")) {
                                                                this.editReward(gm, s.substring(12), "set_item_id");
                                                                this.showRewardEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
                                                            } else if (s.startsWith("set_min")) {
                                                                this.editReward(gm, s.substring(8), "set_min");
                                                                this.showRewardEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
                                                            } else if (s.startsWith("set_max")) {
                                                                this.editReward(gm, s.substring(8), "set_max");
                                                                this.showRewardEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
                                                            } else if (s.startsWith("set_chance")) {
                                                                this.editReward(gm, s.substring(11), "set_chance");
                                                                this.showRewardEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
                                                            }
                                                        } else if (action.startsWith("spawn_type_info_menu")) {
                                                            this.showSpawnTypeInfoMenu(gm);
                                                        } else if (action.startsWith("spawn_type_info_event")) {
                                                            this.showSpawnTypeInfoEvent(gm, action.substring(22));
                                                        } else if (action.startsWith("remove_spawn ")) {
                                                            this.removeSpawn(gm, Integer.parseInt(action.substring(13)));
                                                            this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                        } else if (action.startsWith("teleport_spawn ")) {
                                                            this.teleportToSpawn(gm, Integer.parseInt(action.substring(15)));
                                                            this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                        } else if (action.startsWith("show_spawn ")) {
                                                            this.showSpawn(gm, Integer.parseInt(action.substring(11)), false, false, false);
                                                            this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                        } else if (action.startsWith("new_spawn")) {
                                                            this.newSpawn(gm);
                                                            this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                        } else if (action.startsWith("edit_spawn_")) {
                                                            s = action.substring(11);
                                                            if (s.startsWith("menu")) {
                                                                this.showEditSpawnMenu(gm, Integer.parseInt(action.substring(16)));
                                                            } else if (s.startsWith("save_spawn")) {
                                                                this.saveSpawn(gm);
                                                                this.showMapEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage, (EventType) null);
                                                            } else if (s.startsWith("set_id")) {
                                                                this.editSpawn(gm, s.substring(7), "set_id");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_team")) {
                                                                this.editSpawn(gm, s.substring(9), "set_team");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_x")) {
                                                                this.editSpawn(gm, s.substring(6), "set_x");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_y")) {
                                                                this.editSpawn(gm, s.substring(6), "set_y");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_z")) {
                                                                this.editSpawn(gm, s.substring(6), "set_z");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_wawenumber")) {
                                                                this.editSpawn(gm, s.substring(15), "set_wawenumber");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_mobid")) {
                                                                this.editSpawn(gm, s.substring(10), "set_mobid");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_mobammount")) {
                                                                this.editSpawn(gm, s.substring(15), "set_mobammount");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_dooraction_init")) {
                                                                this.editSpawn(gm, s.substring(20), "set_dooraction_init");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_dooraction_start")) {
                                                                this.editSpawn(gm, s.substring(21), "set_dooraction_start");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_width")) {
                                                                this.editSpawn(gm, s.substring(10), "set_width");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_length")) {
                                                                this.editSpawn(gm, s.substring(11), "set_length");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("default_loc")) {
                                                                this.editSpawn(gm, "", "default_loc");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_note")) {
                                                                this.editSpawn(gm, s.substring(9), "set_note");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_type")) {
                                                                this.editSpawn(gm, s.substring(9), "set_type");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            } else if (s.startsWith("set_npc_id")) {
                                                                this.editSpawn(gm, s.substring(11), "set_npc_id");
                                                                this.showEditSpawnMenu(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
                                                            }
                                                        } else {
                                                            gm.sendMessage("Unknown action. " + action + " doesn't exist!");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception var12) {
            gm.sendMessage("Bad parameters included! If you're sure it's not you fault, then inform Sunrise Team for repair.");
            SunriseLoader.debug("GM " + gm.getPlayersName() + " sent invalid bypass: " + action, Level.WARNING);
            var12.printStackTrace();
        }

    }

    private void showLanguageSelectMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_lang.htm");
        StringBuilder tb = new StringBuilder();
        Iterator var4 = LanguageEngine.getLanguages().entrySet().iterator();

        while (var4.hasNext()) {
            Entry<String, String> e = (Entry) var4.next();
            tb.append("<button value=\"" + (String) e.getValue() + "\" action=\"bypass admin_event_manage set_language " + (String) e.getKey() + "\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            tb.append("<br1>");
        }

        html = html.replaceAll("%languages%", tb.toString());
        html = html.replaceAll("%current%", LanguageEngine.getLanguage());
        html = html.replaceAll("%events%", tb.toString());
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void showEventOrderMenu(PlayerEventInfo gm, String lastModified) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_order.htm");
        StringBuilder tb = new StringBuilder();
        List<EventScheduleData> dataList = EventManager.getInstance().getMainEventManager().getEventScheduleData();
        EventScheduleData data = null;

        for (int i = 0; i < dataList.size(); ++i) {
            Iterator var8 = dataList.iterator();

            while (var8.hasNext()) {
                EventScheduleData d = (EventScheduleData) var8.next();
                if (d.getOrder() == i + 1) {
                    data = d;
                    break;
                }
            }

            if (data == null) {
                SunriseLoader.debug("Missing event in eventOrder system with order = " + (i + 1) + " - please correct this in database", Level.WARNING);
            } else {
                boolean isLastModified = data.getEvent().getAltTitle().equals(lastModified);
                if (isLastModified) {
                    tb.append("<table width=280 bgcolor=2f2f2f>");
                } else {
                    tb.append("<table width=280>");
                }

                tb.append("<tr>");
                if (!EventConfig.getInstance().isEventAllowed(data.getEvent())) {
                    tb.append("<td width=120><font color=LEVEL>" + data.getOrder() + "</font>. <font color=4f4f4f>" + data.getEvent().getAltTitle() + "</font></td>");
                } else if (EventMapSystem.getInstance().getMapsCount(data.getEvent()) == 0) {
                    tb.append("<td width=120><font color=LEVEL>" + data.getOrder() + "</font>. <font color=B56A6A>" + data.getEvent().getAltTitle() + "</font></td>");
                } else if (EventManager.getInstance().getMainEventManager().getLastEventOrder() == data.getEvent()) {
                    tb.append("<td width=120><font color=LEVEL>" + data.getOrder() + "</font>. <font color=DCDCDC> " + data.getEvent().getAltTitle() + "</font></td>");
                } else {
                    tb.append("<td width=120><font color=LEVEL>" + data.getOrder() + "</font>. <font color=829F80>" + data.getEvent().getAltTitle() + "</font></td>");
                }

                if (data.getOrder() <= 1) {
                    tb.append("<td width=50></td>");
                    tb.append("<td width=60><button value=\"Down\" action=\"bypass admin_event_manage eventorder_move down " + data.getEvent().getAltTitle() + "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                } else if (data.getOrder() >= dataList.size()) {
                    tb.append("<td width=50><button value=\"Up\" action=\"bypass admin_event_manage eventorder_move up " + data.getEvent().getAltTitle() + "\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("<td width=60></td>");
                } else {
                    tb.append("<td width=50><button value=\"Up\" action=\"bypass admin_event_manage eventorder_move up " + data.getEvent().getAltTitle() + "\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("<td width=60><button value=\"Down\" action=\"bypass admin_event_manage eventorder_move down " + data.getEvent().getAltTitle() + "\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                }

                tb.append("</tr>");
                tb.append("</table>");
            }
        }

        html = html.replaceAll("%events%", tb.toString());
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void gmDebug(PlayerEventInfo gm) {
        if (!SunriseLoader.isDebugging(gm)) {
            SunriseLoader.addGmDebug(gm);
            gm.sendMessage("Signed for debugging.");
        } else {
            SunriseLoader.removeGmDebug(gm);
            gm.sendMessage("Unsigned from debugging.");
        }

    }

    private void moveEventInOrder(PlayerEventInfo gm, String direction, String event) {
        EventType type = EventType.getType(event);
        if (type != null && type.isRegularEvent()) {
            EventScheduleData data = EventManager.getInstance().getMainEventManager().getScheduleData(type);
            if (data == null) {
                gm.sendMessage("No schedule data found for this event.");
            } else {
                if (direction.equals("up")) {
                    data.raiseOrder();
                } else if (direction.equals("down")) {
                    data.decreaseOrder();
                }

                this.showEventOrderMenu(gm, event);
            }
        } else {
            gm.sendMessage("No/Wrong event specified.");
        }
    }

    private void showGlobalConfigMenu(PlayerEventInfo gm, String configType, int page) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_globalconfig_menu.htm");
        html = html.replaceAll("%objectId%", String.valueOf(0));
        StringBuilder tb = new StringBuilder();
        if (configType == null || configType.length() == 0) {
            configType = "Core";
        }

        String color = "FFFFFF";
        if (configType.equals("Core")) {
            color = "86CE71";
        } else if (configType.equals("Scheduler")) {
            color = "D19F6D";
        } else if (configType.equals("Buffer")) {
            color = "A48EE1";
        } else if (configType.equals("Features")) {
            color = "69A5D6";
        }

        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigType = configType;
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigPage = page;
        if (configType.equals("Scheduler")) {
            tb.append("<table width=275 bgcolor=444444><tr>");
            tb.append("<td width=275 align=center><font color=DFBC99><a action=\"bypass admin_event_manage eventorder_menu \">Edit event order</a></font></td>");
            tb.append("</tr></table><br>");
        }

        if (configType.equals("Core")) {
            tb.append("<table width=275 bgcolor=444444><tr>");
            tb.append("<td width=275 align=center><font color=DFBC99><a action=\"bypass admin_event_manage eventlang_menu \">Engine Language</a></font></td>");
            tb.append("</tr></table><br>");
        }

        if (configType.equals("Features")) {
            tb.append("<table width=275 bgcolor=444444><tr>");
            if (page == 1) {
                tb.append("<td width=90 align=center><font color=CB9258>Page 1</font></td>");
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 2\">Page 2</a></font></td>");
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 3\">Page 3</a></font></td>");
            } else if (page == 2) {
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 1\">Page 1</a></font></td>");
                tb.append("<td width=90 align=center><font color=CB9258>Page 2</font></td>");
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 3\">Page 3</a></font></td>");
            } else if (page == 3) {
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 1\">Page 1</a></font></td>");
                tb.append("<td width=90 align=center><font color=DFBC99><a action=\"bypass admin_event_manage globalconfig_menu Features 2\">Page 2</a></font></td>");
                tb.append("<td width=90 align=center><font color=CB9258>Page 3</font></td>");
            }

            tb.append("</tr></table><br>");
        }

        int count = 0;
        int recordsPerPage = 15;
        int startFrom = (page - 1) * recordsPerPage;
        int showTill = page * recordsPerPage;
        Iterator var11 = EventConfig.getInstance().getGlobalConfigs(configType).iterator();

        while (true) {
            GlobalConfigModel config;
            String value;
            do {
                do {
                    if (!var11.hasNext()) {
                        if (configType.equals("Buffer")) {
                            tb.append("<br><center><font color=9f9f9f>These settings apply to the NPC Buffer (ID " + EventConfig.getInstance().getGlobalConfigInt("assignedNpcId") + ") provided by this engine.</font></center>");
                            tb.append("<br1><a action=\"bypass -h nxs_npcbuffer_reload\">Apply changes in these configs</a>");
                        }

                        String result = tb.toString();
                        html = html.replaceAll("%configs%", result);
                        html = html.replaceAll("%title%", "Event Engine");
                        gm.sendPacket(html);
                        gm.sendStaticPacket();
                        return;
                    }

                    config = (GlobalConfigModel) var11.next();
                    value = config.getValue();
                    if (value.length() > 19) {
                        value = value.substring(0, 20) + "..";
                    }
                } while (configType.equals("GearScore") && config.getKey().startsWith("defVal"));

                ++count;
            } while (configType.equals("Features") && (count <= startFrom || count > showTill));

            tb.append("<table width=275 bgcolor=333333><tr>");
            tb.append("<td width=160 align=left><font color=" + color + "><a action=\"bypass admin_event_manage globalconfig_showinfo " + configType + " " + page + " " + config.getKey() + "\">" + config.getKey() + "</a></font></td>");
            tb.append("<td width=100 align=right><font color=9f9f9f><a action=\"bypass admin_event_manage globalconfig_edit " + config.getKey() + "\">" + value + "</a></font></td><td width=10></td>");
            tb.append("</tr></table>");
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigKeyShown.equals(config.getKey())) {
                tb.append("<table width=275><tr>");
                tb.append("<td width=275><font color=9f9f9f>" + config.getDesc() + "</font></td>");
                tb.append("</tr></table>");
            }

            tb.append("<br>");
        }
    }

    private void showGlobalConfigEditation(PlayerEventInfo gm, String key) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_globalconfig_edit.htm");
        GlobalConfigModel config = EventConfig.getInstance().getGlobalConfig((String) null, key);
        if (config == null) {
            gm.sendMessage("Global Config '" + key + "' doesn't exist.");
        } else {
            int inputType = config.getInputType();
            if (inputType == 1) {
                html = html.replaceAll("%input%", "<edit var=\"value\" width=140 height=15>");
            } else if (inputType == 2) {
                html = html.replaceAll("%input%", "<multiedit var=\"value\" width=180 height=30>");
            }

            html = html.replaceAll("%key%", config.getKey());
            html = html.replaceAll("%info%", config.getDesc());
            html = html.replaceAll("%value%", config.getValue());
            html = html.replaceAll("%page%", "" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigPage);
            html = html.replaceAll("%type%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeGlobalConfigType);
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    private void setGlobalConfigValue(PlayerEventInfo gm, String key, String value) {
        GlobalConfigModel config = EventConfig.getInstance().getGlobalConfig((String) null, key);
        if (config == null) {
            gm.sendMessage("Global Config '" + key + "' doesn't exist.");
        } else {
            EventConfig.getInstance().setGlobalConfigValue(config, key, value);
            gm.sendMessage("The value of global config '" + key + "' is now '" + value + "'.");
            this.showGlobalConfigEditation(gm, key);
        }
    }

    private void showNewEventMenu(PlayerEventInfo gm, EventType event, boolean runTimeEdit) {
        AbstractMainEvent ev = EventManager.getInstance().getCurrentMainEvent();
        if (ev != null) {
            gm.sendMessage("Another event is already running.");
            this.showMenu(gm, false);
        } else if (EventManager.getInstance().getMainEventManager().autoSchedulerEnabled() && !EventManager.getInstance().getMainEventManager().autoSchedulerPaused()) {
            gm.sendMessage("You may not start event when the automatic scheduler is enabled.");
            this.showMenu(gm, false);
        } else {
            if (event != null && event.isRegularEvent()) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = event;
            } else if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
                event = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
            }

            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_launch_event.htm");
            html = html.replaceAll("%objectId%", String.valueOf(0));
            StringBuilder tb = new StringBuilder();
            if (event != null) {
                tb.append((event.getHtmlTitle().length() > 16 ? event.getAltTitle() : event.getHtmlTitle()) + ";");
            }

            EventType[] var7 = EventType.values();
            int defaultRunTime = var7.length;

            for (int var9 = 0; var9 < defaultRunTime; ++var9) {
                EventType type = var7[var9];
                if (type.isRegularEvent() && type.allowEdits() && type != EventType.Unassigned && EventManager.getInstance().getMainEvent(type) != null && (event == null || event != type)) {
                    tb.append((type.getHtmlTitle().length() > 16 ? type.getAltTitle() : type.getHtmlTitle()) + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%events%", result.substring(0, result.length() - 1));
            tb = new StringBuilder();
            Iterator var13;
            if (event == null) {
                tb.append("-Select event- ");
            } else {
                var13 = EventMapSystem.getInstance().getMainEventMaps(event).iterator();

                while (var13.hasNext()) {
                    EventMap map = (EventMap) var13.next();
                    if (EventManager.getInstance().getMainEvent(event) != null && EventManager.getInstance().getMainEvent(event).canRun(map)) {
                        tb.append(map.getMapName() + ";");
                    }
                }
            }

            result = tb.toString();
            if (result.length() > 0) {
                html = html.replaceAll("%available_maps%", result.substring(0, result.length() - 1));
            } else {
                html = html.replaceAll("%available_maps%", "No map available");
            }

            tb = new StringBuilder();
            var13 = EventManager.getInstance().getMainEventManager().regNpcLocs.entrySet().iterator();

            while (var13.hasNext()) {
                Entry<Integer, RegNpcLoc> e = (Entry) var13.next();
                tb.append(((RegNpcLoc) e.getValue())._name + ";");
            }

            result = tb.toString();
            html = html.replaceAll("%regNpcLocs%", result.substring(0, result.length() - 1));
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = null;
            }

            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent == null) {
                html = html.replaceAll("%selected%", "<font color=BD8282>No event selected</font>");
            } else {
                html = html.replaceAll("%selected%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getHtmlTitle());
            }

            if (runTimeEdit) {
                if (event != null) {
                    html = html.replaceAll("%runTimeName%", "<a action=\"bypass admin_event_manage new_event " + event.getAltTitle() + "\">Run time:</a>");
                }

                html = html.replaceAll("%runTimeEdit%", "<edit var=\"runTime\" width=130 height=13>");
                html = html.replaceAll("%runTimeVar%", "\\$runTime");
            } else {
                if (event == null) {
                    html = html.replaceAll("%runTimeName%", "Run time:");
                } else {
                    html = html.replaceAll("%runTimeName%", "<a action=\"bypass admin_event_manage new_event_runtime_edit " + event.getAltTitle() + "\">Run time:</a>");
                }

                if (event != null) {
                    defaultRunTime = EventConfig.getInstance().getGlobalConfigInt("defaultRunTime");

                    try {
                        defaultRunTime = EventManager.getInstance().getMainEvent(event).getInt("runTime");
                    } catch (NullPointerException var11) {
                    }

                    html = html.replaceAll("%runTimeEdit%", "<font color=9f9f9f>" + defaultRunTime + " min</font>");
                    html = html.replaceAll("%runTimeVar%", "" + defaultRunTime);
                } else {
                    html = html.replaceAll("%runTimeEdit%", "<font color=9f9f9f>- Select event -</font>");
                    html = html.replaceAll("%runTimeVar%", "10");
                }
            }

            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    private void deleteMainInstanceType(PlayerEventInfo gm, int id) {
        EventType type = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
        if (type != null && type.isRegularEvent()) {
            AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
            if (event == null) {
                gm.sendMessage("Main event " + type.getAltTitle() + " does not exist or isn't finished yet.");
            } else if (event.getInstanceTypes().size() == 1) {
                gm.sendMessage("The event must have at least one InstanceType!");
            } else {
                MainEventInstanceType instance = event.getInstanceType(id);
                MainEventInstanceTypeManager.getInstance().removeInstanceType(instance);
            }
        } else {
            gm.sendMessage("Wrong event.");
        }
    }

    private void showEditEventInstancesMenu(PlayerEventInfo gm, EventType type) {
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = type;
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editevent_instances.htm");
        StringBuilder tb = new StringBuilder();
        AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
        if (event == null) {
            gm.sendMessage("Main event " + type.getAltTitle() + " does not exist or isn't finished yet.");
        } else {
            Iterator var6 = event.getInstanceTypes().values().iterator();

            while (var6.hasNext()) {
                MainEventInstanceType instance = (MainEventInstanceType) var6.next();
                tb.append("<table width=280 bgcolor=363636>");
                tb.append("<tr>");
                tb.append("<td align=left width=150><font color=ac9887)>" + instance.getName() + " </font><font color=54585C>(" + instance.getId() + ")</font></td>");
                tb.append("<td align=right width=80><button value=\"Edit\" action=\"bypass admin_event_manage edit_main_instance_type " + instance.getId() + "\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr>");
                tb.append("</table>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=6>");
            }

            html = html.replaceAll("%instances%", tb.toString());
            if (type != EventType.Unassigned) {
                html = html.replaceAll("%event%", type.getAltTitle());
                html = html.replaceAll("%name%", type.getHtmlTitle());
                html = html.replaceAll("%eventId%", String.valueOf(type.getId()));
                html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(type) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
                tb = new StringBuilder();
                tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
                String[] var10 = this.mainEventEditingPages;
                int var12 = var10.length;

                for (int var8 = 0; var8 < var12; ++var8) {
                    String s = var10[var8];
                    if (!s.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                        tb.append(s + ";");
                    }
                }

                String result = tb.toString();
                html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            } else {
                gm.sendMessage("This doesn't have instances.");
            }
        }
    }

    private void editMainInstanceType(PlayerEventInfo gm, int id, String action, String value, boolean addToValue) {
        EventType type = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
        if (type != null && type.isRegularEvent()) {
            AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
            if (event == null) {
                gm.sendMessage("Main event " + type.getAltTitle() + " does not exist or isn't finished yet.");
            } else {
                MainEventInstanceType instance = event.getInstanceType(id);
                if (action.equals("set_name")) {
                    instance.setName(value);
                } else {
                    instance.setConfig(action, value, addToValue);
                }

                MainEventInstanceTypeManager.getInstance().updateInstanceType(instance);
            }
        } else {
            gm.sendMessage("Wrong event.");
        }
    }

    private void showEditMainInstanceTypeWindow(PlayerEventInfo gm, int id) {
        EventType type = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
        if (type != null && type.isRegularEvent()) {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editevent_instances_edit.htm");
            AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
            if (event == null) {
                gm.sendMessage("Main event " + type.getAltTitle() + " does not exist or isn't finished yet.");
            } else {
                MainEventInstanceType instance;
                if (id == -1) {
                    instance = new MainEventInstanceType(MainEventInstanceTypeManager.getInstance().getNextId(), event, "NewInstance", "New Instance", (String) null);
                    event.insertConfigs(instance);
                    MainEventInstanceTypeManager.getInstance().addInstanceType(instance, true);
                    ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown = "";
                } else {
                    instance = event.getInstanceType(id);
                }

                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEdittingMainInstanceType = instance.getId();
                StringBuilder tb = new StringBuilder();
                boolean expanded = false;

                for (Iterator var9 = instance.getConfigs().entrySet().iterator(); var9.hasNext(); tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=4>")) {
                    Entry<String, ConfigModel> e = (Entry) var9.next();
                    if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory.equals("InstanceType") && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown.equals(((ConfigModel) e.getValue()).getKey())) {
                        expanded = true;
                    } else {
                        expanded = false;
                    }

                    if (expanded) {
                        tb.append("<table width=280 bgcolor=599944>");
                    } else {
                        tb.append("<table width=280 bgcolor=3f3f3f>");
                    }

                    tb.append("<tr>");
                    tb.append("<td width=175 align=left><font color=ac9887> " + ((ConfigModel) e.getValue()).getKey() + "</font></td>");
                    String value = ((ConfigModel) e.getValue()).getValue();
                    if (value.length() > 6) {
                        value = "...";
                    }

                    boolean brackets = true;
                    if (value.length() >= 6) {
                        brackets = false;
                    }

                    tb.append("<td width=45 align=left><font color=9f9f9f>" + (brackets ? "(" : "") + "" + value + "" + (brackets ? ")" : "") + "</font></td>");
                    tb.append("<td width=50 align=right><button value=\"Expand\" width=55 action=\"bypass admin_event_manage expand_configmodel InstanceType " + (String) e.getKey() + "\" height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                    if (expanded) {
                        tb.append("<table width=278 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td width=240><font color=9f9f9f>" + ((ConfigModel) e.getValue()).getDesc() + "</font></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        if (((ConfigModel) e.getValue()).getInput() == InputType.MultiEdit || ((ConfigModel) e.getValue()).getInput() == InputType.MultiAdd || ((ConfigModel) e.getValue()).getValue().length() > 5) {
                            tb.append("<table width=278 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append(((ConfigModel) e.getValue()).getValueShownInHtml());
                            tb.append("</tr>");
                            tb.append("</table>");
                        }

                        tb.append("<table width=280 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td>" + ((ConfigModel) e.getValue()).getInputHtml(180) + "</td>");
                        tb.append("<td align=left><button value=\"" + ((ConfigModel) e.getValue()).getAddButtonName() + "\" width=40 action=\"bypass admin_event_manage edit_main_instance_type_" + ((ConfigModel) e.getValue()).getAddButtonAction() + "_config " + (String) e.getKey() + " \\$" + (String) e.getKey() + "\" height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        tb.append("<table width=280 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td><font color=6f6f6f>Default: " + ((ConfigModel) e.getValue()).getDefaultVal() + "</font></td>");
                        tb.append("<td align=right><button value=\"" + ((ConfigModel) e.getValue()).getUtilButtonName() + "\" action=\"bypass admin_event_manage edit_main_instance_type_set_config " + (String) e.getKey() + " " + ((ConfigModel) e.getValue()).getDefaultVal() + "\" width=" + ((ConfigModel) e.getValue()).getUtilButtonWidth() + " height=17 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                    }
                }

                html = html.replaceAll("%configs%", tb.toString());
                html = html.replaceAll("%instance_name%", instance.getName());
                html = html.replaceAll("%instance_id%", "" + instance.getId());
                html = html.replaceAll("%event%", type.getAltTitle());
                html = html.replaceAll("%name%", type.getHtmlTitle());
                html = html.replaceAll("%eventId%", String.valueOf(type.getId()));
                html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(type) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("Wrong event.");
        }
    }

    private void showEditEventMenu(PlayerEventInfo gm, String eventName, String page) {
        if (eventName.equals("Default")) {
            eventName = "Unassigned";
        }

        EventType type = EventType.getType(eventName);
        if (type != null && type.isRegularEvent()) {
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = type;
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = 1;
            if (page == null) {
                page = this.mainEventEditingPages[0];
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection = page;
            if (page.equals(this.mainEventEditingPages[1])) {
                this.showRewardsEditation(gm, type.getAltTitle(), RewardPosition.None, (String) null);
            } else if (page.equals(this.mainEventEditingPages[2])) {
                this.showConfigsMenu(gm, type.getAltTitle(), 1);
            } else if (page.equals(this.mainEventEditingPages[3])) {
                this.showEditEventInstancesMenu(gm, type);
            } else {
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editevent.htm");
                StringBuilder tb = new StringBuilder();
                Iterator var8 = EventMapSystem.getInstance().getMaps(type).values().iterator();

                while (var8.hasNext()) {
                    EventMap map = (EventMap) var8.next();
                    boolean error = map.getMissingSpawns().length() > 0;
                    tb.append("<table width=280 bgcolor=363636>");
                    tb.append("<tr>");
                    tb.append("<td align=left width=150><font color=" + (error ? "CD6565" : "ac9887") + ">" + map.getMapName() + " </font><font color=54585C>" + map.getGlobalId() + "</font></td>");
                    tb.append("<td align=right width=80><button value=\"" + (error ? "! " : "") + "Status" + (error ? " !" : "") + "\" action=\"bypass admin_event_manage show_map_status " + map.getGlobalId() + " " + type.getAltTitle() + "\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("<td align=right width=80><button value=\"Edit\" action=\"bypass admin_event_manage edit_event_map " + map.getGlobalId() + " 0\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                    tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=6>");
                }

                if (!EventMapSystem.getInstance().getMaps(type).isEmpty()) {
                    html = html.replaceAll("%maps%", tb.toString());
                } else {
                    html = html.replaceAll("%maps%", "<font color=B46F6B>No maps available for this event.</font>");
                }

                html = html.replaceAll("%mapsAmmount%", String.valueOf(EventMapSystem.getInstance().getMaps(type).values().size()));
                if (type != EventType.Unassigned) {
                    html = html.replaceAll("%event%", type.getAltTitle());
                    html = html.replaceAll("%name%", type.getHtmlTitle());
                    html = html.replaceAll("%eventId%", String.valueOf(type.getId()));
                    html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(type) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
                    tb = new StringBuilder();
                    tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
                    String[] var12 = this.mainEventEditingPages;
                    int var14 = var12.length;

                    for (int var10 = 0; var10 < var14; ++var10) {
                        String s = var12[var10];
                        if (!s.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                            tb.append(s + ";");
                        }
                    }

                    String result = tb.toString();
                    html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
                } else {
                    html = html.replaceAll("%event%", "Default");
                    html = html.replaceAll("%name%", "Unassigned maps");
                    html = html.replaceAll("%eventId%", String.valueOf(0));
                    html = html.replaceAll("%enableDisable%", "N/A");
                    html = html.replaceAll("%event_pages%", "Maps");
                }

                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("Wrong event.");
        }
    }

    private void showEditEventsMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editevents.htm");
        int count = 0;
        int i = 0;
        StringBuilder tb = new StringBuilder();
        tb.append("<font color=LEVEL>Main Events:</font><br1><table width=270>");
        EventType[] var6 = EventType.values();
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            EventType type = var6[var8];
            if (type.isRegularEvent() && type.allowEdits() && type != EventType.Unassigned) {
                boolean exists = EventManager.getInstance().getMainEvent(type) != null;
                if (i == 0) {
                    tb.append("<tr>");
                }

                ++i;
                if (exists) {
                    tb.append("<td><button value=\"" + type.getHtmlTitle() + "\" action=\"bypass admin_event_manage edit_event " + type.getAltTitle() + "\" width=130 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                } else {
                    tb.append("<td align=left><font color=6f6f6f>" + type.getHtmlTitle() + "</font></td>");
                }

                ++count;
                if (i == 2) {
                    tb.append("</tr>");
                    i = 0;
                }
            }
        }

        if (i != 0 && i % 2 == 1) {
            tb.append("</tr>");
        }

        tb.append("</table>");
        if (count > 0) {
            html = html.replaceAll("%allowed_events%", tb.toString());
        } else {
            html = html.replaceAll("%allowed_events%", "");
        }

        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void allowDisallowMapMode(PlayerEventInfo gm, String map) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            int mapId = Integer.parseInt(map);
            if (mode.getDisMaps().contains(mapId)) {
                mode.getDisMaps().remove(mapId);
            } else {
                mode.getDisMaps().add(mapId);
            }

            EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditModeMaps(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_maps.htm");
            StringBuilder tb = new StringBuilder();
            tb.append("<table width=270>");
            Iterator var6 = EventMapSystem.getInstance().getMaps(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent).values().iterator();

            while (var6.hasNext()) {
                EventMap map = (EventMap) var6.next();
                if (event.canRun(map)) {
                    tb.append("<tr><td width=180><font color=ac9887>" + map.getMapName() + "</font></td>");
                } else {
                    tb.append("<tr><td width=180><font color=696969>" + map.getMapName() + "</font></td>");
                }

                if (mode.getDisMaps().contains(map.getGlobalId())) {
                    tb.append("<td width=90><font color=B46F6B><a action=\"bypass admin_event_manage mini_edit_modes_map " + map.getGlobalId() + "\">Disabled</a></font></td></tr>");
                } else {
                    tb.append("<td width=90><font color=74BE85><a action=\"bypass admin_event_manage mini_edit_modes_map " + map.getGlobalId() + "\">Enabled</a></font></td></tr>");
                }
            }

            tb.append("</table>");
            html = html.replaceAll("%maps%", tb.toString());
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
            html = html.replaceAll("%modeName%", mode.getModeName());
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void createNewMode(PlayerEventInfo gm, String modeName) {
        int newModeId = 0;
        Iterator var4 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

        while (var4.hasNext()) {
            Entry<Integer, MiniEventManager> e = (Entry) var4.next();
            if (newModeId < (Integer) e.getKey()) {
                newModeId = (Integer) e.getKey();
            }
        }

        ++newModeId;
        MiniEventManager manager = EventManager.getInstance().createManager(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, newModeId, modeName, modeName);
        if (manager != null) {
            manager.getMode().setAllowed(false);
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = newModeId;
            this.showEditModeWindow(gm);
        }
    }

    private void createNewModeMenu(PlayerEventInfo gm) {
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            gm.sendMessage("Event Modes aren't implemented for Main events yet.");
        } else {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_new_menu.htm");
            html = html.replaceAll("%name%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getHtmlTitle());
            html = html.replaceAll("%type%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getId()));
            int newModeId = 0;
            Iterator var4 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

            while (var4.hasNext()) {
                Entry<Integer, MiniEventManager> e = (Entry) var4.next();
                if (newModeId < (Integer) e.getKey()) {
                    newModeId = (Integer) e.getKey();
                }
            }

            ++newModeId;
            html = html.replaceAll("%newModeId%", String.valueOf(newModeId));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    private void clearEvent(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            event.cleanMe(true);
            gm.sendMessage("All games successfully aborted and all players unregistered.");
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void createRegFile(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            String name = event.getEventType().getAltTitle() + "_" + mode.getModeName() + ".htm";
            File file = new File("data/html/sunrise/event/events/" + name);

            try {
                if (!file.createNewFile()) {
                    file.delete();
                    gm.sendMessage("The registration html file has been deleted.");
                    return;
                }

                File defaultFile = new File("data/html/sunrise/event/events/" + event.getEventType().getAltTitle() + "_Default.htm");
                if (!defaultFile.exists()) {
                    gm.sendMessage("The default event file 'data/html/sunrise/event/events/" + event.getEventType().getAltTitle() + "_Default.htm' not found.");
                    return;
                }

                Writer writer = null;
                BufferedReader reader = null;

                try {
                    reader = new BufferedReader(new FileReader(defaultFile));
                    writer = new BufferedWriter(new FileWriter(file));

                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        writer.write(line + "\n");
                    }
                } catch (Exception var10) {
                    var10.printStackTrace();
                }
            } catch (IOException var11) {
                var11.printStackTrace();
            }

            CallBack.getInstance().getOut().reloadHtmls();
            gm.sendMessage("The file has been created in 'data/html/sunrise/event/events/" + name + "'.");
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void enableDisableMode(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            event.getMode().setAllowed(!event.getMode().isAllowed());
            EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void deleteMode(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId == 1) {
                gm.sendMessage("You can't delete the default mode. You may only disable it.");
            } else {
                event.cleanMe(true);
                ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).remove(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, 0);
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = 1;
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void setModeName(PlayerEventInfo gm, String name, boolean visible) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            if (!visible) {
                mode.setModeName(name);
            } else {
                mode.setVisibleName(name);
            }

            EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void setModeNpcId(PlayerEventInfo gm, String value) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();

            try {
                int id = Integer.parseInt(value);
                mode.setNpcId(id);
                gm.sendMessage("Done. Mini event " + mode.getModeName() + " will now be accessable from NPC Id " + id);
            } catch (Exception var6) {
                gm.sendMessage("Npc id has to be a number.");
            }

            EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void scheduleRemoveTime(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            RunTime time = (RunTime) event.getMode().getScheduleInfo().getTimes().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId);
            if (time == null) {
                gm.sendMessage("RunTime object with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId + " does not exist.");
            } else {
                event.getMode().getScheduleInfo().getTimes().remove(time._id);
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId = 0;
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void schedulerSetHour(PlayerEventInfo gm, String hour, boolean from) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            RunTime time = (RunTime) event.getMode().getScheduleInfo().getTimes().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId);
            if (time == null) {
                gm.sendMessage("RunTime object with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId + " does not exist.");
            } else {
                boolean check = true;

                try {
                    String hoursString = hour.split(":")[0];
                    String minsString = hour.split(":")[1];
                    if (hour.equals("0:00")) {
                        hour = "00:00";
                    }

                    int hours = Integer.parseInt(hoursString);
                    int mins = Integer.parseInt(minsString);
                    if (hours < 0 || hours > 23) {
                        gm.sendMessage("Hours must be within 0 - 23 values.");
                        check = false;
                    }

                    if (mins < 0 || mins > 59) {
                        gm.sendMessage("Minutes must be within 0 - 59 values.");
                        check = false;
                    }
                } catch (Exception var11) {
                    gm.sendMessage("Sorry, wrong data specified. Use only numbers.");
                    check = false;
                }

                if (check) {
                    if (from) {
                        time.from = hour;
                    } else {
                        time.to = hour;
                    }

                    EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
                }
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void schedulerAddDay(PlayerEventInfo gm, String dayName) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            RunTime time = (RunTime) event.getMode().getScheduleInfo().getTimes().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId);
            if (time == null) {
                gm.sendMessage("RunTime object with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId + " does not exist.");
            } else {
                if (dayName.equals("AllDays")) {
                    Day[] var5 = Day.values();
                    int var6 = var5.length;

                    for (int var7 = 0; var7 < var6; ++var7) {
                        Day d = var5[var7];
                        if (!time.days.contains(d)) {
                            time.days.add(d);
                        }
                    }
                } else {
                    Day d = Day.getDayByName(dayName);
                    if (!time.days.contains(d)) {
                        time.days.add(d);
                    } else {
                        gm.sendMessage("This day has already been added.");
                    }
                }

                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void schedulerRemoveDay(PlayerEventInfo gm, String dayPrefix) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            RunTime time = (RunTime) event.getMode().getScheduleInfo().getTimes().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId);
            if (time == null) {
                gm.sendMessage("RunTime object with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId + " does not exist.");
            } else {
                time.days.remove(Day.getDay(dayPrefix));
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditModeSchedulerTime(PlayerEventInfo gm) {
        this.showEditModeSchedulerTime(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId);
    }

    private void showEditModeSchedulerTime(PlayerEventInfo gm, int timeId) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            RunTime time = (RunTime) event.getMode().getScheduleInfo().getTimes().get(timeId);
            if (time == null) {
                gm.sendMessage("RunTime object with ID " + timeId + " does not exist.");
            } else {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId = timeId;
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_scheduler_edit.htm");
                html = html.replaceAll("%from%", time.from);
                html = html.replaceAll("%to%", time.to);
                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%type%", event.getEventType().getAltTitle());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
                html = html.replaceAll("%modeName%", mode.getModeName());
                long l = time.getNext(!time.isActual(), true);
                Date date = new Date(l);
                String ts = date.toString();
                if (time.days.isEmpty()) {
                    html = html.replaceAll("%future%", "Future: N/A - No days are specified!");
                } else {
                    html = html.replaceAll("%future%", (time.isActual() ? "Ends: " : "Starts: ") + ts.substring(0, ts.length() - 9));
                }

                StringBuilder tb = new StringBuilder();
                tb.append("<table width=220>");
                Iterator var12 = time.days.iterator();

                while (var12.hasNext()) {
                    Day d = (Day) var12.next();
                    tb.append("<tr>");
                    tb.append("<td align=left>" + d._fullName + " (" + d.prefix + ")</td>");
                    tb.append("<td align=right><button value=\"Remove\" action=\"bypass admin_event_manage mini_edit_modes_scheduler_removeday " + d.prefix + "\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                }

                tb.append("</table>");
                html = html.replaceAll("%days%", tb.toString());
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void schedulerRefresh(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            mode.refreshScheduler();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void schedulerAddNewTime(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            RunTime time = mode.getScheduleInfo().addTime();
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeTimeId = time._id;
            EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, time._id);
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditModeSchedulerMenu(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_scheduler_menu.htm");
            Calendar c = Calendar.getInstance();
            String time = c.get(11) + ":" + c.get(12);
            html = html.replaceAll("%currTime%", time);
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
            html = html.replaceAll("%modeName%", mode.getModeName());
            StringBuilder tb = new StringBuilder();
            tb.append("<table width=270>");
            Iterator var8 = mode.getScheduleInfo().getTimes().entrySet().iterator();

            while (var8.hasNext()) {
                Entry<Integer, RunTime> e = (Entry) var8.next();
                tb.append("<tr>");
                tb.append("<td align=left><font color=ac9887>" + ((RunTime) e.getValue()).from + " - " + ((RunTime) e.getValue()).to);
                tb.append(" - </font><font color=9f9f9f>" + ((RunTime) e.getValue()).getDaysString(true) + "</font></td>");
                tb.append("<td align=right><button value=\"Edit\" action=\"bypass admin_event_manage mini_edit_modes_scheduler_edit " + e.getKey() + "\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr>");
            }

            tb.append("</table>");
            html = html.replaceAll("%times%", tb.toString());
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditModeWindow(PlayerEventInfo gm) {
        this.showEditModeWindow(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
    }

    private void showEditModeWindow(PlayerEventInfo gm, int modeId) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, modeId);
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = modeId;
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_edit.htm");
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
            html = html.replaceAll("%modeName%", mode.getModeName());
            html = html.replaceAll("%visibleName%", mode.getVisibleName());
            html = html.replaceAll("%npcId%", mode.getNpcId() == 0 ? "None assigned" : String.valueOf(mode.getNpcId()));
            html = html.replaceAll("%htmlPath%", "/sunrise/event/events/npc/" + event.getEventType().getAltTitle() + "_" + event.getMode().getModeName() + ".htm");
            long l = mode.getFuture();
            Date date = new Date(System.currentTimeMillis() + l);
            String ts = date.toString();
            if (!event.canRun()) {
                html = html.replaceAll("%future%", "<font color=804848>No suitable maps for this Event mode. Can't run this mode!</font>");
            } else if (mode.isNonstopRun()) {
                html = html.replaceAll("%future%", "This mode runs nonstop.");
            } else if (l != -1L && !mode.getScheduleInfo().getTimes().isEmpty()) {
                html = html.replaceAll("%future%", (mode.isRunning() ? "Ends: " : "Starts: ") + ts.substring(0, ts.length() - 9));
            } else {
                html = html.replaceAll("%future%", "<font color=804848>Future: null - No valid scheduler is specified. Can't run this mode!</font>");
            }

            html = html.replaceAll("%enableDisable%", mode.isAllowed() ? "<font color=74BE85><a action=\"bypass admin_event_manage mini_edit_modes_enabledisable\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage mini_edit_modes_enabledisable\">Disabled</a></font>");
            html = html.replaceAll("%deleteButton%", mode.getModeId() == 1 ? "" : "<td width=95><button value=\"Delete mode\" action=\"bypass admin_event_manage mini_edit_modes_delete\" width=95 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
            boolean isHtmlGenerated = false;
            File file = new File("data/html/sunrise/event/events/" + event.getEventType().getAltTitle() + "_" + event.getMode().getModeName() + ".htm");
            if (file.exists()) {
                isHtmlGenerated = true;
            }

            html = html.replaceAll("%generateButton%", isHtmlGenerated ? "<button value=\"Delete HTML\" action=\"bypass admin_event_manage mini_edit_modes_generatefile\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">" : "<button value=\"Generate HTML\" action=\"bypass admin_event_manage mini_edit_modes_generatefile\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void removeFeature(PlayerEventInfo gm, String featureName) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            AbstractFeature feature = null;
            Iterator var5 = event.getMode().getFeatures().iterator();

            while (var5.hasNext()) {
                AbstractFeature f = (AbstractFeature) var5.next();
                if (f.getType().toString().equals(featureName)) {
                    feature = f;
                    break;
                }
            }

            if (feature == null) {
                gm.sendMessage("This feature doesn't exist for this mode.");
            } else {
                event.getMode().getFeatures().remove(feature);
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void setActiveFeatureCategory(PlayerEventInfo gm, String categoryName) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            if (categoryName.equals("All")) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory = null;
            } else {
                FeatureCategory category = null;
                FeatureCategory[] var5 = FeatureCategory.values();
                int var6 = var5.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    FeatureCategory fc = var5[var7];
                    if (fc.toString().equalsIgnoreCase(categoryName)) {
                        category = fc;
                        break;
                    }
                }

                if (category == null) {
                    gm.sendMessage("This category doesn't exist.");
                } else {
                    ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory = category;
                }
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void setActiveShowedFeature(PlayerEventInfo gm, String featureTypeName) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed.toString().equals(featureTypeName)) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed = null;
            } else {
                FeatureType[] var4 = FeatureType.values();
                int var5 = var4.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    FeatureType type = var4[var6];
                    if (type.toString().equalsIgnoreCase(featureTypeName)) {
                        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed = type;
                    }
                }

            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showNewMiniEventFeatureMenu(PlayerEventInfo gm, String featureType) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            EventMode mode = event.getMode();
            String html;
            StringBuilder tb;
            if (!featureType.equals("Default")) {
                html = null;
                tb = null;
                Class[] classParams = new Class[]{EventType.class, PlayerEventInfo.class, String.class};

                Constructor _constructor;
                try {
                    _constructor = Class.forName("gr.sr.events.engine.mini.features." + featureType + "Feature").getConstructor(classParams);
                } catch (Exception var19) {
                    var19.printStackTrace();
                    return;
                }

                AbstractFeature feature;
                try {
                    Object[] objectParams = new Object[]{((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, null, null};
                    Object tmp = _constructor.newInstance(objectParams);
                    feature = (AbstractFeature) tmp;
                } catch (Exception var18) {
                    var18.printStackTrace();
                    return;
                }

                mode.addFeature(feature);
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, mode.getModeId());
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed = null;
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory = null;
                this.showEditFeature(gm, featureType);
            } else {
                html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_featureedit_new.htm");
                tb = new StringBuilder();
                int count = 0;
                FeatureType[] var12 = FeatureType.values();
                int var13 = var12.length;

                for (int var14 = 0; var14 < var13; ++var14) {
                    FeatureType feature = var12[var14];
                    if (!feature.toString().equals(featureType)) {
                        boolean alreadyIn = false;
                        FeatureInfo info = FeatureBase.getInstance().get(feature);
                        if (info == null) {
                            SunriseLoader.debug("Feature " + feature.toString() + " isn't in FeatureBase.", Level.WARNING);
                        } else {
                            boolean allowedForEvent = info.isForEvent(event.getEventType());
                            boolean allowedForCategory = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory == null || ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory == info.getCategory();
                            Iterator var16 = mode.getFeatures().iterator();

                            while (var16.hasNext()) {
                                AbstractFeature f = (AbstractFeature) var16.next();
                                if (f.getType() == feature) {
                                    alreadyIn = true;
                                    break;
                                }
                            }

                            if (!alreadyIn && allowedForEvent && allowedForCategory) {
                                tb.append("<table width=275 bgcolor=333333><tr>");
                                tb.append("<td width=110><font color=ac9887>" + info.getVisibleName() + "</font></td>");
                                tb.append("<td width=75 align=left><font color=ac9887><a action=\"bypass admin_event_manage mini_edit_feature_showinfo " + feature.toString() + "\">View Info</a></font></td>");
                                tb.append("<td width=90 align=right><button value=\"Add to mode\" action=\"bypass admin_event_manage mini_edit_feature_add " + feature.toString() + "\" width=90 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                                tb.append("</tr></table>");
                                if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureShowed == feature) {
                                    tb.append("<table width=275><tr>");
                                    tb.append("<td width=275><font color=9f9f9f>" + info.getDesc() + "</font></td>");
                                    tb.append("</tr></table>");
                                }

                                tb.append("<br>");
                                ++count;
                            }
                        }
                    }
                }

                if (count == 0) {
                    String cat = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory == null ? "." : " for category " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory.toString() + ".";
                    tb.append("<font color=B46F6B>No features are available" + cat + "</font>");
                }

                html = html.replaceAll("%features%", tb.toString());
                if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory != null) {
                    html = html.replaceAll("%category%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeFeatureCategory.toString());
                } else {
                    html = html.replaceAll("%category%", "All");
                }

                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%type%", event.getEventType().getAltTitle());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
                html = html.replaceAll("%modeName%", mode.getModeName());
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void setFeatureConfigValue(PlayerEventInfo gm, String featureName, String configName, String value) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            AbstractFeature feature = null;
            Iterator var7 = event.getMode().getFeatures().iterator();

            while (var7.hasNext()) {
                AbstractFeature f = (AbstractFeature) var7.next();
                if (f.getType().toString().equals(featureName)) {
                    feature = f;
                    break;
                }
            }

            if (feature == null) {
                gm.sendMessage("This feature doesn't exist for this mode.");
            } else {
                feature.setValueFor(configName, value);
                EventConfig.getInstance().updateEventModes(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
                gm.sendMessage("Done");
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditFeatureConfig(PlayerEventInfo gm, String featureName, String config) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            AbstractFeature feature = null;
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_featureedit_config.htm");
            Iterator var7 = event.getMode().getFeatures().iterator();

            while (var7.hasNext()) {
                AbstractFeature f = (AbstractFeature) var7.next();
                if (f.getType().toString().equals(featureName)) {
                    feature = f;
                    break;
                }
            }

            if (feature == null) {
                gm.sendMessage("This feature doesn't exist for this mode.");
            } else {
                FeatureConfig configInfo = feature.getConfig(config);
                int inputType = configInfo._inputFormType;
                if (inputType == 1) {
                    html = html.replaceAll("%input%", "<edit var=\"value\" width=140 height=15>");
                } else if (inputType == 2) {
                    html = html.replaceAll("%input%", "<multiedit var=\"value\" width=180 height=30>");
                }

                html = html.replaceAll("%key%", config);
                html = html.replaceAll("%info%", configInfo._desc);
                html = html.replaceAll("%value%", feature.getValueFor(config));
                html = html.replaceAll("%featureName%", feature.getType().toString());
                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%type%", event.getEventType().getAltTitle());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showEditFeature(PlayerEventInfo gm, String featureName) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            AbstractFeature feature = null;
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_featureedit.htm");
            Iterator var6 = event.getMode().getFeatures().iterator();

            while (var6.hasNext()) {
                AbstractFeature f = (AbstractFeature) var6.next();
                if (f.getType().toString().equals(featureName)) {
                    feature = f;
                    break;
                }
            }

            if (feature == null) {
                gm.sendMessage("This feature doesn't exist for this mode.");
            } else {
                StringBuilder tb = new StringBuilder();
                boolean bg = true;

                for (Iterator var8 = feature.getConfigs().iterator(); var8.hasNext(); bg = !bg) {
                    FeatureConfig c = (FeatureConfig) var8.next();
                    String value = feature.getValueFor(c._name);
                    if (value.length() > 19) {
                        value = value.substring(0, 20) + "..";
                    }

                    tb.append("<table width=280" + (bg ? " bgcolor=333333" : "") + ">");
                    tb.append("<tr>");
                    tb.append("<td align=left><font color=ac9887><a action=\"bypass admin_event_manage mini_edit_featureconfig " + featureName + " " + c._name + "\">" + c._name + "</font></td>");
                    tb.append("<td align=right><font color=9f9f9f><a action=\"bypass admin_event_manage mini_edit_featureconfig " + featureName + " " + c._name + "\">" + value + "</a></font></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                }

                html = html.replaceAll("%options%", tb.toString());
                html = html.replaceAll("%featureName%", feature.getType().toString());
                html = html.replaceAll("%desc%", FeatureBase.getInstance().get(feature.getType()).getDesc());
                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%type%", event.getEventType().getAltTitle());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showCompactModesMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_menu_compact.htm");
        StringBuilder tb = new StringBuilder();
        tb.append("<table width=270>");
        Iterator var4 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

        while (var4.hasNext()) {
            Entry<Integer, MiniEventManager> e = (Entry) var4.next();
            tb.append("<tr><td width=180><font color=" + (((MiniEventManager) e.getValue()).getMode().isRunning() ? "ac9887" : "606060") + "><a action=\"bypass admin_event_manage mini_edit_modes_menu " + ((MiniEventManager) e.getValue()).getMode().getModeId() + "\">" + ((MiniEventManager) e.getValue()).getMode().getModeName() + "</a></font></td>");
            if (!((MiniEventManager) e.getValue()).getMode().isAllowed()) {
                tb.append("<td width=90><font color=B46F6B><a action=\"bypass admin_event_manage mini_edit_modes_enabledisable " + ((MiniEventManager) e.getValue()).getMode().getModeId() + "\">Disabled</a></font></td></tr>");
            } else {
                tb.append("<td width=90><font color=74BE85><a action=\"bypass admin_event_manage mini_edit_modes_enabledisable " + ((MiniEventManager) e.getValue()).getMode().getModeId() + "\">Enabled</a></font></td></tr>");
            }
        }

        tb.append("</table>");
        html = html.replaceAll("%modes%", tb.toString());
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void showModesMenu(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event == null) {
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = 1;
            event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        }

        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_modes_menu.htm");
            StringBuilder tb = new StringBuilder();
            tb.append(event.getMode().getModeName() + ";");
            Iterator var5 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

            while (var5.hasNext()) {
                Entry<Integer, MiniEventManager> e = (Entry) var5.next();
                if (!((MiniEventManager) e.getValue()).getMode().getModeName().equals(event.getMode().getModeName())) {
                    tb.append(((MiniEventManager) e.getValue()).getMode().getModeName() + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%modes%", result.substring(0, result.length() - 1));
            tb = new StringBuilder();
            int sizer = 0;
            tb.append("<table width=270 bgcolor=4f4f4f>");
            Iterator var7 = event.getMode().getFeatures().iterator();

            while (var7.hasNext()) {
                AbstractFeature feature = (AbstractFeature) var7.next();
                tb.append("<tr>");
                tb.append("<td width=95><font color=ac9887>" + feature.getType().toString() + "</font></td>");
                tb.append("<td width=105><font color=9f9f9f>");
                Iterator var9 = feature.getConfigs().iterator();

                while (var9.hasNext()) {
                    FeatureConfig c = (FeatureConfig) var9.next();
                    if (sizer >= 12) {
                        tb.append("...");
                        break;
                    }

                    String value = feature.getValueFor(c._name);
                    sizer += value.length() + 2;
                    tb.append(" " + value);
                    tb.append(";");
                }

                sizer = 0;
                tb.append("</font></td>");
                tb.append("<td width=70 align=right><button value=\"Edit\" action=\"bypass admin_event_manage mini_edit_feature " + feature.getType().toString() + "\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr>");
            }

            tb.append("</table>");
            html = html.replaceAll("%features%", tb.toString());
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%modeId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId));
            html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(event.getEventType()) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
            tb = new StringBuilder();
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
            String[] var14 = this.miniEventEditingPages;
            int var15 = var14.length;

            for (int var16 = 0; var16 < var15; ++var16) {
                String page = var14[var16];
                if (!page.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                    tb.append(page + ";");
                }
            }

            result = tb.toString();
            html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showManualMatchSetMapMenu(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_manualmatch_map.htm");
            StringBuilder tb = new StringBuilder();
            Iterator var5 = EventMapSystem.getInstance().getMaps(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent).values().iterator();

            while (var5.hasNext()) {
                EventMap map = (EventMap) var5.next();
                tb.append("<button value=\"" + map.getMapName() + "\" action=\"bypass admin_event_manage mini_manual_match_set_map " + map.getGlobalId() + "\" width=160 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                tb.append("<br1>");
            }

            html = html.replaceAll("%maps%", tb.toString());
            html = html.replaceAll("%mapsAmmount%", String.valueOf(EventMapSystem.getInstance().getMaps(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent).values().size()));
            EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
            html = html.replaceAll("%activeMap%", map == null ? "Unknown map" : map.getMapName());
            html = html.replaceAll("%activeMapId%", String.valueOf(map == null ? 0 : map.getGlobalId()));
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().toString());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showMatches(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_showmatches.htm");
            StringBuilder tb = new StringBuilder();
            tb.append(event.getMode().getModeName() + ";");
            Iterator var5 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

            while (var5.hasNext()) {
                Entry<Integer, MiniEventManager> e = (Entry) var5.next();
                if (!((MiniEventManager) e.getValue()).getMode().getModeName().equals(event.getMode().getModeName())) {
                    tb.append(((MiniEventManager) e.getValue()).getMode().getModeName() + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%modes%", result.substring(0, result.length() - 1));
            tb = new StringBuilder();
            Iterator var14 = event.getActiveGames().iterator();

            int i;
            while (var14.hasNext()) {
                MiniEventGame match = (MiniEventGame) var14.next();
                tb.append("<button value=\"");
                i = 1;
                EventTeam[] var9 = match.getTeams();
                int var10 = var9.length;

                for (int var11 = 0; var11 < var10; ++var11) {
                    EventTeam t = var9[var11];
                    tb.append(t.getTeamName());
                    if (i < match.getTeams().length) {
                        tb.append(" VS ");
                    }

                    ++i;
                }

                tb.append("\" action=\"bypass admin_event_manage mini_edit_match " + match.getGameId() + "\" width=180 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                tb.append("<br1>");
            }

            html = html.replaceAll("%matches%", tb.toString());
            String fontColor = "9f9f9f";
            if (event.getActiveGames().size() == event.getMaxGamesCount()) {
                fontColor = "B46F6B";
            }

            html = html.replaceAll("%matchesAmmount%", "<font color=" + fontColor + ">" + event.getActiveGames().size() + "/" + event.getMaxGamesCount() + "</font>");
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().toString());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(event.getEventType()) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
            tb = new StringBuilder();
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
            String[] var16 = this.miniEventEditingPages;
            i = var16.length;

            for (int var17 = 0; var17 < i; ++var17) {
                String s = var16[var17];
                if (!s.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                    tb.append(s + ";");
                }
            }

            result = tb.toString();
            html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void editMatch(PlayerEventInfo gm, int matchId) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            MiniEventGame game = null;
            Iterator var5 = event.getActiveGames().iterator();

            while (var5.hasNext()) {
                MiniEventGame g = (MiniEventGame) var5.next();
                if (g.getGameId() == matchId) {
                    game = g;
                    break;
                }
            }

            if (game != null) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMatch = game.getGameId();
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_editmatch.htm");
                StringBuilder tb = new StringBuilder();
                tb.append("<table width=200>");
                EventTeam[] var7 = game.getTeams();
                int var8 = var7.length;

                int var9;
                for (var9 = 0; var9 < var8; ++var9) {
                    EventTeam team = var7[var9];
                    tb.append("<tr>");
                    tb.append("<td><font color=" + team.getNameColorInString() + ">" + team.getTeamName() + "</font></td>");
                    tb.append("<td><button value=\"Show\" action=\"bypass admin_event_manage mini_show_team_members " + team.getTeamId() + "\" width=60 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                }

                tb.append("</table>");
                html = html.replaceAll("%teams%", tb.toString());
                html = html.replaceAll("%matchId%", String.valueOf(game.getGameId()));
                tb = new StringBuilder();
                int i = 1;
                EventTeam[] var15 = game.getTeams();
                var9 = var15.length;

                for (int var16 = 0; var16 < var9; ++var16) {
                    EventTeam team = var15[var16];
                    tb.append("<font color=" + team.getNameColorInString() + "> " + team.getScore() + "</font>");
                    if (i < game.getTeams().length) {
                        tb.append(" : ");
                    }

                    ++i;
                }

                html = html.replaceAll("%score%", tb.toString());
                html = html.replaceAll("%activeMap%", game.getMap().getMapName());
                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%type%", event.getEventType().toString());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }

        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void watchMatch(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            MiniEventGame game = null;
            Iterator var4 = event.getActiveGames().iterator();

            while (var4.hasNext()) {
                MiniEventGame g = (MiniEventGame) var4.next();
                if (g.getGameId() == ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMatch) {
                    game = g;
                    break;
                }
            }

            if (game == null) {
                gm.sendMessage("This match doesn't exist.");
            } else {
                gm.setInstanceId(game.getInstanceId());
                Loc loc = null;
                EventTeam[] var12 = game.getTeams();
                int var6 = var12.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    EventTeam team = var12[var7];
                    Iterator var9 = team.getPlayers().iterator();

                    while (var9.hasNext()) {
                        PlayerEventInfo pi = (PlayerEventInfo) var9.next();
                        if (pi.isOnline()) {
                            loc = new Loc(pi.getX(), pi.getY(), pi.getZ());
                            break;
                        }
                    }
                }

                if (loc != null) {
                    gm.teleToLocation(loc, false);
                } else {
                    gm.sendMessage("There's no player inside the match.");
                }

            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void abortMatch(PlayerEventInfo gm) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            MiniEventGame game = null;
            Iterator var4 = event.getActiveGames().iterator();

            while (var4.hasNext()) {
                MiniEventGame g = (MiniEventGame) var4.next();
                if (g.getGameId() == ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMatch) {
                    game = g;
                    break;
                }
            }

            if (game == null) {
                gm.sendMessage("This match doesn't exist.");
            } else {
                game.broadcastMessage("Match was aborted by a GM.", false);
                game.clearEvent();
                gm.sendMessage("Match was successfully aborted.");
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showTeamMembers(PlayerEventInfo gm, int teamId) {
        MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        if (event != null && !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
            MiniEventGame game = null;
            Iterator var5 = event.getActiveGames().iterator();

            while (var5.hasNext()) {
                MiniEventGame g = (MiniEventGame) var5.next();
                if (g.getGameId() == ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMatch) {
                    game = g;
                    break;
                }
            }

            if (game == null) {
                gm.sendMessage("This match doesn't exist.");
            } else {
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_showteam.htm");
                StringBuilder tb = new StringBuilder();
                tb.append("<table width=200>");
                Iterator var7 = game.getTeams()[teamId - 1].getPlayers().iterator();

                while (var7.hasNext()) {
                    PlayerEventInfo player = (PlayerEventInfo) var7.next();
                    tb.append("<tr>");
                    tb.append("<td><font color=9f9f9f>" + player.getPlayersName() + "</font></td>");
                    tb.append("<td><button value=\"Teleport\" action=\"bypass admin_teleportto " + player.getPlayersName() + "\" width=65 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                }

                tb.append("</table>");
                html = html.replaceAll("%players%", tb.toString());
                html = html.replaceAll("%matchId%", String.valueOf(game.getGameId()));
                html = html.replaceAll("%name%", event.getEventName());
                html = html.replaceAll("%teamName%", game.getTeams()[teamId - 1].getTeamName());
                html = html.replaceAll("%type%", event.getEventType().toString());
                html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        } else {
            gm.sendMessage("This mini event doesn't exist.");
        }
    }

    private void showManualMatchMenu(PlayerEventInfo gm) {
        try {
            MiniEventManager event = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            if (event == null || ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
                gm.sendMessage("This mini event doesn't exist.");
                return;
            }

            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_manualmatch.htm");
            StringBuilder tb = new StringBuilder();
            tb.append("<table width=200>");
            Iterator var5 = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.entrySet().iterator();

            while (var5.hasNext()) {
                Entry<Integer, List<PlayerEventInfo>> element = (Entry) var5.next();
                Entry<Integer, List<PlayerEventInfo>> e = element;

                for (Iterator var8 = ((List) element.getValue()).iterator(); var8.hasNext(); tb.append("</tr>")) {
                    PlayerEventInfo player = (PlayerEventInfo) var8.next();
                    tb.append("<tr>");
                    tb.append("<td><font color=" + EventManager.getInstance().getTeamColorForHtml((Integer) e.getKey()) + ">" + (player != null && player.isOnline() ? player.getPlayersName() : "Offline. Clear list") + "</font></td>");
                    if (player != null) {
                        tb.append("<td><button value=\"Remove\" action=\"bypass admin_event_manage mini_manual_match_rem_team " + player != null ? player.getPlayersId() : "0\" width=70 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    }
                }
            }

            tb.append("<tr>");
            tb.append("<td></td><td><button value=\"Clear\" action=\"bypass admin_event_manage mini_manual_match_clear\" width=70 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
            tb.append("</tr>");
            tb.append("</table>");
            html = html.replaceAll("%players%", tb.toString());
            EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
            html = html.replaceAll("%activeMap%", map == null ? "Random map" : map.getMapName());
            html = html.replaceAll("%activeMapId%", String.valueOf(map == null ? 0 : map.getGlobalId()));
            html = html.replaceAll("%name%", event.getEventName());
            html = html.replaceAll("%type%", event.getEventType().toString());
            html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } catch (Exception var10) {
            var10.printStackTrace();
        }

    }

    private void setManualMatchMap(PlayerEventInfo gm, int mapId) {
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap = mapId;
        gm.sendMessage("Map for this event has been changed.");
    }

    private void startManualMatch(PlayerEventInfo gm) {
        try {
            MiniEventManager manager = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            RegistrationData[] data = new RegistrationData[((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.size()];

            Entry i;
            LinkedList infos;
            for (Iterator var4 = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.entrySet().iterator(); var4.hasNext(); data[(Integer) i.getKey() - 1] = new RegistrationData(infos)) {
                i = (Entry) var4.next();
                infos = new LinkedList();
                Iterator var7 = ((List) i.getValue()).iterator();

                while (var7.hasNext()) {
                    PlayerEventInfo player = (PlayerEventInfo) var7.next();
                    infos.add(CallBack.getInstance().getPlayerBase().addInfo(player));
                }
            }

            if (!manager.checkCanFight(gm, data)) {
                gm.sendMessage("Game can't be started. Check registered players and try it again.");
                return;
            }

            EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
            if (!manager.launchGame(data, map)) {
                gm.sendMessage("Game can't be started, propably due to missing teams or players");
                return;
            }

            gm.sendMessage("Game was successfully started.");
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).manualMatchPlayers.clear();
        } catch (Exception var9) {
            gm.sendMessage("Something is wrong with match settings, propably missing team. Error: " + var9.toString());
            var9.printStackTrace();
        }

    }

    private void showMiniEventMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_menu.htm");
        html = html.replaceAll("%objectId%", String.valueOf(0));
        int count = 0;
        int i = 0;
        StringBuilder tb = new StringBuilder();
        tb.append("<font color=LEVEL>Enabled Mini Events:</font><br1><table width=281>");
        Iterator var6 = EventManager.getInstance().getMiniEvents().entrySet().iterator();

        Entry e;
        while (var6.hasNext()) {
            e = (Entry) var6.next();
            if (((EventType) e.getKey()).allowEdits() && EventConfig.getInstance().isEventAllowed((EventType) e.getKey())) {
                if (i == 0) {
                    tb.append("<tr>");
                }

                ++i;
                tb.append("<td><button value=\"" + ((EventType) e.getKey()).getHtmlTitle() + "\" action=\"bypass admin_event_manage mini_edit_event " + ((EventType) e.getKey()).getId() + "\" width=140 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                ++count;
                if (i == 2) {
                    tb.append("</tr>");
                    i = 0;
                }
            }
        }

        if (i != 0 && i % 2 == 1) {
            tb.append("</tr>");
        }

        tb.append("</table>");
        if (count > 0) {
            html = html.replaceAll("%allowed_events%", tb.toString());
        } else {
            html = html.replaceAll("%allowed_events%", "");
        }

        count = 0;
        i = 0;
        tb = new StringBuilder();
        tb.append("<font color=LEVEL>Disabled Mini Events:</font><br1><table width=281>");
        var6 = EventManager.getInstance().getMiniEvents().entrySet().iterator();

        while (var6.hasNext()) {
            e = (Entry) var6.next();
            if (e.getKey() != EventType.Unassigned && ((EventType) e.getKey()).allowEdits() && !EventConfig.getInstance().isEventAllowed((EventType) e.getKey())) {
                if (i == 0) {
                    tb.append("<tr>");
                }

                ++i;
                tb.append("<td><button value=\"" + ((EventType) e.getKey()).getHtmlTitle() + "\" action=\"bypass admin_event_manage mini_edit_event " + ((EventType) e.getKey()).getId() + "\" width=140 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                ++count;
                if (i == 2) {
                    tb.append("</tr>");
                    i = 0;
                }
            }
        }

        if (i != 0 && i % 2 == 1) {
            tb.append("</tr>");
        }

        tb.append("</table>");
        if (count > 0) {
            html = html.replaceAll("%blocked_events%", tb.toString());
        } else {
            html = html.replaceAll("%blocked_events%", "");
        }

        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void setModeId(PlayerEventInfo gm, String mode) {
        Iterator var3 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

        while (var3.hasNext()) {
            Entry<Integer, MiniEventManager> e = (Entry) var3.next();
            if (((MiniEventManager) e.getValue()).getMode().getModeName().equals(mode)) {
                gm.sendMessage("You're now editting mode " + mode + " (ID " + e.getKey() + ").");
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = (Integer) e.getKey();
            }
        }

    }

    private void showEditMiniEventMenu(PlayerEventInfo gm, int eventId, String page) {
        EventType type = null;
        EventType[] var5 = EventType.values();
        int var6 = var5.length;

        for (int var7 = 0; var7 < var6; ++var7) {
            EventType t = var5[var7];
            if (t.getId() == eventId) {
                type = t;
            }
        }

        if (type != null) {
            this.showEditMiniEventMenu(gm, type, page);
        } else {
            gm.sendMessage("This event doesn't exist.");
        }
    }

    private void addAvailableEvent(PlayerEventInfo gm) {
        EventConfig.getInstance().setEventAllowed(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, true);
        gm.sendMessage("Event's been enabled.");
    }

    private void removeAvailableEvent(PlayerEventInfo gm) {
        EventConfig.getInstance().setEventAllowed(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, false);
        gm.sendMessage("Event's been disabled.");
    }

    private void showSpawnTypeInfoMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_spawntypeinfo_menu.htm");
        StringBuilder tb = new StringBuilder();
        EventType[] var4 = EventType.values();
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            EventType type = var4[var6];
            if (type != EventType.Unassigned && type != EventType.Tournament) {
                tb.append("<button value=\"" + type.getHtmlTitle() + "\" action=\"bypass admin_event_manage spawn_type_info_event " + type.getAltTitle() + "\" width=160 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                tb.append("<br1>");
            }
        }

        html = html.replaceAll("%events%", tb.toString());
        html = html.replaceAll("%spawnId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn));
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void showSpawnTypeInfoEvent(PlayerEventInfo gm, String eventName) {
        EventType type = null;
        EventType[] var4 = EventType.values();
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            EventType t = var4[var6];
            if (t.getAltTitle().equals(eventName)) {
                type = t;
            }
        }

        if (type == null) {
            gm.sendMessage("This event doesn't exist. 1");
        } else {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_spawntypeinfo_info.htm");
            StringBuilder tb = new StringBuilder();
            MiniEventManager event = EventManager.getInstance().getMiniEvent(type, 1);
            if (event == null) {
                gm.sendMessage("This event doesn't exist.");
            } else {
                tb.append("<table width=280>");
                Iterator var12 = event.getAvailableSpawnTypes().entrySet().iterator();

                while (var12.hasNext()) {
                    Entry<SpawnType, String> e = (Entry) var12.next();
                    tb.append("<tr><td><font color=" + ((SpawnType) e.getKey()).getHtmlColor() + ">" + ((SpawnType) e.getKey()).toString() + "</font></td>");
                    tb.append("<td><font color=9f9f9f>" + (String) e.getValue() + "</font></td></tr>");
                }

                tb.append("</table>");
                html = html.replaceAll("%spawnTypes%", tb.toString());
                html = html.replaceAll("%event%", type.getHtmlTitle());
                html = html.replaceAll("%spawnId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    private void showEditMiniEventMenu(PlayerEventInfo gm, EventType type, String page) {
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId = 1;
        if (type.isRegularEvent()) {
            this.showEditEventMenu(gm, type.getAltTitle(), page);
        } else {
            if (page == null) {
                page = this.miniEventEditingPages[0];
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection = page;
            MiniEventManager event = EventManager.getInstance().getMiniEvent(type, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            if (type != EventType.Unassigned) {
                if (event == null) {
                    event = EventManager.getInstance().getMiniEvent(type, 1);
                }

                if (event == null) {
                    event = EventConfig.getInstance().createDefaultMode(type);
                    gm.sendMessage("The default mode if this event was somehow deleted (or something has simply fucked up!). The engine created a new one, don't forget to reconfigure it!");
                }
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = type;
            if (page.equals(this.miniEventEditingPages[1])) {
                this.showModesMenu(gm);
            } else if (page.equals(this.miniEventEditingPages[2])) {
                this.showRewardsEditation(gm, type.getAltTitle(), RewardPosition.None, (String) null);
            } else if (page.equals(this.miniEventEditingPages[3])) {
                this.showMatches(gm);
            } else if (page.equals(this.miniEventEditingPages[4])) {
                this.showConfigsMenu(gm, type.getAltTitle(), 1);
            } else {
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mini_editevent.htm");
                StringBuilder tb = new StringBuilder();
                Iterator var8 = EventMapSystem.getInstance().getMaps(type).values().iterator();

                while (var8.hasNext()) {
                    EventMap map = (EventMap) var8.next();
                    boolean error = map.getMissingSpawns().length() > 0;
                    tb.append("<table width=280 bgcolor=363636>");
                    tb.append("<tr>");
                    tb.append("<td align=left width=150><font color=" + (error ? "CD6565" : "ac9887") + ">" + map.getMapName() + " </font><font color=54585C>" + map.getGlobalId() + "</font></td>");
                    tb.append("<td align=right width=80><button value=\"" + (error ? "! " : "") + "Status" + (error ? " !" : "") + "\" action=\"bypass admin_event_manage show_map_status " + map.getGlobalId() + " " + type.getAltTitle() + "\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("<td align=right width=80><button value=\"Edit\" action=\"bypass admin_event_manage edit_event_map " + map.getGlobalId() + " 0\" width=70 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                    tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=6>");
                }

                if (!EventMapSystem.getInstance().getMaps(type).isEmpty()) {
                    html = html.replaceAll("%maps%", tb.toString());
                } else {
                    html = html.replaceAll("%maps%", "<font color=B46F6B>No maps available for this event.</font>");
                }

                html = html.replaceAll("%mapsAmmount%", String.valueOf(EventMapSystem.getInstance().getMaps(type).values().size()));
                if (event != null) {
                    html = html.replaceAll("%name%", event.getEventName());
                    html = html.replaceAll("%type%", event.getEventType().toString());
                    html = html.replaceAll("%eventId%", String.valueOf(event.getEventType().getId()));
                    html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(type) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
                    tb = new StringBuilder();
                    tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
                    String[] var12 = this.miniEventEditingPages;
                    int var14 = var12.length;

                    for (int var10 = 0; var10 < var14; ++var10) {
                        String s = var12[var10];
                        if (!s.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                            tb.append(s + ";");
                        }
                    }

                    String result = tb.toString();
                    html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
                } else {
                    html = html.replaceAll("%name%", "Unassigned maps");
                    html = html.replaceAll("%type%", "-");
                    html = html.replaceAll("%eventId%", String.valueOf(0));
                    html = html.replaceAll("%enableDisable%", "N/A");
                    html = html.replaceAll("%event_pages%", "Maps");
                }

                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    private void removeReward(PlayerEventInfo gm) {
        RewardItem item = EventRewardSystem.getInstance().getReward(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
        if (item == null) {
            gm.sendMessage("This item doesn't exist.");
        } else {
            EventRewardSystem.getInstance().removeRewardFromDb(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
        }
    }

    private void saveReward(PlayerEventInfo gm) {
    }

    private void editReward(PlayerEventInfo gm, String parameter, String action) {
        RewardItem item = EventRewardSystem.getInstance().getReward(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
        if (item == null) {
            gm.sendMessage("This item doesn't exist.");
        } else {
            PositionContainer container = EventRewardSystem.getInstance().getRewardPosition(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
            EventRewardSystem.getInstance().removeFromDb(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, container._position, container._parameter, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, item._id, item._minAmmount, item._maxAmmount, item._chance);
            if (action.equals("set_item_id")) {
                if (parameter.startsWith("exp")) {
                    item._id = -1;
                } else if (parameter.startsWith("sp")) {
                    item._id = -2;
                } else if (parameter.startsWith("fame")) {
                    item._id = -3;
                } else {
                    item._id = Integer.parseInt(parameter);
                }
            } else if (action.equals("set_min")) {
                item._minAmmount = Integer.parseInt(parameter);
            } else if (action.equals("set_max")) {
                item._maxAmmount = Integer.parseInt(parameter);
            } else if (action.equals("set_chance")) {
                item._chance = Integer.parseInt(parameter);
            }

            EventRewardSystem.getInstance().addRewardToDb(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, container._position, container._parameter, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, item._id, item._minAmmount, item._maxAmmount, item._chance, true);
        }
    }

    private void addEventReward(PlayerEventInfo gm) {
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent == null) {
            gm.sendMessage("This event doesn't exist.");
        } else if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos == RewardPosition.None) {
            gm.sendMessage("Select a position first.");
            this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
        } else {
            int rewardId = EventRewardSystem.getInstance().createReward(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward = rewardId;
            this.showRewardEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward);
        }
    }

    private void showRewardEditation(PlayerEventInfo gm, int rewardId) {
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent == null) {
            gm.sendMessage("This event doesn't exist.");
        } else {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editreward.htm");
            html = html.replaceAll("%eventName%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString());
            RewardItem item = EventRewardSystem.getInstance().getReward(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, rewardId);
            if (item == null) {
                gm.sendMessage("This item doesn't exist.");
            } else {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingReward = rewardId;
                html = html.replaceAll("%id%", String.valueOf(item._id));
                html = html.replaceAll("%min%", String.valueOf(item._minAmmount));
                html = html.replaceAll("%max%", String.valueOf(item._maxAmmount));
                html = html.replaceAll("%chance%", String.valueOf(item._chance));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    private void showRewardsEditation(PlayerEventInfo gm, String event, RewardPosition position, String parameter) {
        EventType type = null;
        EventType[] var6 = EventType.values();
        int var7 = var6.length;

        for (int var8 = 0; var8 < var7; ++var8) {
            EventType t = var6[var8];
            if (t.toString().equalsIgnoreCase(event) || t.getAltTitle().equalsIgnoreCase(event)) {
                type = t;
            }
        }

        if (type != null && type != EventType.Unassigned) {
            Object eventInstance;
            if (!type.isRegularEvent()) {
                eventInstance = EventManager.getInstance().getMiniEvent(type, 1);
            } else {
                eventInstance = EventManager.getInstance().getMainEvent(type);
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos = position;
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam = parameter;
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = type;
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editrewards.htm");
            html = html.replaceAll("%eventName%", type.getAltTitle());
            html = html.replaceAll("%eventId%", String.valueOf(type.getId()));
            StringBuilder tb;
            String modesPanel;
            if (!type.isRegularEvent()) {
                tb = new StringBuilder();
                tb.append("<table width=270>");
                tb.append("<tr>");
                tb.append("<td width=60><font color=ac9775><a action=\"bypass admin_event_manage mini_set_active_mode reward Default\">Modes:</a></font></td>");
                tb.append("<td width=100><combobox width=88 height=17 var=ebox list=%modes%></td>");
                tb.append("<td width=40><button value=\"Set\" action=\"bypass admin_event_manage mini_set_active_mode reward \\$ebox\" width=45 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("<td width=40><button value=\"New\" action=\"bypass admin_event_manage mini_edit_modes_new\" width=45 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr>");
                tb.append("</table>");
                modesPanel = tb.toString();
            } else {
                modesPanel = "";
            }

            html = html.replaceAll("%modesPanel%", modesPanel);
            if (!type.isRegularEvent()) {
                MiniEventManager manager = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
                if (manager == null) {
                    gm.sendMessage("This mini event doesn't exist.");
                    return;
                }

                tb = new StringBuilder();
                tb.append(manager.getMode().getModeName() + ";");
                Iterator var12 = ((Map) EventManager.getInstance().getMiniEvents().get(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent)).entrySet().iterator();

                while (var12.hasNext()) {
                    Entry<Integer, MiniEventManager> e = (Entry) var12.next();
                    if (!((MiniEventManager) e.getValue()).getMode().getModeName().equals(manager.getMode().getModeName())) {
                        tb.append(((MiniEventManager) e.getValue()).getMode().getModeName() + ";");
                    }
                }

                String result = tb.toString();
                html = html.replaceAll("%modes%", result.substring(0, result.length() - 1));
            } else {
                html = html.replaceAll("%modes%", "N/A");
            }

            tb = new StringBuilder();
            EventRewards rewards = EventRewardSystem.getInstance().getAllRewardsFor(type, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId);
            int i = 0;
            if (rewards != null) {
                int size = 0;
                tb.append("<table bgcolor=4F4F4F width=280>");
                Iterator var14 = rewards.getAllRewards().entrySet().iterator();

                label179:
                while (true) {
                    do {
                        Entry e;
                        do {
                            do {
                                if (!var14.hasNext()) {
                                    tb.append("</table>");
                                    break label179;
                                }

                                e = (Entry) var14.next();
                            } while (((PositionContainer) e.getKey())._position == null);
                        } while (((PositionContainer) e.getKey())._position.posType == null);

                        if (i == 0) {
                            tb.append("<tr>");
                        }

                        tb.append("<td width = 54>");
                        tb.append("<font color=" + (((PositionContainer) e.getKey())._position != position || ((PositionContainer) e.getKey())._parameter != null && !((PositionContainer) e.getKey())._parameter.equals(parameter) ? "9f9f9f" : "LEVEL") + "><a action=\"bypass admin_event_manage edit_event_reward_menu " + type.getAltTitle() + " " + ((PositionContainer) e.getKey())._position.toString() + " " + (((PositionContainer) e.getKey())._parameter == null ? "" : ((PositionContainer) e.getKey())._parameter) + "\">");
                        if (((PositionContainer) e.getKey())._position.posType == PositionType.General) {
                            size += ((PositionContainer) e.getKey())._position.toString().length() + 9;
                            tb.append(((PositionContainer) e.getKey())._position.toString() + " [" + ((Map) e.getValue()).size() + " it.] ");
                        } else if (((PositionContainer) e.getKey())._position.posType == PositionType.Numbered && ((PositionContainer) e.getKey())._position == RewardPosition.KillingSpree) {
                            size += ((PositionContainer) e.getKey())._parameter.length() + 10;
                            tb.append("KS-" + ((PositionContainer) e.getKey())._parameter + ". [" + ((Map) e.getValue()).size() + " it.] ");
                        } else if (((PositionContainer) e.getKey())._position.posType == PositionType.Numbered) {
                            size += ((PositionContainer) e.getKey())._parameter.length() + 10;
                            tb.append(((PositionContainer) e.getKey())._parameter + ". [" + ((Map) e.getValue()).size() + " it.] ");
                        } else if (((PositionContainer) e.getKey())._position.posType == PositionType.Range) {
                            size += ((PositionContainer) e.getKey())._parameter.toString().length() + 9;
                            tb.append(((PositionContainer) e.getKey())._parameter + " [" + ((Map) e.getValue()).size() + " it.] ");
                        } else if (((PositionContainer) e.getKey())._position.posType == PositionType.EventSpecific) {
                            size += ((PositionContainer) e.getKey())._position.toString().length() + 9;
                            tb.append(((PositionContainer) e.getKey())._position + " [" + ((Map) e.getValue()).size() + " it.] ");
                        }

                        tb.append("</a></font></td>");
                        ++i;
                    } while (i != 5 && size < 40);

                    tb.append("</tr>");
                    size = 0;
                    i = 0;
                }
            }

            html = html.replaceAll("%rewardedPositions%", tb.toString());
            tb = new StringBuilder();
            if (position != RewardPosition.None) {
                Iterator var32 = EventRewardSystem.getInstance().getRewards(type, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, position, parameter).entrySet().iterator();

                while (var32.hasNext()) {
                    Entry<Integer, RewardItem> e = (Entry) var32.next();
                    String itemName;
                    if (((RewardItem) e.getValue())._id == -1) {
                        itemName = "XP";
                    } else if (((RewardItem) e.getValue())._id == -2) {
                        itemName = "SP";
                    } else if (((RewardItem) e.getValue())._id == -3) {
                        itemName = "Fame";
                    } else {
                        itemName = CallBack.getInstance().getOut().getItemName(((RewardItem) e.getValue())._id);
                    }

                    tb.append("<button value=\"" + itemName + " (" + ((RewardItem) e.getValue())._minAmmount + "-" + ((RewardItem) e.getValue())._maxAmmount + ")\" action=\"bypass admin_event_manage edit_event_reward " + e.getKey() + "\" width=200 height=19 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                    tb.append("<br1>");
                }
            } else {
                tb.append("Select a position.");
            }

            html = html.replaceAll("%rewards%", tb.toString());
            if (position == RewardPosition.None) {
                html = html.replaceAll("%pos%", "N/A");
            } else {
                html = html.replaceAll("%pos%", position.toString());
            }

            html = html.replaceAll("%parameter%", parameter == null ? "" : parameter);
            String desc = ((Configurable) eventInstance).getDescriptionForReward(position);
            html = html.replaceAll("%desc%", desc == null ? position.description : desc);
            tb = new StringBuilder();
            RewardPosition[] var28 = ((Configurable) eventInstance).getRewardTypes();
            int var30 = var28.length;

            int var16;
            for (var16 = 0; var16 < var30; ++var16) {
                RewardPosition rewardType = var28[var16];
                tb.append(rewardType + ";");
            }

            String s = tb.toString();
            html = html.replaceAll("%rewardTypes%", s.substring(0, s.length() - 1));
            html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(type) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
            tb = new StringBuilder();
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
            String[] var31 = type.isRegularEvent() ? this.mainEventEditingPages : this.miniEventEditingPages;
            var16 = var31.length;

            for (int var35 = 0; var35 < var16; ++var35) {
                String page = var31[var35];
                if (!page.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                    tb.append(page + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        } else {
            gm.sendMessage("This event doesn't exist.");
        }
    }

    public void showAddPositionToRewardedWindow(PlayerEventInfo gm) {
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent == null) {
            gm.sendMessage("This event doesn't exist.");
        } else {
            Object eventInstance;
            if (!((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent()) {
                eventInstance = EventManager.getInstance().getMiniEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, 1);
            } else {
                eventInstance = EventManager.getInstance().getMainEvent(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent);
            }

            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editrewards_addpos.htm");
            html = html.replaceAll("%eventName%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString());
            html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
            StringBuilder tb = new StringBuilder();
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
            String[] var5 = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent() ? this.mainEventEditingPages : this.miniEventEditingPages;
            int total = var5.length;

            int count;
            for (count = 0; count < total; ++count) {
                String page = var5[count];
                if (!page.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                    tb.append(page + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
            html = html.replaceAll("%eventId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getId()));
            tb = new StringBuilder();
            total = 0;
            count = 0;
            boolean b = true;
            String general = "";
            RewardPosition[] var10 = ((Configurable) eventInstance).getRewardTypes();
            int var11 = var10.length;

            int var12;
            RewardPosition pos;
            for (var12 = 0; var12 < var11; ++var12) {
                pos = var10[var12];
                if (pos.posType != null && pos.posType == PositionType.General && EventRewardSystem.getInstance().getAllRewardsFor(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId).getContainer(pos, (String) null) == null) {
                    ++count;
                    general = general + pos.toString() + ";";
                }
            }

            if (count > 0) {
                ++total;
                if (general.length() > 0) {
                    general = general.substring(0, general.length() - 1);
                }

                tb.append("<table width=280><tr>");
                tb.append("<td width=90 align=left><font color=9f9f9f><a action=\"bypass admin_event_manage show_add_position_window_help\">General:</a></font></td>");
                tb.append("<td width=160 align=right><combobox width=150 height=17 var=gen list=\"" + general + "\"></td>");
                tb.append("<td width=40 align=right><button value=\"Add\" action=\"bypass admin_event_manage add_rewarded_position \\$gen\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr></table>");
                tb.append("<br>");
            } else {
                b = true;
            }

            count = 0;
            var10 = ((Configurable) eventInstance).getRewardTypes();
            var11 = var10.length;

            for (var12 = 0; var12 < var11; ++var12) {
                pos = var10[var12];
                if (pos.posType != null && pos.posType == PositionType.Numbered && pos != RewardPosition.KillingSpree) {
                    ++count;
                }
            }

            if (count > 0) {
                ++total;
                tb.append("<table width=280><tr>");
                tb.append("<td width=150 align=left><font color=9f9f9f><a action=\"bypass admin_event_manage show_add_position_window_help\">Numbered:</a></font></td>");
                tb.append("<td align=right><edit var=\"num\" width=35 height=13></td>");
                tb.append("<td align=left>.</td>");
                tb.append("<td width=40 align=right><button value=\"Add\" action=\"bypass admin_event_manage add_rewarded_position Numbered \\$num\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr></table>");
                tb.append("<br>");
            }

            count = 0;
            var10 = ((Configurable) eventInstance).getRewardTypes();
            var11 = var10.length;

            for (var12 = 0; var12 < var11; ++var12) {
                pos = var10[var12];
                if (pos.posType != null && pos.posType == PositionType.Numbered && pos == RewardPosition.KillingSpree) {
                    ++count;
                }
            }

            if (count > 0) {
                ++total;
                tb.append("<table width=280><tr>");
                tb.append("<td width=90 align=left><font color=9f9f9f><a action=\"bypass admin_event_manage show_add_position_window_help\">Killing Spree:</a></font></td>");
                tb.append("<td align=right><edit var=\"ks\" width=35 height=13></td>");
                tb.append("<td align=left>kills in a row</td>");
                tb.append("<td width=40 align=right><button value=\"Add\" action=\"bypass admin_event_manage add_rewarded_position KillingSpree \\$ks\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr></table>");
                tb.append("<br>");
            }

            count = 0;
            var10 = ((Configurable) eventInstance).getRewardTypes();
            var11 = var10.length;

            for (var12 = 0; var12 < var11; ++var12) {
                pos = var10[var12];
                if (pos.posType != null && pos.posType == PositionType.Range) {
                    ++count;
                }
            }

            if (count > 0) {
                ++total;
                tb.append("<table width=280><tr>");
                tb.append("<td width=150 align=left><font color=9f9f9f><a action=\"bypass admin_event_manage show_add_position_window_help\">Range:</a></font></td>");
                tb.append("<td align=right><edit var=\"ran1\" width=40 height=13></td>");
                tb.append("<td align=left> - </td>");
                tb.append("<td align=left><edit var=\"ran2\" width=40 height=13></td>");
                tb.append("<td align=right><button value=\"Add\" action=\"bypass admin_event_manage add_rewarded_position Range \\$ran1 \\$ran2\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr></table>");
                tb.append("<br>");
            }

            count = 0;
            String bonus = "";
            RewardPosition[] var18 = ((Configurable) eventInstance).getRewardTypes();
            var12 = var18.length;

            for (int var19 = 0; var19 < var12; ++var19) {
                pos = var18[var19];
                if (pos.posType != null && pos.posType == PositionType.EventSpecific && EventRewardSystem.getInstance().getAllRewardsFor(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId).getContainer(pos, (String) null) == null) {
                    ++count;
                    bonus = bonus + pos.toString() + ";";
                }
            }

            if (count > 0) {
                ++total;
                if (bonus.length() > 0) {
                    bonus = bonus.substring(0, bonus.length() - 1);
                }

                tb.append("<table width=280><tr>");
                tb.append("<td width=90 align=left><font color=9f9f9f><a action=\"bypass admin_event_manage show_add_position_window_help\">Custom:</a></font></td>");
                tb.append("<td width=160 align=right><combobox width=150 height=17 var=bon list=\"" + bonus + "\"></td>");
                tb.append("<td width=40 align=right><button value=\"Add\" action=\"bypass admin_event_manage add_rewarded_position \\$bon\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr></table>");
                tb.append("<br>");
            }

            html = html.replaceAll("%general%", tb.toString());
            if (total == 0) {
                html = html.replaceAll("%general%", "<font color=9f9f9f>All available positions have been added already.</font>");
            } else if (b) {
                html = html.replaceAll("%general%", "");
            }

            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    public void showAddPositionToRewardedWindowHelp(PlayerEventInfo gm) {
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent == null) {
            gm.sendMessage("This event doesn't exist.");
        } else {
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editrewards_addpos_help.htm");
            html = html.replaceAll("%eventName%", ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.toString());
            html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
            StringBuilder tb = new StringBuilder();
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
            String[] var4 = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.isRegularEvent() ? this.mainEventEditingPages : this.miniEventEditingPages;
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String page = var4[var6];
                if (!page.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                    tb.append(page + ";");
                }
            }

            String result = tb.toString();
            html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
            html = html.replaceAll("%eventId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getId()));
            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    public void addPositionToRewarded(PlayerEventInfo gm, RewardPosition position, String param) {
        if (EventRewardSystem.getInstance().setPositionRewarded(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, position, param)) {
            gm.sendMessage("The position has been added.");
        } else {
            gm.sendMessage("This position has been added already.");
        }

        this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
    }

    public void removePositionFromRewarded(PlayerEventInfo gm, RewardPosition position, String param) {
        if (EventRewardSystem.getInstance().removePositionRewarded(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventModeId, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam)) {
            gm.sendMessage("The position has been removed");
        } else {
            gm.sendMessage("This position doesn't exist in the list.");
        }

        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos = RewardPosition.None;
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam = null;
        this.showRewardsEditation(gm, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent.getAltTitle(), ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardPos, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingRewardParam);
    }

    private void newSpawn(PlayerEventInfo gm) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else if (map.getSpawns().size() >= 60) {
            gm.sendMessage("Maximimum ammount of spawns per map is 60.");
        } else {
            int newId = map.getNewSpawnId();
            EventSpawn spawn = new EventSpawn(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap, newId, new Loc(gm.getX(), gm.getY(), gm.getZ()), 0, "RegularSpawn");
            spawn.setSaved(false);
            List<EventSpawn> list = new LinkedList();
            list.add(spawn);
            map.addSpawns(list);
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn = newId;
        }
    }

    private boolean saveMap(PlayerEventInfo gm) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
            return true;
        } else {
            this.checkMap(gm, map);
            EventMapSystem.getInstance().addMapToDb(map, false);
            Iterator var3 = map.getEvents().iterator();

            EventType type;
            do {
                if (!var3.hasNext()) {
                    return false;
                }

                type = (EventType) var3.next();
            } while (!type.isRegularEvent());

            return true;
        }
    }

    private void checkMap(PlayerEventInfo gm, EventMap map) {
        map.checkMap(gm);
    }

    private void saveSpawn(PlayerEventInfo gm) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventSpawn spawn = map.getSpawn(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
            if (spawn == null) {
                gm.sendMessage("The spawn with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn + " doesn't exist.");
            } else {
                if (spawn.getSpawnType() == SpawnType.Door) {
                    if (!CallBack.getInstance().getOut().doorExists(spawn.getDoorId())) {
                        gm.sendMessage("Door with ID " + spawn.getDoorId() + " DOESN'T exist! Spawn won't be saved until you fix it.");
                    }
                } else if (spawn.getSpawnType() == SpawnType.Flag) {
                    if (spawn.getNote() == null || spawn.getNote().equals("")) {
                        gm.sendMessage("Spawn's NOTE is not specified!!");
                        gm.sendMessage("Put in spawn's note flag's heading, or select default value (click on it \"note\").");
                    }
                } else if (spawn.getSpawnType() == SpawnType.Fence) {
                    if (spawn.getNote() == null || spawn.getNote().equals("")) {
                        gm.sendMessage("Spawn's NOTE is not specified!!");
                        gm.sendMessage("Spawn's note defines fence's WIDTH and LENGTH.");
                        gm.sendMessage("Separate both values by empty space, example \"200 300\" will spawn fence with width = 200 and length = 300.");
                        return;
                    }

                    try {
                        StringTokenizer st = new StringTokenizer(spawn.getNote());
                        int width = Integer.parseInt(st.nextToken());
                        int length = Integer.parseInt(st.nextToken());
                        gm.sendMessage("Fence's Width = " + width + ", Length = " + length + ".");
                    } catch (Exception var7) {
                        gm.sendMessage("Spawn's NOTE is not specified!!");
                        gm.sendMessage("Spawn's note defines fence's WIDTH and LENGTH.");
                        gm.sendMessage("Separate both values by empty space, example \"200 300\" will spawn fence with width = 200 and length = 300.");
                        return;
                    }
                }

                if (spawn.isSaved()) {
                    gm.sendMessage("Spawn saved.");
                } else {
                    gm.sendMessage("Spawn successfully sent to database.");
                }

                EventMapSystem.getInstance().addSpawnToDb(spawn);
            }
        }
    }

    private void editSpawn(PlayerEventInfo gm, String parameter, String action) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventSpawn spawn = map.getSpawn(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn);
            if (spawn == null) {
                gm.sendMessage("The spawn with ID " + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn + " doesn't exist.");
            } else {
                if (action.startsWith("set_id")) {
                    spawn.setId(Integer.parseInt(parameter));
                    ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn = spawn.getSpawnId();
                } else if (action.startsWith("set_team")) {
                    spawn.setTeamId(Integer.parseInt(parameter));
                } else if (action.startsWith("set_x")) {
                    spawn.setX(Integer.parseInt(parameter));
                } else if (action.startsWith("set_y")) {
                    spawn.setY(Integer.parseInt(parameter));
                } else if (action.startsWith("set_z")) {
                    spawn.setZ(Integer.parseInt(parameter));
                } else {
                    StringTokenizer st;
                    String width;
                    String length;
                    if (action.startsWith("set_wawenumber")) {
                        if (spawn.getNote() == null) {
                            spawn.setNote("20001 1 First");
                        }

                        try {
                            st = new StringTokenizer(spawn.getNote());
                            width = st.nextToken();
                            length = st.nextToken();
                            spawn.setNote(width + " " + length + " " + parameter);
                        } catch (Exception var18) {
                            gm.sendMessage("Error while changing wawe number for this mob spawn: " + var18.toString());
                            spawn.setNote("20001 1 First");
                            return;
                        }
                    } else {
                        String wawe;
                        int id;
                        NpcTemplateData t;
                        if (action.startsWith("set_mobid")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("20001 1 First");
                            }

                            id = Integer.parseInt(parameter);
                            t = new NpcTemplateData(id);
                            if (!t.exists()) {
                                gm.sendMessage("Mob with ID " + id + " DOESN'T exist! Please fix it.");
                            }

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                st.nextToken();
                                wawe = st.nextToken();
                                String wawe2 = st.nextToken();
                                spawn.setNote(id + " " + wawe + " " + wawe2);
                            } catch (Exception var17) {
                                gm.sendMessage("Error while changing mob id for this mob spawn: " + var17.toString());
                                spawn.setNote("20001 1 First");
                                return;
                            }
                        } else if (action.startsWith("set_mobammount")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("20001 1 First");
                            }

                            id = Integer.parseInt(parameter);

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                length = st.nextToken();
                                st.nextToken();
                                wawe = st.nextToken();
                                spawn.setNote(length + " " + id + " " + wawe);
                            } catch (Exception var16) {
                                gm.sendMessage("Error while changing mob ammount for this mob spawn: " + var16.toString());
                                spawn.setNote("20001 1 First");
                                return;
                            }
                        } else if (action.startsWith("set_dooraction_init")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("Default Default");
                            }

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                st.nextToken();
                                length = st.nextToken();
                                spawn.setNote(parameter + " " + length);
                            } catch (Exception var15) {
                                gm.sendMessage("Error while changing actions for this door: " + var15.toString());
                                spawn.setNote("Default Default");
                                return;
                            }
                        } else if (action.startsWith("set_dooraction_start")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("Default Default");
                            }

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                width = st.nextToken();
                                spawn.setNote(width + " " + parameter);
                            } catch (Exception var14) {
                                gm.sendMessage("Error while changing actions for this door: " + var14.toString());
                                spawn.setNote("Default Default");
                                return;
                            }
                        } else if (action.startsWith("set_width")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("100 100");
                            }

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                st.nextToken();
                                length = st.nextToken();
                                spawn.setNote(parameter + " " + length);
                            } catch (Exception var13) {
                                gm.sendMessage("Error while changing width / length for this fence: " + var13.toString());
                                spawn.setNote("100 100");
                                EventManager.getInstance().debug(var13);
                                return;
                            }
                        } else if (action.startsWith("set_length")) {
                            if (spawn.getNote() == null) {
                                spawn.setNote("100 100");
                            }

                            try {
                                st = new StringTokenizer(spawn.getNote());
                                width = st.nextToken();
                                spawn.setNote(width + " " + parameter);
                            } catch (Exception var12) {
                                gm.sendMessage("Error while changing width / length for this fence: " + var12.toString());
                                spawn.setNote("100 100");
                                EventManager.getInstance().debug(var12);
                                return;
                            }
                        } else if (action.startsWith("default_loc")) {
                            id = gm.getX();
                            int y = gm.getY();
                            int z = gm.getZ();
                            spawn.setX(id);
                            spawn.setY(y);
                            spawn.setZ(z);
                        } else if (action.startsWith("set_note")) {
                            spawn.setNote(parameter.equals("null") ? "" : parameter);
                        } else if (action.startsWith("set_npc_id")) {
                            try {
                                id = Integer.parseInt(parameter);
                                t = new NpcTemplateData(id);
                                if (!t.exists()) {
                                    gm.sendMessage("NPC ID " + id + " doesn't exist.");
                                    return;
                                }

                                spawn.setNote(String.valueOf(id));
                            } catch (Exception var11) {
                                gm.sendMessage("The NPC ID must be a number.");
                            }
                        } else if (action.startsWith("set_type")) {
                            spawn.setType(parameter);
                            if (spawn.getSpawnType() == SpawnType.Door) {
                                spawn.setY(0);
                                spawn.setZ(0);
                                if (gm.getTarget() != null && gm.getTarget().isDoor()) {
                                    spawn.setX(gm.getTarget().getDoorData().getDoorId());
                                    gm.sendMessage("Setted Door ID from you target.");
                                }
                            } else if (spawn.getSpawnType() == SpawnType.Npc) {
                                if (gm.getTarget() != null && gm.getTarget().isNpc()) {
                                    spawn.setNote(String.valueOf(gm.getTarget().getNpc().getNpcId()));
                                    gm.sendMessage("Setted NPC ID from you target.");
                                }
                            } else if (spawn.getSpawnType() == SpawnType.Flag && spawn.getNote() == null) {
                                spawn.setNote("" + gm.getHeading());
                                gm.sendMessage("Filled spawn's note by your current heading.");
                            }
                        }
                    }
                }

                if (spawn.getSpawnType() == SpawnType.Fence) {
                    if (spawn.getSpawnTeam() != 0) {
                        spawn.setTeamId(0);
                    }

                    if (spawn.getNote() == null) {
                        spawn.setNote("100 100");
                    }
                } else if (spawn.getSpawnType() == SpawnType.Door) {
                    if (spawn.getSpawnTeam() != 0) {
                        spawn.setTeamId(0);
                    }

                    if (spawn.getNote() == null) {
                        spawn.setNote("Default Default");
                    }
                } else if (spawn.getSpawnType() == SpawnType.Npc) {
                    if (spawn.getSpawnTeam() != 0) {
                        spawn.setTeamId(0);
                    }

                    if (spawn.getNote() == null) {
                        spawn.setNote("-1");
                    }
                } else if (spawn.getSpawnType() == SpawnType.Monster && spawn.getNote() == null) {
                    spawn.setNote("20001 1 First");
                }

                map.removeSpawn(spawn.getSpawnId(), false);
                List<EventSpawn> list = new LinkedList();
                list.add(spawn);
                map.addSpawns(list);
                gm.sendMessage("Done.");
            }
        }
    }

    private void showEditSpawnMenu(PlayerEventInfo gm, int spawnId) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventSpawn spawn = map.getSpawn(spawnId);
            if (spawn == null) {
                gm.sendMessage("The spawn with ID " + spawnId + " doesn't exist.");
            } else {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn = spawn.getSpawnId();
                String html;
                String init;
                int var10;
                String noteDefault;
                switch (spawn.getSpawnType()) {
                    case Door:
                        html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editspawn_door.htm");
                        if (spawn.getNote() == null) {
                            spawn.setNote("Default Default");
                        }

                        try {
                            StringTokenizer st = new StringTokenizer(spawn.getNote());
                            init = st.nextToken();
                            StringBuilder tb = new StringBuilder();
                            tb.append(init + ";");
                            DoorAction[] var9 = DoorAction.values();
                            var10 = var9.length;

                            for (int var11 = 0; var11 < var10; ++var11) {
                                DoorAction a = var9[var11];
                                if (!init.equals(a.toString())) {
                                    tb.append(a.toString() + ";");
                                }
                            }

                            String temp = tb.toString();
                            html = html.replaceAll("%actions_init%", temp.substring(0, temp.length() - 1));
                            String start = st.nextToken();
                            tb = new StringBuilder();
                            tb.append(start + ";");
                            DoorAction[] var26 = DoorAction.values();
                            int var28 = var26.length;

                            for (int var13 = 0; var13 < var28; ++var13) {
                                DoorAction a = var26[var13];
                                if (!start.equals(a.toString())) {
                                    tb.append(a.toString() + ";");
                                }
                            }

                            temp = tb.toString();
                            html.replaceAll("%actions_start%", temp.substring(0, temp.length() - 1));
                        } catch (Exception var16) {
                            gm.sendMessage("Error while changing door's actions. Reverted to default values.");
                            spawn.setNote("Default Default");
                            return;
                        }
                    case Fence:
                        html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editspawn_fence.htm");
                        if (spawn.getNote() == null) {
                            init = "100";
                            noteDefault = "100";
                        } else {
                            try {
                                StringTokenizer st = new StringTokenizer(spawn.getNote());
                                init = st.nextToken();
                                noteDefault = st.nextToken();
                            } catch (Exception var15) {
                                gm.sendMessage("Error, wrong length / width format for this fence. Values setted back to their default.");
                                init = "100";
                                noteDefault = "100";
                            }
                        }

                        html = html.replaceAll("%width%", init);
                        html = html.replaceAll("%length%", noteDefault);
                        break;
                    case Npc:
                        html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editspawn_npc.htm");
                        int id = spawn.getNpcId();
                        html = html.replaceAll("%id%", String.valueOf(id));
                        break;
                    case Monster:
                    case MapGuard:
                        html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editspawn_monster.htm");
                        break;
                    default:
                        html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editspawn.htm");
                }

                noteDefault = "null";
                if (spawn.getSpawnType() == SpawnType.Flag) {
                    noteDefault = "" + gm.getHeading();
                } else if (spawn.getSpawnType() == SpawnType.Fence) {
                    noteDefault = "100 100";
                } else if (spawn.getSpawnType() == SpawnType.Door) {
                    noteDefault = "Default Default";
                }

                html = html.replaceAll("%note_default%", noteDefault);
                html = html.replaceAll("%mapId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap));
                html = html.replaceAll("%mapName%", map.getMapName());
                html = html.replaceAll("%id%", String.valueOf(spawn.getSpawnId()));
                html = html.replaceAll("%team%", String.valueOf(spawn.getSpawnTeam()));
                html = html.replaceAll("%x%", String.valueOf(spawn.getLoc().getX()));
                html = html.replaceAll("%y%", String.valueOf(spawn.getSpawnType() != SpawnType.Door ? spawn.getLoc().getY() : "N/A"));
                html = html.replaceAll("%z%", String.valueOf(spawn.getSpawnType() != SpawnType.Door ? spawn.getLoc().getZ() : "N/A"));
                html = html.replaceAll("%note%", spawn.getNote() == null ? " " : spawn.getNote());
                html = html.replaceAll("%type%", spawn.getSpawnType().toString());
                StringBuilder tb = new StringBuilder();
                tb.append(spawn.getSpawnType().toString() + ";");
                SpawnType[] var21 = SpawnType.values();
                int var24 = var21.length;

                for (var10 = 0; var10 < var24; ++var10) {
                    SpawnType type = var21[var10];
                    if (type != spawn.getSpawnType() && type.isForEvents(map.getEvents())) {
                        tb.append(type.toString() + ";");
                    }
                }

                String s = tb.toString();
                html = html.replaceAll("%types%", s.substring(0, s.length() - 1));
                html = html.replaceAll("%save_close%", spawn.isSaved() ? "Close" : "* Save *");
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    private void removeSpawn(PlayerEventInfo gm, int spawnId) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            try {
                this.showSpawn(gm, spawnId, false, false, true);
            } catch (Exception var5) {
            }

            if (map.removeSpawn(spawnId, true)) {
                gm.sendMessage("Spawn with ID " + spawnId + " was successfully removed.");
            } else {
                gm.sendMessage("The spawn with ID " + spawnId + " doesn't exist.");
            }

        }
    }

    protected NpcData showSpawn(PlayerEventInfo gm, int spawnId, boolean onlyShow, boolean onlyDummy, boolean onlyDespawn) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
            return null;
        } else {
            EventSpawn spawn = map.getSpawn(spawnId);
            NpcData npc;
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId()) != null && ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).get(spawn.getSpawnId()) != null) {
                if (onlyShow) {
                    return null;
                }

                ObjectData o = (ObjectData) ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).get(spawn.getSpawnId());
                if (o.isFence()) {
                    FenceData fence = o.getFence();
                    fence.deleteMe();
                } else if (o.isNpc()) {
                    npc = o.getNpc();
                    npc.deleteMe();
                }

                ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).remove(spawn.getSpawnId());
            } else {
                if (onlyDespawn) {
                    return null;
                }

                if (spawn != null) {
                    if (spawn.getSpawnType() != SpawnType.Door && spawn.getSpawnType() != SpawnType.Fence) {
                        NpcTemplateData template = new NpcTemplateData(31691);

                        try {
                            npc = template.doSpawn(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 1, gm.getHeading(), 0, gm.getInstanceId());
                            npc.setTitle("ID: " + spawn.getSpawnId() + "/Team: " + spawn.getSpawnTeam());
                            npc.setName(spawn.getSpawnType().toString() + " spawn");
                            npc.broadcastNpcInfo();
                            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId()) == null) {
                                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.put(map.getGlobalId(), new LinkedHashMap());
                            }

                            ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).put(spawn.getSpawnId(), npc.getObjectData());
                            npc.broadcastSkillUse(npc, npc, 5965, 1);
                            return npc;
                        } catch (Exception var10) {
                            var10.printStackTrace();
                            gm.sendMessage("Done.");
                        }
                    } else if (spawn.getSpawnType() == SpawnType.Fence) {
                        if (onlyDummy) {
                            return null;
                        }

                        FenceData fence = CallBack.getInstance().getOut().createFence(2, spawn.getFenceWidth(), spawn.getFenceLength(), spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 0);
                        List<FenceData> f = new LinkedList();
                        f.add(fence);
                        CallBack.getInstance().getOut().spawnFences(f, gm.getInstanceId());
                        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId()) == null) {
                            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.put(map.getGlobalId(), new LinkedHashMap());
                        }

                        ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).put(spawn.getSpawnId(), fence);
                    } else {
                        gm.sendMessage("Spawn can't be spawned or this ID " + spawnId + " doesn't exist.");
                    }
                }
            }

            return null;
        }
    }

    private void teleportToSpawn(PlayerEventInfo gm, int spawnId) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventSpawn spawn = map.getSpawn(spawnId);
            if (spawn != null) {
                gm.teleToLocation(spawn.getLoc(), false);
                gm.sendMessage("Done.");
            } else {
                gm.sendMessage("The spawn with ID " + spawnId + " doesn't exist.");
            }

        }
    }

    private boolean removeMap(PlayerEventInfo gm, int mapId) {
        boolean mainEventMap = false;
        EventMap map = EventMapSystem.getInstance().getMapById(mapId);
        Iterator var5 = map.getEvents().iterator();

        while (var5.hasNext()) {
            EventType type = (EventType) var5.next();
            if (type.isRegularEvent()) {
                mainEventMap = true;
            }
        }

        if (EventMapSystem.getInstance().removeMap(mapId)) {
            gm.sendMessage("Done.");
        } else {
            gm.sendMessage("Map with id " + mapId + " doesn't exist.");
        }

        return mainEventMap;
    }

    private void sortSpawns(PlayerEventInfo gm, String sortType) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist.");
        } else {
            if (sortType.equals("id")) {
                if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).asc) {
                    Collections.sort(map.getSpawns(), EventMap.compareByIdDesc);
                } else {
                    Collections.sort(map.getSpawns(), EventMap.compareByIdAsc);
                }

                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).asc = !((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).asc;
                gm.sendMessage("Done, spawns sorted " + (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).asc ? "ascending" : "descending") + " by their ID.");
            } else if (sortType.equals("type")) {
                Collections.sort(map.getSpawns(), EventMap.compareByType);
                gm.sendMessage("Done, spawns sorted by their type priority.");
            }

        }
    }

    private void showFilterMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_filter_menu.htm");
        html = html.replaceAll("%mapId%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap));
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void filterSpawns(PlayerEventInfo gm, String s) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist.");
        } else {
            SpawnType type = null;
            if (!s.equals("All")) {
                SpawnType[] var5 = SpawnType.values();
                int var6 = var5.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    SpawnType t = var5[var7];
                    if (t.toString().equalsIgnoreCase(s)) {
                        type = t;
                        break;
                    }
                }
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter = type;
            gm.sendMessage("Done, spawns filtered by " + s + ".");
        }
    }

    private void showConfigsMenu(PlayerEventInfo gm, String type, int page) {
        EventType eventType = EventType.getType(type);
        if (eventType != null && eventType != EventType.Unassigned) {
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent = eventType;
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_config_menu.htm");
            StringBuilder tb = new StringBuilder();
            Object event;
            if (!eventType.isRegularEvent()) {
                event = EventManager.getInstance().getMiniEvent(eventType, 1);
            } else {
                event = EventManager.getInstance().getMainEvent(eventType);
            }

            String temp = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingConfigCategory;
            String category = null;
            Iterator var10 = ((Configurable) event).getCategories().iterator();

            while (var10.hasNext()) {
                String c = (String) var10.next();
                if (c.equals(temp)) {
                    category = c;
                    break;
                }
            }

            if (category == null) {
                category = "General";
            }

            List<ConfigModel> configs = new LinkedList();
            Iterator var12 = ((Configurable) event).getConfigs().entrySet().iterator();

            while (var12.hasNext()) {
                Entry<String, ConfigModel> e = (Entry) var12.next();
                if (((ConfigModel) e.getValue()).getCategory().equals(category)) {
                    configs.add(e.getValue());
                }
            }

            int pageLimit = SunriseLoader.isLimitedHtml() ? 10 : 25;
            int counter = 0;
            int maxPages = (int) Math.ceil((double) configs.size() / (double) pageLimit);
            if (page > maxPages) {
                page = maxPages;
            }

            int showFrom = (page - 1) * pageLimit;
            int showTo = page * pageLimit - 1;
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage > maxPages) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelPage = 1;
            }

            Iterator var17 = configs.iterator();

            while (true) {
                while (var17.hasNext()) {
                    ConfigModel config = (ConfigModel) var17.next();
                    if (counter >= showFrom && counter <= showTo) {
                        ++counter;
                        boolean expanded;
                        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory.equals("Event") && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown.equals(config.getKey())) {
                            expanded = true;
                        } else {
                            expanded = false;
                        }

                        if (expanded) {
                            tb.append("<table width=280 bgcolor=599944>");
                        } else {
                            tb.append("<table width=280 bgcolor=3f3f3f>");
                        }

                        tb.append("<tr>");
                        tb.append("<td width=175 align=left><font color=ac9887> " + config.getKey() + "</font></td>");
                        String value = config.getValue();
                        if (value.length() > 6) {
                            value = "...";
                        }

                        boolean brackets = true;
                        if (value.length() >= 6) {
                            brackets = false;
                        }

                        tb.append("<td width=45 align=left><font color=9f9f9f>" + (brackets ? "(" : "") + "" + value + "" + (brackets ? ")" : "") + "</font></td>");
                        tb.append("<td width=50 align=right><button value=\"Expand\" width=55 action=\"bypass admin_event_manage expand_configmodel Event " + config.getKey() + "\" height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        if (expanded) {
                            tb.append("<table width=278 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append("<td width=240><font color=9f9f9f>" + config.getDesc() + "</font></td>");
                            tb.append("</tr>");
                            tb.append("</table>");
                            if (config.getInput() == InputType.MultiEdit || config.getInput() == InputType.MultiAdd || config.getValue().length() > 5) {
                                tb.append("<table width=278 bgcolor=2f2f2f>");
                                tb.append("<tr>");
                                tb.append(config.getValueShownInHtml());
                                tb.append("</tr>");
                                tb.append("</table>");
                            }

                            tb.append("<table width=280 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append("<td>" + config.getInputHtml(220) + "</td>");
                            tb.append("<td align=left><button value=\"" + config.getAddButtonName() + "\" width=40 action=\"bypass admin_event_manage " + config.getAddButtonAction() + "_config " + config.getKey() + " \\$" + config.getKey() + "\" height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                            tb.append("</tr>");
                            tb.append("</table>");
                            tb.append("<table width=280 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append("<td><font color=6f6f6f>" + config.getConfigHtmlNote() + "</font></td>");
                            tb.append("<td align=right><button value=\"" + config.getUtilButtonName() + "\" action=\"bypass admin_event_manage set_config " + config.getKey() + " " + config.getDefaultVal() + "\" width=" + config.getUtilButtonWidth() + " height=17 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                            tb.append("</tr>");
                            tb.append("</table>");
                        }

                        tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=4>");
                    } else {
                        ++counter;
                    }
                }

                html = html.replaceAll("%currentPage%", String.valueOf(page));
                html = html.replaceAll("%event%", eventType.getHtmlTitle());
                html = html.replaceAll("%type%", eventType.getAltTitle());
                html = html.replaceAll("%eventId%", String.valueOf(eventType.getId()));
                html = html.replaceAll("%configs%", tb.toString());
                if (maxPages > 1) {
                    tb = new StringBuilder();
                    tb.append("<img src=\"L2UI.SquareBlank\" width=280 height=5>");
                    tb.append("<table bgcolor=292929 width=290>");

                    for (int i = 0; i < maxPages; ++i) {
                        if (i % 4 == 0) {
                            tb.append("<tr>");
                        }

                        tb.append("<td align=center width=72><font color=" + (page == i + 1 ? "80B382" : "9f9f9f") + "><a action=\"bypass admin_event_manage event_configs_menu_page " + (i + 1) + "\">Page " + (i + 1) + "</a></font></td>");
                        if (i > 1 && (i + 1) % 4 == 0) {
                            tb.append("</tr>");
                        }
                    }

                    tb.append("</table>");
                    tb.append("<img src=\"L2UI.SquareBlank\" width=280 height=5>");
                    html = html.replaceAll("%pages%", tb.toString());
                } else {
                    html = html.replaceAll("%pages%", "<br>");
                }

                if (!eventType.isRegularEvent()) {
                    html = html.replaceAll("%editEvent%", "mini_edit_event");
                } else {
                    html = html.replaceAll("%editEvent%", "edit_event");
                }

                html = html.replaceAll("%enableDisable%", EventConfig.getInstance().isEventAllowed(eventType) ? "<font color=74BE85><a action=\"bypass admin_event_manage set_available\">Enabled</a></font>" : "<font color=B46F6B><a action=\"bypass admin_event_manage set_available\">Disabled</a></font>");
                tb = new StringBuilder();
                tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection + ";");
                String[] var27 = eventType.isRegularEvent() ? this.mainEventEditingPages : this.miniEventEditingPages;
                int var28 = var27.length;

                int i;
                for (i = 0; i < var28; ++i) {
                    String s = var27[i];
                    if (!s.equals(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEventEditingSection)) {
                        tb.append(s + ";");
                    }
                }

                String result = tb.toString();
                html = html.replaceAll("%event_pages%", result.substring(0, result.length() - 1));
                String header;
                if (((Configurable) event).getCategories().isEmpty()) {
                    header = "<br1><font color=ac9887>" + eventType.getHtmlTitle() + "</font> <font color=9f9f9f>configs page " + page + "</font><br1>";
                } else {
                    tb = new StringBuilder();
                    tb.append("<img src=\"L2UI.SquareBlank\" width=280 height=6>");
                    tb.append("<table width=290 bgcolor=363636>");
                    tb.append("<tr>");
                    if (category.equals("General")) {
                        tb.append("<td align=center><font color=D89C67><a action=\"bypass admin_event_manage show_configs_category General\">[General]</a></font></td>");
                    } else {
                        tb.append("<td align=center><font color=7f7f7f><a action=\"bypass admin_event_manage show_configs_category General\">[General]</a></font></td>");
                    }

                    i = 0;
                    Iterator var33 = ((Configurable) event).getCategories().iterator();

                    while (var33.hasNext()) {
                        String cat = (String) var33.next();
                        ++i;
                        if (category.equals(cat)) {
                            tb.append("<td align=center><font color=D89C67><a action=\"bypass admin_event_manage show_configs_category " + cat + "\">[" + cat + "]</a></font></td>");
                        } else {
                            tb.append("<td align=center><font color=7f7f7f><a action=\"bypass admin_event_manage show_configs_category " + cat + "\">[" + cat + "]</a></font></td>");
                        }

                        if (i == 3) {
                            tb.append("<tr>");
                            tb.append("</tr>");
                            i = 0;
                        }
                    }

                    tb.append("</tr>");
                    tb.append("</table>");
                    tb.append("<img src=\"L2UI.SquareBlank\" width=280 height=6>");
                    header = tb.toString();
                }

                html = html.replaceAll("%header%", header);
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
                return;
            }
        } else {
            gm.sendMessage("Event " + type + " doesn't exist.");
        }
    }

    private void showConfig(PlayerEventInfo gm, String key) {
        gm.sendMessage("weird call");
    }

    private void setConfig(PlayerEventInfo gm, String key, String value, boolean addToValue) {
        EventType eventType = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingEvent;
        EventConfig.getInstance().addConfig(eventType, key, value, addToValue);
        gm.sendMessage("Done. Key " + key + " has now value " + value + ".");
    }

    private void showConfigMenu(PlayerEventInfo gm, String type) {
        EventType event = EventType.getType(type);
        if (event == null) {
            gm.sendMessage("This event doesn't exist.");
        } else if (event.isRegularEvent()) {
            gm.sendMessage("Sorry, this function isn't implemented for Main events yet.");
        } else {
            EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
            if (map == null) {
                gm.sendMessage("This map doesn't exist.");
            } else {
                String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mapconfig_menu.htm");
                StringBuilder tb = new StringBuilder();
                boolean expanded = false;
                int size = 0;

                for (Iterator var9 = ((Map) map.getConfigModels().get(event)).entrySet().iterator(); var9.hasNext(); ++size) {
                    Entry<String, ConfigModel> e = (Entry) var9.next();
                    if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory.equals("MapConfig") && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown.equals(((ConfigModel) e.getValue()).getKey())) {
                        expanded = true;
                    } else {
                        expanded = false;
                    }

                    if (expanded) {
                        tb.append("<table width=280 bgcolor=599944>");
                    } else {
                        tb.append("<table width=280 bgcolor=3f3f3f>");
                    }

                    tb.append("<tr>");
                    tb.append("<td width=175 align=left><font color=ac9887> " + ((ConfigModel) e.getValue()).getKey() + "</font></td>");
                    String value = ((ConfigModel) e.getValue()).getValue();
                    if (value.length() > 6) {
                        value = "...";
                    }

                    boolean brackets = true;
                    if (value.length() >= 6) {
                        brackets = false;
                    }

                    tb.append("<td width=45 align=left><font color=9f9f9f>" + (brackets ? "(" : "") + "" + value + "" + (brackets ? ")" : "") + "</font></td>");
                    tb.append("<td width=50 align=right><button value=\"Expand\" width=55 action=\"bypass admin_event_manage expand_configmodel MapConfig " + (String) e.getKey() + "\" height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                    if (expanded) {
                        tb.append("<table width=278 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td width=240><font color=9f9f9f>" + ((ConfigModel) e.getValue()).getDesc() + "</font></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        if (((ConfigModel) e.getValue()).getInput() == InputType.MultiEdit || ((ConfigModel) e.getValue()).getInput() == InputType.MultiAdd || ((ConfigModel) e.getValue()).getValue().length() > 5) {
                            tb.append("<table width=278 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append(((ConfigModel) e.getValue()).getValueShownInHtml());
                            tb.append("</tr>");
                            tb.append("</table>");
                        }

                        tb.append("<table width=280 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td>" + ((ConfigModel) e.getValue()).getInputHtml(180) + "</td>");
                        tb.append("<td align=left><button value=\"" + ((ConfigModel) e.getValue()).getAddButtonName() + "\" width=40 action=\"bypass admin_event_manage " + ((ConfigModel) e.getValue()).getAddButtonAction() + "_map_config " + event.getAltTitle() + " " + (String) e.getKey() + " \\$" + (String) e.getKey() + "\" height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        tb.append("<table width=280 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td><font color=6f6f6f>" + ((ConfigModel) e.getValue()).getConfigHtmlNote() + "</font></td>");
                        tb.append("<td align=right><button value=\"" + ((ConfigModel) e.getValue()).getUtilButtonName() + "\" action=\"bypass admin_event_manage set_map_config " + event.getAltTitle() + " " + (String) e.getKey() + " " + ((ConfigModel) e.getValue()).getDefaultVal() + "\" width=" + ((ConfigModel) e.getValue()).getUtilButtonWidth() + " height=17 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                    }

                    tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=4>");
                }

                if (size == 0) {
                    tb.append("No configs available for this event.");
                }

                html = html.replaceAll("%configs%", tb.toString());
                html = html.replaceAll("%mapName%", map.getMapName());
                html = html.replaceAll("%event%", event.getAltTitle());
                html = html.replaceAll("%mapId%", String.valueOf(map.getGlobalId()));
                html = html.replaceAll("%page%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage));
                html = html.replaceAll("%title%", "Event Engine");
                gm.sendPacket(html);
                gm.sendStaticPacket();
            }
        }
    }

    private void setMapConfig(PlayerEventInfo gm, String type, String key, String value, boolean addToValue) {
        EventType event = EventType.getType(type);
        if (event == null) {
            gm.sendMessage("This event doesn't exist.");
        } else {
            EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
            if (map == null) {
                gm.sendMessage("This map doesn't exist.");
            } else {
                EventConfig.getInstance().setMapConfig(map, event, key, value, addToValue);
                map.setSaved(false);
            }
        }
    }

    private void showMapStatus(PlayerEventInfo gm, int mapId, String event) {
        EventMap map = EventMapSystem.getInstance().getMapById(mapId);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + mapId + ").");
        } else {
            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap = map.getGlobalId();
            String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_mapstatus.htm");
            html = html.replaceAll("%mapName%", map.getMapName());
            html = html.replaceAll("%mapId%", "" + map.getGlobalId());
            html = html.replaceAll("%previous_event%", event);
            String notWorkingEvents = null;
            String spawns = null;
            notWorkingEvents = map.getNotWorkingEvents();
            spawns = map.getMissingSpawns();
            html = html.replaceAll("%notWorkingEvents%", notWorkingEvents);
            html = html.replaceAll("%spawns%", spawns);
            if (spawns.length() > 1) {
                html = html.replaceAll("%status%", "<font color=B46F6B>This map can't be played unless you fix it.</font>");
            } else {
                html = html.replaceAll("%status%", "<font color=74BE85>This map is active and can be played.</font>");
            }

            html = html.replaceAll("%title%", "Event Engine");
            gm.sendPacket(html);
            gm.sendStaticPacket();
        }
    }

    private void showExpandSpawnEffect(PlayerEventInfo gm, int spawnId) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventSpawn spawn = map.getSpawn(spawnId);
            if (spawn != null) {
                gm.addRadarMarker(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
                NpcData data = this.showSpawn(gm, spawn.getSpawnId(), true, true, false);
                if (data != null) {
                    CallBack.getInstance().getOut().scheduleGeneral(() -> {
                        this.showSpawn(gm, spawn.getSpawnId(), false, true, false);
                    }, 500L);
                }
            }
        }
    }

    private void showMapEditationAddEvent(PlayerEventInfo gm, int mapId) {
        EventMap map = null;
        if (mapId > 0) {
            map = EventMapSystem.getInstance().getMapById(mapId);
            if (map == null) {
                gm.sendMessage("This map doesn't exist. (" + mapId + ").");
                return;
            }
        }

        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editmap_add_event.htm");
        StringBuilder tb = new StringBuilder();
        tb.append("<table width=270>");
        int counter = 0;
        EventType[] var7 = EventType.values();
        int var8 = var7.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            EventType available = var7[var9];
            if (available.allowEdits() && available != EventType.Unassigned && map != null && !map.getEvents().contains(available) && EventManager.getInstance().getEvent(available) != null) {
                ++counter;
                if (counter == 1) {
                    tb.append("<tr>");
                }

                tb.append("<td><button value=\"" + available.getAltTitle() + "\" action=\"bypass admin_event_manage add_event_to_map " + available.getAltTitle() + "\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                if (counter == 3) {
                    tb.append("</tr>");
                    counter = 0;
                }
            }
        }

        if (counter != 0) {
            tb.append("</tr>");
        }

        tb.append("</table>");
        String events = tb.toString();
        html = html.replaceAll("%events%", events);
        if (map != null) {
            html = html.replaceAll("%name%", map.getMapName());
            html = html.replaceAll("%save_close%", map.isSaved() ? "Close" : "* Save *");
            html = html.replaceAll("%mapId%", String.valueOf(map.getGlobalId()));
            html = html.replaceAll("%page%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage));
        }

        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void showMapEditationInfo(PlayerEventInfo gm, EventMap map) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editmap_info.htm");
        StringBuilder tb = new StringBuilder();
        EventType event = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapEvent;
        if (event != null) {
            tb.append(event.getAltTitle() + ";");
        }

        Iterator var6 = map.getEvents().iterator();

        while (true) {
            EventType t;
            do {
                if (!var6.hasNext()) {
                    String events = tb.toString();
                    if (events.length() > 0) {
                        events = events.substring(0, events.length() - 1);
                    }

                    html = html.replaceAll("%events%", events);
                    html = html.replaceAll("%name%", map.getMapName());
                    html = html.replaceAll("%desc%", map.getMapDesc() != null && map.getMapDesc().length() != 0 ? map.getMapDesc() : "This map has no description set.");
                    html = html.replaceAll("%save_close%", map.isSaved() ? "Close" : "* Save *");
                    html = html.replaceAll("%mapId%", String.valueOf(map.getGlobalId()));
                    html = html.replaceAll("%page%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage));
                    html = html.replaceAll("%title%", "Event Engine");
                    gm.sendPacket(html);
                    gm.sendStaticPacket();
                    return;
                }

                t = (EventType) var6.next();
            } while ((event == null || t == event) && event != null);

            tb.append(t.getAltTitle() + ";");
        }
    }

    private void showMapEditationEvents(PlayerEventInfo gm, int mapId) {
        EventMap map = null;
        if (mapId > 0) {
            map = EventMapSystem.getInstance().getMapById(mapId);
            if (map == null) {
                gm.sendMessage("This map doesn't exist. (" + mapId + ").");
                return;
            }
        }

        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editmap_events.htm");
        StringBuilder tb = new StringBuilder();
        EventType event = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapEvent;
        boolean selected = false;
        if (map != null) {
            if (event != null && map.getEvents().contains(event)) {
                tb.append(event.getAltTitle() + ";");
                selected = true;
            }

            Iterator var8 = map.getEvents().iterator();

            label122:
            while (true) {
                EventType t;
                do {
                    if (!var8.hasNext()) {
                        String events = tb.toString();
                        if (events.length() > 0) {
                            events = events.substring(0, events.length() - 1);
                        }

                        html = html.replaceAll("%events%", events);
                        html = html.replaceAll("%name%", map.getMapName());
                        html = html.replaceAll("%save_close%", map.isSaved() ? "Close" : "* Save *");
                        break label122;
                    }

                    t = (EventType) var8.next();
                } while ((event == null || t == event) && event != null);

                tb.append(t.getAltTitle() + ";");
            }
        }

        tb = new StringBuilder();
        if (selected) {
            boolean expanded = false;
            int size = 0;
            if (map != null) {
                for (Iterator var10 = ((Map) map.getConfigModels().get(event)).entrySet().iterator(); var10.hasNext(); ++size) {
                    Entry<String, ConfigModel> e = (Entry) var10.next();
                    if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShownCategory.equals("MapConfig") && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown != null && ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentConfigModelShown.equals(((ConfigModel) e.getValue()).getKey())) {
                        expanded = true;
                    } else {
                        expanded = false;
                    }

                    if (expanded) {
                        tb.append("<table width=280 bgcolor=599944>");
                    } else {
                        tb.append("<table width=280 bgcolor=3f3f3f>");
                    }

                    tb.append("<tr>");
                    tb.append("<td width=175 align=left><font color=ac9887> " + ((ConfigModel) e.getValue()).getKey() + "</font></td>");
                    String value = ((ConfigModel) e.getValue()).getValue();
                    if (value.length() > 6) {
                        value = "...";
                    }

                    boolean brackets = true;
                    if (value.length() >= 6) {
                        brackets = false;
                    }

                    tb.append("<td width=45 align=left><font color=9f9f9f>" + (brackets ? "(" : "") + "" + value + "" + (brackets ? ")" : "") + "</font></td>");
                    tb.append("<td width=50 align=right><button value=\"Expand\" width=55 action=\"bypass admin_event_manage expand_configmodel MapConfig " + (String) e.getKey() + "\" height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                    tb.append("</tr>");
                    tb.append("</table>");
                    if (expanded) {
                        tb.append("<table width=278 bgcolor=2f2f2f>");
                        tb.append("<tr>");
                        tb.append("<td width=240><font color=9f9f9f>" + ((ConfigModel) e.getValue()).getDesc() + "</font></td>");
                        tb.append("</tr>");
                        tb.append("</table>");
                        if (((ConfigModel) e.getValue()).getInput() == InputType.MultiEdit || ((ConfigModel) e.getValue()).getInput() == InputType.MultiAdd || ((ConfigModel) e.getValue()).getValue().length() > 5) {
                            tb.append("<table width=278 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append(((ConfigModel) e.getValue()).getValueShownInHtml());
                            tb.append("</tr>");
                            tb.append("</table>");
                        }

                        if (event != null) {
                            tb.append("<table width=280 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append("<td>" + ((ConfigModel) e.getValue()).getInputHtml(180) + "</td>");
                            tb.append("<td align=left><button value=\"" + ((ConfigModel) e.getValue()).getAddButtonName() + "\" width=40 action=\"bypass admin_event_manage " + ((ConfigModel) e.getValue()).getAddButtonAction() + "_map_config " + event.getAltTitle() + " " + (String) e.getKey() + " \\$" + (String) e.getKey() + "\" height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                            tb.append("</tr>");
                            tb.append("</table>");
                            tb.append("<table width=280 bgcolor=2f2f2f>");
                            tb.append("<tr>");
                            tb.append("<td><font color=6f6f6f>" + ((ConfigModel) e.getValue()).getConfigHtmlNote() + "</font></td>");
                            tb.append("<td align=right><button value=\"" + ((ConfigModel) e.getValue()).getUtilButtonName() + "\" action=\"bypass admin_event_manage set_map_config " + event.getAltTitle() + " " + (String) e.getKey() + " " + ((ConfigModel) e.getValue()).getDefaultVal() + "\" width=" + ((ConfigModel) e.getValue()).getUtilButtonWidth() + " height=17 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                            tb.append("</tr>");
                            tb.append("</table>");
                        }
                    }

                    tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=4>");
                }
            }

            if (size == 0) {
                tb.append("No configs available for this event.");
            }
        } else {
            tb.append("<font color=B46F6B>Select an event.</font>");
        }

        html = html.replaceAll("%configs%", tb.toString());
        if (map != null) {
            html = html.replaceAll("%mapId%", String.valueOf(map.getGlobalId()));
        }

        html = html.replaceAll("%page%", String.valueOf(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage));
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    private void showMapEditation(PlayerEventInfo gm, int mapId, int page, EventType predefinedEvent) {
        EventMap map = null;
        if (mapId > 0) {
            map = EventMapSystem.getInstance().getMapById(mapId);
            if (map == null) {
                gm.sendMessage("This map doesn't exist. (" + mapId + ").");
                return;
            }
        }

        if (map == null) {
            List<EventType> defaultEvent = new LinkedList();
            defaultEvent.add(EventType.Unassigned);
            map = new EventMap(EventMapSystem.getInstance().getNewMapId(), "New Map", "", defaultEvent, (List) null, "");
            map.setSaved(false);
            EventMapSystem.getInstance().addMap(map);
            if (predefinedEvent != null) {
                map.addEvent(predefinedEvent);
                map.removeEvent(EventType.Unassigned);
            }

            ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage = "Info";
        }

        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingPage = page;
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap = map.getGlobalId();
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage != null) {
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage.equals("Events")) {
                this.showMapEditationEvents(gm, mapId);
                return;
            }

            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapPage.equals("Info")) {
                this.showMapEditationInfo(gm, map);
                return;
            }
        }

        StringBuilder tb = new StringBuilder();
        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter == null) {
            tb.append("All;");
        } else {
            tb.append(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter.toString() + ";");
        }

        SpawnType[] var7 = SpawnType.values();
        int var8 = var7.length;

        for (int var9 = 0; var9 < var8; ++var9) {
            SpawnType t = var7[var9];
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter == null || ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter != t) {
                Iterator var11 = map.getSpawns().iterator();

                while (var11.hasNext()) {
                    EventSpawn spawn = (EventSpawn) var11.next();
                    if (spawn.getSpawnType() == t) {
                        tb.append(t.toString() + ";");
                        break;
                    }
                }
            }
        }

        if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter != null) {
            tb.append("All;");
        }

        String filters = tb.toString();
        filters = filters.substring(0, filters.length() - 1);
        boolean filter = ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter != null;
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_editmap_spawns.htm");
        tb = new StringBuilder();
        tb.append("<table width=275 bgcolor=\"666666\">>");
        tb.append("<tr><td width=20><font color=D5D5D5><a action=\"bypass admin_event_manage sort_map_spawns id\">ID</a></font></td><td width=40 align=center><font color=D5D5D5><a action=\"bypass admin_event_manage sort_map_spawns type\">Type</a></font></td><td width=70 align=center><font color=D5D5D5><a action=\"bypass admin_event_manage sort_map_spawns team\">Team</a></font></td><td width=80 align=right><combobox width=80 height=15 var=filt list=\"" + filters + "\"></td>" + "<td width=50 align=right><button value=\"Filter\" action=\"bypass admin_event_manage filter_event_spawns \\$filt\" width=50 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "</tr>");
        tb.append("</table>");
        int size;
        if (filter) {
            size = map.getSpawns(-1, ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter).size();
        } else {
            size = map.getSpawns().size();
        }

        int maxSpawnsPerPage = 15;
        int maxPages = size / maxSpawnsPerPage;
        if (page > maxPages) {
            page = maxPages;
        }

        int firstSpawnShowed = page * maxSpawnsPerPage;
        int lastSpawnShowed = size;
        if (size - firstSpawnShowed > maxSpawnsPerPage) {
            lastSpawnShowed = firstSpawnShowed + maxSpawnsPerPage;
        }

        int count = 0;
        String bgcolor = "444444";
        Iterator var17 = map.getSpawns().iterator();

        while (true) {
            EventSpawn spawn;
            do {
                do {
                    do {
                        if (!var17.hasNext()) {
                            if (size > maxSpawnsPerPage) {
                                tb.append("<table width=270>");
                                int i = 4;

                                for (int x = 0; x < maxPages + 1; ++x) {
                                    if (i == 4) {
                                        tb.append("<tr>");
                                    }

                                    tb.append("<td><center><button value=\"Page " + (x + 1) + "\" action=\"bypass admin_event_manage edit_event_map " + mapId + " " + x + "\" width=60 height=17 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></td>");
                                    if (i == 0) {
                                        tb.append("</tr>");
                                        i = 4;
                                    } else {
                                        --i;
                                    }
                                }

                                tb.append("</table>");
                            }

                            html = html.replaceAll("%name%", map.getMapName());
                            html = html.replaceAll("%spawns%", tb.toString());
                            tb = new StringBuilder();
                            var17 = map.getEvents().iterator();

                            while (var17.hasNext()) {
                                EventType t = (EventType) var17.next();
                                tb.append(t.getAltTitle() + ";");
                            }

                            String events = tb.toString();
                            events = events.substring(0, events.length() - 1);
                            html = html.replaceAll("%events%", events);
                            html = html.replaceAll("%save_close%", map.isSaved() ? "Close" : "* Save *");
                            html = html.replaceAll("%title%", "Event Engine");
                            gm.sendPacket(html);
                            gm.sendStaticPacket();
                            return;
                        }

                        spawn = (EventSpawn) var17.next();
                    } while (filter && spawn.getSpawnType() != ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawnFilter);

                    ++count;
                } while (count <= firstSpawnShowed);
            } while (count > lastSpawnShowed);

            String spawnUnspawn;
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId()) != null && ((Map) ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).spawned.get(map.getGlobalId())).get(spawn.getSpawnId()) != null) {
                spawnUnspawn = "Hide";
            } else {
                spawnUnspawn = "Show";
            }

            int team = spawn.getSpawnTeam();
            if (team == 0) {
                bgcolor = "858585";
            } else if (team == -1) {
                bgcolor = "949494";
            } else {
                bgcolor = EventManager.getInstance().getTeamColorForHtml(team);
            }

            Color color = new Color(Integer.decode("0x" + bgcolor));
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn != spawn.getSpawnId()) {
                color = color.darker().darker();
            }

            bgcolor = Integer.toHexString(-16777216 | color.getRGB()).substring(2);
            tb.append("<table width=275 bgcolor=" + bgcolor + ">");
            tb.append("<tr>");
            tb.append("<td width=15><a action=\"bypass admin_event_manage expand_spawn_info " + spawn.getSpawnId() + "\"><font color=" + spawn.getSpawnType().getHtmlColor() + ">" + spawn.getSpawnId() + "</a></font></td>");
            tb.append("<td width=50><font color=" + spawn.getSpawnType().getHtmlColor() + ">" + spawn.getSpawnType().toString() + "</font></td>");
            tb.append("<td width=15 align=left><font color=" + EventManager.getInstance().getTeamColorForHtml(team) + ">T" + spawn.getSpawnTeam() + "</font></td>");
            tb.append("<td width=60 align=right><button value=\"" + spawnUnspawn + "\" action=\"bypass admin_event_manage show_spawn " + spawn.getSpawnId() + "\" width=50 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
            tb.append("<td align=right><button value=\"Expand\" action=\"bypass admin_event_manage expand_spawn_info " + spawn.getSpawnId() + "\" width=60 height=18 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
            tb.append("</tr>");
            tb.append("</table>");
            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingSpawn == spawn.getSpawnId()) {
                if (team == 0) {
                    bgcolor = "949494";
                } else if (team == -1) {
                    bgcolor = "949494";
                } else {
                    bgcolor = EventManager.getInstance().getTeamColorForHtml(team);
                }

                color = new Color(Integer.decode("0x" + bgcolor));
                if (team != 0 && team != -1) {
                    bgcolor = Integer.toHexString(-16777216 | color.darker().darker().getRGB()).substring(2);
                } else {
                    bgcolor = Integer.toHexString(-16777216 | color.darker().getRGB()).substring(2);
                }

                tb.append("<table width=270 bgcolor=" + bgcolor + ">");
                tb.append("<tr>");
                EventType type = (EventType) map.getEvents().get(0);
                String desc;
                if (type == EventType.Unassigned) {
                    desc = spawn.getSpawnType().getDefaultDesc();
                } else {
                    Configurable event = EventManager.getInstance().getEvent(type);
                    desc = (String) event.getAvailableSpawnTypes().get(spawn.getSpawnType());
                }

                if (desc == null) {
                    desc = spawn.getSpawnType().getDefaultDesc();
                }

                desc = desc.replaceAll("%TEAM%", "" + spawn.getSpawnTeam());
                tb.append("<td width=150><font color=9f9f9f>" + desc + "</font></td>");
                tb.append("</tr>");
                tb.append("</table>");
                tb.append("<table width=270 bgcolor=" + bgcolor + ">");
                tb.append("<tr>");
                tb.append("<td><button value=\"Delete\" action=\"bypass admin_event_manage remove_spawn " + spawn.getSpawnId() + "\" width=83 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("<td><button value=\"Teleport to\" action=\"bypass admin_event_manage teleport_spawn " + spawn.getSpawnId() + "\" width=83 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("<td><button value=\"Edit spawn\" action=\"bypass admin_event_manage edit_spawn_menu " + spawn.getSpawnId() + "\" width=83 height=21 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
                tb.append("</tr>");
                tb.append("</table>");
            }

            tb.append("<img src=\"L2UI.SquareBlank\" width=1 height=4>");
        }
    }

    private void setMapName(PlayerEventInfo gm, String n) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else if (n.length() > 16) {
            gm.sendMessage("The name can't be logner than 20 chars.");
        } else {
            map.setMapName(n);
        }
    }

    private void setMapDesc(PlayerEventInfo gm, String n) {
    }

    private void addAvailableEvent(PlayerEventInfo gm, String event) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            EventType type = EventType.getType(event);
            if (type != null) {
                if (!map.getEvents().contains(type)) {
                    map.addEvent(type);
                    map.removeEvent(EventType.Unassigned);
                    gm.sendMessage("Done.");
                } else {
                    gm.sendMessage("This event has already been added.");
                }
            } else {
                gm.sendMessage("This event doesn't exist.");
            }

        }
    }

    private void removeAvailableEvent(PlayerEventInfo gm, String event) {
        EventMap map = EventMapSystem.getInstance().getMapById(((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap);
        if (map == null) {
            gm.sendMessage("This map doesn't exist. (" + ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMap + ").");
        } else {
            Iterator var4 = map.getEvents().iterator();

            EventType t;
            do {
                if (!var4.hasNext()) {
                    gm.sendMessage("This event doesn't exist.");
                    gm.sendMessage("You can choose from: " + map.getEvents().toString());
                    return;
                }

                t = (EventType) var4.next();
            } while (!t.toString().equalsIgnoreCase(event) && !t.getAltTitle().equalsIgnoreCase(event));

            if (((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapEvent == t) {
                ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).activeEditingMapEvent = null;
            }

            map.removeEvent(t);
            gm.sendMessage("Done.");
        }
    }

    private void setGoBackPage(PlayerEventInfo gm, String page) {
        ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentGoBackAblePage = page;
    }

    private String getGoBackPage(PlayerEventInfo gm) {
        return ((EventManagement.EditingInfo) this._editingInfos.get(gm.getPlayersId())).currentGoBackAblePage;
    }

    private void eventGmMessage(PlayerEventInfo gm, String message, boolean event) {
        CallBack.getInstance().getOut().announceToAllScreenMessage(message, "Event");
    }

    private void eventGmMessageMenu(PlayerEventInfo gm) {
        String html = HtmCache.getInstance().getHtm( "" ,"data/html/admin/events/eventmanage_messagemenu.htm");
        html = html.replaceAll("%title%", "Event Engine");
        gm.sendPacket(html);
        gm.sendStaticPacket();
    }

    public static final EventManagement getInstance() {
        return EventManagement.SingletonHolder._instance;
    }

    private class EditingInfo {
        protected final Map<Integer, Map<Integer, ObjectData>> spawned = new ConcurrentHashMap();
        protected int activeEditingMap = 0;
        protected String activeEditingMapPage = "Info";
        protected EventType activeEditingMapEvent = null;
        protected int activeEditingPage = 0;
        protected String activeEventEditingSection = null;
        protected int activeEditingSpawn;
        protected RewardPosition activeEditingRewardPos;
        protected String activeEditingRewardParam;
        protected EventType activeEditingEvent;
        protected String activeEditingConfigCategory;
        protected int activeEventModeId = 1;
        protected int activeEventModeTimeId = 0;
        protected FeatureType activeEventModeFeatureShowed = null;
        protected FeatureCategory activeEventModeFeatureCategory = null;
        protected int activeEditingMatch;
        protected int activeEditingReward;
        protected int activeGlobalConfigPage = 0;
        protected String activeGlobalConfigType = null;
        protected String activeGlobalConfigKeyShown = "";
        protected int activeEdittingMainInstanceType = 0;
        protected String currentGoBackAblePage = null;
        protected int currentConfigModelPage = 1;
        protected String currentConfigModelShownCategory = "";
        protected String currentConfigModelShown = "";
        protected SpawnType spawnFilter = null;
        protected boolean asc = false;
        protected final Map<Integer, List<PlayerEventInfo>> manualMatchPlayers = new ConcurrentHashMap();

        protected EditingInfo() {
        }
    }

    private static class SingletonHolder {
        protected static final EventManagement _instance = new EventManagement();

        private SingletonHolder() {
        }
    }
}
