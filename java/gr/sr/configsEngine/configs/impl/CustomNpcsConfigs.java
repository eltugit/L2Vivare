package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class CustomNpcsConfigs extends AbstractConfigs {

    public static boolean ENABLE_CUSTOM_GATEKEEPER;

    public static boolean ALLOW_TELEPORT_DURING_SIEGE;

    public static boolean ALLOW_TELEPORT_WITH_KARMA;

    public static boolean ALLOW_TELEPORT_WHILE_COMBAT;

    public static boolean ENABLE_PLAYERS_COUNT;

    public static String ZONE_TYPE_FOR_PLAYERS_COUNT;

    public static boolean ENABLE_NOBLE_MANAGER;

    public static int NOBLE_NPC_ID;

    public static int NOBLE_ITEM_ID;

    public static int NOBLE_ITEM_AMOUNT;

    public static int NOBLE_REQUIRED_LEVEL;

    public static boolean ENABLE_CASINO_MANAGER;

    public static int CASINO_NPC_ID;

    public static int CASINO_ITEM_ID;

    public static int CASINO_BET1;

    public static int CASINO_BET2;

    public static int CASINO_BET3;

    public static int CASINO_REQUIRED_LEVEL;

    public static int CASINO_SUCCESS_CHANCE;

    public static boolean ENABLE_POINTS_MANAGER;

    public static int POINTS_NPC_ID;

    public static int POINTS_ITEM_ID_FOR_REP;

    public static int POINTS_ITEM_AMOUNT_FOR_REP;

    public static int POINTS_AMOUNT_FOR_REP;

    public static int POINTS_ITEM_ID_FOR_FAME;

    public static int POINTS_ITEM_AMOUNT_FOR_FAME;

    public static int POINTS_AMOUNT_FOR_FAME;

    public static boolean ENABLE_DELEVEL_MANAGER;

    public static int DELEVEL_NPC_ID;

    public static int DELEVEL_REQUIRED_LEVEL;

    public static boolean DELEVEL_DYNAMIC_PRICE;

    public static int DELEVEL_ITEM_ID;

    public static int DELEVEL_ITEM_AMOUNT;

    public static boolean ENABLE_REPORT_MANAGER;

    public static int REPORT_MANAGER_NPC_ID;

    public static int REPORT_REQUIRED_LEVEL;

    public static String REPORT_PATH;

    public static boolean ENABLE_ACHIEVEMENT_MANAGER;

    public static int ACHIEVEMENT_NPC_ID;

    public static int ACHIEVEMENT_REQUIRED_LEVEL;

    public static boolean ENABLE_BETA_MANAGER;

    public static int BETA_NPC_ID;

    public static boolean ENABLE_PREMIUM_MANAGER;

    public static int PREMIUM_NPC_ID;

    public static int PREMIUM_ITEM_ID;

    public static int PREMIUM_ITEM_AMOUNT_1;

    public static int PREMIUM_ITEM_AMOUNT_2;

    public static int PREMIUM_ITEM_AMOUNT_3;

    public static int PREMIUM_REQUIRED_LEVEL;

    public static boolean ENABLE_CASTLE_MANAGER;

    public static int CASTLE_NPC_ID;

    public static int CASTLE_REQUIRED_LEVEL;

    public static boolean ENABLE_GRANDBOSS_MANAGER;

    public static int GRANDBOSS_NPC_ID;

    public static List<Integer> GRANDBOSS_LIST;

    public CustomNpcsConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/Npcs.ini");
        ENABLE_CUSTOM_GATEKEEPER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableCustomGatekeeper", "False"));
        ALLOW_TELEPORT_DURING_SIEGE = Boolean.parseBoolean(this.getString(this._settings, this._override, "TeleportDuringSiege", "False"));
        ALLOW_TELEPORT_WITH_KARMA = Boolean.parseBoolean(this.getString(this._settings, this._override, "TeleportWithKarma", "False"));
        ALLOW_TELEPORT_WHILE_COMBAT = Boolean.parseBoolean(this.getString(this._settings, this._override, "TeleportWhileCombat", "False"));
        ENABLE_PLAYERS_COUNT = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePlayersCount", "False"));
        ZONE_TYPE_FOR_PLAYERS_COUNT = this.getString(this._settings, this._override, "ZoneTypeForPlayersCount", "FlagZone");
        ENABLE_NOBLE_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableNobleManager", "False"));
        NOBLE_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForNoblesseManager", "575"));
        NOBLE_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "ItemIDForNoble", "3470"));
        NOBLE_ITEM_AMOUNT = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForNoble", "100"));
        NOBLE_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForNoble", "76"));
        ENABLE_CASINO_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableCasinoManager", "False"));
        CASINO_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForCasinoManager", "574"));
        CASINO_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "ItemIDForBet", "3480"));
        CASINO_BET1 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForBet1", "5"));
        CASINO_BET2 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForBet2", "10"));
        CASINO_BET3 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForBet3", "15"));
        CASINO_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForCasino", "76"));
        CASINO_SUCCESS_CHANCE = Integer.parseInt(this.getString(this._settings, this._override, "SuccessChance", "40"));
        ENABLE_POINTS_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePointsManager", "False"));
        POINTS_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForPointsManager", "554"));
        POINTS_ITEM_ID_FOR_REP = Integer.parseInt(this.getString(this._settings, this._override, "ItemIdForRep", "3470"));
        POINTS_ITEM_AMOUNT_FOR_REP = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForRep", "10"));
        POINTS_AMOUNT_FOR_REP = Integer.parseInt(this.getString(this._settings, this._override, "AmountOfRep", "10000"));
        POINTS_ITEM_ID_FOR_FAME = Integer.parseInt(this.getString(this._settings, this._override, "ItemIdForFame", "3470"));
        POINTS_ITEM_AMOUNT_FOR_FAME = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForFame", "10"));
        POINTS_AMOUNT_FOR_FAME = Integer.parseInt(this.getString(this._settings, this._override, "AmountOfFame", "10000"));
        ENABLE_DELEVEL_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableDelevelManager", "False"));
        DELEVEL_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForDelevelManager", "560"));
        DELEVEL_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForDelevel", "80"));
        DELEVEL_DYNAMIC_PRICE = Boolean.parseBoolean(this.getString(this._settings, this._override, "DynamicPrices", "False"));
        DELEVEL_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "ItemIDForDelevel", "3470"));
        DELEVEL_ITEM_AMOUNT = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForDelevel", "1"));
        ENABLE_REPORT_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableReportManager", "False"));
        REPORT_MANAGER_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForReportManager", "553"));
        REPORT_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForReport", "80"));
        REPORT_PATH = this.getString(this._settings, this._override, "ReportPath", "data/sunrise/BugReports/");
        ENABLE_ACHIEVEMENT_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAchievementManager", "False"));
        ACHIEVEMENT_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForAchievementManager", "539"));
        ACHIEVEMENT_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForAchievement", "80"));
        ENABLE_BETA_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableBetaManager", "False"));
        BETA_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForBetaManager", "559"));
        ENABLE_PREMIUM_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePremiumManager", "False"));
        PREMIUM_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForPremiumManager", "542"));
        PREMIUM_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "ItemIDForPremium", "3470"));
        PREMIUM_ITEM_AMOUNT_1 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForPremium1", "10"));
        PREMIUM_ITEM_AMOUNT_2 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForPremium2", "20"));
        PREMIUM_ITEM_AMOUNT_3 = Integer.parseInt(this.getString(this._settings, this._override, "ItemAmountForPremium3", "30"));
        PREMIUM_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForPremium", "80"));
        ENABLE_CASTLE_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableCastleManager", "False"));
        CASTLE_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForCastleManager", "541"));
        CASTLE_REQUIRED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "LevelForCastleManager", "80"));
        ENABLE_GRANDBOSS_MANAGER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableGrandBossManager", "False"));
        GRANDBOSS_NPC_ID = Integer.parseInt(this.getString(this._settings, this._override, "NpcIdForGrandBossManager", "543"));
        String[] grandBossesLists = this.getString(this._settings, this._override, "GrandBossesList", "1204;1035;1048").trim().split(";");
        GRANDBOSS_LIST = new ArrayList<>(grandBossesLists.length);

        for (String bossId : grandBossesLists) {
            try {
                GRANDBOSS_LIST.add(Integer.parseInt(bossId));
            } catch (NumberFormatException var6) {
                _log.warn(this.getClass() + ": Wrong Grand boss Id passed: " + bossId);
                _log.warn(var6.getMessage());
            }
        }

    }

    protected static CustomNpcsConfigs instance;

    public static CustomNpcsConfigs getInstance() {
        if (instance == null)
            instance = new CustomNpcsConfigs();
        return instance;
    }
}
