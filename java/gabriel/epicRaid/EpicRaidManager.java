package gabriel.epicRaid;


import gabriel.PartyZone.PartyZoneManager;
import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.events.siegeRank.EpicRaidRankManager;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.main.MainEventManager;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class EpicRaidManager extends Quest {
    private static final Logger _log = LoggerFactory.getLogger(EpicRaidManager.class);

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
            20210001,
            20210002,
            20210003,
            17130046
    };

    private long timeStarted = 0L;

    private EpicRaid nextRaid;
    private ErStartTask _task;


    public EpicRaidAccessChecker getChecker() {
        return _taskChecker;
    }

    private EpicRaidAccessChecker _taskChecker;
    private boolean started = false;
    private long timeToStart;
    private Instance inst = null;
    private L2Npc boss = null;
    private static L2Spawn _npcSpawn = null;
    private L2Npc _lastNpcSpawn = null;
    private AccessGranter accessGranted = null;
    private boolean closed = false;


    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }


    public AccessGranter getAccessGranted() {
        return accessGranted;
    }

    public void setAccessGranted(AccessGranter ccAccessGranted) {
        this.accessGranted = ccAccessGranted;
    }

    public boolean launched = false;


    public boolean isLaunched() {
        return launched;
    }

    private EpicRaidManager() {
        super(-1, EpicRaidManager.class.getSimpleName(), "instances");
        Calendar todayCalender = Calendar.getInstance();
        String todayDay = String.valueOf(todayCalender.get(Calendar.DAY_OF_WEEK));

        if (GabConfig.ER_EVENT_DIAS_RUN.contains(todayDay)) {
            if (GabConfig.ER_EVENT_ENABLED) {
                launched = true;
                EpicRaidParser.getInstance().load();
                _log.info("Loaded " + EpicRaidHolder.getInstance().size() + " Epic raids into the system!");
                scheduleEventStart();
                nextRandomRaid();
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

    public ErStartTask get_task() {
        return _task;
    }

    public EpicRaid getNextRaid() {
        return nextRaid;
    }

    public void addRaidList(EpicRaid raid) {
        EpicRaidHolder.getInstance().addRaid(raid);
    }

    public List<EpicRaid> getRaidList() {
        return EpicRaidHolder.getInstance().getAllRaids();
    }


    public L2Npc getNpcSpawn() {
        return _lastNpcSpawn;
    }


    int timeIndex = 0;

    public int getNpcIdForToday(long time){
        List<Integer> npcId = new LinkedList<>();
        Calendar todayCalender = Calendar.getInstance();
        if(time > 0){
            todayCalender.setTimeInMillis(time);
        }
        int todayDay = todayCalender.get(Calendar.DAY_OF_WEEK);
        switch (todayDay) {
            case 1:
                npcId = GabConfig.ER_BOSS_SUNDAY;
                break;
            case 2:
                npcId = GabConfig.ER_BOSS_MONDAY;
                break;
            case 3:
                npcId = GabConfig.ER_BOSS_TUESDAY;
                break;
            case 4:
                npcId = GabConfig.ER_BOSS_WEDNESDAY;
                break;
            case 5:
                npcId = GabConfig.ER_BOSS_THURSDAY;
                break;
            case 6:
                npcId = GabConfig.ER_BOSS_FRIDAY;
                break;
            case 7:
                npcId = GabConfig.ER_BOSS_SATURDAY;
                break;
            default:
                npcId = new LinkedList<>();
                break;
        }

        try{
            npcId.get(timeIndex);
        }catch (Exception e){
            timeIndex = 0;
        }

        return npcId.get(timeIndex) == null ?
                npcId.get(0) == null ?
                        -1 :
                        npcId.get(0)
                : npcId.get(timeIndex);
    }

    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            timeIndex = 0;
            for (String timeOfDay : GabConfig.ER_EVENT_INTERVAL) {
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
                if(nextStartTime != null && testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
                    timeIndex++;

                if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()) {
                    nextStartTime = testStartTime;
                }
            }
            _task = new ErStartTask(nextStartTime.getTimeInMillis());
            timeToStart = nextStartTime.getTimeInMillis();
            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            _log.warn("Epic Raid [EpicRaidManager.scheduleEventStart()]: Error figuring out a start time. Check EREventInterval in config file.");
        }
    }


    public void startEvent() {
        GabUtils.yellowBroadcast("Event is now active!", "Epic Boss");
        closed = false;
        EpicRaidRankManager.getInstance().resetStart();
        if (GabConfig.ER_EVENT_CLOSE_ALL) {
            if (EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.REGISTERING ||
                    EventManager.getInstance().getMainEventManager().get_state() == MainEventManager.State.RUNNING) {
                GabUtils.yellowBroadcast("Ending Current event because Epic Raid is starting!", "Epic Boss");
                EventManager.getInstance().getMainEventManager().abort(null, false);
            }
            if (PartyZoneManager.getInstance().isRunning()) {
                GabUtils.yellowBroadcast("Ending PartyZone event because Epic Raid is starting!", "Epic Boss");
                PartyZoneManager.getInstance()._task.stopped = true;
                PartyZoneManager.getInstance().endEvent();
            }
        }

        if (!GabConfig.ER_EVENT_RADIUS_CHECK)
            ZoneManager.getInstance().getZoneById(getNextRaid().getNpcId(), L2EpicRaidCheckerZone.class).setActive(true);
        _task.setStartTime(System.currentTimeMillis() + 60000L * GabConfig.ER_EVENT_RUNNING_TIME);
        ThreadPoolManager.getInstance().executeEvent(_task);
        handleCreateInstance();
        setStarted(true);
        timeStarted = Calendar.getInstance().getTimeInMillis();

        spawnEnterNpc();
        if (GabConfig.ER_EVENT_DRAW_LINES) {
            if (GabConfig.ER_EVENT_RADIUS_CHECK) {
                handleDrawn(false, true);
            } else {
                handleDrawn(false, false);
            }
        }
        _taskChecker = new EpicRaidAccessChecker();
        ThreadPoolManager.getInstance().executeEvent(_taskChecker);


    }

    private void spawnEnterNpc() {
        L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(GabConfig.ER_EVENT_NPC_ID);
        if (tmpl == null) {
            _log.warn("EpicRaidManager[EpicRaidManager.spawnEnterNpc()]: L2NpcTemplate is a NullPointer -> Invalid npc id in configs?");
            return;
        }
        try {
            _npcSpawn = new L2Spawn(tmpl);
            _npcSpawn.setLocation(getNextRaid().getTeleNpc());
            _npcSpawn.setAmount(1);
            _npcSpawn.setHeading(0);
            _npcSpawn.setRespawnDelay(1);
            SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
            _npcSpawn.init();
            _lastNpcSpawn = _npcSpawn.getLastSpawn();
            _lastNpcSpawn.setCurrentHp(_lastNpcSpawn.getMaxHp());
            _lastNpcSpawn.getTemplate().setTitle("Epic Boss Manager");
            _lastNpcSpawn.isAggressive();
            _lastNpcSpawn.decayMe();
            _lastNpcSpawn.spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());
            _lastNpcSpawn.broadcastPacket(new MagicSkillUse(_lastNpcSpawn, _lastNpcSpawn, 1034, 1, 1, 1));
        } catch (Exception e) {
            _log.warn("EpicRaidManager[EpicRaidManager.spawnEnterNpc()]: exception: " + e.getMessage(), e);
        }
    }

    private void unSpawnEnterNpc() {
        if(_lastNpcSpawn != null) {
            _lastNpcSpawn.deleteMe();
            SpawnTable.getInstance().deleteSpawn(_lastNpcSpawn.getSpawn(), false);
        }
        if(_npcSpawn != null) {
            _npcSpawn.stopRespawn();
        }
        _npcSpawn = null;
        _lastNpcSpawn = null;
    }

    boolean rewarded = false;

    public void endEvent(boolean killed) {
        _taskChecker.cancel();
        closed = false;
        if (killed) {
            GabUtils.yellowBroadcast("Epic Boss has been killed! See the Community Board for the next raid!", "Epic Boss");
        } else {
            GabUtils.yellowBroadcast("Epic Boss Event has now ended! See the Community Board for the next raid!", "Epic Boss");
        }

        if(!rewarded){

            EpicRaidTimer.getInstance(inst).rewardPlayers();
            //vai enviar so se o boss for morto.
            EpicRaidRankManager.getInstance().sendToEveryOne();
            rewarded = true;
        }
        EpicRaidTimer.getInstance(inst).getPlayersInside().clear();
        EpicRaidTimer.getInstance(inst).stop();
        setStarted(false);


//        long minutes = 10 * 60 * 1000;
//        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
        if (!GabConfig.ER_EVENT_RADIUS_CHECK) {
            ZoneManager.getInstance().getZoneById(getNextRaid().getNpcId(), L2EpicRaidCheckerZone.class).setActive(false);
            removeEveryoneFromZone();
        }

        if (boss != null) {
            boss.deleteMe();
            SpawnTable.getInstance().deleteSpawn(boss.getSpawn(), false);
            boss.getSpawn().stopRespawn();
            boss = null;
        }
        handleDestroyInstance();
        this._task.cancel = true;
//        }, minutes);


        if (GabConfig.ER_EVENT_DRAW_LINES) {
            if (GabConfig.ER_EVENT_RADIUS_CHECK) {
                handleDrawn(true, true);
            } else {
                handleDrawn(true, false);
            }
        }
        unSpawnEnterNpc();
        this.scheduleEventStart();
        nextRandomRaid();
    }

    public void skipDelay() {
        if (_task.nextRun.cancel(false)) {
            _task.setStartTime(System.currentTimeMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
        }
    }

    public void nextRandomRaid() {
        int npcId = getNpcIdForToday(timeToStart);
        if(npcId == -1){
            nextRaid = getRaidList().get(Rnd.get(getRaidList().size() - 1));
        }else{
            Optional<EpicRaid> dd = getRaidList().stream().filter(e->e.getNpcId() == getNpcIdForToday(timeToStart)).findFirst();
            nextRaid = dd.orElseGet(() -> getRaidList().get(Rnd.get(getRaidList().size() - 1)));
        }
    }

    private void handleCreateInstance() {
        int templateId = GabConfig.ER_EVENT_INSTANCE_ID;
        inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst == null) {
            InstanceManager.getInstance().createInstance(templateId);
            inst = InstanceManager.getInstance().getInstance(templateId);
            inst.setPvPInstance(true);
            inst.setDuration(GabConfig.ER_EVENT_RUNNING_TIME * 60000);
            inst.setShowTimer(true);
            inst.setTimerIncrase(false);
            inst.setTimerText("Zone Ends In");

            inst.setAllowSummon(false);
            inst.setExitLoc(new Location(82840, 148616, -3472));
//            for (L2DoorInstance door : DoorData.getInstance().getDoors()) {
//                StatsSet set = new StatsSet();
//                set.add(DoorData.getInstance().getDoorTemplate(door.getId()));
//                InstanceManager.getInstance().getInstance(GabConfig.ER_EVENT_INSTANCE_ID).addDoor(door.getId(), set);
//            }
            for (int door : doors) {
                StatsSet set = new StatsSet();
                set.add(DoorData.getInstance().getDoorTemplate(door));
                InstanceManager.getInstance().getInstance(GabConfig.ER_EVENT_INSTANCE_ID).addDoor(door, set);
            }

            int BOSS = getNextRaid().getNpcId();
            if (BOSS != 0) {
                boss = addSpawn(BOSS, getNextRaid().getLocOfRaidBoss().getX(), getNextRaid().getLocOfRaidBoss().getY(), getNextRaid().getLocOfRaidBoss().getZ(), 0, false, 0, false, GabConfig.ER_EVENT_INSTANCE_ID);
//                ((L2GrandBossInstance) boss).setOnKillDelay(100);
                EpicRaidManager.getInstance().addKillId(BOSS);
                EpicRaidManager.getInstance().addAttackId(BOSS);
            }

            EpicRaidTimer.getInstance(inst).startInsideTimer();
        }
    }

    private void handleDestroyInstance() {
        int templateId = GabConfig.ER_EVENT_INSTANCE_ID;
        inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            inst.setDuration(60 * 1000); // 30 segundos...
            inst.setEmptyDestroyTime(0);
        }
    }


    public void teleportPlayerIntoInstance(L2PcInstance player) {
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        Location loc = getNextRaid().getPlayerTeleports().get(Rnd.get(getNextRaid().getPlayerTeleports().size() - 1));
        player.teleToLocation(loc, GabConfig.ER_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
    }


    public void teleportPlayerIntoInstance(L2PcInstance player, Location loc) {
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        player.teleToLocation(loc, GabConfig.ER_EVENT_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
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
        int templateId = GabConfig.ER_EVENT_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            return timeStarted < Calendar.getInstance().getTimeInMillis() ? "---" : GabUtils.getTimeRemaining(timeStarted);
        }
        return "----";
    }


    class ErStartTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;
        boolean running = false;
        boolean cancel = false;

        public ErStartTask(long startTime) {
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
                    EpicRaidManager.getInstance().endEvent(false);
                } else {
                    running = true;
                    rewarded = false;
                    EpicRaidManager.getInstance().startEvent();
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

                    GabUtils.yellowBroadcast((time / 60 / 60) + " hour(s) until event is finished!", "Epic Boss");
                } else {
                    GabUtils.yellowBroadcast((time / 60 / 60) + " hour(s) until event starts!", "Epic Boss");
                }
            } else if (time >= 60) {
                if (running) {
                    GabUtils.yellowBroadcast((time / 60) + " minute(s) until the event is finished!", "Epic Boss");
                } else {
                    GabUtils.yellowBroadcast((time / 60) + " minute(s) until the event starts!", "Epic Boss");
                }
            } else {
                if (running) {
                    GabUtils.yellowBroadcast(time + " second(s) until the event is finished!", "Epic Boss");
                } else {
                    GabUtils.yellowBroadcast(time + " second(s) until the event starts!", "Epic Boss");
                }
            }
        }
    }

    @Override
    public String onAttack(L2Npc npc, L2PcInstance attacker, int damage, boolean isSummon) {
        if (boss != null && boss == npc) {
            int remaindedHpPercent = (int) ((npc.getCurrentHp() * 100) / npc.getMaxHp());
            if (remaindedHpPercent <= GabConfig.ER_EVENT_PERCENT_CLOSE) {
                if (!isClosed())
                    setClosed(true);
            }
        }
        return super.onAttack(npc, attacker, damage, isSummon);
    }

    @Override
    public final String onKill(L2Npc npc, L2PcInstance player, boolean isSummon) {

        // boss was killed, finish instance
        if ((boss != null) && (boss == npc)) {

            endEvent(true);
        }
        return super.onKill(npc, player, isSummon);
    }

    protected static EpicRaidManager instance;


    public static EpicRaidManager getInstance() {
        if (instance == null)
            instance = new EpicRaidManager();
        return instance;
    }

    private static final int STROKES = 48;
    private static final double angleIncr = 360.0 / STROKES;
    private ExServerPrimitive primitive = null;

    public void handleDrawn(boolean delete, boolean radius) {
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if (radius) {
                if (!delete)
                    sendCircle(player, _lastNpcSpawn.getLocation(), GabConfig.ER_EVENT_RADIUS_VALUE);
                else
                    deleteMarges(player);
            } else {
                if (!delete)
                    sendZoneMarges(player);
                else
                    deleteMarges(player);
            }
        }
    }

    public void sendZoneMarges(L2PcInstance player) {
        L2EpicRaidCheckerZone currZone = ZoneManager.getInstance().getZoneById(getNextRaid().getNpcId(), L2EpicRaidCheckerZone.class);
        visualizeZone(currZone.getCoors(), player);
    }

    private void visualizeZone(List<int[]> rs, L2PcInstance player) {
        Location center = getNpcSpawn().getSpawn().getLocation();

        primitive = new ExServerPrimitive("EpicRaidZone", center.getX(), center.getY(), -16000);
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
        primitive = new ExServerPrimitive("EpicRaidZone", center.getX(), center.getY(), -16000);

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
            if (player.isInsideZone(ZoneIdType.EPIC_RAID_CHECKER)) {
                player.setInsideZone(ZoneIdType.NO_SUMMON_FRIEND, false);
                player.setInsideZone(ZoneIdType.EPIC_RAID_CHECKER, false);
            }
        }
    }

    private void deleteMarges(L2PcInstance player) {
        if (player.getDrawLines() != null) {
            sendCircle(player, player.getLocation(), 0);
            player.setDrawLines(null);
        }
    }
}
