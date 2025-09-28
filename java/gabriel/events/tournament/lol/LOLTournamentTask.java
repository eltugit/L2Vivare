package gabriel.events.tournament.lol;


import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.util.Broadcast;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class LOLTournamentTask implements Runnable {
    private long _startTime;
    public ScheduledFuture<?> nextRun;

    public LOLTournamentTask(long startTime) {
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
            if (LOLTournamentManager.getInstance().getPeriod() == 0) {
                LOLTournamentScheduler.getInstance().startReg();
            } else {
                LOLTournamentScheduler.getInstance().endEvent();
            }
        }

        if (delay > 0) {
            nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
        }
    }

    private void announce(long time) {
        if (time <= 300 && time >= 60) {
            if (LOLTournamentManager.getInstance().getPeriod() == 1) {
                Broadcast.toAllOnlinePlayers("League of Arena: " + (time / 60) + " Minute(s) Until the Last Can Join!");
            } else {
                Broadcast.toAllOnlinePlayers("League of Arena: " + (time / 60) + " Minute(s) Until Event Start!");
            }
        } else {
            if (LOLTournamentManager.getInstance().getPeriod() == 1) {
                Broadcast.toAllOnlinePlayers("League of Arena: " + time + " Second(s) Until the Last Can Join!");
            } else {
                Broadcast.toAllOnlinePlayers("League of Arena: " + time + " Second(s) Until Event Start!");
            }

        }
    }
}
