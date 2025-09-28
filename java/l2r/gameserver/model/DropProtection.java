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
package l2r.gameserver.model;

import gabriel.config.GabConfig;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2NpcInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2PetInstance;

import java.util.concurrent.ScheduledFuture;

/**
 * @author DrHouse
 */
public class DropProtection implements Runnable
{
	private volatile boolean _isProtected = false;
	private L2PcInstance _owner = null;
	private ScheduledFuture<?> _task = null;
	
	private static final long PROTECTED_MILLIS_TIME = GabConfig.DROP_PROTECTION * 1000;
	
	@Override
	public synchronized void run()
	{
		_isProtected = false;
		_owner = null;
		_task = null;
	}
	
	public boolean isProtected()
	{
		return _isProtected;
	}
	
	public L2PcInstance getOwner()
	{
		return _owner;
	}
	
	public synchronized boolean tryPickUp(L2PcInstance actor)
	{
		if (!_isProtected)
		{
			return true;
		}

		boolean ownerCC = _owner.getParty() != null && _owner.getParty().isInCommandChannel();
		boolean ownerPT = _owner.getParty() != null;

		if(ownerCC){ // se tiverr em CC, so o lider pode pegar
            if(_owner.getParty().getCommandChannel().getLeader().getObjectId() == actor.getObjectId())
                return true;
            return false;
        }

		if(ownerPT){ // se tiver em pt, so o lider pode pegar
            if(_owner.getParty().getLeader().getObjectId() == actor.getObjectId())
                return true;
            return false;
        }

		//funcionamento normal.

		if (_owner == actor)
		{
			return true;
		}
		
		if ((_owner.getParty() != null) && (_owner.getParty() == actor.getParty()))
		{
			return true;
		}
		
		/*
		 * if (_owner.getClan() != null && _owner.getClan() == actor.getClan()) return true;
		 */
		
		return false;
	}


	
	public boolean tryPickUp(L2PetInstance pet)
	{
		return tryPickUp(pet.getOwner());
	}
	
	public synchronized void unprotect()
	{
		if (_task != null)
		{
			_task.cancel(false);
		}
		_isProtected = false;
		_owner = null;
		_task = null;
	}
	
	public synchronized void protect(L2PcInstance player){
		protect(player, null);
	}
	public synchronized void protect(L2PcInstance player, L2Npc npc)
	{
		unprotect();
		
		_isProtected = true;
		
		if ((_owner = player) == null)
		{
			throw new NullPointerException("Trying to protect dropped item to null owner");
		}
		long time = npc.isRaid() ? PROTECTED_MILLIS_TIME * 4 : PROTECTED_MILLIS_TIME; //se for raid, 4x a config normal
		_task = ThreadPoolManager.getInstance().scheduleGeneral(this, time);
	}
}
