package gr.sr.javaBuffer.runnable;

import gr.sr.javaBuffer.BufferMenuCategories;
import gr.sr.javaBuffer.JavaBufferBypass;
import gr.sr.javaBuffer.PlayerMethods;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class BuffSaver implements Runnable {
    private final L2PcInstance player;
    private final int mode;
    private final BufferMenuCategories categories;
    private final String profile;
    private final int buffId;

    public BuffSaver(L2PcInstance p, BufferMenuCategories categ, String prof, int buffId, int mode) {
        this.player = p;
        this.categories = categ;
        this.profile = prof;
        this.buffId = buffId;
        this.mode = mode;
    }

    public void run() {
        PlayerMethods.addBuffToProfile(this.profile, this.buffId, this.player);
        JavaBufferBypass.callBuffToAdd(this.categories, this.player, this.profile, this.mode);
    }
}
