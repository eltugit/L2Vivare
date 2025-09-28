package gabriel.events.castleSiegeKoth;

import gabriel.config.GabConfig;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class CSKOTHVoicedInfo implements IVoicedCommandHandler {
    private static final String[] _voicedCommands = {"infocs", "joincs", "leavecs"};

    /**
     * Set this to false and recompile script if you dont want to use string cache.
     * This will decrease performance but will be more consistent against possible html editions during runtime
     * Recompiling the script will get the new html would be enough too [DrHouse]
     */
    private static final boolean USE_STATIC_HTML = true;

    public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) {
        String HTML = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/gabriel/events/CastleSiegeKingOfTheHill/Status.htm");

        if (command.equalsIgnoreCase("infocs")) {
            if (CSKOTHEvent.isStarting() || CSKOTHEvent.isStarted()) {
                String htmContent = (USE_STATIC_HTML && !HTML.isEmpty()) ? HTML : HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/gabriel/events/CastleSiegeKingOfTheHill/Status.htm");

                try {
                    NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(5);

                    npcHtmlMessage.setHtml(htmContent);
                    // npcHtmlMessage.replace("%objectId%",
                    // String.valueOf(getObjectId()));
                    npcHtmlMessage.replace("%team1name%", GabConfig.CSKOTH_EVENT_TEAM_1_NAME);
                    npcHtmlMessage.replace("%team1playercount%", String.valueOf(CSKOTHEvent.getTeamsPlayerCounts()[0]));
                    npcHtmlMessage.replace("%team1points%", String.valueOf(CSKOTHEvent.getTeamsPoints()[0]));
                    npcHtmlMessage.replace("%team2name%", GabConfig.CSKOTH_EVENT_TEAM_2_NAME);
                    npcHtmlMessage.replace("%team2playercount%", String.valueOf(CSKOTHEvent.getTeamsPlayerCounts()[1]));
                    npcHtmlMessage.replace("%team2points%", String.valueOf(CSKOTHEvent.getTeamsPoints()[1]));
                    activeChar.sendPacket(npcHtmlMessage);
                } catch (Exception e) {
                    _log.warn("wrong KOTH voiced: " + e);
                }

            } else {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            }
        } else if (command.equalsIgnoreCase("joincs")) {
            if (GabConfig.CSKOTH_ALLOW_REGISTER_VOICED_COMMAND)
                CSKOTHEvent.onBypass("cskoth_event_participation", activeChar);
            else
                activeChar.sendMessage("Command disabled");
        } else if (command.equalsIgnoreCase("leavecs")) {
            if (GabConfig.CSKOTH_ALLOW_REGISTER_VOICED_COMMAND)
                CSKOTHEvent.onBypass("cskoth_event_remove_participation", activeChar);
            else
                activeChar.sendMessage("Command disabled");
        }
        return true;
    }

    public String[] getVoicedCommandList() {
        return _voicedCommands;
    }
}
