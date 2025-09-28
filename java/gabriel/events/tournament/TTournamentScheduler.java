package gabriel.events.tournament;


import gabriel.config.GabConfig;
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
public class TTournamentScheduler {
    private static final Logger _log = LoggerFactory.getLogger(TTournamentScheduler.class);

    private static TTournamentScheduler instance;
    private static TTournamentManager manager;
    private boolean schedulerIsRunning = false;
    public boolean launched = false;

    public static TTournamentScheduler getInstance() {
        if (instance == null)
            instance = new TTournamentScheduler();
        return instance;
    }

    private TTournamentScheduler() {
        manager = TTournamentManager.getInstance();
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));
        if (Config.ARENA_DIAS_RUN.contains(todayDay)) {
            if (Config.ARENA_ENABLE_AUTOMATED && Config.ARENA_EVENT_ENABLED) {
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
            if (Config.ARENA_ENABLE_AUTOMATED) {
                Calendar currentTime = Calendar.getInstance();
                Calendar nextStartTime = null;
                Calendar testStartTime = null;
                for (String timeOfDay : Config.ARENA_EVENT_INTERVAL) {
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
                manager.set_task(new TTournamentTask(nextStartTime.getTimeInMillis()));
                ThreadPoolManager.getInstance().executeEvent(manager.get_task());
            }
        } catch (Exception e) {
            _log.warn("TTournamentScheduler[TTournamentScheduler.scheduleEventStart()]: Error figuring out a start time. Check EventDurationMinutes in config file.");
        }
    }

    public void startReg() {
        manager.setPeriod(1);
        schedulerIsRunning = false;
        _log.info("Initialized Arena Event");
        Broadcast.toAllOnlinePlayers("Tournament Event: Tournament opened for " + Config.ARENA_DURATION_PERIOD + " minute(s).");
        Broadcast.toAllOnlinePlayers("Tournament Event: Matches will be taking place for the whole time till it finishes!");

        manager.get_task().setStartTime(System.currentTimeMillis() + 60000L * Config.ARENA_DURATION_PERIOD);
        ThreadPoolManager.getInstance().executeEvent(manager.get_task());

    }

    public void endEvent() {
        manager.setPeriod(0);
        Broadcast.toAllOnlinePlayers("Tournament Event: Event Finished.");
        schedulerIsRunning = true;
        this.scheduleEventStart();
        _log.info("Finishing Arena Event");


    }
}
