package gr.sr.events.engine.stats;

import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.html.EventHtmlManager;
import gr.sr.interf.PlayerEventInfo;

public class EventStatsManager
{
    private final GlobalStats _globalStats;
    private final EventSpecificStats _eventStats;
    
    public EventStatsManager() {
        this._globalStats = new GlobalStats();
        this._eventStats = new EventSpecificStats();
        this._globalStats.load();
        this._eventStats.load();
    }
    
    public GlobalStats getGlobalStats() {
        return this._globalStats;
    }
    
    public EventSpecificStats getEventStats() {
        return this._eventStats;
    }
    
    public void onBypass(final PlayerEventInfo player, final String command) {
        if (command.startsWith("global_")) {
            this._globalStats.onCommand(player, command.substring(7));
        }
        else if (command.startsWith("eventstats_")) {
            this._eventStats.onCommand(player, command.substring(11));
        }
        else if (command.startsWith("cbmenu")) {
            if (EventHtmlManager.BBS_COMMAND == null) {
                EventHtmlManager.BBS_COMMAND = EventConfig.getInstance().getGlobalConfigValue("cbPage");
            }
            EventManager.getInstance().getHtmlManager().onCbBypass(player, EventHtmlManager.BBS_COMMAND);
        }
    }
    
    public void onLogin(final PlayerEventInfo player) {
        this._globalStats.onLogin(player);
        this._eventStats.onLogin(player);
    }
    
    public void onDisconnect(final PlayerEventInfo player) {
        this._globalStats.onDisconnect(player);
        this._eventStats.onDisconnect(player);
    }
    
    public void reload() {
        this._globalStats.loadGlobalStats();
    }
    
    public static EventStatsManager getInstance() {
        return SingletonHolder._instance;
    }
    
    private static class SingletonHolder
    {
        protected static final EventStatsManager _instance;
        
        static {
            _instance = new EventStatsManager();
        }
    }
}
