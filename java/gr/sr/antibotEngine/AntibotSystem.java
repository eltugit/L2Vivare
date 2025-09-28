package gr.sr.antibotEngine;


import gr.sr.antibotEngine.dynamicHtmls.GenerateHtmls;
import gr.sr.antibotEngine.runnable.EnchantChance;
import gr.sr.antibotEngine.runnable.JailTimer;
import gr.sr.configsEngine.configs.impl.AntibotConfigs;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.instancemanager.PunishmentManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2GrandBossInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2RaidBossInstance;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.model.punishment.PunishmentAffect;
import l2r.gameserver.model.punishment.PunishmentTask;
import l2r.gameserver.model.punishment.PunishmentType;
import l2r.util.Rnd;


public class AntibotSystem {
    private AntibotSystem() {
    }

    
    public static void sendFarmBotSignal(L2Character ch) {
        L2PcInstance player;
        if (AntibotConfigs.ENABLE_ANTIBOT_SYSTEMS && AntibotConfigs.ENABLE_ANTIBOT_FARM_SYSTEM && ch != null && (player = ch.getActingPlayer()) != null) {
            if (AntibotConfigs.ENABLE_ANTIBOT_FOR_GMS) {
                checkBot(player);
                return;
            }

            if (!AntibotConfigs.ENABLE_ANTIBOT_FOR_GMS && !player.isGM()) {
                checkBot(player);
            }
        }

    }

    private static void checkBot(L2PcInstance player) {
        if (player.getTarget() != null && player.getTarget().isMonster()) {
            if (!(player.getTarget() instanceof L2RaidBossInstance) && !(player.getTarget() instanceof L2GrandBossInstance) || AntibotConfigs.ENABLE_ANTIBOT_FARM_SYSTEM_ON_RAIDS) {
                switch(AntibotConfigs.ANTIBOT_FARM_TYPE) {
                    case 0:
                        if (AntibotConfigs.ENABLE_ANTIBOT_SPECIFIC_MOBS && AntibotConfigs.ANTIBOT_FARM_MOBS_IDS.contains(player.getTarget().getId())) {
                            specificMobsChecker(player);
                            return;
                        }

                        specificMobsChecker(player);
                        return;
                    case 1:
                        if (AntibotConfigs.ENABLE_ANTIBOT_SPECIFIC_MOBS) {
                            AntibotConfigs.ANTIBOT_FARM_MOBS_IDS.contains(player.getTarget().getId());
                        }

                        antibot(player);
                    default:
                }
            }
        }
    }

    private static void specificMobsChecker(L2PcInstance player) {
        player.setKills(player.getKills() + 1);
        if (player.getKills() == AntibotConfigs.ANTIBOT_MOB_COUNTER || player.getKills() == 0) {
            handleBotCaptcha(player, false);
        }

    }

    private static void antibot(L2PcInstance player) {
        if ((float)Rnd.get(1000) <= AntibotConfigs.ANTIBOT_FARM_CHANCE * 10.0F) {
            handleBotCaptcha(player, false);
        }

    }

    private static void handleBotCaptcha(L2PcInstance player, boolean safe) {
        if (safe) {
            player.stopAbnormalEffect(AbnormalEffect.REAL_TARGET);
            player.setIsInvul(false);
            player.setIsParalyzed(false);
            if (player.hasSummon()) {
                player.getSummon().setIsParalyzed(false);
                player.getSummon().setIsInvul(false);
            }

            player.setFarmBot(false);
            player.setTries(3);
            player.setKills(0);
            if (player._jailTimer != null) {
                player._jailTimer.cancel(true);
                return;
            }
        } else {
            GenerateHtmls.captchaHtml(player, "FARM");
            player.startAbnormalEffect(AbnormalEffect.REAL_TARGET);
            player.setIsParalyzed(true);
            player.setIsInvul(true);
            if (player.hasSummon()) {
                player.getSummon().setIsParalyzed(true);
                player.getSummon().setIsInvul(true);
            }

            player._jailTimer = ThreadPoolManager.getInstance().scheduleGeneral(new JailTimer(player), (long)(AntibotConfigs.JAIL_TIMER * 1000));
            player.setFarmBot(true);
        }

    }

    public static void jailPlayer(L2PcInstance player, String var1) {
        PunishmentManager.getInstance().startPunishment(new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + (long)(AntibotConfigs.TIME_TO_SPEND_IN_JAIL * 1000), "", AntibotSystem.class.getSimpleName()));
        handleBotCaptcha(player, true);
        switch(var1) {
            case "time":
                player.sendMessage(AntibotConfigs.JAIL_TIMER + " second(s) passed, punish jail.");
                break;
            case "tries":
                player.sendMessage("You have wasted your tries, punish jail.");
        }
    }

    
    public static void refreshImage(L2PcInstance player, boolean wrong, boolean alreadySent) {
        if (alreadySent) {
            if (wrong) {
                player.setTries(player.getTries() - 1);
                player.sendMessage("Wrong captcha code or bot answer, try again!");
            }

            if (player.getTries() > 0) {
                GenerateHtmls.captchaHtml(player, "FARM");
            } else {
                jailPlayer(player, "tries");
            }
        } else {
            GenerateHtmls.captchaHtml(player, "ENCHANT");
        }
    }

    
    public static void checkFarmCaptchaCode(L2PcInstance player, String codeTry, String answerTry) {
        String botCode = player.getFarmBotCode();
        String botAnswer = player.getBotAnswer();
        if (codeTry.equals(botCode)) {
            if (AntibotConfigs.ENABLE_DOUBLE_PROTECTION && answerTry.equals(botAnswer)) {
                player.sendMessage("Captcha code accepted.");
                handleBotCaptcha(player, true);
            } else {
                player.sendMessage("Captcha code accepted.");
                handleBotCaptcha(player, true);
            }
        } else if (AntibotConfigs.ENABLE_DOUBLE_PROTECTION && !answerTry.equals(botAnswer)) {
            refreshImage(player, true, true);
        } else if (!codeTry.equals(botCode)) {
            refreshImage(player, true, true);
        }
    }

    
    public static void sendEnchantBotSignal(L2PcInstance player) {
        if (AntibotConfigs.ENABLE_ANTIBOT_SYSTEMS && AntibotConfigs.ENABLE_ANTIBOT_ENCHANT_SYSTEM && !player.isEnchantBot()) {
            if (AntibotConfigs.ENABLE_ANTIBOT_FOR_GMS) {
                antibotEnchantSystem(player);
                return;
            }

            if (!AntibotConfigs.ENABLE_ANTIBOT_FOR_GMS && !player.isGM()) {
                antibotEnchantSystem(player);
            }
        }

    }

    
    public static void checkOnEnterBot(L2PcInstance player) {
        if (player.isEnchantBot() && AntibotConfigs.ENABLE_ANTIBOT_SYSTEMS && AntibotConfigs.ENABLE_ANTIBOT_ENCHANT_SYSTEM) {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                player._enchantChanceTimer = ThreadPoolManager.getInstance().scheduleGeneral(new EnchantChance(player), 1000L);
            }, 15000L);
        }

    }

    public static void antibotEnchantSystem(L2PcInstance player) {
        if (AntibotConfigs.ANTIBOT_ENCHANT_TYPE == 0) {
            player.setEnchants(player.getEnchants() + 1);
            if (player.getEnchants() == 0 || player.getEnchants() == AntibotConfigs.ANTIBOT_ENCHANT_COUNTER) {
                enchantBot(player);
            }
        }

        if (AntibotConfigs.ANTIBOT_ENCHANT_TYPE == 1) {
            Rnd.chance(AntibotConfigs.ANTIBOT_ENCHANT_CHANCE);
            enchantBot(player);
        }

    }

    private static void enchantBot(L2PcInstance player) {
        player._enchantChanceTimer = ThreadPoolManager.getInstance().scheduleGeneral(new EnchantChance(player), 1000L);
        player.setEnchantBot(true);
    }

    
    public static void checkEnchantCaptchaCode(L2PcInstance player, String codeTry, String answerTry) {
        String correctCode = player.getEnchantBotCode();
        String correctAnswer = player.getBotAnswer();
        if (codeTry.equals(correctCode)) {
            if (AntibotConfigs.ENABLE_DOUBLE_PROTECTION && answerTry.equals(correctAnswer)) {
                player.sendMessage("Captcha code accepted.");
                player.sendMessage("Enchant chance is normal again!");
                player.setEnchants(0);
                player.setEnchantChance((double)AntibotConfigs.ENCHANT_CHANCE_PERCENT_TO_START);
                player.setEnchantBot(false);
                if (player._enchantChanceTimer != null) {
                    player._enchantChanceTimer.cancel(true);
                }

            } else {
                player.sendMessage("Captcha code accepted.");
                player.sendMessage("Enchant chance is normal again!");
                player.setEnchants(0);
                player.setEnchantChance((double)AntibotConfigs.ENCHANT_CHANCE_PERCENT_TO_START);
                player.setEnchantBot(false);
                if (player._enchantChanceTimer != null) {
                    player._enchantChanceTimer.cancel(true);
                }

            }
        } else if (AntibotConfigs.ENABLE_DOUBLE_PROTECTION && !answerTry.equals(correctAnswer)) {
            player.sendMessage("Wrong bot answer.");
            GenerateHtmls.captchaHtml(player, "ENCHANT");
        } else if (!codeTry.equals(correctCode)) {
            GenerateHtmls.captchaHtml(player, "ENCHANT");
        }
    }
}

