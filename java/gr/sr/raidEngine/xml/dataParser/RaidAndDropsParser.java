package gr.sr.raidEngine.xml.dataParser;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.raidEngine.RaidDrop;
import gr.sr.raidEngine.RaidGroup;
import gr.sr.raidEngine.RaidType;
import gr.sr.raidEngine.xml.dataHolder.RaidAndDropsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;

public class RaidAndDropsParser extends AbstractFileParser<RaidAndDropsHolder> {
    private static Logger log = LoggerFactory.getLogger(RaidAndDropsParser.class);
    private final String path = "./config/sunrise/event/raidEngine/RaidsAndDrops.xml";
    private static final RaidAndDropsParser RAID_AND_DROPS_PARSER = new RaidAndDropsParser();

    public static RaidAndDropsParser getInstance() {
        return RAID_AND_DROPS_PARSER;
    }

    protected RaidAndDropsParser() {
        super(RaidAndDropsHolder.getInstance());
    }

    public File getXMLFile() {
        return new File(path);
    }

    protected void readData() {
        try {
            InputSource inputSource = new InputSource(new InputStreamReader(new FileInputStream(this.getXMLFile()), this.getEncoding().get()));

            for(Node node = this.getFactory().newDocumentBuilder().parse(inputSource).getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equalsIgnoreCase("list")) {
                    for(Node firstChild = node.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                        if (firstChild.getNodeName().equalsIgnoreCase("spawnChanceGroup")) {
                            float groupChance = Float.parseFloat(firstChild.getAttributes().getNamedItem("groupChance").getNodeValue());
                            RaidType raidType = (RaidType)Enum.valueOf(RaidType.class, firstChild.getAttributes().getNamedItem("type").getNodeValue());
                            RaidGroup raidGroup = new RaidGroup(groupChance, raidType);

                            for(Node firstSubFirstChild = firstChild.getFirstChild(); firstSubFirstChild != null; firstSubFirstChild = firstSubFirstChild.getNextSibling()) {
                                int maxDrop;
                                if (firstSubFirstChild.getNodeName().equalsIgnoreCase("raid")) {
                                    int id = Integer.parseInt(firstSubFirstChild.getAttributes().getNamedItem("id").getNodeValue());
                                    maxDrop = Integer.parseInt(firstSubFirstChild.getAttributes().getNamedItem("maxDrops").getNodeValue());
                                    raidGroup.getRaids().put(id, maxDrop);
                                } else if (firstSubFirstChild.getNodeName().equalsIgnoreCase("dropLists")) {
                                    for(Node secondSubFirstChild = firstSubFirstChild.getFirstChild(); secondSubFirstChild != null; secondSubFirstChild = secondSubFirstChild.getNextSibling()) {
                                        if (secondSubFirstChild.getNodeName().equalsIgnoreCase("item")) {
                                            maxDrop = Integer.parseInt(secondSubFirstChild.getAttributes().getNamedItem("id").getNodeValue());
                                            int min = Integer.parseInt(secondSubFirstChild.getAttributes().getNamedItem("min").getNodeValue());
                                            int max = Integer.parseInt(secondSubFirstChild.getAttributes().getNamedItem("max").getNodeValue());
                                            float chance = Float.parseFloat(secondSubFirstChild.getAttributes().getNamedItem("chance").getNodeValue());
                                            int maxOccurs = Integer.parseInt(secondSubFirstChild.getAttributes().getNamedItem("maxOccurs").getNodeValue());
                                            int minOccurs = Integer.parseInt(secondSubFirstChild.getAttributes().getNamedItem("minOccurs").getNodeValue());
                                            raidGroup.getDrops().add(new RaidDrop(maxDrop, min, max, chance, maxOccurs, minOccurs));
                                        }
                                    }
                                }
                            }

                            ((RaidAndDropsHolder)this.getHolder())._raids.add(raidGroup);
                        }
                    }
                }
            }

            Collections.sort(((RaidAndDropsHolder)this.getHolder())._raids, ((RaidAndDropsHolder)this.getHolder()).compareByChance);
        } catch (Exception var12) {
            log.warn("Raid Engine: Error: " + var12);
            var12.printStackTrace();
        }
    }
}
