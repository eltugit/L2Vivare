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

import gabriel.epicRaid.AccessGranter;
import gabriel.epicRaid.EpicRaidManager;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.NpcSay;
import l2r.gameserver.util.Broadcast;

/**
 * @author Gabriel Costa Souza
 */
public class L2EpicEventNpcInstance extends L2Npc {
    private static final String htmlPath = "data/html/mods/EREvent/";
    private static final int FLAG_RADIUS = 900;
    private static final int FLAG_INTERVAL = 1000; // in ms

    public L2EpicEventNpcInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2EREventNpcInstance);
        if (template.getId() == 12006) {
            ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
                if (this != null && !this.isDead()) {
                    for (L2PcInstance player : getKnownList().getKnownPlayersInRadius(FLAG_RADIUS)) {
                        if (player != null && !player.isDead()) {
                            player.updatePvPStatus();
                        }
                    }
                }
            }, FLAG_INTERVAL, FLAG_INTERVAL);
        }
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        EpicRaidManager manager = EpicRaidManager.getInstance();
        if (command.equals("enterLair")) {
            if (player.isGM()) {
                EpicRaidManager.getInstance().teleportPlayerIntoInstance(player);
                return;
            }
            if (EpicRaidManager.getInstance().isClosed()) {
                Broadcast.toKnownPlayers(this, new NpcSay(this, Say2.BATTLEFIELD, "The boss is almost dead! You cannot enter now!"));
                return;
            }
            AccessGranter ag = manager.getChecker().getListOfAccesses().stream().filter(e -> e.getRightAccess(player) != null).findFirst().orElse(null);
            if ((manager.getAccessGranted() != null && ag != null && manager.getAccessGranted() == ag)) {
                ag.teleportPlayers(this.getLocation());
            } else {
                player.sendMessage("You dont have the access rights to enter the Epic Raid.");
            }
        }
    }


    @Override
    public void showChatWindow(L2PcInstance playerInstance, int val) {
        if (playerInstance == null)
            return;

        final String htmContent;
        htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Participation.htm");


        if (htmContent != null) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
            playerInstance.sendPacket(npcHtmlMessage);
        } else {
            playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
}

