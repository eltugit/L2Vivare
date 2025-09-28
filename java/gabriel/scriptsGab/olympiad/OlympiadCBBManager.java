package gabriel.scriptsGab.olympiad;

import gabriel.scriptsGab.utils.BBS;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.MultisellData;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.olympiad.Olympiad;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;
import l2r.gameserver.model.entity.olympiad.enums.CompetitionType;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExOlympiadMatchList;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class OlympiadCBBManager {
    private static String BBS_HOME_DIR = "data/html/scripts/services/gabriel/";
    private static final Logger _log = LoggerFactory.getLogger(OlympiadCBBManager.class);
    private static final int GATE_PASS = Config.ALT_OLY_COMP_RITEM;


    private OlympiadCBBManager() {

    }

    protected static OlympiadCBBManager instance;

    public static OlympiadCBBManager getInstance() {
        if (instance == null)
            instance = new OlympiadCBBManager();
        return instance;
    }


    public void parseCommand(String command, L2PcInstance player) {
        String subCommand = command.split(";")[1];

        String content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
        String sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
        String midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "olympiadTemplate.htm");

        if (subCommand.equals("start")) {
            String extraTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "extraTemplate/olyMain.htm");
            midTemplate = midTemplate.replace("%toReplace%", extraTemplate);
            midTemplate = midTemplate.replace("%error%", "");

            int period = Olympiad.getInstance().getPeriod();

            String tempString = period == 0 ? "are not running" : "is in validation";
            String tempString2 = period == 0 ? "It will start in" : "Validation will end in";

            String notRunning = "Olympiad " + tempString + ". " + tempString2 + ": %timeToStart%";
            String running = "Oly: %olympiad_period% period %olympiad_cycle% rounds currently %olympiad_opponent% people participating";
            if (Olympiad.getInstance().inCompPeriod()) {
                midTemplate = midTemplate.replace("%cycle%", running);
                midTemplate = midTemplate.replace("%olympiad_period%", String.valueOf(Olympiad.getInstance().getPeriod()));
                midTemplate = midTemplate.replace("%olympiad_cycle%", String.valueOf(Olympiad.getInstance().getCurrentCycle()));
                midTemplate = midTemplate.replace("%olympiad_opponent%", String.valueOf(OlympiadManager.getInstance().getCountOpponents()));
            } else {
                midTemplate = midTemplate.replace("%cycle%", notRunning);
                midTemplate = midTemplate.replace("%timeToStart%", getTimeToStart());
            }
            midTemplate = midTemplate.replace("%points%", player.isNoble() ? String.valueOf(Olympiad.getInstance().getNoblePoints(player.getObjectId())) : "0");
            midTemplate = midTemplate.replace("%wins%", player.isNoble() ? String.valueOf(Olympiad.getInstance().getCompetitionWon(player.getObjectId())) : "0");
            midTemplate = midTemplate.replace("%defeats%", player.isNoble() ? String.valueOf(Olympiad.getInstance().getCompetitionLost(player.getObjectId())) : "0");
            midTemplate = midTemplate.replace("%matches%", player.isNoble() ? String.valueOf(Olympiad.getInstance().getCompetitionDone(player.getObjectId())) : "0");

        } else if (subCommand.toLowerCase().contains("olympiaddesc")) {
            int val = Integer.parseInt(subCommand.substring(13, 14));
            String suffix = subCommand.substring(14);
            String page = val + ((suffix != null) ? suffix : "");
            StringBuilder sb = new StringBuilder();

            switch (page) {
                case "0":
                case "1a":
                case "1b":
                case "1c":
                case "1d":
                case "1e":
                case "2a":
                case "3a":
                    parseCommand("gab_olympiadPage;olympiadnoble 0", player);
                    return;
                case "2b":
                    sb.append(createTextField("Match your breathing to your fellow comrades."));
                    sb.append(createTextField("You will need strength and courage to achieve victory."));
                    sb.append(createButton("3 vs 3 Team Match", "OlympiadNoble 11", "OlympiadWnd_DF_Fight3None_Down", "OlympiadWnd_DF_Fight3None"));
                    break;
                case "2c":
                    sb.append(createTextField("If someone challenges you, turn their challenge into your victory!"));
                    sb.append(createButton("1 vs 1 Class Match", "OlympiadNoble 5", "OlympiadWnd_DF_Apply_Down", "OlympiadWnd_DF_Apply"));
                    break;
                case "2d":
                    sb.append(createTextField("In a match with someone with a different occupation, You will need strength and courage to achieve victory."));
                    sb.append(createButton("1 vs 1 Class-free Match", "OlympiadNoble 4", "OlympiadWnd_DF_Fight1None_Down", "OlympiadWnd_DF_Fight1None"));
                    break;
                case "4a":
                    sb.append(createTextField("You can calculate your Olympiad points or obtain items with tokens."));
                    sb.append(createButton("Calculate Points", "OlympiadNoble 6", "OlympiadWnd_DF_Reward_Down", "OlympiadWnd_DF_Reward"));
                    sb.append(createButton("Equipment Rewards", "OlympiadNoble 7", "OlympiadWnd_DF_BuyEquip_Down", "OlympiadWnd_DF_BuyEquip"));
//                    sb.append(createButton("Misc. Rewards", "OlympiadNoble 8", "OlympiadWnd_DF_BuyEtc_Down","OlympiadWnd_DF_BuyEtc"));
                    break;
            }
            midTemplate = midTemplate.replace("%toReplace%", sb.toString());

        } else if (subCommand.toLowerCase().contains("olympiadnoble")) {

            StringBuilder sb = new StringBuilder();

            if (player.isCursedWeaponEquipped()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("You cannot have an Cursed Weapon equipped!"));
            }
            if (player.getClassIndex() != 0) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Come back with your Main Class!"));
            }
            if (!player.isNoble() || (player.getClassId().level() < 3)) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("Come back when you have the third class active!"));
            }
//            if (!player.isInsideZone(ZoneIdType.ZONE_OLY_REGISTER)) {
//                player.sendMessage("You can register only in the Olympiad zone!");
//                return;
//            }

            int passes;
            int val = Integer.parseInt(subCommand.substring(14));

            switch (val) {
                case 0: // H5 match selection
                    if (!OlympiadManager.getInstance().isRegistered(player)) {
                        sb.append(createButton("1 vs 1 Class Match", "OlympiadDesc 2c", "OlympiadWnd_DF_Apply_Down", "OlympiadWnd_DF_Apply"));
                        sb.append(createButton("1 vs 1 Match", "OlympiadDesc 2d", "OlympiadWnd_DF_Fight1None_Down", "OlympiadWnd_DF_Fight1None"));
                        sb.append(createButton("3 vs 3 Team Match", "OlympiadDesc 2b", "OlympiadWnd_DF_Fight3None_Down", "OlympiadWnd_DF_Fight3None"));
                    } else {
                        sb.append(createTextField("You are already registered for a match."));
                        sb.append(createTextField("Do you wish to cancel?"));
                        sb.append(createButton("Cancel", "OlympiadNoble 1", "OlympiadWnd_DF_Back_Down", "OlympiadWnd_DF_Back"));
                    }
                    break;
                case 1: // unregister
                    if (OlympiadManager.getInstance().unRegisterNoble(player)) {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("You are now unregistered!"));
                    } else {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("The Olympiad Games are not in progress!"));
                    }

                    break;
                case 4: // register non classed
                    if (OlympiadManager.getInstance().registerNoble(player, CompetitionType.NON_CLASSED)) {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("You are now registered for non classed matches!"));
                    } else {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("The Olympiad Games are not in progress!"));
                    }
                    break;
                case 5: // register classed
                    if (OlympiadManager.getInstance().registerNoble(player, CompetitionType.CLASSED)) {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("You are now registered for classed matches!"));
                    } else {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("The Olympiad Games are not in progress!"));
                    }
                    break;
                case 6: // request tokens reward
                    passes = Olympiad.getInstance().getNoblessePasses(player, false);
                    if (passes > 0) {
                        sb.append(createTextField("You have met the minimum match requirement."));
                        sb.append(createTextField("Would you like to calculate your points?"));
                        sb.append(createButton("Calculate Points", "OlympiadNoble 10", "OlympiadWnd_DF_Back_Down", "OlympiadWnd_DF_Back"));
                    } else {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("You have not met the minimum requirements to calculate your points."));
                    }
                    break;
                case 7: // Equipment Rewards
                    MultisellData.getInstance().separateAndSend(102, player, null, false);
                    return;
                case 8: // Misc. Rewards
                    MultisellData.getInstance().separateAndSend(103, player, null, false);
                    return;
                case 10: // give tokens to player
                    passes = Olympiad.getInstance().getNoblessePasses(player, true);

                    if (passes > 0) {
                        L2ItemInstance item = player.getInventory().addItem("Olympiad", GATE_PASS, passes, player, null);

                        InventoryUpdate iu = new InventoryUpdate();
                        iu.addModifiedItem(item);
                        player.sendInventoryUpdate(iu);

                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
                        sm.addLong(passes);
                        sm.addItemName(item);
                        player.sendPacket(sm);
                    }
                    break;
                case 11: // register team
                    if (OlympiadManager.getInstance().registerNoble(player, CompetitionType.TEAMS)) {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("You are now registered for team matches!"));
                    } else {
                        midTemplate = midTemplate.replace("%error%", getErrorMessage("The Olympiad Games are not in progress!"));
                    }
                    break;
                default:
                    _log.warn("Olympiad System: Couldnt send packet for request " + val);
                    break;
            }
            midTemplate = midTemplate.replace("%toReplace%", sb.toString());
        } else if (subCommand.equals("watchmatch")) {
            if (!Olympiad.getInstance().inCompPeriod()) {
                midTemplate = midTemplate.replace("%error%", getErrorMessage("The Olympiad Games are not in progress!"));
                player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAME_IS_NOT_CURRENTLY_IN_PROGRESS);
            } else {
                player.sendPacket(new ExOlympiadMatchList());
            }
        }
        content = content.replace("%backBypass%", "gab_olympiadPage;start");
        content = content.replace("%name%", player.getName());
        content = content.replace("%template%", sidePanel);
        content = content.replace("%CHANGE%", midTemplate);
        BBS.separateAndSend(content, player);
    }

    private String getErrorMessage(String msg) {
        return "<tr><td><table width=500 height=\"25\"><tr>\n" +
                "    <td width=200 align=\"center\"><font color=FF0000>" + msg + "</font></td>\n" +
                "</tr></table></td></tr>";
    }

    private String createButton(String value, String bypass, String back, String fore) {
        return "<tr><td><table width=500 height=\"50\"><tr><td width=250 align=\"center\"><button action=\"bypass -h gab_olympiadPage;" + bypass + "\" value=\"" + value + "\" width=200 height=31 back=\"L2UI_CT1." + back + "\" fore=\"L2UI_CT1." + fore + "\" /></td></tr></table></td></tr>";
    }

    private String createTextField(String text) {
        return "<tr><td><table width=500 height=\"50\"><tr>\n" +
                "    <td width=500 align=\"center\"><font color=FFFFFF>" + text + "</font></td>\n" +
                "</tr></table></td></tr>";
    }

    private String getTimeToStart() {
        int period = Olympiad.getInstance().getPeriod();

        long milliToStart = period == 0 ? Olympiad.getInstance().getMillisToCompBegin() : Olympiad.getInstance().getMillisToValidationEnd();

        double numSecs = (milliToStart / 1000) % 60;
        double countDown = ((milliToStart / 1000.) - numSecs) / 60;
        int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        int numHours = (int) Math.floor(countDown % 24);
        int numDays = (int) Math.floor((countDown - numHours) / 24);
        if (numDays == 0) {
            return numHours + " hours and " + numMins + " mins.";
        } else {
            return numDays + " days, " + numHours + " hours and " + numMins + " mins.";
        }
    }


}
