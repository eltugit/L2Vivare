package gr.sr.events.engine.mini.events;

import gr.sr.events.engine.EventBuffer;
import gr.sr.events.engine.EventRewardSystem;
import gr.sr.events.engine.EventWarnings;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.mini.MiniEventGame;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.events.engine.team.KoreanTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.SkillData;
import gr.sr.l2j.CallBack;

import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;

public class KoreanGame extends MiniEventGame
{
    private final KoreanTeam[] _teams;
    private boolean _initState;
    private ScheduledFuture<?> _eventEnd;
    private ScheduledFuture<?> _roundStart;
    
    public KoreanGame(final int gameId, final EventMap arena, final KoreanManager event, final RegistrationData[] teams) {
        super(gameId, arena, event, teams);
        this._initState = true;
        final int teamsAmmount = 2;
        this._teams = new KoreanTeam[2];
        for (int i = 0; i < 2; ++i) {
            this._teams[i] = new KoreanTeam(i + 1, teams[i].getKeyPlayer().getPlayersName() + "'s party");
            for (final PlayerEventInfo pi : teams[i].getPlayers()) {
                pi.onEventStart(this);
                this._teams[i].addPlayer(pi, true);
            }
        }
        CallbackManager.getInstance().eventStarts(1, this.getEvent().getEventType(), Arrays.asList(this._teams));
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
        try {
            final boolean removeBuffs = this.getEvent().getBoolean("removeBuffsOnStart");
            this.broadcastMessage(LanguageEngine.getMsg("game_teleporting"), true);
            this._eventEnd = CallBack.getInstance().getOut().scheduleGeneral(() -> this.endByTime(), this.getGameTime());
            for (final KoreanTeam team : this._teams) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    pi.teleport(this._arena.getNextSpawn(team.getTeamId(), SpawnType.Safe).getLoc(), 0, true, this._instanceId);
                    if (removeBuffs) {
                        pi.removeBuffs();
                    }
                    pi.disableAfkCheck(true);
                    if (this.getEvent().getBoolean("removeCubics")) {
                        pi.removeCubics();
                    }
                    if (this._allowSchemeBuffer) {
                        EventBuffer.getInstance().buffPlayer(pi, true);
                    }
                    pi.enableAllSkills();
                }
            }
            this.scheduleMessage(LanguageEngine.getMsg("game_teleportDone"), 1500, true);
            this.handleDoors(1);
            final int startTime = this._event.getMapConfigInt(this._arena, "WaitTime");
            this._roundStart = CallBack.getInstance().getOut().scheduleGeneral(() -> this.finishRoundStart(), startTime);
            this.scheduleMessage(LanguageEngine.getMsg("game_matchStartsIn", startTime / 1000), 5000, true);
        }
        catch (Exception e) {
            this.abortDueToError("Map wasn't set up correctly.");
            e.printStackTrace();
        }
    }
    
    private void finishRoundStart() {
        if (this._aborted) {
            return;
        }
        this.broadcastMessage(LanguageEngine.getMsg("game_korean_teleportingToArena"), true);
        this.unspawnBuffers();
        this.handleDoors(2);
        this.teleportToEventLocation();
        this._initState = false;
        final PlayerEventInfo player1 = this.getNextPlayer(1);
        final PlayerEventInfo player2 = this.getNextPlayer(2);
        this.scheduleMessage(LanguageEngine.getMsg("game_korean_nextFight", player1.getPlayersName(), player2.getPlayersName(), 8), 3000, true);
        CallBack.getInstance().getOut().scheduleGeneral(() -> this.startFight(player1, player2), 11000L);
        this.startAnnouncing();
    }
    
    private void startFight(final PlayerEventInfo player1, final PlayerEventInfo player2) {
        if (this._aborted) {
            return;
        }
        final SkillData skill = new SkillData(5965, 1);
        player1.disableAfkCheck(false);
        player1.setIsParalyzed(false);
        player1.setIsInvul(false);
        player1.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_STEALTH());
        player1.broadcastSkillUse(null, null, skill.getId(), skill.getLevel());
        player1.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
        player2.disableAfkCheck(false);
        player2.setIsParalyzed(false);
        player2.setIsInvul(false);
        player2.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_STEALTH());
        player2.broadcastSkillUse(null, null, skill.getId(), skill.getLevel());
        player2.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
        this.broadcastMessage(LanguageEngine.getMsg("game_korean_fightStarted"), true);
    }
    
    private void teleportToEventLocation() {
        try {
            for (final KoreanTeam team : this._teams) {
                for (final PlayerEventInfo member : team.getPlayers()) {
                    member.teleport(this._arena.getNextSpawn(team.getTeamId(), SpawnType.Regular).getLoc(), 0, false, -1);
                    member.setIsInvul(true);
                    member.setIsParalyzed(true);
                    member.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_STEALTH());
                }
            }
        }
        catch (Exception e) {
            this.abortDueToError("Map wasn't propably set up correctly.");
            e.printStackTrace();
        }
    }
    
    private PlayerEventInfo getNextPlayer(final int teamId) {
        return this._teams[teamId - 1].getNextPlayer();
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (this._aborted) {
            return;
        }
        this.updateScore(player, killer);
        player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
        if (player.getEventTeam().getDeaths() >= player.getEventTeam().getPlayers().size()) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> this.endByDie(this.oppositeTeam(player.getEventTeam())), 3000L);
        }
        else {
            final PlayerEventInfo nextPlayer = ((KoreanTeam)player.getEventTeam()).getNextPlayer();
            CallBack.getInstance().getOut().scheduleGeneral(() -> this.announceNextPlayer(nextPlayer), 3000L);
        }
    }
    
    private void announceNextPlayer(final PlayerEventInfo nextPlayer) {
        final SkillData skill = new SkillData(5965, 1);
        nextPlayer.setIsParalyzed(false);
        nextPlayer.setIsInvul(false);
        nextPlayer.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_STEALTH());
        nextPlayer.broadcastSkillUse(null, null, skill.getId(), skill.getLevel());
        nextPlayer.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
        this.broadcastMessage(LanguageEngine.getMsg("game_korean_nextPlayer", nextPlayer.getPlayersName()), false);
    }
    
    private void endByTime() {
        if (this._aborted) {
            return;
        }
        this.cancelSchedulers();
        this.broadcastMessage(LanguageEngine.getMsg("game_matchEnd_timeLimit", this.getGameTime() / 60000), false);
        this.scheduleMessage(LanguageEngine.getMsg("game_matchEnd_tie"), 3000, false);
        for (final KoreanTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi, RewardPosition.Tie_TimeLimit, null, pi.getTotalTimeAfk(), 0, 0);
                this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
                this._event.logPlayer(pi, 2);
            }
        }
        this.saveGlobalStats();
        CallBack.getInstance().getOut().scheduleGeneral(() -> this.clearEvent(), 8000L);
    }
    
    private void endByDie(final EventTeam winner) {
        this.cancelSchedulers();
        this.broadcastMessage(LanguageEngine.getMsg("game_korean_winner", winner.getTeamName()), false);
        for (final PlayerEventInfo pi : winner.getPlayers()) {
            if (pi.isOnline()) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi, RewardPosition.Winner, null, pi.getTotalTimeAfk(), 0, 0);
                this.setEndStatus(pi, 1);
            }
            this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.WINS, 1);
            this._event.logPlayer(pi, 1);
        }
        for (final PlayerEventInfo pi : this.oppositeTeam(winner).getPlayers()) {
            if (pi.isOnline()) {
                EventRewardSystem.getInstance().rewardPlayer(this.getEvent().getEventType(), this.getEvent().getMode().getModeId(), pi, RewardPosition.Looser, null, pi.getTotalTimeAfk(), 0, 0);
                this.setEndStatus(pi, 0);
            }
            this.getPlayerData(pi).getGlobalStats().raise(GlobalStats.GlobalStatType.LOSES, 1);
            this._event.logPlayer(pi, 2);
        }
        this.saveGlobalStats();
        CallBack.getInstance().getOut().scheduleGeneral(() -> this.clearEvent(), 5000L);
    }
    
    @Override
    public void clearEvent() {
        this.cancelSchedulers();
        this.cleanSpectators();
        this.applyStatsChanges();
        for (final KoreanTeam team : this._teams) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi.isOnline()) {
                    if (pi.isParalyzed()) {
                        pi.setIsParalyzed(false);
                    }
                    pi.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
                    pi.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_STEALTH());
                    pi.setIsInvul(false);
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
            final EventTeam playerTeam = player.getEventTeam();
            playerTeam.removePlayer(player);
            player.restoreData();
            player.setXYZInvisible(player.getOrigLoc().getX(), player.getOrigLoc().getY(), player.getOrigLoc().getZ());
            if (!this._aborted) {
                if (playerTeam.getPlayers().isEmpty() || !this.checkTeamStatus(playerTeam.getTeamId())) {
                    this.cancelSchedulers();
                    CallBack.getInstance().getOut().scheduleGeneral(() -> {
                        this.broadcastMessage(LanguageEngine.getMsg("event_disconnect_all"), false);
                        if (this._initState) {
                            this.clearEvent();
                        }
                        else {
                            this.endByDie(this.oppositeTeam(playerTeam));
                        }
                    }, 3000L);
                }
                else if (!this._initState && ((KoreanTeam)playerTeam).isFighting(player)) {
                    final PlayerEventInfo nextPlayer = ((KoreanTeam)playerTeam).getNextPlayer();
                    if (nextPlayer == null) {
                        this.cancelSchedulers();
                        CallBack.getInstance().getOut().scheduleGeneral(() -> {
                            this.broadcastMessage(LanguageEngine.getMsg("event_disconnect_all"), false);
                            this.endByDie(this.oppositeTeam(playerTeam));
                        }, 5000L);
                    }
                    else {
                        CallBack.getInstance().getOut().scheduleGeneral(() -> this.announceNextPlayer(nextPlayer), 5000L);
                    }
                }
            }
        }
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
    
    private KoreanTeam oppositeTeam(final EventTeam team) {
        if (team.getTeamId() == 1) {
            return this._teams[1];
        }
        if (team.getTeamId() == 2) {
            return this._teams[0];
        }
        return null;
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
