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
package scripts.handlers.itemhandlers;

import java.util.List;

import gabriel.others.AutoPot;
import l2r.features.AutoPotTask;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ShotType;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.holders.SkillHolder;
import l2r.gameserver.model.items.L2Weapon;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ActionType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExAutoSoulShot;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.util.Rnd;

public class SoulShots implements IItemHandler
{
	
	private static final double HEALING_POT_CD = 15, // DO NOT PUT LESS THAN 10
		MANA_POT_CD = 10, CP_POT_CD = 1, BT_POT_CD = 0.5;
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.ITEM_NOT_FOR_PETS);
			return false;
		}
		
		final L2PcInstance activeChar = playable.getActingPlayer();
		final L2ItemInstance weaponInst = activeChar.getActiveWeaponInstance();
		final L2Weapon weaponItem = activeChar.getActiveWeaponItem();
		int itemId = item.getId();
		final List<SkillHolder> skills = item.getItem().getSkills();
		
		switch (itemId)
		{
			case 728: // mana potion
			{
				checkAutoPot(activeChar, item, itemId, MANA_POT_CD);
			}
				break;
			case 1539: // greater healing potion
			{
				checkAutoPot(activeChar, item, itemId, HEALING_POT_CD);
			}
				break;
			case 5592: // greater cp potion
			{
				checkAutoPot(activeChar, item, itemId, CP_POT_CD);
			}
				break;
			case 5591: // greater cp potion
			{
				checkAutoPot(activeChar, item, itemId, CP_POT_CD);
			}
				break;
			case 10410: // Full Bottle of Souls - 5 Souls
			{
				checkAutoPot(activeChar, item, itemId, BT_POT_CD);
			}
				break;
		}
		// Check if Soul shot can be used
		if ((weaponInst == null) || (weaponItem.getSoulShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId) && !activeChar.getAutoPot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_USE_SOULSHOTS));
			}
			return false;
		}
		
		if (skills.isEmpty())
		{
			_log.warn(getClass().getSimpleName() + ": is missing skills!");
			return false;
		}
		
		boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT) && (weaponInst.getItem().getItemGradeSPlus() == item.getItem().getItemGradeSPlus());
		
		if (!gradeCheck)
		{
			if (!activeChar.getAutoSoulShot().contains(itemId) && !activeChar.getAutoPot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.SOULSHOTS_GRADE_MISMATCH));
			}
			return false;
		}
		
		activeChar.soulShotLock.lock();
		try
		{
			// Check if Soul shot is already active
			if (activeChar.isChargedShot(ShotType.SOULSHOTS))
			{
				return false;
			}
			
			// Consume Soul shots if player has enough of them
			int SSCount = weaponItem.getSoulShotCount();
			if ((weaponItem.getReducedSoulShot() > 0) && (Rnd.get(100) < weaponItem.getReducedSoulShotChance()))
			{
				SSCount = weaponItem.getReducedSoulShot();
			}
			
			if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false))
			{
				if (!activeChar.disableAutoShot(itemId))
				{
					activeChar.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
				}
				return false;
			}
			
			// Charge soul shot
			weaponInst.setChargedShot(ShotType.SOULSHOTS, true);
		}
		finally
		{
			activeChar.soulShotLock.unlock();
		}
		
		if (!AutoPotTask.getInstance().checkId(itemId))
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.USE_S1_);
			sm.addItemName(itemId);
			activeChar.sendPacket(sm);
			
			activeChar.sendPacket(SystemMessageId.ENABLED_SOULSHOT);
		}
		
		activeChar.sendPacket(new MagicSkillUse(activeChar, activeChar, skills.get(0).getSkillId(), skills.get(0).getSkillLvl(), 0, 0));
		return true;
	}
	
	private void checkAutoPot(L2PcInstance activeChar, L2ItemInstance item, Integer itemId, double cooldown)
	{
		if (activeChar.isAutoPot(itemId))
		{
			activeChar.sendPacket(new ExAutoSoulShot(itemId, 0));
			activeChar.sendMessage("Deactivated auto " + item.getItemName());
			activeChar.setAutoPot(itemId, null, false);
		}
		else if (activeChar.getInventory().getItemByItemId(itemId) != null)
		{
			activeChar.sendPacket(new ExAutoSoulShot(itemId, 1));
			activeChar.sendMessage("Activated auto " + item.getItemName());
			activeChar.setAutoPot(itemId, ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new AutoPot(itemId, activeChar, item, (long) (cooldown * 1000)), 1000, (long) (cooldown * 1000)), true);
		}
	}
}
