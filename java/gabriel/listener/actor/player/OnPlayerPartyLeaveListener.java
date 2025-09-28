package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnPlayerPartyLeaveListener extends PlayerListener {
    public void onPartyLeave(L2PcInstance player);
}
