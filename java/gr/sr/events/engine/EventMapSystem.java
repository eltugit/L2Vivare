package gr.sr.events.engine;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.EventSpawn;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.Loc;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventMapSystem {
    private final Map<EventType, Map<Integer, EventMap>> _maps = new ConcurrentHashMap<>();
    private int _lastMapId = 0;

    public EventMapSystem() {
        for (EventType type : EventType.values()) {
            this._maps.put(type, new ConcurrentHashMap<>());
        }
    }

    private EventType[] getTypes(String s) {
        String[] splits = s.split(";");
        List<EventType> types = new LinkedList<>();
        for (String typeString : splits) {
            EventType t = EventType.getType(typeString);
            if (t != null) {
                types.add(t);
            }
        }
        return types.<EventType>toArray(new EventType[types.size()]);
    }

    public String convertToString(List<EventType> types) {
        StringBuilder tb = new StringBuilder();
        int i = 1;
        for (EventType t : types) {
            tb.append(t.toString());
            if (i < types.size()) {
                tb.append(";");
            }
            i++;
        }
        return tb.toString();
    }

    public void loadMaps() {
        int count = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT mapId, mapName, eventType, configs, description FROM sunrise_maps");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                String type = rset.getString("eventType");
                List<EventType> types = new LinkedList<>();
                for (EventType t : getTypes(type)) {
                    types.add(t);
                }
                EventMap map = new EventMap(rset.getInt("mapId"), rset.getString("mapName"), rset.getString("description"), types, loadSpawns(rset.getInt("mapId")), rset.getString("configs"));
                map.loadConfigs();
                if (map.getGlobalId() > this._lastMapId) {
                    this._lastMapId = map.getGlobalId();
                }
                for (EventType t : types) {
                    ((Map<Integer, EventMap>) this._maps.get(t)).put(Integer.valueOf(((Map) this._maps.get(t)).size() + 1), map);
                }
                count++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SunriseLoader.debug("Event Engine: Loaded " + count + " EventMaps.");
    }

    public List<EventSpawn> loadSpawns(int arenaId) {
        List<EventSpawn> spawns = new LinkedList<>();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT mapId, spawnId, x, y, z, teamId, type, note FROM sunrise_spawns WHERE mapId = " + arenaId);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                EventSpawn spawn = new EventSpawn(rset.getInt("mapId"), rset.getInt("spawnId"), new Loc(rset.getInt("x"), rset.getInt("y"), rset.getInt("z")), rset.getInt("teamId"), rset.getString("type"));
                String note = rset.getString("note");
                if (note != null) {
                    spawn.setNote(note);
                }
                spawn.setSaved(true);
                spawns.add(spawn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spawns;
    }

    public void addSpawnToDb(EventSpawn spawn) {
        if (spawn.isSaved()) {
            return;
        }
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_spawns VALUES (" + spawn.getMapId() + ", " + spawn.getSpawnId() + ", " + spawn.getLoc().getX() + ", " + spawn.getLoc().getY() + ", " + spawn.getLoc().getZ() + ", " + spawn.getSpawnTeam() + ", '" + spawn.getSpawnType().toString() + "', " + ((spawn.getNote() == null) ? "''" : ("'" + spawn.getNote() + "'")) + ")")) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        spawn.setSaved(true);
    }

    public void removeSpawnFromDb(EventSpawn spawn) {
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_spawns WHERE mapId = " + spawn.getMapId() + " AND spawnId = " + spawn.getSpawnId())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeMapFromDb(EventMap map) {
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_maps WHERE mapId = " + map.getGlobalId())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMapToDb(EventMap map, boolean force) {
        if (map.isSaved() && !force) {
            return;
        }
        map.setConfigs(EventConfig.getInstance().convertMapConfigs(map));
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_maps VALUES (" + map.getGlobalId() + ", '" + map.getMapName().replaceAll("'", "") + "', '" + convertToString(map.getEvents()) + "', '" + map.getConfigs() + "', '" + map.getMapDesc() + "')")) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        map.setSaved(true);
    }

    public EventMap getNextMap(MiniEventManager manager, int lastId, EventMode mode) {
        EventType type = manager.getEventType();
        int nextMapId = lastId;
        EventMap map = null;
        int limit = 0;
        while (limit < ((Map) this._maps.get(type)).size() + 99) {
            limit++;
            nextMapId++;
            map = (EventMap) ((Map) this._maps.get(type)).get(Integer.valueOf(nextMapId));
            if (map == null) {
                nextMapId = 0;
                continue;
            }
            if (!manager.canRun(map) || mode.getDisMaps().contains(Integer.valueOf(map.getGlobalId()))) {
                map = null;
                continue;
            }
            return map;
        }
        SunriseLoader.debug("No map available for event " + type.getAltTitle() + " and mode " + mode.getModeName(), Level.WARNING);
        return map;
    }

    public int getMapIndex(EventType event, EventMap map) {
        for (Map.Entry<Integer, EventMap> e : (Iterable<Map.Entry<Integer, EventMap>>) ((Map) this._maps.get(event)).entrySet()) {
            if (((EventMap) e.getValue()).getGlobalId() == map.getGlobalId()) {
                return ((Integer) e.getKey()).intValue();
            }
        }
        return 0;
    }

    public EventMap getMapById(int id) {
        for (Map<Integer, EventMap> map : this._maps.values()) {
            for (Map.Entry<Integer, EventMap> m : map.entrySet()) {
                if (((EventMap) m.getValue()).getGlobalId() == id) {
                    return m.getValue();
                }
            }
        }
        return null;
    }

    public int getNewMapId() {
        return ++this._lastMapId;
    }

    public int getMapsCount(EventType type) {
        return ((Map) this._maps.get(type)).size();
    }

    public Map<Integer, EventMap> getMaps(EventType type) {
        return this._maps.get(type);
    }

    public boolean removeMap(int id) {
        EventMap map = getMapById(id);
        if (map == null) {
            return false;
        }
        removeMapFromDb(map);
        if (map.getGlobalId() >= this._lastMapId) {
            this._lastMapId--;
        }
        for (EventType element : map.getEvents()) {
            EventType type = element;
            for (Map.Entry<Integer, EventMap> e : (Iterable<Map.Entry<Integer, EventMap>>) ((Map) this._maps.get(type)).entrySet()) {
                if (((EventMap) e.getValue()).getGlobalId() == id) {
                    ((Map) this._maps.get(type)).remove(e.getKey());
                    reorganizeMaps(type);
                }
            }
        }
        for (EventSpawn spawn : map.getSpawns()) {
            removeSpawnFromDb(spawn);
        }
        return true;
    }

    private void reorganizeMaps(EventType type) {
        Collection<EventMap> maps = ((Map) this._maps.get(type)).values();
        Map<Integer, EventMap> mapping = new ConcurrentHashMap<>();
        for (EventMap map : maps) {
            mapping.put(Integer.valueOf(mapping.size() + 1), map);
        }
        this._maps.put(type, mapping);
    }

    public void addMap(EventMap map) {
        for (EventType type : map.getEvents()) {
            ((Map<Integer, EventMap>) this._maps.get(type)).put(Integer.valueOf(((Map) this._maps.get(type)).size() + 1), map);
        }
    }

    public void addMapToEvent(EventMap map, EventType type) {
        List<EventMap> maps = new LinkedList<>();
        maps.addAll(((Map) this._maps.get(type)).values());
        maps.add(map);
        ((Map) this._maps.get(type)).clear();
        int i = 0;
        for (EventMap m : maps) {
            ((Map<Integer, EventMap>) this._maps.get(type)).put(Integer.valueOf(i), m);
            i++;
        }
    }

    public void removeMapFromEvent(EventMap map, EventType type) {
        for (Map.Entry<Integer, EventMap> e : (Iterable<Map.Entry<Integer, EventMap>>) ((Map) this._maps.get(type)).entrySet()) {
            if (((EventMap) e.getValue()).getGlobalId() == map.getGlobalId()) {
                ((Map) this._maps.get(type)).remove(e.getKey());
            }
        }
    }

    public List<EventMap> getMainEventMaps(EventType type) {
        if (!type.isRegularEvent()) {
            return null;
        }
        List<EventMap> maps = new LinkedList<>();
        maps.addAll(((Map) this._maps.get(type)).values());
        return maps;
    }

    public EventMap getMap(EventType type, String mapName) {
        for (EventMap map : (this._maps.get(type)).values()) {
            if (map.getMapName().toString().equals(mapName)) {
                return map;
            }
        }
        return null;
    }

    public static final EventMapSystem getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EventMapSystem _instance = new EventMapSystem();
    }
}


