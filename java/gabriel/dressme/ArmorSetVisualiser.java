package gabriel.dressme;


import gabriel.dressmeEngine.data.DressMeArmorData;
import gabriel.dressmeEngine.xml.dataHolder.DressMeArmorHolder;
import l2r.gameserver.data.xml.impl.ArmorSetsData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.model.L2ArmorSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import java.util.Map;


public class ArmorSetVisualiser {

    protected static ArmorSetVisualiser instance;


    public static ArmorSetVisualiser getInstance() {
        if (instance == null)
            instance = new ArmorSetVisualiser();
        return instance;
    }


    public static boolean hasArmorSetEquipped(L2PcInstance player, int slot, int itemId) {
        Map<Integer, L2ArmorSet> armorSet = ArmorSetsData.getInstance().getSetByItemId(itemId, slot);
        boolean ok = false;
        if (armorSet == null) {
            return ok;
        }
        for (Map.Entry<Integer, L2ArmorSet> entry : armorSet.entrySet()) {
            if (!ok) {
                ok = entry.getValue().containAllInclusChest(player);
            }
        }
        return ok;
    }


    public static boolean hasArmorEquipped(L2PcInstance player) {
        Inventory inv = player.getInventory();

        L2ItemInstance legsItem = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
        L2ItemInstance chestItem = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        L2ItemInstance glovesItem = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
        L2ItemInstance feetItem = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

        int legs = 0;
        int gloves = 0;
        int feet = 0;
        int chest = 0;

        if (legsItem != null) {
            legs = legsItem.getId();
        }
        if (glovesItem != null) {
            gloves = glovesItem.getId();
        }
        if (feetItem != null) {
            feet = feetItem.getId();
        }
        if (chestItem != null) {
            chest = chestItem.getId();
        }

        return chest != 0 && legs != 0 && gloves != 0 && feet != 0;
    }


    public static boolean isArmorSetSlot(int slot) {
        switch (slot) {
            case Inventory.PAPERDOLL_CHEST:
            case Inventory.PAPERDOLL_LEGS:
            case Inventory.PAPERDOLL_GLOVES:
            case Inventory.PAPERDOLL_FEET:
            case Inventory.PAPERDOLL_HEAD:
                return true;
            default:
                return false;
        }
    }


    public static boolean isSuitEquipped(int slot, L2PcInstance player) {
        if (slot == Inventory.PAPERDOLL_CHEST) {
            L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            if (chest == null)
                return false;
            if (chest.getVisualItemId() == 0)
                return false;
            DressMeArmorData data = DressMeArmorHolder.getInstance().getArmorByPartId(chest.getVisualItemId());
            return data != null;
        }
        return false;
    }


    public static int[] getRightDressForChest(L2PcInstance player) {
        L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
        L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
        L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

        DressMeArmorData data = DressMeArmorHolder.getInstance().getArmorByPartId(chest.getVisualItemId());
        if (data != null) {
            if (chest.getVisualItemId() == 59999)
                return new int[]{0, 0, 0, 0};
            else if (data.isSuit())
                return new int[]{data.getGloves() > 0 ? data.getGloves() : 0, data.getChest(), data.getLegs() > 0 ? data.getLegs() : 0, data.getFeet() > 0 ? data.getFeet() : 0};
            else
                return new int[]{data.getGloves(), data.getChest(), data.getLegs() == -1 ? 0 : data.getLegs(), data.getFeet()};
        }
        return new int[]{gloves == null ? 0 : gloves.getDisplayId(), chest == null ? 0 : chest.getDisplayId(), legs == null ? 0 : legs.getDisplayId(), feet == null ? 0 : feet.getDisplayId()};
    }


    public static boolean isChest(int itemId) {
        L2Item item = ItemData.getInstance().getTemplate(itemId);
        if (item == null)
            return false;
        return item.getBodyPart() == L2Item.SLOT_CHEST || item.getBodyPart() == L2Item.SLOT_FULL_ARMOR || item.getBodyPart() == L2Item.SLOT_ALLDRESS;
    }


    public static boolean isOtherPieces(int itemId) {
        L2Item item = ItemData.getInstance().getTemplate(itemId);
        if (item == null)
            return false;

        switch (item.getBodyPart()) {
            case L2Item.SLOT_CHEST:
            case L2Item.SLOT_FULL_ARMOR:
            case L2Item.SLOT_ALLDRESS:
            case L2Item.SLOT_LEGS:
            case L2Item.SLOT_GLOVES:
            case L2Item.SLOT_FEET:
                return true;
            default:
                return false;
        }
    }


    public static int getIntForPcInventory(int itemId, int itemVisualId) {
        DressMeArmorData data = DressMeArmorHolder.getInstance().getArmorByPartId(itemVisualId);
        if (data == null)
            return itemId;

        L2Item item = ItemData.getInstance().getTemplate(itemId);
        if (item == null)
            return itemId;

        switch (item.getBodyPart()) {
            case L2Item.SLOT_CHEST:
            case L2Item.SLOT_FULL_ARMOR:
            case L2Item.SLOT_ALLDRESS:
                return data.getChest();
            case L2Item.SLOT_LEGS:
                return data.getLegs();
            case L2Item.SLOT_GLOVES:
                return data.getGloves();
            case L2Item.SLOT_FEET:
                return data.getFeet();
            default:
                return itemId;
        }
    }


    public static int getIntForPcInventorySLOT(int slot, int itemVisualId) {
        DressMeArmorData data = DressMeArmorHolder.getInstance().getArmorByPartId(itemVisualId);
        if (data == null)
            return 0;

        switch (slot) {
            case L2Item.SLOT_CHEST:
            case L2Item.SLOT_FULL_ARMOR:
            case L2Item.SLOT_ALLDRESS:
                return data.getChest();
            case Inventory.PAPERDOLL_LEGS:
                return data.getLegs();
            case Inventory.PAPERDOLL_GLOVES:
                return data.getGloves();
            case Inventory.PAPERDOLL_FEET:
                return data.getFeet();
            default:
                return 0;
        }
    }
}
