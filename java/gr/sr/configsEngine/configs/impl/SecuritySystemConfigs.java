package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class SecuritySystemConfigs extends AbstractConfigs {
    public static boolean ENABLE_SECURITY_SYSTEM;
    public static boolean ENABLE_ADMIN_SECURITY_SYSTEM;
    public static int SAFE_ADMIN_PUNISH;
    public static int MAX_ENCHANT_LEVEL;
    public static boolean ENABLE_GLOBAL_ANNOUNCE;
    public static boolean ENABLE_MESSAGE_TO_PLAYER;
    public static int TIME_IN_JAIL_LOW;
    public static int TIME_IN_JAIL_MID;
    public static int TIME_IN_JAIL_HIGH;
    public static String MESSAGE_TO_SEND;
    public static String ANNOUNCE_TO_SEND;
    public static List<Integer> ADMIN_OBJECT_ID_LIST;

    public SecuritySystemConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/SecuritySystem.ini");
        ENABLE_SECURITY_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableSecuritySystem", "true"));
        ENABLE_ADMIN_SECURITY_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAdminSecuritySystem", "false"));
        SAFE_ADMIN_PUNISH = Integer.parseInt(this.getString(this._settings, this._override, "SafeAdminPunish", "3"));
        MAX_ENCHANT_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "MaxEnchantLevel", "0"));
        ENABLE_GLOBAL_ANNOUNCE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableGlobalAnnounce", "false"));
        ENABLE_MESSAGE_TO_PLAYER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableMessageToPlayer", "false"));
        TIME_IN_JAIL_LOW = Integer.parseInt(this.getString(this._settings, this._override, "TimeInJailLow", "1"));
        TIME_IN_JAIL_MID = Integer.parseInt(this.getString(this._settings, this._override, "TimeInJailMid", "60"));
        TIME_IN_JAIL_HIGH = Integer.parseInt(this.getString(this._settings, this._override, "TimeInJailHigh", "120"));
        MESSAGE_TO_SEND = this.getString(this._settings, this._override, "MessageToSend", "[GM]Server: You must be fool!");
        ANNOUNCE_TO_SEND = this.getString(this._settings, this._override, "AnnounceToSend", "tried to corrupt server bypasses, punish jail!");
        String[] adminsObjectIds = this.getString(this._settings, this._override, "AdminsObjectIds", "1204;1035;1048").trim().split(";");
        ADMIN_OBJECT_ID_LIST = new ArrayList<>(adminsObjectIds.length);

        for(int i = 0; i < adminsObjectIds.length; ++i) {
            String adminObjs = adminsObjectIds[i];
            try {
                ADMIN_OBJECT_ID_LIST.add(Integer.parseInt(adminObjs));
            } catch (NumberFormatException e) {
                _log.warn("Security System: Wrong Object Id passed: " + adminObjs);
                _log.warn(e.getMessage());
            }
        }
    }

    protected static SecuritySystemConfigs instance;

    public static SecuritySystemConfigs getInstance() {
        if (instance == null)
            instance = new SecuritySystemConfigs();
        return instance;
    }
}
