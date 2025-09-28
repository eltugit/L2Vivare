package gabriel.pvpInstanceZone.xml;

import gabriel.pvpInstanceZone.CustomPvPInstanceZone;
import l2r.gameserver.model.Location;
import l2r.util.data.xml.IXmlReader.IXmlReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PvPInstanceParser implements IXmlReader {


    public final Map<String, CustomPvPInstanceZone> _zones = new HashMap<>();

    public Map<String, CustomPvPInstanceZone> get_zones() {
        return _zones;
    }

    public CustomPvPInstanceZone[] getZonesArray() {
        List<CustomPvPInstanceZone> list = new ArrayList<>(_zones.values());
        return list.toArray(new CustomPvPInstanceZone[list.size()]);
    }

    public PvPInstanceParser() {
        load();
    }

    @Override
    public synchronized void load() {
        _zones.clear();
        parseDatapackFile("data/xml/gabriel/pvpInstance.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _zones.size() + " PvP Instance Zones.");
    }

    @Override
    public void parseDocument(Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    String name = "";
                    List<Location> locList = new ArrayList<>();
                    List<Integer> boss = new ArrayList<>();
                    if ("zone".equalsIgnoreCase(d.getNodeName())) {
                        name = parseString(d.getAttributes(), "name");

                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("playerTeleport".equalsIgnoreCase(cd.getNodeName())) {
                                for (Node tp = cd.getFirstChild(); tp != null; tp = tp.getNextSibling()) {
                                    if ("locationTeleportPlayer".equalsIgnoreCase(tp.getNodeName())) {
                                        int x = parseInteger(tp.getAttributes(), "x");
                                        int y = parseInteger(tp.getAttributes(), "y");
                                        int z = parseInteger(tp.getAttributes(), "z");
                                        locList.add(new Location(x, y, z));
                                    }
                                }

                            }
                            if ("locationSpawnRaidBoss".equalsIgnoreCase(cd.getNodeName())) {
                                boss = new ArrayList<>();
                                int bossId = parseInteger(cd.getAttributes(), "bossId");
                                int x = parseInteger(cd.getAttributes(), "x");
                                int y = parseInteger(cd.getAttributes(), "y");
                                int z = parseInteger(cd.getAttributes(), "z");
                                boss.add(bossId);
                                boss.add(x);
                                boss.add(y);
                                boss.add(z);
                            }
                        }
                        _zones.put(name, new CustomPvPInstanceZone(name, locList.toArray(new Location[locList.size()]), boss.toArray(new Integer[boss.size()])));
                    }
                }
            }
        }
    }

    protected static PvPInstanceParser instance;

    public static PvPInstanceParser getInstance() {
        if (instance == null)
            instance = new PvPInstanceParser();
        return instance;
    }
}