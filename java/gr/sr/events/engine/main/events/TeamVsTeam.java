package gr.sr.events.engine.main.events;

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
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.delegate.PartyData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TeamVsTeam extends AbstractMainEvent
{
    protected Map<Integer, TvTEventInstance> _matches;
    protected boolean _waweRespawn;
    protected int _teamsCount;
    
    public TeamVsTeam(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, TvTEventInstance>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("killsForReward", "0", "The minimum kills count required to get a reward (includes all possible rewards)."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which the player is resurrected. In seconds."));
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("createParties", "true", "Put 'True' if you want this event to automatically create parties for players in each team.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("maxPartySize", "9", "The maximum size of party, that can be created. Works only if <font color=LEVEL>createParties</font> is true."));
        this.addConfig(new ConfigModel("teamsCount", "2", "The count of teams in the event. Max is 5. <font color=FF0000>In order to change the count of teams in the event, you must also edit this config in the Instance's configuration.</font>"));
        this.addConfig(new ConfigModel("firstBloodMessage", "true", "You can turn off/on the first blood announce in the event (first kill made in the event). This is also rewardable - check out reward type FirstBlood.", ConfigModel.InputType.Boolean));
        this.addInstanceTypeConfig(new ConfigModel("teamsCount", "2", "You may specify the count of teams only for this instance. This config overrides event default teams count."));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._waweRespawn = this.getBoolean("waweRespawn");
        if (this._waweRespawn) {
            this.initWaweRespawns(this.getInt("resDelay"));
        }
        this._runningInstances = 0;
    }
    
    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this._teamsCount = type.getConfigInt("teamsCount");
        if (this._teamsCount < 2 || this._teamsCount > 5) {
            this._teamsCount = this.getInt("teamsCount");
        }
        if (this._teamsCount < 2 || this._teamsCount > 5) {
            this._teamsCount = 2;
        }
        this.createTeams(this._teamsCount, type.getInstance().getId());
        return this._teamsCount;
    }
    
    @Override
    public void runEvent() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: started runEvent()");
        }
        if (!this.dividePlayers()) {
            this.clearEvent();
            return;
        }
        this._matches.clear();
        for (final InstanceData instance : this._instances) {
            if (SunriseLoader.detailedDebug) {
                this.print("Event: creating eventinstance for instance " + instance.getId());
            }
            final TvTEventInstance match = this.createEventInstance(instance);
            this._matches.put(instance.getId(), match);
            ++this._runningInstances;
            match.scheduleNextTask(0);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: event instance started");
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("Event: finished runEvent()");
        }
    }
    
    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minKills = this.getInt("killsForReward");
        this.rewardAllTeams(-1, minKills, minKills);
    }
    
    @Override
    protected synchronized boolean instanceEnded() {
        --this._runningInstances;
        if (SunriseLoader.detailedDebug) {
            this.print("Event: notifying instance ended: runningInstances = " + this._runningInstances);
        }
        if (this._runningInstances == 0) {
            this._manager.end();
            return true;
        }
        return false;
    }
    
    @Override
    protected synchronized void endInstance(final int instance, final boolean canBeAborted, final boolean canRewardIfAborted, final boolean forceNotReward) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: endInstance() " + instance + ", canBeAborted " + canBeAborted + ", canReward.. " + canRewardIfAborted + " forceNotReward " + forceNotReward);
        }
        if (forceNotReward) {
            this._matches.get(instance).forceNotRewardThisInstance();
        }
        this._matches.get(instance).setNextState(EventState.END);
        if (canBeAborted) {
            this._matches.get(instance).setCanBeAborted();
        }
        if (canRewardIfAborted) {
            this._matches.get(instance).setCanRewardIfAborted();
        }
        this._matches.get(instance).scheduleNextTask(0);
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final int count = this._teams.get(instance).size();
        final StringBuilder tb = new StringBuilder();
        for (final EventTeam team : this._teams.get(instance).values()) {
            if (count <= 4) {
                tb.append(team.getTeamName() + ": " + team.getScore() + "  ");
            }
            else {
                tb.append(team.getTeamName().substring(0, 1) + ": " + team.getScore() + "  ");
            }
        }
        if (count <= 3) {
            tb.append(LanguageEngine.getMsg("event_scorebar_time", this._matches.get(instance).getClock().getTime()));
        }
        return tb.toString();
    }
    
    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (this._hideTitles) {
            return "";
        }
        if (pi.isAfk()) {
            return "AFK";
        }
        return "Kills: " + this.getPlayerData(pi).getScore() + " Deaths: " + this.getPlayerData(pi).getDeaths();
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
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new PvPEventPlayerData(player, this, new GlobalStatsModel(this.getEventType()));
        return d;
    }
    
    @Override
    public PvPEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (PvPEventPlayerData)player.getEventData();
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
    public synchronized void clearEvent() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called global clearEvent()");
        }
        this.clearEvent(0);
    }
    
    @Override
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: respawning player " + pi.getPlayersName() + ", instance " + instance);
        }
        final EventSpawn spawn = this.getSpawn(SpawnType.Regular, pi.getTeamId());
        if (spawn != null) {
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            pi.teleport(loc, 0, true, instance);
            pi.sendMessage(LanguageEngine.getMsg("event_respawned"));
        }
        else {
            this.debug("Error on respawnPlayer - no spawn type REGULAR, team " + pi.getTeamId() + " has been found. Event aborted.");
        }
    }
    
    @Override
    public String getEstimatedTimeLeft() {
        if (this._matches == null) {
            return "Starting";
        }
        for (final TvTEventInstance match : this._matches.values()) {
            if (match.isActive()) {
                return match.getClock().getTime();
            }
        }
        return "N/A";
    }
    
    @Override
    public int getTeamsCount() {
        return this.getInt("teamsCount");
    }
    
    @Override
    public String getMissingSpawns(final EventMap map) {
        final StringBuilder tb = new StringBuilder();
        for (int i = 0; i < this.getTeamsCount(); ++i) {
            if (!map.checkForSpawns(SpawnType.Regular, i + 1, 1)) {
                tb.append(this.addMissingSpawn(SpawnType.Regular, i + 1, 1));
            }
        }
        return tb.toString();
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
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }
    
    @Override
    protected TvTEventData createEventData(final int instanceId) {
        return new TvTEventData(instanceId);
    }
    
    @Override
    protected TvTEventInstance createEventInstance(final InstanceData instance) {
        return new TvTEventInstance(instance);
    }
    
    @Override
    protected TvTEventData getEventData(final int instance) {
        return this._matches.get(instance)._data;
    }
    
    protected enum EventState
    {
        START, 
        FIGHT, 
        END, 
        TELEPORT, 
        INACTIVE;
    }
    
    protected class TvTEventInstance extends AbstractEventInstance
    {
        protected EventState _state;
        protected TvTEventData _data;
        
        protected TvTEventInstance(final InstanceData instance) {
            super(instance);
            this._state = EventState.START;
            this._data = TeamVsTeam.this.createEventData(instance.getId());
        }
        
        protected void setNextState(final EventState state) {
            this._state = state;
        }
        
        @Override
        public boolean isActive() {
            return this._state != EventState.INACTIVE;
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    TeamVsTeam.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (TeamVsTeam.this.checkPlayers(this._instance.getId())) {
                            TeamVsTeam.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            TeamVsTeam.this.setupTitles(this._instance.getId());
                            TeamVsTeam.this.enableMarkers(this._instance.getId(), true);
                            TeamVsTeam.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        TeamVsTeam.this.forceStandAll(this._instance.getId());
                        if (TeamVsTeam.this.getBoolean("createParties")) {
                            TeamVsTeam.this.createParties(TeamVsTeam.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(TeamVsTeam.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        if (!TeamVsTeam.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                TeamVsTeam.this.rewardAllTeams(this._instance.getId(), TeamVsTeam.this.getInt("killsForReward"), TeamVsTeam.this.getInt("killsForReward"));
                            }
                            TeamVsTeam.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    TeamVsTeam.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                TeamVsTeam.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class TvTEventData extends AbstractEventData
    {
        public TvTEventData(final int instance) {
            super(instance);
        }
    }
}
