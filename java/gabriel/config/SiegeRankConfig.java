package gabriel.config;

import gabriel.events.siegeRank.RewardRank;
import gr.sr.utils.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class SiegeRankConfig {
    private static final Logger _log = LoggerFactory.getLogger(SiegeRankConfig.class);

    public static final String L2GABSON_CONFIG_FILE = "./config/gabriel/SiegeRank.ini";
    private static String[] propertySplit;

    public static boolean SIEGE_RANK_ENABLE;
    public static boolean SIEGE_RANK_ONLY_HTML;
    public static Map<Integer, List<RewardRank>> SIEGE_RANK_REWARD = new LinkedHashMap<>();
    public static int SIEGE_RANK_ADMIN_OBJ_ID;

    private SiegeRankConfig() {
        load();
    }

    public void load() {
        try {
            L2Properties siegeRank = new L2Properties();
            InputStream is = new FileInputStream(new File(L2GABSON_CONFIG_FILE));
            siegeRank.load(is);
            SIEGE_RANK_ENABLE = Boolean.parseBoolean(siegeRank.getProperty("TopRankRewardEnable", "false"));
            SIEGE_RANK_ONLY_HTML = Boolean.parseBoolean(siegeRank.getProperty("TopRankRewardOnlyHtml", "false"));
            SIEGE_RANK_ADMIN_OBJ_ID = Integer.parseInt(siegeRank.getProperty("AdminObjId", "false"));
            propertySplit = siegeRank.getProperty("TopRankReward", "1-57,10;57,10;57,10/2-57,9;57,9;57,9/3-57,8;57,8;57,8/4-57,7;57,7;57,7/5-57,6;57,6;57,6/6-57,5;57,5;57,5/7-57,4;57,4;57,4/8-57,2;57,2;57,2/9-57,1;57,1;57,1").trim().split("/");
            for (String s : propertySplit) {
                int position = Integer.parseInt(s.split("-")[0]);
                String[] rewards = s.split("-")[1].split(";");
                List<RewardRank> list = new LinkedList<>();
                for (String reward : rewards) {
                    int itemId = Integer.parseInt(reward.split(",")[0]);
                    int count = Integer.parseInt(reward.split(",")[1]);
                    list.add(new RewardRank(itemId, count));
                }
                SIEGE_RANK_REWARD.put(position, list);
            }
            _log.info("Siege Rank Config Has been Initialized!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_CONFIG_FILE + " File.");
        }
    }

    protected static SiegeRankConfig instance;

    public static SiegeRankConfig getInstance() {
        if (instance == null)
            instance = new SiegeRankConfig();
        return instance;
    }

}
