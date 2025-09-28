package gabriel.events.weeklyRank;

import gabriel.config.GabConfig;
import gabriel.events.weeklyRank.objects.CicleObject;
import gabriel.events.weeklyRank.objects.ClanRankObject;
import gabriel.events.weeklyRank.objects.PlayerAssistRankObject;
import gabriel.events.weeklyRank.objects.PlayerRankObject;
import gr.sr.utils.db.DbUtils;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class WeeklyManagerDAO {
    private static final Logger _log = LoggerFactory.getLogger(WeeklyManagerDAO.class);
    private List<ClanRankObject> clanList = null;
    private List<PlayerRankObject> charList = null;
    private List<PlayerAssistRankObject> assistList = null;
    private final CicleObject finalRewarders = null;

    protected static WeeklyManagerDAO instance;
    private static final String INSERT_CLAN = "INSERT INTO weekly_rank_clan VALUES (?,?,?) ON DUPLICATE KEY UPDATE clanKills=?";
    private static final String SELECT_CLAN = "SELECT * FROM weekly_rank_clan ORDER BY clanKills";
    private static final String SELECT_CLAN_O = "SELECT * FROM weekly_rank_clan ORDER BY clanKills DESC LIMIT 1";
    private static final String SELECT_CLAN_O_TOP = "SELECT * FROM weekly_rank_clan ORDER BY clanKills DESC LIMIT " + GabConfig.WEEKLYRANK_CLAN_TOP;

    private static final String INSERT_CHAR = "INSERT INTO weekly_rank_player VALUES (?,?,?) ON DUPLICATE KEY UPDATE charKills=?";
    private static final String SELECT_CHAR = "SELECT * FROM weekly_rank_player ORDER BY charKills";
    private static final String SELECT_CHAR_O = "SELECT * FROM weekly_rank_player ORDER BY charKills DESC LIMIT 1";
    private static final String SELECT_CHAR_O_TOP = "SELECT * FROM weekly_rank_player ORDER BY charKills DESC LIMIT " + GabConfig.WEEKLYRANK_PLAYER_TOP;

    private static final String INSERT_CHAR_ASSIST = "INSERT INTO weekly_rank_player_assist VALUES (?,?,?) ON DUPLICATE KEY UPDATE charAssist=?";
    private static final String SELECT_CHAR_ASSIST = "SELECT * FROM weekly_rank_player_assist ORDER BY charAssist";
    private static final String SELECT_CHAR_ASSIST_O = "SELECT * FROM weekly_rank_player_assist ORDER BY charAssist DESC LIMIT 1";
    private static final String SELECT_CHAR_ASSIST_O_TOP = "SELECT * FROM weekly_rank_player_assist ORDER BY charAssist DESC LIMIT " + GabConfig.WEEKLYRANK_PLAYER_TOP_ASSIST;

    private static final String SELECT_LATEST_CICLE = "SELECT cicleId FROM weekly_rank_claim ORDER BY cicleId DESC LIMIT 1";
    private static final String INSERT_RANK_CLAIM = "INSERT INTO weekly_rank_claim VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE clanRank=?, charRank=?, assistRank=?";
    private static final String SELECT_RANK_CLAIM = "SELECT * FROM weekly_rank_claim ORDER BY cicleId DESC LIMIT 1";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS `?_latest`";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS ?_latest LIKE ?";
    private static final String POPULATE_TABLE = "INSERT ?_latest SELECT * FROM ?";
    private static final String TRUNCATE_TABLE_LATEST = "TRUNCATE TABLE ?_latest";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE ?";


    protected static WeeklyManagerDAO getInstance() {
        if (instance == null)
            instance = new WeeklyManagerDAO();
        return instance;
    }

    private WeeklyManagerDAO() {
        clanList = new LinkedList<>();
        charList = new LinkedList<>();
        assistList = new LinkedList<>();
        load();
    }

    protected CicleObject getFinalRewarders() {
        return loadRankClaimTable();
    }

    protected void load() {
        loadClan();
        loadPlayers();
    }

    protected void reset(){
        clanList.clear();
        charList.clear();
        assistList.clear();
    }

    protected void increaseClanKill(L2Clan clan) {
        int clanId = clan.getId();
        ClanRankObject rank = clanList.stream().filter(e -> e.getClanId() == clanId).findFirst().orElse(null);
        if (rank == null) {
            clanList.add(new ClanRankObject(0, clanId, clan.getName(), 1, false));
        } else {
            rank.increaseClanKills();
        }
    }

    protected int getClanKill(int clanId) {
        ClanRankObject clan = getClanObject(clanId);
        return clan == null ? 0 : clan.getClanKills();
    }

    protected ClanRankObject getClanObject(int clanId) {
        return clanList.stream().filter(e -> e.getClanId() == clanId).findFirst().orElse(null);
    }

    protected void increasePlayerKill(L2PcInstance player) {
        int charId = player.getObjectId();
        PlayerRankObject rank = charList.stream().filter(e -> e.getCharId() == charId).findFirst().orElse(null);
        if (rank == null) {
            charList.add(new PlayerRankObject(0, charId, player.getName(), 1, false));
        } else {
            rank.increaseCharKills();
        }
    }

    protected int getPlayerKill(int charId) {
        PlayerRankObject player = getPlayerObject(charId);
        return player == null ? 0 : player.getCharKills();
    }

    protected PlayerRankObject getPlayerObject(int charId) {
        return charList.stream().filter(e -> e.getCharId() == charId).findFirst().orElse(null);
    }

    protected void increasePlayerAssist(L2PcInstance player) {
        int charId = player.getObjectId();
        PlayerAssistRankObject rank = assistList.stream().filter(e -> e.getCharId() == charId).findFirst().orElse(null);
        if (rank == null) {
            assistList.add(new PlayerAssistRankObject(0, charId, player.getName(), 1, false));
        } else {
            rank.increaseCharAssists();
        }
    }

    protected int getPlayerAssist(int charId) {
        PlayerAssistRankObject player = getPlayerAssistObject(charId);
        return player == null ? 0 : player.getCharAssists();
    }

    protected PlayerAssistRankObject getPlayerAssistObject(int charId) {
        return assistList.stream().filter(e -> e.getCharId() == charId).findFirst().orElse(null);
    }

    protected void loadClan() {
        clanList.clear();
        int rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CLAN)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int clanId = rs.getInt("clanId");
                        final String clanName = rs.getString("clanName");
                        final int clanKills = rs.getInt("clanKills");
                        clanList.add(new ClanRankObject(rankPlace, clanId, clanName, clanKills, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed loadClan");
            e.printStackTrace();
        }
    }

    protected void loadPlayers() {
        charList.clear();
        int rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int charId = rs.getInt("charId");
                        final String charName = rs.getString("charName");
                        final int charKills = rs.getInt("charKills");
                        charList.add(new PlayerRankObject(rankPlace, charId, charName, charKills, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed loadPlayers");
            e.printStackTrace();
        }
        assistList.clear();
        rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR_ASSIST)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int charId = rs.getInt("charId");
                        final String charName = rs.getString("charName");
                        final int charAssist = rs.getInt("charAssist");
                        assistList.add(new PlayerAssistRankObject(rankPlace, charId, charName, charAssist, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed loadPlayers");
            e.printStackTrace();
        }
    }

    protected boolean increaseKillClan(L2Clan clan, int amount) {
        boolean ok = false;
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            statement = con.prepareStatement(INSERT_CLAN);
            statement.setInt(1, clan.getId());
            statement.setString(2, clan.getName());
            statement.setInt(3, amount);
            statement.setInt(4, amount);
            statement.executeUpdate();
            statement.close();
            ok = true;
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update increaseKillClan " + clan.getName() + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
        //consistency
        if (ok)
            increaseClanKill(clan);
        return ok;
    }

    protected boolean increaseKillPlayer(L2PcInstance player, int amount) {
        boolean ok = false;
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            statement = con.prepareStatement(INSERT_CHAR);
            statement.setInt(1, player.getObjectId());
            statement.setString(2, player.getName());
            statement.setInt(3, amount);
            statement.setInt(4, amount);
            statement.executeUpdate();
            statement.close();
            ok = true;
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update increaseKillPlayer " + player.getName() + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
        //consistency
        if (ok)
            increasePlayerKill(player);
        return ok;
    }
    protected boolean increaseAssistPlayer(L2PcInstance player, int amount) {
        boolean ok = false;
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            statement = con.prepareStatement(INSERT_CHAR_ASSIST);
            statement.setInt(1, player.getObjectId());
            statement.setString(2, player.getName());
            statement.setInt(3, amount);
            statement.setInt(4, amount);
            statement.executeUpdate();
            statement.close();
            ok = true;
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update increaseKillPlayer " + player.getName() + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
        //consistency
        if (ok)
            increasePlayerAssist(player);
        return ok;
    }

    protected ClanRankObject getTopClan() {
        int clanObjId = 0;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CLAN_O)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        clanObjId = rs.getInt("clanId");
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTopClan");
            e.printStackTrace();
        }
        return getClanObject(clanObjId);
    }

    protected List<ClanRankObject> getTopXClan() {
        List<ClanRankObject> objs = new LinkedList<>();
        int rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CLAN_O_TOP)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int clanId = rs.getInt("clanId");
                        final String clanName = rs.getString("clanName");
                        final int clanKills = rs.getInt("clanKills");
                        objs.add(new ClanRankObject(rankPlace, clanId, clanName, clanKills, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTop10Clan");
            e.printStackTrace();
        }
        return objs;
    }

    protected PlayerRankObject getTopPlayer() {
        int charObjId = 0;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR_O)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        charObjId = rs.getInt("charId");
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTopPlayer");
            e.printStackTrace();
        }
        return getPlayerObject(charObjId);
    }

    protected PlayerAssistRankObject getTopPlayerAssist() {
        int charObjId = 0;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR_ASSIST_O)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        charObjId = rs.getInt("charId");
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTopPlayer");
            e.printStackTrace();
        }
        return getPlayerAssistObject(charObjId);
    }

    protected List<PlayerRankObject> getTopXPlayer() {
        List<PlayerRankObject> objs = new LinkedList<>();
        int rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR_O_TOP)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int charId = rs.getInt("charId");
                        final String charName = rs.getString("charName");
                        final int charKills = rs.getInt("charKills");
                        objs.add(new PlayerRankObject(rankPlace, charId, charName, charKills, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTop10Player");
            e.printStackTrace();
        }
        return objs;
    }

    protected List<PlayerAssistRankObject> getTopXPlayerAssist() {
        List<PlayerAssistRankObject> objs = new LinkedList<>();
        int rankPlace = 1;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_CHAR_ASSIST_O_TOP)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final int charId = rs.getInt("charId");
                        final String charName = rs.getString("charName");
                        final int charAssist = rs.getInt("charAssist");
                        objs.add(new PlayerAssistRankObject(rankPlace, charId, charName, charAssist, false));
                        rankPlace++;
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getTop10Player");
            e.printStackTrace();
        }
        return objs;
    }

    protected int getLatestCicle() {
        int cicle = 0;

        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_LATEST_CICLE)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cicle = rs.getInt("cicleId");
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed getLatestCicle");
            e.printStackTrace();
        }

        return cicle;
    }


    protected void prepareForRewardClan(int cicle) {

        StringBuilder clanString = new StringBuilder();
        StringBuilder playerString = new StringBuilder();
        StringBuilder assist = new StringBuilder();

        if (GabConfig.WEEKLYRANK_ALLOW_CLAN) {
            for (ClanRankObject clanRankObject : getTopXClan()) {
                clanString.append("rank:");
                clanString.append(clanRankObject.getRankPlace());
                clanString.append(";");
                clanString.append("clanId:");
                clanString.append(clanRankObject.getClanId());
                clanString.append(";");
                clanString.append("clanName:");
                clanString.append(clanRankObject.getClanName());
                clanString.append(";");
                clanString.append("clanKills:");
                clanString.append(clanRankObject.getClanKills());
                clanString.append(";");
                clanString.append("received:");
                clanString.append("0");
                clanString.append(";");
                clanString.append("-");
                Broadcast.toAllOnlinePlayers("CLAN: "+clanRankObject.getClanName() + " Rank: "+clanRankObject.getRankPlace()+ " Clan kills: "+clanRankObject.getClanKills());
            }
            dropTableIfExist("weekly_rank_clan");
            createLatestTable("weekly_rank_clan");
            copyTablesToLatest("weekly_rank_clan");
            truncateTable("weekly_rank_clan", false);

        }

        if (GabConfig.WEEKLYRANK_ALLOW_PLAYER) {
            for (PlayerRankObject playerRankObject : getTopXPlayer()) {
                playerString.append("rank:");
                playerString.append(playerRankObject.getRankPlace());
                playerString.append(";");
                playerString.append("charId:");
                playerString.append(playerRankObject.getCharId());
                playerString.append(";");
                playerString.append("charName:");
                playerString.append(playerRankObject.getCharName());
                playerString.append(";");
                playerString.append("charKills:");
                playerString.append(playerRankObject.getCharKills());
                playerString.append(";");
                playerString.append("received:");
                playerString.append("0");
                playerString.append(";");
                playerString.append("-");
                Broadcast.toAllOnlinePlayers("Player: "+playerRankObject.getCharName() + " Rank: "+playerRankObject.getRankPlace()+ " Clan kills: "+playerRankObject.getCharKills());
            }
            dropTableIfExist("weekly_rank_player");
            createLatestTable("weekly_rank_player");
            copyTablesToLatest("weekly_rank_player");
            truncateTable("weekly_rank_player", false);
        }

        if (GabConfig.WEEKLYRANK_ALLOW_PLAYER_ASSIST) {
            for (PlayerAssistRankObject playerRankObject : getTopXPlayerAssist()) {
                assist.append("rank:");
                assist.append(playerRankObject.getRankPlace());
                assist.append(";");
                assist.append("charId:");
                assist.append(playerRankObject.getCharId());
                assist.append(";");
                assist.append("charName:");
                assist.append(playerRankObject.getCharName());
                assist.append(";");
                assist.append("charAssist:");
                assist.append(playerRankObject.getCharAssists());
                assist.append(";");
                assist.append("received:");
                assist.append("0");
                assist.append(";");
                assist.append("-");
                Broadcast.toAllOnlinePlayers("Player Assist: "+playerRankObject.getCharName() + " Rank: "+playerRankObject.getRankPlace()+ " Clan kills: "+playerRankObject.getCharAssists());

            }
            dropTableIfExist("weekly_rank_player_assist");
            createLatestTable("weekly_rank_player_assist");
            copyTablesToLatest("weekly_rank_player_assist");
            truncateTable("weekly_rank_player_assist", false);
        }

        storeRankClaimTable(cicle, clanString.toString(), playerString.toString(), assist.toString());
        reset();
    }

    private void dropTableIfExist(String tableName) {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            String sql = DROP_TABLE.replace("?", tableName);
            statement = con.prepareStatement(sql);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update dropTableIfExist " + tableName + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    private void createLatestTable(String tableName) {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            String sql = CREATE_TABLE.replace("?", tableName);
            statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update createLatestTable " + tableName + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    private void copyTablesToLatest(String tableName) {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            String sql = POPULATE_TABLE.replace("?", tableName);
            statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update copyTablesToLatest " + tableName + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    private void truncateTable(String tableName, boolean latest) {
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;
            String sql = latest ? TRUNCATE_TABLE_LATEST.replace("?", tableName) : TRUNCATE_TABLE.replace("?", tableName);
            statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update truncateTable " + tableName + " to database:" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    private int storeRankClaimTable(int cicle, String clanString, String playerString, String assistString) {

        int ok = 3;
        Connection con = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement statement = null;

            statement = con.prepareStatement(INSERT_RANK_CLAIM);
            statement.setInt(1, cicle);
            statement.setString(2, clanString);
            statement.setString(3, playerString);
            statement.setString(4, assistString);
            statement.setString(5, clanString);
            statement.setString(6, playerString);
            statement.setString(7, assistString);
            statement.executeUpdate();
            statement.close();
            ok = 0;
        } catch (SQLException e) {
            _log.warn("Gabson: Couldn't update prepareForRewardClan" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }

        return ok;
    }

    protected boolean hasClaimed(int objectId, boolean assist){
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_RANK_CLAIM)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String charRank = rs.getString("charRank");
                        String assistRank = rs.getString("assistRank");
                        String clanRank = rs.getString("clanRank");
                        if(assist){
                            String[] splitoted = assistRank.split("-");
                            String toUpdate = "";
                            for (String s : splitoted) {
                                toUpdate = s;
                                String[] charsep = s.split("charId");
                                String charId = charsep[1].split(":")[1].split(";")[0];
                                if (Integer.parseInt(charId) == objectId) {
                                    if (s.contains("received:0")) {
                                        toUpdate = toUpdate.replace("received:0", "received:1");
                                        assistRank = assistRank.replace(s, toUpdate);
                                    } else {
                                        return true;
                                    }
                                    break;
                                }
                            }
                        }else{
                            String[] splitoted = charRank.split("-");
                            String toUpdate = "";
                            for (String s : splitoted) {
                                toUpdate = s;
                                String[] charsep = s.split("charId");
                                String charId = charsep[1].split(":")[1].split(";")[0];
                                if (Integer.parseInt(charId) == objectId) {
                                    if (s.contains("received:0")) {
                                        toUpdate = toUpdate.replace("received:0", "received:1");
                                        charRank = charRank.replace(s, toUpdate);
                                    } else {
                                        return true;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed updateClaimedReward");
            e.printStackTrace();
        }

        return false;

    }

    protected int updateClaimedReward(int objectId, boolean player, boolean assist) {
        int cicleId = 0;
        String clanString = "";
        String playerString = "";
        String assistString = "";
        boolean objIdound = false;

        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_RANK_CLAIM)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cicleId = rs.getInt("cicleId");
                        String charRank = rs.getString("charRank");
                        playerString = charRank;
                        String assistRank = rs.getString("assistRank");
                        assistString = assistRank;
                        String clanRank = rs.getString("clanRank");
                        clanString = clanRank;

                        if (player) {
                            if(assist){
                                String[] splitoted = assistRank.split("-");
                                String toUpdate = "";
                                for (String s : splitoted) {
                                    toUpdate = s;
                                    String[] charsep = s.split("charId");
                                    String charId = charsep[1].split(":")[1].split(";")[0];
                                    if (Integer.parseInt(charId) == objectId) {
                                        objIdound = true;
                                        if (s.contains("received:0")) {
                                            toUpdate = toUpdate.replace("received:0", "received:1");
                                            assistRank = assistRank.replace(s, toUpdate);
                                            assistString = assistRank;
                                        } else {
                                            return 2;
                                        }
                                        break;
                                    }
                                }
                            }else{
                                String[] splitoted = charRank.split("-");
                                String toUpdate = "";
                                for (String s : splitoted) {
                                    toUpdate = s;
                                    String[] charsep = s.split("charId");
                                    String charId = charsep[1].split(":")[1].split(";")[0];
                                    if (Integer.parseInt(charId) == objectId) {
                                        objIdound = true;
                                        if (s.contains("received:0")) {
                                            toUpdate = toUpdate.replace("received:0", "received:1");
                                            charRank = charRank.replace(s, toUpdate);
                                            playerString = charRank;
                                        } else {
                                            return 2;
                                        }
                                        break;
                                    }
                                }
                            }

                        } else {
                            String[] splitoted = clanRank.split("-");
                            String toUpdate = "";
                            for (String s : splitoted) {
                                toUpdate = s;
                                String[] charsep = s.split("clanId");
                                String clanId = charsep[1].split(":")[1].split(";")[0];
                                if (Integer.parseInt(clanId) == objectId) {
                                    objIdound = true;
                                    if (s.contains("received:0")) {
                                        toUpdate = toUpdate.replace("received:0", "received:1");
                                        clanRank = clanRank.replace(s, toUpdate);
                                        clanString = clanRank;
                                    } else {
                                        return 2;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed updateClaimedReward");
            e.printStackTrace();
        }

        if (!objIdound)
            return 1;

        return storeRankClaimTable(cicleId, clanString, playerString, assistString);
    }

    protected CicleObject loadRankClaimTable() {

        Map<Integer, PlayerRankObject> playerRanks = new LinkedHashMap<>();
        Map<Integer, ClanRankObject> clanRanks = new LinkedHashMap<>();
        Map<Integer, PlayerAssistRankObject> assistRanks = new LinkedHashMap<>();
        int cicleId = 0;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(SELECT_RANK_CLAIM)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ClanRankObject clanRankObject = null;
                        PlayerRankObject playerRankObject = null;
                        PlayerAssistRankObject assistRankObject = null;

                        cicleId = rs.getInt("cicleId");
                        final String clanRank = rs.getString("clanRank");

                        String[] clanRankSplit = clanRank.split("-");
                        for (String sClanS : clanRankSplit) {
                            int rank = 0;
                            int clanId = 0;
                            String clanName = "";
                            int clanKills = 0;
                            boolean received = false;

                            String[] subCategories = sClanS.split(";");
                            for (String subCategory : subCategories) {
                                String[] sep = subCategory.split(":");
                                String name = sep[0];
                                String value = sep.length == 1 ? "0" : sep[1];
                                switch (name) {
                                    case "rank":
                                        rank = Integer.parseInt(value);
                                        break;
                                    case "clanId":
                                        clanId = Integer.parseInt(value);
                                        break;
                                    case "clanName":
                                        clanName = value;
                                        break;
                                    case "clanKills":
                                        clanKills = Integer.parseInt(value);
                                        break;
                                    case "received":
                                        received = value.equals("1");
                                        break;
                                }
                            }
                            clanRankObject = new ClanRankObject(rank, clanId, clanName, clanKills, received);
                            clanRanks.put(rank, clanRankObject);

                        }

                        final String charRank = rs.getString("charRank");

                        String[] playerRankSplit = charRank.split("-");
                        for (String sPlayerS : playerRankSplit) {
                            int rank = 0;
                            int charId = 0;
                            String charName = "";
                            int charKills = 0;
                            boolean received = false;

                            String[] subCategories = sPlayerS.split(";");
                            for (String subCategory : subCategories) {
                                String[] sep = subCategory.split(":");
                                String name = sep[0];
                                String value = sep.length == 1 ? "0" : sep[1];
                                switch (name) {
                                    case "rank":
                                        rank = Integer.parseInt(value);
                                        break;
                                    case "charId":
                                        charId = Integer.parseInt(value);
                                        break;
                                    case "charName":
                                        charName = value;
                                        break;
                                    case "charKills":
                                        charKills = Integer.parseInt(value);
                                        break;
                                    case "received":
                                        received = value.equals("1");
                                        break;
                                }
                            }
                            playerRankObject = new PlayerRankObject(rank, charId, charName, charKills, received);
                            playerRanks.put(rank, playerRankObject);

                        }

                        final String assistRank = rs.getString("assistRank");

                        String[] assistRankSplit = assistRank.split("-");
                        for (String sPlayerS : assistRankSplit) {
                            int rank = 0;
                            int charId = 0;
                            String charName = "";
                            int charAssist = 0;
                            boolean received = false;

                            String[] subCategories = sPlayerS.split(";");
                            for (String subCategory : subCategories) {
                                String[] sep = subCategory.split(":");
                                String name = sep[0];
                                String value = sep.length == 1 ? "0" : sep[1];
                                switch (name) {
                                    case "rank":
                                        rank = Integer.parseInt(value);
                                        break;
                                    case "charId":
                                        charId = Integer.parseInt(value);
                                        break;
                                    case "charName":
                                        charName = value;
                                        break;
                                    case "charAssist":
                                        charAssist = Integer.parseInt(value);
                                        break;
                                    case "received":
                                        received = value.equals("1");
                                        break;
                                }
                            }
                            assistRankObject = new PlayerAssistRankObject(rank, charId, charName, charAssist, received);
                            assistRanks.put(rank, assistRankObject);

                        }

                    }
                }
            }
        } catch (Exception e) {
            _log.error("Failed loadRankClaimTable");
            e.printStackTrace();
        }
        return new CicleObject(cicleId, playerRanks, clanRanks, assistRanks);
    }

    protected Integer[] existInRank(int objId, boolean playerRank, boolean assist) {
        CicleObject obj = loadRankClaimTable();
        int rank = -1;
        int found = -1;
        if (playerRank) {
            if(assist){
                for (Map.Entry<Integer, PlayerAssistRankObject> entry : obj.getAssistRanks().entrySet()) {
                    int rankR = entry.getKey();
                    PlayerAssistRankObject objR = entry.getValue();
                    if (objR.getCharId() == objId) {
                        found = 1;
                        rank = rankR;
                    }

                }
            }else{
                for (Map.Entry<Integer, PlayerRankObject> entry : obj.getPlayerRanks().entrySet()) {
                    int rankR = entry.getKey();
                    PlayerRankObject objR = entry.getValue();
                    if (objR.getCharId() == objId) {
                        found = 1;
                        rank = rankR;
                    }

                }
            }


        } else {
            for (Map.Entry<Integer, ClanRankObject> entry : obj.getClanRanks().entrySet()) {
                int rankR = entry.getKey();
                ClanRankObject objR = entry.getValue();
                if (objR.getClanId() == objId) {
                    found = 1;
                    rank = rankR;
                }
            }
        }

        return new Integer[]{rank, found};

    }

}
