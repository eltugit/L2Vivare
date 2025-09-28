package gr.sr.events.engine;


import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gr.sr.events.Configurable;
import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.Event;
import gr.sr.events.engine.base.EventPlayerData;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.html.EventHtmlManager;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.mini.MiniEventGame;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.mini.events.KoreanManager;
import gr.sr.events.engine.mini.events.MiniTvTManager;
import gr.sr.events.engine.mini.events.OnevsOneManager;
import gr.sr.events.engine.mini.events.PartyvsPartyManager;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.SunriseEvents;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.NpcData;
import gr.sr.interf.delegate.SkillData;
import gr.sr.interf.handlers.AdminCommandHandlerInstance;
import gr.sr.l2j.CallBack;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;


public class EventManager {
    private final Map<EventType, Map<Integer, MiniEventManager>> _miniEvents;
    private final Map<EventType, AbstractMainEvent> _mainEvents;
    private Map<DisconnectedPlayerData, Long> _disconnectedPlayers;
    private MainEventManager _mainManager;
    private EventHtmlManager _html;
    public static boolean ALLOW_VOICE_COMMANDS = EventConfig.getInstance().getGlobalConfigBoolean("allowVoicedCommands");
    public static String REGISTER_VOICE_COMMAND = EventConfig.getInstance().getGlobalConfigValue("registerVoicedCommand");
    public static String UNREGISTER_VOICE_COMMAND = EventConfig.getInstance().getGlobalConfigValue("unregisterVoicedCommand");
    public Comparator<PlayerEventInfo> compareByLevels;
    public Comparator<PlayerEventInfo> compareByPvps;
    public Comparator<EventTeam> compareTeamKills;

    public EventManager() {
        this.compareByLevels = ((o1, o2) -> {
            int level1 = o1.getLevel();
            int level2 = o2.getLevel();
            return (level1 < level2) ? 1 : ((level1 == level2) ? 0 : -1);
        });
        this.compareByPvps = ((o1, o2) -> {
            int pvp1 = o1.getPvpKills();
            int pvp2 = o2.getPvpKills();
            return (pvp1 < pvp2) ? 1 : ((pvp1 == pvp2) ? 0 : -1);
        });
        this.compareTeamKills = ((t1, t2) -> {
            int kills1 = t1.getKills();
            int kills2 = t2.getKills();
            return (kills1 < kills2) ? 1 : ((kills1 == kills2) ? 0 : -1);
        });
        this.comparePlayersKills = ((p1, p2) -> {
            int kills1 = p1.getKills();
            int kills2 = p2.getKills();
            return (kills1 < kills2) ? 1 : ((kills1 == kills2) ? 0 : -1);
        });
        this.comparePlayersScore = ((p1, p2) -> {
            int score1 = p1.getScore();
            int score2 = p2.getScore();
            if (score1 == score2) {
                int deaths1 = p1.getDeaths();
                int deaths2 = p2.getDeaths();
                return (deaths1 < deaths2) ? -1 : ((deaths1 == deaths2) ? 0 : 1);
            }
            return (score1 < score2) ? 1 : -1;
        });
        this.compareTeamScore = ((t1, t2) -> {
            int score1 = t1.getScore();
            int score2 = t2.getScore();
            return (score1 < score2) ? 1 : ((score1 == score2) ? 0 : -1);
        });
        CallBack.getInstance().getOut().registerAdminHandler(new AdminSunrise());
        this._miniEvents = new LinkedHashMap<>((EventType.values()).length);
        this._mainEvents = new LinkedHashMap<>((EventType.values()).length);
        SunriseLoader.debug("Loading events...");
        loadEvents();
    }

    public Comparator<PlayerEventInfo> comparePlayersKills;
    public Comparator<PlayerEventInfo> comparePlayersScore;
    public Comparator<EventTeam> compareTeamScore;

    private void loadEvents() {
        int count = 0;
        this._disconnectedPlayers = new LinkedHashMap<>();
        this._mainManager = new MainEventManager();
        for (EventType event : EventType.values()) {
            if (event != EventType.Unassigned) {
                Event eventInstance = event.loadEvent(this._mainManager);
                if (eventInstance != null) {
                    if (event.isRegularEvent()) {
                        this._mainEvents.put(eventInstance.getEventType(), (AbstractMainEvent) eventInstance);
                        count++;
                    }
                }
            }
        }
        SunriseLoader.debug("Loaded " + count + " main events.");
        this._miniEvents.put(EventType.Unassigned, new LinkedHashMap<>());
        this._miniEvents.put(EventType.Classic_1v1, new LinkedHashMap<>());
        this._miniEvents.put(EventType.PartyvsParty, new LinkedHashMap<>());
        this._miniEvents.put(EventType.Korean, new LinkedHashMap<>());
        this._miniEvents.put(EventType.MiniTvT, new LinkedHashMap<>());
        SunriseLoader.debug("Loaded " + this._miniEvents.size() + " mini event types.");
    }

    public void setHtmlManager(EventHtmlManager manager) {
        this._html = manager;
    }

    public MiniEventManager createManager(EventType type, int modeId, String name, String visibleName) {
        OnevsOneManager onevsOneManager;
        PartyvsPartyManager partyvsPartyManager;
        KoreanManager koreanManager;
        MiniTvTManager miniTvTManager;
        MiniEventManager manager = null;
        switch (type) {
            case Classic_1v1:
                onevsOneManager = new OnevsOneManager(type);
                onevsOneManager.getMode().setModeName(name);
                onevsOneManager.getMode().setVisibleName(visibleName);
                (this._miniEvents.get(type)).put(Integer.valueOf(modeId), onevsOneManager);
                return (MiniEventManager) onevsOneManager;
            case PartyvsParty:
                partyvsPartyManager = new PartyvsPartyManager(type);
                partyvsPartyManager.getMode().setModeName(name);
                partyvsPartyManager.getMode().setVisibleName(visibleName);
                (this._miniEvents.get(type)).put(Integer.valueOf(modeId), partyvsPartyManager);
                return (MiniEventManager) partyvsPartyManager;
            case Korean:
                koreanManager = new KoreanManager(type);
                koreanManager.getMode().setModeName(name);
                koreanManager.getMode().setVisibleName(visibleName);
                (this._miniEvents.get(type)).put(Integer.valueOf(modeId), koreanManager);
                return (MiniEventManager) koreanManager;
            case MiniTvT:
                miniTvTManager = new MiniTvTManager(type);
                miniTvTManager.getMode().setModeName(name);
                miniTvTManager.getMode().setVisibleName(visibleName);
                (this._miniEvents.get(type)).put(Integer.valueOf(modeId), miniTvTManager);
                return (MiniEventManager) miniTvTManager;
        }
        SunriseLoader.debug("Event " + type.getAltTitle() + " isn't implemented yet.", Level.WARNING);
        return null;
    }

    
    public static final EventManager getInstance() {
        return SingletonHolder._instance;
    }

    public Map<EventType, Map<Integer, MiniEventManager>> getMiniEvents() {
        return this._miniEvents;
    }

    public Map<EventType, AbstractMainEvent> getMainEvents() {
        return this._mainEvents;
    }

    public Configurable getEvent(EventType type) {
        return getEvent(type, 1);
    }

    public Configurable getEvent(EventType type, int modeId) {
        if (type.isRegularEvent()) {
            return (Configurable) getMainEvent(type);
        }
        return (Configurable) getMiniEvent(type, modeId);
    }

    public MiniEventManager getMiniEvent(EventType type, int id) {
        if (this._miniEvents.get(type) == null) {
            return null;
        }
        return (MiniEventManager) ((Map) this._miniEvents.get(type)).get(Integer.valueOf(id));
    }

    public AbstractMainEvent getMainEvent(EventType type) {
        if (!this._mainEvents.containsKey(type)) {
            return null;
        }
        return this._mainEvents.get(type);
    }

    public AbstractMainEvent getCurrentMainEvent() {
        return this._mainManager.getCurrent();
    }

    public boolean onBypass(PlayerEventInfo player, String bypass) {
        return this._html.onBypass(player, bypass);
    }

    public boolean showNpcHtml(PlayerEventInfo player, NpcData npc) {
        return this._html.showNpcHtml(player, npc);
    }

    public EventHtmlManager getHtmlManager() {
        return this._html;
    }

    public boolean canRegister(PlayerEventInfo player) {
        if (player.getOwner().getInstanceId() == GabConfig.CHALLENGER_EVENT_INSTANCE_ID) {
            player.sendMessage("Cannot register for events while inside the competitive zone.");
            return false;
        }
        if (player.isInJail()) {
            player.sendMessage(LanguageEngine.getMsg("registering_jail"));
            return false;
        }
        if (player.isInSiege()) {
            player.sendMessage(LanguageEngine.getMsg("registering_siege"));
            return false;
        }
        if (player.isInDuel()) {
            player.sendMessage(LanguageEngine.getMsg("registering_duel"));
            return false;
        }
        if (player.isOlympiadRegistered() || player.isInOlympiadMode() || player.isInOlympiad()) {
            player.sendMessage(LanguageEngine.getMsg("registering_olympiad"));
            return false;
        }
        if (player.getKarma() > 0) {
            player.sendMessage(LanguageEngine.getMsg("registering_karma"));
            return false;
        }
        if (player.isCursedWeaponEquipped()) {
            player.sendMessage(LanguageEngine.getMsg("registering_cursedWeapon"));
            return false;
        }
        if (player.isInStoreMode()) {
            player.sendMessage(LanguageEngine.getMsg("registering_storemode"));
            return false;
        }
        return true;
    }

    public boolean isInEvent(CharacterData cha) {
        if (getCurrentMainEvent() != null) {
            return getCurrentMainEvent().isInEvent(cha);
        }
        return false;
    }

    public boolean allowDie(CharacterData cha, CharacterData killer) {
        if (getCurrentMainEvent() != null) {
            return getCurrentMainEvent().allowKill(cha, killer);
        }
        return true;
    }

    public void onDamageGive(CharacterData cha, CharacterData attacker, int damage, boolean isDOT) {
        if (getCurrentMainEvent() != null) {
            getCurrentMainEvent().onDamageGive(attacker, cha, damage, isDOT);
        }
    }

    public boolean onAttack(CharacterData cha, CharacterData target) {
        if (getCurrentMainEvent() != null) {
            return getCurrentMainEvent().onAttack(cha, target);
        }
        return true;
    }

    public boolean tryVoicedCommand(PlayerEventInfo player, String text) {
        if (player != null && ALLOW_VOICE_COMMANDS) {
            if (text.equalsIgnoreCase(REGISTER_VOICE_COMMAND)) {
                getInstance().getMainEventManager().registerPlayer(player);
                return true;
            }
            if (text.equalsIgnoreCase(UNREGISTER_VOICE_COMMAND)) {
                getInstance().getMainEventManager().unregisterPlayer(player, false);
                return true;
            }
            if (text.equalsIgnoreCase(".suicide")) {
                if (player.isInEvent()) {
                    player.sendMessage("You are being suicided.");
                    player.doDie();
                    return true;
                }
            }
        }
        return false;
    }

    public void removeEventSkills(PlayerEventInfo player) {
        for (SkillData sk : player.getSkills()) {
            if (sk.getId() >= 35000 && sk.getId() <= 35099) {
                player.removeBuff(sk.getId());
                player.removeSkill(sk.getId());
            }
        }
    }

    public void onPlayerLogin(PlayerEventInfo player) {
        removeEventSkills(player);
        EventStatsManager.getInstance().onLogin(player);
        DisconnectedPlayerData data = null;
        for (Map.Entry<DisconnectedPlayerData, Long> e : this._disconnectedPlayers.entrySet()) {
            if (((DisconnectedPlayerData) e.getKey())._player.getPlayersId() == player.getPlayersId()) {
                data = e.getKey();
                this._disconnectedPlayers.remove(e.getKey());
                break;
            }
        }
        if (data != null) {
            DisconnectedPlayerData fData = data;
            EventGame event = data._event;
            if (event != null) {
                CallBack.getInstance().getOut().scheduleGeneral(() -> event.addDisconnectedPlayer(player, fData), 1500L);
            }
        }
    }

    public void addDisconnectedPlayer(PlayerEventInfo player, EventTeam team, EventPlayerData d, EventGame event) {
        long time = System.currentTimeMillis();
        DisconnectedPlayerData data = new DisconnectedPlayerData(player, event, d, team, time, player.getInstanceId());
        this._disconnectedPlayers.put(data, Long.valueOf(time));
    }

    public void clearDisconnectedPlayers() {
        this._disconnectedPlayers.clear();
    }

    public void spectateGame(PlayerEventInfo player, EventType event, int modeId, int gameId) {
        MiniEventManager manager = getMiniEvent(event, modeId);
        if (manager == null) {
            player.sendStaticPacket();
            return;
        }
        MiniEventGame game = null;
        for (MiniEventGame g : manager.getActiveGames()) {
            if (g.getGameId() == gameId) {
                game = g;
                break;
            }
        }
        if (game == null) {
            player.sendMessage(LanguageEngine.getMsg("observing_gameEnded"));
            return;
        }
        if (!canRegister(player)) {
            player.sendMessage(LanguageEngine.getMsg("observing_cant"));
            return;
        }
        if (player.isRegistered()) {
            player.sendMessage(LanguageEngine.getMsg("observing_alreadyRegistered"));
            return;
        }
        CallBack.getInstance().getPlayerBase().addInfo(player);
        player.initOrigInfo();
        game.addSpectator(player);
    }

    public void removePlayerFromObserverMode(PlayerEventInfo pi) {
        MiniEventGame game = pi.getActiveGame();
        if (game == null) {
            return;
        }
        game.removeSpectator(pi, false);
    }

    public String getDarkColorForHtml(int teamId) {
        switch (teamId) {
            case 1:
                return "7C8194";
            case 2:
                return "987878";
            case 3:
                return "868F81";
            case 4:
                return "937D8D";
            case 5:
                return "93937D";
            case 6:
                return "D2934D";
            case 7:
                return "3EC1C1";
            case 8:
                return "D696D1";
            case 9:
                return "9B7957";
            case 10:
                return "949494";
        }
        return "8f8f8f";
    }

    public String getTeamColorForHtml(int teamId) {
        switch (teamId) {
            case 1:
                return "5083CF";
            case 2:
                return "D04F4F";
            case 3:
                return "56C965";
            case 4:
                return "9F52CD";
            case 5:
                return "DAC73D";
            case 6:
                return "D2934D";
            case 7:
                return "3EC1C1";
            case 8:
                return "D696D1";
            case 9:
                return "9B7957";
            case 10:
                return "949494";
        }
        return "FFFFFF";
    }

    public int getTeamColorForName(int teamId) {
        switch (teamId) {
            case 1:
                return 13599568;
            case 2:
                return 5197776;
            case 3:
                return 6670678;
            case 4:
                return 13456031;
            case 5:
                return 4048858;
            case 6:
                return 5084114;
            case 7:
                return 12697918;
            case 8:
                return 13735638;
            case 9:
                return 5732763;
            case 10:
                return 9737364;
        }
        return 0;
    }

    public String getTeamName(int teamId) {
        switch (teamId) {
            case 1:
                return "Blue";
            case 2:
                return "Red";
            case 3:
                return "Green";
            case 4:
                return "Purple";
            case 5:
                return "Yellow";
            case 6:
                return "Orange";
            case 7:
                return "Teal";
            case 8:
                return "Pink";
            case 9:
                return "Brown";
            case 10:
                return "Grey";
        }
        return "No";
    }

    public void debug(String message) {
        SunriseLoader.debug(message);
    }

    public void debug(Exception e) {
        e.printStackTrace();
    }

    
    public MainEventManager getMainEventManager() {
        return this._mainManager;
    }

    private static class SingletonHolder {
        protected static final EventManager _instance = new EventManager();
    }

    public class AdminSunrise
            extends AdminCommandHandlerInstance {
        private final String[] ADMIN_COMMANDS = new String[]{"admin_event_manage"};

        public boolean useAdminCommand(String command, PlayerEventInfo activeChar) {
            if (command.startsWith("admin_event_manage")) {
                StringTokenizer st = new StringTokenizer(command);
                st.nextToken();
                if (!st.hasMoreTokens()) {
                    SunriseEvents.onAdminBypass(activeChar, "menu");
                } else {
                    SunriseEvents.onAdminBypass(activeChar, command.substring(19));
                }
            }
            return true;
        }

        public String[] getAdminCommandList() {
            return this.ADMIN_COMMANDS;
        }
    }

    public class DisconnectedPlayerData {
        protected final PlayerEventInfo _player;
        protected final EventGame _event;
        private final EventPlayerData _data;
        private final EventTeam _team;
        private final long _time;
        private final int _instance;

        public DisconnectedPlayerData(PlayerEventInfo player, EventGame event, EventPlayerData data, EventTeam team, long time, int instance) {
            this._time = time;
            this._player = player;
            this._data = data;
            this._team = team;
            this._event = event;
            this._instance = instance;
        }

        public PlayerEventInfo getPlayer() {
            return this._player;
        }

        public EventGame getEvent() {
            return this._event;
        }

        public EventTeam getTeam() {
            return this._team;
        }

        public EventPlayerData getPlayerData() {
            return this._data;
        }

        public long getTime() {
            return this._time;
        }

        public int getInstance() {
            return this._instance;
        }
    }
}


