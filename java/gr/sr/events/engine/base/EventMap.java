package gr.sr.events.engine.base;

import gr.sr.events.Configurable;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.EventMapSystem;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.interf.PlayerEventInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventMap {
    private final int _globalId;
    private boolean _saved = true;
    private String _mapName;
    private String _configs;
    private String _mapDesc;
    private int _highestSpawnId;
    private final List<EventType> _events;
    private final List<EventSpawn> _spawns = new LinkedList<>();
    private final Map<Integer, Map<SpawnType, Integer>> _history = new ConcurrentHashMap<>();
    private final Map<Integer, EventSpawn> _lastSpawns = new ConcurrentHashMap<>();
    private final List<EventSpawn> _doorsSpawn = new LinkedList<>();
    private boolean _hasDoors;
    public static Comparator<EventSpawn> compareByIdAsc;
    private final Map<EventType, Map<String, ConfigModel>> _configModels = new ConcurrentHashMap<>();

    static {
        compareByIdAsc = ((s1, s2) -> {
            int id1 = s1.getSpawnId();
            int id2 = s2.getSpawnId();
            return (id1 == id2) ? 0 : ((id1 < id2) ? -1 : 1);
        });
        compareByIdDesc = ((s1, s2) -> {
            int id1 = s1.getSpawnId();
            int id2 = s2.getSpawnId();
            return (id1 == id2) ? 0 : ((id1 > id2) ? -1 : 1);
        });
        compareByType = ((s1, s2) -> {
            SpawnType t1 = s1.getSpawnType();
            SpawnType t2 = s2.getSpawnType();
            return t1.compareTo(t2);
        });
    }

    public static Comparator<EventSpawn> compareByIdDesc;
    public static Comparator<EventSpawn> compareByType;

    public EventMap(int mapId, String mapName, String mapDesc, List<EventType> events, List<EventSpawn> spawns, String configs) {
        this._globalId = mapId;
        this._mapName = mapName;
        this._mapDesc = mapDesc;
        this._configs = configs;
        this._spawns.clear();
        this._history.clear();
        this._lastSpawns.clear();
        this._events = events;
        if (this._events == null) {
            SunriseLoader.debug("_events null in EventMap constructor");
            this._events.clear();
        }
        this._configModels.clear();
        addSpawns(spawns);
        initDoors();
    }

    public void loadConfigs() {
        for (EventType event : this._events) {
            initEventsConfigs(event);
        }
        EventConfig.getInstance().loadMapConfigs(this, this._configs);
    }

    private void initEventsConfigs(EventType event) {
        this._configModels.put(event, new LinkedHashMap<>());
        Configurable conf = EventManager.getInstance().getEvent(event);
        if (conf == null || conf.getMapConfigs() == null) {
            return;
        }
        for (ConfigModel config : conf.getMapConfigs().values()) {
            ((Map<String, ConfigModel>) this._configModels.get(event)).put(config.getKey(), new ConfigModel(config.getKey(), config.getValue(), config.getDesc(), config.getInput()));
        }
    }

    private void deleteEventsConfigs(EventType event) {
        this._configModels.remove(event);
    }

    public void setConfigValue(EventType event, String key, String value, boolean addToValue) {
        try {
            if (!this._configModels.containsKey(event)) {
                SunriseLoader.debug("Trying to set MapConfig's: map ID " + getGlobalId() + " event " + event.getAltTitle() + ", config's key = " + key + ". The map doesn't have such event.");
                return;
            }
            if (((Map) this._configModels.get(event)).get(key) == null) {
                SunriseLoader.debug("Trying to set MapConfig's: map ID " + getGlobalId() + " event " + event.getAltTitle() + ", config's key = " + key + ", but this config doesn't exist for that map! Skipping...");
                return;
            }
            if (!addToValue) {
                ((ConfigModel) ((Map) this._configModels.get(event)).get(key)).setValue(value);
            } else {
                ((ConfigModel) ((Map) this._configModels.get(event)).get(key)).addToValue(value);
            }
        } catch (Exception e) {
            SunriseLoader.debug("Error setting map config's value to " + value + ", config's key = " + key + ", map ID = " + getGlobalId() + " and event = " + event.getAltTitle(), Level.WARNING);
            e.printStackTrace();
        }
    }

    public Map<EventType, Map<String, ConfigModel>> getConfigModels() {
        return this._configModels;
    }

    public ConfigModel getConfigModel(EventType event, String key) {
        try {
            if (!this._configModels.containsKey(event)) {
                SunriseLoader.debug("Trying to set MapConfig's value: map ID " + getGlobalId() + " event " + event.getAltTitle() + ", config's key = " + key + ". The map doesn't have such event.");
                return null;
            }
            return (ConfigModel) ((Map) this._configModels.get(event)).get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void addSpawns(List<EventSpawn> spawns) {
        if (spawns == null) {
            return;
        }
        this._spawns.addAll(spawns);
        for (EventSpawn spawn : spawns) {
            if (!this._history.containsKey(Integer.valueOf(spawn.getSpawnTeam()))) {
                this._history.put(Integer.valueOf(spawn.getSpawnTeam()), new LinkedHashMap<>());
            }
            if (!((Map) this._history.get(Integer.valueOf(spawn.getSpawnTeam()))).containsKey(spawn.getSpawnType())) {
                ((Map<SpawnType, Integer>) this._history.get(Integer.valueOf(spawn.getSpawnTeam()))).put(spawn.getSpawnType(), Integer.valueOf(0));
            }
        }
        recalcLastSpawnId();
    }

    public List<EventSpawn> getSpawns(int teamId, SpawnType type) {
        List<EventSpawn> temp = new LinkedList<>();
        for (EventSpawn spawn : this._spawns) {
            if ((spawn.getSpawnTeam() == teamId || teamId == -1) && spawn.getSpawnType() == type) {
                temp.add(spawn);
            }
        }
        return temp;
    }

    public List<EventSpawn> getMarkers(int teamId) {
        return getSpawns(teamId, SpawnType.Radar);
    }

    public void clearHistory(int teamId, SpawnType type) {
        if (teamId == -1) {
            for (Map.Entry<Integer, Map<SpawnType, Integer>> e : this._history.entrySet()) {
                ((Map<SpawnType, Integer>) this._history.get(e.getKey())).put(type, Integer.valueOf(0));
            }
        } else {
            ((Map<SpawnType, Integer>) this._history.get(Integer.valueOf(teamId))).put(type, Integer.valueOf(0));
        }
    }

    public EventSpawn getNextSpawn(int teamId, SpawnType type) {
        List<EventSpawn> spawns = getSpawns(teamId, type);
        if (spawns == null || spawns.isEmpty()) {
            return null;
        }
        if (teamId == -1) {
            teamId = 0;
        }
        int lastId = 0;
        try {
            lastId = ((Integer) ((Map) this._history.get(Integer.valueOf(teamId))).get(type)).intValue();
        } catch (NullPointerException e) {
            lastId = 0;
        }
        EventSpawn nextSpawn = null;
        for (EventSpawn spawn : spawns) {
            if (spawn.getSpawnId() > lastId) {
                nextSpawn = spawn;
                break;
            }
        }
        if (nextSpawn == null) {
            nextSpawn = spawns.get(0);
        }
        lastId = nextSpawn.getSpawnId();
        if (!this._history.containsKey(Integer.valueOf(teamId))) {
            this._history.put(Integer.valueOf(teamId), new LinkedHashMap<>());
        }
        ((Map<SpawnType, Integer>) this._history.get(Integer.valueOf(teamId))).put(type, Integer.valueOf(lastId));
        return nextSpawn;
    }

    public List<EventSpawn> getSpawns() {
        return this._spawns;
    }

    public EventSpawn getSpawn(int spawnId) {
        for (EventSpawn spawn : this._spawns) {
            if (spawn.getSpawnId() == spawnId) {
                return spawn;
            }
        }
        return null;
    }

    public boolean removeSpawn(int spawnId, boolean db) {
        for (EventSpawn spawn : this._spawns) {
            if (spawn.getSpawnId() == spawnId) {
                this._spawns.remove(spawn);
                if (getSpawns(spawn.getSpawnTeam(), spawn.getSpawnType()).isEmpty()) {
                    this._history.remove(spawn.getSpawnType());
                }
                if (db) {
                    EventMapSystem.getInstance().removeSpawnFromDb(spawn);
                }
                recalcLastSpawnId();
                return true;
            }
        }
        return false;
    }

    private void recalcLastSpawnId() {
        int highestId = 0;
        for (EventSpawn spawn : this._spawns) {
            if (spawn.getSpawnId() > highestId) {
                highestId = spawn.getSpawnId();
            }
        }
        this._highestSpawnId = highestId;
    }

    private void initDoors() {
        for (EventSpawn spawn : this._spawns) {
            if (spawn.getSpawnType() == SpawnType.Door) {
                this._doorsSpawn.add(spawn);
                this._hasDoors = true;
            }
        }
    }

    public String[] getAviableConfigs(EventType type) {
        if (type != EventType.Unassigned && this._events.contains(type)) {
            Configurable event = EventManager.getInstance().getEvent(type, 1);
            if (event == null) {
                System.out.println("null event at getAviableConfigs(EventType)");
                return null;
            }
            return (String[]) event.getMapConfigs().keySet().toArray((Object[]) new String[event.getMapConfigs().size()]);
        }
        System.out.println("getAviableConfigs - type " + type.getAltTitle() + " returned null.");
        return null;
    }

    public boolean hasDoor() {
        return this._hasDoors;
    }

    public List<EventSpawn> getDoors() {
        return this._doorsSpawn;
    }

    public EventSpawn getLastSpawn(int teamId) {
        return this._lastSpawns.get(Integer.valueOf(teamId));
    }

    public String getMapName() {
        return this._mapName;
    }

    public String getMapDesc() {
        return this._mapDesc;
    }

    public int getGlobalId() {
        return this._globalId;
    }

    public List<EventType> getEvents() {
        return this._events;
    }

    public String getConfigs() {
        return this._configs;
    }

    public void setConfigs(String s) {
        this._configs = s;
    }

    public void setMapName(String name) {
        this._mapName = name;
        this._saved = false;
    }

    public void setMapDesc(String desc) {
        this._mapDesc = desc;
        this._saved = false;
    }

    public int getNewSpawnId() {
        return this._highestSpawnId + 1;
    }

    public void addEvent(EventType type) {
        this._events.add(type);
        EventMapSystem.getInstance().addMapToEvent(this, type);
        initEventsConfigs(type);
        this._saved = false;
    }

    public void removeEvent(EventType type) {
        if (this._events.remove(type)) {
            EventMapSystem.getInstance().removeMapFromEvent(this, type);
            deleteEventsConfigs(type);
            if (this._events.isEmpty()) {
                this._events.add(EventType.Unassigned);
                EventMapSystem.getInstance().addMapToEvent(this, EventType.Unassigned);
            }
            this._saved = false;
        }
    }

    public boolean isSaved() {
        return this._saved;
    }

    public void setSaved(boolean b) {
        this._saved = b;
        if (this._saved) {
            initDoors();
        }
    }

    public boolean checkForSpawns(SpawnType type, int teamId, int count) {
        try {
            return (getSpawns(teamId, type).size() >= count);
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public String getMissingSpawns() {
        StringBuilder tb = new StringBuilder();
        for (EventType type : getEvents()) {
            Configurable event = EventManager.getInstance().getEvent(type);
            if (event == null) {
                return "";
            }
            if (type.isRegularEvent()) {
                tb.append(event.getMissingSpawns(this));
            }
        }
        return tb.toString();
    }

    public String getNotWorkingEvents() {
        StringBuilder tb = new StringBuilder();
        for (EventType type : getEvents()) {
            if (type.isMiniEvent()) {
                String temp = "";
                for (MiniEventManager manager : (EventManager.getInstance().getMiniEvents().get(type)).values()) {
                    temp = temp + manager.getMissingSpawns(this);
                }
                if (temp.length() > 0) {
                    tb.append("<font color=LEVEL>" + type.getHtmlTitle() + "</font><br1>");
                    tb.append(temp);
                    tb.append("<br>");
                }
            }
        }
        return tb.toString();
    }

    public void checkMap(PlayerEventInfo gm) {
        for (EventType type : getEvents()) {
            if (type == EventType.Classic_1v1 || type == EventType.Classic_2v2 || type == EventType.PartyvsParty || type == EventType.TvT || type == EventType.TvTAdv || type == EventType.MiniTvT) {
                if (!checkForSpawns(SpawnType.Regular, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1, count 1");
                }
                if (!checkForSpawns(SpawnType.Regular, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 2, count 1");
                }
                continue;
            }
            if (type == EventType.CTF) {
                if (!checkForSpawns(SpawnType.Flag, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type FLAG, team 1, count 1.");
                }
                if (!checkForSpawns(SpawnType.Flag, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type FLAG, team 2, count 1.");
                }
                if (!checkForSpawns(SpawnType.Regular, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1, count 1 or more.");
                }
                if (!checkForSpawns(SpawnType.Regular, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 2, count 1 or more.");
                }
                continue;
            }
            if (type == EventType.DM || type == EventType.LMS) {
                if (!checkForSpawns(SpawnType.Regular, 1, -1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, count 1 or more. (team doesn't matter)");
                }
                continue;
            }
            if (type == EventType.Mutant || type == EventType.Zombies) {
                if (!checkForSpawns(SpawnType.Regular, 1, -1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR,count 1 or more. (team doesn't matter");
                }
                if (!checkForSpawns(SpawnType.Zombie, 1, -1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type ZOMBIE, count 1 or more.");
                }
                continue;
            }
            if (type == EventType.Korean) {
                if (!checkForSpawns(SpawnType.Safe, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type SAFE, team 1, count 1. This is initial spawn for Players.");
                }
                if (!checkForSpawns(SpawnType.Safe, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type SAFE, team 2, count 1. This is initial spawn for Players.");
                }
                if (!checkForSpawns(SpawnType.Regular, 1, 4)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1, count 4 (for each player one spot)");
                }
                if (!checkForSpawns(SpawnType.Regular, 2, 4)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 2, count 4 (for each player one spot)");
                }
                continue;
            }
            if (type == EventType.Underground_Coliseum) {
                if (!checkForSpawns(SpawnType.Regular, 1, 4)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1. count 1. This is initial spawn used to teleport players before event starts.");
                }
                if (!checkForSpawns(SpawnType.Regular, 2, 4)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 2, count 1. This is initial spawn used to teleport players before event starts.");
                }
                if (!checkForSpawns(SpawnType.Safe, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type SAFE, team 1, count 1. This is respawn spot.");
                }
                if (!checkForSpawns(SpawnType.Safe, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type SAFE, team 2, count 1. This is respawn spot.");
                }
                continue;
            }
            if (type == EventType.RBHunt) {
                if (!checkForSpawns(SpawnType.Boss, -1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type BOSS count 1.");
                }
                if (!checkForSpawns(SpawnType.Regular, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1, count 1 or more.");
                }
                if (!checkForSpawns(SpawnType.Regular, 2, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 2, count 1 or more.");
                }
                continue;
            }
            if (type == EventType.SurvivalArena) {
                boolean round1 = false;
                boolean round2 = false;
                boolean round3 = false;
                if (!round1) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type MONSTER for FIRST round!");
                }
                if (!round2) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type MONSTER for SECOND round!");
                }
                if (!round3) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type MONSTER for FINAL round!");
                }
                if (!checkForSpawns(SpawnType.Regular, 1, 1)) {
                    gm.sendMessage(type.getAltTitle() + ": Missing spawn type REGULAR, team 1, count 1.");
                }
            }
        }
    }
}


