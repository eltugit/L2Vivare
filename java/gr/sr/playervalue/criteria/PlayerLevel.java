package gr.sr.playervalue.criteria;


import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class PlayerLevel
        implements ICriteria {
    private final Map<Integer, Integer> _levels = new ConcurrentHashMap<>();

    public PlayerLevel() {
        loadData();
    }
    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
    private void loadData() {
        this._levels.clear();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT level, score FROM sunrise_playervalue_levels");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                int level = rset.getInt("level");
                int score = rset.getInt("score");
                this._levels.put(Integer.valueOf(level), Integer.valueOf(score));
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventConfig.getInstance().getGlobalConfig("GearScore", "enableGearScore").setValue("false");
        }
        if (this._levels.isEmpty()) {
            recalculate1(10, 10);
            saveToDb(this._levels);
            return;
        }
        Map<Integer, Integer> missing = new ConcurrentHashMap<>();
        for (int i = 1; i <= 85; i++) {
            if (!this._levels.containsKey(Integer.valueOf(i))) {
                missing.put(Integer.valueOf(i), Integer.valueOf(0));
                SunriseLoader.debug("PlayerValue engine - PlayerLevel criteria - in table 'sunrise_playervalue_levels' was missing record for level " + i + ". The engine will try to add it back with value 0, but you might need to correct it.", Level.SEVERE);
            }
        }
        if (!missing.isEmpty()) {
            saveToDb(missing);
        }
    }

    public void saveToDb(Map<Integer, Integer> levels) {
        if (levels.isEmpty()) {
            return;
        }
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            StringBuilder tb = new StringBuilder();
            for (Map.Entry<Integer, Integer> i : levels.entrySet()) {
                tb.append("(" + i.getKey() + "," + i.getValue() + "),");
            }
            String values = tb.toString();
            try (PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_playervalue_levels VALUES " + values.substring(0, values.length() - 1) + ";")) {
                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recalculate1(int firstValue, int levelCoefficient) {
        int value = firstValue;
        for (int level = 1; level <= 85; level++) {
            value += levelCoefficient;
            this._levels.put(Integer.valueOf(level), Integer.valueOf(value));
        }
    }

    public int getPoints(PlayerEventInfo player) {
        if (this._levels.containsKey(Integer.valueOf(player.getLevel()))) {
            return ((Integer) this._levels.get(Integer.valueOf(player.getLevel()))).intValue();
        }
        return 0;
    }

    public static final PlayerLevel getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final PlayerLevel _instance = new PlayerLevel();
    }
}


