package gabriel.events.challengerZone;

import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class AdminChallengerEvent implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_ch_advance",
            };

    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (activeChar == null || !activeChar.getPcAdmin().canUseAdminCommand())
            return false;

        if (command.equals("admin_CHALLENGER_advance")) {
            ChallengerZoneManager.getInstance().skipDelay();
        }
        return true;
    }

    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}
