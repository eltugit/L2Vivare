package gabriel.scriptsGab.utils;

import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.enums.ItemLocation;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;

public class ItemFunctions {
    public static long getItemCount(L2Playable playable, int itemId) {
        if (playable == null)
            return 0;
        L2Playable player = playable.getActingPlayer();
        return player.getInventory().getInventoryItemCount(itemId, 0);
    }

    public static L2ItemInstance createItem(int itemId) {
        L2ItemInstance item = new L2ItemInstance(IdFactory.getInstance().getNextId(), itemId);
        item.setItemLocation(ItemLocation.VOID);
        item.setCount(1L);

        return item;
    }
    public static L2ItemInstance addItem2(L2Playable playable, int itemId, long count, boolean notify, String log) {
        if (playable == null || count < 1)
            return null;

        L2PcInstance player = playable.getActingPlayer();

        L2Item t = ItemData.getInstance().getTemplate(itemId);
        if (t == null) {
            player.sendMessage("Incorrect item id: " + itemId + ". Contact a GM");
            return null;
        }

        if (t.isStackable())
            return player.addItem("ItemFunction", itemId, count, null, notify);
        else
            for (int i = 0; i < count-1; i++)
                player.addItem("ItemFunction", itemId, 1, null, notify);
        return player.addItem("ItemFunction", itemId, 1, null, notify);

    }
    public static int getRightSlot(int slot) {
        switch (slot) {
            case 1:
                return Inventory.PAPERDOLL_REAR;
            case 2:
                return Inventory.PAPERDOLL_LEAR;
            case 3:
                return Inventory.PAPERDOLL_NECK;
            case 4:
                return Inventory.PAPERDOLL_RFINGER;
            case 5:
                return Inventory.PAPERDOLL_LFINGER;
            case 6:
                return Inventory.PAPERDOLL_HEAD;
            case 7:
                return Inventory.PAPERDOLL_RHAND;
            case 8:
                return Inventory.PAPERDOLL_LHAND;
            case 9:
                return Inventory.PAPERDOLL_GLOVES;
            case 10:
                return Inventory.PAPERDOLL_CHEST;
            case 11:
                return Inventory.PAPERDOLL_LEGS;
            case 12:
                return Inventory.PAPERDOLL_FEET;
        }
        return -1;
    }

}
