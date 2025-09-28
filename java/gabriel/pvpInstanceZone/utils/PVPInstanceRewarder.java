package gabriel.pvpInstanceZone.utils;


import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.events.extremeZone.ExtremeZoneManager;
import gabriel.events.weeklyRank.WeeklyManager;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import gr.sr.utils.Tools;
import l2r.gameserver.enums.TeleportWhereType;
import l2r.gameserver.instancemanager.AntiFeedManager;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Broadcast;
import l2r.util.StringUtil;

import java.util.*;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class PVPInstanceRewarder {
    protected static PVPInstanceRewarder instance;

    private PVPInstanceRewarder() {

    }


    public static PVPInstanceRewarder getInstance() {
        if (instance == null)
            instance = new PVPInstanceRewarder();
        return instance;
    }


    public void onDiePvPInstance(L2PcInstance victim, L2PcInstance killer) {
        PVPInstanceTeleporter.getInstance().addToResurrectorPvPInstance(victim);
    }


    public void onKillPvPInstance(L2PcInstance victim, L2PcInstance killer) {
//        if (killer != null && AntiFeedManager.getInstance().check(killer, victim)) {

        try {
            if (killer != null && victim != null && !Tools.isDualBox(killer,victim )) {
                String namePlayer = killer.getName();

                PvPZoneManager.scoreClan.merge(namePlayer, 1, Integer::sum);

                if (ConfigPvPInstance.ENABLE_PVP_INSTANCE_PVPPOINTINCREASE) {
                    killer.increasePvpKills(victim, true);
                }
                giverewardPvPInstance(killer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static int previousMultiplyer = 0;

    //
    public static void giverewardPvPInstance(L2PcInstance player) {
        if (player == null) {
            return;
        }
        int playersInside = InstanceManager.getInstance().getInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID).getPlayers().size();
        int multiplyer = getPlayerMultiplyer(playersInside);

        if(previousMultiplyer != multiplyer && multiplyer > 0){
            previousMultiplyer = multiplyer;
            Broadcast.toAllOnlinePlayers(" "+playersInside+ " Players in the PvP Zone. Therefore the Kill/Assist reward has been added +"+multiplyer);
        }

        if (ConfigPvPInstance.ALLOW_FAME_PER_KILL) {
            player.setFame(player.getFame() + ConfigPvPInstance.FAME_PER_KILL);
        }

        L2PcInstance singleRewardTarget = player;
        if(ConfigPvPInstance.PVP_INSTANCE_REWARDS_KILL_PARTY_SETTING_BOUND && player.isInParty())
        {
            singleRewardTarget = player.getParty().getActualLooterForEvents(player, 57,false, player);
        }

        if (player.getParty() != null) {

            for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_KILL) {
                singleRewardTarget.addItem("Custom Reward", reward[0], reward[1]+multiplyer, singleRewardTarget, true); // reward killer
            }

            if (ConfigPvPInstance.ENABLE_PVP_KILLER_ASSIST_ALSO) {
                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                    player.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                }
            }
            for (L2PcInstance member : player.getParty().getMembers()) {
                if (member != null && member.isInsideRadius(player.getX(), player.getY(), player.getZ(), 5000, false, false)) {
                    WeeklyManager.getInstance().increasePlayerAssist(member);
                    if (ConfigPvPInstance.ALLOW_ALL_PARTYMEMBERS_REWARD) {
                        if (ConfigPvPInstance.ENABLE_ASSIST_REWARD_BOX) {
                            if (member != player) {
                                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                                    member.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }
                            }
                        } else {
                            if (member != player && !member.getClient().getConnectionAddress().equals(player.getClient().getConnectionAddress())) {
                                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                                    member.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }
                            }
                        }

                    } else {
                        if (ConfigPvPInstance.ENABLE_ASSIST_REWARD_BOX) {
                            if (((Arrays.stream(ConfigPvPInstance.CLASS_ID_ASSIST_REWARD).anyMatch(i -> i == member.getClassId().getId()))) && (member != player)) {

                                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                                    member.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }

                            }
                        } else {
                            if (((Arrays.stream(ConfigPvPInstance.CLASS_ID_ASSIST_REWARD).anyMatch(i -> i == member.getClassId().getId()))) && (member != player && !member.getClient().getConnectionAddress().equals(player.getClient().getConnectionAddress()))) {

                                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                                    member.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                                    member.getMuseumPlayer().addData("assist_on_kill", 1);
                                }

                            }
                        }

                    }


                }
            }
        } else {
            for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_KILL) {
                player.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); //reward caso o player n tiver em pt
            }
            if (ConfigPvPInstance.ENABLE_PVP_KILLER_ASSIST_ALSO_SOLO) {
                for (int[] reward : ConfigPvPInstance.PVP_INSTANCE_REWARDS_ASSIST) {
                    player.addItem("Custom Reward", reward[0], reward[1]+multiplyer, player, true); // reward assist
                }
//                WeeklyManager.getInstance().increasePlayerAssist(player);
            }
        }
    }

    public void rewardPvPInstance(int rank, int objIdReceiver, int multiplyer){
        if(rank == -1)
            return;

        L2PcInstance player = L2World.getInstance().getPlayer(objIdReceiver);
        if(rank == 1 && player != null && player.isOnline() && player.getClient() != null && !player.getClient().isDetached()){
//            if(!player.isHero())
//                player.setHero(true);
//            Broadcast.toAllOnlinePlayers(player.getName()+ " Won temporary hero for Top 1 in PvP Zone.");

        }
        List<RewardHolder> holder = new LinkedList<>();
        try{
            holder = ConfigPvPInstance.TOP_RANK_REWARDS.get(rank);
        }catch (Exception e){
            return;
        }
        if( holder == null || holder.isEmpty())
            return;
        String sender = "PvP Instance Rank";
        String title = "PvP Instance reward: Rank "+rank+".";
        String body = "You finished: "+rank+"! Congratulations!";
        Map<Integer, Long> items = new LinkedHashMap<>();
        if(holder != null && !holder.isEmpty()) {
            body += "\n Here are your rewards!";
            for (RewardHolder rewardHolder : holder) {
                items.put(rewardHolder.getItemId(), rewardHolder.getCount()*multiplyer);
            }
        }
        if( items == null || items.isEmpty())
            return;

        GabUtils.sendMailToPlayer(GabConfig.ER_EVENT_ADMIN_OBJ_ID, sender, objIdReceiver, title, body, items);
    }

    private static int getPlayerMultiplyer(int playersInside){
        for (Map.Entry<String, Integer> entry : ConfigPvPInstance.PVP_INSTANCE_PLAYER_REWARD_MULTIPLYER.entrySet()) {
            int minBoundry = Integer.parseInt(entry.getKey().split("-")[0]);
            int maxboundry = Integer.parseInt(entry.getKey().split("-")[1]);
            if(playersInside >= minBoundry && playersInside <= maxboundry){
                return entry.getValue();
            }
        }
        return 0;
    }

    static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    public void handlePvPInstanceRemovePlayers(List<Integer> _players, Instance instance) {
        if(PvPZoneManager.isAnnonym())
            ExtremeZoneManager.getInstance().endEvent();

        int multiplyer = 1; // sempre 1
//        int multiplyer = getPlayerMultiplyer(_players.size());
//
//        if(multiplyer > 1)
//            Broadcast.toAllOnlinePlayers("There were "+_players.size()+ " players inside the PvP Zone. Therefore the reward has been multiplied by x"+multiplyer);

        for (Integer objId : _players) {
            final L2PcInstance player = L2World.getInstance().getPlayer(objId);

            if(player == null)
                continue;

            NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

            Map<String, Integer> mapSorted = sortByValues(PvPZoneManager.scoreClan);

            List<String> keys = new ArrayList<>(mapSorted.keySet());
            int index = -1;
            for (int i = 0; i < keys.size(); i++) {
                String name = keys.get(i);

                if(player.getName().equals(name)) {
                    index = i;
                    break;
                }

                // do stuff here
            }
            rewardPvPInstance((index+1), objId, multiplyer);

            final StringBuilder content = StringUtil.startAppend(200);

            adminReply.setFile(player, player.getHtmlPrefix(), "data/html/PvPInstanceRank.htm");
            String key = "";
            Integer value = 0;
            index = 0;
            if ((index < 0) || (index >= mapSorted.size())) {
                key = "-";
                value = 0;
            } else {
                key = (String) mapSorted.keySet().toArray()[0];
                value = mapSorted.get(key);
            }
            adminReply.replace("%1st%", key + " Total Kills: " + value);
            index = 1;
            if ((index < 0) || (index >= mapSorted.size())) {
                key = "-";
                value = 0;
            } else {
                key = (String) mapSorted.keySet().toArray()[1];
                value = mapSorted.get(key);
            }
            adminReply.replace("%2nd%", key + " Total Kills: " + value);
            index = 2;
            if ((index < 0) || (index >= mapSorted.size())) {
                key = "-";
                value = 0;
            } else {
                key = (String) mapSorted.keySet().toArray()[2];
                value = mapSorted.get(key);
            }
            adminReply.replace("%3rd%", key + " Total Kills: " + value);
            int i = 0;
            if (i == 0) {
                StringUtil.append(content, "<tr>");
            }
            while (i != 21) {
                if (((i % 3) == 0)) {
                    StringUtil.append(content, "<tr>");
                }
                index = i + 3;
                if ((index < 0) || (index >= mapSorted.size())) {
                    key = "-";
                    value = 0;
                } else {
                    key = (String) mapSorted.keySet().toArray()[i + 3];
                    value = mapSorted.get(key);
                }
                // StringUtil.append(content, "<td align=\"center\"><font color=\"LEVEL\">" + i + 4 + ":</font> " + new Vector(sortedScoreClan.keySet()).get(i + 3) + "<br1>Kills: " + new Vector(sortedScoreClan.values()).get(i + 3) + "</td>");
                StringUtil.append(content, "<td align=\"center\"><font color=\"LEVEL\">" + (i + 4) + ":</font> " + key + "<br1>Kills: " + value + "</td>");
                i++;
                if ((i == 3) || ((i % 6) == 0)) {
                    StringUtil.append(content, "</tr>");
                }
            }
            if ((i % 6) != 0) {
                StringUtil.append(content, "</tr>");
            }
            StringUtil.append(content, "<tr><td align=\"center\"></td>");
            index = 24;
            if ((index < 0) || (index >= mapSorted.size())) {
                key = "-";
                value = 0;
            } else {
                key = (String) mapSorted.keySet().toArray()[24];
                value = mapSorted.get(key);
            }
            StringUtil.append(content, "<td align=\"center\"><font color=\"LEVEL\">" + 25 + ":</font> " + key + "<br1>Kills: " + value + "</td>");

            // StringUtil.append(content, "<td align=\"center\"><font color=\"LEVEL\">25th:</font> " + new Vector(sortedScoreClan.keySet()).get(24) + "<br1>Kills: " + new Vector(sortedScoreClan.values()).get(24) + "</td>");
            StringUtil.append(content, "<td align=\"center\"></td></tr>");
            adminReply.replace("%list%", content.toString());
            PVPInstanceShowBoard.separateAndSend(adminReply.getHtml(), player);

            PvPZoneManager.handleLeavePvPZone(player, false);
            player.setInstanceId(0);
            player.doRevive();

            if (instance.getSpawnLocPvP() == null) {
                if (instance.getExitLoc() != null)
                    player.teleToLocation(instance.getExitLoc());
                else
                    player.teleToLocation(TeleportWhereType.TOWN);
            } else {
                if (instance.getSpawnLocPvP() != null) {
                    player.teleToLocation(instance.getSpawnLocPvP(), true);
                } else {
                    player.teleToLocation(TeleportWhereType.TOWN);
                }
            }
        }
    }
}
