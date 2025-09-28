package gabriel.listener.actor.player;


import gabriel.listener.PlayerListener;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public interface OnTeleportListener extends PlayerListener {
    public void onTeleport(L2PcInstance player, int x, int y, int z, int instanceId);
}
