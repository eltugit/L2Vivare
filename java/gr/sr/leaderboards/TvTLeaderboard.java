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


public class TvTLeaderboard {
    private static TvTLeaderboard board;
    public Logger _log = LoggerFactory.getLogger(ArenaLeaderboard.class);
    public Map<Integer, TvTRank> _ranks = new ConcurrentHashMap();
    protected Future<?> _actionTask = null;
    protected int SAVETASK_DELAY;
    protected Long nextTimeUpdateReward;

    
    public static TvTLeaderboard getInstance() {
        if (board == null) {
            board = new TvTLeaderboard();
        }

        return board;
    }

    public TvTLeaderboard() {
        this.SAVETASK_DELAY = LeaderboardsConfigs.RANK_TVT_INTERVAL;
        this.nextTimeUpdateReward = 0L;
        this.engineInit();
    }

    
    public void onKill(int objdIdP, String name) {
        TvTRank var3;
        if (this._ranks.get(objdIdP) == null) {
            var3 = new TvTRank(this);
        } else {
            var3 = (TvTRank)this._ranks.get(objdIdP);
        }

        var3.pvp();
        var3.name = name;
        this._ranks.put(objdIdP, var3);
    }

    
    public void onDeath(int objdIdP, String name) {
        TvTRank var3;
        if (this._ranks.get(objdIdP) == null) {
            var3 = new TvTRank(this);
        } else {
            var3 = (TvTRank)this._ranks.get(objdIdP);
        }

        var3.death();
        var3.name = name;
        this._ranks.put(objdIdP, var3);
    }

    public void startTask() {
        if (this._actionTask == null) {
            this._actionTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new TvTTask(this), 1000L, (long)(this.SAVETASK_DELAY * 1000));
        }

    }
    
    public void stopTask() {
        if (this._actionTask != null) {
            this._actionTask.cancel(true);
        }

        this._actionTask = null;
    }

    public void formRank() {
        ConcurrentHashMap map = new ConcurrentHashMap();
        Iterator iter = this._ranks.keySet().iterator();

        int objIdP;
        TvTRank rank;
        while(iter.hasNext()) {
            objIdP = (Integer)iter.next();
            rank = (TvTRank)this._ranks.get(objIdP);
            map.put(objIdP, rank.kills - rank.death);
        }

        int var6 = -1;
        objIdP = 0;
        Iterator var7 = map.keySet().iterator();

        while(var7.hasNext()) {
            int var5 = (Integer)var7.next();
            if ((Integer)map.get(var5) > var6) {
                objIdP = var5;
                var6 = (Integer)map.get(var5);
            }
        }

        if ((rank = (TvTRank)this._ranks.get(objIdP)) == null) {
            Broadcast.toAllOnlinePlayers("TvTMaster: No winners at this time!");
            this._ranks.clear();
        } else {
            L2PcInstance var8 = (L2PcInstance)L2World.getInstance().findObject(objIdP);
            Broadcast.toAllOnlinePlayers("TvTMaster: " + rank.name + " is the winner for this time with " + rank.kills + "/" + rank.death + ". Next calculation in " + LeaderboardsConfigs.RANK_TVT_INTERVAL + " min(s).");
            if (var8 != null && LeaderboardsConfigs.RANK_TVT_REWARD_ID > 0 && LeaderboardsConfigs.RANK_TVT_REWARD_COUNT > 0) {
                var8.getInventory().addItem("TvTManager", LeaderboardsConfigs.RANK_TVT_REWARD_ID, (long)LeaderboardsConfigs.RANK_TVT_REWARD_COUNT, var8, (Object)null);
                if (LeaderboardsConfigs.RANK_TVT_REWARD_COUNT > 1) {
                    var8.sendPacket(((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(LeaderboardsConfigs.RANK_TVT_REWARD_ID)).addInt(LeaderboardsConfigs.RANK_TVT_REWARD_COUNT));
                } else {
                    var8.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(LeaderboardsConfigs.RANK_TVT_REWARD_ID));
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
            TvTRank var5 = (TvTRank)this._ranks.get(var4);
            var2.put(var4, var5.kills - var5.death);
        }

        Map var9 = Util.sortMap(var2, false);
        int var11 = 0;
        String html = "<html><body><center><font color=\"cc00ad\">TvT TOP " + 20 + " Players</font><br>";
        html = html + "<table width=260 border=0 cellspacing=0 cellpadding=0 bgcolor=333333>";
        html = html + "<tr> <td align=center>No.</td> <td align=center>Name</td> <td align=center>Kills</td> <td align=center>Deaths</td> </tr>";
        html = html + "<tr> <td align=center>&nbsp;</td> <td align=center>&nbsp;</td> <td align=center></td> <td align=center></td> </tr>";
        boolean var12 = false;

        int var7;
        for(Iterator var6 = var9.keySet().iterator(); var6.hasNext(); ++var11) {
            var7 = (Integer)var6.next();
            if (var11 >= 20) {
                break;
            }

            TvTRank var8 = (TvTRank)this._ranks.get(var7);
            html = html + a(var11, var8.name, var8.kills, var8.death, var7 == var1);
            if (var7 == var1) {
                var12 = true;
            }
        }

        TvTRank var14;
        if (!var12 && (var14 = (TvTRank)this._ranks.get(var1)) != null) {
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

            html = html + a(var7, var14.name, var14.kills, var14.death, true);
        }

        html = html + "</table>";
        html = html + "<br><br>";
        if (LeaderboardsConfigs.RANK_TVT_REWARD_ID > 0 && LeaderboardsConfigs.RANK_TVT_REWARD_COUNT > 0) {
            html = html + "Next Reward Time in <font color=\"LEVEL\">" + (int)(this.nextTimeUpdateReward - System.currentTimeMillis()) / 1000 + " min(s)</font><br1>";
            html = html + "<font color=\"aadd77\">" + LeaderboardsConfigs.RANK_TVT_REWARD_COUNT + " &#" + LeaderboardsConfigs.RANK_TVT_REWARD_ID + ";</font>";
        }

        return html + "</center></body></html>";
    }

    private static String a(int var0, String var1, int var2, int var3, boolean var4) {
        String var5 = "";
        return var5 + "\t<tr><td align=center>" + (var4 ? "<font color=\"LEVEL\">" : "") + (var0 + 1) + ".</td><td align=center>" + var1 + "</td><td align=center>" + var2 + "</td><td align=center>" + var3 + (var4 ? "</font>" : "") + " </td></tr>";
    }

    public void engineInit() {
        this.startTask();
        this._log.info(this.getClass().getSimpleName() + ": Initialized");
    }

    public class TvTRank {
        public int kills;
        public int death;
        public int classId;
        public String name;

        public TvTRank(TvTLeaderboard boardd) {
            board = boardd;
            this.kills = 0;
            this.death = 0;
        }

        public void pvp() {
            ++this.kills;
        }

        public void death() {
            ++this.death;
        }
    }
    public class TvTTask implements Runnable {
        public TvTTask(TvTLeaderboard boardd) {
            board = boardd;
        }

        public void run() {
            board._log.info("TvTManager: Autotask init.");
            board.formRank();
            board.nextTimeUpdateReward = System.currentTimeMillis() + (long)(board.SAVETASK_DELAY * 1000);
        }
    }
}
