package gabriel.listener.actor;


import gabriel.listener.CharListener;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.skills.L2Skill;

public interface OnCurrentHpDamageListener extends CharListener {
    public void onCurrentHpDamage(L2Character actor, double damage, L2Character attacker, L2Skill skill);
}
