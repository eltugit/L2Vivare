package gabriel.packetSender;

import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PackageSender {
    private final L2Character target;
    private final L2GameServerPacket packet;

    public PackageSender(L2Character target, L2GameServerPacket packet) {
        this.target = target;
        this.packet = packet;
    }

    public L2Character getTarget() {
        return target;
    }

    public L2GameServerPacket getPacket() {
        return packet;
    }
}
