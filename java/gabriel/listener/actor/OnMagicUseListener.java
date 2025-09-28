package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;

public interface OnMagicUseListener extends CharListener {
    public void onMagicUse(L2Character actor, L2Skill skill, L2Character target, boolean alt);
}
