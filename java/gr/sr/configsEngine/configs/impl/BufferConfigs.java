package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class BufferConfigs extends AbstractConfigs {
    
    public static boolean ENABLE_ITEM_BUFFER;
    
    public static int DONATE_BUFF_ITEM_ID;
    public static int FREE_BUFFS_TILL_LEVEL;
    public static int BUFF_ITEM_ID;
    public static int PRICE_PERBUFF;
    public static boolean HEAL_PLAYER_AFTER_ACTIONS;
    
    public static int MAX_SCHEME_PROFILES;
    public static int MAX_DANCE_PERPROFILE;
    public static int MAX_BUFFS_PERPROFILE;
    public static int[] MAGE_BUFFS_LIST;
    public static int[] FIGHTER_BUFFS_LIST;
    public static boolean BUFFER_ENABLE_DELAY;
    public static double BUFFER_DELAY;
    public static boolean BUFFER_DELAY_SENDMESSAGE;

    public BufferConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/Buffer.ini");
        ENABLE_ITEM_BUFFER = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableItemBuffer", "true"));
        DONATE_BUFF_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "DonateItemId", "40001"));
        FREE_BUFFS_TILL_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "FreeBuffsTillLevel", "0"));
        BUFF_ITEM_ID = Integer.parseInt(this.getString(this._settings, this._override, "PriceId", "57"));
        PRICE_PERBUFF = Integer.parseInt(this.getString(this._settings, this._override, "PriceCount", "100"));
        HEAL_PLAYER_AFTER_ACTIONS = Boolean.parseBoolean(this.getString(this._settings, this._override, "HealPlayerAfterAction", "false"));
        MAX_SCHEME_PROFILES = Integer.parseInt(this.getString(this._settings, this._override, "MaxProfilesPerChar", "4"));
        MAX_DANCE_PERPROFILE = Integer.parseInt(this.getString(this._settings, this._override, "MaxDancesPerProfile", "16"));
        MAX_BUFFS_PERPROFILE = Integer.parseInt(this.getString(this._settings, this._override, "MaxBuffsPerProfile", "24"));
        String[] mageBuffList;
        MAGE_BUFFS_LIST = new int[(mageBuffList = this.getString(this._settings, this._override, "MageBuffsList", "1204;1035;1048").trim().split(";")).length];

        try {
            int length = 0;
            for(int i = 0; i < mageBuffList.length; ++i) {
                MAGE_BUFFS_LIST[length++] = Integer.parseInt(mageBuffList[i]);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] fightBuffList;
        FIGHTER_BUFFS_LIST = new int[(fightBuffList = this.getString(this._settings, this._override, "FighterBuffsList", "1204;1035;1048").trim().split(";")).length];

        try {
            int length = 0;
            for(int i = 0; i < fightBuffList.length; ++i) {
                FIGHTER_BUFFS_LIST[length++] = Integer.parseInt(fightBuffList[i]);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        BUFFER_ENABLE_DELAY = Boolean.parseBoolean(this.getString(this._settings, this._override, "BufferEnableDelay", "false"));
        BUFFER_DELAY = Double.parseDouble(this.getString(this._settings, this._override, "BufferDelay", "0.75"));
        BUFFER_DELAY_SENDMESSAGE = Boolean.parseBoolean(this.getString(this._settings, this._override, "BufferDelaySendMessage", "false"));
    }
    protected static BufferConfigs instance;

    public static BufferConfigs getInstance() {
        if(instance == null)
            instance = new BufferConfigs();
        return instance;
    }
}
