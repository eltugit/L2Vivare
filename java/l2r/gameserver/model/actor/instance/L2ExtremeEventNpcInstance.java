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

import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class L2ExtremeEventNpcInstance extends L2Npc {
    private static final String htmlPath = "data/html/gabriel/events/ExtremeEvent/";

    public L2ExtremeEventNpcInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ExtremeEventNpcInstance);
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
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

