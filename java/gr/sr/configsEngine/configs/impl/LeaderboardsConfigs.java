package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class LeaderboardsConfigs extends AbstractConfigs {
    public static boolean ENABLE_LEADERBOARD;
    public static boolean RANK_ARENA_ACCEPT_SAME_IP;
    public static boolean RANK_ARENA_ENABLED;
    public static int RANK_ARENA_INTERVAL;
    public static int RANK_ARENA_REWARD_ID;
    public static int RANK_ARENA_REWARD_COUNT;
    public static boolean RANK_FISHERMAN_ENABLED;
    public static int RANK_FISHERMAN_INTERVAL;
    public static int RANK_FISHERMAN_REWARD_ID;
    public static int RANK_FISHERMAN_REWARD_COUNT;
    public static boolean RANK_CRAFT_ENABLED;
    public static int RANK_CRAFT_INTERVAL;
    public static int RANK_CRAFT_REWARD_ID;
    public static int RANK_CRAFT_REWARD_COUNT;
    public static boolean RANK_TVT_ENABLED;
    public static int RANK_TVT_INTERVAL;
    public static int RANK_TVT_REWARD_ID;
    public static int RANK_TVT_REWARD_COUNT;

    public LeaderboardsConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/Leaderboard.ini");
        ENABLE_LEADERBOARD = Boolean.parseBoolean(this.getString(this._settings, this._override, "Enableleaderboard", "true"));
        RANK_ARENA_ACCEPT_SAME_IP = Boolean.parseBoolean(this.getString(this._settings, this._override, "ArenaAcceptSameIP", "true"));
        RANK_ARENA_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "RankArenaEnabled", "false"));
        RANK_ARENA_INTERVAL = Integer.parseInt(this.getString(this._settings, this._override, "RankArenaInterval", "120"));
        RANK_ARENA_REWARD_ID = Integer.parseInt(this.getString(this._settings, this._override, "RankArenaRewardId", "57"));
        RANK_ARENA_REWARD_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "RankArenaRewardCount", "1000"));
        RANK_FISHERMAN_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "RankFishermanEnabled", "false"));
        RANK_FISHERMAN_INTERVAL = Integer.parseInt(this.getString(this._settings, this._override, "RankFishermanInterval", "120"));
        RANK_FISHERMAN_REWARD_ID = Integer.parseInt(this.getString(this._settings, this._override, "RankFishermanRewardId", "57"));
        RANK_FISHERMAN_REWARD_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "RankFishermanRewardCount", "1000"));
        RANK_CRAFT_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "RankCraftEnabled", "false"));
        RANK_CRAFT_INTERVAL = Integer.parseInt(this.getString(this._settings, this._override, "RankCraftInterval", "120"));
        RANK_CRAFT_REWARD_ID = Integer.parseInt(this.getString(this._settings, this._override, "RankCraftRewardId", "57"));
        RANK_CRAFT_REWARD_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "RankCraftRewardCount", "1000"));
        RANK_TVT_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "RankTvTEnabled", "false"));
        RANK_TVT_INTERVAL = Integer.parseInt(this.getString(this._settings, this._override, "RankTvTInterval", "120"));
        RANK_TVT_REWARD_ID = Integer.parseInt(this.getString(this._settings, this._override, "RankTvTRewardId", "57"));
        RANK_TVT_REWARD_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "RankTvTRewardCount", "1000"));
    }

    protected static LeaderboardsConfigs instance;

    public static LeaderboardsConfigs getInstance() {
        if (instance == null)
            instance = new LeaderboardsConfigs();
        return instance;
    }
}
