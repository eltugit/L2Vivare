package gr.sr.voteEngine.old.runnable;

import gr.sr.configsEngine.configs.impl.IndividualVoteSystemConfigs;
import gr.sr.voteEngine.old.VoteHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.Iterator;
import java.util.Map.Entry;

public class VoteDelay implements Runnable {
    private L2PcInstance player;
    private int voteCount;
    private String siteVoted;

    public VoteDelay(L2PcInstance ply, String siteVoted, int voteCount) {
        this.player = ply;
        this.voteCount = voteCount;
        this.siteVoted = siteVoted;
    }

    public void run() {
        this.player.sendMessage("Checking votes...");
        if (this.voteCount < VoteHandler.getVotes(this.player, false, this.siteVoted)) {
            this.player.sendPacket(new ExShowScreenMessage("Thank you for voting for us!", 5000));
            this.player.sendMessage("Voting succeed!");
            handleReward(this.player, this.siteVoted);
            VoteHandler.setIsActive(false, this.siteVoted);
            this.player.setIsVoting(false);
            this.player.setVar(this.siteVoted, String.valueOf(System.currentTimeMillis()));
        } else {
            VoteHandler.setIsActive(false, this.siteVoted);
            this.player.setIsVoting(false);
            this.player.sendPacket(new ExShowScreenMessage("Voting failed!", 5000));
            this.player.sendMessage("There are still " + this.voteCount + " vote(s). Your vote was not counted, please try again later.");
            if (IndividualVoteSystemConfigs.ENABLE_TRIES) {
                this.player.setVar("vote_tries", String.valueOf(Integer.parseInt(this.player.getVar("vote_tries", "0")) - 1));
            }

        }
    }

    private static void handleReward(L2PcInstance player, String voteString) {
        Iterator it;
        Entry reward;
        switch(voteString) {
            case "HopZone":
                it = IndividualVoteSystemConfigs.HOPZONE_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "GameBytes":
                it = IndividualVoteSystemConfigs.GAMEBYTES_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "TopServers200":
                it = IndividualVoteSystemConfigs.TOPSERVERS200_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "TopGs200":
                it = IndividualVoteSystemConfigs.TOPGS200_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "L2TopCo":
                it = IndividualVoteSystemConfigs.TOPCO_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "TopZone":
                it = IndividualVoteSystemConfigs.TOPZONE_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
            case "L2NetWork":
                it = IndividualVoteSystemConfigs.NETWORK_REWARDS.entrySet().iterator();

                while(it.hasNext()) {
                    reward = (Entry)it.next();
                    player.addItem("Vote reward for " + voteString, (Integer)reward.getKey(), (long)(Integer)reward.getValue(), player, true);
                }
                break;
        }
    }
}
