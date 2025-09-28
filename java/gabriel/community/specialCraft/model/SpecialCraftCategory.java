package gabriel.community.specialCraft.model;

import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftCategory {
    private final String Category;
    private final Map<String, SpecialCraftSubCategory> list;

    public SpecialCraftCategory(String category, Map<String, SpecialCraftSubCategory> list) {
        Category = category;
        this.list = list;
    }

    public String getCategoryName() {

        return Category;
    }

    public Map<String, SpecialCraftSubCategory> getSubCategoriesMap() {
        return list;
    }
}

