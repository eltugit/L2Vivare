package gabriel.events.weeklyRank.objects;

import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class RankRewardObject {
    final int rank;
    final List<RewardObject> rewards;

    public RankRewardObject(int rank, List<RewardObject> rewards) {
        this.rank = rank;
        this.rewards = rewards;
    }

    public int getRank() {
        return rank;
    }

    public List<RewardObject> getRewards() {
        return rewards;
    }
}
