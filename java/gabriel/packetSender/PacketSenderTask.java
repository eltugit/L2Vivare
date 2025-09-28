package gabriel.packetSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.gameserver.network.serverpackets.MagicSkillLaunched;
import l2r.gameserver.network.serverpackets.MagicSkillUse;

/**
 * @author Gabriel Costa Souza Discord: Gabriel 'GCS'#2589 Skype - email: gabriel_costa25@hotmail.com website: l2jgabdev.com
 */

public class PacketSenderTask
{
	
	private static final Map<L2Character, List<L2GameServerPacket>> PACKET_QUEUE = new ConcurrentHashMap<>();
	private static boolean _working = false;
	
	protected static PacketSenderTask instance;
	
	public static PacketSenderTask getInstance()
	{
		if (instance == null)
		{
			instance = new PacketSenderTask();
		}
		return instance;
	}
	
	public boolean delayedPackage(L2GameServerPacket packet)
	{
		return (packet.getClass() == MagicSkillUse.class) || (packet.getClass() == MagicSkillLaunched.class);
	}
	
	public void addTask(L2Character target, L2Character sender, L2GameServerPacket packet)
	{
		
		List<L2GameServerPacket> packetList = PACKET_QUEUE.get(target);
		if (packetList == null)
		{
			final List<L2GameServerPacket> list = new ArrayList<>(1);
			list.add(packet);
			PACKET_QUEUE.put(target, list);
			return;
		}
		
		synchronized (packetList)
		{
			// for (L2GameServerPacket op : packetList)
			// {
			// if (op.getClass() == packet.getClass())
			// {
			// packetList.remove(op);
			// break;
			// }
			// }
			packetList.add(packet);
		}
	}
	
	// sweet spot -> 10 - 50
	private PacketSenderTask()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() ->
		{
			
			if (_working)
			{
				return;
			}
			_working = true;
			
			QUEUE:
			for (Map.Entry<L2Character, List<L2GameServerPacket>> entry : PACKET_QUEUE.entrySet())
			{
				final List<L2GameServerPacket> list = entry.getValue();
				if (list.isEmpty())
				{
					PACKET_QUEUE.remove(entry.getKey());
					continue QUEUE;
				}
				int size = 15;
				if (list.size() < 15)
				{
					size = list.size();
				}
				for (int i = 0; i < size; i++)
				{
					final L2GameServerPacket packet;
					synchronized (list)
					{
						packet = list.remove(0);
					}
					entry.getKey().sendPacket(packet);
				}
				
				// if(entry.getKey().isGM())
				// System.out.println("list::: "+list.size());
				//
				// final L2GameServerPacket packet;
				// synchronized (list)
				// {
				// packet = list.remove(0);
				// }
				// entry.getKey().sendPacket(packet);
				// continue QUEUE;
			}
			_working = false;
			
		}, 50, 50);
	}
}
