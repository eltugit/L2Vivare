package gabriel.grandbosses;


import gr.sr.utils.L2Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class GrandBossesConfig {
    private static final Logger _log = LoggerFactory.getLogger(GrandBossesConfig.class);

    public static final String L2GABSON_CONFIG_FILE = "./config/gabriel/grandbosses.ini";
    private static String[] propertySplit;

    // --------------------------------------------------
    // GrandBosses settings
    // --------------------------------------------------
    /**
     * Ant Queen
     **/

    public static boolean ANTQUEEN_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> ANTQUEEN_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int ANTQUEEN_CUSTOM_SPAWN_RANDOM_INTERVAL;
    /**
     * Antharas
     **/

    public static boolean ANTHARAS_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> ANTHARAS_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL;
    /**
     * Core
     **/

    public static boolean CORE_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> CORE_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int CORE_CUSTOM_SPAWN_RANDOM_INTERVAL;
    /**
     * Orfen
     **/

    public static boolean ORFEN_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> ORFEN_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL;
    /**
     * Baium
     **/

    public static boolean BAIUM_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> BAIUM_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL;
    /**
     * Valakas
     **/

    public static boolean VALAKAS_CUSTOM_SPAWN_ENABLED;

    public static ArrayList<Calendar> VALAKAS_CUSTOM_SPAWN_TIMES = new ArrayList<Calendar>();

    public static int VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL;


    private GrandBossesConfig() {
        load();
    }


    public void load() {
        try {
            L2Properties grandbosses = new L2Properties();
            InputStream is = new FileInputStream(new File(L2GABSON_CONFIG_FILE));
            grandbosses.load(is);

            ANTQUEEN_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("AntQueenCustomSpawn", "false"));
            ANTQUEEN_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("AntQueenRandomSpawn", "0").trim());
            ANTQUEEN_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("AntQueenDaysAndHours", "").trim(), ANTQUEEN_CUSTOM_SPAWN_RANDOM_INTERVAL);
            // Antharas
            ANTHARAS_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("AntharasCustomSpawn", "false"));
            ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("AntharasRandomSpawn", "0").trim());
            ANTHARAS_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("AntharasDaysAndHours", "").trim(), ANTHARAS_CUSTOM_SPAWN_RANDOM_INTERVAL);
            // Core
            CORE_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("CoreCustomSpawn", "false"));
            CORE_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("CoreRandomSpawn", "0").trim());
            CORE_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("CoreDaysAndHours", "").trim(), CORE_CUSTOM_SPAWN_RANDOM_INTERVAL);
            // Orfen
            ORFEN_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("OrfenCustomSpawn", "false"));
            ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("OrfenRandomSpawn", "0").trim());
            ORFEN_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("OrfenDaysAndHours", "").trim(), ORFEN_CUSTOM_SPAWN_RANDOM_INTERVAL);
            // Baium
            BAIUM_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("BaiumCustomSpawn", "false"));
            BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("BaiumRandomSpawn", "0").trim());
            BAIUM_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("BaiumDaysAndHours", "").trim(), BAIUM_CUSTOM_SPAWN_RANDOM_INTERVAL);
            // Valakas
            VALAKAS_CUSTOM_SPAWN_ENABLED = Boolean.parseBoolean(grandbosses.getProperty("ValakasCustomSpawn", "false"));
            VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL = Integer.parseInt(grandbosses.getProperty("ValakasRandomSpawn", "0").trim());
            VALAKAS_CUSTOM_SPAWN_TIMES = ParseDates(grandbosses.getProperty("ValakasDaysAndHours", "").trim(), VALAKAS_CUSTOM_SPAWN_RANDOM_INTERVAL);

            _log.info("GrandBossesConfig Has been Initialized!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Failed to Load " + L2GABSON_CONFIG_FILE + " File.");
        }
    }

    protected static GrandBossesConfig instance;


    public static GrandBossesConfig getInstance() {
        if (instance == null)
            instance = new GrandBossesConfig();
        return instance;
    }

    private static ArrayList<Calendar> ParseDates(String datesString, int randMax) {
        ArrayList<Calendar> tempList = new ArrayList<>();
        String[] dates = datesString.split(";");
        Calendar today = Calendar.getInstance();
        for (String dateItem : dates) {
            String[] tokens = dateItem.split(",");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, GetDayFromString(tokens[0]));
            String[] timeTokens = tokens[1].split(":");
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeTokens[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(timeTokens[1]));
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.MINUTE, (new Random()).nextInt(randMax));
            if ((cal.getTimeInMillis() - today.getTimeInMillis()) < 0) {
                cal.add(Calendar.DAY_OF_MONTH, 7);
            }
            tempList.add(cal);
        }
        return tempList;
    }

    private static int GetDayFromString(String day) {
        day = day.toLowerCase();
        int dayVal = Calendar.MONDAY;
        switch (day) {
            case "monday":
                dayVal = Calendar.MONDAY;
                break;
            case "tuesday":
                dayVal = Calendar.TUESDAY;
                break;
            case "wednesday":
                dayVal = Calendar.WEDNESDAY;
                break;
            case "thursday":
                dayVal = Calendar.THURSDAY;
                break;
            case "friday":
                dayVal = Calendar.FRIDAY;
                break;
            case "saturday":
                dayVal = Calendar.SATURDAY;
                break;
            case "sunday":
                dayVal = Calendar.SUNDAY;
                break;
        }
        return dayVal;
    }

    public static Calendar FindNext(ArrayList<Calendar> times) {
        Calendar today = Calendar.getInstance();
        ArrayList<Calendar> tempTimes = new ArrayList<>();
        for (Calendar time : times) {
            if ((time.getTimeInMillis() - today.getTimeInMillis()) < 0) {
                time.add(Calendar.DAY_OF_MONTH, 7);
            }
            tempTimes.add(time);
        }
        if (tempTimes.size() == 0) {
            return null;
        }
        Calendar nextTime = tempTimes.get(0);
        for (Calendar t : tempTimes) {
            if ((t.getTimeInMillis() - today.getTimeInMillis()) < (nextTime.getTimeInMillis() - today.getTimeInMillis())) {
                nextTime = t;
            }
        }
        return nextTime;
    }


}
