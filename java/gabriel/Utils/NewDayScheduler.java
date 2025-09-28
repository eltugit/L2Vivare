package gabriel.Utils;

import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gabriel.events.tournament.TTournamentManager;
import gabriel.events.tournament.TTournamentScheduler;
import gabriel.events.tournament.lol.LOLTournamentManager;
import gabriel.events.tournament.lol.LOLTournamentScheduler;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;

import static l2r.gameserver.GameServer.printSection;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * This class handles all methods that needs to be "refreshed" at a new day
 */
public class NewDayScheduler {
    private static final Logger _log = LoggerFactory.getLogger(NewDayScheduler.class);

    protected static NewDayScheduler instance;

    public static NewDayScheduler getInstance() {
        if (instance == null)
            instance = new NewDayScheduler();
        return instance;
    }

    private NewDayScheduler() {
        scheduleEventStart();
    }

    private void refreshDaily() {
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));
        if (TTournamentScheduler.getInstance().launched) {
            if (!Config.ARENA_DIAS_RUN.contains(todayDay)) {
                TTournamentScheduler.getInstance().closeManager();
                TTournamentManager.getInstance().closeTournament();
            }
        } else {
            if (Config.ARENA_DIAS_RUN.contains(todayDay)) {
                if (Config.ARENA_EVENT_ENABLED) {
                    TTournamentScheduler.getInstance();
                    TTournamentManager.getInstance();
                }
            }
        }
        if (LOLTournamentScheduler.getInstance().launched) {
            if (!Config.ARENA_DIAS_RUN_LOL.contains(todayDay)) {
                LOLTournamentScheduler.getInstance().closeManager();
                LOLTournamentManager.getInstance().closeTournament();
            }
        } else {
            if (Config.ARENA_DIAS_RUN_LOL.contains(todayDay)) {
                if (Config.ARENA_EVENT_ENABLED_LOL) {
                    LOLTournamentScheduler.getInstance();
                    LOLTournamentManager.getInstance();
                }
            }
        }
        if (EpicRaidManager.getInstance().isLaunched()) {
            if (!GabConfig.ER_EVENT_DIAS_RUN.contains(todayDay)) {
                EpicRaidManager.getInstance().closeManager();
            }
        } else {
            if (GabConfig.ER_EVENT_DIAS_RUN.contains(todayDay)) {
                if (GabConfig.ER_EVENT_ENABLED) {
                    EpicRaidManager.getInstance();
                    printSection("Gabriel: Epic Raid is Enabled");
                } else {
                    EpicRaidManager.getInstance().closeManager();
                    printSection("Gabriel: Epic Raid is Disabled");
                }
            }
        }
//
//        if(ExtremeZoneManager.getInstance().isLaunched()){
//            if(!GabConfig.EXTREME_EVENT_DIAS_RUN.contains(todayDay)) {
//                ExtremeZoneManager.getInstance().closeManager();
//            }
//        }else{
//            if(GabConfig.EXTREME_EVENT_DIAS_RUN.contains(todayDay)){
//                if(GabConfig.EXTREME_EVENT_ENABLED){
//                    ExtremeZoneManager.getInstance();
//                    printSection("Gabriel: Extreme Zone is Enabled");
//                }else{
//                    ExtremeZoneManager.getInstance().closeManager();
//                    printSection("Gabriel: Extreme Zone is Disabled");
//                }
//            }
//        }

    }

    private NewDayTask _task;

    private String[] timeOfDayToRefresh = {
            "00:01",
    };


    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            for (String timeOfDay : timeOfDayToRefresh) {
                // Creating a Calendar object from the specified interval value
                testStartTime = Calendar.getInstance();
                testStartTime.setLenient(true);
                String[] splitTimeOfDay = timeOfDay.split(":");
                testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
                testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
                // If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
                if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis()) {
                    testStartTime.add(Calendar.DAY_OF_MONTH, 1);
                }
                // Check for the test date to be the minimum (smallest in the specified list)
                if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()) {
                    nextStartTime = testStartTime;
                }
            }
            _task = new NewDayTask(nextStartTime.getTimeInMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            _log.warn("NewDayScheduler Error figuring out a start time. Check EREventInterval in config file.");
        }
    }


    class NewDayTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;
        boolean running = false;
        boolean cancel = false;

        public NewDayTask(long startTime) {
            _startTime = startTime;
        }

        public void setStartTime(long startTime) {
            _startTime = startTime;
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
            if (cancel) {
                running = false;
                return;
            }

            int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

            int nextMsg = 0;
            if (delay > 3600) {
                nextMsg = delay - 3600;
            } else if (delay > 1800) {
                nextMsg = delay - 1800;
            } else if (delay > 900) {
                nextMsg = delay - 900;
            } else if (delay > 600) {
                nextMsg = delay - 600;
            } else if (delay > 300) {
                nextMsg = delay - 300;
            } else if (delay > 60) {
                nextMsg = delay - 60;
            } else if (delay > 5) {
                nextMsg = delay - 5;
            } else if (delay > 0) {
                nextMsg = delay;
            } else {
                refreshDaily();
            }

            if (delay > 0) {
                nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
            }
        }
    }

}
