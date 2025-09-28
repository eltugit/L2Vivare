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
package gabriel.PartyZone;


import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gr.sr.interf.SunriseEvents;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2DoorInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.util.Broadcast;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class PartyZoneManager {
    protected static final Logger _log = Logger.getLogger(PartyZoneManager.class.getName());

    public PartyZoneTask _task;
    private final List<Integer> bosses = new ArrayList<>();
    private final List<Integer> npcIds = new ArrayList<>();
    private final List<Integer> npcIds2 = new ArrayList<>();
    private final List<Integer> npcIdsMed = new ArrayList<>();
    private final List<Integer> npcIdsMed2 = new ArrayList<>();
    private final List<Integer> npcIdsHard = new ArrayList<>();
    private final List<Integer> npcIdsHard2 = new ArrayList<>();
    private final List<Integer> npcIdsvip = new ArrayList<>();
    private final int respawnDelay = Integer.parseInt(GabConfig.PARTY_AREA_RESPAWN_DELAY);
    private Random r = new Random();
    private long timeToStart;
    private long timeStarted = 0L;

    public boolean isRunning() {
        return running;
    }

    private boolean running;

    private PartyZoneManager() {
        AdminCommandHandler.getInstance().registerHandler(new AdminPartyZoneEvent());
        this.scheduleEventStart();
    }


    public static PartyZoneManager getInstance() {
        return SingletonHolder._instance;
    }

    public void scheduleEventStart() {
        try {
            Calendar currentTime = Calendar.getInstance();
            Calendar nextStartTime = null;
            Calendar testStartTime = null;
            for (String timeOfDay : GabConfig.PARTY_AREA_TIME.split(",")) {
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
            _task = new PartyZoneTask(nextStartTime.getTimeInMillis());

            timeToStart = nextStartTime.getTimeInMillis();

            ThreadPoolManager.getInstance().executeEvent(_task);
        } catch (Exception e) {
            System.out.println("Could not parse PartyZoneHunt reward timer!");
        }
    }

    public void startEvent() {
        for (String s : GabConfig.PARTY_AREA_NPCS.split(",")) {
            npcIds.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCS2.split(",")) {
            npcIds2.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCSMED.split(",")) {
            npcIdsMed.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCSMED2.split(",")) {
            npcIdsMed2.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCSHARD.split(",")) {
            npcIdsHard.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCSHARD2.split(",")) {
            npcIdsHard2.add(Integer.valueOf(s));
        }
        for (String s : GabConfig.PARTY_AREA_NPCSVIP.split(",")) {
            npcIdsvip.add(Integer.valueOf(s));
        }

        int templateId = GabConfig.PARTY_AREA_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst == null) {
            InstanceManager.getInstance().createInstance(templateId);
            inst = InstanceManager.getInstance().getInstance(templateId);
            inst.setPvPInstance(false);
            inst.setDuration(Integer.parseInt(GabConfig.PARTY_AREA_DURATION) * 60000);

            for (int door : Arrays.asList(20210001,20210002,20210003)) {
                StatsSet set = new StatsSet();
                set.add(DoorData.getInstance().getDoorTemplate(door));
                InstanceManager.getInstance().getInstance(GabConfig.PARTY_AREA_INSTANCE_ID).addDoor(door, set);
            }

            for (L2DoorInstance door : InstanceManager.getInstance().getInstance(GabConfig.PARTY_AREA_INSTANCE_ID).getDoors()) {
                door.setOpen(true);
            }

            inst.setShowTimer(true);
            inst.setTimerIncrase(false);
            inst.setTimerText("Zone Ends In");

            inst.setAllowSummon(false);
            inst.setExitLoc(new Location(83464, 148616, -3408));
        }

        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCS.split(";")), npcIds, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCS2.split(";")), npcIds2, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCSMED.split(";")), npcIdsMed, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCSMED2.split(";")), npcIdsMed2, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCSHARD.split(";")), npcIdsHard, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCSHARD2.split(";")), npcIdsHard2, templateId);
        shandleSpawns(Arrays.asList(GabConfig.PARTY_AREA_LOCSVIP.split(";")), npcIdsvip, templateId);
        spawnBoss(GabConfig.BOSSINFOEZ);
        spawnBoss(GabConfig.BOSSINFOMED);
        spawnBoss(GabConfig.BOSSINFOHARD);
        spawnBoss(GabConfig.BOSSINFOVIP);
        running = true;
        timeStarted = Calendar.getInstance().getTimeInMillis();
        _task.setStartTime(System.currentTimeMillis() + (Integer.parseInt(GabConfig.PARTY_AREA_DURATION) * 60 * 1000));
        ThreadPoolManager.getInstance().executeEvent(_task);
    }

    public void shandleSpawns(List<String> locations, List<Integer> npcIds, int templateId){

        for (String location : locations) {
            L2NpcTemplate template1;
            int monsterTemplate = npcIds.get(r.nextInt(npcIds.size()));
            template1 = NpcTable.getInstance().getTemplate(monsterTemplate);

            int x = Integer.parseInt(location.split(",")[0]);
            int y = Integer.parseInt(location.split(",")[1]);
            int z = Integer.parseInt(location.split(",")[2]);

            try {
                L2Spawn spawn = new L2Spawn(template1);
                spawn.setCustom(true);
                spawn.setLocation(new Location(x, y, z));
                spawn.setAmount(1);
                spawn.setRespawnDelay(respawnDelay);
                spawn.setInstanceId(templateId);
                SpawnTable.getInstance().addNewSpawn(spawn, false);
                spawn.init();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void spawnBoss(String bosst){
        bosst = bosst.trim().replace(" ","");
        if(bosst.isEmpty())
            return;

        for (String boss : bosst.split("~")) {
            String[] bsplit = boss.split(";");
            int bossId = Integer.parseInt(bsplit[0]);
            bosses.add(bossId);
            String[] location = bsplit[1].split(",");
            int x = Integer.parseInt(location[0]);
            int y = Integer.parseInt(location[1]);
            int z = Integer.parseInt(location[2]);
            L2NpcTemplate template1 = NpcTable.getInstance().getTemplate(bossId);
            try {
                L2Spawn spawn = new L2Spawn(template1);
                spawn.setCustom(true);
                spawn.setLocation(new Location(x, y, z));
                spawn.setAmount(1);
                spawn.setRespawnDelay(0);
                spawn.setInstanceId(GabConfig.PARTY_AREA_INSTANCE_ID);
                spawn.stopRespawn();
                SpawnTable.getInstance().addNewSpawn(spawn, false);
                spawn.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void teleportPlayerIntoInstance(L2PcInstance player, String type) {
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return;
        }
        if (GabConfig.ER_EVENT_CLOSE_ALL && EpicRaidManager.getInstance().isStarted() && !EpicRaidManager.getInstance().isClosed()) {
            player.sendMessage("Can't enter because Epic Raid is Running now!");
            return;
        }
        String tp = "";
        switch (type){
            case "ez":
                tp = GabConfig.PARTY_AREA_PLAYER_TELEPORT;
                break;
            case "med":
                tp = GabConfig.PARTY_AREA_PLAYER_TELEPORTMED;
                break;
            case "vip":
                tp = GabConfig.PARTY_AREA_PLAYER_TELEPORTVIP;
                break;
            default:
                tp = GabConfig.PARTY_AREA_PLAYER_TELEPORTHARD;
                break;
        }
        if(type.equals("vip") && !player.isPremium()){
            player.sendMessage("This is an premium only zone.");
            return;
        }
        String[] locs = tp.split(",");
        Location loc = new Location(Integer.parseInt(locs[0]), Integer.parseInt(locs[1]), Integer.parseInt(locs[2]));
        player.teleToLocation(loc, GabConfig.PARTY_AREA_INSTANCE_ID, Config.MAX_OFFSET_ON_TELEPORT);
        L2Skill flames = SkillData.getInstance().getInfo(1427, 1);
        if (flames != null)
            flames.getEffects(player, player);
    }

    public void skipDelay() {
        if (_task.nextRun.cancel(false)) {
            _task.setStartTime(System.currentTimeMillis());
            ThreadPoolManager.getInstance().executeEvent(_task);
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
        int templateId = GabConfig.PARTY_AREA_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            return timeStarted < Calendar.getInstance().getTimeInMillis() ? "---" : GabUtils.getTimeRemaining(timeStarted);
        }
        return "----";
    }


    public void endEvent() {
        if (!running)
            return;
        int templateId = GabConfig.PARTY_AREA_INSTANCE_ID;
        Instance inst = InstanceManager.getInstance().getInstance(templateId);
        if (inst != null) {
            InstanceManager.getInstance().destroyInstance(templateId);
        }

        finalizeSpawns(npcIds);
        finalizeSpawns(npcIdsMed);
        finalizeSpawns(npcIdsHard);
        finalizeSpawns(npcIdsvip);
        finalizeSpawns(bosses);
        running = false;
        this.scheduleEventStart();
    }

    private void finalizeSpawns(List<Integer> list){
        for (int npcId : list) {
            for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(npcId)) {
                spawn.stopRespawn();
//                spawn.getLastSpawn().deleteMe();
                SpawnTable.getInstance().deleteSpawn(spawn, false);
            }
        }
        list.clear();
    }

    public class PartyZoneTask implements Runnable {
        private long _startTime;
        public ScheduledFuture<?> nextRun;

        public PartyZoneTask(long startTime) {
            _startTime = startTime;
            stopped = false;
        }

        public void setStartTime(long startTime) {
            _startTime = startTime;
        }

        public boolean stopped;

        public void run() {
            int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);

            if (stopped)
                return;

            if (delay > 0 && delay < 3601) {
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
                if (running) {
                    Broadcast.toAllOnlinePlayers("Party Zone Spawns have been finished");
                    endEvent();
                } else {
                    Broadcast.toAllOnlinePlayers("Starting Party Zone Spawns");
                    startEvent();
                }
            }

            if (delay > 0) {
                nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
            }
        }

        private void announce(long time) {
            if (time > 5401) {
                if (running) {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + (time / 60 / 60) + " Hour(s) until finish!");
                } else {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + (time / 60 / 60) + " Hour(s) until start!");
                }
            } else if (time <= 5400 && time >= 60) {
                if (running) {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + (time / 60) + " minute(s) until finish!");
                } else {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + (time / 60) + " minute(s) until start!");
                }
            } else {
                if (running) {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + time + " second(s) until finish!");
                } else {
                    Broadcast.toAllOnlinePlayers("Party Zone: " + time + " second(s) until start!");
                }
            }
        }
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final PartyZoneManager _instance = new PartyZoneManager();
    }
}
