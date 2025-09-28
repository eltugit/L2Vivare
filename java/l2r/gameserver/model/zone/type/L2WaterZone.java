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
package l2r.gameserver.model.zone.type;

import gabriel.config.GabConfig;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.zone.L2ZoneType;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.util.Broadcast;

public class L2WaterZone extends L2ZoneType
{
	public L2WaterZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneIdType.WATER, true);
		
		// TODO: update to only send speed status when that packet is known
		if (character.isPlayer())
		{
			L2PcInstance player = character.getActingPlayer();
			if (player.isTransformed() && !player.getTransformation().canSwim())
			{
				character.stopTransformation(true);
			}
			else
			{
				player.broadcastUserInfo();
			}
		}
		else if (character.isNpc())
		{
			character.broadcastInfo();
		}
        if (GabConfig.RESPAWN_TWWARD_ONLEAVE_SIEGEZONE) {
            if (character instanceof L2PcInstance && ((L2PcInstance) character).isCombatFlagEquipped() && TerritoryWarManager.getInstance().isTWInProgress()) {

                L2ItemInstance itemName = character.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
                TerritoryWarManager.getInstance().dropCombatFlag((L2PcInstance) character, false, false);
                Broadcast.toAllOnlinePlayers("Territory War: Player '" + character.getName() + "' tried to Enter a water zone with the " + itemName.getName() + " equipped. This flag has been reseted.");
                character.sendMessage("You cannot enter a water zone with a Ward Equipped. This ward has been reseted");
                InventoryUpdate iu = new InventoryUpdate();
                iu.addRemovedItem(itemName);
                iu.addModifiedItem(itemName);
                character.sendPacket(iu);
                ((L2PcInstance) character).broadcastUserInfo();
            }
        }
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneIdType.WATER, false);

		if (character.isPlayer())
		{
			character.getActingPlayer().broadcastUserInfo(false);
		}
		else if (character.isNpc())
		{
			character.broadcastInfo();
		}
	}
	
	@Override
	public void onDieInside(L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(L2Character character)
	{
	}
	
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}
