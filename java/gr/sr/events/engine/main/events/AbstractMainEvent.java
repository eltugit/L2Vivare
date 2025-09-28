package gr.sr.events.engine.main.events;

import gr.sr.events.Configurable;
import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.*;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.html.EventHtmlManager;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;
import gr.sr.l2j.ClassType;
import l2r.gameserver.model.skills.L2Skill;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class AbstractMainEvent
        extends Event
        implements Configurable, EventGame {
    public MainEventManager _manager;
    protected Map<SpawnType, String> _spawnTypes = new ConcurrentHashMap<>();
    protected InstanceData[] _instances;
    protected int _runningInstances;
    protected String _htmlDescription;
    protected Map<Integer, Map<Integer, EventTeam>> _teams = new ConcurrentHashMap<>();
    protected Map<Integer, List<FenceData>> _fences;
    protected Map<Integer, List<NpcData>> _npcs;
    protected List<Integer> _rewardedInstances = new LinkedList<>();
    protected String scorebarText;
    protected Map<MainEventInstanceType, List<PlayerEventInfo>> _tempPlayers = new ConcurrentHashMap<>();
    private final List<String> _configCategories = new LinkedList<>();
    protected final Map<String, ConfigModel> _configs = new ConcurrentHashMap<>();
    private final Map<String, ConfigModel> _mapConfigs = new ConcurrentHashMap<>();
    protected final Map<String, ConfigModel> _instanceTypeConfigs = new ConcurrentHashMap<>();
    protected RewardPosition[] _rewardTypes = null;
    protected WaweRespawnScheduler _waweScheduler;
    protected List<PlayerEventInfo> _spectators;
    protected boolean _allowScoreBar;
    protected boolean _allowSchemeBuffer;
    protected boolean _allowNoblesOnRess;
    protected boolean _removeBuffsOnEnd;
    protected boolean _allowSummons;
    protected boolean _allowPets;
    protected boolean _hideTitles;
    protected boolean _removePartiesOnStart;
    protected boolean _rejoinEventAfterDisconnect;
    protected boolean _removeWarningAfterReconnect;
    protected boolean _enableRadar;
    protected int _countOfShownTopPlayers;
    private int firstRegisteredRewardCount;
    private String firstRegisteredRewardType;
    private boolean _firstBlood;
    protected PlayerEventInfo _firstBloodPlayer;
    private int _afkHalfReward;
    private int _afkNoReward;
    private final Map<Integer, MainEventInstanceType> _types = new ConcurrentHashMap<>();
    private int[] notAllovedSkillls;
    protected int[] notAllovedItems;
    private int[] setOffensiveSkills;
    private int[] setNotOffensiveSkills;
    private int[] setNeutralSkills;
    private final List<PlayerEventInfo> _firstRegistered = new LinkedList<>();
    private final Object firstBloodLock;

    public AbstractMainEvent(EventType type, MainEventManager manager) {
        super(type);
        this._htmlDescription = null;
        this._rewardTypes = null;
        this.firstBloodLock = new Object();
        this._manager = manager;
        this._teams.clear();
        this._rewardedInstances.clear();
        this._spawnTypes.clear();
        this._spawnTypes.put(SpawnType.Regular, "Defines where the players will be spawned.");
        this._spawnTypes.put(SpawnType.Buffer, "Defines where the buffer(s) will be spawned.");
        this._spawnTypes.put(SpawnType.Fence, "Defines where fences will be spawned.");
        this._configCategories.clear();
        this._configs.clear();
        this._mapConfigs.clear();
        this._instanceTypeConfigs.clear();
        loadConfigs();
        this._types.clear();
    }

    public void loadConfigs() {
        addConfig(new ConfigModel("allowScreenScoreBar", "true", "True to allow the screen score bar, showing mostly scores for all teams and time left till the event ends.", ConfigModel.InputType.Boolean));
        if (!getEventType().isFFAEvent()) {
            ConfigModel divideMethod = new ConfigModel("divideToTeamsMethod", "LevelOnly", "The method used to divide the players into the teams on start of the event. All following methods try to put similar count of healers to all teams.<br1><font color=LEVEL>LevelOnly</font> sorts players by their level and then divides them into the teams (eg. Player1 (lvl85) to teamA, Player2(level84) to teamB, Player3(lvl81) to teamA, Player4(lvl75) to teamB, Player5(lvl70) to teamA,...)<br1><font color=LEVEL>PvPsAndLevel</font>: in addition to sorting by level, this method's main sorting factor are player's PvP kills. The rest of dividing procedure is same as for LevelsOnly. Useful for PvP servers, where level doesn't matter much.<br1>", ConfigModel.InputType.Enum);
            divideMethod.addEnumOptions(new String[]{"LevelOnly", "PvPsAndLevel"});
            addConfig(divideMethod);
            addConfig(new ConfigModel("balanceHealersInTeams", "true", "Put true if you want the engine to try to balance the count of healers in all teams (in all teams same healers count), making it as similar as possible.", ConfigModel.InputType.Boolean));
            addConfig(new ConfigModel("balanceDominatorsInTeams", "true", "Put true if you want the engine to try to balance the count of dominators in all teams (in all teams same dominators count), making it as similar as possible.", ConfigModel.InputType.Boolean));
            addConfig(new ConfigModel("balanceTanksInTeams", "true", "Put true if you want the engine to try to balance the count of tanks in all teams (in all teams same tanks count), making it as similar as possible.", ConfigModel.InputType.Boolean));
        } else {
            addConfig(new ConfigModel("announcedTopPlayersCount", "5", "You can specify the count of top players, that will be announced (in chat) in the end of the event."));
        }
        addConfig(new ConfigModel("runTime", "20", "The run time of this event, launched automatically by the scheduler. Max value globally for all events is 120 minutes. In minutes!"));
        addConfig(new ConfigModel("minLvl", "-1", "Minimum level for players participating the event (playerLevel >= value)."));
        addConfig(new ConfigModel("maxLvl", "100", "Maximum level for players participating the event (playerLevel <= value)."));
        addConfig(new ConfigModel("minPlayers", "2", "The minimum count of players required to start one instance of the event."));
        addConfig(new ConfigModel("maxPlayers", "-1", "The maximum count of players possible to play in the event. Put -1 to make it unlimited."));
        addConfig(new ConfigModel("removeBufsOnEnd", "true", "Put true to make that the buffs are removed from all players when the event ends (or gets aborted).", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("removePartiesOnStart", "false", "Put true if you want that when the event starts, to automatically delete all parties, that had been created BEFORE the event started.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("rejoinAfterDisconnect", "true", "When a player is on event and disconnects from the server, this gives <font color=7f7f7f>(if set on true)</font> him the opportunity to get back to the event if he relogins. The engine will simply wait if he logins again, and then teleport him back to the event (to his previous team). Sometimes it can happen that, for example, the whole team disconnects and the event is aborted, so then the engine will not teleport the player back to the event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("removeWarningAfterRejoin", "true", "Works if <font color=LEVEL>rejoinAfterDisconnect = true</font>. When a player successfully re-joins his previous event after he disconnected from server and then logged in again, this feature will remove the warning point which he received when he disconnected. Remember that if a player has a configurable count of warnings (by default 3), he is unable to participate in any event. Warnings decrease by 1 every day.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("playersInInstance", "-1", "This config currently has no use ;)."));
        addConfig(new ConfigModel("allowPotions", "false", "Specify if you want to allow players using potions in the event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("allowSummons", "true", "Put false if you want to disable summons on this event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("allowPets", "true", "Put false if you want to disable pets on this event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("allowHealers", "true", "Put false if you want to permit healer classes to register to the event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("hideTitles", "false", "Put true to disable titles containing player's event stats.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("removeBuffsOnStart", "true", "If 'true', all buffs will be removed from players on first teleport to the event.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("removeBuffsOnRespawn", "false", "If 'true', all buffs will be removed from players when they respawn. Useful for certain servers.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("notAllowedSkills", "", "Put here skills that won't be aviable for use in this event <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        addConfig(new ConfigModel("notAllowedItems", "", "Put here items that won't be aviable for use in this event <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        addConfig(new ConfigModel("enableRadar", "true", "Enable/disable the quest-like radar for players. It will show an arrow above player's head and point him to a RADAR type spawn of his team. Useful for example when you create a RADAR spawn right next to enemy team's flag (it will show all players from the one team where is the flag they need to capture). Works only if the active map contains a RADAR spawn (and spawn's teamID must be > 0).", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("dualboxCheck", "true", "You can enable/disable the registration dualbox check here.", ConfigModel.InputType.Boolean));
        addConfig(new ConfigModel("maxPlayersPerIp", "1", "If the 'dualboxCheck' config is enabled, you can specify here how many players with the same IP are allowed to be in the event."));
        addConfig(new ConfigModel("afkHalfReward", "120", "The time (in seconds) the player must be AFK to lower his reward (in the end of the event) by 50%. The AFK counter starts counting the time spent AFK after <font color=LEVEL>afkWarningDelay</font> + <font color=LEVEL>afkKickDelay</font> miliseconds (these two are Global configs) of idling (not clicking, not moving, not doing anything). Write 0 to disable this feature."));
        addConfig(new ConfigModel("afkNoReward", "300", "The time (in seconds) the player must be AFK to receive no reward in the end of the event.The AFK counter starts counting the time spent AFK after <font color=LEVEL>afkWarningDelay</font> + <font color=LEVEL>afkKickDelay</font> miliseconds (these two are Global configs) of idling (not clicking, not moving, not doing anything). Write 0 to disable this feature."));
        addConfig(new ConfigModel("firstRegisteredRewardCount", "10", "If you have specified a 'FirstRegisteredReward' reward, you can define here how many first registered players will be rewarded in the end of the event."));
        addConfig((new ConfigModel("firstRegisteredRewardType", "WinnersOnly", "Select here who will be rewarded with the 'FirstRegisteredReward' reward in the end of the event.", ConfigModel.InputType.Enum)).addEnumOptions(new String[]{"WinnersOnly", "All"}));
        addConfig(new ConfigModel("countOfShownTopPlayers", "10", "Count of players shown in the Top-scorers list in the community board. Better not to use high values. If you don't want to use this feature (not recommended - ugly HTML), put 0."));
        addInstanceTypeConfig(new ConfigModel("strenghtRate", "5", "Every instance has it's rate. This rate determines how 'strong' the players are inside. Strenght rate is used in some engine's calculations. Check out other configs. <font color=B46F6B>Values MUST be within 1-10. Setting it more causes problems.</font>"));
        addInstanceTypeConfig(new ConfigModel("minLvl", "-1", "Min level (for players) for this instance."));
        addInstanceTypeConfig(new ConfigModel("maxLvl", "100", "Max level (for players) for this instance."));
        addInstanceTypeConfig(new ConfigModel("minPvps", "0", "Min PvP points count to play in this instance."));
        addInstanceTypeConfig(new ConfigModel("maxPvps", "-1", "Max PvP points count to play in this instance. Put -1 to make it infinity."));
        addInstanceTypeConfig(new ConfigModel("minPlayers", "2", "Count of players required to start this instance. If there's less players, then the instance tries to divide it's players to stronger instances (check out config <font color=LEVEL>joinStrongerInstIfNeeded</font>) and if it doesn't success (the config is set to false or all possible stronger instances are full), it will unregister the players from the event. Check out other configs related to "));
        addInstanceTypeConfig(new ConfigModel("joinStrongerInstIfNeeded", "False", "If there are not enought players needed for this instance to start (as specified in <font color=LEVEL>minPlayers</font> config), the instance will try to divide it's players <font color=7f7f7f>(players, that CAN'T join any other instance - cuz they either don't meet their criteria or the instances are full already)</font> to stronger instances (if they aren't full yet; level, pvp, equip and other checks are not applied in this case).", ConfigModel.InputType.Boolean));
        addInstanceTypeConfig(new ConfigModel("joinStrongerInstMaxDiff", "2", "If <font color=LEVEL>joinStrongerInstIfNeeded</font> is enabled, this specifies the maximum allowed difference between strength rate of both instances (where <font color=ac9887>the weaker instance</font> with not enought players divides it's players to <font color=ac9887>a stronger instance</font>)."));
        addInstanceTypeConfig(new ConfigModel("maxPlayers", "-1", "Max players ammount aviable for this instance. Put -1 to make it infinity."));
    }

    public void startRegistration() {
        this._tempPlayers.clear();
        this._firstRegistered.clear();
        this.firstRegisteredRewardCount = getInt("firstRegisteredRewardCount");
        this.firstRegisteredRewardType = getString("firstRegisteredRewardType");
        if (SunriseLoader.detailedDebug) {
            print("AbstractMainEvent: startRegistration() done");
        }
    }

    public void initMap() {
        try {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: starting initMap()");
            }
            this._fences = new ConcurrentHashMap<Integer, List<FenceData>>();
            this._npcs = new ConcurrentHashMap<Integer, List<NpcData>>();
            final EventMap map = this._manager.getMap();
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: init map - " + map.getMapName());
            }
            for (final InstanceData instance : this._instances) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: initmap iterating instance " + instance.getId());
                }
                this._fences.put(instance.getId(), new LinkedList<FenceData>());
                for (final EventSpawn spawn : map.getSpawns(-1, SpawnType.Fence)) {
                    final FenceData fence = CallBack.getInstance().getOut().createFence(2, spawn.getFenceWidth(), spawn.getFenceLength(), spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), map.getGlobalId());
                    this._fences.get(instance.getId()).add(fence);
                }
                CallBack.getInstance().getOut().spawnFences(this._fences.get(instance.getId()), instance.getId());
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: initmap iterating instance spawned fences");
                }
                this._npcs.put(instance.getId(), new LinkedList<NpcData>());
                for (final EventSpawn spawn : map.getSpawns(-1, SpawnType.Npc)) {
                    if (spawn.getNpcId() != -1) {
                        final NpcData npc = new NpcTemplateData(spawn.getNpcId()).doSpawn(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 1, instance.getId());
                        this._npcs.get(instance.getId()).add(npc);
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: initmap iterating instance spawned npcs");
                }
                final int mapGuardId = EventConfig.getInstance().getGlobalConfigInt("mapGuardNpcId");
                if (mapGuardId != -1) {
                    for (final EventSpawn spawn2 : map.getSpawns(-1, SpawnType.MapGuard)) {
                        final NpcData npc2 = new NpcTemplateData(mapGuardId).doSpawn(spawn2.getLoc().getX(), spawn2.getLoc().getY(), spawn2.getLoc().getZ(), 1, instance.getId());
                        this._npcs.get(instance.getId()).add(npc2);
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: initmap iterating instance spawned map guards");
                }
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: initmap finished");
            }
        }
        catch (NullPointerException e) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: error on initMap() " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
            SunriseLoader.debug("Error on initMap()", Level.WARNING);
            e.printStackTrace();
        }
    }


    public void cleanMap(final int instanceId) {
        try {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: starting cleanmap(), instanceId " + instanceId);
            }
            if (this._instances != null) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: instances are not null");
                }
                for (final InstanceData instance : this._instances) {
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: iterating instance " + instance.getId());
                    }
                    if (instanceId == 0 || instance.getId() == instanceId) {
                        if (this._fences != null && this._fences.containsKey(instance.getId())) {
                            CallBack.getInstance().getOut().unspawnFences(this._fences.get(instance.getId()));
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: instance + " + instance.getId() + ", fences deleted");
                        }
                        if (this._npcs != null && this._npcs.containsKey(instance.getId())) {
                            for (final NpcData npc : this._npcs.get(instance.getId())) {
                                npc.deleteMe();
                            }
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: instance + " + instance.getId() + ", npcs deleted");
                        }
                        if (this._fences != null) {
                            this._fences.remove(instance.getId());
                        }
                        if (this._npcs != null) {
                            this._npcs.remove(instance.getId());
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: instance + " + instance.getId() + " cleaned.");
                        }
                    }
                }
            }
            if (instanceId == 0) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: set npcs and fences to null (instanceId = 0)");
                }
                this._npcs = null;
                this._fences = null;
            }
            else if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: cannot set npcs and fences to null yet, instanceId != 0");
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void initEvent() {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: initEvent starting");
        }
        if (this._rewardTypes == null) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: event " + this.getEventName() + " has not set up any _rewardTypes");
            }
            this.debug("Event " + this.getEventName() + " has not set up _rewardTypes. You've propably forgotten to call 'setRewardTypes()' in event's constructor.");
        }
        this._firstBlood = false;
        this._spectators = new LinkedList<PlayerEventInfo>();
        if (!this._rewardedInstances.isEmpty()) {
            this._rewardedInstances.clear();
        }
        Collections.sort(this._manager.getMap().getSpawns(), EventMap.compareByIdAsc);
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: spawns sorted, instances cleaned");
        }
        this._afkHalfReward = this.getInt("afkHalfReward");
        this._afkNoReward = this.getInt("afkNoReward");
        this._allowScoreBar = this.getBoolean("allowScreenScoreBar");
        this._allowSchemeBuffer = EventConfig.getInstance().getGlobalConfigBoolean("eventSchemeBuffer");
        this._allowNoblesOnRess = EventConfig.getInstance().getGlobalConfigBoolean("eventNoblessOnRess");
        this._removeBuffsOnEnd = this.getBoolean("removeBufsOnEnd");
        this._allowSummons = this.getBoolean("allowSummons");
        this._allowPets = this.getBoolean("allowPets");
        this._hideTitles = this.getBoolean("hideTitles");
        this._removePartiesOnStart = this.getBoolean("removePartiesOnStart");
        this._countOfShownTopPlayers = this.getInt("countOfShownTopPlayers");
        this._rejoinEventAfterDisconnect = this.getBoolean("rejoinAfterDisconnect");
        this._removeWarningAfterReconnect = this.getBoolean("removeWarningAfterRejoin");
        this._enableRadar = this.getBoolean("enableRadar");
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: scorebar - " + this._allowScoreBar + ", scheme buffer = " + this._allowSchemeBuffer);
        }
        if (!this.getString("notAllowedItems").equals("")) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: loading not allowed items");
            }
            final String[] splits = this.getString("notAllowedItems").split(",");
            this.notAllovedItems = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.notAllovedItems[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.notAllovedItems);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: not allowed items = " + this.notAllovedItems.toString());
                }
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'notAllowedItems' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while loading not allowed items " + SunriseLoader.getTraceString(e.getStackTrace()));
                }
            }
        }
        else if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: no not allowed items specified!");
        }
        if (!this.getString("notAllowedSkills").equals("")) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: loading not allowed skills!");
            }
            final String[] splits = this.getString("notAllowedSkills").split(",");
            this.notAllovedSkillls = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.notAllovedSkillls[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.notAllovedSkillls);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: not allowed skills = " + this.notAllovedSkillls.toString());
                }
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'notAllowedSkills' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while loading not allowed skills " + SunriseLoader.getTraceString(e.getStackTrace()));
                }
            }
        }
        else if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: no not allowed skills specified!");
        }
        this.loadOverridenSkillsParameters();
        this._firstBloodPlayer = null;
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: initEvent finished for AbstractMainEvent()");
        }
        this.dumpConfigs();
    }


    private void loadOverridenSkillsParameters() {
        String s = EventConfig.getInstance().getGlobalConfigValue("setOffensiveSkills");
        if (s != null && s.length() > 0) {
            try {
                final String[] splits = s.split(";");
                this.setOffensiveSkills = new int[splits.length];
                try {
                    for (int i = 0; i < splits.length; ++i) {
                        this.setOffensiveSkills[i] = Integer.parseInt(splits[i]);
                    }
                    Arrays.sort(this.setOffensiveSkills);
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: set offensive skills = " + this.setOffensiveSkills.toString());
                    }
                }
                catch (Exception e) {
                    SunriseLoader.debug("Error while loading GLOBAL config 'setOffensiveSkills' in event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
                }
            }
            catch (Exception e2) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setOffensiveSkills' in event " + this.getEventName() + " - " + e2.toString(), Level.SEVERE);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while loading 'setOffensiveSkills' GLOBAL config " + SunriseLoader.getTraceString(e2.getStackTrace()));
                }
            }
        }
        s = EventConfig.getInstance().getGlobalConfigValue("setNotOffensiveSkills");
        if (s != null && s.length() > 0) {
            try {
                final String[] splits = s.split(";");
                this.setNotOffensiveSkills = new int[splits.length];
                try {
                    for (int i = 0; i < splits.length; ++i) {
                        this.setNotOffensiveSkills[i] = Integer.parseInt(splits[i]);
                    }
                    Arrays.sort(this.setNotOffensiveSkills);
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: set not offensive skills = " + this.setNotOffensiveSkills.toString());
                    }
                }
                catch (Exception e) {
                    SunriseLoader.debug("Error while loading GLOBAL config 'setNotOffensiveSkills' in event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
                }
            }
            catch (Exception e2) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setNotOffensiveSkills' in event " + this.getEventName() + " - " + e2.toString(), Level.SEVERE);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while loading 'setNotOffensiveSkills' GLOBAL config " + SunriseLoader.getTraceString(e2.getStackTrace()));
                }
            }
        }
        s = EventConfig.getInstance().getGlobalConfigValue("setNeutralSkills");
        if (s != null && s.length() > 0) {
            try {
                final String[] splits = s.split(";");
                this.setNeutralSkills = new int[splits.length];
                try {
                    for (int i = 0; i < splits.length; ++i) {
                        this.setNeutralSkills[i] = Integer.parseInt(splits[i]);
                    }
                    Arrays.sort(this.setNeutralSkills);
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: set neutral skills = " + this.setNeutralSkills.toString());
                    }
                }
                catch (Exception e) {
                    SunriseLoader.debug("Error while loading GLOBAL config 'setNeutralSkills' in event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
                }
            }
            catch (Exception e2) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setNeutralSkills' in event " + this.getEventName() + " - " + e2.toString(), Level.SEVERE);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while loading 'setNeutralSkills' GLOBAL config " + SunriseLoader.getTraceString(e2.getStackTrace()));
                }
            }
        }
    }

    @Override
    public int isSkillOffensive(final SkillData skill) {
        if (this.setOffensiveSkills != null && Arrays.binarySearch(this.setOffensiveSkills, skill.getId()) >= 0) {
            return 1;
        }
        if (this.setNotOffensiveSkills != null && Arrays.binarySearch(this.setNotOffensiveSkills, skill.getId()) >= 0) {
            return 0;
        }
        return -1;
    }


    @Override
    public boolean isSkillNeutral(final SkillData skill) {
        return this.setNeutralSkills != null && Arrays.binarySearch(this.setNeutralSkills, skill.getId()) >= 0;
    }

    private void dumpConfigs() {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: dumping configs START =====================");
        }
        for (final Map.Entry<String, ConfigModel> e : this._configs.entrySet()) {
            if (SunriseLoader.detailedDebug) {
                this.print(e.getKey() + " - " + e.getValue().getValue());
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: dumping configs END ====================");
        }
    }


    protected void createTeams(final int count, final int instanceId) {
        try {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: creating " + count + " teams for instanceId " + instanceId);
            }
            switch (count) {
                case 1: {
                    this.createNewTeam(instanceId, 1, LanguageEngine.getMsg("team_ffaevent"));
                    break;
                }
                case 2: {
                    this.createNewTeam(instanceId, 1, LanguageEngine.getMsg("team_blue"), LanguageEngine.getMsg("team_fullname_blue"));
                    this.createNewTeam(instanceId, 2, LanguageEngine.getMsg("team_red"), LanguageEngine.getMsg("team_fullname_red"));
                    break;
                }
                case 3: {
                    this.createNewTeam(instanceId, 1, LanguageEngine.getMsg("team_blue"), LanguageEngine.getMsg("team_fullname_blue"));
                    this.createNewTeam(instanceId, 2, LanguageEngine.getMsg("team_red"), LanguageEngine.getMsg("team_fullname_red"));
                    this.createNewTeam(instanceId, 3, LanguageEngine.getMsg("team_green"), LanguageEngine.getMsg("team_fullname_green"));
                    break;
                }
                case 4: {
                    this.createNewTeam(instanceId, 1, LanguageEngine.getMsg("team_blue"), LanguageEngine.getMsg("team_fullname_blue"));
                    this.createNewTeam(instanceId, 2, LanguageEngine.getMsg("team_red"), LanguageEngine.getMsg("team_fullname_red"));
                    this.createNewTeam(instanceId, 3, LanguageEngine.getMsg("team_green"), LanguageEngine.getMsg("team_fullname_green"));
                    this.createNewTeam(instanceId, 4, LanguageEngine.getMsg("team_purple"), LanguageEngine.getMsg("team_fullname_purple"));
                    break;
                }
                case 5: {
                    this.createNewTeam(instanceId, 1, LanguageEngine.getMsg("team_blue"), LanguageEngine.getMsg("team_fullname_blue"));
                    this.createNewTeam(instanceId, 2, LanguageEngine.getMsg("team_red"), LanguageEngine.getMsg("team_fullname_red"));
                    this.createNewTeam(instanceId, 3, LanguageEngine.getMsg("team_green"), LanguageEngine.getMsg("team_fullname_green"));
                    this.createNewTeam(instanceId, 4, LanguageEngine.getMsg("team_purple"), LanguageEngine.getMsg("team_fullname_purple"));
                    this.createNewTeam(instanceId, 5, LanguageEngine.getMsg("team_yellow"), LanguageEngine.getMsg("team_fullname_yellow"));
                    break;
                }
                default: {
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: the teams count is too high on event " + this.getEventName());
                    }
                    SunriseLoader.debug("The TEAMS COUNT is too high for event " + this.getEventName() + " - max value is 5!! The event will start with 5 teams.", Level.WARNING);
                    this.createTeams(5, instanceId);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createNewTeam(final int instanceId, final int id, final String name, final String fullName) {
        this._teams.get(instanceId).put(id, new EventTeam(id, name, fullName));
        if (SunriseLoader.detailedDebug) {
            this.print("... AbstractMainEvent: created new team for instanceId " + instanceId + ", id " + id + ", fullname " + fullName);
        }
    }

    protected void createNewTeam(final int instanceId, final int id, final String name) {
        this._teams.get(instanceId).put(id, new EventTeam(id, name));
        if (SunriseLoader.detailedDebug) {
            this.print("... AbstractMainEvent: created new team for instanceId " + instanceId + ", id " + id);
        }
    }

    public boolean canRegister(final PlayerEventInfo player) {
        if (!this.getBoolean("allowHealers") && player.isPriest()) {
            player.sendMessage("Healers are not allowed on the event.");
            return false;
        }
        if (!this.getBoolean("allowHealers") && player.isTank()) {
            player.sendMessage("Tank are not allowed on the event.");
            return false;
        }
        final int maxPlayers = this.getInt("maxPlayers");
        if (maxPlayers != -1 && this._manager.getPlayersCount() >= maxPlayers) {
            if (SunriseLoader.detailedDebug) {
                this.print("... registerPlayer() in AbstractMainEvent (canRegister()) for " + player.getPlayersName() + ", the event is full already! " + maxPlayers + "/" + this._manager.getPlayersCount());
            }
            player.sendMessage(LanguageEngine.getMsg("registering_full"));
            return false;
        }
        synchronized (this._tempPlayers) {
            for (final MainEventInstanceType instance : this._types.values()) {
                if (this.canJoinInstance(player, instance)) {
                    if (SunriseLoader.detailedDebug) {
                        this.print("... registerPlayer() in AbstractMainEvent (canRegister()) for " + player.getPlayersName() + " player CAN join instancetype " + instance.getId());
                    }
                    if (!this._tempPlayers.containsKey(instance)) {
                        this._tempPlayers.put(instance, new LinkedList<PlayerEventInfo>());
                    }
                    else {
                        final int max = instance.getConfigInt("maxPlayers");
                        if (max > -1 && this._tempPlayers.get(instance).size() >= max) {
                            if (!SunriseLoader.detailedDebug) {
                                continue;
                            }
                            this.print("... registerPlayer() in AbstractMainEvent (canRegister()) for " + player.getPlayersName() + " instance type " + instance.getId() + " is full already (max " + max + ")");
                            continue;
                        }
                    }
                    this._tempPlayers.get(instance).add(player);
                    if (SunriseLoader.detailedDebug) {
                        this.print("... registerPlayer() in AbstractMainEvent (canRegister()) for " + player.getPlayersName() + " registered to instance type " + instance.getId());
                    }
                    if (this._firstRegistered.size() < this.firstRegisteredRewardCount) {
                        this._firstRegistered.add(player);
                        if (this.firstRegisteredRewardType.equals("WinnersOnly")) {
                            player.sendMessage(LanguageEngine.getMsg("registered_first_type1", this.firstRegisteredRewardCount));
                        }
                        else {
                            player.sendMessage(LanguageEngine.getMsg("registered_first_type2", this.firstRegisteredRewardCount));
                        }
                    }
                    return true;
                }
                if (!SunriseLoader.detailedDebug) {
                    continue;
                }
                this.print("... registerPlayer() in AbstractMainEvent (canRegister()) for " + player.getPlayersName() + " player CANNOT join instancetype " + instance.getId());
            }
        }
        player.sendMessage(LanguageEngine.getMsg("registering_noInstance"));
        return false;
    }

    public void playerUnregistered(final PlayerEventInfo player) {
        synchronized (this._tempPlayers) {
            for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e : this._tempPlayers.entrySet()) {
                for (final PlayerEventInfo pi : e.getValue()) {
                    if (pi.getPlayersId() == player.getPlayersId()) {
                        this._tempPlayers.get(e.getKey()).remove(pi);
                        if (SunriseLoader.detailedDebug) {
                            this.print("... playerUnregistered player " + player.getPlayersName() + " removed from _tempPlayers");
                        }
                        return;
                    }
                }
            }
        }
        if (this._firstRegistered != null && this._firstRegistered.contains(player)) {
            this._firstRegistered.remove(player);
        }
        if (SunriseLoader.detailedDebug) {
            this.print("... palyerUnregistered couldn't remove player " + player.getPlayersName() + " from _tempPlayers");
        }
    }

    public boolean canStart() {
        return EventManager.getInstance().getMainEventManager().getPlayersCount() >= this.getInt("minPlayers");
    }

    protected void reorganizeInstances() {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: calling reorganizeInstance");
        }
        final List<MainEventInstanceType> sameStrenghtInstances = new LinkedList<MainEventInstanceType>();
        this.dumpTempPlayers();
        for (int currentStrenght = 1; currentStrenght <= 10; ++currentStrenght) {
            for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e : this._tempPlayers.entrySet()) {
                if (!this.isFull(e.getKey()) && e.getKey().getStrenghtRate() == currentStrenght) {
                    sameStrenghtInstances.add(e.getKey());
                }
            }
            Collections.sort(sameStrenghtInstances, (i1, i2) -> {
                int neededPlayers1 = i1.getConfigInt("minPlayers") - this._tempPlayers.get(i1).size();
                int neededPlayers2 = i2.getConfigInt("minPlayers") - this._tempPlayers.get(i2).size();
                return (neededPlayers1 < neededPlayers2) ? -1 : ((neededPlayers1 == neededPlayers2) ? 0 : 1);
            });
            this.reorganize(sameStrenghtInstances);
            sameStrenghtInstances.clear();
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: instances DONE reorganized!!");
        }
        this.dumpTempPlayers();
    }

    private void dumpTempPlayers() {
        if (SunriseLoader.detailedDebug) {
            this.print("***** AbstractMainEvent: STARTING tempPlayers dump");
        }
        try {
            for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e : this._tempPlayers.entrySet()) {
                if (SunriseLoader.detailedDebug) {
                    this.print("... ***** AbstractMainEvent: instance " + e.getKey().getName() + " (" + e.getKey().getId() + ") has " + e.getValue().size() + " players");
                }
            }
        }
        catch (Exception e2) {
            if (SunriseLoader.detailedDebug) {
                this.print("error while dumping temp players - " + SunriseLoader.getTraceString(e2.getStackTrace()));
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("***** AbstractMainEvent: ENDED tempPlayers dump");
        }
    }

    protected void reorganize(final List<MainEventInstanceType> instances) {
        for (final MainEventInstanceType instance : instances) {
            final MainEventInstanceType element = instance;
            if (this.hasEnoughtPlayers(instance)) {
                instances.remove(instance);
            }
            else {
                final int count = this._tempPlayers.get(instance).size();
                int toMove = instance.getConfigInt("minPlayers") - count;
                for (final MainEventInstanceType possibleInstance : instances) {
                    if (possibleInstance != instance) {
                        final int moved = this.movePlayers(instance, possibleInstance, toMove);
                        toMove -= moved;
                        if (toMove == 0) {
                            instances.remove(instance);
                            break;
                        }
                        if (toMove > 0) {}
                    }
                }
            }
        }
        if (!instances.isEmpty()) {
            int minPlayers = Integer.MAX_VALUE;
            MainEventInstanceType inst = null;
            for (final MainEventInstanceType instance2 : instances) {
                if (instance2.getConfigInt("minPlayers") < minPlayers) {
                    minPlayers = instance2.getConfigInt("minPlayers");
                    inst = instance2;
                }
            }
            for (final MainEventInstanceType instance2 : instances) {
                if (instance2 != inst) {
                    this.movePlayers(inst, instance2, -1);
                }
            }
            if (inst != null) {
                System.out.println("*** Done, instance " + inst.getName() + " has " + this._tempPlayers.get(inst).size() + " players.");
            }
            if (SunriseLoader.detailedDebug && inst != null) {
                this.print("AbstractMainEvent: reorganize() - instance " + inst.getName() + " has " + this._tempPlayers.get(inst).size() + " players");
            }
        }
    }
    protected int movePlayers(final MainEventInstanceType target, final MainEventInstanceType source, final int count) {
        if (count == 0) {
            return 0;
        }
        int moved = 0;
        for (final PlayerEventInfo player : this._tempPlayers.get(source)) {
            this._tempPlayers.get(target).add(player);
            this._tempPlayers.get(source).remove(player);
            ++moved;
            if (count != -1 && moved >= count) {
                break;
            }
        }
        return moved;
    }

    protected boolean isFull(final MainEventInstanceType instance) {
        return this._tempPlayers.get(instance).size() >= instance.getConfigInt("maxPlayers");
    }

    protected boolean hasEnoughtPlayers(final MainEventInstanceType instance) {
        return this._tempPlayers.get(instance).size() >= instance.getConfigInt("minPlayers");
    }

    protected boolean dividePlayers() {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: starting dividePlayers");
        }
        this.reorganizeInstances();
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: starting notEnoughtPlayersInstance operations");
        }
        final List<MainEventInstanceType> notEnoughtPlayersInstances = new LinkedList<MainEventInstanceType>();
        for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e : this._tempPlayers.entrySet()) {
            if (e.getValue().size() < e.getKey().getConfigInt("minPlayers")) {
                notEnoughtPlayersInstances.add(e.getKey());
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: notEnoughtPlayersInstances size = " + notEnoughtPlayersInstances.size());
        }
        final List<MainEventInstanceType> fixed = new LinkedList<MainEventInstanceType>();
        for (final MainEventInstanceType currentInstance : notEnoughtPlayersInstances) {
            final MainEventInstanceType element = currentInstance;
            if (currentInstance != null && !fixed.contains(currentInstance)) {
                final int strenght = currentInstance.getStrenghtRate();
                int playersCount = this._tempPlayers.get(currentInstance).size();
                final boolean joinStrongerInstIfNeeded = currentInstance.getConfigBoolean("joinStrongerInstIfNeeded");
                final int maxDiff = currentInstance.getConfigInt("joinStrongerInstMaxDiff");
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: iterating through notEnoughtInstances: " + currentInstance.getId() + " [" + currentInstance.getStrenghtRate() + "] - playersCount (" + playersCount + "), strenght (" + strenght + ")");
                }
                for (final MainEventInstanceType possibleInstance : notEnoughtPlayersInstances) {
                    if (possibleInstance != null && !fixed.contains(possibleInstance) && possibleInstance != currentInstance) {
                        playersCount = this._tempPlayers.get(currentInstance).size();
                        if (possibleInstance.getStrenghtRate() == strenght) {
                            if (this._tempPlayers.get(possibleInstance).size() + playersCount < possibleInstance.getConfigInt("minPlayers") || !SunriseLoader.detailedDebug) {
                                continue;
                            }
                            this.print("How could have this happened? (" + currentInstance.getName() + ", " + possibleInstance.getName() + ")");
                        }
                        else {
                            if (!joinStrongerInstIfNeeded || possibleInstance.getStrenghtRate() <= strenght || possibleInstance.getStrenghtRate() - strenght > maxDiff) {
                                continue;
                            }
                            if (SunriseLoader.detailedDebug) {
                                this.print("AbstractMainEvent: /// possible instance " + possibleInstance.getName() + "[" + possibleInstance.getStrenghtRate() + "] - playersCount (" + this._tempPlayers.get(possibleInstance).size() + "), strenght (" + possibleInstance.getStrenghtRate() + ")");
                            }
                            final int sumPlayers = this._tempPlayers.get(possibleInstance).size() + playersCount;
                            if (sumPlayers < possibleInstance.getConfigInt("minPlayers")) {
                                continue;
                            }
                            final int max = possibleInstance.getConfigInt("maxPlayers");
                            int toMove;
                            if (sumPlayers > max) {
                                toMove = max - this._tempPlayers.get(possibleInstance).size();
                            }
                            else {
                                toMove = this._tempPlayers.get(currentInstance).size();
                            }
                            if (SunriseLoader.detailedDebug) {
                                this.print("AbstractMainEvent: /*/*/ moving " + toMove + " players from " + currentInstance.getName() + " to " + possibleInstance.getName());
                            }
                            this.movePlayers(possibleInstance, currentInstance, toMove);
                            if (SunriseLoader.detailedDebug) {
                                this.print("AbstractMainEvent: /*/*/ size of " + possibleInstance.getName() + " is now " + this._tempPlayers.get(possibleInstance).size());
                            }
                            if (this._tempPlayers.get(possibleInstance).size() < possibleInstance.getConfigInt("minPlayers")) {
                                continue;
                            }
                            if (SunriseLoader.detailedDebug) {
                                this.print("AbstractMainEvent: /*/*/ instance " + possibleInstance.getName() + " removed from notEnoughtPlayersInstances.");
                            }
                            fixed.add(possibleInstance);
                        }
                    }
                }
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: reorganizing notEnoughtPlayers first part done");
        }
        this.dumpTempPlayers();
        for (final MainEventInstanceType currentInstance : notEnoughtPlayersInstances) {
            final MainEventInstanceType element = currentInstance;
            final int playersCount2 = this._tempPlayers.get(currentInstance).size();
            if (playersCount2 != 0) {
                final int strenght2 = currentInstance.getStrenghtRate();
                final boolean joinStrongerInstIfNeeded = currentInstance.getConfigBoolean("joinStrongerInstIfNeeded");
                final int maxDiff = currentInstance.getConfigInt("joinStrongerInstMaxDiff");
                for (final MainEventInstanceType fixedInstance : fixed) {
                    if (joinStrongerInstIfNeeded && fixedInstance.getStrenghtRate() > strenght2 && fixedInstance.getStrenghtRate() - strenght2 <= maxDiff) {
                        final int sumPlayers = this._tempPlayers.get(fixedInstance).size();
                        if (sumPlayers >= fixedInstance.getConfigInt("maxPlayers")) {
                            continue;
                        }
                        final int toMove2 = fixedInstance.getConfigInt("maxPlayers") - this._tempPlayers.get(fixedInstance).size();
                        this.movePlayers(fixedInstance, currentInstance, toMove2);
                    }
                }
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: reorganizing notEnoughtPlayers second part done");
        }
        this.dumpTempPlayers();
        int c = 0;
        for (final MainEventInstanceType toRemove : fixed) {
            notEnoughtPlayersInstances.remove(toRemove);
            ++c;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: fixed " + c + " notEnoughtPlayers instances");
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: starting tempPlayers reorganizations");
        }
        for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e2 : this._tempPlayers.entrySet()) {
            int playersCount3 = e2.getValue().size();
            if (playersCount3 != 0) {
                final int strenght3 = e2.getKey().getStrenghtRate();
                final boolean joinStrongerInstIfNeeded2 = e2.getKey().getConfigBoolean("joinStrongerInstIfNeeded");
                final int maxDiff2 = e2.getKey().getConfigInt("joinStrongerInstMaxDiff");
                if (this.hasEnoughtPlayers(e2.getKey())) {
                    continue;
                }
                while (playersCount3 > 0) {
                    final int temp = playersCount3;
                    for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> i : this._tempPlayers.entrySet()) {
                        if (playersCount3 <= 0) {
                            break;
                        }
                        if (!this.hasEnoughtPlayers(i.getKey()) || i.getKey().getStrenghtRate() != strenght3) {
                            continue;
                        }
                        final int canMove = i.getKey().getConfigInt("maxPlayers") - i.getValue().size();
                        if (canMove <= 0 || this.movePlayers(i.getKey(), e2.getKey(), 1) != 1) {
                            continue;
                        }
                        --playersCount3;
                    }
                    if (playersCount3 == temp) {
                        break;
                    }
                }
                if (playersCount3 == 0 || !joinStrongerInstIfNeeded2 || playersCount3 <= 0) {
                    continue;
                }
                final int temp = playersCount3;
                for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> i : this._tempPlayers.entrySet()) {
                    if (playersCount3 <= 0) {
                        break;
                    }
                    if (!this.hasEnoughtPlayers(i.getKey()) || i.getKey().getStrenghtRate() <= strenght3 || i.getKey().getStrenghtRate() - strenght3 > maxDiff2) {
                        continue;
                    }
                    final int canMove = i.getKey().getConfigInt("maxPlayers") - i.getValue().size();
                    if (canMove <= 0 || this.movePlayers(i.getKey(), e2.getKey(), 1) != 1) {
                        continue;
                    }
                    --playersCount3;
                }
                if (playersCount3 != temp) {
                    break;
                }
                continue;
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("* AbstractMainEvent: instances organizing FINISHED:");
        }
        this.dumpTempPlayers();
        for (final MainEventInstanceType inst : notEnoughtPlayersInstances) {
            int j = 0;
            for (final PlayerEventInfo player : this._tempPlayers.get(inst)) {
                player.screenMessage(LanguageEngine.getMsg("registering_notEnoughtPlayers"), this.getEventName(), true);
                this._manager.unregisterPlayer(player, true);
                ++j;
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: ... Not enought players for instance " + inst.getName() + " (" + this._tempPlayers.get(inst).size() + "), instance removed; " + j + " players unregistered.");
            }
            this._tempPlayers.remove(inst);
        }
        int availableInstances = 0;
        this._instances = new InstanceData[this._tempPlayers.size()];
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: dividing players into teams - instances count = " + this._tempPlayers.size());
        }
        for (final Map.Entry<MainEventInstanceType, List<PlayerEventInfo>> e3 : this._tempPlayers.entrySet()) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: STARTING event for instance: " + e3.getKey().getName());
            }
            final InstanceData instance = CallBack.getInstance().getOut().createInstance(e3.getKey().getName(), this._manager.getRunTime() * 1000 + 60000, 0, true);
            this._instances[availableInstances] = instance;
            e3.getKey().setInstance(instance);
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: ... created InstanceData, duration: " + (this._manager.getRunTime() * 1000 + 60000));
            }
            ++availableInstances;
            this._teams.put(instance.getId(), new ConcurrentHashMap<Integer, EventTeam>());
            final int teamsCount = this.initInstanceTeams(e3.getKey());
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: ... teamscount = " + teamsCount + "; DIVIDE to teams:");
            }
            this.dividePlayersToTeams(instance.getId(), e3.getValue(), teamsCount);
        }
        this._tempPlayers.clear();
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: available instances = " + availableInstances);
        }
        if (availableInstances == 0) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: event COULD NOT START due to lack of players in instances");
            }
            this.announce(LanguageEngine.getMsg("announce_noInstance"));
            this.clearEvent();
            return false;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: ... dividePlayers allowed event to start!");
        }
        for (final Map.Entry<Integer, Map<Integer, EventTeam>> k : this._teams.entrySet()) {
            CallbackManager.getInstance().eventStarts(k.getKey(), this.getEventType(), k.getValue().values());
            for (final EventTeam team : k.getValue().values()) {
                team.calcAverageLevel();
            }
        }
        return true;
    }

    protected void dividePlayersToTeams(final int instanceId, final List<PlayerEventInfo> players, final int teamsCount) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: /// dividingplayers to teams for INSTANCE " + instanceId);
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: /// players count = " + players.size());
        }
        if (!this.getEventType().isFFAEvent() && teamsCount > 1) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// team based event");
            }
            final String type = this.getString("divideToTeamsMethod");
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// using method: " + type);
            }
            Collections.sort(players, EventManager.getInstance().compareByLevels);
            if (type.startsWith("PvPs")) {
                Collections.sort(players, EventManager.getInstance().compareByPvps);
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// players sorted");
            }
            final Map<ClassType, List<PlayerEventInfo>> sortedPlayers = new LinkedHashMap<ClassType, List<PlayerEventInfo>>();
            for (final ClassType classType : ClassType.values()) {
                sortedPlayers.put(classType, new LinkedList<PlayerEventInfo>());
            }
            for (final PlayerEventInfo pi : players) {
                sortedPlayers.get(pi.getClassType()).add(pi);
            }
            for (final Map.Entry<ClassType, List<PlayerEventInfo>> te : sortedPlayers.entrySet()) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// ... " + te.getKey().toString() + " - " + te.getValue().size() + " players.");
                }
            }
            int teamId = 0;
            boolean reverseOrder = false;
            if (this.getBoolean("balanceHealersInTeams")) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// balancing healers in teams.");
                }
                while (sortedPlayers.get(ClassType.Priest).size() > 0) {
                    if (reverseOrder) {
                        --teamId;
                    }
                    else {
                        ++teamId;
                    }
                    if (teamId > teamsCount) {
                        teamId = teamsCount;
                        reverseOrder = true;
                    }
                    if (teamId < 1) {
                        teamId = 1;
                        reverseOrder = false;
                    }
                    final PlayerEventInfo player = sortedPlayers.get(ClassType.Priest).get(0);
                    sortedPlayers.get(ClassType.Priest).remove(player);
                    player.onEventStart(this);
                    this._teams.get(instanceId).get(teamId).addPlayer(player, true);
                }
            }
            if (this.getBoolean("balanceDominatorsInTeams")) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// balancing dominators in teams.");
                }
                while (sortedPlayers.get(ClassType.Dominator).size() > 0) {
                    if (reverseOrder) {
                        --teamId;
                    }
                    else {
                        ++teamId;
                    }
                    if (teamId > teamsCount) {
                        teamId = teamsCount;
                        reverseOrder = true;
                    }
                    if (teamId < 1) {
                        teamId = 1;
                        reverseOrder = false;
                    }
                    final PlayerEventInfo player = sortedPlayers.get(ClassType.Dominator).get(0);
                    sortedPlayers.get(ClassType.Dominator).remove(player);
                    player.onEventStart(this);
                    this._teams.get(instanceId).get(teamId).addPlayer(player, true);
                }
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// dominators and healers balanced into teams.");
            }
            for (final EventTeam team : this._teams.get(instanceId).values()) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// team " + team.getTeamName() + " has " + team.getPlayers().size() + " healers and dominators.");
                }
            }
            if (this.getBoolean("balanceTanksInTeams")) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// balancing tanks in teams.");
                }
                while (sortedPlayers.get(ClassType.Tank).size() > 0) {
                    if (reverseOrder) {
                        --teamId;
                    }
                    else {
                        ++teamId;
                    }
                    if (teamId > teamsCount) {
                        teamId = teamsCount;
                        reverseOrder = true;
                    }
                    if (teamId < 1) {
                        teamId = 1;
                        reverseOrder = false;
                    }
                    final PlayerEventInfo player = sortedPlayers.get(ClassType.Tank).get(0);
                    sortedPlayers.get(ClassType.Tank).remove(player);
                    player.onEventStart(this);
                    this._teams.get(instanceId).get(teamId).addPlayer(player, true);
                }
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// tanks balanced into teams.");
            }
            while (sortedPlayers.get(ClassType.Fighter).size() > 0) {
                if (reverseOrder) {
                    --teamId;
                }
                else {
                    ++teamId;
                }
                if (teamId > teamsCount) {
                    teamId = teamsCount;
                    reverseOrder = true;
                }
                if (teamId < 1) {
                    teamId = 1;
                    reverseOrder = false;
                }
                final PlayerEventInfo player = sortedPlayers.get(ClassType.Fighter).get(0);
                sortedPlayers.get(ClassType.Fighter).remove(player);
                player.onEventStart(this);
                this._teams.get(instanceId).get(teamId).addPlayer(player, true);
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// fighters balanced into teams.");
            }
            while (sortedPlayers.get(ClassType.Mystic).size() > 0) {
                if (reverseOrder) {
                    --teamId;
                }
                else {
                    ++teamId;
                }
                if (teamId > teamsCount) {
                    teamId = teamsCount;
                    reverseOrder = true;
                }
                if (teamId < 1) {
                    teamId = 1;
                    reverseOrder = false;
                }
                final PlayerEventInfo player = sortedPlayers.get(ClassType.Mystic).get(0);
                sortedPlayers.get(ClassType.Mystic).remove(player);
                player.onEventStart(this);
                this._teams.get(instanceId).get(teamId).addPlayer(player, true);
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// mages balanced into teams.");
            }
            teamId = 0;
            for (final Map.Entry<ClassType, List<PlayerEventInfo>> e : sortedPlayers.entrySet()) {
                for (final PlayerEventInfo pi2 : e.getValue()) {
                    int leastPlayers = Integer.MAX_VALUE;
                    for (final EventTeam team2 : this._teams.get(instanceId).values()) {
                        if (team2.getPlayers().size() < leastPlayers) {
                            leastPlayers = team2.getPlayers().size();
                            teamId = team2.getTeamId();
                        }
                    }
                    pi2.onEventStart(this);
                    this._teams.get(instanceId).get(teamId).addPlayer(pi2, true);
                }
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// players divided:");
            }
            for (final EventTeam team : this._teams.get(instanceId).values()) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// team " + team.getTeamName() + " has " + team.getPlayers().size() + " PLAYERS.");
                }
            }
        }
        else {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /// FFA event");
            }
            for (final PlayerEventInfo pi3 : players) {
                pi3.onEventStart(this);
                this._teams.get(instanceId).get(1).addPlayer(pi3, true);
            }
        }
    }

    public void createParties(final int partySize) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: CREATING PARTIES... ");
        }
        for (final Map.Entry<Integer, Map<Integer, EventTeam>> teams : this._teams.entrySet()) {
            if (SunriseLoader.detailedDebug) {
                this.print("* AbstractMainEvent: PROCESSING INSTANCE " + teams.getKey() + " (creating parties)");
            }
            for (final EventTeam team : teams.getValue().values()) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: / parties: processing team " + team.getTeamName());
                }
                int totalCount = 0;
                final Map<ClassType, List<PlayerEventInfo>> players = new LinkedHashMap<ClassType, List<PlayerEventInfo>>();
                for (final ClassType classType : ClassType.values()) {
                    players.put(classType, new LinkedList<PlayerEventInfo>());
                }
                for (final PlayerEventInfo player : team.getPlayers()) {
                    if (player.isOnline()) {
                        players.get(player.getClassType()).add(player);
                        ++totalCount;
                    }
                }
                for (final List<PlayerEventInfo> pls : players.values()) {
                    Collections.sort(pls, EventManager.getInstance().compareByLevels);
                }
                int healersCount = players.get(ClassType.Priest).size();
                int domisCount = players.get(ClassType.Dominator).size();
                int tanksCount = players.get(ClassType.Tank).size();
                final int partiesCount = (int)Math.ceil(totalCount / (double)partySize);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: ////// total count of players in the team " + totalCount + "; PARTIES COUNT " + partiesCount + "; healers count " + healersCount);
                }
                final List<PlayerEventInfo> toParty = new LinkedList<PlayerEventInfo>();
                int healersToGive = (int)Math.ceil(healersCount / (double)partiesCount);
                if (healersToGive == 0) {
                    healersToGive = 1;
                }
                int domisToGive = (int)Math.ceil(domisCount / (double)partiesCount);
                if (domisToGive == 0) {
                    domisToGive = 1;
                }
                int tanksToGive = (int)Math.ceil(tanksCount / (double)partiesCount);
                if (tanksToGive == 0) {
                    tanksToGive = 1;
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: ////// healersToGive to each party: " + healersToGive);
                    this.print("AbstractMainEvent: ////// domisToGive to each party: " + domisToGive);
                    this.print("AbstractMainEvent: ////// tanksToGive to each party: " + tanksToGive);
                }
                for (int i = 0; i < partiesCount; ++i) {
                    if (healersCount > 0) {
                        for (int h = 0; h < healersToGive && healersCount >= healersToGive; --healersCount, ++h) {
                            final PlayerEventInfo pi = players.get(ClassType.Priest).get(0);
                            toParty.add(pi);
                            players.get(ClassType.Priest).remove(pi);
                        }
                    }
                    if (domisCount > 0) {
                        for (int h = 0; h < domisToGive && domisCount >= domisToGive; --domisCount, ++h) {
                            final PlayerEventInfo pi = players.get(ClassType.Dominator).get(0);
                            toParty.add(pi);
                            players.get(ClassType.Dominator).remove(pi);
                        }
                    }
                    if (tanksCount > 0) {
                        for (int h = 0; h < tanksToGive && tanksCount >= tanksToGive; --tanksCount, ++h) {
                            final PlayerEventInfo pi = players.get(ClassType.Tank).get(0);
                            toParty.add(pi);
                            players.get(ClassType.Tank).remove(pi);
                        }
                    }
                    boolean b = false;
                    while (toParty.size() < partySize) {
                        boolean added = false;
                        final Iterator<PlayerEventInfo> iterator5 = players.get(b ? ClassType.Mystic : ClassType.Fighter).iterator();
                        if (iterator5.hasNext()) {
                            final PlayerEventInfo fighter = iterator5.next();
                            toParty.add(fighter);
                            players.get(b ? ClassType.Mystic : ClassType.Fighter).remove(fighter);
                            added = true;
                        }
                        b = !b;
                        if (!added) {
                            final Iterator<PlayerEventInfo> iterator6 = players.get(b ? ClassType.Mystic : ClassType.Fighter).iterator();
                            if (iterator6.hasNext()) {
                                final PlayerEventInfo mystic = iterator6.next();
                                toParty.add(mystic);
                                players.get(b ? ClassType.Mystic : ClassType.Fighter).remove(mystic);
                                added = true;
                            }
                        }
                        if (!added) {
                            if (healersCount > 0) {
                                final Iterator<PlayerEventInfo> iterator7 = players.get(ClassType.Priest).iterator();
                                if (!iterator7.hasNext()) {
                                    continue;
                                }
                                final PlayerEventInfo healer = iterator7.next();
                                toParty.add(healer);
                                players.get(ClassType.Priest).remove(healer);
                                added = true;
                                --healersCount;
                            }
                            else if (domisCount > 0) {
                                final Iterator<PlayerEventInfo> iterator8 = players.get(ClassType.Dominator).iterator();
                                if (!iterator8.hasNext()) {
                                    continue;
                                }
                                final PlayerEventInfo domi = iterator8.next();
                                toParty.add(domi);
                                players.get(ClassType.Dominator).remove(domi);
                                added = true;
                                --domisCount;
                            }
                            else {
                                if (tanksCount <= 0) {
                                    break;
                                }
                                final Iterator<PlayerEventInfo> iterator9 = players.get(ClassType.Tank).iterator();
                                if (!iterator9.hasNext()) {
                                    continue;
                                }
                                final PlayerEventInfo tank = iterator9.next();
                                toParty.add(tank);
                                players.get(ClassType.Tank).remove(tank);
                                added = true;
                                --tanksCount;
                            }
                        }
                    }
                    this.dumpParty(team, toParty);
                    this.partyPlayers(toParty);
                    toParty.clear();
                }
            }
        }
    }


    private void dumpParty(final EventTeam team, final List<PlayerEventInfo> players) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: ////// START of dump of party for team " + team.getTeamName());
        }
        for (final PlayerEventInfo pl : players) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: /*/*/*/*/*/*/ player " + pl.getPlayersName() + " is of class id " + pl.getClassType().toString());
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: ////// END of dump of party for team " + team.getTeamName());
        }
    }

    protected void partyPlayers(final List<PlayerEventInfo> players) {
        try {
            if (players.size() > 1) {
                PartyData party = null;
                int count = 0;
                for (final PlayerEventInfo player : players) {
                    if (player.getParty() != null) {
                        player.getParty().removePartyMember(player);
                    }
                    player.setCanInviteToParty(false);
                }
                PlayerEventInfo leader = null;
                for (final PlayerEventInfo player2 : players) {
                    if (count == 0) {
                        leader = player2;
                        party = new PartyData(player2);
                    }
                    else {
                        CallBack.getInstance().getOut().scheduleGeneral(new AddToParty(party, player2), 800 * count);
                    }
                    if (++count >= 9) {
                        break;
                    }
                }
                if (leader != null) {
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: reallowing inviting to the party back to the leader (" + leader.getPlayersName() + ").");
                    }
                    else if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: NOT reallowing inviting to the party back to the leader because he is null!");
                    }
                }
                final PlayerEventInfo fLeader = leader;
                CallBack.getInstance().getOut().scheduleGeneral(() -> {
                    if (fLeader != null) {
                        fLeader.setCanInviteToParty(true);
                    }
                }, 800 * (count + 1));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: createParties error (and parties will be deleted): " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
            this.debug("Error while partying players: " + e.toString() + ". Deleting parties...");
            try {
                for (final PlayerEventInfo player3 : players) {
                    if (player3.getParty() != null) {
                        player3.getParty().removePartyMember(player3);
                    }
                }
            }
            catch (Exception e2) {
                e2.printStackTrace();
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: error while removing parties (cause of another error): " + SunriseLoader.getTraceString(e2.getStackTrace()));
                }
            }
            this.debug("Parties deleted.");
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: parties deleted.");
            }
        }
    }


    public void teleportPlayers(final int instanceId, final SpawnType type, final boolean ffa) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: ========================================");
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: STARTING TO TELEPORT PLAYERS (ffa = " + ffa + ")");
        }
        final boolean removeBuffs = this.getBoolean("removeBuffsOnStart");
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: removeBuffs = " + removeBuffs);
        }
        int i = 0;
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            final EventSpawn spawn = this.getSpawn(type, ffa ? -1 : player.getTeamId());
            if (spawn == null) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: ! Missing spawn for team " + ((this._teams.get(instanceId).size() == 1) ? -1 : player.getTeamId()) + ", map " + this._manager.getMap().getMapName() + ", event " + this.getEventType().getAltTitle() + " !!");
                }
                SunriseLoader.debug("Missing spawn for team " + ((this._teams.get(instanceId).size() == 1) ? -1 : player.getTeamId()) + ", map " + this._manager.getMap().getMapName() + ", event " + this.getEventType().getAltTitle() + " !!", Level.SEVERE);
            }
            if (spawn != null) {
                int radius = spawn.getRadius();
                if (radius == -1) {
                    radius = 50;
                }
                final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
                loc.addRadius(radius);
                player.teleport(loc, 0, false, instanceId);
                if(player.getOwner().isProcessingRequest()){
                    player.getOwner().setActiveRequester(null);
                    player.getOwner().onTransactionResponse();
                }
                player.getOwner().leaveParty();
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// player " + player.getPlayersName() + " teleported to " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + " (radius = " + radius + "), SPAWN ID " + spawn.getSpawnId() + ", SPAWN TEAM " + spawn.getSpawnTeam());
                }
            }
            if (removeBuffs) {
                player.removeBuffs();
            }
            ++i;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: " + i + " PLAYERS TELEPORTED");
        }
        this.clearMapHistory(-1, type);
    }

    public boolean checkPlayers(final int instanceId) {
        if (!this.checkIfEventCanContinue(instanceId, null)) {
            this.announce(instanceId, LanguageEngine.getMsg("announce_alldisconnected"));
            this.endInstance(instanceId, true, false, true);
            this.debug(this.getEventName() + ": no players left in the teams after teleporting to the event, the fight can't continue. The event has been aborted!");
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: check players: FALSE (NOT ENOUGHT players to start the event)");
            }
            return false;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: check players: OK (enought players to start the event)");
        }
        return true;
    }

    protected void enableMarkers(final int instanceId, final boolean useEventSpawnMarkers) {
        if (!this._enableRadar) {
            return;
        }
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                pi.createRadar();
            }
        }
        if (useEventSpawnMarkers) {
            List<EventSpawn> markers = null;
            for (final EventTeam team2 : this._teams.get(instanceId).values()) {
                markers = this._manager.getMap().getMarkers(team2.getTeamId());
                if (markers != null && !markers.isEmpty()) {
                    EventSpawn marker = null;
                    final Iterator<EventSpawn> iterator4 = markers.iterator();
                    if (iterator4.hasNext()) {
                        final EventSpawn pMarkers = marker = iterator4.next();
                    }
                    for (final PlayerEventInfo pi2 : team2.getPlayers()) {
                        pi2.getRadar().setLoc(marker.getLoc().getX(), marker.getLoc().getY(), marker.getLoc().getZ());
                        pi2.getRadar().setRepeat(true);
                        pi2.getRadar().enable();
                    }
                }
            }
        }
    }


    protected void removeStaticDoors(int instanceId) {
        CallBack.getInstance().getOut().addDoorToInstance(instanceId, 17190001, true);
        CallBack.getInstance().getOut().getInstanceDoors(instanceId)[0].openMe();
    }

    protected void disableMarkers(int instanceId) {
        if (!this._enableRadar) {
            return;
        }
        for (EventTeam team : (this._teams.get(instanceId)).values()) {
            for (PlayerEventInfo pi : team.getPlayers()) {
                pi.getRadar().disable();
            }
        }
    }

    protected void addMarker(PlayerEventInfo pi, EventSpawn marker, boolean repeat) {
        if (!this._enableRadar) {
            return;
        }
        pi.getRadar().setLoc(marker.getLoc().getX(), marker.getLoc().getY(), marker.getLoc().getZ());
        pi.getRadar().setRepeat(repeat);
        if (!pi.getRadar().isEnabled()) {
            pi.getRadar().enable();
        }
    }

    protected void removeMarker(PlayerEventInfo pi, EventSpawn marker) {
        pi.removeRadarMarker(marker.getLoc().getX(), marker.getLoc().getY(), marker.getLoc().getZ());
    }

    public void setupTitles(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: SETUPING TITLES");
        }
        for (final PlayerEventInfo pi : this.getPlayers(instanceId)) {
            if (this._allowSchemeBuffer) {
                EventBuffer.getInstance().buffPlayer(pi, true);
            }
            if (AbstractMainEvent.this._allowNoblesOnRess) {
                L2Skill noblesse = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(1323, 1);
                if (noblesse != null) {
                    noblesse.getEffects(pi.getOwner(), pi.getOwner());
                }
            }
            if (this._removePartiesOnStart) {
                final PartyData pt = pi.getParty();
                if (pt != null) {
                    pi.getParty().removePartyMember(pi);
                }
            }
            if (pi.isTitleUpdated()) {
                pi.setTitle(this.getTitle(pi), true);
            }
        }
    }

    protected EventSpawn getSpawn(SpawnType type, int teamId) {
        EventMap map = this._manager.getMap();
        if (map == null) {
            return null;
        }
        return map.getNextSpawn(teamId, type);
    }

    protected void clearMapHistory(int teamId, SpawnType type) {
        EventMap map = this._manager.getMap();
        if (map != null) {
            map.clearHistory(teamId, type);
            if (SunriseLoader.detailedDebug) {
                print("AbstractMainEvent: map history clean done");
            }
        } else if (SunriseLoader.detailedDebug) {
            print("AbstractMainEvent: couldn't clean map, map is NULL!");
        }
    }

    public void forceSitAll(int instanceId) {
        if (SunriseLoader.detailedDebug) {
            print("AbstractMainEvent: FORCE SIT ALL");
        }
        for (PlayerEventInfo player : getPlayers(instanceId)) {
            player.abortCasting();
            player.disableAfkCheck(true);
            player.setIsSitForced(true);
            player.sitDown();
        }
    }

    public void forceStandAll(int instanceId) {
        if (SunriseLoader.detailedDebug) {
            print("AbstractMainEvent: FORCE STAND UP");
        }
        for (PlayerEventInfo player : getPlayers(instanceId)) {
            player.disableAfkCheck(false);
            player.setIsSitForced(false);
            player.standUp();
        }
    }

    protected void sysMsgToAll(String text) {
        if (SunriseLoader.detailedDebug) {
            print("? AbstractMainEvent: sysMsgToAll - " + text);
        }
        for (PlayerEventInfo pi : getPlayers(0)) {
            pi.sendMessage(text);
        }
    }

    protected void sysMsgToAll(int instance, String text) {
        if (SunriseLoader.detailedDebug) {
            print("? AbstractMainEvent: sysMsgToAll to instance " + instance + "; text= " + text);
        }
        for (PlayerEventInfo pi : getPlayers(instance)) {
            pi.sendMessage(text);
        }
    }

    public Set<PlayerEventInfo> getPlayers(final int instanceId) {
        final Set<PlayerEventInfo> players = ConcurrentHashMap.newKeySet();
        if (this._teams.isEmpty()) {
            return players;
        }
        if (instanceId == 0 || instanceId == 1 || instanceId == -1) {
            for (final Map<Integer, EventTeam> fm : this._teams.values()) {
                for (final EventTeam team : fm.values()) {
                    for (final PlayerEventInfo player : team.getPlayers()) {
                        players.add(player);
                    }
                }
            }
        }
        else {
            for (final EventTeam team2 : this._teams.get(instanceId).values()) {
                for (final PlayerEventInfo player2 : team2.getPlayers()) {
                    players.add(player2);
                }
            }
        }
        return players;
    }

    protected void initWaweRespawns(int delay) {
        if (SunriseLoader.detailedDebug) {
            print("AbstractMainEvent: STARTING WAWE SPAWN SYSTEM");
        }
        this._waweScheduler = new WaweRespawnScheduler(delay * 1000);
    }

    public void addSpectator(PlayerEventInfo gm, int instanceId) {
        if (gm.isInEvent() || gm.isRegistered()) {
            gm.sendMessage(LanguageEngine.getMsg("observing_alreadyRegistered"));
            return;
        }
        if (this._spectators != null) {
            EventSpawn selected = null;
            for (EventSpawn s : EventManager.getInstance().getMainEventManager().getMap().getSpawns()) {
                if (s.getSpawnType() == SpawnType.Regular || s.getSpawnType() == SpawnType.Safe) {
                    selected = s;
                }
            }
            if (selected == null) {
                gm.sendMessage(LanguageEngine.getMsg("observing_noSpawn"));
                return;
            }
            gm.initOrigInfo();
            gm.setInstanceId(instanceId);
            gm.teleToLocation(selected.getLoc().getX(), selected.getLoc().getY(), selected.getLoc().getZ(), false);
            synchronized (this._spectators) {
                this._spectators.add(gm);
            }
        }
    }

    public void removeSpectator(PlayerEventInfo gm) {
        if (this._spectators != null) {
            gm.setInstanceId(0);
            gm.teleToLocation(gm.getOrigLoc().getX(), gm.getOrigLoc().getY(), gm.getOrigLoc().getZ(), false);
            synchronized (this._spectators) {
                this._spectators.remove(gm);
            }
        }
    }

    public boolean isWatching(PlayerEventInfo gm) {
        return (this._spectators != null && this._spectators.contains(gm));
    }

    protected void clearPlayers(final boolean unregister, final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent:  =====================");
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: CALLED CLEAR PLAYERS for instanceId " + instanceId + ", unregister = " + unregister);
        }
        if (instanceId == 0) {
            if (this._waweScheduler != null) {
                this._waweScheduler.stop();
            }
            EventManager.getInstance().clearDisconnectedPlayers();
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo spectator : this._spectators) {
                if (instanceId == 0 || spectator.getInstanceId() == instanceId) {
                    spectator.setInstanceId(0);
                    spectator.teleToLocation(spectator.getOrigLoc().getX(), spectator.getOrigLoc().getY(), spectator.getOrigLoc().getZ(), false);
                    this._spectators.remove(spectator);
                }
            }
            if (instanceId == 0) {
                this._spectators.clear();
                this._spectators = null;
            }
        }
        this.cleanMap(instanceId);
        int unregistered = 0;
        if (unregister) {
            switch (this._manager.getState()) {
                case REGISTERING: {
                    for (final PlayerEventInfo player : this._manager.getPlayers()) {
                        player.setIsRegisteredToMainEvent(false, null);
                        CallBack.getInstance().getPlayerBase().eventEnd(player);
                        ++unregistered;
                    }
                    break;
                }
                case END:
                case RUNNING:
                case TELE_BACK: {
                    this._manager.paralizeAll(false);
                    for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                        this._manager.getPlayers().remove(player);
                        if (player.getEventTeam() != null) {
                            player.getEventTeam().removePlayer(player);
                        }
                        player.setIsRegisteredToMainEvent(false, null);
                        CallBack.getInstance().getPlayerBase().eventEnd(player);
                        ++unregistered;
                    }
                    for (final PlayerEventInfo player : this._manager.getPlayers()) {
                        if (instanceId == 0 || player.getInstanceId() == instanceId) {
                            player.setIsRegisteredToMainEvent(false, null);
                            CallBack.getInstance().getPlayerBase().eventEnd(player);
                            ++unregistered;
                        }
                    }
                    break;
                }
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: unregistered " + unregistered + " players");
        }
        if (this._instances != null) {
            for (final InstanceData instance : this._instances) {
                if (instanceId == 0 || instanceId == instance.getId()) {
                    CallbackManager.getInstance().eventEnded(instanceId, this.getEventType(), this._teams.get(instance.getId()).values());
                    for (final EventTeam team : this._teams.get(instance.getId()).values()) {
                        for (final PlayerEventInfo pi : team.getPlayers()) {
                            team.removePlayer(pi);
                        }
                    }
                }
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: Event " + this.getEventName() + " finished clearPlayers() for instance ID " + instanceId);
        }
        SunriseLoader.debug("Event " + this.getEventName() + " finished clearPlayers() for instance ID " + instanceId);
        if (instanceId == 0) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: checking if all unregistered...");
            }
            Collection<PlayerEventInfo> playersLeft = this.getPlayers(0);
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: playersLeft size = " + playersLeft.size());
            }
            if (playersLeft.size() > 0 || this._manager.getPlayers().size() > 0) {
                for (final PlayerEventInfo player2 : playersLeft) {
                    if (player2.getEventTeam() != null) {
                        player2.getEventTeam().removePlayer(player2);
                    }
                    player2.setIsRegisteredToMainEvent(false, null);
                    CallBack.getInstance().getPlayerBase().eventEnd(player2);
                }
                playersLeft = this._manager.getPlayers();
                for (final PlayerEventInfo player2 : playersLeft) {
                    if (player2.getEventTeam() != null) {
                        player2.getEventTeam().removePlayer(player2);
                    }
                    player2.setIsRegisteredToMainEvent(false, null);
                    CallBack.getInstance().getPlayerBase().eventEnd(player2);
                }
                playersLeft = this.getPlayers(0);
            }
        }
        if (instanceId == 0) {
            this._tempPlayers.clear();
            this._rewardedInstances.clear();
            this._instances = null;
            this._manager.clean(null);
        }
    }

    public void setKillsStats(PlayerEventInfo playereventinfo, int i) {
    }

    public void setDeathsStats(PlayerEventInfo playereventinfo, int i) {
    }

    public void setScoreStats(PlayerEventInfo playereventinfo, int i) {
    }

    public RewardPosition[] getRewardTypes() {
        return this._rewardTypes;
    }

    public void setRewardTypes(RewardPosition[] types) {
        this._rewardTypes = types;
    }

    public String getString(String propName) {
        if (this._configs.containsKey(propName)) {
            String value = ((ConfigModel) this._configs.get(propName)).getValue();
            return value;
        }
        return "";
    }

    public int getInt(String propName) {
        if (this._configs.containsKey(propName)) {
            int value = ((ConfigModel) this._configs.get(propName)).getValueInt();
            return value;
        }
        return 0;
    }

    public boolean getBoolean(String propName) {
        if (this._configs.containsKey(propName)) {
            return ((ConfigModel) this._configs.get(propName)).getValueBoolean();
        }
        return false;
    }

    protected void addConfig(ConfigModel model) {
        this._configs.put(model.getKey(), model);
    }

    protected void removeConfig(String key) {
        this._configs.remove(key);
    }

    protected void addConfig(String category, ConfigModel model) {
        if (!this._configCategories.contains(category)) {
            this._configCategories.add(category);
        }
        this._configs.put(model.getKey(), model.setCategory(category));
    }

    protected void addMapConfig(ConfigModel model) {
        this._mapConfigs.put(model.getKey(), model);
    }

    protected void addInstanceTypeConfig(ConfigModel model) {
        this._instanceTypeConfigs.put(model.getKey(), model);
    }

    protected void removeConfigs() {
        this._configCategories.clear();
        this._configs.clear();
    }

    protected void removeMapConfigs() {
        this._mapConfigs.clear();
    }

    protected void removeInstanceTypeConfigs() {
        this._instanceTypeConfigs.clear();
    }

    public final Map<String, ConfigModel> getConfigs() {
        return this._configs;
    }

    public void clearConfigs() {
        removeConfigs();
        removeMapConfigs();
        removeInstanceTypeConfigs();
    }

    public List<String> getCategories() {
        return this._configCategories;
    }

    public void setConfig(String key, String value, boolean addToValue) {
        if (!this._configs.containsKey(key)) {
            return;
        }
        if (!addToValue) {
            ((ConfigModel) this._configs.get(key)).setValue(value);
        } else {
            ((ConfigModel) this._configs.get(key)).addToValue(value);
        }
    }

    public Map<String, ConfigModel> getMapConfigs() {
        return this._mapConfigs;
    }

    public Map<SpawnType, String> getAvailableSpawnTypes() {
        return this._spawnTypes;
    }

    public int getMaxPlayers() {
        return getInt("maxPlayers");
    }

    public String getEstimatedTimeLeft() {
        return "N/A";
    }

    public boolean canRun(EventMap map) {
        return (getMissingSpawns(map).length() == 0);
    }

    protected String addMissingSpawn(final SpawnType type, final int team, final int count) {
        return "<font color=B46F6B>" + this.getEventType().getAltTitle() + "</font> -> <font color=9f9f9f>No</font> <font color=B46F6B>" + type.toString().toUpperCase() + "</font> <font color=9f9f9f>spawn for team " + team + " " + ((team == 0) ? "(team doesn't matter)" : "") + " count " + count + " (or more)</font><br1>";
    }

    public void announce(final int instance, final String msg) {
        for (final PlayerEventInfo pi : this.getPlayers(instance)) {
            pi.creatureSay(this.getEventName() + ": " + msg, this.getEventName(), 18);
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo spectator : this._spectators) {
                if (spectator.isOnline() && spectator.getInstanceId() == instance) {
                    spectator.creatureSay(this.getEventName() + ": " + msg, this.getEventName(), 18);
                }
            }
        }
    }

    public void announce(final int instance, final String msg, final int team) {
        if (SunriseLoader.detailedDebug) {
            this.print("? AbstractMainEvent: announcing to instance " + instance + " team " + team + " msg: " + msg);
        }
        for (final PlayerEventInfo pi : this.getPlayers(instance)) {
            if (pi.getTeamId() == team) {
                pi.creatureSay(this.getEventName() + ": " + msg, this.getEventName(), 18);
            }
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo spectator : this._spectators) {
                if (spectator.isOnline() && spectator.getInstanceId() == instance) {
                    spectator.creatureSay(this.getEventName() + ": " + msg, this.getEventName() + " [T" + team + " msg]", 18);
                }
            }
        }
    }

    public void announceToAllTeamsBut(final int instance, final String msg, final int excludedTeam) {
        if (SunriseLoader.detailedDebug) {
            this.print("? AbstractMainEvent: announcing to all teams but " + excludedTeam + ", instance " + instance + " msg " + msg);
        }
        for (final PlayerEventInfo pi : this.getPlayers(instance)) {
            if (pi.getTeamId() != excludedTeam) {
                pi.creatureSay(this.getEventName() + ": " + msg, this.getEventName(), 18);
            }
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo spectator : this._spectators) {
                if (spectator.isOnline() && spectator.getInstanceId() == instance) {
                    spectator.creatureSay(this.getEventName() + ": " + msg, this.getEventName() + " [all except T" + excludedTeam + " msg]", 18);
                }
            }
        }
    }

    public void screenAnnounce(final int instance, final String msg) {
        if (SunriseLoader.detailedDebug) {
            this.print("? AbstractMainEvent: screenannounce to instance " + instance + " msg: " + msg);
        }
        for (final PlayerEventInfo pi : this.getPlayers(instance)) {
            pi.creatureSay(msg, this.getEventName(), 15);
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo spectator : this._spectators) {
                if (spectator.isOnline() && spectator.getInstanceId() == instance) {
                    spectator.creatureSay(msg, this.getEventName(), 15);
                }
            }
        }
    }

    protected void scheduleRevive(final PlayerEventInfo pi, final int time) {
        new ReviveTask(pi, time);
    }

    protected void setInstanceNotReceiveRewards(final int instanceId) {
        synchronized (this._rewardedInstances) {
            this._rewardedInstances.add(instanceId);
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: instance of ID " + instanceId + " has been marked as NOTREWARDED");
        }
    }

    protected void rewardFirstRegisteredFFA(final List<PlayerEventInfo> list) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: rewarding first registered (ffa)");
        }
        int count = 0;
        if (list != null) {
            for (final PlayerEventInfo player : list) {
                if (this._firstRegistered.contains(player) && player.isOnline()) {
                    player.sendMessage(LanguageEngine.getMsg("event_extraReward", this.firstRegisteredRewardCount));
                    EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.FirstRegistered, null, player.getTotalTimeAfk(), 0, 0);
                    ++count;
                }
            }
        }
        else {
            for (final PlayerEventInfo player : this._firstRegistered) {
                player.sendMessage(LanguageEngine.getMsg("event_extraReward", this.firstRegisteredRewardCount));
                EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.FirstRegistered, null, player.getTotalTimeAfk(), 0, 0);
                ++count;
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: " + count + " players were given FirstRegistered reward");
        }
    }

    protected void rewardAllPlayersFromTeam(final int instanceId, final int minScore, final int minKills, final int teamId) {
        try {
            if (this.getEventType().isFFAEvent()) {
                SunriseLoader.debug(this.getEventName() + " cannot use rewardAllPlayers since this is a FFA event.", Level.SEVERE);
                return;
            }
            if (this._instances == null) {
                SunriseLoader.debug(this.getEventName() + " _instances were null when the event tried to reward!", Level.SEVERE);
                return;
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: CALLED REWARD ALL PLAYERS  for instance " + instanceId + ", min score " + minScore + ", min kills " + minKills);
            }
            final boolean firstXRewardWinners = "WinnersOnly".equals(this.firstRegisteredRewardType);
            for (final InstanceData instance : this._instances) {
                Label_1199: {
                    if (instance.getId() == instanceId || instanceId == -1) {
                        synchronized (this._rewardedInstances) {
                            if (this._rewardedInstances.contains(instance.getId())) {
                                break Label_1199;
                            }
                            this._rewardedInstances.add(instance.getId());
                        }
                        final EventTeam team = this._teams.get(instance.getId()).get(teamId);
                        if (team == null) {
                            SunriseLoader.debug(this.getEventName() + " no team of ID " + teamId + " to be rewarded!", Level.SEVERE);
                            return;
                        }
                        final int playersCount = team.getPlayers().size();
                        final List<PlayerEventInfo> sorted = new LinkedList<PlayerEventInfo>();
                        final Map<PlayerEventInfo, Integer> map = new LinkedHashMap<PlayerEventInfo, Integer>();
                        for (final PlayerEventInfo player : team.getPlayers()) {
                            sorted.add(player);
                        }
                        Collections.sort(sorted, EventManager.getInstance().comparePlayersScore);
                        for (final PlayerEventInfo player : sorted) {
                            map.put(player, this.getPlayerData(player).getScore());
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: before giving reward");
                        }
                        final Map<Integer, List<PlayerEventInfo>> scores = EventRewardSystem.getInstance().rewardPlayers(map, this.getEventType(), 1, minScore, this._afkHalfReward, this._afkNoReward);
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: rewards given");
                        }
                        int place = 1;
                        final int limitToAnnounce = this.getInt("announcedTopPlayersCount");
                        final int totalLimit = Math.min(limitToAnnounce * 2, 15);
                        int counter = 1;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> e : scores.entrySet()) {
                            if (counter > totalLimit) {
                                break;
                            }
                            if (place > limitToAnnounce) {
                                continue;
                            }
                            for (final PlayerEventInfo player2 : e.getValue()) {
                                if (counter > totalLimit) {
                                    break;
                                }
                                this.announce(instance.getId(), LanguageEngine.getMsg("event_announceScore", place, player2.getPlayersName(), this.getPlayerData(player2).getScore()));
                                ++counter;
                            }
                            ++place;
                        }
                        place = 1;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> i : scores.entrySet()) {
                            if (place == 1) {
                                if (firstXRewardWinners) {
                                    this.rewardFirstRegisteredFFA(i.getValue());
                                }
                                if (i.getValue().size() > 1) {
                                    if (playersCount > i.getValue().size()) {
                                        StringBuilder tb = new StringBuilder("*** ");
                                        for (final PlayerEventInfo player3 : i.getValue()) {
                                            tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part1", player3.getPlayersName()) + " ");
                                        }
                                        final String s = tb.toString();
                                        tb = new StringBuilder(s.substring(0, s.length() - 4));
                                        tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part2"));
                                        this.announce(instance.getId(), tb.toString());
                                    }
                                    else {
                                        this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_ffa_announceWinner3"));
                                    }
                                }
                                else {
                                    this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_ffa_announceWinner1", i.getValue().get(0).getPlayersName()));
                                }
                                for (final PlayerEventInfo player2 : i.getValue()) {
                                    this.getPlayerData(player2).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                }
                            }
                            else {
                                for (final PlayerEventInfo player2 : i.getValue()) {
                                    this.getPlayerData(player2).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                                }
                            }
                            ++place;
                        }
                    }
                }
            }
            if (!firstXRewardWinners) {
                this.rewardFirstRegisteredFFA(null);
            }
            this.saveGlobalStats(instanceId);
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    protected void rewardAllPlayers(final int instanceId, final int minScore, final int minKills) {
        try {
            if (!this.getEventType().isFFAEvent()) {
                SunriseLoader.debug(this.getEventName() + " cannot use rewardAllPlayers since it is an non-FFA event.", Level.SEVERE);
                return;
            }
            if (this._instances == null) {
                SunriseLoader.debug(this.getEventName() + " _instances were null when the event tried to reward!", Level.SEVERE);
                return;
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: CALLED REWARD ALL PLAYERS  for instance " + instanceId + ", min score " + minScore + ", min kills " + minKills);
            }
            final boolean firstXRewardWinners = "WinnersOnly".equals(this.firstRegisteredRewardType);
            for (final InstanceData instance : this._instances) {
                Label_1126: {
                    if (instance.getId() == instanceId || instanceId == -1) {
                        synchronized (this._rewardedInstances) {
                            if (this._rewardedInstances.contains(instance.getId())) {
                                break Label_1126;
                            }
                            this._rewardedInstances.add(instance.getId());
                        }
                        final int playersCount = this.getPlayers(instance.getId()).size();
                        final List<PlayerEventInfo> sorted = new LinkedList<PlayerEventInfo>();
                        final Map<PlayerEventInfo, Integer> map = new LinkedHashMap<PlayerEventInfo, Integer>();
                        for (final PlayerEventInfo player : this.getPlayers(instance.getId())) {
                            sorted.add(player);
                        }
                        Collections.sort(sorted, EventManager.getInstance().comparePlayersScore);
                        for (final PlayerEventInfo player : sorted) {
                            map.put(player, this.getPlayerData(player).getScore());
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: before giving reward");
                        }
                        final Map<Integer, List<PlayerEventInfo>> scores = EventRewardSystem.getInstance().rewardPlayers(map, this.getEventType(), 1, minScore, this._afkHalfReward, this._afkNoReward);
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: rewards given");
                        }
                        int place = 1;
                        final int limitToAnnounce = this.getInt("announcedTopPlayersCount");
                        final int totalLimit = Math.min(limitToAnnounce * 2, 15);
                        int counter = 1;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> e : scores.entrySet()) {
                            if (counter > totalLimit) {
                                break;
                            }
                            if (place > limitToAnnounce) {
                                continue;
                            }
                            for (final PlayerEventInfo player2 : e.getValue()) {
                                if (counter > totalLimit) {
                                    break;
                                }
                                this.announce(instance.getId(), LanguageEngine.getMsg("event_announceScore", place, player2.getPlayersName(), this.getPlayerData(player2).getScore()));
                                ++counter;
                            }
                            ++place;
                        }
                        place = 1;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> i : scores.entrySet()) {
                            if (place == 1) {
                                if (firstXRewardWinners) {
                                    this.rewardFirstRegisteredFFA(i.getValue());
                                }
                                if (i.getValue().size() > 1) {
                                    if (playersCount > i.getValue().size()) {
                                        StringBuilder tb = new StringBuilder("*** ");
                                        for (final PlayerEventInfo player3 : i.getValue()) {
                                            tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part1", player3.getPlayersName()) + " ");
                                        }
                                        final String s = tb.toString();
                                        tb = new StringBuilder(s.substring(0, s.length() - 4));
                                        tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part2"));
                                        this.announce(instance.getId(), tb.toString());
                                    }
                                    else {
                                        this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_ffa_announceWinner3"));
                                    }
                                }
                                else {
                                    this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_ffa_announceWinner1", i.getValue().get(0).getPlayersName()));
                                }
                                for (final PlayerEventInfo player2 : i.getValue()) {
                                    this.getPlayerData(player2).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                }
                            }
                            else {
                                for (final PlayerEventInfo player2 : i.getValue()) {
                                    this.getPlayerData(player2).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                                }
                            }
                            ++place;
                        }
                    }
                }
            }
            if (!firstXRewardWinners) {
                this.rewardFirstRegisteredFFA(null);
            }
            this.saveGlobalStats(instanceId);
        }
        catch (Exception ex) {}
    }

    public void rewardAllTeams(final int instanceId, final int minScore, final int minKills) {
        try {
            if (this.getEventType().isFFAEvent()) {
                SunriseLoader.debug(this.getEventName() + " cannot use rewardAllTeams since it is an FFA event.");
                return;
            }
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: CALLED REWARD ALL TEAMS  for instance " + instanceId + ", min score " + minScore + ", min kills " + minKills);
            }
            final boolean firstXRewardWinners = "WinnersOnly".equals(this.firstRegisteredRewardType);
            for (final InstanceData instance : this._instances) {
                Label_1096: {
                    if (instance.getId() == instanceId || instanceId == -1) {
                        synchronized (this._rewardedInstances) {
                            if (this._rewardedInstances.contains(instance.getId())) {
                                break Label_1096;
                            }
                            this._rewardedInstances.add(instance.getId());
                        }
                        final int teamsCount = this._teams.get(instance.getId()).size();
                        final List<EventTeam> sorted = new LinkedList<EventTeam>();
                        final Map<EventTeam, Integer> map = new LinkedHashMap<EventTeam, Integer>();
                        for (final EventTeam team : this._teams.get(instance.getId()).values()) {
                            sorted.add(team);
                        }
                        Collections.sort(sorted, EventManager.getInstance().compareTeamScore);
                        for (final EventTeam team : sorted) {
                            map.put(team, team.getScore());
                        }
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: before giving reward");
                        }
                        final Map<Integer, List<EventTeam>> scores = EventRewardSystem.getInstance().rewardTeams(map, this.getEventType(), 1, minScore, this._afkHalfReward, this._afkNoReward);
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: rewards given");
                        }
                        int place = 1;
                        for (final EventTeam team2 : sorted) {
                            this.announce(instance.getId(), LanguageEngine.getMsg("event_announceScore", place, team2.getFullName(), team2.getScore()));
                            team2.setFinalPosition(place);
                            ++place;
                        }
                        place = 1;
                        for (final Map.Entry<Integer, List<EventTeam>> i : scores.entrySet()) {
                            if (place == 1) {
                                if (firstXRewardWinners) {
                                    this.rewardFirstRegistered(i.getValue());
                                }
                                if (i.getValue().size() > 1) {
                                    if (teamsCount > i.getValue().size()) {
                                        StringBuilder tb = new StringBuilder("*** ");
                                        for (final EventTeam team3 : i.getValue()) {
                                            tb.append(LanguageEngine.getMsg("event_team_announceWinner2_part1", team3.getFullName()) + " ");
                                        }
                                        final String s = tb.toString();
                                        tb = new StringBuilder(s.substring(0, s.length() - 4));
                                        tb.append(LanguageEngine.getMsg("event_team_announceWinner2_part2"));
                                        this.announce(instance.getId(), tb.toString());
                                    }
                                    else {
                                        this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_team_announceWinner3"));
                                    }
                                }
                                else {
                                    this.announce(instance.getId(), "*** " + LanguageEngine.getMsg("event_team_announceWinner1", i.getValue().get(0).getFullName()));
                                }
                                for (final EventTeam team4 : i.getValue()) {
                                    for (final PlayerEventInfo player : team4.getPlayers()) {
                                        this.getPlayerData(player).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                    }
                                }
                            }
                            else {
                                for (final EventTeam team4 : i.getValue()) {
                                    for (final PlayerEventInfo player : team4.getPlayers()) {
                                        this.getPlayerData(player).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                                    }
                                }
                            }
                            ++place;
                        }
                        this.saveGlobalStats(instance.getId());
                    }
                }
            }
            if (!firstXRewardWinners) {
                this.rewardFirstRegistered(null);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void rewardFirstRegistered(final List<EventTeam> list) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: rewarding first registered (teams)");
        }
        int count = 0;
        if (list != null) {
            for (final EventTeam t : list) {
                for (final PlayerEventInfo player : t.getPlayers()) {
                    if (this._firstRegistered.contains(player) && player.isOnline()) {
                        player.sendMessage(LanguageEngine.getMsg("event_extraReward", this.firstRegisteredRewardCount));
                        EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.FirstRegistered, null, player.getTotalTimeAfk(), 0, 0);
                        ++count;
                    }
                }
            }
        }
        else {
            for (final PlayerEventInfo player2 : this._firstRegistered) {
                player2.sendMessage(LanguageEngine.getMsg("event_extraReward", this.firstRegisteredRewardCount));
                EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player2, RewardPosition.FirstRegistered, null, player2.getTotalTimeAfk(), 0, 0);
                ++count;
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: " + count + " players were given FirstRegistered reward");
        }
    }

    protected void saveGlobalStats(final int instance) {
        final Map<PlayerEventInfo, GlobalStatsModel> stats = new ConcurrentHashMap<PlayerEventInfo, GlobalStatsModel>();
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            this.getPlayerData(player).getGlobalStats().raise(GlobalStats.GlobalStatType.COUNT_PLAYED, 1);
            stats.put(player, this.getPlayerData(player).getGlobalStats());
        }
        EventStatsManager.getInstance().getGlobalStats().updateGlobalStats(stats);
    }

    protected NpcData spawnNPC(final int x, final int y, final int z, final int npcId, final int instanceId, final String name, final String title) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: spawning npc " + x + ", " + y + ", " + z + ", npc id " + npcId + ", instance " + instanceId + ", name " + name + ", title " + title);
        }
        final NpcTemplateData template = new NpcTemplateData(npcId);
        if (!template.exists()) {
            return null;
        }
        template.setSpawnName(name);
        template.setSpawnTitle(title);
        try {
            final NpcData npc = template.doSpawn(x, y, z, 1, instanceId);
            if (npc != null) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: npc spawned succesfully.");
                }
                else if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: npc null after spawning (template exists = " + template.exists() + ").");
                }
            }
            return npc;
        }
        catch (Exception e) {
            e.printStackTrace();
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: error while spawning npc - " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
            return null;
        }
    }

    public void insertConfigs(final MainEventInstanceType type) {
        for (final Map.Entry<String, ConfigModel> e : this._instanceTypeConfigs.entrySet()) {
            type.addDefaultConfig(e.getKey(), e.getValue().getValue(), e.getValue().getDesc(), e.getValue().getDefaultVal(), e.getValue().getInput(), e.getValue().getInputParams());
        }
    }

    public void addInstanceType(final MainEventInstanceType type) {
        this._types.put(type.getId(), type);
    }

    public void removeInstanceType(final MainEventInstanceType type) {
        this._types.remove(type.getId());
    }

    public MainEventInstanceType getInstanceType(final int id) {
        return this._types.get(id);
    }

    public Map<Integer, MainEventInstanceType> getInstanceTypes() {
        return this._types;
    }

    public InstanceData[] getInstances() {
        return this._instances;
    }

    public int getTeamsCountInInstance(final int instance) {
        return this._teams.get(instance).size();
    }

    protected void tryFirstBlood(final PlayerEventInfo killer) {
        synchronized (this.firstBloodLock) {
            if (!this._firstBlood) {
                final RewardPosition[] rewardTypes = this.getRewardTypes();
                final int length = rewardTypes.length;
                int i = 0;
                while (i < length) {
                    final RewardPosition pos = rewardTypes[i];
                    if (pos == RewardPosition.FirstBlood) {
                        this._firstBloodPlayer = killer;
                        if (this.getBoolean("firstBloodMessage")) {
                            this.screenAnnounce(killer.getInstanceId(), LanguageEngine.getMsg("event_firstBlood", killer.getPlayersName()));
                        }
                        EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, killer, RewardPosition.FirstBlood, null, 0, 0, 0);
                        if (SunriseLoader.detailedDebug) {
                            this.print("AbstractMainEvent: FIRST BLOOD reward given to " + killer.getPlayersName());
                            break;
                        }
                        break;
                    }
                    else {
                        ++i;
                    }
                }
                this._firstBlood = true;
            }
        }
    }

    protected void giveOnKillReward(final PlayerEventInfo killer) {
        for (final RewardPosition pos : this.getRewardTypes()) {
            if (pos == RewardPosition.OnKill) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, killer, RewardPosition.OnKill, null, 0, 0, 0);
                break;
            }
        }
    }

    protected void giveKillingSpreeReward(final EventPlayerData killerData) {
        if (killerData instanceof PvPEventPlayerData) {
            final int spree = ((PvPEventPlayerData)killerData).getSpree();
            if (EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, killerData.getOwner(), RewardPosition.KillingSpree, String.valueOf(spree), 0, 0, 0)) {
                killerData.getOwner().sendMessage("You have been awarded for your " + spree + " kills in row!");
            }
        }
    }

    public String getScorebarCb(final int instance) {
        final int teamsCount = this.getTeamsCountInInstance(instance);
        final StringBuilder tb = new StringBuilder();
        if (teamsCount > 1) {
            tb.append("<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center><font color=ac9887>Score:</font> ");
            int i = 0;
            for (final EventTeam team : this._teams.get(instance).values()) {
                ++i;
                if (teamsCount > 3) {
                    if (i != teamsCount) {
                        tb.append("<font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">" + team.getTeamName() + "</font><font color=9f9f9f> - " + team.getScore() + "  |  </font>");
                    }
                    else {
                        tb.append("<font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">" + team.getTeamName() + "</font><font color=9f9f9f> - " + team.getScore() + "</font>");
                    }
                }
                else if (i != teamsCount) {
                    tb.append("<font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">" + team.getFullName() + "</font><font color=9f9f9f> - " + team.getScore() + "  |  </font>");
                }
                else {
                    tb.append("<font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">" + team.getFullName() + "</font><font color=9f9f9f> - " + team.getScore() + "</font>");
                }
            }
            tb.append("</td></tr></table>");
        }
        return tb.toString();
    }

    public String getEventInfoCb(final int instance, final Object param) {
        final StringBuilder tb = new StringBuilder();
        try {
            final int teamsCount = this.getTeamsCountInInstance(instance);
            final List<EventTeam> teams = new LinkedList<EventTeam>();
            teams.addAll(this._teams.get(instance).values());
            Collections.sort(teams, EventManager.getInstance().compareTeamScore);
            if (teamsCount == 2) {
                tb.append(this.addExtraEventInfoCb(instance));
                tb.append("<br><img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<table width=510 bgcolor=2E2E2E>");
                boolean firstTeam = true;
                for (final EventTeam team : teams) {
                    if (firstTeam) {
                        tb.append("<tr><td width=250 align=center><font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">1. " + team.getFullName() + "</font> <font color=6f6f6f>(" + team.getPlayers().size() + " players; " + team.getAverageLevel() + " avg lvl)</font></td>");
                    }
                    else {
                        tb.append("<td width=10></td><td width=250 align=center><font color=" + EventManager.getInstance().getTeamColorForHtml(team.getTeamId()) + ">2. " + team.getFullName() + "</font> <font color=6f6f6f>(" + team.getPlayers().size() + " players; " + team.getAverageLevel() + " avg lvl)</font></td></tr>");
                    }
                    firstTeam = false;
                }
                tb.append("<tr></tr>");
                int countTopScorers = this._countOfShownTopPlayers;
                final Map<Integer, List<PlayerEventInfo>> topPlayers = new ConcurrentHashMap<Integer, List<PlayerEventInfo>>();
                final List<PlayerEventInfo> temp = new LinkedList<PlayerEventInfo>();
                int counter = 0;
                for (final EventTeam team2 : teams) {
                    topPlayers.put(team2.getTeamId(), new LinkedList<PlayerEventInfo>());
                    temp.addAll(team2.getPlayers());
                    Collections.sort(temp, EventManager.getInstance().comparePlayersScore);
                    if (temp.size() < countTopScorers) {
                        countTopScorers = temp.size();
                    }
                    for (final PlayerEventInfo player : temp) {
                        topPlayers.get(team2.getTeamId()).add(player);
                        if (++counter >= countTopScorers) {
                            break;
                        }
                    }
                    temp.clear();
                    counter = 0;
                }
                firstTeam = true;
                for (int i = 0; i < countTopScorers; ++i) {
                    if (firstTeam) {
                        final PlayerEventInfo tempPlayer = topPlayers.get(1).get(i);
                        tb.append("<tr><td width=250 align=center><font color=9f9f9f>" + (i + 1) + ". " + tempPlayer.getPlayersName() + "</font><font color=" + EventManager.getInstance().getDarkColorForHtml(1) + "> - " + tempPlayer.getScore() + " score</font></td>");
                    }
                    else {
                        final PlayerEventInfo tempPlayer = topPlayers.get(2).get(i);
                        tb.append("<td width=10></td><td width=250 align=center><font color=9f9f9f>" + (i + 1) + ". " + tempPlayer.getPlayersName() + "</font><font color=" + EventManager.getInstance().getDarkColorForHtml(2) + "> - " + tempPlayer.getScore() + " score</font></td></tr>");
                    }
                    firstTeam = !firstTeam;
                    if (firstTeam) {}
                }
                tb.append("</table>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
            }
            else if (teamsCount == 1) {
                tb.append(this.addExtraEventInfoCb(instance));
                tb.append("<br><img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<table width=510 bgcolor=2E2E2E>");
                final List<PlayerEventInfo> tempPlayers = new LinkedList<PlayerEventInfo>();
                tempPlayers.addAll(this.getPlayers(instance));
                Collections.sort(tempPlayers, EventManager.getInstance().comparePlayersScore);
                final int countTopPlayers = this._countOfShownTopPlayers;
                int j = 0;
                for (final PlayerEventInfo player2 : tempPlayers) {
                    String kd = String.valueOf((player2.getDeaths() != 0) ? (player2.getKills() / (double)player2.getDeaths()) : player2.getKills());
                    kd = kd.substring(0, Math.min(3, kd.length()));
                    tb.append("<tr><td width=510 align=center><font color=9f9f9f>" + (j + 1) + ".</font> <font color=ac9887>" + player2.getPlayersName() + "</font><font color=7f7f7f> - " + player2.getScore() + " points</font>  <font color=5f5f5f>(K:D ratio: " + kd + ")</font></td>");
                    tb.append("</tr>");
                    if (++j >= countTopPlayers) {
                        break;
                    }
                }
                tb.append("</table>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
            }
            else if (teamsCount > 2) {
                final int page = (int)((param != null && param instanceof Integer) ? param : 1);
                final int maxPages = (int)Math.ceil(teamsCount - 1);
                int countTopScorers2 = this._countOfShownTopPlayers;
                int shownTeam1Id = 1;
                int shownTeam2Id = 2;
                if (page > 1) {
                    shownTeam1Id += page - 1;
                    shownTeam2Id += page - 1;
                }
                tb.append(this.addExtraEventInfoCb(instance));
                tb.append("<br><img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<table width=510 bgcolor=2E2E2E>");
                boolean firstTeam2 = true;
                for (final EventTeam team3 : teams) {
                    if (team3.getTeamId() == shownTeam1Id || team3.getTeamId() == shownTeam2Id) {
                        if (firstTeam2) {
                            tb.append("<tr><td width=250 align=center><font color=" + EventManager.getInstance().getTeamColorForHtml(team3.getTeamId()) + ">" + shownTeam1Id + ". " + team3.getFullName() + "</font> <font color=6f6f6f>(" + team3.getPlayers().size() + " players; " + team3.getAverageLevel() + " avg lvl)</font></td>");
                        }
                        else {
                            tb.append("<td width=10></td><td width=250 align=center><font color=" + EventManager.getInstance().getTeamColorForHtml(team3.getTeamId()) + ">" + shownTeam2Id + ". " + team3.getFullName() + "</font> <font color=6f6f6f>(" + team3.getPlayers().size() + " players; " + team3.getAverageLevel() + " avg lvl)</font></td></tr>");
                        }
                        firstTeam2 = false;
                    }
                }
                tb.append("<tr></tr>");
                final Map<Integer, List<PlayerEventInfo>> topPlayers2 = new ConcurrentHashMap<Integer, List<PlayerEventInfo>>();
                final List<PlayerEventInfo> temp2 = new LinkedList<PlayerEventInfo>();
                int counter2 = 0;
                for (final EventTeam team4 : teams) {
                    if (team4.getTeamId() == shownTeam1Id || team4.getTeamId() == shownTeam2Id) {
                        topPlayers2.put(team4.getTeamId(), new LinkedList<PlayerEventInfo>());
                        temp2.addAll(team4.getPlayers());
                        Collections.sort(temp2, EventManager.getInstance().comparePlayersScore);
                        if (temp2.size() < countTopScorers2) {
                            countTopScorers2 = temp2.size();
                        }
                        for (final PlayerEventInfo player3 : temp2) {
                            topPlayers2.get(team4.getTeamId()).add(player3);
                            if (++counter2 >= countTopScorers2) {
                                break;
                            }
                        }
                        temp2.clear();
                        counter2 = 0;
                    }
                }
                firstTeam2 = true;
                for (int k = 0; k < countTopScorers2; ++k) {
                    if (firstTeam2) {
                        final PlayerEventInfo tempPlayer2 = topPlayers2.get(shownTeam1Id).get(k);
                        tb.append("<tr><td width=250 align=center><font color=9f9f9f>" + (k + 1) + ". " + tempPlayer2.getPlayersName() + "</font><font color=" + EventManager.getInstance().getDarkColorForHtml(shownTeam1Id) + "> - " + tempPlayer2.getScore() + " score</font></td>");
                    }
                    else {
                        final PlayerEventInfo tempPlayer2 = topPlayers2.get(shownTeam2Id).get(k);
                        tb.append("<td width=10></td><td width=250 align=center><font color=9f9f9f>" + (k + 1) + ". " + tempPlayer2.getPlayersName() + "</font><font color=" + EventManager.getInstance().getDarkColorForHtml(shownTeam2Id) + "> - " + tempPlayer2.getScore() + " score</font></td></tr>");
                    }
                    firstTeam2 = !firstTeam2;
                    if (firstTeam2) {}
                }
                tb.append("</table>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                tb.append("<img src=\"L2UI.SquareGray\" width=512 height=2>");
                tb.append("<img src=\"L2UI.SquareBlank\" width=510 height=3>");
                boolean previousButton = false;
                boolean nextButton = false;
                if (page > 1) {
                    previousButton = true;
                }
                if (page < maxPages) {
                    nextButton = true;
                }
                if (nextButton && previousButton) {
                    tb.append("<table width=510 bgcolor=2E2E2E><tr><td width=200 align=left><button value=\"Prev page\" action=\"bypass -h " + EventHtmlManager.BBS_COMMAND + " nextpageteam " + (page - 1) + " " + instance + "\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td width=200 align=right><button value=\"Next page\" action=\"bypass -h " + EventHtmlManager.BBS_COMMAND + " nextpageteam " + (page + 1) + " " + instance + "\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
                }
                else if (nextButton) {
                    tb.append("<table width=510 bgcolor=2E2E2E><tr><td width=510 align=right><button value=\"Next page\" action=\"bypass -h " + EventHtmlManager.BBS_COMMAND + " nextpageteam " + (page + 1) + " " + instance + "\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
                }
                else if (previousButton) {
                    tb.append("<table width=510 bgcolor=2E2E2E><tr><td width=510 align=left><button value=\"Prev page\" action=\"bypass -h " + EventHtmlManager.BBS_COMMAND + " nextpageteam " + (page - 1) + " " + instance + "\" width=85 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>");
                }
                tb.append("</table>");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tb.toString();
    }

    protected String addExtraEventInfoCb(final int instance) {
        boolean firstBloodEnabled = false;
        for (final RewardPosition pos : this.getRewardTypes()) {
            if (pos == RewardPosition.FirstBlood) {
                firstBloodEnabled = true;
                break;
            }
        }
        if (firstBloodEnabled) {
            return "<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center><font color=CE7171>First blood:</font><font color=7f7f7f> " + ((this._firstBloodPlayer != null) ? this._firstBloodPlayer.getPlayersName() : "None yet") + "</font></td></tr></table>";
        }
        return "";
    }

    @Override
    public String getDescriptionForReward(final RewardPosition reward) {
        if (reward == RewardPosition.FirstRegistered) {
            final String type = this.getString("firstRegisteredRewardType");
            if (type.equals("All")) {
                return "The reward for the " + this.getInt("firstRegisteredRewardCount") + " first registered players, given in the end of the event. <br1>Check out event configs for more customization.";
            }
            if (type.equals("WinnersOnly")) {
                return "The reward for the " + this.getInt("firstRegisteredRewardCount") + " first registered players, given in the end of the event only if the players won the event. <br1>Check out event configs for more customization.";
            }
        }
        return null;
    }

    protected boolean canJoinInstance(final PlayerEventInfo player, final MainEventInstanceType instance) {
        final int minLvl = instance.getConfigInt("minLvl");
        final int maxLvl = instance.getConfigInt("maxLvl");
        if ((maxLvl != -1 && player.getLevel() > maxLvl) || player.getLevel() < minLvl) {
            return false;
        }
        final int minPvps = instance.getConfigInt("minPvps");
        final int maxPvps = instance.getConfigInt("maxPvps");
        if (player.getPvpKills() < minPvps || (maxPvps != -1 && player.getPvpKills() > maxPvps)) {
            return false;
        }
        player.sendMessage(LanguageEngine.getMsg("event_choosingInstance", instance.getName()));
        return true;
    }

    @Override
    public void playerWentAfk(final PlayerEventInfo player, final boolean warningOnly, final int afkTime) {
        if (warningOnly) {
            player.sendMessage(LanguageEngine.getMsg("event_afkWarning", PlayerEventInfo.AFK_WARNING_DELAY / 1000, PlayerEventInfo.AFK_KICK_DELAY / 1000));
        }
        else if (afkTime == 0) {
            player.sendMessage(LanguageEngine.getMsg("event_afkMarked"));
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: player " + player.getPlayersName() + " has just gone afk");
            }
        }
        else if (afkTime % 60 == 0) {
            player.sendMessage(LanguageEngine.getMsg("event_afkDurationInfo", afkTime / 60));
        }
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
    }

    @Override
    public void playerReturnedFromAfk(final PlayerEventInfo player) {
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
    }

    @Override
    public boolean addDisconnectedPlayer(final PlayerEventInfo player, final EventManager.DisconnectedPlayerData data) {
        final boolean added = false;
        if (data != null) {
            if (this._rejoinEventAfterDisconnect && this._manager.getState() == MainEventManager.State.RUNNING) {
                final AbstractEventInstance instance = this.getMatch(data.getInstance());
                if (instance != null && instance.isActive()) {
                    final EventTeam team = data.getTeam();
                    if (team != null) {
                        player.sendMessage(LanguageEngine.getMsg("registering_afterDisconnect_true"));
                        player.setIsRegisteredToMainEvent(true, this.getEventType());
                        synchronized (this._manager.getPlayers()) {
                            this._manager.getPlayers().add(player);
                        }
                        player.onEventStart(this);
                        this._teams.get(instance.getInstance().getId()).get(team.getTeamId()).addPlayer(player, true);
                        this.prepareDisconnectedPlayer(player);
                        this.respawnPlayer(player, instance.getInstance().getId());
                        if (this._removeWarningAfterReconnect) {
                            EventWarnings.getInstance().removeWarning(player, 1);
                        }
                    }
                }
            }
            else {
                player.sendMessage(LanguageEngine.getMsg("registering_afterDisconnect_false"));
            }
        }
        return added;
    }

    protected void prepareDisconnectedPlayer(final PlayerEventInfo player) {
        final boolean removeBuffs = this.getBoolean("removeBuffsOnStart");
        if (removeBuffs) {
            player.removeBuffs();
        }
        if (this._allowSchemeBuffer) {
            EventBuffer.getInstance().buffPlayer(player, true);
        }
        if (this._allowNoblesOnRess) {
            L2Skill noblesse = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(1323, 1);
            if (noblesse != null) {
                noblesse.getEffects(player.getOwner(), player.getOwner());
            }
        }
        if (this._removePartiesOnStart) {
            final PartyData pt = player.getParty();
            if (pt != null) {
                player.getParty().removePartyMember(player);
            }
        }
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
        }
    }

    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        if (player.isOnline()) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: player " + player.getPlayersName() + " (instance id = " + player.getInstanceId() + ") disconnecting from the event");
            }
            if (this._spectators != null && this._spectators.contains(player)) {
                synchronized (this._spectators) {
                    this._spectators.remove(player);
                }
                player.setInstanceId(0);
                player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            }
            final EventTeam team = player.getEventTeam();
            final EventPlayerData playerData = player.getEventData();
            player.restoreData();
            player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            EventWarnings.getInstance().addPoints(player.getPlayersId(), 1);
            boolean running = false;
            boolean allowRejoin = true;
            final AbstractEventInstance playersMatch = this.getMatch(player.getInstanceId());
            if (playersMatch == null) {
//                SunriseLoader.debug("Player's EventInstance is null, called onDisconnect", Level.WARNING);
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: !!! -.- player's EVENT INSTANCE is null after calling onDisconnect. Player's instanceId is = " + player.getInstanceId());
                }
                running = false;
            }
            else {
                running = playersMatch.isActive();
            }
            team.removePlayer(player);
            this._manager.getPlayers().remove(player);
            CallBack.getInstance().getPlayerBase().playerDisconnected(player);
            if (running) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: -.- event is active");
                }
                this.debug(this.getEventName() + ": Player " + player.getPlayersName() + " disconnected from " + this.getEventName() + " event.");
                if (team.getPlayers().isEmpty()) {
                    this.announce(player.getInstanceId(), LanguageEngine.getMsg("event_disconnect_team", team.getTeamName()));
                    allowRejoin = false;
                    this.debug(this.getEventName() + ": all players from team " + team.getTeamName() + " have disconnected.");
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: ALL PLAYERS FROM TEAM " + team.getTeamName() + " disconnected");
                    }
                }
                if (!this.checkIfEventCanContinue(player.getInstanceId(), player)) {
                    this.announce(player.getInstanceId(), LanguageEngine.getMsg("event_disconnect_all"));
                    this.endInstance(player.getInstanceId(), true, false, false);
                    allowRejoin = false;
                    this.debug(this.getEventName() + ": no players left in the teams, the fight can't continue. The event has been aborted!");
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: NO PLAYERS LEFT IN THE TEAMS, THE FIGHT CAN'T CONTINUE! (checkIfEventCanContinue = false)");
                    }
                    return;
                }
                if (allowRejoin && this.allowsRejoinOnDisconnect()) {
                    EventManager.getInstance().addDisconnectedPlayer(player, team, playerData, this);
                }
            }
            else if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: -.- event IS NOT active anymore");
            }
        }
    }

    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        int teamsOn = 0;
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi != null && pi.isOnline()) {
                    ++teamsOn;
                    break;
                }
            }
        }
        return teamsOn >= 2;
    }

    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        if (this.notAllovedItems != null && Arrays.binarySearch(this.notAllovedItems, item.getItemId()) >= 0) {
            player.sendMessage(LanguageEngine.getMsg("event_itemNotAllowed"));
            return false;
        }
        if (item.isPotion() && !this.getBoolean("allowPotions")) {
            return false;
        }
        if (item.isScroll()) {
            return false;
        }
        if (item.isPetCollar() && !this._allowPets) {
            player.sendMessage(LanguageEngine.getMsg("event_petsNotAllowed"));
            return false;
        }
        return true;
    }

    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return true;
    }

    @Override
    public void onItemUse(final PlayerEventInfo player, final ItemData item) {
    }

    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        if (this.notAllovedSkillls != null && Arrays.binarySearch(this.notAllovedSkillls, skill.getId()) >= 0) {
            player.sendMessage(LanguageEngine.getMsg("event_skillNotAllowed"));
            return false;
        }
        if (skill.getSkillType().equals("RESURRECT")) {
            return false;
        }
        if (skill.getSkillType().equals("RECALL")) {
            return false;
        }
        if (skill.getSkillType().equals("SUMMON_FRIEND")) {
            return false;
        }
        if (skill.getSkillType().equals("FAKE_DEATH")) {
            return false;
        }
        if (!this._allowSummons && skill.getSkillType().equals("SUMMON")) {
            player.sendMessage(LanguageEngine.getMsg("event_summonsNotAllowed"));
            return false;
        }
        return true;
    }

    @Override
    public void onSkillUse(final PlayerEventInfo player, final SkillData skill) {
        if (skill.getSkillType() != null && skill.getSkillType().equals("SUMMON")) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> EventBuffer.getInstance().buffPet(player), 2000L);
        }
    }

    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() != null && target.getEventInfo().getEvent() == player.getEvent() && (target.getEventInfo().getTeamId() == player.getTeamId() || (player.hasSummon() && target.isSummon() && player.getSummon() == target.getOwner()));
    }

    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() == null || (target.getEventInfo().getEvent() == player.getEvent() && target.getEventInfo().getTeamId() != player.getTeamId());
    }

    @Override
    public boolean onAttack(final CharacterData cha, final CharacterData target) {
        return true;
    }

    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        if (text.equals(".scheme")) {
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "none", this.getEventType().getAltTitle());
            return false;
        }
        return true;
    }

    @Override
    public boolean onNpcAction(final PlayerEventInfo player, final NpcData npc) {
        return false;
    }

    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
    }

    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
    }

    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
    }

    @Override
    public boolean canInviteToParty(final PlayerEventInfo player, final PlayerEventInfo target) {
        return target.getEvent() == player.getEvent() && player.canInviteToParty() && target.canInviteToParty() && target.getTeamId() == player.getTeamId();
    }

    @Override
    public boolean canTransform(final PlayerEventInfo player) {
        return true;
    }

    @Override
    public boolean canBeDisarmed(final PlayerEventInfo player) {
        return true;
    }

    @Override
    public int allowTransformationSkill(final PlayerEventInfo playerEventInfo, final SkillData skillData) {
        return 0;
    }

    @Override
    public boolean canSaveShortcuts(final PlayerEventInfo player) {
        return true;
    }

    public boolean isInEvent(final CharacterData ch) {
        return false;
    }

    public boolean allowKill(final CharacterData target, final CharacterData killer) {
        return true;
    }

    protected void clockTick() {
    }

    public boolean allowsRejoinOnDisconnect() {
        return true;
    }

    public class Clock implements Runnable
    {
        private final AbstractEventInstance _event;
        private int time;
        private boolean _announcesCountdown;
        protected ScheduledFuture<?> _task;

        public Clock(final AbstractEventInstance instance) {
            this._announcesCountdown = true;
            this._task = null;
            this._event = instance;
        }

        public String getTime() {
            final String mins = "" + this.time / 60;
            final String secs = "" + this.time % 60;
            return "" + mins + ":" + secs + "";
        }

        public void disableAnnouncingCountdown() {
            this._announcesCountdown = false;
        }

        @Override
        public void run() {
            try {
                AbstractMainEvent.this.clockTick();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (AbstractMainEvent.this._allowScoreBar && AbstractMainEvent.this._instances != null) {
                for (final InstanceData instance : AbstractMainEvent.this._instances) {
                    try {
                        AbstractMainEvent.this.scorebarText = AbstractMainEvent.this.getScorebar(instance.getId());
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                        if (SunriseLoader.detailedDebug) {
                            AbstractMainEvent.this.print("ERROR on CLOCK.getScorebar: " + SunriseLoader.getTraceString(e2.getStackTrace()));
                        }
                        if (SunriseLoader.detailedDebug) {
                            AbstractMainEvent.this.print("Event aborted");
                        }
                        for (final InstanceData ins : AbstractMainEvent.this._instances) {
                            AbstractMainEvent.this.announce(ins.getId(), LanguageEngine.getMsg("event_mysteriousError"));
                        }
                        AbstractMainEvent.this.clearEvent();
                        return;
                    }
                    if (AbstractMainEvent.this.scorebarText != null) {
                        for (final PlayerEventInfo player : AbstractMainEvent.this.getPlayers(instance.getId())) {
                            player.sendEventScoreBar(AbstractMainEvent.this.scorebarText);
                        }
                        if (AbstractMainEvent.this._spectators != null) {
                            for (final PlayerEventInfo spec : AbstractMainEvent.this._spectators) {
                                if (spec.getInstanceId() == instance.getId()) {
                                    spec.sendEventScoreBar(AbstractMainEvent.this.scorebarText);
                                }
                            }
                        }
                    }
                }
            }
            if (this._announcesCountdown) {
                switch (this.time) {
                    case 60:
                    case 300:
                    case 600:
                    case 1200:
                    case 1800: {
                        AbstractMainEvent.this.announce(this._event.getInstance().getId(), LanguageEngine.getMsg("event_countdown_min", this.time / 60));
                        break;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                    case 30: {
                        AbstractMainEvent.this.announce(this._event.getInstance().getId(), LanguageEngine.getMsg("event_countdown_sec", this.time));
                        break;
                    }
                }
            }
            if (this.time <= 0) {
                if (SunriseLoader.detailedDebug) {
                    AbstractMainEvent.this.print("AbstractMainEvent: Clock.time is " + this.time + ", scheduling next event task");
                }
                this._task = this._event.scheduleNextTask(0);
            }
            else {
                this.setTime(this.time - 1, false);
                this._task = CallBack.getInstance().getOut().scheduleGeneral(this, 1000L);
            }
        }

        public void abort() {
            if (this._task != null) {
                this._task.cancel(false);
            }
        }

        public synchronized void setTime(final int t, final boolean debug) {
            if (debug && SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: setting value of Clock.time to " + t);
            }
            this.time = t;
        }

        public void startClock(final int mt) {
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: starting Clock and setting Clock.time to " + mt);
            }
            this.time = mt;
            CallBack.getInstance().getOut().scheduleGeneral(this, 1L);
        }
    }

    private class ReviveTask implements Runnable
    {
        private final PlayerEventInfo player;
        private final int instance;

        protected ReviveTask(final PlayerEventInfo p, final int time) {
            this.player = p;
            this.instance = this.player.getInstanceId();
            CallBack.getInstance().getOut().scheduleGeneral(this, time);
            this.player.sendMessage(LanguageEngine.getMsg("event_revive", time / 1000));
        }

        @Override
        public void run() {
            if (this.player.getActiveEvent() != null && this.player.isDead()) {
                this.player.doRevive();
                if (AbstractMainEvent.this._allowSchemeBuffer) {
                    EventBuffer.getInstance().buffPlayer(this.player);
                    EventBuffer.getInstance().buffPet(this.player);
                }
                if (AbstractMainEvent.this._allowNoblesOnRess) {
                    L2Skill noblesse = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(1323, 1);
                    if (noblesse != null) {
                        noblesse.getEffects(player.getOwner(), player.getOwner());
                    }
                }
                this.player.setCurrentCp(this.player.getMaxCp());
                this.player.setCurrentHp(this.player.getMaxHp());
                this.player.setCurrentMp(this.player.getMaxMp());
                this.player.setTitle(AbstractMainEvent.this.getTitle(this.player), true);
                AbstractMainEvent.this.respawnPlayer(this.player, this.instance);
                if (AbstractMainEvent.this.getBoolean("removeBuffsOnRespawn")) {
                    this.player.removeBuffs();
                }
            }
        }

        protected ReviveTask(final PlayerEventInfo p) {
            this.player = p;
            this.instance = this.player.getInstanceId();
            CallBack.getInstance().getOut().executeTask(this);
        }
    }

    protected class WaweRespawnScheduler implements Runnable
    {
        private ScheduledFuture<?> _future;
        private final int _delay;
        private final List<PlayerEventInfo> _players;

        public WaweRespawnScheduler(final int delay) {
            this._players = new LinkedList<PlayerEventInfo>();
            this._delay = delay;
            this._future = CallBack.getInstance().getOut().scheduleGeneral(this, delay);
            this._players.clear();
        }

        public void addPlayer(final PlayerEventInfo player) {
            synchronized (this._players) {
                this._players.add(player);
            }
            player.screenMessage(LanguageEngine.getMsg("event_revive", Math.max(1L, this._future.getDelay(TimeUnit.SECONDS))), AbstractMainEvent.this.getEventType().getAltTitle(), true);
            player.sendMessage(LanguageEngine.getMsg("event_revive", Math.max(1L, this._future.getDelay(TimeUnit.SECONDS))));
        }

        public void stop() {
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: stopping wawe spawn scheduler");
            }
            this._players.clear();
            if (this._future != null) {
                this._future.cancel(false);
            }
            this._future = null;
        }

        @Override
        public void run() {
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: running wawe spawn scheduler...");
            }
            int count = 0;
            synchronized (this._players) {
                for (final PlayerEventInfo pi : this._players) {
                    if (pi != null && pi.isDead()) {
                        ++count;
                        new ReviveTask(pi);
                    }
                }
                this._players.clear();
            }
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: ...wawe scheduler respawned " + count + " players");
            }
            this._future = CallBack.getInstance().getOut().scheduleGeneral(this, this._delay);
        }
    }

    private class AddToParty implements Runnable
    {
        private final PartyData _party;
        private final PlayerEventInfo _player;

        public AddToParty(final PartyData party, final PlayerEventInfo player) {
            this._party = party;
            this._player = player;
        }

        @Override
        public void run() {
            try {
                if (this._party.exists()) {
                    if (this._player.getParty() != null) {
                        this._player.getParty().removePartyMember(this._player);
                    }
                    if (this._party.getMemberCount() >= AbstractMainEvent.this.getInt("maxPartySize")) {
                        return;
                    }
                    this._party.addPartyMember(this._player);
                }
                this._player.setCanInviteToParty(true);
            }
            catch (NullPointerException e) {
                e.printStackTrace();
                if (SunriseLoader.detailedDebug) {
                    AbstractMainEvent.this.print("AbstractMainEvent: error while adding players to the party: " + SunriseLoader.getTraceString(e.getStackTrace()));
                }
                AbstractMainEvent.this.debug("error while adding players to the party: " + SunriseLoader.getTraceString(e.getStackTrace()));
            }
        }
    }

    protected abstract class AbstractEventData
    {
        protected int _instanceId;

        protected AbstractEventData(final int instance) {
            this._instanceId = instance;
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: abstracteventdata created data for instanceId = " + instance);
            }
        }
    }

    protected abstract class AbstractEventInstance implements Runnable
    {
        protected InstanceData _instance;
        protected Clock _clock;
        protected boolean _canBeAborted;
        protected boolean _canRewardIfAborted;
        protected boolean _forceNotRewardThisInstance;
        protected ScheduledFuture<?> _task;

        public AbstractEventInstance(final InstanceData instance) {
            this._canBeAborted = false;
            this._canRewardIfAborted = false;
            this._forceNotRewardThisInstance = false;
            this._task = null;
            this._instance = instance;
            this._clock = new Clock(this);
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: created abstracteventinstance for instanceId " + instance.getId());
            }
        }

        public abstract boolean isActive();

        public void setCanBeAborted() {
            this._canBeAborted = true;
        }

        public void forceNotRewardThisInstance() {
            this._forceNotRewardThisInstance = true;
            synchronized (AbstractMainEvent.this._rewardedInstances) {
                AbstractMainEvent.this._rewardedInstances.add(this._instance.getId());
            }
        }

        public void setCanRewardIfAborted() {
            this._canRewardIfAborted = true;
        }

        public InstanceData getInstance() {
            return this._instance;
        }

        public Clock getClock() {
            return this._clock;
        }

        public ScheduledFuture<?> scheduleNextTask(final int time) {
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininstance: scheduling next task in " + time);
            }
            if (this._clock._task != null) {
                if (SunriseLoader.detailedDebug) {
                    AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininstane: _clock_task is not null");
                }
                this._clock._task.cancel(false);
                this._clock._task = null;
            }
            else if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininstance: _clock_task is NULL!");
            }
            if (time > 0) {
                this._task = CallBack.getInstance().getOut().scheduleGeneral(this, time);
            }
            else {
                CallBack.getInstance().getOut().executeTask(this);
            }
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: next task scheduled.");
            }
            return this._task;
        }

        public void abort() {
            if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininstance: aborting...");
            }
            if (this._task != null) {
                this._task.cancel(false);
                if (SunriseLoader.detailedDebug) {
                    AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininsance _task is not null");
                }
            }
            else if (SunriseLoader.detailedDebug) {
                AbstractMainEvent.this.print("AbstractMainEvent: abstractmaininstance _task is NULL!");
            }
            this._clock.abort();
        }
    }

    public abstract void runEvent();

    public abstract void onEventEnd();

    public abstract void clearEvent(int paramInt);

    protected abstract boolean instanceEnded();

    protected abstract void endInstance(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);

    protected abstract void respawnPlayer(PlayerEventInfo paramPlayerEventInfo, int paramInt);

    protected abstract String getScorebar(int paramInt);

    protected abstract String getTitle(PlayerEventInfo paramPlayerEventInfo);

    public abstract String getHtmlDescription();

    protected abstract AbstractEventInstance createEventInstance(InstanceData paramInstanceData);

    protected abstract AbstractEventInstance getMatch(int paramInt);

    protected abstract int initInstanceTeams(MainEventInstanceType paramMainEventInstanceType);

    protected abstract AbstractEventData createEventData(int paramInt);

    protected abstract AbstractEventData getEventData(int paramInt);

    public abstract String getMissingSpawns(EventMap paramEventMap);
}


