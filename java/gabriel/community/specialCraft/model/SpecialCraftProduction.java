package gabriel.community.specialCraft.model;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftProduction {
    private final int id;
    private final long count;
    private final int enchantmentLevel;
    private final double chance;
    private final SpecialCraftRank rank;

    public SpecialCraftProduction(int id, long count, int enchantmentLevel, double chance, SpecialCraftRank rank) {
        this.id = id;
        this.count = count;
        this.enchantmentLevel = enchantmentLevel;
        this.chance = chance;
        this.rank = rank;
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

    public double getChance() {
        return chance;
    }

    public SpecialCraftRank getRank() {
        return rank;
    }
}
