package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;
import l2r.util.StringUtil;

import java.util.HashMap;
import java.util.Map;


public class FlagZoneConfigs extends AbstractConfigs {
    public static boolean ENABLE_FLAG_ZONE;
    public static boolean ENABLE_ANTIFEED_PROTECTION;
    public static boolean ENABLE_PC_IP_PROTECTION;
    public static boolean SHOW_DIE_ANIMATION;
    public static boolean AUTO_FLAG_ON_ENTER;
    public static int MAX_SAME_TARGET_CONTINUOUS_KILLS;
    public static Map<Integer, Long> FLAG_ZONE_REWARDS;
    public static boolean ENABLE_FLAG_ZONE_AUTO_REVIVE;
    public static int FLAG_ZONE_REVIVE_DELAY;
    public static int FLAG_ZONE_AUTO_RES_LOCS_COUNT;
    public static int[] xCoords;
    public static int[] yCoords;
    public static int[] zCoords;

    public FlagZoneConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/zones/FlagZone.ini");
        ENABLE_FLAG_ZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableFlagZone", "False"));
        ENABLE_ANTIFEED_PROTECTION = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAntifeedProtection", "True"));
        ENABLE_PC_IP_PROTECTION = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePcIpProtection", "True"));
        SHOW_DIE_ANIMATION = Boolean.parseBoolean(this.getString(this._settings, this._override, "ShowDieAnimation", "True"));
        AUTO_FLAG_ON_ENTER = Boolean.parseBoolean(this.getString(this._settings, this._override, "AutoFlagOnEnter", "True"));
        MAX_SAME_TARGET_CONTINUOUS_KILLS = Integer.parseInt(this.getString(this._settings, this._override, "MaxSameTargetContinuousKills", "3"));
        ENABLE_FLAG_ZONE_AUTO_REVIVE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableFlagZoneAutoRes", "False"));
        FLAG_ZONE_REVIVE_DELAY = Integer.parseInt(this.getString(this._settings, this._override, "FlagZoneReviveDelay", "5"));
        FLAG_ZONE_AUTO_RES_LOCS_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "FlagZoneAutoResLocsCount", "5"));
        String[] xCCoords;
        xCoords = new int[(xCCoords = this.getString(this._settings, this._override, "AutoResXCoords", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for (String loc : xCCoords) {
                xCoords[count++] = Integer.parseInt(loc);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] yCCords;
        yCoords = new int[(yCCords = this.getString(this._settings, this._override, "AutoResYCoords", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for (String loc : yCCords) {
                yCoords[count++] = Integer.parseInt(loc);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] zCCoords;
        zCoords = new int[(zCCoords = this.getString(this._settings, this._override, "AutoResZCoords", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for (String loc : zCCoords) {
                zCoords[count++] = Integer.parseInt(loc);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] flagZoneRewards = this.getString(this._settings, this._override, "FlagZoneRewards", "").split(";");
        FLAG_ZONE_REWARDS = new HashMap<>(flagZoneRewards.length);

        if (!flagZoneRewards[0].isEmpty()) {
            for (String rewardString : flagZoneRewards) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> FlagZoneRewards \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                long itemCount = Long.parseLong(rewards[1]);
                try {
                    FLAG_ZONE_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> FlagZoneRewards \"", rewardString, "\""));
                    }
                }
            }
        }

    }

    protected static FlagZoneConfigs instance;

    public static FlagZoneConfigs getInstance() {
        if (instance == null)
            instance = new FlagZoneConfigs();
        return instance;
    }
}
