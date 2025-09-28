package gabriel.community.specialCraft.xml;

import gabriel.community.specialCraft.model.*;
import gr.sr.data.xml.AbstractFileParser;
import l2r.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class SpecialCraftParser extends AbstractFileParser<SpecialCraftHolder> {
    private static final Logger _log = LoggerFactory.getLogger(SpecialCraftParser.class);

    private static final String PATH = Config.DATAPACK_ROOT + "/data/xml/gabriel/specialCraft.xml";
    protected static SpecialCraftParser instance;

    private SpecialCraftParser() {
        super(SpecialCraftHolder.getInstance());
    }

    public static SpecialCraftParser getInstance() {
        if (instance == null)
            instance = new SpecialCraftParser();
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
            InputSource in = new InputSource(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            in.setEncoding("UTF-8");
            Document doc = factory.newDocumentBuilder().parse(in);
            for (Node node = doc.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getNodeName().equalsIgnoreCase("list")) {
                    for (Node listNode = node.getFirstChild(); listNode != null; listNode = listNode.getNextSibling()) {
                        if (listNode.getNodeName().equalsIgnoreCase("SpecialCraft")) {

                            String category = listNode.getAttributes().getNamedItem("category").getNodeValue();

                            Map<String, SpecialCraftSubCategory> subCategories = new HashMap<>();

                            for (Node subCategoryNode = listNode.getFirstChild(); subCategoryNode != null; subCategoryNode = subCategoryNode.getNextSibling()) {
                                if (subCategoryNode.getNodeName().equalsIgnoreCase("SubCategory")) {
                                    String subCategory = subCategoryNode.getAttributes().getNamedItem("subCategory").getNodeValue();
                                    List<SpecialCraftItem> items = new LinkedList<>();

//                                    items.computeIfAbsent(subCategory, k-> new LinkedList<>());

                                    for (Node itemNode = subCategoryNode.getFirstChild(); itemNode != null; itemNode = itemNode.getNextSibling()) {
                                        if (itemNode.getNodeName().equalsIgnoreCase("Item")) {
                                            int transactionId = Integer.parseInt(itemNode.getAttributes().getNamedItem("transactionId").getNodeValue());
                                            int itemId = Integer.parseInt(itemNode.getAttributes().getNamedItem("itemId").getNodeValue());
                                            List<SpecialCraftIngredient> ingredientList = new LinkedList<>();
                                            List<SpecialCraftProduction> productionList = new LinkedList<>();
                                            for (Node itemReqsNode = itemNode.getFirstChild(); itemReqsNode != null; itemReqsNode = itemReqsNode.getNextSibling()) {
                                                if (itemReqsNode.getNodeName().equalsIgnoreCase("ingredient")) {
                                                    int id = Integer.parseInt(itemReqsNode.getAttributes().getNamedItem("id").getNodeValue());
                                                    long count = Long.parseLong(itemReqsNode.getAttributes().getNamedItem("count").getNodeValue());
                                                    Node enchant = itemReqsNode.getAttributes().getNamedItem("enchantmentLevel");
                                                    int enchantmentLevel = enchant == null? 0 : Integer.parseInt(enchant.getNodeValue());
                                                    ingredientList.add(new SpecialCraftIngredient(id, count, enchantmentLevel));
                                                }

                                                if (itemReqsNode.getNodeName().equalsIgnoreCase("production")) {
                                                    int id = Integer.parseInt(itemReqsNode.getAttributes().getNamedItem("id").getNodeValue());
                                                    long count = Long.parseLong(itemReqsNode.getAttributes().getNamedItem("count").getNodeValue());
                                                    Node enchant = itemReqsNode.getAttributes().getNamedItem("enchantmentLevel");
                                                    int enchantmentLevel = enchant == null? 0 : Integer.parseInt(enchant.getNodeValue());
                                                    double chance = Double.parseDouble(itemReqsNode.getAttributes().getNamedItem("chance").getNodeValue());
                                                    SpecialCraftRank rank = SpecialCraftRank.valueOf(itemReqsNode.getAttributes().getNamedItem("rarity").getNodeValue());
                                                    productionList.add(new SpecialCraftProduction(id, count, enchantmentLevel, chance, rank));
                                                }
                                            }
                                            items.add( new SpecialCraftItem(transactionId, itemId, ingredientList, productionList));
                                        }
                                    }
                                    subCategories.put(subCategory, new SpecialCraftSubCategory(subCategory, items));
                                }
                            }

                            getHolder().addRandomCraft(category, new SpecialCraftCategory(category, subCategories));


                        }


                    }
                }
            }
            for (Map.Entry<String, SpecialCraftCategory> entry : getHolder().getItems().entrySet()) {
                SpecialCraftCategory cat = entry.getValue();
                SpecialCraftSubCategory tempCat = new SpecialCraftSubCategory("All", new LinkedList<>());
                for (SpecialCraftSubCategory value : cat.getSubCategoriesMap().values()) {
                    tempCat.getItems().addAll(value.getItems());
                }
                cat.getSubCategoriesMap().put("All", tempCat);
            }

        } catch (Exception e) {
            e.printStackTrace();
            _log.warn(getClass().getSimpleName() + ": Error: " + e);
        }
    }
}