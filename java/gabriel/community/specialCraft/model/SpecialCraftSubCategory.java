package gabriel.community.specialCraft.model;

import java.util.List;

public class SpecialCraftSubCategory {
    private final String SubCategory;
    private final List<SpecialCraftItem> items;

    public SpecialCraftSubCategory(String subCategory, List<SpecialCraftItem> items) {
        SubCategory = subCategory;
        this.items = items;
    }

    public String getSubCategoryName() {
        return SubCategory;
    }

    public List<SpecialCraftItem> getItems() {
        return items;
    }
}
