package gabriel.autofarm.manager;

import gabriel.config.AutoFarmConfig;
import gr.sr.configsEngine.configs.impl.AutoRestartConfigs;
import gr.sr.utils.Tools;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.instancemanager.GlobalVariablesManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AutoPlayManager
{

    private enum TypeConfig {
        HWID, ACCOUNT, IP
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoPlayManager.class);
    public static final String NEXT_AUTOPLAY_RESET = "NEXT_AUTOPLAY_RESET";
    public static final String AUTOPLAY_STARTED_AT = "AUTOPLAY_STARTED_AT";
    private final Map<String, Long> _remainingTimesHwid = new ConcurrentHashMap<>();
    private final Map<String, Long> _remainingTimesAccount = new ConcurrentHashMap<>();
    private final Map<String, Long> _remainingTimesIp = new ConcurrentHashMap<>();
    private final Map<String, Integer> _activePlayers = new ConcurrentHashMap<>();
    private ScheduledFuture<?> _resetTask = null;
    private Calendar _resetCalendar = null;

    public AutoPlayManager()
    {
        load();
    }

    private void load()
    {
        AutoFarmConfig.getInstance();

        _remainingTimesHwid.clear();
        _remainingTimesAccount.clear();
        _remainingTimesIp.clear();

        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement("SELECT * from autoplay_times");
            ResultSet rs = stm.executeQuery())
        {
            while (rs.next())
            {
                if(rs.getInt("isAccount") == 1){
                    _remainingTimesAccount.put(rs.getString("hwid"), rs.getLong("remaining_time"));
                }
//                else if(rs.getInt("isAccount") == 2){
//                    _remainingTimesIp.put(rs.getString("hwid"), rs.getLong("remaining_time"));
//                }else{
//                    _remainingTimesHwid.put(rs.getString("hwid"), rs.getLong("remaining_time"));
//                }

            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        LOGGER.info(getClass().getSimpleName() + ": Restored " + (_remainingTimesHwid.size() + _remainingTimesAccount.size()+ _remainingTimesIp.size()) + " autoplay remaining times.");

        // If server starts after last reset
        long lastAutoplayReset = GlobalVariablesManager.getInstance().getLong(NEXT_AUTOPLAY_RESET, 0);
        if (System.currentTimeMillis() > lastAutoplayReset)
        {
            reset();
        }

        if (_resetTask != null)
        {
            _resetTask.cancel(false);
            _resetTask = null;
        }

        _resetTask = ThreadPoolManager.getInstance().scheduleGeneral(this::reset, getMillisToReset());
        GlobalVariablesManager.getInstance().set(NEXT_AUTOPLAY_RESET, _resetCalendar.getTimeInMillis());
        LOGGER.info(getClass().getSimpleName() + ": Scheduled auto play time reset to " + _resetCalendar.getTime());
    }

    public long getMillisToReset()
    {
        if (_resetCalendar != null)
        {
            return (_resetCalendar.getTimeInMillis() - System.currentTimeMillis());
        }

        Calendar cal = Calendar.getInstance();
        long nextRun = 0L;
        int timeToRestart = 0;
        String[] timesOfDay  = AutoRestartConfigs.RESTART_INTERVAL_BY_TIME_OF_DAY;

        for(int i = 0; i < timesOfDay.length; ++i)
        {
            String timeOfDay = timesOfDay[i];
            Calendar cal2;
            (cal2 = Calendar.getInstance()).setLenient(true);
            String[] time = timeOfDay.split(":");
            cal2.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            cal2.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MILLISECOND, 0);
            if (cal2.getTimeInMillis() < cal.getTimeInMillis())
            {
                cal2.add(Calendar.DATE, 1);
            }
            long timeInMilis = cal2.getTimeInMillis() - cal.getTimeInMillis();
            if (timeToRestart == 0)
            {
                nextRun = timeInMilis;
                _resetCalendar = cal2;
            }
            if (timeInMilis < nextRun)
            {
                nextRun = timeInMilis;
                _resetCalendar = cal2;
            }
            ++timeToRestart;
        }

        return (_resetCalendar.getTimeInMillis() - System.currentTimeMillis());
    }

    public void reset()
    {
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement("TRUNCATE TABLE `autoplay_times`");)
        {
            stm.executeUpdate();
//            _remainingTimesHwid.clear();
            _remainingTimesAccount.clear();
//            _remainingTimesIp.clear();
            LOGGER.info(getClass().getSimpleName() + ": Reset auto play times.");
            for (L2PcInstance player : L2World.getInstance().getPlayers())
            {
                if (player != null && (player.isOnlineInt() == 1) && !player.isInOfflineMode() && canAutoPlay(player))
                {
                    sendAvailableTime(player);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void store(L2PcInstance player, long newTime, TypeConfig isAccount)
    {
        String hwid = getPlayerInfo(player, isAccount);
        try(Connection con = L2DatabaseFactory.getInstance().getConnection();
            PreparedStatement stm = con.prepareStatement("REPLACE INTO autoplay_times(hwid, remaining_time, isAccount) VALUES (?, ?, ?)"))
        {
            stm.setString(1, hwid);
            stm.setLong(2, newTime);
            stm.setInt(3, isAccount.ordinal());
            stm.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private long getRemainingTime(L2PcInstance player)
    {
        if (player.getClient() == null || player.getClient().getHWID() == null)
        {
            return 0;
        }

        if (!canAutoPlay(player))
        {
            return 0;
        }

        final long time = player.isPremium() ? AutoFarmConfig.AUTO_FARM_PREMIUM_MINUTES * 60L : AutoFarmConfig.AUTO_FARM_NORMAL_MINUTES * 60L;

//        long hwidTime = _remainingTimesHwid.getOrDefault(getPlayerInfo(player, TypeConfig.HWID), time);
//        System.out.println("AutoPlayManager:getRemainingTime:hwidTime: "+ hwidTime);
//        long ipTime = _remainingTimesIp.getOrDefault(getPlayerInfo(player, TypeConfig.IP), time);
//        System.out.println("AutoPlayManager:getRemainingTime:ipTime: "+ ipTime);
//        System.out.println("AutoPlayManager:getRemainingTime:acc: "+ _remainingTimesAccount.getOrDefault(getPlayerInfo(player, TypeConfig.ACCOUNT), time));
        long accountTime = _remainingTimesAccount.getOrDefault(getPlayerInfo(player, TypeConfig.ACCOUNT), time);
//        if(hwidTime > 0)
//            return hwidTime;
//        if(ipTime > 0)
//            return hwidTime;
//
//        if(hwidTime <= 0 || ipTime <= 0)
//            return 0;
//
//        if(accountTime > 0)
//            return hwidTime;

        return accountTime;
//        return 0;
    }

    private void reduceRemainingTime(L2PcInstance player)
    {
        long autoplayStartedAt = Long.parseLong(player.getVar(AUTOPLAY_STARTED_AT, "0"));
        if (autoplayStartedAt > 0)
        {
            long secondsPassed = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - autoplayStartedAt);
            long remaining = getRemainingTime(player);
            long newRemainingTime = Math.max(remaining - secondsPassed, 0);

//            System.out.println("AutoPlayManager:reduceRemainingTime: newRemainingTime: "+newRemainingTime );

//            _remainingTimesHwid.put(getPlayerInfo(player, TypeConfig.HWID), newRemainingTime);
            _remainingTimesAccount.put(getPlayerInfo(player, TypeConfig.ACCOUNT), newRemainingTime);
//            _remainingTimesIp.put(getPlayerInfo(player, TypeConfig.IP), newRemainingTime);

            player.unsetVar(AUTOPLAY_STARTED_AT);

//            store(player, newRemainingTime, TypeConfig.HWID);
            store(player, newRemainingTime, TypeConfig.ACCOUNT);
//            store(player, newRemainingTime, TypeConfig.IP);
//             LOGGER.info(getClass().getSimpleName() + ": Storing remaining time for " + player + ". Remaining time = " + newRemainingTime);
        }

        sendAvailableTime(player);
    }

    private boolean canAutoPlay(L2PcInstance player)
    {

        final long time = player.isPremium() ? AutoFarmConfig.AUTO_FARM_PREMIUM_MINUTES * 60L : AutoFarmConfig.AUTO_FARM_NORMAL_MINUTES * 60L;

//        long hwidTime = _remainingTimesHwid.getOrDefault(getPlayerInfo(player, TypeConfig.HWID), time);
        long accountTime = _remainingTimesAccount.getOrDefault(getPlayerInfo(player, TypeConfig.ACCOUNT), time);
//        long ipTime = _remainingTimesIp.getOrDefault(getPlayerInfo(player, TypeConfig.IP), time);

//        System.out.println("AutoPlayManager:canAutoPlay:hwidTime: "+hwidTime );
//        System.out.println("AutoPlayManager:canAutoPlay:accountTime: "+accountTime );
//        System.out.println("AutoPlayManager:canAutoPlay:ipTime: "+ipTime );

//        if(hwidTime == 0 || accountTime == 0 || ipTime == 0)
        if(accountTime == 0)
            return false;

        final int activeObjectId = _activePlayers.getOrDefault(player.getClient().getHWID(), 0);
        return (activeObjectId == 0 || activeObjectId == player.getObjectId());
    }

    public void onEnterWorld(L2PcInstance player)
    {
        if (player.getClient() == null)
        {
            LOGGER.info("Player has client = null while trying to get auto play info.");
            return;
        }

        if (canAutoPlay(player))
        {
            _activePlayers.put(player.getClient().getHWID(), player.getObjectId());
            long remainingTime = getRemainingTime(player);
            if (remainingTime > 0) {
//                _remainingTimesHwid.put(getPlayerInfo(player, TypeConfig.HWID), remainingTime);
                _remainingTimesAccount.put(getPlayerInfo(player, TypeConfig.ACCOUNT), remainingTime);
//                _remainingTimesIp.put(getPlayerInfo(player, TypeConfig.IP), remainingTime);
//                store(player, remainingTime, TypeConfig.HWID);
                store(player, remainingTime, TypeConfig.ACCOUNT);
//                store(player, remainingTime, TypeConfig.IP);
            }
        }

        sendAvailableTime(player);
    }
    public static String getPlayerInfo(L2PcInstance player, TypeConfig config) {
        switch (config) {
            case HWID:
                return player.getClient().getHWID();
            case ACCOUNT:
                return player.getAccountName();
            case IP:
                return Tools.getNetIp(player);
            default:
                return Tools.getNetIp(player);
        }
    }

    public void onLeaveWorld(L2PcInstance player)
    {
        if (canAutoPlay(player))
        {
            if (getRemainingTime(player) > 0)
            {
                reduceRemainingTime(player);
            }
            _activePlayers.remove(player.getClient().getHWID());
            // LOGGER.info(getClass().getSimpleName() + ": Removed " + player + " from auto play active players. (Leave World)");
        }

        player.unsetVar(AUTOPLAY_STARTED_AT);
    }

    public void sendAvailableTime(L2PcInstance player)
    {
        player.sendPacket(new CreatureSay(0, Say2.MSNCHAT, player.getName(), "[AutoPlayTime]" + getRemainingTime(player)));
    }

    public void startAutoPlay(L2PcInstance player)
    {
        if (!canAutoPlay(player))
        {
            LOGGER.info(getClass().getSimpleName() + ": Player " + player + " tried to activate auto play when not available!");
            return;
        }

        if (getRemainingTime(player) > 0)
        {
            player.setVar(AUTOPLAY_STARTED_AT, String.valueOf(System.currentTimeMillis()));

            // LOGGER.info(getClass().getSimpleName() + ": Player " + player + " started auto play. Remaining time = " + getRemainingTime(player));
        }
        else
        {
            player.unsetVar(AUTOPLAY_STARTED_AT);
        }
    }

    public void stopAutoPlay(L2PcInstance player)
    {
        if (canAutoPlay(player))
        {
            // LOGGER.info(getClass().getSimpleName() + ": Stopping auto play for " + player);
            reduceRemainingTime(player);
        }
    }

    public static AutoPlayManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder
    {
        private static final AutoPlayManager INSTANCE = new AutoPlayManager();
    }

}
