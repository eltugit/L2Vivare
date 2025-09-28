package gr.sr.playervalue.criteria;


import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.base.GlobalConfigModel;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.ItemData;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GearScore
        implements ICriteria {
    private final Map<Integer, Integer> scores = new ConcurrentHashMap<>();
    private final List<Integer> changed = new LinkedList<>();

    public GearScore() {
        loadData();
    }

    private void loadData() {
        this.scores.clear();
        this.changed.clear();
        int size = 0;
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            Map<Integer, Integer> data = new ConcurrentHashMap<>();
            try (PreparedStatement statement = con.prepareStatement("SELECT itemId, score FROM sunrise_playervalue_items");
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int itemId = rset.getInt("itemId");
                    int score = rset.getInt("score");
                    data.put(Integer.valueOf(itemId), Integer.valueOf(score));
                }
            }
            Map<Integer, Integer> missing = new ConcurrentHashMap<>();
            for (Integer element : CallBack.getInstance().getOut().getAllArmorsId()) {
                int id = element.intValue();
                size++;
                if (data.containsKey(Integer.valueOf(id))) {
                    this.scores.put(Integer.valueOf(id), data.get(Integer.valueOf(id)));
                    continue;
                }
                int def = getDefaultValue(id);
                this.scores.put(Integer.valueOf(id), Integer.valueOf(def));
                missing.put(Integer.valueOf(id), Integer.valueOf(def));
            }
            for (Integer element : CallBack.getInstance().getOut().getAllWeaponsId()) {
                int id = element.intValue();
                size++;
                if (data.containsKey(Integer.valueOf(id))) {
                    this.scores.put(Integer.valueOf(id), data.get(Integer.valueOf(id)));
                    continue;
                }
                int def = getDefaultValue(id);
                this.scores.put(Integer.valueOf(id), Integer.valueOf(def));
                missing.put(Integer.valueOf(id), Integer.valueOf(def));
            }
            if (!missing.isEmpty()) {
                StringBuilder tb = new StringBuilder();
                for (Map.Entry<Integer, Integer> e : missing.entrySet()) {
                    tb.append("(" + e.getKey() + "," + e.getValue() + "),");
                }
                String values = tb.toString();
                try (PreparedStatement statement = con.prepareStatement("INSERT INTO sunrise_playervalue_items VALUES " + values.substring(0, values.length() - 1) + ";")) {
                    statement.execute();
                    missing = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventConfig.getInstance().getGlobalConfig("GearScore", "enableGearScore").setValue("false");
        }
        SunriseLoader.debug("Gear score engine - loaded " + size + " items.");
    }

    public void saveAll() {
        try (Connection con = CallBack.getInstance().getOut().getConnection()) {
            StringBuilder tb = new StringBuilder();
            for (Integer element : this.changed) {
                int i = element.intValue();
                tb.append("(" + i + "," + getScore(i) + "),");
            }
            String values = tb.toString();
            try (PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_playervalue_items VALUES " + values.substring(0, values.length() - 1) + ";")) {
                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getScore(int itemId) {
        return ((Integer) this.scores.get(Integer.valueOf(itemId))).intValue();
    }

    public void setScore(int itemId, int value) {
        this.scores.put(Integer.valueOf(itemId), Integer.valueOf(value));
        this.changed.add(Integer.valueOf(itemId));
    }

    public int getDefaultValue(int itemId) {
        ItemData item = new ItemData(itemId);
        int score = 0;
        String configName = "defVal_";
        if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_NONE()) {
            configName = configName + "N-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_D()) {
            configName = configName + "D-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_C()) {
            configName = configName + "C-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_B()) {
            configName = configName + "B-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_A()) {
            configName = configName + "A-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_S()) {
            configName = configName + "S-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_S80()) {
            configName = configName + "S80-Grade_";
        } else if (item.getCrystalType() == CallBack.getInstance().getValues().CRYSTAL_S84()) {
            configName = configName + "S84-Grade_";
        }
        if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_UNDERWEAR()) {
            configName = configName + "Underwear";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_L_EAR() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_LR_EAR() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_R_EAR()) {
            configName = configName + "Earring";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_NECK()) {
            configName = configName + "Necklace";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_R_FINGER() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_L_FINGER() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_LR_FINGER()) {
            configName = configName + "Ring";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_HEAD()) {
            configName = configName + "Helmet";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_R_HAND() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_LR_HAND()) {
            if (item.isWeapon()) {
                if (item.getWeaponType() == null) {
                    return 0;
                }
                String first = item.getWeaponType().toString();
                if (first.length() > 1) {
                    first = first.substring(0, 1);
                    String name = item.getWeaponType().toString();
                    name = name.substring(1, name.length()).toLowerCase();
                    configName = configName + first + name;
                } else {
                    configName = configName + item.getWeaponType().toString();
                }
            } else {
                return 0;
            }
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_L_HAND()) {
            configName = configName + "Shield";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_GLOVES()) {
            configName = configName + "Gloves";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_CHEST()) {
            configName = configName + "Chest";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_LEGS()) {
            configName = configName + "Gaiters";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_FEET()) {
            configName = configName + "Boots";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_BACK()) {
            configName = configName + "Cloak";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_FULL_ARMOR()) {
            configName = configName + "FullArmor";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_HAIR() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_HAIR2() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_HAIRALL()) {
            configName = configName + "Hair";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_R_BRACELET() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_L_BRACELET()) {
            configName = configName + "Bracelet";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_DECO()) {
            configName = configName + "Talisman";
        } else if (item.getBodyPart() == CallBack.getInstance().getValues().SLOT_BELT()) {
            configName = configName + "Belt";
        } else {
            return 0;
        }
        if (!EventConfig.getInstance().globalConfigExists(configName)) {
            GlobalConfigModel gc = EventConfig.getInstance().addGlobalConfig("GearScore", configName, "Gear score default value for " + configName + " equippable item type.", "0", 1);
            EventConfig.getInstance().saveGlobalConfig(gc);
            score = 0;
        } else {
            score = EventConfig.getInstance().getGlobalConfigInt(configName);
        }
        return score;
    }

    public int getPoints(PlayerEventInfo player) {
        int points = 0;
        for (ItemData item : player.getItems()) {
            if (item.isEquipped()) {
                if (item.isArmor() || item.isJewellery() || item.isWeapon()) {
                    points += getScore(item.getItemId());
                }
            }
        }
        return points;
    }

    public static final GearScore getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final GearScore _instance = new GearScore();
    }
}


