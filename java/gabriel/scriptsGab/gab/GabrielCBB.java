package gabriel.scriptsGab.gab;


import gabriel.PartyZone.PartyZoneManager;
import gabriel.Utils.GabUtils;
import gabriel.Utils.Visuals;
import gabriel.community.communityDonate.changeBaseClass.BaseClassChange;
import gabriel.community.specialCraft.SpecialCraftManager;
import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gabriel.events.challengerZone.ChallengerZoneManager;
import gabriel.events.extremeZone.ExtremeZoneManager;
import gabriel.events.siegeRank.SiegeRankManager;
import gabriel.events.tournament.lol.LOLTournamentCBBManager;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PVPInstance.PVPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import gabriel.scriptsGab.classBase.ClassBBSManager;
import gabriel.scriptsGab.classBase.SubClassBBSManager;
import gabriel.scriptsGab.itemBroker.ItemBroker;
import gabriel.scriptsGab.olympiad.OlympiadCBBManager;
import gabriel.scriptsGab.utils.BBS;
import gabriel.events.tournament.TournamentCBBManager;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import gabriel.events.weeklyRank.WeeklyManager;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class GabrielCBB {
    protected static GabrielCBB instance;
    private static String BBS_HOME_DIR = "data/html/scripts/services/gabriel/";
    private static String BASE_COMMAND = "gab_";
    private static String ITEM_BROKER = "itembroker";
    private static String SPECIAL = "special";


    public static GabrielCBB getInstance() {
        if (instance == null)
            instance = new GabrielCBB();
        return instance;
    }


    public void parseCommand(String _command, L2PcInstance player) {

        String command = _command.substring(BASE_COMMAND.length());
        String sidePanel = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplate.htm");
        String midTemplate = "";

        String content = "";
        content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "index.htm");

        if (_command.equals(BASE_COMMAND)) {
            content = content.replace("%name%", player.getName());
            content = handleCharSettings(content, player);
        } else if (command.startsWith(ITEM_BROKER)) {
            ItemBroker.getInstance().parseCommand(command, player);
            return;
        } else if (command.startsWith("_bbsclass")) {
            ClassBBSManager.getInstance().cbByPass(command, player);
            return;
        } else if (command.startsWith("siege")) {
            SiegeRankManager.getInstance().parseCommand(command, player);
            return;
        } else if (command.startsWith("_bbssubclass")) {
            SubClassBBSManager.getInstance().cbByPass(command, player);
            return;
        } else if (command.startsWith("loltournament")) {
            LOLTournamentCBBManager.getInstance().parseCommand(command, player);
            return;
        } else if (command.startsWith("specialCraft")) {
            SpecialCraftManager.getInstance().parseCommand(command, player);
            return;
        }else if (command.startsWith("handle")) {
            String[] commands = command.split(";");
            String subCommand = commands[1];
            handleBypass(subCommand, player);
            parseCommand(BASE_COMMAND, player);
            return;
        } else if (command.startsWith("weeklyRank")) {
            WeeklyManager.getInstance().parseCommand(command, player, false);
            return;
        }else if (command.startsWith("charMainClass")) {
            BaseClassChange.getInstance().onBypassFeedback(player, command);
            return;
        } else if (command.startsWith("tournament")) {
            TournamentCBBManager.getInstance().parseCommand(command, player);
            return;
        } else if (command.startsWith("olympiadPage")) {
            OlympiadCBBManager.getInstance().parseCommand(command, player);
            return;
        } else if (command.startsWith("pvpInstancePage")) {
            try {
                content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
                sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
                midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "pvpInstanceTemplate.htm");
                midTemplate = handleReplacesForPvPInstance(midTemplate);
                content = content.replace("%backBypass%", "gab_special");
            } catch (Exception e) {

            }

        } else if (command.startsWith("partyZonePage")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "partyZoneTemplate.htm");
            try {
                midTemplate = handleReplacesForPartyZone(midTemplate);
            } catch (Exception e) {
                //
            }
            content = content.replace("%backBypass%", "gab_special");
        } else if (command.startsWith("epicRaidPage")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "epicRaidTemplate.htm");
            midTemplate = handleReplacesForEpicRaid(midTemplate, player);
            if (midTemplate.equals("notactive")) {
                midTemplate = "<tr><td align=center><img src=\"L2UI.SquareBlank\" width=480 height=3></td></tr>\n" +
                        "<tr><td align=center><img src=\"L2UI.SquareGray\" width=480 height=2></td></tr>\n" +
                        "<tr><td align=center><img src=\"L2UI.SquareBlank\" width=480 height=3></td></tr>\n" +
                        "<tr><td><table width=500><tr>\n" +
                        "    <td width=170 align=\"center\"><font color=9f9f9f>====</font></td>\n" +
                        "    <td width=170 align=\"center\"><font color=9f9f9f>Epic Raid is not active today!</font></td>\n" +
                        "    <td width=170 align=\"center\"><font color=9f9f9f>====</font></td></tr>\n" +
                        "</table></td></tr>\n" +
                        "<tr><td align=center><img src=\"L2UI.SquareBlank\" width=480 height=3></td></tr>\n" +
                        "<tr><td align=center><img src=\"L2UI.SquareGray\" width=480 height=2></td></tr>\n" +
                        "<tr><td align=center><img src=\"L2UI.SquareBlank\" width=480 height=3></td></tr>";
            }
            content = content.replace("%backBypass%", "gab_special");
        } else if (command.startsWith("enterPartyZone")) {
            if (player.getInstanceId() == GabConfig.PARTY_AREA_INSTANCE_ID) {
                player.sendMessage("You are already inside this zone!");
                return;
            }
            String[] tps = command.split(" ");
            String type = "ez";
            try{
                type = tps[1].toLowerCase();
            }
            catch (Exception e){
                //
            }
            PartyZoneManager.getInstance().teleportPlayerIntoInstance(player, type);
            return;
        } else if (command.startsWith("enterEpicZone")) {
            Location loc = EpicRaidManager.getInstance().getNextRaid().getTeleToZone();
            player.teleToLocation(loc, 0, Config.MAX_OFFSET_ON_TELEPORT);
            return;
        } else if (command.startsWith("enterInstanceZone")) {
            if (player.getInstanceId() == ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID) {
                player.sendMessage("You are already inside this zone!");
                parseCommand("gab_pvpInstancePage", player);
                return;
            }
            PVPInstance.getInstance().enterInstance(player, 0);
            return;
        } else if (command.startsWith("extremeZonePage")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "extremeTemplate.htm");
            midTemplate = handleReplacesForExtremeZone(midTemplate, player);
            content = content.replace("%backBypass%", "gab_special");
        } else if (command.startsWith("enterExtremeZone")) {
            if (!ExtremeZoneManager.getInstance().isStarted()) {
                player.sendMessage("There is no active Extreme Zone at the moment.");
                return;
            }
            ExtremeZoneManager.getInstance().teleportPlayerIntoInstance(player);
            return;
        } else if (command.startsWith("challengerZonePage")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "challengerTemplate.htm");
            midTemplate = handleReplacesForChallengerZone(midTemplate, player);
            content = content.replace("%backBypass%", "gab_special");
        } else if (command.startsWith("enterChallengerZone")) {
            if (!ChallengerZoneManager.getInstance().isStarted()) {
                player.sendMessage("There is no active Challenger Zone at the moment.");
                return;
            }
            ChallengerZoneManager.getInstance().teleportPlayerIntoInstance(player);
            return;
        } else if (command.startsWith(SPECIAL)) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            sidePanel = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), BBS_HOME_DIR + "template/sideTemplateSpecial.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "mainSpecialTemplate.htm");

        } else if (command.startsWith("dressme")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "specials.htm");
            midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "mainDressmeTemplate.htm");

        } else if (command.startsWith("services")) {
            content = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "indexTemplate.htm");
            String subCommand = command.split(";")[1];
            switch (subCommand) {
                case "warehouse":
                    midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "warehouse.htm");
                    break;
                case "symbolMaker":
                    midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "symbolmaker.htm");
                    break;
                case "blacksmith":
                    midTemplate = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "blacksmith.htm");
                    break;
            }
            content = content.replace("%backBypass%", "gab_");

        }
        content = content.replace("%backBypass%", "gab_special");
        content = content.replace("%template%", sidePanel);
        content = content.replace("%CHANGE%", midTemplate);
        content = content.replace("%name%", player.getName());
        BBS.separateAndSend(content, player);

    }

    private String handleReplacesForExtremeZone(String html, L2PcInstance player) {
        ExtremeZoneManager manager = ExtremeZoneManager.getInstance();
//        boolean active = ExtremeZoneManager.getInstance().isStarted();
        Instance inst = InstanceManager.getInstance().getInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
        boolean active = inst != null && PvPZoneManager.isAnnonym();
        String buttonTele = "<tr><td><table width=500 height=\"50\"><tr>\n" +
                "    <td width=200 align=\"center\">\n" +
                "        <button value=\"Teleport\" action=\"bypass -h gab_enterExtremeZone\" width=150 height=25 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
                "    </td>\n" +
                "</tr></table></td></tr>";


        try {
            if (active) {
                html = html.replace("%to%", "close");
                html = html.replace("%epicC%", "Current Epic Boss");
                html = html.replace("%bossimgName%", manager.getNextZone().getImageName());
                html = html.replace("%epicN%", manager.getNextZone().getName());
                html = html.replace("%time%", manager.getTimeToEnd());
                html = html.replace("%goButton%", buttonTele);
            } else {
                html = html.replace("%to%", "start");
                html = html.replace("%epicC%", "Next Boss");
                html = html.replace("%bossimgName%", manager.getNextZone().getImageName());
                html = html.replace("%epicN%", manager.isLaunched() ? manager.getNextZone().getName() : "Come again next time");
                html = html.replace("%time%", manager.isLaunched() ? manager.getTimeToStart() : "");
                html = html.replace("%goButton%", "");
            }
        } catch (Exception e) {
            //Do nothing
        }
        html = GabUtils.sendImagesToPlayer(html, player);
        return html;
    }

    private String handleReplacesForChallengerZone(String html, L2PcInstance player) {
        ChallengerZoneManager manager = ChallengerZoneManager.getInstance();
        Instance inst = InstanceManager.getInstance().getInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
        boolean active = inst != null && PvPZoneManager.isTvTvTvT();
        String buttonTele = "<tr><td><table width=500 height=\"50\"><tr>\n" +
                "    <td width=200 align=\"center\">\n" +
                "        <button value=\"Teleport\" action=\"bypass -h gab_enterChallengerZone\" width=150 height=25 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
                "    </td>\n" +
                "</tr></table></td></tr>";


        try {
            if (active) {
                html = html.replace("%to%", "close");
                html = html.replace("%epicC%", "Current Epic Boss");
                html = html.replace("%bossimgName%", manager.getNextZone().getImageName());
                html = html.replace("%epicN%", manager.getNextZone().getName());
                html = html.replace("%time%", manager.getTimeToEnd());
                html = html.replace("%goButton%", buttonTele);
            } else {
                html = html.replace("%to%", "start");
                html = html.replace("%epicC%", "Next Boss");
                html = html.replace("%bossimgName%", manager.getNextZone().getImageName());
                html = html.replace("%epicN%", manager.isLaunched() ? manager.getNextZone().getName() : "Come again next time");
                html = html.replace("%time%", manager.isLaunched() ? manager.getTimeToStart() : "");
                html = html.replace("%goButton%", "");
            }
        } catch (Exception e) {
            //Do nothing
        }
        html = GabUtils.sendImagesToPlayer(html, player);
        return html;
    }

    private String handleCharSettings(String content, L2PcInstance player) {
        if (player.getVarB("noTrade")) {
            content = content.replace("%tradeRefusal%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%tradeRefusal%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("noExp")) {
            content = content.replace("%blockExp%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%blockExp%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("noBuff")) {
            content = content.replace("%blockBuff%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%blockBuff%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("showEnchantAnime")) {
            content = content.replace("%blockEnch%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%blockEnch%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideStores")) {
            content = content.replace("%hideStores%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideStores%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("onEnterLoadSS")) {
            content = content.replace("%loadShots%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%loadShots%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideSSAnime")) {
            content = content.replace("%blockShot%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%blockShot%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideSkillAnime")) {
            content = content.replace("%blockAnims%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%blockAnims%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("showVisualChange")) {
            content = content.replace("%dressMe%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%dressMe%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideAgathion")) {
            content = content.replace("%hideAgathion%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideAgathion%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideHero")) {
            content = content.replace("%hideHero%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideHero%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideTitle")) {
            content = content.replace("%hideTitle%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideTitle%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideName")) {
            content = content.replace("%hideName%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideName%", "<font color=A26D64>Disabled</font>");
        }
        if (player.getVarB("hideEnchant")) {
            content = content.replace("%hideEnchant%", "<font color=849D68>Enabled</font>");
        } else {
            content = content.replace("%hideEnchant%", "<font color=A26D64>Disabled</font>");
        }
        return content;
    }

    private String handleReplacesForPvPInstance(String html) {
        try {
            Instance inst = InstanceManager.getInstance().getInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
            html = html.replace("%currMap%", inst == null ? "Not active!" : PVPInstance.getInstance().getMapName());
            html = html.replace("%currMode%", inst == null ? "Not active!" : PVPInstance.getInstance().getMapMode());
            html = html.replace("%timeRemaining%", inst == null ? "Not active!" : PvPZoneManager.getInstance().getRemainingTime());
            html = html.replace("%playersInside%", inst == null ? "Not active!" : String.valueOf(PvPZoneManager.playerinInstance.size()));
            try {
                if (inst != null && PvPZoneManager.isTvT()) {
                    html = html.replace("%teamblue%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=0000FF>Team 1:</font></td><td width=200 align=\"center\"><font color=898989>%pl1%</font></td></tr></table></td></tr>");
                    html = html.replace("%teamred%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=FF0000>Team 2:</font></td><td width=200 align=\"center\"><font color=898989>%pl2%</font></td></tr></table></td></tr>");
                    html = html.replace("%teamgreen%", "");
                    html = html.replace("%teamyellow%", "");
                    html = html.replace("%pl1%", String.valueOf(PVPInstance.getInstance().getBlueTeams()));
                    html = html.replace("%pl2%", String.valueOf(PVPInstance.getInstance().getRedTeams()));
                } else if (inst != null && PvPZoneManager.isTvTvTvT()) {
                    html = html.replace("%teamblue%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=0000FF>Team 1:</font></td><td width=200 align=\"center\"><font color=898989>%pl1%</font></td></tr></table></td></tr>");
                    html = html.replace("%teamred%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=FF0000>Team 2:</font></td><td width=200 align=\"center\"><font color=898989>%pl2%</font></td></tr></table></td></tr>");
                    html = html.replace("%teamgreen%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=00FF00>Team 3:</font></td><td width=200 align=\"center\"><font color=898989>%pl3%</font></td></tr></table></td></tr>");
                    html = html.replace("%teamyellow%", "<tr><td><table width=500 height=\"25\"><tr><td width=200 align=\"center\"><font color=FFFF00>Team 4:</font></td><td width=200 align=\"center\"><font color=898989>%pl4%</font></td></tr></table></td></tr>");
                    html = html.replace("%pl1%", String.valueOf(PVPInstance.getInstance().getBlueTeams()));
                    html = html.replace("%pl2%", String.valueOf(PVPInstance.getInstance().getRedTeams()));
                    html = html.replace("%pl3%", String.valueOf(PVPInstance.getInstance().getGreenTeams()));
                    html = html.replace("%pl4%", String.valueOf(PVPInstance.getInstance().getYellowTeams()));
                } else {
                    html = html.replace("%teamblue%", "");
                    html = html.replace("%teamred%", "");
                    html = html.replace("%teamgreen%", "");
                    html = html.replace("%teamyellow%", "");
                }
            } catch (Exception e) {
                html = html.replace("%teamblue%", "");
                html = html.replace("%teamred%", "");
                html = html.replace("%teamgreen%", "");
                html = html.replace("%teamyellow%", "");
            }
        } catch (Exception e) {

        }

        return html;
    }

    private String handleReplacesForPartyZone(String html) {
        PartyZoneManager manager = PartyZoneManager.getInstance();
        boolean active = PartyZoneManager.getInstance().isRunning();

        String buttonTele = "<tr><td><table width=500 height=\"50\"><tr>\n" +
                "    <td width=200 align=\"center\">\n" +
                "        <button value=\"Enter\" action=\"bypass -h gab_enterPartyZone\" width=200 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
                "    </td>\n" +
                "</tr></table></td></tr>";
//                "    <td width=150 align=\"center\">\n" +
//                "        <button value=\"LoL ( Mid Lane )\" action=\"bypass -h gab_enterPartyZone med\" width=150 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
//                "    </td>\n" +
//                "    <td width=150 align=\"center\">\n" +
//                "        <button value=\"LoL ( Bot Lane )\" action=\"bypass -h gab_enterPartyZone hard\" width=150 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
//                "    </td>\n" +
//                "</tr></table></td></tr>";

//        buttonTele = buttonTele + "<tr><td><table width=500 height=\"50\"><tr>\n" +
//                "    <td width=300 align=\"center\">\n" +
//                "        <button value=\"Fafurion Nest ( Vip )\" action=\"bypass -h gab_enterPartyZone vip\" width=200 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
//                "    </td>\n" +
//                "</tr></table></td></tr>";

//        "<tr><td><table width=500 height=\"50\"><tr>\n" +
//                "    <td width=200 align=\"center\">\n" +
//                "        <button value=\"Enter Medium\" action=\"bypass -h gab_enterPartyZone med\" width=200 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
//                "    </td>\n" +
//                "</tr></table></td></tr>"+
//        "<tr><td><table width=500 height=\"50\"><tr>\n" +
//                "    <td width=200 align=\"center\">\n" +
//                "        <button value=\"Enter Hard\" action=\"bypass -h gab_enterPartyZone hard\" width=200 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
//                "    </td>\n" +
//                "</tr></table></td></tr>";

        if (active) {
            html = html.replace("%to%", "close");
            html = html.replace("%time%", manager.getTimeToEnd());
            html = html.replace("%%goButton%%", buttonTele);
        } else {
            html = html.replace("%to%", "start");
            html = html.replace("%time%", manager.getTimeToStart());
            html = html.replace("%%goButton%%", "");
        }
        return html;
    }

    private String handleReplacesForEpicRaid(String html, L2PcInstance player) {
        EpicRaidManager manager = EpicRaidManager.getInstance();
        boolean active = EpicRaidManager.getInstance().isStarted();
        String backk = "";
        String buttonTele = "<tr><td><table width=500 height=\"50\"><tr>\n" +
                "    <td width=200 align=\"center\">\n" +
                "        <button value=\"Teleport\" action=\"bypass -h gab_enterEpicZone\" width=200 height=40 back=L2UI_CT1.Button_DF_Down fore=L2UI_CT1.Button_DF />\n" +
                "    </td>\n" +
                "</tr></table></td></tr>";
        try {
            if (active) {
                html = html.replace("%to%", "close");
                html = html.replace("%epicC%", "Current Epic Boss");
                html = html.replace("%bossimgName%", manager.getNextRaid().getImageName());
                html = html.replace("%epicN%", manager.getNextRaid().getName());
                html = html.replace("%time%", manager.getTimeToEnd());
                html = html.replace("%goButton%", buttonTele);
            } else {
                html = html.replace("%to%", "start");
                html = html.replace("%epicC%", "Next Boss");
                html = html.replace("%bossimgName%", manager.getNextRaid().getImageName());
                html = html.replace("%epicN%", manager.isLaunched() ? manager.getNextRaid().getName() : "Come again next time");
                html = html.replace("%time%", manager.isLaunched() ? manager.getTimeToStart() : "");
                html = html.replace("%goButton%", "");
            }
        } catch (Exception e) {
            backk = "notactive";
        }
        if (!backk.equals("notactive"))
            html = GabUtils.sendImagesToPlayer(html, player);
        else {
            html = backk;
        }
        return html;
    }

    private void handleBypass(String command, L2PcInstance activeChar) {
        String[] temps = command.split("_");
        String subCommand = "-1";
        if (temps.length > 1) {
            command = temps[0];
            subCommand = temps[1];
        }

        switch (command) {
            case "animeLimit":
                final int range = Integer.parseInt(subCommand);
                activeChar.setVar("animeLimit", String.valueOf(range));
                if (range == -1) {
                    activeChar.sendMessage("Skill animation range: Unlimited");
                } else {
                    activeChar.sendMessage("Skill animation range: " + range);
                }
                break;
            case "tradeprot":
                if (activeChar.getVarB("noTrade")) {
                    activeChar.setVar("noTrade", "false");
                    activeChar.sendMessage("Trade refusal mode disabled.");
                } else {
                    activeChar.setVar("noTrade", "true");
                    activeChar.sendMessage("Trade refusal mode enabled.");
                }
                break;
            case "changeexp":
                if (CustomServerConfigs.ALLOW_EXP_GAIN_COMMAND) {
                    if (!activeChar.getVarB("noExp")) {
                        activeChar.setVar("noExp", "true");
                        activeChar.sendMessage("Experience gain enabled.");
                    } else {
                        activeChar.setVar("noExp", "false");
                        activeChar.sendMessage("Experience gain disabled.");
                    }
                } else {
                    activeChar.sendMessage("Experience command disabled by a gm.");
                }
                break;
            case "nobuff":
                if (activeChar.getVarB("noBuff")) {
                    activeChar.setVar("noBuff", "false");
                    activeChar.sendMessage("Bad-buff protection disabled.");
                } else {
                    activeChar.setVar("noBuff", "true");
                    activeChar.sendMessage("Bad-buff protection enabled.");
                }
                break;
            case "enchantanime":
                if (activeChar.getVarB("showEnchantAnime")) {
                    activeChar.setVar("showEnchantAnime", "false");
                    activeChar.sendMessage("Enchant animation disabled.");
                } else {
                    activeChar.setVar("showEnchantAnime", "true");
                    activeChar.sendMessage("Enchant animation enabled.");
                }
                break;
            case "hidestores":
                if (activeChar.getVarB("hideStores")) {
                    activeChar.setVar("hideStores", "false");
                    Visuals.refreshStoreStatus(activeChar, false);
                    activeChar.sendMessage("All stores are visible.");
                } else {
                    activeChar.setVar("hideStores", "true");
                    Visuals.refreshStoreStatus(activeChar, true);
                    activeChar.sendMessage("All stores are invisible.");
                }
                break;
            case "shotsonenter":
                if (activeChar.getVarB("onEnterLoadSS")) {
                    activeChar.setVar("onEnterLoadSS", "false");
                    activeChar.sendMessage("On enter auto load shots disabled.");
                } else {
                    activeChar.setVar("onEnterLoadSS", "true");
                    activeChar.sendMessage("On enter auto load shots enabled.");
                }
                break;
            case "blockshotsanime":
                if (!activeChar.getVarB("hideSSAnime")) {
                    activeChar.setVar("hideSSAnime", "true");
                    activeChar.sendMessage("Broadcast shots animation enabled.");
                } else {
                    activeChar.setVar("hideSSAnime", "false");
                    activeChar.sendMessage("Broadcast shots animation disabled.");
                }
                break;
            case "blockskillanime":
                if (!activeChar.getVarB("hideSkillAnime")) {
                    activeChar.setVar("hideSkillAnime", "true");
                    activeChar.sendMessage("Broadcast Skill animation enabled.");
                } else {
                    activeChar.setVar("hideSkillAnime", "false");
                    activeChar.sendMessage("Broadcast Skill animation disabled.");
                }
                break;
            case "hideDressMe":
                if (!activeChar.getVarB("showVisualChange")) {
                    activeChar.setVar("showVisualChange", "true");
                    activeChar.sendMessage("Dress me information showing!");
                } else {
                    activeChar.setVar("showVisualChange", "false");
                    activeChar.sendMessage("Dress me information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
            case "hideAgathion":
                if (!activeChar.getVarB("hideAgathion")) {
                    activeChar.setVar("hideAgathion", "true");
                    activeChar.sendMessage("Agathion information showing!");
                } else {
                    activeChar.setVar("hideAgathion", "false");
                    activeChar.sendMessage("Agathion information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
            case "hideHero":
                if (!activeChar.getVarB("hideHero")) {
                    activeChar.setVar("hideHero", "true");
                    activeChar.sendMessage("Hero information showing!");
                } else {
                    activeChar.setVar("hideHero", "false");
                    activeChar.sendMessage("Hero information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
            case "hideTitle":
                if (!activeChar.getVarB("hideTitle")) {
                    activeChar.setVar("hideTitle", "true");
                    activeChar.sendMessage("Title information showing!");
                } else {
                    activeChar.setVar("hideTitle", "false");
                    activeChar.sendMessage("Title information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
            case "hideEnchant":
                if (!activeChar.getVarB("hideEnchant")) {
                    activeChar.setVar("hideEnchant", "true");
                    activeChar.sendMessage("Enchant information showing!");
                } else {
                    activeChar.setVar("hideEnchant", "false");
                    activeChar.sendMessage("Enchant information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
            case "hideName":
                if (!activeChar.getVarB("hideName")) {
                    activeChar.setVar("hideName", "true");
                    activeChar.sendMessage("Name information showing!");
                } else {
                    activeChar.setVar("hideName", "false");
                    activeChar.sendMessage("Name information hidden!");
                }
                Visuals.refreshAppStatus(activeChar);
                activeChar.broadcastUserInfo();
                break;
        }
    }
}
