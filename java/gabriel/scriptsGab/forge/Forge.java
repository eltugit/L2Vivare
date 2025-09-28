package gabriel.scriptsGab.forge;


import gabriel.config.GabConfig;
import gabriel.others.ElementalValue;
import gabriel.scriptsGab.forge.xml.FoundationHolder;
import gabriel.scriptsGab.utils.BBS;
import gabriel.scriptsGab.utils.ItemFunctions;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.EtcItemType;
import l2r.gameserver.network.serverpackets.InventoryUpdate;

import java.util.HashMap;
import java.util.Map;


public class Forge {
    protected static Forge instance;


    public static Forge getInstance() {
        if (instance == null)
            instance = new Forge();
        return instance;
    }


    public void parseCommand(String command, L2PcInstance player) {
        if (!GabConfig.BBS_FORGE_ENABLED) {
            player.sendMessage("This service is turned off.");
            return;
        }

        String content = "";
        if (command.equals("_bbsforge")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/index.htm");
        } else {
            if (command.equals("_bbsforge:enchant:list")) {
                content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/itemlist.htm");

                L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
                L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
                L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
                L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

                L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

                L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
                L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
                L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
                L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
                L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

                Map<Integer, String[]> data = new HashMap<>();

                data.put(Integer.valueOf(6), ForgeElement.generateEnchant(head, GabConfig.BBS_FORGE_ENCHANT_MAX[1], 6, player));
                data.put(Integer.valueOf(10), ForgeElement.generateEnchant(chest, GabConfig.BBS_FORGE_ENCHANT_MAX[1], 10, player));
                data.put(Integer.valueOf(11), ForgeElement.generateEnchant(legs, GabConfig.BBS_FORGE_ENCHANT_MAX[1], 11, player));
                data.put(Integer.valueOf(9), ForgeElement.generateEnchant(gloves, GabConfig.BBS_FORGE_ENCHANT_MAX[1], 9, player));
                data.put(Integer.valueOf(12), ForgeElement.generateEnchant(feet, GabConfig.BBS_FORGE_ENCHANT_MAX[1], 12, player));

                data.put(Integer.valueOf(5), ForgeElement.generateEnchant(lfinger, GabConfig.BBS_FORGE_ENCHANT_MAX[2], 5, player));
                data.put(Integer.valueOf(4), ForgeElement.generateEnchant(rfinger, GabConfig.BBS_FORGE_ENCHANT_MAX[2], 4, player));
                data.put(Integer.valueOf(3), ForgeElement.generateEnchant(neck, GabConfig.BBS_FORGE_ENCHANT_MAX[2], 3, player));
                data.put(Integer.valueOf(2), ForgeElement.generateEnchant(lear, GabConfig.BBS_FORGE_ENCHANT_MAX[2], 2, player));
                data.put(Integer.valueOf(1), ForgeElement.generateEnchant(rear, GabConfig.BBS_FORGE_ENCHANT_MAX[2], 1, player));

                data.put(Integer.valueOf(7), ForgeElement.generateEnchant(rhand, GabConfig.BBS_FORGE_ENCHANT_MAX[0], 7, player));
                if (rhand != null && rhand.isEnchantable() == 1 && rhand.getItem().getBodyPart() == L2Item.SLOT_LR_HAND) {
                    data.put(Integer.valueOf(8), new String[]
                            {
                                    rhand.getItem().getIcon(),
                                    new StringBuilder().append(rhand.getName()).append(" ").append(rhand.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(rhand.getEnchantLevel()).toString() : "").toString(),
                                    "<font color=\"FF0000\">...</font>",
                                    "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                            });
                } else {
                    data.put(Integer.valueOf(8), ForgeElement.generateEnchant(lhand, GabConfig.BBS_FORGE_ENCHANT_MAX[0], 8, player));
                }


                content = content.replace("%content%", ForgeElement.page(player));

                for (Map.Entry<Integer, String[]> info : data.entrySet()) {
                    int slot = info.getKey();
                    String[] array = info.getValue();
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_icon%").toString(), array[0]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_name%").toString(), array[1]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_button%").toString(), array[2]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_pic%").toString(), array[3]);
                }
            } else if (command.startsWith("_bbsforge:enchant:item:")) {
                String[] array = command.split(":");
                int item = Integer.parseInt(array[3]);

                String name = ItemData.getInstance().getTemplate(GabConfig.BBS_FORGE_ENCHANT_ITEM).getName();

                if (name.isEmpty()) {
                    name = "None Name";
                }
                if ((item < 1) || (item > 12)) {
                    return;
                }
                L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));
                if (_item == null) {
                    player.sendMessage("You removed the item.");
                    parseCommand("_bbsforge:enchant:list", player);
                    return;
                }

                if (_item.isEnchantable() != 1) {
                    player.sendMessage("You can not enchant this item");
                    parseCommand("_bbsforge:enchant:list", player);
                    return;
                }

                content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/enchant.htm");

                String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/enchant_template.htm");

                template = template.replace("{icon}", _item.getItem().getIcon());
                String _name = _item.getName();
                _name = _name.replace(" {PvP}", "");

                if (_name.length() > 30) {
                    _name = new StringBuilder().append(_name.substring(0, 29)).append("...").toString();
                }
                template = template.replace("{name}", _name);
                template = template.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : new StringBuilder().append("+").append(_item.getEnchantLevel()).toString());
                template = template.replace("{msg}", "Select the level of enchanting");

                String button_tm = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/enchant_button_template.htm");
                String button = null;
                String block = null;

                int[] level = _item.isArmor() ? GabConfig.BBS_FORGE_ARMOR_ENCHANT_LVL : _item.isWeapon() ? GabConfig.BBS_FORGE_WEAPON_ENCHANT_LVL : GabConfig.BBS_FORGE_JEWELS_ENCHANT_LVL;
                for (int i = 0; i < level.length; i++) {
                    if (_item.getEnchantLevel() >= level[i])
                        continue;
                    block = button_tm;
                    block = block.replace("{link}", new StringBuilder().append("bypass _bbsforge:enchant:").append(i * item).append(":").append(item).toString());
                    block = block.replace("{value}", new StringBuilder().append("+").append(level[i]).append(" (").append(_item.isArmor() ? GabConfig.BBS_FORGE_ENCHANT_PRICE_ARMOR[i] : _item.isWeapon() ? GabConfig.BBS_FORGE_ENCHANT_PRICE_WEAPON[i] : GabConfig.BBS_FORGE_ENCHANT_PRICE_JEWELS[i]).append(" ").append(name).append(")").toString());
                    button = new StringBuilder().append(button).append(block).toString();
                }

                template = template.replace("{button}", ((button == null) ? "" : button));

                content = content.replace("%content%", template);
            } else if (command.equals("_bbsforge:foundation:list")) {
                content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/foundationlist.htm");

                L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
                L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
                L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
                L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

                L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

                L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
                L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
                L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
                L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
                L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

                Map<Integer, String[]> data = new HashMap<>();

                data.put(Integer.valueOf(6), ForgeElement.generateFoundation(head, 6, player));
                data.put(Integer.valueOf(10), ForgeElement.generateFoundation(chest, 10, player));
                data.put(Integer.valueOf(11), ForgeElement.generateFoundation(legs, 11, player));
                data.put(Integer.valueOf(9), ForgeElement.generateFoundation(gloves, 9, player));
                data.put(Integer.valueOf(12), ForgeElement.generateFoundation(feet, 12, player));

                data.put(Integer.valueOf(5), ForgeElement.generateFoundation(lfinger, 5, player));
                data.put(Integer.valueOf(4), ForgeElement.generateFoundation(rfinger, 4, player));
                data.put(Integer.valueOf(3), ForgeElement.generateFoundation(neck, 3, player));
                data.put(Integer.valueOf(2), ForgeElement.generateFoundation(lear, 2, player));
                data.put(Integer.valueOf(1), ForgeElement.generateFoundation(rear, 1, player));

                data.put(Integer.valueOf(7), ForgeElement.generateFoundation(rhand, 7, player));
                if (rhand != null && (rhand.isEnchantable() == 1) && rhand.getItem().getBodyPart() == L2Item.SLOT_LR_HAND) {
                    data.put(Integer.valueOf(8), new String[]
                            {
                                    rhand.getItem().getIcon(),
                                    new StringBuilder().append(rhand.getName()).append(" ").append(rhand.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(rhand.getEnchantLevel()).toString() : "").toString(),
                                    "<font color=\"FF0000\">...</font>",
                                    "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                            });
                } else {
                    data.put(Integer.valueOf(8), ForgeElement.generateFoundation(lhand, 8, player));
                }
                content = content.replace("%content%", ForgeElement.page(player));

                for (Map.Entry<Integer, String[]> info : data.entrySet()) {
                    int slot = info.getKey();
                    String[] array = info.getValue();
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_icon%").toString(), array[0]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_name%").toString(), array[1]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_button%").toString(), array[2]);
                    content = content.replace(new StringBuilder().append("%").append(slot).append("_pic%").toString(), array[3]);
                }
            } else {
                if (command.startsWith("_bbsforge:foundation:item:")) {
                    String[] array = command.split(":");
                    int item = Integer.parseInt(array[3]);

                    if ((item < 1) || (item > 12)) {
                        return;
                    }
                    L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:foundation:list", player);
                        return;
                    }

                    if (_item.isHeroItem()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        parseCommand("_bbsforge:foundation:list", player);
                        return;
                    }

                    int found = FoundationHolder.getInstance().getFoundation(_item.getId());
                    if (found == -1) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:foundation:list", player);
                        return;
                    }

                    final int price;
                    if (_item.isArmor())
                        price = GabConfig.BBS_FORGE_FOUNDATION_PRICE_ARMOR[_item.getItem().getCrystalType().ordinal()];
                    else if (_item.isWeapon())
                        price = GabConfig.BBS_FORGE_FOUNDATION_PRICE_WEAPON[_item.getItem().getCrystalType().ordinal()];
                    else
                        price = GabConfig.BBS_FORGE_FOUNDATION_PRICE_JEWEL[_item.getItem().getCrystalType().ordinal()];

                    if (player.destroyItemByItemId("Forge", GabConfig.BBS_FORGE_FOUNDATION_ITEM, price, player, true)) {
                        Inventory inv = player.getInventory();
                        L2ItemInstance _found = ItemFunctions.createItem(found);
                        L2ItemInstance temp = _item;
                        if (inv.destroyItemBoolean("Forge", _item.getObjectId(), _item.getCount(), player, null)) {
                            _found.setEnchantLevel(temp.getEnchantLevel());
                            _found.setAugmentation(temp.getAugmentation());

                            if (temp.getElementals() != null) {
                                for (Elementals elemental : temp.getElementals()) {
                                    _found.setElementAttr(elemental.getElement(), elemental.getValue());
                                }
                            }

                            inv.addItem("Forge", _found, player, null);

                            player.getInventory().equipItem(_found);

                            InventoryUpdate iu = new InventoryUpdate();
                            iu.addModifiedItem(_found);
                            player.sendPacket(iu);
                            player.broadcastUserInfo(true);

                            player.sendMessage(new StringBuilder().append("You exchange item ").append(_item.getName()).append(" to Foundation ").append(_found.getName()).toString());
                        } else {
                            _found.deleteMe();
                            player.sendMessage("Foundation failed");
                        }
                    }
                    parseCommand("_bbsforge:foundation:list", player);
                    return;
                }
                if (command.startsWith("_bbsforge:enchant:")) {
                    String[] array = command.split(":");

                    int val = Integer.parseInt(array[2]);
                    int item = Integer.parseInt(array[3]);

                    int conversion = val / item;

                    L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:enchant:list", player);
                        return;
                    }

                    if (_item.isEnchantable() != 1) {
                        player.sendMessage("You can not enchant this item");
                        parseCommand("_bbsforge:enchant:list", player);
                        return;
                    }


                    int[] level = _item.isArmor() ? GabConfig.BBS_FORGE_ARMOR_ENCHANT_LVL : _item.isWeapon() ? GabConfig.BBS_FORGE_WEAPON_ENCHANT_LVL : GabConfig.BBS_FORGE_JEWELS_ENCHANT_LVL;
                    int Value = level[conversion];

                    int max = _item.isArmor() ? GabConfig.BBS_FORGE_ENCHANT_MAX[1] : _item.isWeapon() ? GabConfig.BBS_FORGE_ENCHANT_MAX[0] : GabConfig.BBS_FORGE_ENCHANT_MAX[2];
                    if (Value > max) {
                        return;
                    }
                    if (_item.getItemType() == EtcItemType.ARROW) {
                        player.sendMessage("You can not enchant the arrows.");
                        parseCommand("_bbsforge:enchant:list", player);
                        return;
                    }

                    int price = _item.isArmor() ? GabConfig.BBS_FORGE_ENCHANT_PRICE_ARMOR[conversion] : _item.isWeapon() ? GabConfig.BBS_FORGE_ENCHANT_PRICE_WEAPON[conversion] : GabConfig.BBS_FORGE_ENCHANT_PRICE_JEWELS[conversion];

                    if (player.destroyItemByItemId("Forge", GabConfig.BBS_FORGE_ENCHANT_ITEM, price, player, true)) {

                        player.getInventory().unEquipItemInBodySlot(player.getInventory().getSlotFromItem(_item));
                        _item.setEnchantLevel(Value);
                        player.getInventory().equipItem(_item);

                        InventoryUpdate iu = new InventoryUpdate();
                        iu.addModifiedItem(_item);
                        player.sendPacket(iu);
                        player.broadcastUserInfo(true);

                        player.sendMessage(String.format("%s was enchante to +%d. Thank you!", _item.getName(), Value));
                    }

                    parseCommand("_bbsforge:enchant:list", player);
                    return;
                }
                if (command.equals("_bbsforge:attribute:list")) {
                    content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/attributelist.htm");

                    L2ItemInstance head = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HEAD);
                    L2ItemInstance chest = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                    L2ItemInstance legs = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
                    L2ItemInstance gloves = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
                    L2ItemInstance feet = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_FEET);

                    L2ItemInstance lhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                    L2ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

                    L2ItemInstance lfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LFINGER);
                    L2ItemInstance rfinger = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RFINGER);
                    L2ItemInstance neck = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_NECK);
                    L2ItemInstance lear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEAR);
                    L2ItemInstance rear = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_REAR);

                    Map<Integer, String[]> data = new HashMap<>();

                    data.put(Integer.valueOf(6), ForgeElement.generateAttribution(head, 6, player, true));
                    data.put(Integer.valueOf(10), ForgeElement.generateAttribution(chest, 10, player, true));
                    data.put(Integer.valueOf(11), ForgeElement.generateAttribution(legs, 11, player, true));
                    data.put(Integer.valueOf(9), ForgeElement.generateAttribution(gloves, 9, player, true));
                    data.put(Integer.valueOf(12), ForgeElement.generateAttribution(feet, 12, player, true));

                    data.put(Integer.valueOf(5), ForgeElement.generateAttribution(lfinger, 5, player, true));
                    data.put(Integer.valueOf(4), ForgeElement.generateAttribution(rfinger, 4, player, true));
                    data.put(Integer.valueOf(3), ForgeElement.generateAttribution(neck, 3, player, true));
                    data.put(Integer.valueOf(2), ForgeElement.generateAttribution(lear, 2, player, true));
                    data.put(Integer.valueOf(1), ForgeElement.generateAttribution(rear, 1, player, true));

                    data.put(Integer.valueOf(7), ForgeElement.generateAttribution(rhand, 7, player, true));
                    if (rhand != null &&
                            (rhand.isElementable()) && rhand.getItem().getBodyPart() == L2Item.SLOT_LR_HAND) {
                        data.put(Integer.valueOf(8), new String[]
                                {
                                        rhand.getItem().getIcon(),
                                        new StringBuilder().append(rhand.getName()).append(" ").append(rhand.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(rhand.getEnchantLevel()).toString() : "").toString(),
                                        "<font color=\"FF0000\">...</font>",
                                        "L2UI_CT1.ItemWindow_DF_SlotBox_Disable"
                                });
                    } else {
                        data.put(Integer.valueOf(8), ForgeElement.generateAttribution(lhand, 8, player, true));
                    }
                    content = content.replace("%content%", ForgeElement.page(player));

                    for (Map.Entry<Integer, String[]> info : data.entrySet()) {
                        int slot = info.getKey();
                        String[] array = info.getValue();
                        content = content.replace(new StringBuilder().append("%").append(slot).append("_icon%").toString(), array[0]);
                        content = content.replace(new StringBuilder().append("%").append(slot).append("_name%").toString(), array[1]);
                        content = content.replace(new StringBuilder().append("%").append(slot).append("_button%").toString(), array[2]);
                        content = content.replace(new StringBuilder().append("%").append(slot).append("_pic%").toString(), array[3]);
                    }
                } else if (command.startsWith("_bbsforge:attribute:item:")) {
                    String[] array = command.split(":");
                    int item = Integer.parseInt(array[3]);

                    if ((item < 1) || (item > 12)) {
                        return;
                    }
                    L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));
                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage("You can not enchant this grade.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (_item.isHeroItem()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (!_item.isElementable()) {
                        player.sendMessage("You cannot enchant this item.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/attribute.htm");

                    String slotclose = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
                    String buttonFire = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:0:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();
                    String buttonWater = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:1:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();
                    String buttonWind = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:2:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();
                    String buttonEarth = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:3:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();
                    String buttonHoly = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:4:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();
                    String buttonUnholy = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:element:5:").append(item).append("\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>").toString();

                    if (_item.isWeapon()) {

                        if (ElementalValue.getElementalValue(_item, Elementals.FIRE) > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.WATER) > 0) {
                            buttonFire = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.WIND) > 0) {
                            buttonWater = slotclose;
                            buttonFire = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.EARTH) > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonFire = slotclose;
                            buttonHoly = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.HOLY) > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonFire = slotclose;
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.DARK) > 0) {
                            buttonWater = slotclose;
                            buttonWind = slotclose;
                            buttonEarth = slotclose;
                            buttonHoly = slotclose;
                            buttonFire = slotclose;
                        }
                    }

                    if (_item.isArmor()) {
                        if (ElementalValue.getElementalValue(_item, Elementals.FIRE) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.FIRE) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonFire = slotclose;
                            }
                            buttonWater = slotclose;
                        }

                        if (ElementalValue.getElementalValue(_item, Elementals.WATER) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.WATER) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonWater = slotclose;
                            }
                            buttonFire = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.WIND) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.WIND) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonWind = slotclose;
                            }
                            buttonEarth = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.EARTH) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.EARTH) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonEarth = slotclose;
                            }
                            buttonWind = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.HOLY) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.HOLY) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonHoly = slotclose;
                            }
                            buttonUnholy = slotclose;
                        }
                        if (ElementalValue.getElementalValue(_item, Elementals.DARK) > 0) {
                            if (ElementalValue.getElementalValue(_item, Elementals.DARK) >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX) {
                                buttonUnholy = slotclose;
                            }
                            buttonHoly = slotclose;
                        }
                    }

                    String html = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/attribute_choice_template.htm");

                    html = html.replace("{icon}", _item.getItem().getIcon());
                    String _name = _item.getName();
                    _name = _name.replace(" {PvP}", "");

                    if (_name.length() > 30) {
                        _name = new StringBuilder().append(_name.substring(0, 29)).append("...").toString();
                    }
                    html = html.replace("{name}", _name);
                    html = html.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : new StringBuilder().append(" +").append(_item.getEnchantLevel()).toString());
                    html = html.replace("{msg}", "Select the attribute");
                    html = html.replace("{fire}", buttonFire);
                    html = html.replace("{water}", buttonWater);
                    html = html.replace("{earth}", buttonEarth);
                    html = html.replace("{wind}", buttonWind);
                    html = html.replace("{holy}", buttonHoly);
                    html = html.replace("{unholy}", buttonUnholy);

                    content = content.replace("%content%", html);
                } else if (command.startsWith("_bbsforge:attribute:element:")) {
                    String[] array = command.split(":");
                    int element = Integer.parseInt(array[3]);

                    String elementName = "";
                    if (element == 0)
                        elementName = "Attribute Fire";
                    else if (element == 1)
                        elementName = "Attribute Water";
                    else if (element == 2)
                        elementName = "Attribute Wind";
                    else if (element == 3)
                        elementName = "Attribute Earth";
                    else if (element == 4)
                        elementName = "Attribute Holy";
                    else if (element == 5) {
                        elementName = "Attribute Dark";
                    }
                    int item = Integer.parseInt(array[4]);

                    String name = ItemData.getInstance().getTemplate(GabConfig.BBS_FORGE_ENCHANT_ITEM).getName();

                    if (name.isEmpty()) {
                        name = "None Name";
                    }
                    L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));

                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage("You can not enchant this grade.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (_item.isHeroItem()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/attribute.htm");
                    String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/enchant_template.htm");

                    template = template.replace("{icon}", _item.getItem().getIcon());
                    String _name = _item.getName();
                    _name = _name.replace(" {PvP}", "");

                    if (_name.length() > 30) {
                        _name = new StringBuilder().append(_name.substring(0, 29)).append("...").toString();
                    }
                    template = template.replace("{name}", _name);
                    template = template.replace("{enchant}", _item.getEnchantLevel() <= 0 ? "" : new StringBuilder().append("+").append(_item.getEnchantLevel()).toString());
                    template = template.replace("{msg}", "Selected: " + elementName);

                    String button_tm = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/enchant_button_template.htm");
                    StringBuilder button = new StringBuilder();
                    String block = null;

                    int[] level = _item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_LVL_WEAPON : GabConfig.BBS_FORGE_ATRIBUTE_LVL_ARMOR;
                    for (int i = 0; i < level.length; i++) {
                        if (ElementalValue.getElementalValue(_item, (byte) element) >= (_item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_LVL_WEAPON[i] : GabConfig.BBS_FORGE_ATRIBUTE_LVL_ARMOR[i]))
                            continue;
                        block = button_tm;
                        block = block.replace("{link}", String.valueOf(new StringBuilder().append("bypass _bbsforge:attribute:").append(i * item).append(":").append(item).append(":").append(element).toString()));
                        block = block.replace("{value}", new StringBuilder().append("+").append(_item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_LVL_WEAPON[i] : GabConfig.BBS_FORGE_ATRIBUTE_LVL_ARMOR[i]).append(" (").append(_item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_PRICE_WEAPON[i] : GabConfig.BBS_FORGE_ATRIBUTE_PRICE_ARMOR[i]).append(" ").append(name).append(")").toString());
                        button.append(block);
                    }

                    template = template.replace("{button}", button.toString());

                    content = content.replace("%content%", template);
                } else if (command.startsWith("_bbsforge:attribute:")) {
                    String[] array = command.split(":");
                    int val = Integer.parseInt(array[2]);
                    int item = Integer.parseInt(array[3]);
                    int att = Integer.parseInt(array[4]);

                    L2ItemInstance _item = player.getInventory().getPaperdollItem(ItemFunctions.getRightSlot(item));

                    if (_item == null) {
                        player.sendMessage("You removed the item.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (!ForgeElement.itemCheckGrade(true, _item)) {
                        player.sendMessage("You can not enchant this grade.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if (_item.isHeroItem()) {
                        player.sendMessage("You can not enchant the weapons of heroes.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    if ((_item.isArmor()) && (!ForgeElement.canEnchantArmorAttribute(att, _item))) {
                        player.sendMessage("Can not insert attribute not comply with the terms.");
                        parseCommand("_bbsforge:attribute:list", player);
                        return;
                    }

                    int conversion = val / item;

                    int Value = _item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_LVL_WEAPON[conversion] : GabConfig.BBS_FORGE_ATRIBUTE_LVL_ARMOR[conversion];

                    if (Value > (_item.isWeapon() ? GabConfig.BBS_FORGE_WEAPON_ATTRIBUTE_MAX : GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX)) {
                        return;
                    }
                    int price = _item.isWeapon() ? GabConfig.BBS_FORGE_ATRIBUTE_PRICE_WEAPON[conversion] : GabConfig.BBS_FORGE_ATRIBUTE_PRICE_ARMOR[conversion];

                    if (player.destroyItemByItemId("Forge", GabConfig.BBS_FORGE_ENCHANT_ITEM, price, player, true)) {
                        player.getInventory().unEquipItemInBodySlot(player.getInventory().getSlotFromItem(_item));

                        _item.setElementAttr((byte) att, Value);

                        player.getInventory().equipItem(_item);

                        InventoryUpdate iu = new InventoryUpdate();
                        iu.addModifiedItem(_item);
                        player.sendPacket(iu);
                        player.broadcastUserInfo(true);

                        String elementName = "";
                        if (att == 0)
                            elementName = "Attribute Fire";
                        else if (att == 1)
                            elementName = "Attribute Water";
                        else if (att == 2)
                            elementName = "Attribute DaWindrk";
                        else if (att == 3)
                            elementName = "Attribute Earth";
                        else if (att == 4)
                            elementName = "Attribute Holy";
                        else if (att == 5) {
                            elementName = "Attribute Dark";
                        }
                        player.sendMessage(String.format("In %s was added %s +%d.", _item.getName(), elementName, Value));
                    }

                    parseCommand("_bbsforge:attribute:list", player);
                    return;
                }
            }
        }
        BBS.separateAndSend(content, player);
    }
}
