package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnPlayerExitListener extends PlayerListener {
    public void onPlayerExit(L2PcInstance player);
}
