package gr.sr.logsViewer.runnable;

import gr.sr.logsViewer.LogsViewer;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class Capture implements Runnable {
    private final L2PcInstance player;
    private final String log;

    public Capture(L2PcInstance p, String log) {
        this.player = p;
        this.log = log;
    }

    public void run() {
        LogsViewer.startLogViewer(this.player, this.log);
    }
}
