package gabriel.others;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.gameserver.network.serverpackets.MagicSkillLaunched;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.gameserver.util.Util;

/**
 * @author Gabriel Costa Souza Discord: Gabriel 'GCS'#2589 Skype - email: gabriel_costa25@hotmail.com
 */

public class PacketSenderTaskBlock
{
	/**
	 * Checks for magic skill use, magic skill lauched, attack, social action
	 * @param packet
	 * @param actor
	 * @param target
	 * @return
	 */
	
	public static boolean checkPacketRange(L2GameServerPacket packet, L2Character actor, L2PcInstance target)
	{
		return checkPacketRange(packet, actor, target, false);
	}
	
	public static boolean checkPacketRange(L2GameServerPacket packet, L2Character actor, L2PcInstance target, boolean ss)
	{
		if ((packet.getClass() == MagicSkillUse.class) || (packet.getClass() == MagicSkillLaunched.class) || ss)
		{
			int tempRange = Integer.parseInt(target.getVar("animeLimit", "-1"));
			if (tempRange == 0)
			{
				return false;
			}
			if (((tempRange > 250) || (tempRange == -1)) && (/* target.getInstanceId() > 0 && */target.getKnownList().getKnownPlayers().size() > 30))
			{
				tempRange = 250;
			}
			
			return (tempRange <= -1) || Util.checkIfInRange(tempRange, actor, target, false);
		}
		return true;
	}
	
}
