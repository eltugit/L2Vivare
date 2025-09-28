package l2r.geoserver.utils;

import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ExServerPrimitive;
import l2r.gameserver.util.LinePointIterator;
import l2r.gameserver.util.LinePointIterator3D;
import l2r.geoserver.GeoData;
import l2r.geoserver.geodata.GeoEngine;

import java.awt.*;

public class GeodataUtils {
    public static final byte EAST = 1;
    public static final byte WEST = 2;
    public static final byte SOUTH = 4;
    public static final byte NORTH = 8;
    public static final byte NSWE_ALL = 15;
    public static final byte NSWE_NONE = 0;

    public static void debug2DLine(final L2PcInstance player, final int x, final int y, final int tx, final int ty, final int z) {
        final int geoX = GeoData.getInstance().getGeoX(x);
        final int geoY = GeoData.getInstance().getGeoY(y);
        final int geoX2 = GeoData.getInstance().getGeoX(tx);
        final int geoY2 = GeoData.getInstance().getGeoY(ty);
        final ExServerPrimitive exServerPrimitive = new ExServerPrimitive("Debug2DLine", x, y, z);
        exServerPrimitive.addLine(Color.BLUE, GeoData.getInstance().getWorldX(geoX), GeoData.getInstance().getWorldY(geoY), z, GeoData.getInstance().getWorldX(geoX2), GeoData.getInstance().getWorldY(geoY2), z);
        final LinePointIterator linePointIterator = new LinePointIterator(geoX, geoY, geoX2, geoY2);
        while (linePointIterator.next()) {
            exServerPrimitive.addPoint(Color.RED, GeoData.getInstance().getWorldX(linePointIterator.x()), GeoData.getInstance().getWorldY(linePointIterator.y()), z);
        }
        player.sendPacket(exServerPrimitive);
    }

    public static void debug3DLine(final L2PcInstance l2PcInstance, final int x, final int y, final int z, final int tx, final int ty, final int tz) {
        final int geoX = GeoData.getInstance().getGeoX(x);
        final int geoY = GeoData.getInstance().getGeoY(y);
        final int geoX2 = GeoData.getInstance().getGeoX(tx);
        final int geoY2 = GeoData.getInstance().getGeoY(ty);
        final ExServerPrimitive exServerPrimitive = new ExServerPrimitive("Debug3DLine", x, y, z);
        exServerPrimitive.addLine(Color.BLUE, GeoData.getInstance().getWorldX(geoX), GeoData.getInstance().getWorldY(geoY), z, GeoData.getInstance().getWorldX(geoX2), GeoData.getInstance().getWorldY(geoY2), tz);
        final LinePointIterator3D linePointIterator3D = new LinePointIterator3D(geoX, geoY, z, geoX2, geoY2, tz);
        linePointIterator3D.next();
        int prevX = linePointIterator3D.x();
        int prevY = linePointIterator3D.y();
        exServerPrimitive.addPoint(Color.RED, GeoData.getInstance().getWorldX(prevX), GeoData.getInstance().getWorldY(prevY), linePointIterator3D.z());
        while (linePointIterator3D.next()) {
            final int x2 = linePointIterator3D.x();
            final int y2 = linePointIterator3D.y();
            if (x2 != prevX || y2 != prevY) {
                exServerPrimitive.addPoint(Color.RED, GeoData.getInstance().getWorldX(x2), GeoData.getInstance().getWorldY(y2), linePointIterator3D.z());
                prevX = x2;
                prevY = y2;
            }
        }
        l2PcInstance.sendPacket(exServerPrimitive);
    }

    private static Color getDirectionColor(final int x, final int y, final int z, int geoIndex, final byte NSWE) {
        if ((GeoEngine.getNSWE(x, y, z, geoIndex) & NSWE) != 0) {
            return Color.GREEN;
        }
        return Color.RED;
    }

    public static void debugGrid(final L2PcInstance l2PcInstance) {
        final int geoRadius = 10;
        final int n2 = 10;
        if (geoRadius < 0) {
            throw new IllegalArgumentException("geoRadius < 0");
        }
        int n3 = n2;
        int i = 0;
        ExServerPrimitive exServerPrimitive = null;
        final int geoX = GeoData.getInstance().getGeoX(l2PcInstance.getX());
        final int geoY = GeoData.getInstance().getGeoY(l2PcInstance.getY());
        for (int j = -geoRadius; j <= geoRadius; ++j) {
            for (int k = -geoRadius; k <= geoRadius; ++k) {
                if (n3 >= n2) {
                    n3 = 0;
                    if (exServerPrimitive != null) {
                        ++i;
                        l2PcInstance.sendPacket(exServerPrimitive);
                    }
                    exServerPrimitive = new ExServerPrimitive("DebugGrid_" + i, l2PcInstance.getX(), l2PcInstance.getY(), -16000);
                }
                if (exServerPrimitive == null) {
                    throw new IllegalStateException();
                }
                final int n4 = geoX + j;
                final int n5 = geoY + k;
                int instanceId = l2PcInstance.getInstanceId();
                final int worldX = GeoData.getInstance().getWorldX(n4);
                final int worldY = GeoData.getInstance().getWorldY(n5);
                final int height = GeoData.getInstance().getHeight(l2PcInstance.getLocation());
                final Color a = getDirectionColor(worldX, worldY, height, instanceId, NORTH);
                exServerPrimitive.addLine(a, worldX - 1, worldY - 7, height, worldX + 1, worldY - 7, height);
                exServerPrimitive.addLine(a, worldX - 2, worldY - 6, height, worldX + 2, worldY - 6, height);
                exServerPrimitive.addLine(a, worldX - 3, worldY - 5, height, worldX + 3, worldY - 5, height);
                exServerPrimitive.addLine(a, worldX - 4, worldY - 4, height, worldX + 4, worldY - 4, height);
                final Color a2 = getDirectionColor(worldX, worldY, height, instanceId, EAST);
                exServerPrimitive.addLine(a2, worldX + 7, worldY - 1, height, worldX + 7, worldY + 1, height);
                exServerPrimitive.addLine(a2, worldX + 6, worldY - 2, height, worldX + 6, worldY + 2, height);
                exServerPrimitive.addLine(a2, worldX + 5, worldY - 3, height, worldX + 5, worldY + 3, height);
                exServerPrimitive.addLine(a2, worldX + 4, worldY - 4, height, worldX + 4, worldY + 4, height);
                final Color a3 = getDirectionColor(worldX, worldY, height, instanceId, SOUTH);
                exServerPrimitive.addLine(a3, worldX - 1, worldY + 7, height, worldX + 1, worldY + 7, height);
                exServerPrimitive.addLine(a3, worldX - 2, worldY + 6, height, worldX + 2, worldY + 6, height);
                exServerPrimitive.addLine(a3, worldX - 3, worldY + 5, height, worldX + 3, worldY + 5, height);
                exServerPrimitive.addLine(a3, worldX - 4, worldY + 4, height, worldX + 4, worldY + 4, height);
                final Color a4 = getDirectionColor(worldX, worldY, height, instanceId, WEST);
                exServerPrimitive.addLine(a4, worldX - 7, worldY - 1, height, worldX - 7, worldY + 1, height);
                exServerPrimitive.addLine(a4, worldX - 6, worldY - 2, height, worldX - 6, worldY + 2, height);
                exServerPrimitive.addLine(a4, worldX - 5, worldY - 3, height, worldX - 5, worldY + 3, height);
                exServerPrimitive.addLine(a4, worldX - 4, worldY - 4, height, worldX - 4, worldY + 4, height);
                ++n3;
            }
        }
        l2PcInstance.sendPacket(exServerPrimitive);
    }
}
