package gr.sr.events.engine.base;

import gr.sr.events.EventGame;
import gr.sr.events.engine.stats.GlobalStats;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.interf.PlayerEventInfo;

public class PvPEventPlayerData
        extends EventPlayerData {
    private int _kills = 0;
    private int _deaths = 0;
    private int _spree = 0;

    public PvPEventPlayerData(PlayerEventInfo owner, EventGame event, GlobalStatsModel stats) {
        super(owner, event, stats);
    }

    public int getKills() {
        return this._kills;
    }

    public int raiseKills(int i) {
        this._kills += i;
        this._globalStats.raise(GlobalStats.GlobalStatType.KILLS, i);
        return this._kills;
    }

    public void setKills(int i) {
        this._kills = i;
        this._globalStats.set(GlobalStats.GlobalStatType.KILLS, i);
    }

    public int getDeaths() {
        return this._deaths;
    }

    public int raiseDeaths(int i) {
        this._deaths += i;
        this._globalStats.raise(GlobalStats.GlobalStatType.DEATHS, i);
        return this._deaths;
    }

    public void setDeaths(int i) {
        this._deaths = i;
        this._globalStats.set(GlobalStats.GlobalStatType.DEATHS, i);
    }

    public int getSpree() {
        return this._spree;
    }

    public int raiseSpree(int i) {
        this._spree += i;
        return this._spree;
    }

    public void setSpree(int i) {
        this._spree = i;
    }
}


