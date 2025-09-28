package gabriel.balancer;

import l2r.Config;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class ClassBalanceOly {
    private static Logger LOG = Logger.getLogger(ClassBalanceOly.class.getName());
    static ArrayList<ClassForBalance> _classes;
    static Map<Integer, ClassForBalance> _overall;

    public void load() {
        // _classes.clear();
        // _overall.clear();
        _classes = new ArrayList();
        _overall = new HashMap();
        LOG.info("ClassBalance Oly Started");
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            File file = new File(Config.DATAPACK_ROOT + "/data/xml/gabriel/ClassBalanceOly.xml");
            // LOG.info("ClassBalance array size test 1");
            // LOG.info("ClassBalance _classes size " + _classes.size());
            // LOG.info("ClassBalance _overall size " + _overall.size());
            if (!file.exists()) {
                if (Config.DEBUG) {
                    LOG.info("The augmentation skillmap file is missing.");
                }
                return;
            }
            Document doc = factory.newDocumentBuilder().parse(file);
            int i = 1;
            int ii = 1;
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equals(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        switch (d.getNodeName()) {
                            case "classes":
                                parseClasses(d, ii);
                                ii++;
                                break;
                            case "overall":
                                parseOverall(d, i);
                                i++;
                        }
                    }
                }
            }
            // LOG.info("ClassBalance array size test 2");
            // LOG.info("ClassBalance _classes size " + _classes.size());
            // for (int jj = 0; jj < _classes.size(); jj++)
            // {
            // System.out.println(_classes.get(jj).toString());
            // }
            // LOG.info("ClassBalance _overall size " + _overall.size());
        } catch (Exception e) {
        }
    }

    protected ClassBalanceOly() {
        load();
    }

    private void parseOverall(Node d, int i) {
        NamedNodeMap attrs = d.getAttributes();
        int classid = Integer.parseInt(attrs.getNamedItem("classId").getNodeValue());
        float autDmg = Float.parseFloat(attrs.getNamedItem("physDamage").getNodeValue());
        float magicDmg = Float.parseFloat(attrs.getNamedItem("magicDamage").getNodeValue());
        float skillDmg = Float.parseFloat(attrs.getNamedItem("physSkillDamage").getNodeValue());
        float physCrit = Float.parseFloat(attrs.getNamedItem("physCrit").getNodeValue());
        float magicCrit = Float.parseFloat(attrs.getNamedItem("magicCrit").getNodeValue());
        float blowDamage = Float.parseFloat(attrs.getNamedItem("blowDamage").getNodeValue());
        float backstabDamage = Float.parseFloat(attrs.getNamedItem("backstabDamage").getNodeValue());
        _overall.put(Integer.valueOf(classid), new ClassForBalance(i, classid, 0, autDmg, magicDmg, skillDmg, physCrit, magicCrit, backstabDamage, blowDamage));
    }

    private void parseClasses(Node d, int i) {
        NamedNodeMap attrs = d.getAttributes();
        int firstClass = Integer.parseInt(attrs.getNamedItem("firstClass").getNodeValue());
        int secondClass = Integer.parseInt(attrs.getNamedItem("secondClass").getNodeValue());
        float autDmg = Float.parseFloat(attrs.getNamedItem("physDamage").getNodeValue());
        float magicDmg = Float.parseFloat(attrs.getNamedItem("magicDamage").getNodeValue());
        float skillDmg = Float.parseFloat(attrs.getNamedItem("physSkillDamage").getNodeValue());
        float physCrit = Float.parseFloat(attrs.getNamedItem("physCrit").getNodeValue());
        float magicCrit = Float.parseFloat(attrs.getNamedItem("magicCrit").getNodeValue());
        float blowDamage = Float.parseFloat(attrs.getNamedItem("blowDamage").getNodeValue());
        float backstabDamage = Float.parseFloat(attrs.getNamedItem("backstabDamage").getNodeValue());
        _classes.add(new ClassForBalance(i, firstClass, secondClass, autDmg, magicDmg, skillDmg, physCrit, magicCrit, backstabDamage, blowDamage));
    }

    public static ClassBalanceOly getInstance() {
        return SingletonHolder._instance;
    }

    public ClassForBalance getMultiplier(int a, int s) {
        for (ClassForBalance aaa : _classes) {
            if ((a == aaa.getFirstClass()) && (s == aaa.getSecondClass())) {
                return aaa;
            }
        }
        return null;
    }

    public ClassForBalance getOverallMultiplier(int a) {
        return _overall.get(Integer.valueOf(a));
    }

    public boolean existInOverallList(int a) {
        return _overall.containsKey(Integer.valueOf(a));
    }

    public boolean existInBalanceList(int s, int a) {
        for (ClassForBalance aaa : _classes) {
            if ((s == aaa.getFirstClass()) && (a == aaa.getSecondClass())) {
                return true;
            }
        }
        return false;
    }

    private static class SingletonHolder {
        protected static final ClassBalanceOly _instance = new ClassBalanceOly();
    }
}