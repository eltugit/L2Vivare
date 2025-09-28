package gr.sr.pvpRewardEngine;


import gabriel.config.GabConfig;
import gabriel.events.weeklyRank.WeeklyManager;
import gr.sr.configsEngine.configs.impl.PvpRewardSystemConfigs;
import gr.sr.utils.Tools;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.Arrays;
import java.util.Map.Entry;


public class PvpRewardHandler {

    public PvpRewardHandler() {
    }

    
    public static void pvpRewardSystem(L2PcInstance killer, L2PcInstance target) {
        if (!PvpRewardSystemConfigs.ENABLE_PVP_REWARD_SYSTEM_IP_RESTRICTION || !Tools.isDualBox(killer, target)) {
            reward(killer);
        }
    }

    private static void reward(L2PcInstance player){

        if (player.getParty() != null)
        {
            for (L2PcInstance member : player.getParty().getMembers())
            {
                if (member != null && member.isInsideRadius(player.getX(), player.getY(), player.getZ(), 5000, false, false))
                {
                    WeeklyManager.getInstance().increasePlayerAssist(member);
                    if(PvpRewardSystemConfigs.ALLOW_ALL_PARTYMEMBERS_REWARD){
                        if(PvpRewardSystemConfigs.ENABLE_ASSIST_REWARD_BOX) {
                            if(member != player){
                                for (int[] reward : PvpRewardSystemConfigs.PVP_INSTANCE_REWARDS_ASSIST)
                                {
                                    long count = reward[1];
                                    if(member.isInsideCastleSiegeZone()){
                                        count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
                                    }
                                    member.addItem("Custom Reward", reward[0], count, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }
                            }
                        }else {
                            if(member != player && !member.getClient().getConnectionAddress().equals(player.getClient().getConnectionAddress())){
                                for (int[] reward : PvpRewardSystemConfigs.PVP_INSTANCE_REWARDS_ASSIST)
                                {
                                    long count = reward[1];
                                    if(member.isInsideCastleSiegeZone()){
                                        count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
                                    }
                                    member.addItem("Custom Reward", reward[0], count, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }
                            }
                        }

                    }else{
                        if(PvpRewardSystemConfigs.ENABLE_ASSIST_REWARD_BOX) {
                            if(((Arrays.stream(PvpRewardSystemConfigs.CLASS_ID_ASSIST_REWARD).anyMatch(i -> i == member.getClassId().getId()))) && (member!=player)) {

                                for (int[] reward : PvpRewardSystemConfigs.PVP_INSTANCE_REWARDS_ASSIST)
                                {
                                    long count = reward[1];
                                    if(member.isInsideCastleSiegeZone()){
                                        count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
                                    }
                                    member.addItem("Custom Reward", reward[0],count, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }

                            }
                        }else {
                            if(((Arrays.stream(PvpRewardSystemConfigs.CLASS_ID_ASSIST_REWARD).anyMatch(i -> i == member.getClassId().getId()))) && (member!=player && !member.getClient().getConnectionAddress().equals(player.getClient().getConnectionAddress()))) {

                                for (int[] reward : PvpRewardSystemConfigs.PVP_INSTANCE_REWARDS_ASSIST)
                                {
                                    long count = reward[1];
                                    if(member.isInsideCastleSiegeZone()){
                                        count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
                                    }
                                    member.addItem("Custom Reward", reward[0], count, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }

                            }
                        }

                    }
                }
            }
        }

        L2PcInstance singleRewardTarget = player;
        if(PvpRewardSystemConfigs.PVP_REWARDS_PARTY_SETTING_BOUND && player.isInParty()){
            singleRewardTarget = player.getParty().getActualLooterForEvents(player, 57,false, player);
        }
        for (Entry<Integer, Long> rewards : PvpRewardSystemConfigs.PVP_REWARDS.entrySet()) {
            if (rewards == null) continue;
            long count = rewards.getValue();
            if(singleRewardTarget.isInsideCastleSiegeZone()){
                count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
            }
            singleRewardTarget.addItem("PvpReward", rewards.getKey(), count, singleRewardTarget, true);
        }

        if(PvpRewardSystemConfigs.ENABLE_PVP_KILLER_ASSIST_ALSO){
            for (int[] reward : PvpRewardSystemConfigs.PVP_INSTANCE_REWARDS_ASSIST)
            {
                long count = reward[1];
                if(player.isInsideCastleSiegeZone()){
                    count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
                }
                player.addItem("Custom Reward", reward[0], count, player, true); // reward assist
            }
//            WeeklyManager.getInstance().increasePlayerAssist(player);
        }

        if(GabConfig.FAME_KILL > 0){
            player.setFame(player.getFame() + GabConfig.FAME_KILL);
//            killer.sendMessage("You have been awarded for killing an enemy! Enjoy your rewards and your extra fame!");
        }
    }
//
//    private static void reward(L2PcInstance killer) {
//        boolean isInParty = killer.getParty() != null;
//        for (Entry<Integer, Long> rewards : PvpRewardSystemConfigs.PVP_REWARDS.entrySet()) {
//            if (rewards == null) continue;
//            long count = rewards.getValue();
//            if(killer.isInsideCastleSiegeZone()){
//                count = count * GabConfig.PVP_COUNT_SIEGETW_MULTIPLYER;
//            }
//            killer.addItem("PvpReward", rewards.getKey(), count, killer, true);
//
//            if (isInParty && GabConfig.ENABLE_ASSIST_REWARD) {
//                if (GabConfig.EVERYONE_ASSIST_REWARD) {
//                    for (L2PcInstance member : killer.getParty().getMembers()) {
//                        if (member != null && member.isInsideRadius(killer.getX(), killer.getY(), killer.getZ(), 5000, false, false) && member != killer) {
//                            member.addItem("PvpRewardAssist", rewards.getKey(), count, member, true);
//                            member.getMuseumPlayer().addData("assist_on_kill", 1);
//                        }
//                    }
//                } else {
//                    for (L2PcInstance member : killer.getParty().getMembers()) {
//                        if(Arrays.stream(GabConfig.CLASSID_ASSIST_REWARD).anyMatch(i -> Integer.parseInt(i) == member.getClassId().getId() && member != killer)){
//                            if (member.isInsideRadius(killer.getX(), killer.getY(), killer.getZ(), 5000, false, false)) {
//                                member.addItem("PvpRewardAssist", rewards.getKey(), count, member, true);
//                                member.getMuseumPlayer().addData("assist_on_kill", 1);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}
