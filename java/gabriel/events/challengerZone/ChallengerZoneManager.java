package gabriel.events.challengerZone;


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
import l2r.gameserver.model.zone.type.L2ChallengerCheckerZone;
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
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ChallengerZoneManager extends Quest {
    private static final Logger _log = LoggerFactory.getLogger(ChallengerZoneManager.class);
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
            17130046
    };
    private ChallengerZone nextZone;
    public ChallengerStartTask _task;
    private long timeStarted = 0L;

    public ChallengerAccessChecker getChecker() {
        return _taskChecker;
    }

    private ChallengerAccessChecker _taskChecker;
    private boolean started = false;
    private long timeToStart;
    private Instance inst = null;
    private static L2Spawn _npcSpawn = null;
    private L2Npc _lastNpcSpawn = null;
    private AccessGranterCH accessGranted = null;
    private boolean closed = false;

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

    public AccessGranterCH getAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(AccessGranterCH ccAccessGranted) {
        this.accessGranted = ccAccessGranted;
    }

    private ChallengerZoneManager() {
        super(-1, ChallengerZoneManager.class.getSimpleName(), "instances");
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));

        if (GabConfig.CHALLENGER_EVENT_DIAS_RUN.contains(todayDay)) {
            if (GabConfig.CHALLENGER_EVENT_ENABLED) {
                launched = true;
                ChallengerZoneParser.getInstance().load();
                _log.info("Loaded " + ChallengerZoneHolder.getInstance().size() + " Challenger Zones into the system!");
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

    public ChallengerStartTask get_task() {
        return _task;
    }

    public ChallengerZone getNextZone() {
        return nextZone;
    }

    public void addRaidList(ChallengerZone raid) {
        ChallengerZoneHolder.getInstance().addRaid(raid);
    }

    public List<ChallengerZone> getExtremeZone() {
        return ChallengerZoneHolder.getInstance().getAllExtremes();
    }


    public L2Npc getNpcSpawn() {
        return _lastNpcSpawn;
    }


    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            for (String timeOfDay : GabConfig.CHALLENGER_EVENT_INTERVAL) {
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

            _task = new ChallengerStartTask(nextStartTime.getTimeInMillis());
            timeToStart = nextStartTime.getTimeInMillis();
            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            _log.warn("Challenge Zone [ChallengerZoneManager.scheduleEventStart()]: Error figuring out a start time. Check ChallengerEventInterval in config file.");
        }
    }


    public void startEvent() {
        if(!GabConfig.CHALLENGER_EVENT_ENABLED)
            return;
        for (String s : GabConfig.CHALLENGER_TEXT.split(";")) {
            if (s.contains("%boss%")) {
                s = s.replace("%boss%", nextZone.getName());
            }
            GabUtils.challengerZoneBroadcast(s);
        }

        if (GabConfig.CHALLENGER_EVENT_CLOSE_ALL) {
//            if (EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.REGISTERING ||
//                    EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.RUNNING) {
//                GabUtils.challengerZoneBroadcast("Ending Current event because Extreme Zone is starting!");
//                EventManager.getInstance().getMainEventManager().abort(null, false);
//            }
//            if (KOTHEvent.isStarted() || KOTHEvent.isStarting()) {
//                GabUtils.challengerZoneBroadcast("Ending King of the Hill event because Extreme Zone is starting!");
//                KOTHManager.getInstance()._task.stopped = true;
//                KOTHManager.getInstance().endEvent();
//            }
//            if (CSEvent.isStarted() || CSEvent.isStarting()) {
//                GabUtils.challengerZoneBroadcast("Ending Castle Siege event because Extreme Zone is starting!");
//                CSManager.getInstance()._task.stopped = true;
//                CSManager.getInstance().endEvent();
//            }
//            if (PartyZoneManager.getInstance().isRunning()) {
//                GabUtils.challengerZoneBroadcast("Ending PartyZone event because Extreme Zone is starting!");
//                PartyZoneManager.getInstance()._task.stopped = true;
//                PartyZoneManager.getInstance().endEvent();
//            }
//            if (EpicRaidManager.getInstance().isStarted()) {
//                GabUtils.challengerZoneBroadcast("Ending Epic Raid event because Extreme Zone is starting!");
//                EpicRaidManager.getInstance()._task.cancel = true;
//                EpicRaidManager.getInstance().endEvent(false);
//            }
//            if (PVPInstance.getInstance().isActive()) {
//                GabUtils.challengerZoneBroadcast("Ending PvPInstance event because Extreme Zone is starting!");
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

        if (!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK)
            ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2ChallengerCheckerZone.class).setActive(true);
//        _task.setStartTime(System.currentTimeMillis() + 60000L * GabConfig.CHALLENGER_EVENT_RUNNING_TIME);
//        _task = new ExtremeStartTask(System.currentTimeMillis() + 60000L * GabConfig.CHALLENGER_EVENT_RUNNING_TIME);
//        ThreadPoolManager.getInstance().executeEvent(_task);
        handleCreateInstance();
        setStarted(true);
        spawnCaptureNpc();
        timeStarted = Calendar.getInstance().getTimeInMillis();

    }

    private void spawnZoneChecker() {
        L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(GabConfig.CHALLENGER_EVENT_NPC_ID);
        if (tmpl == null) {
            _log.warn("ChallengerZoneManager[ChallengerZoneManager.spawnEnterNpc()]: L2NpcTemplate is a NullPointer -> Invalid npc id in configs?");
            return;
        }
        try {
            _npcSpawn = new L2Spawn(tmpl);
            if(_npcSpawn == null)
                System.out.println("npcspawn null");
            if(getNextZone() == null)
                System.out.println("getNextZone() null");
            if(getNextZone().getTeleNpc() == null)
                System.out.println("getNextZone().getTeleNpc() null");
            _npcSpawn.setLocation(getNextZone().getTeleNpc());/////// erro aki denovo...
            _npcSpawn.setAmount(1);
            _npcSpawn.setHeading(0);
            _npcSpawn.setRespawnDelay(1);
            SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
            _npcSpawn.init();
            _lastNpcSpawn = _npcSpawn.getLastSpawn();
            _lastNpcSpawn.setCurrentHp(_lastNpcSpawn.getMaxHp());
            _lastNpcSpawn.getTemplate().setTitle("Challenger Zone Event");
            _lastNpcSpawn.isAggressive();
            _lastNpcSpawn.decayMe();
            _lastNpcSpawn.setInstanceId(GabConfig.CHALLENGER_EVENT_INSTANCE_ID);
            _lastNpcSpawn.spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());
            _lastNpcSpawn.broadcastPacket(new MagicSkillUse(_lastNpcSpawn, _lastNpcSpawn, 1034, 1, 1, 1));
        } catch (Exception e) {
            _log.warn("ChallengerZoneManager[ChallengerZoneManager.spawnEnterNpc()]: exception: " + e.getMessage(), e);
        }
        _taskChecker = new ChallengerAccessChecker();
        ThreadPoolManager.getInstance().executeEvent(_taskChecker);

    }

    private void unSpawnEnterNpc() {
        if (_lastNpcSpawn != null) {
            if (GabConfig.CHALLENGER_EVENT_DRAW_LINES) {
                if (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
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
            GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_EVENT_FINISH);
            if (!GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
                ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2ChallengerCheckerZone.class).setActive(false);
                removeEveryoneFromZone();
            }
        } catch (Exception e) {
            //
        }
        try {
            ChallengerZoneTimer.getInstance(inst).rewardPlayers();
            ChallengerZoneTimer.getInstance(inst).getPlayersInside().clear();
        } catch (Exception e) {
            //
        }
        try {
            ChallengerZoneTimer.getInstance(inst).stop();

        } catch (Exception e) {
            //
        }
        try {
            nextRandomExtremeZone();
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
            if (GabConfig.CHALLENGER_EVENT_DRAW_LINES) {
                if (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
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
        int templateId = GabConfig.CHALLENGER_EVENT_INSTANCE_ID;
        inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst == null) {
            InstanceManager.getInstance().createInstance(templateId);
            inst = InstanceManager.getInstance().getInstance(templateId);
            inst.setPvPInstance(true);
            inst.setDuration(GabConfig.CHALLENGER_EVENT_RUNNING_TIME * 60000);
            inst.setShowTimer(true);
            inst.setTimerIncrase(false);
            inst.setTimerText("Zone Ends In");
            inst.setAllowSummon(false);
            inst.setExitLoc(new Location(82962, 148623, -3468));

            for (int door : doors) {
                StatsSet set = new StatsSet();
                set.add(DoorData.getInstance().getDoorTemplate(door));
                InstanceManager.getInstance().getInstance(GabConfig.CHALLENGER_EVENT_INSTANCE_ID).addDoor(door, set);
            }

            ChallengerZoneTimer.getInstance(inst).startInsideTimer();
        }
    }

    private void handleDestroyInstance() {
        int templateId = GabConfig.CHALLENGER_EVENT_INSTANCE_ID;
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
        int templateId = GabConfig.CHALLENGER_EVENT_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            return timeStarted < Calendar.getInstance().getTimeInMillis() ? "---" : GabUtils.getTimeRemaining(timeStarted);
        }
        return "----";
    }


    public class ChallengerStartTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;
        boolean running = false;
        public boolean cancel = false;

        public ChallengerStartTask(long startTime) {
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
                    ChallengerZoneManager.getInstance().endEvent();
                } else {
                    running = true;
                    ChallengerZoneManager.getInstance().startEvent();
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
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_HOUR_RE.replace("%time%", String.valueOf(time / 60 / 60)));
                } else {
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_HOUR_RS.replace("%time%", String.valueOf(time / 60 / 60)));
                }
            } else if (time <= 3600 && time >= 60) {
                if (running) {
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_MIN_RE.replace("%time%", String.valueOf(time / 60)));
                } else {
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_MIN_RS.replace("%time%", String.valueOf(time / 60)));
                }
            } else {
                if (running) {
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_SEC_RE.replace("%time%", String.valueOf(time)));
                } else {
                    GabUtils.challengerZoneBroadcast(GabConfig.CHALLENGER_SEC_RS.replace("%time%", String.valueOf(time)));
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

        spawnCaptureNpc();
        return super.onKill(npc, player, isSummon);
    }

    private void spawnCaptureNpc() {
        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            if (!isStarted())
                return;
            setAccessGranted(null);
        }, GabConfig.CHALLENGER_EVENT_MINUTESTORESPAWN * 60 * 1000);

        spawnZoneChecker();

        if (GabConfig.CHALLENGER_EVENT_DRAW_LINES) {
            if (GabConfig.CHALLENGER_EVENT_RADIUS_CHECK) {
                handleDrawn(false, true);
            } else {
                handleDrawn(false, false);
            }
        }

    }

    protected static ChallengerZoneManager instance;

    public static ChallengerZoneManager getInstance() {
        if (instance == null)
            instance = new ChallengerZoneManager();
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
                sendCircle(player, _lastNpcSpawn.getLocation(), GabConfig.CHALLENGER_EVENT_RADIUS_VALUE);
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
        L2ChallengerCheckerZone currZone = ZoneManager.getInstance().getZoneById(getNextZone().getNpcId(), L2ChallengerCheckerZone.class);
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

        if(!GabConfig.CHALLENGER_EVENT_ENABLED)
            return;

        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if (player.isInsideZone(ZoneIdType.CHALLENGER_CHECKER)) {
                player.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, false);
                player.setInsideZone(ZoneIdType.CHALLENGER_CHECKER, false);
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

        if(!GabConfig.CHALLENGER_EVENT_ENABLED)
            return;

        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        if(!canEnter(player)){
            player.sendMessage("Please come back when you have an party with 9 people!");
            return;
        }

        Location loc = getNextZone().getPlayerLocs().get(Rnd.get(getNextZone().getPlayerLocs().size()));
        player.teleToLocation(loc, GabConfig.CHALLENGER_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
    }

    public boolean canEnter(L2PcInstance player){
        if(player.getParty() == null || player.getParty().getMemberCount() < 9)
            return false;
        return true;
    }

    public void handlePartyLeave(L2PcInstance player){
        if(player.isInParty() && GabConfig.CHALLENGER_EVENT_ENABLED){
            for (L2PcInstance member : player.getParty().getMembers()) {
                if (member.isInsideZone(ZoneIdType.CHALLENGER_CHECKER)) {
                    member.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, false);
                    member.setInsideZone(ZoneIdType.CHALLENGER_CHECKER, false);
                }
                if(member.getInstanceId() == GabConfig.CHALLENGER_EVENT_INSTANCE_ID){
                    player.teleToLocation(new Location(83208, 147672, -3494), 0, Config.MAX_OFFSET_ON_TELEPORT);
                }
            }
        }
    }

    public void teleportPlayerIntoInstance(L2PcInstance player, Location loc) {

        if(!GabConfig.CHALLENGER_EVENT_ENABLED)
            return;
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }

        if(!canEnter(player)){
            player.sendMessage("Please come back when you have an party with 9 people!");
            return;
        }

        player.teleToLocation(loc, GabConfig.CHALLENGER_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
    }

}
