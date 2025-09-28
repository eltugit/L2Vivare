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

import gabriel.others.CharCustomHeroTable;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.olympiad.Olympiad;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author vGodFather
 */
public class AdminOlympiad implements IAdminCommandHandler
{
	private static final Logger _log = LoggerFactory.getLogger(AdminOlympiad.class);
	
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_oly",
		"admin_saveoly",
		"admin_endoly",
		"admin_finisholy",
		"admin_manualhero",
		"admin_sethero",
		"admin_checkoly",
        "admin_addolypoints",
        "admin_removeolypoints",
        "admin_setolypoints",
        "admin_getolypoints"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_oly"))
		{
			showMainPage(activeChar);
			return true;
		}
		else if (command.startsWith("admin_saveoly"))
		{
			Olympiad.getInstance().saveOlympiadStatus();
			activeChar.sendMessage("olympiad system saved.");
		}
		else if (command.startsWith("admin_endoly") || command.startsWith("admin_finisholy"))
		{
			try
			{
				Olympiad.getInstance().manualSelectHeroes();
			}
			catch (Exception e)
			{
				_log.warn("An error occured while ending olympiad: " + e);
			}
			activeChar.sendMessage("Heroes formed.");
		}
		else if (command.startsWith("admin_manualhero"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			
			final L2PcInstance target = activeChar.getTarget().isPlayer() ? activeChar.getTarget().getActingPlayer() : activeChar;
			target.setHero(!target.isHero());
			target.broadcastUserInfo();
		}
		else if (command.startsWith("admin_sethero"))
        {
            L2PcInstance target = null;
            String[] cmd = command.split(" ");
            long time = 0;

            if (cmd.length >= 2)
            {
                try
                {
                    time = Long.valueOf(cmd[1]);
                }
                catch (Exception e)
                {
                    activeChar.sendMessage("Tell time in days, just int.");
                }


                if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
                {
                    target = (L2PcInstance) activeChar.getTarget();
                    if (time == -1)
                    {
                        CharCustomHeroTable.getInstance().delete(target);
                        activeChar.sendMessage(target.getName() + " is not a hero now.");
                    }
                    else
                    {
                        CharCustomHeroTable.getInstance().add(target, 1, System.currentTimeMillis(), (time * 24 * 60 * 60 * 1000));
                        if (time == 0)
                            activeChar.sendMessage(target.getName() + " is a hero now.");
                        else
                            activeChar.sendMessage(target.getName() + " is a hero now for " + String.valueOf(time) + " days.");
                    }
                }
                else
                    activeChar.sendMessage("This command requires a target.");
            }
        }
		else if (command.startsWith("admin_checkoly"))
		{
			final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			int period = Olympiad.getInstance().getPeriod();
			activeChar.sendMessage("Olympiad System: Period ends at " + format.format(period == 0 ? Olympiad.getInstance().getMillisToOlympiadEnd() + System.currentTimeMillis() : Olympiad.getInstance().getMillisToValidationEnd() + System.currentTimeMillis()));
		}
		else if (command.startsWith("admin_addolypoints"))
        {
            try
            {
                String val = command.substring(19);
                L2Object target = activeChar.getTarget();
                L2PcInstance player = null;
                if (target instanceof L2PcInstance)
                {
                    player = (L2PcInstance) target;
                    if (player.isNoble())
                    {
                        StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
                        if (playerStat == null)
                        {
                            activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
                            return false;
                        }
                        int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
                        int points = oldpoints + Integer.parseInt(val);
                        if (points > 100)
                        {
                            activeChar.sendMessage("You can't set more than 100 or less than 0 Olympiad points!");
                            return false;
                        }
                        playerStat.set("olympiad_points", points);

                        activeChar.sendMessage("Player " + player.getName() + " now has " + points + " Olympiad points.");
                    }
                    else
                    {
                        activeChar.sendMessage("Oops! This player is not noblesse!");
                        return false;
                    }
                }
                else
                {
                    activeChar.sendMessage("Usage: target a player and write the amount of points you would like to add.");
                    activeChar.sendMessage("Example: //addolypoints 10");
                    activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
                }
            }
            catch (StringIndexOutOfBoundsException e)
            {
                activeChar.sendMessage("Usage: //addolypoints <points>");
            }
        }
        else if (command.startsWith("admin_removeolypoints"))
        {
            try
            {
                String val = command.substring(22);
                L2Object target = activeChar.getTarget();
                L2PcInstance player = null;
                if (target instanceof L2PcInstance)
                {
                    player = (L2PcInstance) target;
                    if (player.isNoble())
                    {
                        StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
                        if (playerStat == null)
                        {
                            activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
                            return false;
                        }
                        int oldpoints = Olympiad.getInstance().getNoblePoints(player.getObjectId());
                        int points = oldpoints - Integer.parseInt(val);
                        if (points < 0)
                            points = 0;
                        playerStat.set("olympiad_points", points);
                        activeChar.sendMessage("Player " + player.getName() + " now has " + points + " Olympiad points.");
                    }
                    else
                    {
                        activeChar.sendMessage("Oops! This player is not noblesse!");
                        return false;
                    }
                }
                else
                {
                    activeChar.sendMessage("Usage: target a player and write the amount of points you would like to remove.");
                    activeChar.sendMessage("Example: //removeolypoints 10");
                    activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
                }
            }
            catch (StringIndexOutOfBoundsException e)
            {
                activeChar.sendMessage("Usage: //removeolypoints points");
            }
        }
        else if (command.startsWith("admin_setolypoints"))
        {
            try
            {
                String val = command.substring(19);
                L2Object target = activeChar.getTarget();
                L2PcInstance player = null;
                if (target instanceof L2PcInstance)
                {
                    player = (L2PcInstance) target;
                    if (player.isNoble())
                    {
                        StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
                        if (playerStat == null)
                        {
                            activeChar.sendMessage("Oops! This player hasn't played on Olympiad yet!");
                            return false;
                        }
                        if (Integer.parseInt(val) < 1 && Integer.parseInt(val) > 100)
                        {
                            activeChar.sendMessage("You can't set more than 100 or less than 0 Olympiad points! or lower then 0");
                            return false;
                        }
                        playerStat.set("olympiad_points", Integer.parseInt(val));
                        activeChar.sendMessage("Player " + player.getName() + " now has " + Integer.parseInt(val) + " Olympiad points.");
                    }
                    else
                    {
                        activeChar.sendMessage("Oops! This player is not noblesse!");
                        return false;
                    }
                }
                else
                {
                    activeChar.sendMessage("Usage: target a player and write the amount of points you would like to set.");
                    activeChar.sendMessage("Example: //setolypoints 10");
                    activeChar.sendMessage("However, keep in mind that you can't have less than 0 or more than 100 points.");
                }
            }
            catch (StringIndexOutOfBoundsException e)
            {
                activeChar.sendMessage("Usage: //setolypoints <points>");
            }
        }
        else if (command.startsWith("admin_getolypoints"))
        {
            try
            {
                L2Object target = activeChar.getTarget();
                L2PcInstance player = null;
                if (target instanceof L2PcInstance)
                {
                    player = (L2PcInstance) target;
                    if (player.isNoble())
                    {
                        activeChar.sendMessage(">=========>>" + player.getName() + "<<=========");
                        activeChar.sendMessage("   Match(s):" + Olympiad.getInstance().getCompetitionDone(player.getObjectId()));
                        activeChar.sendMessage("   Win(s):" + Olympiad.getInstance().getCompetitionWon(activeChar.getObjectId()));
                        activeChar.sendMessage("   Defeat(s):" + Olympiad.getInstance().getCompetitionLost(activeChar.getObjectId()));
                        activeChar.sendMessage("   Point(s) " + Olympiad.getInstance().getNoblePoints(player.getObjectId()));
                        activeChar.sendMessage(">=========>>" + player.getName() + "<<=========");
                    }
                    else
                    {
                        activeChar.sendMessage("Oops! This player is not noblesse!");
                        return false;
                    }
                }
                else
                    activeChar.sendMessage("You must target a player to use the command.");
            }
            catch (StringIndexOutOfBoundsException e)
            {
                activeChar.sendMessage("Usage: //getolypoints");
            }
        }
		
		showMainPage(activeChar);
		return true;
	}
	
	private void showMainPage(L2PcInstance activeChar)
	{
		final NpcHtmlMessage adminReply = new NpcHtmlMessage();
		adminReply.setFile(activeChar, activeChar.getHtmlPrefix(), "data/html/admin/olympiad_menu.htm");
		
		int period = Olympiad.getInstance().getPeriod();
		
		long milliToEnd = period == 0 ? Olympiad.getInstance().getMillisToOlympiadEnd() : Olympiad.getInstance().getMillisToValidationEnd();
		
		double numSecs = (milliToEnd / 1000) % 60;
		double countDown = ((milliToEnd / 1000) - numSecs) / 60;
		int numMins = (int) Math.floor(countDown % 60);
		countDown = (countDown - numMins) / 60;
		int numHours = (int) Math.floor(countDown % 24);
		int numDays = (int) Math.floor((countDown - numHours) / 24);
		
		final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		adminReply.replace("%olyperiod%", period == 0 ? "Olympiad" : "Validation");
		adminReply.replace("%endolyperiod%", String.valueOf(format.format(milliToEnd + System.currentTimeMillis())));
		adminReply.replace("%endolytime%", numDays + " day(s) " + numHours + " hour(s) " + numMins + " min(s)");
		activeChar.sendPacket(adminReply);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
