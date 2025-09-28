package gr.sr.events.engine.main.base;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainEventInstanceTypeManager {
    private int _highestId = 0;

    public MainEventInstanceTypeManager() {
        loadAll();
    }

    private void loadAll() {
        int count = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT event, id, name, visible_name, params FROM sunrise_main_instances");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                EventType type = EventType.getType(rset.getString("event"));
                if (type != null) {
                    int id = rset.getInt("id");
                    String name = rset.getString("name");
                    String visibleName = rset.getString("visible_name");
                    String params = rset.getString("params");
                    AbstractMainEvent event = EventManager.getInstance().getMainEvent(type);
                    if (event == null) {
                        SunriseLoader.debug("MainEventInstanceTypeManager - event object of type " + rset.getString("event") + " doesn't exist. Skipping");
                        continue;
                    }
                    MainEventInstanceType instance = new MainEventInstanceType(id, event, name, visibleName, params);
                    event.insertConfigs(instance);
                    instance.loadConfigs();
                    addInstanceType(instance, false);
                    count++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SunriseLoader.debug("Loaded " + count + " main event InstanceTypes.");
        for (AbstractMainEvent event : EventManager.getInstance().getMainEvents().values()) {
            if (event.getInstanceTypes().isEmpty()) {
                MainEventInstanceType instance = new MainEventInstanceType(getNextId(), event, "Default", "Default Instance", null);
                event.insertConfigs(instance);
                addInstanceType(instance, true);
                SunriseLoader.debug("Event " + event.getEventName() + " had no InstanceTypes set up. They either got deleted or you're starting the server with this event for the first time. This has been automatically fixed!");
            }
        }
    }

    public void updateInstanceType(MainEventInstanceType type) {
        type.setParams(type.encodeParams());
        addInstanceType(type, true);
    }

    public void addInstanceType(MainEventInstanceType type, boolean storeToDb) {
        AbstractMainEvent event = type.getEvent();
        event.addInstanceType(type);
        if (type.getId() > this._highestId) {
            this._highestId = type.getId();
        }
        if (storeToDb) {
            try (Connection con = CallBack.getInstance().getOut().getConnection();
                 PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_main_instances VALUES (?,?,?,?,?)")) {
                statement.setString(1, event.getEventType().getAltTitle());
                statement.setInt(2, type.getId());
                statement.setString(3, type.getName());
                statement.setString(4, type.getVisibleName());
                statement.setString(5, type.getParams());
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeInstanceType(MainEventInstanceType type) {
        AbstractMainEvent event = type.getEvent();
        event.removeInstanceType(type);
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_main_instances WHERE event = '" + event.getEventType().getAltTitle() + "' AND id = " + type.getId())) {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized int getNextId() {
        return ++this._highestId;
    }

    public static final MainEventInstanceTypeManager getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final MainEventInstanceTypeManager _instance = new MainEventInstanceTypeManager();
    }
}


