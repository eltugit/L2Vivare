package gr.sr.raidEngine.tasks;

import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;

public class TeleportBack implements Runnable {
    private L2Character character;
    private final Location location;

    public TeleportBack(L2Character character, Location loc) {
        this.character = character;
        this.location = loc;
    }

    public void run() {
        this.character.teleToLocation(this.location, true);
        this.character.setIsCastingNow(false);
        this.character.setCurrentHpMp((double)this.character.getMaxHp(), (double)this.character.getMaxMp());
        this.character.enableAllSkills();
        this.character.setIsPorting(false);
    }
}
