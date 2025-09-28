package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class SmartCommunityConfigs extends AbstractConfigs {
    public static int TOP_CURRENCY_ID;
    public static int TOP_PLAYER_RESULTS;
    public static int RAID_LIST_ROW_HEIGHT;
    public static int RAID_LIST_RESULTS;
    public static int EXTRA_PLAYERS_COUNT;
    public static boolean RAID_LIST_SORT_ASC;
    public static boolean ALLOW_REAL_ONLINE_STATS;

    public SmartCommunityConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/SmartCB.ini");
        TOP_CURRENCY_ID = Integer.parseInt(this.getString(this._settings, this._override, "TopCurrencyId", "57"));
        TOP_PLAYER_RESULTS = Integer.parseInt(this.getString(this._settings, this._override, "TopPlayerResults", "20"));
        RAID_LIST_ROW_HEIGHT = Integer.parseInt(this.getString(this._settings, this._override, "RaidListRowHeight", "18"));
        RAID_LIST_RESULTS = Integer.parseInt(this.getString(this._settings, this._override, "RaidListResults", "20"));
        RAID_LIST_SORT_ASC = Boolean.parseBoolean(this.getString(this._settings, this._override, "RaidListSortAsc", "True"));
        EXTRA_PLAYERS_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "ExtraPlayersCount", "20"));
        ALLOW_REAL_ONLINE_STATS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowRealOnlineStats", "True"));
    }

    protected static SmartCommunityConfigs instance;

    public static SmartCommunityConfigs getInstance() {
        if (instance == null)
            instance = new SmartCommunityConfigs();
        return instance;
    }
}
