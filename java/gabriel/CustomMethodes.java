package gabriel;


import gabriel.dressmeEngine.DressMeHandler;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */


public class CustomMethodes {

    public static void checkForOldVisuals(L2PcInstance player) {
        Inventory inv = player.getInventory();
        for (L2ItemInstance item : inv.getItems()) {
            if (item.getOldVisualItemId() != 0) {
                DressMeHandler.visuality(player, item, item.getOldVisualItemId());
            }
        }
    }

    public static boolean isSiegeActive() {
        List<Castle> castle = CastleManager.getInstance().getCastles();
        for (Castle castle1 : castle) {
            if (castle1.getCastleId() == 10)
                continue;
            if (castle1.getSiege().isInProgress()) {
                return true;
            }
        }
        return false;
    }
}
