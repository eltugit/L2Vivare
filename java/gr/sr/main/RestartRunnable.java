package gr.sr.main;

import gr.sr.configsEngine.configs.impl.AutoRestartConfigs;
import l2r.gameserver.Shutdown;
import l2r.gameserver.model.actor.instance.L2PcInstance;

class RestartRunnable implements Runnable {
    Restart restart;

    RestartRunnable(Restart restart) {
        this.restart = restart;
    }

    public final void run() {
        Restart._log.info("Start automated restart GameServer.");
        Shutdown.getInstance().startShutdown((L2PcInstance)null, AutoRestartConfigs.RESTART_SECONDS, true);
    }
}
