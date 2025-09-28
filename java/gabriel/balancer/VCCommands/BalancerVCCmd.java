package gabriel.balancer.VCCommands;

import gabriel.balancer.ClassBalance;
import gabriel.balancer.ClassBalanceOly;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class BalancerVCCmd implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_reload_cl_vs_cl_advanced", "admin_reload_cl_vs_cl_advanced_Oly"
            };

    @Override
    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (activeChar == null)
            //if (activeChar == null || !activeChar.getPcAdmin().canUseAdminCommand())
            return false;

        if (command.equals("admin_reload_cl_vs_cl_advanced")) {
            ClassBalance.getInstance().load();
            activeChar.sendMessage("Gabriel Balance reloaded");

        } else if (command.equals("admin_reload_cl_vs_cl_advanced_Oly")) {
            ClassBalanceOly.getInstance().load();
            activeChar.sendMessage("Gabriel Balance Oly reloaded");

        }
        return true;
    }

    @Override
    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}
