package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventBuffer;
import gr.sr.events.engine.EventWarnings;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.delegate.PartyData;
import gr.sr.l2j.CallBack;
import l2r.gameserver.model.skills.L2Skill;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LastManStanding extends Deathmatch
{
    protected int _roundWaitTime;
    protected int _maxRounds;
    protected int _roundTimeLimit;
    private int _scoreForRoundWinner;
    protected boolean _disableAnnouncingCountdown;
    private String[] _scorebarFormat;
    
    public LastManStanding(final EventType type, final MainEventManager manager) {
        super(type, manager);
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        final ConfigModel scorebarFormat = new ConfigModel("screenScoreBarFormat", "AliveAndRounds", "Specify here how will the player's screen Score bar look like. <br1><font color=LEVEL>Alive</font> shows the count of players that are still alive, excluding you.<br1><font color=LEVEL>Time</font> shows the time left for the event (using Run time value)<br1><font color=LEVEL>Rounds</font> shows the current round / max rounds in the event.<br1><font color=LEVEL>Top</font> shows the score of the top player in the event.<br1>Example: <font color=LEVEL>AliveAndRounds</font> will show following text: 'Alive: 12, Round: 1/3', where 12 is the count of alive players excluding you, 1 is the current round and 3 si the total count of rounds in this event (configurable).", ConfigModel.InputType.Enum);
        scorebarFormat.addEnumOptions(new String[] { "Alive", "Rounds", "Time", "Top", "AliveAndRounds", "AliveAndTime", "AliveAndTop", "RoundsAndTime", "RoundsAndTop", "TopAndTime" });
        this.addConfig(scorebarFormat);
        this.removeConfig("runTime");
        this.removeConfig("rejoinAfterDisconnect");
        this.removeConfig("removeWarningAfterRejoin");
        this.addConfig(new ConfigModel("runTime", "30", "The run time of this event, launched automatically by the scheduler. Max value globally for all events is 120 minutes. <font color=699768>It is recommended to use a higher run time (30+ minutes) in combination with lower value of </font><font color=LEVEL>maxRounds</font> <font color=699768>(3-5).</font> In minutes!"));
        this.addConfig(new ConfigModel("maxRounds", "3", "The maximum count of rounds that will be runned in this event. One round ends when there's only one player alive. If an event instance reaches this rounds limit, the event instance will end. The event ends (meaning you can start/schedule a new event) only when all event instances have ended."));
        this.addConfig(new ConfigModel("roundTimeLimit", "600", "The time after it automatically ends current round. Useful to prevent afking on events or if any stupid player don't know what to do (even tho if a player goes afk, he will be killed automatically). In seconds."));
        this.addConfig(new ConfigModel("scoreForRoundWinner", "3", "Number of score points given to a round winner (the only player who survived). Remember, that one kill = 1 score."));
        this.addConfig(new ConfigModel("roundWaitTime", "5", "The time players have to wait when a new round started. They are rooted and can't attack anyone. There's a countdown too. This is here because it looks cool."));
        this.addConfig(new ConfigModel("disableCountdown", "true", "Put true to disable classic event's end countdown announcement. Good if you want to have this event only round-based, like it ends after 3 rounds and not look like there's a 20 minutes limit. Putting high run time (eg. 30 minutes) and lower rounds count (3-5) is recommended for this event.", ConfigModel.InputType.Boolean));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._maxRounds = this.getInt("maxRounds");
        this._roundTimeLimit = this.getInt("roundTimeLimit");
        this._scoreForRoundWinner = this.getInt("scoreForRoundWinner");
        this._roundWaitTime = this.getInt("roundWaitTime");
        this._disableAnnouncingCountdown = this.getBoolean("disableCountdown");
        this._scorebarFormat = this.getString("screenScoreBarFormat").split("And");
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
        this._matches = new ConcurrentHashMap<Integer, DMEventInstance>();
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
    
    protected void startRound(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: trying to start new round for instance " + instanceId);
        }
        if (this.getEventData(instanceId).canStartNewRound()) {
            if (SunriseLoader.detailedDebug) {
                this.print("Event: starting new round; current round = " + this.getEventData(instanceId)._round);
            }
            this.getEventData(instanceId)._roundActive = true;
            this.getEventData(instanceId)._alivePlayers = this.getPlayers(instanceId).size();
            for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                this.respawnPlayer(player, instanceId);
                if (this._allowSchemeBuffer) {
                    EventBuffer.getInstance().buffPlayer(player, true);
                }
                if (this._allowNoblesOnRess) {
                    L2Skill noblesse = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(1323, 1);
                    if (noblesse != null) {
                        noblesse.getEffects(player.getOwner(), player.getOwner());
                    }
                }
            }
            this.getEventData(instanceId).newRound();
            this.getEventData(instanceId).setWaitingState(true);
            this.waitingStateEffects(instanceId, true);
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                if (this.getEventData(instanceId)._isActive && this._matches.get(instanceId)._nextState == EventState.END) {
                    this.announce(instanceId, LanguageEngine.getMsg("lms_roundStarted", this.getEventData(instanceId)._round));
                    this.getEventData(instanceId).setWaitingState(false);
                    this.waitingStateEffects(instanceId, false);
                }
                return;
            }, this._roundWaitTime * 1000);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: new round started!");
            }
        }
        else {
            if (SunriseLoader.detailedDebug) {
                this.print("Event: CAN'T START new round!");
            }
            this.announce(instanceId, "Configs are wrong for Last Man Standing event. Event aborted until fixed.");
            SunriseLoader.debug("Rounds count config for LMS must be at least 1. Event has been aborted", Level.WARNING);
            this.endInstance(instanceId, true, false, true);
        }
    }
    
    protected void waitingStateEffects(final int instance, final boolean apply) {
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            player.setIsParalyzed(apply);
            player.paralizeEffect(apply);
            player.setIsInvul(apply);
        }
    }
    
    private synchronized void endRound(final int instanceId, final boolean aborted, final boolean endInstance) {
        if (!this.getEventData(instanceId)._roundActive) {
            return;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("Event: ending round of instance " + instanceId + " aborted = " + aborted + ", end instance " + endInstance);
        }
        this.getEventData(instanceId)._roundActive = false;
        PlayerEventInfo winner = null;
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (!player.isDead()) {
                winner = player;
            }
        }
        if (!aborted && winner != null) {
            this.getPlayerData(winner).raiseScore(this._scoreForRoundWinner);
            this.setScoreStats(winner, this.getPlayerData(winner).getScore());
            this.announce(instanceId, LanguageEngine.getMsg("lms_roundWon", winner.getPlayersName(), this.getEventData(instanceId)._round));
        }
        if (this.getEventData(instanceId).canStartNewRound() && !endInstance) {
            this.announce(instanceId, LanguageEngine.getMsg("lms_roundStartsIn", 10));
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                if (this.getEventData(instanceId).isActive() && this._matches.get(instanceId)._nextState == EventState.END) {
                    this.startRound(instanceId);
                }
            }, 10000L);
        }
        else {
            this.announce(instanceId, LanguageEngine.getMsg("lms_eventEnded"));
            synchronized (this.getEventData(instanceId)) {
                this.getEventData(instanceId).setInactive();
            }
            this.endInstance(instanceId, true, true, false);
        }
    }
    
    protected void endRoundDueToTime(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: end due to round time = " + instanceId);
        }
        this.announce(instanceId, LanguageEngine.getMsg("lms_roundAborted_timeLimit", this._roundTimeLimit / 60));
        this.endRound(instanceId, true, false);
    }
    
    private void endRoundDueToEventTimeLimit(final int instanceId, final boolean announceTimeLimit) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: ending round due to event time limit " + instanceId + ", announce time limit = " + announceTimeLimit);
        }
        synchronized (this.getEventData(instanceId)) {
            this.getEventData(instanceId).setInactive();
        }
        if (announceTimeLimit) {
            this.announce(instanceId, LanguageEngine.getMsg("lms_roundAborted"));
        }
        this.endRound(instanceId, true, true);
    }
    
    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        for (final DMEventInstance match : this._matches.values()) {
            if (this.getEventData(match.getInstance().getId()).isActive()) {
                this.endRoundDueToEventTimeLimit(match.getInstance().getId(), true);
            }
        }
        super.onEventEnd();
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final int countAlive = this.getEventData(instance)._alivePlayers - 1;
        final String time = this._matches.get(instance).getClock().getTime();
        final String rounds = this.getEventData(instance)._round + "/" + this._maxRounds;
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getScore() > top) {
                top = this.getPlayerData(player).getScore();
            }
        }
        final StringBuilder tb = new StringBuilder();
        final String[] types = this._scorebarFormat;
        for (int i = 0; i < types.length; ++i) {
            final String type = types[i];
            if (type.equals("Alive")) {
                tb.append(LanguageEngine.getMsg("lms_scorebar_alive") + " " + countAlive);
            }
            else if (type.equals("Time")) {
                tb.append(LanguageEngine.getMsg("event_scorebar_time", time));
            }
            else if (type.equals("Rounds")) {
                tb.append(LanguageEngine.getMsg("lms_scorebar_rounds") + " " + rounds);
            }
            else if (type.equals("Top")) {
                tb.append(LanguageEngine.getMsg("lms_scorebar_top") + " " + top);
            }
            if (i + 1 < types.length) {
                tb.append("  ");
            }
        }
        return tb.toString();
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
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
        CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
        this.setScoreStats(player, this.getPlayerData(player).getScore());
        this.setKillsStats(player, this.getPlayerData(player).getKills());
    }
    
    @Override
    public synchronized void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: onDie - player " + player.getPlayersName() + " (instance " + player.getInstanceId() + "), killer " + killer.getName());
        }
        this.getPlayerData(player).raiseDeaths(1);
        this.getPlayerData(player).setSpree(0);
        player.disableAfkCheck(true);
        this.setDeathsStats(player, this.getPlayerData(player).getDeaths());
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
        if (this.getEventData(player.getInstanceId()).playerDied()) {
            this.endRound(player.getInstanceId(), false, false);
        }
        else if (this.getEventData(player.getInstanceId()).canStartNewRound()) {
            player.sendMessage(LanguageEngine.getMsg("lms_notifyPlayerRespawn"));
        }
    }
    
    @Override
    public void playerWentAfk(final PlayerEventInfo player, final boolean warningOnly, final int afkTime) {
        if (warningOnly) {
            player.sendMessage(LanguageEngine.getMsg("event_afkWarning_kill", PlayerEventInfo.AFK_WARNING_DELAY / 1000, PlayerEventInfo.AFK_KICK_DELAY / 1000));
        }
        else if (this.getEventData(player.getInstanceId())._roundActive) {
            this.announce(player.getInstanceId(), LanguageEngine.getMsg("event_afkMarked_andDied", player.getPlayersName()));
            player.doDie();
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
    public void onDisconnect(final PlayerEventInfo player) {
        if (player.isOnline()) {
            if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: player " + player.getPlayersName() + " (instance id = " + player.getInstanceId() + ") disconnecting from the event");
            }
            final EventTeam team = player.getEventTeam();
            player.restoreData();
            player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            EventWarnings.getInstance().addPoints(player.getPlayersId(), 1);
            boolean running = false;
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
                this.debug(this.getEventName() + ": Player " + player.getPlayersName() + " disconnected from main event, still enought players to continue the event.");
                if (team.getPlayers().isEmpty()) {
                    this.announce(player.getInstanceId(), LanguageEngine.getMsg("event_disconnect_team", team.getTeamName()));
                    this.debug(this.getEventName() + ": all players from team " + team.getTeamName() + " have disconnected.");
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: ALL PLAYERS FROM TEAM " + team.getTeamName() + " disconnected");
                    }
                }
                if (!this.checkIfEventCanContinue(player.getInstanceId(), player)) {
                    this.announce(player.getInstanceId(), LanguageEngine.getMsg("event_disconnect_all"));
                    this.endInstance(player.getInstanceId(), true, false, false);
                    this.debug(this.getEventName() + ": no players left in the teams, the fight cannot continue. The event has been aborted!");
                    if (SunriseLoader.detailedDebug) {
                        this.print("AbstractMainEvent: NO PLAYERS LEFT IN THE TEAMS, THE FIGHT CAN'T CONTINUE! (checkIfEventCanContinue = false)");
                    }
                    return;
                }
                if (this.checkIfAllDied(player.getInstanceId())) {
                    this.endRound(player.getInstanceId(), false, false);
                }
            }
            else if (SunriseLoader.detailedDebug) {
                this.print("AbstractMainEvent: -.- event IS NOT active anymore");
            }
        }
    }
    
    private boolean checkIfAllDied(final int instanceId) {
        int alive = 0;
        for (final PlayerEventInfo pi : this.getPlayers(instanceId)) {
            if (pi != null && !pi.isDead()) {
                ++alive;
            }
        }
        return alive < 2;
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
                    player.paralizeEffect(false);
                }
                player.setIsInvul(false);
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
        try {
            if (this._matches != null) {
                for (final DMEventInstance match : this._matches.values()) {
                    if (this.getEventData(match.getInstance().getId()).isActive()) {
                        this.endRoundDueToEventTimeLimit(match.getInstance().getId(), true);
                    }
                }
            }
        }
        catch (Exception ex) {}
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
            pi.disableAfkCheck(false);
            pi.teleport(loc, 0, true, instance);
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
            if (this.getEventData(match.getInstance().getId())._isActive) {
                return "+-" + (this._maxRounds - this.getEventData(match.getInstance().getId())._round + 1) + " rounds";
            }
        }
        return null;
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        final int countAlive = this.getEventData(instance)._alivePlayers - 1;
        final String rounds = this.getEventData(instance)._round + " of " + this._maxRounds;
        final String status = "<td align=center width=200><font color=ac9887>Round: </font><font color=9f9f9f>" + rounds + "</font></td><td align=center width=200><font color=ac9887>Alive: </font><font color=9f9f9f>" + countAlive + " players</font></td>";
        return "<table width=510 bgcolor=3E3E3E><tr>" + status + "</tr></table>";
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            }
            else {
                this._htmlDescription = "This is a free-for-all event, don't expect any help from teammates. ";
                this._htmlDescription = this._htmlDescription + "This event has " + this.getInt("maxRounds") + " rounds. You can gain score by killing your opponents (1 kill = 1 score), but if you die, you won't get resurrected until the next round starts. ";
                this._htmlDescription = this._htmlDescription + "The player, who wins the round (when all other players are dead) receives additional " + this.getInt("scoreForRoundWinner") + " score points. ";
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
    protected String getTitle(final PlayerEventInfo pi) {
        if (this._hideTitles) {
            return "";
        }
        if (pi.isAfk()) {
            return "AFK";
        }
        return "Score: " + this.getPlayerData(pi).getScore() + " Deaths: " + this.getPlayerData(pi).getDeaths();
    }
    
    @Override
    protected void clockTick() {
        for (final DMEventInstance match : this._matches.values()) {
            ((LMSData)match._data).onTick();
        }
    }
    
    @Override
    public boolean allowsRejoinOnDisconnect() {
        return false;
    }
    
    @Override
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }
    
    @Override
    protected DMData createEventData(final int instance) {
        return new LMSData(instance);
    }
    
    @Override
    protected LMSEventInstance createEventInstance(final InstanceData instance) {
        return new LMSEventInstance(instance);
    }
    
    @Override
    protected LMSData getEventData(final int instance) {
        try {
            return (LMSData)this._matches.get(instance)._data;
        }
        catch (Exception e) {
            SunriseLoader.debug("Error on getEventData for instance " + instance);
            e.printStackTrace();
            return null;
        }
    }
    
    protected class LMSEventInstance extends DMEventInstance
    {
        public LMSEventInstance(final InstanceData instance) {
            super(instance);
            if (LastManStanding.this._disableAnnouncingCountdown) {
                this._clock.disableAnnouncingCountdown();
            }
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    LastManStanding.this.print("Event: running task of state " + this._nextState.toString() + "...");
                }
                switch (this._nextState) {
                    case START: {
                        if (LastManStanding.this.checkPlayers(this._instance.getId())) {
                            if (LastManStanding.this._antifeed) {
                                for (final PlayerEventInfo player : LastManStanding.this.getPlayers(this._instance.getId())) {
                                    player.startAntifeedProtection(false);
                                }
                            }
                            LastManStanding.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, true);
                            LastManStanding.this.setupTitles(this._instance.getId());
                            LastManStanding.this.enableMarkers(this._instance.getId(), true);
                            LastManStanding.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        LastManStanding.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        LastManStanding.this.startRound(this._instance.getId());
                        this._clock.startClock(LastManStanding.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        if (!LastManStanding.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                LastManStanding.this.rewardAllPlayers(this._instance.getId(), 0, LastManStanding.this.getInt("killsForReward"));
                            }
                            LastManStanding.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    LastManStanding.this.print("Event: ... finished running task. next state " + this._nextState.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                LastManStanding.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class LMSData extends DMData
    {
        protected boolean _isActive;
        private boolean _waitingState;
        protected boolean _roundActive;
        private int _waitingStateTime;
        protected int _round;
        protected int _alivePlayers;
        private Timelimit _timelimit;

        protected LMSData(final int instance) {
            super(instance);
            this._alivePlayers = 0;
            this._round = 0;
            this._isActive = true;
            this._waitingState = false;
            this._roundActive = true;
        }
        
        public void onTick() {
            this._timelimit.onTick();
            if (this._waitingState && this._waitingStateTime > 0) {
                switch (--this._waitingStateTime) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 10:
                    case 15:
                    case 20:
                    case 30:
                    case 60:
                    case 120:
                    case 180: {
                        LastManStanding.this.announce(this._instanceId, LanguageEngine.getMsg("lms_roundStart", this._round, this._waitingStateTime));
                        break;
                    }
                }
            }
        }
        
        protected boolean playerDied() {
            if (this._alivePlayers > 0) {
                --this._alivePlayers;
            }
            return this._alivePlayers == 1;
        }
        
        protected boolean canStartNewRound() {
            return this._isActive && this._round < LastManStanding.this._maxRounds;
        }
        
        protected void newRound() {
            this._isActive = true;
            ++this._round;
            this._timelimit = new Timelimit();
        }
        
        protected void setWaitingState(final boolean b) {
            this._waitingState = b;
            if (b) {
                this._waitingStateTime = LastManStanding.this._roundWaitTime + 1;
            }
        }
        
        protected boolean isActive() {
            return this._isActive;
        }
        
        protected synchronized void setInactive() {
            this._isActive = false;
        }
        
        private class Timelimit
        {
            private int limit;
            private boolean aborted;
            
            public Timelimit() {
                this.aborted = false;
                this.limit = LastManStanding.this._roundTimeLimit;
            }
            
            public void onTick() {
                if (this.limit > 0) {
                    --this.limit;
                }
                if (!this.aborted && this.limit <= 0) {
                    this.aborted = true;
                    LastManStanding.this.endRoundDueToTime(LMSData.this._instanceId);
                    if (SunriseLoader.detailedDebug) {
                        LastManStanding.this.print("Event: round ended due to time limit");
                    }
                }
            }
        }
    }
}
