package gr.sr.javaBuffer;


import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.Managers.ServicesBBSManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.ShowBoard;
import l2r.gameserver.network.serverpackets.TutorialShowHtml;


public class BufferPacketSender {
    public BufferPacketSender() {
    }

    
    public static void sendPacket(L2PcInstance player, String file, BufferPacketCategories bufferPacketCategories, int npcObjId) {
        NpcHtmlMessage html = new NpcHtmlMessage();
        switch(bufferPacketCategories) {
            case FILE:
                switch(npcObjId) {
                    case 0:
                        html.setFile(player, player.getHtmlPrefix(), "/data/html/sunrise/ItemBuffer/" + file);
                        player.sendPacket(html);
                        return;
                    default:
                        html.setFile(player, player.getHtmlPrefix(), "/data/html/sunrise/NpcBuffer/" + file);
                        html.replace("%objectId%", String.valueOf(npcObjId));
                        player.sendPacket(html);
                        return;
                }
            case DYNAMIC:
                switch(npcObjId) {
                    case 0:
                        html.setHtml(file);
                        player.sendPacket(html);
                        return;
                    case 1:
                        showCommunity(player, file);
                        return;
                    default:
                        html.setHtml(file);
                        html.replace("%objectId%", String.valueOf(npcObjId));
                        player.sendPacket(html);
                        return;
                }
            case LONG:
                if (npcObjId != 0) {
                    file = file.replace("%objectId%", String.valueOf(npcObjId));
                    player.sendPacket(new TutorialShowHtml(file));
                    return;
                }
                break;
            case COMMUNITY:
                file = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/CommunityBoard/services/buffer/" + file);
                showCommunity(player, file);
        }

    }

    private static void showCommunity(L2PcInstance player, String html) {
        if ((html = html.replace("\t", "").replace("%command%", ServicesBBSManager.getInstance()._servicesBBSCommand)).length() < 8180) {
            player.sendPacket(new ShowBoard(html, "101"));
            player.sendPacket(new ShowBoard((String)null, "102"));
            player.sendPacket(new ShowBoard((String)null, "103"));
        } else if (html.length() < 16360) {
            player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
            player.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
            player.sendPacket(new ShowBoard((String)null, "103"));
        } else {
            if (html.length() < 24540) {
                player.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
                player.sendPacket(new ShowBoard(html.substring(8180, 16360), "102"));
                player.sendPacket(new ShowBoard(html.substring(16360, html.length()), "103"));
            }

        }
    }
}
