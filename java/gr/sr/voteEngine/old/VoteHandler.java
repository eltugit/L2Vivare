package gr.sr.voteEngine.old;


import gr.sr.configsEngine.configs.impl.IndividualVoteSystemConfigs;
import gr.sr.securityEngine.SecurityActions;
import gr.sr.securityEngine.SecurityType;
import gr.sr.voteEngine.old.runnable.VoteCounterDelay;
import gr.sr.voteEngine.old.runnable.VoteDelay;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class VoteHandler {
    private static boolean isVotingTopZone = false;
    private static boolean isVotingHopZone = false;
    private static boolean isVotingL2TopCo = false;
    private static boolean isVotingGameBytes = false;
    private static boolean isVotingL2NetWork = false;
    private static boolean isVotingTopGs200 = false;
    private static boolean isVotingTopServers200 = false;
    private static long delayToVote = 43200000L;
    private static int VOTE_DELAY_CHECK;

    private VoteHandler() {
    }
    
    public static void onBypassFeedback(L2PcInstance player, String command) {
        if (player != null) {
            if (command.startsWith("vote_")) {
                command = command.split("_")[1];
                if (voteChecks(player, command)) {
                    preActivateVoting(player, command);
                }
            }
        }
    }

    public static int getVotes(final L2PcInstance l2PcInstance, final boolean isTryingToVote, final String voteLink) {
        int n = 0;
        int i = -1;
        String spec = "";
        int n2 = 0;
        switch (voteLink) {
            case "HopZone": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_HOPZONE;
                break;
            }
            case "TopZone": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_TOPZONE;
                break;
            }
            case "TopServers200": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_TOPSERVERS200;
                break;
            }
            case "TopGs200": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_TOPGS200;
                break;
            }
            case "L2TopCo": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_TOPCO;
                break;
            }
            case "L2NetWork": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_NETWORK;
                break;
            }
            case "GameBytes": {
                spec = IndividualVoteSystemConfigs.VOTE_LINK_GAMEBYTES;
                break;
            }
        }
        try {
            final URLConnection openConnection = new URL(spec).openConnection();
            switch (voteLink) {
                case "TopZone": {
                    openConnection.addRequestProperty("User-Agent", "L2TopZone");
                    break;
                }
                default: {
                    openConnection.addRequestProperty("User-Agent", "Mozilla/4.76");
                    break;
                }
            }
            String line;
            while ((line = new BufferedReader(new InputStreamReader(openConnection.getInputStream())).readLine()) != null && n == 0) {
                switch (voteLink) {
                    case "TopZone": {
                        i = Integer.valueOf(line);
                        n = 1;
                        continue;
                    }
                    case "HopZone": {
                        if (line.contains("rank tooltip")) {
                            i = Integer.valueOf(line.split(">")[2].replace("</span", ""));
                            n = 1;
                            continue;
                        }
                        continue;
                    }
                    case "TopServers200": {
                        if (line.contains("Total this month:")) {
                            i = Integer.valueOf(line.split(">")[4].replace("</td", ""));
                            n = 1;
                            continue;
                        }
                        continue;
                    }
                    case "TopGs200": {
                        if (line.contains("In This Month:")) {
                            i = Integer.valueOf(line.split(">")[6].replace("</td", ""));
                            n = 1;
                            continue;
                        }
                        continue;
                    }
                    case "L2TopCo": {
                        i = Integer.valueOf(line);
                        n = 1;
                        continue;
                    }
                    case "L2NetWork": {
                        if (line.contains("tls-in-sts")) {
                            i = Integer.valueOf(line.split(">")[2].replace("</b", ""));
                            n = 1;
                            continue;
                        }
                        continue;
                    }
                    case "GameBytes": {
                        if (line.contains("<span class=\"last\">")) {
                            ++n2;
                        }
                        if (n2 == 6) {
                            i = Integer.valueOf(line.split(">")[1].replace("</span", ""));
                            n = 1;
                            continue;
                        }
                        continue;
                    }
                }
            }
            if (isTryingToVote) {
                if (i < 0) {
                    l2PcInstance.sendMessage("Connection failed!");
                    handleVote(l2PcInstance, false, voteLink);
                }
                else {
                    l2PcInstance.sendMessage("Connection established!");
                    l2PcInstance.sendMessage("Current votes: " + String.valueOf(i));
                }
            }
        }
        catch (Exception ex) {
            handleVote(l2PcInstance, false, voteLink);
            if (isTryingToVote) {
                l2PcInstance.sendMessage(voteLink + " server is offline try again later, connection failed.");
            }
        }
        return i;
    }

    
    public static String getWhenCanVote(L2PcInstance player, String var1) {
        long whenToVote = Long.parseLong(player.getVar(var1, "0"));
        int hours = (int)((whenToVote) + delayToVote - System.currentTimeMillis()) / 3600000 % 24;
        int minutes = (int)(whenToVote + delayToVote - System.currentTimeMillis()) / 1000 % 60;
        int seconds = (int)((whenToVote + delayToVote - System.currentTimeMillis()) / 1000L % 60L);
        return whenToVote + delayToVote < System.currentTimeMillis() ? "<font color=a22020>NOW!</font>" : "in <font color=AE9977>" + hours + "</font> hours,<font color=AE9977>" + minutes + "</font> min & <font color=AE9977>" + seconds + "</font> sec";
    }
    
    public static void preActivateVoting(L2PcInstance player, String site) {
        setIsActive(true, site);
        if (Long.parseLong(player.getVar(site, "0")) + delayToVote >= System.currentTimeMillis()) {
            handleVote(player, true, site);
        } else {
            player.sendMessage("Connecting to " + site + "...Please wait.");
            int getVotes;
            if ((getVotes = getVotes(player, true, site)) < 0) {
                handleVote(player, false, site);
            } else {
                player.setIsVoting(true);
                ThreadPoolManager.getInstance().scheduleGeneral(new VoteCounterDelay(player, site, true), 10L);
                ThreadPoolManager.getInstance().scheduleGeneral(new VoteDelay(player, site, getVotes), (long)(VOTE_DELAY_CHECK * 1000));
            }
        }
    }

    private static void handleVote(L2PcInstance player, boolean isTryingToVote, String site) {
        setIsActive(false, site);
        if (isTryingToVote) {
            player.sendMessage("12 hours have to pass till you are able to vote again.");
        }

    }
    
    public static boolean voteChecks(L2PcInstance player, String site) {
        if (player.isVoting()) {
            player.sendMessage("You are already voting. Try again later.");
            return false;
        } else {
            boolean canVote = true;
            switch(site) {
                case "HopZone":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_HOPZONE) {
                        canVote = false;
                    }
                    break;
                case "GameBytes":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_GAMEBYTES) {
                        canVote = false;
                    }
                    break;
                case "TopServers200":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_TOPSERVERS200) {
                        canVote = false;
                    }
                    break;
                case "TopGs200":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_TOPGS00) {
                        canVote = false;
                    }
                    break;
                case "L2TopCo":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_TOPCO) {
                        canVote = false;
                    }
                    break;
                case "TopZone":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_TOPZONE) {
                        canVote = false;
                    }
                    break;
                case "L2NetWork":
                    if (!IndividualVoteSystemConfigs.VOTE_MANAGER_ALLOW_NETWORK) {
                        canVote = false;
                    }
                    break;
                default:
                    SecurityActions.startSecurity(player, SecurityType.VOTE_SYSTEM);
                    return false;
            }

            if (!canVote) {
                player.sendMessage("Site " + site + " is disabled by admin.");
                return false;
            } else if (getIsActive(site)) {
                player.sendMessage("Someone is already voting for " + site + ". Wait for your turn or try other topsite please!");
                return false;
            } else if (IndividualVoteSystemConfigs.ENABLE_TRIES && Integer.parseInt(player.getVar("vote_tries", "0")) <= 0) {
                player.sendMessage("Due to your multiple failures in voting you lost your chance to vote today.");
                return false;
            } else {
                return true;
            }
        }
    }

    public static void setIsActive(boolean isVoting, String site) {
        switch(site) {
            case "HopZone":
                isVotingHopZone = isVoting;
                break;
            case "GameBytes":
                isVotingGameBytes = isVoting;
                break;
            case "TopServers200":
                isVotingTopServers200 = isVoting;
                break;
            case "TopGs200":
                isVotingTopGs200 = isVoting;
                break;
            case "L2TopCo":
                isVotingL2TopCo = isVoting;
                break;
            case "TopZone":
                isVotingTopZone = isVoting;
                break;
            case "L2NetWork":
                isVotingL2NetWork = isVoting;
                break;
        }
    }

    public static boolean getIsActive(String site) {
        switch(site) {
            case "HopZone":
                return isVotingHopZone;
            case "GameBytes":
                return isVotingGameBytes;
            case "TopServers200":
                return isVotingTopServers200;
            case "TopGs200":
                return isVotingTopGs200;
            case "L2TopCo":
                return isVotingL2TopCo;
            case "TopZone":
                return isVotingTopZone;
            case "L2NetWork":
                return isVotingL2NetWork;
            default:
                return false;
        }
    }

    static {
        VOTE_DELAY_CHECK = IndividualVoteSystemConfigs.VOTE_DELAY_CHECK;
    }
}
