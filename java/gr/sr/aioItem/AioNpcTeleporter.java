package gr.sr.aioItem;

import l2r.gameserver.model.actor.instance.L2PcInstance;

final class AioNpcTeleporter implements Runnable {
    private L2PcInstance player;
    private final int xLoc;
    private final int yLoc;
    private final int zLoc;

    AioNpcTeleporter(L2PcInstance player, int xLoc, int yLoc, int zLoc) {
        this.player = player;
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        this.zLoc = zLoc;
    }

    public final void run() {
        this.player.teleToLocation(this.xLoc, this.yLoc, this.zLoc, true);
        this.player.setIsCastingNow(false);
        this.player.enableAllSkills();
    }
}
