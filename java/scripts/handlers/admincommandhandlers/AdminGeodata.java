/*
 * Copyright (C) 2004-2015 L2J DataPack
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

import l2r.geoserver.GeoData;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

import java.util.StringTokenizer;

/**
 * @author vGodFather
 */
public class AdminGeodata implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_geo_trace",
		"admin_geo_nswe",
		"admin_geo_can_move",
		"admin_geo_can_see",
		"admin_geogrid",
		"admin_pathfind_buffers",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_geo_trace":
				if (activeChar.getVarB("geo_trace", false))
				{
					activeChar.sendMessage("Geo trace disabled");
					activeChar.setVar("geo_trace", "false");
				}
				else
				{
					activeChar.sendMessage("Geo trace enabled");
					activeChar.setVar("geo_trace", "true");
				}
				break;
			case "admin_geo_nswe":
				String result = "";
				short nswe = GeoData.getInstance().getNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ(),0);
				if ((nswe & 8) == 0)
				{
					result += " N";
				}
				if ((nswe & 4) == 0)
				{
					result += " S";
				}
				if ((nswe & 2) == 0)
				{
					result += " W";
				}
				if ((nswe & 1) == 0)
				{
					result += " E";
				}
				activeChar.sendMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
				break;
			case "admin_geo_can_move":
			{
				final L2Object target = activeChar.getTarget();
				if (target != null)
				{
					if (GeoData.getInstance().canSeeTarget(activeChar, target))
					{
						activeChar.sendMessage("Can move beeline.");
					}
					else
					{
						activeChar.sendMessage("Can not move beeline!");
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
				break;
			}
			case "admin_geo_can_see":
			{
				final L2Object target = activeChar.getTarget();
				if (target != null)
				{
					if (GeoData.getInstance().canSeeTarget(activeChar, target))
					{
						activeChar.sendMessage("Can see target.");
					}
					else
					{
						activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
					}
				}
				else
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				}
				break;
			}
			case "admin_geogrid":
			{
//				GeodataUtils.debugGrid(activeChar);
				break;
			}
			case "admin_pathfind_buffers":
			{
//				AdminHtml.showAdminHtml(activeChar, PathFindBuffers.getStats().toL2Html());
				break;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
