package gr.sr.main;


import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.SmartCommunityConfigs;
import gr.sr.dataHolder.PlayersTopData;
import gr.sr.javaBuffer.xml.dataParser.BuffsParser;
import gr.sr.utils.Tools;
import l2r.L2DatabaseFactory;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.model.L2Clan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TopListsLoader {
    private static final Logger log = LoggerFactory.getLogger(TopListsLoader.class);
    private static Map<Integer, Integer[]> teleportData = new ConcurrentHashMap();
    private static List<PlayersTopData> pvpData = new ArrayList();
    private static List<PlayersTopData> pkData = new ArrayList();
    private static List<PlayersTopData> topCurrencyData = new ArrayList();
    private static List<PlayersTopData> clanData = new ArrayList();
    private static List<PlayersTopData> onlineTimeData = new ArrayList();
    private static String lastUpdate = "N/A";
    private static Long lastUpdateInMs = 0L;

    public TopListsLoader() {
        BuffsParser.getInstance().load();
        loadTeleportData();
        this.loadPvp();
        this.loadPk();
        this.loadTopCurrency();
        this.loadClan();
        this.loadOnlineTime();
        long time;
        String lastUpdate = Tools.convertHourToString(time = System.currentTimeMillis());
        this.setLastUpdate(lastUpdate);
        this.setLastUpdateInMs(time);
    }

    public static void loadTeleportData() {
        teleportData.clear();
        try(Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT id, x, y, z, onlyForNoble, itemIdToGet, teleportPrice FROM custom_teleports")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    int id = rs.getInt("id");
                    Integer[] data;
                    (data = new Integer[6])[0] = rs.getInt("x");
                    data[1] = rs.getInt("y");
                    data[2] = rs.getInt("z");
                    data[3] = rs.getInt("onlyForNoble");
                    data[4] = rs.getInt("itemIdToGet");
                    data[5] = rs.getInt("teleportPrice");
                    teleportData.put(id, data);
                }
            }
        } catch (Exception e) {
            log.error(TopListsLoader.class.getSimpleName() + ": Error while loading teleport data", e);
        }
        log.info("Loaded " + teleportData.keySet().size() + " teleports for the Custom Npcs-Items.");
    }

    public void loadPvp() {
        pvpData.clear();

        try(Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT char_name,clanid,pvpkills FROM characters where accesslevel = 0 order by pvpkills DESC LIMIT 20;")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String name = "No Clan";
                    int clanId = rs.getInt("clanid");
                    int pvpKills = rs.getInt("pvpkills");
                    String charName = rs.getString("char_name");
                    L2Clan clan;
                    if (clanId != 0 && (clan = ClanTable.getInstance().getClan(clanId)) != null) {
                        name = clan.getName();
                    }

                    PlayersTopData data = new PlayersTopData(charName, name, pvpKills, 0, 0L, 0, 0);
                    pvpData.add(data);
                }
            }
        } catch (Exception e) {
            log.error(TopListsLoader.class.getSimpleName() + ": Error while loading top pvp", e);
        }

    }

    public void loadPk() {
        pkData.clear();

        try(Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT char_name,clanid,pkkills FROM characters where accesslevel = 0 order by pkkills DESC LIMIT 20;")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String clanName = "No Clan";
                    int clanId = rs.getInt("clanid");
                    int pkKills = rs.getInt("pkkills");
                    String charName = rs.getString("char_name");
                    L2Clan clan;
                    if (clanId != 0 && (clan = ClanTable.getInstance().getClan(clanId)) != null) {
                        clanName = clan.getName();
                    }
                    PlayersTopData playersTopData = new PlayersTopData(charName, clanName, 0, pkKills, 0L, 0, 0);
                    pkData.add(playersTopData);
                }
            }
        } catch (Exception e) {
            log.error(TopListsLoader.class.getSimpleName() + ": Error while loading top pk", e);
        }

    }

    public void loadTopCurrency() {
        topCurrencyData.clear();

        try(Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT owner_id,count FROM items WHERE item_id = " + String.valueOf(SmartCommunityConfigs.TOP_CURRENCY_ID) + " AND loc = 'INVENTORY' order by count DESC")){
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ownerId = rs.getString("owner_id");
                    long count = rs.getLong("count");
                    PreparedStatement ps2 = connection.prepareStatement("SELECT char_name,clanid,online,classid FROM characters WHERE accesslevel = 0 and charId=" + ownerId);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            String clanName = "No Clan";
                            String charName = rs2.getString("char_name");
                            PlayersTopData var117 = new PlayersTopData(charName, clanName, 0, 0, count, 0, 0);
                            topCurrencyData.add(var117);
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(this.getClass() + ": Could not load top currency.", e);
        }
    }

    public void loadClan() {
        clanData.clear();

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT clan_id,clan_name,clan_level FROM clan_data order by clan_level DESC LIMIT 20;")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String clanLeader = null;
                    String clanName = rs.getString("clan_name");
                    int clanId = rs.getInt("clan_id");
                    int clanLevel = rs.getInt("clan_level");
                    L2Clan clan;
                    if (clanId != 0 && (clan = ClanTable.getInstance().getClan(clanId)) != null) {
                        clanLeader = clan.getLeaderName();
                    }
                    PlayersTopData var55 = new PlayersTopData(clanLeader, clanName, 0, 0, 0L, clanLevel, 0);
                    clanData.add(var55);
                }
            }
        } catch (Exception var53) {
            log.error(TopListsLoader.class.getSimpleName() + ": Error while loading top clan", var53);
        }

    }

    public void loadOnlineTime() {
        onlineTimeData.clear();

        try(Connection connection = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = connection.prepareStatement("SELECT char_name,clanid,onlinetime FROM characters where accesslevel = 0 order by onlinetime DESC LIMIT 20;")){
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String clanName = "No Clan";
                    int clanId = rs.getInt("clanid");
                    int onlineTime = rs.getInt("onlinetime");
                    String charName = rs.getString("char_name");
                    L2Clan clan;
                    if (clanId != 0 && (clan = ClanTable.getInstance().getClan(clanId)) != null) {
                        clanName = clan.getName();
                    }

                    PlayersTopData var55 = new PlayersTopData(charName, clanName, 0, 0, 0L, 0, onlineTime);
                    onlineTimeData.add(var55);
                }
            }
        } catch (Exception var53) {
            log.error(TopListsLoader.class.getSimpleName() + ": Error while loading top online time", var53);
        }

    }


    public List<PlayersTopData> getTopPvp() {
        return pvpData;
    }


    public List<PlayersTopData> getTopPk() {
        return pkData;
    }


    public List<PlayersTopData> getTopCurrency() {
        return topCurrencyData;
    }


    public List<PlayersTopData> getTopClan() {
        return clanData;
    }


    public List<PlayersTopData> getTopOnlineTime() {
        return onlineTimeData;
    }


    public Integer[] getTeleportInfo(int index) {
        return (Integer[]) teleportData.get(index);
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        TopListsLoader.lastUpdate = lastUpdate;
    }

    public Long getLastUpdateInM() {
        return lastUpdateInMs;
    }

    public void setLastUpdateInMs(long time) {
        lastUpdateInMs = time;
    }

    public String getNextUpdate() {
        String nextUpdate = "N/A";
        if (lastUpdateInMs > 0L) {
            nextUpdate = Tools.convertMinuteToString(lastUpdateInMs + (long)(CustomServerConfigs.TOP_LISTS_RELOAD_TIME * 60 * 1000) - System.currentTimeMillis());
        }

        return nextUpdate;
    }

    protected static TopListsLoader instance;


    public static TopListsLoader getInstance() {
        if (instance == null)
            instance = new TopListsLoader();
        return instance;
    }

}
