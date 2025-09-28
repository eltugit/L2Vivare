//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.securityEngine;


import gr.sr.configsEngine.configs.impl.SecuritySystemConfigs;
import l2r.Config;
import l2r.gameserver.instancemanager.PunishmentManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.punishment.PunishmentAffect;
import l2r.gameserver.model.punishment.PunishmentTask;
import l2r.gameserver.model.punishment.PunishmentType;
import l2r.gameserver.util.Broadcast;
import l2r.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SecurityActions {
    private static Logger log = LoggerFactory.getLogger(SecurityActions.class);

    private SecurityActions() {
    }

    
    public static void startSecurity(L2PcInstance player, SecurityType type) {
        if (SecuritySystemConfigs.ENABLE_SECURITY_SYSTEM && !player.isGM()) {
            String typeText = "";
            String typePrio = "";
            switch(type) {
                case ACHIEVEMENT_SYSTEM:
                    typeText = SecurityType.ACHIEVEMENT_SYSTEM.getText();
                    typePrio = "High";
                    break;
                case AIO_ITEM:
                    typeText = SecurityType.AIO_ITEM.getText();
                    typePrio = "High";
                    break;
                case COMMUNITY_SYSTEM:
                    typeText = SecurityType.COMMUNITY_SYSTEM.getText();
                    typePrio = "High";
                    break;
                case AIO_ITEM_BUFFER:
                    typeText = SecurityType.AIO_ITEM_BUFFER.getText();
                    typePrio = "High";
                    break;
                case AIO_NPC:
                    typeText = SecurityType.AIO_NPC.getText();
                    typePrio = "High";
                    break;
                case NPC_BUFFER:
                    typeText = SecurityType.NPC_BUFFER.getText();
                    typePrio = "High";
                    break;
                case CUSTON_GATEKEEPER:
                    typeText = SecurityType.CUSTON_GATEKEEPER.getText();
                    typePrio = "High";
                    break;
                case DONATE_MANAGER:
                    typeText = SecurityType.DONATE_MANAGER.getText();
                    typePrio = "High";
                    break;
                case VOTE_SYSTEM:
                    typeText = SecurityType.VOTE_SYSTEM.getText();
                    typePrio = "High";
                    break;
                case ENCHANT_EXPLOIT:
                    typeText = SecurityType.ENCHANT_EXPLOIT.getText();
                    typePrio = "High";
                    break;
                case ANTIBOT_SYSTEM:
                    typeText = SecurityType.ANTIBOT_SYSTEM.getText();
                    typePrio = "Low";
            }

            String temp = typeText;
            typeText = typePrio;
            switch(typeText) {
                case "Low":
                    handlePunishment(player, true);
                    break;
                case "Mid":
                    handlePunishment(player, true);
                    PunishmentManager.getInstance().startPunishment(new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + (long)(SecuritySystemConfigs.TIME_IN_JAIL_MID * 1000), "", SecurityActions.class.getSimpleName()));
                    break;
                case "High":
                    handlePunishment(player, true);
                    handlePunishment(player, false);
                    PunishmentManager.getInstance().startPunishment(new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + (long)(SecuritySystemConfigs.TIME_IN_JAIL_HIGH * 1000), "", SecurityActions.class.getSimpleName()));
                    break;
            }
            Util.handleIllegalPlayerAction(player, "Player: " + player.getName() + " might use third party program to exploit " + temp + ".", Config.DEFAULT_PUNISH);
            log.warn("#### ATTENCTION ####");
            log.warn(player.getName() + " might use third party program to exploit " + temp + ".");
        }
    }

    private static void handlePunishment(L2PcInstance player, boolean punish) {
        if (punish) {
            if (SecuritySystemConfigs.ENABLE_MESSAGE_TO_PLAYER) {
                PunishmentManager.getInstance().startPunishment(new PunishmentTask(player.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL, System.currentTimeMillis() + (long)(SecuritySystemConfigs.TIME_IN_JAIL_LOW * 1000), "", SecurityActions.class.getSimpleName()));
                return;
            }
        } else if (SecuritySystemConfigs.ENABLE_GLOBAL_ANNOUNCE) {
            Broadcast.toAllOnlinePlayers("Security System: " + player.getName() + " " + SecuritySystemConfigs.ANNOUNCE_TO_SEND, true);
        }

    }
}
