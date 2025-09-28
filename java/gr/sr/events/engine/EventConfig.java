package gr.sr.events.engine;

import gr.sr.events.Configurable;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.GlobalConfigModel;
import gr.sr.events.engine.html.EventHtmlManager;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.mini.ScheduleInfo;
import gr.sr.events.engine.mini.features.AbstractFeature;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventConfig {
    private final Map<EventType, Config> _eventConfigs = new ConcurrentHashMap<>();
    private final Map<String, List<GlobalConfigModel>> _globalConfigs = new ConcurrentHashMap<>();

    public void loadEventConfigs() {
        loadMiniEventModes();
        loadEventConfigsFromDb();
    }

    public void loadGlobalConfigs() {
        loadGlobalConfigsFromDb();
        EventSQLManager.addMissingGlobalConfigs();
        SunriseLoader.debug("Loaded GlobalConfigs engine.");
    }

    private void loadMiniEventModes() {
        int count = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_modes");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                EventType type = EventType.getType(rset.getString("event"));
                if (type != null) {
                    int modeId = rset.getInt("modeId");
                    String modeName = rset.getString("name");
                    String visibleName = rset.getString("visible_name");
                    String parameters = rset.getString("params");
                    boolean allowed = Boolean.parseBoolean(rset.getString("allowed"));
                    String maps = rset.getString("disallowedMaps");
                    String times = rset.getString("times");
                    int npcId = rset.getInt("npcId");
                    loadMode(type, modeId, modeName, visibleName, parameters, maps, times, allowed, npcId);
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SunriseLoader.debug("Loaded " + count + " mini event modes.");
    }

    private void loadMode(EventType type, int modeId, String modeName, String visibleName, String parameters, String maps, String time, boolean allowed, int npcId) {
        MiniEventManager manager = EventManager.getInstance().createManager(type, modeId, modeName, visibleName);
        if (manager == null) {
            SunriseLoader.debug("manager's null after it was created!", Level.WARNING);
            return;
        }
        manager.getMode().setAllowed(allowed);
        manager.getMode().getScheduleInfo().encrypt(time);
        if (!maps.equals("") && !maps.equals(" ")) {
            for (String s : maps.split(";")) {
                manager.getMode().getDisMaps().add(Integer.valueOf(Integer.parseInt(s)));
            }
        }
        EventMode.FeatureType featureType = null;
        String[] featuresAndConfigs = parameters.split(";");
        if (featuresAndConfigs.length > 0 && featuresAndConfigs[0] != "" && featuresAndConfigs[0] != " ") {
            for (String features : featuresAndConfigs) {
                String[] splitted = features.split(":");
                for (EventMode.FeatureType t : EventMode.FeatureType.values()) {
                    if (t.toString().equals(splitted[0])) {
                        featureType = t;
                        break;
                    }
                }
                if (featureType == null) {
                    SunriseLoader.debug("feature type - " + splitted[0] + " doesn't exist. (event " + type.getAltTitle() + ", modeId " + modeId + ")", Level.WARNING);
                } else {
                    manager.getMode().addFeature(null, featureType, splitted[1]);
                }
            }
        }
        manager.getMode().refreshScheduler();
        manager.getMode().setNpcId(npcId);
    }

    public MiniEventManager createDefaultMode(EventType type) {
        MiniEventManager manager = EventManager.getInstance().createManager(type, 1, "Default", "Default");
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_modes WHERE event = '" + type.getAltTitle() + "' AND modeId = " + '\001')) {
                statement.execute();
            }
            MiniEventManager event = (MiniEventManager) ((Map) EventManager.getInstance().getMiniEvents().get(type)).get(Integer.valueOf(1));
            try (PreparedStatement preparedStatement1 = con.prepareStatement("INSERT INTO sunrise_modes VALUES (?,?,?,?,?,?,?,?,?)")) {
                preparedStatement1.setString(1, type.getAltTitle());
                preparedStatement1.setInt(2, 1);
                preparedStatement1.setString(3, event.getMode().getModeName().replaceAll("'", ""));
                preparedStatement1.setString(4, event.getMode().getVisibleName().replaceAll("'", ""));
                preparedStatement1.setString(5, String.valueOf(event.getMode().isAllowed()));
                preparedStatement1.setString(6, getParams(event));
                preparedStatement1.setString(7, getDisMaps(event));
                preparedStatement1.setString(8, getTimesAvailable(event));
                preparedStatement1.setInt(9, event.getMode().getNpcId());
                preparedStatement1.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return manager;
    }

    public void updateEventModes(EventType type, int modeId) {
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            if (modeId <= 0) {
                try (PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_modes WHERE event = '" + type.getAltTitle() + "'")) {
                    statement.execute();
                }
                for (Map.Entry<Integer, MiniEventManager> e : (Iterable<Map.Entry<Integer, MiniEventManager>>) ((Map) EventManager.getInstance().getMiniEvents().get(type)).entrySet()) {
                    try (PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO sunrise_modes VALUES (?,?,?,?,?,?,?,?,?)")) {
                        preparedStatement.setString(1, type.getAltTitle());
                        preparedStatement.setInt(2, ((Integer) e.getKey()).intValue());
                        preparedStatement.setString(3, ((MiniEventManager) e.getValue()).getMode().getModeName().replaceAll("'", ""));
                        preparedStatement.setString(4, ((MiniEventManager) e.getValue()).getMode().getVisibleName().replaceAll("'", ""));
                        preparedStatement.setString(5, String.valueOf(((MiniEventManager) e.getValue()).getMode().isAllowed()));
                        preparedStatement.setString(6, getParams(e.getValue()));
                        preparedStatement.setString(7, getDisMaps(e.getValue()));
                        preparedStatement.setString(8, getTimesAvailable(e.getValue()));
                        preparedStatement.setInt(9, ((MiniEventManager) e.getValue()).getMode().getNpcId());
                        preparedStatement.execute();
                    }
                }
            } else {
                try (PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_modes WHERE event = '" + type.getAltTitle() + "' AND modeId = " + modeId)) {
                    statement.execute();
                }
                MiniEventManager event = EventManager.getInstance().getMiniEvent(type, modeId);
                if (event != null) {
                    try (PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO sunrise_modes VALUES (?,?,?,?,?,?,?,?,?)")) {
                        preparedStatement.setString(1, type.getAltTitle());
                        preparedStatement.setInt(2, modeId);
                        preparedStatement.setString(3, event.getMode().getModeName().replaceAll("'", ""));
                        preparedStatement.setString(4, event.getMode().getVisibleName().replaceAll("'", ""));
                        preparedStatement.setString(5, String.valueOf(event.getMode().isAllowed()));
                        preparedStatement.setString(6, getParams(event));
                        preparedStatement.setString(7, getDisMaps(event));
                        preparedStatement.setString(8, getTimesAvailable(event));
                        preparedStatement.setInt(9, event.getMode().getNpcId());
                        preparedStatement.execute();
                    }
                } else {
                    SunriseLoader.debug("Tried to save unexisting event mode - " + type.getAltTitle() + ", mode " + modeId, Level.WARNING);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getTimesAvailable(MiniEventManager manager) {
        ScheduleInfo info = manager.getMode().getScheduleInfo();
        return info.decrypt();
    }

    private String getDisMaps(MiniEventManager manager) {
        StringBuilder tb = new StringBuilder();
        for (Integer element : manager.getMode().getDisMaps()) {
            int mapId = element.intValue();
            tb.append(mapId + ";");
        }
        String result = tb.toString();
        if (result.length() > 0) {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String getParams(MiniEventManager manager) {
        StringBuilder tb = new StringBuilder();
        for (AbstractFeature feature : manager.getMode().getFeatures()) {
            tb.append(feature.getType().toString() + ":" + feature.getParams() + ";");
        }
        String result = tb.toString();
        if (result.length() == 0) {
            return result;
        }
        return result.substring(0, result.length() - 1);
    }

    private void loadEventConfigsFromDb() {
        int count = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_configs");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                EventType type = EventType.getType(rset.getString("event"));
                if (type != null) {
                    this._eventConfigs.put(type, new Config(type, Boolean.parseBoolean(rset.getString("allowed"))));
                    count += deconvert(type, rset.getString("params"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (EventType t : EventType.values()) {
            if (t.allowEdits() && t != EventType.Unassigned) {
                if (!this._eventConfigs.containsKey(t)) {
                    this._eventConfigs.put(t, new Config(t, true));
                    addNew(t);
                }
            }
        }
        SunriseLoader.debug("Loaded " + count + " configs for events.");
    }

    private String convert(EventType type) {
        StringBuilder tb = new StringBuilder();
        Configurable event = EventManager.getInstance().getEvent(type);
        if (event == null) {
            SunriseLoader.debug("null event on EventConfig.convert, event type " + type.getAltTitle(), Level.SEVERE);
            return "";
        }
        for (Map.Entry<String, ConfigModel> e : (Iterable<Map.Entry<String, ConfigModel>>) event.getConfigs().entrySet()) {
            tb.append((String) e.getKey() + ":" + ((ConfigModel) e.getValue()).getValue());
            tb.append(";");
        }
        String result = tb.toString();
        if (result.length() > 0) {
            return result.substring(0, result.length() - 1);
        }
        return "";
    }

    private int deconvert(EventType type, String params) {
        try {
            int count = 0;
            String[] configs = params.split(";");
            for (String config : configs) {
                String value, key = config.split(":")[0];
                if ((config.split(":")).length > 1) {
                    value = config.split(":")[1];
                } else {
                    value = "";
                }
                EventManager.getInstance().getEvent(type).setConfig(key, value, false);
                count++;
            }
            return count;
        } catch (Exception exception) {
            return 0;
        }
    }

    public boolean isEventAllowed(EventType type) {
        return (this._eventConfigs.get(type) == null) ? false : ((Config) this._eventConfigs.get(type))._allowed;
    }

    public void setEventAllowed(EventType type, boolean b) {
        ((Config) this._eventConfigs.get(type))._allowed = b;
        updateInDb(type);
    }

    public void updateInDb(EventType type) {
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_configs WHERE event = '" + type.getAltTitle() + "'")) {
                statement.execute();
            }
            try (PreparedStatement statement = con.prepareStatement("INSERT INTO sunrise_configs VALUES ('" + type.getAltTitle() + "', '" + Boolean.toString(((Config) this._eventConfigs.get(type))._allowed) + "', ?)")) {
                statement.setString(1, convert(type));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addNew(EventType type) {
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO sunrise_configs VALUES ('" + type.getAltTitle() + "', 'true', ?)")) {
            statement.setString(1, convert(type));
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addConfig(EventType event, String param, String value, boolean addToValue) {
        if (event.isMiniEvent()) {
            for (MiniEventManager mgr : (EventManager.getInstance().getMiniEvents().get(event)).values()) {
                if (mgr != null) {
                    mgr.setConfig(param, value, addToValue);
                }
            }
        } else {
            EventManager.getInstance().getEvent(event).setConfig(param, value, addToValue);
        }
        updateInDb(event);
    }

    public void removeConfigMultiAddValue(EventType event, String key, int index) {
        if (event.isMiniEvent()) {
            for (MiniEventManager mgr : (EventManager.getInstance().getMiniEvents().get(event)).values()) {
                if (mgr != null) {
                    ((ConfigModel) mgr.getConfigs().get(key)).removeMultiAddValueIndex(index);
                }
            }
        } else {
            ((ConfigModel) EventManager.getInstance().getEvent(event).getConfigs().get(key)).removeMultiAddValueIndex(index);
        }
        updateInDb(event);
    }

    public String convertMapConfigs(EventMap map) {
        StringBuilder tb = new StringBuilder();
        int totalEvents = map.getConfigModels().size();
        int count = 1;
        for (Map.Entry<EventType, Map<String, ConfigModel>> e : (Iterable<Map.Entry<EventType, Map<String, ConfigModel>>>) map.getConfigModels().entrySet()) {
            if (!((Map) e.getValue()).values().isEmpty()) {
                tb.append(((EventType) e.getKey()).getAltTitle() + ":");
                int size = ((Map) e.getValue()).values().size();
                int i = 1;
                for (ConfigModel config : (e.getValue()).values()) {
                    tb.append(config.getKey() + "-" + config.getValue());
                    if (i < size) {
                        tb.append(",");
                    }
                    i++;
                }
                if (count < totalEvents) {
                    tb.append(";");
                }
            }
            count++;
        }
        return tb.toString();
    }

    public void saveMapConfigs(EventMap map) {
        EventMapSystem.getInstance().addMapToDb(map, true);
    }

    public void loadMapConfigs(EventMap map, String params) {
        try {
            if (params == null || params.isEmpty() || params.equals(" ")) {
                return;
            }
            String[] events = params.split(";");
            for (String event : events) {
                if ((event.split(":")).length > 1) {
                    EventType eventType = EventType.getType(event.split(":")[0]);
                    String[] configs = event.split(":")[1].split(",");
                    if (eventType == null) {
                        SunriseLoader.debug("error while mapConfigs loading - event: " + event + " does not exist, map ID = " + map.getGlobalId(), Level.WARNING);
                    } else {
                        for (String config : configs) {
                            String value, key = config.split("-")[0];
                            if ((config.split("-")).length > 1) {
                                value = config.split("-")[1];
                            } else {
                                value = "";
                            }
                            map.setConfigValue(eventType, key, value, false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMapConfig(EventMap map, EventType event, String param, String value, boolean addToValue) {
        map.setConfigValue(event, param, value, addToValue);
        saveMapConfigs(map);
    }

    public void removeMapConfigMultiAddValue(EventMap map, EventType event, String key, int index) {
        map.getConfigModel(event, key).removeMultiAddValueIndex(index);
        saveMapConfigs(map);
    }

    public String getMapConfig(EventMap map, EventType type, String key) {
        try {
            return map.getConfigModel(type, key).getValue();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMapConfigInt(EventMap map, EventType type, String key) {
        try {
            return map.getConfigModel(type, key).getValueInt();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean getMapConfigBoolean(EventMap map, EventType type, String key) {
        try {
            return map.getConfigModel(type, key).getValueBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadGlobalConfigsFromDb() {
        this._globalConfigs.clear();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM sunrise_globalconfigs");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                String type = rset.getString("configType");
                String key = rset.getString("key");
                String desc = rset.getString("desc");
                String value = rset.getString("value");
                int input = rset.getInt("inputType");
                addGlobalConfig(type, key, desc, value, input);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GlobalConfigModel addGlobalConfig(String type, String key, String desc, String value, int inputType) {
        if (!this._globalConfigs.containsKey(type)) {
            this._globalConfigs.put(type, new LinkedList<>());
        }
        GlobalConfigModel gc = new GlobalConfigModel(type, key, value, desc, inputType);
        ((List<GlobalConfigModel>) this._globalConfigs.get(type)).add(gc);
        return gc;
    }

    public void removeGlobalConfig(String type, String key) {
        for (GlobalConfigModel c : this._globalConfigs.get(type)) {
            if (c.getKey().equals(key)) {
                ((List) this._globalConfigs.get(type)).remove(c);
                break;
            }
        }
    }

    public List<GlobalConfigModel> getGlobalConfigs(String type) {
        return this._globalConfigs.get(type);
    }

    public String getGlobalConfigValue(String type, String key) {
        if (SunriseLoader.loadedOrBeingLoaded()) {
            GlobalConfigModel gc = getGlobalConfig(type, key);
            if (gc != null) {
                return gc.getValue();
            }
            SunriseLoader.debug("GlobalConfig '" + key + "' has not been found.", Level.WARNING);
        }
        return null;
    }

    public int getGlobalConfigInt(String type, String key) {
        if (SunriseLoader.loadedOrBeingLoaded()) {
            String val = getGlobalConfigValue(type, key);
            try {
                return Integer.parseInt(val);
            } catch (Exception e) {
                SunriseLoader.debug("GlobalConfig '" + key + "' int cast error.", Level.WARNING);
                return 0;
            }
        }
        return 0;
    }

    public boolean getGlobalConfigBoolean(String type, String key) {
        if (SunriseLoader.loadedOrBeingLoaded()) {
            String val = getGlobalConfigValue(type, key);
            try {
                return Boolean.parseBoolean(val);
            } catch (Exception e) {
                SunriseLoader.debug("GlobalConfig '" + key + "' boolean cast error.", Level.WARNING);
                return false;
            }
        }
        return false;
    }

    public int getGlobalConfigInt(String key) {
        return getGlobalConfigInt(null, key);
    }

    public String getGlobalConfigValue(String key) {
        return getGlobalConfigValue(null, key);
    }

    public boolean getGlobalConfigBoolean(String key) {
        return getGlobalConfigBoolean(null, key);
    }

    public String getGlobalConfigDesc(String type, String key) {
        GlobalConfigModel gc = getGlobalConfig(type, key);
        if (gc != null) {
            return gc.getDesc();
        }
        return null;
    }

    public String getGlobalConfigType(GlobalConfigModel config) {
        for (Map.Entry<String, List<GlobalConfigModel>> element : this._globalConfigs.entrySet()) {
            Map.Entry<String, List<GlobalConfigModel>> list = element;
            for (GlobalConfigModel c : list.getValue()) {
                if (config.getKey().equals(c.getKey())) {
                    return list.getKey();
                }
            }
        }
        return null;
    }

    public boolean globalConfigExists(String key) {
        GlobalConfigModel gc = getGlobalConfig(null, key);
        return (gc != null);
    }

    public void setGlobalConfigValue(GlobalConfigModel config, String key, String value) {
        config.setValue(value);
        try {
            if (key.equals("detailedDebug")) {
                SunriseLoader.detailedDebug = Boolean.parseBoolean(value);
            } else if (key.equals("detailedDebugToConsole")) {
                SunriseLoader.detailedDebugToConsole = Boolean.parseBoolean(value);
            } else if (key.equals("logToFile")) {
                SunriseLoader.logToFile = Boolean.parseBoolean(value);
            } else if (key.equals("maxWarnings")) {
                EventWarnings.MAX_WARNINGS = Integer.parseInt(value);
            } else if (key.equals("maxBuffsPerPage")) {
                EventHtmlManager.BUFFS_PER_PAGE = Integer.parseInt(value);
            } else if (key.equals("cbPage")) {
                EventHtmlManager.BBS_COMMAND = value;
            } else if (key.equals("allowVoicedCommands")) {
                EventManager.ALLOW_VOICE_COMMANDS = Boolean.parseBoolean(value);
            } else if (key.equals("registerVoicedCommand")) {
                EventManager.REGISTER_VOICE_COMMAND = value;
            } else if (key.equals("unregisterVoicedCommand")) {
                EventManager.UNREGISTER_VOICE_COMMAND = value;
            }
        } catch (Exception e) {
            SunriseLoader.debug("Wrong value set for config " + key + " (value = " + value + ")");
        }
        saveGlobalConfig(config);
    }

    public void saveGlobalConfig(GlobalConfigModel config) {
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_globalconfigs VALUES (?,?,?,?,?)")) {
            statement.setString(1, config.getCategory());
            statement.setString(2, config.getKey());
            statement.setString(3, config.getDesc());
            statement.setString(4, config.getValue());
            statement.setInt(5, config.getInputType());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public GlobalConfigModel getGlobalConfig(String type, String key) {
        if (type == null) {
            for (List<GlobalConfigModel> list : this._globalConfigs.values()) {
                for (GlobalConfigModel gc : list) {
                    if (gc.getKey().equals(key)) {
                        return gc;
                    }
                }
            }
            return null;
        }
        for (GlobalConfigModel gc : this._globalConfigs.get(type)) {
            if (gc.getKey().equals(key)) {
                return gc;
            }
        }
        return null;
    }

    public static final EventConfig getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EventConfig _instance = new EventConfig();
    }

    public class Config {
        protected boolean _allowed;

        public Config(EventType type, boolean allowed) {
            this._allowed = allowed;
        }
    }
}


