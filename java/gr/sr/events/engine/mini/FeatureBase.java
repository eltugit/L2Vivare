package gr.sr.events.engine.mini;

import gr.sr.events.engine.base.EventType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FeatureBase
{
    private static FeatureBase _instance;
    private final Map<EventMode.FeatureType, FeatureInfo> _data;
    
    public static FeatureBase getInstance() {
        return FeatureBase._instance;
    }
    
    public FeatureBase() {
        this._data = new ConcurrentHashMap<EventMode.FeatureType, FeatureInfo>();
        this.add(EventMode.FeatureType.Delays, "Delays", EventMode.FeatureCategory.Configs, EventType.getMiniEvents(), "specifies all delays for this event.");
        this.add(EventMode.FeatureType.Enchant, "Enchant", EventMode.FeatureCategory.Items, EventType.getMiniEvents(), "specifies all enchant related settings.");
        this.add(EventMode.FeatureType.ItemGrades, "Item Grades", EventMode.FeatureCategory.Items, EventType.getMiniEvents(), "allows you to specify allowed item grades.");
        this.add(EventMode.FeatureType.Items, "Items", EventMode.FeatureCategory.Items, EventType.getMiniEvents(), "specifies which items will be allowed and which disabled.");
        this.add(EventMode.FeatureType.Level, "Level", EventMode.FeatureCategory.Players, EventType.getMiniEvents(), "specifies max/min level allowed to participate this mode");
        this.add(EventMode.FeatureType.TimeLimit, "Time Limit", EventMode.FeatureCategory.Configs, EventType.getMiniEvents(), "specifies all time-based settings.");
        this.add(EventMode.FeatureType.Skills, "Skills", EventMode.FeatureCategory.Players, EventType.getMiniEvents(), "specifies all skills-related settings.");
        this.add(EventMode.FeatureType.Buffer, "Buffer", EventMode.FeatureCategory.Configs, EventType.getMiniEvents(), "specifies all buffs-related settings.");
        this.add(EventMode.FeatureType.Rounds, "Rounds", EventMode.FeatureCategory.Configs, new EventType[] { EventType.Classic_1v1, EventType.PartyvsParty, EventType.MiniTvT }, "allows you to edit the ammount of rounds only for this mode.");
        this.add(EventMode.FeatureType.TeamsAmmount, "Teams Ammount", EventMode.FeatureCategory.Configs, new EventType[] { EventType.Classic_1v1, EventType.PartyvsParty, EventType.MiniTvT }, "allows you to edit the ammount of teams only for this mode.");
        this.add(EventMode.FeatureType.TeamSize, "Team Size", EventMode.FeatureCategory.Players, new EventType[] { EventType.Korean, EventType.PartyvsParty, EventType.MiniTvT }, "allows you to edit the ammount of players in one team only for this mode.");
        this.add(EventMode.FeatureType.StrenghtChecks, "Strenght Checks", EventMode.FeatureCategory.Players, new EventType[] { EventType.Korean, EventType.PartyvsParty, EventType.Classic_1v1 }, "allows you to edit the automatic match making strenght difference checks");
    }
    
    public void add(final EventMode.FeatureType type, final String visibleName, final EventMode.FeatureCategory cat, final EventType[] events, final String desc) {
        final FeatureInfo info = new FeatureInfo(cat, visibleName, events, desc);
        this._data.put(type, info);
    }
    
    public FeatureInfo get(final EventMode.FeatureType type) {
        return this._data.get(type);
    }
    
    static {
        FeatureBase._instance = new FeatureBase();
    }
    
    public class FeatureInfo
    {
        private final EventMode.FeatureCategory _category;
        private final EventType[] _events;
        private final String _desc;
        private final String _visibleName;
        
        public FeatureInfo(final EventMode.FeatureCategory cat, final String visName, final EventType[] events, final String desc) {
            this._category = cat;
            this._events = events;
            this._desc = desc;
            this._visibleName = visName;
        }
        
        public EventMode.FeatureCategory getCategory() {
            return this._category;
        }
        
        public String getVisibleName() {
            return this._visibleName;
        }
        
        public String getDesc() {
            return this._desc;
        }
        
        public boolean isForEvent(final EventType event) {
            for (final EventType t : this._events) {
                if (t == event) {
                    return true;
                }
            }
            return false;
        }
    }
}
