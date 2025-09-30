package gabriel.config;

import gr.sr.utils.L2Properties;
import l2r.gameserver.model.holders.ItemHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class AutoFarmConfig {
    private static final Logger _log = LoggerFactory.getLogger(AutoFarmConfig.class);

    public static final String L2GABSON_CONFIG_FILE = "./config/gabriel/AutoFarm.ini";
    private static String[] propertySplit;

    public static boolean AUTO_FARM_ENABLE;
    public static boolean AUTO_FARM_PREMIUM_NORMAL_AFFECT;
    public static boolean AUTO_FARM_ATT_PL;
    public static boolean AUTO_FARM_ATT_PL_PRE;
    public static boolean AUTO_FARM_BUY_ONLY_IF_PREMIUM_ALREADY;
    public static int AUTO_FARM_SKILLS_AMOUNT;
    public static int AUTO_FARM_NORMAL_MINUTES;
    public static int AUTO_FARM_NORMAL_BOXES;
    public static int AUTO_FARM_PREMIUM_MINUTES;
    public static int AUTO_FARM_PREMIUM_BOXES;
    public static List<Integer> AUTO_FARM_MONSTER_ID;
    public static List<String> AUTO_FARM_MONSTER_NAME;

    public static boolean AUTO_FARM_ATTACK_RAID_GRAND;
    public static boolean AUTO_FARM_ATTACK_RAID_GRAND_PREMIUM;
    public static final List<Integer> AUTO_FARM_BLOCKED_IDS = new LinkedList<>();
    public static boolean AUTO_FARM_PREMIUM_ONLY;

    public static final Map<Integer, ItemHolder> AUTO_FARM_SLOTS_PRICES = new LinkedHashMap<>();
    public static final Map<Integer, ItemHolder> AUTO_FARM_MINUTES_PRICES = new LinkedHashMap<>();
    public static final Map<Integer, ItemHolder> AUTO_FARM_PREMIUM_DAY_PRICES = new LinkedHashMap<>();
    public static final Map<Integer, ItemHolder> AUTO_FARM_PREMIUM_WEEK_PRICES = new LinkedHashMap<>();
    public static final Map<Integer, ItemHolder> AUTO_FARM_PREMIUM_MONTH_PRICES = new LinkedHashMap<>();

    public static String AUTO_FARM_CHECK_EXTRASLOTS_MODE;
    public static String AUTO_FARM_CHECK_TODAYTIME_MODE;
    public static String AUTO_FARM_CHECK_EXTRATIME_MODE;
    public static String AUTO_FARM_CHECK_PREMIUM_MODE;

    public static int AUTO_FARM_MAX_PERMA_SLOTS;


    private AutoFarmConfig() {
        load();
    }

    public void load() {
        try {
            L2Properties autoFarm = new L2Properties();
            InputStream is = new FileInputStream(new File(L2GABSON_CONFIG_FILE));
            autoFarm.load(is);

            AUTO_FARM_ENABLE = Boolean.parseBoolean(autoFarm.getProperty("EnableAutoFarm", "false"));
            AUTO_FARM_PREMIUM_NORMAL_AFFECT = Boolean.parseBoolean(autoFarm.getProperty("AutoFarmPremiumIsAffectedByNormalPremium", "false"));
            AUTO_FARM_SKILLS_AMOUNT = Integer.parseInt(autoFarm.getProperty("AmountConfigurableSkills", "8").trim());
            AUTO_FARM_ATT_PL = Boolean.parseBoolean(autoFarm.getProperty("EnableAttackPlayers", "false"));
            AUTO_FARM_ATT_PL_PRE = Boolean.parseBoolean(autoFarm.getProperty("AttackPlayersForPremiumOnly", "false"));
            AUTO_FARM_BUY_ONLY_IF_PREMIUM_ALREADY = Boolean.parseBoolean(autoFarm.getProperty("EnableBuyAutoFarmPremiumOnlyIfPremiumAlready", "false"));

            AUTO_FARM_NORMAL_MINUTES = Integer.parseInt(autoFarm.getProperty("NormalPlayersMinutePerDay", "30").trim());
            AUTO_FARM_NORMAL_BOXES = Integer.parseInt(autoFarm.getProperty("NormalPlayersBoxes", "1").trim());
            AUTO_FARM_PREMIUM_MINUTES = Integer.parseInt(autoFarm.getProperty("PremiumPlayersMinutePerDay", "120").trim());
            AUTO_FARM_PREMIUM_BOXES = Integer.parseInt(autoFarm.getProperty("PremiumPlayersBoxes", "4").trim());

            AUTO_FARM_MONSTER_ID = new ArrayList<>();
            String[] monId = autoFarm.getProperty("IgnoreMonsterId", "").split(",");
            if (!monId[0].isEmpty()) {
                for (String s : monId) {
                    AUTO_FARM_MONSTER_ID.add(Integer.parseInt(s));
                }
            }

            AUTO_FARM_MONSTER_NAME = new ArrayList<>();
            String[] monName = autoFarm.getProperty("IgnoreMonsterNames", "").split(",");
            if (!monName[0].isEmpty()) {
                for (String s : monName) {
                    AUTO_FARM_MONSTER_NAME.add(s);
                }
            }

            AUTO_FARM_ATTACK_RAID_GRAND = Boolean.parseBoolean(autoFarm.getProperty("EnableAttackRaidAndGrands", "false"));
            AUTO_FARM_ATTACK_RAID_GRAND_PREMIUM = Boolean.parseBoolean(autoFarm.getProperty("EnableAttackRaidAndGrandsPremiums", "false"));

            propertySplit = autoFarm.getProperty("ClassIdBlocked", "false").trim().split(",");
            for (String s : propertySplit) {
                AUTO_FARM_BLOCKED_IDS.add(Integer.parseInt(s));
            }

            propertySplit = autoFarm.getProperty("SlotsShopPrices", "false").trim().split(";");
            for (String s : propertySplit) {
                String[] inner = s.trim().split(",");
                AUTO_FARM_SLOTS_PRICES.put(Integer.parseInt(inner[0]), new ItemHolder(Integer.parseInt(inner[1]), Integer.parseInt(inner[2])));
            }
            propertySplit = autoFarm.getProperty("MinutesShopPrices", "false").trim().split(";");
            for (String s : propertySplit) {
                String[] inner = s.trim().split(",");
                AUTO_FARM_MINUTES_PRICES.put(Integer.parseInt(inner[0]), new ItemHolder(Integer.parseInt(inner[1]), Integer.parseInt(inner[2])));
            }
            propertySplit = autoFarm.getProperty("PremiumDayShopPrices", "false").trim().split(";");
            for (String s : propertySplit) {
                String[] inner = s.trim().split(",");
                AUTO_FARM_PREMIUM_DAY_PRICES.put(Integer.parseInt(inner[0]), new ItemHolder(Integer.parseInt(inner[1]), Integer.parseInt(inner[2])));
            }
            propertySplit = autoFarm.getProperty("PremiumWeekShopPrices", "false").trim().split(";");
            for (String s : propertySplit) {
                String[] inner = s.trim().split(",");
                AUTO_FARM_PREMIUM_WEEK_PRICES.put(Integer.parseInt(inner[0]), new ItemHolder(Integer.parseInt(inner[1]), Integer.parseInt(inner[2])));
            }
            propertySplit = autoFarm.getProperty("PremiumMonthShopPrices", "false").trim().split(";");
            for (String s : propertySplit) {
                String[] inner = s.trim().split(",");
                AUTO_FARM_PREMIUM_MONTH_PRICES.put(Integer.parseInt(inner[0]), new ItemHolder(Integer.parseInt(inner[1]), Integer.parseInt(inner[2])));
            }

            AUTO_FARM_PREMIUM_ONLY = Boolean.parseBoolean(autoFarm.getProperty("EnableAutoFarmPremiumOnly", "false"));
            AUTO_FARM_CHECK_EXTRASLOTS_MODE = autoFarm.getProperty("EXTRASLOTSConfigurationCheckMode", "IP");
            AUTO_FARM_CHECK_TODAYTIME_MODE = autoFarm.getProperty("TIMETODAYConfigurationCheckMode", "IP");
            AUTO_FARM_CHECK_EXTRATIME_MODE = autoFarm.getProperty("EXTRATIMEConfigurationCheckMode", "IP");
            AUTO_FARM_CHECK_PREMIUM_MODE = autoFarm.getProperty("PREMIUMConfigurationCheckMode", "IP");
            AUTO_FARM_MAX_PERMA_SLOTS = Integer.parseInt(autoFarm.getProperty("MaxPemanentSlots", "0").trim());


            _log.info("Auto Farm Config Has been Initialized!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_CONFIG_FILE + " File.");
        }
    }

    protected static AutoFarmConfig instance;

    public static AutoFarmConfig getInstance() {
        if (instance == null)
            instance = new AutoFarmConfig();
        return instance;
    }

}
