package gabriel.events.tournament;


import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.util.Broadcast;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class TTournamentTask implements Runnable {
    private long _startTime;
    public ScheduledFuture<?> nextRun;

    public TTournamentTask(long startTime) {
        _startTime = startTime;
    }

    public void setStartTime(long startTime) {
        _startTime = startTime;
    }

    @Override
    public void run() {
        int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

        if (delay > 0 && delay <= 300) {
            this.announce(delay);
        }

        int nextMsg = 0;
        if (delay > 300) {
            nextMsg = delay - 300;
        } else if (delay > 60) {
            nextMsg = delay - 60;
        } else if (delay > 5) {
            nextMsg = delay - 5;
        } else if (delay > 0) {
            nextMsg = delay;
        } else {
            if (TTournamentManager.getInstance().getPeriod() == 0) {
                TTournamentScheduler.getInstance().startReg();
            } else {
                TTournamentScheduler.getInstance().endEvent();
            }
        }

        if (delay > 0) {
            nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
        }
    }

    private void announce(long time) {
        if (time <= 300 && time >= 60) {
            if (TTournamentManager.getInstance().getPeriod() == 1) {
                Broadcast.toAllOnlinePlayers("Tournament Event: " + (time / 60) + " minute(s) until the last party can join!");
            } else {
                Broadcast.toAllOnlinePlayers("Tournament Event: " + (time / 60) + " minute(s) until Tournament event Starts!");
            }
        } else {
            if (TTournamentManager.getInstance().getPeriod() == 1) {
                Broadcast.toAllOnlinePlayers("Tournament Event: " + time + " second(s) until the last party can join!");
            } else {
                Broadcast.toAllOnlinePlayers("Tournament Event: " + time + " second(s) until Tournament event Starts!");
            }

        }
    }
}
