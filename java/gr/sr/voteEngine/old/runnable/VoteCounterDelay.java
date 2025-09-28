package gr.sr.voteEngine.old.runnable;

import gr.sr.configsEngine.configs.impl.CommunityServicesConfigs;
import gr.sr.configsEngine.configs.impl.IndividualVoteSystemConfigs;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.Managers.ServicesBBSManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ShowBoard;

public class VoteCounterDelay implements Runnable {
    private L2PcInstance player;
    private String siteLink;
    private boolean isSiteVoted;
    private static int hopeZoneCounter = 0;
    private static int topZoneCounter = 0;
    private static int topServers200Counter = 0;
    private static int topGs200Counter = 0;
    private static int l2TopCoCounter = 0;
    private static int l2NetworkCounter = 0;
    private static int gameBytesCounter = 0;

    public VoteCounterDelay(L2PcInstance player, String site, boolean isSiteVoted) {
        this.player = player;
        this.siteLink = site;
        this.isSiteVoted = isSiteVoted;
    }

    public void run() {
        if (this.isSiteVoted) {
            setCounter(IndividualVoteSystemConfigs.VOTE_DELAY_CHECK + 1, this.siteLink);
        }

        if (getCounter(this.siteLink) > 0) {
            setCounter(getCounter(this.siteLink) - 1, this.siteLink);
            this.sendHtml(this.siteLink, false);
            ThreadPoolManager.getInstance().scheduleGeneral(new VoteCounterDelay(this.player, this.siteLink, false), 1000L);
        } else {
            this.sendHtml(this.siteLink, true);
        }
    }

    private void sendHtml(String counter, boolean showBanners) {
        String html;
        if (showBanners) {
            html = HtmCache.getInstance().getHtm(this.player, this.player.getHtmlPrefix(), "data/html/CommunityBoard/services/vote.htm").replaceAll("%voteBanners%", ServicesBBSManager.getVoteBanners(this.player)).replaceAll("%command%", CommunityServicesConfigs.BYPASS_COMMAND);
            this.separateAndSend(html, this.player);
        } else {
            html = HtmCache.getInstance().getHtm(this.player, this.player.getHtmlPrefix(), "data/html/CommunityBoard/services/vote_on.htm").replaceAll("%counter%", String.valueOf(getCounter(counter)));
            this.separateAndSend(html, this.player);
        }
    }

    protected void separateAndSend(String html, L2PcInstance player) {
        if ((html = html.replace("\t", "")).length() < 8180) {
            player.sendPacket(new ShowBoard(html, "101"));
            player.sendPacket(new ShowBoard((String)null, "102"));
            player.sendPacket(new ShowBoard((String)null, "103"));
        } else if (html.length() < 16360) {
            player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
            player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
            player.sendPacket(new ShowBoard((String)null, "103"));
        } else {
            if (html.length() < 24540) {
                player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
                player.sendPacket(new ShowBoard(html.substring(8180, 16360), "102"));
                player.sendPacket(new ShowBoard(html.substring(16360, html.length()), "103"));
            }
        }
    }

    public static void setCounter(int vote, String site) {
        switch(site) {
            case "HopZone":
                hopeZoneCounter = vote;
                break;
            case "GameBytes":
                gameBytesCounter = vote;
                break;
            case "TopServers200":
                topServers200Counter = vote;
                break;
            case "TopGs200":
                topGs200Counter = vote;
                break;
            case "L2TopCo":
                l2TopCoCounter = vote;
                break;
            case "TopZone":
                topZoneCounter = vote;
                break;
            case "L2NetWork":
                l2NetworkCounter = vote;
                break;
        }

    }

    public static int getCounter(String site) {
        switch(site) {
            case "HopZone":
                return hopeZoneCounter;
            case "GameBytes":
                return gameBytesCounter;
            case "TopServers200":
                return topServers200Counter;
            case "TopGs200":
                return topGs200Counter;
            case "L2TopCo":
                return l2TopCoCounter;
            case "TopZone":
                return topZoneCounter;
            case "L2NetWork":
                return l2NetworkCounter;
            default:
                return 0;
        }
    }
}
