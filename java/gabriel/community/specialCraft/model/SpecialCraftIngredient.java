package gabriel.community.specialCraft.model;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftIngredient {
    private final int id;
    private final long count;
    private final int enchantmentLevel;

    public SpecialCraftIngredient(int id, long count, int enchantmentLevel) {
        this.id = id;
        this.count = count;
        this.enchantmentLevel = enchantmentLevel;
    }

    public int getId() {
        return id;
    }

    public long getCount() {
        return count;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }
}
