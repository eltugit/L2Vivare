package gabriel.restrictions;


import gabriel.config.GabConfig;
import gabriel.events.castleSiegeKoth.CSKOTHEvent;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

import java.util.Arrays;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
//Bota ai os ids e testa pf
public class CSRestrictions implements GabrielRestrictions {
    private static final class SingletonHolder {
        private static final CSRestrictions INSTANCE = new CSRestrictions();
    }
    public static CSRestrictions getInstance() {
        return SingletonHolder.INSTANCE;
    }

    CSRestrictions() {
    }

    public boolean checkInside(L2Character activeChar, L2Character target) {
        return CSKOTHEvent.isPlayerParticipant(activeChar.getObjectId()) && CSKOTHEvent.isPlayerParticipant(target.getObjectId());
    }

    private boolean isL2PcInstance(L2Character a, L2Character b) {
        return (a instanceof L2PcInstance) && (b instanceof L2PcInstance);
    }

    @Override
    public boolean isFriend(L2Character activeChar, L2Character target) {
        if (checkInside(activeChar, target)) {
            if (BasicGabsRestrictions.checkSameParty(activeChar, target)) {
                return true;
            }
            else if (activeChar.getTeam() == target.getTeam()) {
                return true;
            }
            else if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                return false;
        }
        return false;
    }

    /**
     * @param activeChar
     * @param target
     * @return if false = can attack / if true cannot attack
     */
    @Override
    public boolean doAttack(L2Character activeChar, L2Character target) {

        if (isL2PcInstance(activeChar, target) && checkInside(activeChar, target)) {
            if (GabConfig.DEBUG_GABS)
                System.out.println("GABS: TournamentRestrictions.doAttack called from inside InstanceZone");
            if (activeChar.getTeam() != target.getTeam()) {
                return false;
            }
        }
        return false;
    }

    /**
     * @param activeChar
     * @param target
     * @return if false = can use skill / if true cannot use skill
     */
    @Override
    public boolean beginCast(L2Character activeChar, L2Character target, L2Skill skill) {
        if (skill.isRetailInInstanceCustom())
            return false;
        boolean isOffensive = skill.isOffensive();
        boolean forceUse = skill.forceUse;
        if (isL2PcInstance(activeChar, target) && checkInside(activeChar, target) && (activeChar != target)) {
            if (GabConfig.DEBUG_GABS)
                System.out.println("GABS: TournamentRestrictions.beginCast called from inside InstanceZone");
            if (activeChar.getTeam() != target.getTeam()) {
                return !isOffensive;
            }
        }
        return false;
    }

    /**
     * @param activeChar
     * @param target
     * @return if true, is autoAttackable (attack without ctrl)
     */
    @Override
    public boolean isAutoAttackable(L2PcInstance activeChar, L2PcInstance target) {
        if (isL2PcInstance(activeChar, target) && checkInside(activeChar, target)) {
            if (activeChar.getTeam() != target.getTeam()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return forceUse on skill (Ctrl pressed) Attack even if clan or ally
     *
     * @param activeChar
     * @return true if forceUse, false if not
     */
    @Override
    public boolean useMagic(L2Character activeChar) {
        return false;
    }

    /**
     * Return forceUse on skill (Ctrl pressed) Attack even if clan or ally
     *
     * @param activeChar
     * @return true if forceUse, false if not
     */
    @Override
    public boolean checkUseMagicConditionsForce(L2Character activeChar) {
        return false;
    }

    /**
     * Return false will do nothing
     *
     * @param activeChar
     * @param target
     * @return
     */
    @Override
    public boolean checkUseMagicConditions(L2Character activeChar, L2Character target) {
        if (isL2PcInstance(activeChar, target)) {
            if (checkInside(activeChar, target)) {
                return false;
            }
        }
        return false;
    }

    /**
     * Checks if the player in the area can get damaged by the area skill
     *
     * @param activeChar
     * @param target
     * @param skill
     * @param castSummon
     * @return false if can attack with area skills if true cannot get affected by area skills
     */
    @Override
    public boolean checkForAreaOffensiveSkills(L2PcInstance activeChar, L2PcInstance target, L2Skill skill, boolean castSummon) {
        if (skill.isRetailInInstanceCustom())
            return false;
        if (isL2PcInstance(activeChar, target) && checkInside(activeChar, target)) {
            if (GabConfig.DEBUG_GABS)
                System.out.println("GABS: TournamentRestrictions.checkForAreaOffensiveSkills called from inside InstanceZone");
            if (activeChar.getTeam() != target.getTeam()) {
                return false;
            }
        }
        return false;
    }
}