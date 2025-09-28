package gabriel.others;


import gr.sr.utils.db.DbUtils;
import javolution.util.FastMap;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;


public class CharCustomHeroTable {
    private static final Logger _log = LoggerFactory.getLogger(CharCustomHeroTable.class);

    private FastMap<Integer, CharCustomContainer> _pcHero = new FastMap<Integer, CharCustomContainer>();
    private static String QRY_SELECT = "SELECT cch.charId, cch.hero, cch.hero_reg_time, cch.hero_time FROM character_custom_hero AS cch";
    private static String QRY_INSERT = "REPLACE INTO character_custom_hero VALUES (?,?,?,?)";
    private static String QRY_DELETE = "DELETE FROM character_custom_hero WHERE charId = ?";


    public CharCustomHeroTable() {
        Connection con = null;
        try {
            Vector<Integer> deleteCharIds = new Vector<Integer>();

            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement ps = con.prepareStatement(QRY_SELECT);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer charId = rs.getInt("charId");
                int hero = rs.getInt("hero");
                ;
                long hero_reg_time = rs.getLong("hero_reg_time");
                long hero_time = rs.getLong("hero_time");

                // Hero
                if ((hero_time == 0) || (hero_reg_time + hero_time > System.currentTimeMillis()))
                    _pcHero.put(charId, new CharCustomContainer(hero, hero_reg_time, hero_time));
                else
                    deleteCharIds.add(charId);
            }
            ps.close();
            rs.close();

            for (Integer deleteCharId : deleteCharIds) {
                PreparedStatement psDel = con.prepareStatement(QRY_DELETE);
                psDel.setInt(1, deleteCharId);
                psDel.executeUpdate();
                psDel.close();
            }

            _log.info("CharCustomTable: Loaded Hero: " + _pcHero.size() + " Expired/Deleted: " + deleteCharIds.size());
            deleteCharIds.clear();
        } catch (Exception e) {
            _log.warn("Could not load char custom: " + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    /**
     * Returns the instance of this class, assign a new object to _instance if it's null
     *
     * @return CharCustomTable
     */

    public static CharCustomHeroTable getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Sets the name color of the L2PcInstance if it name is on the list
     *
     * @param activeChar
     */

    public synchronized String process(L2PcInstance activeChar) {
        CharCustomContainer heroContainer = _pcHero.get(activeChar.getObjectId());
        if (heroContainer == null)
            return null;
        long time = heroContainer.getTime();
        long regTime = heroContainer.getRegTime();
        long currentTime = System.currentTimeMillis();
        String msg = "";

        if ((time == 0) || (regTime + time > currentTime)) {
            if (heroContainer.getValue() == 1) {
                activeChar.setHero(true);
                if (time != 0)
                    activeChar.sendMessage("[HERO]: Your hero will be removed in " + String.valueOf(((regTime + time) - currentTime) / (86400 * 1000)) + " day(s)!");
            } else {
                activeChar.setHero(false);
                activeChar.sendMessage("[HERO]: Hero was removed!");
            }
            activeChar.broadcastUserInfo();
        } else {
            delete(activeChar.getObjectId());
            activeChar.sendMessage("[HERO]: Hero was removed!");
        }
        return msg;
    }

    /**
     * Adds the name of the L2PcInstance to the list with the hero values
     *
     * @param activeChar
     * @param hero
     * @param regTime
     * @param time
     */

    public synchronized void add(L2PcInstance activeChar, int hero, long regTime, long time) {
        Integer charId = activeChar.getObjectId();
        CharCustomContainer heroContainer = _pcHero.get(charId);

        if (heroContainer != null)
            if (!delete(charId))
                return;

        Connection con = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement psIns = con.prepareStatement(QRY_INSERT);
            psIns.setInt(1, charId);
            psIns.setInt(2, hero);
            psIns.setLong(3, regTime);
            psIns.setLong(4, time);
            psIns.executeUpdate();
            psIns.close();
            _pcHero.put(charId, new CharCustomContainer(hero, regTime, time));
            activeChar.setHero(true);
            activeChar.broadcastUserInfo();
        } catch (Exception e) {
            _log.warn("CharCustomHeroTable: Error while add " + charId + " as hero to DB!" + e.getMessage(), e);
        } finally {
            DbUtils.close(con);
        }
    }

    /**
     * Returns true if the name is deleted successfully from list, otherwise false Deletes the name from the list
     *
     * @param charId
     * @return boolean
     */
    public synchronized boolean delete(Integer charId) {
        CharCustomContainer heroContainer = _pcHero.get(charId);
        if (heroContainer == null)
            return false;

        heroContainer = null;
        Connection con = null;

        try {
            con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement psDel = con.prepareStatement(QRY_DELETE);
            psDel.setInt(1, charId);
            psDel.executeUpdate();
            psDel.close();
            _pcHero.remove(charId);
        } catch (Exception e) {
            _log.warn("CharCustomHeroTable: Error while delete " + charId + " as hero to DB!" + e.getMessage(), e);
            return false;
        } finally {
            DbUtils.close(con);
        }
        return true;
    }

    /**
     * Returns true if the name is deleted successfully from list, otherwise false Deletes the name from the list
     *
     * @param activeChar
     * @return boolean
     */

    public synchronized boolean delete(L2PcInstance activeChar) {
        Integer charId = activeChar.getObjectId();
        CharCustomContainer heroContainer = _pcHero.get(charId);
        if (heroContainer == null)
            return false;

        heroContainer = null;

        if (!delete(charId))
            return false;
        activeChar.setHero(false);
        activeChar.broadcastUserInfo();

        return true;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final CharCustomHeroTable _instance = new CharCustomHeroTable();
    }
}
