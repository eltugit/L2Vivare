package gabriel.balancer;


import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;


public class BalanceGabrielHandler {

    private boolean isPcInstance(L2Character attacker, L2Character target) {
        return attacker instanceof L2PcInstance && target instanceof L2PcInstance;
    }


    public double calcBlowDamage(L2Character attacker, L2Character target, double damage) {
        if (!isPcInstance(attacker, target)) return damage;

        int atakerclasid = ((L2PcInstance) attacker).getClassId().getId();
        int targetclasid = ((L2PcInstance) target).getClassId().getId();

        if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
            if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {

                float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getBlowDamage();

                damage *= customDMG;

            }

            if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {

                float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getBlowDamage();

                damage *= customDMG;
            }
        } else {
            if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {

                float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getBlowDamage();

                damage *= customDMG;

            }

            if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {

                float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getBlowDamage();

                damage *= customDMG;
            }
        }
        return damage;
    }


    public double calcPhysDam(L2Character attacker, L2Character target, double damage, L2Skill skill, boolean crit) {
        if (!isPcInstance(attacker, target)) return damage;
        int atakerclasid = ((L2PcInstance) attacker).getClassId().getId();
        int targetclasid = ((L2PcInstance) target).getClassId().getId();
        if (skill == null) {
            if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getAutoAttackDamage();
                    damage *= customDMG;
                }
                if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getAutoAttackDamage();
                    damage *= customDMG;
                }
            } else {
                if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getAutoAttackDamage();
                    damage *= customDMG;
                }
                if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getAutoAttackDamage();
                    damage *= customDMG;
                }
            }
        } else {
            if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getSkillAttackDamage();
                    damage *= customDMG;
                }
                if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getSkillAttackDamage();
                    damage *= customDMG;
                }
            } else {
                if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getSkillAttackDamage();
                    damage *= customDMG;
                }
                if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getSkillAttackDamage();
                    damage *= customDMG;
                }
            }
        }

        if (crit) {
            if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getPhysCriticalDamage();
                    damage *= customDMG;
                }
                if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getPhysCriticalDamage();
                    damage *= customDMG;
                }
            } else {
                if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getPhysCriticalDamage();
                    damage *= customDMG;
                }
                if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getPhysCriticalDamage();
                    damage *= customDMG;
                }
            }
        }
        return damage;
    }

    public double calcMagicDam(L2Character attacker, L2Character target, double damage, boolean mcrit, boolean isMagic) {
        if (!isPcInstance(attacker, target)) return damage;
        int atakerclasid = ((L2PcInstance) attacker).getClassId().getId();
        int targetclasid = ((L2PcInstance) target).getClassId().getId();

        if (mcrit) {
            if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getMagicCriticalDamage();
                    damage *= customDMG;
                }
                if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getMagicCriticalDamage();
                    damage *= customDMG;
                }
            } else {
                if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getMagicCriticalDamage();
                    damage *= customDMG;
                }
                if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getMagicCriticalDamage();
                    damage *= customDMG;
                }
            }
        } else {
            if (isMagic) {
                if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                    if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                        float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getMagicAttackDamage();
                        damage *= customDMG;
                    }
                    if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                        float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getMagicAttackDamage();
                        damage *= customDMG;
                    }
                } else {
                    if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                        float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getMagicAttackDamage();
                        damage *= customDMG;
                    }
                    if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                        float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getMagicAttackDamage();
                        damage *= customDMG;
                    }
                }
            } else {
                if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                    if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                        float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getSkillAttackDamage();
                        damage *= customDMG;
                    }
                    if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                        float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getSkillAttackDamage();
                        damage *= customDMG;
                    }
                } else {
                    if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                        float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getSkillAttackDamage();
                        damage *= customDMG;
                    }
                    if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                        float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getSkillAttackDamage();
                        damage *= customDMG;
                    }
                }

            }
        }
        return damage;
    }

    public double calBackStabDam(L2Character attacker, L2Character target, double proximityBonus) {
        if (!isPcInstance(attacker, target)) return proximityBonus;
        int atakerclasid = ((L2PcInstance) attacker).getClassId().getId();
        int targetclasid = ((L2PcInstance) target).getClassId().getId();
        if (attacker.isBehindTarget()) {
            proximityBonus = 1.2;
            if (((L2PcInstance) attacker).isInOlympiadMode() && ((L2PcInstance) target).isInOlympiadMode()) {
                if (ClassBalanceOly.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getMultiplier(atakerclasid, targetclasid).getBackstabDamage();
                    proximityBonus *= customDMG;
                }
                if (ClassBalanceOly.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalanceOly.getInstance().getOverallMultiplier(atakerclasid).getBackstabDamage();
                    proximityBonus *= customDMG;
                }
            } else {
                if (ClassBalance.getInstance().existInBalanceList(atakerclasid, targetclasid)) {
                    float customDMG = ClassBalance.getInstance().getMultiplier(atakerclasid, targetclasid).getBackstabDamage();
                    proximityBonus *= customDMG;
                }
                if (ClassBalance.getInstance().existInOverallList(atakerclasid)) {
                    float customDMG = ClassBalance.getInstance().getOverallMultiplier(atakerclasid).getBackstabDamage();
                    proximityBonus *= customDMG;
                }
            }
        }
        return proximityBonus;
    }

    protected static BalanceGabrielHandler instance;

    public static BalanceGabrielHandler getInstance() {
        if (instance == null)
            instance = new BalanceGabrielHandler();
        return instance;
    }
}
