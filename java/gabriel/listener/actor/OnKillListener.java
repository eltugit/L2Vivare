package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;

public interface OnKillListener extends CharListener {
    public void onKill(L2Character actor, L2Character victim);

    public boolean ignorePetOrSummon();
}
