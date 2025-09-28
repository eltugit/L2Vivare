package gabriel.events.weeklyRank;

import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.events.weeklyRank.objects.*;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.scriptsGab.utils.BBS;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.util.Util;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class WeeklyManager {
    protected static WeeklyManager instance;
    private final WeeklyManagerDAO manager;
    private long timeToStart;
    private boolean running = false;
    private int currentCicle;
    private static final String htmlPath = "data/html/gabriel/WeeklyRank/";

    public WeeklyRangTimer _task;

   
    public static WeeklyManager getInstance() {
        if (instance == null)
            instance = new WeeklyManager();
        return instance;
    }

    private WeeklyManager() {
        manager = WeeklyManagerDAO.getInstance();
        load();
        scheduleEventStart();
    }

    public void load() {
        manager.load();
        currentCicle = manager.getLatestCicle();
    }

    public void parseCommand(String commands, L2PcInstance playerInstance, boolean isNpc){
        String command = "";
        if(!isNpc) {
            String[] comm = commands.split(" ");
            command = comm[1];
        }else{
            command = commands;
        }
        switch (command) {
            case "currentRank":
                showRank(playerInstance, false);
                break;
            case "winnersRank":
                showRank(playerInstance, true);
                break;
            case "getRewardClan":
                getReward(playerInstance, false, false);
                showRank(playerInstance, true);
                break;
            case "getRewardPlayer":
                getReward(playerInstance, true, false);
                showRank(playerInstance, true);
                break;
            case "getRewardPlayerAssist":
                getReward(playerInstance, true, true);
                showRank(playerInstance, true);
                break;
        }
    }

    
    public void getReward(L2PcInstance player, boolean playerRank, boolean assist) {

        if (playerRank) {
            Integer[] data = existInRank(player.getObjectId(), playerRank,assist);
            boolean found = data[1] == 1;
            int rank = data[0];

            if (!found) {
                player.sendMessage("You are not in the rank!");
                return;
            }

            if (rank == -1) {
                player.sendMessage("An unexpected error occured server sided! Report to the admin! Error: Rank");
                return;
            }
            if(WeeklyManager.getInstance().hasClaimed(player.getObjectId(), !assist)){
                player.sendMessage("You already claimed another rank!");
                return;
            }
            int update = updateRank(player.getObjectId(), playerRank,assist);
            switch (update) {
                case 1:
                    player.sendMessage("You are not in the rank!");
                    break;
                case 2:
                    player.sendMessage("You already received the reward!");
                    break;
                case 3:
                    player.sendMessage("An unexpected error occured server sided! Report to the admin! Error: DB");
                    break;
                case 0:
                    rewardPlayer(player, playerRank, rank, assist);
                    break;
            }
        } else {
            if (player.getClan() != null) {
                if (player.getClan().getLeaderId() == player.getObjectId()) {

                    Integer[] data = existInRank(player.getClanId(), playerRank,assist);
                    boolean found = data[1] == 1;
                    int rank = data[0];

                    if (!found) {
                        player.sendMessage("You are not in the rank!");
                        return;
                    }

                    if (rank == -1) {
                        player.sendMessage("An unexpected error occured server sided! Report to the admin! Error: Rank");
                        return;
                    }

                    int update = updateRank(player.getClanId(), playerRank,assist);
                    switch (update) {
                        case 1:
                            player.sendMessage("You are not in the rank!");
                            break;
                        case 2:
                            player.sendMessage("You already received the reward!");
                            break;
                        case 3:
                            player.sendMessage("An unexpected error occured server sided! Report to the admin! Error: DB");
                            break;
                        case 0:
                            rewardPlayer(player, playerRank, rank, assist);
                            break;
                    }
                } else {
                    player.sendMessage("You are not the clan leader!");
                    return;
                }
            } else {
                player.sendMessage("You are not in a clan");
                return;
            }
        }
    }

    private void rewardPlayer(L2PcInstance player, boolean playerRank, int rank, boolean assist) {
        if (playerRank) {
            if(assist) {
                for (RewardObject reward : GabConfig.WEEKLYRANK_RANK_REWARD_PLAYER_ASSIST.get(rank).getRewards()) {
                    player.addItem("Custom Reward", reward.getItemId(), reward.getAmount(), player, true);
                }
            }else {
                for (RewardObject reward : GabConfig.WEEKLYRANK_RANK_REWARD_PLAYER.get(rank).getRewards()) {
                    player.addItem("Custom Reward", reward.getItemId(), reward.getAmount(), player, true);
                }
            }

        } else {
            for (RewardObject reward : GabConfig.WEEKLYRANK_RANK_REWARD_CLAN.get(rank).getRewards()) {
                player.addItem("Custom Reward", reward.getItemId(), reward.getAmount(), player, true);
            }
        }
    }

    private void showRank(L2PcInstance player, boolean winners){
        Map<Integer, String> tpls = Util.parseTemplate2(HtmCache.getInstance().getHtm(player, htmlPath + "index.htm"));
        String content2 = tpls.get(0);


        String pvpKill = tpls.get(1);
        String pvpKillClaim = tpls.get(2);
        Integer[] data = existInRank(player.getObjectId(), true, false);
        boolean found = data[1] == 1;
        if (found && winners) {
            content2 = content2.replace("%infopvpkill%", pvpKillClaim);
        }else{
            content2 = content2.replace("%infopvpkill%", pvpKill);
        }
        String clanKill = tpls.get(3);
        String clanKillClaim = tpls.get(4);

        data = existInRank(player.getClanId(), false, false);
        found = data[1] == 1;
        if (found && winners) {
            content2 = content2.replace("%infoclankill%", clanKillClaim);
        }else{
            content2 = content2.replace("%infoclankill%", clanKill);
        }

        String assistKill = tpls.get(5);
        String assistKillClaim = tpls.get(6);

        data = existInRank(player.getObjectId(), true, true);
        found = data[1] == 1;
        if (found && winners) {
            content2 = content2.replace("%infoassit%", assistKillClaim);
        }else{
            content2 = content2.replace("%infoassit%", assistKill);
        }

        String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), htmlPath + "template.htm");
        StringBuilder playersRank = new StringBuilder();
        StringBuilder clanRank = new StringBuilder();
        StringBuilder assistRank = new StringBuilder();

        List<PlayerRankObject> temp =  winners ? new ArrayList<>(WeeklyManager.getInstance().getFinalRewarders().getPlayerRanks().values()) : WeeklyManager.getInstance().getTopXPlayer();

        for (int i = 1; i <= 10; i++) {
            String tmm = template;
            try {
                PlayerRankObject playerRankObject = temp.get(i - 1);
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(playerRankObject.getRankPlace()));
                tmm = tmm.replace("%value2%", playerRankObject.getCharName());
                tmm = tmm.replace("%score%", String.valueOf(playerRankObject.getCharKills()));
            } catch (Exception e) {
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(i));
                tmm = tmm.replace("%value2%", "---");
                tmm = tmm.replace("%score%", "---");
            }
            playersRank.append(tmm);
        }

        content2 = content2.replace("%tablePlayer%", playersRank.toString());

        List<ClanRankObject> tempp = winners ? new ArrayList<>(WeeklyManager.getInstance().getFinalRewarders().getClanRanks().values()) : WeeklyManager.getInstance().getTopXClan();
        for (int i = 1; i <= 10; i++) {
            String tmm = template;

            try {
                ClanRankObject playerRankObject = tempp.get(i - 1);
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(playerRankObject.getRankPlace()));
                tmm = tmm.replace("%value2%", playerRankObject.getClanName());
                tmm = tmm.replace("%score%", String.valueOf(playerRankObject.getClanKills()));
            } catch (Exception e) {
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(i));
                tmm = tmm.replace("%value2%", "---");
                tmm = tmm.replace("%score%", "---");
            }

            clanRank.append(tmm);
        }
        content2 = content2.replace("%tableClan%", clanRank.toString());

        List<PlayerAssistRankObject> temppp = winners ? new ArrayList<>(WeeklyManager.getInstance().getFinalRewarders().getAssistRanks().values()) : WeeklyManager.getInstance().getTopXPlayerAssist();
        for (int i = 1; i <= 10; i++) {
            String tmm = template;

            try {
                PlayerAssistRankObject playerRankObject = temppp.get(i - 1);
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(playerRankObject.getRankPlace()));
                tmm = tmm.replace("%value2%", playerRankObject.getCharName());
                tmm = tmm.replace("%score%", String.valueOf(playerRankObject.getCharAssists()));
            } catch (Exception e) {
                tmm = tmm.replace("%bg%", bgColor(i));
                tmm = tmm.replace("%pos%", String.valueOf(i));
                tmm = tmm.replace("%value2%", "---");
                tmm = tmm.replace("%score%", "---");
            }

            assistRank.append(tmm);
        }
        content2 = content2.replace("%tableAssist%", assistRank.toString());
        BBS.separateAndSend(content2, player);
    }

    private String bgColor(int rank) {
        switch (rank) {
            case 1:
                return "302e18";
            case 2:
                return "373736";
            case 3:
                return "352d16";
            case 4:
                return "1c1c19";
            case 5:
                return "2e2c29";
            case 6:
                return "1c1c19";
            case 7:
                return "2e2c29";
            case 8:
                return "1c1c19";
            case 9:
                return "2e2c29";
            case 10:
                return "1c1c19";
        }
        return "1c1c19";
    }

    
    public void increaseClanKill(L2PcInstance player) {
//        if(player.getInstanceId() != ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID)
//            return;
        L2Clan killerClan = player.getClan();
        if (killerClan == null)
            return;
        int kills = manager.getClanKill(killerClan.getId());
        manager.increaseKillClan(killerClan, kills + 1);
    }

    
    public void increasePlayerKill(L2PcInstance player) {
//        if(player.getInstanceId() != ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID)
//            return;
        int kills = manager.getPlayerKill(player.getObjectId());
        manager.increaseKillPlayer(player, kills + 1);
    }

    
    public void increasePlayerAssist(L2PcInstance player) {
        //vai ignorar si
        if((Arrays.stream(ConfigPvPInstance.CLASS_ID_ASSIST_REWARD).noneMatch(i -> i == player.getClassId().getId())))
            return;
//        if(player.getInstanceId() != ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID)
//            return;
        int kills = manager.getPlayerAssist(player.getObjectId());
        manager.increaseAssistPlayer(player, kills + 1);
    }

    public ClanRankObject getTopClan() {
        return manager.getTopClan();
    }

    public PlayerRankObject getTopPlayer() {
        return manager.getTopPlayer();
    }
    public PlayerAssistRankObject getTopPlayerAssist() {
        return manager.getTopPlayerAssist();
    }

    
    public List<ClanRankObject> getTopXClan() {
        return manager.getTopXClan();
    }

    
    public List<PlayerRankObject> getTopXPlayer() {
        return manager.getTopXPlayer();
    }

    
    public List<PlayerAssistRankObject> getTopXPlayerAssist() {
        return manager.getTopXPlayerAssist();
    }

    public void startRewardingProcedure() {
        currentCicle++;
        manager.prepareForRewardClan(currentCicle);
    }

    
    public CicleObject getFinalRewarders() {
        return manager.getFinalRewarders();
    }

    
    public boolean hasClaimed(int objectId, boolean assist) {
        return manager.hasClaimed(objectId, assist);
    }

    
    public int updateRank(int objectId, boolean player, boolean assist) {
        return manager.updateClaimedReward(objectId, player, assist);
    }

    
    public Integer[] existInRank(int objId, boolean playerRank, boolean assist) {
        return manager.existInRank(objId, playerRank, assist);
    }



    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar testStartTime = null;
            // Creating a Calendar object from the specified interval value

            testStartTime = Calendar.getInstance();
            testStartTime.setLenient(true);
            testStartTime.set(Calendar.DAY_OF_WEEK, GabConfig.WEEKLYRANK_DAY);
            testStartTime.set(Calendar.HOUR_OF_DAY, GabConfig.WEEKLYRANK_HOUR);
            testStartTime.set(Calendar.MINUTE, GabConfig.WEEKLYRANK_MINUTE);

            // If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
            if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                testStartTime.add(Calendar.WEEK_OF_YEAR, 1);
            }
            _task = new WeeklyRangTimer(testStartTime.getTimeInMillis());

            timeToStart = testStartTime.getTimeInMillis();

            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            System.out.println("Could not parse WeeklyManager reward timer!");
        }
    }

    public class WeeklyRangTimer implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;

        public WeeklyRangTimer(long startTime) {
            _startTime = startTime;
            stopped = false;
        }

        public void setStartTime(long startTime) {
            _startTime = startTime;
        }

        public final boolean stopped;

        public void run() {
            int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

            if (stopped) {
                running = false;
                return;
            }

            if (delay > 0 && delay <= 6000) {
                this.announce(delay);
            }

            int nextMsg = 0;
            if (delay > 3600) {
                nextMsg = delay - 3600;
            } else if (delay > 1800) {
                nextMsg = delay - 1800;
            } else if (delay > 900) {
                nextMsg = delay - 900;
            } else if (delay > 600) {
                nextMsg = delay - 600;
            } else if (delay > 300) {
                nextMsg = delay - 300;
            } else if (delay > 60) {
                nextMsg = delay - 60;
            } else if (delay > 5) {
                nextMsg = delay - 5;
            } else if (delay > 0) {
                nextMsg = delay;
            } else {
                // start
                GabUtils.yellowBroadcast("The TOP " + GabConfig.WEEKLYRANK_CLAN_TOP + " clan and TOP " + GabConfig.WEEKLYRANK_PLAYER_TOP + " player rewards can now be claimed!", "Weekly Rank", 20);
                GabUtils.yellowBroadcast("The Weekly rank has been reseted! Go proof you are worth it!", "Weekly Rank", 20);
                startRewardingProcedure();
            }

            if (delay > 0) {
                nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
            }
        }

        private void announce(long time) {
            if (time <= 6000) {
                if (time > 5401) {
                    GabUtils.yellowBroadcast((time / 60 / 60) + " Hour(s) until weekly reward can be claimed!", "Weekly Rank", 20);
                } else if (time <= 5400 && time >= 60) {
                    GabUtils.yellowBroadcast((time / 60) + " minute(s) until weekly reward can be claimed!", "Weekly Rank", 20);
                } else {
                    GabUtils.yellowBroadcast(time + " second(s) until weekly reward can be claimed!", "Weekly Rank", 20);
                }
            }
        }
    }

}
