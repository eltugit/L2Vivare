/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package gabriel.events.castleSiegeKoth;


import gabriel.config.GabConfig;
import gabriel.listener.actor.player.OnAnswerListener;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.handler.VoicedCommandHandler;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ConfirmDlg;
import l2r.gameserver.network.serverpackets.ExCubeGameCloseUI;
import l2r.gameserver.network.serverpackets.ExCubeGameEnd;
import l2r.gameserver.util.Broadcast;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class CSKOTHManager {
    protected static final Logger _log = Logger.getLogger(CSKOTHManager.class.getName());

    /**
     * Task for event cycles<br>
     */
    public CSKOTHStartTask _task;

    public CSKOTHStartTask get_task() {
        return _task;
    }

    /**
     * New instance only by getInstance()<br>
     */
    private CSKOTHManager() {
        if (GabConfig.CSKOTH_EVENT_ENABLED) {
            CSKOTHEvent.init();
            AdminCommandHandler.getInstance().registerHandler(new AdminCSKOTHEvent());
            if (GabConfig.CSKOTH_ALLOW_VOICED_COMMAND)
                VoicedCommandHandler.getInstance().registerHandler(new CSKOTHVoicedInfo());
            this.scheduleEventStart();
            _log.info("CSKOTHManager[CSKOTHManager.CSKOTHManager()]: Started.");
        } else {
            _log.info("CSKOTHManager[CSKOTHManager.CSKOTHManager()]: Engine is disabled.");
        }
    }

    /**
     * Initialize new/Returns the one and only instance<br><br>
     *
     * @return KOTHManager<br>
     */
    public static CSKOTHManager getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Starts KOTHStartTask
     */
    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            for (String timeOfDay : GabConfig.CSKOTH_EVENT_INTERVAL) {
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
            _task = new CSKOTHStartTask(nextStartTime.getTimeInMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            _log.warning("KOTHEventEngine[KOTHManager.scheduleEventStart()]: Error figuring out a start time. Check KOTHEventInterval in config file.");
        }
    }

    /**
     * Method to start participation
     */
    public void startReg() {
        if (!CSKOTHEvent.startParticipation()) {
            Broadcast.toAllOnlinePlayers("Castle Siege Event: Event was cancelled.");
            _log.warning("KOTHEventEngine[KOTHManager.run()]: Error spawning event npc for participation.");

            this.scheduleEventStart();
        } else {
            Broadcast.toAllOnlinePlayers("Castle Siege Event: Registration opened for " + GabConfig.CSKOTH_EVENT_PARTICIPATION_TIME + " minute(s).");
            for (L2PcInstance player : L2World.getInstance().getPlayers()) {
                ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
                dlg.addString("Do you want to participate the Castle Siege Event?");
                dlg.addTime(15 * 1000);
                dlg.addRequesterId(player.getObjectId());
                player.ask(dlg, new OnAnswerListener() {
                    @Override
                    public void sayYes() {
                        CSKOTHEvent.onBypass("cskoth_event_participation", player);
                    }

                    @Override
                    public void sayNo() {

                    }
                });
            }
            // schedule registration end
            _task.setStartTime(System.currentTimeMillis() + 60000L * GabConfig.CSKOTH_EVENT_PARTICIPATION_TIME);
            ThreadPoolManager.getInstance().executeEvent(_task);
        }
    }
    public int startedTime = 0;

    /**
     * Method to start the fight
     */
    public void startEvent() {
        if (!CSKOTHEvent.startFight()) {
            Broadcast.toAllOnlinePlayers("Castle Siege: Event cancelled due to lack of Participation.");
            _log.info("KOTHEventEngine[KOTHManager.run()]: Lack of registration, abort event.");

            this.scheduleEventStart();
        } else {
            CSKOTHEvent.sysMsgToAllParticipants("Castle Siege: Teleporting participants to an arena.");

//            startedTime = GabConfig.CSKOTH_EVENT_RUNNING_TIME;
            _task.setStartTime(System.currentTimeMillis() + 60000L * GabConfig.CSKOTH_EVENT_RUNNING_TIME);
            ThreadPoolManager.getInstance().executeEvent(_task);
        }
    }


    /**
     * Method to end the event and reward
     */
    public void endEvent() {
        Broadcast.toAllOnlinePlayers(CSKOTHEvent.calculateRewards());
        CSKOTHEvent.sysMsgToAllParticipants("Castle Siege: Teleporting back to the registration npc in " + GabConfig.CSKOTH_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");

        for (CSKOTHEventTeam team : CSKOTHEvent._teams) {
            if(CSKOTHEvent.winner == null)
                CSKOTHEvent.winner = team;
            else{
                if(team.getPoints() > CSKOTHEvent.winner.getPoints())
                    CSKOTHEvent.winner = team;
            }
        }

        for (CSKOTHEventTeam team : CSKOTHEvent._teams) {
            for (L2PcInstance partc : team.getParticipatedPlayers().values()) {
                if (partc != null) {
                    partc.sendPacket(new ExCubeGameEnd(CSKOTHEvent.winner != null && CSKOTHEvent.winner.getName().equals(GabConfig.CSKOTH_EVENT_TEAM_2_NAME)));
                    partc.sendPacket(new ExCubeGameCloseUI());

                }
            }
        }
        CSKOTHEvent.stopFight();
        this.scheduleEventStart();
    }

    public void skipDelay() {
        if (_task.nextRun.cancel(false)) {
            _task.setStartTime(System.currentTimeMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
        }
    }

    /**
     * Class for koth cycles
     */
    public class CSKOTHStartTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;

        public CSKOTHStartTask(long startTime) {
            _startTime = startTime;
        }

        public void setStartTime(long startTime) {
            _startTime = startTime;
        }

        public boolean stopped = false;

        public void run() {
            int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

            if (stopped)
                return;

            if (delay > 0) {
                this.announce(delay);
            }
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
                // start
                if (CSKOTHEvent.isInactive()) {
                    CSKOTHManager.this.startReg();
                } else if (CSKOTHEvent.isParticipating()) {
                    CSKOTHManager.this.startEvent();
                } else {
                    CSKOTHManager.this.endEvent();
                }
            }

            if (delay > 0) {
                nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
            }

        }

        private void announce(long time) {
            if (time >= 3600 && time % 3600 == 0) {
                if (CSKOTHEvent.isParticipating()) {
                    Broadcast.toAllOnlinePlayers("Castle Siege Event: " + (time / 60 / 60) + " hour(s) until registration is closed!");
                } else if (CSKOTHEvent.isStarted()) {
                    CSKOTHEvent.sysMsgToAllParticipants("Castle Siege Event: " + (time / 60 / 60) + " hour(s) until event is finished!");
                }
            } else if (time >= 60) {
                if (CSKOTHEvent.isParticipating()) {
                    Broadcast.toAllOnlinePlayers("Castle Siege Event: " + (time / 60) + " minute(s) until registration is closed!");
                } else if (CSKOTHEvent.isStarted()) {
                    CSKOTHEvent.sysMsgToAllParticipants("Castle Siege Event: " + (time / 60) + " minute(s) until the event is finished!");
                }
            } else {
                if (CSKOTHEvent.isParticipating()) {
                    Broadcast.toAllOnlinePlayers("Castle Siege Event: " + time + " second(s) until registration is closed!");
                } else if (CSKOTHEvent.isStarted()) {
                    CSKOTHEvent.sysMsgToAllParticipants("Castle Siege Event: " + time + " second(s) until the event is finished!");
                }
            }
        }
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final CSKOTHManager _instance = new CSKOTHManager();
    }
}
