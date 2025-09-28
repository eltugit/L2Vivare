package gabriel.restrictions;


import gabriel.config.GabConfig;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2EffectType;
import l2r.gameserver.model.skills.L2Skill;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PVPInstanceRestrictions implements GabrielRestrictions {
    private static final class SingletonHolder {
        private static final PVPInstanceRestrictions INSTANCE = new PVPInstanceRestrictions();
    }

    public static PVPInstanceRestrictions getInstance() {
        return SingletonHolder.INSTANCE;
    }

    PVPInstanceRestrictions() {
    }

    public boolean checkInside(L2Character activeChar, L2Character target) {
        return activeChar.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID && target.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID;
    }

    private boolean isL2PcInstance(L2Character a, L2Character b) {
        return (a instanceof L2PcInstance) && (b instanceof L2PcInstance);
    }

    @Override
    public boolean isFriend(L2Character activeChar, L2Character target) {
        if (checkInside(activeChar, target)) {
            if (PvPZoneManager.getInstance().isAnnonym())
                return false;
            else if (BasicGabsRestrictions.checkSameParty(activeChar, target))
                return true;
            else if (BasicGabsRestrictions.checkSameClan(activeChar, target)) {
                if (PvPZoneManager.getInstance().isTvTvTvT() && BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return true;

                if (PvPZoneManager.getInstance().isTvT() && BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return true;
                return false;
            }
            else if (PvPZoneManager.getInstance().isTvTvTvT() && BasicGabsRestrictions.checkSame4Team(activeChar, target))
                return true;
            else if (PvPZoneManager.getInstance().isTvT() && BasicGabsRestrictions.checkSameTeam(activeChar, target))
                return true;
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
                System.out.println("GABS: PVPInstanceRestrictions.doAttack called from inside InstanceZone");
            if (PvPZoneManager.isNormal()) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                    return false; //Allow attack clan in normal PvPZoneManager to activate clan buff
            }
            if (PvPZoneManager.isTvT()) {
                if (BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return true;
            }
            if (PvPZoneManager.isTvTvTvT()) {
                if (BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return true;
            }
            if (PvPZoneManager.isAnnonym()) {
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
                System.out.println("GABS: PVPInstanceRestrictions.beginCast called from inside InstanceZone");
            if (PvPZoneManager.isNormal()) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                    if (forceUse)
                        return false;
                    else
                        return isOffensive;
                else if (!BasicGabsRestrictions.checkSameClan(activeChar, target)) {
                    if (BasicGabsRestrictions.checkSameParty(activeChar, target) && !isOffensive)
                        return false;//allow buffs in party if not from the sale clan
                    return !isOffensive;
                }
            }
            if (PvPZoneManager.isTvT()) {
                if (BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return isOffensive;
                else if (!BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return !isOffensive;
            }
            if (PvPZoneManager.isTvTvTvT()) {
                if (BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return isOffensive;
                else if (!BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return !isOffensive;
            }
            if (PvPZoneManager.isAnnonym()) {
                return false;
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
            if (PvPZoneManager.isNormal()) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                    return false;
            }
            if (PvPZoneManager.isTvT()) {
                if (BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return false;
            }
            if (PvPZoneManager.isTvTvTvT()) {
                if (BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return false;
            }
            if (PvPZoneManager.isAnnonym()) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
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
        if ((activeChar instanceof L2PcInstance) && activeChar.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID) {
            if (GabConfig.DEBUG_GABS)
                System.out.println("GABS: PVPInstanceRestrictions.useMagic called from inside InstanceZone");
            return PvPZoneManager.isAnnonym();
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
    public boolean checkUseMagicConditionsForce(L2Character activeChar) {
        if ((activeChar instanceof L2PcInstance) && activeChar.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID) {
            if (GabConfig.DEBUG_GABS)
                System.out.println("GABS: PVPInstanceRestrictions.checkUseMagicConditions called from inside InstanceZone");
            return PvPZoneManager.isAnnonym();
        }
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
                System.out.println("GABS: PVPInstanceRestrictions.checkForAreaOffensiveSkills called from inside InstanceZone");
            if (PvPZoneManager.isNormal() && !skill.hasEffectType(L2EffectType.RESURRECTION)) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                    return true;
            }
            if (PvPZoneManager.isTvT()) {
                if (BasicGabsRestrictions.checkSameTeam(activeChar, target))
                    return true;
            }
            if (PvPZoneManager.isTvTvTvT()) {
                if (BasicGabsRestrictions.checkSame4Team(activeChar, target))
                    return true;
            }
            if (PvPZoneManager.isAnnonym()) {
                if (BasicGabsRestrictions.checkSameClan(activeChar, target))
                    return false;

                if (!activeChar.checkPvpSkill(target, skill, castSummon))
                    return false;
            }
        }
        return false;
    }
}