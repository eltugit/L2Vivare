package gabriel.pvpInstanceZone.ConfigPvPInstance;


import gabriel.pvpInstanceZone.utils.RewardHolder;
import gr.sr.utils.L2Properties;
import l2r.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class ConfigPvPInstance {


    public static boolean ENABLE_PVP_INSTANCE_ZONE;

    public static String MODE_TYPE;

    public static String ANONYM_STRING;
    public static int PVP_INSTANCE_MAP_DURATION;

    public static int PVP_INSTANCE_INSTANCE_ID;

    public static int PVP_INSTANCE_RESS_DELAY;
    public static int PVP_INSTANCE_MIN_LEVEL;

    public static boolean ENABLE_PVP_INSTANCE_NOB_ENTER;
    public static boolean ENABLE_PVP_INSTANCE_NOB_RES;

    public static boolean ENABLE_PVP_INSTANCE_LOGOUT;

    public static boolean ENABLE_PVP_INSTANCE_RESTART;

    public static boolean ENABLE_PVP_INSTANCE_PVPPOINTINCREASE;
    public static List<int[]> PVP_INSTANCE_REWARDS_KILL;
    public static boolean PVP_INSTANCE_REWARDS_KILL_PARTY_SETTING_BOUND;

    public static List<int[]> PVP_INSTANCE_REWARDS_ASSIST;
    public static boolean ENABLE_PVP_KILLER_ASSIST_ALSO;
    public static boolean ENABLE_PVP_KILLER_ASSIST_ALSO_SOLO;
    public static boolean ALLOW_ALL_PARTYMEMBERS_REWARD;
    public static boolean ENABLE_ASSIST_REWARD_BOX;
    public static int PVP_INSTANCE_BACK_X;
    public static int PVP_INSTANCE_BACK_Y;
    public static int FAME_PER_KILL;
    public static boolean ALLOW_FAME_PER_KILL;
    public static Map<Integer, List<RewardHolder>> TOP_RANK_REWARDS = new LinkedHashMap<>();
    public static final Map<String, Integer> PVP_INSTANCE_PLAYER_REWARD_MULTIPLYER = new LinkedHashMap<>();

    public static int PVP_INSTANCE_BACK_Z;
    public static int PVP_INSTANCE_NPC_ID;
    public static int[] CLASS_ID_ASSIST_REWARD;

    public static final String L2GABSON_INSTANCE_FILE = "./config/gabriel/pvpInstance.ini";
    protected static final Logger _log = Logger.getLogger(ConfigPvPInstance.class.getName());
    private static String[] propertySplit;

    private ConfigPvPInstance() {
        load();
    }


    public void load() {
        try {
            L2Properties L2JGabsonSettings = new L2Properties();
            InputStream is = new FileInputStream(new File(L2GABSON_INSTANCE_FILE));
            L2JGabsonSettings.load(is);

            ENABLE_PVP_INSTANCE_ZONE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstanceZone", "False"));
            MODE_TYPE = L2JGabsonSettings.getProperty("PvPInstanceMode", "BlaBLa");
            ANONYM_STRING = L2JGabsonSettings.getProperty("AnonymString", "GABSON");
            PVP_INSTANCE_MAP_DURATION = Integer.parseInt(L2JGabsonSettings.getProperty("PvPInstanceMapDuration", "30"));
            PVP_INSTANCE_INSTANCE_ID = Integer.parseInt(L2JGabsonSettings.getProperty("PvPInstanceInstanceID", "9300"));
            PVP_INSTANCE_RESS_DELAY = Integer.parseInt(L2JGabsonSettings.getProperty("PvPInstanceRessDelay", "10"));
            PVP_INSTANCE_MIN_LEVEL = Integer.parseInt(L2JGabsonSettings.getProperty("PvPInstanceMinimumLevel", "85"));
            ENABLE_PVP_INSTANCE_NOB_ENTER = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstanceNoblesseEnter", "False"));
            ENABLE_PVP_INSTANCE_NOB_RES = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstanceNoblesseRespawn", "False"));
            ENABLE_PVP_INSTANCE_LOGOUT = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstanceLogout", "False"));
            ENABLE_PVP_INSTANCE_RESTART = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstanceRestart", "False"));
            PVP_INSTANCE_REWARDS_KILL = new ArrayList<int[]>();
            PVP_INSTANCE_REWARDS_KILL_PARTY_SETTING_BOUND = Boolean.parseBoolean(L2JGabsonSettings.getProperty("PvPInstanceRewardKillDependsPartySetting", "False"));

            PVP_INSTANCE_REWARDS_ASSIST = new ArrayList<int[]>();
            PVP_INSTANCE_BACK_X = Integer.parseInt(L2JGabsonSettings.getProperty("PVPBackX", "84081"));
            PVP_INSTANCE_BACK_Y = Integer.parseInt(L2JGabsonSettings.getProperty("PVPBackY", "148621"));
            PVP_INSTANCE_BACK_Z = Integer.parseInt(L2JGabsonSettings.getProperty("PVPBackZ", "-1986"));
            PVP_INSTANCE_NPC_ID = Integer.parseInt(L2JGabsonSettings.getProperty("PVPInstanceManager", "70000"));
            ENABLE_PVP_INSTANCE_PVPPOINTINCREASE = Boolean.parseBoolean(L2JGabsonSettings.getProperty("EnablePvPInstancePvPPointIncrease", "False"));
            ENABLE_PVP_KILLER_ASSIST_ALSO = Boolean.parseBoolean(L2JGabsonSettings.getProperty("GiveKillerAlsoAssistCoin", "False"));
            ENABLE_PVP_KILLER_ASSIST_ALSO_SOLO = Boolean.parseBoolean(L2JGabsonSettings.getProperty("GiveKillerAlsoAssistCoinSolo", "False"));
            ENABLE_ASSIST_REWARD_BOX = Boolean.parseBoolean(L2JGabsonSettings.getProperty("RewardBoxAssistCoin", "False"));
            ALLOW_ALL_PARTYMEMBERS_REWARD = Boolean.parseBoolean(L2JGabsonSettings.getProperty("GiveEveryOneFromPartyAssistReward", "False"));
            ALLOW_FAME_PER_KILL = Boolean.parseBoolean(L2JGabsonSettings.getProperty("AllowPvPInstanceFamePerKill", "False"));
            FAME_PER_KILL = Integer.parseInt(L2JGabsonSettings.getProperty("PvPInstanceFamePerKill", "70000"));

            propertySplit = L2JGabsonSettings.getProperty("PvPInstanceRewardKill", "57,2").split(";");
            for (String reward : propertySplit) {
                String[] rewardSplit = reward.split(",");
                if (rewardSplit.length != 2) {
                    _log.warning(StringUtil.concat("PvPInstance[ConfigPvPInstance.load()]: invalid config property -> PvPInstanceRewardKill \"", reward, "\""));
                } else {
                    try {
                        PVP_INSTANCE_REWARDS_KILL.add(new int[]
                                {
                                        Integer.parseInt(rewardSplit[0]),
                                        Integer.parseInt(rewardSplit[1])
                                });
                    } catch (NumberFormatException nfe) {
                        if (!reward.isEmpty()) {
                            _log.warning(StringUtil.concat("TvTEventEngine[ConfigPvPInstance.load()]: invalid config property -> PvPInstanceRewardKill \"", reward, "\""));
                        }
                    }
                }
            }

            String[] rewards = L2JGabsonSettings.getProperty("RankRewardPvPInstance", "1-57,1000;6393,100~2-57,500;6393,50~3-57,250;6393,25").split("~");

            for (String rewardSet : rewards)
            {
                int rank = Integer.parseInt(rewardSet.split("-")[0]);
                List<RewardHolder> rewardHolder = new LinkedList<>();
                String[] rewardss = rewardSet.split("-")[1].split(";");
                for (String s : rewardss) {
                    int id = Integer.parseInt(s.split(",")[0]);
                    int count = Integer.parseInt(s.split(",")[1]);
                    rewardHolder.add(new RewardHolder(id, count));
                }
                TOP_RANK_REWARDS.put(rank, rewardHolder);
            }

            propertySplit = L2JGabsonSettings.getProperty("PvPInstanceRewardAssist", "57,2").split(";");
            for (String reward : propertySplit) {
                String[] rewardSplit = reward.split(",");
                if (rewardSplit.length != 2) {
                    _log.warning(StringUtil.concat("PvPInstance[ConfigPvPInstance.load()]: invalid config property -> PvPInstanceRewardAssist \"", reward, "\""));
                } else {
                    try {
                        PVP_INSTANCE_REWARDS_ASSIST.add(new int[]
                                {
                                        Integer.parseInt(rewardSplit[0]),
                                        Integer.parseInt(rewardSplit[1])
                                });
                    } catch (NumberFormatException nfe) {
                        if (!reward.isEmpty()) {
                            _log.warning(StringUtil.concat("TvTEventEngine[ConfigPvPInstance.load()]: invalid config property -> PvPInstanceRewardAssist \"", reward, "\""));
                        }
                    }
                }
            }
            propertySplit = L2JGabsonSettings.getProperty("ClassIdToAssistReward", "1").split(",");
            CLASS_ID_ASSIST_REWARD = new int[propertySplit.length];
            for (int i = 0; i < propertySplit.length; i++) {
                CLASS_ID_ASSIST_REWARD[i] = Integer.parseInt(propertySplit[i]);
            }
            propertySplit = L2JGabsonSettings.getProperty("PlayerMultiplyReward", "57,2").replace(" ", "").split(";");
            if(propertySplit.length > 0){
                for (String s : propertySplit) {
                    String[] splitted = s.split(":");
                    PVP_INSTANCE_PLAYER_REWARD_MULTIPLYER.put(splitted[0], Integer.parseInt(splitted[1]));
                }
            }


            _log.info("PvPInstance Config loaded with success!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_INSTANCE_FILE + " File.");
        }
    }

    protected static ConfigPvPInstance instance;


    public static ConfigPvPInstance getInstance() {
        if (instance == null)
            instance = new ConfigPvPInstance();
        return instance;
    }
}
