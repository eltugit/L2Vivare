package gr.sr.raidEngine.tasks;

import gr.sr.raidEngine.manager.RaidManager;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;

public class RaidNotifyTask implements Runnable {
    private final L2RaidBossInstance boss;

    public RaidNotifyTask(L2RaidBossInstance boss) {
        this.boss = boss;
    }

    public void run() {
        if (this.boss != null && RaidManager.getInstance()._currentLocation != null) {
            RaidManager.getInstance().announceToAllOnline(this.boss.getName() + " is in " + RaidManager.getInstance()._currentLocation.getName() + ", so come and kill it now! Open your map and use .findraid in order to find raid location.");
        }
    }
}
