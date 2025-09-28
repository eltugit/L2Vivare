package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;

public interface OnMagicHitListener extends CharListener {
    public void onMagicHit(L2Character actor, L2Skill skill, L2Character caster);
}
