package gabriel.config;


import gabriel.Utils.ExProperties;
import gabriel.events.siegeRank.RewardRank;
import gabriel.events.weeklyRank.objects.RankRewardObject;
import gabriel.events.weeklyRank.objects.RewardObject;
import gabriel.others.TempEnchant;
import gnu.trove.map.hash.TIntIntHashMap;
import gr.sr.utils.L2Properties;
import gr.sr.utils.StringUtil;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */


public class GabConfig {
    private static final Logger _log = LoggerFactory.getLogger(GabConfig.class);

    public static List<Integer> blocked_skills = new LinkedList<>();

    public static final String L2GABSON_CONFIG_FILE = "./config/gabriel/gabrielMods.ini";
    public static final String EPICRAID_CONFIG_FILE = "./config/gabriel/epicraid.ini";
    public static final String EXTREMEZONE_CONFIG_FILE = "./config/gabriel/extremeZone.ini";
    public static final String CHALLENGERZONE_CONFIG_FILE = "./config/gabriel/challengerZone.ini";
    public static final String CSKOTH_CONFIG_FILE = "./config/gabriel/castleSiegeKothEvent.ini";
    public static List<Integer> BUFF_ID_DELETE_EVENTS;


    public static int[] FLAG_IDS = {
            12009,22844,22845,22846,22847
    };

    public static boolean COMMUNITY_DONATE_COLOR_ALLOW;
    public static Map<String, Integer> COMMUNITY_DONATE_COLOR_PRICE;
    public static int COMMUNITY_DONATE_COLOR_ID;
    public static boolean COMMUNITY_DONATE_COLOR_NONPEACE;


    public static boolean ALLOW_CLAN_CLOAK;

    public static Map<Integer, Integer> CLAN_CLOAK;


    public static boolean COMMUNITY_DONATE_CLASSCHANGE_ALLOW;

    public static boolean COMMUNITY_DONATE_CLASSCHANGE_NONPEACE;

    public static int MAX_PT_CC;
    public static int COMMUNITY_DONATE_CLASSCHANGE_PRICE;

    public static int COMMUNITY_DONATE_CLASSCHANGE_ID;

    //---------------------------------------------
    // Extreme Zone
    //---------------------------------------------
    public static boolean EXTREME_EVENT_ENABLED;

    public static boolean EXTREME_EVENT_PVP_INSIDE;
    public static int EXTREME_EVENT_RUNNING_TIME;

    public static int EXTREME_EVENT_INSTANCE_ID;
    public static int EXTREME_EVENT_NPC_ID;
    public static int EXTREME_EVENT_MINIMUM_INSIDE_SECONDS;
    public static int EXTREME_EVENT_MINUTESTORESPAWN;

    public static int EXTREME_EVENT_ADMIN_OBJ_ID;
    public static int EXTREME_EVENT_ITEM_COUNT_DIVIDER;
    public static int EXTREME_EVENT_ITEM_MAX_COUNT;
    public static int EXTREME_EVENT_RADIUS_VALUE;

    public static boolean EXTREME_EVENT_RADIUS_CHECK;

    public static boolean EXTREME_EVENT_CLOSE_ALL;
    public static boolean EXTREME_EVENT_DRAW_LINES;
    public static String EXTREME_EVENT_DIAS_RUN;
    public static String[] EXTREME_EVENT_INTERVAL;
    public static String[] EXTREME_EVENT_PARTICIPATION_REWARD;
    public static String[] EXTREME_EVENT_CAPTURED_REWARD;

    public static String EX_TEXT;
    public static String EX_TEXT_FROM;
    public static int EX_TEXT_KIND;
    public static String EX_HOUR_RS;
    public static String EX_MIN_RS;
    public static String EX_SEC_RS;
    public static String EX_HOUR_RE;
    public static String EX_MIN_RE;
    public static String EX_SEC_RE;
    public static String EX_EVENT_FINISH;
    public static String EX_EVENT_CAPTURE;

    //---------------------------------------------
    // Challenger Zone
    //---------------------------------------------
    public static boolean CHALLENGER_EVENT_ENABLED;
    public static boolean CHALLENGER_EVENT_PVP_INSIDE;
    public static int CHALLENGER_EVENT_RUNNING_TIME;
    public static int CHALLENGER_EVENT_INSTANCE_ID;
    public static int CHALLENGER_EVENT_NPC_ID;
    public static int CHALLENGER_EVENT_MINIMUM_INSIDE_SECONDS;
    public static int CHALLENGER_EVENT_MINUTESTORESPAWN;
    public static int CHALLENGER_EVENT_ADMIN_OBJ_ID;
    public static int CHALLENGER_EVENT_ITEM_COUNT_DIVIDER;
    public static int CHALLENGER_EVENT_ITEM_MAX_COUNT;
    public static int CHALLENGER_EVENT_RADIUS_VALUE;
    public static boolean CHALLENGER_EVENT_RADIUS_CHECK;
    public static boolean CHALLENGER_EVENT_CLOSE_ALL;
    public static boolean CHALLENGER_EVENT_DRAW_LINES;
    public static String CHALLENGER_EVENT_DIAS_RUN;
    public static String[] CHALLENGER_EVENT_INTERVAL;
    public static String[] CHALLENGER_EVENT_PARTICIPATION_REWARD;
    public static String[] CHALLENGER_EVENT_CAPTURED_REWARD;
    public static String CHALLENGER_TEXT;
    public static String CHALLENGER_TEXT_FROM;
    public static int CHALLENGER_TEXT_KIND;
    public static String CHALLENGER_HOUR_RS;
    public static String CHALLENGER_MIN_RS;
    public static String CHALLENGER_SEC_RS;
    public static String CHALLENGER_HOUR_RE;
    public static String CHALLENGER_MIN_RE;
    public static String CHALLENGER_SEC_RE;
    public static String CHALLENGER_EVENT_FINISH;
    public static String CHALLENGER_EVENT_CAPTURE;


    public static boolean CUSTOM_SIEGE_TIME;
    // Gludio

    public static int SIEGEDAYCASTLEGludio;
    // Dion

    public static int SIEGEDAYCASTLEDion;
    // Giran

    public static int SIEGEDAYCASTLEGiran;
    // Oren

    public static int SIEGEDAYCASTLEOren;
    // Aden

    public static int SIEGEDAYCASTLEAden;
    // Innadril/Heine

    public static int SIEGEDAYCASTLEInnadril;
    // Goddard

    public static int SIEGEDAYCASTLEGoddard;
    // Rune

    public static int SIEGEDAYCASTLERune;
    // Schuttgart

    public static int SIEGEDAYCASTLESchuttgart;
    /**
     * Next siege time config (Retail 2)
     */

    public static int NEXT_SIEGE_TIME;

    /**
     * Hour of the siege will start
     */


    public static int HOUR_OF_SIEGE_GLUDIO;

    public static int MINUTE_OF_SIEGE_GLUDIO;

    public static int SECOND_OF_SIEGE_GLUDIO;

    public static int HOUR_OF_SIEGE_DION;

    public static int MINUTE_OF_SIEGE_DION;

    public static int SECOND_OF_SIEGE_DION;


    public static int HOUR_OF_SIEGE_GIRAN;

    public static int MINUTE_OF_SIEGE_GIRAN;

    public static int SECOND_OF_SIEGE_GIRAN;


    public static int HOUR_OF_SIEGE_OREN;

    public static int MINUTE_OF_SIEGE_OREN;

    public static int SECOND_OF_SIEGE_OREN;


    public static int HOUR_OF_SIEGE_ADEN;

    public static int MINUTE_OF_SIEGE_ADEN;

    public static int SECOND_OF_SIEGE_ADEN;


    public static int HOUR_OF_SIEGE_INNADRIL;

    public static int MINUTE_OF_SIEGE_INNADRIL;

    public static int SECOND_OF_SIEGE_INNADRIL;


    public static int HOUR_OF_SIEGE_GODDARD;

    public static int MINUTE_OF_SIEGE_GODDARD;

    public static int SECOND_OF_SIEGE_GODDARD;


    public static int HOUR_OF_SIEGE_RUNE;

    public static int MINUTE_OF_SIEGE_RUNE;

    public static int SECOND_OF_SIEGE_RUNE;


    public static int HOUR_OF_SIEGE_SCHUT;

    public static int MINUTE_OF_SIEGE_SCHUT;

    public static int SECOND_OF_SIEGE_SCHUT;


    public static boolean ALLOW_LIGHT_USE_HEAVY;

    public static String NOTALLOWCLASS;

    public static List<Integer> NOTALLOWEDUSEHEAVY;

    public static boolean ALLOW_HEAVY_USE_LIGHT;

    public static String NOTALLOWCLASSE;

    public static List<Integer> NOTALLOWEDUSELIGHT;


    public static boolean ALLOW_ELEMENT_PVP;

    public static boolean ALLOW_ELEMENT_HERO;

    public static boolean ALLOW_ENCHANT_HERO;

    private static String[] propertySplit;
    public static boolean DEBUG_GABS;
    public static String L2GABSON_SERVER_NAME;
    /**
     * New characters race spawn
     */
    public static boolean CUSTOM_SPAWN_FOR_RACE;
    public static int SPAWN_HUMAN_X;
    public static int SPAWN_HUMAN_Y;
    public static int SPAWN_HUMAN_Z;
    public static int SPAWN_ELF_X;
    public static int SPAWN_ELF_Y;
    public static int SPAWN_ELF_Z;
    public static int SPAWN_DARKELF_X;
    public static int SPAWN_DARKELF_Y;
    public static int SPAWN_DARKELF_Z;
    public static int SPAWN_ORC_X;
    public static int SPAWN_ORC_Y;
    public static int SPAWN_ORC_Z;
    public static int SPAWN_DWARF_X;
    public static int SPAWN_DWARF_Y;
    public static int SPAWN_DWARF_Z;
    public static int SPAWN_KAMAEL_X;
    public static int SPAWN_KAMAEL_Y;
    public static int SPAWN_KAMAEL_Z;



    //---------------------------------------------
    // CS King of the hill
    //---------------------------------------------
    
    public static boolean CSKOTH_EVENT_ENABLED;
    public static boolean CSKOTH_EVENT_IN_INSTANCE;
    public static String CSKOTH_EVENT_INSTANCE_FILE;
    public static String[] CSKOTH_EVENT_INTERVAL;
    public static int CSKOTH_EVENT_PARTICIPATION_TIME;
    public static int CSKOTH_EVENT_RUNNING_TIME;
    public static int CSKOTH_EVENT_PARTICIPATION_NPC_ID;
    public static int[] CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
    public static final int[] CSKOTH_EVENT_PARTICIPATION_FEE = new int[2];
    public static int CSKOTH_EVENT_MIN_PLAYERS_IN_TEAMS;
    public static int CSKOTH_EVENT_MAX_PLAYERS_IN_TEAMS;
    public static int CSKOTH_EVENT_RESPAWN_TELEPORT_DELAY;
    public static int CSKOTH_EVENT_START_LEAVE_TELEPORT_DELAY;
    
    public static String CSKOTH_EVENT_TEAM_1_NAME;
    public static int[] CSKOTH_EVENT_TEAM_1_COORDINATES = new int[3];
    
    public static String CSKOTH_EVENT_TEAM_2_NAME;
    public static int[] CSKOTH_EVENT_TEAM_2_COORDINATES = new int[3];
    public static int[] CSKOTH_EVENT_TEAM_OWNER_COORDINATES = new int[3];
    public static List<int[]> CSKOTH_EVENT_REWARDS;
    public static boolean CSKOTH_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
    public static boolean CSKOTH_EVENT_SCROLL_ALLOWED;
    public static boolean CSKOTH_EVENT_POTIONS_ALLOWED;
    public static boolean CSKOTH_EVENT_SUMMON_BY_ITEM_ALLOWED;
    public static List<Integer> CSKOTH_DOORS_IDS_TO_OPEN;
    public static List<Integer> CSKOTH_DOORS_IDS_TO_CLOSE;
    public static boolean CSKOTH_REWARD_TEAM_TIE;
    public static boolean CSKOTH_REWARD_PLAYER;
    public static byte CSKOTH_EVENT_MIN_LVL;
    public static byte CSKOTH_EVENT_MAX_LVL;
    public static int CSKOTH_EVENT_EFFECTS_REMOVAL;
    public static TIntIntHashMap CSKOTH_EVENT_FIGHTER_BUFFS;
    public static TIntIntHashMap CSKOTH_EVENT_MAGE_BUFFS;
    public static int CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP;
    public static boolean CSKOTH_ALLOW_VOICED_COMMAND;
    public static boolean CSKOTH_ALLOW_REGISTER_VOICED_COMMAND;
    public static int CSKOTH_EVENT_FAMA_KILL;
    
    public static boolean CSKOTH_ARENA_KILL_ENABLE;
    public static List<int[]> CSKOTH_EVENT_REWARDS_KILL;
    public static boolean CSKOTH_ARENA_FAME_ENABLE;
    public static boolean CSKOTH_ARENA_REWARDKILL_ENABLE;
    public static int CSKOTH_EVENT_CRYSTAL_NPC_ID;
    public static int[] CSKOTH_EVENT_CRYSTAL_LOC;
    public static List<Integer> CSKOTH_EVENT_DOORMANS_SPAWN;
    
    /**
     * Show welcome PM
     */
    public static boolean SHOW_WELCOME_PM;
    public static String PM_FROM;
    public static String[] PM_TEXT;


    public static Map<Integer, Integer> SKILL_BALANCE_FIX_CHANCE;

    public static Map<Integer, Integer> SKILL_BALANCE_FIX_TIMER;

    public static Map<Integer, Integer> SKILL_BALANCE_FIX_CHANCE_OLY;

    public static Map<Integer, Integer> SKILL_BALANCE_FIX_TIMER_OLY;

    public static Map<Integer, Integer> SKILL_BALANCE_FIX_CHANCE_ADD;

    public static Map<Integer, Integer> SKILL_BALANCE_FIX_CHANCE_OLY_ADD;


    public static boolean CUSTOM_TW_TIME;

    public static int SIEGEDAY_TW;

    public static int NEXT_TIME_TW;

    public static int HOUR_TW;

    public static int MINUTE_TW;

    /**
     * Show welcome PM
     */
    public static boolean SHOW_WELCOME_PMENG;
    public static String PM_FROMENG;
    public static String[] PM_TEXTENG;

    /**
     * WORLD PVP REWARD
     */
    public static boolean ENABLE_PVP_COUNT_SIEGETW;
    public static int PVP_COUNT_SIEGETW_MULTIPLYER;


    public static Map<Integer, TempEnchant> TEMP_ENCHANT;

    public static boolean TRADEABLE_AUGMENTATION;
    public static boolean ENABLE_ASSIST_REWARD;
    public static boolean EVERYONE_ASSIST_REWARD;
    public static String[] CLASSID_ASSIST_REWARD;
    public static int FAME_KILL;

    /**
     * Pvps for use chat
     */
    public static boolean CHAT_SHOUT;
    public static int PVPS_TO_USE_CHAT_SHOUT;
    public static boolean CHAT_TRADE;
    public static int PVPS_TO_USE_CHAT_TRADE;

    /**
     * Elemental levels
     */
    public static boolean ELEMENTAL_CUSTOM_LEVEL_ENABLE;
    public static int ELEMENTAL_LEVEL_WEAPON;
    public static int ELEMENTAL_LEVEL_ARMOR;
    public static int ELEMENTAL_WEAPON_AMOUNT;
    public static int ELEMENTAL_ARMOR_AMOUNT;

    /**
     * Subclasses
     */
    public static boolean HERO_SKILL_SUBS;
    public static boolean ALLOW_ELF_DELF_SUBS;
    public static boolean ALLOW_DELF_ELF_SUBS;
    public static boolean ALLOW_DAG_DAG_SUBS;
    public static boolean ALLOW_TANK_TANK_SUBS;
    public static boolean ALLOW_ARC_ARC_SUBS;
    public static boolean ALLOW_MAG_MAG_SUBS;
    public static boolean ALLOW_SUM_SUM_SUBS;
    public static boolean ALLOW_DOMINATOR_SUBS;
    public static Integer CLASS_HEAVY_NERF_SKILL;
    public static List<Integer> CLASS_ID_NERF_HEAVY;
    public static Integer BOW_USAGE_NERF_SKILL;
    public static List<Integer> CLASS_ID_NERF_BOW_USAGE;

    public static boolean ALLOW_FLAG_ON_RAID_AND_MINION;

    public static boolean BUFFS_COME_BACK_AFTER_CANCEL;
    public static int BUFFS_BACK_IN_S;
    public static boolean ALLOW_RETURN_IN_OLY;

    public static List<Integer> SKILL_ID_BLOCK_CANCEL;
    public static int[] BUFF_ID_IGNORE_LIST;

    public static boolean NON_REG_ENTER_CASTLE_SIEGE;
    public static boolean ALLOW_DUAL_BOX_SIEGE;

    public static boolean PLAYERS_CAN_HEAL_RB;

    public static boolean ALLOW_CREATE_SUBPLEDGE;
    public static int MAX_PLAYER_MAIN_CLAN;
    public static int MAX_PLAYER_ROYAL;
    public static int MAX_PLAYER_ROYAL_ABOVE11;
    public static int MAX_PLAYER_KNIGHT;
    public static int MAX_PLAYER_KNIGHT_ABOVE9;
    public static int MAX_PLAYER_ACADEMY;

    public static boolean ALLOW_COMMUNITY_CERTIFIC;
    public static boolean RESPAWN_TWWARD_ONLEAVE_SIEGEZONE;

    public static boolean ALLOW_PARTY_LIMITATIONS;
    public static int[] HEALLERS;
    public static int[] TANKS;
    public static int MAX_HEALERS;
    public static int MAX_TANKS;
    public static int MAX_DOMINATORS;

    public static int DROP_PROTECTION;
    public static boolean ALLOW_IKD_CA;
    public static boolean OLY_VISUAL_RESTRICTION;

    public static int CUSTOM_ENCHANT_COLOR_COIN_ID;
    public static int CUSTOM_ENCHANT_COLOR_COIN_AMOUNT;
    public static int CUSTOM_AGATHION_COIN_ID;
    public static int CUSTOM_AGATHION_COLOR_COIN_AMOUNT;

    public static boolean GABRIEL_BALANCER;
    public static boolean AUTO_ENCHANT_SKILLS_3RD_JOB;
    public static boolean AUTO_SHORTCUT_SKILLS;
    public static boolean ALLOW_GIVE_CLASS_CLOAK;

    public static boolean ALLOW_ALL_SETS;

    public static boolean COMMUNITY_DONATE_HERO_ALLOW;
    public static boolean COMMUNITY_DONATE_HERO_NONPEACE;
    public static int COMMUNITY_DONATE_HERO_PRICE;
    public static int COMMUNITY_DONATE_HERO_ID;

    public static boolean COMMUNITY_DONATE_CLANVIP_ALLOW;
    public static boolean COMMUNITY_DONATE_CLANVIP_NONPEACE;
    public static int COMMUNITY_DONATE_CLANVIP_PRICE;
    public static int COMMUNITY_DONATE_CLANVIP_ID;

    public static boolean COMMUNITY_DONATE_FULLSKILL_ALLOW;
    public static boolean COMMUNITY_DONATE_FULLSKILL_NONPEACE;
    public static int COMMUNITY_DONATE_FULLSKILL_PRICE;
    public static int COMMUNITY_DONATE_FULLSKILL_ID;

    public static int PREMIUM_PLAYER_EXTRA_ENCHANT_CHANCE;

    public static int MAX_WARD_CLAN;
    public static boolean ONE_WARD_AT_TIME_CLAN;
    public static boolean DROP_ON_MAX_WARDS;

    public static String ALLOW_CLASS_MASTERSCB;
    public static String CLASS_MASTERS_PRICECB;
    public static int[] CLASS_MASTERS_PRICE_LISTCB = new int[4];
    public static int CLASS_MASTERS_PRICE_ITEMCB;
    public static ArrayList<Integer> ALLOW_CLASS_MASTERS_LISTCB = new ArrayList<>();

    public static String PARTY_AREA_NPCS;
    public static String PARTY_AREA_NPCS2;
    public static String PARTY_AREA_NPCSMED;
    public static String PARTY_AREA_NPCSMED2;
    public static String PARTY_AREA_NPCSHARD;
    public static String PARTY_AREA_NPCSHARD2;
    public static String PARTY_AREA_NPCSVIP;
    public static String PARTY_AREA_LOCS;
    public static String PARTY_AREA_LOCS2;
    public static String PARTY_AREA_LOCSMED;
    public static String PARTY_AREA_LOCSMED2;
    public static String PARTY_AREA_LOCSHARD;
    public static String PARTY_AREA_LOCSHARD2;
    public static String PARTY_AREA_LOCSVIP;
    public static String PARTY_AREA_RESPAWN_DELAY;
    public static String PARTY_AREA_TIME;
    public static String PARTY_AREA_DURATION;
    public static String PARTY_AREA_PLAYER_TELEPORT;
    public static String PARTY_AREA_PLAYER_TELEPORTMED;
    public static String PARTY_AREA_PLAYER_TELEPORTHARD;
    public static String PARTY_AREA_PLAYER_TELEPORTVIP;

    public static String BOSSINFOEZ;
    public static String BOSSINFOMED;
    public static String BOSSINFOHARD;
    public static String BOSSINFOVIP;

    public static int PARTY_AREA_INSTANCE_ID;

    public static boolean PARTY_AREA_EVENT_PVP_INSIDE;


    //---------------------------------------------
    // Epic Raid
    //---------------------------------------------
    public static boolean ER_EVENT_ENABLED;
    public static int ER_EVENT_RUNNING_TIME;
    public static List<Integer> ER_BOSS_MONDAY;
    public static List<Integer> ER_BOSS_TUESDAY;
    public static List<Integer> ER_BOSS_WEDNESDAY;
    public static List<Integer> ER_BOSS_THURSDAY;
    public static List<Integer> ER_BOSS_FRIDAY;
    public static List<Integer> ER_BOSS_SATURDAY;
    public static List<Integer> ER_BOSS_SUNDAY;
    public static int ER_EVENT_INSTANCE_ID;
    public static int ER_EVENT_NPC_ID;
    public static int ER_EVENT_MINIMUM_INSIDE_SECONDS;
    public static int ER_EVENT_ADMIN_OBJ_ID;
    public static int ER_EVENT_ITEM_COUNT_DIVIDER;
    public static int ER_EVENT_ITEM_MAX_COUNT;
    public static int ER_EVENT_RADIUS_VALUE;
    public static int ER_EVENT_PERCENT_CLOSE;
    public static boolean ER_EVENT_RADIUS_CHECK;
    public static boolean ER_EVENT_CLOSE_ALL;
    public static boolean ER_EVENT_DRAW_LINES;
    public static String[] ER_EVENT_INTERVAL;
    public static String[] ER_EVENT_PARTICIPATION_REWARD;
    public static String ER_EVENT_DIAS_RUN;
    public static boolean ER_RANK_ENABLE;
    public static boolean ER_RANK_ONLY_HTML;
    public static Map<Integer, List<RewardRank>> ER_RANK_REWARD = new LinkedHashMap<>();

    public static boolean BBS_FORGE_ENABLED;
    public static int BBS_FORGE_ENCHANT_ITEM;
    public static int BBS_FORGE_ENCHANT_START;
    public static int BBS_FORGE_FOUNDATION_ITEM;
    public static int[] BBS_FORGE_FOUNDATION_PRICE_ARMOR;
    public static int[] BBS_FORGE_FOUNDATION_PRICE_WEAPON;
    public static int[] BBS_FORGE_FOUNDATION_PRICE_JEWEL;
    public static int[] BBS_FORGE_ENCHANT_MAX;
    public static int[] BBS_FORGE_WEAPON_ENCHANT_LVL;
    public static int[] BBS_FORGE_ARMOR_ENCHANT_LVL;
    public static int[] BBS_FORGE_JEWELS_ENCHANT_LVL;
    public static int[] BBS_FORGE_ENCHANT_PRICE_WEAPON;
    public static int[] BBS_FORGE_ENCHANT_PRICE_ARMOR;
    public static int[] BBS_FORGE_ENCHANT_PRICE_JEWELS;
    public static int[] BBS_FORGE_AUGMENT_ITEMS_LIST;
    public static long[] BBS_FORGE_AUGMENT_COUNT_LIST;
    public static int BBS_FORGE_WEAPON_ATTRIBUTE_MAX;
    public static int BBS_FORGE_ARMOR_ATTRIBUTE_MAX;
    public static int[] BBS_FORGE_ATRIBUTE_LVL_WEAPON;
    public static int[] BBS_FORGE_ATRIBUTE_LVL_ARMOR;
    public static int[] BBS_FORGE_ATRIBUTE_PRICE_ARMOR;
    public static int[] BBS_FORGE_ATRIBUTE_PRICE_WEAPON;
    public static boolean BBS_FORGE_ATRIBUTE_PVP;
    public static String[] BBS_FORGE_GRADE_ATTRIBUTE;

    public static boolean ENABLE_AUCTION_SYSTEM;
    public static long AUCTION_FEE;
    public static int AUCTION_INACTIVITY_DAYS_TO_DELETE;
    public static boolean ALLOW_AUCTION_OUTSIDE_TOWN;
    public static int SECONDS_BETWEEN_ADDING_AUCTIONS;
    public static boolean AUCTION_PRIVATE_STORE_AUTO_ADDED;

    public static boolean ALLOW_DROP_CALCULATOR;
    public static int[] DROP_CALCULATOR_DISABLED_TELEPORT;
    // Weekly Rank

    public static boolean WEEKLYRANK_ALLOW;
    public static boolean WEEKLYRANK_ALLOW_CLAN;
    public static boolean WEEKLYRANK_ALLOW_PLAYER;
    public static boolean WEEKLYRANK_ALLOW_PLAYER_ASSIST;

    public static int WEEKLYRANK_DAY;
    public static int WEEKLYRANK_HOUR;
    public static int WEEKLYRANK_MINUTE;

    public static int WEEKLYRANK_CLAN_TOP;
    public static int WEEKLYRANK_PLAYER_TOP;
    public static int WEEKLYRANK_PLAYER_TOP_ASSIST;


    public static Map<Integer, RankRewardObject> WEEKLYRANK_RANK_REWARD_CLAN;

    public static Map<Integer, RankRewardObject> WEEKLYRANK_RANK_REWARD_PLAYER;
    public static Map<Integer, RankRewardObject> WEEKLYRANK_RANK_REWARD_PLAYER_ASSIST;

    public static final String WEEKLYRANK_CONFIG_FILE = "./config/gabriel/WeeklyRank.ini";

    public static void loadWeeklyRank() {
        ExProperties weeklyRank = load(WEEKLYRANK_CONFIG_FILE);

        WEEKLYRANK_ALLOW = weeklyRank.getProperty("EnableWeeklyRank", false);
        WEEKLYRANK_ALLOW_CLAN = weeklyRank.getProperty("EnableWeeklyClanRank", false);
        WEEKLYRANK_ALLOW_PLAYER = weeklyRank.getProperty("EnableWeeklyPlayerRank", false);
        WEEKLYRANK_ALLOW_PLAYER_ASSIST = weeklyRank.getProperty("EnableWeeklyPlayerAssistRank", false);


        WEEKLYRANK_DAY = weeklyRank.getProperty("DayToEnableReward", 1);
        WEEKLYRANK_HOUR = weeklyRank.getProperty("HourToEnableReward", 12);
        WEEKLYRANK_MINUTE = weeklyRank.getProperty("MinutesToEnableReward", 30);

        WEEKLYRANK_CLAN_TOP = weeklyRank.getProperty("ClanRewardTopAmount", 5);
        WEEKLYRANK_PLAYER_TOP = weeklyRank.getProperty("PlayerRewardTopAmount", 5);
        WEEKLYRANK_PLAYER_TOP_ASSIST = weeklyRank.getProperty("PlayerAssistRewardTopAmount", 5);

        WEEKLYRANK_RANK_REWARD_CLAN = new LinkedHashMap<>();
        WEEKLYRANK_RANK_REWARD_PLAYER = new LinkedHashMap<>();
        WEEKLYRANK_RANK_REWARD_PLAYER_ASSIST = new LinkedHashMap<>();

        String[] clanRewards = weeklyRank.getProperty("ClanRewardTopRewards", "1-57,5000/57,4000/57,3000;2-57,5000/57,4000/57,3000;").split(";");

        for (String clanReward : clanRewards) {
            String[] rankSplit = clanReward.split("-");
            int rank = Integer.parseInt(rankSplit[0]);
            List<RewardObject> rewardObjects = new LinkedList<>();
            String[] rewardsSplit = rankSplit[1].split("/");
            for (String s : rewardsSplit) {
                int id = Integer.parseInt(s.split(",")[0]);
                int amount = Integer.parseInt(s.split(",")[1]);
                rewardObjects.add(new RewardObject(id, amount));
            }
            WEEKLYRANK_RANK_REWARD_CLAN.put(rank, new RankRewardObject(rank, rewardObjects));
        }

        String[] playerRewards = weeklyRank.getProperty("PlayerRewardTopRewards", "1-57,5000/57,4000/57,3000;2-57,5000/57,4000/57,3000;").split(";");

        for (String playerReward : playerRewards) {
            String[] rankSplit = playerReward.split("-");
            int rank = Integer.parseInt(rankSplit[0]);
            List<RewardObject> rewardObjects = new LinkedList<>();
            String[] rewardsSplit = rankSplit[1].split("/");
            for (String s : rewardsSplit) {
                int id = Integer.parseInt(s.split(",")[0]);
                int amount = Integer.parseInt(s.split(",")[1]);
                rewardObjects.add(new RewardObject(id, amount));
            }
            WEEKLYRANK_RANK_REWARD_PLAYER.put(rank, new RankRewardObject(rank, rewardObjects));
        }


        String[] player2Rewards = weeklyRank.getProperty("PlayerAssistRewardTopRewards", "1-57,5000/57,4000/57,3000;2-57,5000/57,4000/57,3000;").split(";");

        for (String playerReward : playerRewards) {
            String[] rankSplit = playerReward.split("-");
            int rank = Integer.parseInt(rankSplit[0]);
            List<RewardObject> rewardObjects = new LinkedList<>();
            String[] rewardsSplit = rankSplit[1].split("/");
            for (String s : rewardsSplit) {
                int id = Integer.parseInt(s.split(",")[0]);
                int amount = Integer.parseInt(s.split(",")[1]);
                rewardObjects.add(new RewardObject(id, amount));
            }
            WEEKLYRANK_RANK_REWARD_PLAYER_ASSIST.put(rank, new RankRewardObject(rank, rewardObjects));
        }

    }
    public static List<Integer> BLOCK_SKILL_INVUL_IDS_ON_TWFLAG_OR_SIEGE_CAST;

    private GabConfig() {
        load();
    }

    public static <T> List<T> splitParseToList(String input, String delimiter, Function<String, T> parser) {
        String[] segments = input.split(delimiter);
        List<T> result = new LinkedList<>();
        for (String segment : segments) {
            String unparsed = segment.trim().replace(" ","");
            T value = parser.apply(unparsed);
            result.add(value);
        }
        return result;
    }

    public void load() {
        InputStream is = null;
        try {
            L2Properties L2JGabsonSettings = new L2Properties();
            is = new FileInputStream(new File(L2GABSON_CONFIG_FILE));
            L2JGabsonSettings.load(is);
            DEBUG_GABS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("DebugGabs", "False"));
            L2GABSON_SERVER_NAME = L2JGabsonSettings.getProperty("ServerName", "GABSON");

            BLOCK_SKILL_INVUL_IDS_ON_TWFLAG_OR_SIEGE_CAST = new LinkedList<>();
            BLOCK_SKILL_INVUL_IDS_ON_TWFLAG_OR_SIEGE_CAST = splitParseToList(L2JGabsonSettings.getProperty("BLOCK_SKILL_INVUL_IDS_ON_TWFLAG_OR_SIEGE_CAST", "0,0"), ",", Integer::parseInt);

            //Custom Spawn
            CUSTOM_SPAWN_FOR_RACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("CustomSpawnForRace", "False"));
            MAX_PT_CC = Integer.parseInt(L2JGabsonSettings.getProperty("MAX_PT_CC", "4"));
            SPAWN_HUMAN_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnHumanX", "0"));
            SPAWN_HUMAN_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnHumanY", "0"));
            SPAWN_HUMAN_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnHumanZ", "0"));
            SPAWN_ELF_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnElfX", "0"));
            SPAWN_ELF_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnElfY", "0"));
            SPAWN_ELF_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnElfZ", "0"));
            SPAWN_DARKELF_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDarkElfX", "0"));
            SPAWN_DARKELF_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDarkElfY", "0"));
            SPAWN_DARKELF_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDarkElfZ", "0"));
            SPAWN_ORC_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnOrcX", "0"));
            SPAWN_ORC_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnOrcY", "0"));
            SPAWN_ORC_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnOrcZ", "0"));
            SPAWN_DWARF_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDwarfX", "0"));
            SPAWN_DWARF_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDwarfY", "0"));
            SPAWN_DWARF_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnDwarfZ", "0"));
            SPAWN_KAMAEL_X = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnKamaelX", "0"));
            SPAWN_KAMAEL_Y = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnKamaelY", "0"));
            SPAWN_KAMAEL_Z = Integer.parseInt(L2JGabsonSettings.getProperty("SpawnKamaelZ", "0"));

            CUSTOM_TW_TIME = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnableTWTime", "true"));
            SIEGEDAY_TW = Integer.parseInt(L2JGabsonSettings.getProperty("DayOfTW", "7"));
            NEXT_TIME_TW = Integer.parseInt(L2JGabsonSettings.getProperty("NextTWTime", "7"));
            HOUR_TW = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfTW", "7"));
            MINUTE_TW = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfTW", "7"));

            ALLOW_ELEMENT_PVP = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AltAllowElementPVPItem", "0"));
            ALLOW_ELEMENT_HERO = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AltAllowElementHEROItem", "0"));
            ALLOW_ENCHANT_HERO = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AltAllowEnchantHEROItem", "0"));

            ALLOW_HEAVY_USE_LIGHT = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowHeavyUseLight", "False"));
            NOTALLOWCLASSE = L2JGabsonSettings.getProperty("NotAllowedUseLight", "");
            NOTALLOWEDUSELIGHT = new FastList<Integer>();
            for (String classId : NOTALLOWCLASSE.split(",")) {
                NOTALLOWEDUSELIGHT.add(Integer.parseInt(classId));
            }
            ALLOW_LIGHT_USE_HEAVY = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowLightUseHeavy", "False"));
            NOTALLOWCLASS = L2JGabsonSettings.getProperty("NotAllowedUseHeavy", "");
            NOTALLOWEDUSEHEAVY = new FastList<Integer>();
            for (String classId : NOTALLOWCLASS.split(",")) {
                NOTALLOWEDUSEHEAVY.add(Integer.parseInt(classId));
            }

            /** Siege day of each castle */
            CUSTOM_SIEGE_TIME = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnableSiegeTime", "true"));

            // Gludio
            SIEGEDAYCASTLEGludio = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeGludio", "7"));
            // Dion
            SIEGEDAYCASTLEDion = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeDion", "7"));
            // Giran
            SIEGEDAYCASTLEGiran = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeGiran", "7"));
            // Oren
            SIEGEDAYCASTLEOren = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeOren", "7"));
            // Aden
            SIEGEDAYCASTLEAden = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeAden", "1"));
            // Innadril/Heine
            SIEGEDAYCASTLEInnadril = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeInnadril", "1"));
            // Goddard
            SIEGEDAYCASTLEGoddard = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeGoddard", "1"));
            // Rune
            SIEGEDAYCASTLERune = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeRune", "1"));
            // Schuttgart
            SIEGEDAYCASTLESchuttgart = Integer.parseInt(L2JGabsonSettings.getProperty("SiegeSchuttgart", "1"));
            /** Next siege time config (Retail 2)*/
            NEXT_SIEGE_TIME = Integer.parseInt(L2JGabsonSettings.getProperty("NextSiegeTime", "2"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_DION = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeDion", "18"));
            MINUTE_OF_SIEGE_DION = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeDion", "0"));
            SECOND_OF_SIEGE_DION = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeDion", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_GIRAN = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeGiran", "18"));
            MINUTE_OF_SIEGE_GIRAN = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeGiran", "0"));
            SECOND_OF_SIEGE_GIRAN = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeGiran", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_OREN = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeOren", "18"));
            MINUTE_OF_SIEGE_OREN = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeOren", "0"));
            SECOND_OF_SIEGE_OREN = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeOren", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_ADEN = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeAden", "18"));
            MINUTE_OF_SIEGE_ADEN = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeAden", "0"));
            SECOND_OF_SIEGE_ADEN = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeAden", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_INNADRIL = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeInnadril", "18"));
            MINUTE_OF_SIEGE_INNADRIL = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeInnadril", "0"));
            SECOND_OF_SIEGE_INNADRIL = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeInnadril", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_GODDARD = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeGoddard", "18"));
            MINUTE_OF_SIEGE_GODDARD = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeGoddard", "0"));
            SECOND_OF_SIEGE_GODDARD = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeGoddard", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_RUNE = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeRune", "18"));
            MINUTE_OF_SIEGE_RUNE = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeRune", "0"));
            SECOND_OF_SIEGE_RUNE = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeRune", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_SCHUT = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeSchut", "18"));
            MINUTE_OF_SIEGE_SCHUT = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeSchut", "0"));
            SECOND_OF_SIEGE_SCHUT = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeSchut", "0"));
            /** Hour of the siege will start*/
            HOUR_OF_SIEGE_GLUDIO = Integer.parseInt(L2JGabsonSettings.getProperty("HourOfSiegeGludio", "18"));
            MINUTE_OF_SIEGE_GLUDIO = Integer.parseInt(L2JGabsonSettings.getProperty("MinuteOfSiegeGludio", "0"));
            SECOND_OF_SIEGE_GLUDIO = Integer.parseInt(L2JGabsonSettings.getProperty("SecondOfSiegeGludio", "0"));


            SHOW_WELCOME_PM = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ShowWelcomePM", "False"));
            PM_FROM = L2JGabsonSettings.getProperty("PMFrom", "Server");
            PM_TEXT = L2JGabsonSettings.getProperty("PMText", "Welcome to our server").split(";");

            SHOW_WELCOME_PMENG = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ShowWelcomePMeng", "False"));
            PM_FROMENG = L2JGabsonSettings.getProperty("PMFromeng", "Server");
            PM_TEXTENG = L2JGabsonSettings.getProperty("PMTexteng", "Welcome to our server").split(";");

            ENABLE_PVP_COUNT_SIEGETW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowPvPCountSiegeAndTW", "False"));
            ENABLE_ASSIST_REWARD = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnableAssistReward", "False"));
            EVERYONE_ASSIST_REWARD = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllPartyMembersGetReward", "False"));
            CLASSID_ASSIST_REWARD = L2JGabsonSettings.getProperty("ClassIdToAssistReward", "90,91,97,98,99,100,105,106,107,112,115,116,136").split(",");
            FAME_KILL = Integer.parseInt(L2JGabsonSettings.getProperty("FamePerKill", "0"));
            PVP_COUNT_SIEGETW_MULTIPLYER = Integer.parseInt(L2JGabsonSettings.getProperty("RewardMultiplier", "0"));
            TRADEABLE_AUGMENTATION = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnableAugmentTrade", "0"));


            String[] tempenchantIds = L2JGabsonSettings.getProperty("TempEnchant", "0-20-1").split(",");
            TEMP_ENCHANT = new HashMap<>();
            if (tempenchantIds[0] != null && tempenchantIds.length > 0) {
                for (String tempenchantId : tempenchantIds) {
                    tempenchantId = tempenchantId.trim().replace(" ", "");
                    int id = Integer.parseInt(tempenchantId.split("-")[0]);
                    int value = Integer.parseInt(tempenchantId.split("-")[1]);
                    int time = Integer.parseInt(tempenchantId.split("-")[2]);
                    TEMP_ENCHANT.put(id, new TempEnchant(id, value, time));
                }
            }

            CHAT_SHOUT = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ChatShout", "False"));
            PVPS_TO_USE_CHAT_SHOUT = Integer.parseInt(L2JGabsonSettings.getProperty("PvpsToUseChatShout", "30"));
            CHAT_TRADE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ChatTrade", "False"));
            PVPS_TO_USE_CHAT_TRADE = Integer.parseInt(L2JGabsonSettings.getProperty("PvpsToUseChatTrade", "30"));

            ELEMENTAL_CUSTOM_LEVEL_ENABLE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ElementalCustomLevelEnable", "false"));
            ELEMENTAL_LEVEL_WEAPON = Integer.parseInt(L2JGabsonSettings.getProperty("ElementalLevelWeapon", "14"));
            ELEMENTAL_LEVEL_ARMOR = Integer.parseInt(L2JGabsonSettings.getProperty("ElementalLevelArmor", "14"));
            ELEMENTAL_WEAPON_AMOUNT = Integer.parseInt(L2JGabsonSettings.getProperty("FirstElementWeapon", "20"));
            ELEMENTAL_ARMOR_AMOUNT = Integer.parseInt(L2JGabsonSettings.getProperty("FirstElementArmor", "6"));

            HERO_SKILL_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("HeroSkillInSub", "False"));
            ALLOW_ELF_DELF_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowElfGetDarkElfSub", "False"));
            ALLOW_DELF_ELF_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowDarkElfGetDarkSub", "False"));
            ALLOW_DAG_DAG_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowDaggersGetDaggersSub", "False"));
            ALLOW_TANK_TANK_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowTanksGetTanksSub", "False"));
            ALLOW_ARC_ARC_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowArcherGetArcherSub", "False"));
            ALLOW_SUM_SUM_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowSummGetSummSub", "False"));
            ALLOW_MAG_MAG_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowMageGetMageSub", "False"));
            ALLOW_DOMINATOR_SUBS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowDominatorSub", "False"));

            CLASS_HEAVY_NERF_SKILL = Integer.parseInt(L2JGabsonSettings.getProperty("HeavyNerfSkill", "98765"));
            BOW_USAGE_NERF_SKILL = Integer.parseInt(L2JGabsonSettings.getProperty("BOW_USAGE_NERF_SKILL", "98765"));
            CLASS_ID_NERF_HEAVY = new LinkedList<>();
            for (String s : L2JGabsonSettings.getProperty("ClassIdsToNerfWhenUsingHeavy", "0,1,2").trim().split(",")) {
                CLASS_ID_NERF_HEAVY.add(Integer.parseInt(s));
            }
            CLASS_ID_NERF_BOW_USAGE = new LinkedList<>();
            for (String s : L2JGabsonSettings.getProperty("CLASS_ID_NERF_BOW_USAGE", "0,1,2").trim().split(",")) {
                CLASS_ID_NERF_BOW_USAGE.add(Integer.parseInt(s));
            }

            ALLOW_FLAG_ON_RAID_AND_MINION = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowFlagOnBossAndRaidFighters", "False"));

            BUFFS_COME_BACK_AFTER_CANCEL = Boolean.parseBoolean(L2JGabsonSettings.getProperty("BuffsComeBackAfterCancel", "False"));
            BUFFS_BACK_IN_S = Integer.parseInt(L2JGabsonSettings.getProperty("BuffsBackInSeconds", "15"));
            ALLOW_RETURN_IN_OLY = Boolean.parseBoolean(L2JGabsonSettings.getProperty("BlockBuffReturnInOly", "False"));
            propertySplit = L2JGabsonSettings.getProperty("StealBuffIdIgnoreList", "1").split(",");
            BUFF_ID_IGNORE_LIST = new int[propertySplit.length];
            for (int i = 0; i < propertySplit.length; i++) {
                BUFF_ID_IGNORE_LIST[i] = Integer.parseInt(propertySplit[i]);
            }
            SKILL_ID_BLOCK_CANCEL = new ArrayList<>();

            String[] skillBlockCancel = L2JGabsonSettings.getProperty("SkillIdIgnoreReturnCancel", "279,6090").split(",");
            if (skillBlockCancel.length > 0) {
                for (String skill : skillBlockCancel) {
                    try {
                        SKILL_ID_BLOCK_CANCEL.add(Integer.parseInt(skill));
                    } catch (Exception e) {
                        //
                    }
                }

            }

            SKILL_BALANCE_FIX_CHANCE = new HashMap<>();
            String[] skilLBalanceLine = L2JGabsonSettings.getProperty("SkillBalanceFixRate", "1169,10;101,20").trim().split(";");
            if (skilLBalanceLine.length > 0) {
                for (String skill : skilLBalanceLine) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_CHANCE.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            SKILL_BALANCE_FIX_TIMER = new HashMap<>();
            String[] skillBalanceFixTimer = L2JGabsonSettings.getProperty("SkillBalanceFixAbnormalTime", "1169,10;101,20").trim().split(";");
            if (skillBalanceFixTimer.length > 0) {
                for (String skill : skillBalanceFixTimer) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_TIMER.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            SKILL_BALANCE_FIX_CHANCE_OLY = new HashMap<>();
            String[] skilLBalanceLineOly = L2JGabsonSettings.getProperty("SkillBalanceFixRateOly", "1169,10;101,20").trim().split(";");
            if (skilLBalanceLineOly.length > 0) {
                for (String skill : skilLBalanceLineOly) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_CHANCE_OLY.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            SKILL_BALANCE_FIX_TIMER_OLY = new HashMap<>();
            String[] skillBalanceFixTimerOly = L2JGabsonSettings.getProperty("SkillBalanceFixAbnormalTimeOly", "1169,10;101,20").trim().split(";");
            if (skillBalanceFixTimerOly.length > 0) {
                for (String skill : skillBalanceFixTimerOly) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_TIMER_OLY.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            SKILL_BALANCE_FIX_CHANCE_OLY_ADD = new HashMap<>();
            String[] skilLBalanceLineOlyAdd = L2JGabsonSettings.getProperty("SkillBalanceFixRateOlyAdd", "1169,10;101,20").trim().split(";");
            if (skilLBalanceLineOlyAdd.length > 0) {
                for (String skill : skilLBalanceLineOlyAdd) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_CHANCE_OLY_ADD.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            SKILL_BALANCE_FIX_CHANCE_ADD = new HashMap<>();
            String[] skilLBalanceLineAdd = L2JGabsonSettings.getProperty("SkillBalanceFixRateAdd", "1169,10;101,20").trim().split(";");
            if (skilLBalanceLineAdd.length > 0) {
                for (String skill : skilLBalanceLineAdd) {
                    try {
                        int skillId = Integer.parseInt(skill.split(",")[0]);
                        int chance = Integer.parseInt(skill.split(",")[1]);
                        SKILL_BALANCE_FIX_CHANCE_ADD.put(skillId, chance);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            NON_REG_ENTER_CASTLE_SIEGE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("NonRegisterEnterCastleSiegeZone", "False"));
            ALLOW_DUAL_BOX_SIEGE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowDualBoxSiegeCastle", "False"));
            PLAYERS_CAN_HEAL_RB = Boolean.parseBoolean(L2JGabsonSettings.getProperty("PlayersCanHealRb", "True"));

            ALLOW_CREATE_SUBPLEDGE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowCreateRoyalandKnight", "False"));
            MAX_PLAYER_MAIN_CLAN = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerMainClan", "40"));
            MAX_PLAYER_ROYAL = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerRoyal", "20"));
            MAX_PLAYER_ROYAL_ABOVE11 = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerRoyalAbove11", "30"));
            MAX_PLAYER_KNIGHT = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerKnight", "10"));
            MAX_PLAYER_KNIGHT_ABOVE9 = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerKnightAbove9", "25"));
            MAX_PLAYER_ACADEMY = Integer.parseInt(L2JGabsonSettings.getProperty("MaxPlayerAcademy", "20"));

            ALLOW_COMMUNITY_CERTIFIC = Boolean.parseBoolean(L2JGabsonSettings.getProperty("CustomCertification", "false"));
            RESPAWN_TWWARD_ONLEAVE_SIEGEZONE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("RespawnTwWardOnLeaveSiegeZone", "False"));

            propertySplit = L2JGabsonSettings.getProperty("Heallers", "1").split(",");
            HEALLERS = new int[propertySplit.length];
            for (int i = 0; i < propertySplit.length; i++) {
                HEALLERS[i] = Integer.parseInt(propertySplit[i]);
            }

            propertySplit = L2JGabsonSettings.getProperty("Tanks", "1").split(",");
            TANKS = new int[propertySplit.length];
            for (int i = 0; i < propertySplit.length; i++) {
                TANKS[i] = Integer.parseInt(propertySplit[i]);
            }
            MAX_HEALERS = Integer.parseInt(L2JGabsonSettings.getProperty("MaxHealersParty", "2"));
            MAX_TANKS = Integer.parseInt(L2JGabsonSettings.getProperty("MaxTanksParty", "2"));
            MAX_DOMINATORS = Integer.parseInt(L2JGabsonSettings.getProperty("MaxDominatorsParty", "1"));
            ALLOW_PARTY_LIMITATIONS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowPartyLimitations", "False"));
            DROP_PROTECTION = Integer.parseInt(L2JGabsonSettings.getProperty("DropProtection", "15000"));
            ALLOW_IKD_CA = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowInvKickDissmClanAlly", "False"));
            OLY_VISUAL_RESTRICTION = Boolean.parseBoolean(L2JGabsonSettings.getProperty("ChangeVisualPlayerOly", "False"));

            CUSTOM_ENCHANT_COLOR_COIN_ID = Integer.parseInt(L2JGabsonSettings.getProperty("CustomEnchantcolorCoinId", "4037"));
            CUSTOM_ENCHANT_COLOR_COIN_AMOUNT = Integer.parseInt(L2JGabsonSettings.getProperty("CustomEnchantcolorCoinAmount", "5"));
            CUSTOM_AGATHION_COIN_ID = Integer.parseInt(L2JGabsonSettings.getProperty("CustomAgathionCoinId", "4037"));
            CUSTOM_AGATHION_COLOR_COIN_AMOUNT = Integer.parseInt(L2JGabsonSettings.getProperty("CustomAgathionCoinAmount", "5"));

            GABRIEL_BALANCER = Boolean.parseBoolean(L2JGabsonSettings.getProperty("GabrielBalancer", "False"));

            AUTO_ENCHANT_SKILLS_3RD_JOB = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowAutoEnchantSkillToMaxOn3Rd", "True"));
            AUTO_SHORTCUT_SKILLS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowSkill3rdJobAutoShortcut", "True"));
            ALLOW_GIVE_CLASS_CLOAK = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowGiveClassCloakOn3RdJob", "True"));


            ALLOW_ALL_SETS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowAllSets", "True"));

            COMMUNITY_DONATE_HERO_ALLOW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowHeroSell", "false"));
            COMMUNITY_DONATE_HERO_NONPEACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowHeroNonPeace", "false"));
            COMMUNITY_DONATE_HERO_PRICE = Integer.parseInt(L2JGabsonSettings.getProperty("HeroPrice", "57"));
            COMMUNITY_DONATE_HERO_ID = Integer.parseInt(L2JGabsonSettings.getProperty("HeroCoin", "57"));

            COMMUNITY_DONATE_COLOR_ALLOW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowCOLORSell", "false"));
            COMMUNITY_DONATE_COLOR_NONPEACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowCOLORNonPeace", "false"));
            COMMUNITY_DONATE_COLOR_PRICE = new LinkedHashMap<>();
            String[] parserrr = L2JGabsonSettings.getProperty("COLORCOLORS", "7FFF00,200;81F7f3,200;FFD700,200;00ffff,200;FF8C00,200;8A2BE2,200;008000,200;9f3754,200;2e64fe,200;9c046c,200;7e0000,200;696969,200;0000ff,200;011aff,200;2b1422,200;0e0e0e,200").split(";");
            for (String s : parserrr) {
                String colors = s.split(",")[0];
                int count = Integer.parseInt(s.split(",")[1]);
                COMMUNITY_DONATE_COLOR_PRICE.put(colors, count);
            }
            COMMUNITY_DONATE_COLOR_ID = Integer.parseInt(L2JGabsonSettings.getProperty("COLORCoin", "57"));

            PREMIUM_PLAYER_EXTRA_ENCHANT_CHANCE = Integer.parseInt(L2JGabsonSettings.getProperty("PremiumEnchantExtraChance", "10"));

            COMMUNITY_DONATE_CLANVIP_ALLOW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowHeroSell", "false"));
            COMMUNITY_DONATE_CLANVIP_NONPEACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowHeroNonPeace", "false"));
            COMMUNITY_DONATE_CLANVIP_PRICE = Integer.parseInt(L2JGabsonSettings.getProperty("HeroPrice", "57"));
            COMMUNITY_DONATE_CLANVIP_ID = Integer.parseInt(L2JGabsonSettings.getProperty("HeroCoin", "57"));

            COMMUNITY_DONATE_FULLSKILL_ALLOW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowFullSkillSell", "false"));
            COMMUNITY_DONATE_FULLSKILL_NONPEACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowFullSkillNonPeace", "false"));
            COMMUNITY_DONATE_FULLSKILL_PRICE = Integer.parseInt(L2JGabsonSettings.getProperty("FullSkillPrice", "57"));
            COMMUNITY_DONATE_FULLSKILL_ID = Integer.parseInt(L2JGabsonSettings.getProperty("FullSkillCoin", "57"));
            MAX_WARD_CLAN = Integer.parseInt(L2JGabsonSettings.getProperty("MaxTerritoryWardPerClan", "57"));
            ONE_WARD_AT_TIME_CLAN = Boolean.parseBoolean(L2JGabsonSettings.getProperty("OnlyOneWardAtTheTime", "False"));
            DROP_ON_MAX_WARDS = Boolean.parseBoolean(L2JGabsonSettings.getProperty("DropWardIfStartSummonWithMaxWards", "False"));

            COMMUNITY_DONATE_CLASSCHANGE_ALLOW = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowClassChangeSell", "false"));
            COMMUNITY_DONATE_CLASSCHANGE_NONPEACE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowClassChangeNonPeace", "false"));
            COMMUNITY_DONATE_CLASSCHANGE_PRICE = Integer.parseInt(L2JGabsonSettings.getProperty("ClassChangePrice", "57"));
            COMMUNITY_DONATE_CLASSCHANGE_ID = Integer.parseInt(L2JGabsonSettings.getProperty("ClassChangeCoin", "57"));

            ENABLE_AUCTION_SYSTEM = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnableAuctionSystem", "true"));
            AUCTION_FEE = Integer.parseInt(L2JGabsonSettings.getProperty("AuctionFee", "10000"));
            AUCTION_INACTIVITY_DAYS_TO_DELETE = Integer.parseInt(L2JGabsonSettings.getProperty("AuctionInactivityDaysToDelete", "7"));
            ALLOW_AUCTION_OUTSIDE_TOWN = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AuctionOutsideTown", "false"));
            SECONDS_BETWEEN_ADDING_AUCTIONS = Integer.parseInt(L2JGabsonSettings.getProperty("AuctionAddDelay", "30"));
            AUCTION_PRIVATE_STORE_AUTO_ADDED = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AuctionPrivateStoreAutoAdded", "true"));


            ALLOW_DROP_CALCULATOR = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowDropCalculator", "true"));
            String[] parser = L2JGabsonSettings.getProperty("DropCalculatorDisabledTeleport", "25603, 25544, 18282, 18283, 18284, 18285, 18286, 20629, 21801, 21802, 21803, 21804, 21805, 21806, 21807, 21808, 21809, 21810, 21811, 21812, 21813, 21814, 21815, 21816, 21817, 21818, 21819, 21820, 21821, 21822, 22423, 22431, 22854, 22856, 22855, 22857, 25725, 25726, 25727").split(",");
            DROP_CALCULATOR_DISABLED_TELEPORT = new int[parser.length];
            for (int i = 0; i < parser.length; i++) {
                if (parser[i].substring(0, 1).equals(" ")) {
                    parser[i] = parser[i].substring(1);
                }
                DROP_CALCULATOR_DISABLED_TELEPORT[i] = Integer.parseInt(parser[i]);
            }
            ALLOW_CLASS_MASTERSCB = L2JGabsonSettings.getProperty("AllowClassMastersCB", "0");
            if ((ALLOW_CLASS_MASTERSCB.length() != 0) && !ALLOW_CLASS_MASTERSCB.equals("0")) {
                for (final String id : ALLOW_CLASS_MASTERSCB.split(",")) {
                    ALLOW_CLASS_MASTERS_LISTCB.add(Integer.parseInt(id));
                }
            }
            CLASS_MASTERS_PRICECB = L2JGabsonSettings.getProperty("ClassMastersPriceCB", "0,0,0");
            if (CLASS_MASTERS_PRICECB.length() >= 5) {
                int level = 0;
                for (final String id : CLASS_MASTERS_PRICECB.split(",")) {
                    CLASS_MASTERS_PRICE_LISTCB[level] = Integer.parseInt(id);
                    level++;
                }
            }
            CLASS_MASTERS_PRICE_ITEMCB = Integer.parseInt(L2JGabsonSettings.getProperty("ClassMastersPriceItemCB", "57"));

            PARTY_AREA_NPCS = L2JGabsonSettings.getProperty("NpcIdsEZ", "22328,22329,22327");
            PARTY_AREA_NPCS2 = L2JGabsonSettings.getProperty("NpcIdsEZ2", "22328,22329,22327");
            PARTY_AREA_NPCSMED = L2JGabsonSettings.getProperty("NpcIdsMED", "22328,22329,22327");
            PARTY_AREA_NPCSMED2 = L2JGabsonSettings.getProperty("NpcIdsMED2", "22328,22329,22327");
            PARTY_AREA_NPCSHARD = L2JGabsonSettings.getProperty("NpcIdsHARD", "22328,22329,22327");
            PARTY_AREA_NPCSHARD2 = L2JGabsonSettings.getProperty("NpcIdsHARD2", "22328,22329,22327");
            PARTY_AREA_NPCSVIP = L2JGabsonSettings.getProperty("NpcIdsVIP", "22328,22329,22327");
            PARTY_AREA_LOCS = L2JGabsonSettings.getProperty("NpcLocationsEZ", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCS2 = L2JGabsonSettings.getProperty("NpcLocationsEZ2", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCSMED = L2JGabsonSettings.getProperty("NpcLocationsMED", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCSMED2 = L2JGabsonSettings.getProperty("NpcLocationsMED2", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCSHARD = L2JGabsonSettings.getProperty("NpcLocationsHARD", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCSHARD2 = L2JGabsonSettings.getProperty("NpcLocationsHARD2", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_LOCSVIP = L2JGabsonSettings.getProperty("NpcLocationsVIP", "53278,246188,-6577;53151,246023,-6577;53287,245848,-6577;53440,246002,-6577");
            PARTY_AREA_RESPAWN_DELAY = L2JGabsonSettings.getProperty("RespawnDelayMobs", "60");
            PARTY_AREA_TIME = L2JGabsonSettings.getProperty("TimeOfDay", "18:30,18:40");
            PARTY_AREA_DURATION = L2JGabsonSettings.getProperty("EventDuration", "60");
            BOSSINFOEZ = L2JGabsonSettings.getProperty("BOSSINFOEZ", "0;0,0,0");
            BOSSINFOMED = L2JGabsonSettings.getProperty("BOSSINFOMED", "0;0,0,0");
            BOSSINFOHARD = L2JGabsonSettings.getProperty("BOSSINFOHARD", "0;0,0,0");
            BOSSINFOVIP = L2JGabsonSettings.getProperty("BOSSINFOVIP", "0;0,0,0");

            PARTY_AREA_PLAYER_TELEPORT = L2JGabsonSettings.getProperty("PlayerTeleportLocationEZ", "53278,246188,-6577");
            PARTY_AREA_PLAYER_TELEPORTMED = L2JGabsonSettings.getProperty("PlayerTeleportLocationMED", "53278,246188,-6577");
            PARTY_AREA_PLAYER_TELEPORTHARD = L2JGabsonSettings.getProperty("PlayerTeleportLocationHARD", "53278,246188,-6577");
            PARTY_AREA_PLAYER_TELEPORTVIP = L2JGabsonSettings.getProperty("PlayerTeleportLocationVIP", "53278,246188,-6577");
            PARTY_AREA_INSTANCE_ID = Integer.parseInt(L2JGabsonSettings.getProperty("PartyZoneInstanceId", "80053"));
            PARTY_AREA_EVENT_PVP_INSIDE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("PartyZoneEventRewardPvPPoints", "false"));


            blocked_skills = new LinkedList<>();

            String[] st = L2JGabsonSettings.getProperty("BLOCKED_SKILLS_TOUR_CSKOTH", "1255,3205").split(",");
            if(st.length > 1){
                for (String s : st) {
                    blocked_skills.add(Integer.parseInt(s));
                }
            }

            ExProperties forge = load(L2GABSON_CONFIG_FILE);

            propertySplit = L2JGabsonSettings.getProperty("CancelBuffIdsOnEventEnter", "1").split(",");
            BUFF_ID_DELETE_EVENTS = new LinkedList<>();
            for (int i = 0; i < propertySplit.length; i++) {
                BUFF_ID_DELETE_EVENTS.add(Integer.parseInt(propertySplit[i]));
            }

            BBS_FORGE_ENABLED = forge.getProperty("Allow", false);
            BBS_FORGE_ENCHANT_ITEM = forge.getProperty("Item", 4356);
            BBS_FORGE_FOUNDATION_ITEM = forge.getProperty("FoundationItem", 37000);
            BBS_FORGE_FOUNDATION_PRICE_ARMOR = forge.getProperty("FoundationPriceArmor", new int[]{1, 1, 1, 1, 1, 2, 5, 10});
            BBS_FORGE_FOUNDATION_PRICE_WEAPON = forge.getProperty("FoundationPriceWeapon", new int[]{1, 1, 1, 1, 1, 2, 5, 10});
            BBS_FORGE_FOUNDATION_PRICE_JEWEL = forge.getProperty("FoundationPriceJewel", new int[]{1, 1, 1, 1, 1, 2, 5, 10});
            BBS_FORGE_ENCHANT_MAX = forge.getProperty("MaxEnchant", new int[]{25});
            BBS_FORGE_WEAPON_ENCHANT_LVL = forge.getProperty("WValue", new int[]{5});
            BBS_FORGE_ARMOR_ENCHANT_LVL = forge.getProperty("AValue", new int[]{5});
            BBS_FORGE_JEWELS_ENCHANT_LVL = forge.getProperty("JValue", new int[]{5});
            BBS_FORGE_ENCHANT_PRICE_WEAPON = forge.getProperty("WPrice", new int[]{5});
            BBS_FORGE_ENCHANT_PRICE_ARMOR = forge.getProperty("APrice", new int[]{5});
            BBS_FORGE_ENCHANT_PRICE_JEWELS = forge.getProperty("JPrice", new int[]{5});

            BBS_FORGE_AUGMENT_ITEMS_LIST = forge.getProperty("AugmentItems", new int[]{4037, 4037, 4037, 4037});
            BBS_FORGE_AUGMENT_COUNT_LIST = forge.getProperty("AugmentCount", new long[]{1L, 3L, 6L, 10L});

            BBS_FORGE_ATRIBUTE_LVL_WEAPON = forge.getProperty("AtributeWeaponValue", new int[]{25});
            BBS_FORGE_ATRIBUTE_PRICE_WEAPON = forge.getProperty("PriceForAtributeWeapon", new int[]{25});
            BBS_FORGE_ATRIBUTE_LVL_ARMOR = forge.getProperty("AtributeArmorValue", new int[]{25});
            BBS_FORGE_ATRIBUTE_PRICE_ARMOR = forge.getProperty("PriceForAtributeArmor", new int[]{25});
            BBS_FORGE_ATRIBUTE_PVP = forge.getProperty("AtributePvP", true);
            BBS_FORGE_WEAPON_ATTRIBUTE_MAX = forge.getProperty("MaxWAttribute", 25);
            BBS_FORGE_ARMOR_ATTRIBUTE_MAX = forge.getProperty("MaxAAttribute", 25);

            BBS_FORGE_GRADE_ATTRIBUTE = forge.getProperty("AtributeGrade", "NG:NO;D:NO;C:NO;B:NO;A:ON;S:ON;S80:ON;S84:ON").trim().replaceAll(" ", "").split(";");

            ALLOW_CLAN_CLOAK = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowClanCloaks", "false"));
            CLAN_CLOAK = new HashMap<>();
            propertySplit = L2JGabsonSettings.getProperty("ClanCloaks", "00000,57;00000,57").trim().split(";");
            if (propertySplit.length > 0) {
                for (String s : propertySplit) {
                    if (!s.isEmpty()) {
                        CLAN_CLOAK.put(Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]));
                    }
                }
            }


            _log.info("GabConfig Has been Initialized!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_CONFIG_FILE + " File.");
        }
        try {
            L2Properties koth = new L2Properties();
            is = new FileInputStream(new File(CSKOTH_CONFIG_FILE));
            koth.load(is);

            // King of the Hill Event

            CSKOTH_EVENT_ENABLED = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventEnabled", "false"));
            CSKOTH_EVENT_IN_INSTANCE = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventInInstance", "false"));
            CSKOTH_EVENT_INSTANCE_FILE = koth.getProperty("CastleSiegeEventInstanceFile", "coliseum.xml");
            CSKOTH_EVENT_INTERVAL = koth.getProperty("CastleSiegeEventInterval", "20:00").split(",");
            CSKOTH_EVENT_PARTICIPATION_TIME = Integer.parseInt(koth.getProperty("CastleSiegeEventParticipationTime", "3600"));
            CSKOTH_EVENT_RUNNING_TIME = Integer.parseInt(koth.getProperty("CastleSiegeEventRunningTime", "1800"));
            CSKOTH_EVENT_PARTICIPATION_NPC_ID = Integer.parseInt(koth.getProperty("CastleSiegeEventParticipationNpcId", "0"));
            CSKOTH_REWARD_PLAYER = Boolean.parseBoolean(koth.getProperty("CastleSiegeRewardPlayer", "True"));
            CSKOTH_EVENT_REWARDS = new ArrayList<int[]>();
            CSKOTH_DOORS_IDS_TO_OPEN = new ArrayList<Integer>();
            CSKOTH_DOORS_IDS_TO_CLOSE = new ArrayList<Integer>();
            CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];

            if (CSKOTH_EVENT_PARTICIPATION_NPC_ID == 0) {
                CSKOTH_EVENT_ENABLED = false;
                _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventParticipationNpcId");
            } else {
                String[] propertySplit = koth.getProperty("CastleSiegeEventParticipationNpcCoordinates", "0,0,0").split(",");
                if (propertySplit.length < 3) {
                    CSKOTH_EVENT_ENABLED = false;
                    _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventParticipationNpcCoordinates");
                } else {
                    CSKOTH_EVENT_REWARDS = new ArrayList<int[]>();
                    CSKOTH_DOORS_IDS_TO_OPEN = new ArrayList<Integer>();
                    CSKOTH_DOORS_IDS_TO_CLOSE = new ArrayList<Integer>();
                    CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
                    CSKOTH_EVENT_TEAM_1_COORDINATES = new int[3];
                    CSKOTH_EVENT_TEAM_2_COORDINATES = new int[3];
                    CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                    CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                    CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
                    if (propertySplit.length == 4)
                        CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
                    CSKOTH_EVENT_MIN_PLAYERS_IN_TEAMS = Integer.parseInt(koth.getProperty("CastleSiegeEventMinPlayersInTeams", "1"));
                    CSKOTH_EVENT_MAX_PLAYERS_IN_TEAMS = Integer.parseInt(koth.getProperty("CastleSiegeEventMaxPlayersInTeams", "20"));
                    CSKOTH_EVENT_MIN_LVL = (byte) Integer.parseInt(koth.getProperty("CastleSiegeEventMinPlayerLevel", "1"));
                    CSKOTH_EVENT_MAX_LVL = (byte) Integer.parseInt(koth.getProperty("CastleSiegeEventMaxPlayerLevel", "80"));
                    CSKOTH_EVENT_RESPAWN_TELEPORT_DELAY = Integer.parseInt(koth.getProperty("CastleSiegeEventRespawnTeleportDelay", "20"));
                    CSKOTH_EVENT_START_LEAVE_TELEPORT_DELAY = Integer.parseInt(koth.getProperty("CastleSiegeEventStartLeaveTeleportDelay", "20"));
                    CSKOTH_EVENT_EFFECTS_REMOVAL = Integer.parseInt(koth.getProperty("CastleSiegeEventEffectsRemoval", "0"));
                    CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP = Integer.parseInt(koth.getProperty("CastleSiegeEventMaxParticipantsPerIP", "0"));
                    CSKOTH_EVENT_CRYSTAL_NPC_ID = Integer.parseInt(koth.getProperty("CastleSiegeCrystalNpcId", "0"));

                    propertySplit = koth.getProperty("CastleSiegeEventDoorMansNpcsToSpawn", "0,0,0").split(",");
                    CSKOTH_EVENT_DOORMANS_SPAWN = new LinkedList<>();
                    if(propertySplit.length > 0)
                        for (String s : propertySplit) {
                            CSKOTH_EVENT_DOORMANS_SPAWN.add(Integer.parseInt(s));
                        }


                    propertySplit = koth.getProperty("CastleSiegeEventCrystalCoordinates", "0,0,0").split(",");
                    CSKOTH_EVENT_CRYSTAL_LOC = new int[3];
                    CSKOTH_EVENT_CRYSTAL_LOC[0] = Integer.parseInt(propertySplit[0]);
                    CSKOTH_EVENT_CRYSTAL_LOC[1] = Integer.parseInt(propertySplit[1]);
                    CSKOTH_EVENT_CRYSTAL_LOC[2] = Integer.parseInt(propertySplit[2]);
                    CSKOTH_ALLOW_VOICED_COMMAND = Boolean.parseBoolean(koth.getProperty("CastleSiegeAllowVoicedInfoCommand", "false"));
                    CSKOTH_ALLOW_REGISTER_VOICED_COMMAND = Boolean.parseBoolean(koth.getProperty("CastleSiegeAllowRegisterVoicedCommand", "false"));
                    CSKOTH_EVENT_TEAM_1_NAME = koth.getProperty("CastleSiegeEventTeam1Name", "Team1");
                    propertySplit = koth.getProperty("CastleSiegeEventTeam1Coordinates", "0,0,0").split(",");
                    if (propertySplit.length < 3) {
                        CSKOTH_EVENT_ENABLED = false;
                        _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventTeam1Coordinates");
                    } else {
                        CSKOTH_ARENA_KILL_ENABLE = Boolean.parseBoolean(koth.getProperty("CastleSiegeArenaKillEnable", "False"));
                        CSKOTH_EVENT_REWARDS_KILL = new ArrayList<int[]>();
                        CSKOTH_EVENT_FAMA_KILL = Integer.parseInt(koth.getProperty("CastleSiegeEventFamaKill", "0"));
                        CSKOTH_ARENA_FAME_ENABLE = Boolean.parseBoolean(koth.getProperty("CastleSiegeArenaFameEnable", "False"));
                        CSKOTH_ARENA_REWARDKILL_ENABLE = Boolean.parseBoolean(koth.getProperty("CastleSiegeArenaRewardKillEnable", "False"));

                        CSKOTH_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                        CSKOTH_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                        CSKOTH_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
                        CSKOTH_EVENT_TEAM_2_NAME = koth.getProperty("CastleSiegeEventTeam2Name", "Team2");
                        propertySplit = koth.getProperty("CastleSiegeEventTeam2Coordinates", "0,0,0").split(",");
                        if (propertySplit.length < 3) {
                            CSKOTH_EVENT_ENABLED = false;
                            _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventTeam2Coordinates");
                        } else {
                            CSKOTH_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                            CSKOTH_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                            CSKOTH_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);

                            propertySplit = koth.getProperty("CastleSiegeEventTeamCastleOwnerCoordinates", "0,0,0").split(",");
                            if (propertySplit.length < 3) {
                                CSKOTH_EVENT_ENABLED = false;
                                _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventTeamCastleOwnerCoordinates");
                            } else {
                                CSKOTH_EVENT_TEAM_OWNER_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
                                CSKOTH_EVENT_TEAM_OWNER_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
                                CSKOTH_EVENT_TEAM_OWNER_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
                            }
                            propertySplit = koth.getProperty("CastleSiegeEventParticipationFee", "0,0").split(",");
                            try {
                                CSKOTH_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
                                CSKOTH_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
                            } catch (NumberFormatException nfe) {
                                if (propertySplit.length > 0)
                                    _log.warn("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventParticipationFee");
                            }
                            propertySplit = koth.getProperty("CastleSiegeEventReward", "57,100000").split(";");
                            for (String reward : propertySplit) {
                                String[] rewardSplit = reward.split(",");
                                if (rewardSplit.length != 2)
                                    _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventReward \"", reward, "\""));
                                else {
                                    try {
                                        CSKOTH_EVENT_REWARDS.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
                                    } catch (NumberFormatException nfe) {
                                        if (!reward.isEmpty())
                                            _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventReward \"", reward, "\""));
                                    }
                                }
                            }

//                            CSKOTH_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventTargetTeamMembersAllowed", "true"));
                            CSKOTH_EVENT_SCROLL_ALLOWED = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventScrollsAllowed", "false"));
                            CSKOTH_EVENT_POTIONS_ALLOWED = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventPotionsAllowed", "false"));
                            CSKOTH_EVENT_SUMMON_BY_ITEM_ALLOWED = Boolean.parseBoolean(koth.getProperty("CastleSiegeEventSummonByItemAllowed", "false"));
                            CSKOTH_REWARD_TEAM_TIE = Boolean.parseBoolean(koth.getProperty("CastleSiegeRewardTeamTie", "false"));
                            propertySplit = koth.getProperty("CastleSiegeDoorsToOpen", "").split(";");
                            for (String door : propertySplit) {
                                try {
                                    CSKOTH_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
                                } catch (NumberFormatException nfe) {
                                    if (!door.isEmpty())
                                        _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CSDoorsToOpen \"", door, "\""));
                                }
                            }

                            propertySplit = koth.getProperty("CastleSiegeDoorsToClose", "").split(";");
                            for (String door : propertySplit) {
                                try {
                                    CSKOTH_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
                                } catch (NumberFormatException nfe) {
                                    if (!door.isEmpty())
                                        _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CSDoorsToClose \"", door, "\""));
                                }
                            }

                            propertySplit = koth.getProperty("CastleSiegeEventFighterBuffs", "").split(";");
                            if (!propertySplit[0].isEmpty()) {
                                CSKOTH_EVENT_FIGHTER_BUFFS = new TIntIntHashMap(propertySplit.length);
                                for (String skill : propertySplit) {
                                    String[] skillSplit = skill.split(",");
                                    if (skillSplit.length != 2)
                                        _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventFighterBuffs \"", skill, "\""));
                                    else {
                                        try {
                                            CSKOTH_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                                        } catch (NumberFormatException nfe) {
                                            if (!skill.isEmpty())
                                                _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventFighterBuffs \"", skill, "\""));
                                        }
                                    }
                                }
                            }

                            propertySplit = koth.getProperty("CastleSiegeEventMageBuffs", "").split(";");
                            if (!propertySplit[0].isEmpty()) {
                                CSKOTH_EVENT_MAGE_BUFFS = new TIntIntHashMap(propertySplit.length);
                                for (String skill : propertySplit) {
                                    String[] skillSplit = skill.split(",");
                                    if (skillSplit.length != 2)
                                        _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventMageBuffs \"", skill, "\""));
                                    else {
                                        try {
                                            CSKOTH_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                                        } catch (NumberFormatException nfe) {
                                            if (!skill.isEmpty())
                                                _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventMageBuffs \"", skill, "\""));
                                        }
                                    }
                                }
                            }

                            propertySplit = koth.getProperty("CastleSiegeEventRewardKill", "57,2").split(";");
                            for (String reward : propertySplit) {
                                String[] rewardSplit = reward.split(",");
                                if (rewardSplit.length != 2)
                                    _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventRewardKill \"", reward, "\""));
                                else {
                                    try {
                                        CSKOTH_EVENT_REWARDS_KILL.add(new int[]{Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1])});
                                    } catch (NumberFormatException nfe) {
                                        if (!reward.isEmpty())
                                            _log.warn(StringUtil.concat("CastleSiegeEventEngine[Config.load()]: invalid config property -> CastleSiegeEventRewardKill \"", reward, "\""));
                                    }
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + CSKOTH_CONFIG_FILE + " File.");
        }
        try {
            L2Properties xtremeZone = new L2Properties();
            is = new FileInputStream(new File(EXTREMEZONE_CONFIG_FILE));
            xtremeZone.load(is);
            // Extreme Zone Event
            EXTREME_EVENT_ENABLED = Boolean.parseBoolean(xtremeZone.getProperty("ExtremeEventEnabled", "false"));
            EXTREME_EVENT_PVP_INSIDE = Boolean.parseBoolean(xtremeZone.getProperty("ExtremeEventRewardPvPPoints", "false"));
            EXTREME_EVENT_INTERVAL = xtremeZone.getProperty("ExtremeEventInterval", "20:00").split(",");
            EXTREME_EVENT_RUNNING_TIME = Integer.parseInt(xtremeZone.getProperty("ExtremeEventRunningTime", "120"));
            EXTREME_EVENT_INSTANCE_ID = Integer.parseInt(xtremeZone.getProperty("ExtremeEventInstanceId", "1800"));
            EXTREME_EVENT_NPC_ID = Integer.parseInt(xtremeZone.getProperty("ExtremeEventNpcId", "12006"));
            EXTREME_EVENT_MINIMUM_INSIDE_SECONDS = Integer.parseInt(xtremeZone.getProperty("ParticipationMinimunSeconds", "30"));
            EXTREME_EVENT_MINUTESTORESPAWN = Integer.parseInt(xtremeZone.getProperty("TimeToBossResspawn", "30"));
            EXTREME_EVENT_ADMIN_OBJ_ID = Integer.parseInt(xtremeZone.getProperty("AdminObjId", "30"));
            EXTREME_EVENT_ITEM_COUNT_DIVIDER = Integer.parseInt(xtremeZone.getProperty("ExtremeItemCountDivider", "10"));
            EXTREME_EVENT_ITEM_MAX_COUNT = Integer.parseInt(xtremeZone.getProperty("ExtremeItemMaxCount", "250"));
            EXTREME_EVENT_PARTICIPATION_REWARD = xtremeZone.getProperty("ParticipationReward", "30003,5;23001,10").split(";");
            EXTREME_EVENT_CAPTURED_REWARD = xtremeZone.getProperty("CapturedReward", "30003,5;23001,10").split(";");
            EXTREME_EVENT_RADIUS_CHECK = Boolean.parseBoolean(xtremeZone.getProperty("AllowRadiusCheck", "false"));
            EXTREME_EVENT_RADIUS_VALUE = Integer.parseInt(xtremeZone.getProperty("RadiusCheckValue", "200"));
            EXTREME_EVENT_CLOSE_ALL = Boolean.parseBoolean(xtremeZone.getProperty("CloseAllEventsIfEXRunning", "false"));
            EXTREME_EVENT_DRAW_LINES = Boolean.parseBoolean(xtremeZone.getProperty("DrawAreaLines", "false"));
            EXTREME_EVENT_DIAS_RUN = xtremeZone.getProperty("DiasRun", "2,3");

            EX_TEXT = xtremeZone.getProperty("EXChatToSend", "Extreme Zone has been started!;Boss: %boss%");
            EX_TEXT_FROM = xtremeZone.getProperty("EXChatFrom", "Extreme Zone");
            EX_TEXT_KIND = Integer.parseInt(xtremeZone.getProperty("EXTypeOfMessage", "20"));
            EX_HOUR_RS = xtremeZone.getProperty("EXChatHourStart", "Hour(s) until start!");
            EX_MIN_RS = xtremeZone.getProperty("EXChatMinStart", "minute(s) until start!");
            EX_SEC_RS = xtremeZone.getProperty("EXChatSecStart", "second(s) until start!");
            EX_HOUR_RE = xtremeZone.getProperty("EXChatHourEnd", "Hour(s) until finish!");
            EX_MIN_RE = xtremeZone.getProperty("EXChatMinEnd", "minute(s) until finish!");
            EX_SEC_RE = xtremeZone.getProperty("EXChatSecEnd", "second(s) until finish!");
            EX_EVENT_FINISH = xtremeZone.getProperty("EXEventFinish", "Extreme Zone Event has now ended! See the Community Board for the next raid!");
            EX_EVENT_CAPTURE = xtremeZone.getProperty("EXEventCapture", "%boss% has been defeated! Go to the area and capture for rewards!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + EXTREMEZONE_CONFIG_FILE + " File.");
        }
        try {
            L2Properties chZone = new L2Properties();
            is = new FileInputStream(new File(CHALLENGERZONE_CONFIG_FILE));
            chZone.load(is);
            // Challenger Zone Event
            CHALLENGER_EVENT_ENABLED = Boolean.parseBoolean(chZone.getProperty("ChallengerEventEnabled", "false"));
            CHALLENGER_EVENT_PVP_INSIDE = Boolean.parseBoolean(chZone.getProperty("ChallengerEventRewardPvPPoints", "false"));
            CHALLENGER_EVENT_INTERVAL = chZone.getProperty("ChallengerEventInterval", "20:00").split(",");
            CHALLENGER_EVENT_RUNNING_TIME = Integer.parseInt(chZone.getProperty("ChallengerEventRunningTime", "120"));
            CHALLENGER_EVENT_INSTANCE_ID = Integer.parseInt(chZone.getProperty("ChallengerEventInstanceId", "1800"));
            CHALLENGER_EVENT_NPC_ID = Integer.parseInt(chZone.getProperty("ChallengerEventNpcId", "12006"));
            CHALLENGER_EVENT_MINIMUM_INSIDE_SECONDS = Integer.parseInt(chZone.getProperty("ParticipationMinimunSeconds", "30"));
            CHALLENGER_EVENT_MINUTESTORESPAWN = Integer.parseInt(chZone.getProperty("TimeToBossResspawn", "30"));
            CHALLENGER_EVENT_ADMIN_OBJ_ID = Integer.parseInt(chZone.getProperty("AdminObjId", "30"));
            CHALLENGER_EVENT_ITEM_COUNT_DIVIDER = Integer.parseInt(chZone.getProperty("ChallengerItemCountDivider", "10"));
            CHALLENGER_EVENT_ITEM_MAX_COUNT = Integer.parseInt(chZone.getProperty("ChallengerItemMaxCount", "250"));
            CHALLENGER_EVENT_PARTICIPATION_REWARD = chZone.getProperty("ParticipationReward", "30003,5;23001,10").split(";");
            CHALLENGER_EVENT_CAPTURED_REWARD = chZone.getProperty("CapturedReward", "30003,5;23001,10").split(";");
            CHALLENGER_EVENT_RADIUS_CHECK = Boolean.parseBoolean(chZone.getProperty("AllowRadiusCheck", "false"));
            CHALLENGER_EVENT_RADIUS_VALUE = Integer.parseInt(chZone.getProperty("RadiusCheckValue", "200"));
            CHALLENGER_EVENT_CLOSE_ALL = Boolean.parseBoolean(chZone.getProperty("CloseAllEventsIfEXRunning", "false"));
            CHALLENGER_EVENT_DRAW_LINES = Boolean.parseBoolean(chZone.getProperty("DrawAreaLines", "false"));
            CHALLENGER_EVENT_DIAS_RUN = chZone.getProperty("DiasRun", "2,3");

            CHALLENGER_TEXT = chZone.getProperty("EXChatToSend", "Challenger Zone has been started!;Boss: %boss%");
            CHALLENGER_TEXT_FROM = chZone.getProperty("EXChatFrom", "Challenger Zone");
            CHALLENGER_TEXT_KIND = Integer.parseInt(chZone.getProperty("EXTypeOfMessage", "20"));
            CHALLENGER_HOUR_RS = chZone.getProperty("EXChatHourStart", "Hour(s) until start!");
            CHALLENGER_MIN_RS = chZone.getProperty("EXChatMinStart", "minute(s) until start!");
            CHALLENGER_SEC_RS = chZone.getProperty("EXChatSecStart", "second(s) until start!");
            CHALLENGER_HOUR_RE = chZone.getProperty("EXChatHourEnd", "Hour(s) until finish!");
            CHALLENGER_MIN_RE = chZone.getProperty("EXChatMinEnd", "minute(s) until finish!");
            CHALLENGER_SEC_RE = chZone.getProperty("EXChatSecEnd", "second(s) until finish!");
            CHALLENGER_EVENT_FINISH = chZone.getProperty("EXEventFinish", "Challenger Zone Event has now ended! See the Community Board for the next raid!");
            CHALLENGER_EVENT_CAPTURE = chZone.getProperty("EXEventCapture", "%boss% has been defeated! Go to the area and capture for rewards!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + CHALLENGERZONE_CONFIG_FILE + " File.");
        }

        try {
            L2Properties epicraid = new L2Properties();
            is = new FileInputStream(new File(EPICRAID_CONFIG_FILE));
            epicraid.load(is);
            // Epic Raid Event
            ER_EVENT_ENABLED = Boolean.parseBoolean(epicraid.getProperty("EREventEnabled", "false"));

            Calendar todayCalender = Calendar.getInstance();
            int todayDay = todayCalender.get(Calendar.DAY_OF_WEEK);
            switch (todayDay) {
                case 1:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalSunday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalSunday: " + epicraid.getProperty("EREventIntervalSunday", "20:00"));
                    break;
                case 2:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalMonday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalMonday: " + epicraid.getProperty("EREventIntervalMonday", "20:00"));
                    break;
                case 3:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalTuesday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalTuesday: " + epicraid.getProperty("EREventIntervalTuesday", "20:00"));
                    break;
                case 4:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalWednesday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalWednesday: " + epicraid.getProperty("EREventIntervalWednesday", "20:00"));
                    break;
                case 5:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalThursday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalThursday: " + epicraid.getProperty("EREventIntervalThursday", "20:00"));
                    break;
                case 6:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalFriday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalFriday: " + epicraid.getProperty("EREventIntervalFriday", "20:00"));
                    break;
                case 7:
                    ER_EVENT_INTERVAL = epicraid.getProperty("EREventIntervalSaturday", "20:00").split(",");
                    System.out.println("CARREGANDO EREventIntervalSaturday: " + epicraid.getProperty("EREventIntervalSaturday", "20:00"));
                    break;
            }



            ER_BOSS_MONDAY = Arrays.stream(epicraid.getProperty("EREventBOSSMonday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_TUESDAY = Arrays.stream(epicraid.getProperty("EREventBOSSTuesday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_WEDNESDAY = Arrays.stream(epicraid.getProperty("EREventBOSSWednesday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_THURSDAY = Arrays.stream(epicraid.getProperty("EREventBOSSThursday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_FRIDAY = Arrays.stream(epicraid.getProperty("EREventBOSSFriday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_SATURDAY = Arrays.stream(epicraid.getProperty("EREventBOSSSaturday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());
            ER_BOSS_SUNDAY = Arrays.stream(epicraid.getProperty("EREventBOSSSunday", "80002").replaceAll("\\s","").split(",")).map(Integer::parseInt).collect(Collectors.toList());


            ER_EVENT_RUNNING_TIME = Integer.parseInt(epicraid.getProperty("EREventRunningTime", "1800"));
            ER_EVENT_INSTANCE_ID = Integer.parseInt(epicraid.getProperty("EREventInstanceId", "1800"));
            ER_EVENT_NPC_ID = Integer.parseInt(epicraid.getProperty("EREventNpcId", "12006"));
            ER_EVENT_MINIMUM_INSIDE_SECONDS = Integer.parseInt(epicraid.getProperty("ParticipationMinimunSeconds", "30"));
            ER_EVENT_ADMIN_OBJ_ID = Integer.parseInt(epicraid.getProperty("AdminObjId", "30"));
            ER_EVENT_ITEM_COUNT_DIVIDER = Integer.parseInt(epicraid.getProperty("ERItemCountDivider", "10"));
            ER_EVENT_ITEM_MAX_COUNT = Integer.parseInt(epicraid.getProperty("ERItemMaxCount", "250"));
            ER_EVENT_PARTICIPATION_REWARD = epicraid.getProperty("ParticipationReward", "30003,5;23001,10").split(";");
            ER_EVENT_RADIUS_CHECK = Boolean.parseBoolean(epicraid.getProperty("AllowRadiusCheck", "false"));
            ER_EVENT_RADIUS_VALUE = Integer.parseInt(epicraid.getProperty("RadiusCheckValue", "200"));
            ER_EVENT_PERCENT_CLOSE = Integer.parseInt(epicraid.getProperty("PercentHpToClose", "10"));
            ER_EVENT_CLOSE_ALL = Boolean.parseBoolean(epicraid.getProperty("ClosePvPInstanceIfERRunning", "false"));
            ER_EVENT_DRAW_LINES = Boolean.parseBoolean(epicraid.getProperty("DrawAreaLines", "false"));
            ER_EVENT_DIAS_RUN = epicraid.getProperty("DiasRun", "2,3");

            ER_RANK_ENABLE = Boolean.parseBoolean(epicraid.getProperty("TopRankRewardEnable", "false"));
            ER_RANK_ONLY_HTML = Boolean.parseBoolean(epicraid.getProperty("TopRankRewardOnlyHtml", "false"));
            propertySplit = epicraid.getProperty("TopRankReward", "1-57,10;57,10;57,10/2-57,9;57,9;57,9/3-57,8;57,8;57,8/4-57,7;57,7;57,7/5-57,6;57,6;57,6/6-57,5;57,5;57,5/7-57,4;57,4;57,4/8-57,2;57,2;57,2/9-57,1;57,1;57,1").trim().split("/");
            for (String s : propertySplit) {
                int position = Integer.parseInt(s.split("-")[0]);
                String[] rewards = s.split("-")[1].split(";");
                List<RewardRank> list = new LinkedList<>();
                for (String reward : rewards) {
                    int itemId = Integer.parseInt(reward.split(",")[0]);
                    int count = Integer.parseInt(reward.split(",")[1]);
                    list.add(new RewardRank(itemId, count));
                }
                ER_RANK_REWARD.put(position, list);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + EPICRAID_CONFIG_FILE + " File.");
        }

        SiegeRankConfig.getInstance().load();
        loadWeeklyRank();
    }

    protected static GabConfig instance;

    public static GabConfig getInstance() {
        if (instance == null)
            instance = new GabConfig();
        return instance;
    }

    public static ExProperties load(String filename) {
        return load(new File(filename));
    }

    public static ExProperties load(File file) {
        ExProperties result = new ExProperties();

        try {
            result.load(file);
        } catch (IOException e) {
            _log.warn("Error loading config : " + file.getName() + "!");
        }

        return result;
    }

}
