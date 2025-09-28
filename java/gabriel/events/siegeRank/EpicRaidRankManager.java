package gabriel.events.siegeRank;


import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gabriel.scriptsGab.utils.BBS;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.clientpackets.RequestSendPost;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class EpicRaidRankManager {
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

    public void resetStart() {
        clanScore.clear();
    }

    private boolean isActive() {
        return EpicRaidManager.getInstance().isActive();
    }

    protected static EpicRaidRankManager instance;


    public static EpicRaidRankManager getInstance() {
        if (instance == null)
            instance = new EpicRaidRankManager();
        return instance;
    }

    private EpicRaidRankManager() {
    }


    public void sendToEveryOne() {
        if(!GabConfig.ER_RANK_ENABLE)
            return;
//        if (isSiege && isActive()) {
//            return;
//        }

        L2World.getInstance().getPlayers().forEach(this::buildHtm);
        if (!GabConfig.ER_RANK_ONLY_HTML)
            rewardTops();
    }

    private void rewardTops() {
//        System.out.println("Rewarding Tops EpicRaidManager");
        if(!GabConfig.ER_RANK_ENABLE)
            return;
        int rank = 1;
        for (String s : getSortedMap().keySet()) {
            List<RewardRank> rewards = GabConfig.ER_RANK_REWARD.get(rank);
            if (rewards == null) {
//                System.out.println("Rank reward is null for rank: "+rank);
                return;
            }

//            System.out.println("Gettingclan: "+s);
            L2Clan clan = ClanTable.getInstance().getClanByName(s);

            if (clan != null) {

                Map<Integer, Long> items = new HashMap<>();

                for (RewardRank reward : rewards) {
                    int itemId = reward.getItemId();
                    long itemCount = Long.parseLong(String.valueOf(reward.getCount()));

                    if (itemCount == 0)
                        continue;

                    if (itemCount > 0) {
                        items.put(itemId, itemCount);
                    }
                }

                //pronto agr vai mostrar o nome
                GabUtils.sendMailToPlayer(GabConfig.ER_EVENT_ADMIN_OBJ_ID, "Epic Rank", clan.getLeaderId(), "Epic Rank Reward!", "Congratulations! Your clan finished at position: " + rank + " with: " + getSortedMap().get(s).getKills() + " Kills, " + getSortedMap().get(s).getDeaths() + " Deaths and you Scored: " + getSortedMap().get(s).getScore() + " Points! Congratulations!", items);
                rank++;
            }else{
                System.out.println("Clan: is null:: "+s);
            }
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
