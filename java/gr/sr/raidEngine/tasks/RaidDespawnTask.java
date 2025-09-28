package gr.sr.raidEngine.tasks;

import gr.sr.raidEngine.manager.RaidManager;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;

public class RaidDespawnTask implements Runnable {
    private final L2RaidBossInstance boss;

    public RaidDespawnTask(L2RaidBossInstance boss) {
        this.boss = boss;
    }

    public void run() {
        if (this.boss != null) {
            RaidManager.getInstance().announceToAllOnline(this.boss.getName() + " wasn't killed in time and escaped. Wait for next Event Raid!");
            this.boss.deleteMe();
        }

        RaidManager.getInstance().clearTasksAndVars();
        RaidManager.getInstance().setNextRaidSpawn();
    }
}
