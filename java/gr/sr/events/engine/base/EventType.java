package gr.sr.events.engine.base;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.EventMapSystem;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.events.*;
import gr.sr.events.engine.mini.events.KoreanManager;
import gr.sr.events.engine.mini.events.MiniTvTManager;
import gr.sr.events.engine.mini.events.OnevsOneManager;
import gr.sr.events.engine.mini.events.PartyvsPartyManager;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public enum EventType {
    Unassigned(0, "", "", EventType.Category.MainTeam, true, false),
    TvT(1, "TvT", "Team vs Team", EventType.Category.MainTeam, true, false, TeamVsTeam.class),
    CTF(2, "CTF", "Capture the Flag", EventType.Category.MainTeam, true, false, CaptureTheFlag.class),
    Domination(3, "Domination", "Domination", EventType.Category.MainTeam, true, false, Domination.class),
    MassDomination(4, "MassDom", "Mass Domination", EventType.Category.MainTeam, true, false, MassDomination.class),
    DM(5, "DM", "Deathmatch", EventType.Category.MainFFA, true, false, Deathmatch.class),
    LastMan(6, "LastMan", "Last Man Standing", EventType.Category.MainFFA, true, false, LastManStanding.class),
    TvTAdv(7, "TvTAdv", "TvT Advanced", EventType.Category.MainTeam, true, false, VIPTeamVsTeam.class),
    LuckyChests(8, "Chests", "Lucky Chests", EventType.Category.MainFFA, true, false, LuckyChests.class),
    Zombies(9, "Zombies", "Zombies", EventType.Category.MainTeam, true, false, Zombies.class),
    Mutant(10, "Mutant", "Mutant", EventType.Category.MainTeam, true, false, Mutant.class),
    TreasureHunt(11, "THunt", "Treasure Hunt", EventType.Category.MainTeam, true, false, TreasureHunt.class),
    TreasureHuntPvp(12, "THuntPvP", "Treasure Hunt PvP", EventType.Category.MainTeam, true, false, TreasureHuntPvp.class),
    HuntingGround(13, "HuntGround", "Hunting Grounds", EventType.Category.MainTeam, true, false, HuntingGrounds.class),
    Battlefields(14, "Battlefields", "Battlefields", EventType.Category.MainTeam, true, false, Battlefield.class),
    Commanders(15, "Commanders", "Commanders", EventType.Category.MainTeam, true, false),
    BombFight(16, "Bomb", "Bomb Fight", EventType.Category.MainTeam, true, false),
    RussianRoulette(17, "Russian", "Russian Roulette", EventType.Category.MainTeam, true, false),
    Simon(18, "Simon", "Simon Says", EventType.Category.MainTeam, true, false),
    Classic_1v1(50, "1v1", "Single players fights", EventType.Category.Mini, true, false, OnevsOneManager.class),
    PartyvsParty(51, "PTvsPT", "Party fights", EventType.Category.Mini, true, false, PartyvsPartyManager.class),
    Korean(52, "Korean", "Korean Style", EventType.Category.Mini, true, false, KoreanManager.class),
    MiniTvT(53, "MiniTvT", "Mini TvT", EventType.Category.Mini, true, true, MiniTvTManager.class),
    LMS(54, "LMS", "Last Man", EventType.Category.Mini, true, false),
    LTS(55, "LTS", "Last Team", EventType.Category.Mini, true, false),
    Classic_2v2(56, "2v2", "2v2 event", EventType.Category.Mini, true, false),
    Tournament(57, "Tournament", "Tournament", EventType.Category.Mini, false, false),
    Underground_Coliseum(58, "UC", "Tower Crush", EventType.Category.Mini, true, false),
    Hitman(59, "Hitman", "Hitman", EventType.Category.Mini, false, false),
    RBHunt(60, "RBH", "Raid Hunt", EventType.Category.Mini, true, false),
    SurvivalArena(61, "Survival", "Survival Arena", EventType.Category.Mini, true, true);

    private int _order;
    private EventType.Category _category;
    private String _shortName;
    private String _longName;
    private boolean _allowEdits;
    private boolean _allowConfig;
    private Class<? extends Event> eventClass;
    public static int lastGivenEvent = 0;

    private EventType(int order, String shortName, String longName, EventType.Category category, boolean allowEdits, boolean allowConfig, Class<? extends Event> eventClass) {
        this._order = order;
        this._category = category;
        this._shortName = shortName;
        this._longName = longName;
        this._allowEdits = allowEdits;
        this._allowConfig = allowConfig;
        this.eventClass = eventClass;
    }

    private EventType(int order, String shortName, String longName, EventType.Category category, boolean allowEdits, boolean allowConfig) {
        this(order, shortName, longName, category, allowEdits, allowConfig, (Class)null);
    }

    public Event loadEvent(MainEventManager manager) {
        if (this.eventClass != null) {
            try {
                if (this.isRegularEvent()) {
                    Constructor<? extends Event> constructor = this.eventClass.getConstructor(EventType.class, MainEventManager.class);
                    if (constructor == null) {
                        SunriseLoader.debug("Wrong constructor for event " + this.getAltTitle() + ".", Level.SEVERE);
                        return null;
                    }

                    return (Event)constructor.newInstance(this, manager);
                }
            } catch (Exception var3) {
                System.out.println(this.getAltTitle() + " event load error");
                var3.printStackTrace();
                throw new RuntimeException(var3);
            }
        }

        return null;
    }

    public int getId() {
        return this.getOrder();
    }

    public int getOrder() {
        return this._order;
    }

    public int getMainEventId() {
        return 0;
    }

    public boolean isRegularEvent() {
        return this._category == EventType.Category.MainTeam || this._category == EventType.Category.MainFFA;
    }

    public boolean isMiniEvent() {
        return this._category == EventType.Category.Mini;
    }

    public boolean isGlobalEvent() {
        return this._category == EventType.Category.Global;
    }

    public boolean isFFAEvent() {
        return this._category == EventType.Category.MainFFA;
    }

    public EventType.Category getCategory() {
        return this._category;
    }

    public boolean allowConfig() {
        return this._allowConfig;
    }

    public boolean allowEdits() {
        return this._allowEdits;
    }

    public String getAltTitle() {
        return this._shortName;
    }

    public String getHtmlTitle() {
        return this._longName;
    }

    public static EventType getById(int id) {
        EventType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EventType t = var1[var3];
            if (t.getId() == id) {
                return t;
            }
        }

        return Unassigned;
    }

    public static EventType getType(String value) {
        EventType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EventType t = var1[var3];
            if (t.toString().equalsIgnoreCase(value) || t.getAltTitle().equalsIgnoreCase(value) || t.getHtmlTitle().equalsIgnoreCase(value) || String.valueOf(t.getId()).equals(value)) {
                return t;
            }
        }

        return null;
    }

    public static EventType[] getMiniEvents() {
        List<EventType> types = new LinkedList();
        EventType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EventType t = var1[var3];
            types.add(t);
        }

        return (EventType[])types.toArray(new EventType[types.size()]);
    }

    public static EventType getEventByMainId(int id) {
        EventType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            EventType t = var1[var3];
            if (t.getMainEventId() == id) {
                return t;
            }
        }

        return null;
    }

    public static EventType getNextRegularEvent() {
        EventType t = EventManager.getInstance().getMainEventManager().nextAvailableEvent(false);
        if (t == null) {
            return null;
        } else {
            lastGivenEvent = t.getId();
            return t;
        }
    }

    public static EventType getNextRegularEvent(int lastId) {
        int i = 0;

        EventType[] var3;
        int var4;
        int var5;
        EventType t;
        for(int next = lastId + 1; i < values().length; ++i) {
            var3 = values();
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
                t = var3[var5];
                if (t.getId() == next && t.isRegularEvent() && EventConfig.getInstance().isEventAllowed(t) && EventManager.getInstance().getMainEvent(t) != null && EventMapSystem.getInstance().getMapsCount(t) > 0) {
                    return t;
                }
            }

            ++next;
        }

        var3 = values();
        var4 = var3.length;

        for(var5 = 0; var5 < var4; ++var5) {
            t = var3[var5];
            if (t.isRegularEvent() && EventConfig.getInstance().isEventAllowed(t) && EventManager.getInstance().getMainEvent(t) != null && EventMapSystem.getInstance().getMapsCount(t) > 0) {
                return t;
            }
        }

        return null;
    }

    public static enum Category {
        MainTeam,
        MainFFA,
        Mini,
        Global;

        private Category() {
        }
    }
}
