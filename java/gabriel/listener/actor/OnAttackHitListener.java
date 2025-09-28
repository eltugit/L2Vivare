package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;

public interface OnAttackHitListener extends CharListener {
    public void onAttackHit(L2Character actor, L2Character attacker);
}
