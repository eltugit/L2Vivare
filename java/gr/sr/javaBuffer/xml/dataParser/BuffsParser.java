package gr.sr.javaBuffer.xml.dataParser;

import gr.sr.data.xml.AbstractFileParser;
import gr.sr.javaBuffer.BufferMenuCategories;
import gr.sr.javaBuffer.BuffsInstance;
import gr.sr.javaBuffer.xml.dataHolder.BuffsHolder;
import l2r.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class BuffsParser extends AbstractFileParser<BuffsHolder> {
    private static Logger log = LoggerFactory.getLogger(BuffsParser.class);
    private final String path;
    private static final BuffsParser parser = new BuffsParser();

    public static BuffsParser getInstance() {
        return parser;
    }

    protected BuffsParser() {
        super(BuffsHolder.getInstance());
        this.path = Config.DATAPACK_ROOT + "/data/xml/sunrise/JavaBuffer.xml";
    }

    public File getXMLFile() {
        return new File(this.path);
    }

    protected void readData() {
        try {
            InputSource var1 = new InputSource(new InputStreamReader(new FileInputStream(this.getXMLFile()), this.getEncoding().get()));

            for(Node node = this.getFactory().newDocumentBuilder().parse(var1).getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equalsIgnoreCase("list")) {
                    for(Node firstChild = node.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                        if (firstChild.getNodeName().equalsIgnoreCase("buffs")) {
                            String name = String.valueOf(firstChild.getAttributes().getNamedItem("name").getNodeValue());
                            String category = String.valueOf(firstChild.getAttributes().getNamedItem("category").getNodeValue());
                            String buffId = String.valueOf(firstChild.getAttributes().getNamedItem("buff_id").getNodeValue());
                            String buffLevel = String.valueOf(firstChild.getAttributes().getNamedItem("buff_level").getNodeValue());
                            String customLevel = String.valueOf(firstChild.getAttributes().getNamedItem("custom_level").getNodeValue());
                            String description = String.valueOf(firstChild.getAttributes().getNamedItem("description").getNodeValue());
                            BuffsInstance var11 = new BuffsInstance(Integer.valueOf(buffId), Integer.valueOf(buffLevel), Integer.valueOf(customLevel), name, description, BufferMenuCategories.valueOf(category));
                            ((BuffsHolder)this.getHolder())._buffs.put(var11.getId(), var11);
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Buffer Engine: Error: " + e);
            e.printStackTrace();
        }
    }
}
