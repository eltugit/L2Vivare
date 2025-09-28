package gr.sr.raidEngine;

public class RaidDrop {
    private final int itemId;
    private final int min;
    private final int max;
    private final float chance;
    private final int maxOccurs;
    private final int minOccurs;

    public RaidDrop(int itemId, int min, int max, float chance, int maxOccurs, int minOccurs) {
        this.itemId = itemId;
        this.min = min;
        this.max = max;
        this.chance = chance;
        this.maxOccurs = maxOccurs;
        this.minOccurs = minOccurs;
    }

    public int getItemId() {
        return this.itemId;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    public float getChance() {
        return this.chance;
    }

    public int getMaxOccurs() {
        return this.maxOccurs;
    }

    public int getMinOccurs() {
        return this.minOccurs;
    }
}
