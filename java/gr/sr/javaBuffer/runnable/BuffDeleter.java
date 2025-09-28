package gr.sr.javaBuffer.runnable;


import gr.sr.javaBuffer.PlayerMethods;
import gr.sr.javaBuffer.buffItem.dynamicHtmls.GenerateHtmls;
import l2r.gameserver.model.actor.instance.L2PcInstance;


public class BuffDeleter implements Runnable {
    private final L2PcInstance player;
    private final int mode;
    private final String profile;
    private final int buffId;

    public BuffDeleter(L2PcInstance player, String profile, int buffId, int mode) {
        this.player = player;
        this.profile = profile;
        this.buffId = buffId;
        this.mode = mode;
    }

    public void run() {
        PlayerMethods.delBuffFromProfile(this.profile, this.buffId, this.player);
        switch(this.mode) {
            case 0:
                GenerateHtmls.showBuffsToDelete(this.player, this.profile, "removeBuffs");
                return;
            case 1:
                gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToDelete(this.player, this.profile, "removeBuffs");
                return;
            default:
                gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToDelete(this.player, this.profile, "removeBuffs", this.mode);
        }
    }
}
