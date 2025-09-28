package gabriel.pvpInstanceZone.utils;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ShowBoard;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class PVPInstanceShowBoard {

    public static void separateAndSend(String html, L2PcInstance acha) {
        if (html == null) {
            return;
        }
        if (html.length() < 4090) {
            acha.sendPacket(new ShowBoard(html, "101"));
            acha.sendPacket(new ShowBoard(null, "102"));
            acha.sendPacket(new ShowBoard(null, "103"));

        } else if (html.length() < 8180) {
            acha.sendPacket(new ShowBoard(html.substring(0, 4090), "101"));
            acha.sendPacket(new ShowBoard(html.substring(4090, html.length()), "102"));
            acha.sendPacket(new ShowBoard(null, "103"));

        } else if (html.length() < 12270) {
            acha.sendPacket(new ShowBoard(html.substring(0, 4090), "101"));
            acha.sendPacket(new ShowBoard(html.substring(4090, 8180), "102"));
            acha.sendPacket(new ShowBoard(html.substring(8180, html.length()), "103"));

        }
    }
}
