package gabriel.restrictions;


import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class GlobalGabrielRestrictions implements GabrielRestrictions {
    private List<GabrielRestrictions> restrictionsList;

    private static final class SingletonHolder {
        private static final GlobalGabrielRestrictions INSTANCE = new GlobalGabrielRestrictions();
    }


    public static GlobalGabrielRestrictions getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public GlobalGabrielRestrictions() {
        restrictionsList = new ArrayList<>();
        restrictionsList.add(new PVPInstanceRestrictions());
        restrictionsList.add(new TournamentRestrictions());
        restrictionsList.add(new CSRestrictions());
    }

    @Override

    public boolean checkInside(L2Character activeChar, L2Character target) {
        return false;
    }

    @Override

    public boolean doAttack(L2Character activeChar, L2Character target) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.doAttack(activeChar, target))
                return true;
        }
        return false;
    }

    @Override
    public boolean beginCast(L2Character activeChar, L2Character target, L2Skill skill) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.beginCast(activeChar, target, skill))
                return true;
        }
        return false;
    }

    @Override

    public boolean isAutoAttackable(L2PcInstance activeChar, L2PcInstance target) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.isAutoAttackable(activeChar, target))
                return true;
        }
        return false;
    }

    @Override

    public boolean useMagic(L2Character activeChar) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.useMagic(activeChar))
                return true;
        }
        return false;
    }

    @Override

    public boolean checkUseMagicConditions(L2Character activeChar, L2Character target) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.checkUseMagicConditions(activeChar, target))
                return true;
        }
        return false;
    }

    @Override
    public boolean checkUseMagicConditionsForce(L2Character activeChar) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.checkUseMagicConditionsForce(activeChar))
                return true;
        }
        return false;
    }

    @Override
    public boolean checkForAreaOffensiveSkills(L2PcInstance activeChar, L2PcInstance target, L2Skill skill, boolean castSummon) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.checkForAreaOffensiveSkills(activeChar, target, skill, castSummon))
                return true;
        }
        return false;
    }

    @Override
    public boolean isFriend(L2Character activeChar, L2Character target) {
        for (GabrielRestrictions gabrielRestrictions : restrictionsList) {
            if (gabrielRestrictions.isFriend(activeChar, target))
                return true;
        }
        return false;
    }


}
