package l2r.gameserver.model.actor.instance;

import gabriel.config.GabConfig;
import gabriel.events.castleSiegeKoth.CSKOTHEvent;
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
 * website: l2jgabdev.com
 */
public class L2CastleSiegeEventNpcInstance extends L2Npc {
    private static final String htmlPath = "data/html/gabriel/events/CastleSiegeKingOfTheHill/";

    public L2CastleSiegeEventNpcInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2CSKOTHEventNpcInstance);
    }

    @Override
    public void onBypassFeedback(L2PcInstance playerInstance, String command) {
        CSKOTHEvent.onBypass(command, playerInstance);
    }

    @Override
    public void showChatWindow(L2PcInstance playerInstance, int val) {
        if (playerInstance == null)
            return;

        if (CSKOTHEvent.isParticipating()) {
            final boolean isParticipant = CSKOTHEvent.isPlayerParticipant(playerInstance.getObjectId());
            final String htmContent;

            if (!isParticipant)
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Participation.htm");
            else
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "RemoveParticipation.htm");

            if (htmContent != null) {
                int[] teamsPlayerCounts = CSKOTHEvent.getTeamsPlayerCounts();
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

                npcHtmlMessage.setHtml(htmContent);
                npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
                npcHtmlMessage.replace("%team1name%", GabConfig.CSKOTH_EVENT_TEAM_1_NAME);
                npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
                npcHtmlMessage.replace("%team2name%", GabConfig.CSKOTH_EVENT_TEAM_2_NAME);
                npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
                npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
                if (!isParticipant)
                    npcHtmlMessage.replace("%fee%", CSKOTHEvent.getParticipationFee());

                playerInstance.sendPacket(npcHtmlMessage);
            }
        } else if (CSKOTHEvent.isStarting() || CSKOTHEvent.isStarted()) {
            final String htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Status.htm");

            if (htmContent != null) {
                NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

                npcHtmlMessage.setHtml(htmContent);
                npcHtmlMessage.replace("%team1name%", GabConfig.CSKOTH_EVENT_TEAM_1_NAME);
                npcHtmlMessage.replace("%team1playercount%", String.valueOf(CSKOTHEvent.getTeamsPlayerCounts()[0]));
                npcHtmlMessage.replace("%team1points%", String.valueOf(CSKOTHEvent.getTeamsPoints()[0]));
                npcHtmlMessage.replace("%team2name%", GabConfig.CSKOTH_EVENT_TEAM_2_NAME);
                npcHtmlMessage.replace("%team2playercount%", String.valueOf(CSKOTHEvent.getTeamsPlayerCounts()[1]));
                npcHtmlMessage.replace("%team2points%", String.valueOf(CSKOTHEvent.getTeamsPoints()[1]));


                playerInstance.sendPacket(npcHtmlMessage);
            }
        }

        playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
    }
}

