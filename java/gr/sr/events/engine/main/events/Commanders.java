package gr.sr.events.engine.main.events;

import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Commanders extends TeamVsTeam
{
    protected int _baseNpcId;
    protected int _countOfSuperiorTeams;
    protected int _tick;
    private final Map<Integer, Integer> _skillsForAll;
    
    public Commanders(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._skillsForAll = new ConcurrentHashMap<Integer, Integer>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("skillsForAllPlayers", "35100-1", "IDs of skills which will be given to players on the event. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35000-1').", ConfigModel.InputType.MultiAdd));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        if (!this.getString("skillsForAllPlayers").equals("")) {
            final String[] splits = this.getString("skillsForAllPlayers").split(",");
            this._skillsForAll.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForAll.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForAllPlayers' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        this._tick = 0;
    }
    
    protected int getCountOfTeams(final int instanceId) {
        final int countOfPlayers = this.getPlayers(instanceId).size();
        int countOfTeams = countOfPlayers / 50;
        if (countOfTeams % 2 != 0) {
            --countOfTeams;
        }
        return countOfTeams;
    }
    
    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this._teamsCount = this.getCountOfTeams(type.getInstance().getId());
        if (this._teamsCount < 2) {
            this._teamsCount = 2;
        }
        this.createTeams(this._teamsCount, type.getInstance().getId());
        return this._teamsCount;
    }
    
    @Override
    protected void createTeams(final int count, final int instanceId) {
        try {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: creating " + count + " teams for instanceId " + instanceId);
            }
            for (int i = 0; i < count; ++i) {
                this.createNewTeam(instanceId, count + 1, "Noneyet", "Noneyet");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void createNewTeam(final int instanceId, final int id, final String name, final String fullName) {
        this._teams.get(instanceId).put(id, new EventTeam(id, name, fullName));
    }
    
    protected void preparePlayers(final int instanceId, final boolean start) {
    }
    
    protected void setupTeams(final int instanceId, final int maxSuperTeams) {
        int superTeam = 1;
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            for (final PlayerEventInfo player : team.getPlayers()) {
                this.getPlayerData(player).setSuperTeam(superTeam);
            }
            if (superTeam == maxSuperTeams) {
                superTeam = 1;
            }
            ++superTeam;
        }
    }
    
    protected void spawnCommanderStuff(final int instanceId, final boolean spawn) {
        if (spawn) {
            this.clearMapHistory(-1, SpawnType.Base);
            for (final EventTeam team : this._teams.get(instanceId).values()) {
                final EventSpawn sp = this.getSpawn(SpawnType.Base, team.getTeamId());
                final NpcData base = this.spawnNPC(sp.getLoc().getX(), sp.getLoc().getY(), sp.getLoc().getZ(), this._baseNpcId, instanceId, "Base", "Team Base");
                this.getEventData(instanceId).setBase(team.getTeamId(), base);
            }
        }
        else {
            NpcData base = null;
            for (final EventTeam team : this._teams.get(instanceId).values()) {
                base = this.getEventData(instanceId).getBase(team.getTeamId());
                if (base != null) {
                    base.deleteMe();
                }
            }
        }
    }
    
    protected void setNewCommander(final int instanceId, final PlayerEventInfo newCommander, final int teamId) {
        this.announce(instanceId, "*** Your commander is " + newCommander.getPlayersName(), teamId);
    }
    
    protected void commanderAction(final int instanceId, final int teamid, final String action) {
    }
    
    protected void hiveDead(final int instanceId, final PlayerEventInfo newCommander, final int teamId) {
    }
    
    protected void handleSkills(final int instanceId, final boolean add) {
        if (this._skillsForAll != null) {
            SkillData skill = null;
            for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                if (add) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
                else {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.removeSkill(skill.getId());
                        }
                    }
                }
            }
        }
    }
    
    @Override
    protected void clockTick() {
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        if (player.getTeamId() != target.getEventInfo().getTeamId()) {
            this.tryFirstBlood(player);
            this.giveOnKillReward(player);
            player.getEventTeam().raiseScore(1);
            player.getEventTeam().raiseKills(1);
            this.getPlayerData(player).raiseScore(1);
            this.getPlayerData(player).raiseKills(1);
            this.getPlayerData(player).raiseSpree(1);
            this.giveKillingSpreeReward(this.getPlayerData(player));
            if (player.isTitleUpdated()) {
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
            this.setScoreStats(player, this.getPlayerData(player).getScore());
            this.setKillsStats(player, this.getPlayerData(player).getKills());
        }
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: onDie - player " + player.getPlayersName() + " (instance " + player.getInstanceId() + "), killer " + killer.getName());
        }
        this.getPlayerData(player).raiseDeaths(1);
        this.getPlayerData(player).setSpree(0);
        this.setDeathsStats(player, this.getPlayerData(player).getDeaths());
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
        if (this._waweRespawn) {
            this._waweScheduler.addPlayer(player);
        }
        else {
            this.scheduleRevive(player, this.getInt("resDelay") * 1000);
        }
    }
    
    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
    }
    
    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return true;
        }
        if (target.getEventInfo().getEvent() != player.getEvent()) {
            return false;
        }
        if (this.getPlayerData(target.getEventInfo()).getSuperTeam() == this.getPlayerData(player).getSuperTeam()) {
            return false;
        }
        if (this.isCommander(player)) {
            player.sendMessage("The commander can't attack.");
            return false;
        }
        return true;
    }
    
    protected boolean isCommander(final PlayerEventInfo player) {
        return this.getPlayerData(player).isCommander();
    }
    
    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() != null && target.getEventInfo().getEvent() == player.getEvent() && this.getPlayerData(player).getSuperTeam() == this.getPlayerData(target.getEventInfo()).getSuperTeam();
    }
    
    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        return false;
    }
    
    @Override
    public synchronized void clearEvent(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called CLEAREVENT for instance " + instanceId);
        }
        try {
            if (this._matches != null) {
                for (final TvTEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        match.abort();
                        this.spawnCommanderStuff(match.getInstance().getId(), false);
                        this.handleSkills(match.getInstance().getId(), false);
                        this.preparePlayers(match.getInstance().getId(), false);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (player.isOnline()) {
                if (player.isParalyzed()) {
                    player.setIsParalyzed(false);
                }
                if (player.isImmobilized()) {
                    player.unroot();
                }
                if (!player.isGM()) {
                    player.setIsInvul(false);
                }
                player.removeRadarAllMarkers();
                player.setInstanceId(0);
                if (this._removeBuffsOnEnd) {
                    player.removeBuffs();
                }
                player.restoreData();
                player.teleport(player.getOrigLoc(), 0, true, 0);
                player.sendMessage(LanguageEngine.getMsg("event_teleportBack"));
                if (player.getParty() != null) {
                    final PartyData party = player.getParty();
                    party.removePartyMember(player);
                }
                player.broadcastUserInfo();
            }
        }
        this.clearPlayers(true, instanceId);
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            }
            else {
                this._htmlDescription = this.getInt("teamsCount") + " teams fighting against each other. ";
                this._htmlDescription += "Gain score by killing your opponents";
                if (this.getInt("killsForReward") > 0) {
                    this._htmlDescription = this._htmlDescription + " (at least " + this.getInt("killsForReward") + " kill(s) is required to receive a reward)";
                }
                if (this.getBoolean("waweRespawn")) {
                    this._htmlDescription = this._htmlDescription + " and dead players are resurrected by an advanced wawe-spawn engine each " + this.getInt("resDelay") + " seconds";
                }
                else {
                    this._htmlDescription = this._htmlDescription + " and if you die, you will be resurrected in " + this.getInt("resDelay") + " seconds";
                }
                if (this.getBoolean("createParties")) {
                    this._htmlDescription += ". The event automatically creates parties on start";
                }
                this._htmlDescription += ".";
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new CommandersPlayerData(player, this);
        return d;
    }
    
    @Override
    public CommandersPlayerData getPlayerData(final PlayerEventInfo player) {
        return (CommandersPlayerData)player.getEventData();
    }
    
    @Override
    protected TvTEventData createEventData(final int instanceId) {
        return new ComsEventData(instanceId);
    }
    
    @Override
    protected ComsEventInstance createEventInstance(final InstanceData instance) {
        return new ComsEventInstance(instance);
    }
    
    @Override
    protected ComsEventData getEventData(final int instance) {
        return (ComsEventData)this._matches.get(instance)._data;
    }
    
    public class CommandersPlayerData extends PvPEventPlayerData
    {
        boolean _commander;
        int _superTeam;
        
        public CommandersPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event, new GlobalStatsModel(Commanders.this.getEventType()));
            this._commander = false;
        }
        
        protected void setSuperTeam(final int i) {
            this._superTeam = i;
        }
        
        protected int getSuperTeam() {
            return this._superTeam;
        }
        
        protected void setCommander(final boolean b) {
            this._commander = b;
        }
        
        protected boolean isCommander() {
            return this._commander;
        }
    }
    
    protected class ComsEventInstance extends TvTEventInstance
    {
        protected ComsEventInstance(final InstanceData instance) {
            super(instance);
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    Commanders.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (Commanders.this.checkPlayers(this._instance.getId())) {
                            Commanders.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            Commanders.this.setupTitles(this._instance.getId());
                            Commanders.this.setupTeams(this._instance.getId(), Commanders.this._countOfSuperiorTeams);
                            Commanders.this.removeStaticDoors(this._instance.getId());
                            Commanders.this.enableMarkers(this._instance.getId(), true);
                            Commanders.this.spawnCommanderStuff(this._instance.getId(), true);
                            Commanders.this.handleSkills(this._instance.getId(), true);
                            Commanders.this.preparePlayers(this._instance.getId(), true);
                            Commanders.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Commanders.this.forceStandAll(this._instance.getId());
                        if (Commanders.this.getBoolean("createParties")) {
                            Commanders.this.createParties(Commanders.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(Commanders.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        if (!Commanders.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                Commanders.this.rewardAllTeams(this._instance.getId(), Commanders.this.getInt("killsForReward"), Commanders.this.getInt("killsForReward"));
                            }
                            Commanders.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Commanders.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                Commanders.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class ComsEventData extends TvTEventData
    {
        private final Map<Integer, PlayerEventInfo> _commanders;
        private final Map<Integer, NpcData> _bases;
        
        public ComsEventData(final int instance) {
            super(instance);
            this._commanders = new ConcurrentHashMap<Integer, PlayerEventInfo>();
            this._bases = new ConcurrentHashMap<Integer, NpcData>();
            this._commanders.clear();
            this._bases.clear();
        }
        
        public PlayerEventInfo getCommander(final int team) {
            return this._commanders.get(team);
        }
        
        public void setCommander(final int team, final PlayerEventInfo commander) {
            this._commanders.put(team, commander);
        }
        
        public NpcData getBase(final int team) {
            return this._bases.get(team);
        }
        
        public void setBase(final int team, final NpcData base) {
            this._bases.put(team, base);
        }
    }
}
