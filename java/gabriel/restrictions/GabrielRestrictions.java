package gabriel.restrictions;


import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public interface GabrielRestrictions {
    //check inside
    boolean checkInside(L2Character activeChar, L2Character target);

    //Ataque Padrão
    boolean doAttack(L2Character activeChar, L2Character target);

    //Ataque skill um target
    boolean beginCast(L2Character activeChar, L2Character target, L2Skill skill);

    //attackable with ctrl
    boolean isAutoAttackable(L2PcInstance activeChar, L2PcInstance target);

    //Handles forceUse
    boolean useMagic(L2Character activeChar);

    //check if can cast skill, this will run before beginCast Method
    boolean checkUseMagicConditions(L2Character activeChar, L2Character target);

    //Handles forceUse
    boolean checkUseMagicConditionsForce(L2Character activeChar);

    //atk skill area
    boolean checkForAreaOffensiveSkills(L2PcInstance activeChar, L2PcInstance target, L2Skill skill, boolean castSummon);

    public boolean isFriend(L2Character activeChar, L2Character target);

}
