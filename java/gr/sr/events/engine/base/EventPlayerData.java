package gr.sr.events.engine.base;

import gr.sr.events.EventGame;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.interf.PlayerEventInfo;

public class EventPlayerData {
    private final PlayerEventInfo _owner;
    protected GlobalStatsModel _globalStats;
    private int _score;

    public EventPlayerData(PlayerEventInfo owner, EventGame event, GlobalStatsModel stats) {
        this._owner = owner;
        this._globalStats = stats;
    }

    public PlayerEventInfo getOwner() {
        return this._owner;
    }

    public int getScore() {
        return this._score;
    }

    public int raiseScore(int i) {
        this._score += i;
        this._globalStats.raise(GlobalStats.GlobalStatType.SCORE, i);
        return this._score;
    }

    public void setScore(int i) {
        this._score = i;
        this._globalStats.set(GlobalStats.GlobalStatType.SCORE, i);
    }

    public GlobalStatsModel getGlobalStats() {
        return this._globalStats;
    }
}


