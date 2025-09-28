package gabriel.interServerExchange;

import gr.sr.utils.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class ISEConfig {
    private static final Logger _log = LoggerFactory.getLogger(ISEConfig.class);

    public static final String L2GABSON_CONFIG_FILE = "./config/gabriel/InterServerExchange.ini";
    private static String[] propertySplit;

    public static boolean ENABLED;
    public static boolean ALLOW_AUG;
    public static boolean ALLOW_ELEMENTALS;
    public static boolean ALLOW_ENCHANT;
    public static boolean ALLOW_DEPOSIT;
    public static boolean ALLOW_WITHDRAW;
    public static List<Integer> BLOCKED_ITEMS;
    public static String URL;
    public static String LOGIN;
    public static String PASSWORD;

    private ISEConfig() {
        load();
    }

    public void load() {
        try {
            L2Properties siegeRank = new L2Properties();
            InputStream is = new FileInputStream(L2GABSON_CONFIG_FILE);
            siegeRank.load(is);

            URL = siegeRank.getProperty("ISEUrl", "jdbc:mysql://localhost/gabriel?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
            LOGIN = siegeRank.getProperty("ISELogin", "root");
            PASSWORD = siegeRank.getProperty("ISEPassword", "");
            ENABLED = Boolean.parseBoolean(siegeRank.getProperty("EnableInterServerExchange", "False"));
            ALLOW_AUG = Boolean.parseBoolean(siegeRank.getProperty("ALLOW_AUG", "False"));
            ALLOW_ELEMENTALS = Boolean.parseBoolean(siegeRank.getProperty("ALLOW_ELEMENTALS", "False"));
            ALLOW_ENCHANT = Boolean.parseBoolean(siegeRank.getProperty("ALLOW_ENCHANT", "False"));
            ALLOW_DEPOSIT = Boolean.parseBoolean(siegeRank.getProperty("ALLOW_DEPOSIT", "False"));
            ALLOW_WITHDRAW = Boolean.parseBoolean(siegeRank.getProperty("ALLOW_WITHDRAW", "False"));
            String[] split = siegeRank.getProperty("BlockedItems", "57,5575,3470").split(",");
            BLOCKED_ITEMS = new ArrayList<Integer>();

            for (String id : split) {
                try {
                    Integer itemId = Integer.parseInt(id);
                    BLOCKED_ITEMS.add(itemId);
                } catch (Exception e) {
                    _log.info("Wrong config item id: " + id + ". Skipped.");
                }
            }

            _log.info("ISEConfig Has been Initialized!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_CONFIG_FILE + " File.");
        }
    }

    protected static ISEConfig instance;

    public static ISEConfig getInstance() {
        if (instance == null)
            instance = new ISEConfig();
        return instance;
    }

}
