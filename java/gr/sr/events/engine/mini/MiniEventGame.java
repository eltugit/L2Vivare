package gr.sr.events.engine.mini;

import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.EventSpawn;
import gr.sr.events.engine.base.SpawnType;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.mini.features.*;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MiniEventGame implements Runnable, EventGame
{
    protected static Logger _log;
    public static final int MAP_GUARD_ID = 9996;
    protected int _instanceId;
    protected int _gameId;
    protected MiniEventManager _event;
    protected EventMap _arena;
    protected Announcer _announcer;
    private LocChecker _locCheckerInstance;
    protected ScheduledFuture<?> _locChecker;
    private static int _locCheckDelay;
    protected List<PlayerEventInfo> _spectators;
    protected List<PlayerEventInfo> _voted;
    protected List<FenceData> _fences;
    protected List<NpcData> _buffers;
    protected List<NpcData> _mapGuards;
    protected List<NpcData> _npcs;
    protected boolean _aborted;
    private int[] notAllovedSkillls;
    private int[] notAllovedItems;
    private int[] setOffensiveSkills;
    private int[] setNotOffensiveSkills;
    private int[] setNeutralSkills;
    protected boolean _allowSchemeBuffer;
    protected boolean _allowSummons;
    protected boolean _allowPets;
    
    public MiniEventGame(final int gameId, final EventMap arena, final MiniEventManager event, final RegistrationData[] teams) {
        this._locChecker = null;
        this._aborted = false;
        this._gameId = gameId;
        this._event = event;
        this._arena = arena;
        this._instanceId = 0;
    }
    
    public abstract int getInstanceId();
    
    public abstract EventTeam[] getTeams();
    
    protected void initAnnouncer() {
        (this._announcer = new Announcer()).setTime(System.currentTimeMillis() + this.getGameTime());
    }
    
    @Override
    public void run() {
        this.initEvent();
    }
    
    public void scheduleLocChecker() {
        if (this._locCheckerInstance == null) {
            this._locCheckerInstance = new LocChecker();
        }
        this._locChecker = CallBack.getInstance().getOut().scheduleGeneral(this._locCheckerInstance, MiniEventGame._locCheckDelay);
    }
    
    protected abstract void checkPlayersLoc();
    
    protected abstract void checkIfPlayersTeleported();
    
    public void addSpectator(final PlayerEventInfo player) {
        if (this._spectators == null) {
            this._spectators = new LinkedList<PlayerEventInfo>();
        }
        EventSpawn spectatorLoc = this.getMap().getNextSpawn(-1, SpawnType.Spectator);
        if (spectatorLoc == null) {
            spectatorLoc = this.getMap().getNextSpawn(-1, SpawnType.Regular);
        }
        if (spectatorLoc == null) {
            player.sendMessage(LanguageEngine.getMsg("observing_noSpawn"));
            return;
        }
        player.setIsSpectator(true);
        player.setActiveGame(this);
        player.removeSummon();
        player.removeCubics();
        if (player.getParty() != null) {
            player.getParty().removePartyMember(player);
        }
        player.setInstanceId(this._instanceId);
        player.enterObserverMode(spectatorLoc.getLoc().getX(), spectatorLoc.getLoc().getY(), spectatorLoc.getLoc().getZ());
        this._spectators.add(player);
    }
    
    public void removeSpectator(final PlayerEventInfo pi, final boolean disconnect) {
        if (!pi.isOnline()) {
            return;
        }
        if (!disconnect) {
            pi.removeObserveMode();
            CallBack.getInstance().getPlayerBase().eventEnd(pi);
        }
        this._spectators.remove(pi);
    }
    
    protected void cleanSpectators() {
        if (this._spectators != null) {
            for (final PlayerEventInfo pi : this._spectators) {
                this.removeSpectator(pi, false);
            }
        }
    }
    
    protected void initEvent() {
        this._instanceId = CallBack.getInstance().getOut().createInstance("Game " + this.getEvent().getEventName() + " ID" + this._gameId, this.getGameTime() + 59000, 0, true).getId();
        this.handleDoors(0);
        this.loadFences();
        CallBack.getInstance().getOut().spawnFences(this._fences, this._instanceId);
        this.loadNpcs();
        this.loadMapGuards();
        this.initAnnouncer();
        this._allowSchemeBuffer = EventConfig.getInstance().getGlobalConfigBoolean("eventSchemeBuffer");
        this._allowSummons = this.getEvent().getBoolean("allowSummons");
        this._allowPets = this.getEvent().getBoolean("allowPets");
        if (!this._event.getString("notAllowedSkills").equals("")) {
            final String[] splits = this._event.getString("notAllowedSkills").split(",");
            this.notAllovedSkillls = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.notAllovedSkillls[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.notAllovedSkillls);
            }
            catch (Exception ex) {}
        }
        if (!this._event.getString("notAllowedItems").equals("")) {
            final String[] splits = this._event.getString("notAllowedItems").split(",");
            this.notAllovedItems = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.notAllovedItems[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.notAllovedItems);
            }
            catch (Exception ex2) {}
        }
        this.loadOverridenSkillsParameters();
    }
    
    private void loadOverridenSkillsParameters() {
        String s = EventConfig.getInstance().getGlobalConfigValue("setOffensiveSkills");
        try {
            final String[] splits = s.split(";");
            this.setOffensiveSkills = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.setOffensiveSkills[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.setOffensiveSkills);
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setOffensiveSkills' in event " + this._event.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        catch (Exception e2) {
            SunriseLoader.debug("Error while loading GLOBAL config 'setOffensiveSkills' in event " + this._event.getEventName() + " - " + e2.toString(), Level.SEVERE);
        }
        s = EventConfig.getInstance().getGlobalConfigValue("setNotOffensiveSkills");
        try {
            final String[] splits = s.split(";");
            this.setNotOffensiveSkills = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.setNotOffensiveSkills[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.setNotOffensiveSkills);
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setNotOffensiveSkills' in event " + this._event.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        catch (Exception e2) {
            SunriseLoader.debug("Error while loading GLOBAL config 'setNotOffensiveSkills' in event " + this._event.getEventName() + " - " + e2.toString(), Level.SEVERE);
        }
        s = EventConfig.getInstance().getGlobalConfigValue("setNeutralSkills");
        try {
            final String[] splits = s.split(";");
            this.setNeutralSkills = new int[splits.length];
            try {
                for (int i = 0; i < splits.length; ++i) {
                    this.setNeutralSkills[i] = Integer.parseInt(splits[i]);
                }
                Arrays.sort(this.setNeutralSkills);
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading GLOBAL config 'setNeutralSkills' in event " + this._event.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        catch (Exception e2) {
            SunriseLoader.debug("Error while loading GLOBAL config 'setNeutralSkills' in event " + this._event.getEventName() + " - " + e2.toString(), Level.SEVERE);
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
    
    protected void updateScore(final PlayerEventInfo player, final CharacterData killer) {
        player.raiseDeaths(1);
        player.getEventTeam().raiseDeaths(1);
        if (killer != null && killer.getEventInfo() != null) {
            if (killer.getEventInfo().getEventTeam() == null) {
                return;
            }
            killer.getEventInfo().raiseKills(1);
            killer.getEventInfo().getEventTeam().raiseKills(1);
        }
    }
    
    protected void startEvent() {
    }
    
    protected void setEndStatus(final PlayerEventInfo pi, final int status) {
    }
    
    public void applyStatsChanges() {
    }
    
    protected void onScore(final List<PlayerEventInfo> players, final int ammount) {
    }
    
    protected void abortDueToError(final String message) {
        this.broadcastMessage(message, false);
        this.clearEvent();
        EventManager.getInstance().debug(this._event.getEventType() + " match automatically aborted: " + message);
    }
    
    public void broadcastMessage(final String msg, final boolean abortable) {
        if (abortable && this._aborted) {
            return;
        }
        for (final EventTeam team : this.getTeams()) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                pi.screenMessage(msg, this.getEvent().getEventName(), false);
            }
        }
        if (this._spectators != null) {
            for (final PlayerEventInfo pi2 : this._spectators) {
                pi2.screenMessage(msg, this.getEvent().getEventName(), false);
            }
        }
    }
    
    protected boolean checkTeamStatus(final int teamId) {
        for (final PlayerEventInfo pi : this.getTeams()[teamId - 1].getPlayers()) {
            if (pi.isOnline() && !pi.isDead()) {
                return true;
            }
        }
        return false;
    }
    
    protected void loadFences() {
        try {
            this._fences = new LinkedList<FenceData>();
            for (final EventSpawn spawn : this._arena.getSpawns(0, SpawnType.Fence)) {
                this._fences.add(CallBack.getInstance().getOut().createFence(2, spawn.getFenceWidth(), spawn.getFenceLength(), spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), this._arena.getGlobalId()));
            }
        }
        catch (NullPointerException ex) {}
    }
    
    private void loadMapGuards() {
        final int id = EventConfig.getInstance().getGlobalConfigInt("mapGuardNpcId");
        if (id == -1) {
            return;
        }
        final NpcTemplateData template = new NpcTemplateData(id);
        if (!template.exists()) {
            MiniEventGame._log.warning("Missing template for EventMap Guard.");
            return;
        }
        for (final EventSpawn spawn : this.getMap().getSpawns()) {
            if (spawn.getSpawnType() == SpawnType.MapGuard) {
                try {
                    final NpcData data = template.doSpawn(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 1, this._instanceId);
                    if (this._mapGuards == null) {
                        this._mapGuards = new LinkedList<NpcData>();
                    }
                    this._mapGuards.add(data);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected void loadNpcs() {
        for (final EventSpawn spawn : this.getMap().getSpawns(-1, SpawnType.Npc)) {
            try {
                final int npcId = spawn.getNpcId();
                if (npcId == -1) {
                    continue;
                }
                final NpcData data = new NpcTemplateData(npcId).doSpawn(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 1, this._instanceId);
                if (this._npcs == null) {
                    this._npcs = new LinkedList<NpcData>();
                }
                this._npcs.add(data);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    protected void unspawnNpcs() {
        if (this._npcs != null) {
            for (final NpcData npc : this._npcs) {
                if (npc != null) {
                    npc.deleteMe();
                }
            }
            this._npcs.clear();
        }
    }
    
    protected void loadBuffers() {
        try {
            int bufferId = EventConfig.getInstance().getGlobalConfigInt("npcBufferId");
            for (final AbstractFeature feature : this._event.getMode().getFeatures()) {
                if (feature.getType() == EventMode.FeatureType.Buffer) {
                    if (!((BufferFeature)feature).canSpawnBuffer()) {
                        return;
                    }
                    if (((BufferFeature)feature).getCustomNpcBufferId() == 0) {
                        continue;
                    }
                    bufferId = ((BufferFeature)feature).getCustomNpcBufferId();
                }
            }
            if (bufferId == -1) {
                return;
            }
            final NpcTemplateData template = new NpcTemplateData(bufferId);
            if (!template.exists()) {
                MiniEventGame._log.warning("Missing NPC Buffer's template (ID " + bufferId + ") for event system.");
                return;
            }
            for (final EventSpawn spawn : this._arena.getSpawns()) {
                if (spawn.getSpawnType() == SpawnType.Buffer) {
                    final NpcData data = template.doSpawn(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ(), 1, this._instanceId);
                    if (this._buffers == null) {
                        this._buffers = new LinkedList<NpcData>();
                    }
                    this._buffers.add(data);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void unspawnBuffers() {
        if (this._buffers != null) {
            for (final NpcData npc : this._buffers) {
                if (npc != null) {
                    npc.deleteMe();
                }
            }
            this._buffers.clear();
        }
    }
    
    protected void unspawnMapGuards() {
        if (this._mapGuards != null) {
            for (final NpcData npc : this._mapGuards) {
                if (npc != null) {
                    npc.deleteMe();
                }
            }
            this._mapGuards.clear();
        }
    }
    
    protected void handleDoors(final int state) {
        if (!this._arena.hasDoor()) {
            return;
        }
        if (state == 0) {
            for (final EventSpawn doorSpawn : this._arena.getDoors()) {
                final DoorAction action = DoorAction.getAction(doorSpawn.getNote(), 1);
                CallBack.getInstance().getOut().addDoorToInstance(this._instanceId, doorSpawn.getDoorId(), action == DoorAction.Open);
            }
        }
        else {
            for (final DoorData door : CallBack.getInstance().getOut().getInstanceDoors(this._instanceId)) {
                for (final EventSpawn doorSpawn2 : this._arena.getDoors()) {
                    final DoorAction action2 = DoorAction.getAction(doorSpawn2.getNote(), state);
                    if (doorSpawn2.getDoorId() == door.getDoorId()) {
                        if (action2 == DoorAction.Close && door.isOpened()) {
                            door.closeMe();
                        }
                        else {
                            if (action2 != DoorAction.Open || door.isOpened()) {
                                continue;
                            }
                            door.openMe();
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void playerWentAfk(final PlayerEventInfo player, final boolean warningOnly, final int afkTime) {
        if (warningOnly) {
            player.sendMessage(LanguageEngine.getMsg("event_afkWarning", PlayerEventInfo.AFK_WARNING_DELAY / 1000, PlayerEventInfo.AFK_KICK_DELAY / 1000));
        }
        else if (afkTime == 0) {
            player.sendMessage(LanguageEngine.getMsg("event_afkMarked"));
        }
        else if (afkTime % 60 == 0) {
            player.sendMessage(LanguageEngine.getMsg("event_afkDurationInfo", afkTime / 60));
        }
    }
    
    @Override
    public void playerReturnedFromAfk(final PlayerEventInfo player) {
    }
    
    protected void scheduleClearEvent(final int delay) {
        CallBack.getInstance().getOut().scheduleGeneral(() -> this.clearEvent(), 8000L);
    }
    
    protected void startAnnouncing() {
        if (this._announcer != null) {
            this._announcer.announce = true;
        }
    }
    
    public EventMap getMap() {
        return this._arena;
    }
    
    public int getGameId() {
        return this._gameId;
    }
    
    public MiniEventManager getEvent() {
        return this._event;
    }
    
    protected void saveGlobalStats() {
        final Map<PlayerEventInfo, GlobalStatsModel> stats = new ConcurrentHashMap<PlayerEventInfo, GlobalStatsModel>();
        for (final EventTeam team : this.getTeams()) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.COUNT_PLAYED, 1);
                stats.put(pi, this.getPlayerData(pi).getGlobalStats());
            }
        }
        EventStatsManager.getInstance().getGlobalStats().updateGlobalStats(stats);
    }
    
    protected void scheduleMessage(final String message, final int delay, final boolean abortable) {
        CallBack.getInstance().getOut().scheduleGeneral(() -> this.broadcastMessage(message, abortable), delay);
    }
    
    protected String getRoundName(final int round, final int maxRounds) {
        if (round == maxRounds) {
            return LanguageEngine.getMsg("round_final");
        }
        switch (round) {
            case 1: {
                return LanguageEngine.getMsg("round_1");
            }
            case 2: {
                return LanguageEngine.getMsg("round_2");
            }
            case 3: {
                return LanguageEngine.getMsg("round_3");
            }
            case 4: {
                return LanguageEngine.getMsg("round_4");
            }
            case 5: {
                return LanguageEngine.getMsg("round_5");
            }
            case 6: {
                return LanguageEngine.getMsg("round_6");
            }
            case 7: {
                return LanguageEngine.getMsg("round_7");
            }
            case 8: {
                return LanguageEngine.getMsg("round_8");
            }
            case 9: {
                return LanguageEngine.getMsg("round_9");
            }
            case 10: {
                return LanguageEngine.getMsg("round_10");
            }
            default: {
                return round + "th";
            }
        }
    }
    
    protected int getGameTime() {
        for (final AbstractFeature f : this._event.getMode().getFeatures()) {
            if (f.getType() == EventMode.FeatureType.TimeLimit) {
                return ((TimeLimitFeature)f).getTimeLimit();
            }
        }
        return this._event.getInt("TimeLimitMs");
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
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
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() != null && target.getEventInfo().getEvent() == player.getEvent() && target.getEventInfo().getTeamId() == player.getTeamId();
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
    }
    
    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
    }
    
    @Override
    public void onDisconnect(final PlayerEventInfo player) {
    }
    
    @Override
    public boolean addDisconnectedPlayer(final PlayerEventInfo player, final EventManager.DisconnectedPlayerData data) {
        return false;
    }
    
    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        if (text.equals(".scheme")) {
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "none", this.getEvent().getEventType().getAltTitle());
            return false;
        }
        if (text.equalsIgnoreCase(".voteabort") || text.equalsIgnoreCase(".voteend")) {
            this.voteEnd(player);
            return false;
        }
        return true;
    }
    
    private void voteEnd(final PlayerEventInfo player) {
        if (this._voted == null) {
            this._voted = new LinkedList<PlayerEventInfo>();
        }
        if (!this._voted.contains(player)) {
            this._voted.add(player);
            this.broadcastMessage("A player voted to end this mini event.", true);
            for (final EventTeam t : this.getTeams()) {
                for (final PlayerEventInfo p : t.getPlayers()) {
                    if (!this._voted.contains(p)) {
                        return;
                    }
                }
            }
            this.abortDueToError("Players voted to abort this match.");
        }
    }
    
    @Override
    public boolean onNpcAction(final PlayerEventInfo player, final NpcData npc) {
        return true;
    }
    
    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        if (this.notAllovedItems != null && Arrays.binarySearch(this.notAllovedItems, item.getItemId()) >= 0) {
            player.sendMessage(LanguageEngine.getMsg("event_itemNotAllowed"));
            return false;
        }
        if (item.isScroll()) {
            return false;
        }
        if (item.isPotion() && !this._event.getBoolean("allowPotions")) {
            return false;
        }
        for (final AbstractFeature f : this.getEvent().getMode().getFeatures()) {
            if (f.getType() == EventMode.FeatureType.ItemGrades && !((ItemGradesFeature)f).checkItem(player, item)) {
                return false;
            }
            if (f.getType() == EventMode.FeatureType.Items && !((ItemsFeature)f).checkItem(player, item)) {
                return false;
            }
            if (f.getType() == EventMode.FeatureType.Enchant && !((EnchantFeature)f).checkItem(player, item)) {
                return false;
            }
        }
        if (item.isPetCollar() && !this._allowPets) {
            player.sendMessage(LanguageEngine.getMsg("event_petsNotAllowed"));
            return false;
        }
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
        for (final AbstractFeature f : this.getEvent().getMode().getFeatures()) {
            if (f.getType() == EventMode.FeatureType.Skills && !((SkillsFeature)f).checkSkill(player, skill)) {
                return false;
            }
        }
        if (!this._allowSummons && skill.getSkillType().equals("SUMMON")) {
            player.sendMessage(LanguageEngine.getMsg("event_summonsNotAllowed"));
            return false;
        }
        return true;
    }
    
    @Override
    public void onSkillUse(final PlayerEventInfo player, final SkillData skill) {
    }
    
    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return true;
    }
    
    @Override
    public boolean canInviteToParty(final PlayerEventInfo player, final PlayerEventInfo target) {
        return target.getEvent() == player.getEvent() && target.getTeamId() == player.getTeamId();
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
    public int allowTransformationSkill(final PlayerEventInfo player, final SkillData skill) {
        return 0;
    }
    
    @Override
    public boolean canSaveShortcuts(final PlayerEventInfo player) {
        return true;
    }
    
    static {
        MiniEventGame._log = Logger.getLogger(MiniEventGame.class.getName());
        MiniEventGame._locCheckDelay = 10000;
    }
    
    public class Announcer implements Runnable
    {
        private long _start;
        boolean announce;
        private ScheduledFuture<?> _nextAnnounce;
        
        protected Announcer() {
            this.announce = false;
        }
        
        public void setTime(final long startTime) {
            this._start = startTime;
            this.run();
        }
        
        @Override
        public void run() {
            final int delay = (int)Math.round((this._start - System.currentTimeMillis()) / 1000.0);
            if (this.announce && delay > 0) {
                this.announce(delay);
            }
            int nextMsg = 0;
            if (delay > 3600) {
                nextMsg = delay - 3600;
            }
            else if (delay > 1800) {
                nextMsg = delay - 1800;
            }
            else if (delay > 900) {
                nextMsg = delay - 900;
            }
            else if (delay > 600) {
                nextMsg = delay - 600;
            }
            else if (delay > 300) {
                nextMsg = delay - 300;
            }
            else if (delay > 60) {
                nextMsg = delay - 60;
            }
            else {
                if (delay <= 10) {
                    return;
                }
                nextMsg = delay - 10;
            }
            if (delay > 0) {
                this._nextAnnounce = CallBack.getInstance().getOut().scheduleGeneral(this, nextMsg * 1000);
            }
        }
        
        private void announce(final int delay) {
            if (delay >= 3600 && delay % 3600 == 0) {
                final int d = delay / 3600;
                MiniEventGame.this.broadcastMessage(LanguageEngine.getMsg("game_countdown", d, "hour" + ((d == 1) ? "" : "s")), false);
            }
            else if (delay >= 60) {
                final int d = delay / 60;
                MiniEventGame.this.broadcastMessage(LanguageEngine.getMsg("game_countdown", d, "minute" + ((d == 1) ? "" : "s")), false);
            }
            else {
                MiniEventGame.this.broadcastMessage(LanguageEngine.getMsg("game_countdown", delay, "second" + ((delay == 1) ? "" : "s")), false);
            }
        }
        
        public void cancel() {
            if (this._nextAnnounce != null) {
                this._nextAnnounce.cancel(false);
            }
        }
    }
    
    private class LocChecker implements Runnable
    {
        protected LocChecker() {
        }
        
        @Override
        public void run() {
            try {
                MiniEventGame.this.checkPlayersLoc();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            MiniEventGame.this.scheduleLocChecker();
        }
    }
}
