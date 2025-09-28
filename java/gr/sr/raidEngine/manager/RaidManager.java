package gr.sr.raidEngine.manager;


import gr.sr.raidEngine.RaidConfigs;
import gr.sr.raidEngine.RaidDrop;
import gr.sr.raidEngine.RaidGroup;
import gr.sr.raidEngine.RaidLocation;
import gr.sr.raidEngine.tasks.RaidSpawnTask;
import gr.sr.raidEngine.tasks.TeleportBack;
import gr.sr.raidEngine.xml.dataHolder.RaidConfigsHolder;
import gr.sr.raidEngine.xml.dataParser.RaidAndDropsParser;
import gr.sr.raidEngine.xml.dataParser.RaidConfigsParser;
import gr.sr.raidEngine.xml.dataParser.RaidLocationsParser;
import l2r.Config;
import l2r.gameserver.GameTimeController;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.SetupGauge;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Broadcast;
import l2r.gameserver.util.Util;
import l2r.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.Future;


public class RaidManager {
    private static Logger log = LoggerFactory.getLogger(RaidManager.class);
    public RaidConfigs configs;
    private static boolean b = false;
    private static int c = 5;
    private static int maxLevelDifference = 8;
    public L2RaidBossInstance _raid = null;
    public RaidLocation _currentLocation = null;
    public Future<?> _notifyThread = null;
    public Future<?> _despawnThread = null;
    public Future<?> _spawnThread = null;
    public int _lastRaidId = 0;
    public RaidGroup _raidGroup = null;
    private final int e = 2000;

    public RaidManager() {
        RaidConfigsParser.getInstance().load();
        RaidAndDropsParser.getInstance().load();
        RaidLocationsParser.getInstance().load();
        this.configs = (RaidConfigs)RaidConfigsHolder.getInstance().getConfigs().get(Rnd.get(RaidConfigsHolder.getInstance().getConfigs().size()));
        if (this.configs.isEnabled()) {
            this.setNextRaidSpawn();
        } else {
            log.info(RaidManager.class.getSimpleName() + ": Disabled.");
        }
    }

    
    public void reload() {
        this.clearTasksAndVars();
        RaidConfigsParser.getInstance().reload();
        RaidAndDropsParser.getInstance().reload();
        RaidLocationsParser.getInstance().reload();
        this.configs = (RaidConfigs)RaidConfigsHolder.getInstance().getConfigs().get(Rnd.get(RaidConfigsHolder.getInstance().getConfigs().size()));
        if (this.configs.isEnabled()) {
            this.setNextRaidSpawn();
        } else {
            log.info(RaidManager.class.getSimpleName() + ": Disabled.");
        }
    }

    public void setNextRaidSpawn() {
        long timeStart = System.currentTimeMillis();
        Calendar cal;
        (cal = Calendar.getInstance()).set(11, this.configs.getHour());
        cal.set(12, this.configs.getMinutes() + Rnd.get(this.configs.getRandomMins()));
        cal.set(5, this.configs.getDay());
        if (this.configs.getDay() > 0 && !this.configs.isDaily()) {
            while(cal.getTimeInMillis() <= timeStart) {
                cal.add(2, 1);
                cal.set(5, this.configs.getDay());
            }
        }

        if (this.configs.getDay() <= 0 && this.configs.isDaily()) {
            while(cal.getTimeInMillis() <= timeStart) {
                cal.add(5, 1);
            }
        }

        long nextRun;
        double var6 = (double)((nextRun = cal.getTimeInMillis() - timeStart) / 1000L % 60L);
        double redef;
        int minutes = (int)Math.floor((redef = ((double)(nextRun / 1000L) - var6) / 60.0D) % 60.0D);
        int hours = (int)Math.floor((redef = (redef - (double)minutes) / 60.0D) % 24.0D);
        int days = (int)Math.floor((redef - (double)hours) / 24.0D);
        log.info(this.getClass().getSimpleName() + ": Next Raid In: " + days + " day(s) " + hours + " hour(s) and " + minutes + " min(s).");
        this._spawnThread = ThreadPoolManager.getInstance().scheduleGeneral(new RaidSpawnTask(this._raid), nextRun);
    }

    
    public void onRaidDeath(L2RaidBossInstance rbInstance, L2PcInstance player) {
        if (this._raidGroup != null && rbInstance != null && this._raid != null && rbInstance.getObjectId() == this._raid.getObjectId()) {
            this.announceToAllOnline(this._raid.getName() + " has fallen in the hands of powerful warriors. Wait for next Event Boss spawn!");
            this.sendRadarInfo(false);
            this.rewardPlayer(rbInstance, player);
            this.clearTasksAndVars();
            this.configs = (RaidConfigs)RaidConfigsHolder.getInstance().getConfigs().get(Rnd.get(RaidConfigsHolder.getInstance().getConfigs().size()));
            this.setNextRaidSpawn();
        }
    }

    private void rewardPlayer(L2RaidBossInstance boss, L2PcInstance player) {
        for (RaidDrop drop : this._raidGroup.getRandomDrops(boss.getId())) {
            int itemCount = Rnd.get(drop.getMin(), drop.getMax());
            if (player.isInParty()) {
                for (L2PcInstance partMember : player.getParty().getMembers()){
                    long divide = (long)(itemCount / player.getParty().getMembers().size());
                    if(Util.checkIfInRange(1400, boss, partMember, true)){
                        if (divide < 1L) {
                            break;
                        }
                        this.giveReward(partMember, boss, drop.getItemId(), Math.max(divide, 1L), drop.getChance());
                    }
                }
            }else{
                this.giveReward(player, boss, drop.getItemId(), (long) itemCount, drop.getChance());
            }
        }
    }

    private void giveReward(L2PcInstance player, L2RaidBossInstance boss, int itemId, long itemCount, float chance) {
        if (player.getParty() == null) {
            if (boss.getLevel() < player.getLevel() && player.getLevel() - boss.getLevel() > maxLevelDifference) {
                return;
            }
        } else {
            for (L2PcInstance partyMember : player.getParty().getMembers()) {
                if (boss.getLevel() < partyMember.getLevel() && partyMember.getLevel() - boss.getLevel() > maxLevelDifference) {
                    return;
                }
            }
        }

        if ((float)Rnd.get(100) < chance) {
            if (Config.AUTO_LOOT_RAIDS) {
                player.addItem("drop", itemId, itemCount, player, true);
            } else {
                boss.dropItem(player, itemId, itemCount);
            }

            SystemMessage sm;
            (sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DIED_DROPPED_S3_S2)).addCharName(boss);
            sm.addItemName(itemId);
            sm.addLong(itemCount);
            boss.broadcastPacket(sm);
        }

    }

    
    public void checkRaidAttack(L2PcInstance player, L2RaidBossInstance boss) {
        if (this._currentLocation != null && Util.calculateDistance(boss.getLocation(), this._currentLocation.getLocation(), true, false) > 2000.0D) {
            Location loc = this._currentLocation.getLocation();
            if (!boss.isPorting()) {
                boss.setIsPorting(true);
                boss.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                boss.setTarget(boss);
                boss.disableAllSkills();
                boss.broadcastPacket(new MagicSkillUse(boss, 1050, 1, 700, 0));
                boss.sendPacket(new SetupGauge(0, 700));
                boss.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new TeleportBack(boss, loc), 700L));
                boss.forceIsCasting(10 + GameTimeController.getInstance().getGameTicks() + 7);
            }
        }

    }

    public void clearTasksAndVars() {
        this._raid = null;
        this._currentLocation = null;
        if (this._spawnThread != null) {
            this._spawnThread.cancel(true);
            this._spawnThread = null;
        }

        if (this._notifyThread != null) {
            this._notifyThread.cancel(true);
            this._notifyThread = null;
        }

        if (this._despawnThread != null) {
            this._despawnThread.cancel(true);
            this._despawnThread = null;
        }

    }

    public void sendRadarInfo(boolean updateMarker) {
        int x = this._raid.getLocation().getX();
        int y = this._raid.getLocation().getY();
        int z = this._raid.getLocation().getZ();

        for (L2PcInstance l2PcInstance : L2World.getInstance().getPlayers()) {
            L2PcInstance player;
            if ((player = l2PcInstance) != null && player.isOnline() && this._raid != null && !this._raid.getKnownList().getKnownPlayers().values().contains(player)) {
                if (updateMarker) {
                    player.getRadar().removeMarker(x, y, z);
                    player.getRadar().addMarker(x, y, z);
                } else {
                    player.getRadar().removeMarker(x, y, z);
                }
            }
        }

    }

    public void announceToAllOnline(String msg) {
        Broadcast.toAllOnlinePlayers(new CreatureSay(1, 20, "Raid Manager", msg));
    }

    protected static RaidManager instance;

    
    public static RaidManager getInstance() {
        if (instance == null)
            instance = new RaidManager();
        return instance;
    }
}
