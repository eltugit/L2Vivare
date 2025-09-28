package gabriel.events.extremeZone;

import gabriel.config.GabConfig;
import gr.sr.data.xml.AbstractFileParser;
import l2r.Config;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.L2WorldRegion;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.zone.L2ZoneForm;
import l2r.gameserver.model.zone.form.ZoneNPoly;
import l2r.gameserver.model.zone.type.L2ExtremeCheckerZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class ExtremeZoneParser extends AbstractFileParser<ExtremeZoneHolder> {
    private static final Logger _log = LoggerFactory.getLogger(ExtremeZoneParser.class);

    private static String PATH = Config.DATAPACK_ROOT + "/data/xml/gabriel/extremeZone.xml";
    protected static ExtremeZoneParser instance;

    private ExtremeZoneParser() {
        super(ExtremeZoneHolder.getInstance());
    }

    public static ExtremeZoneParser getInstance() {
        if (instance == null)
            instance = new ExtremeZoneParser();
        return instance;
    }

    public File getXMLFile() {
        return new File(PATH);
    }

    protected void readData() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);

        File file = getXMLFile();

        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);

            for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equalsIgnoreCase("list")) {
                    for (Node listNode = node.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
                        if (listNode.getNodeName().equalsIgnoreCase("extremezone")) {
                            int npcId = Integer.parseInt(listNode.getAttributes().getNamedItem("npcId").getNodeValue());
                            String name = listNode.getAttributes().getNamedItem("name").getNodeValue();
                            String imageName = listNode.getAttributes().getNamedItem("imageName").getNodeValue();

                            Location raidBoss = null;
                            Location teleNpc = null;
                            List<Location> playerLoc = new ArrayList<>();

                            for (Node epicRaidNode = listNode.getFirstChild(); epicRaidNode != null; epicRaidNode = epicRaidNode.getNextSibling()) {
                                if ("locationSpawnRaidBoss".equalsIgnoreCase(epicRaidNode.getNodeName())) {
                                    int xNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("x").getNodeValue());
                                    int yNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("y").getNodeValue());
                                    int zNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("z").getNodeValue());
                                    raidBoss = new Location(xNpc, yNpc, zNpc);
                                }

                                if ("playerTeleport".equalsIgnoreCase(epicRaidNode.getNodeName())) {
                                    for (Node playerTpsNode = epicRaidNode.getFirstChild(); playerTpsNode != null; playerTpsNode = playerTpsNode.getNextSibling()) {
                                        if ("locationTeleportPlayer".equalsIgnoreCase(playerTpsNode.getNodeName())) {
                                            int xP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("x").getNodeValue());
                                            int yP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("y").getNodeValue());
                                            int zP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("z").getNodeValue());
                                            playerLoc.add(new Location(xP, yP, zP));
                                        }
                                    }
                                }

                                if ("locationSpawnNpc".equalsIgnoreCase(epicRaidNode.getNodeName())) {
                                    int xNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("x").getNodeValue());
                                    int yNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("y").getNodeValue());
                                    int zNpc = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("z").getNodeValue());
                                    teleNpc = new Location(xNpc, yNpc, zNpc);
                                }

                                if (!GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                                    L2ZoneForm zoneForm = null;
                                    String zoneName = "";
                                    int zoneId = 0;
                                    if ("zoneInfo".equalsIgnoreCase(epicRaidNode.getNodeName())) {
                                        zoneName = epicRaidNode.getAttributes().getNamedItem("name").getNodeValue();
                                        int minZ = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("minZ").getNodeValue());
                                        int maxZ = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("maxZ").getNodeValue());
                                        zoneId = Integer.parseInt(epicRaidNode.getAttributes().getNamedItem("id").getNodeValue());
                                        List<int[]> rs = new ArrayList<>();
                                        int[] point;
                                        int[][] coords;
                                        for (Node mapCoordNode = epicRaidNode.getFirstChild(); mapCoordNode != null; mapCoordNode = mapCoordNode.getNextSibling()) {
                                            if ("node".equalsIgnoreCase(mapCoordNode.getNodeName())) {
                                                point = new int[2];
                                                point[0] = Integer.parseInt(mapCoordNode.getAttributes().getNamedItem("X").getNodeValue());
                                                point[1] = Integer.parseInt(mapCoordNode.getAttributes().getNamedItem("Y").getNodeValue());
                                                rs.add(point);
                                            }
                                        }

                                        coords = rs.toArray(new int[rs.size()][2]);
                                        if (coords.length > 2) {
                                            final int[] aX = new int[coords.length];
                                            final int[] aY = new int[coords.length];
                                            for (int i = 0; i < coords.length; i++) {
                                                aX[i] = coords[i][0];
                                                aY[i] = coords[i][1];
                                            }
                                            zoneForm = new ZoneNPoly(aX, aY, minZ, maxZ);
                                        } else {
                                            _log.warn("Could not parse extremeRaidZone: " + zoneName + " You need to have at least 3 node locations!");
                                            continue;
                                        }

                                        L2ExtremeCheckerZone temp = new L2ExtremeCheckerZone(zoneId);
                                        temp.setZone(zoneForm);
                                        temp.setName(zoneName);
                                        temp.setCoors(rs);

                                        ZoneManager.getInstance().addZone(zoneId, temp);

                                        int ax, ay, bx, by;
                                        final L2WorldRegion[][] worldRegions = L2World.getInstance().getWorldRegions();
                                        for (int x = 0; x < worldRegions.length; x++) {
                                            for (int y = 0; y < worldRegions[x].length; y++) {
                                                ax = (x - L2World.OFFSET_X) << L2World.SHIFT_BY;
                                                bx = ((x + 1) - L2World.OFFSET_X) << L2World.SHIFT_BY;
                                                ay = (y - L2World.OFFSET_Y) << L2World.SHIFT_BY;
                                                by = ((y + 1) - L2World.OFFSET_Y) << L2World.SHIFT_BY;

                                                if (temp.getZone().intersectsRectangle(ax, bx, ay, by)) {
                                                    worldRegions[x][y].addZone(temp);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            getHolder().addRaid(new ExtremeZone(name, imageName, npcId, playerLoc, raidBoss, teleNpc));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            _log.warn(getClass().getSimpleName() + ": Error: " + e);
        }
    }
}
