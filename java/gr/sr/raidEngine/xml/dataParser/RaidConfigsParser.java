package gr.sr.raidEngine.xml.dataParser;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.raidEngine.RaidConfigs;
import gr.sr.raidEngine.xml.dataHolder.RaidConfigsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class RaidConfigsParser extends AbstractFileParser<RaidConfigsHolder> {
    private static Logger log = LoggerFactory.getLogger(RaidConfigsParser.class);
    private final String path = "./config/sunrise/event/raidEngine/configs.xml";
    private static final RaidConfigsParser RAID_CONFIGS_PARSER = new RaidConfigsParser();

    public static RaidConfigsParser getInstance() {
        return RAID_CONFIGS_PARSER;
    }

    protected RaidConfigsParser() {
        super(RaidConfigsHolder.getInstance());
    }

    public File getXMLFile() {
        return new File(path);
    }

    protected void readData() {
        try {
            InputSource inputSource = new InputSource(new InputStreamReader(new FileInputStream(this.getXMLFile()), this.getEncoding().get()));

            for(Node firstChild = this.getFactory().newDocumentBuilder().parse(inputSource).getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                if (firstChild.getNodeName().equalsIgnoreCase("list")) {
                    for(Node secondChild = firstChild.getFirstChild(); secondChild != null; secondChild = secondChild.getNextSibling()) {
                        if (secondChild.getNodeName().equalsIgnoreCase("config")) {
                            boolean enabled = Boolean.parseBoolean(secondChild.getAttributes().getNamedItem("ENABLED").getNodeValue());
                            boolean daily = Boolean.parseBoolean(secondChild.getAttributes().getNamedItem("DAILY").getNodeValue());
                            long duration = (long)(Integer.parseInt(secondChild.getAttributes().getNamedItem("DURATION").getNodeValue()) * 60 * 1000);
                            long notify_delay = (long)(Integer.parseInt(secondChild.getAttributes().getNamedItem("NOTIFY_DELAY").getNodeValue()) * 60 * 1000);
                            int day = Integer.parseInt(secondChild.getAttributes().getNamedItem("DAY").getNodeValue());
                            int hour = Integer.parseInt(secondChild.getAttributes().getNamedItem("HOUR").getNodeValue());
                            int minute = Integer.parseInt(secondChild.getAttributes().getNamedItem("MINUTE").getNodeValue());
                            int random_minute = Integer.parseInt(secondChild.getAttributes().getNamedItem("RANDOM_MINUTE").getNodeValue());
                            ((RaidConfigsHolder)this.getHolder())._configs.add(new RaidConfigs(enabled, duration, notify_delay, daily, day, hour, minute, random_minute));
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
