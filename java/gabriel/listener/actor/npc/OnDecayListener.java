package gabriel.listener.actor.npc;


import gabriel.listener.NpcListener;
import l2r.gameserver.model.actor.instance.L2NpcInstance;

public interface OnDecayListener extends NpcListener {
    public void onDecay(L2NpcInstance actor);
}
