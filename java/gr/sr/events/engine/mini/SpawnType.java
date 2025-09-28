package gr.sr.events.engine.mini;

import gr.sr.events.engine.base.EventType;

import java.util.List;

public enum SpawnType
{
    Regular("CD9F36", (EventType[])null, "Adds place where the players of team %TEAM% will be spawned."), 
    Door("916406", new EventType[] { EventType.Classic_1v1, EventType.PartyvsParty, EventType.Korean, EventType.MiniTvT }, "Adds door to the event's instance."), 
    Npc("FFFFFF", (EventType[])null, "Adds an NPC to the event with ID you specify."), 
    Fence("878578", (EventType[])null, "Adds fence to the event's instance."), 
    Buffer("68AFB3", new EventType[] { EventType.Classic_1v1, EventType.PartyvsParty, EventType.Korean, EventType.MiniTvT }, "Adds buffer NPC to the event's instance."), 
    Spectator("FFFFFF", new EventType[] { EventType.Classic_1v1, EventType.PartyvsParty, EventType.Korean, EventType.MiniTvT }, "Defines observation spot for all spectators."), 
    MapGuard("FFFFFF", (EventType[])null, "Adds a map guard to the event's instance."), 
    Safe("5BB84B", new EventType[] { EventType.Korean }, ""), 
    Flag("867BC4", new EventType[] { EventType.CTF, EventType.Underground_Coliseum }, ""), 
    Zombie("7C9B59", new EventType[] { EventType.Zombies, EventType.Mutant }, ""), 
    Monster("879555", new EventType[] { EventType.SurvivalArena }, ""), 
    Boss("BE2C49", new EventType[] { EventType.RBHunt }, ""), 
    Zone("68AFB3", new EventType[] { EventType.Domination, EventType.MassDomination }, ""), 
    Chest("68AFB3", new EventType[] { EventType.LuckyChests }, ""), 
    Simon("68AFB3", new EventType[] { EventType.Simon }, ""), 
    Russian("68AFB3", new EventType[] { EventType.RussianRoulette }, ""), 
    Base("68AFB3", new EventType[] { EventType.Battlefields }, ""), 
    VIP("68AFB3", new EventType[] { EventType.TvTAdv }, "");
    
    private String _htmlColor;
    private EventType[] _events;
    private String _desc;
    
    private SpawnType(final String htmlColor, final EventType[] allowedEvents, final String description) {
        this._htmlColor = htmlColor;
        this._events = allowedEvents;
        this._desc = description;
    }
    
    public String getHtmlColor() {
        return this._htmlColor;
    }
    
    public String getDefaultDesc() {
        return this._desc;
    }
    
    public boolean isForEvents(final List<EventType> events) {
        if (events == null) {
            return true;
        }
        for (final EventType t : events) {
            if (this.isForEvent(t)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isForEvent(final EventType type) {
        for (final EventType t : this._events) {
            if (t.getId() == type.getId()) {
                return true;
            }
        }
        return false;
    }
}
