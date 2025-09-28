package gr.sr.antibotEngine.runnable;

import gr.sr.antibotEngine.AntibotSystem;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class JailTimer implements Runnable {
    private L2PcInstance player;

    public JailTimer(L2PcInstance p) {
        this.player = p;
    }

    public void run() {
        if (this.player.isFarmBot()) {
            AntibotSystem.jailPlayer(this.player, "time");
        }

    }
}
