//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.javaBuffer.runnable;

import gr.sr.configsEngine.configs.impl.BufferConfigs;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BuffDelay implements Runnable {
    private L2PcInstance player;
    public static Set<L2PcInstance> _delayers = ConcurrentHashMap.newKeySet();

    public BuffDelay(L2PcInstance player) {
        this.player = player;
        _delayers.add(this.player);
    }

    public void run() {
        double var1 = (double)System.currentTimeMillis();

        while((double)System.currentTimeMillis() < var1 + BufferConfigs.BUFFER_DELAY * 1000.0D) {
        }

        if (_delayers.contains(this.player)) {
            _delayers.remove(this.player);
        }

    }
}
