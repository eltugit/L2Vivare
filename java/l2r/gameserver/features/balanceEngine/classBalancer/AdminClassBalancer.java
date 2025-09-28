package l2r.gameserver.features.balanceEngine.classBalancer;

import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class AdminClassBalancer implements IAdminCommandHandler
{
	
	private static final String ADMIN_COMMANDS[] =
	{
		"admin_classbalancer",
		"admin_loadclassbalancer",
		"admin_updateclassbalancer"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_classbalancer"))
		{
			ClassBalanceBBSManager.getInstance().cbByPass(command, activeChar);
		}
		else if (command.equalsIgnoreCase("admin_loadclassbalancer"))
		{
			ClassBalanceManager.getInstance().loadBalances();
			activeChar.sendMessage("Class balances has successfully been loaded!");
		}
		else if (command.equalsIgnoreCase("admin_updateclassbalancer"))
		{
			ClassBalanceManager.getInstance().updateBalances();
			activeChar.sendMessage("Class balances has successfully been updated!");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
