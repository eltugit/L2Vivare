package gr.sr.leaderboards;


import gr.sr.configsEngine.configs.impl.LeaderboardsConfigs;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ItemList;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Broadcast;
import l2r.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;


public class FishermanLeaderboard {
    private static FishermanLeaderboard leaderboard;
    public Logger _log = LoggerFactory.getLogger(FishermanLeaderboard.class);
    public Map<Integer, FishRank> _ranks = new ConcurrentHashMap();
    protected Future<?> _actionTask = null;
    protected int TASK_DELAY;
    protected Long nextTimeUpdateReward;

    
    public static FishermanLeaderboard getInstance() {
        if (leaderboard == null) {
            leaderboard = new FishermanLeaderboard();
        }

        return leaderboard;
    }

    public FishermanLeaderboard() {
        this.TASK_DELAY = LeaderboardsConfigs.RANK_FISHERMAN_INTERVAL;
        this.nextTimeUpdateReward = 0L;
        this.engineInit();
    }

    
    public void onCatch(int objIdP, String name) {
        FishRank rank;
        if (this._ranks.get(objIdP) == null) {
            rank = new FishRank(this);
        } else {
            rank = (FishRank)this._ranks.get(objIdP);
        }

        rank.cought();
        rank.name = name;
        this._ranks.put(objIdP, rank);
    }

    
    public void onEscape(int objIdP, String name) {
        FishRank rank;
        if (this._ranks.get(objIdP) == null) {
            rank = new FishRank(this);
        } else {
            rank = (FishRank)this._ranks.get(objIdP);
        }

        rank.escaped();
        rank.name = name;
        this._ranks.put(objIdP, rank);
    }
    
    public void stopTask() {
        if (this._actionTask != null) {
            this._actionTask.cancel(true);
        }

        this._actionTask = null;
    }

    public void startTask() {
        if (this._actionTask == null) {
            this._actionTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new FishermanTask(this), 1000L, (long)(this.TASK_DELAY * 1000));
        }

    }

    public void formRank() {
        ConcurrentHashMap var1 = new ConcurrentHashMap();
        Iterator var2 = this._ranks.keySet().iterator();

        int var3;
        FishRank var4;
        while(var2.hasNext()) {
            var3 = (Integer)var2.next();
            var4 = (FishRank)this._ranks.get(var3);
            var1.put(var3, var4.cought - var4.escaped);
        }

        int var6 = -1;
        var3 = 0;
        Iterator var7 = var1.keySet().iterator();

        while(var7.hasNext()) {
            int var5 = (Integer)var7.next();
            if ((Integer)var1.get(var5) > var6) {
                var3 = var5;
                var6 = (Integer)var1.get(var5);
            }
        }

        if ((var4 = (FishRank)this._ranks.get(var3)) == null) {
            Broadcast.toAllOnlinePlayers("Fisherman: No winners at this time!");
            this._ranks.clear();
        } else {
            L2PcInstance var8 = (L2PcInstance)L2World.getInstance().findObject(var3);
            Broadcast.toAllOnlinePlayers("Attention Fishermans: " + var4.name + " is the winner for this time with " + var4.cought + "/" + var4.escaped + ". Next calculation in " + LeaderboardsConfigs.RANK_FISHERMAN_INTERVAL + " min(s).");
            if (var8 != null && LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID > 0 && LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT > 0) {
                var8.getInventory().addItem("FishManager", LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID, (long)LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT, var8, (Object)null);
                if (LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT > 1) {
                    var8.sendPacket(((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID)).addInt(LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT));
                } else {
                    var8.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID));
                }

                var8.sendPacket(new ItemList(var8, false));
            }

            this._ranks.clear();
        }
    }

    
    public String showHtm(int var1) {
        ConcurrentHashMap var2 = new ConcurrentHashMap();
        Iterator var3 = this._ranks.keySet().iterator();

        while(var3.hasNext()) {
            int var4 = (Integer)var3.next();
            FishRank var5 = (FishRank)this._ranks.get(var4);
            var2.put(var4, var5.cought - var5.escaped);
        }

        Map var9 = Util.sortMap(var2, false);
        int var11 = 0;
        String html = "<html><body><center><font color=\"cc00ad\">TOP " + 20 + " Fisherman</font><br>";
        html = html + "<table width=260 border=0 cellspacing=0 cellpadding=0 bgcolor=333333>";
        html = html + "<tr> <td align=center>No.</td> <td align=center>Name</td> <td align=center>Cought</td> <td align=center>Escaped</td> </tr>";
        html = html + "<tr> <td align=center>&nbsp;</td> <td align=center>&nbsp;</td> <td align=center></td> <td align=center></td> </tr>";
        boolean var12 = false;

        int var7;
        for(Iterator var6 = var9.keySet().iterator(); var6.hasNext(); ++var11) {
            var7 = (Integer)var6.next();
            if (var11 >= 20) {
                break;
            }

            FishRank var8 = (FishRank)this._ranks.get(var7);
            html = html + generateHtml(var11, var8.name, var8.cought, var8.escaped, var7 == var1);
            if (var7 == var1) {
                var12 = true;
            }
        }

        FishRank rank;
        if (!var12 && (rank = (FishRank)this._ranks.get(var1)) != null) {
            html = html + "<tr> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> </tr>";
            var7 = 0;
            Iterator var15 = var9.keySet().iterator();

            while(var15.hasNext()) {
                int var10 = (Integer)var15.next();
                ++var7;
                if (var10 == var1) {
                    break;
                }
            }

            html = html + generateHtml(var7, rank.name, rank.cought, rank.escaped, true);
        }

        html = html + "</table>";
        html = html + "<br><br>";
        if (LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID > 0 && LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT > 0) {
            html = html + "Next Reward Time in <font color=\"LEVEL\">" + (int)(this.nextTimeUpdateReward - System.currentTimeMillis()) / 1000 + " min(s)</font><br1>";
            html = html + "<font color=\"aadd77\">" + LeaderboardsConfigs.RANK_FISHERMAN_REWARD_COUNT + " &#" + LeaderboardsConfigs.RANK_FISHERMAN_REWARD_ID + ";</font>";
        }

        return html + "</center></body></html>";
    }

    private static String generateHtml(int index, String name, int caught, int escaped, boolean var4) {
        String html = "";
        return html + "\t<tr><td align=center>" + (var4 ? "<font color=\"LEVEL\">" : "") + (index + 1) + ".</td><td align=center>" + name + "</td><td align=center>" + caught + "</td><td align=center>" + escaped + (var4 ? "</font>" : "") + " </td></tr>";
    }

    public void engineInit() {
        this.startTask();
        this._log.info(this.getClass().getSimpleName() + ": Initialized");
    }

    public class FishermanTask implements Runnable {
        public FishermanTask(FishermanLeaderboard ld) {
            leaderboard = ld;
        }

        public void run() {
            leaderboard._log.info("FishManager: Autotask init.");
            leaderboard.formRank();
            leaderboard.nextTimeUpdateReward = System.currentTimeMillis() + (long)(leaderboard.TASK_DELAY * 1000);
        }
    }
    public class FishRank {
        public int cought;
        public int escaped;
        public String name;

        public FishRank(FishermanLeaderboard ld) {
            leaderboard = ld;
            this.cought = 0;
            this.escaped = 0;
        }

        public void cought() {
            ++this.cought;
        }

        public void escaped() {
            ++this.escaped;
        }
    }
}
