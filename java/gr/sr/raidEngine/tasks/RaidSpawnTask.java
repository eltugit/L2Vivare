package gr.sr.raidEngine.tasks;

import gr.sr.raidEngine.RaidGroup;
import gr.sr.raidEngine.RaidLocation;
import gr.sr.raidEngine.manager.RaidManager;
import gr.sr.raidEngine.xml.dataHolder.RaidAndDropsHolder;
import gr.sr.raidEngine.xml.dataHolder.RaidLocationsHolder;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;
import l2r.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RaidSpawnTask implements Runnable {
    protected static final Logger _log = LoggerFactory.getLogger(RaidSpawnTask.class);
    private L2RaidBossInstance boss;

    public RaidSpawnTask(L2RaidBossInstance boss) {
        this.boss = boss;
    }

    public void run() {
        RaidManager.getInstance()._currentLocation = (RaidLocation)RaidLocationsHolder.getInstance().getLocations().get(Rnd.get(RaidLocationsHolder.getInstance().getLocations().size()));
        List raidGroups = RaidAndDropsHolder.getInstance().getRaidGroups();
        int raidGroupsSize = raidGroups.size();
        RaidGroup raidGroup = null;
        while(raidGroup == null) {
            double chance = Rnd.get() * 100.0D;
            for(int i = 0; i < raidGroupsSize; ++i) {
                RaidGroup temp = (RaidGroup) raidGroups.get(i);
                if(temp.getGroupChance() < chance){
                    raidGroup = temp;
                }
            }
        }

        int bossId = 0;
        ArrayList raidGroupRaids = new ArrayList(raidGroup.getRaids().keySet());
        if (raidGroupRaids.size() <= 0) {
            RaidManager.getInstance().clearTasksAndVars();
            RaidManager.getInstance().setNextRaidSpawn();
        } else {
            while(bossId == 0 || bossId == RaidManager.getInstance()._lastRaidId) {
                bossId = (Integer)raidGroupRaids.get(Rnd.get(raidGroupRaids.size()));
                if (raidGroupRaids.size() == 1) {
                    break;
                }
            }

            this.boss = new L2RaidBossInstance(NpcTable.getInstance().getTemplate(bossId));
            this.boss.setHeading(0);
            this.boss.setIsEventRaid(true);
            this.boss.setTitle("Event Boss");
            this.boss.spawnMe(RaidManager.getInstance()._currentLocation.getLocation().getX(), RaidManager.getInstance()._currentLocation.getLocation().getY(), RaidManager.getInstance()._currentLocation.getLocation().getZ());
            String typeName = raidGroup.getTypeName();
            //TODO GABRIEL
            switch(typeName) {
                case "Strong":
                    // Status Edit???
                    break;
                case "Weak":
                    // Status Edit???
                    break;
                case "Super Strong":
                    // Status Edit???
            }

            this.boss.setCurrentHpMp((double)this.boss.getMaxHp(), (double)this.boss.getMaxMp());
            RaidManager.getInstance()._raid = this.boss;
            RaidManager.getInstance()._raidGroup = raidGroup;
            RaidManager.getInstance()._lastRaidId = bossId;
            long raidDuration = RaidManager.getInstance().configs.getDuration();
            double raidChance = (double)(raidDuration / 1000L % 60L);
            double time;
            int minutes = (int)Math.floor((time = ((double)(raidDuration / 1000L) - raidChance) / 60.0D) % 60.0D);
            int hours = (int)Math.floor((time - (double)minutes) / 60.0D % 24.0D);
            RaidManager.getInstance().announceToAllOnline(raidGroup.getTypeName() + " Raid Boss: " + this.boss.getName() + " has spawned in " + RaidManager.getInstance()._currentLocation.getName() + " and will be there for the next " + hours + " hours " + minutes + " mins! Open your map and use .findraid in order to find raid location.");
            _log.info(this.getClass().getSimpleName() + " : Special Raid(" + this.boss.getName() + ") spawned in " + RaidManager.getInstance()._currentLocation.getName());
            RaidManager.getInstance()._notifyThread = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new RaidNotifyTask(this.boss), RaidManager.getInstance().configs.getNotifyDelay(), RaidManager.getInstance().configs.getNotifyDelay());
            RaidManager.getInstance()._despawnThread = ThreadPoolManager.getInstance().scheduleGeneral(new RaidDespawnTask(this.boss), RaidManager.getInstance().configs.getDuration());
        }
    }
}
