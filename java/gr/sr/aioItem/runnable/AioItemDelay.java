package gr.sr.aioItem.runnable;

import gr.sr.configsEngine.configs.impl.AioItemsConfigs;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AioItemDelay implements Runnable {
    private L2PcInstance player;
    public static Set<L2PcInstance> _delayers = ConcurrentHashMap.newKeySet();

    public AioItemDelay(L2PcInstance var1) {
        this.player = var1;
        _delayers.add(this.player);
    }

    public void run() {
        double delay = (double)System.currentTimeMillis();

        while((double)System.currentTimeMillis() < delay + AioItemsConfigs.AIO_DELAY * 1000.0D) {
        }

        if (_delayers.contains(this.player)) {
            _delayers.remove(this.player);
        }

    }
}
