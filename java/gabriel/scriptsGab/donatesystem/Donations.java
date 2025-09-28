package gabriel.scriptsGab.donatesystem;

import gabriel.scriptsGab.donatesystem.xml.DonationHolder;
import gabriel.scriptsGab.utils.ItemFunctions;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Donations {
    protected static Donations instance;
    private static final String[] _vars = new String[]
            {
                    "FOUNDATION",
                    "ENCHANT",
                    "ATTRIBUTION"
            };
    private static final String Active = "<font color=669900>You buy it!</font>";
    private static final String NotActive = "<font color=FF0000>Not buy!</font>";

    public static Donations getInstance() {
        if (instance == null)
            instance = new Donations();
        return instance;
    }

    public void parseCommand(String command, String[] args, L2PcInstance player) {
        switch (command) {
            case "list":
                list(args, player);
                break;
            case "open":
                open(args, player);
                break;
            case "var":
                var(args, player);
                break;
            case "attribute":
                attribute(args, player);
                break;
            case "put":
                put(args, player);
                break;
            case "buy":
                buy(args, player);
                break;
            case "clear_att":
                clear_att(args, player);
                break;
        }
    }


    public void list(String[] arg, L2PcInstance player) {
        if (player == null)
            return;

        if (!arg[0].isEmpty() && Util.isNumber(arg[0]) && (arg.length <= 1 || arg[1].isEmpty() || Util.isNumber(arg[1]))) {
            int id = Integer.parseInt(arg[0]);
            removeVars(player);
            NpcHtmlMessage html = new NpcHtmlMessage(0);
            String index = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/index.htm");
            html.setHtml(index);
            String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/template.htm");
            String block = "";
            String list = "";
            List<Donation> _donate = DonationHolder.getInstance().getGroup(id);
            final int perpage = 6;
            int page = arg.length > 1 ? Integer.parseInt(arg[1]) : 1;
            int counter = 0;

            for (int i = (page - 1) * perpage; i < _donate.size(); i++) {
                Donation pack = _donate.get(i);
                block = template.replace("{bypass}", "bypass -h scripts_Donations:open " + pack.getId());
                block = block.replace("{name}", pack.getName());
                block = block.replace("{icon}", pack.getIcon());
                SimpleList simple = pack.getSimple();
                block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, simple.getCount(), simple.getId()));
                list += block;
                counter++;
                if (counter >= perpage) {
                    break;
                }
            }

            double count = Math.ceil((double) _donate.size() / (double) perpage); // Use rounding to obtain the last page with the remainder!
            int inline = 1;
            String navigation = "";

            for (int i = 1; i <= count; ++i) {
                if (i == page)
                    navigation = navigation + "<td width=25 align=center valign=top><button value=\"[" + i + "]\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                else
                    navigation = navigation + "<td width=25 align=center valign=top><button value=\"" + i + "\" action=\"bypass -h scripts_Donations:list " + id + " " + i + "\" width=32 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>";
                if (inline % 7 == 0)
                    navigation = navigation + "</tr><tr>";
                inline++;
            }

            if (inline == 2) {
                navigation = "<td width=30 align=center valign=top>...</td>";
            }

            html.replace("%list%", list);
            html.replace("%navigation%", navigation);
            player.sendPacket(html);
        }
    }

    private void removeVars(L2PcInstance player) {
        for (String var : _vars)
            player.deleteQuickVar(var);
        for (int i = 1; i <= 3; i++)
            player.deleteQuickVar("att_" + i);
    }

    public void open(String[] arg, L2PcInstance player) {
        if (!Util.isNumber(arg[0]))
            return;
        if (player == null)
            return;
        int id = Integer.parseInt(arg[0]);
        Donation donate = DonationHolder.getInstance().getDonate(id);

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        String index = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/open.htm");
        html.setHtml(index);
        String content = "";
        Map<Integer, Long> price = new HashMap<Integer, Long>();

        html.replace("%name%", donate.getName());
        html.replace("%icon%", donate.getIcon());
        html.replace("%id%", String.valueOf(donate.getId()));
        html.replace("%group%", String.valueOf(donate.getGroup()));

        SimpleList simple = donate.getSimple();
        html.replace("%cost%", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, simple.getCount(), simple.getId()));
        price.put(Integer.valueOf(simple.getId()), Long.valueOf(simple.getCount()));
        if (donate.haveFound()) {
            boolean enchant = isVar(player, _vars[0]);
            FoundList found = donate.getFound();
            String block = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/foundation.htm");

            block = block.replace("{bypass}", "bypass -h scripts_Donations:var " + _vars[0] + " " + (enchant ? 0 : 1) + " " + donate.getId());
            block = block.replace("{status}", enchant ? Active : NotActive);
            block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, found.getCount(), found.getId()));
            block = block.replace("{action}", enchant ? "Cancel" : "Buy");
            if (enchant)
                updatePrice(price, found.getId(), found.getCount());

            content += block;
        }

        Enchant enchant = donate.getEnchant();
        if (enchant != null) {
            boolean is = isVar(player, _vars[1]);
            String block = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/enchant.htm");
            block = block.replace("{bypass}", "bypass -h scripts_Donations:var " + _vars[1] + " " + (is ? 0 : 1) + " " + donate.getId());
            block = block.replace("{status}", is ? Active : NotActive);
            block = block.replace("{ench}", "+" + enchant.getEnchant());
            block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, enchant.getCount(), enchant.getId()));
            block = block.replace("{action}", is ? "Cancel" : "Buy");
            if (is)
                updatePrice(price, enchant.getId(), enchant.getCount());

            content += block;
        }
        Attribution att = donate.getAttribution();
        if (att != null && att.getSize() >= 1) {
            boolean is = isVar(player, _vars[2]);
            if (is && checkAttVars(player, att.getSize())) {
                is = false;
                player.unsetVar(_vars[2]);
                var(new String[]{_vars[2], "0"}, player);
            }

            String block = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/attribute.htm");
            block = block.replace("{bypass}", is ? "bypass -h scripts_services.Donations:var " + _vars[2] + " " + 0 + " " + donate.getId() : "bypass -h scripts_Donations:attribute " + donate.getId());
            block = block.replace("{status}", is ? Active : NotActive);
            block = block.replace("{cost}", "<font color=99CC66>Cost: </font>" + Util.formatPay(player, att.getCount(), att.getId()));
            block = block.replace("{action}", is ? "Cancel" : "Buy");
            if (is)
                updatePrice(price, att.getId(), att.getCount());

            content += block;
        }

        String total = "";

        for (Map.Entry<Integer, Long> map : price.entrySet())
            total += Util.formatPay(player, map.getValue(), map.getKey()) + "<br1>";

        html.replace("%content%", content);
        html.replace("%total%", total);

        player.sendPacket(html);
    }

    private boolean isVar(L2PcInstance player, String var) {
        boolean found = player.getQuickVarI(var, 0) != 0;
        return found;
    }

    public void var(String[] arg, L2PcInstance player) {
        if (arg.length < 3)
            return;

        if (!Util.isNumber(arg[1]) || !Util.isNumber(arg[2]))
            return;

        int action = Integer.parseInt(arg[1]);
        String var = arg[0];
        player.setQuickVar(var, action);

        if (action == 0) {
            player.deleteQuickVar(var);
            if (var.equals(_vars[2])) {
                for (int i = 1; i <= 3; i++)
                    player.deleteQuickVar("att_" + i);
            }
        }

        open(new String[]{arg[2]}, player);
    }

    private void updatePrice(Map<Integer, Long> price, int id, long count) {
        if (price.containsKey(id))
            price.put(id, count + price.get(id));
        else
            price.put(id, count);
    }

    private boolean checkAttVars(L2PcInstance player, int size) {
        int count = 0;

        for (int i = 1; i <= 3; i++) {
            int var = player.getQuickVarI("att_" + i, -1);
            if (var != -1)
                count++;
        }
        return count != size;
    }

    public void attribute(String[] arg, L2PcInstance player) {
        if (arg.length < 1)
            return;

        if (!Util.isNumber(arg[0]))
            return;

        int id = Integer.parseInt(arg[0]);
        Donation donate = DonationHolder.getInstance().getDonate(id);
        if (donate == null)
            return;

        Attribution atribute = donate.getAttribution();

        if (atribute == null)
            return;

        if (atribute.getSize() < 1) {
            open(arg, player);
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(0);
        String index = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/Donate/attribute_choice.htm");
        html.setHtml(index);
        html.replace("%name%", donate.getName());
        html.replace("%icon%", donate.getIcon());
        html.replace("%bypass%", "bypass -h scripts_Donations:open " + donate.getId());
        html.replace("%value%", String.valueOf(atribute.getValue()));
        html.replace("%size%", String.valueOf(atribute.getSize()));
        html.replace("%id%", String.valueOf(donate.getId()));

        int att_1 = player.getQuickVarI("att_1", -1);
        int att_2 = player.getQuickVarI("att_2", -1);
        int att_3 = player.getQuickVarI("att_3", -1);
        html.replace("%att_1%", atribute.getSize() >= 1 ? (att_1 == -1 ? "..." : elementName(att_1)) : "<font color=FF0000>Slot is block</font>");
        html.replace("%att_2%", atribute.getSize() >= 2 ? (att_2 == -1 ? "..." : elementName(att_2)) : "<font color=FF0000>Slot is block</font>");
        html.replace("%att_3%", atribute.getSize() == 3 ? (att_3 == -1 ? "..." : elementName(att_3)) : "<font color=FF0000>Slot is block</font>");

        build(player, html, donate, att_1, att_2, att_3);
        player.sendPacket(html);
    }

    private String elementName(int id) {
        String name = "";
        switch (id) {
            case 0:
                name = "Attributes Fire";
                break;
            case 1:
                name = "Attributes Water";
                break;
            case 2:
                name = "Attributes Wind";
                break;
            case 3:
                name = "Attributes Earth";
                break;
            case 4:
                name = "Attributes Holy";
                break;
            case 5:
                name = "Attributes Unholy";
                break;
            default:
                name = "NONE";
                break;
        }

        return name;
    }

    private NpcHtmlMessage build(L2PcInstance player, NpcHtmlMessage html, Donation donate, int att_1, int att_2, int att_3) {
        String slotclose = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
        int id = donate.getId();
        int size = donate.getAttribution().getSize();
        boolean block = false;
        if (size == 1 && (att_1 != -1 || att_2 != -1 || att_3 != -1))
            block = true;
        else if (size == 2 && (att_1 != -1 || att_2 != -1) && (att_1 != -1 || att_3 != -1) && (att_2 != -1 || att_3 != -1))
            block = true;
        else if (size == 3 && att_1 != -1 && att_2 != -1 && att_3 != -1)
            block = true;

        boolean one = block(player, 0, 1) || block;
        String fire = one ? slotclose : button(0, id);
        String water = one ? slotclose : button(1, id);
        boolean two = block(player, 2, 3) || block;
        String wind = two ? slotclose : button(2, id);
        String earth = two ? slotclose : button(3, id);
        boolean three = block(player, 4, 5) || block;
        String holy = three ? slotclose : button(4, id);
        String unholy = three ? slotclose : button(5, id);
        html.replace("%fire%", fire);
        html.replace("%water%", water);
        html.replace("%wind%", wind);
        html.replace("%earth%", earth);
        html.replace("%holy%", holy);
        html.replace("%unholy%", unholy);

        return html;
    }

    private boolean block(L2PcInstance player, int id, int id2) {
        for (int i = 1; i <= 3; i++) {
            int var = player.getQuickVarI("att_" + i, -1);
            if (var == id || var == id2)
                return true;
        }

        return false;
    }

    private String button(int att, int id) {
        return "<button action=\"bypass -h scripts_Donations:put " + id + " " + att + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
    }

    public void put(String[] arg, L2PcInstance player) {
        if (arg.length < 2)
            return;

        if (!Util.isNumber(arg[0]) || !Util.isNumber(arg[1]))
            return;

        if (player == null)
            return;
        int att = Integer.parseInt(arg[1]);
        if (player.getQuickVarI("att_1", -1) == -1)
            player.setQuickVar("att_1", att);
        else if (player.getQuickVarI("att_2", -1) == -1)
            player.setQuickVar("att_2", att);
        else if (player.getQuickVarI("att_3", -1) == -1)
            player.setQuickVar("att_3", att);

        player.setQuickVar(_vars[2], 1);

        attribute(arg, player);
    }

    public void buy(String[] arg, L2PcInstance player) {
        if (arg.length < 1)
            return;

        if (!Util.isNumber(arg[0]))
            return;

        int id = Integer.parseInt(arg[0]);
        Donation donate = DonationHolder.getInstance().getDonate(id);
        if (donate == null)
            return;

        Map<Integer, Long> price = new HashMap<Integer, Long>();
        SimpleList simple = donate.getSimple();

        price.put(simple.getId(), simple.getCount());

        FoundList foundation = donate.getFound();
        boolean found_list = donate.haveFound() && foundation != null && player.getQuickVarI(_vars[0], -1) != -1;
        if (found_list)
            updatePrice(price, foundation.getId(), foundation.getCount());

        Enchant enchant = donate.getEnchant();
        boolean enchanted = enchant != null && player.getQuickVarI(_vars[1], -1) != -1;
        if (enchanted)
            updatePrice(price, enchant.getId(), enchant.getCount());

        Attribution att = donate.getAttribution();
        boolean attribution = att != null && player.getQuickVarI(_vars[2], -1) != -1;
        if (attribution)
            updatePrice(price, att.getId(), att.getCount());

        //player.getInventory().checkDoubleItems();

        for (Map.Entry<Integer, Long> map : price.entrySet()) {
            int _id = map.getKey();
            long _count = map.getValue();

            if (ItemFunctions.getItemCount(player, map.getKey()) < map.getValue()) {
                player.sendMessage("It is not enough " + Util.formatPay(player, _count, _id));
                open(arg, player);
                return;
            }
        }

        //	List<ItemActionLog> logs = new ArrayList<>(price.size() + (found_list ? foundation.getList().size() : simple.getList().size()));
        for (Map.Entry<Integer, Long> map : price.entrySet()) {
            int _id = map.getKey();
            long _count = map.getValue();
            player.sendMessage("Disappeared: " + Util.formatPay(player, _count, _id));
            L2ItemInstance itemToRemove = player.getInventory().getItemByItemId(_id);
            //logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_LOSE, "DonateEquipment", player, itemToRemove, _count));
            player.getInventory().destroyItem("Donate", itemToRemove, _count, player, null);
        }

        for (DonateItem _donate : (found_list ? foundation.getList() : simple.getList())) {
            L2ItemInstance item = player.getInventory().addItem("PremiumItem", _donate.getId(), _donate.getCount(), player, null);

            int enchant_level = 0;
            if (enchanted)
                enchant_level = enchant.getEnchant();
            else if (_donate.getEnchant() > 0)
                enchant_level = _donate.getEnchant();

            if (enchant_level > 0 && item.isEnchantable() == 1)
                item.setEnchantLevel(enchant_level);

            if ((item.isArmor() || item.isWeapon()) && attribution) // Add all elements
            {
                for (int i = 1; i <= att.getSize(); i++) {
                    int element_id = player.getQuickVarI("att_" + i, -1);
                    if (element_id != -1) {
                        byte element = (byte) element_id;

                        if (item.isArmor()) // If is armor need reverse element Water to Fire and etc..
                            element = Elementals.getOppositeElement(element);

                        item.setElementAttr(element, att.getValue());

                        if (item.isEquipped()) {
                            item.updateElementAttrBonus(player);
                        }
                        player.sendUserInfo(true);
                    }
                }
            }

            // send packets
            InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(item);
            player.sendInventoryUpdate(iu);


            //logs.add(new ItemActionLog(ItemStateLog.EXCHANGE_GAIN, "DonateEquipment", player, item, _donate.getCount()));
        }

        //Log.logItemActions(logs);

        removeVars(player);
        player.sendMessage("You buy: " + donate.getName());
    }

    public void clear_att(String[] arg, L2PcInstance player) {
        if (arg.length < 1)
            return;

        if (!Util.isNumber(arg[0]))
            return;

        for (int i = 1; i <= 3; i++)
            player.deleteQuickVar("att_" + i);

        attribute(arg, player);
    }
}
