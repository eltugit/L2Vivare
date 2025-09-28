/*
 * Copyright (C) 2004-2013 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package scripts.handlers.admincommandhandlers;

import gr.sr.protection.ConfigProtection;
import gr.sr.protection.hwidmanager.HWIDBan;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.instance.L2PcInstance;


public class AdminHWIDBan implements IAdminCommandHandler
{
	private static String[] _adminCommands =
	{
		"admin_hwid_ban"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
        if (!ConfigProtection.ALLOW_GUARD_SYSTEM) {
            return false;
        }
        if (activeChar == null) {
            return false;
        }
        if (!command.startsWith("admin_hwid")) {
            return false;
        }
        if (command.startsWith("admin_hwid_ban")) {

            L2Object playerTarger = activeChar.getTarget();
            if (playerTarger == null && !(playerTarger instanceof L2PcInstance)) {
                activeChar.sendMessage("Target is empty");
                return false;
            }
            L2PcInstance target = (L2PcInstance) playerTarger;
            if (target != null) {
                HWIDBan.addHWIDBan(target.getClient());
                activeChar.sendMessage(target.getName() + " banned in HWID");
            }
        }
        return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return _adminCommands;
	}
}