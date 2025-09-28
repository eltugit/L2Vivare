package gabriel.listener.actor.npc;


import gabriel.listener.NpcListener;
import l2r.gameserver.model.actor.instance.L2NpcInstance;

public interface OnSpawnListener extends NpcListener {
    public void onSpawn(L2NpcInstance actor);
}
