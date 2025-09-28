package gr.sr.events.engine.lang;

import gr.sr.events.SunriseLoader;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public final class LanguageEngine {
    private static final String DIRECTORY = "config/sunrise/event/language";
    private static Map<String, String> _msgMap = new LinkedHashMap<>();
    private static Map<String, String> _languages = new LinkedHashMap<>();
    private static String _currentLang = "en";

    public static void init() {
        try {
            prepare();
            load();
        } catch (Exception e) {
            SunriseLoader.debug("Error while loading language files", Level.SEVERE);
            e.printStackTrace();
        }
    }

    public static void prepare() {
        File folder = new File("config/sunrise/event/language");
        if (!folder.exists() || folder.isDirectory()) {
            folder.mkdir();
        }
    }

    public static void load() {
        File dir = new File("config/sunrise/event/language");
        for (File file : dir.listFiles(pathname -> pathname.getName().endsWith(".xml"))) {
            if (file.getName().startsWith("event_lang_")) {
                loadXml(file, file.getName().substring(11, file.getName().indexOf(".xml")));
            }
        }
        SunriseLoader.debug("Loaded " + _languages.size() + " languages.");
    }

    private static void loadXml(File file, String lang) {
        int count = 0;
        String version = "";
        String langName = "";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);
        Document doc = null;
        if (file.exists()) {
            try {
                doc = factory.newDocumentBuilder().parse(file);
            } catch (Exception e) {
                SunriseLoader.debug("Could not load language file for event engine - " + lang, Level.WARNING);
            }
            if (doc != null) {
                Node n = doc.getFirstChild();
                NamedNodeMap docAttr = n.getAttributes();
                if (docAttr.getNamedItem("version") != null) {
                    version = docAttr.getNamedItem("version").getNodeValue();
                }
                if (docAttr.getNamedItem("lang") != null) {
                    langName = docAttr.getNamedItem("lang").getNodeValue();
                }
                if (version != null) {
                    SunriseLoader.debug("Processing " + lang + " language -  version " + version, Level.INFO);
                    if (!version.equals("2.21")) {
                        SunriseLoader.debug("Language file for language " + lang + " is not up-to-date with latest version of the engine (" + "2.21" + "). Some newly added messages might not be translated.", Level.WARNING);
                    }
                }
                if (!_languages.containsKey(lang)) {
                    _languages.put(lang, langName);
                }
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if (d.getNodeName().equals("message")) {
                        NamedNodeMap attrs = d.getAttributes();
                        String id = attrs.getNamedItem("id").getNodeValue();
                        String text = attrs.getNamedItem("text").getNodeValue();
                        _msgMap.put(lang + "_" + id, text);
                        count++;
                    }
                }
            }
        }
        SunriseLoader.debug("Loaded " + lang + " language with " + count + " messages.", Level.INFO);
    }

    public static String getMsgByLang(String lang, String id) {
        String msg = _msgMap.get(lang + "_" + id);
        if (msg == null) {
            msg = _msgMap.get("en_" + id);
        }
        if (msg == null) {
            SunriseLoader.debug("No Msg found: ID " + id + " lang = " + lang, Level.WARNING);
        }
        return msg;
    }

    public static String getMsg(String id) {
        String lang = getLanguage();
        if (lang == null) {
            lang = "en";
        }
        return getMsgByLang(lang, id);
    }

    public static String getMsg(String id, Object... obs) {
        String msg = getMsg(id);
        return fillMsg(msg, obs);
    }

    public static String fillMsg(String msg, Object... obs) {
        String newMsg = msg;
        for (Object o : obs) {
            if (o instanceof Integer || o instanceof Long) {
                int first = newMsg.indexOf("%i");
                if (first != -1) {
                    if (o instanceof Integer) {
                        newMsg = newMsg.replaceFirst("%i", ((Integer) o).toString());
                    } else {
                        newMsg = newMsg.replaceFirst("%i", ((Long) o).toString());
                    }
                }
            } else if (o instanceof Double) {
                int first = newMsg.indexOf("%d");
                if (first != -1) {
                    newMsg = newMsg.replaceFirst("%d", ((Double) o).toString());
                }
            } else if (o instanceof String) {
                int first = newMsg.indexOf("%s");
                if (first != -1) {
                    newMsg = newMsg.replaceFirst("%s", (String) o);
                }
            }
        }
        return newMsg;
    }

    public static void setLanguage(String lang) {
        _currentLang = lang;
    }

    public static String getLanguage() {
        return _currentLang;
    }

    public static Map<String, String> getLanguages() {
        return _languages;
    }
}


