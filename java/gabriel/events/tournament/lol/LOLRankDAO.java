package gabriel.events.tournament.lol;

import gr.sr.utils.db.DbUtils;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class LOLRankDAO {
    protected static LOLRankDAO instance;
    private List<LOLPlayerRank> generalRank = new LinkedList<>();
    private final String SELECT_TOURNAMENTS_GENERAL = "SELECT * FROM tournament_lol";
    private final String UPDATE_TOURNAMENTS_GENERAL = "INSERT INTO tournament_lol VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE ownerName=?,eloSoloDuo=?,eloTeams=?";
    private final String TRUNCATE_TABLE = "TRUNCATE TABLE tournament_lol";
    private final int AUTO_UPDATE = 5 * 60 * 1000;

    public static LOLRankDAO getInstance() {
        if (instance == null)
            instance = new LOLRankDAO();
        return instance;
    }

    public List<LOLPlayerRank> getListTopRank(boolean team){
        List<LOLPlayerRank> temp = new LinkedList<>(generalRank);
        temp.sort(Comparator.comparingInt(e->e.getElo(team)));
        Collections.reverse(temp);
        temp = temp.stream().limit(10).collect(Collectors.toList());
        return temp;
    }

    private LOLRankDAO() {
        init();
        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::updateData, AUTO_UPDATE, AUTO_UPDATE);
    }

    public int getPlayerElo(L2PcInstance player, boolean team){
        if(getGeneralRankInfoForObjId(player.getObjectId()) == null)
            generalRank.add(new LOLPlayerRank(player.getObjectId(), player.getName(), 0,0));

        return generalRank.stream().filter(e->e.getOwnerId() == player.getObjectId()).findFirst().get().getElo(team);
    }

    public void incrementWins(L2PcInstance player, int elo, boolean team){
        if(getGeneralRankInfoForObjId(player.getObjectId()) == null)
            generalRank.add(new LOLPlayerRank(player.getObjectId(), player.getName(), 0,0));

        getGeneralRankInfoForObjId(player.getObjectId()).incrementEloBy(elo, team);
    }

    public void incrementLose(L2PcInstance player, int elo, boolean team){
        if(getGeneralRankInfoForObjId(player.getObjectId()) == null)
            generalRank.add(new LOLPlayerRank(player.getObjectId(), player.getName(), 0,0));

        getGeneralRankInfoForObjId(player.getObjectId()).declineEloBy(elo, team);
    }

    public LOLPlayerRank getGeneralRankInfoForObjId(int objId){
        return generalRank.stream().filter(e->e.getOwnerId() == objId).findFirst().orElse(null);
    }

    private void init() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_TOURNAMENTS_GENERAL);
            rset = statement.executeQuery();

            while (rset.next()) {
                int objId = rset.getInt("ownerId");
                String objName = rset.getString("ownerName");
                int eloSoloDuo = rset.getInt("eloSoloDuo");
                int eloTeams = rset.getInt("eloTeams");
                generalRank.add(new LOLPlayerRank(objId, objName, eloSoloDuo, eloTeams));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    private void updateData(){
        for (LOLPlayerRank rankInfo : generalRank) {
           update(rankInfo);
        }
    }

    public void update(LOLPlayerRank rankInfo){
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_TOURNAMENTS_GENERAL))
        {
            statement.setInt(1, rankInfo.getOwnerId());
            statement.setString(2, rankInfo.getOwnerName());
            statement.setInt(3, rankInfo.getElo(false));
            statement.setInt(4, rankInfo.getElo(true));

            statement.setString(5, rankInfo.getOwnerName());
            statement.setInt(6, rankInfo.getElo(false));
            statement.setInt(7, rankInfo.getElo(true));
            statement.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void truncateTable(){
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            String sql = TRUNCATE_TABLE;
            statement = con.prepareStatement(sql);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {

        } finally {
            DbUtils.closeQuietly(con, statement);

        }
    }
}
