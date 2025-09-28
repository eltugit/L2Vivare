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


public class ArenaLeaderboard {
    private static ArenaLeaderboard leaderboard;
    public Logger _log = LoggerFactory.getLogger(ArenaLeaderboard.class);
    public Map<Integer, ArenaRank> _ranks = new ConcurrentHashMap();
    protected Future<?> _actionTask = null;
    protected int SAVETASK_DELAY;
    protected Long nextTimeUpdateReward;

    
    public static ArenaLeaderboard getInstance() {
        if (leaderboard == null) {
            leaderboard = new ArenaLeaderboard();
        }
        return leaderboard;
    }

    public ArenaLeaderboard() {
        this.SAVETASK_DELAY = LeaderboardsConfigs.RANK_ARENA_INTERVAL;
        this.nextTimeUpdateReward = 0L;
        this.engineInit();
    }

    
    public void onKill(int playerObjId, String name) {
        ArenaRank rank;
        if (this._ranks.get(playerObjId) == null) {
            rank = new ArenaRank(this);
        } else {
            rank = (ArenaRank)this._ranks.get(playerObjId);
        }

        rank.pvp();
        rank.name = name;
        this._ranks.put(playerObjId, rank);
    }

    
    public void onDeath(int playerObjId, String name) {
        ArenaRank arena;
        if (this._ranks.get(playerObjId) == null) {
            arena = new ArenaRank(this);
        } else {
            arena = (ArenaRank)this._ranks.get(playerObjId);
        }

        arena.death();
        arena.name = name;
        this._ranks.put(playerObjId, arena);
    }

    public void startTask() {
        if (this._actionTask == null) {
            this._actionTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new ArenaTask(this), 1000L, (long)(this.SAVETASK_DELAY * 1000));
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
        Iterator iterator = this._ranks.keySet().iterator();

        int playerObjId;
        ArenaRank arena;
        while(iterator.hasNext()) {
            playerObjId = (Integer)iterator.next();
            arena = (ArenaRank)this._ranks.get(playerObjId);
            map.put(playerObjId, arena.kills - arena.death);
        }

        int klsdts = -1;
        playerObjId = 0;
        Iterator iter = map.keySet().iterator();

        while(iter.hasNext()) {
            int objdIdP = (Integer)iter.next();
            if ((Integer)map.get(objdIdP) > klsdts) {
                playerObjId = objdIdP;
                klsdts = (Integer)map.get(objdIdP);
            }
        }

        if ((arena = (ArenaRank)this._ranks.get(playerObjId)) == null) {
            Broadcast.toAllOnlinePlayers("PvP Arena Manager: No winners at this time!");
            this._ranks.clear();
        } else {
            L2PcInstance player = (L2PcInstance)L2World.getInstance().findObject(playerObjId);
            Broadcast.toAllOnlinePlayers("PvP Arena Manager: " + arena.name + " is the winner for this time with " + arena.kills + "/" + arena.death + ". Next calculation in " + LeaderboardsConfigs.RANK_ARENA_INTERVAL + " min(s).");
            if (player != null && LeaderboardsConfigs.RANK_ARENA_REWARD_ID > 0 && LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT > 0) {
                player.getInventory().addItem("ArenaManager", LeaderboardsConfigs.RANK_ARENA_REWARD_ID, (long)LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT, player, (Object)null);
                if (LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT > 1) {
                    player.sendPacket(((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(LeaderboardsConfigs.RANK_ARENA_REWARD_ID)).addInt(LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT));
                } else {
                    player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(LeaderboardsConfigs.RANK_ARENA_REWARD_ID));
                }

                player.sendPacket(new ItemList(player, false));
            }

            this._ranks.clear();
        }
    }

    
    public String showHtm(int var1) {
        ConcurrentHashMap var2 = new ConcurrentHashMap();
        Iterator it = this._ranks.keySet().iterator();

        while(it.hasNext()) {
            int var4 = (Integer)it.next();
            ArenaRank var5 = (ArenaRank)this._ranks.get(var4);
            var2.put(var4, var5.kills - var5.death);
        }

        Map var9 = Util.sortMap(var2, false);
        int var11 = 0;
        String var13 = "<html><body><center><font color=\"cc00ad\">Arena TOP " + 20 + " Players</font><br>";
        var13 = var13 + "<table width=260 border=0 cellspacing=0 cellpadding=0 bgcolor=333333>";
        var13 = var13 + "<tr> <td align=center>No.</td> <td align=center>Name</td> <td align=center>Kills</td> <td align=center>Deaths</td> </tr>";
        var13 = var13 + "<tr> <td align=center>&nbsp;</td> <td align=center>&nbsp;</td> <td align=center></td> <td align=center></td> </tr>";
        boolean var12 = false;

        int var7;
        for(Iterator var6 = var9.keySet().iterator(); var6.hasNext(); ++var11) {
            var7 = (Integer)var6.next();
            if (var11 >= 20) {
                break;
            }

            ArenaRank var8 = (ArenaRank)this._ranks.get(var7);
            var13 = var13 + a(var11, var8.name, var8.kills, var8.death, var7 == var1);
            if (var7 == var1) {
                var12 = true;
            }
        }

        ArenaRank var14;
        if (!var12 && (var14 = (ArenaRank)this._ranks.get(var1)) != null) {
            var13 = var13 + "<tr> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> <td align=center>...</td> </tr>";
            var7 = 0;
            Iterator var15 = var9.keySet().iterator();

            while(var15.hasNext()) {
                int var10 = (Integer)var15.next();
                ++var7;
                if (var10 == var1) {
                    break;
                }
            }

            var13 = var13 + a(var7, var14.name, var14.kills, var14.death, true);
        }

        var13 = var13 + "</table>";
        var13 = var13 + "<br><br>";
        if (LeaderboardsConfigs.RANK_ARENA_REWARD_ID > 0 && LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT > 0) {
            var13 = var13 + "Next Reward Time in <font color=\"LEVEL\">" + (int)(this.nextTimeUpdateReward - System.currentTimeMillis()) / 1000 + " min(s)</font><br1>";
            var13 = var13 + "<font color=\"aadd77\">" + LeaderboardsConfigs.RANK_ARENA_REWARD_COUNT + " &#" + LeaderboardsConfigs.RANK_ARENA_REWARD_ID + ";</font>";
        }

        return var13 + "</center></body></html>";
    }

    private static String a(int var0, String var1, int var2, int var3, boolean var4) {
        String var5 = "";
        return var5 + "\t<tr><td align=center>" + (var4 ? "<font color=\"LEVEL\">" : "") + (var0 + 1) + ".</td><td align=center>" + var1 + "</td><td align=center>" + var2 + "</td><td align=center>" + var3 + (var4 ? "</font>" : "") + " </td></tr>";
    }

    public void engineInit() {
        this.startTask();
        this._log.info(this.getClass().getSimpleName() + ": Initialized");
    }

    public class ArenaRank {
        public int kills;
        public int death;
        public int classId;
        public String name;

        public ArenaRank(ArenaLeaderboard var1) {
            leaderboard = var1;
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

    public class ArenaTask implements Runnable {
        public ArenaTask(ArenaLeaderboard leaderboard) {
            ArenaLeaderboard.leaderboard = leaderboard;
        }

        public void run() {
            leaderboard._log.info("ArenaManager: Autotask init.");
            leaderboard.formRank();
            leaderboard.nextTimeUpdateReward = System.currentTimeMillis() + (long)(leaderboard.SAVETASK_DELAY * 1000);
        }
    }

}

