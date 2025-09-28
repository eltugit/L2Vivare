package gabriel.events.siegeRank;


import gabriel.config.GabConfig;
import gabriel.config.SiegeRankConfig;
import gabriel.scriptsGab.utils.BBS;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.clientpackets.RequestSendPost;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class SiegeRankManager {
    private static String BBS_HOME_DIR = "data/html/gabriel/events/SiegeRank/";

    private static class ClanScore implements Comparable<ClanScore> {
        private final String clanLeader;
        private final String clanName;
        private final String clanAlly;

        public ClanScore(String clanLeader, String clanName, int clanAlly, String allyName) {
            this.clanLeader = clanLeader;
            this.clanName = clanName;
            if (clanAlly == 0)
                this.clanAlly = "---";
            else {
                this.clanAlly = allyName;
            }

        }

        public ClanScore(String clanLeader, String clanName, String clanAlly, int kills, int deaths) {
            this.clanLeader = clanLeader;
            this.clanName = clanName;
            this.clanAlly = clanAlly;
            this.kills = kills;
            this.deaths = deaths;
        }

        int kills = 0;
        int deaths = 0;

        public void increaseKills() {
            kills++;
        }

        public void increaseDeaths() {
            deaths++;
        }

        public int getScore() {
            return (kills - deaths) + kills;
        }

        public int getKills() {
            return kills;
        }

        public int getDeaths() {
            return deaths;
        }

        public String getClanLeader() {
            return clanLeader;
        }

        public String getClanName() {
            return clanName;
        }

        public String getClanAlly() {
            return clanAlly;
        }

        @Override
        public int compareTo(ClanScore anotherCountry) {
            return Integer.compare(anotherCountry.getScore(), getScore());
        }
    }

    private Map<String, ClanScore> clanScore = new ConcurrentHashMap<>();
    public boolean reseted = false;


    public void resetStart(boolean isSiege) {
        if (isSiege && reseted)
            return;
        reseted = true;
        clanScore.clear();
    }

    private boolean isSiegeActive() {
        List<Castle> castle = CastleManager.getInstance().getCastles();
        for (Castle castle1 : castle) {
            if (castle1.getCastleId() == 10)
                continue;
            if (castle1.getSiege().isInProgress()) {
                return true;
            }
        }
        return false;
    }

    protected static SiegeRankManager instance;


    public static SiegeRankManager getInstance() {
        if (instance == null)
            instance = new SiegeRankManager();
        return instance;
    }

    private SiegeRankManager() {
//        clanScore.put("LaMark1", new ClanScore("LePirocua1", "LaMark1", "NoAlly1", 245,21));
//        clanScore.put("LaMark2", new ClanScore("LePirocua2", "LaMark2", "NoAlly2", 34,33));
//        clanScore.put("LaMark3", new ClanScore("LePirocua3", "LaMark3", "NoAlly3", 54,34));
//        clanScore.put("LaMark4", new ClanScore("LePirocua4", "LaMark4", "NoAlly4", 12,86));
//        clanScore.put("LaMark5", new ClanScore("LePirocua5", "LaMark5", "NoAlly5", 37,57));
//        clanScore.put("LaMark6", new ClanScore("LePirocua6", "LaMark6", "NoAlly6", 87,12));
//        clanScore.put("LaMark7", new ClanScore("LePirocua7", "LaMark7", "NoAlly7", 57,35));
//        clanScore.put("LaMark8", new ClanScore("LePirocua8", "LaMark8", "NoAlly8", 98,45));
//        clanScore.put("LaMark9", new ClanScore("LePirocua9", "LaMark9", "NoAlly9", 12,89));
//        clanScore.put("LaMark10", new ClanScore("LePirocua10", "LaMark10", "NoAlly10", 47,57));
//        clanScore.put("LaMark11", new ClanScore("LePirocua11", "LaMark11", "NoAlly11", 500,26));
    }


    public void sendToEveryOne(boolean isSiege) {
        if (isSiege && isSiegeActive()) {
            return;
        }

        L2World.getInstance().getPlayers().forEach(this::buildHtm);
        rewardTops();
    }

    private void rewardTops() {
        int rank = 1;
        for (String s : getSortedMap().keySet()) {
            List<RewardRank> rewards = SiegeRankConfig.SIEGE_RANK_REWARD.get(rank);
            if (rewards == null)
                return;

            L2Clan clan = ClanTable.getInstance().getClanByName(s);
            L2PcInstance admin = L2World.getInstance().getPlayer(GabConfig.ER_EVENT_ADMIN_OBJ_ID);
            if (admin == null || !admin.isOnline()) {
                admin = L2PcInstance.load(GabConfig.ER_EVENT_ADMIN_OBJ_ID);
            }
            if (clan != null) {
                RequestSendPost post = new RequestSendPost();
                post.set_receiver(clan.getLeaderName());
                post.set_isCod(false);
                post.set_subject("Siege Rank Reward!");
                post.set_text("Congratulations! Your clan finishet at position: " + rank + " with: " + getSortedMap().get(s).getKills() + " Kills, " + getSortedMap().get(s).getDeaths() + " Deaths and you Scored: " + getSortedMap().get(s).getScore() + " Points! Congratulations!");
                post.setAdminSend(true);

                RequestSendPost.AttachmentItem[] itemArr = new RequestSendPost.AttachmentItem[rewards.size()];

                int i = 0;
                for (RewardRank reward : rewards) {
                    int itemId = reward.getItemId();
                    int itemCount = reward.getCount();

                    if (itemCount == 0)
                        continue;

                    if (itemCount > 1) {
                        admin.getInventory().addItem("MailSiegeRankToPlayer", itemId, itemCount, admin, admin);
                        L2ItemInstance itemInstance = admin.getInventory().getItemByItemId(itemId);
                        RequestSendPost.AttachmentItem requestItem = new RequestSendPost.AttachmentItem(itemInstance.getObjectId(), itemCount);
                        itemArr[i] = requestItem;
                        i++;
                    }
                }
                post.set_items(itemArr);
                post.runImpl();
                rank++;
            }

            if (admin != null && admin.isOnline())
                admin.deleteMe();
        }
    }

    public void parseCommand(String commands, L2PcInstance player) {
        String[] tempC = commands.split(" ");
        String command = tempC[0];
        String subCommand = tempC[1];
        switch (subCommand) {
            case "main":
                buildHtm(player);
                break;
        }
    }


    public void increaseKills(L2PcInstance player) {
        if (player.getClan() == null)
            return;
        clanScore.computeIfAbsent(player.getClan().getName(), k -> new ClanScore(player.getClan().getLeaderName(), player.getClan().getName(), player.getClan().getAllyId(), player.getClan().getAllyName())).increaseKills();
    }


    public void increaseDeaths(L2PcInstance player) {
        if (player.getClan() == null)
            return;
        clanScore.computeIfAbsent(player.getClan().getName(), k -> new ClanScore(player.getClan().getLeaderName(), player.getClan().getName(), player.getClan().getAllyId(), player.getClan().getAllyName())).increaseDeaths();
    }


    private Map<String, ClanScore> getSortedMap() {

        Map<String, ClanScore> temp = new LinkedHashMap<>();
        clanScore.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> temp.put(x.getKey(), x.getValue()));

        return temp;
    }


    private void buildHtm(L2PcInstance player) {
        String content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "index.htm");
        String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "template.htm");
        StringBuilder tmpSb = new StringBuilder();
        int rank = 1;

        for (Map.Entry<String, ClanScore> stringClanScoreEntry : getSortedMap().entrySet()) {
            if (rank > 10) {
                break;
            }
            String clanName = stringClanScoreEntry.getKey();
            ClanScore clanScore = stringClanScoreEntry.getValue();

            if (rank == 1) {
                content = content.replace("%top1ClanName%", clanName);
                content = content.replace("%1killed%", String.valueOf(clanScore.getKills()));
                content = content.replace("%1deaths%", String.valueOf(clanScore.getDeaths()));
                content = content.replace("%1score%", String.valueOf(clanScore.getScore()));
                content = content.replace("%1leader%", String.valueOf(clanScore.getClanLeader()));
                content = content.replace("%1ally%", String.valueOf(clanScore.getClanAlly()));

            } else {
                String tmp = template;
                tmp = tmp.replace("%bg%", bgColor(rank));
                tmp = tmp.replace("%pos%", String.valueOf(rank));
                tmp = tmp.replace("%clan%", String.valueOf(clanScore.getClanName()));
                tmp = tmp.replace("%ally%", String.valueOf(clanScore.getClanAlly()));
                tmp = tmp.replace("%kills%", String.valueOf(clanScore.getKills()));
                tmp = tmp.replace("%deaths%", String.valueOf(clanScore.getDeaths()));
                tmp = tmp.replace("%score%", String.valueOf(clanScore.getScore()));
                tmpSb.append(tmp);
            }
            rank++;
        }

        if (rank == 1) {
            content = content.replace("%top1ClanName%", "Top Killer");
            content = content.replace("%1killed%", "---");
            content = content.replace("%1deaths%", "---");
            content = content.replace("%1score%", "---");
            content = content.replace("%1leader%", "---");
            content = content.replace("%1ally%", "---");
            rank++;
        }

        if (rank < 10) {
            int remaining = 10 - rank;
            for (int i = 0; i <= remaining; i++) {
                String tmp = template;
                tmp = tmp.replace("%bg%", bgColor(rank + i));
                tmp = tmp.replace("%pos%", String.valueOf(rank + i));
                tmp = tmp.replace("%clan%", "---");
                tmp = tmp.replace("%ally%", "---");
                tmp = tmp.replace("%kills%", "---");
                tmp = tmp.replace("%deaths%", "---");
                tmp = tmp.replace("%score%", "---");
                tmpSb.append(tmp);
            }
        }


        content = content.replace("%tablePlayer%", tmpSb.toString());


        BBS.separateAndSend(content, player);
    }

    private String bgColor(int rank) {
        switch (rank) {
            case 1:
                return "302e18";
            case 2:
                return "373736";
            case 3:
                return "352d16";
            case 4:
                return "1c1c19";
            case 5:
                return "2e2c29";
            case 6:
                return "1c1c19";
            case 7:
                return "2e2c29";
            case 8:
                return "1c1c19";
            case 9:
                return "2e2c29";
            case 10:
                return "1c1c19";
        }
        return "1c1c19";
    }

}
