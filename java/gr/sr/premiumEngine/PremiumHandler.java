package gr.sr.premiumEngine;


import gr.sr.configsEngine.configs.impl.PremiumServiceConfigs;
import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


public class PremiumHandler {
    public static final Logger _log = LoggerFactory.getLogger(PremiumHandler.class);
    private static long endDate;

    public PremiumHandler() {
    }

    
    public static long getPremServiceData(String accountName) {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT premium_service,enddate FROM characters_premium WHERE account_name=?")){
            {
                ps.setString(1, accountName);
                try (ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        if (PremiumServiceConfigs.USE_PREMIUM_SERVICE) {
                            endDate = rs.getLong("enddate");
                        }

                    }
                }
            }
        } catch (Exception e) {
            _log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data: " + e);
        }

        return endDate;
    }

    
    public static void restorePremServiceData(L2PcInstance player, String accountName) {
        boolean found = false;

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT premium_service,enddate FROM characters_premium WHERE account_name=?");
             PreparedStatement ps2 = connection.prepareStatement("INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?")){

                ps.setString(1, accountName);
                try (ResultSet rs = ps.executeQuery())
                {
                    while (rs.next())
                    {
                        found = true;
                        if (PremiumServiceConfigs.USE_PREMIUM_SERVICE) {
                            Long date = rs.getLong("enddate");
                            if(date <= System.currentTimeMillis()){
                                ps2.setString(1, accountName);
                                ps2.setInt(2, 0);
                                ps2.setLong(3, 0L);
                                ps2.setInt(4, 0);
                                ps2.setLong(5, 0L);
                                ps2.execute();
                                player.setPremiumService(false);
                            }
                            else{
                                player.setPremiumService(rs.getInt("premium_service") == 1);
                            }
                        }
                    }
                }
            }catch(Exception e) {
            _log.error(PremiumHandler.class.getSimpleName() + ": Could not restore PremiumService data for:" + accountName + "." + e);
        }

        if (!found) {
            try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement("INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?")) {

                ps.setString(1, player._accountName);
                ps.setInt(2, 0);
                ps.setLong(3, 0L);
                ps.setInt(4, 0);
                ps.setLong(5, 0L);
                ps.executeUpdate();
            } catch (Exception e) {
                _log.error(PremiumHandler.class.getSimpleName() + ": Could not insert char data: " + e);
            }
            player.setPremiumService(false);
        }
    }

    
    public static void addPremiumServices(int timeToAdd, L2PcInstance player) {
        addPremiumServices(timeToAdd, player.getAccountName(), PremiumDuration.MONTHS);
    }

    
    public static void addPremiumServices(int timeToAdd, String accountName) {
        addPremiumServices(timeToAdd, accountName, PremiumDuration.MONTHS);
    }

    
    public static void addPremiumServices(int valueAdd, L2PcInstance player, PremiumDuration premiumDuration) {
        addPremiumServices(valueAdd, player.getAccountName(), premiumDuration);
    }

    
    public static void addPremiumServices(int valueAdd, String acountName, PremiumDuration premiumDuration) {
        Calendar calendar;
        (calendar = Calendar.getInstance()).setTimeInMillis(System.currentTimeMillis());
        switch(premiumDuration) {
            case DAYS:
                calendar.add(5, valueAdd);
                break;
            case WEEKS:
                calendar.add(3, valueAdd);
                break;
            case MONTHS:
                calendar.add(2, valueAdd);
        }

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?")){

            ps.setString(1, acountName);
            ps.setInt(2, 1);
            ps.setLong(3, calendar.getTimeInMillis());
            ps.setInt(4, 1);
            ps.setLong(5, calendar.getTimeInMillis());
            ps.execute();
        } catch (Exception e) {
            _log.error(PremiumHandler.class.getSimpleName() + ": Could not increase data." + e);
        }

    }

    
    public static void removePremiumServices(String accountName) {
        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("INSERT INTO characters_premium (account_name,premium_service,enddate) values(?,?,?) ON DUPLICATE KEY UPDATE premium_service = ?, enddate = ?")){

            ps.setString(1, accountName);
            ps.setInt(2, 0);
            ps.setLong(3, 0L);
            ps.setInt(4, 0);
            ps.setLong(5, 0L);
            ps.execute();
        } catch (SQLException var12) {
            _log.error(PremiumHandler.class.getSimpleName() + ": Could not clean data." + var12);
        }

    }
}
