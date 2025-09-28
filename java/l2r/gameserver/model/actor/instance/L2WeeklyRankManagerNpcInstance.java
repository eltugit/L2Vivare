/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.model.actor.instance;

import gabriel.events.weeklyRank.WeeklyManager;
import gabriel.events.weeklyRank.objects.ClanRankObject;
import gabriel.events.weeklyRank.objects.PlayerAssistRankObject;
import gabriel.events.weeklyRank.objects.PlayerRankObject;
import javolution.text.TextBuilder;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class L2WeeklyRankManagerNpcInstance extends L2Npc {
    private static final String htmlPath = "data/html/gabriel/WeeklyRank/";

    public L2WeeklyRankManagerNpcInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2WeeklyRankManagerNpcInstance);
    }

    @Override
    public void onBypassFeedback(L2PcInstance playerInstance, String command) {
        switch (command) {
            case "currPlayerRank":
                showPlayerRankWindow(playerInstance);
                return;
            case "currPlayerRankAssist":
                showPlayerRankWindowAssist(playerInstance);
                return;
            case "currClanRank":
                showClanRankWindow(playerInstance);
                return;
            case "winnerRank":
                showWinnerRankPage(playerInstance);
                return;
            case "winnerPlayerRank":
                showTopWinnersPlayers(playerInstance);
                return;
            case "winnerClanRank":
                showTopWinnersClan(playerInstance);
                return;
            case "winnerPlayerRankAssist":
                showTopWinnersPlayersAssist(playerInstance);
                return;
            case "getRewardClan":
                getReward(playerInstance, false, false);
                return;
            case "getRewardPlayer":
                getReward(playerInstance, true, false);
                return;
            case "getRewardPlayerAssist":
                getReward(playerInstance, true, true);
                return;
        }

        WeeklyManager.getInstance().parseCommand(command, playerInstance, true);
    }

    @Override
    public void showChatWindow(L2PcInstance player, int val) {

        WeeklyManager.getInstance().parseCommand("currentRank", player, true);

//        if (player == null)
//            return;
//
//        final String htmContent;
//        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRank.htm");
//        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
//
//        if (htmContent != null) {
//
//            npcHtmlMessage.setHtml(htmContent);
//            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
//
//            player.sendPacket(npcHtmlMessage);
//        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }



    private void showWinnerRankPage(L2PcInstance player) {
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankWinner.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

            player.sendPacket(npcHtmlMessage);
        }
    }


    private void showPlayerRankWindow(L2PcInstance player) {
        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (PlayerRankObject playerRankObject : manager.getTopXPlayer()) {
                html.append("<tr>");
                html.append("<td width=20>" + playerRankObject.getRankPlace() + "</td>");
                html.append("<td width=155>" + playerRankObject.getCharName() + "</td>");
                html.append("<td width=125>" + playerRankObject.getCharKills() + "</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            npcHtmlMessage.replace("%rank%", html.toString());

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void showPlayerRankWindowAssist(L2PcInstance player) {
        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (PlayerAssistRankObject playerRankObject : manager.getTopXPlayerAssist()) {
                html.append("<tr>");
                html.append("<td width=20>" + playerRankObject.getRankPlace() + "</td>");
                html.append("<td width=155>" + playerRankObject.getCharName() + "</td>");
                html.append("<td width=125>" + playerRankObject.getCharAssists() + "</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            npcHtmlMessage.replace("%rank%", html.toString());

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void showClanRankWindow(L2PcInstance player) {
        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (ClanRankObject clanRankObject : manager.getTopXClan()) {
                html.append("<tr>");
                html.append("<td width=20>" + clanRankObject.getRankPlace() + "</td>");
                html.append("<td width=155>" + clanRankObject.getClanName() + "</td>");
                html.append("<td width=125>" + clanRankObject.getClanKills() + "</td>");
                html.append("</tr>");
            }
            html.append("</table>");

            npcHtmlMessage.replace("%rank%", html.toString());

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void showTopWinnersPlayers(L2PcInstance player) {

        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (Map.Entry<Integer, PlayerRankObject> entrySet : manager.getFinalRewarders().getPlayerRanks().entrySet()) {
                int rank = entrySet.getKey();
                PlayerRankObject obj = entrySet.getValue();

                html.append("<tr>");
                html.append("<td width=20>" + rank + "</td>");
                html.append("<td width=155>" + obj.getCharName() + "</td>");
                html.append("<td width=125>" + obj.getCharKills() + "</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            Integer[] data = manager.existInRank(player.getObjectId(), true, false);
            boolean found = data[1] == 1;
            if (found) {
                html.append("<center>\n" +
                        "<button value=\"Get Reward\" action=\"bypass -h npc_%objectId%_getRewardPlayer\" width=150 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">\n" +
                        "</center>");
            }
            npcHtmlMessage.replace("%rank%", html.toString());
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void showTopWinnersPlayersAssist(L2PcInstance player) {

        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (Map.Entry<Integer, PlayerAssistRankObject> entrySet : manager.getFinalRewarders().getAssistRanks().entrySet()) {
                int rank = entrySet.getKey();
                PlayerAssistRankObject obj = entrySet.getValue();

                html.append("<tr>");
                html.append("<td width=20>" + rank + "</td>");
                html.append("<td width=155>" + obj.getCharName() + "</td>");
                html.append("<td width=125>" + obj.getCharAssists() + "</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            Integer[] data = manager.existInRank(player.getObjectId(), true, true);
            boolean found = data[1] == 1;
            if (found) {
                html.append("<center>\n" +
                        "<button value=\"Get Reward\" action=\"bypass -h npc_%objectId%_getRewardPlayerAssist\" width=150 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">\n" +
                        "</center>");
            }
            npcHtmlMessage.replace("%rank%", html.toString());
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void showTopWinnersClan(L2PcInstance player) {
        WeeklyManager manager = WeeklyManager.getInstance();
        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(player.getHtmlPrefix(), htmlPath + "WeeklyRankPage.htm");
        NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
        if (htmContent != null) {

            npcHtmlMessage.setHtml(htmContent);

            TextBuilder html = new TextBuilder();
            html.append("<table width=300>");

            for (Map.Entry<Integer, ClanRankObject> entrySet : manager.getFinalRewarders().getClanRanks().entrySet()) {
                int rank = entrySet.getKey();
                ClanRankObject obj = entrySet.getValue();

                html.append("<tr>");
                html.append("<td width=20>" + rank + "</td>");
                html.append("<td width=155>" + obj.getClanName() + "</td>");
                html.append("<td width=125>" + obj.getClanKills() + "</td>");
                html.append("</tr>");
            }

            html.append("</table>");

            Integer[] data = manager.existInRank(player.getClanId(), false, false);
            boolean found = data[1] == 1;
            if (found) {
                html.append("<center>\n" +
                        "<button value=\"Get Reward\" action=\"bypass -h npc_%objectId%_getRewardClan\" width=150 height=30 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">\n" +
                        "</center>");
            }
            npcHtmlMessage.replace("%rank%", html.toString());
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

        }
        player.sendPacket(npcHtmlMessage);
    }

    private void getReward(L2PcInstance player, boolean playerRank, boolean assist) {
        WeeklyManager.getInstance().getReward(player, playerRank, assist);
    }


}
