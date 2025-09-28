package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class AntibotConfigs extends AbstractConfigs {
    
    public static boolean ENABLE_ANTIBOT_SYSTEMS;
    
    public static boolean ENABLE_DOUBLE_PROTECTION;
    public static boolean ENABLE_ANTIBOT_FOR_GMS;
    
    public static boolean ENABLE_ANTIBOT_FARM_SYSTEM;
    public static boolean ENABLE_ANTIBOT_FARM_SYSTEM_ON_RAIDS;
    public static int JAIL_TIMER;
    public static int TIME_TO_SPEND_IN_JAIL;
    public static int ANTIBOT_FARM_TYPE;
    public static float ANTIBOT_FARM_CHANCE;
    public static int ANTIBOT_MOB_COUNTER;
    public static boolean ENABLE_ANTIBOT_SPECIFIC_MOBS;
    public static List<Integer> ANTIBOT_FARM_MOBS_IDS;
    public static boolean ENABLE_ANTIBOT_ENCHANT_SYSTEM;
    public static int ENCHANT_CHANCE_TIMER;
    
    public static int ENCHANT_CHANCE_PERCENT_TO_START;
    public static int ENCHANT_CHANCE_PERCENT_TO_LOW;
    public static int ANTIBOT_ENCHANT_TYPE;
    public static int ANTIBOT_ENCHANT_COUNTER;
    public static int ANTIBOT_ENCHANT_CHANCE;

    public AntibotConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/AntiBot.ini");
        ENABLE_ANTIBOT_SYSTEMS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotSystems", "False"));
        ENABLE_DOUBLE_PROTECTION = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableDoubleProtection", "True"));
        ENABLE_ANTIBOT_FOR_GMS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotForGms", "False"));
        ENABLE_ANTIBOT_FARM_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotFarmSystem", "False"));
        ENABLE_ANTIBOT_FARM_SYSTEM_ON_RAIDS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotFarmSystemOnRaids", "True"));
        JAIL_TIMER = Integer.parseInt(this.getString(this._settings, this._override, "JailTimer", "180"));
        TIME_TO_SPEND_IN_JAIL = Integer.parseInt(this.getString(this._settings, this._override, "TimeToSpendInJail", "180"));
        ENABLE_ANTIBOT_SPECIFIC_MOBS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotSpecificMobs", "False"));
        ANTIBOT_FARM_TYPE = Integer.parseInt(this.getString(this._settings, this._override, "AntibotFarmType", "0"));
        ANTIBOT_FARM_CHANCE = Float.parseFloat(this.getString(this._settings, this._override, "AntibotFarmChance", "50"));
        ANTIBOT_MOB_COUNTER = Integer.parseInt(this.getString(this._settings, this._override, "AntibotMobCounter", "100"));
        String[] farmMobsIds = this.getString(this._settings, this._override, "FarmMobsIds", "").split(";");
        ANTIBOT_FARM_MOBS_IDS = new ArrayList(farmMobsIds.length);

        for(int i = 0; i < farmMobsIds.length; ++i) {
            String mobId = farmMobsIds[i];

            try {
                ANTIBOT_FARM_MOBS_IDS.add(Integer.parseInt(mobId.trim()));
            } catch (NumberFormatException var5) {
                _log.info("Antibot System: Error parsing mob id. Skipping " + mobId + ".");
            }
        }

        ENABLE_ANTIBOT_ENCHANT_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntibotEnchantSystem", "False"));
        ENCHANT_CHANCE_TIMER = Integer.parseInt(this.getString(this._settings, this._override, "EnchantChanceTimer", "180"));
        ENCHANT_CHANCE_PERCENT_TO_START = Integer.parseInt(this.getString(this._settings, this._override, "EnchantChancePercentToStart", "80"));
        ENCHANT_CHANCE_PERCENT_TO_LOW = Integer.parseInt(this.getString(this._settings, this._override, "EnchantChancePercentToLow", "10"));
        ANTIBOT_ENCHANT_TYPE = Integer.parseInt(this.getString(this._settings, this._override, "AntibotEnchantType", "0"));
        ANTIBOT_ENCHANT_COUNTER = Integer.parseInt(this.getString(this._settings, this._override, "AntibotEnchantCounter", "100"));
        ANTIBOT_ENCHANT_CHANCE = Integer.parseInt(this.getString(this._settings, this._override, "AntibotEnchantChance", "100"));
    }
    protected static AntibotConfigs instance;

    public static AntibotConfigs getInstance() {
        if(instance == null)
            instance = new AntibotConfigs();
        return instance;
    }
}
