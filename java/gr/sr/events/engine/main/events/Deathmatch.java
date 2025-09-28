package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.delegate.PartyData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Deathmatch extends AbstractMainEvent
{
    protected Map<Integer, DMEventInstance> _matches;
    protected boolean _waweRespawn;
    protected boolean _antifeed;
    
    public Deathmatch(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, DMEventInstance>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("killsForReward", "0", "The minimum kills count required to get a reward (includes all possible rewards)."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which the player is resurrected. In seconds."));
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("firstBloodMessage", "true", "You can turn off/on the first blood announce in the event (first kill made in the event). This is also rewardable - check out reward type FirstBlood.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("antifeedProtection", "true", "Enables the special anti-feed protection. This protection changes player's name, title, race, clan/ally crest, class and basically all of his apperance, sometimes also gender.", ConfigModel.InputType.Boolean));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._waweRespawn = this.getBoolean("waweRespawn");
        this._antifeed = this.getBoolean("antifeedProtection");
        if (this._waweRespawn) {
            this.initWaweRespawns(this.getInt("resDelay"));
        }
        this._runningInstances = 0;
    }
    
    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this.createTeams(1, type.getInstance().getId());
        return 1;
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
            final DMEventInstance match = this.createEventInstance(instance);
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
        this.rewardAllPlayers(-1, minKills, minKills);
        if (this._antifeed) {
            for (final PlayerEventInfo player : this.getPlayers(0)) {
                player.stopAntifeedProtection(false);
            }
        }
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
        try {
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
        catch (Exception ex) {}
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final StringBuilder tb = new StringBuilder();
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getKills() > top) {
                top = this.getPlayerData(player).getKills();
            }
        }
        tb.append(LanguageEngine.getMsg("dm_topKills", top) + " ");
        tb.append("   " + LanguageEngine.getMsg("event_scorebar_time", this._matches.get(instance).getClock().getTime()));
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
        return LanguageEngine.getMsg("event_title_pvppk", this.getPlayerData(pi).getScore(), this.getPlayerData(pi).getDeaths());
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        this.tryFirstBlood(player);
        this.giveOnKillReward(player);
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
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return player.getPlayersId() == target.getObjectId() || (player.hasSummon() && target.isSummon() && player.getSummon() == target.getOwner());
    }
    
    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() == null || target.getEventInfo().getEvent() == player.getEvent();
    }
    
    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        if (text.equals(".scheme")) {
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "none", this.getEventType().getAltTitle());
            return false;
        }
        if (this._antifeed) {
            player.sendMessage(LanguageEngine.getMsg("dm_cantChat"));
            return false;
        }
        return true;
    }
    
    @Override
    public boolean canInviteToParty(final PlayerEventInfo player, final PlayerEventInfo target) {
        return false;
    }
    
    @Override
    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        int alive = 0;
        for (final PlayerEventInfo pi : this.getPlayers(instanceId)) {
            if (pi != null && pi.isOnline()) {
                ++alive;
            }
        }
        return alive >= 2;
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
                for (final DMEventInstance match : this._matches.values()) {
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
                if (player.hasAntifeedProtection()) {
                    player.stopAntifeedProtection(false);
                }
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
        final EventSpawn spawn = this.getSpawn(SpawnType.Regular, -1);
        if (spawn != null) {
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            pi.teleport(loc, 0, true, instance);
            pi.sendMessage(LanguageEngine.getMsg("event_respawned"));
        }
        else {
            this.debug("Error on respawnPlayer - no spawn type REGULAR, team -1 (FFA) has been found. Event aborted.");
        }
    }
    
    @Override
    public String getEstimatedTimeLeft() {
        if (this._matches == null) {
            return "Starting";
        }
        for (final DMEventInstance match : this._matches.values()) {
            if (match.isActive()) {
                return match.getClock().getTime();
            }
        }
        return "N/A";
    }
    
    @Override
    public int getTeamsCount() {
        return 1;
    }
    
    @Override
    public String getMissingSpawns(final EventMap map) {
        if (!map.checkForSpawns(SpawnType.Regular, -1, 1)) {
            return this.addMissingSpawn(SpawnType.Regular, 0, 1);
        }
        return "";
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getKills() > top) {
                top = this.getPlayerData(player).getKills();
            }
        }
        final String status = "<font color=ac9887>Top kills count: </font><font color=7f7f7f>" + top + "</font>";
        return "<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center>" + status + "</td></tr></table>";
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            }
            else {
                this._htmlDescription = "This is a free-for-all event, don't expect any help from teammates. Gain score by killing your opponents";
                this._htmlDescription = this._htmlDescription + " and if you die, you will be resurrected within " + this.getInt("resDelay") + " seconds. ";
                if (this.getBoolean("waweRespawn")) {
                    this._htmlDescription += "Also, wawe-spawn system ensures that all dead players are spawned in the same moment (but in different spots). ";
                }
                if (this.getBoolean("antifeedProtection")) {
                    this._htmlDescription += "This event has a protection, which completely changes the appearance of all players and temporary removes their title and clan/ally crests. ";
                }
                if (this.getInt("killsForReward") > 0) {
                    this._htmlDescription = this._htmlDescription + "In the end, you need at least " + this.getInt("killsForReward") + " kills to receive a reward.";
                }
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }
    
    @Override
    protected DMData createEventData(final int instance) {
        return new DMData(instance);
    }
    
    @Override
    protected DMEventInstance createEventInstance(final InstanceData instance) {
        return new DMEventInstance(instance);
    }
    
    @Override
    protected DMData getEventData(final int instance) {
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
    
    protected class DMEventInstance extends AbstractEventInstance
    {
        protected EventState _nextState;
        protected DMData _data;
        
        public DMEventInstance(final InstanceData instance) {
            super(instance);
            this._nextState = EventState.START;
            this._data = Deathmatch.this.createEventData(this._instance.getId());
        }
        
        protected void setNextState(final EventState state) {
            this._nextState = state;
        }
        
        @Override
        public boolean isActive() {
            return this._nextState != EventState.INACTIVE;
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    Deathmatch.this.print("Event: running task of state " + this._nextState.toString() + "...");
                }
                switch (this._nextState) {
                    case START: {
                        if (Deathmatch.this.checkPlayers(this._instance.getId())) {
                            if (Deathmatch.this._antifeed) {
                                for (final PlayerEventInfo player : Deathmatch.this.getPlayers(this._instance.getId())) {
                                    player.startAntifeedProtection(false);
                                }
                            }
                            Deathmatch.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, true);
                            Deathmatch.this.setupTitles(this._instance.getId());
                            Deathmatch.this.enableMarkers(this._instance.getId(), true);
                            Deathmatch.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Deathmatch.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        this._clock.startClock(Deathmatch.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        if (!Deathmatch.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                Deathmatch.this.rewardAllPlayers(this._instance.getId(), 0, Deathmatch.this.getInt("killsForReward"));
                            }
                            Deathmatch.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Deathmatch.this.print("Event: ... finished running task. next state " + this._nextState.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                Deathmatch.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class DMData extends AbstractEventData
    {
        protected DMData(final int instance) {
            super(instance);
        }
    }
}
