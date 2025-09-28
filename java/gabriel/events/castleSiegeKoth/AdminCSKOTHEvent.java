package gabriel.events.castleSiegeKoth;

import gabriel.config.GabConfig;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class AdminCSKOTHEvent implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_cskoth_add",
                    "admin_cskoth_remove",
                    "admin_cskoth_advance"
            };

    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (activeChar == null || !activeChar.getPcAdmin().canUseAdminCommand())
            return false;

        if (command.equals("admin_cskoth_add")) {
            L2Object target = activeChar.getTarget();

            if (!(target instanceof L2PcInstance)) {
                activeChar.sendMessage("You should select a player!");
                return true;
            }

            add(activeChar, (L2PcInstance) target);
        } else if (command.equals("admin_cskoth_remove")) {
            L2Object target = activeChar.getTarget();

            if (!(target instanceof L2PcInstance)) {
                activeChar.sendMessage("You should select a player!");
                return true;
            }

            remove(activeChar, (L2PcInstance) target);
        } else if (command.equals("admin_cskoth_advance")) {
            CSKOTHManager.getInstance().skipDelay();
        }

        return true;
    }

    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }

    private void add(L2PcInstance activeChar, L2PcInstance playerInstance) {
        if (CSKOTHEvent.isPlayerParticipant(playerInstance.getObjectId())) {
            activeChar.sendMessage("Player already participated in the event!");
            return;
        }

        if (!CSKOTHEvent.addParticipant(playerInstance)) {
            activeChar.sendMessage("Player instance could not be added, it seems to be null!");
            return;
        }

        if (CSKOTHEvent.isStarted()) {
            new CSKOTHEventTeleporter(playerInstance, CSKOTHEvent.getParticipantTeamCoordinates(playerInstance.getObjectId()), true, false);
        }
    }

    private void remove(L2PcInstance activeChar, L2PcInstance playerInstance) {
        if (!CSKOTHEvent.removeParticipant(playerInstance.getObjectId())) {
            activeChar.sendMessage("Player is not part of the event!");
            return;
        }

        new CSKOTHEventTeleporter(playerInstance, GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
    }
}
