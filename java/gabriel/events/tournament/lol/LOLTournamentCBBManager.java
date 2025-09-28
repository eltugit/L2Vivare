package gabriel.events.tournament.lol;

import gabriel.events.tournament.TFight;
import gabriel.scriptsGab.utils.BBS;
import gr.sr.interf.SunriseEvents;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;

import java.util.Arrays;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class LOLTournamentCBBManager {
    private static final String BBS_HOME_DIR = "data/html/gabriel/CBB/gabriel/loltournament/";


    private LOLTournamentCBBManager() {

    }

    protected static LOLTournamentCBBManager instance;

    public static LOLTournamentCBBManager getInstance() {
        if (instance == null)
            instance = new LOLTournamentCBBManager();
        return instance;
    }
    /*
    if(elo < 100){
            return new String[]{"AeronSysTextures9.Unranked0", "Unranked"};
        }else if(elo > 100 && elo <= 200){
            return new String[]{"AeronSysTextures9.Bronze7", "Bronze 8"};
        }else if(elo > 200 && elo <= 300){
            return new String[]{"AeronSysTextures9.Bronze6", "Bronze 7"};
        }else if(elo > 300 && elo <= 400){
            return new String[]{"AeronSysTextures9.Bronze5", "Bronze 6"};
        }else if(elo > 400 && elo <= 500){
            return new String[]{"AeronSysTextures9.Bronze4", "Bronze 5"};
        }else if(elo > 500 && elo <= 600){
            return new String[]{"AeronSysTextures9.Bronze3", "Bronze 4"};
        }else if(elo > 600 && elo <= 700){
            return new String[]{"AeronSysTextures9.Bronze2", "Bronze 3"};
        }else if(elo > 700 && elo <= 800){
            return new String[]{"AeronSysTextures9.Bronze1", "Bronze 2"};
        }else if(elo > 800 && elo <= 900){
            return new String[]{"AeronSysTextures9.Bronze0", "Bronze 1"};
        }else if(elo > 900 && elo <= 1000){
            return new String[]{"AeronSysTextures9.Silver7", "Silver 8"};
        }else if(elo > 1000 && elo <= 1100){
            return new String[]{"AeronSysTextures9.Silver6", "Silver 7"};
        }else if(elo > 1100 && elo <= 1200){
            return new String[]{"AeronSysTextures9.Silver5", "Silver 6"};
        }else if(elo > 1200 && elo <= 1300){
            return new String[]{"AeronSysTextures9.Silver4", "Silver 5"};
        }else if(elo > 1300 && elo <= 1400){
            return new String[]{"AeronSysTextures9.Silver3", "Silver 4"};
        }else if(elo > 1400 && elo <= 1500){
            return new String[]{"AeronSysTextures9.Silver2", "Silver 3"};
        }else if(elo > 1500 && elo <= 1600){
            return new String[]{"AeronSysTextures9.Silver1", "Silver 2"};
        }else if(elo > 1600 && elo <= 1700){
            return new String[]{"AeronSysTextures9.Silver0", "Silver 1"};
        }else if(elo > 1700 && elo <= 1800){
            return new String[]{"AeronSysTextures9.Gold7", "Gold 8"};
        }else if(elo > 1800 && elo <= 1900){
            return new String[]{"AeronSysTextures9.Gold6", "Gold 7"};
        }else if(elo > 1900 && elo <= 2000){
            return new String[]{"AeronSysTextures9.Gold5", "Gold 6"};
        }else if(elo > 2000 && elo <= 2100){
            return new String[]{"AeronSysTextures9.Gold4", "Gold 5"};
        }else if(elo > 2100 && elo <= 2200){
            return new String[]{"AeronSysTextures9.Gold3", "Gold 4"};
        }else if(elo > 2200 && elo <= 2300){
            return new String[]{"AeronSysTextures9.Gold2", "Gold 3"};
        }else if(elo > 2300 && elo <= 2400){
            return new String[]{"AeronSysTextures9.Gold1", "Gold 2"};
        }else if(elo > 2400 && elo <= 2500){
            return new String[]{"AeronSysTextures9.Gold0", "Gold 1"};
        }else if(elo > 2500 && elo <= 2600){
            return new String[]{"AeronSysTextures9.Platinum7", "Platinum 8"};
        }else if(elo > 2600 && elo <= 2700){
            return new String[]{"AeronSysTextures9.Platinum6", "Platinum 7"};
        }else if(elo > 2700 && elo <= 2800){
            return new String[]{"AeronSysTextures9.Platinum5", "Platinum 6"};
        }else if(elo > 2800 && elo <= 2900){
            return new String[]{"AeronSysTextures9.Platinum4", "Platinum 5"};
        }else if(elo > 2900 && elo <= 3000){
            return new String[]{"AeronSysTextures9.Platinum3", "Platinum 4"};
        }else if(elo > 3000 && elo <= 3100){
            return new String[]{"AeronSysTextures9.Platinum2", "Platinum 3"};
        }else if(elo > 3100 && elo <= 3200){
            return new String[]{"AeronSysTextures9.Platinum1", "Platinum 2"};
        }else if(elo > 3200 && elo <= 3300){
            return new String[]{"AeronSysTextures9.Platinum0", "Platinum 1"};
        }else if(elo > 3300 && elo <= 3400){
            return new String[]{"AeronSysTextures9.Diamond7", "Diamond 8"};
        }else if(elo > 3400 && elo <= 3500){
            return new String[]{"AeronSysTextures9.Diamond6", "Diamond 7"};
        }else if(elo > 3500 && elo <= 3600){
            return new String[]{"AeronSysTextures9.Diamond5", "Diamond 6"};
        }else if(elo > 3600 && elo <= 3700){
            return new String[]{"AeronSysTextures9.Diamond4", "Diamond 5"};
        }else if(elo > 3700 && elo <= 3800){
            return new String[]{"AeronSysTextures9.Diamond3", "Diamond 4"};
        }else if(elo > 3800 && elo <= 3900){
            return new String[]{"AeronSysTextures9.Diamond2", "Diamond 3"};
        }else if(elo > 3900 && elo <= 4000){
            return new String[]{"AeronSysTextures9.Diamond1", "Diamond 2"};
        }else if(elo > 4000 && elo <= 4100){
            return new String[]{"AeronSysTextures9.Diamond0", "Diamond 1"};
        }else if(elo > 4100 && elo <= 4500){
            return new String[]{"AeronSysTextures9.Master0", "Master"};
        }else if(elo > 4500){
            return new String[]{"AeronSysTextures9.Challenger0", "Challenger"};
        }
     */
    public String[] getTag(int elo){
        if(elo < 10){
            return new String[]{"AeronSysTextures9.Unranked0", "Unranked"};
        }else if(elo > 10 && elo <= 20){
            return new String[]{"AeronSysTextures9.Bronze4", "Bronze 5"};
        }else if(elo > 20 && elo <= 30){
            return new String[]{"AeronSysTextures9.Bronze3", "Bronze 4"};
        }else if(elo > 30 && elo <= 40){
            return new String[]{"AeronSysTextures9.Bronze2", "Bronze 3"};
        }else if(elo > 40 && elo <= 50){
            return new String[]{"AeronSysTextures9.Bronze1", "Bronze 2"};
        }else if(elo > 50 && elo <= 60){
            return new String[]{"AeronSysTextures9.Bronze0", "Bronze 1"};
        }else if(elo > 60 && elo <= 70){
            return new String[]{"AeronSysTextures9.Silver4", "Silver 5"};
        }else if(elo > 70 && elo <= 80){
            return new String[]{"AeronSysTextures9.Silver3", "Silver 4"};
        }else if(elo > 80 && elo <= 90){
            return new String[]{"AeronSysTextures9.Silver2", "Silver 3"};
        }else if(elo > 90 && elo <= 100){
            return new String[]{"AeronSysTextures9.Silver1", "Silver 2"};
        }else if(elo > 100 && elo <= 110){
            return new String[]{"AeronSysTextures9.Silver0", "Silver 1"};
        }else if(elo > 110 && elo <= 120){
            return new String[]{"AeronSysTextures9.Gold4", "Gold 5"};
        }else if(elo > 120 && elo <= 130){
            return new String[]{"AeronSysTextures9.Gold3", "Gold 4"};
        }else if(elo > 130 && elo <= 140){
            return new String[]{"AeronSysTextures9.Gold2", "Gold 3"};
        }else if(elo > 140 && elo <= 150){
            return new String[]{"AeronSysTextures9.Gold1", "Gold 2"};
        }else if(elo > 150 && elo <= 160){
            return new String[]{"AeronSysTextures9.Gold0", "Gold 1"};
        }else if(elo > 160 && elo <= 170){
            return new String[]{"AeronSysTextures9.Platinum4", "Platinum 5"};
        }else if(elo > 170 && elo <= 180){
            return new String[]{"AeronSysTextures9.Platinum3", "Platinum 4"};
        }else if(elo > 180 && elo <= 190){
            return new String[]{"AeronSysTextures9.Platinum2", "Platinum 3"};
        }else if(elo > 190 && elo <= 200){
            return new String[]{"AeronSysTextures9.Platinum1", "Platinum 2"};
        }else if(elo > 200 && elo <= 210){
            return new String[]{"AeronSysTextures9.Platinum0", "Platinum 1"};
        }else if(elo > 210 && elo <= 220){
            return new String[]{"AeronSysTextures9.Diamond4", "Diamond 5"};
        }else if(elo > 220 && elo <= 230){
            return new String[]{"AeronSysTextures9.Diamond3", "Diamond 4"};
        }else if(elo > 230 && elo <= 240){
            return new String[]{"AeronSysTextures9.Diamond2", "Diamond 3"};
        }else if(elo > 240 && elo <= 250){
            return new String[]{"AeronSysTextures9.Diamond1", "Diamond 2"};
        }else if(elo > 250 && elo <= 300){
            return new String[]{"AeronSysTextures9.Diamond0", "Diamond 1"};
        }else if(elo > 300 && elo <= 350){
            return new String[]{"AeronSysTextures9.Master0", "Master"};
        }else if(elo > 350){
            return new String[]{"AeronSysTextures9.Challenger0", "Challenger"};
        }
        return new String[]{"AeronSysTextures9.Unranked0", "Unranked"};
    }

    public void parseCommand(String command, L2PcInstance player) {
        String subCommand = command.split(";")[1];
        final LOLTournamentManager manager = LOLTournamentManager.getInstance();

        String content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "loltournament.htm");

        if (subCommand.equals("start")) {
            clearVars(player);
            final StringBuilder tb = new StringBuilder();
            final StringBuilder ob = new StringBuilder();
            if (Config.ARENA_EVENT_ENABLED_LOL && LOLTournamentManager.getInstance().getPeriod() == 1) {
                String[] arenaMatches = Config.ARENA_EVENT_DUELS_LOL.split(",");

                for (String arenaMatch : arenaMatches) {
                    int match = Integer.parseInt(arenaMatch);
                    if (match >= 1 && match <= 9) {
                        boolean isRegistered = LOLTournamentManager.getInstance().checkIfRegistered(player, match);
                        tb.append("<tr><td><table width=300 height=\"30\"><tr><td width=200 align=\"center\"><button value=\""+(isRegistered ? "Unregister " : "Register ") + match +" x " + match + "\" action=\"bypass -h gab_loltournament;"+(isRegistered ? "unregister" : "register")+"-"+match+"\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"></td></tr></table></td></tr>");
                    }
                }

                for (String arenaMatch : arenaMatches) {
                    int match = Integer.parseInt(arenaMatch);
                    if (match >= 1 && match <= 9) {
                        ob.append("<tr><td><table width=300 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Observe " + match +" x " + match + "\" action=\"bypass -h gab_loltournament;observe-"+match+"\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm\"></td></tr></table></td></tr>");
                    }
                }
            } else {
                tb.append("<tr><td><table width=500 height=\"50\"><tr>\n" +
                        "    <td width=200 align=\"center\"><font>Not running at this moment.</font></td>\n" +
                        "</tr></table></td></tr>");
            }
            String[] soloDuo = getTag(LOLRankDAO.getInstance().getPlayerElo(player, false));
            String[] teams = getTag(LOLRankDAO.getInstance().getPlayerElo(player, true));
            content = content.replace("%iconSolo%", soloDuo[0]);
            content = content.replace("%nameSolo%", soloDuo[1]);
            content = content.replace("%iconTeam%", teams[0]);
            content = content.replace("%nameTeam%", teams[1]);
            content = content.replace("%toReplace%", tb.toString());
            content = content.replace("%toReplace2%", ob.toString());
            content = content.replace("%error%", "");
            content = content.replace("%backBypass%", "gab_special");
        } else if (subCommand.startsWith("goPage")) {
            clearVars(player);
            final StringBuilder tb = new StringBuilder();
            String quantity = subCommand.split("-")[1];
            tb.append("<tr><td><table width=500 height=\"50\"><tr>\n" +
                    "    <td width=200 align=\"center\"><font>Options for %quantity% x %quantity% </font></td>\n" +
                    "</tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Register\" action=\"bypass -h gab_loltournament;register-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Unregister\" action=\"bypass -h gab_loltournament;unregister-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Observe\" action=\"bypass -h gab_loltournament;observe-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            content = content.replace("%toReplace%", tb.toString());
            content = content.replace("%error%", "");
            content = content.replace("%quantity%", quantity);
            content = content.replace("%backBypass%", "gab_loltournamentPage;start");
        } else if (subCommand.startsWith("register")) {
            clearVars(player);
            String quantity = command.split("-")[1];
            if (!quantity.equals("1") && player.getParty() == null) {
                content = content.replace("%toReplace%", getErrorMessage("Come back when you are in a party!"));
            }else if (quantity.equals("1") && player.getParty() != null) {
                content = content.replace("%toReplace%", getErrorMessage("Come back when you are not in a party!"));
            } else if (!quantity.equals("1") && !player.getParty().isLeader(player)) {
                content = content.replace("%toReplace%", getErrorMessage("You aint the leader"));
            } else if (manager.checkIfAlreadyRegisteredInTour(player, Integer.parseInt(quantity))) {
                content = content.replace("%toReplace%", getErrorMessage("Cant participate if you are already registered!"));
            }else if (player.isInOlympiadMode()) {
                content = content.replace("%toReplace%", getErrorMessage("Cant participate if you are in olympiad!"));
            } else if (!player.isInsideZone(ZoneIdType.TOWN)) {
                content = content.replace("%toReplace%", getErrorMessage("You need to be inside a Town to be able to register!"));
            } else if (SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player) || player.isInArenaEvent()) {
                content = content.replace("%toReplace%", getErrorMessage("You are already participating in another event!"));
            } else if (!player.isNoble()) {
                content = content.replace("%toReplace%", getErrorMessage("Only players with nobless can join!"));
            } else if ((Arrays.stream(Config.ARENA_BANNED_CLASSES_ARENAS).anyMatch(i -> i == Integer.parseInt(quantity))) && checkSupport(player, quantity)) {
                content = content.replace("%toReplace%", getErrorMessage("Cannot enter if you have a banned class!"));
            } else if (!quantity.equals("1") && player.getParty().getMembers().size() != Integer.parseInt(quantity)) {
                content = content.replace("%toReplace%", getErrorMessage("Can only enter with "+quantity+" players inside party!"));
            } else if (manager.registerTeam(player, Integer.parseInt(quantity))) {
                content = content.replace("%toReplace%", getErrorMessage("You are now registered!"));
            }
            String[] soloDuo = getTag(LOLRankDAO.getInstance().getPlayerElo(player, false));
            String[] teams = getTag(LOLRankDAO.getInstance().getPlayerElo(player, true));
            content = content.replace("%iconSolo%", soloDuo[0]);
            content = content.replace("%nameSolo%", soloDuo[1]);
            content = content.replace("%iconTeam%", teams[0]);
            content = content.replace("%nameTeam%", teams[1]);
            content = content.replace("%toReplace2%", "<tr><td><br></td></tr>");
        } else if (subCommand.startsWith("unregister")) {
            clearVars(player);
            String quantity = command.split("-")[1];
            content = content.replace("%backBypass%", "gab_loltournamentPage;goPage-%quantity%");
            if (manager.unregisterTeam(player, Integer.parseInt(quantity))) {
                content = content.replace("%toReplace%", getErrorMessage("You unregistered!"));
            } else {
                content = content.replace("%toReplace%", getErrorMessage("You were not registered!"));
            }

            String[] soloDuo = getTag(LOLRankDAO.getInstance().getPlayerElo(player, false));
            String[] teams = getTag(LOLRankDAO.getInstance().getPlayerElo(player, true));
            content = content.replace("%iconSolo%", soloDuo[0]);
            content = content.replace("%nameSolo%", soloDuo[1]);
            content = content.replace("%iconTeam%", teams[0]);
            content = content.replace("%nameTeam%", teams[1]);
            content = content.replace("%toReplace2%", "<tr><td><br></td></tr>");
        } else if (subCommand.startsWith("obsNext")) {
            String quantity = command.split("-")[1];
            String pageS = player.getQuickVar("currPageTour", "1");
            int page = Integer.parseInt(pageS);
            page++;
            String toStore = String.valueOf(page);
            player.setQuickVar("currPageTour", toStore);
            parseCommand("gab_loltournament;observe-" + quantity, player);

        } else if (subCommand.startsWith("obsPrev")) {
            String quantity = command.split("-")[1];
            String pageS = player.getQuickVar("currPageTour", "1");
            int page = Integer.parseInt(pageS);
            page--;
            if (page < 1) {
                page = 1;
            }
            String toStore = String.valueOf(page);
            player.setQuickVar("currPageTour", toStore);
            parseCommand("gab_loltournament;observe-" + quantity, player);
        } else if (subCommand.startsWith("observe")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "loltournament.htm");

            String pageS = player.getQuickVar("currPageTour", "1");
            player.setQuickVar("currPageTour", pageS);
            int page = Integer.parseInt(pageS);
            final int itemsPerPage = 8;

            String quantity = command.split("-")[1];
            final StringBuilder tb = new StringBuilder();
            List<TFight> fights = manager.getFights(Integer.parseInt(quantity));
            content = content.replace("%backBypass%", "gab_loltournamentPage;goPage-%quantity%");

            if (fights == null || fights.isEmpty()) {
                content = content.replace("%toReplace%", getErrorMessage("No Fights active!"));
            } else {
                tb.append("<tr><td><table width=300 height=\"50\"><tr>" +
                        "    <td width=200 align=\"center\"><font>Observing %quantity% x %quantity% </font></td>" +
                        "</tr></table></td></tr>");

                int counter = 0;
                int laterIndex = 0;
                for (int i = (page - 1) * itemsPerPage; i < fights.size(); i++) {
                    TFight fight = fights.get(i);
                    laterIndex = i + 1;
                    if (fight != null) {
                        tb.append("<tr><td><table width=300 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"").append(fight.getFirstTeam().getLeaderName()).append(" x ").append(fight.getSecondTeam().getLeaderName()).append("\" action=\"bypass -h gab_loltournament;obsGo-").append(quantity).append("-").append(fight.getInstanceId()).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>").append("</tr></table></td></tr>");
                    }
                    counter++;
                    if (counter >= itemsPerPage) {
                        break;
                    }
                }
                boolean hasNextItem;
                try {
                    TFight fight = fights.get(laterIndex);
                    hasNextItem = true;
                } catch (IndexOutOfBoundsException e) {
                    hasNextItem = false;
                }

                tb.append(handleNavigation(hasNextItem, Integer.parseInt(quantity), player));
                String[] soloDuo = getTag(LOLRankDAO.getInstance().getPlayerElo(player, false));
                String[] teams = getTag(LOLRankDAO.getInstance().getPlayerElo(player, true));
                content = content.replace("%iconSolo%", soloDuo[0]);
                content = content.replace("%nameSolo%", soloDuo[1]);
                content = content.replace("%iconTeam%", teams[0]);
                content = content.replace("%nameTeam%", teams[1]);
                content = content.replace("%toReplace%", tb.toString());
                content = content.replace("%error%", "");

            }

            String[] soloDuo = getTag(LOLRankDAO.getInstance().getPlayerElo(player, false));
            String[] teams = getTag(LOLRankDAO.getInstance().getPlayerElo(player, true));
            content = content.replace("%iconSolo%", soloDuo[0]);
            content = content.replace("%nameSolo%", soloDuo[1]);
            content = content.replace("%iconTeam%", teams[0]);
            content = content.replace("%nameTeam%", teams[1]);
            content = content.replace("%toReplace2%", "<tr><td><br></td></tr>");
            content = content.replace("%quantity%", quantity);
            content = content.replace("%quantity%", quantity);

        } else if (subCommand.startsWith("obsGo")) {

            if (player.isInTournament() || player.isInArenaEvent() || SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player)) {
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You cannot observe if registered in event!"));
                return;
            } else if (player.isInOlympiadMode()) {
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You cannot observe if registered in oly!"));
                return;
            }
            int quantity = Integer.parseInt(command.split("-")[1]);
            int instanceId = Integer.parseInt(command.split("-")[2]);
            TFight fight = manager.getFight(quantity, instanceId);
            if (fight != null) {
                fight.addSpectator(player);
                clearVars(player);
            } else {
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Fight already ended!"));
                player.sendMessage("Fight already ended!");
            }

            return;
        }else if (subCommand.startsWith("rank")) {

            String content2 = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "index.htm");
            String template = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "template.htm");
            StringBuilder playersRank = new StringBuilder();
            StringBuilder clanRank = new StringBuilder();

            List<LOLPlayerRank> temp = LOLRankDAO.getInstance().getListTopRank(false);

            for (int i = 1; i <= 10; i++) {
                String tmm = template;
                try {
                    LOLPlayerRank status = temp.get(i - 1);
                    tmm = tmm.replace("%bg%", bgColor(i));
                    tmm = tmm.replace("%pos%", String.valueOf(i));
                    tmm = tmm.replace("%value1%", getTag(status.getElo(false))[1] );
                    tmm = tmm.replace("%value2%", String.valueOf(status.getOwnerName()));
                    tmm = tmm.replace("%score%", String.valueOf(status.getElo(false)));
                } catch (Exception e) {
                    tmm = tmm.replace("%bg%", bgColor(i));
                    tmm = tmm.replace("%pos%", String.valueOf(i));
                    tmm = tmm.replace("%value1%", "---");
                    tmm = tmm.replace("%value2%", "---");
                    tmm = tmm.replace("%score%", "---");
                }


                playersRank.append(tmm);
            }

            content2 = content2.replace("%tablePlayer%", playersRank.toString());
            List<LOLPlayerRank> tempp = LOLRankDAO.getInstance().getListTopRank(true);

            for (int i = 1; i <= 10; i++) {
                String tmm = template;

                try {
                    LOLPlayerRank status = tempp.get(i - 1);
                    tmm = tmm.replace("%bg%", bgColor(i));
                    tmm = tmm.replace("%pos%", String.valueOf(i));
                    tmm = tmm.replace("%value1%", getTag(status.getElo(true))[1] );
                    tmm = tmm.replace("%value2%", String.valueOf(status.getOwnerName()));
                    tmm = tmm.replace("%score%", String.valueOf(status.getElo(true)));
                } catch (Exception e) {
                    tmm = tmm.replace("%bg%", bgColor(i));
                    tmm = tmm.replace("%pos%", String.valueOf(i));
                    tmm = tmm.replace("%value1%", "---");
                    tmm = tmm.replace("%value2%", "---");
                    tmm = tmm.replace("%score%", "---");
                }

                clanRank.append(tmm);
            }
            content2 = content2.replace("%tableClan%", clanRank.toString());


            BBS.separateAndSend(content2, player);

            return;
        }

        content = content.replace("%name%", player.getName());
        content = content.replace("%CHANGE%", content);
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

    private String handleNavigation(boolean hasNextItem, int quantity, L2PcInstance player) {
        StringBuilder tb = new StringBuilder();
        String pageS = player.getQuickVar("currPageTour", "1");
        int page = Integer.parseInt(pageS);

        tb.append("<tr><td><table width=500 height=\"30\"><tr>");
        if (page != 1) {
            tb.append("<td width=200 align=\"center\"><button value=\"Previous\" action=\"bypass -h gab_loltournament;obsPrev-").append(quantity).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        }
        if (hasNextItem)
            tb.append("<td width=200 align=\"center\"><button value=\"Next\" action=\"bypass -h gab_loltournament;obsNext-").append(quantity).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        if (!tb.toString().endsWith("</td>"))
            tb.append("<td><br></td>");
        tb.append("</tr></table></td></tr>");
        return tb.toString();
    }

    private String getErrorMessage(String msg) {
        return "<tr><td><table width=300 height=\"25\"><tr>\n" +
                "    <td width=200 align=\"center\"><font color=FF0000>" + msg + "</font></td>\n" +
                "</tr></table></td></tr>";
    }

    private boolean checkSupport(L2PcInstance player, String quantity) {
        if(quantity.equals("1")) {
            if (Arrays.stream(Config.ARENA_BANNED_CLASSES).anyMatch(i -> i == player.getClassId().getId())) {
                return true;
            }
            return false;
        }

        L2Party party = player.getParty();
        for (L2PcInstance partyMember : party.getMembers()) {
            if (Arrays.stream(Config.ARENA_BANNED_CLASSES).anyMatch(i -> i == partyMember.getClassId().getId())) {
                return true;
            }
        }
        return false;
    }

    private void clearVars(L2PcInstance player) {
        player.deleteQuickVar("currPageTour");
    }


}
