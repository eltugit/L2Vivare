package gabriel.scriptsGab.utils;

import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ShowBoard;

public class BBS {
    public static void separateAndSend(String html, L2PcInstance acha) {
        if (html == null) {
            return;
        }
        if (html.length() < 16250) {
            acha.sendPacket(new ShowBoard(html, "101"));
            acha.sendPacket(new ShowBoard("", "102"));
            acha.sendPacket(new ShowBoard("", "103"));
        } else if (html.length() < (16250 * 2)) {
            acha.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            acha.sendPacket(new ShowBoard(html.substring(16250), "102"));
            acha.sendPacket(new ShowBoard("", "103"));
        } else if (html.length() < (16250 * 3)) {
            acha.sendPacket(new ShowBoard(html.substring(0, 16250), "101"));
            acha.sendPacket(new ShowBoard(html.substring(16250, 16250 * 2), "102"));
            acha.sendPacket(new ShowBoard(html.substring(16250 * 2), "103"));
        } else {
            acha.sendPacket(new ShowBoard("<html><body><br><center>Error: HTML was too long!</center></body></html>", "101"));
            acha.sendPacket(new ShowBoard("", "102"));
            acha.sendPacket(new ShowBoard("", "103"));
        }
    }
}
