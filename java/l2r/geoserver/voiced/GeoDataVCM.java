package l2r.geoserver.voiced;

import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.geoserver.GeoData;
import l2r.geoserver.geodata.GeoEngine;
import l2r.geoserver.geodata.PathFindBuffers;
import l2r.geoserver.utils.GeodataUtils;

import java.io.File;
import java.util.StringTokenizer;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class GeoDataVCM implements IAdminCommandHandler {
    private static final String[] a = new String[]{"admin_geo_trace", "admin_geo_nswe", "admin_geo_can_move", "admin_geo_can_see", "admin_geogrid", "admin_pathfind_buffers"};

    public GeoDataVCM() {
    }

    public boolean useAdminCommand(String var1, L2PcInstance player) {
        StringTokenizer var3 = new StringTokenizer(var1, " ");
        String var4 = var3.nextToken();
        String command = var4.toLowerCase();
        byte var6 = -1;
        L2Object var9;

        switch (command) {
            case "admin_geo_z":
                player.sendMessage("GeoEngine: Geo_Z = " + GeoEngine.getHeight(player.getLocation(), player.getInstanceId()) + " Loc_Z = " + player.getZ());
                break;
            case "admin_geo_nswe":
                String var7 = "";
                short var8 = GeoEngine.getNSWE(player.getX(), player.getY(), player.getZ(), player.getInstanceId());
                if ((var8 & 8) == 0) {
                    var7 = var7 + " N";
                }

                if ((var8 & 4) == 0) {
                    var7 = var7 + " S";
                }

                if ((var8 & 2) == 0) {
                    var7 = var7 + " W";
                }

                if ((var8 & 1) == 0) {
                    var7 = var7 + " E";
                }
                player.sendMessage("GeoEngine: Geo_NSWE -> " + var8 + "->" + var7);
                break;
            case "admin_geo_trace":
                if (player.getVarB("geo_trace", false)) {
                    player.sendMessage("Geo trace disabled");
                    player.setVar("geo_trace", "false");
                } else {
                    player.sendMessage("Geo trace enabled");
                    player.setVar("geo_trace", "true");
                }
                break;
            case "admin_pathfind_buffers":
                sendHtm(player, PathFindBuffers.getStats().visualization());
                break;
            case "admin_geo_can_see":
                var9 = player.getTarget();
                if (var9 != null) {
                    if (GeoData.getInstance().canSeeTarget(player, var9)) {
                        player.sendMessage("Can see target.");
                    } else {
                        player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANT_SEE_TARGET));
                    }
                } else {
                    player.sendPacket(SystemMessageId.INCORRECT_TARGET);
                }
                break;
            case "admin_geo_can_move":
                var9 = player.getTarget();
                if (var9 != null) {
                    if (GeoData.getInstance().canSeeTarget(player, var9)) {
                        player.sendMessage("Can move beeline.");
                    } else {
                        player.sendMessage("Can not move beeline!");
                    }
                } else {
                    player.sendPacket(SystemMessageId.INCORRECT_TARGET);
                }
                break;
            case "admin_geogrid":
                GeodataUtils.debugGrid(player);
                break;
        }

        return true;
    }

    private static void sendHtm(L2PcInstance player, String path) {
        sendHtm(player, "html/admin/" + path, false);
    }

    private static void sendHtm(L2PcInstance var0, String var1, boolean load) {
        String var3 = null;
        if (!load) {
            var3 = HtmCache.getInstance().getHtm(var0, var0.getHtmlPrefix(), var1);
        } else {
            File var4 = new File(Config.DATAPACK_ROOT, var1);
            var3 = HtmCache.getInstance().loadFile(var4);
        }

        NpcHtmlMessage var5 = new NpcHtmlMessage();
        if (var3 != null) {
            var5.setHtml(var3);
        } else {
            var5.setHtml("<html><body>My text is missing:<br>" + var1 + "</body></html>");
        }

        var0.sendPacket(var5);
    }

    public String[] getAdminCommandList() {
        return a;
    }
}
