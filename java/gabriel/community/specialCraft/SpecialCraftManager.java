package gabriel.community.specialCraft;

import gabriel.Utils.GabUtils;
import gabriel.community.specialCraft.model.*;
import gabriel.community.specialCraft.xml.SpecialCraftHolder;
import gabriel.community.specialCraft.xml.SpecialCraftParser;
import gabriel.scriptsGab.utils.BBS;
import gabriel.scriptsGab.utils.ItemFunctions;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.util.Util;

import java.util.*;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftManager {
    protected static SpecialCraftManager instance;
    private static final String HTML_INDEX = "data/html/gabriel/CBB/specialCraft/specialCraft.htm";
    private static final String HTML_REDEEM = "data/html/gabriel/CBB/specialCraft/redeem.htm";
    private static final int ITEMS_PER_PAGE = 6;

    public static SpecialCraftManager getInstance() {
        if (instance == null)
            instance = new SpecialCraftManager();
        return instance;
    }

    private SpecialCraftManager() {
        SpecialCraftParser.getInstance().load();
    }

    public void parseCommand(String command, L2PcInstance player){
        String[] cmmds = command.split(" ");
        String actualCommand = cmmds[1];

        switch (actualCommand){
            case "main":{
                String category = cmmds[2];
                String subCategory = cmmds[3];
                int itemIndex = Integer.parseInt(cmmds[4]);
                int page = Integer.parseInt(cmmds[5]);
                showHtm(player, category, subCategory, itemIndex, page);
                break;
            }
            case "create":{
                int itemIndex = Integer.parseInt(cmmds[2]);
                SpecialCraftItem item = SpecialCraftHolder.getInstance().getItemByTransactionIndex(itemIndex);
                roll(player, item);
                if(item == null){
                    player.sendMessage("This item doesnt exist! Report to the administration: itemId:"+itemIndex);
                    return;
                }
                break;
            }
        }
    }

    public void roll(L2PcInstance player, SpecialCraftItem item){
        //eu botei isso aki como segurança.
        if(!player.isGM() && player._randomCraftTimer != 0 && GabUtils.getSecondsFromStart(player._randomCraftTimer) < 1){ // aki é o tempo
            player.sendMessage("Please wait 1 second and try again.");
            return;
        }

        boolean haveItems = true;
        for (SpecialCraftIngredient ingredient : item.getIngredients()) {
            haveItems = player.getInventory().getInventoryItemCount(ingredient.getId(), ingredient.getEnchantmentLevel(), false) > ingredient.getCount();
        }

        if(!haveItems){
            player.sendMessage("You don't have enough items to create this item!");
            return;
        }
        InventoryUpdate iu = new InventoryUpdate();

        for (SpecialCraftIngredient ingredient : item.getIngredients()) {
            L2ItemInstance[] inventoryContents = player.getInventory().getAllItemsByItemId(ingredient.getId(),ingredient.getEnchantmentLevel(), false);

            L2ItemInstance itemToTake = inventoryContents[0];
            // get item with the LOWEST enchantment level from the inventory...
            // +0 is lowest by default...
            if (itemToTake.getEnchantLevel() > 0) {
                for (L2ItemInstance item2 : inventoryContents) {
                    if (item2.getEnchantLevel() < itemToTake.getEnchantLevel()) {
                        itemToTake = item2;
                        // nothing will have enchantment less than 0. If a zero-enchanted
                        // item is found, just take it
                        if (itemToTake.getEnchantLevel() == 0) {
                            break;
                        }
                    }
                }
            }

            L2ItemInstance itemToRemove = player.getInventory().getItemByObjectId(itemToTake.getObjectId());
            if(itemToRemove != null && itemToRemove.getId() != 57){
                iu.addRemovedItem(itemToRemove);
            }
            player.destroyItem("SpecialCraft", itemToTake.getObjectId(), ingredient.getCount(), null, true);
        }


        List<SpecialCraftProduction> production = item.getProductions();
        if(production.size() < 2)
            return;
        double rangeMin = 0;
        double rangeMax = 100;
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();

        production.sort(Comparator.comparingDouble(SpecialCraftProduction::getChance));

        double lowerChance = production.get(0).getChance();

        SpecialCraftProduction chosen = null;

        //Premium player extra chance
        if(player.isPremium())
            randomValue += 10;

        if(randomValue <= lowerChance){
            chosen = production.get(0);
        }else{
            chosen = production.get(1);
        }

        Map<Integer, String> tpls = Util.parseTemplate2(HtmCache.getInstance().getHtm(player, HTML_REDEEM));
        String html = tpls.get(0);


        html = html.replace("%icon%", GabUtils.getItemIcon(chosen.getId()));

        String name = GabUtils.getItemName(chosen.getId());
        if(chosen.getEnchantmentLevel() > 0){
            name += " +"+chosen.getEnchantmentLevel();
        }
        name += " ("+chosen.getChance()+"%)";
        html = html.replace("%itemName%", name);
        html = html.replace("%itemCount%", GabUtils.formatNumberSulfic(chosen.getCount()));
//        player.sendPacket(new TutorialShowHtml(html));
        player.sendPacket(new NpcHtmlMessage(html));


        L2ItemInstance receivedItem = ItemFunctions.addItem2(player, chosen.getId(), chosen.getCount(), true, "");
        if(chosen.getEnchantmentLevel() > 0) {
            receivedItem.setEnchantLevel(chosen.getEnchantmentLevel());
            receivedItem.updateDatabase();
        }

        iu.addModifiedItem(receivedItem);
        player.sendInventoryUpdate(iu);

        player._randomCraftTimer = Calendar.getInstance().getTimeInMillis();
    }

    public void showHtm(L2PcInstance player, String category, String subCategory, int index, int page){
        Map<Integer, String> tpls = Util.parseTemplate2(HtmCache.getInstance().getHtm(player, HTML_INDEX));
        String html = tpls.get(0);
        String btnSubCategory = tpls.get(1);
        String subCategoryFiller = tpls.get(2);
        String subCategoryItems = tpls.get(3);


        SpecialCraftCategory cat = SpecialCraftHolder.getInstance().getMainCategoryByName(category);
        StringBuilder builderSubCat = new StringBuilder();
        StringBuilder builderItems = new StringBuilder();
        int indexSubCat = 0;

        String btnTTempSubCat = btnSubCategory;
        btnTTempSubCat = btnTTempSubCat.replace("%currentCat%", category).replace("%subCat%", "All").replace("%subCatName%", "All");
        builderSubCat.append(btnTTempSubCat);
        indexSubCat++;

        SortedSet<String> keys = new TreeSet<>(cat.getSubCategoriesMap().keySet());
        for (String key : keys) {
            if(key.equals("All"))
                continue;
            btnTTempSubCat = btnSubCategory;
            btnTTempSubCat = btnTTempSubCat.replace("%currentCat%", category).replace("%subCat%", key).replace("%subCatName%", key);
            builderSubCat.append(btnTTempSubCat);
            indexSubCat++;
        }

        //handles the filling for the "invis" sub cats
        int widthToUseForFiller = (6-indexSubCat) * 127;
        subCategoryFiller = subCategoryFiller.replace("%subFillerWidth%", String.valueOf(widthToUseForFiller));
        builderSubCat.append(subCategoryFiller);

        html = html.replace("%tempSubCats%", builderSubCat.toString());
        html = html.replace("%currentCategory%", category + " ("+subCategory+")");

        SpecialCraftSubCategory subCat = SpecialCraftHolder.getInstance().getSubCategoryByName(cat, subCategory);

//        for (SpecialCraftItem item : subCat.getItems()) {
//            String tmp = subCategoryItems;
//            tmp = tmp.replace("%iconCurrItem%", GabUtils.getItemIcon(item.getItemId()));
//            tmp = tmp.replace("%currCat%", category);
//            tmp = tmp.replace("%currSubCat%", subCategory);
//            tmp = tmp.replace("%indexItem%", String.valueOf(item.getTransactionId()));
//            tmp = tmp.replace("%nameCurrItem%", GabUtils.getItemName(item.getItemId()));
//            tmp = tmp.replace("%page%", String.valueOf(page));
//            builderItems.append(tmp);
//        }
        List<SpecialCraftItem> map = subCat.getItems();
        int counter = 0;
        boolean hasNext = map.size() > ITEMS_PER_PAGE;
        boolean hasPrev = page > 1;
        for (int i = (page - 1) * ITEMS_PER_PAGE; i < map.size(); i++) {
            SpecialCraftItem item = map.get(i);
            if (item != null) {

                String tmp = subCategoryItems;

                String itemName = GabUtils.getItemName(item.getItemId());

                if (itemName.length() > 18) {
                    itemName = itemName.substring(0, 18) + "...";
                }
                tmp = tmp.replace("%iconCurrItem%", GabUtils.getItemIcon(item.getItemId()));
                tmp = tmp.replace("%currCat%", category);
                tmp = tmp.replace("%currSubCat%", subCategory);
                tmp = tmp.replace("%indexItem%", String.valueOf(item.getTransactionId()));
                tmp = tmp.replace("%nameCurrItem%", itemName);
                tmp = tmp.replace("%page%", String.valueOf(page));

                builderItems.append(tmp);
            }

            counter++;

            try{
                item = map.get(i+1);
                //Dummy try
            }catch (Exception e){
                hasNext = false;
            }

            if (counter >= ITEMS_PER_PAGE) {
                break;
            }
        }
        html = html.replace("%itemsToShow%", builderItems.toString());
        String nextBtn = tpls.get(4);
        String previousBtn = tpls.get(5);
        String dummyPrev = tpls.get(6);

        StringBuilder navBuilder = new StringBuilder();
        if(hasPrev){
            String tmp = previousBtn;
            tmp = tmp.replace("%currCat%", category);
            tmp = tmp.replace("%currSubCat%", subCategory);
            tmp = tmp.replace("%indexItem%", String.valueOf(index));
            tmp = tmp.replace("%page%", String.valueOf(page-1));
            navBuilder.append(tmp);
        }

        if(hasNext){
            String tmp = nextBtn;
            tmp = tmp.replace("%currCat%", category);
            tmp = tmp.replace("%currSubCat%", subCategory);
            tmp = tmp.replace("%indexItem%", String.valueOf(index));
            tmp = tmp.replace("%page%", String.valueOf(page+1));
            navBuilder.append(tmp);
        }

        if(!hasPrev && !hasNext){
            navBuilder.append(dummyPrev);
        }

        html = html.replace("%navigation%", navBuilder.toString());


        String itemCardTemplateEmpty = tpls.get(7);
        String itemCardTemplateFilled = tpls.get(8);
        String createItemBtn = tpls.get(9);
        String costProdItemTpl = tpls.get(10);

        createItemBtn = createItemBtn.replace("%index%", String.valueOf(index));

        StringBuilder cardBuilder = new StringBuilder();
        StringBuilder costBuilder = new StringBuilder();
        StringBuilder productionBuilder = new StringBuilder();
        SpecialCraftItem item = null;
        if(index == -1){
            cardBuilder.append(itemCardTemplateEmpty);
            cardBuilder.append(itemCardTemplateEmpty);
        }else{
            item = SpecialCraftHolder.getInstance().getItemByTransactionIndex(index);
            if(item == null){
                player.sendMessage("This item doesnt exist! Report to the administration: itemId:"+index);
                return;
            }
            for (SpecialCraftIngredient ingredient : item.getIngredients()) {
                String tmpProd = costProdItemTpl;
                tmpProd = tmpProd.replace("%itemIcon%", GabUtils.getItemIcon(ingredient.getId()));
                tmpProd = tmpProd.replace("%qnt%", GabUtils.formatNumberSulfic(ingredient.getCount()));
                String name = GabUtils.getItemName(ingredient.getId());
                if(ingredient.getEnchantmentLevel() > 0){
                    name += " +"+ingredient.getEnchantmentLevel();
                }
                tmpProd = tmpProd.replace("%itemName%", name);
                costBuilder.append(tmpProd);
            }

            for (SpecialCraftProduction production : item.getProductions()) {
                String tmp = itemCardTemplateFilled;
                tmp = tmp.replace("%border%", getBorder(production.getRank()));
                tmp = tmp.replace("%icon%", GabUtils.getItemIcon(production.getId()));
                tmp = tmp.replace("%qnt%", GabUtils.formatNumberSulfic(production.getCount()));
                cardBuilder.append(tmp);

                String tmpProd = costProdItemTpl;
                tmpProd = tmpProd.replace("%itemIcon%", GabUtils.getItemIcon(production.getId()));
                tmpProd = tmpProd.replace("%qnt%", GabUtils.formatNumberSulfic(production.getCount()));
                String name = GabUtils.getItemName(production.getId());
                if(production.getEnchantmentLevel() > 0){
                    name += " +"+production.getEnchantmentLevel();
                }
                name += " ("+production.getChance()+"%)";
                tmpProd = tmpProd.replace("%itemName%", name);
                productionBuilder.append(tmpProd);

            }
        }

        html = html.replace("%cardsSpace%", cardBuilder.toString());
        html = html.replace("%production%", productionBuilder.toString());
        html = html.replace("%cost%", costBuilder.toString());
        html = html.replace("%createItem%", index > 0 ? createItemBtn : "");


        BBS.separateAndSend(html, player);
    }

    private String getBorder(SpecialCraftRank rank){
        switch (rank){
            case RARE:
                return "GT.LCoinShopCraftCard_04";
            case MEDIUM:
                return "GT.LCoinShopCraftCard_03";
            case COMMON:
                return "GT.LCoinShopCraftCard_02";
            default:
                return "GT.LCoinShopCraftCard_01";
        }
    }
}
