package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;

public interface OnDeathListener extends CharListener {
    public void onDeath(L2Character actor, L2Character killer);
}
