package gr.sr.main;

import gr.sr.configsEngine.configs.impl.AutoRestartConfigs;
import l2r.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Restart {
    protected static final Logger _log = LoggerFactory.getLogger(Restart.class);
    private Calendar calendar;
    private final SimpleDateFormat formater = new SimpleDateFormat("HH:mm");

    protected Restart() {
    }

    public String getRestartNextTime() {
        return this.calendar.getTime() != null ? this.formater.format(this.calendar.getTime()) : "Error";
    }

    public void StartCalculationOfNextRestartTime() {
        try {
            Calendar cal = Calendar.getInstance();
            long nextRun = 0L;
            int timeToRestart = 0;
            String[] timesOfDay  = AutoRestartConfigs.RESTART_INTERVAL_BY_TIME_OF_DAY;

            for(int i = 0; i < timesOfDay.length; ++i) {
                String timeOfDay = timesOfDay[i];
                Calendar cal2;
                (cal2 = Calendar.getInstance()).setLenient(true);
                String[] time = timeOfDay.split(":");
                cal2.set(11, Integer.parseInt(time[0]));
                cal2.set(12, Integer.parseInt(time[1]));
                cal2.set(13, 0);
                if (cal2.getTimeInMillis() < cal.getTimeInMillis()) {
                    cal2.add(5, 1);
                }
                long timeInMilis = cal2.getTimeInMillis() - cal.getTimeInMillis();
                if (timeToRestart == 0) {
                    nextRun = timeInMilis;
                    this.calendar = cal2;
                }
                if (timeInMilis < nextRun) {
                    nextRun = timeInMilis;
                    this.calendar = cal2;
                }
                ++timeToRestart;
            }
            _log.info(Restart.class.getSimpleName() + ": Next restart: " + this.calendar.getTime().toString());
            ThreadPoolManager.getInstance().scheduleGeneral(new RestartRunnable(this), nextRun);
        } catch (Exception var11) {
            _log.warn(Restart.class.getSimpleName() + ": Auto restart failed initialize!");
        }
    }

    protected static Restart instance;

    public static Restart getInstance() {
        if (instance == null)
            instance = new Restart();
        return instance;
    }
}
