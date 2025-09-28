package gabriel.interServerExchange;

import gabriel.scriptsGab.utils.ItemFunctions;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.L2Augmentation;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class InterServerExchangeManager {
    protected static InterServerExchangeManager instance;

    private static final String INSERT_SQL = "INSERT INTO gabriel_inter_change (account,itemid,count, enchant, augAttributes, elements) VALUE (?,?,?,?,?,?)";
    private static final String UPDATE_SQL = "UPDATE gabriel_inter_change SET count =? WHERE account = ? AND itemid = ?";
    private static final String DELETE_SQL = "DELETE FROM gabriel_inter_change WHERE account = ? AND itemid = ? AND count = ? AND enchant = ? AND augAttributes = ? AND elements = ?";
    private static final String SELECT_SQL = "SELECT * FROM gabriel_inter_change WHERE account = ?";
    private static final String SELECT_SQL2 = "SELECT * FROM gabriel_inter_change WHERE account = ? AND itemid = ? AND enchant = 0 AND augAttributes = -1";

    public static InterServerExchangeManager getInstance() {
        if (instance == null)
            instance = new InterServerExchangeManager();
        return instance;
    }

    private InterServerExchangeManager() {
        ISEConfig.getInstance();
    }
    //gabriel_inter_change account itemid count
    public List<L2ItemInstance> getItems(L2PcInstance player) {

        List<L2ItemInstance> items = new LinkedList<>();
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL)
        ) {
            statement.setString(1, player.getAccountName());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int itemid = rset.getInt("itemid");

                    if(ISEConfig.BLOCKED_ITEMS.stream().anyMatch(e->e == itemid))
                        continue;

                    long count = rset.getLong("count");
                    int enchant = rset.getInt("enchant");

                    int augments = rset.getInt("augAttributes");

                    String elements = rset.getString("elements");

                    boolean isItemOk = true;

                    if(!ISEConfig.ALLOW_AUG && augments != -1)
                        isItemOk = false;
                    if(!ISEConfig.ALLOW_ELEMENTALS && !elements.isEmpty())
                        isItemOk = false;
                    if(!ISEConfig.ALLOW_ENCHANT && enchant > 0)
                        isItemOk = false;

                    if(!isItemOk)
                        continue;

                    L2ItemInstance item = ItemFunctions.createItem(itemid);
                    if(item == null){
                        System.out.println("ITEM DOES NOT EXIST IN THIS SERVER: "+itemid);
                        continue;
                    }
                    item.setCount(count);
                    if(enchant > 0)
                        item.setEnchantLevel(enchant);

                    if(augments > 0){
                        L2Augmentation _augmentation = new L2Augmentation(augments);
                        item.setAugmentation(_augmentation);
                    }

                    if(!elements.isEmpty()) {
                        for (String s : elements.split(";")) {
                            if (s.isEmpty())
                                continue;
                            byte type = Byte.parseByte(s.split("-")[0]);
                            int value = Integer.parseInt(s.split("-")[1]);
                            item.applyAttribute(type, value);
                        }
                    }
                    L2World.getInstance().storeObject(item);
                    items.add(item);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public long getCount(L2PcInstance player, int itemId) {
        try (Connection con = ISEDatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL2)
        ) {
            statement.setString(1, player.getAccountName());
            statement.setInt(2, itemId);
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int itemid = rset.getInt("itemid");

                    if(ISEConfig.BLOCKED_ITEMS.stream().anyMatch(e->e == itemid))
                        continue;

                    return rset.getLong("count");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void add(L2PcInstance player, L2ItemInstance item){
        long existingCount = 0;
        if(item.isEtcItem()){
            existingCount = getCount(player, item.getId());
        }
        if(existingCount > 0){
            update(player, item.getId(), item.getCount() + existingCount, true);
            return;
        }
        try (Connection con = ISEDatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(INSERT_SQL))
        {
            stmt.setString(1, player.getAccountName());
            stmt.setInt(2, item.getId());
            stmt.setLong(3, item.getCount());
            stmt.setInt(4, item.getEnchantLevel());
            stmt.setInt(5, item.getAugmentation() == null ? -1 : item.getAugmentation().getAttributes());
            StringBuilder elements = new StringBuilder();
            if(item.getElementals() != null) {
                for (Elementals elemental : item.getElementals()) {
                    elements.append(elemental.getElement());
                    elements.append("-");
                    elements.append(elemental.getValue());
                    elements.append(";");
                }
            }
            stmt.setString(6, elements.toString());
            stmt.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void update(L2PcInstance player, int itemId, long count, boolean insert){
        try (Connection con = (insert? ISEDatabaseFactory.getInstance() : L2DatabaseFactory.getInstance()).getConnection();
             PreparedStatement stmt = con.prepareStatement(UPDATE_SQL))
        {
            stmt.setLong(1, count);
            stmt.setString(2, player.getAccountName());
            stmt.setInt(3, itemId);
            stmt.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void delete(L2PcInstance player, L2ItemInstance item){
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(DELETE_SQL))
        {
            stmt.setString(1, player.getAccountName());
            stmt.setInt(2, item.getId());
            stmt.setLong(3, item.getCount());
            stmt.setLong(4, item.getEnchantLevel());
            stmt.setInt(5, item.getAugmentation() == null ? -1 : item.getAugmentation().getAttributes());
            StringBuilder elements = new StringBuilder();
            if(item.getElementals() != null) {
                for (Elementals elemental : item.getElementals()) {
                    elements.append(elemental.getElement());
                    elements.append("-");
                    elements.append(elemental.getValue());
                    elements.append(";");
                }
            }
            stmt.setString(6, elements.toString());
            stmt.execute();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
