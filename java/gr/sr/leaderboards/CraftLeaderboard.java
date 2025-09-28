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


public class CraftLeaderboard {
    private static CraftLeaderboard leaderboard;
    public Logger _log = LoggerFactory.getLogger(CraftLeaderboard.class);
    public Map<Integer, CraftRank> _ranks = new ConcurrentHashMap();
    protected Future<?> _actionTask = null;
    protected int TASK_DELAY;
    protected Long nextTimeUpdateReward;

    
    public static CraftLeaderboard getInstance() {
        if (leaderboard == null) {
            leaderboard = new CraftLeaderboard();
        }

        return leaderboard;
    }

    public CraftLeaderboard() {
        this.TASK_DELAY = LeaderboardsConfigs.RANK_CRAFT_INTERVAL;
        this.nextTimeUpdateReward = 0L;
        this.engineInit();
    }

    
    public void onSucess(int objIdP, String name) {
        CraftRank rank;
        if (this._ranks.get(objIdP) == null) {
            rank = new CraftRank(this);
        } else {
            rank = (CraftRank)this._ranks.get(objIdP);
        }

        rank.sucess();
        rank.name = name;
        this._ranks.put(objIdP, rank);
    }

    
    public void onFail(int objIdP, String name) {
        CraftRank rank;
        if (this._ranks.get(objIdP) == null) {
            rank = new CraftRank(this);
        } else {
            rank = (CraftRank)this._ranks.get(objIdP);
        }

        rank.fail();
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
            this._actionTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new CraftTask(this), 1000L, (long)(this.TASK_DELAY * 1000));
        }

    }

    public void formRank() {
        ConcurrentHashMap map = new ConcurrentHashMap();
        Iterator it = this._ranks.keySet().iterator();

        int objIdP;
        CraftRank rank;
        while(it.hasNext()) {
            objIdP = (Integer)it.next();
            rank = (CraftRank)this._ranks.get(objIdP);
            map.put(objIdP, rank.sucess - rank.fail);
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

        if ((rank = (CraftRank)this._ranks.get(objIdP)) == null) {
            Broadcast.toAllOnlinePlayers("CraftMaster: No winners at this time!");
            this._ranks.clear();
        } else {
            L2PcInstance player = (L2PcInstance)L2World.getInstance().findObject(objIdP);
            Broadcast.toAllOnlinePlayers("Attention Players: " + rank.name + " is the winner for this time with " + rank.sucess + "/" + rank.fail + ". Next calculation in " + LeaderboardsConfigs.RANK_CRAFT_INTERVAL + " min(s).");
            if (player != null && LeaderboardsConfigs.RANK_CRAFT_REWARD_ID > 0 && LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT > 0) {
                player.getInventory().addItem("CraftManager", LeaderboardsConfigs.RANK_CRAFT_REWARD_ID, (long)LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT, player, (Object)null);
                if (LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT > 1) {
                    player.sendPacket(((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(LeaderboardsConfigs.RANK_CRAFT_REWARD_ID)).addInt(LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT));
                } else {
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(LeaderboardsConfigs.RANK_CRAFT_REWARD_ID));
                }
                player.sendPacket(new ItemList(player, false));
            }
            this._ranks.clear();
        }
    }

    
    public String showHtm(int var1) {
        ConcurrentHashMap mp = new ConcurrentHashMap();
        Iterator it = this._ranks.keySet().iterator();

        while(it.hasNext()) {
            int var4 = (Integer)it.next();
            CraftRank rank = (CraftRank)this._ranks.get(var4);
            mp.put(var4, rank.sucess - rank.fail);
        }

        Map map = Util.sortMap(mp, false);
        int var11 = 0;
        String htm = "<html><body><center><font color=\"cc00ad\">TOP " + 20 + " Crafters</font><br>";
        htm = htm + "<table width=260 border=0 cellspacing=0 cellpadding=0 bgcolor=333333>";
        htm = htm + "<tr> <td align=center>No.</td> <td align=center>Name</td> <td align=center>Success</td> <td align=center>Fail</td> </tr>";
        htm = htm + "<tr> <td align=center>&nbsp;</td> <td align=center>&nbsp;</td> <td align=center></td> <td align=center></td> </tr>";
        boolean var12 = false;

        int var7;
        for(Iterator var6 = map.keySet().iterator(); var6.hasNext(); ++var11) {
            var7 = (Integer)var6.next();
            if (var11 >= 20) {
                break;
            }

            CraftRank var8 = (CraftRank)this._ranks.get(var7);
            htm = htm + generateHtml(var11, var8.name, var8.sucess, var8.fail, var7 == var1);
            if (var7 == var1) {
                var12 = true;
            }
        }

        CraftRank rank;
        if (!var12 && (rank = (CraftRank)this._ranks.get(var1)) != null) {
            htm = htm + "<tr> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> </tr>";
            var7 = 0;
            Iterator var15 = map.keySet().iterator();

            while(var15.hasNext()) {
                int var10 = (Integer)var15.next();
                ++var7;
                if (var10 == var1) {
                    break;
                }
            }

            htm = htm + generateHtml(var7, rank.name, rank.sucess, rank.fail, true);
        }

        htm = htm + "</table>";
        htm = htm + "<br><br>";
        if (LeaderboardsConfigs.RANK_CRAFT_REWARD_ID > 0 && LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT > 0) {
            htm = htm + "Next Reward Time in <font color=\"LEVEL\">" + (int)(this.nextTimeUpdateReward - System.currentTimeMillis()) / 1000 + " min(s)</font><br1>";
            htm = htm + "<font color=\"aadd77\">" + LeaderboardsConfigs.RANK_CRAFT_REWARD_COUNT + " &#" + LeaderboardsConfigs.RANK_CRAFT_REWARD_ID + ";</font>";
        }

        return htm + "</center></body></html>";
    }

    private static String generateHtml(int index, String name, int sucess, int fail, boolean var4) {
        String html = "";
        return html + "\t<tr><td align=center>" + (var4 ? "<font color=\"LEVEL\">" : "") + (index + 1) + ".</td><td align=center>" + name + "</td><td align=center>" + sucess + "</td><td align=center>" + fail + (var4 ? "</font>" : "") + " </td></tr>";
    }

    public void engineInit() {
        this.startTask();
        this._log.info(this.getClass().getSimpleName() + ": Initialized");
    }

    public class CraftTask implements Runnable {
        public CraftTask(CraftLeaderboard ld) {
            leaderboard = ld;
        }

        public void run() {
            leaderboard._log.info("CraftManager: Autotask init.");
            leaderboard.formRank();
            leaderboard.nextTimeUpdateReward = System.currentTimeMillis() + (long)(leaderboard.TASK_DELAY * 1000);
        }
    }

    public class CraftRank {
        public int sucess;
        public int fail;
        public String name;

        public CraftRank(CraftLeaderboard var1) {
            leaderboard = var1;
            this.sucess = 0;
            this.fail = 0;
        }

        public void sucess() {
            ++this.sucess;
        }

        public void fail() {
            ++this.fail;
        }
    }


}
