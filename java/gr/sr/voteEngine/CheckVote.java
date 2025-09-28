
package gr.sr.voteEngine;

import l2r.gameserver.model.actor.instance.L2PcInstance;

final class CheckVote implements Runnable {
    private final L2PcInstance player;
    private final RewardVote rewardVote;

    public CheckVote(RewardVote rewardVote, L2PcInstance player) {
        this.rewardVote = rewardVote;
        this.player = player;
    }

    public final void run() {
        if (rewardVote._cannotUsePlayers.contains(this.player)) {
            rewardVote._cannotUsePlayers.remove(this.player);
        }

    }
}
