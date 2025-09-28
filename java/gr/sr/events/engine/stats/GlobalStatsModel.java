package gr.sr.events.engine.stats;

import gr.sr.events.engine.base.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalStatsModel
{
    protected EventType _event;
    protected Map<GlobalStats.GlobalStatType, Integer> _stats;
    
    public GlobalStatsModel(final EventType event, final Map<GlobalStats.GlobalStatType, Integer> stats) {
        this._stats = new ConcurrentHashMap<GlobalStats.GlobalStatType, Integer>();
        this._event = event;
        this._stats = stats;
    }
    
    public GlobalStatsModel(final EventType event) {
        this._stats = new ConcurrentHashMap<GlobalStats.GlobalStatType, Integer>();
        this._event = event;
        this._stats.clear();
        for (final GlobalStats.GlobalStatType t : GlobalStats.GlobalStatType.values()) {
            this._stats.put(t, 0);
        }
    }
    
    public int get(final GlobalStats.GlobalStatType type) {
        return this._stats.get(type);
    }
    
    public void set(final GlobalStats.GlobalStatType type, final int value) {
        this._stats.put(type, value);
    }
    
    public void raise(final GlobalStats.GlobalStatType type, final int value) {
        this.set(type, this.get(type) + value);
    }
    
    public void add(final GlobalStatsModel newStats) {
        for (final Map.Entry<GlobalStats.GlobalStatType, Integer> e : newStats._stats.entrySet()) {
            this.raise(e.getKey(), e.getValue());
        }
    }
    
    public EventType getEvent() {
        return this._event;
    }
    
    public String getFavoriteEvent() {
        return "N/A";
    }
}
