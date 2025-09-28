package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class GetRewardVoteSystemConfigs extends AbstractConfigs {
    public static boolean ENABLE_VOTE_SYSTEM;
    public static boolean FORCE_VOTE_ALL_ENABLED_SITES;
    public static String VOTE_SITE_BANNERS_URL;
    public static String COMMAND;
    public static int WAIT_TIME;
    public static int NEEDED_LEVEL;
    public static boolean ENABLE_CONSOLE_LOG;
    public static boolean ENABLE_NEXT_CHECK_SYSTEM;
    public static boolean ENABLE_HWID_CHECK;
    public static String TOP_LINK;
    public static int TOP_SERVER_ID;
    public static String TOPZONE_LINK;
    public static int TOPZONE_SERVER_ID;
    public static String TOPZONE_API_KEY;
    public static String NETWORK_LINK;
    public static String NETWORK_SERVER_ID;
    public static String L2_NET_LINK;
    public static String L2_NET_SERVER_ID;
    public static boolean ENABLE_CHECK_L2_NET;
    public static boolean ENABLE_CHECK_TOPCO;
    public static boolean ENABLE_CHECK_TOPZONE;
    public static boolean ENABLE_CHECK_NETWORK;
    public static boolean ENABLE_CHECK_GAMEBYTES;
    public static String GAMEBYTES_LINK;
    public static String GAMEBYTES_SERVER_ID;
    public static String GAMEBYTES_API_KEY;
    public static boolean ENABLE_CHECK_HOPZONE;
    public static String HOPZONE_HRA_TOKEN;
    public static String HOPZONE_LINK;

    public static boolean ENABLE_CHECK_L2JBRASIL;
    public static String L2JBRASIL_LINK;
    public static String L2JBRASIL_SERVER_ID;
    public static boolean ENABLE_CHECK_L2TOPSERVER;
    public static String L2TOPSERVER_LINK;
    public static String L2TOPSERVER_API_KEY;

    public static Long[][] REWARDS_LIST;
    public static int REWARD_PREMIUM_TIME;
    public static boolean DEBUG_VOTING;

    public GetRewardVoteSystemConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/votesystems/GetRewardVoteSystem.ini");
        FORCE_VOTE_ALL_ENABLED_SITES = this.getBoolean(this._settings, this._override, "ForceVoteAllEnabledSites", false);
        ENABLE_VOTE_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableVoteSystem", "True"));
        VOTE_SITE_BANNERS_URL = this.getString(this._settings, this._override, "SiteBannersUrl", "www.l2jsunrise.com");
        COMMAND = this.getString(this._settings, this._override, "Command", "getreward");
        WAIT_TIME = Integer.parseInt(this.getString(this._settings, this._override, "WaitTime", "5"));
        NEEDED_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "NeededLevel", "20"));
        ENABLE_CONSOLE_LOG = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableConsoleLog", "False"));
        DEBUG_VOTING = Boolean.parseBoolean(this.getString(this._settings, this._override, "Debug", "False"));
        ENABLE_NEXT_CHECK_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableNextCheck", "True"));
        ENABLE_HWID_CHECK = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableHwidCheck", "True"));
        TOP_LINK = this.getString(this._settings, this._override, "TopLink", "http://l2top.co/");
        TOP_SERVER_ID = Integer.parseInt(this.getString(this._settings, this._override, "L2TopServerId", "1306"));
        TOPZONE_LINK = this.getString(this._settings, this._override, "TopzoneLink", "http://l2topzone.com/");
        TOPZONE_SERVER_ID = Integer.parseInt(this.getString(this._settings, this._override, "L2TopZoneServerId", "1306"));
        TOPZONE_API_KEY = this.getString(this._settings, this._override, "L2TopzoneApiKey", "14a548b1c62fcee72f08eef09bbee556");
        NETWORK_LINK = this.getString(this._settings, this._override, "NetworkLink", "https://l2network.eu/");
        NETWORK_SERVER_ID = this.getString(this._settings, this._override, "L2NetworkServerId", "Anius");
        L2_NET_LINK = this.getString(this._settings, this._override, "L2NetLink", "https://l2net.net/");
        L2_NET_SERVER_ID = this.getString(this._settings, this._override, "L2NetServerId", "12345");
        ENABLE_CHECK_L2_NET = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableL2NetCheck", "True"));
        ENABLE_CHECK_TOPCO = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableTopCoCheck", "True"));
        ENABLE_CHECK_TOPZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableTopZoneCheck", "True"));
        ENABLE_CHECK_NETWORK = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableNetworkCheck", "True"));
        ENABLE_CHECK_GAMEBYTES = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableGamebytesCheck", "True"));
        GAMEBYTES_LINK = this.getString(this._settings, this._override, "GamebyteskLink", "https://www.gamebytes.net/");
        GAMEBYTES_SERVER_ID = this.getString(this._settings, this._override, "GamebytesServerId", "Anius");
        GAMEBYTES_API_KEY = this.getString(this._settings, this._override, "GamebytesApiKey", "Anius");
        ENABLE_CHECK_HOPZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableHopzoneCheck", "True"));
        HOPZONE_LINK = this.getString(this._settings, this._override, "HopzoneLink", "https://api.hopzone.net/");
        HOPZONE_HRA_TOKEN = this.getString(this._settings, this._override, "HopzoneHraToken", "3ee81b3b82743e2");

        ENABLE_CHECK_L2JBRASIL = this.getBoolean(this._settings, this._override, "EnableL2jBrasilCheck", false);
        L2JBRASIL_LINK = this.getString(this._settings, this._override, "L2jBrasilLink", "http://top.l2jbrasil.com/");
        L2JBRASIL_SERVER_ID = this.getString(this._settings, this._override, "L2jBrasilServerId", "test");
        ENABLE_CHECK_L2TOPSERVER = this.getBoolean(this._settings, this._override, "EnableL2TOPSERVERCheck", false);
        L2TOPSERVER_LINK = this.getString(this._settings, this._override, "L2TOPSERVERLink", "https://l2topservers.com/");
        L2TOPSERVER_API_KEY = this.getString(this._settings, this._override, "L2TOPSERVERApiKey", "test");

        REWARDS_LIST = parseRewardList(this.getString(this._settings, this._override, "Rewards", "57,144740,100;3483,100,85"));
        REWARD_PREMIUM_TIME = Integer.parseInt(this.getString(this._settings, this._override, "PremiumRewardTime", "0"));
    }

    private static Long[][] parseRewardList(String toSplitString) {
        String[] rewards = toSplitString.split(";");

        if (rewards.length == 0) {
            return null;
        } else {
            Long[][] rewardBag = new Long[rewards.length][];
            for(int i = 0; i < rewards.length; i++) {
                String[] tempRewards = rewards[i].split(",");
                if (tempRewards.length != 3) {
                    return null;
                }
                rewardBag[i] = new Long[3];
                rewardBag[i][0] = Long.parseLong(tempRewards[0]);
                rewardBag[i][1] = Long.parseLong(tempRewards[1]);
                rewardBag[i][2] = Long.parseLong(tempRewards[2]);
            }

            return rewardBag;
        }
    }

    protected static GetRewardVoteSystemConfigs instance;

    public static GetRewardVoteSystemConfigs getInstance() {
        if (instance == null)
            instance = new GetRewardVoteSystemConfigs();
        return instance;
    }
}
