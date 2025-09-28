package gabriel.Utils;


import gabriel.config.GabConfig;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ArmorType;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class HeavyLightHelper {

    private static final int[] slotIds = {
            Inventory.PAPERDOLL_CHEST,
            Inventory.PAPERDOLL_HEAD,
            Inventory.PAPERDOLL_GLOVES,
            Inventory.PAPERDOLL_LEGS,
            Inventory.PAPERDOLL_FEET,
    };


    public static void handleHeavyLightClassChange(L2PcInstance player) {
        boolean found = false;

        if (GabConfig.NOTALLOWEDUSELIGHT.contains(player.getClassId().getId())) {
            L2ItemInstance item;
            for (int slotId : slotIds) {
                item = player.getInventory().getPaperdollItem(slotId);
                if (item != null && item.getItemType() == ArmorType.LIGHT) {
                    int slot = item.getItem().getBodyPart();
                    L2ItemInstance[] items = player.getInventory().unEquipItemInBodySlotAndRecord(slot);
                    if (items.length >= 1) {
                        InventoryUpdate iu = new InventoryUpdate();
                        SystemMessage sm = null;
                        if (items[0].getEnchantLevel() > 0) {
                            sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
                            sm.addInt(items[0].getEnchantLevel());
                        } else {
                            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
                        }

                        iu.addItem(item);
                        player.sendPacket(iu);
                        sm.addItemName(items[0]);
                        player.sendPacket(sm);
                        found = true;
                    }
                }
            }
        }
        if (found) {
            CreatureSay cs = new CreatureSay(0, Say2.PARTYROOM_COMMANDER, "Protection System", " " + player.getName() + " not posible use light!");
            player.sendMessage("You can use light only in Oly!");
            player.sendPacket(cs);
        }
    }

    public static void handleLightHeavyClassChange(L2PcInstance player) {
        boolean found = false;
        if (GabConfig.NOTALLOWEDUSEHEAVY.contains(player.getClassId().getId())) {
            L2ItemInstance item;
            for (int slotId : slotIds) {
                item = player.getInventory().getPaperdollItem(slotId);
                if (item != null && item.getItemType() == ArmorType.HEAVY) {
                    int slot = item.getItem().getBodyPart();
                    L2ItemInstance[] items = player.getInventory().unEquipItemInBodySlotAndRecord(slot);
                    if (items.length >= 1) {
                        InventoryUpdate iu = new InventoryUpdate();
                        SystemMessage sm = null;
                        if (items[0].getEnchantLevel() > 0) {
                            sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPMENT_S1_S2_REMOVED);
                            sm.addInt(items[0].getEnchantLevel());
                        } else {
                            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISARMED);
                        }

                        iu.addItem(item);
                        player.sendPacket(iu);
                        sm.addItemName(items[0]);
                        player.sendPacket(sm);
                        found = true;
                    }
                }
            }
        }
        if (found) {
            CreatureSay cs = new CreatureSay(0, Say2.PARTYROOM_COMMANDER, "Protection System", " " + player.getName() + " not posible use heavy!");
            player.sendPacket(cs);
            player.sendMessage("You can use heavy only in Oly!");
        }
    }
}
