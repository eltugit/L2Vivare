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
import gr.sr.events.engine.team.FixedPartyTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.l2j.CallBack;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class MiniTvTGame extends MiniEventGame
{
    private final int _teamsAmmount;
    private final int _roundsAmmount;
    private final String _root;
    private final FixedPartyTeam[] _teams;
    private ScheduledFuture<?> _eventEnd;
    private ScheduledFuture<?> _roundStart;
    private int _round;
    private final Comparator<RegistrationData> compareByLevels;
    
    public MiniTvTGame(final int gameId, final EventMap arena, final MiniTvTManager event, final RegistrationData[] players) {
        super(gameId, arena, event, players);
        this._round = 0;
        this.compareByLevels = ((o1, o2) -> {
            int level1 = o1.getKeyPlayer().getLevel();
            int level2 = o2.getKeyPlayer().getLevel();
            return (level1 == level2) ? 0 : ((level1 < level2) ? 1 : -1);
        });
        this._teamsAmmount = event.getTeamsCount();
        this._roundsAmmount = event.getRoundsAmmount();
        this._root = this.getEvent().getMapConfig(this._arena, "RootPlayers");
        this._teams = new FixedPartyTeam[this._teamsAmmount];
        for (int i = 0; i < this._teamsAmmount; ++i) {
            this._teams[i] = new FixedPartyTeam(i + 1, event.getPlayersInTeam());
        }
        final List<RegistrationData> datas = new LinkedList<RegistrationData>();
        for (final RegistrationData d : players) {
            datas.add(d);
        }
        Collections.sort(datas, this.compareByLevels);
        int team = 0;
        for (final RegistrationData playerData : datas) {
            final PlayerEventInfo pi = playerData.getKeyPlayer();
            pi.onEventStart(this);
            if (pi.getParty() != null) {
                pi.getParty().removePartyMember(pi);
            }
            this._teams[team].addPlayer(pi, true);
            if (++team >= this._teamsAmmount) {
                team = 0;
            }
        }
        final boolean createParties = datas.size() > 2;
        if (createParties) {
            this.createParties();
        }
        CallbackManager.getInstance().eventStarts(1, this.getEvent().getEventType(), Arrays.asList(this._teams));
    }
    
    private void createParties() {
        for (final FixedPartyTeam team : this._teams) {
            team.createParties();
        }
    }
    
    @Override
    public void run() {
        this.initEvent();
    }
    
    @Override
    protected void initEvent() {
        super.initEvent();
        this.loadBuffers();
        this.startEvent();
    }
    
    @Override
    protected void startEvent() {
        this.broadcastMessage(LanguageEngine.getMsg("game_teleporting"), true);
        this._eventEnd = CallBack.getInstance().getOut().scheduleGeneral(() -> this.endByTime(), this.getGameTime());
        this.nextRound(false);
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
        for (final FixedPartyTeam team : this._teams) {
            final EventSpawn spawn = this._arena.getNextSpawn(team.getTeamId(), SpawnType.Regular);
            if (spawn == null) {
                this.abortDueToError("No regular spawn found for team " + team.getTeamId() + ". Match aborted.");
                this.clearEvent();
                return;
            }
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (!pi.isOnline()) {
                    continue;
                }
                pi.teleport(spawn.getLoc(), 0, true, this._instanceId);
                if (removeBuffs) {
                    pi.removeBuffs();
                }
                if (this._root.equalsIgnoreCase("true")) {
                    pi.root();
                }
                if (this._round == 1 && this.getEvent().getBoolean("removeCubics")) {
                    pi.removeCubics();
                }
                pi.disableAfkCheck(true);
                if (this._allowSchemeBuffer) {
                    EventBuffer.getInstance().buffPlayer(pi, true);
                }
                pi.enableAllSkills();
            }
        }
        this.handleDoors(1);
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
    
    private void finishRoundStart() {
        if (this._aborted) {
            return;
        }
        this.unspawnBuffers();
        this.handleDoors(2);
        for (final FixedPartyTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (!pi.isOnline()) {
                    continue;
                }
                pi.disableAfkCheck(false);
                if (!this._root.equalsIgnoreCase("true")) {
                    continue;
                }
                pi.unroot();
            }
        }
        if (this._round == 1) {
            this.startAnnouncing();
        }
        this.broadcastMessage(LanguageEngine.getMsg("game_roundStarted", this.getRoundName(this._round, this._roundsAmmount)), true);
    }
    
    @Override
    public synchronized void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (this._aborted) {
            return;
        }
        this.updateScore(player, killer);
        final FixedPartyTeam team = this.checkLastAliveTeam();
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
        for (final FixedPartyTeam team : this._teams) {
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
    
    private FixedPartyTeam checkLastAliveTeam() {
        int aliveTeams = 0;
        FixedPartyTeam tempTeam = null;
        for (final FixedPartyTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi.isOnline() && !pi.isDead()) {
                    ++aliveTeams;
                    tempTeam = team;
                    break;
                }
            }
        }
        if (aliveTeams == 1) {
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
        for (final FixedPartyTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi, RewardPosition.Tie_TimeLimit, null, pi.getTotalTimeAfk(), 0, 0);
                this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                this._event.logPlayer(pi, 2);
            }
        }
        this.saveGlobalStats();
        this.scheduleClearEvent(8000);
    }
    
    private void endByDie() {
        this.cancelSchedulers();
        final List<FixedPartyTeam> sortedTeams = new LinkedList<FixedPartyTeam>();
        for (final FixedPartyTeam team : this._teams) {
            sortedTeams.add(team);
        }
        Collections.sort(sortedTeams, EventManager.getInstance().compareTeamScore);
        final Map<Integer, List<FixedPartyTeam>> scores = new LinkedHashMap<Integer, List<FixedPartyTeam>>();
        for (final FixedPartyTeam team2 : sortedTeams) {
            if (!scores.containsKey(team2.getScore())) {
                scores.put(team2.getScore(), new LinkedList<FixedPartyTeam>());
            }
            scores.get(team2.getScore()).add(team2);
        }
        int place = 1;
        for (final FixedPartyTeam team : sortedTeams) {
            this.broadcastMessage(LanguageEngine.getMsg("event_announceScore_includeKills", place, team.getTeamName(), team.getScore(), team.getKills()), false);
            ++place;
        }
        place = 1;
        for (final Map.Entry<Integer, List<FixedPartyTeam>> i : scores.entrySet()) {
            if (place == 1) {
                if (i.getValue().size() > 1) {
                    if (this._teamsAmmount > i.getValue().size()) {
                        StringBuilder tb = new StringBuilder();
                        for (final FixedPartyTeam team3 : i.getValue()) {
                            tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part1", team3.getTeamName()));
                        }
                        final String s = tb.toString();
                        tb = new StringBuilder(s.substring(0, s.length() - 4));
                        tb.append(LanguageEngine.getMsg("event_ffa_announceWinner2_part2"));
                        this.broadcastMessage(tb.toString(), false);
                        for (final FixedPartyTeam team4 : i.getValue()) {
                            for (final PlayerEventInfo pi : team4.getPlayers()) {
                                if (pi.isOnline()) {
                                    EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi, RewardPosition.Winner, null, pi.getTotalTimeAfk(), 0, 0);
                                    this.setEndStatus(pi, 1);
                                }
                                this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                this._event.logPlayer(pi, 1);
                            }
                        }
                    }
                    else {
                        this.broadcastMessage(LanguageEngine.getMsg("event_team_announceWinner3"), false);
                        for (final FixedPartyTeam team5 : i.getValue()) {
                            for (final PlayerEventInfo pi2 : team5.getPlayers()) {
                                if (pi2.isOnline()) {
                                    EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi2, RewardPosition.Tie, null, pi2.getTotalTimeAfk(), 0, 0);
                                }
                                this.getPlayerData(pi2).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                                this._event.logPlayer(pi2, 2);
                            }
                        }
                    }
                }
                else {
                    final FixedPartyTeam winnerTeam = i.getValue().get(0);
                    this.broadcastMessage(LanguageEngine.getMsg("event_team_announceWinner1", i.getValue().get(0).getTeamName()), false);
                    for (final PlayerEventInfo pi3 : winnerTeam.getPlayers()) {
                        if (pi3.isOnline()) {
                            EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi3, RewardPosition.Winner, null, pi3.getTotalTimeAfk(), 0, 0);
                            this.setEndStatus(pi3, 1);
                        }
                        this.getPlayerData(pi3).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
                        this._event.logPlayer(pi3, 1);
                    }
                }
            }
            else {
                for (final FixedPartyTeam team5 : i.getValue()) {
                    for (final PlayerEventInfo pi2 : team5.getPlayers()) {
                        if (pi2.isOnline()) {
                            EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi2, RewardPosition.Looser, null, pi2.getTotalTimeAfk(), 0, 0);
                            this.setEndStatus(pi2, 0);
                        }
                        this.getPlayerData(pi2).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                        this._event.logPlayer(pi2, 2);
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
        for (final FixedPartyTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi.isOnline()) {
                    if (this._root.equalsIgnoreCase("true")) {
                        pi.unroot();
                    }
                    pi.restoreData();
                    pi.teleport(pi.getOrigLoc(), 0, true, 0);
                    pi.sendMessage(LanguageEngine.getMsg("event_teleportBack"));
                    CallBack.getInstance().getPlayerBase().eventEnd(pi);
                }
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
            this.broadcastMessage(LanguageEngine.getMsg("game_playerDisconnected", player.getPlayersName()), true);
            final FixedPartyTeam team = (FixedPartyTeam)player.getEventTeam();
            player.restoreData();
            player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            if (!this._aborted) {
                team.removePlayer(player);
                if (this.checkIfAllTeamsDisconnected()) {
                    this.broadcastMessage(LanguageEngine.getMsg("event_disconnect_all"), true);
                    this.clearEvent();
                    return;
                }
                final FixedPartyTeam lastTeam = this.checkLastAliveTeam();
                if (lastTeam != null) {
                    CallBack.getInstance().getOut().scheduleGeneral(() -> this.nextRound(false), 3000L);
                }
            }
        }
    }
    
    private boolean checkIfAllTeamsDisconnected() {
        int teamsOn = 0;
        for (final FixedPartyTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi != null && pi.isOnline()) {
                    ++teamsOn;
                    break;
                }
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
        CallbackManager.getInstance().eventEnded(1, this.getEvent().getEventType(), Arrays.asList(this._teams));
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
        return this._teams;
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
