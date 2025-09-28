package gr.sr.raidEngine.xml.dataParser;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.raidEngine.RaidLocation;
import gr.sr.raidEngine.xml.dataHolder.RaidLocationsHolder;
import l2r.gameserver.model.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class RaidLocationsParser extends AbstractFileParser<RaidLocationsHolder> {
    private static Logger log = LoggerFactory.getLogger(RaidLocationsParser.class);
    private final String path = "./config/sunrise/event/raidEngine/RaidsLocations.xml";
    private static final RaidLocationsParser RAID_LOCATIONS_PARSER = new RaidLocationsParser();

    public static RaidLocationsParser getInstance() {
        return RAID_LOCATIONS_PARSER;
    }

    protected RaidLocationsParser() {
        super(RaidLocationsHolder.getInstance());
    }

    public File getXMLFile() {
        return new File(path);
    }

    protected void readData() {
        try {
            InputSource vinputSourcer1 = new InputSource(new InputStreamReader(new FileInputStream(this.getXMLFile()), this.getEncoding().get()));

            for(Node firstChild = this.getFactory().newDocumentBuilder().parse(vinputSourcer1).getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                if (firstChild.getNodeName().equalsIgnoreCase("list")) {
                    for(Node subFirstChild = firstChild.getFirstChild(); subFirstChild != null; subFirstChild = subFirstChild.getNextSibling()) {
                        if (subFirstChild.getNodeName().equalsIgnoreCase("raid")) {
                            String locName = String.valueOf(subFirstChild.getAttributes().getNamedItem("locName").getNodeValue());
                            int x = Integer.parseInt(subFirstChild.getAttributes().getNamedItem("x").getNodeValue());
                            int y = Integer.parseInt(subFirstChild.getAttributes().getNamedItem("y").getNodeValue());
                            int z = Integer.parseInt(subFirstChild.getAttributes().getNamedItem("z").getNodeValue());
                            ((RaidLocationsHolder)this.getHolder())._locations.add(new RaidLocation(locName, new Location(x, y, z)));
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Raid Engine: Error: " + e);
            e.printStackTrace();
        }
    }
}
