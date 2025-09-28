package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class AioItemsConfigs extends AbstractConfigs {
    
    public static boolean ALLOW_AIO_ITEM_COMMAND;
    
    public static boolean ENABLE_AIO_NPCS;
    
    public static int AIO_ITEM_ID;
    public static boolean DESTROY_ON_DISABLE;
    public static boolean GIVEANDCHECK_ATSTARTUP;
    public static boolean AIO_ENABLE_TP_DELAY;
    public static boolean AIO_ENABLE_DELAY;
    public static double AIO_DELAY;
    
    public static boolean AIO_DELAY_SENDMESSAGE;
    
    public static int CHANGE_GENDER_DONATE_COIN;
    
    public static int CHANGE_GENDER_DONATE_PRICE;
    
    public static int CHANGE_GENDER_NORMAL_COIN;
    
    public static int CHANGE_GENDER_NORMAL_PRICE;
    
    public static int CHANGE_NAME_COIN;
    
    public static int CHANGE_NAME_PRICE;
    
    public static int CHANGE_CNAME_COIN;
    
    public static int CHANGE_CNAME_PRICE;
    
    public static int AUGMENT_COIN;
    
    public static int AUGMENT_PRICE;
    
    public static int ELEMENT_COIN;
    
    public static int ELEMENT_PRICE;
    
    public static boolean ELEMENT_ALLOW_MORE_ATT_FOR_WEAPONS;
    
    public static int ELEMENT_VALUE_ARMOR;
    
    public static int ELEMENT_VALUE_WEAPON;
    
    public static int GET_FULL_CLAN_COIN;
    
    public static int GET_FULL_CLAN_PRICE;
    
    public static int AIO_EXCHANGE_ID;
    
    public static int AIO_EXCHANGE_PRICE;
    
    public static boolean ALLOW_TELEPORT_DURING_SIEGE;
    
    public static List<Integer> MULTISELL_LIST;

    public AioItemsConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/AioItems.ini");
        ALLOW_AIO_ITEM_COMMAND = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowAioItemVoiceCommand", "false"));
        ENABLE_AIO_NPCS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableAioNpcs", "true"));
        AIO_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "AioItemId", "41005"));
        DESTROY_ON_DISABLE = Boolean.parseBoolean(this.getString(this._settings, this._override, "DestroyOnDisable", "false"));
        GIVEANDCHECK_ATSTARTUP = Boolean.parseBoolean(this.getString(this._settings, this._override, "CheckAndReGiveAioItem", "true"));
        CHANGE_GENDER_DONATE_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderDonateCoin", "40000"));
        CHANGE_GENDER_DONATE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderDonatePrice", "10"));
        CHANGE_GENDER_NORMAL_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderNormalCoin", "40002"));
        CHANGE_GENDER_NORMAL_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderNormalPrice", "50000"));
        CHANGE_NAME_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNameCoin", "40000"));
        CHANGE_NAME_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNamePrice", "25000"));
        AUGMENT_COIN = Integer.parseInt(this.getString(this._settings, this._override, "AugmentCoin", "40000"));
        AUGMENT_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "AugmentPrice", "8"));
        ELEMENT_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ElementCoin", "40000"));
        ELEMENT_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ElementPrice", "8"));
        ELEMENT_ALLOW_MORE_ATT_FOR_WEAPONS = Boolean.parseBoolean(this.getString(this._settings, this._override, "ElementAllowMoreAttForWeapons", "false"));
        ELEMENT_VALUE_ARMOR = Integer.parseInt(this.getString(this._settings, this._override, "ElementValueArmor", "120"));
        ELEMENT_VALUE_WEAPON = Integer.parseInt(this.getString(this._settings, this._override, "ElementValueWeapon", "120"));
        GET_FULL_CLAN_COIN = Integer.parseInt(this.getString(this._settings, this._override, "GetFullClanCoin", "40000"));
        GET_FULL_CLAN_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "GetFullClanPrice", "25"));
        CHANGE_CNAME_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNameCoin", "40000"));
        CHANGE_CNAME_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNamePrice", "8"));
        AIO_EXCHANGE_ID = Integer.parseInt(this.getString(this._settings, this._override, "AioExchangeId", "3470"));
        AIO_EXCHANGE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "AioExchangePrice", "1000000000"));
        AIO_ENABLE_DELAY = Boolean.parseBoolean(this.getString(this._settings, this._override, "AioEnableDelay", "false"));
        AIO_ENABLE_TP_DELAY = Boolean.parseBoolean(this.getString(this._settings, this._override, "AioEnableTPDelay", "false"));
        AIO_DELAY = Double.parseDouble(this.getString(this._settings, this._override, "AioDelay", "0.75"));
        AIO_DELAY_SENDMESSAGE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AioDelaySendMessage", "false"));
        ALLOW_TELEPORT_DURING_SIEGE = Boolean.parseBoolean(this.getString(this._settings, this._override, "TeleportDuringSiege", "False"));
        String[] multisellList = this.getString(this._settings, this._override, "MultisellList", "1204;1035;1048").trim().split(";");
        MULTISELL_LIST = new ArrayList(multisellList.length);
        int multisellListLength = multisellList.length;

        for(int i = 0; i < multisellListLength; ++i) {
            String multisell = multisellList[i];

            try {
                MULTISELL_LIST.add(Integer.parseInt(multisell));
            } catch (NumberFormatException var6) {
                _log.warn(this.getClass() + ": Wrong Multisell Id passed: " + multisell);
                _log.warn(var6.getMessage());
            }
        }

    }

    protected static AioItemsConfigs instance;

    public static AioItemsConfigs getInstance() {
        if (instance == null)
            instance = new AioItemsConfigs();
        return instance;
    }
}
