package gr.sr.playervalue.criteria;


import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PlayerClass
        implements ICriteria {
    private final Map<Integer, Integer> scores = new ConcurrentHashMap<>();
    private final List<Integer> changed = new LinkedList<>();
    private final Integer[] classes;

    public PlayerClass() {
        this.classes = CallBack.getInstance().getOut().getAllClassIds();
        loadData();
    }

    private void loadData() {
        this.scores.clear();
        this.changed.clear();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT classId, score FROM sunrise_playervalue_classes");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                int classId = rset.getInt("classId");
                int score = rset.getInt("score");
                this.scores.put(Integer.valueOf(classId), Integer.valueOf(score));
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventConfig.getInstance().getGlobalConfig("GearScore", "enableGearScore").setValue("false");
        }
        for (Integer i : this.classes) {
            if (!this.scores.containsKey(Integer.valueOf(i.intValue()))) {
                this.changed.add(Integer.valueOf(i.intValue()));
                this.scores.put(Integer.valueOf(i.intValue()), Integer.valueOf(0));
            }
        }
        save();
    }

    private void save() {
        if (this.changed.isEmpty()) {
            return;
        }
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            StringBuilder tb = new StringBuilder();
            for (Integer element : this.changed) {
                int i = element.intValue();
                tb.append("(" + i + "," + this.scores.get(Integer.valueOf(i)) + "),");
            }
            String values = tb.toString();
            try (PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_playervalue_classes VALUES " + values.substring(0, values.length() - 1) + ";")) {
                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.changed.clear();
    }

    public int getScore(int classId) {
        if (this.scores.containsKey(Integer.valueOf(classId))) {
            return ((Integer) this.scores.get(Integer.valueOf(classId))).intValue();
        }
        SunriseLoader.debug("PlayerValue engine: Class ID " + classId + " has no value setted up.", Level.WARNING);
        return 0;
    }

    public void setValue(int classId, int value) {
        this.scores.put(Integer.valueOf(classId), Integer.valueOf(value));
        this.changed.add(Integer.valueOf(classId));
    }

    public int getPoints(PlayerEventInfo player) {
        int playerClass = player.getActiveClass();
        return getScore(playerClass);
    }

    public static final PlayerClass getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final PlayerClass _instance = new PlayerClass();
    }
}


