package gabriel.events.siegeRank;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class RewardRank {
    private int itemId;
    private int count;

    public RewardRank(int itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }

    public int getItemId() {
        return itemId;
    }

    public int getCount() {
        return count;
    }
}
