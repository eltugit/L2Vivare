package gabriel.dressmeEngine.handler;

import gabriel.TryDressMeCustom;
import gabriel.Utils.Visuals;
import gabriel.config.GabConfig;
import gabriel.dressme.HandleDressMeDb;
import gabriel.dressmeEngine.DressMeHandler;
import gabriel.dressmeEngine.DressMeLoader;
import gabriel.dressmeEngine.data.DressMeWeaponData;
import gabriel.dressmeEngine.util.Util;
import gabriel.dressmeEngine.xml.dataHolder.DressMeArmorHolder;
import gabriel.scriptsGab.gab.GabrielCBB;
import gr.sr.main.Conditions;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.ArmorSetsData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.L2ArmorSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ItemType;
import l2r.gameserver.network.clientpackets.RequestPreviewItem;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DressMeVCmd implements IVoicedCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(DressMeVCmd.class);

    String index_path = "data/html/sunrise/dressme/index.htm";
    String info_path = "data/html/sunrise/dressme/info.htm";
    String undressme_path = "data/html/sunrise/dressme/undressme.htm";

    String index_armor_path = "data/html/sunrise/dressme/index-armor.htm";
    String template_armor_path = "data/html/sunrise/dressme/template-armor.htm";

    String index_cloak = "data/html/sunrise/dressme/index-cloak.htm";
    String template_cloak_path = "data/html/sunrise/dressme/template-cloak.htm";

    String index_shield_path = "data/html/sunrise/dressme/index-shield.htm";
    String template_shield_path = "data/html/sunrise/dressme/template-shield.htm";

    String index_weapon_path = "data/html/sunrise/dressme/index-weapon.htm";
    String template_weapon_path = "data/html/sunrise/dressme/template-weapon.htm";

    String index_hat_path = "data/html/sunrise/dressme/index-hat.htm";
    String template_hat_path = "data/html/sunrise/dressme/template-hat.htm";

    String dress_cloak_path = "data/html/sunrise/dressme/dress-cloak.htm";
    String dress_shield_path = "data/html/sunrise/dressme/dress-shield.htm";
    String dress_armor_path = "data/html/sunrise/dressme/dress-armor.htm";
    String dress_weapon_path = "data/html/sunrise/dressme/dress-weapon.htm";
    String dress_hat_path = "data/html/sunrise/dressme/dress-hat.htm";

    String index_enchant_path = "data/html/sunrise/dressme/index-enchant.htm";
    String template_enchant_path = "data/html/sunrise/dressme/template-enchant.htm";
    String dress_enchant_path = "data/html/sunrise/dressme/dress-enchant.htm";

    String index_aga_path = "data/html/sunrise/dressme/index-agathion.htm";
    String template_aga_path = "data/html/sunrise/dressme/template-agathion.htm";
    String dress_aga_path = "data/html/sunrise/dressme/dress-agathion.htm";

    private final String[] _commandList = new String[]
            {
                    "dressme",
                    "undressme",

                    "dressinfo",

                    "showdress",
                    "hidedress",

                    "dressme-armor",
                    "dress-armor",
                    "dress-armorpage",
                    "undressme-armor",

                    "dressme-cloak",
                    "dress-cloak",
                    "dress-cloakpage",
                    "undressme-cloak",

                    "dressme-shield",
                    "dress-shield",
                    "dress-shieldpage",
                    "undressme-shield",

                    "dressme-weapon",
                    "dress-weapon",
                    "dress-weaponpage",
                    "undressme-weapon",

                    "dressme-hat",
                    "dress-hat",
                    "dress-hatpage",
                    "undressme-hat",

                    "dress-tryarmor",
                    "dress-trycloak",
                    "dress-tryshield",
                    "dress-tryweapon",
                    "dress-tryhat",

                    "dressme-enchant",
                    "dress-enchantpage",
                    "dress-enchant",
                    "dress-tryenchant",
                    "undressme-enchant",

                    "dressme-agathion",
                    "dress-agathionpage",
                    "dress-agathion",
                    "dress-tryagathion",
                    "undressme-agathion",

                    "dressme-suitarmor"
            };

    @Override
    public boolean useVoicedCommand(String command, L2PcInstance player, String args) {
        if (command.equals("dressme")) {
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
//            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_path);
//            html = html.replace("<?show_hide?>", player.getVarB("showVisualChange") ? "Show visual equip on other player!" : "Hide visual equip on other player!");
//            html = html.replace("<?show_hide_b?>", !player.getVarB("showVisualChange") ? "showdress" : "hidedress");
//            player.deleteQuickVar("suitDress");
//            sendHtml(player, html);
            return true;
        } else if (command.equals("dressme-suitarmor")) {
            L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            if ((slot == null) || !slot.isArmor()) {
                player.sendMessage("Error: Armor chest must be equiped!");
                return false;
            }

            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_armor_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_armor_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            String type = "SUIT";
//            Map<Integer, DressMeArmorData> map = HandleDressMeDb.order(player, DressMeHandler.initArmorMap(type, new LinkedHashMap<>(), slot), "character_dressme_armor_list", "dressId");
            List<gabriel.dressmeEngine.data.DressMeArmorData> map = HandleDressMeDb.orderL(player, DressMeHandler.initArmorMap(type, new HashMap<>(), slot), "character_dressme_armor_list", "dressId");
            if (map == null) {
                _log.error("Dress me system: Armor Map is null.");
                return false;
            }

            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeArmorData dress = map.get(i+1);
                gabriel.dressmeEngine.data.DressMeArmorData dress = map.get(i);
                if (dress != null) {
                    block = template;

                    String dress_name = dress.getName();

                    if (dress_name.length() > 29) {
                        dress_name = dress_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-armorpage " + dress.getId());
                    block = block.replace("{name}", dress_name);


                    if (HandleDressMeDb.dressMeArmorInside(player, dress)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(dress.getChest()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-suitarmor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-suitarmor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            NpcHtmlMessage msg = new NpcHtmlMessage();
            msg.setHtml(html);
            player.sendPacket(msg);
            return true;
        } else if (command.equals("dressme-armor")) {
            L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            if ((slot == null) || !slot.isArmor()) {
                player.sendMessage("Error: Armor chest must be equiped!");
                return false;
            }

            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_armor_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_armor_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            String type = slot.getArmorItem().getItemType().getDescription();
//            Map<Integer, DressMeArmorData> map = HandleDressMeDb.order(player, DressMeHandler.initArmorMap(type, new LinkedHashMap<>(), slot), "character_dressme_armor_list", "dressId");
            List<gabriel.dressmeEngine.data.DressMeArmorData> map = HandleDressMeDb.orderL(player, DressMeHandler.initArmorMap(type, new HashMap<>(), slot), "character_dressme_armor_list", "dressId");
            if (map == null) {
                _log.error("Dress me system: Armor Map is null.");
                return false;
            }

            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeArmorData dress = map.get(i+1);
                gabriel.dressmeEngine.data.DressMeArmorData dress = map.get(i);
                if (dress != null) {
                    block = template;

                    String dress_name = dress.getName();

                    if (dress_name.length() > 29) {
                        dress_name = dress_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-armorpage " + dress.getId());
                    block = block.replace("{name}", dress_name);


                    if (HandleDressMeDb.dressMeArmorInside(player, dress)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(dress.getChest()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-armor " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            NpcHtmlMessage msg = new NpcHtmlMessage();
            msg.setHtml(html);
            player.sendPacket(msg);
            return true;
        } else if (command.equals("dressme-cloak")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_cloak);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_cloak_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;
//            Map<Integer, DressMeCloakData> map = HandleDressMeDb.order(player, DressMeLoader.CLOAKS, "character_dressme_cloak_list", "cloakDressId");
            List<gabriel.dressmeEngine.data.DressMeCloakData> map = HandleDressMeDb.orderL(player, DressMeLoader.CLOAKS, "character_dressme_cloak_list", "cloakDressId");
            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeCloakData cloak = map.get(i+1);
                gabriel.dressmeEngine.data.DressMeCloakData cloak = map.get(i);
                if (cloak != null) {
                    block = template;

                    String cloak_name = cloak.getName();

                    if (cloak_name.length() > 29) {
                        cloak_name = cloak_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-cloakpage " + cloak.getId());
                    block = block.replace("{name}", cloak_name);

                    if (HandleDressMeDb.dressMeCloakInside(player, cloak)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(cloak.getCloakId()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-cloak " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            sendHtml(player, html);
            return true;
        } else if (command.equals("dressme-shield")) {
            try {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_shield_path);
                String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_shield_path);
                String block = "";
                String list = "";

                if (args == null) {
                    args = "1";
                }

                String[] param = args.split(" ");

                final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
                final int perpage = 5;
                int counter = 0;
                L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                if (slot == null) {
                    player.sendMessage("Please equip an Shield or Sigil!");
                    return false;
                }

                String type = slot.getArmorItem().getItemType().getDescription();
                if (type == null) {
                    return false;
                }
//            Map<Integer, DressMeShieldData> map = HandleDressMeDb.order(player, DressMeHandler.initShieldMap(type, new LinkedHashMap<>(), slot), "character_dressme_shield_list", "shieldDressId");
                List<gabriel.dressmeEngine.data.DressMeShieldData> map = HandleDressMeDb.orderL(player, DressMeHandler.initShieldMap(type, new LinkedHashMap<>(), slot), "character_dressme_shield_list", "shieldDressId");

                for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeShieldData shield = map.get(i + 1);
                    gabriel.dressmeEngine.data.DressMeShieldData shield = map.get(i);
                    if (shield != null) {
                        block = template;

                        String shield_name = shield.getName();

                        if (shield_name.length() > 29) {
                            shield_name = shield_name.substring(0, 29) + "...";
                        }

                        block = block.replace("{bypass}", "bypass -h voice .dress-shieldpage " + shield.getId());
                        block = block.replace("{name}", shield_name);

                        if (HandleDressMeDb.dressMeShieldInside(player, shield)) {
                            block = block.replace("{price}", "Owned");
                        } else {
                            block = block.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
                        }
                        block = block.replace("{icon}", Util.getItemIcon(shield.getShieldId()));
                        list += block;
                    }

                    counter++;

                    if (counter >= perpage) {
                        break;
                    }
                }

                double count = Math.ceil((double) map.size() / perpage);
                int inline = 1;
                String navigation = "";

                for (int i = 1; i <= count; i++) {
                    if (i == page) {
                        navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                    } else {
                        navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-shield " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                    }

                    if (inline == 7) {
                        navigation += "</tr><tr>";
                        inline = 0;
                    }
                    inline++;
                }

                if (navigation.equals("")) {
                    navigation = "<td width=30 align=center valign=top>...</td>";
                }

                html = html.replace("{list}", list);
                html = html.replace("{navigation}", navigation);

                sendHtml(player, html);
                return true;
            } catch (Exception e) {
                //
            }

        } else if (command.equals("dressme-weapon")) {
            L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
            if (slot == null) {
                player.sendMessage("Error: Weapon must be equiped!");
                return false;
            }

            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_weapon_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_weapon_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

            ItemType type = slot.getItemType();
//            Map<Integer, DressMeWeaponData> map = HandleDressMeDb.order(player, DressMeHandler.initWeaponMap(type.toString(), new LinkedHashMap<>(), slot), "character_dressme_weapon_list", "weaponDressId");
            List<gabriel.dressmeEngine.data.DressMeWeaponData> map = HandleDressMeDb.orderL(player, DressMeHandler.initWeaponMap(type.toString(), new LinkedHashMap<>(), slot), "character_dressme_weapon_list", "weaponDressId");

            if (map == null) {
                _log.error("Dress me system: Weapon Map is null.");
                return false;
            }

            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeWeaponData weapon = map.get(i + 1);
                gabriel.dressmeEngine.data.DressMeWeaponData weapon = map.get(i);
                if (weapon != null) {
                    block = template;

                    String cloak_name = weapon.getName();

                    if (cloak_name.length() > 29) {
                        cloak_name = cloak_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-weaponpage " + weapon.getId());
                    block = block.replace("{name}", cloak_name);
                    if (HandleDressMeDb.dressMeWeaponInside(player, weapon)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(weapon.getId()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-weapon " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            sendHtml(player, html);
            return true;
        } else if (command.equals("dressme-hat")) {
            L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR);
            if (slot == null) {
                slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
            }

            if (slot == null) {
                player.sendMessage("Error: Hat must be equiped!");
                return false;
            }

            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_hat_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_hat_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;

//            Map<Integer, DressMeHatData> map = HandleDressMeDb.order(player, DressMeHandler.initHatMap(new LinkedHashMap<>(), slot), "character_dressme_hat_list", "hatDressId");
            List<gabriel.dressmeEngine.data.DressMeHatData> map = HandleDressMeDb.orderL(player, DressMeHandler.initHatMap(new LinkedHashMap<>(), slot), "character_dressme_hat_list", "hatDressId");

            if (map == null) {
                _log.error("Dress me system: Hat Map is null.");
                return false;
            }

            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeHatData hat = map.get(i + 1);
                gabriel.dressmeEngine.data.DressMeHatData hat = map.get(i);
                if (hat != null) {
                    block = template;

                    String hat_name = hat.getName();

                    if (hat_name.length() > 29) {
                        hat_name = hat_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-hatpage " + hat.getId());
                    block = block.replace("{name}", hat_name);
                    if (HandleDressMeDb.dressMeHatInside(player, hat)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, hat.getPriceCount(), hat.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(hat.getHatId()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-hat " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-hat " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            sendHtml(player, html);
            return true;
        } else if (command.equals("dress-armorpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeArmorData dress = gabriel.dressmeEngine.xml.dataHolder.DressMeArmorHolder.getInstance().getArmor(set);
            if (dress != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_armor_path);

                Inventory inv = player.getInventory();

                L2ItemInstance my_chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
                html = html.replace("{my_chest_icon}", my_chest == null ? "icon.NOIMAGE" : my_chest.getItem().getIcon());
                L2ItemInstance my_legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
                html = html.replace("{my_legs_icon}", my_legs == null ? "icon.NOIMAGE" : my_legs.getItem().getIcon());
                L2ItemInstance my_gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
                html = html.replace("{my_gloves_icon}", my_gloves == null ? "icon.NOIMAGE" : my_gloves.getItem().getIcon());
                L2ItemInstance my_feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);
                html = html.replace("{my_feet_icon}", my_feet == null ? "icon.NOIMAGE" : my_feet.getItem().getIcon());

                html = html.replace("{bypass}", "bypass -h voice .dress-armor " + set);
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryarmor " + set);
                html = html.replace("{name}", dress.getName());
                if (HandleDressMeDb.dressMeArmorInside(player, dress)) {
                    html = html.replace("{price}", "WardRobe");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, dress.getPriceCount(), dress.getPriceId()));
                }

                L2Item chest = ItemData.getInstance().getTemplate(dress.getChest());
                html = html.replace("{chest_icon}", chest.getIcon());
                html = html.replace("{chest_name}", chest.getName());
                html = html.replace("{chest_grade}", chest.getItemGrade().name());

                if (dress.getLegs() != -1) {
                    L2Item legs = ItemData.getInstance().getTemplate(dress.getLegs());
                    html = html.replace("{legs_icon}", legs.getIcon());
                    html = html.replace("{legs_name}", legs.getName());
                    html = html.replace("{legs_grade}", legs.getItemGrade().name());
                } else {
                    html = html.replace("{legs_icon}", "icon.NOIMAGE");
                    html = html.replace("{legs_name}", "<font color=FF0000>...</font>");
                    html = html.replace("{legs_grade}", "NO");
                }
                if (dress.getGloves() != -1) {
                    L2Item gloves = ItemData.getInstance().getTemplate(dress.getGloves());
                    html = html.replace("{gloves_icon}", gloves.getIcon());
                    html = html.replace("{gloves_name}", gloves.getName());
                    html = html.replace("{gloves_grade}", gloves.getItemGrade().name());
                } else {
                    html = html.replace("{gloves_icon}", "icon.NOIMAGE");
                    html = html.replace("{gloves_name}", "<font color=FF0000>...</font>");
                    html = html.replace("{gloves_grade}", "NO");
                }
                if (dress.getFeet() != -1) {
                    L2Item feet = ItemData.getInstance().getTemplate(dress.getFeet());
                    html = html.replace("{feet_icon}", feet.getIcon());
                    html = html.replace("{feet_name}", feet.getName());
                    html = html.replace("{feet_grade}", feet.getItemGrade().name());
                } else {
                    html = html.replace("{feet_icon}", "icon.NOIMAGE");
                    html = html.replace("{feet_name}", "<font color=FF0000>...</font>");
                    html = html.replace("{feet_grade}", "NO");
                }

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-cloakpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeCloakData cloak = gabriel.dressmeEngine.xml.dataHolder.DressMeCloakHolder.getInstance().getCloak(set);
            if (cloak != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_cloak_path);

                Inventory inv = player.getInventory();
                L2ItemInstance my_cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_CLOAK);
                html = html.replace("{my_cloak_icon}", my_cloak == null ? "icon.NOIMAGE" : my_cloak.getItem().getIcon());

                html = html.replace("{bypass}", "bypass -h voice .dress-cloak " + cloak.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-trycloak " + cloak.getId());
                html = html.replace("{name}", cloak.getName());

                if (HandleDressMeDb.dressMeCloakInside(player, cloak)) {
                    html = html.replace("{price}", "WardRobe");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, cloak.getPriceCount(), cloak.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(cloak.getCloakId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-shieldpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeShieldData shield = gabriel.dressmeEngine.xml.dataHolder.DressMeShieldHolder.getInstance().getShield(set);
            if (shield != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_shield_path);

                Inventory inv = player.getInventory();
                L2ItemInstance my_shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);
                html = html.replace("{my_shield_icon}", my_shield == null ? "icon.NOIMAGE" : my_shield.getItem().getIcon());

                html = html.replace("{bypass}", "bypass -h voice .dress-shield " + shield.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryshield " + shield.getId());
                html = html.replace("{name}", shield.getName());

                if (HandleDressMeDb.dressMeShieldInside(player, shield)) {
                    html = html.replace("{price}", "WardRobe");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, shield.getPriceCount(), shield.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(shield.getShieldId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-weaponpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeWeaponData weapon = gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder.getInstance().getWeapon(set);
            if (weapon != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_weapon_path);

                Inventory inv = player.getInventory();
                L2ItemInstance my_weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
                html = html.replace("{my_weapon_icon}", my_weapon == null ? "icon.NOIMAGE" : my_weapon.getItem().getIcon());

                html = html.replace("{bypass}", "bypass -h voice .dress-weapon " + weapon.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryweapon " + weapon.getId());
                html = html.replace("{name}", weapon.getName());

                if (HandleDressMeDb.dressMeWeaponInside(player, weapon)) {
                    html = html.replace("{price}", "WardRobe");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, weapon.getPriceCount(), weapon.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(weapon.getId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-hatpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeHatData hat = gabriel.dressmeEngine.xml.dataHolder.DressMeHatHolder.getInstance().getHat(set);
            if (hat != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_hat_path);

                Inventory inv = player.getInventory();

                L2ItemInstance my_hat = null;
                switch (hat.getSlot()) {
                    case 1: // HAIR
                    case 3: // FULL HAIR
                        my_hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
                        break;
                    case 2: // HAIR2
                        my_hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
                        break;
                }

                html = html.replace("{my_hat_icon}", my_hat == null ? "icon.NOIMAGE" : my_hat.getItem().getIcon());

                html = html.replace("{bypass}", "bypass -h voice .dress-hat " + hat.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryhat " + hat.getId());
                html = html.replace("{name}", hat.getName());

                if (HandleDressMeDb.dressMeHatInside(player, hat)) {
                    html = html.replace("{price}", "WardRobe");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, hat.getPriceCount(), hat.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(hat.getHatId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dressinfo")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), info_path);
            sendHtml(player, html);
            return true;
        } else if (command.equals("dress-armor")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeArmorData dress = gabriel.dressmeEngine.xml.dataHolder.DressMeArmorHolder.getInstance().getArmor(set);
            Inventory inv = player.getInventory();

            L2ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            L2ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
            L2ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
            L2ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

            if (chest == null) {
                player.sendMessage("Error: Chest must be equiped.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            if (chest.getItem().getBodyPart() == L2Item.SLOT_FULL_ARMOR && !dress.isSuit()) {
                L2Item visual = ItemData.getInstance().getTemplate(dress.getChest());
                if (chest.getItem().getBodyPart() != visual.getBodyPart()) {
                    player.sendMessage("Error: You can't change visual in full armor type not full armors.");
                    useVoicedCommand("dress-armorpage", player, args);
                    return false;
                }
            }

            // Checks for armor set for the equipped chest.
            if (!ArmorSetsData.getInstance().isArmorSet(chest.getId())) {
                player.sendMessage("Error: You can't visualize current set.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            L2ArmorSet armoSet = ArmorSetsData.getInstance().getSet(chest.getId());
            if ((armoSet == null) || !armoSet.containAll(player)) {
                player.sendMessage("Error: You can't visualize, set is not complete.");
                useVoicedCommand("dress-armorpage", player, args);
                return false;
            }

            if (!GabConfig.ALLOW_ALL_SETS) {
                if (!chest.getArmorItem().getItemType().getDescription().equals(dress.getType()) && !dress.isSuit()) {
                    player.sendMessage("Error: You can't visualize current set.");
                    useVoicedCommand("dress-armorpage", player, args);
                    return false;
                }
            }

            if (HandleDressMeDb.dressMeArmorInside(player, dress)) {
                DressMeHandler.visuality(player, chest, dress.getChest());

//                if (dress.getLegs() != -1) {
//                    DressMeHandler.visuality(player, legs, dress.getLegs());
//                } else if ((dress.getLegs() == -1) && (chest.getItem().getBodyPart() != L2Item.SLOT_FULL_ARMOR)) {
//                    DressMeHandler.visuality(player, legs, dress.getChest());
//                }
//                DressMeHandler.visuality(player, legs, dress.getLegs());
//                DressMeHandler.visuality(player, gloves, dress.getGloves());
//                DressMeHandler.visuality(player, feet, dress.getFeet());
                DressMeHandler.visuality(player, legs, -1);
                DressMeHandler.visuality(player, gloves, -1);
                DressMeHandler.visuality(player, feet, -1);
                player.broadcastUserInfo();
                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, dress.getPriceId(), dress.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", dress.getPriceId(), dress.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeArmor(player, dress)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }

                    DressMeHandler.visuality(player, chest, dress.getChest());

//                    if (dress.getLegs() != -1) {
//                        DressMeHandler.visuality(player, legs, dress.getLegs());
//                    } else if ((dress.getLegs() == -1) && (chest.getItem().getBodyPart() != L2Item.SLOT_FULL_ARMOR)) {
//                        DressMeHandler.visuality(player, legs, dress.getChest());
//                    }
//                    DressMeHandler.visuality(player, legs, dress.getLegs());
//                    DressMeHandler.visuality(player, gloves, dress.getGloves());
//                    DressMeHandler.visuality(player, feet, dress.getFeet());
                    DressMeHandler.visuality(player, legs, -1);
                    DressMeHandler.visuality(player, gloves, -1);
                    DressMeHandler.visuality(player, feet, -1);
                    player.broadcastUserInfo();
                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-cloak")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeCloakData cloak_data = gabriel.dressmeEngine.xml.dataHolder.DressMeCloakHolder.getInstance().getCloak(set);
            Inventory inv = player.getInventory();

            L2ItemInstance cloak = inv.getPaperdollItem(Inventory.PAPERDOLL_CLOAK);

            if (cloak == null) {
                player.sendMessage("Error: Cloak must be equiped.");
                useVoicedCommand("dress-cloakpage", player, args);
                return false;
            }

            if (HandleDressMeDb.dressMeCloakInside(player, cloak_data)) {
                DressMeHandler.visuality(player, cloak, cloak_data.getCloakId());
                player.broadcastUserInfo();
                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, cloak_data.getPriceId(), cloak_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", cloak_data.getPriceId(), cloak_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeCloak(player, cloak_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    player.destroyItemByItemId("VisualChange", cloak_data.getPriceId(), cloak_data.getPriceCount(), player, true);
                    DressMeHandler.visuality(player, cloak, cloak_data.getCloakId());
                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
                player.broadcastUserInfo();
            }

            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-shield")) {
            final int shield_id = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeShieldData shield_data = gabriel.dressmeEngine.xml.dataHolder.DressMeShieldHolder.getInstance().getShield(shield_id);
            Inventory inv = player.getInventory();

            L2ItemInstance shield = inv.getPaperdollItem(Inventory.PAPERDOLL_LHAND);

            if (shield == null) {
                player.sendMessage("Error: Shield must be equiped.");
                useVoicedCommand("dress-shieldpage", player, args);
                return false;
            }
            String type = shield.getArmorItem().getItemType().getDescription();

            if (type.equals("SHIELD") && !shield_data.isShield()) {
                player.sendMessage("Error: Sigil must be equiped.");
                useVoicedCommand("dress-shieldpage", player, args);
                return false;
            }
            if (type.equals("SIGIL") && shield_data.isShield()) {
                player.sendMessage("Error: Shield must be equiped.");
                useVoicedCommand("dress-shieldpage", player, args);
                return false;
            }


            if (HandleDressMeDb.dressMeShieldInside(player, shield_data)) {
                DressMeHandler.visuality(player, shield, shield_data.getShieldId());
                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, shield_data.getPriceId(), shield_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", shield_data.getPriceId(), shield_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeShield(player, shield_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    player.destroyItemByItemId("VisualChange", shield_data.getPriceId(), shield_data.getPriceCount(), player, true);
                    DressMeHandler.visuality(player, shield, shield_data.getShieldId());
                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }

            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-weapon")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeWeaponData weapon_data = gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder.getInstance().getWeapon(set);
            Inventory inv = player.getInventory();

            L2ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);

            if (weapon == null) {
                player.sendMessage("Error: Weapon must be equiped.");
                useVoicedCommand("dress-weaponpage", player, args);
                return false;
            }

            if (!weapon.getItemType().toString().equals(weapon_data.getType())) {
                player.sendMessage("Error: Weapon must be equals type.");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if (weapon.getItem().getBodyPart() == L2Item.SLOT_LR_HAND && !weapon_data.isBig() && !isOkType(weapon_data)) {
                player.sendMessage("Error: Weapon must be equals type.");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if (weapon.getItem().getBodyPart() != L2Item.SLOT_LR_HAND && weapon_data.isBig() && !isOkType(weapon_data)) {
                player.sendMessage("Error: Weapon must be equals type.");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if (weapon.getWeaponItem().isMagicWeapon() && !weapon_data.isMagic()) {
                player.sendMessage("Error: Weapon must be equals type (magic).");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if (!weapon.getWeaponItem().isMagicWeapon() && weapon_data.isMagic()) {
                player.sendMessage("Error: Weapon must be equals type (Non Magic).");
                useVoicedCommand("dressme-weapon", player, null);
                return false;
            }

            if (HandleDressMeDb.dressMeWeaponInside(player, weapon_data)) {
                DressMeHandler.visuality(player, weapon, weapon_data.getId());
                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, weapon_data.getPriceId(), weapon_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", weapon_data.getPriceId(), weapon_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeWeapon(player, weapon_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    DressMeHandler.visuality(player, weapon, weapon_data.getId());
                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }
            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-hat")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeHatData hat_data = gabriel.dressmeEngine.xml.dataHolder.DressMeHatHolder.getInstance().getHat(set);
            Inventory inv = player.getInventory();

            L2ItemInstance hat = null;
            switch (hat_data.getSlot()) {
                case 1: // HAIR
                case 3: // FULL HAIR
                    hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
                    break;
                case 2: // HAIR2
                    hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
                    break;
            }

            if (hat == null) {
                player.sendMessage("Error: Hat must be equiped.");
                useVoicedCommand("dress-hatpage", player, args);
                return false;
            }

            L2Item visual = ItemData.getInstance().getTemplate(hat_data.getHatId());
            if (hat.getItem().getBodyPart() != visual.getBodyPart()) {
                player.sendMessage("Error: You can't change visual on different hat types!");
                useVoicedCommand("dress-hatpage", player, args);
                return false;
            }
            if (HandleDressMeDb.dressMeHatInside(player, hat_data)) {
                DressMeHandler.visuality(player, hat, hat_data.getHatId());
                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, hat_data.getPriceId(), hat_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", hat_data.getPriceId(), hat_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeHat(player, hat_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    DressMeHandler.visuality(player, hat, hat_data.getHatId());
                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }
            player.broadcastUserInfo();

            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), undressme_path);
            html = html.replace("<?show_hide?>", player.getVarB("showVisualChange") ? "Show visual equip on other player!" : "Hide visual equip on other player!");
            html = html.replace("<?show_hide_b?>", !player.getVarB("showVisualChange") ? "showdress" : "hidedress");

            sendHtml(player, html);
            return true;
        } else if (command.equals("undressme-armor")) {
            Inventory inv = player.getInventory();
            L2ItemInstance chest = inv.getPaperdollItem(Inventory.PAPERDOLL_CHEST);
            L2ItemInstance legs = inv.getPaperdollItem(Inventory.PAPERDOLL_LEGS);
            L2ItemInstance gloves = inv.getPaperdollItem(Inventory.PAPERDOLL_GLOVES);
            L2ItemInstance feet = inv.getPaperdollItem(Inventory.PAPERDOLL_FEET);

            if (chest != null) {
                DressMeHandler.visuality(player, chest, 0);
            }
            if (legs != null) {
                DressMeHandler.visuality(player, legs, 0);
            }
            if (gloves != null) {
                DressMeHandler.visuality(player, gloves, 0);
            }
            if (feet != null) {
                DressMeHandler.visuality(player, feet, 0);
            }

            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme-cloak")) {
            L2ItemInstance cloak = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_CLOAK);
            if (cloak != null) {
                DressMeHandler.visuality(player, cloak, 0);
            }
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme-shield")) {
            L2ItemInstance shield = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (shield != null) {
                DressMeHandler.visuality(player, shield, 0);
            }
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme-weapon")) {
            L2ItemInstance weapon = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
            if (weapon != null) {
                DressMeHandler.visuality(player, weapon, 0);
            }
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme-hat")) {
            L2ItemInstance slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR);
            if (slot == null) {
                slot = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
            }

            if (slot != null) {
                DressMeHandler.visuality(player, slot, 0);
            }
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("showdress")) {
            player.setVar("showVisualChange", "true");
            Visuals.refreshAppStatus(player);
            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("hidedress")) {
            player.setVar("showVisualChange", "false");
            Visuals.refreshAppStatus(player);
            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-tryweapon")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeWeaponData weapon_data = gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder.getInstance().getWeapon(set);
            if (weapon_data == null) {
                return false;
            }

            Inventory inv = player.getInventory();
            L2ItemInstance weapon = inv.getPaperdollItem(Inventory.PAPERDOLL_RHAND);
            if (weapon != null) {
                player.setQuickVar("DressMeTry", weapon_data.getId());
                player.broadcastUserInfo();
                // Remove the try items in 6 seconds
                ThreadPoolManager.getInstance().scheduleGeneral(new TryDressMeCustom(player, weapon), 6 * 1000);
            }

            useVoicedCommand("dress-weaponpage", player, args);
            return false;
        } else if (command.equals("dress-tryarmor")) {
            final int set = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeArmorData dress = DressMeArmorHolder.getInstance().getArmor(set);
            if (dress == null) {
                return false;
            }

            final Map<Integer, Integer> itemList = new HashMap<>();
            itemList.put(Inventory.PAPERDOLL_CHEST, dress.getChest());
            itemList.put(Inventory.PAPERDOLL_LEGS, (dress.getLegs() > 0 ? dress.getLegs() : dress.getChest()));
            itemList.put(Inventory.PAPERDOLL_GLOVES, dress.getGloves());
            itemList.put(Inventory.PAPERDOLL_FEET, dress.getFeet());
            player.sendShopPreviewInfoPacket(itemList);

            // Remove the try items in 6 seconds
            ThreadPoolManager.getInstance().scheduleGeneral(new RequestPreviewItem.RemoveWearItemsTask(player), 6 * 1000);

            useVoicedCommand("dress-armorpage", player, args);
            return false;
        } else if (command.equals("dress-trycloak")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeCloakData cloak_data = gabriel.dressmeEngine.xml.dataHolder.DressMeCloakHolder.getInstance().getCloak(set);
            if (cloak_data == null) {
                return false;
            }

            final Map<Integer, Integer> itemList = new HashMap<>();
            itemList.put(Inventory.PAPERDOLL_CLOAK, cloak_data.getCloakId());
            player.sendShopPreviewInfoPacket(itemList);

            // Remove the try items in 6 seconds
            ThreadPoolManager.getInstance().scheduleGeneral(new RequestPreviewItem.RemoveWearItemsTask(player), 6 * 1000);

            useVoicedCommand("dress-cloakpage", player, args);
            return false;
        } else if (command.equals("dress-tryshield")) {
            final int shield_id = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeShieldData shield_data = gabriel.dressmeEngine.xml.dataHolder.DressMeShieldHolder.getInstance().getShield(shield_id);
            if (shield_data == null) {
                return false;
            }

            final Map<Integer, Integer> itemList = new HashMap<>();
            itemList.put(Inventory.PAPERDOLL_LHAND, shield_data.getShieldId());
            player.sendShopPreviewInfoPacket((itemList));

            // Remove the try items in 6 seconds
            ThreadPoolManager.getInstance().scheduleGeneral(new RequestPreviewItem.RemoveWearItemsTask(player), 6 * 1000);

            useVoicedCommand("dress-shieldpage", player, args);
            return false;
        } else if (command.equals("dress-tryhat")) {
            final int hat_id = Integer.parseInt(args.split(" ")[0]);
            Inventory inv = player.getInventory();

            gabriel.dressmeEngine.data.DressMeHatData hat_data = gabriel.dressmeEngine.xml.dataHolder.DressMeHatHolder.getInstance().getHat(hat_id);
            if (hat_data == null) {
                return false;
            }

            L2ItemInstance hat = null;

            switch (hat_data.getSlot()) {
                case 1: // HAIR
                case 3: // FULL HAIR
                    hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR);
                    break;
                case 2: // HAIR2
                    hat = inv.getPaperdollItem(Inventory.PAPERDOLL_HAIR2);
                    break;
            }

            if (hat != null) {
//                hat.setOldVisualItemId(hat.getVisualItemId());
                player.setQuickVar("DressMeTry", hat_data.getHatId());
                player.setQuickVar("hairslotDressMeTry", hat_data.getSlot());

//                DressMeHandler.visualityCustom(player, hat, hat_data.getHatId());
                player.broadcastUserInfo();
                player.broadcastUserInfo();
                // Remove the try items in 6 seconds
                ThreadPoolManager.getInstance().scheduleGeneral(new TryDressMeCustom(player, hat), 6 * 1000);
            }
            useVoicedCommand("dress-hatpage", player, args);
            return false;
        } else if (command.equals("dressme-enchant")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_enchant_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_enchant_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;
//            Map<Integer, DressMeEnchantData> map = HandleDressMeDb.order(player, DressMeLoader.ENCHANT, "character_dressme_enchant_list", "enchantId");
            List<gabriel.dressmeEngine.data.DressMeEnchantData> map = HandleDressMeDb.orderL(player, DressMeLoader.ENCHANT, "character_dressme_enchant_list", "enchantId");


            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeEnchantData enchant = map.get(i + 1);
                gabriel.dressmeEngine.data.DressMeEnchantData enchant = map.get(i);
                if (enchant != null) {
                    block = template;

                    String enchant_name = enchant.getName();

                    if (enchant_name.length() > 29) {
                        enchant_name = enchant_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-enchantpage " + enchant.getId());
                    block = block.replace("{name}", enchant_name);

                    if (HandleDressMeDb.dressMeEnchantInside(player, enchant)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, enchant.getPriceCount(), enchant.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(enchant.getEnchantId()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-enchant " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-enchant " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            sendHtml(player, html);
            return true;
        } else if (command.equals("dress-enchantpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeEnchantData enchantData = gabriel.dressmeEngine.xml.dataHolder.DressMeEnchantHolder.getInstance().getEnchant(set);
            if (enchantData != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_enchant_path);

                html = html.replace("{my_enchant_icon}", "icon.NOIMAGE");

                html = html.replace("{bypass}", "bypass -h voice .dress-enchant " + enchantData.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryenchant " + enchantData.getId());
                html = html.replace("{name}", enchantData.getName());

                if (HandleDressMeDb.dressMeEnchantInside(player, enchantData)) {
                    html = html.replace("{price}", "Owned");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, enchantData.getPriceCount(), enchantData.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(enchantData.getEnchantId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-enchant")) {
            final int enchantID = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeEnchantData enchant_data = gabriel.dressmeEngine.xml.dataHolder.DressMeEnchantHolder.getInstance().getEnchant(enchantID);
            L2Item item = ItemData.getInstance().getTemplate(enchant_data.getEnchantId());

            if (HandleDressMeDb.dressMeEnchantInside(player, enchant_data)) {

                DressMeHandler.handleEnchantChange(player, item.getDressMeEnchant(), false);

                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, enchant_data.getPriceId(), enchant_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", enchant_data.getPriceId(), enchant_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeEnchant(player, enchant_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    DressMeHandler.handleEnchantChange(player, item.getDressMeEnchant(), false);

                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }

            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-tryenchant")) {
            final int enchant_id = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeEnchantData enchant_data = gabriel.dressmeEngine.xml.dataHolder.DressMeEnchantHolder.getInstance().getEnchant(enchant_id);
            L2Item item = ItemData.getInstance().getTemplate(enchant_data.getEnchantId());
            DressMeHandler.handleEnchantChange(player, item.getDressMeEnchant(), true);
            useVoicedCommand("dress-enchantpage", player, args);
            return false;
        } else if (command.equals("dressme-agathion")) {
            String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), index_aga_path);
            String template = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), template_aga_path);
            String block = "";
            String list = "";

            if (args == null) {
                args = "1";
            }

            String[] param = args.split(" ");

            final int page = param[0].length() > 0 ? Integer.parseInt(param[0]) : 1;
            final int perpage = 5;
            int counter = 0;
//            Map<Integer, DressMeAgathionData> map = HandleDressMeDb.order(player, DressMeLoader.AGATHION, "character_dressme_agathion_list", "agathionId");
            List<gabriel.dressmeEngine.data.DressMeAgathionData> map = HandleDressMeDb.orderL(player, DressMeLoader.AGATHION, "character_dressme_agathion_list", "agathionId");

            for (int i = (page - 1) * perpage; i < map.size(); i++) {
//                DressMeAgathionData agathionData = map.get(i + 1);
                gabriel.dressmeEngine.data.DressMeAgathionData agathionData = map.get(i);
                if (agathionData != null) {
                    block = template;

                    String agathion_name = agathionData.getName();

                    if (agathion_name.length() > 29) {
                        agathion_name = agathion_name.substring(0, 29) + "...";
                    }

                    block = block.replace("{bypass}", "bypass -h voice .dress-agathionpage " + agathionData.getId());
                    block = block.replace("{name}", agathion_name);

                    if (HandleDressMeDb.dressMeAgathionInside(player, agathionData)) {
                        block = block.replace("{price}", "Owned");
                    } else {
                        block = block.replace("{price}", Util.formatPay(player, agathionData.getPriceCount(), agathionData.getPriceId()));
                    }
                    block = block.replace("{icon}", Util.getItemIcon(agathionData.getAgathionId()));
                    list += block;
                }

                counter++;

                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) map.size() / perpage);
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; i++) {
                if (i == page) {
                    navigation += "<td width=25 align=center valign=top><button value=\"[" + i + "]\" action=\"bypass -h voice .dressme-agathion " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                } else {
                    navigation += "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h voice .dressme-agathion " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                }

                if (inline == 7) {
                    navigation += "</tr><tr>";
                    inline = 0;
                }
                inline++;
            }

            if (navigation.equals("")) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html = html.replace("{list}", list);
            html = html.replace("{navigation}", navigation);

            sendHtml(player, html);
            return true;
        } else if (command.equals("dress-agathionpage")) {
            final int set = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeAgathionData agathionData = gabriel.dressmeEngine.xml.dataHolder.DressMeAgathionHolder.getInstance().getAgathion(set);
            if (agathionData != null) {
                String html = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), dress_aga_path);

                html = html.replace("{my_aga_icon}", "icon.NOIMAGE");

                html = html.replace("{bypass}", "bypass -h voice .dress-agathion " + agathionData.getId());
                html = html.replace("{bypassTry}", "bypass -h voice .dress-tryagathion " + agathionData.getId());
                html = html.replace("{name}", agathionData.getName());

                if (HandleDressMeDb.dressMeAgathionInside(player, agathionData)) {
                    html = html.replace("{price}", "Owned");
                } else {
                    html = html.replace("{price}", Util.formatPay(player, agathionData.getPriceCount(), agathionData.getPriceId()));
                }

                L2Item item = ItemData.getInstance().getTemplate(agathionData.getAgathionId());
                html = html.replace("{item_icon}", item.getIcon());
                html = html.replace("{item_name}", item.getName());
                html = html.replace("{item_grade}", item.getItemGrade().name());

                sendHtml(player, html);
                return true;
            }
            return false;
        } else if (command.equals("dress-agathion")) {
            final int agathionId = Integer.parseInt(args.split(" ")[0]);

            gabriel.dressmeEngine.data.DressMeAgathionData agathion_data = gabriel.dressmeEngine.xml.dataHolder.DressMeAgathionHolder.getInstance().getAgathion(agathionId);
            L2Item item = ItemData.getInstance().getTemplate(agathion_data.getAgathionId());

            if (HandleDressMeDb.dressMeAgathionInside(player, agathion_data)) {

                DressMeHandler.handleCustomAgathionChange(player, item.getDressMeAgathion(), false);

                player.sendMessage("Dress Was already in wardrobe! No price reduced!");
            } else {
                if (Conditions.checkPlayerItemCount(player, agathion_data.getPriceId(), agathion_data.getPriceCount())) {
                    if (!player.destroyItemByItemId("VisualChange", agathion_data.getPriceId(), agathion_data.getPriceCount(), player, true)
                            || !HandleDressMeDb.insertDressMeAgathion(player, agathion_data)) {
                        player.sendMessage("Something went went wrong trying to send your dress to your wardrobe or we couldn't get the money from your inventory!");
                        return false;
                    }
                    DressMeHandler.handleCustomAgathionChange(player, item.getDressMeAgathion(), false);

                    player.sendMessage("Item bought with success! You can get it again from your wardrobe!");
                }
            }

            player.broadcastUserInfo();
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("dress-tryagathion")) {
            final int agathion_id = Integer.parseInt(args.split(" ")[0]);
            gabriel.dressmeEngine.data.DressMeAgathionData agathion_data = gabriel.dressmeEngine.xml.dataHolder.DressMeAgathionHolder.getInstance().getAgathion(agathion_id);
            L2Item item = ItemData.getInstance().getTemplate(agathion_data.getAgathionId());
            DressMeHandler.handleCustomAgathionChange(player, item.getDressMeAgathion(), true);
            useVoicedCommand("dress-agathionpage", player, args);
            return false;
        } else if (command.equals("undressme-agathion")) {
            DressMeHandler.handleCustomAgathionChange(player, 0, false);
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        } else if (command.equals("undressme-enchant")) {
            DressMeHandler.handleEnchantChange(player, 0, false);
            player.broadcastUserInfo();
//            useVoicedCommand("undressme", player, null);
            GabrielCBB.getInstance().parseCommand("gab_dressme", player);
            return true;
        }


        return false;
    }

    private boolean isOkType(DressMeWeaponData weapon) {
        switch (weapon.getType()) {
            case "BOW":
            case "POLE":
            case "DUAL":
            case "DUALFIST":
            case "CROSSBOW":
            case "ANCIENTSWORD":
            case "DUALDAGGER":
                return true;
            default:
                return false;
        }
    }

    private void sendHtml(L2PcInstance player, String html) {
        NpcHtmlMessage msg = new NpcHtmlMessage();
        msg.setHtml(html);
        player.sendPacket(msg);
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}
