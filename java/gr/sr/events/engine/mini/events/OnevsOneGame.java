package gr.sr.events.engine.mini.events;

import gr.sr.events.engine.EventBuffer;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.EventRewardSystem;
import gr.sr.events.engine.EventWarnings;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.mini.MiniEventGame;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.events.engine.team.OnePlayerTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.l2j.CallBack;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class OnevsOneGame extends MiniEventGame
{
    private final int _teamsAmmount;
    private final int _roundsAmmount;
    protected OnePlayerTeam[] _players;
    private ScheduledFuture<?> _eventEnd;
    private ScheduledFuture<?> _roundStart;
    private int _round;
    
    public OnevsOneGame(final int gameId, final EventMap arena, final OnevsOneManager event, final RegistrationData[] teams) {
        super(gameId, arena, event, teams);
        this._teamsAmmount = event.getTeamsCount();
        this._roundsAmmount = event.getRoundsAmmount();
        this._players = new OnePlayerTeam[this._teamsAmmount];
        for (int i = 0; i < this._teamsAmmount; ++i) {
            this._players[i] = new OnePlayerTeam(i + 1, teams[i].getKeyPlayer().getPlayersName());
            teams[i].getKeyPlayer().onEventStart(this);
            this._players[i].addPlayer(teams[i].getKeyPlayer(), true);
        }
        CallbackManager.getInstance().eventStarts(1, this.getEvent().getEventType(), Arrays.asList(this._players));
        this._round = 0;
    }
    
    @Override
    protected void initEvent() {
        super.initEvent();
        this.startEvent();
    }
    
    @Override
    protected void startEvent() {
        try {
            this.broadcastMessage(LanguageEngine.getMsg("game_teleporting"), false);
            this._eventEnd = CallBack.getInstance().getOut().scheduleGeneral(() -> this.endByTime(), this.getGameTime());
            this.scheduleMessage(LanguageEngine.getMsg("game_teleportDone"), 1500, true);
            this.nextRound(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void nextRound(final boolean forceEnd) {
        if (this._aborted) {
            return;
        }
        if (this._round == this._roundsAmmount || forceEnd) {
            this.endByDie();
            return;
        }
        ++this._round;
        boolean removeBuffs = this.getEvent().getBoolean("removeBuffsOnRespawn");
        if (this._round == 1) {
            removeBuffs = this.getEvent().getBoolean("removeBuffsOnStart");
        }
        this.handleDoors(1);
        this.loadBuffers();
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null && team.getPlayer().isOnline()) {
                final EventSpawn spawn = this._arena.getNextSpawn(team.getTeamId(), SpawnType.Regular);
                if (spawn == null) {
                    this.abortDueToError("No regular spawn found for team " + team.getTeamId() + ". Match aborted.");
                    this.clearEvent();
                    return;
                }
                team.getPlayer().teleport(spawn.getLoc(), 0, false, this._instanceId);
                if (removeBuffs) {
                    team.getPlayer().removeBuffs();
                }
                team.getPlayer().disableAfkCheck(true);
                team.getPlayer().root();
                if (this._round == 1 && this.getEvent().getBoolean("removeCubics")) {
                    team.getPlayer().removeCubics();
                }
                if (this._allowSchemeBuffer) {
                    EventBuffer.getInstance().buffPlayer(team.getPlayer(), true);
                }
                if (this._round == 1) {
                    team.getPlayer().enableAllSkills();
                }
            }
        }
        int startTime;
        if (this._round == 1) {
            startTime = this.getEvent().getMapConfigInt(this._arena, "FirstRoundWaitDelay");
        }
        else {
            startTime = this.getEvent().getMapConfigInt(this._arena, "RoundWaitDelay");
        }
        this.scheduleMessage(LanguageEngine.getMsg("game_roundStartIn", this.getRoundName(this._round, this._roundsAmmount), startTime / 1000), 5000, true);
        this._roundStart = CallBack.getInstance().getOut().scheduleGeneral(() -> this.finishRoundStart(), startTime);
    }
    
    protected void finishRoundStart() {
        if (this._aborted) {
            return;
        }
        this.unspawnBuffers();
        this.handleDoors(2);
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null && team.getPlayer().isOnline()) {
                team.getPlayer().disableAfkCheck(false);
                team.getPlayer().unroot();
            }
        }
        this.broadcastMessage(LanguageEngine.getMsg("game_roundStarted", this.getRoundName(this._round, this._roundsAmmount)), true);
        if (this._round == 1) {
            this.startAnnouncing();
        }
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (this._aborted) {
            return;
        }
        this.updateScore(player, killer);
        final OnePlayerTeam team = this.checkLastAlivePlayer();
        if (team != null) {
            team.raiseScore(1);
            this.onScore(team.getPlayers(), 1);
            final boolean forceEnd = !this.checkIfTheMatchCanContinue();
            if (this._round == this._roundsAmmount || forceEnd) {
                this.scheduleMessage(LanguageEngine.getMsg("game_matchEnd"), 3000, true);
            }
            else {
                this.scheduleMessage(LanguageEngine.getMsg("game_roundWonBy", this.getRoundName(this._round, this._roundsAmmount), team.getTeamName()), 3000, true);
            }
            CallBack.getInstance().getOut().scheduleGeneral(() -> this.nextRound(forceEnd), 4000L);
        }
    }
    
    private boolean checkIfTheMatchCanContinue() {
        final int remainingRounds = this._roundsAmmount - this._round;
        int bestScore = 0;
        int secondScore = 0;
        for (final OnePlayerTeam team : this._players) {
            if (team.getScore() > bestScore) {
                secondScore = bestScore;
                bestScore = team.getScore();
            }
            else if (team.getScore() > secondScore && secondScore != bestScore) {
                secondScore = team.getScore();
            }
        }
        return bestScore - secondScore <= remainingRounds;
    }
    
    private OnePlayerTeam checkLastAlivePlayer() {
        int alivePlayers = 0;
        OnePlayerTeam tempTeam = null;
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null && team.getPlayer().isOnline() && !team.getPlayer().isDead()) {
                ++alivePlayers;
                tempTeam = team;
            }
        }
        if (alivePlayers == 1) {
            return tempTeam;
        }
        return null;
    }
    
    protected void endByTime() {
        if (this._aborted) {
            return;
        }
        this.cancelSchedulers();
        this.broadcastMessage(LanguageEngine.getMsg("game_matchEnd_timeLimit", this.getGameTime() / 60000), false);
        this.scheduleMessage(LanguageEngine.getMsg("game_matchEnd_tie"), 3000, false);
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), team.getPlayer(), RewardPosition.Tie_TimeLimit, null, team.getPlayer().getTotalTimeAfk(), 0, 0);
                this.getPlayerData(team.getPlayer()).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                this._event.logPlayer(team.getPlayer(), 2);
            }
        }
        this.saveGlobalStats();
        this.scheduleClearEvent(8000);
    }
    
    private void endByDie() {
        this.cancelSchedulers();
        final List<OnePlayerTeam> sortedTeams = new LinkedList<OnePlayerTeam>();
        for (final OnePlayerTeam team : this._players) {
            sortedTeams.add(team);
        }
        Collections.sort(sortedTeams, EventManager.getInstance().compareTeamScore);
        final Map<Integer, List<OnePlayerTeam>> scores = new LinkedHashMap<Integer, List<OnePlayerTeam>>();
        for (final OnePlayerTeam team2 : sortedTeams) {
            if (!scores.containsKey(team2.getScore())) {
                scores.put(team2.getScore(), new LinkedList<OnePlayerTeam>());
            }
            scores.get(team2.getScore()).add(team2);
        }
        int place = 1;
        for (final OnePlayerTeam team : sortedTeams) {
            this.broadcastMessage(LanguageEngine.getMsg("event_announceScore_includeKills", place, team.getTeamName(), team.getScore(), team.getKills()), false);
            ++place;
        }
        place = 1;
        for (final Map.Entry<Integer, List<OnePlayerTeam>> i : scores.entrySet()) {
            if (place == 1) {
                if (i.getValue().size() > 1) {
                    if (this._teamsAmmount > i.getValue().size()) {
                        StringBuilder tb = new StringBuilder();
                        for (final OnePlayerTeam team3 : i.getValue()) {
                            tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part1", team3.getTeamName()));
                        }
                        final String s = tb.toString();
                        tb = new StringBuilder(s.substring(0, s.length() - 4));
                        tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part1"));
                        this.broadcastMessage(tb.toString(), false);
                        for (final OnePlayerTeam team4 : i.getValue()) {
                            if (team4.getPlayer() != null) {
                                if (team4.getPlayer().isOnline()) {
                                    EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), team4.getPlayer(), RewardPosition.Winner, null, team4.getPlayer().getTotalTimeAfk(), 0, 0);
                                    this.setEndStatus(team4.getPlayer(), 1);
                                }
                                this.getPlayerData(team4.getPlayer()).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                this._event.logPlayer(team4.getPlayer(), 1);
                            }
                        }
                    }
                    else {
                        this.broadcastMessage(LanguageEngine.getMsg("event_ffa_announceWinner3"), false);
                        for (final OnePlayerTeam team5 : i.getValue()) {
                            if (team5.getPlayer() != null) {
                                if (team5.getPlayer().isOnline()) {
                                    EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), team5.getPlayer(), RewardPosition.Tie, null, team5.getPlayer().getTotalTimeAfk(), 0, 0);
                                }
                                this.getPlayerData(team5.getPlayer()).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                this._event.logPlayer(team5.getPlayer(), 2);
                            }
                        }
                    }
                }
                else {
                    final OnePlayerTeam winnerPlayer = i.getValue().get(0);
                    this.broadcastMessage(LanguageEngine.getMsg("event_ffa_announceWinner1", i.getValue().get(0).getTeamName()), false);
                    if (winnerPlayer.getPlayer() != null) {
                        if (winnerPlayer.getPlayer().isOnline()) {
                            EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), winnerPlayer.getPlayer(), RewardPosition.Winner, null, winnerPlayer.getPlayer().getTotalTimeAfk(), 0, 0);
                            this.setEndStatus(winnerPlayer.getPlayer(), 1);
                        }
                        this.getPlayerData(winnerPlayer.getPlayer()).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                        this._event.logPlayer(winnerPlayer.getPlayer(), 1);
                    }
                }
            }
            else {
                for (final OnePlayerTeam team5 : i.getValue()) {
                    if (team5.getPlayer() != null) {
                        if (team5.getPlayer().isOnline()) {
                            EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), team5.getPlayer(), RewardPosition.Looser, null, team5.getPlayer().getTotalTimeAfk(), 0, 0);
                            this.setEndStatus(team5.getPlayer(), 0);
                        }
                        this.getPlayerData(team5.getPlayer()).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                        this._event.logPlayer(team5.getPlayer(), 2);
                    }
                }
            }
            ++place;
        }
        this.saveGlobalStats();
        this.scheduleClearEvent(8000);
    }
    
    @Override
    public void clearEvent() {
        this.cancelSchedulers();
        this.cleanSpectators();
        this.applyStatsChanges();
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null && team.getPlayer().isOnline()) {
                if (team.getPlayer().isImmobilized()) {
                    team.getPlayer().unroot();
                }
                team.getPlayer().restoreData();
                team.getPlayer().teleport(team.getPlayer().getOrigLoc(), 0, true, 0);
                team.getPlayer().sendMessage(LanguageEngine.getMsg("event_teleportBack"));
                CallBack.getInstance().getPlayerBase().eventEnd(team.getPlayer());
            }
        }
        if (this._fences != null) {
            CallBack.getInstance().getOut().unspawnFences(this._fences);
        }
        this.unspawnMapGuards();
        this.unspawnNpcs();
        this._event.notifyGameEnd(this);
    }
    
    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        if (player != null && player.isOnline()) {
            if (player.isSpectator()) {
                this.removeSpectator(player, true);
                return;
            }
            EventWarnings.getInstance().addPoints(player.getPlayersId(), 1);
            if (this._teamsAmmount == 2) {
                this.broadcastMessage(LanguageEngine.getMsg("game_playerDisconnected2", player.getPlayersName()), true);
            }
            else {
                this.broadcastMessage(LanguageEngine.getMsg("game_playerDisconnected", player.getPlayersName()), true);
            }
            final EventTeam playerTeam = player.getEventTeam();
            player.restoreData();
            player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            if (!this._aborted) {
                playerTeam.removePlayer(player);
                if (this.checkIfPlayersDisconnected()) {
                    this.broadcastMessage(LanguageEngine.getMsg("event_disconnect_all"), true);
                    this.clearEvent();
                    return;
                }
                final OnePlayerTeam team = this.checkLastAlivePlayer();
                if (team != null) {
                    CallBack.getInstance().getOut().scheduleGeneral(() -> this.nextRound(false), 3000L);
                }
            }
        }
    }
    
    private boolean checkIfPlayersDisconnected() {
        int teamsOn = 0;
        for (final OnePlayerTeam team : this._players) {
            if (team.getPlayer() != null && team.getPlayer().isOnline()) {
                ++teamsOn;
            }
        }
        return teamsOn == 0 || teamsOn == 1;
    }
    
    @Override
    protected void checkPlayersLoc() {
    }
    
    @Override
    protected void checkIfPlayersTeleported() {
    }
    
    private void cancelSchedulers() {
        if (this._aborted) {
            return;
        }
        this._aborted = true;
        CallbackManager.getInstance().eventEnded(1, this.getEvent().getEventType(), Arrays.asList(this._players));
        if (this._announcer != null) {
            this._announcer.cancel();
            this._announcer = null;
        }
        if (this._locChecker != null) {
            this._locChecker.cancel(false);
            this._locChecker = null;
        }
        if (this._eventEnd != null) {
            this._eventEnd.cancel(false);
            this._eventEnd = null;
        }
        if (this._roundStart != null) {
            this._roundStart.cancel(false);
            this._roundStart = null;
        }
    }
    
    @Override
    public int getInstanceId() {
        return this._instanceId;
    }
    
    @Override
    public EventTeam[] getTeams() {
        return this._players;
    }
    
    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new PvPEventPlayerData(player, this, new GlobalStatsModel(this._event.getEventType()));
        return d;
    }
    
    @Override
    public PvPEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (PvPEventPlayerData)player.getEventData();
    }
}
