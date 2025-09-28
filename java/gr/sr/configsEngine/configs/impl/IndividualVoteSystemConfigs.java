package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;
import l2r.util.StringUtil;

import java.util.HashMap;
import java.util.Map;


public class IndividualVoteSystemConfigs extends AbstractConfigs {
    public static boolean ENABLE_VOTE_SYSTEM;
    public static boolean ENABLE_TRIES;
    public static int TRIES_AMOUNT;
    public static int VOTE_DELAY_CHECK;
    public static String VOTE_LINK_HOPZONE;
    public static String VOTE_LINK_TOPZONE;
    public static String VOTE_LINK_TOPCO;
    public static String VOTE_LINK_NETWORK;
    public static String VOTE_LINK_GAMEBYTES;
    public static String VOTE_LINK_TOPSERVERS200;
    public static String VOTE_LINK_TOPGS200;
    public static boolean VOTE_MANAGER_ALLOW_HOPZONE;
    public static boolean VOTE_MANAGER_ALLOW_TOPZONE;
    public static boolean VOTE_MANAGER_ALLOW_TOPCO;
    public static boolean VOTE_MANAGER_ALLOW_NETWORK;
    public static boolean VOTE_MANAGER_ALLOW_GAMEBYTES;
    public static boolean VOTE_MANAGER_ALLOW_TOPSERVERS200;
    public static boolean VOTE_MANAGER_ALLOW_TOPGS00;
    public static Map<Integer, Integer> HOPZONE_REWARDS;
    public static Map<Integer, Integer> TOPZONE_REWARDS;
    public static Map<Integer, Integer> TOPCO_REWARDS;
    public static Map<Integer, Integer> NETWORK_REWARDS;
    public static Map<Integer, Integer> GAMEBYTES_REWARDS;
    public static Map<Integer, Integer> TOPSERVERS200_REWARDS;
    public static Map<Integer, Integer> TOPGS200_REWARDS;

    public IndividualVoteSystemConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/votesystems/IndividualVoteSystem.ini");
        ENABLE_VOTE_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableVoteSystem", "False"));
        ENABLE_TRIES = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableTries", "True"));
        TRIES_AMOUNT = Integer.parseInt(this.getString(this._settings, this._override, "TriesAmount", "8"));
        VOTE_DELAY_CHECK = Integer.parseInt(this.getString(this._settings, this._override, "VoteDelayCheck", "20"));
        VOTE_LINK_HOPZONE = this.getString(this._settings, this._override, "VoteLinkHopzone", "Null");
        VOTE_LINK_TOPZONE = this.getString(this._settings, this._override, "VoteLinkTopzone", "Null");
        VOTE_LINK_TOPCO = this.getString(this._settings, this._override, "VoteLinkTopCo", "Null");
        VOTE_LINK_NETWORK = this.getString(this._settings, this._override, "VoteLinkNetwork", "Null");
        VOTE_LINK_GAMEBYTES = this.getString(this._settings, this._override, "VoteLinkGameBytes", "Null");
        VOTE_LINK_TOPSERVERS200 = this.getString(this._settings, this._override, "VoteLinkTop200", "Null");
        VOTE_LINK_TOPGS200 = this.getString(this._settings, this._override, "VoteLinkTopGs200", "Null");
        VOTE_MANAGER_ALLOW_HOPZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowHopZoneVote", "false"));
        VOTE_MANAGER_ALLOW_TOPZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTopZoneVote", "false"));
        VOTE_MANAGER_ALLOW_TOPCO = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTopCoVote", "false"));
        VOTE_MANAGER_ALLOW_NETWORK = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowNetworkVote", "false"));
        VOTE_MANAGER_ALLOW_GAMEBYTES = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowGameBytesVote", "false"));
        VOTE_MANAGER_ALLOW_TOPSERVERS200 = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTopservers200Vote", "false"));
        VOTE_MANAGER_ALLOW_TOPGS00 = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTopgs200Vote", "false"));
        String[] rewardsForHopZones = this.getString(this._settings, this._override, "RewardsForHopZone", "").split(";");
        HOPZONE_REWARDS = new HashMap<>(rewardsForHopZones.length);


        if (!rewardsForHopZones[0].isEmpty()) {
            for (String rewardString : rewardsForHopZones) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForHopZone \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    HOPZONE_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForHopZone \"", rewardString, "\""));
                    }
                }
            }
        }

        String[] rewardsForTopZones = this.getString(this._settings, this._override, "RewardsForTopZone", "").split(";");
        TOPZONE_REWARDS = new HashMap<>(rewardsForTopZones.length);
        if (!rewardsForTopZones[0].isEmpty()) {
            for (String rewardString : rewardsForTopZones) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopZone \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    TOPZONE_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopZone \"", rewardString, "\""));
                    }
                }
            }
        }


        String[] rewardsForTopCos = this.getString(this._settings, this._override, "RewardsForTopCo", "").split(";");
        TOPCO_REWARDS = new HashMap<>(rewardsForTopCos.length);

        if (!rewardsForTopCos[0].isEmpty()) {
            for (String rewardString : rewardsForTopCos) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopCo \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    TOPCO_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopCo \"", rewardString, "\""));
                    }
                }
            }
        }


        String[] rewardsForNetWorks = this.getString(this._settings, this._override, "RewardsForNetWork", "").split(";");
        NETWORK_REWARDS = new HashMap<>(rewardsForNetWorks.length);

        if (!rewardsForNetWorks[0].isEmpty()) {
            for (String rewardString : rewardsForNetWorks) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForNetWork \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    NETWORK_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForNetWork \"", rewardString, "\""));
                    }
                }
            }
        }

        String[] rewardsForGamebytes = this.getString(this._settings, this._override, "RewardsForGamebytes", "").split(";");
        GAMEBYTES_REWARDS = new HashMap<>(rewardsForGamebytes.length);

        if (!rewardsForGamebytes[0].isEmpty()) {
            for (String rewardString : rewardsForGamebytes) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForGamebytes \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    GAMEBYTES_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForGamebytes \"", rewardString, "\""));
                    }
                }
            }
        }

        String[] rewardsForTopServer200s = this.getString(this._settings, this._override, "RewardsForTopServer200", "").split(";");
        TOPSERVERS200_REWARDS = new HashMap<>(rewardsForTopServer200s.length);

        if (!rewardsForTopServer200s[0].isEmpty()) {
            for (String rewardString : rewardsForTopServer200s) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopServer200 \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    TOPSERVERS200_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopServer200 \"", rewardString, "\""));
                    }
                }
            }
        }


        String[] rewardsForTopGs200s = this.getString(this._settings, this._override, "RewardsForTopGs200", "").split(";");
        TOPGS200_REWARDS = new HashMap<>(rewardsForTopGs200s.length);

        if (!rewardsForTopGs200s[0].isEmpty()) {
            for (String rewardString : rewardsForTopGs200s) {
                String[] rewards = rewardString.split(",");
                if (rewards.length != 2) {
                    _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopGs200 \"", rewardString, "\""));
                }

                int itemId = Integer.parseInt(rewards[0]);
                int itemCount = Integer.parseInt(rewards[1]);
                try {
                    TOPGS200_REWARDS.put(itemId, itemCount);
                } catch (NumberFormatException e) {
                    if (!rewardString.isEmpty()) {
                        _log.warn(StringUtil.concat("Config.load(): invalid config property -> RewardsForTopGs200 \"", rewardString, "\""));
                    }
                }
            }
        }




    }

    protected static IndividualVoteSystemConfigs instance;

    public static IndividualVoteSystemConfigs getInstance() {
        if (instance == null)
            instance = new IndividualVoteSystemConfigs();
        return instance;
    }
}
