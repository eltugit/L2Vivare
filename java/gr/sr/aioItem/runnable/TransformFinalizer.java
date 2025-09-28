package gr.sr.aioItem.runnable;


import l2r.gameserver.model.actor.instance.L2PcInstance;


public class TransformFinalizer implements Runnable {
    private final L2PcInstance player;

    public TransformFinalizer(L2PcInstance player) {
        this.player = player;
    }

    public void run() {
        this.player.untransform();
    }
}
