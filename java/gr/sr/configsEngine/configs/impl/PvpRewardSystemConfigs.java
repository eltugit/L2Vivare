package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;
import l2r.util.StringUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PvpRewardSystemConfigs extends AbstractConfigs {
    public static boolean ENABLE_PVP_REWARD_SYSTEM;
    public static boolean ENABLE_PVP_REWARD_SYSTEM_IP_RESTRICTION;
    public static Map<Integer, Long> PVP_REWARDS;
    public static List<int[]> PVP_INSTANCE_REWARDS_ASSIST;
    public static boolean ENABLE_PVP_KILLER_ASSIST_ALSO;
    public static boolean ENABLE_PVP_KILLER_ASSIST_ALSO_SOLO;
    public static boolean ALLOW_ALL_PARTYMEMBERS_REWARD;
    public static int[] CLASS_ID_ASSIST_REWARD;
    public static boolean ENABLE_ASSIST_REWARD_BOX;
    public static boolean PVP_REWARDS_PARTY_SETTING_BOUND;

    public PvpRewardSystemConfigs() {
    }

    public void loadConfigs() {
        String[] propertySplit;

        this.loadFile("./config/sunrise/PvpSystem.ini");
        ENABLE_PVP_REWARD_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePvpRewardSystem", "false"));
        ENABLE_PVP_REWARD_SYSTEM_IP_RESTRICTION = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePvpRewardSystemIpRestriction", "false"));
        ALLOW_ALL_PARTYMEMBERS_REWARD = Boolean.parseBoolean(this.getString(this._settings, this._override, "GiveEveryOneFromPartyAssistReward", "false"));
        propertySplit = this.getString(this._settings, this._override, "ClassIdToAssistReward", "1").split(",");
        PVP_REWARDS_PARTY_SETTING_BOUND = Boolean.parseBoolean(this.getString(this._settings, this._override, "PvpRewardsDependsPartySetting", "false"));

        CLASS_ID_ASSIST_REWARD = new int[propertySplit.length];
        for (int i = 0; i < propertySplit.length; i++)
        {
            CLASS_ID_ASSIST_REWARD[i] = Integer.parseInt(propertySplit[i]);
        }
        ENABLE_ASSIST_REWARD_BOX  =Boolean.parseBoolean(this.getString(this._settings, this._override, "RewardBoxAssistCoin", "false"));
        ENABLE_PVP_KILLER_ASSIST_ALSO = Boolean.parseBoolean(this.getString(this._settings, this._override, "GiveKillerAlsoAssistCoin", "false"));
        ENABLE_PVP_KILLER_ASSIST_ALSO_SOLO = Boolean.parseBoolean(this.getString(this._settings, this._override, "GiveKillerAlsoAssistCoinSolo", "false"));
        propertySplit = this.getString(this._settings, this._override, "PvPInstanceRewardAssist", "57,2").split(";");
        PVP_INSTANCE_REWARDS_ASSIST = new LinkedList<>();

        for (String reward : propertySplit)
        {
            String[] rewardSplit = reward.split(",");
            if (rewardSplit.length != 2)
            {
                _log.warn(StringUtil.concat("PvpRewardSystemConfigs[PvpRewardSystemConfigs.load()]: invalid config property -> PvPInstanceRewardAssist \"", reward, "\""));
            }
            else
            {
                try
                {
                    PVP_INSTANCE_REWARDS_ASSIST.add(new int[]
                            {
                                    Integer.parseInt(rewardSplit[0]),
                                    Integer.parseInt(rewardSplit[1])
                            });
                }
                catch (NumberFormatException nfe)
                {
                    if (!reward.isEmpty())
                    {
                        _log.warn(StringUtil.concat("PvpRewardSystemConfigs[PvpRewardSystemConfigs.load()]: invalid config property -> PvPInstanceRewardAssist \"", reward, "\""));
                    }
                }
            }
        }

        String[] pvpRewards = this.getString(this._settings, this._override, "PvpRewards", "").split(";");
        PVP_REWARDS = new HashMap(pvpRewards.length);
        if (!pvpRewards[0].isEmpty()) {
            for (String pvpReward : pvpRewards) {
                String rewardList;
                String[] rewards;
                if ((rewards = (rewardList = pvpReward).split(",")).length != 2) {
                    _log.warn(this.getClass() + ": invalid config property -> PvpRewards \"", rewardList, "\"");
                } else {
                    try {
                        PVP_REWARDS.put(Integer.parseInt(rewards[0]), Long.parseLong(rewards[1]));
                    } catch (NumberFormatException var6) {
                        if (!rewardList.isEmpty()) {
                            _log.warn(this.getClass() + ": invalid config property -> PvpRewards \"", rewardList, "\"");
                        }
                    }
                }
            }
        }

    }

    protected static PvpRewardSystemConfigs instance;

    public static PvpRewardSystemConfigs getInstance() {
        if (instance == null)
            instance = new PvpRewardSystemConfigs();
        return instance;
    }
}
