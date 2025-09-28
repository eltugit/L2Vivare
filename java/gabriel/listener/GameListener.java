package gabriel.listener;


import gabriel.listener.commons.Listener;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

public abstract interface GameListener extends Listener<L2GameServerPacket> {
}