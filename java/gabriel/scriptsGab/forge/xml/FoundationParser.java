package gabriel.scriptsGab.forge.xml;

import gr.sr.data.xml.AbstractFileParser;
import l2r.Config;
import org.dom4j.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public final class FoundationParser extends AbstractFileParser<FoundationHolder> {
    private static final FoundationParser _instance = new FoundationParser();

    public static FoundationParser getInstance() {
        return _instance;
    }

    private FoundationParser() {
        super(FoundationHolder.getInstance());
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/foundation/foundation.xml");
    }

    @Override
    protected void readData() {
        File file = getXMLFile();
        org.dom4j.Document document;
        Element rootElement = null;
        try {
            InputStream inputstream = new FileInputStream(file);
            super._reader.setValidation(false);

            document = _reader.read(inputstream);
            rootElement = document.getRootElement();
        } catch (Exception e) {
            _log.warn(getClass().getSimpleName() + ": Error: " + e);
        }

        for (Iterator<Element> iterator = rootElement.elementIterator("foundation"); iterator.hasNext(); ) {
            Element foundation = iterator.next();
            int simple = Integer.parseInt(foundation.attributeValue("simple"));
            int found = Integer.parseInt(foundation.attributeValue("found"));

            getHolder().addFoundation(simple, found);
        }
    }
}
