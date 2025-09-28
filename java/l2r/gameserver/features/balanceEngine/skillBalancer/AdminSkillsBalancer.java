package l2r.gameserver.features.balanceEngine.skillBalancer;

import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;

public class AdminSkillsBalancer implements IAdminCommandHandler
{
	
	private static final String ADMIN_COMMANDS[] =
	{
		"admin_skillsbalancer",
		"admin_loadskillsbalancer",
		"admin_updateskillsbalancer"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_skillsbalancer"))
		{
			SkillsBalanceBBSManager.getInstance().cbByPass(command, activeChar);
		}
		else if (command.equalsIgnoreCase("admin_loadskillsbalancer"))
		{
			SkillsBalanceManager.getInstance().loadBalances();
			activeChar.sendMessage("Skills balances has successfully been loaded!");
		}
		else if (command.equalsIgnoreCase("admin_updateskillsbalancer"))
		{
			SkillsBalanceManager.getInstance().updateBalances();
			activeChar.sendMessage("Skills balances has successfully been updated!");
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
