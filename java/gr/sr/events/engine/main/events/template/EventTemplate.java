package gr.sr.events.engine.main.events.template;

import gr.sr.events.EventGame;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventTemplate extends AbstractMainEvent
{
    private final Map<Integer, CustomEventInstance> _matches;
    private boolean _waweRespawn;
    private int _teamsCount;

    @Override
    protected CustomEventData createEventData(final int instanceId) {
        return new CustomEventData(instanceId);
    }

    @Override
    protected CustomEventInstance createEventInstance(final InstanceData instance) {
        return new CustomEventInstance(instance);
    }

    @Override
    protected CustomEventData getEventData(final int instance) {
        return this._matches.get(instance)._data;
    }

    public EventTemplate(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, CustomEventInstance>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.OnKill });
        this.addConfig(new ConfigModel("killsForReward", "0", "The minimum kills count required to get a reward (includes all possible rewards)."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which the player is resurrected. In seconds."));
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("createParties", "true", "Put 'True' if you want this event to automatically create parties for players in each team.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("maxPartySize", "10", "The maximum size of party, that can be created. Works only if <font color=LEVEL>createParties</font> is true."));
        this.addConfig(new ConfigModel("teamsCount", "2", "The ammount of teams in the event. Max is 5."));
        this.addInstanceTypeConfig(new ConfigModel("teamsCount", "2", "You may specify the count of teams only for this instance. This config overrides event's default teams ammount."));
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
        if (!this.dividePlayers()) {
            this.clearEvent();
            return;
        }
        this._matches.clear();
        for (final InstanceData instance : this._instances) {
            final CustomEventInstance match = new CustomEventInstance(instance);
            this._matches.put(instance.getId(), match);
            ++this._runningInstances;
            match.scheduleNextTask(0);
        }
    }

    @Override
    public void onEventEnd() {
        final int minKills = this.getInt("killsForReward");
        final int minScore = this.getInt("scoreForReward");
        this.rewardAllTeams(-1, minScore, minKills);
    }

    @Override
    protected synchronized boolean instanceEnded() {
        --this._runningInstances;
        if (this._runningInstances == 0) {
            this._manager.end();
            return true;
        }
        return false;
    }

    @Override
    protected synchronized void endInstance(final int instance, final boolean canBeAborted, final boolean canRewardIfAborted, final boolean forceNotReward) {
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
            this.giveOnKillReward(player);
            player.getEventTeam().raiseScore(1);
            player.getEventTeam().raiseKills(1);
            this.getPlayerData(player).raiseScore(1);
            this.getPlayerData(player).raiseKills(1);
            this.getPlayerData(player).raiseSpree(1);
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
        this.getPlayerData(player).raiseDeaths(1);
        this.setDeathsStats(player, this.getPlayerData(player).getDeaths());
        if (this._waweRespawn) {
            this._waweScheduler.addPlayer(player);
        }
        else {
            this.scheduleRevive(player, this.getInt("resDelay") * 1000);
        }
    }

    protected void spawnStuff(final int instanceId) {
    }

    protected void unspawnStuff(final int instanceId) {
    }

    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new CustomEventPlayerData(player, this);
        return d;
    }

    @Override
    public CustomEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (CustomEventPlayerData)player.getEventData();
    }

    @Override
    public synchronized void clearEvent(final int instanceId) {
        try {
            if (this._matches != null) {
                for (final CustomEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        match.abort();
                        this.unspawnStuff(instanceId);
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
        this.clearEvent(0);
    }

    @Override
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
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
    public void onDisconnect(final PlayerEventInfo player) {
        if (player.isOnline()) {}
        super.onDisconnect(player);
    }

    @Override
    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        return super.checkIfEventCanContinue(instanceId, disconnectedPlayer);
    }

    @Override
    protected void clockTick() {
    }

    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        return true;
    }

    @Override
    public boolean onNpcAction(final PlayerEventInfo player, final NpcData npc) {
        return false;
    }

    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
        super.onDamageGive(cha, target, damage, isDOT);
    }

    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return super.canSupport(player, target);
    }

    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        return super.canAttack(player, target);
    }

    @Override
    public boolean onAttack(final CharacterData cha, final CharacterData target) {
        return true;
    }

    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        return super.canUseItem(player, item);
    }

    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return super.canDestroyItem(player, item);
    }

    @Override
    public void onItemUse(final PlayerEventInfo player, final ItemData item) {
        super.onItemUse(player, item);
    }

    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        return super.canUseSkill(player, skill);
    }

    @Override
    public void onSkillUse(final PlayerEventInfo player, final SkillData skill) {
        super.onSkillUse(player, skill);
    }

    @Override
    public String getEstimatedTimeLeft() {
        if (this._matches == null) {
            return "Starting";
        }
        for (final CustomEventInstance match : this._matches.values()) {
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
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }

    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            this._htmlDescription = "No information about this event.";
        }
        return this._htmlDescription;
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

    public class CustomEventPlayerData extends PvPEventPlayerData
    {
        public CustomEventPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event, new GlobalStatsModel(EventTemplate.this.getEventType()));
        }
    }

    private enum EventState
    {
        START,
        FIGHT,
        END,
        TELEPORT,
        INACTIVE;
    }

    private class CustomEventInstance extends AbstractEventInstance
    {
        protected EventState _state;
        protected CustomEventData _data;

        protected CustomEventInstance(final InstanceData instance) {
            super(instance);
            this._state = EventState.START;
            this._data = EventTemplate.this.createEventData(instance.getId());
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
                switch (this._state) {
                    case START: {
                        if (EventTemplate.this.checkPlayers(this._instance.getId())) {
                            EventTemplate.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            EventTemplate.this.setupTitles(this._instance.getId());
                            EventTemplate.this.spawnStuff(this._instance.getId());
                            EventTemplate.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        EventTemplate.this.forceStandAll(this._instance.getId());
                        if (EventTemplate.this.getBoolean("createParties")) {
                            EventTemplate.this.createParties(EventTemplate.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(EventTemplate.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        EventTemplate.this.unspawnStuff(this._instance.getId());
                        this.setNextState(EventState.INACTIVE);
                        if (!EventTemplate.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                EventTemplate.this.rewardAllTeams(this._instance.getId(), EventTemplate.this.getInt("scoreForReward"), EventTemplate.this.getInt("killsForReward"));
                            }
                            EventTemplate.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                EventTemplate.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }

    private class CustomEventData extends AbstractEventData
    {
        public CustomEventData(final int instance) {
            super(instance);
        }
    }
}