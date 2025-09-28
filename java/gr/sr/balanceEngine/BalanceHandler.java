package gr.sr.balanceEngine;


import gr.sr.configsEngine.configs.impl.FormulasConfigs;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.items.type.WeaponType;
import l2r.gameserver.model.skills.L2Skill;


public class BalanceHandler {
    public BalanceHandler() {
    }


    public double calc(L2Character attacker, L2Character target, L2Skill skill, double damage, boolean isMagic) {
        if (isMagic) {
            if (attacker.isPlayer()) {
                if (target.isPlayer()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_PLAYER_VS_PLAYER;
                } else if (target.isMonster()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_PLAYER_VS_MOB;
                }
            } else if (attacker.isSummon()) {
                if (target.isPlayer()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_SUMMON_VS_PLAYER;
                } else if (target.isMonster()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_SUMMON_VS_MOB;
                }
            } else if (attacker.isMonster()) {
                if (target.isPlayer()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_MOB_VS_PLAYER;
                } else if (target.isMonster()) {
                    damage *= (double)FormulasConfigs.ALT_MAGIC_DAMAGE_MOB_VS_MOB;
                }
            }
        } else if (attacker.isPlayer()) {
            L2Weapon playerWeapon;
            if ((playerWeapon = attacker.getActiveWeaponItem()) != null) {
                if (target.isPlayer()) {
                    if (skill != null) {
                        getInstance();
                        damage = calcDmg(target.getActingPlayer(), playerWeapon.getItemType(), damage);
                    } else {
                        getInstance();
                        damage = calcDmgOnHit(target.getActingPlayer(), playerWeapon.getItemType(), damage);
                    }
                } else if (target.isSummon()) {
                    if (skill != null) {
                        getInstance();
                        damage = calcDmg(target.getActingPlayer(), playerWeapon.getItemType(), damage);
                    } else {
                        getInstance();
                        damage = calcDmgOnHit(target.getActingPlayer(), playerWeapon.getItemType(), damage);
                    }
                } else if (target.isMonster()) {
                    damage *= (double)FormulasConfigs.ALT_PHYSICAL_DAMAGE_PLAYER_VS_MOB;
                }
            }
        } else if (attacker.isSummon()) {
            if (target.isPlayer()) {
                damage *= (double)FormulasConfigs.ALT_PHYSICAL_DAMAGE_SUMMON_VS_PLAYER;
            } else if (target.isMonster()) {
                damage *= (double)FormulasConfigs.ALT_PHYSICAL_DAMAGE_SUMMON_VS_MOB;
            }
        } else if (attacker.isMonster()) {
            if (target.isPlayer()) {
                damage *= (double)FormulasConfigs.ALT_PHYSICAL_DAMAGE_MOB_VS_PLAYER;
            } else if (target.isMonster()) {
                damage *= (double)FormulasConfigs.ALT_PHYSICAL_DAMAGE_MOB_VS_MOB;
            }
        }

        return damage;
    }

    private static double calcDmg(L2PcInstance player, WeaponType weaponType, double damage) {
        if (player.getActiveChestArmorItem() != null) {
            switch(weaponType) {
                case DAGGER:
                case DUALDAGGER:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_ROBE;
                    }
                    break;
                case BOW:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_ROBE;
                    }
                    break;
                case CROSSBOW:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_ROBE;
                    }
                    break;
                case POLE:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_ROBE;
                    }
                    break;
                case BLUNT:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_ROBE;
                    }
                    break;
                case SWORD:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_ROBE;
                    }
                    break;
                case DUAL:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_ROBE;
                    }
                    break;
                case DUALFIST:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_ROBE;
                    }
                    break;
                case ANCIENTSWORD:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_ROBE;
                    }
                    break;
                case RAPIER:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_HEAVY;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_LIGHT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_ROBE;
                    }
            }
        }

        return damage;
    }

    private static double calcDmgOnHit(L2PcInstance player, WeaponType weaponType, double damage) {
        if (player.getActiveChestArmorItem() != null) {
            switch(weaponType) {
                case DAGGER:
                case DUALDAGGER:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DAGGER_DMG_VS_ROBE_HIT;
                    }
                    break;
                case BOW:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_ROBE_HIT;
                    }
                    break;
                case CROSSBOW:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_CROSSBOW_DMG_VS_ROBE_HIT;
                    }
                    break;
                case POLE:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_POLE_DMG_VS_ROBE_HIT;
                    }
                    break;
                case BLUNT:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_BLUNT_DMG_VS_ROBE_HIT;
                    }
                    break;
                case SWORD:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_SWORD_DMG_VS_ROBE_HIT;
                    }
                    break;
                case DUAL:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_ROBE_HIT;
                    }
                    break;
                case DUALFIST:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_DUAL_FIST_DMG_VS_ROBE_HIT;
                    }
                    break;
                case ANCIENTSWORD:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_ANCIENT_DMG_VS_ROBE_HIT;
                    }
                    break;
                case RAPIER:
                    if (player.isWearingHeavyArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_HEAVY_HIT;
                    }

                    if (player.isWearingLightArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_LIGHT_HIT;
                    }

                    if (player.isWearingMagicArmor()) {
                        damage /= (double)FormulasConfigs.ALT_RAPIER_DMG_VS_ROBE_HIT;
                    }
            }
        }

        return damage;
    }

    public double calcSkillDamageDependsOnClass(L2PcInstance player, L2PcInstance target, WeaponType weaponType, double damage) {
        ClassId classId = player.getClassId();
        player.getClassId().getId();
        switch(classId) {
            case duelist:
                if (target.isWearingHeavyArmor()) {
                    damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_HEAVY;
                }

                if (target.isWearingLightArmor()) {
                    damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_LIGHT;
                }

                if (target.isWearingMagicArmor()) {
                    damage /= (double)FormulasConfigs.ALT_DUAL_DMG_VS_ROBE;
                }
                break;
            case phoenixKnight:
                if (target.isWearingHeavyArmor()) {
                    damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_HEAVY;
                }

                if (target.isWearingLightArmor()) {
                    damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_LIGHT;
                }

                if (target.isWearingMagicArmor()) {
                    damage /= (double)FormulasConfigs.ALT_BOW_DMG_VS_ROBE;
                }
        }

        return damage;
    }

    private static BalanceHandler instance;


    public static BalanceHandler getInstance() {
        if(instance == null)
            instance = new BalanceHandler();
        return instance;
    }
}
