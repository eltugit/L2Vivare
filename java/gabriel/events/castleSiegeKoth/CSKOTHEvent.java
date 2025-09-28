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
import gr.sr.utils.StringUtil;
import javolution.util.FastMap;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.SpawnTable;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.enums.Team;
import l2r.gameserver.instancemanager.AntiFeedManager;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2DoorInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.instance.L2PetInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;
import l2r.gameserver.model.itemcontainer.PcInventory;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.*;
import l2r.util.Rnd;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class CSKOTHEvent {
    enum EventState {
        INACTIVE,
        INACTIVATING,
        PARTICIPATING,
        STARTING,
        STARTED,
        REWARDING
    }
    public static CSKOTHEventTeam winner = null;

    protected static CSKOTHEvent instance;

    public static CSKOTHEvent getInstance() {
        if (instance == null)
            instance = new CSKOTHEvent();
        return instance;
    }
    public static CSKOTHEventTeam owner = null;
    private static final int crystalRange = 115;
    protected static final Logger _log = Logger.getLogger(CSKOTHEvent.class.getName());
    /**
     * html path
     **/
    private static final String htmlPath = "data/html/gabriel/events/CastleSiegeKingOfTheHill/";
    /**
     * The teams of the KothEvent<br>
     */
    public static final CSKOTHEventTeam[] _teams = new CSKOTHEventTeam[2];
    /**
     * The state of the KothEvent<br>
     */
    private static EventState _state = EventState.INACTIVE;
    /**
     * The spawn of the participation npc<br>
     */
    private static L2Spawn _npcSpawn = null;
    /**
     * the npc instance of the participation npc<br>
     */
    private static L2Npc _lastNpcSpawn = null;
    /**
     * Instance id<br>
     */
    private static int _castleSiegeInstance = 0;

    /**
     * No instance of this class!<br>
     */
    private CSKOTHEvent() {
    }

    /**
     * Teams initializing<br>
     */

    
    public static boolean canAttackDoor(L2Character attacker){
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if(CSKOTHEvent.getParticipantTeam(attacker.getObjectId()) != null && CSKOTHEvent.getParticipantTeam(attacker.getObjectId()).isAttacking())
            return true;
        return false;
    }

    public static void init() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        AntiFeedManager.getInstance().registerEvent(AntiFeedManager.KOTH_ID);
        _teams[0] = new CSKOTHEventTeam(GabConfig.CSKOTH_EVENT_TEAM_1_NAME, GabConfig.CSKOTH_EVENT_TEAM_1_COORDINATES);
        _teams[1] = new CSKOTHEventTeam(GabConfig.CSKOTH_EVENT_TEAM_2_NAME, GabConfig.CSKOTH_EVENT_TEAM_2_COORDINATES);
    }

    /**
     * Starts the participation of the KothEvent<br>
     * 1. Get L2NpcTemplate by GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_ID<br>
     * 2. Try to spawn a new npc of it<br><br>
     *
     * @return boolean: true if success, otherwise false<br>
     */
    public static boolean startParticipation() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        L2NpcTemplate tmpl = NpcTable.getInstance().getTemplate(GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_ID);

        if (tmpl == null) {
            _log.warning("KOTHEventEngine[KOTHEvent.startParticipation()]: L2NpcTemplate is a NullPointer -> Invalid npc id in configs?");
            return false;
        }

        try {
            _npcSpawn = new L2Spawn(tmpl);

            _npcSpawn.setLocation(new Location(GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[0], GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[1], GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[2]));
            _npcSpawn.setAmount(1);
            _npcSpawn.setHeading(GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[3]);
            _npcSpawn.setRespawnDelay(1);
            // later no need to delete spawn from db, we don't store it (false)
            SpawnTable.getInstance().addNewSpawn(_npcSpawn, false);
            _npcSpawn.init();
            _lastNpcSpawn = _npcSpawn.getLastSpawn();
            _lastNpcSpawn.setCurrentHp(_lastNpcSpawn.getMaxHp());
            _lastNpcSpawn.setTitle("Castle Siege Event Participation");
            _lastNpcSpawn.isAggressive();
            _lastNpcSpawn.decayMe();
            _lastNpcSpawn.spawnMe(_npcSpawn.getLastSpawn().getX(), _npcSpawn.getLastSpawn().getY(), _npcSpawn.getLastSpawn().getZ());
            _lastNpcSpawn.broadcastPacket(new MagicSkillUse(_lastNpcSpawn, _lastNpcSpawn, 1034, 1, 1, 1));
        } catch (Exception e) {
            _log.log(Level.WARNING, "KOTHEventEngine[KOTHEvent.startParticipation()]: exception: " + e.getMessage(), e);
            return false;
        }

        setState(EventState.PARTICIPATING);
        return true;
    }

    private static int highestLevelPcInstanceOf(Map<Integer, L2PcInstance> players) {
        int maxLevel = Integer.MIN_VALUE, maxLevelId = -1;
        for (L2PcInstance player : players.values()) {
            if (player.getLevel() >= maxLevel) {
                maxLevel = player.getLevel();
                maxLevelId = player.getObjectId();
            }
        }
        return maxLevelId;
    }

    private static int previousBluePoint = 0;
    private static int previousRedPoint = 0;

    /**
     * Starts the KothEvent fight<br>
     * 1. Set state EventState.STARTING<br>
     * 2. Close doors specified in configs<br>
     * 3. Abort if not enought participants(return false)<br>
     * 4. Set state EventState.STARTED<br>
     * 5. Teleport all participants to team spot<br><br>
     *
     * @return boolean: true if success, otherwise false<br>
     */
    public static boolean startFight() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        // Set state to STARTING
        owner = null;
        unSwapTeleports();
        setState(EventState.STARTING);

        // Randomize and balance team distribution
        Map<Integer, L2PcInstance> allParticipants = new FastMap<Integer, L2PcInstance>();
        allParticipants.putAll(_teams[0].getParticipatedPlayers());
        allParticipants.putAll(_teams[1].getParticipatedPlayers());
        _teams[0].cleanMe();
        _teams[1].cleanMe();

        L2PcInstance player;
        Iterator<L2PcInstance> iter;
        if (needParticipationFee()) {
            iter = allParticipants.values().iterator();
            while (iter.hasNext()) {
                player = iter.next();
                if (!hasParticipationFee(player))
                    iter.remove();
            }
        }

        int[] balance = {0, 0};
        int priority = 0;
        int highestLevelPlayerId;
        L2PcInstance highestLevelPlayer;
        // XXX: allParticipants should be sorted by level instead of using highestLevelPcInstanceOf for every fetch
        while (!allParticipants.isEmpty()) {
            // Priority team gets one player
            highestLevelPlayerId = highestLevelPcInstanceOf(allParticipants);
            highestLevelPlayer = allParticipants.get(highestLevelPlayerId);
            allParticipants.remove(highestLevelPlayerId);
            _teams[priority].addPlayer(highestLevelPlayer);
            balance[priority] += highestLevelPlayer.getLevel();
            // Exiting if no more players
            if (allParticipants.isEmpty()) break;
            // The other team gets one player
            // XXX: Code not dry
            priority = 1 - priority;
            highestLevelPlayerId = highestLevelPcInstanceOf(allParticipants);
            highestLevelPlayer = allParticipants.get(highestLevelPlayerId);
            allParticipants.remove(highestLevelPlayerId);
            _teams[priority].addPlayer(highestLevelPlayer);
            balance[priority] += highestLevelPlayer.getLevel();
            // Recalculating priority
            priority = balance[0] > balance[1] ? 1 : 0;
        }

        List<L2PcInstance> healers = new LinkedList<>();

        final List<L2PcInstance> temp = new ArrayList<>(_teams[0].getParticipatedPlayers().values());
        for (L2PcInstance pl : temp) {
            if(pl.isHealer()){
                healers.add(pl);
                _teams[0].removePlayer(pl.getObjectId());
            }
        }

        final List<L2PcInstance> temp2 = new ArrayList<>(_teams[1].getParticipatedPlayers().values());
        for (L2PcInstance pl : temp2) {
            if(pl.isHealer()){
                healers.add(pl);
                _teams[1].removePlayer(pl.getObjectId());
            }
        }

        int prev = 0;
        for (L2PcInstance healer : healers) {
            if(prev == 0){
                _teams[0].addPlayer(healer);
                prev += 1;
            }else{
                _teams[1].addPlayer(healer);
                prev = 0;
            }
        }


        // Check for enought participants
        if (_teams[0].getParticipatedPlayerCount() < GabConfig.CSKOTH_EVENT_MIN_PLAYERS_IN_TEAMS || _teams[1].getParticipatedPlayerCount() < GabConfig.CSKOTH_EVENT_MIN_PLAYERS_IN_TEAMS) {
            // Set state INACTIVE
            setState(EventState.INACTIVE);
            // Cleanup of teams
            _teams[0].cleanMe();
            _teams[1].cleanMe();
            // Unspawn the event NPC
            unSpawnNpc();
            AntiFeedManager.getInstance().clear(AntiFeedManager.KOTH_ID);
            return false;
        }else{

        }

        if (needParticipationFee()) {
            iter = _teams[0].getParticipatedPlayers().values().iterator();
            while (iter.hasNext()) {
                player = iter.next();
                if (!payParticipationFee(player))
                    iter.remove();
            }
            iter = _teams[1].getParticipatedPlayers().values().iterator();
            while (iter.hasNext()) {
                player = iter.next();
                if (!payParticipationFee(player))
                    iter.remove();
            }
        }

        if (GabConfig.CSKOTH_EVENT_IN_INSTANCE) {
            try {
                _castleSiegeInstance = InstanceManager.getInstance().createDynamicInstance(GabConfig.CSKOTH_EVENT_INSTANCE_FILE);
                InstanceManager.getInstance().getInstance(_castleSiegeInstance).setAllowSummon(false);
                InstanceManager.getInstance().getInstance(_castleSiegeInstance).setPvPInstance(true);
                InstanceManager.getInstance().getInstance(_castleSiegeInstance).setEmptyDestroyTime(GabConfig.CSKOTH_EVENT_START_LEAVE_TELEPORT_DELAY * 1000L + 60000L);


                for (int door : GabConfig.CSKOTH_DOORS_IDS_TO_OPEN) {
                    StatsSet set = new StatsSet();
                    set.add(DoorData.getInstance().getDoorTemplate(door));
                    InstanceManager.getInstance().getInstance(_castleSiegeInstance).addDoor(door, set);
                }

            } catch (Exception e) {
                _castleSiegeInstance = 0;
                _log.log(Level.WARNING, "KOTHEventEngine[KOTHEvent.createDynamicInstance]: exception: " + e.getMessage(), e);
            }
        }

        // Opens all doors specified in configs for koth
        openDoors(GabConfig.CSKOTH_DOORS_IDS_TO_OPEN);
        // Closes all doors specified in configs for koth
        closeDoors(GabConfig.CSKOTH_DOORS_IDS_TO_CLOSE);
        // Set state STARTED
        setState(EventState.STARTED);




        // Iterate over all teams
        for (CSKOTHEventTeam team : _teams) {
            // Iterate over all participated player instances in this team
            for (L2PcInstance playerInstance : team.getParticipatedPlayers().values()) {
                if (playerInstance != null) {
                    // Teleporter implements Runnable and starts itself
                    playerInstance._inGabAbstractEvent = true;
                    playerInstance.sendMessage("You are now in Castle Siege");
                    new CSKOTHEventTeleporter(playerInstance, team.getCoordinates(), false, false);
                    playerInstance.sendPacket(new CreatureSay(0, Say2.PARTYROOM_COMMANDER, "Castle Siege", "Find the crystal and capture the castle!!"));

                }
            }
        }spawnNpcArtifact();
        _teams[0].setAttacking(true);
        _teams[1].setAttacking(true);
        CSKOTHManager.getInstance().startedTime = GabConfig.CSKOTH_EVENT_RUNNING_TIME*60;
        ThreadPoolManager.getInstance().scheduleEventAtFixedRate(() -> {

            if(CSKOTHManager.getInstance().startedTime <= 0)
                return;

            CSKOTHEventTeam blue = CSKOTHEvent._teams[0];
            CSKOTHEventTeam red = CSKOTHEvent._teams[1];
            int blueInside = npcToSummon == null ? 0 : (int) blue.getParticipatedPlayers().values().stream().filter(e->!e.isDead() && e.isInRange(npcToSummon.getLocation(), crystalRange)).count();
            int redInside = npcToSummon == null ? 0 : (int) red.getParticipatedPlayers().values().stream().filter(e->!e.isDead() && e.isInRange(npcToSummon.getLocation(), crystalRange)).count();

            if(blueInside == 0)
                blue.setCapturePoints(0);
            if(redInside == 0)
                red.setCapturePoints(0);

            if(owner == null && (blueInside == 0 && redInside == 0)){
                npcToSummon.getTemplate().setTitle("Access Point");
                npcToSummon.broadcastInfo();
                npcToSummon.broadcastStatusUpdate();
            }

            if(owner != null && npcToSummon != null && owner.getName().equals(blue.getName()) && redInside == 0){
                npcToSummon.getTemplate().setTitle("Access Point");
                npcToSummon.broadcastInfo();
                npcToSummon.broadcastStatusUpdate();
            }

            if(owner != null && npcToSummon != null && owner.getName().equals(red.getName()) && blueInside == 0) {
                npcToSummon.getTemplate().setTitle("Access Point");
                npcToSummon.broadcastInfo();
                npcToSummon.broadcastStatusUpdate();
            }



            if(owner != null)
                owner.increasePoints();

            if(blueInside > 0/* && blueInside > redInside*/ && blue.isAttacking()) {
                if(owner == null && blueInside > redInside){
                    blue.incCapturePoints();
                }else if(owner != null)
                    blue.incCapturePoints();
            }
            else if(redInside > 0 /*&& redInside > blueInside*/ && red.isAttacking()) {
                if(owner == null && redInside > blueInside){
                    red.incCapturePoints();
                }else if(owner != null)
                    red.incCapturePoints();

            }

            int blueCapturePoints = blue.getCapturePoints();
            int redCapturePoints = red.getCapturePoints();

            if(blueCapturePoints > 0 && blueCapturePoints % 5 == 0 && previousBluePoint != blueCapturePoints) {
                previousBluePoint = blueCapturePoints;
                npcToSummon.getTemplate().setTitle(_teams[0].getName() + " team: " + blueCapturePoints + "%");
                npcToSummon.broadcastInfo();
                npcToSummon.broadcastStatusUpdate();
                CSKOTHEvent.sysMsgToAllParticipants(_teams[0].getName() + " team has " + blueCapturePoints + "% of the cast completed!");
            }

            if(redCapturePoints > 0 && redCapturePoints % 5 == 0 && previousRedPoint != redCapturePoints) {
                previousRedPoint = redCapturePoints;
                npcToSummon.getTemplate().setTitle(_teams[1].getName() + " team: " + redCapturePoints + "%");
                npcToSummon.broadcastInfo();
                npcToSummon.broadcastStatusUpdate();
                CSKOTHEvent.sysMsgToAllParticipants(_teams[1].getName() + " team has " + redCapturePoints + "% of the cast completed!");
            }
            int bluePointsReal = (int) blue.getPoints();
            int redPointsReal = (int) red.getPoints();

            int timeRemaining = --CSKOTHManager.getInstance().startedTime;
            for (CSKOTHEventTeam team : CSKOTHEvent._teams) {
                for (L2PcInstance partc : team.getParticipatedPlayers().values()) {
                    if (partc != null) {
                        partc.sendPacket(new ExCubeGameChangePoints(timeRemaining, bluePointsReal,redPointsReal));
                    }
                }
            }




            if(blueCapturePoints >= 100 && redCapturePoints < 100)
                swapTeams(true);
            else if(redCapturePoints >= 100 && blueCapturePoints < 100)
                swapTeams(false);


        }, 1000,1000, TimeUnit.MILLISECONDS);


        return true;
    }

    /**
     * Calculates the KothEvent reward<br>
     * 1. If both teams are at a tie(points equals), send it as system message to all participants, if one of the teams have 0 participants left online abort rewarding<br>
     * 2. Wait till teams are not at a tie anymore<br>
     * 3. Set state EvcentState.REWARDING<br>
     * 4. Reward team with more points<br>
     * 5. Show win html to wining team participants<br><br>
     *
     * @return String: winning team name<br>
     */
    public static String calculateRewards() {
        if (owner == null && _teams[0].getPoints() == _teams[1].getPoints()) {
            // Check if one of the teams have no more players left
            if (_teams[0].getParticipatedPlayerCount() == 0 || _teams[1].getParticipatedPlayerCount() == 0) {
                // set state to rewarding
                setState(EventState.REWARDING);
                // return here, the fight can't be completed
                return "Castle Siege Event: Event has ended. No team won due to inactivity!";
            }

            // Both teams have equals points
            sysMsgToAllParticipants("Castle Siege Event: Event has ended, both teams have tied.");
            if (GabConfig.CSKOTH_REWARD_TEAM_TIE) {
                rewardTeam(_teams[0]);
                rewardTeam(_teams[1]);
                return "Castle Siege Event: Event has ended with both teams tying.";
            } else
                return "Castle Siege Event: Event has ended with both teams tying.";
        }

        // Set state REWARDING so nobody can point anymore
        setState(EventState.REWARDING);

        // Get team which has more points

        CSKOTHEventTeam team = _teams[_teams[0].getPoints() > _teams[1].getPoints() ? 0 : 1];
//        if(winner != null)
//            team = winner;
        rewardTeam(team);
        return "Castle Siege Event: Event finish. Team " + team.getName() + " won with " + team.getPoints() + " points inside zone.";
    }

    private static void rewardTeam(CSKOTHEventTeam team) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        // Iterate over all participated player instances of the winning team
        for (L2PcInstance playerInstance : team.getParticipatedPlayers().values()) {
            // Check for nullpointer
            if (playerInstance == null) {
                continue;
            }

            SystemMessage systemMessage = null;

            // Iterate over all Castle Siege Event rewards
            for (int[] reward : GabConfig.CSKOTH_EVENT_REWARDS) {
                PcInventory inv = playerInstance.getInventory();

                // Check for stackable item, non stackabe items need to be added one by one
                if (ItemData.getInstance().createDummyItem(reward[0]).isStackable()) {
                    inv.addItem("Castle Siege Event", reward[0], reward[1], playerInstance, playerInstance);

                    if (reward[1] > 1) {
                        systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
                        systemMessage.addItemName(reward[0]);
                        systemMessage.addInt(reward[1]);
                    } else {
                        systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
                        systemMessage.addItemName(reward[0]);
                    }

                    playerInstance.sendPacket(systemMessage);
                } else {
                    for (int i = 0; i < reward[1]; ++i) {
                        inv.addItem("Castle Siege Event", reward[0], 1, playerInstance, playerInstance);
                        systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
                        systemMessage.addItemName(reward[0]);
                        playerInstance.sendPacket(systemMessage);
                    }
                }
            }

            StatusUpdate statusUpdate = new StatusUpdate(playerInstance);
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);

            statusUpdate.addAttribute(StatusUpdate.CUR_LOAD, playerInstance.getCurrentLoad());
            npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Reward.htm"));
            playerInstance.sendPacket(statusUpdate);
            playerInstance.sendPacket(npcHtmlMessage);
        }
    }

    /**
     * Stops the KothEvent fight<br>
     * 1. Set state EventState.INACTIVATING<br>
     * 2. Remove koth npc from world<br>
     * 3. Open doors specified in configs<br>
     * 4. Teleport all participants back to participation npc location<br>
     * 5. Teams cleaning<br>
     * 6. Set state EventState.INACTIVE<br>
     */
    public static void stopFight() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        // Set state INACTIVATING
        setState(EventState.INACTIVATING);
        //Unspawn event npc
        unSpawnNpc();
        // Opens all doors specified in configs for koth
        openDoors(GabConfig.CSKOTH_DOORS_IDS_TO_CLOSE);
        // Closes all doors specified in Configs for koth
        closeDoors(GabConfig.CSKOTH_DOORS_IDS_TO_OPEN);

        // Iterate over all teams
        for (CSKOTHEventTeam team : _teams) {
            for (L2PcInstance playerInstance : team.getParticipatedPlayers().values()) {
                // Check for nullpointer
                if (playerInstance != null) {
                    playerInstance._inGabAbstractEvent = false;
                    playerInstance.sendMessage("You left the Castle Siege");
                    new CSKOTHEventTeleporter(playerInstance, GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES, false, false);
                }
            }
        }

        // Cleanup of teams
        _teams[0].cleanMe();
        _teams[1].cleanMe();
        winner = null;
        // Set state INACTIVE
        setState(EventState.INACTIVE);
        AntiFeedManager.getInstance().clear(AntiFeedManager.CSKOTH_ID);
    }

    /**
     * Adds a player to a KothEvent team<br>
     * 1. Calculate the id of the team in which the player should be added<br>
     * 2. Add the player to the calculated team<br><br>
     *
     * @param playerInstance as L2PcInstance<br>
     * @return boolean: true if success, otherwise false<br>
     */
    public static synchronized boolean addParticipant(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        // Check for nullpoitner
        if (playerInstance == null) {
            return false;
        }

        byte teamId = 0;

        // Check to which team the player should be added
        if (_teams[0].getParticipatedPlayerCount() == _teams[1].getParticipatedPlayerCount()) {
            teamId = (byte) (Rnd.get(2));
        } else {
            teamId = (byte) (_teams[0].getParticipatedPlayerCount() > _teams[1].getParticipatedPlayerCount() ? 1 : 0);
        }

        return _teams[teamId].addPlayer(playerInstance);
    }

    /**
     * Removes a KothEvent player from it's team<br>
     * 1. Get team id of the player<br>
     * 2. Remove player from it's team<br><br>
     *
     * @return boolean: true if success, otherwise false<br>
     */
    public static boolean removeParticipant(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        // Get the teamId of the player
        Team teamId = getParticipantTeamId(playerObjectId);

        // Check if the player is participant
        if (teamId != Team.NONE) {
            // Remove the player from team
            _teams[teamId.getId() - 1].removePlayer(playerObjectId);
            return true;
        }

        return false;
    }

    public static boolean needParticipationFee() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        return GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[0] != 0 && GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[1] != 0;
    }

    public static boolean hasParticipationFee(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        return playerInstance.getInventory().getInventoryItemCount(GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[0], -1) >= GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[1];
    }

    public static boolean payParticipationFee(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        return playerInstance.destroyItemByItemId("CSKOTH Participation Fee", GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[0], GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[1], _lastNpcSpawn, true);
    }

    
    public static String getParticipationFee() {
        int itemId = GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[0];
        int itemNum = GabConfig.CSKOTH_EVENT_PARTICIPATION_FEE[1];

        if (itemId == 0 || itemNum == 0)
            return "-";

        return StringUtil.concat(String.valueOf(itemNum), " ", ItemData.getInstance().getTemplate(itemId).getName());
    }

    /**
     * Send a SystemMessage to all participated players<br>
     * 1. Send the message to all players of team number one<br>
     * 2. Send the message to all players of team number two<br><br>
     *
     * @param message as String<br>
     */
    public static void sysMsgToAllParticipants(String message) {
        for (L2PcInstance playerInstance : _teams[0].getParticipatedPlayers().values()) {
            if (playerInstance != null) {
                playerInstance.sendMessage(message);
            }
        }

        for (L2PcInstance playerInstance : _teams[1].getParticipatedPlayers().values()) {
            if (playerInstance != null) {
                playerInstance.sendMessage(message);
            }
        }
    }

    /**
     * Close doors specified in configs
     */
    private static void closeDoors(List<Integer> doors) {
        for (int doorId : doors) {
            L2DoorInstance doorInstance = DoorData.getInstance().getDoor(doorId);

            if (doorInstance != null) {
                doorInstance.closeMe();
            }
        }
    }

    /**
     * Open doors specified in configs
     */
    private static void openDoors(List<Integer> doors) {
        for (int doorId : doors) {
            L2DoorInstance doorInstance = DoorData.getInstance().getDoor(doorId);

            if (doorInstance != null) {
                doorInstance.openMe();
            }
        }
    }

    /**
     * UnSpawns the KothEvent npc
     */
    private static void unSpawnNpc() {
        // Delete the npc
        _lastNpcSpawn.deleteMe();
        if (npcToSummon != null) {
            npcToSummon.deleteMe();
            SpawnTable.getInstance().deleteSpawn(npcToSummon.getSpawn(), false);
        }

        SpawnTable.getInstance().deleteSpawn(_lastNpcSpawn.getSpawn(), false);

        // Stop respawning of the npc
        _npcSpawn.stopRespawn();
        npcToSummon = null;
        _npcSpawn = null;
        _lastNpcSpawn = null;
    }

    /**
     * Called when a player logs in<br><br>
     *
     * @param playerInstance as L2PcInstance<br>
     */
    
    public static void onLogin(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        if (playerInstance == null || (!isStarting() && !isStarted())) {
            return;
        }

        Team teamId = getParticipantTeamId(playerInstance.getObjectId());

        if (teamId == Team.NONE) {
            return;
        }

        _teams[teamId.getId() - 1].addPlayer(playerInstance);
        new CSKOTHEventTeleporter(playerInstance, _teams[teamId.getId() - 1].getCoordinates(), true, false);
    }

    /**
     * Called when a player logs out<br><br>
     *
     * @param playerInstance as L2PcInstance<br>
     */
    
    public static void onLogout(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        if (playerInstance != null && (isStarting() || isStarted() || isParticipating())) {
            if (removeParticipant(playerInstance.getObjectId())) {
                playerInstance.setXYZInvisible(GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[0] + Rnd.get(101) - 50,
                        GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[1] + Rnd.get(101) - 50,
                        GabConfig.CSKOTH_EVENT_PARTICIPATION_NPC_COORDINATES[2]);
            }
            playerInstance._inGabAbstractEvent = false;

        }
    }

    /**
     * Called on every bypass by npc of type L2KOTHEventNpc<br>
     * Needs synchronization cause of the max player check<br><br>
     *
     * @param command        as String<br>
     * @param playerInstance as L2PcInstance<br>
     */
    
    public static synchronized void onBypass(String command, L2PcInstance playerInstance) {
        if (playerInstance == null || !isParticipating())
            return;
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        final String htmContent;

        if (command.equals("cskoth_event_participation")) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
            int playerLevel = playerInstance.getLevel();

            if (playerInstance.isCursedWeaponEquipped()) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "CursedWeaponEquipped.htm");
                if (htmContent != null)
                    npcHtmlMessage.setHtml(htmContent);
            } else if (OlympiadManager.getInstance().isRegistered(playerInstance)) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Olympiad.htm");
                if (htmContent != null)
                    npcHtmlMessage.setHtml(htmContent);
            } else if (playerInstance.getKarma() > 0) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Karma.htm");
                if (htmContent != null)
                    npcHtmlMessage.setHtml(htmContent);
            } else if (playerLevel < GabConfig.CSKOTH_EVENT_MIN_LVL || playerLevel > GabConfig.CSKOTH_EVENT_MAX_LVL) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Level.htm");
                if (htmContent != null) {
                    npcHtmlMessage.setHtml(htmContent);
                    npcHtmlMessage.replace("%min%", String.valueOf(GabConfig.CSKOTH_EVENT_MIN_LVL));
                    npcHtmlMessage.replace("%max%", String.valueOf(GabConfig.CSKOTH_EVENT_MAX_LVL));
                }
            } else if (_teams[0].getParticipatedPlayerCount() == GabConfig.CSKOTH_EVENT_MAX_PLAYERS_IN_TEAMS && _teams[1].getParticipatedPlayerCount() == GabConfig.CSKOTH_EVENT_MAX_PLAYERS_IN_TEAMS) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "TeamsFull.htm");
                if (htmContent != null) {
                    npcHtmlMessage.setHtml(htmContent);
                    npcHtmlMessage.replace("%max%", String.valueOf(GabConfig.CSKOTH_EVENT_MAX_PLAYERS_IN_TEAMS));
                }
            } else if (GabConfig.CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP > 0
                    && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.KOTH_ID, playerInstance, GabConfig.CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP)) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "IPRestriction.htm");
                if (htmContent != null) {
                    npcHtmlMessage.setHtml(htmContent);
                    npcHtmlMessage.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(playerInstance, GabConfig.CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP)));
                }
            } else if (needParticipationFee() && !hasParticipationFee(playerInstance)) {
                htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "ParticipationFee.htm");
                if (htmContent != null) {
                    npcHtmlMessage.setHtml(htmContent);
                    npcHtmlMessage.replace("%fee%", getParticipationFee());
                }
            } else if (addParticipant(playerInstance))
                npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Registered.htm"));
            else
                return;

            playerInstance.sendPacket(npcHtmlMessage);
        } else if (command.equals("cskoth_event_remove_participation")) {
            removeParticipant(playerInstance.getObjectId());
            if (GabConfig.CSKOTH_EVENT_MAX_PARTICIPANTS_PER_IP > 0)
                AntiFeedManager.getInstance().removePlayer(AntiFeedManager.KOTH_ID, playerInstance);

            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);

            npcHtmlMessage.setHtml(HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "Unregistered.htm"));
            playerInstance.sendPacket(npcHtmlMessage);
        }
    }

    /**
     * Called on every onAction in L2PcIstance<br><br>
     *
     * @return boolean: true if player is allowed to target, otherwise false<br>
     */
    public static boolean onAction(L2PcInstance playerInstance, int targetedPlayerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (playerInstance == null || !isStarted()) {
            return true;
        }

        if (playerInstance.isGM()) {
            return true;
        }

        Team playerTeamId = getParticipantTeamId(playerInstance.getObjectId());
        Team targetedPlayerTeamId = getParticipantTeamId(targetedPlayerObjectId);

        if ((playerTeamId != Team.NONE && targetedPlayerTeamId == Team.NONE) || (playerTeamId == Team.NONE && targetedPlayerTeamId != Team.NONE)) {
            return false;
        }

        return playerTeamId == Team.NONE || targetedPlayerTeamId == Team.NONE || playerTeamId != targetedPlayerTeamId || playerInstance.getObjectId() == targetedPlayerObjectId || GabConfig.CSKOTH_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
    }

    /**
     * Called on every scroll use<br><br>
     *
     * @return boolean: true if player is allowed to use scroll, otherwise false<br>
     */
    
    public static boolean onScrollUse(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (!isStarted())
            return true;

        return !isPlayerParticipant(playerObjectId) || GabConfig.CSKOTH_EVENT_SCROLL_ALLOWED;
    }

    /**
     * Called on every potion use<br><br>
     *
     * @return boolean: true if player is allowed to use potions, otherwise false<br>
     */
    
    public static boolean onPotionUse(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (!isStarted())
            return true;

        return !isPlayerParticipant(playerObjectId) || GabConfig.CSKOTH_EVENT_POTIONS_ALLOWED;
    }

    /**
     * Called on every escape use(thanks to nbd)<br><br>
     *
     * @return boolean: true if player is not in Castle Siege Event, otherwise false<br>
     */
    
    public static boolean onEscapeUse(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (!isStarted()) {
            return true;
        }

        return !isPlayerParticipant(playerObjectId);
    }

    /**
     * Called on every summon item use<br><br>
     *
     * @return boolean: true if player is allowed to summon by item, otherwise false<br>
     */
    
    public static boolean onItemSummon(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (!isStarted()) {
            return true;
        }

        return !isPlayerParticipant(playerObjectId) || GabConfig.CSKOTH_EVENT_SUMMON_BY_ITEM_ALLOWED;
    }

    /**
     * Is called when a player is killed<br><br>
     *
     * @param killerCharacter      as L2Character<br>
     * @param killedPlayerInstance as L2PcInstance<br>
     */
    
    public static void onKill(L2Character killerCharacter, L2PcInstance killedPlayerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        if (killedPlayerInstance == null || !isStarted()) {
            return;
        }

        Team killedTeamId = getParticipantTeamId(killedPlayerInstance.getObjectId());

        if (killedTeamId == Team.NONE) {
            return;
        }

        new CSKOTHEventTeleporter(killedPlayerInstance, _teams[killedTeamId.getId() - 1].getCoordinates(), false, false);

        if (killerCharacter == null) {
            return;
        }

        L2PcInstance killerPlayerInstance = null;

        if (killerCharacter instanceof L2PetInstance || killerCharacter instanceof L2Summon) {
            killerPlayerInstance = ((L2Summon) killerCharacter).getOwner();

            if (killerPlayerInstance == null) {
                return;
            }
        } else if (killerCharacter instanceof L2PcInstance) {
            killerPlayerInstance = (L2PcInstance) killerCharacter;
        } else {
            return;
        }

        Team killerTeamId = getParticipantTeamId(killerPlayerInstance.getObjectId());

        if (killerTeamId != Team.NONE && killedTeamId != Team.NONE && killerTeamId != killedTeamId) {
            CSKOTHEventTeam killerTeam = _teams[killerTeamId.getId() - 1];

            killerTeam.increasePoints();

//            CreatureSay cs = new CreatureSay(killerPlayerInstance.getObjectId(), Say2.TELL, killerPlayerInstance.getName(), "I have killed " + killedPlayerInstance.getName() + "!");
//            for (L2PcInstance playerInstance : _teams[killerTeamId.getId() - 1].getParticipatedPlayers().values()) {
//                if (playerInstance != null) {
//                    playerInstance.sendPacket(cs);
//                }
//            }
        }
    }

    /**
     * Called on Appearing packet received (player finished teleporting)<br><br>
     */
    
    public static void onTeleported(L2PcInstance playerInstance) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        if (!isStarted() || playerInstance == null || !isPlayerParticipant(playerInstance.getObjectId()))
            return;

        if (_state != EventState.STARTED && playerInstance.getParty() != null) {
            playerInstance.leaveParty();
        }

        if (playerInstance.isMageClass()) {
            if (GabConfig.CSKOTH_EVENT_MAGE_BUFFS != null && !GabConfig.CSKOTH_EVENT_MAGE_BUFFS.isEmpty()) {
                for (int i : GabConfig.CSKOTH_EVENT_MAGE_BUFFS.keys()) {
                    L2Skill skill = SkillData.getInstance().getInfo(i, GabConfig.CSKOTH_EVENT_MAGE_BUFFS.get(i));
                    if (skill != null)
                        skill.getEffects(playerInstance, playerInstance);
                }
            }
        } else {
            if (GabConfig.CSKOTH_EVENT_FIGHTER_BUFFS != null && !GabConfig.CSKOTH_EVENT_FIGHTER_BUFFS.isEmpty()) {
                for (int i : GabConfig.CSKOTH_EVENT_FIGHTER_BUFFS.keys()) {
                    L2Skill skill = SkillData.getInstance().getInfo(i, GabConfig.CSKOTH_EVENT_FIGHTER_BUFFS.get(i));
                    if (skill != null)
                        skill.getEffects(playerInstance, playerInstance);
                }
            }
        }
    }

    /**
     * Sets the KothEvent state<br><br>
     *
     * @param state as EventState<br>
     */
    private static void setState(EventState state) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return;
        synchronized (_state) {
            _state = state;
        }
    }

    /**
     * Is KothEvent inactive?<br><br>
     *
     * @return boolean: true if event is inactive(waiting for next event cycle), otherwise false<br>
     */
    public static boolean isInactive() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isInactive;

        synchronized (_state) {
            isInactive = _state == EventState.INACTIVE;
        }

        return isInactive;
    }

    /**
     * Is KothEvent in inactivating?<br><br>
     *
     * @return boolean: true if event is in inactivating progress, otherwise false<br>
     */
    public static boolean isInactivating() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isInactivating;

        synchronized (_state) {
            isInactivating = _state == EventState.INACTIVATING;
        }

        return isInactivating;
    }

    /**
     * Is KothEvent in participation?<br><br>
     *
     * @return boolean: true if event is in participation progress, otherwise false<br>
     */
    
    public static boolean isParticipating() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isParticipating;

        synchronized (_state) {
            isParticipating = _state == EventState.PARTICIPATING;
        }

        return isParticipating;
    }

    /**
     * Is KothEvent starting?<br><br>
     *
     * @return boolean: true if event is starting up(setting up fighting spot, teleport players etc.), otherwise false<br>
     */
    
    public static boolean isStarting() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isStarting;

        synchronized (_state) {
            isStarting = _state == EventState.STARTING;
        }

        return isStarting;
    }

    /**
     * Is KothEvent started?<br><br>
     *
     * @return boolean: true if event is started, otherwise false<br>
     */
    
    public static boolean isStarted() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isStarted;

        synchronized (_state) {
            isStarted = _state == EventState.STARTED;
        }

        return isStarted;
    }

    /**
     * Is KothEvent rewadrding?<br><br>
     *
     * @return boolean: true if event is currently rewarding, otherwise false<br>
     */
    public static boolean isRewarding() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        boolean isRewarding;

        synchronized (_state) {
            isRewarding = _state == EventState.REWARDING;
        }

        return isRewarding;
    }

    /**
     * Returns the team id of a player, if player is not participant it returns -1<br><br>
     *
     * @return byte: team name of the given playerName, if not in event -1<br>
     */
    public static Team getParticipantTeamId(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return Team.NONE;
        return (_teams[0].containsPlayer(playerObjectId) ? Team.BLUE : (_teams[1].containsPlayer(playerObjectId) ? Team.RED : Team.NONE));
    }

    /**
     * Returns the team of a player, if player is not participant it returns null <br><br>
     *
     * @return KOTHEventTeam: team of the given playerObjectId, if not in event null <br>
     */
    public static CSKOTHEventTeam getParticipantTeam(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return null;
        return (_teams[0].containsPlayer(playerObjectId) ? _teams[0] : (_teams[1].containsPlayer(playerObjectId) ? _teams[1] : null));
    }

    /**
     * Returns the enemy team of a player, if player is not participant it returns null <br><br>
     *
     * @return KOTHEventTeam: enemy team of the given playerObjectId, if not in event null <br>
     */
    public static CSKOTHEventTeam getParticipantEnemyTeam(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return null;
        return (_teams[0].containsPlayer(playerObjectId) ? _teams[1] : (_teams[1].containsPlayer(playerObjectId) ? _teams[0] : null));
    }

    /**
     * Returns the team coordinates in which the player is in, if player is not in a team return null<br><br>
     *
     * @return int[]: coordinates of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
     */
    public static int[] getParticipantTeamCoordinates(int playerObjectId) {
        return _teams[0].containsPlayer(playerObjectId) ? _teams[0].getCoordinates() : (_teams[1].containsPlayer(playerObjectId) ? _teams[1].getCoordinates() : null);
    }

    /**
     * Is given player participant of the event?<br><br>
     *
     * @return boolean: true if player is participant, ohterwise false<br>
     */
    
    public static boolean isPlayerParticipant(int playerObjectId) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return false;
        if (!isParticipating() && !isStarting() && !isStarted()) {
            return false;
        }

        return _teams[0].containsPlayer(playerObjectId) || _teams[1].containsPlayer(playerObjectId);
    }

    /**
     * Returns participated player count<br><br>
     *
     * @return int: amount of players registered in the event<br>
     */
    public static int getParticipatedPlayersCount() {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return 0;
        if (!isParticipating() && !isStarting() && !isStarted()) {
            return 0;
        }

        return _teams[0].getParticipatedPlayerCount() + _teams[1].getParticipatedPlayerCount();
    }

    /**
     * Returns teams names<br><br>
     *
     * @return String[]: names of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
     */
    public static String[] getTeamNames() {
        return new String[]
                {
                        _teams[0].getName(), _teams[1].getName()
                };
    }

    /**
     * Returns player count of both teams<br><br>
     *
     * @return int[]: player count of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
     */
    
    public static int[] getTeamsPlayerCounts() {
        return new int[]
                {
                        _teams[0].getParticipatedPlayerCount(), _teams[1].getParticipatedPlayerCount()
                };
    }

    /**
     * Returns points count of both teams
     *
     * @return int[]: points of teams, 2 elements, index 0 for team 1 and index 1 for team 2<br>
     */
    
    public static long[] getTeamsPoints() {
        return new long[]
                {
                        _teams[0].getPoints(), _teams[1].getPoints()
                };
    }

    
    public static int getKothEventInstance() {
        return _castleSiegeInstance;
    }

    private static L2Npc npcToSummon = null;
    private static L2Npc outerDoorMan = null;
    private static L2Npc innerDoorMan = null;
    private static final int[] toSummonObject =
            {
                    GabConfig.CSKOTH_EVENT_CRYSTAL_NPC_ID,
                    GabConfig.CSKOTH_EVENT_CRYSTAL_LOC[0], // X
                    GabConfig.CSKOTH_EVENT_CRYSTAL_LOC[1], // Y
                    GabConfig.CSKOTH_EVENT_CRYSTAL_LOC[2]   //Z
            };

    public static void spawnNpcArtifact() {
        if (npcToSummon != null) {
            npcToSummon.deleteMe();
            SpawnTable.getInstance().deleteSpawn(npcToSummon.getSpawn(), false);
            npcToSummon = null;
        }
        CSKOTHSpawnManager csSpawnManager = new CSKOTHSpawnManager();
        npcToSummon = csSpawnManager.addSpawn(toSummonObject[0], toSummonObject[1], toSummonObject[2], toSummonObject[3], 32768, false, 0, false, _castleSiegeInstance);
        npcToSummon.setInstanceId(_castleSiegeInstance);
        npcToSummon.teleToLocation(toSummonObject[1], toSummonObject[2], toSummonObject[3]);
        npcToSummon.getTemplate().setTitle("Access Point");
        npcToSummon.broadcastInfo();
        npcToSummon.broadcastStatusUpdate();

        //outer


        for (Integer integer : GabConfig.CSKOTH_EVENT_DOORMANS_SPAWN) {
            for (L2Spawn spawn : SpawnTable.getInstance().getSpawns(integer)) {
                if(spawn == null)
                    continue;
                int x = spawn.getLocation().getX();
                int y = spawn.getLocation().getY();
                int z = spawn.getLocation().getZ();
                int heading = spawn.getLocation().getHeading();
                csSpawnManager.addSpawn(integer, x, y, z, heading, false, 0, false, _castleSiegeInstance).setInstanceId(_castleSiegeInstance);

            }
        }
    }

    public static void unSwapTeleports() {
        _teams[0].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_1_COORDINATES);
        _teams[1].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_2_COORDINATES);
    }

    private static void swapTeams(boolean blueSummoned) {
        if(blueSummoned){
            _teams[0].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_OWNER_COORDINATES);
            _teams[0].setAttacking(false);
            owner = _teams[0];
            CSKOTHEvent.sysMsgToAllParticipants(_teams[0].getName()+" team has summoned the castle!");
            _teams[1].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_2_COORDINATES);
            _teams[1].setAttacking(true);
        }else{
            _teams[1].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_OWNER_COORDINATES);
            _teams[1].setAttacking(false);
            owner = _teams[1];
            CSKOTHEvent.sysMsgToAllParticipants(_teams[1].getName()+" team has summoned the castle!");
            _teams[0].set_coordinates(GabConfig.CSKOTH_EVENT_TEAM_1_COORDINATES);
            _teams[0].setAttacking(true);
        }



        _teams[1].setCapturePoints(0);
        _teams[0].setCapturePoints(0);
        teleportSwappedTeams();
        for (L2DoorInstance door : InstanceManager.getInstance().getInstance(_castleSiegeInstance).getDoors()) {

            if (door.isDead()) {
                door.doRevive();
                door.setCurrentHp(door.getMaxHp());
            }
            if(door.getId() != 20160005)
                door.closeMe();
        }
        // Opens all doors specified in configs for cs
        openDoors(GabConfig.CSKOTH_DOORS_IDS_TO_OPEN);
        // Closes all doors specified in configs for cs
        closeDoors(GabConfig.CSKOTH_DOORS_IDS_TO_OPEN);

    }

    private static void teleportSwappedTeams() {
        for (CSKOTHEventTeam team : _teams) {
            // Iterate over all participated player instances in this team
            for (L2PcInstance playerInstance : team.getParticipatedPlayers().values()) {
                if (playerInstance != null) {
                    // Teleporter implements Runnable and starts itself
                    new CSKOTHEventTeleporter(playerInstance, team.getCoordinates(), true, false);
                }
            }
        }
    }

    
    public static final boolean checkForSkill(L2PcInstance source, L2PcInstance target, L2Skill skill) {
        if(!GabConfig.CSKOTH_EVENT_ENABLED)
            return true;
        if (!isStarted())
            return true;

        final int sourcePlayerId = source.getObjectId();
        final int targetPlayerId = target.getObjectId();
        final boolean isSourceParticipant = isPlayerParticipant(sourcePlayerId);
        final boolean isTargetParticipant = isPlayerParticipant(targetPlayerId);

        // both players not participating
        if (!isSourceParticipant && !isTargetParticipant)
            return true;
        // one player not participating
        if (!(isSourceParticipant && isTargetParticipant))
            return false;
        // players in the different teams ?
        if (getParticipantTeamId(sourcePlayerId) != getParticipantTeamId(targetPlayerId)) {
            return skill.isOffensive();
        }
        return true;
    }

}
