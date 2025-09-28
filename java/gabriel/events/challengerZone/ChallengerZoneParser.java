package gabriel.events.challengerZone;

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
public class ChallengerZoneParser extends AbstractFileParser<ChallengerZoneHolder> {
    private static final Logger _log = LoggerFactory.getLogger(ChallengerZoneParser.class);

    private static String PATH = Config.DATAPACK_ROOT + "/data/xml/gabriel/challengerZone.xml";
    protected static ChallengerZoneParser instance;

    private ChallengerZoneParser() {
        super(ChallengerZoneHolder.getInstance());
    }

    public static ChallengerZoneParser getInstance() {
        if (instance == null)
            instance = new ChallengerZoneParser();
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
                            int zoneId = Integer.parseInt(listNode.getAttributes().getNamedItem("zoneId").getNodeValue());
                            String name = listNode.getAttributes().getNamedItem("name").getNodeValue();
                            String imageName = listNode.getAttributes().getNamedItem("imageName").getNodeValue();

                            Location teleNpc = null;
                            List<Location> playerLoc = new ArrayList<>();

                            for (Node challengerNode = listNode.getFirstChild(); challengerNode != null; challengerNode = challengerNode.getNextSibling()) {
                                if ("playerTeleport".equalsIgnoreCase(challengerNode.getNodeName())) {
                                    for (Node playerTpsNode = challengerNode.getFirstChild(); playerTpsNode != null; playerTpsNode = playerTpsNode.getNextSibling()) {
                                        if ("locationTeleportPlayer".equalsIgnoreCase(playerTpsNode.getNodeName())) {
                                            int xP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("x").getNodeValue());
                                            int yP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("y").getNodeValue());
                                            int zP = Integer.parseInt(playerTpsNode.getAttributes().getNamedItem("z").getNodeValue());
                                            playerLoc.add(new Location(xP, yP, zP));
                                        }
                                    }
                                }

                                if ("locationSpawnNpc".equalsIgnoreCase(challengerNode.getNodeName())) {
                                    int xNpc = Integer.parseInt(challengerNode.getAttributes().getNamedItem("x").getNodeValue());
                                    int yNpc = Integer.parseInt(challengerNode.getAttributes().getNamedItem("y").getNodeValue());
                                    int zNpc = Integer.parseInt(challengerNode.getAttributes().getNamedItem("z").getNodeValue());
                                    teleNpc = new Location(xNpc, yNpc, zNpc);
                                }

                                if (!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
                                    L2ZoneForm zoneForm = null;
                                    String zoneName = "";
                                    int zzoneId = 0;
                                    if ("zoneInfo".equalsIgnoreCase(challengerNode.getNodeName())) {
                                        zoneName = challengerNode.getAttributes().getNamedItem("name").getNodeValue();
                                        int minZ = Integer.parseInt(challengerNode.getAttributes().getNamedItem("minZ").getNodeValue());
                                        int maxZ = Integer.parseInt(challengerNode.getAttributes().getNamedItem("maxZ").getNodeValue());
                                        zzoneId = Integer.parseInt(challengerNode.getAttributes().getNamedItem("id").getNodeValue());
                                        List<int[]> rs = new ArrayList<>();
                                        int[] point;
                                        int[][] coords;
                                        for (Node mapCoordNode = challengerNode.getFirstChild(); mapCoordNode != null; mapCoordNode = mapCoordNode.getNextSibling()) {
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

                                        L2ExtremeCheckerZone temp = new L2ExtremeCheckerZone(zzoneId);
                                        temp.setZone(zoneForm);
                                        temp.setName(zoneName);
                                        temp.setCoors(rs);

                                        ZoneManager.getInstance().addZone(zzoneId, temp);

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
                            getHolder().addRaid(new ChallengerZone(name, imageName, zoneId, playerLoc, teleNpc));
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
