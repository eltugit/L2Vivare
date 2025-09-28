package gabriel.community.specialCraft.xml;

import gabriel.community.specialCraft.model.SpecialCraftCategory;
import gabriel.community.specialCraft.model.SpecialCraftItem;
import gabriel.community.specialCraft.model.SpecialCraftSubCategory;
import gr.sr.data.xml.AbstractHolder;

import java.util.*;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftHolder extends AbstractHolder {
    private static final SpecialCraftHolder _instance = new SpecialCraftHolder();

    public static SpecialCraftHolder getInstance() {
        return _instance;
    }

    private final Map<String, SpecialCraftCategory> _items = new LinkedHashMap<>();

    public void addRandomCraft(String category, SpecialCraftCategory categories)
    {
        _items.put(category, categories);
    }

    public SpecialCraftCategory getMainCategoryByName(String name){
        return _items.get(name);
    }

    public SpecialCraftSubCategory getSubCategoryByName(SpecialCraftCategory category, String subCategory){
//        if(subCategory.equals("All")){
//            SpecialCraftSubCategory tempCat = new SpecialCraftSubCategory("All", new LinkedList<>());
//            for (SpecialCraftSubCategory value : _items.get(category.getCategoryName()).getSubCategoriesMap().values()) {
//                tempCat.getItems().addAll(value.getItems());
//            }
//            return tempCat;
//        }
        return _items.get(category.getCategoryName()).getSubCategoriesMap().get(subCategory);
    }

    public SpecialCraftItem getItemByTransactionIndex(int index){
        SpecialCraftItem item = null;

        LOOP: for (SpecialCraftCategory value : _items.values()) {
            for (SpecialCraftSubCategory specialCraftSubCategory : value.getSubCategoriesMap().values()) {
                for (SpecialCraftItem specialCraftSubCategoryItem : specialCraftSubCategory.getItems()) {
                    if(specialCraftSubCategoryItem.getTransactionId() == index){
                        item = specialCraftSubCategoryItem;
                        break LOOP;
                    }
                }
            }
        }

        return item;
    }

    public Map<String, SpecialCraftCategory> getItems() {
        return _items;
    }

    @Override
    public int size() {
        return _items.size();
    }

    @Override
    public void clear() {
        _items.clear();
    }
}
