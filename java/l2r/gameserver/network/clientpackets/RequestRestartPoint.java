/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.network.clientpackets;

import gabriel.Utils.GabUtils;
import gabriel.events.castleSiegeKoth.CSKOTHEvent;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.enums.RestartPoint;
import l2r.gameserver.enums.TeleportWhereType;
import l2r.gameserver.instancemanager.*;
import l2r.gameserver.model.L2SiegeClan;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2SiegeFlagInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.ClanHall;
import l2r.gameserver.model.entity.Fort;
import l2r.gameserver.model.entity.clanhall.SiegableHall;
import l2r.gameserver.model.skills.L2Skill;
import l2r.util.Rnd;

/**
 * This class ...
 * @version $Revision: 1.7.2.3.2.6 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestRestartPoint extends L2GameClientPacket
{
	private static final String _C__7D_REQUESTRESTARTPOINT = "[C] 7D RequestRestartPoint";
	
	protected int _requestedPointType;
	protected int _requestedPointItemId = 0;
	protected boolean _continuation;
	
	@Override
	protected void readImpl()
	{
		_requestedPointType = readD();
		if (_buf.hasRemaining())
		{
			_requestedPointItemId = readD();
		}
	}
	
	class DeathTask implements Runnable
	{
		final L2PcInstance activeChar;
		
		DeathTask(L2PcInstance _activeChar)
		{
			activeChar = _activeChar;
		}
		
		@Override
		public void run()
		{
			portPlayer(activeChar);
		}
	}
	
	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		
		if (activeChar == null)
		{
			return;
		}
		if(activeChar.isInTournament()){
            activeChar.sendMessage("Cannot Respawn in Tournament!");
            return;
        }
		if (CSKOTHEvent.isStarted() && CSKOTHEvent.isPlayerParticipant(activeChar.getObjectId()))
			return;
		if (activeChar.isFakeDeath())
		{
			activeChar.stopFakeDeath(true);
			return;
		}
		else if (!activeChar.isDead())
		{
		    if(!GabUtils.isInPvPInstance(activeChar)) {
                _log.warn("Living player [" + activeChar.getName() + "] called RestartPointPacket! Ban this player!");
            }
			return;
		}
		
		Castle castle = CastleManager.getInstance().getCastle(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		if ((castle != null) && castle.getSiege().isInProgress())
		{
			if ((activeChar.getClan() != null) && castle.getSiege().checkIsAttacker(activeChar.getClan()))
			{
				// Schedule respawn delay for attacker
				ThreadPoolManager.getInstance().scheduleGeneral(new DeathTask(activeChar), castle.getSiege().getAttackerRespawnDelay());
				if (castle.getSiege().getAttackerRespawnDelay() > 0)
				{
					activeChar.sendMessage("You will be re-spawned in " + (castle.getSiege().getAttackerRespawnDelay() / 1000) + " seconds");
				}
				return;
			}
		}
		
		portPlayer(activeChar);
	}
	
	protected final void portPlayer(final L2PcInstance activeChar)
	{
		Location loc = null;
		Castle castle = null;
		Fort fort = null;
		SiegableHall hall = null;
		boolean isInDefense = false;
		int instanceId = 0;
		
		RestartPoint type = RestartPoint.getType(_requestedPointType);
		
		// force jail
		if (activeChar.isJailed())
		{
			type = RestartPoint.TO_JAIL;
		}
		else if (activeChar.isFestivalParticipant())
		{
			type = RestartPoint.FIXED;
		}
		
		switch (type)
		{
			case TO_CLANHALL: // to clanhall
			{
				if ((activeChar.getClan() == null) || (activeChar.getClan().getHideoutId() == 0))
				{
					_log.warn("Player [" + activeChar.getName() + "] called RestartPointPacket - To Clanhall and he doesn't have Clanhall!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CLANHALL);
				
				if ((ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()) != null) && (ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(ClanHallManager.getInstance().getClanHallByOwner(activeChar.getClan()).getFunction(ClanHall.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case TO_CASTLE: // to castle
			{
				castle = CastleManager.getInstance().getCastle(activeChar);
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					// Siege in progress
					if (castle.getSiege().checkIsDefender(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
					}
					else if (castle.getSiege().checkIsAttacker(activeChar.getClan()))
					{
						loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
					}
					else
					{
						_log.warn("Player [" + activeChar.getName() + "] called RestartPointPacket - To Castle and he doesn't have Castle!");
						return;
					}
				}
				else
				{
					if ((activeChar.getClan() == null) || (activeChar.getClan().getCastleId() == 0))
					{
						return;
					}
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.CASTLE);
				}
				if ((CastleManager.getInstance().getCastleByOwner(activeChar.getClan()) != null) && (CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(CastleManager.getInstance().getCastleByOwner(activeChar.getClan()).getFunction(Castle.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case TO_FORTRESS: // to fortress
			{
				if (((activeChar.getClan() == null) || (activeChar.getClan().getFortId() == 0)) && !isInDefense)
				{
					_log.warn("Player [" + activeChar.getName() + "] called RestartPointPacket - To Fortress and he doesn't have Fortress!");
					return;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.FORTRESS);
				if ((FortManager.getInstance().getFortByOwner(activeChar.getClan()) != null) && (FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP) != null))
				{
					activeChar.restoreExp(FortManager.getInstance().getFortByOwner(activeChar.getClan()).getFunction(Fort.FUNC_RESTORE_EXP).getLvl());
				}
				break;
			}
			case TO_HQFLAG: // to siege HQ
			{
				L2SiegeClan siegeClan = null;
				castle = CastleManager.getInstance().getCastle(activeChar);
				fort = FortManager.getInstance().getFort(activeChar);
				hall = CHSiegeManager.getInstance().getNearbyClanHall(activeChar);
				L2SiegeFlagInstance flag = TerritoryWarManager.getInstance().getHQForClan(activeChar.getClan());
				
				// vGodFather territory flag fix
				if (flag == null)
				{
					flag = TerritoryWarManager.getInstance().getFlagForClan(activeChar.getClan());
				}
				
				if ((castle != null) && castle.getSiege().isInProgress())
				{
					siegeClan = castle.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((fort != null) && fort.getSiege().isInProgress())
				{
					siegeClan = fort.getSiege().getAttackerClan(activeChar.getClan());
				}
				else if ((hall != null) && hall.isInSiege())
				{
					siegeClan = hall.getSiege().getAttackerClan(activeChar.getClan());
				}
				
				// vGodFather territory flag fix
				// player will be ported to village if flag died while player was dead
				if (((siegeClan == null) || siegeClan.getFlag().isEmpty()) && (flag == null))
				{
					// Check if clan hall has inner spawns loc
					if ((hall != null) && ((loc = hall.getSiege().getInnerSpawnLoc(activeChar)) != null))
					{
						break;
					}
					
					// we dont need this warning anymore
					// _log.warn("Player [" + activeChar.getName() + "] called RestartPointPacket - To Siege HQ and he doesn't have Siege HQ!");
					loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
					break;
				}
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.SIEGEFLAG);
//				int extraX = 50 + Rnd.get(20, Rnd.get(50,100));
//				int extraXNed = -50 + Rnd.get(-120, Rnd.get(-100,-50));
//				int extraY = 50 + Rnd.get(20, Rnd.get(50,100));
//				int extraYNed = -50 + Rnd.get(-120, Rnd.get(-100,-50));
//
//				loc = new Location(loc.getX() + (Rnd.nextBoolean() ? extraX: extraXNed), loc.getY() +  (Rnd.nextBoolean() ? extraY: extraYNed), loc.getY(), loc.getHeading(),  loc.getInstanceId());
				break;
			}
			case FIXED: // Fixed or Player is a festival participant
			{
				if (!activeChar.isGM() && !activeChar.isFestivalParticipant() && !activeChar.getInventory().haveItemForSelfResurrection() && !GabUtils.isInPvPInstance(activeChar))
				{
					_log.warn("Player [" + activeChar.getName() + "] called RestartPointPacket - Fixed and he isn't festival participant!");
					return;
				}
				if (activeChar.isGM() || GabUtils.isInPvPInstance(activeChar) || activeChar.destroyItemByItemId("Feather", 10649, 1, activeChar, false) || activeChar.destroyItemByItemId("Feather", 13300, 1, activeChar, false) || activeChar.destroyItemByItemId("Feather", 13128, 1, activeChar, false))
				{
					if(GabUtils.isInPvPInstance(activeChar)){
                        instanceId = activeChar.getInstanceId();

                        loc = PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()].getSpawnLocs()[Rnd.get(PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()].getSpawnLocs().length)];
                        L2Effect[] effects = activeChar.getAllEffects();

                        if (effects == null || effects.length == 0)
                            return;

                        for (L2Effect e : effects) {
                            if (e == null || !e.getSkill().isDebuff())
                                continue;
                            e.exit();
                        }

                        if (activeChar.isDead()) {
                            activeChar.doRevive(100.00);
                            activeChar.healToMaxPvPInstance();
                            activeChar.setInstanceId(instanceId);
                            activeChar.removeReviving();
                            activeChar.teleToLocation(loc, true);

                            if (ConfigPvPInstance.ENABLE_PVP_INSTANCE_NOB_ENTER) {
                                L2Skill noblesse = SkillData.getInstance().getInfo(1323, 1);
                                noblesse.getEffects(activeChar, activeChar);

                                L2Skill flames = SkillData.getInstance().getInfo(1427, 1);
                                if (flames != null)
                                    flames.getEffects(activeChar, activeChar);
                            }
                        }
                        return;
                    }
				}
				else
				// Festival Participant
				{
                    instanceId = activeChar.getInstanceId();
					loc = new Location(activeChar);
				}
				break;
			}
			case AGATHION: // TODO: agathion ress
			{
				break;
			}
			case ITEM_FIXED: // TODO: item fixed
			{
				break;
			}
			case TO_JAIL: // to jail
			{
				if (!activeChar.isJailed())
				{
					return;
				}
				loc = new Location(-114356, -249645, -2984);
				break;
			}
			default:
			{
				loc = MapRegionManager.getInstance().getTeleToLocation(activeChar, TeleportWhereType.TOWN);
				break;
			}
		}
		
		// Teleport and revive
		if (loc != null)
		{
			activeChar.setInstanceId(instanceId);
			activeChar.setIsIn7sDungeon(false);
			activeChar.setIsPendingRevive(true);
			activeChar.teleToLocation(loc, true);
		}
	}
	
	@Override
	public String getType()
	{
		return _C__7D_REQUESTRESTARTPOINT;
	}
}
