package gabriel.pvpInstanceZone.PVPInstance;


import gabriel.config.GabConfig;
import gabriel.epicRaid.EpicRaidManager;
import gabriel.events.challengerZone.ChallengerZoneManager;
import gabriel.events.extremeZone.ExtremeZoneManager;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.CustomPvPInstanceZone;
import gabriel.pvpInstanceZone.PvPInstanceTeam;
import gabriel.pvpInstanceZone.PvPZoneManager;
import gr.sr.interf.SunriseEvents;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.enums.MessageType;
import l2r.gameserver.enums.Team;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2MonsterInstance;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.model.instancezone.InstanceWorld;
import l2r.gameserver.model.quest.Quest;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.util.Broadcast;
import l2r.util.Rnd;

import java.util.Arrays;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public final class PVPInstance extends Quest {
    /*
     * Time after which instance without players will be destroyed Default: 5 minutes
     */

    // Template IDs for PVPInstance
    // @formatter:off
    private static final int[] TEMPLATE_IDS =
            {
                    ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID
            };

    // Duration of the instance, minutes
    private static final int[] DURATION =
            {
                    ConfigPvPInstance.PVP_INSTANCE_MAP_DURATION
            };
    // Maximum party size for the instance
    private static final int[] MAX_PARTY_SIZE =
            {
                    9
            };

    private static int[] PROHIBITED_CLASS_ANNONYM = {

            15, 16, 29, 30, 42, 43, 51, 52, 90, 91, 97, 98, 99, 100, 105, 106, 107, 112, 115, 116, 136
    };

    public static int instanceId;

    /*
     * Escape teleporter npcId
     */
    private static final int TELEPORTER = 70001;

    /**
     * PVPInstance captains (start npc's) npcIds.
     */
    private static final int[] CAPTAINS =
            {
                    ConfigPvPInstance.PVP_INSTANCE_NPC_ID
            };

    protected class PVPInstanceWorld extends InstanceWorld {
        public int index; // 0-18 index of the Raid type in arrays
        // max =

    }

    private L2Npc npc = null;

    private PVPInstance() {
        super(-1, PVPInstance.class.getSimpleName(), "instances");

        addFirstTalkId(TELEPORTER);
        addTalkId(TELEPORTER);
        for (int cap : CAPTAINS) {
            addStartNpc(cap);
            addTalkId(cap);
        }
    }

    protected static PVPInstance instance;

    public static PVPInstance getInstance() {
        if (instance == null)
            instance = new PVPInstance();
        return instance;
    }

    /**
     * Check if party with player as leader allowed to enter
     *
     * @param player party leader
     * @param index  (0-18) index of the PVPInstance in arrays
     * @return true if party allowed to enter
     */
    private static boolean checkConditions(L2PcInstance player, int index) {
        final L2Party party = player.getParty();
        if (SunriseEvents.isRegistered(player)) {
            player.sendMessage("Please unregister from the current event and come back!");
            return false;
        }
        if (PvPZoneManager.getPvPInstanceMode() == 3 || PvPZoneManager.getPvPInstanceMode() == 2) {
            if (party != null)
                party.removePartyMember(player, MessageType.Left);
        }
        if (player.getClassId().level() != 3) {
            player.sendMessage("You need to be at 3rd job to enter!");
            return false;
        }

        if (SunriseEvents.isRegistered(player) || SunriseEvents.isInEvent(player)) {
            player.sendMessage("You are already registered in another event!!");
            return false;
        }

        if (PvPZoneManager.getPvPInstanceMode() == 3 && Arrays.stream(PROHIBITED_CLASS_ANNONYM).anyMatch(i -> i == player.getClassId().getId())) {
            player.sendMessage("Your class cannot enter the Annonymous. Please come back later or change your class.");
            return false;
        }

        if (PvPZoneManager.getPvPInstanceMode() == 2 && Arrays.stream(PROHIBITED_CLASS_ANNONYM).anyMatch(i -> i == player.getClassId().getId())) {
            player.sendMessage("Your class cannot enter the TvTvTvT. Please come back later or change your class.");
            return false;
        }
        if (player.isInTournament()) {
            player.sendMessage("You cannot enter while registered on Tournament");
            return false;
        }
        // party must not exceed max size for selected instance
        if (player.getParty() != null) {
            if (party.getMemberCount() > MAX_PARTY_SIZE[index]) {
                player.sendPacket(SystemMessageId.PARTY_EXCEEDED_THE_LIMIT_CANT_ENTER);
                return false;
            }
        }

        // for each party member
        if (player.getParty() != null) {
            for (L2PcInstance partyMember : party.getMembers()) {
                // player must be near party leader
                if (!partyMember.isInsideRadius(player, 2000, true, true)) {
                    player.sendMessage("Player " + player.getName() + " is in a location that cannot be entered!");
                    return false;
                }
                if (partyMember.isInOlympiadMode()) {
                    player.sendMessage("Player '" + partyMember.getName() + "' is in Olympiad! You cannot teleport to the event");
                    return false;
                }
            }
        }
        if (player.isInOlympiadMode()) {
            player.sendMessage("You are in Olympiad! You cannot teleport to the event");
            return false;
        }
        if (player.is_waitTimeTeleporterOly()) {
            player.sendMessage("You are in Olympiad! You cannot teleport to the event");
            return false;
        }
        return true;
    }

    /**
     * Handling enter of the players into PVPInstance
     *
     * @param player party leader
     * @param index  (0-18) PVPInstance index in arrays
     */
    public synchronized void enterInstance(L2PcInstance player, int index) {
        if (ConfigPvPInstance.ENABLE_PVP_INSTANCE_ZONE) {

            if (GabConfig.ER_EVENT_CLOSE_ALL && EpicRaidManager.getInstance().isStarted() && !EpicRaidManager.getInstance().isClosed()) {
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Zone Is closed because Epic Raid has been started!"));
                return;
            }

            int templateId;
            try {
                templateId = TEMPLATE_IDS[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw e;
            }
            // check for existing instances for this player
            InstanceWorld world = InstanceManager.getInstance().getPlayerWorld(player);
            CustomPvPInstanceZone zone = PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()];
            Location[] teleports = zone.getSpawnLocs();

            // player already in the instance
            if (world != null) {
                // check what instance still exist
                Instance inst = InstanceManager.getInstance().getInstance(templateId);
                if (inst != null) {
                    teleportPlayer(player, teleports[Rnd.get(teleports.length)], templateId);
                }
                return;
            }
            // Creating new PVPInstance instance
            Instance inst = InstanceManager.getInstance().getInstance(templateId);
            if (inst == null) {

                PvPZoneManager.playerinInstance.clear();
                PvPZoneManager.scoreClan.clear();
                PvPZoneManager.BlueTeam = new PvPInstanceTeam();
                PvPZoneManager.RedTeam = new PvPInstanceTeam();
                PvPZoneManager.GreenTeam = new PvPInstanceTeam();
                PvPZoneManager.YellowTeam = new PvPInstanceTeam();

                createInstance(templateId);
                setPvPInstance(templateId);
                inst = InstanceManager.getInstance().getInstance(templateId);
                inst.setDuration(DURATION[0] * 60000);

                inst.setShowTimer(true);
                inst.setTimerIncrase(false);
                inst.setTimerText("Zone Ends In");
                //inst.setEmptyDestroyTime(EMPTY_DESTROY_TIME * 60000);
                inst.setAllowSummon(false);

                inst.setSpawnLocPvP(new Location(ConfigPvPInstance.PVP_INSTANCE_BACK_X, ConfigPvPInstance.PVP_INSTANCE_BACK_Y, ConfigPvPInstance.PVP_INSTANCE_BACK_Z));
                PvPZoneManager.getInstance().start();

                switch (ConfigPvPInstance.MODE_TYPE) {
                    case "Random":
                        PvPZoneManager.setRandomPvPInstanceMode();
                        break;
                    case "Incrementing":
                        PvPZoneManager.incrementPvPInstanceMode();
                        break;
                    case "NormalOnly":
                        PvPZoneManager.setNormalOnly();
                        break;
                    case "TvTOnly":
                        PvPZoneManager.setTvTOnly();
                        break;
                    case "TvTvTvTOnly":
                        PvPZoneManager.setTvTvTvTOnly();
                        break;
                    case "AnnonymOnly":
                        PvPZoneManager.setAnnonymOnly();
                        break;
                    case "Gabriel2Normal1Annonym":
                        PvPZoneManager.gabriel2Normal1Annonym();
                        break;
                    default:
                        PvPZoneManager.setRandomPvPInstanceMode();
                        break;
                }
                PvPZoneManager.setRandomLocationindex();

                zone = PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()];
                teleports = zone.getSpawnLocs();
                Integer[] BOSS = zone.getBoss();
//                PvPZoneManager.setLocationIndex(8);

                if (PvPZoneManager.isNormal()) {
                    if (BOSS != null) {
                        npc = addSpawn(BOSS[0], BOSS[1], BOSS[2], BOSS[3], 0, false, 0, false, ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
                        ((L2MonsterInstance) npc).setOnKillDelay(100);
                    }
                }

                Broadcast.toAllOnlinePlayers("A New PvP Zone has been opened Come and join at the PvP Lobby.");
                Broadcast.toAllOnlinePlayers("Map: " + getMapName());
                Broadcast.toAllOnlinePlayers("Mode: " + getMapMode());

                if (PvPZoneManager.isNormal()) {
                    if (BOSS != null && npc != null) {
                        Broadcast.toAllOnlinePlayers("Special Boss: " + npc.getName() + " spawned in PvP Zone");
                    }
                }
                if (GabConfig.EXTREME_EVENT_ENABLED && ExtremeZoneManager.getInstance().isStarted())
                    ExtremeZoneManager.getInstance().endEvent();

                if (ChallengerZoneManager.getInstance().isStarted())
                    ChallengerZoneManager.getInstance().endEvent();

                if (GabConfig.EXTREME_EVENT_ENABLED && PvPZoneManager.isAnnonym())
                    ExtremeZoneManager.getInstance().startEvent();

                if (PvPZoneManager.isTvTvTvT() && GabConfig.CHALLENGER_EVENT_ENABLED)
                    ChallengerZoneManager.getInstance().startEvent();
            }

            if (!checkConditions(player, index)) {
                return;
            }
            for (L2Effect allEffect : player.getAllEffects()) {
                if (GabConfig.BUFF_ID_DELETE_EVENTS.stream().anyMatch(e->e==allEffect.getSkill().getId()))
                    allEffect.exit();
            }
            final L2Party party = player.getParty();

            // and finally teleport party into instance
            /**
             * TvTvTvT and Annonym Mode
             */
            if (PvPZoneManager.isTvTvTvT() || PvPZoneManager.isAnnonym()) {

                /**
                 * playes cannot enter with party on tvtvtvt and annonym mode
                 */
                if (party != null)
                    party.removePartyMember(player, MessageType.Left);

                String playerName = player.getName();
                if (!PvPZoneManager.scoreClan.containsKey(playerName)) {
                    PvPZoneManager.scoreClan.put(playerName, 0);
                }
                /**
                 * Player distribution TvTvTvT
                 */

                if (PvPZoneManager.isTvTvTvT()) {
                    PvPZoneManager.addPlayerToTeam(player);
                }

                if (PvPZoneManager.isTvTvTvT()) {
                    if (player.getTeam4t() == 1) {
                        teleportPlayer(player, teleports[0], templateId);
                    } else if (player.getTeam4t() == 2) {
                        teleportPlayer(player, teleports[1], templateId);
                    } else if (player.getTeam4t() == 3) {
                        teleportPlayer(player, teleports[2], templateId);
                    } else if (player.getTeam4t() == 4) {
                        teleportPlayer(player, teleports[3], templateId);
                    }
                } else {
                    teleportPlayer(player, teleports[Rnd.get(teleports.length)], templateId);
                }

                PvPZoneManager.playerinInstance.add(player);
            }
            /**
             * Normal Mode and TvT Mode
             */
            if (PvPZoneManager.isNormal() || PvPZoneManager.isTvT()) {
                if (party == null) {
                    String playerName = player.getName();
                    if (!PvPZoneManager.scoreClan.containsKey(playerName)) {
                        PvPZoneManager.scoreClan.put(playerName, 0);
                    }
                    /**
                     * Player distribution TvT
                     */
                    if (PvPZoneManager.isTvT()) {
                        if (player.getTeam() == Team.NONE) {
                            PvPZoneManager.addPlayerToTeam(player);
                        }
                    }
                    teleportPlayer(player, teleports[Rnd.get(teleports.length)], templateId);
                    PvPZoneManager.playerinInstance.add(player);

                } else {
                    int teleportWholeparty = Rnd.get(teleports.length);
                    Team teamParty = getTeamPt();
                    Location startLoc = player.getLocation();

                    if (party.getCommandChannel() == null) {
                        for (L2PcInstance partyMember : party.getMembers()) {
                            if (!partyMember.isInsideRadius(startLoc, 5000, false, false))
                                continue;
                            String playerName = player.getName();
                            if (!PvPZoneManager.scoreClan.containsKey(playerName)) {
                                PvPZoneManager.scoreClan.put(playerName, 0);
                            }

                            if (PvPZoneManager.getPvPInstanceMode() == 1) {
                                if (partyMember.getTeam() == Team.NONE) {
                                    partyMember.setTeam(teamParty);
                                }
                            }
                            teleportPlayer(partyMember, teleports[teleportWholeparty], templateId);
                            PvPZoneManager.playerinInstance.add(partyMember);
                        }
                    } else {
                        for (L2PcInstance partyMember : party.getCommandChannel().getMembers()) {
                            if (!partyMember.isInsideRadius(startLoc, 5000, false, false))
                                continue;
                            String playerName = player.getName();
                            if (!PvPZoneManager.scoreClan.containsKey(playerName)) {
                                PvPZoneManager.scoreClan.put(playerName, 0);
                            }

                            if (PvPZoneManager.getPvPInstanceMode() == 1) {
                                if (partyMember.getTeam() == Team.NONE) {
                                    partyMember.setTeam(teamParty);
                                }
                            }
                            teleportPlayer(partyMember, teleports[teleportWholeparty], templateId);
                            PvPZoneManager.playerinInstance.add(partyMember);
                        }
                    }


                }
            }

            if (ConfigPvPInstance.ENABLE_PVP_INSTANCE_NOB_ENTER) {
                L2Skill noblesse = SkillData.getInstance().getInfo(1323, 1);
                if (noblesse != null) {
                    noblesse.getEffects(player, player);
                }
                L2Skill flames = SkillData.getInstance().getInfo(1427, 1);
                if (flames != null)
                    flames.getEffects(player, player);
            }

//
//            for (L2PcInstance playerinside : PvPZoneManager.playerinInstance) {
//                if(!GabUtils.isInPvPInstance(playerinside)){
//                    PvPZoneManager.handleLeavePvPZone(playerinside, false);
//                    continue;
//                }
//                if (playerinside != player && !player.isInParty() && !player.isGM()) {
//
//                    if (PvPZoneManager.getPvPInstanceMode() == 1 && playerinside.getTeam() == player.getTeam()) {
//                        playerinside.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Just entered the zone with no party and is on the same team as you! Class: " + player.getClassId().name().toUpperCase() + ". Invite \b\tType=1 \tID=" + player.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));
//                    }
//                    if (PvPZoneManager.getPvPInstanceMode() == 2 && playerinside.getTeam4t() == player.getTeam4t()) {
//                        playerinside.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Just entered the zone with no party and is on the same team as you! Class: " + player.getClassId().name().toUpperCase() + ". Invite \b\tType=1 \tID=" + player.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));
//                    }
//                    if (PvPZoneManager.getPvPInstanceMode() == 0) {
//                        playerinside.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "Just entered the zone! Class: " + player.getClassId().name().toUpperCase() + ". Invite \b\tType=1 \tID=" + player.getObjectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b"));
//                    }
//                }
//            }
            return;
        } else {
            player.sendMessage("Area is currently disabled");
        }
    }

    public int getBlueTeams() {
        return PvPZoneManager.BlueTeam.getListPlayers().size();
    }

    public int getRedTeams() {
        return PvPZoneManager.RedTeam.getListPlayers().size();
    }

    public int getGreenTeams() {
        return PvPZoneManager.GreenTeam.getListPlayers().size();
    }

    public int getYellowTeams() {
        return PvPZoneManager.YellowTeam.getListPlayers().size();
    }

    public String getMapMode() {
        if (PvPZoneManager.isNormal()) {
            return "Normal";
        } else if (PvPZoneManager.isTvT()) {
            return "Team vs Team";
        } else if (PvPZoneManager.isTvTvTvT()) {
            return "Team vs Team vs Team vs Team";
        } else if (PvPZoneManager.isAnnonym()) {
            return "One for All";
        }
        return "";
    }

    public String getMapName() {
        CustomPvPInstanceZone zone = PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()];
        return zone.getName();
    }

    private Team getTeamPt() {
        if (getBlueTeams() == getRedTeams() || getBlueTeams() == 0) {
            return Team.BLUE;
        } else if (getBlueTeams() > getRedTeams()) {
            return Team.RED;
        } else if (getBlueTeams() < getRedTeams()) {
            return Team.BLUE;
        } else {
            return Team.NONE;
        }
    }

    /**
     * Handles only player's enter, single parameter - integer PVPInstance index
     */
    @Override
    public final String onAdvEvent(String event, L2Npc npc, L2PcInstance player) {
        if (npc == null) {
            return "";
        }

        try {
            enterInstance(player, Integer.parseInt(event));
        } catch (Exception e) {
            _log.warn("", e);
        }
        return "";
    }

    /**
     * Talk with captains and using of the escape teleporter
     */
    @Override
    public final String onTalk(L2Npc npc, L2PcInstance player) {
        final int npcId = npc.getId();

        if (npcId == TELEPORTER) {
            final L2Party party = player.getParty();
            // only party leader can talk with escape teleporter

            if ((party != null) && party.isLeader(player)) {
                final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
                if (world instanceof PVPInstanceWorld) {
                    // party members must be in the instance
                    if (world.isAllowed(player.getObjectId())) {
                        Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());

                        for (L2PcInstance partyMember : party.getMembers()) {
                            if (partyMember.isInsideRadius(player, 500, true, true)) {
                                if ((partyMember != null) && (partyMember.getInstanceId() == world.getInstanceId())) {
                                    teleportPlayer(partyMember, inst.getSpawnLocPvP(), 0);
                                }
                            }
                        }

                    }
                }
            }
            if (party == null) {
                final InstanceWorld world = InstanceManager.getInstance().getWorld(npc.getInstanceId());
                Instance inst = InstanceManager.getInstance().getInstance(world.getInstanceId());
                teleportPlayer(player, inst.getSpawnLocPvP(), 0);
            }
        } else {

            if (player.isInOlympiadMode()) {
                return "70000-no.htm";

            }
            if (player.getParty() == null) {
                return "70000.htm";
            } else if (player.isInParty() && player.getParty().isLeader(player)) {
                return "70000.htm";
            } else {
                return "70000-no.htm";
            }

        }

        return "";
    }

    /**
     * Only escape teleporters first talk handled
     */
    @Override
    public final String onFirstTalk(L2Npc npc, L2PcInstance player) {
        if (npc.getId() == TELEPORTER) {

            if (player.getParty() == null) {
                return "70000.htm";
            } else if (player.isInParty() && player.getParty().isLeader(player)) {
                return "70000.htm";
            } else if (player.isInParty() && !player.getParty().isLeader(player)) {
                return "70000-no.htm";
            }

            return "70000-no.htm";
        } else if (npc.getId() == CAPTAINS[0]) {
            if (player.isInOlympiadMode()) {

                return "70000-no.htm";

            }
            if (player.getParty() == null) {
                return "70000.htm";
            } else if (player.isInParty() && player.getParty().isLeader(player)) {
                return "70000.htm";
            } else if (player.isInParty() && !player.getParty().isLeader(player)) {
                return "70000-no.htm";
            }
        }
        return "";
    }

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

    private static void createInstance(int id) {
        InstanceManager.getInstance().createInstance(id);
        for (int door : doors) {
            StatsSet set = new StatsSet();
            set.add(DoorData.getInstance().getDoorTemplate(door));
            InstanceManager.getInstance().getInstance(id).addDoor(door, set);
        }
    }

    private static void setPvPInstance(int id) {
        InstanceManager.getInstance().getInstance(id).setPvPInstance(true);
    }

    public void endInstance() {
        Instance inst = InstanceManager.getInstance().getInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
        if (inst != null) {
            for (L2PcInstance playerinside : PvPZoneManager.playerinInstance) {
                playerinside.sendPacket(new CreatureSay(playerinside.getObjectId(), Say2.BATTLEFIELD, playerinside.getName(), "Zone Is closing now because Epic Raid has been started!"));
            }
            inst.setDuration(2 * 1000);
            inst.setEmptyDestroyTime(0);
        }
        InstanceManager.getInstance().destroyInstance(ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
    }

    public static void main(String[] args) {
        instance = new PVPInstance();
    }
}