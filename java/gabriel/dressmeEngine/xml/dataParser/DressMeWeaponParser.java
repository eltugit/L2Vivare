package gabriel.dressmeEngine.xml.dataParser;

import gabriel.dressmeEngine.data.DressMeWeaponData;
import gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder;
import gr.sr.data.xml.AbstractFileParser;
import l2r.Config;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public final class DressMeWeaponParser extends AbstractFileParser<gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder> {
    private final String WEAPON_FILE_PATH = Config.DATAPACK_ROOT + "/data/xml/sunrise/dressme/weapon.xml";

    private static final DressMeWeaponParser _instance = new DressMeWeaponParser();

    public static DressMeWeaponParser getInstance() {
        return _instance;
    }

    private DressMeWeaponParser() {
        super(DressMeWeaponHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(WEAPON_FILE_PATH);
    }

    @Override
    protected void readData() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);

        File file = getXMLFile();

        try {
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeName().equalsIgnoreCase("list")) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if (d.getNodeName().equalsIgnoreCase("weapon")) {
                            int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
                            String name = d.getAttributes().getNamedItem("name").getNodeValue();
                            String type = d.getAttributes().getNamedItem("type").getNodeValue();

                            Node isBigNode = d.getAttributes().getNamedItem("isBig");
                            Boolean isBig = (isBigNode != null) && Boolean.parseBoolean(isBigNode.getNodeValue());
                            Node isMagicNode = d.getAttributes().getNamedItem("isMagic");
                            Boolean isMagic = (isMagicNode != null) && Boolean.parseBoolean(isMagicNode.getNodeValue());

                            int itemId = 0;
                            long itemCount = 0;

                            for (Node price = d.getFirstChild(); price != null; price = price.getNextSibling()) {
                                if ("price".equalsIgnoreCase(price.getNodeName())) {
                                    itemId = Integer.parseInt(price.getAttributes().getNamedItem("id").getNodeValue());
                                    itemCount = Long.parseLong(price.getAttributes().getNamedItem("count").getNodeValue());
                                }
                            }

                            getHolder().addWeapon(new DressMeWeaponData(id, name, type, isBig, isMagic, itemId, itemCount));
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.warn(getClass().getSimpleName() + ": Error: " + e);
        }
    }
}