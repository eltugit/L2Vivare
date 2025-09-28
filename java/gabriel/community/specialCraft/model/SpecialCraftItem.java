package gabriel.community.specialCraft.model;

import java.util.List;

public class SpecialCraftItem {
    private final int transactionId;
    private final int itemId;
    private final List<SpecialCraftIngredient> ingredients;
    private final List<SpecialCraftProduction> productions;

    public SpecialCraftItem(int transactionId, int itemId, List<SpecialCraftIngredient> ingredients, List<SpecialCraftProduction> productions) {
        this.transactionId = transactionId;
        this.itemId = itemId;
        this.ingredients = ingredients;
        this.productions = productions;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getItemId() {
        return itemId;
    }

    public List<SpecialCraftIngredient> getIngredients() {
        return ingredients;
    }

    public List<SpecialCraftProduction> getProductions() {
        return productions;
    }
}
