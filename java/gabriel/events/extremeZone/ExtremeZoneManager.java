package gabriel.events.extremeZone;


import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gr.sr.interf.SunriseEvents;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.instancemanager.ZoneManager;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.zone.type.L2EpicRaidCheckerZone;
import l2r.gameserver.network.serverpackets.ExServerPrimitive;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import l2r.util.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ExtremeZoneManager extends Quest {
    private static final Logger _log = LoggerFactory.getLogger(ExtremeZoneManager.class);
    private static int[] doors = {
            24220008,
            24220009,
            24220010,
            24220011,
            24220012,
            24220013,
            24220014,
            24220015,
            24220016,
            24220017,
            24220018,
            24220019,
            24220020,
            20240001,
            20240002,
            20240003,
            23140101,
            17130046,
            25150011,
            25150021,
            25150001,
            21240006
    };
    private ExtremeZone nextZone;
    public ExtremeStartTask _task;
    private long timeStarted = 0L;

    public ExtremeAccessChecker getChecker() {
        return _taskChecker;
    }

    private ExtremeAccessChecker _taskChecker;
    private boolean started = false;
    private long timeToStart;
    private Instance inst = null;
    private L2Npc boss = null;
    private static L2Spawn _npcSpawn = null;
    private L2Npc _lastNpcSpawn = null;
    private AccessGranterEX accessGranted = null;
    private boolean closed = false;
    private boolean bossAlive = false;

    public L2Npc getBossSpawn() {
        return boss;
    }

    public boolean isBossAlive() {
        return bossAlive;
    }

    public boolean launched = false;

    public boolean isClosed() {
        return closed;
    }

    public boolean isLaunched() {
        return launched;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public AccessGranterEX getAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(AccessGranterEX ccAccessGranted) {
        this.accessGranted = ccAccessGranted;
    }

    private ExtremeZoneManager() {
        super(-1, ExtremeZoneManager.class.getSimpleName(), "instances");
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));

        if (GabConfig.EXTREME_EVENT_DIAS_RUN.contains(todayDay)) {
            if (GabConfig.EXTREME_EVENT_ENABLED) {
                launched = true;
                ExtremeZoneParser.getInstance().load();
                _log.info("Loaded " + ExtremeZoneHolder.getInstance().size() + " Extreme Zones into the system!");
                nextRandomExtremeZone();
//                scheduleEventStart();
            }
        } else {
            launched = false;
            if (_task != null) {
                _task.cancel = true;
            }
            timeToStart = 0L;
            instance = null;
        }
    }

    public void closeManager() {
        launched = false;
        if (_task != null) {
            _task.cancel = true;
        }
        timeToStart = 0L;
        instance = null;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

    public ExtremeStartTask get_task() {
        return _task;
    }

    public ExtremeZone getNextZone() {
        return nextZone;
    }

    public void addRaidList(ExtremeZone raid) {
        ExtremeZoneHolder.getInstance().addRaid(raid);
    }

    public List<ExtremeZone> getExtremeZone() {
        return ExtremeZoneHolder.getInstance().getAllExtremes();
    }


    public L2Npc getNpcSpawn() {
        return _lastNpcSpawn;
    }


    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            for (String timeOfDay : GabConfig.EXTREME_EVENT_INTERVAL) {
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

            _task = new ExtremeStartTask(nextStartTime.getTimeInMillis());
            timeToStart = nextStartTime.getTimeInMillis();
            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            _log.warn("Extreme Zone [ExtremeZoneManager.scheduleEventStart()]: Error figuring out a start time. Check ExtremeEventInterval in config file.");
        }
    }


    public void startEvent() {
        if(!GabConfig.EXTREME_EVENT_ENABLED)
            return;
        for (String s : GabConfig.EX_TEXT.split(";")) {
            if (s.contains("%boss%")) {
                s = s.replace("%boss%", nextZone.getName());
            }
            GabUtils.extremeZoneBroadcast(s);
        }

        if (GabConfig.EXTREME_EVENT_CLOSE_ALL) {
//            if (EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.REGISTERING ||
//                    EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.RUNNING) {
//                GabUtils.extremeZoneBroadcast("Ending Current event because Extreme Zone is starting!");
//                EventManager.getInstance().getMainEventManager().abort(null, false);
//            }
//            if (KOTHEvent.isStarted() || KOTHEvent.isStarting()) {
//                GabUtils.extremeZoneBroadcast("Ending King of the Hill event because Extreme Zone is starting!");
//                KOTHManager.getInstance()._task.stopped = true;
//                KOTHManager.getInstance().endEvent();
//            }
//            if (CSEvent.isStarted() || CSEvent.isStarting()) {
//                GabUtils.extremeZoneBroadcast("Ending Castle Siege event because Extreme Zone is starting!");
//                CSManager.getInstance()._task.stopped = true;
//                CSManager.getInstance().endEvent();
//            }
//            if (PartyZoneManager.getInstance().isRunning()) {
//                GabUtils.extremeZoneBroadcast("Ending PartyZone event because Extreme Zone is starting!");
//                PartyZoneManager.getInstance()._task.stopped = true;
//                PartyZoneManager.getInstance().endEvent();
//            }
//            if (EpicRaidManager.getInstance().isStarted()) {
//                GabUtils.extremeZoneBroadcast("Ending Epic Raid event because Extreme Zone is starting!");
//                EpicRaidManager.getInstance()._task.cancel = true;
//                EpicRaidManager.getInstance().endEvent(false);
//            }
//            if (PVPInstance.getInstance().isActive()) {
//                GabUtils.extremeZoneBroadcast("Ending PvPInstance event because Extreme Zone is starting!");
//                PVPInstance.getInstance().endInstance();
//            }
        }
//
//        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
//            if(!GabUtils.canJoinEvent(player)){
//                continue;
//            }
//
//            DLG.sendDlg(player, "Do you want to join the current ExtremeZone event?", DLG.IdDialog.EVENT_EXTREME);
//        }

        if (!GabConfig.EXTREME_EVENT_RADIUS_CHECK)
            ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2EpicRaidCheckerZone.class).setActive(true);
//        _task.setStartTime(System.currentTimeMillis() + 60000L * GabConfig.EXTREME_EVENT_RUNNING_TIME);
//        _task = new ExtremeStartTask(System.currentTimeMillis() + 60000L * GabConfig.EXTREME_EVENT_RUNNING_TIME);
//        ThreadPoolManager.getInstance().executeEvent(_task);
        handleCreateInstance();
        setStarted(true);
        timeStarted = Calendar.getInstance().getTimeInMillis();

    }

    private void spawnZoneChecker() {
        L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(GabConfig.EXTREME_EVENT_NPC_ID);
        if (tmpl == null) {
            _log.warn("ExtremeZoneManager[ExtremeZoneManager.spawnEnterNpc()]: L2NpcTemplate is a NullPointer -> Invalid npc id in configs?");
            return;
        }
        try {
            _npcSpawn = new L2Spawn(tmpl);
            _npcSpawn.setLocation(getNextZone().getTeleNpc());
            _npcSpawn.setAmount(1);
            _npcSpawn.setHeading(0);
            _npcSpawn.setRespawnDelay(1);
            SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
            _npcSpawn.init();
            _lastNpcSpawn = _npcSpawn.getLastSpawn();
            _lastNpcSpawn.setCurrentHp(_lastNpcSpawn.getMaxHp());
            _lastNpcSpawn.getTemplate().setTitle("Extreme Zone Event");
            _lastNpcSpawn.isAggressive();
            _lastNpcSpawn.decayMe();
            _lastNpcSpawn.setInstanceId(GabConfig.EXTREME_EVENT_INSTANCE_ID);
            _lastNpcSpawn.spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());
            _lastNpcSpawn.broadcastPacket(new MagicSkillUse(_lastNpcSpawn, _lastNpcSpawn, 1034, 1, 1, 1));
        } catch (Exception e) {
            _log.warn("ExtremeZoneManager[ExtremeZoneManager.spawnEnterNpc()]: exception: " + e.getMessage(), e);
        }
        _taskChecker = new ExtremeAccessChecker();
        ThreadPoolManager.getInstance().executeEvent(_taskChecker);

    }

    private void unSpawnEnterNpc() {
        if (_lastNpcSpawn != null) {
            if (GabConfig.EXTREME_EVENT_DRAW_LINES) {
                if (GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                    handleDrawn(true, true);
                } else {
                    handleDrawn(true, false);
                }
            }

            _taskChecker.cancel();

            _lastNpcSpawn.deleteMe();
            SpawnTable.getInstance().deleteSpawn(_lastNpcSpawn.getSpawn(), false);
            _npcSpawn.stopRespawn();
            _npcSpawn = null;
            _lastNpcSpawn = null;
        }
    }

    public void endEvent() {
        try {
            if (_taskChecker != null)
                _taskChecker.cancel();
        } catch (Exception e) {
            //
        }
        setStarted(false);
        try {
            GabUtils.extremeZoneBroadcast(GabConfig.EX_EVENT_FINISH);
            if (!GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2EpicRaidCheckerZone.class).setActive(false);
                removeEveryoneFromZone();
            }
        } catch (Exception e) {
            //
        }
        try {
            ExtremeZoneTimer.getInstance(inst).rewardPlayers();
            ExtremeZoneTimer.getInstance(inst).getPlayersInside().clear();
        } catch (Exception e) {
            //
        }
        try {
            ExtremeZoneTimer.getInstance(inst).stop();

        } catch (Exception e) {
            //
        }
        try {
            nextRandomExtremeZone();
        } catch (Exception e) {
            //
        }
        try {
            if (boss != null) {
                boss.deleteMe();
                boss.getSpawn().stopRespawn();
                SpawnTable.getInstance().deleteSpawn(boss.getSpawn(), false);
                boss = null;
                bossAlive = false;
            }
        } catch (Exception e) {
            //
        }
        try {
            handleDestroyInstance();
        } catch (Exception e) {
            //
        }
        try {
            if (_task != null)
                this._task.cancel = true;
        } catch (Exception e) {
            //
        }
        try {
            if (GabConfig.EXTREME_EVENT_DRAW_LINES) {
                if (GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                    handleDrawn(true, true);
                } else {
                    handleDrawn(true, false);
                }
            }
        } catch (Exception e) {
            //
        }
        try {
            unSpawnEnterNpc();

        } catch (Exception e) {
            //
        }
    }

    public void skipDelay() {
        if (_task.nextRun.cancel(false)) {
            _task.setStartTime(System.currentTimeMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
        }
    }

    public void nextRandomExtremeZone() {
        nextZone = getExtremeZone().get(Rnd.get(getExtremeZone().size()));
    }

    private void handleCreateInstance() {
        int templateId = GabConfig.EXTREME_EVENT_INSTANCE_ID;
        inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst == null) {
            InstanceManager.getInstance().createInstance(templateId);
            inst = InstanceManager.getInstance().getInstance(templateId);
            inst.setPvPInstance(true);
            inst.setDuration(GabConfig.EXTREME_EVENT_RUNNING_TIME * 60000);
            inst.setShowTimer(true);
            inst.setTimerIncrase(false);
            inst.setTimerText("Zone Ends In");
            inst.setAllowSummon(false);
            inst.setExitLoc(new Location(82840, 148616, -3472));

            for (int door : doors) {
                StatsSet set = new StatsSet();
                set.add(DoorData.getInstance().getDoorTemplate(door));
                InstanceManager.getInstance().getInstance(GabConfig.EXTREME_EVENT_INSTANCE_ID).addDoor(door, set);
            }

            int BOSS = getNextZone().getNpcId();
            if (BOSS != 0) {
                boss = addSpawn(BOSS, getNextZone().getLocOfRaidBoss().getX(), getNextZone().getLocOfRaidBoss().getY(), getNextZone().getLocOfRaidBoss().getZ(), 0, false, 0, false, GabConfig.EXTREME_EVENT_INSTANCE_ID);
//                ((L2GrandBossInstance) boss).setOnKillDelay(100);
                ExtremeZoneManager.getInstance().addKillId(BOSS);
                bossAlive = true;
            }

            ExtremeZoneTimer.getInstance(inst).startInsideTimer();
        }
    }

    private void handleDestroyInstance() {
        int templateId = GabConfig.EXTREME_EVENT_INSTANCE_ID;
        inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            InstanceManager.getInstance().destroyInstance(templateId);
        }
    }

    public String getTimeToStart() {
        long milliToStart = timeToStart - System.currentTimeMillis();
        double numSecs = (milliToStart / 1000) % 60;
        int secs = (int) numSecs;
        double countDown = ((milliToStart / 1000.) - numSecs) / 60;
        int numMins = (int) Math.floor(countDown % 60);
        countDown = (countDown - numMins) / 60;
        int numHours = (int) Math.floor(countDown % 24);
        int numDays = (int) Math.floor((countDown - numHours) / 24);
        if (numDays == 0) {
            return numHours + " hours and " + numMins + " mins " + secs + " secs.";
        } else {
            return numDays + " days, " + numHours + " hours and " + numMins + " mins.";
        }
    }

    public String getTimeToEnd() {
        int templateId = GabConfig.EXTREME_EVENT_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            return timeStarted < Calendar.getInstance().getTimeInMillis() ? "---" : GabUtils.getTimeRemaining(timeStarted);
        }
        return "----";
    }


    public class ExtremeStartTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;
        boolean running = false;
        public boolean cancel = false;

        public ExtremeStartTask(long startTime) {
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
                // start
                if (running) {
                    running = false;
                    ExtremeZoneManager.getInstance().endEvent();
                } else {
                    running = true;
                    ExtremeZoneManager.getInstance().startEvent();
                }
            }
            if (delay > 0 && delay < 3601) {
                this.announce(delay);
            }
            if (delay > 0) {
                nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
            }

        }

        private void announce(long time) {
            if (time >= 3600 && time % 3600 == 0) {
                if (running) {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_HOUR_RE.replace("%time%", String.valueOf(time / 60 / 60)));
                } else {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_HOUR_RS.replace("%time%", String.valueOf(time / 60 / 60)));
                }
            } else if (time <= 3600 && time >= 60) {
                if (running) {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_MIN_RE.replace("%time%", String.valueOf(time / 60)));
                } else {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_MIN_RS.replace("%time%", String.valueOf(time / 60)));
                }
            } else {
                if (running) {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_SEC_RE.replace("%time%", String.valueOf(time)));
                } else {
                    GabUtils.extremeZoneBroadcast(GabConfig.EX_SEC_RS.replace("%time%", String.valueOf(time)));
                }
            }
        }
    }

    @Override
    public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
        return super.onAttack(npc, attacker, damage, isSummon);
    }

    @Override
    public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {

        if ((boss != null) && (boss == npc)) {
            boss = null;
            String msg = GabConfig.EX_EVENT_CAPTURE;
            if (msg.contains("%boss%"))
                msg = msg.replace("%boss%", npc.getName());
            GabUtils.extremeZoneBroadcast(msg);
            bossAlive = false;
            spawnCaptureNpc();
        }
        return super.onKill(npc, player, isSummon);
    }

    private void spawnCaptureNpc() {
        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            if (!isStarted())
                return;
            int BOSS = getNextZone().getNpcId();
            if (BOSS != 0 && boss == null) {
                boss = addSpawn(BOSS, getNextZone().getLocOfRaidBoss().getX(), getNextZone().getLocOfRaidBoss().getY(), getNextZone().getLocOfRaidBoss().getZ(), 0, false, 0, false, GabConfig.EXTREME_EVENT_INSTANCE_ID);
//                ((L2GrandBossInstance) boss).setOnKillDelay(100);
                ExtremeZoneManager.getInstance().addKillId(BOSS);
            }
            unSpawnEnterNpc();
            setAccessGranted(null);
        }, GabConfig.EXTREME_EVENT_MINUTESTORESPAWN * 60 * 1000);

        spawnZoneChecker();

        if (GabConfig.EXTREME_EVENT_DRAW_LINES) {
            if (GabConfig.EXTREME_EVENT_RADIUS_CHECK) {
                handleDrawn(false, true);
            } else {
                handleDrawn(false, false);
            }
        }

    }

    protected static ExtremeZoneManager instance;

    public static ExtremeZoneManager getInstance() {
        if (instance == null)
            instance = new ExtremeZoneManager();
        return instance;
    }

    private static final int STROKES = 48;
    private static final double angleIncr = 360.0 / STROKES;
    private ExServerPrimitive primitive = null;

    public void handleDrawn(boolean delete, boolean radius) {
        try {
            for (L2PcInstance player : L2World.getInstance().getPlayers()) {
                drawn(player, delete, radius);
            }
        } catch (Exception e) {

        }
    }

    public void drawn(L2PcInstance player, boolean delete, boolean radius) {
        if (radius) {
            if (!delete)
                sendCircle(player, _lastNpcSpawn.getLocation(), GabConfig.EXTREME_EVENT_RADIUS_VALUE);
            else
                deleteMarges(player);
        } else {
            if (!delete)
                sendZoneMarges(player);
            else
                deleteMarges(player);
        }
    }

    public void sendZoneMarges(L2PcInstance player) {
        L2EpicRaidCheckerZone currZone = ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2EpicRaidCheckerZone.class);
        visualizeZone(currZone.getCoors(), player);
    }

    private void visualizeZone(List<int[]> rs, L2PcInstance player) {
        Location center = getNpcSpawn().getSpawn().getLocation();

        primitive = new ExServerPrimitive("ExtremeZone", center.getX(), center.getY(), -16000);
        for (int i = 0; i < rs.size(); i++) {
            int x = rs.get(i)[0];
            int y = rs.get(i)[1];
            int nextIndex = i + 1;
            if (i == rs.size() - 1) {
                nextIndex = 0;
            }
            int nextx = rs.get(nextIndex)[0];
            int nexty = rs.get(nextIndex)[1];

            primitive.addLine(Color.blue, x, y, center.getZ(), nextx, nexty, center.getZ());
        }
        player.setDrawLines(primitive);
    }

    private void sendCircle(L2PcInstance player, Location center, int radius) {
        int prevX = radius;
        int prevY = 0;
        double angle = angleIncr;
        primitive = new ExServerPrimitive("ExtremeZone", center.getX(), center.getY(), -16000);

        for (int i = 0; i < STROKES; i++, angle += angleIncr) {
            double radians = Math.toRadians(angle);
            int x = (int) (Math.cos(radians) * radius);
            int y = (int) (Math.sin(radians) * radius);
            primitive.addLine(Color.blue, center.getX() + prevX, center.getY() + prevY, center.getZ(), center.getX() + x, center.getY() + y, center.getZ());
            prevX = x;
            prevY = y;
        }
        player.setDrawLines(primitive);
    }

    private void removeEveryoneFromZone() {
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if (player.isInsideZone(ZoneIdType.EXTREME_CHECKER)) {
                player.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, false);
                player.setInsideZone(ZoneIdType.EXTREME_CHECKER, false);
            }
        }
    }

    private void deleteMarges(L2PcInstance player) {
        if (player.getDrawLines() != null) {
            sendCircle(player, player.getLocation(), 0);
            player.setDrawLines(null);
        }
    }

    public void teleportPlayerIntoInstance(L2PcInstance player) {
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        Location loc = getNextZone().getPlayerLocs().get(Rnd.get(getNextZone().getPlayerLocs().size()));
        player.teleToLocation(loc, GabConfig.EXTREME_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
    }

    public void teleportPlayerIntoInstance(L2PcInstance player, Location loc) {
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        player.teleToLocation(loc, GabConfig.EXTREME_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
    }

}
