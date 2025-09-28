package gr.sr.player;


import l2r.gameserver.model.actor.instance.L2PcInstance;


public class PcExtention {
    private L2PcInstance player = null;

    public PcExtention(L2PcInstance p) {
        this.player = p;
    }


    protected L2PcInstance getChar() {
        return this.player;
    }

    public void destroy() {
        this.player = null;
    }
}
