package gabriel.scriptsGab.itemBroker;

import gabriel.scriptsGab.utils.BBS;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.data.xml.impl.RecipeData;
import l2r.gameserver.enums.PrivateStoreType;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2ManufactureItem;
import l2r.gameserver.model.L2RecipeList;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.TradeItem;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.util.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ItemBroker {

    protected static ItemBroker instance;
    private static String BBS_HOME_DIR = "data/html/scripts/services/gabriel/itembroker/";
    private static String BASE_COMMAND = "gab_";
    private static String ITEMBROKER = "itembroker_";
    public List<ItemBagBroker> itemsBag = new ArrayList<>();
    private int itemsPerPage = 4;

    public static ItemBroker getInstance() {
        if (instance == null)
            instance = new ItemBroker();
        return instance;
    }


    public void parseCommand(String command, L2PcInstance player) {
        String subcommand = command.substring(ITEMBROKER.length());

        String content = "";
        content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "index.htm");
        String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/gabriel/template/sideTemplate.htm");
        content = content.replace("%name%", player.getName());
        content = content.replace("%template%", template);


//        if (!player.isInsideZone(ZoneIdType.TOWN)) {
//            player.sendMessage("You cannot use this service outside the village!");
//            return;
//        }
        player.setQuickVar("usingCBB", true);

        if (command.equals(ITEMBROKER)) {
            clearVars(player);
            parseCommand("itembroker_search", player);
            return;
        }
        if (subcommand.startsWith("store")) {
            String[] args = subcommand.substring("store".length()).split("\\s+");
            int objId = Integer.parseInt(args[1]);
            openShop(objId, player);
            return;
        }
        if (subcommand.startsWith("editVar")) {
            String[] args = subcommand.substring("editVar".length()).split("\\s+");
            String name = args[1];
            String value = args[2];
            player.setQuickVar(name, value);
            if (!name.equals("currPage")) {
                player.setQuickVar("currPage", "1");
            }
            parseCommand("itembroker_search", player);
            return;
        }

        if (subcommand.startsWith("search")) {
            try {
                String[] args = subcommand.substring("search".length()).split("\\s+");
                String searchString;
                try {
                    searchString = args[1];
                    clearVars(player);
                    player.setQuickVar("searchString", args[1]);
                } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
                    searchString = player.getQuickVar("searchString", "all");
                }
                String orderBy = player.getQuickVar("orderBy", "asc");
                String typeOfStore = player.getQuickVar("typeOfStore", "all");

                getAllStores(searchString, typeOfStore);
                orderList(orderBy);

                String pageS = player.getQuickVar("currPage", "1");
                player.setQuickVar("currPage", pageS);

                int page = Integer.parseInt(pageS);

                StringBuilder append = new StringBuilder();
                int counter = 0;
                int laterIndex = 0;
                for (int i = (page - 1) * itemsPerPage; i < itemsBag.size(); i++) {
                    ItemBagBroker itemBag = itemsBag.get(i);
                    laterIndex = i + 1;
                    if (itemBag != null) {
                        L2Item item = itemBag.getItem();
                        L2PcInstance p = itemBag.getOwner();
                        append.append(getItemTemplate(item.getId(), p, itemBag.getPrice()));
                    }
                    counter++;
                    if (counter >= itemsPerPage) {
                        break;
                    }
                }
                boolean hasNextItem;
                try {
                    ItemBagBroker laterItem = itemsBag.get(laterIndex);
                    hasNextItem = true;
                } catch (IndexOutOfBoundsException e) {
                    hasNextItem = false;
                }
                content = content.replace("%change%", append.toString());
                content = content.replace("%nav%", getNavigationBar(page, hasNextItem));

            } catch (Exception e) {
                parseCommand("itembroker_search 1", player);
                return;
            }
        }
        BBS.separateAndSend(content, player);
    }

    private void clearVars(L2PcInstance player) {
        player.deleteQuickVar("typeOfStore");
        player.deleteQuickVar("orderBy");
        player.deleteQuickVar("currPage");
        player.deleteQuickVar("searchString");
    }

    private void orderList(String orderBy) {
        if (orderBy.equals("asc")) {
            itemsBag.sort(Comparator.comparingLong(ItemBagBroker::getPrice));

        } else if (orderBy.equals("desc")) {
            itemsBag.sort(Comparator.comparingLong(ItemBagBroker::getPrice).reversed());
        }
    }

    private void openShop(int objId, L2PcInstance pcInstance) {
        L2PcInstance shop = L2World.getInstance().getPlayer(objId);
        pcInstance.doInteract(shop);
    }

    private void getAllStores(String itemName, String typeOfStore) {
        itemsBag.clear();
        boolean allItems = itemName.equals("all");

        for (L2PcInstance p : L2World.getInstance().getPlayers()) {
            if (p.getPrivateStoreType() != PrivateStoreType.NONE) {

                if (typeOfStore.equals("all") || typeOfStore.equals("sell") && (p.getPrivateStoreType() == PrivateStoreType.SELL || p.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL)) {
                    for (TradeItem item : p.getSellList().getItems()) {
                        if (!allItems && item.getItem().getName().toLowerCase().contains(itemName.toLowerCase())) {
                            itemsBag.add(new ItemBagBroker(item.getItem(), p, item.getPrice()));
                        } else if (allItems) {
                            itemsBag.add(new ItemBagBroker(item.getItem(), p, item.getPrice()));
                        }
                    }
                }
                if (typeOfStore.equals("all") || typeOfStore.equals("buy") && (p.getPrivateStoreType() == PrivateStoreType.BUY)) {
                    for (TradeItem item : p.getBuyList().getItems()) {
                        if (!allItems && item.getItem().getName().toLowerCase().contains(itemName.toLowerCase())) {
                            itemsBag.add(new ItemBagBroker(item.getItem(), p, item.getPrice()));
                        } else if (allItems) {
                            itemsBag.add(new ItemBagBroker(item.getItem(), p, item.getPrice()));
                        }
                    }
                }
                if (typeOfStore.equals("all") || typeOfStore.equals("craft") && (p.getPrivateStoreType() == PrivateStoreType.MANUFACTURE)) {
                    for (L2ManufactureItem item : p.getManufactureItems().values()) {
                        L2RecipeList recipe = RecipeData.getInstance().getRecipeList(item.getRecipeId());
                        L2Item itemData = ItemData.getInstance().getTemplate(recipe.getItemId());
                        if (!allItems && itemData.getName().toLowerCase().contains(itemName.toLowerCase())) {
                            itemsBag.add(new ItemBagBroker(itemData, p, item.getCost()));
                        } else if (allItems) {
                            itemsBag.add(new ItemBagBroker(itemData, p, item.getCost()));
                        }
                    }
                }
            }
        }
    }


    private String getItemTemplate(int itemId, L2PcInstance owner, long price) {
        L2Item item = ItemData.getInstance().getTemplate(itemId);
        StringBuilder append = new StringBuilder();
        String template = HtmCache.getInstance().getHtm(owner.getHtmlPrefix(), BBS_HOME_DIR + "itemTemplate.htm");
        template = template.replace("{icon}", item.getIcon());
        template = template.replace("{name}", item.getName());
        template = template.replace("{type}", getPrivateStoreString(owner.getPrivateStoreType()));
        template = template.replace("{charType}", getPrivateCharString(owner.getPrivateStoreType()));
        template = template.replace("{owner}", owner.getName());
        template = template.replace("{color}", getPrivateStoreColor(owner.getPrivateStoreType()));
        String bypass = "bypass gab_itembroker_store " + owner.getObjectId();
        template = template.replace("{bypass}", bypass);

        template = template.replace("{coinAmount}", Util.getNumberWithCommas(price));
        append.append(template);
        return append.toString();
    }

    private String getNavigationBar(int page, boolean hasNext) {
        StringBuilder append = new StringBuilder();
        append.append("<center><table><tr><td>");
        if (page - 1 != 0) {
            append.append("<button value=\"Prev\" action=\"bypass gab_itembroker_editVar currPage ").append(page - 1).append("\" width=80 height=15 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF>");
        }
        append.append("</td><td>");
        if (hasNext) {
            append.append("<button value=\"Next\" action=\"bypass gab_itembroker_editVar currPage ").append(page + 1).append("\" width=80 height=15 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF>");
        }
        append.append("</td></tr></table></center>");

        return append.toString();
    }

    private String getPrivateStoreString(PrivateStoreType type) {
        switch (type) {
            case BUY:
                return "Private Buy";
            case SELL:
                return "Private Sell";
            case PACKAGE_SELL:
                return "Package Sale";
            case MANUFACTURE:
                return "Manufacture";
            default:
                return "";
        }
    }

    private String getPrivateCharString(PrivateStoreType type) {
        switch (type) {
            case BUY:
                return "Buyer";
            case SELL:
            case PACKAGE_SELL:
                return "Seller";
            case MANUFACTURE:
                return "Crafter";
            default:
                return "";
        }
    }

    private String getPrivateStoreColor(PrivateStoreType type) {
        switch (type) {
            case BUY:
                return "d0d188";
            case SELL:
                return "b681bd";
            case PACKAGE_SELL:
                return "904343";
            case MANUFACTURE:
                return "f5b431";
            default:
                return "";
        }
    }
}