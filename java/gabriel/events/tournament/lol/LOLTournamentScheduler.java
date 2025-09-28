package gabriel.events.tournament.lol;


import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class LOLTournamentScheduler {
    private static final Logger _log = LoggerFactory.getLogger(LOLTournamentScheduler.class);

    private static LOLTournamentScheduler instance;
    private static LOLTournamentManager manager;
    private boolean schedulerIsRunning = false;
    public boolean launched = false;

    public static LOLTournamentScheduler getInstance() {
        if (instance == null)
            instance = new LOLTournamentScheduler();
        return instance;
    }

    private LOLTournamentScheduler() {
        manager = LOLTournamentManager.getInstance();
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));
        if (Config.ARENA_DIAS_RUN_LOL.contains(todayDay)) {
            if (Config.ARENA_ENABLE_AUTOMATED_LOL && Config.ARENA_EVENT_ENABLED_LOL) {
                if (manager.getPeriod() == 0) {
                    if (schedulerIsRunning == false) {
                        launched = true;
                        this.scheduleEventStart();
                        schedulerIsRunning = true;
                    }
                } else {
                    _log.info("Initialized Arena Event");
                }
            } else {
                manager.setPeriod(1);
                manager.startMatchMaking();
                _log.info("Initialized Arena Event");
            }
        } else {
            launched = false;
            manager.setPeriod(0);
            instance = null;
        }
    }

    public void closeManager() {
        launched = false;
        manager.setPeriod(0);
        instance = null;
    }

    public void scheduleEventStart() {
        try {
            if (Config.ARENA_ENABLE_AUTOMATED_LOL) {
                Calendar currentTime = Calendar.getInstance();
                Calendar nextStartTime = null;
                Calendar testStartTime = null;
                for (String timeOfDay : Config.ARENA_EVENT_INTERVAL_LOL) {
                    // Creating a Calendar object from the specified interval value
                    testStartTime = Calendar.getInstance();
                    testStartTime.setLenient(true);
                    String[] splitTimeOfDay = timeOfDay.split(":");
                    testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
                    testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
                    // If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
                    if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
                        testStartTime.add(Calendar.DAY_OF_MONTH, 1);
                    // Check for the test date to be the minimum (smallest in the specified list)
                    if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
                        nextStartTime = testStartTime;
                }
                manager.set_task(new LOLTournamentTask(nextStartTime.getTimeInMillis()));
                ThreadPoolManager.getInstance().executeEvent(manager.get_task());
            }
        } catch (Exception e) {
            _log.warn("LOLTournamentScheduler[LOLTournamentScheduler.scheduleEventStart()]: Error figuring out a start time. Check EventDurationMinutes in config file.");
        }
    }

    public void startReg() {
        manager.setPeriod(1);
        schedulerIsRunning = false;
        _log.info("Initialized Arena Event");
        Broadcast.toAllOnlinePlayers("League of Arena: Opened for " + Config.ARENA_DURATION_PERIOD_LOL + " Minute(s).");
//        Broadcast.toAllOnlinePlayers("Tournament Event: Matches will be taking place for the whole time till it finishes!");

        manager.get_task().setStartTime(System.currentTimeMillis() + 60000L * Config.ARENA_DURATION_PERIOD_LOL);
        ThreadPoolManager.getInstance().executeEvent(manager.get_task());

    }

    public void endEvent() {
        manager.setPeriod(0);
        Broadcast.toAllOnlinePlayers("League of Arena: Event Finished.");
        schedulerIsRunning = true;
        this.scheduleEventStart();
        _log.info("Finishing Arena Event");


    }
}
