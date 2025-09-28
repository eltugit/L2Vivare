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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.StringTokenizer;

import l2r.L2DatabaseFactory;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;

import scripts.handlers.custom.ChatBanManager;

/**
 * EditChar admin command implementation.
 */
public class AdminBanHwid implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_chat_hwid",
		"admin_pc_hwid"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_chat_hwid"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String plyr = st.nextToken();
				
				String type = st.nextToken();
				int time = Integer.parseInt(type);
				
				L2PcInstance player = L2World.getInstance().getPlayer(plyr);
				if (player != null)
				{
					addBlockChat(activeChar, player, time);
				}
			}
		}
		else if (command.startsWith("admin_pc_hwid"))
		{
			StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1)
			{
				st.nextToken();
				String plyr = st.nextToken();
				
				L2PcInstance player = L2World.getInstance().getPlayer(plyr);
				if (player != null)
				{
					addBlockChat(activeChar, player, 3);
					updateDatabase(player);
					activeChar.sendMessage(new StringBuilder().append("HWID : ").append(player.getClient().getHWID()).append(" Banned").toString());
					player.logout(true);
				}
			}
		}
		return true;
	}
	
	public static void updateDatabase(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement ps = con.prepareStatement("REPLACE INTO banned_hwid (char_name, hwid) VALUES (?,?)");
			ps.setString(1, player.getName());
			ps.setString(2, player.getClient().getHWID());
			ps.execute();
			ps.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void addBlockChat(L2PcInstance activeChar, L2PcInstance targetChar, int duration)
	{
		String hwid = targetChar.getClient().getHWID();
		
		if (duration <= 0)
		{
			activeChar.sendMessage("The value you have entered is incorrect.");
			return;
		}
		else if (ChatBanManager.getInstance().checkBannedTime(targetChar.getAccountName(), targetChar.getClient().getHWID()))
		{
			if (ChatBanManager.getInstance().checkLogin(targetChar.getAccountName()))
			{
				ChatBanManager.getInstance().removeLogin(targetChar.getAccountName());
			}
			
			if (ChatBanManager.getInstance().checkHwid(targetChar.getClient().getHWID()))
			{
				ChatBanManager.getInstance().removeHwid(targetChar.getClient().getHWID());
			}
			
			activeChar.sendMessage("SYS: Voce removeu a restricao Chat Block HWID do Jogador " + targetChar.getName());
			activeChar.sendPacket(new ExShowScreenMessage("Voce removeu o ChatBan de " + targetChar.getName(), 6000));
			
			return;
		}
		
		ChatBanManager.getInstance().addChatBan(targetChar.getAccountName(), hwid, System.currentTimeMillis() + (duration * 3600000));
		
		activeChar.sendPacket(new ExShowScreenMessage("Voce bloqueou o Chat do Jogador " + targetChar.getName(), 6000));
		activeChar.sendMessage("SYS: Voce bloqueou o Chat HWID do Jogador " + targetChar.getName() + ".");
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}