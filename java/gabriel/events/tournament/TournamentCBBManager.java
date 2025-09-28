package gabriel.events.tournament;

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
public class TournamentCBBManager {
    private static String BBS_HOME_DIR = "data/html/scripts/services/gabriel/";


    private TournamentCBBManager() {

    }

    protected static TournamentCBBManager instance;

    public static TournamentCBBManager getInstance() {
        if (instance == null)
            instance = new TournamentCBBManager();
        return instance;
    }


    public void parseCommand(String command, L2PcInstance player) {
        String subCommand = command.split(";")[1];
        final TTournamentManager manager = TTournamentManager.getInstance();

        String content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
        String sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
        String midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "/tournamentTemplate.htm");

        if (subCommand.equals("start")) {
            clearVars(player);
            final StringBuilder tb = new StringBuilder();
            if (Config.ARENA_EVENT_ENABLED && TTournamentManager.getInstance().getPeriod() == 1) {
                String[] arenaMatches = Config.ARENA_EVENT_DUELS.split(",");

                for (String arenaMatch : arenaMatches) {
                    int match = Integer.parseInt(arenaMatch);
                    if (match >= 1 && match <= 9)
                        tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Arena " + match + " x " + match + "\" action=\"bypass -h gab_tournament;goPage-" + match + "\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
                }
            } else {
                tb.append("<tr><td><table width=500 height=\"50\"><tr>\n" +
                        "    <td width=200 align=\"center\"><font>Not running at this moment.</font></td>\n" +
                        "</tr></table></td></tr>");
            }
            midTemplate = midTemplate.replace("%toReplace%", tb.toString());
            midTemplate = midTemplate.replace("%error%", "");
            content = content.replace("%backBypass%", "gab_special");
        } else if (subCommand.startsWith("goPage")) {
            clearVars(player);
            final StringBuilder tb = new StringBuilder();
            String quantity = subCommand.split("-")[1];
            tb.append("<tr><td><table width=500 height=\"50\"><tr>\n" +
                    "    <td width=200 align=\"center\"><font>Options for %quantity% x %quantity% </font></td>\n" +
                    "</tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Register\" action=\"bypass -h gab_tournament;register-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Unregister\" action=\"bypass -h gab_tournament;unregister-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"Observe\" action=\"bypass -h gab_tournament;observe-%quantity%\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr></table></td></tr>");
            midTemplate = midTemplate.replace("%toReplace%", tb.toString());
            midTemplate = midTemplate.replace("%error%", "");
            midTemplate = midTemplate.replace("%quantity%", quantity);
            content = content.replace("%backBypass%", "gab_tournamentPage;start");
        } else if (subCommand.startsWith("register")) {
            clearVars(player);
            String quantity = command.split("-")[1];
            midTemplate = midTemplate.replace("%backBypass%", "gab_tournamentPage;goPage-%quantity%");
            if (!quantity.equals("1") && player.getParty() == null) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Come back when you are in a party!"));
            } else if (!quantity.equals("1") && !player.getParty().isLeader(player)) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You aint the leader"));
            } else if (player.isInOlympiadMode()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Cant participate if you are in olympiad!"));
            } else if (!player.isInsideZone(ZoneIdType.TOWN)) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You need to be inside a Town to be able to register!"));
            } else if (SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player) || player.isInArenaEvent()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You are already participating in another event!"));
            } else if (!player.isNoble()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Only players with nobless can join!"));
            } else if ((Arrays.stream(Config.ARENA_BANNED_CLASSES_ARENAS).anyMatch(i -> i == Integer.parseInt(quantity))) && checkSupport(player, quantity)) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Cannot enter if you have a banned class!"));
            } else if (!quantity.equals("1") && player.getParty().getMembers().size() != Integer.parseInt(quantity)) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Can only enter with %quantity% players inside party!"));
            } else if (manager.registerTeam(player, Integer.parseInt(quantity))) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You are now registered!"));
            }
            midTemplate = midTemplate.replace("%quantity%", quantity);
            content = content.replace("%backBypass%", "gab_tournamentPage;goPage-%quantity%");
            content = content.replace("%quantity%", quantity);

        } else if (subCommand.startsWith("unregister")) {
            clearVars(player);
            String quantity = command.split("-")[1];
            content = content.replace("%backBypass%", "gab_tournamentPage;goPage-%quantity%");
            content = content.replace("%quantity%", quantity);
            midTemplate = midTemplate.replace("%quantity%", quantity);
            if (manager.unregisterTeam(player, Integer.parseInt(quantity))) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You unregistered!"));
            } else {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You were not registered!"));
            }
        } else if (subCommand.startsWith("obsNext")) {
            String quantity = command.split("-")[1];
            String pageS = player.getQuickVar("currPageTour", "1");
            int page = Integer.parseInt(pageS);
            page++;
            String toStore = String.valueOf(page);
            player.setQuickVar("currPageTour", toStore);
            parseCommand("gab_tournament;observe-" + quantity, player);

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
            parseCommand("gab_tournament;observe-" + quantity, player);
        } else if (subCommand.startsWith("observe")) {
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "/tournamentTemplate.htm");

            String pageS = player.getQuickVar("currPageTour", "1");
            player.setQuickVar("currPageTour", pageS);
            int page = Integer.parseInt(pageS);
            final int itemsPerPage = 8;

            String quantity = command.split("-")[1];
            final StringBuilder tb = new StringBuilder();
            List<TFight> fights = manager.getFights(Integer.parseInt(quantity));
            content = content.replace("%backBypass%", "gab_tournamentPage;goPage-%quantity%");

            if (fights == null || fights.isEmpty()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("No Fights active!"));
            } else {
                tb.append("<tr><td><table width=500 height=\"50\"><tr>" +
                        "    <td width=200 align=\"center\"><font>Observing %quantity% x %quantity% </font></td>" +
                        "</tr></table></td></tr>");

                int counter = 0;
                int laterIndex = 0;
                for (int i = (page - 1) * itemsPerPage; i < fights.size(); i++) {
                    TFight fight = fights.get(i);
                    laterIndex = i + 1;
                    if (fight != null) {
                        tb.append("<tr><td><table width=500 height=\"30\"><tr><td width=200 align=\"center\"><button value=\"").append(fight.getFirstTeam().getLeaderName()).append(" x ").append(fight.getSecondTeam().getLeaderName()).append("\" action=\"bypass -h gab_tournament;obsGo-").append(quantity).append("-").append(fight.getInstanceId()).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>").append("</tr></table></td></tr>");
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


                midTemplate = midTemplate.replace("%toReplace%", tb.toString());
                midTemplate = midTemplate.replace("%error%", "");

            }
            midTemplate = midTemplate.replace("%quantity%", quantity);
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
        }

        content = content.replace("%name%", player.getName());
        content = content.replace("%template%", sidePanel);
        content = content.replace("%CHANGE%", midTemplate);
        BBS.separateAndSend(content, player);
    }

    private String handleNavigation(boolean hasNextItem, int quantity, L2PcInstance player) {
        StringBuilder tb = new StringBuilder();
        String pageS = player.getQuickVar("currPageTour", "1");
        int page = Integer.parseInt(pageS);

        tb.append("<tr><td><table width=500 height=\"30\"><tr>");
        if (page != 1) {
            tb.append("<td width=200 align=\"center\"><button value=\"Previous\" action=\"bypass -h gab_tournament;obsPrev-").append(quantity).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        }
        if (hasNextItem)
            tb.append("<td width=200 align=\"center\"><button value=\"Next\" action=\"bypass -h gab_tournament;obsNext-").append(quantity).append("\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        if (!tb.toString().endsWith("</td>"))
            tb.append("<td><br></td>");
        tb.append("</tr></table></td></tr>");
        return tb.toString();
    }

    private String getErrorMessage(String msg) {
        return "<tr><td><table width=500 height=\"25\"><tr>\n" +
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
