package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnPlayerPartyInviteListener extends PlayerListener {
    public void onPartyInvite(L2PcInstance player);
}
