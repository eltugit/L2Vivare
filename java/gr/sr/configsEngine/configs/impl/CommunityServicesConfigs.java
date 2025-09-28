package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class CommunityServicesConfigs extends AbstractConfigs {

    public static boolean COMMUNITY_SERVICES_ALLOW;

    public static String BYPASS_COMMAND;

    public static boolean COMMUNITY_SERVICES_TP_ALLOW;

    public static boolean ALLOW_TELEPORT_DURING_SIEGE;

    public static boolean COMMUNITY_SERVICES_SHOP_ALLOW;

    public static boolean COMMUNITY_SERVICES_SHOP_NONPEACE;

    public static boolean COMMUNITY_SERVICES_WASH_PK_ALLOW;

    public static boolean COMMUNITY_SERVICES_WASH_PK_NONPEACE;

    public static int COMMUNITY_SERVICES_WASH_PK_PRICE;

    public static int COMMUNITY_SERVICES_WASH_PK_ID;

    public static boolean COMMUNITY_SERVICES_NAME_CHANGE_ALLOW;

    public static boolean COMMUNITY_SERVICES_NAME_CHANGE_NONPEACE;

    public static int COMMUNITY_SERVICES_NAME_CHANGE_PRICE;

    public static int COMMUNITY_SERVICES_NAME_CHANGE_ID;

    public static boolean COMMUNITY_SERVICES_CLAN_NAME_CHANGE_ALLOW;

    public static boolean COMMUNITY_SERVICES_CLAN_NAME_CHANGE_NONPEACE;

    public static int COMMUNITY_SERVICES_CLAN_NAME_CHANGE_PRICE;

    public static int COMMUNITY_SERVICES_CLAN_NAME_CHANGE_ID;

    public static boolean COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_ALLOW;

    public static boolean COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_NONPEACE;

    public static int COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_PRICE_ARMOR;

    public static int COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_PRICE_WEAPON;

    public static int COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_ID;

    public static int COMMUNITY_SERVICES_ATTRIBUTE_LVL_FOR_ARMOR;

    public static int COMMUNITY_SERVICES_ATTRIBUTE_LVL_FOR_WEAPON;

    public static List<Integer> MULTISELL_LIST;

    public CommunityServicesConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/CommunityServices.ini");
        COMMUNITY_SERVICES_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowCommunityServices", "false"));
        BYPASS_COMMAND = this.getString(this._settings, this._override, "BypassCommand", "_bbsloc");
        COMMUNITY_SERVICES_TP_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTeleport", "false"));
        ALLOW_TELEPORT_DURING_SIEGE = Boolean.parseBoolean(this.getString(this._settings, this._override, "TeleportDuringSiege", "False"));
        COMMUNITY_SERVICES_SHOP_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowGMShop", "false"));
        COMMUNITY_SERVICES_SHOP_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowGMShopInNonPeace", "false"));
        COMMUNITY_SERVICES_WASH_PK_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowDecreasePK", "false"));
        COMMUNITY_SERVICES_WASH_PK_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowDecreasePKNonPeace", "false"));
        COMMUNITY_SERVICES_WASH_PK_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "DecreasePKPrice", "57"));
        COMMUNITY_SERVICES_WASH_PK_ID = Integer.parseInt(this.getString(this._settings, this._override, "DecreasePKCoin", "57"));
        COMMUNITY_SERVICES_NAME_CHANGE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeName", "false"));
        COMMUNITY_SERVICES_NAME_CHANGE_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeNameNonPeace", "false"));
        COMMUNITY_SERVICES_NAME_CHANGE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNamePrice", "57"));
        COMMUNITY_SERVICES_NAME_CHANGE_ID = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNameCoin", "57"));
        COMMUNITY_SERVICES_CLAN_NAME_CHANGE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeClanName", "false"));
        COMMUNITY_SERVICES_CLAN_NAME_CHANGE_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeClanNameNonPeace", "false"));
        COMMUNITY_SERVICES_CLAN_NAME_CHANGE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNamePrice", "57"));
        COMMUNITY_SERVICES_CLAN_NAME_CHANGE_ID = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNameCoin", "57"));
        COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowAttribute", "false"));
        COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowAttributeNonPeace", "false"));
        COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_PRICE_ARMOR = Integer.parseInt(this.getString(this._settings, this._override, "AttributePriceArmor", "57"));
        COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_PRICE_WEAPON = Integer.parseInt(this.getString(this._settings, this._override, "AttributePriceWeapon", "57"));
        COMMUNITY_SERVICES_ATTRIBUTE_MANAGER_ID = Integer.parseInt(this.getString(this._settings, this._override, "AttributeCoin", "57"));
        COMMUNITY_SERVICES_ATTRIBUTE_LVL_FOR_ARMOR = Integer.parseInt(this.getString(this._settings, this._override, "AttributeLevelForArmor", "7"));
        COMMUNITY_SERVICES_ATTRIBUTE_LVL_FOR_WEAPON = Integer.parseInt(this.getString(this._settings, this._override, "AttributeLevelForWeapon", "7"));
        String[] multisellLists = this.getString(this._settings, this._override, "MultisellList", "1204;1035;1048").trim().split(";");
        MULTISELL_LIST = new ArrayList<>(multisellLists.length);


        for (String multiSellID : multisellLists) {
            try {
                MULTISELL_LIST.add(Integer.parseInt(multiSellID));
            } catch (NumberFormatException e) {
                _log.warn(CommunityServicesConfigs.class.getSimpleName() + ": Wrong Multisell Id passed: " + multiSellID);
                _log.warn(e.getMessage());
            }
        }

    }

    protected static CommunityServicesConfigs instance;

    public static CommunityServicesConfigs getInstance() {
        if (instance == null)
            instance = new CommunityServicesConfigs();
        return instance;
    }
}
