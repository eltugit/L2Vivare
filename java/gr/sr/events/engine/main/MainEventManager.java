package gr.sr.events.engine.main;


import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.*;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.NpcData;
import gr.sr.interf.delegate.NpcTemplateData;
import gr.sr.l2j.CallBack;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ConfirmDlg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;


public class MainEventManager
{
    EventManager _manager;
    private final EventTaskScheduler _task;
    protected AbstractMainEvent current;
    private EventMap activeMap;
    protected final List<PlayerEventInfo> _players;
    protected State _state;
    protected int _counter;
    private long lastEvent;
    protected final RegistrationCountdown _regCountdown;
    protected ScheduledFuture<?> _regCountdownFuture;
    private ScheduledFuture<?> taskFuture;
    public Map<Integer, RegNpcLoc> regNpcLocs;
    private RegNpcLoc regNpc;
    private NpcData regNpcInstance;
    private int eventRunTime;
    protected boolean autoScheduler;
    private double pausedTimeLeft;
    protected final EventScheduler scheduler;
    protected final List<EventScheduleData> _eventScheduleData;
    private EventType _lastEvent;


    public State get_state() {
        return _state;
    }

    public MainEventManager() {
        this._players = new CopyOnWriteArrayList<PlayerEventInfo>();
        this.regNpcLocs = new ConcurrentHashMap<Integer, RegNpcLoc>();
        this.autoScheduler = false;
        this._eventScheduleData = new LinkedList<EventScheduleData>();
        this._lastEvent = null;
        this._manager = EventManager.getInstance();
        this._state = State.IDLE;
        this._task = new EventTaskScheduler();
        this._regCountdown = new RegistrationCountdown();
        this._counter = 0;
        this.activeMap = null;
        this.eventRunTime = 0;
        this._players.clear();
        this.initRegNpcLocs();
        (this.scheduler = new EventScheduler()).schedule(-1.0, true);
    }

    private void initRegNpcLocs() {
        this.regNpcLocs.clear();
        this.regNpcLocs.put(1, new RegNpcLoc("Your cords", null));
        this.regNpcLocs.put(2, new RegNpcLoc("Hunters Village", new int[] { 116541, 76077, -2730, 0 }));
        this.regNpcLocs.put(3, new RegNpcLoc("Goddard Town", new int[] { 147726, -56323, -2781, 0 }));
        this.regNpcLocs.put(4, new RegNpcLoc("Ketra/Varka", new int[] { 125176, -69204, -3260, 0 }));
        this.regNpcLocs.put(5, new RegNpcLoc("Cemetery", new int[] { 182297, 19407, -3174, 0 }));
        this.regNpcLocs.put(6, new RegNpcLoc("Aden Town", new int[] { 148083, 26983, -2205, 0 }));
    }

    public synchronized void startEvent(final PlayerEventInfo gm, final EventType type, final int regTime, final String mapName, final String npcLoc, int runTime) {
        if (SunriseLoader.detailedDebug) {
            this.print(((gm == null) ? "GM" : "Scheduler") + " starting an event");
        }
        final AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
        if (event == null) {
            if (gm != null) {
                gm.sendMessage("This event is not finished yet (most likely cause it is being reworked to be a mini event).");
            }
            SunriseLoader.debug("An unfinished event is chosen to be run. Skipping to the next one...", Level.WARNING);
            this.scheduler.run();
            return;
        }
        final EventMap map = EventMapSystem.getInstance().getMap(type, mapName);
        if (map == null) {
            if (gm != null) {
                gm.sendMessage("Map " + mapName + " doesn't exist or is not allowed for this event.");
            }
            else {
                SunriseLoader.debug("Map " + mapName + " doesn't exist for event " + type.getAltTitle(), Level.WARNING);
            }
            return;
        }
        RegNpcLoc npc = null;
        if (npcLoc != null) {
            for (final Map.Entry<Integer, RegNpcLoc> e : this.regNpcLocs.entrySet()) {
                if (e.getValue()._name.equalsIgnoreCase(npcLoc)) {
                    npc = e.getValue();
                    break;
                }
            }
        }
        if (npc == null && gm != null) {
            gm.sendMessage("Reg NPC location " + npcLoc + " is not registered in the engine.");
            return;
        }
        if (npc == null) {
            final String configsCords = EventConfig.getInstance().getGlobalConfigValue("spawnRegNpcCords");
            final int x = Integer.parseInt(configsCords.split(";")[0]);
            final int y = Integer.parseInt(configsCords.split(";")[1]);
            final int z = Integer.parseInt(configsCords.split(";")[2]);
            npc = new RegNpcLoc("From Configs", new int[] { x, y, z, 0 });
        }
        if (SunriseLoader.detailedDebug) {
            this.print("map " + map.getMapName() + ", event " + event.getEventName());
        }
        if (regTime <= 0 || regTime >= 1439) {
            if (gm != null) {
                gm.sendMessage("The minutes for registration must be within interval 1-1439 minutes.");
            }
            else {
                SunriseLoader.debug("Can't start main event (automatic scheduler) - regTime is too high or too low (" + regTime + ").", Level.SEVERE);
            }
            return;
        }
        final int eventsRunTime = event.getInt("runTime");
        if (gm == null && eventsRunTime > 0) {
            runTime = eventsRunTime;
        }
        if (runTime <= 0 || runTime >= 120) {
            if (gm != null) {
                gm.sendMessage("RunTime must be at least 1 minute and max. 120 minutes.");
            }
            else {
                SunriseLoader.debug("Can't start main event (automatic scheduler) - runTime is too high or too low (" + runTime + ").", Level.SEVERE);
            }
            return;
        }
        this.eventRunTime = runTime * 60;
        if (SunriseLoader.detailedDebug) {
            this.print("event runtime (in seconds) is " + eventsRunTime * 60 + "s, regtime is " + regTime * 60 + "s");
        }
        this.regNpc = npc;
        this._state = State.REGISTERING;
        (this.current = event).startRegistration();
        if (SunriseLoader.detailedDebug) {
            this.print("event registration started, state is now REGISTERING");
        }
        this.activeMap = map;
        this._counter = regTime * 60;
        this._regCountdownFuture = CallBack.getInstance().getOut().scheduleGeneral(this._regCountdown, 1L);
        if (SunriseLoader.detailedDebug) {
            this.print("scheduled registration countdown");
        }
        this.spawnRegNpc(gm);
        if (SunriseLoader.detailedDebug) {
            this.print("regNpc finished spawn method");
        }
        this.announce(LanguageEngine.getMsg("announce_eventStarted", type.getHtmlTitle()));
        final String announce = EventConfig.getInstance().getGlobalConfigValue("announceRegNpcPos");
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
//            if(player.getInstanceId() == GabConfig.PARTY_AREA_INSTANCE_ID || player.getInstanceId() == GabConfig.ER_EVENT_INSTANCE_ID || player.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID || player.getInstanceId() == GabConfig.EXTREME_EVENT_INSTANCE_ID){
//                continue;
//            }
//            if(player.getInstanceId()>0){
//                continue;
//            }
            ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
            dlg.addString("Do you want to join the current "+event.getEventName()+" event?");
            dlg.addTime(15 * 1000);
            dlg.addRequesterId(player.getObjectId());
            player.sendPacket(dlg);
            player.setAskedToJoinEvent(true);
        }
        if (announce.equals("-")) {
            return;
        }
        if (gm != null) {
            if (!npc._name.equals("Your cords") && !npc._name.equals("From Configs")) {
                this.announce(LanguageEngine.getMsg("announce_npcPos", npc._name));
                if (SunriseLoader.detailedDebug) {
                    this.print("announcing registration cords (1 - gm != null)");
                }
            }
            else if (SunriseLoader.detailedDebug) {
                this.print("not announcing registration cords (either Your Cords or From Configs chosen)");
            }
        }
        else {
            this.announce(LanguageEngine.getMsg("announce_npcPos", announce));
            if (SunriseLoader.detailedDebug) {
                this.print("announcing registration cords (2 - gm == null)");
            }
        }
        if (EventConfig.getInstance().getGlobalConfigBoolean("announce_moreInfoInCb")) {
            this.announce(LanguageEngine.getMsg("announce_moreInfoInCb"));
        }
        SunriseLoader.debug("Started registration for event " + this.current.getEventName());
        if (gm != null) {
            gm.sendMessage("The event has been started.");
        }
        if (SunriseLoader.detailedDebug) {
            this.print("finished startEvent() method");
        }
    }

    protected void spawnRegNpc(final PlayerEventInfo gm) {
        if (gm == null && !EventConfig.getInstance().getGlobalConfigBoolean("allowSpawnRegNpc")) {
            this.print("configs permitted spawning regNpc");
            return;
        }
        if (this.regNpc != null) {
            final int id = EventConfig.getInstance().getGlobalConfigInt("mainEventManagerId");
            final NpcTemplateData template = new NpcTemplateData(id);
            this.print("spawning npc id " + id + ", template exists = " + template.exists());
            try {
                NpcData data = null;
                if (this.regNpc._cords == null) {
                    if (gm != null) {
                        data = template.doSpawn(gm.getX(), gm.getY(), gm.getZ(), 1, gm.getHeading(), 0);
                    }
                }
                else {
                    data = template.doSpawn(this.regNpc._cords[0], this.regNpc._cords[1], this.regNpc._cords[2], 1, this.regNpc._cords[3], 0);
                }
                (this.regNpcInstance = data).setTitle(this.current.getEventType().getHtmlTitle());
                this.regNpcInstance.broadcastNpcInfo();
                if (data != null) {
                    this.print("NPC spawned to cords " + data.getLoc().getX() + ", " + data.getLoc().getY() + ", " + data.getLoc().getZ() + "; objId = " + data.getObjectId());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                this.print("error spawning NPC, " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
        }
    }

    public void unspawnRegNpc() {
        if (SunriseLoader.detailedDebug) {
            this.print("unspawnRegNpc()");
        }
        if (this.regNpcInstance != null) {
            if (SunriseLoader.detailedDebug) {
                this.print("regNpcInstance is not null, unspawning it...");
            }
            this.regNpcInstance.deleteMe();
            this.regNpcInstance = null;
        }
        else if (SunriseLoader.detailedDebug) {
            this.print("regNpcInstance is NULL!");
        }
        this.regNpc = null;
    }

    public synchronized void skipDelay(final PlayerEventInfo gm) {
        if (SunriseLoader.detailedDebug) {
            this.print("skipping event delay... ");
        }
        if (this._state == State.IDLE) {
            if (SunriseLoader.detailedDebug) {
                this.print("state is idle, can't skip delay");
            }
            gm.sendMessage("There's no active event atm.");
            return;
        }
        if (this._state == State.REGISTERING) {
            if (SunriseLoader.detailedDebug) {
                this.print("state is registering, skipping delay...");
            }
            if (this._regCountdownFuture != null) {
                this._regCountdownFuture.cancel(false);
            }
            if (this.taskFuture != null) {
                this.taskFuture.cancel(false);
            }
            this._counter = 0;
            this._regCountdownFuture = CallBack.getInstance().getOut().scheduleGeneral(this._regCountdown, 1L);
            if (SunriseLoader.detailedDebug) {
                this.print("delay successfully skipped");
            }
        }
        else {
            gm.sendMessage("The event can skip waiting delay only when it's in the registration state.");
            if (SunriseLoader.detailedDebug) {
                this.print("can't skip delay, state is " + this._state.toString());
            }
        }
    }

    public void watchEvent(final PlayerEventInfo gm, final int instanceId) {
        final AbstractMainEvent event = this.current;
        if (event == null) {
            gm.sendMessage("No event is available now.");
            return;
        }
        try {
            event.addSpectator(gm, instanceId);
        }
        catch (Exception e) {
            e.printStackTrace();
            gm.sendMessage("Event cannot be spectated now. Please try it again later.");
        }
    }

    public void stopWatching(final PlayerEventInfo gm) {
        final AbstractMainEvent event = this.current;
        if (event == null) {
            gm.sendMessage("No event is available now.");
            return;
        }
        event.removeSpectator(gm);
    }


    public synchronized void abort(final PlayerEventInfo gm, final boolean error) {
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager.abort(), error = " + error);
        }
        if (error) {
            if (SunriseLoader.detailedDebug) {
                this.print("aborting due to error...");
            }
            this.unspawnRegNpc();
            try {
                this.current.clearEvent();
            }
            catch (Exception e) {
                e.printStackTrace();
                this.clean(null);
                if (SunriseLoader.detailedDebug) {
                    this.print("error while aborting - " + SunriseLoader.getTraceString(e.getStackTrace()));
                }
            }
        }
        else {
            if (SunriseLoader.detailedDebug) {
                this.print("aborting due to GM... - _state = " + this._state.toString());
            }
            if (this._state == State.REGISTERING) {
                if (SunriseLoader.detailedDebug) {
                    this.print("aborting while in registering state");
                }
                SunriseLoader.debug("Event aborted by GM");
                this.unspawnRegNpc();
                this.current.clearEvent();
                this.announce(LanguageEngine.getMsg("announce_regAborted"));
                this._regCountdown.abort();
                if (SunriseLoader.detailedDebug) {
                    this.print("event (in registration) successfully aborted");
                }
            }
            else {
                if (this._state != State.RUNNING) {
                    if (SunriseLoader.detailedDebug) {
                        this.print("can't abort event now!");
                    }
                    gm.sendMessage("Event cannot be aborted now.");
                    return;
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("aborting while in running state");
                }
                this.unspawnRegNpc();
                if (this.current != null) {
                    this.current.clearEvent();
                }
                else {
                    this.clean("in RUNNING state after current was null!!!");
                }
                this.announce(LanguageEngine.getMsg("announce_eventAborted"));
                if (SunriseLoader.detailedDebug) {
                    this.print("event (in runtime) successfully aborted");
                }
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager.abort() finished");
        }
        if (!this.autoSchedulerPaused() && this.autoSchedulerEnabled()) {
            this.scheduler.schedule(-1.0, false);
            if (SunriseLoader.detailedDebug) {
                this.print("scheduler enabled, scheduling next event...");
            }
        }
    }

    public void endDueToError(final String text) {
        if (SunriseLoader.detailedDebug) {
            this.print("starting MainEventManager.endDueToError(): " + text);
        }
        this.announce(text);
        this.abort(null, true);
        if (SunriseLoader.detailedDebug) {
            this.print("finished MainEventManager.endDueToError()");
        }
    }

    public void end() {
        if (SunriseLoader.detailedDebug) {
            this.print("started MainEventManager.end()");
        }
        this._state = State.TELE_BACK;
        this.schedule(1);
        if (SunriseLoader.detailedDebug) {
            this.print("finished MainEventManager.end()");
        }
    }

    protected void schedule(final int time) {
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager.schedule(): " + time);
        }
        this.taskFuture = CallBack.getInstance().getOut().scheduleGeneral(this._task, time);
    }

    public void announce(final String text) {
        String announcer = "Event Engine";
        if (this.current != null) {
            announcer = this.current.getEventType().getAltTitle();
        }
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager.announce(): '" + text + "' announcer = " + announcer);
        }
        CallBack.getInstance().getOut().announceToAllScreenMessage(text, announcer);
    }

    public List<PlayerEventInfo> getPlayers() {
        return this._players;
    }

    public int getCounter() {
        return this._counter;
    }

    public String getTimeLeft(final boolean digitalClockFormat) {
        try {
            if (this._state == State.REGISTERING) {
                if (digitalClockFormat) {
                    return this._regCountdown.getTimeAdmin();
                }
                return this._regCountdown.getTime();
            }
            else {
                if (this._state == State.RUNNING) {
                    return this.current.getEstimatedTimeLeft();
                }
                return "N/A";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return "<font color=AE0000>Event error</font>";
        }
    }

    public String getMapName() {
        if (this.activeMap == null) {
            return "N/A";
        }
        return this.activeMap.getMapName();
    }

    public String getMapDesc() {
        if (this.activeMap == null) {
            return "N/A";
        }
        if (this.activeMap.getMapDesc() == null || this.activeMap.getMapDesc().length() == 0) {
            return "This map has no description.";
        }
        return this.activeMap.getMapDesc();
    }

    public EventMap getMap() {
        return this.activeMap;
    }

    public int getRunTime() {
        return (this.eventRunTime == 0) ? 120 : this.eventRunTime;
    }

    public State getState() {
        return this._state;
    }

    protected void msgToAll(final String text) {
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager.msgToAll(): " + text);
        }
        for (final PlayerEventInfo player : this._players) {
            player.sendMessage(text);
        }
    }

    public void paralizeAll(final boolean para) {
        try {
            if (SunriseLoader.detailedDebug) {
                this.print("paralyze all called, para = " + para);
            }
            for (final PlayerEventInfo player : this._players) {
                if (player.isOnline()) {
                    player.setIsParalyzed(para);
                    player.setIsInvul(para);
                    player.paralizeEffect(para);
                }
            }
        }
        catch (NullPointerException e) {
            if (SunriseLoader.detailedDebug) {
                this.print("error while paralyzing, " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
        }
    }

    public boolean canRegister(final PlayerEventInfo player, final boolean start) {
        if (player.getLevel() > this.current.getInt("maxLvl")) {
            player.sendMessage(LanguageEngine.getMsg("registering_highLevel"));
            return false;
        }
        if (player.getLevel() < this.current.getInt("minLvl")) {
            player.sendMessage(LanguageEngine.getMsg("registering_lowLevel"));
            return false;
        }
        if (!player.isGM() && start && this.current.getBoolean("dualboxCheck") && this.dualboxDetected(player, this.current.getInt("maxPlayersPerIp"))) {
            player.sendMessage(LanguageEngine.getMsg("registering_sameIp"));
            return false;
        }
        if (!EventManager.getInstance().canRegister(player)) {
            player.sendMessage(LanguageEngine.getMsg("registering_status"));
            return false;
        }
        return true;
    }


    public boolean registerPlayer(final PlayerEventInfo player) {
        if (SunriseLoader.detailedDebug) {
            this.print(". starting registerPlayer() for " + player.getPlayersName());
        }
        if (GabConfig.ER_EVENT_CLOSE_ALL && EpicRaidManager.getInstance().isStarted() && !EpicRaidManager.getInstance().isClosed()){
            player.sendMessage("Cant Register To Event because Epic Raid is Running!");
            return false;
        }

        if (this._state != State.REGISTERING) {
            player.sendMessage(LanguageEngine.getMsg("registering_notRegState"));
            return false;
        }
        if (player.isRegistered()) {
            player.sendMessage(LanguageEngine.getMsg("registering_alreadyRegistered"));
            return false;
        }
        final int i = EventWarnings.getInstance().getPoints(player);
        if (i >= EventWarnings.MAX_WARNINGS && !player.isGM()) {
            player.sendMessage(LanguageEngine.getMsg("registering_warningPoints", EventWarnings.MAX_WARNINGS, i));
            if (SunriseLoader.detailedDebug) {
                this.print("... registerPlayer() for " + player.getPlayersName() + ", player has too many warnings! (" + i + ")");
            }
            return false;
        }
        if (!this.canRegister(player, true)) {
            if (SunriseLoader.detailedDebug) {
                this.print("... registerPlayer() for " + player.getPlayersName() + ", player failed to register on event, manager didn't allow so!");
            }
            player.sendMessage(LanguageEngine.getMsg("registering_fail"));
            return false;
        }
        if (!this.getCurrent().canRegister(player)) {
            if (SunriseLoader.detailedDebug) {
                this.print("... registerPlayer() for " + player.getPlayersName() + ", player failed to register on event, event itself didn't allow so!");
            }
            player.sendMessage(LanguageEngine.getMsg("registering_notAllowed"));
            return false;
        }
        if (EventConfig.getInstance().getGlobalConfigBoolean("eventSchemeBuffer")) {
            if (!EventBuffer.getInstance().hasBuffs(player)) {
                player.sendMessage(LanguageEngine.getMsg("registering_buffs"));
            }
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "main", this.getCurrent().getEventType().getAltTitle());
        }
        player.sendMessage(LanguageEngine.getMsg("registering_registered"));
        final PlayerEventInfo pi = CallBack.getInstance().getPlayerBase().addInfo(player);
        pi.setIsRegisteredToMainEvent(true, this.current.getEventType());
        this._players.add(pi);
        if (SunriseLoader.detailedDebug) {
            this.print("... registerPlayer() for " + player.getPlayersName() + ", player has been registered!");
        }
        return true;
    }

    public boolean unregisterPlayer(final PlayerEventInfo player, final boolean force) {
        if (player == null) {
            return false;
        }
        if (SunriseLoader.detailedDebug) {
            this.print(". starting unregisterPlayer() for " + player.getPlayersName() + ", force = " + force);
        }
        if (!EventConfig.getInstance().getGlobalConfigBoolean("enableUnregistrations")) {
            if (SunriseLoader.detailedDebug) {
                this.print("... unregisterPlayer()  - unregistrations are not allowed here!");
            }
            if (!force) {
                player.sendMessage(LanguageEngine.getMsg("unregistering_cantUnregister"));
            }
            return false;
        }
        if (!this._players.contains(player)) {
            if (SunriseLoader.detailedDebug) {
                this.print("... unregisterPlayer() for " + player.getPlayersName() + " player is not registered");
            }
            if (!force) {
                player.sendMessage(LanguageEngine.getMsg("unregistering_notRegistered"));
            }
            return false;
        }
        if (this._state != State.REGISTERING && !force) {
            if (SunriseLoader.detailedDebug) {
                this.print("... unregisterPlayer() for " + player.getPlayersName() + " player can't unregister now, becuase _state = " + this._state.toString());
            }
            player.sendMessage(LanguageEngine.getMsg("unregistering_cant"));
            return false;
        }
        player.sendMessage(LanguageEngine.getMsg("unregistering_unregistered"));
        player.setIsRegisteredToMainEvent(false, null);
        CallBack.getInstance().getPlayerBase().eventEnd(player);
        this._players.remove(player);
        if (SunriseLoader.detailedDebug) {
            this.print("... unregisterPlayer() for " + player.getPlayersName() + " player has been unregistered");
        }
        if (this.current != null) {
            this.current.playerUnregistered(player);
        }
        return true;
    }

    public boolean dualboxDetected(final PlayerEventInfo player) {
        if (!player.isOnline(true)) {
            return false;
        }
        final String ip1 = player.getIp();
        if (ip1 == null) {
            return false;
        }
        for (final PlayerEventInfo p : this._players) {
            final String ip2 = p.getIp();
            if (ip1.equals(ip2)) {
                if (SunriseLoader.detailedDebug) {
                    this.print("... MainEventManager.dualboxDetected() for " + player.getPlayersName() + ", found dualbox for IP " + player.getIp());
                }
                return true;
            }
        }
        return false;
    }
    public static boolean isDualBox(PlayerEventInfo player1, PlayerEventInfo player2) {
        try {
            String ip_net1 = player1.getOwner().getClient().getConnectionAddress().getHostAddress();
            String ip_net2 = player2.getOwner().getClient().getConnectionAddress().getHostAddress();
            String ip_pc1 = "";
            String ip_pc2 = "";
            int[][] trace1 = player1.getOwner().getClient().getTrace();
            for (int o = 0; o < (trace1[0]).length; o++) {
                ip_pc1 = ip_pc1 + trace1[0][o];
                if (o != (trace1[0]).length - 1) {
                    ip_pc1 = ip_pc1 + ".";
                }
            }
            int[][] trace2 = player2.getOwner().getClient().getTrace();
            for (int u = 0; u < (trace2[0]).length; u++) {
                ip_pc2 = ip_pc2 + trace2[0][u];
                if (u != (trace2[0]).length - 1) {
                    ip_pc2 = ip_pc2 + ".";
                }
            }
            if (ip_net1.equals(ip_net2) && ip_pc1.equals(ip_pc2)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean dualboxDetected(final PlayerEventInfo player, final int maxPerIp) {
        if (!player.isOnline(true)) {
            return false;
        }
        int occurences = 0;
        final String ip1 = player.getIp();
        if (ip1 == null) {
            return false;
        }
        if (maxPerIp <= 1) {
            for (final PlayerEventInfo p : this._players) {
                if (isDualBox(player, p)) {
                    return true;
                }
            }
        }
        else {
            for (final PlayerEventInfo p : this._players) {
                if (ip1.equals(p.getIp())) {
                    ++occurences;
                }
            }
        }
        if (occurences >= maxPerIp) {
            if (SunriseLoader.detailedDebug) {
                this.print("... MainEventManager.dualboxDetected() for " + player.getPlayersName() + ", found dualbox for IP (method 2) " + player.getIp() + " maxPerIp " + maxPerIp + " occurences = " + occurences);
            }
            return true;
        }
        return false;
    }

    public AbstractMainEvent getCurrent() {
        return this.current;
    }

    public int getPlayersCount() {
        return this._players.size();
    }

    public void abortAutoScheduler(final PlayerEventInfo gm) {
        if (this.autoSchedulerPaused()) {
            this.unpauseAutoScheduler(gm, false);
        }
        if (this.scheduler.abort()) {
            if (gm != null) {
                gm.sendMessage("Automatic event scheduling has been disabled");
            }
            SunriseLoader.debug("Automatic scheduler disabled" + ((gm != null) ? " by a GM." : "."), Level.INFO);
        }
        if (SunriseLoader.detailedDebug) {
            this.print("aborting auto scheduler, gm is null? " + (gm == null));
        }
        this.autoScheduler = false;
    }

    public void pauseAutoScheduler(final PlayerEventInfo gm) {
        if (!EventConfig.getInstance().getGlobalConfigBoolean("enableAutomaticScheduler")) {
            gm.sendMessage("The automatic event scheduler has been disabled in configs.");
            return;
        }
        if (this.scheduler == null) {
            return;
        }
        if (this.getCurrent() != null) {
            gm.sendMessage("There's no pausable delay. Wait till the event ends.");
            return;
        }
        if (!this.autoSchedulerPaused() && this.autoSchedulerEnabled()) {
            if (this.scheduler._future == null) {
                gm.sendMessage("Cannot pause the scheduler now.");
                return;
            }
            if (this.scheduler._future.getDelay(TimeUnit.SECONDS) < 2L) {
                gm.sendMessage("Cannot pause now. Event starts in less than 2 seconds.");
                return;
            }
            this.pausedTimeLeft = (double)this.scheduler._future.getDelay(TimeUnit.SECONDS);
            this.scheduler.abort();
            SunriseLoader.debug("Automatic scheduler paused" + ((gm != null) ? " by a GM." : "."), Level.INFO);
        }
        else {
            gm.sendMessage("The scheduler must be enabled.");
        }
    }

    public void unpauseAutoScheduler(final PlayerEventInfo gm, final boolean run) {
        if (!EventConfig.getInstance().getGlobalConfigBoolean("enableAutomaticScheduler")) {
            gm.sendMessage("The automatic event scheduler has been disabled in configs.");
            return;
        }
        if (this.scheduler == null) {
            return;
        }
        if (this.getCurrent() != null) {
            gm.sendMessage("An event is already running.");
            return;
        }
        if (this.autoSchedulerPaused()) {
            if (run) {
                this.scheduler.schedule(this.pausedTimeLeft, false);
                SunriseLoader.debug("Automatic scheduler continues (event in " + this.pausedTimeLeft + " seconds) again after being paused" + ((gm != null) ? " by a GM." : "."), Level.INFO);
            }
            else {
                SunriseLoader.debug("Automatic scheduler unpaused " + ((gm != null) ? " by a GM." : "."), Level.INFO);
            }
            this.pausedTimeLeft = 0.0;
        }
        else if (gm != null) {
            gm.sendMessage("The scheduler is not paused.");
        }
    }

    public void restartAutoScheduler(final PlayerEventInfo gm) {
        if (!EventConfig.getInstance().getGlobalConfigBoolean("enableAutomaticScheduler")) {
            gm.sendMessage("The automatic event scheduler has been disabled in configs.");
            return;
        }
        if (this.autoSchedulerPaused()) {
            this.unpauseAutoScheduler(gm, true);
        }
        else {
            SunriseLoader.debug("Automatic scheduler enabled" + ((gm != null) ? " by a GM." : "."), Level.INFO);
            this.scheduler.schedule(-1.0, false);
        }
        if (gm != null && this.current == null) {
            gm.sendMessage("Automatic event scheduling has been enabled. Next event in " + EventConfig.getInstance().getGlobalConfigInt("delayBetweenEvents") + " minutes.");
        }
    }

    public boolean autoSchedulerEnabled() {
        return this.autoScheduler;
    }

    public boolean autoSchedulerPaused() {
        return this.pausedTimeLeft > 0.0;
    }

    public String getAutoSchedulerDelay() {
        double d = 0.0;
        if (this.scheduler._future != null && !this.scheduler._future.isDone()) {
            d = (double)this.scheduler._future.getDelay(TimeUnit.SECONDS);
        }
        if (this.autoSchedulerPaused()) {
            d = this.pausedTimeLeft;
        }
        if (d == 0.0) {
            return "N/A";
        }
        if (d >= 60.0) {
            return (int)d / 60 + " min";
        }
        return (int)d + " sec";
    }

    public String getLastEventTime() {
        if (this.lastEvent == 0L) {
            return "N/A";
        }
        final long time = System.currentTimeMillis();
        long diff = time - this.lastEvent;
        if (diff <= 1000L) {
            return "< 1 sec ago";
        }
        diff /= 1000L;
        if (diff <= 60L) {
            return diff + " sec ago";
        }
        diff /= 60L;
        if (diff > 60L) {
            diff /= 60L;
            return diff + " hours ago";
        }
        return diff + " min ago";
    }

    public List<EventScheduleData> getEventScheduleData() {
        return this._eventScheduleData;
    }

    public EventType nextAvailableEvent(final boolean testOnly) {
        EventType event = null;
        int lastOrder = 0;
        if (this._lastEvent != null) {
            for (final EventScheduleData d : this._eventScheduleData) {
                if (d.getEvent() == this._lastEvent) {
                    lastOrder = d.getOrder();
                }
            }
        }
        int limit = this._eventScheduleData.size() * 2;
        if (this._eventScheduleData.isEmpty()) {
            return null;
        }
        while (event == null) {
            for (final EventScheduleData d2 : this._eventScheduleData) {
                if (d2.getOrder() == lastOrder + 1 && d2.getEvent().isRegularEvent() && EventConfig.getInstance().isEventAllowed(d2.getEvent()) && EventManager.getInstance().getMainEvent(d2.getEvent()) != null && EventMapSystem.getInstance().getMapsCount(d2.getEvent()) > 0 && (testOnly || CallBack.getInstance().getOut().random(100) < d2.getChance())) {
                    event = d2.getEvent();
                    if (testOnly) {
                        break;
                    }
                    this._lastEvent = event;
                    break;
                }
            }
            if (--limit <= 0) {
                break;
            }
            if (lastOrder > this._eventScheduleData.size()) {
                lastOrder = 0;
            }
            else {
                ++lastOrder;
            }
        }
        return event;
    }

    public EventScheduleData getScheduleData(final EventType type) {
        for (final EventScheduleData d : this._eventScheduleData) {
            if (d.getEvent().equals(type)) {
                return d;
            }
        }
        return null;
    }

    public EventType getLastEventOrder() {
        return this._lastEvent;
    }

    public EventType getGuessedNextEvent() {
        return this.nextAvailableEvent(true);
    }

    private void addScheduleData(final EventType type, int order, final int chance, final boolean updateInDb) {
        if (type == null) {
            return;
        }
        boolean selectOrder = false;
        if (order == -1 || order > this._eventScheduleData.size()) {
            selectOrder = true;
        }
        else {
            for (final EventScheduleData d : this._eventScheduleData) {
                if (d.getOrder() == order) {
                    selectOrder = true;
                    break;
                }
            }
        }
        if (selectOrder) {
            int freeOrder = -1;
            for (int i = 0; i < this._eventScheduleData.size(); ++i) {
                boolean found = false;
                for (final EventScheduleData d2 : this._eventScheduleData) {
                    if (d2.getOrder() == i + 1) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    freeOrder = i + 1;
                    break;
                }
            }
            if (freeOrder == -1) {
                int highest = 0;
                for (final EventScheduleData d3 : this._eventScheduleData) {
                    if (d3.getOrder() > highest) {
                        highest = d3.getOrder();
                    }
                }
                order = highest + 1;
            }
            else {
                order = freeOrder;
            }
        }
        boolean add = true;
        for (final EventScheduleData d4 : this._eventScheduleData) {
            if (d4.getEvent() == type) {
                add = false;
                break;
            }
        }
        if (add) {
            final EventScheduleData data = new EventScheduleData(type, order, chance);
            this._eventScheduleData.add(data);
        }
        if (selectOrder) {
            this.saveScheduleData(type);
            if (updateInDb) {
                if (order != -1) {
                    SunriseLoader.debug("Adding wrong-configured/missing " + type.getAltTitle() + " event to EventOrder system with order " + order);
                }
                else {
                    SunriseLoader.debug("Error adding " + type.getAltTitle() + " event to EventOrder system");
                }
            }
        }
    }

    public void loadScheduleData() {
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_eventorder ORDER BY eventOrder ASC");
             final ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                final String event = rset.getString("event");
                int order = rset.getInt("eventOrder");
                final int chance = rset.getInt("chance");
                for (final EventScheduleData d : this._eventScheduleData) {
                    if (d.getOrder() == order) {
                        order = -1;
                    }
                }
                this.addScheduleData(EventType.getType(event), order, chance, false);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        for (final EventType type : EventType.values()) {
            if (type.isRegularEvent() && type != EventType.Unassigned && EventManager.getInstance().getEvent(type) != null && this.getScheduleData(type) == null) {
                this.addScheduleData(type, -1, 100, true);
            }
        }
    }

    public int saveScheduleData(final EventType event) {
        final EventScheduleData data = this.getScheduleData(event);
        if (data == null) {
            return -1;
        }
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_eventorder VALUES (?,?,?)")) {
            statement.setString(1, data.getEvent().getAltTitle());
            statement.setInt(2, data.getOrder());
            statement.setInt(3, data.getChance());
            statement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return data._order;
    }

    protected void abortCast() {
        if (SunriseLoader.detailedDebug) {
            this.print("aborting cast of all players on the event");
        }
        for (final PlayerEventInfo p : this._players) {
            p.abortCasting();
        }
    }

    public void clean(final String message) {
        if (SunriseLoader.detailedDebug) {
            this.print("MainEventManager() clean: " + message);
        }
        this.current = null;
        this.activeMap = null;
        this.eventRunTime = 0;
        this._players.clear();
        if (message != null) {
            this.announce(message);
        }
        this._state = State.IDLE;
        if (this.regNpcInstance != null) {
            this.regNpcInstance.deleteMe();
            this.regNpcInstance = null;
        }
        this.regNpc = null;
        this.lastEvent = System.currentTimeMillis();
    }

    protected void print(final String msg) {
        SunriseLoader.detailedDebug(msg);
    }

    private class EventTaskScheduler implements Runnable
    {
        protected EventTaskScheduler() {
        }

        @Override
        public void run() {
            switch (MainEventManager.this._state) {
                case REGISTERING: {
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - ending registration");
                    }
                    MainEventManager.this.announce(LanguageEngine.getMsg("announce_regClosed"));
                    SunriseLoader.debug("Registration phase ended.");
                    for (final PlayerEventInfo p : MainEventManager.this._players) {
                        if (!MainEventManager.this.canRegister(p, false)) {
                            MainEventManager.this.unregisterPlayer(p, true);
                        }
                    }
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - players that can't participate were unregistered");
                    }
                    if (MainEventManager.this.current.canStart()) {
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("eventtask - event started");
                        }
                        SunriseLoader.debug("Event starts.");
                        MainEventManager.this.announce(LanguageEngine.getMsg("announce_started"));
                        MainEventManager.this.current.initEvent();
                        MainEventManager.this._state = State.RUNNING;
                        MainEventManager.this.msgToAll(LanguageEngine.getMsg("announce_teleport10sec"));
                        int delay = EventConfig.getInstance().getGlobalConfigInt("teleToEventDelay");
                        if (delay <= 0 || delay > 60000) {
                            delay = 10000;
                        }
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("eventtask - event started, teletoevent delay " + delay);
                        }
                        if (EventConfig.getInstance().getGlobalConfigBoolean("antistuckProtection")) {
                            if (SunriseLoader.detailedDebug) {
                                MainEventManager.this.print("eventtask - anti stuck protection ON");
                            }
                            MainEventManager.this.abortCast();
                            if (SunriseLoader.detailedDebug) {
                                MainEventManager.this.print("eventtask - aborted cast...");
                            }
                            final int fDelay = delay;
                            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                                MainEventManager.this.paralizeAll(true);
                                MainEventManager.this.schedule(fDelay - 1000);
                                return;
                            }, 1000L);
                        }
                        else {
                            if (SunriseLoader.detailedDebug) {
                                MainEventManager.this.print("eventtask - anti stuck protection OFF");
                            }
                            MainEventManager.this.paralizeAll(true);
                            MainEventManager.this.schedule(delay);
                            if (SunriseLoader.detailedDebug) {
                                MainEventManager.this.print("eventtask - scheduled for next state in " + delay);
                            }
                        }
                        break;
                    }
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - can't start - not enought players - " + MainEventManager.this._players.size());
                    }
                    SunriseLoader.debug("Not enought participants.");
                    MainEventManager.this.unspawnRegNpc();
                    MainEventManager.this.current.clearEvent();
                    MainEventManager.this.announce(LanguageEngine.getMsg("announce_lackOfParticipants"));
                    if (!MainEventManager.this.autoSchedulerPaused() && MainEventManager.this.autoSchedulerEnabled()) {
                        MainEventManager.this.scheduler.schedule(-1.0, false);
                        break;
                    }
                    break;
                }
                case RUNNING: {
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event started, players teleported");
                    }
                    MainEventManager.this.paralizeAll(false);
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event started, players unparalyzed");
                    }
                    MainEventManager.this.current.runEvent();
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event started, event runned");
                    }
                    if (MainEventManager.this.current != null) {
                        MainEventManager.this.current.initMap();
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("eventtask - event started, map initialized");
                        }
                    }
                    else if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event started, cannot initialize map (null)");
                    }
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event started, stats given");
                        break;
                    }
                    break;
                }
                case TELE_BACK: {
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event ending, teleporting back in 10 sec");
                    }
                    if (MainEventManager.this.current != null) {
                        MainEventManager.this.current.onEventEnd();
                    }
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - on event end");
                    }
                    MainEventManager.this.msgToAll(LanguageEngine.getMsg("announce_teleportBack10sec"));
                    MainEventManager.this._state = State.END;
                    SunriseLoader.debug("Teleporting back.");
                    MainEventManager.this.schedule(10000);
                    break;
                }
                case END: {
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event ended, teleporting back NOW!");
                    }
                    MainEventManager.this.unspawnRegNpc();
                    MainEventManager.this.current.clearEvent();
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event ended, event cleared!");
                    }
                    MainEventManager.this.announce(LanguageEngine.getMsg("announce_end"));
                    CallBack.getInstance().getOut().purge();
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("eventtask - event ended, after purge");
                    }
                    if (!MainEventManager.this.autoSchedulerPaused() && MainEventManager.this.autoSchedulerEnabled()) {
                        MainEventManager.this.scheduler.schedule(-1.0, false);
                    }
                    SunriseLoader.debug("Event ended.");
                    break;
                }
            }
        }
    }

    private class RegistrationCountdown implements Runnable
    {
        protected RegistrationCountdown() {
        }

        protected String getTimeAdmin() {
            final String mins = "" + MainEventManager.this._counter / 60;
            final String secs = "" + MainEventManager.this._counter % 60;
            return "" + mins + ":" + secs + "";
        }

        protected String getTime() {
            if (MainEventManager.this._counter > 60) {
                int min = MainEventManager.this._counter / 60;
                if (min < 1) {
                    min = 1;
                }
                return min + " minutes";
            }
            return MainEventManager.this._counter + " seconds";
        }

        @Override
        public void run() {
            if (MainEventManager.this._state == State.REGISTERING) {
                switch (MainEventManager.this._counter) {
                    case 60:
                    case 300:
                    case 600:
                    case 1200:
                    case 1800: {
                        MainEventManager.this.announce(LanguageEngine.getMsg("announce_timeleft_min", MainEventManager.this._counter / 60));
                        break;
                    }
                    case 5:
                    case 10:
                    case 30: {
                        MainEventManager.this.announce(LanguageEngine.getMsg("announce_timeleft_sec", MainEventManager.this._counter));
                        break;
                    }
                }
            }
            if (MainEventManager.this._counter == 0) {
                if (SunriseLoader.detailedDebug) {
                    MainEventManager.this.print("registration coutndown counter 0, scheduling next action");
                }
                MainEventManager.this.schedule(1);
            }
            else {
                final MainEventManager this$0 = MainEventManager.this;
                --this$0._counter;
                MainEventManager.this._regCountdownFuture = CallBack.getInstance().getOut().scheduleGeneral(MainEventManager.this._regCountdown, 1000L);
            }
        }

        protected void abort() {
            if (SunriseLoader.detailedDebug) {
                MainEventManager.this.print("aborting regcoutndown... ");
            }
            if (MainEventManager.this._regCountdownFuture != null) {
                if (SunriseLoader.detailedDebug) {
                    MainEventManager.this.print("... regCount is not null");
                }
                MainEventManager.this._regCountdownFuture.cancel(false);
                MainEventManager.this._regCountdownFuture = null;
            }
            else if (SunriseLoader.detailedDebug) {
                MainEventManager.this.print("... regCount is NULL!");
            }
            MainEventManager.this._counter = 0;
        }
    }

    public class EventScheduleData
    {
        private final EventType _event;
        protected int _order;
        private int _chance;

        protected EventScheduleData(final EventType event, final int order, final int chance) {
            this._event = event;
            this._order = order;
            this._chance = chance;
        }

        public EventType getEvent() {
            return this._event;
        }

        public int getOrder() {
            return this._order;
        }

        public void setOrder(final int c) {
            this._order = c;
        }

        public int getChance() {
            return this._chance;
        }

        public void setChance(final int c) {
            this._chance = c;
        }

        public boolean decreaseOrder() {
            boolean done = false;
            for (final EventScheduleData d : MainEventManager.this._eventScheduleData) {
                if (d.getEvent() != this.getEvent() && d.getOrder() == this._order + 1) {
                    d.setOrder(this._order);
                    ++this._order;
                    MainEventManager.this.saveScheduleData(d.getEvent());
                    MainEventManager.this.saveScheduleData(this.getEvent());
                    done = true;
                    break;
                }
            }
            return done;
        }

        public boolean raiseOrder() {
            boolean done = false;
            for (final EventScheduleData d : MainEventManager.this._eventScheduleData) {
                if (d.getEvent() != this.getEvent() && d.getOrder() == this._order - 1) {
                    d.setOrder(this._order);
                    --this._order;
                    MainEventManager.this.saveScheduleData(d.getEvent());
                    MainEventManager.this.saveScheduleData(this.getEvent());
                    done = true;
                    break;
                }
            }
            return done;
        }
    }

    public class EventScheduler implements Runnable
    {
        protected ScheduledFuture<?> _future;

        @Override
        public void run() {
            try {
                boolean selected = false;
                if (SunriseLoader.detailedDebug) {
                    MainEventManager.this.print("starting EventScheduler.run()");
                }
                int i = 0;
                while (i < EventType.values().length) {
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("trying to find an event to be started...");
                    }
                    SunriseLoader.debug("Trying to find an event to be started:", Level.INFO);
                    final EventType next = EventType.getNextRegularEvent();
                    if (next == null) {
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("no next event is available. stopping it here, pausing scheduler");
                        }
                        SunriseLoader.debug("No next event is aviaible!", Level.INFO);
                        if (MainEventManager.this.autoSchedulerPaused()) {
                            break;
                        }
                        this.schedule(-1.0, false);
                        break;
                    }
                    else {
                        EventMap nextMap = null;
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("next selected event is " + next.getAltTitle());
                        }
                        final AbstractMainEvent event = EventManager.getInstance().getMainEvent(next);
                        final List<EventMap> maps = new LinkedList<EventMap>();
                        maps.addAll(EventMapSystem.getInstance().getMaps(next).values());
                        Collections.shuffle(maps);
                        for (final EventMap map : maps) {
                            if (event.canRun(map)) {
                                nextMap = map;
                                break;
                            }
                        }
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("no available map for event " + next.getAltTitle());
                        }
                        if (nextMap != null) {
                            selected = true;
                            if (SunriseLoader.detailedDebug) {
                                MainEventManager.this.print("selected and starting next event via automatic scheduler");
                            }
                            MainEventManager.this.startEvent(null, next, EventConfig.getInstance().getGlobalConfigInt("defaultRegTime"), nextMap.getMapName(), null, EventConfig.getInstance().getGlobalConfigInt("defaultRunTime"));
                            break;
                        }
                        if (SunriseLoader.detailedDebug) {
                            MainEventManager.this.print("no available map for event " + next.getAltTitle());
                        }
                        ++i;
                    }
                }
                if (!selected) {
                    SunriseLoader.debug("No event could be started. Check if you have any maps for them and if they are configured properly.");
                    if (SunriseLoader.detailedDebug) {
                        MainEventManager.this.print("no event could be started...");
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean abort() {
            if (SunriseLoader.detailedDebug) {
                MainEventManager.this.print("aborting event scheduler");
            }
            if (this._future != null) {
                this._future.cancel(false);
                this._future = null;
                return true;
            }
            return false;
        }

        public void schedule(double delay, final boolean firstStart) {
            if (!EventConfig.getInstance().getGlobalConfigBoolean("enableAutomaticScheduler")) {
                return;
            }
            if (this._future != null) {
                this._future.cancel(false);
                this._future = null;
            }
            MainEventManager.this.autoScheduler = true;
            if (MainEventManager.this.current == null) {
                if (firstStart) {
                    delay = EventConfig.getInstance().getGlobalConfigInt("firstEventDelay") * 60000;
                    this._future = CallBack.getInstance().getOut().scheduleGeneral(this, (long)delay);
                }
                else if (delay > -1.0) {
                    this._future = CallBack.getInstance().getOut().scheduleGeneral(this, (long)delay * 1000L);
                }
                else {
                    delay = EventConfig.getInstance().getGlobalConfigInt("delayBetweenEvents") * 60000;
                    this._future = CallBack.getInstance().getOut().scheduleGeneral(this, (long)delay);
                }
                if (SunriseLoader.detailedDebug) {
                    MainEventManager.this.print("scheduling next event in " + Math.round(delay / 60000.0) + " minutes.");
                }
                SunriseLoader.debug("Next event in " + Math.round(delay / 60000.0) + " minutes.", Level.INFO);
            }
            else {
                SunriseLoader.debug("Automatic scheduler reeanbled.");
                if (SunriseLoader.detailedDebug) {
                    MainEventManager.this.print("reenabling automatic scheduler");
                }
            }
        }
    }

    public class RegNpcLoc
    {
        public String _name;
        public int[] _cords;

        public RegNpcLoc(final String name, final int[] cords) {
            this._name = name;
            this._cords = cords;
        }
    }

    
    public enum State
    {
        IDLE,
        REGISTERING,
        RUNNING,
        TELE_BACK,
        END;
    }
}


