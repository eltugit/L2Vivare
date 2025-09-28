package gr.sr.voteEngine;

import gr.sr.configsEngine.configs.impl.GetRewardVoteSystemConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class   VoteRead {
    private static final Logger log = LoggerFactory.getLogger(VoteRead.class);

    public VoteRead() {
    }

    public static long checkVotedIP(String command) {
        long votedTime = 0L;

        VoteType[] voteTypes = VoteType.values();

        for (VoteType voteType : voteTypes) {
            if (voteType.isEnabled()) {
                if (canVote(voteType, command, voteType.isEnabled())) {
                    votedTime = System.currentTimeMillis() / 1000L;
                } else if (GetRewardVoteSystemConfigs.FORCE_VOTE_ALL_ENABLED_SITES) {
                    votedTime = 0L;
                }
            }
        }

        return votedTime;
    }

    private static boolean canVote(VoteType voteType, String ip, boolean enabled) {
        if (!enabled) {
            return true;
        } else {
            String url = voteType.getVoteLink().replace("%ip%", ip);
            boolean voted = false;
            try {
                URLConnection urlConnection = (new URL(url)).openConnection();
                urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                String urlLine;
                while((urlLine = bufferedReader.readLine()) != null) {
                    if (urlLine.toLowerCase().contains(voteType.getWord().toLowerCase())) {
                        voted = true;
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(VoteRead.class.getSimpleName() + ": Couldn't connect to " + voteType.getLink() + ".");
                if (GetRewardVoteSystemConfigs.ENABLE_NEXT_CHECK_SYSTEM) {
                    voted = true;
                    log.error(VoteRead.class.getSimpleName() + ": Check excluded.");
                }
            }

            if (GetRewardVoteSystemConfigs.DEBUG_VOTING) {
                log.info(VoteRead.class.getSimpleName() + ": " + (voted ? "SUCCESS" : "FAILED") + " for " + voteType.getLink() + " Client IP: " + ip);
            }

            return voted;
        }
    }
}
