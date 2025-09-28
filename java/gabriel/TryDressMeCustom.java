package gabriel;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class TryDressMeCustom implements Runnable {
    private L2ItemInstance itemToRemove;
    private L2PcInstance p;

    public TryDressMeCustom(L2PcInstance player, L2ItemInstance item) {
        itemToRemove = item;
        p = player;
    }

    @Override
    public void run() {
        p.sendPacket(SystemMessageId.NO_LONGER_TRYING_ON);
        p.deleteQuickVar("DressMeTry");
        p.deleteQuickVar("hairslotDressMeTry");
        p.broadcastUserInfo();
    }
}
