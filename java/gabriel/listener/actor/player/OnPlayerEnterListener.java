package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnPlayerEnterListener extends PlayerListener {
    public void onPlayerEnter(L2PcInstance player);
}
