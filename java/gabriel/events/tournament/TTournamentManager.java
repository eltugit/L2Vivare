package gabriel.events.tournament;



import gr.sr.utils.Tools;
import l2r.Config;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.DoorData;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class TTournamentManager {
    private static final Logger _log = LoggerFactory.getLogger(TTournamentManager.class);

    private static TTournamentManager instance;
    private final Map<Integer, List<TFight>> fights = new ConcurrentHashMap<>();
    private final Map<Integer, List<TTeam>> teams = new ConcurrentHashMap<>();
    private int period = 0;
    private TTournamentTask _task;
    private Future<?> matchMaking;
    private int instancesIdGen = 80000;

    public Map<Integer, List<TFight>> getFights() {
        return fights;
    }

    public Map<Integer, List<TTeam>> getTeams() {
        return teams;
    }


    public void startMatchMaking() {
        if(matchMaking != null)
            return;
        matchMaking = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
            if (period == 1) {
                matchMaking();
            }
        }, 20000, Config.ARENA_CALL_INTERVAL);
    }

    public void matchMaking() {
        synchronized (teams) {
            Map<Integer, List<TTeam>> active = new HashMap<>();

            for (Map.Entry<Integer, List<TTeam>> team : TTournamentManager.getInstance().getTeams().entrySet()) {
                int qnt = team.getKey();
                List<TTeam> tteam = team.getValue().stream().filter(e -> !e.isInGame()).collect(Collectors.toList());
                Collections.shuffle(tteam);
                tteam = tteam.stream().limit(2).collect(Collectors.toList());

                if (tteam.size() == 2) {
                    createNewFight(tteam, qnt);
                    active.computeIfAbsent(qnt, k -> new LinkedList<>());
                    active.get(qnt).addAll(tteam);
                }
            }

            for (Map.Entry<Integer, List<TTeam>> entry : active.entrySet()) {
                int qnt = entry.getKey();
                List<TTeam> value = entry.getValue();
                for (TTeam tTeam : value) {
                    TTournamentManager.getInstance().getTeams().get(qnt).remove(tTeam);
                    removeTeam(qnt, tTeam);
                }
            }
        }
    }

    private boolean inFight(TTeam toCheck) {
        boolean found = false;
        for (List<TFight> value : TTournamentManager.getInstance().getFights().values()) {
            for (TFight tFight : value) {
                if (tFight.getFirstTeam().getLeaderName().equals(toCheck.getLeaderName()) || tFight.getSecondTeam().getLeaderName().equals(toCheck.getLeaderName())) {
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    private int generateInstance() {
        instancesIdGen++;
        return instancesIdGen;
    }

    private void createNewFight(List<TTeam> team, int qnt) {
        if(team.size() < 2)
            return;

        int instanceId = generateInstance();
        Instance inst = InstanceManager.getInstance().getInstance(instanceId);

        if (inst == null) {
            InstanceManager.getInstance().createInstance(instanceId);
            InstanceManager.getInstance().getInstance(instanceId).setPvPInstance(true);
        }
        inst = InstanceManager.getInstance().getInstance(instanceId);


        StatsSet set = new StatsSet();
        set.add(DoorData.getInstance().getDoorTemplate(24190001));
        set.add(DoorData.getInstance().getDoorTemplate(24190002));
        set.add(DoorData.getInstance().getDoorTemplate(24190003));
        set.add(DoorData.getInstance().getDoorTemplate(24190004));

        inst.addDoor(24190001, set);
        inst.addDoor(24190002, set);
        inst.addDoor(24190003, set);
        inst.addDoor(24190004, set);

        TFight fight = new TFight(qnt, instanceId, false);

        TTournamentManager.getInstance().checkFightExist(qnt);
        TTeam first = team.get(0);
        TTeam second = team.get(1);

        if (first.getLeaderName().equals(second.getLeaderName()))
            return;

        if(inFight(first) || inFight(second))
            return;

        fight.setInst(inst);
        fight.setFirstTeam(first);
        fight.setSecondTeam(second);

        first.setFight(fight);
        second.setFight(fight);

        first.setInGame(true);
        second.setInGame(true);

        TTournamentManager.getInstance().getFights().get(qnt).add(fight);

        //start fight
        ThreadPoolManager.getInstance().scheduleEvent(fight,1000);
    }

    public void removeTeam(int qnt, TTeam team) {
        List<TTeam> toRemoveList = TTournamentManager.getInstance().getTeams().get(qnt);
        TTeam temp = null;
        for (TTeam tTeam : toRemoveList) {
            if (tTeam.getLeaderName().equals(team.getLeaderName())) {
                temp = tTeam;
                break;
            }
        }
        if (temp != null)
            TTournamentManager.getInstance().getTeams().get(qnt).remove(temp);

        try {
            TTournamentManager.getInstance().getTeams().get(qnt).remove(team);
        } catch (Exception e) {
            //
        }

    }

    public void closeTournament() {
        stopMatchMaking();
        period = 0;
//        instance = null;
    }

    
    public static TTournamentManager getInstance() {
        if (instance == null)
            instance = new TTournamentManager();
        return instance;
    }

    public void stopMatchMaking() {
        if (matchMaking != null) {
            matchMaking.cancel(true);
            matchMaking = null;
        }
    }

    private TTournamentManager() {
        startMatchMaking();
        _log.info("Custom Tournament by Gabriel has been started");
    }

    
    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public TTournamentTask get_task() {
        return _task;
    }

    public void set_task(TTournamentTask _task) {
        this._task = _task;
    }


    private void checkTeamExist(int quantity) {
        List<TTeam> temp = teams.get(quantity);
        if (temp == null) {
            temp = new ArrayList<>();
        }
        teams.put(quantity, temp);
    }

    public void checkFightExist(int quantity) {
        List<TFight> temp = fights.get(quantity);
        if (temp == null) {
            temp = new ArrayList<>();
        }
        fights.put(quantity, temp);
    }

    
    public boolean registerTeam(L2PcInstance player, int qntPlayers) {
        checkFightExist(qntPlayers);
        checkTeamExist(qntPlayers);
        TTeam team = new TTeam();
        L2Party party = player.getParty();
        boolean found = false;
        if (party != null) {
            L2PcInstance partLeader = party.getLeader();
            for (L2PcInstance partyMember : party.getMembers()) {
                if (Config.ARENA_EVENT_PREVENT_IP && checkIfAlreadyRegistered(partyMember, qntPlayers)) {
                    partLeader.sendPacket(new CreatureSay(partLeader.getObjectId(), Say2.BATTLEFIELD, partLeader.getName(), String.format("Member %s is already registered", partyMember.getName())));
                    found = true;
                }
                team.addPlayer(partyMember);
                team.setLeaderName(partLeader.getName());
            }
            if (found) {
                team.clean();
                return false;
            }
        }else{
            if (Config.ARENA_EVENT_PREVENT_IP && checkIfAlreadyRegistered(player, qntPlayers)) {
                player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You are already registered."));
                found = true;
            }
            team.addPlayer(player);
            team.setLeaderName(player.getName());

            if (found) {
                team.clean();
                return false;
            }
        }
        teams.get(qntPlayers).add(team);
        return true;
    }

    /**
     * Unregister through npc
     *
     * @param player
     * @param quantity
     * @return
     */
    
    public boolean unregisterTeam(L2PcInstance player, int quantity) {
        checkFightExist(quantity);
        checkTeamExist(quantity);
        for (TTeam tTeam : teams.get(quantity)) {
            if (tTeam.getLeaderName().equals(player.getName())) {
                tTeam.clean();
                teams.get(quantity).remove(tTeam);
                for (L2PcInstance tTeamPlayer : tTeam.getPlayers()) {
                    tTeamPlayer.settTeam(null);
                }
                return true;
            }
        }
        player.sendPacket(new CreatureSay(player.getObjectId(), Say2.BATTLEFIELD, player.getName(), "You are not registered!"));
        return false;
    }

    
    public void onDisconnect(L2PcInstance player) {
        if (player != null) {
            TTeam team = player.gettTeam();
            player.setInArenaEvent(false);
            if (team != null) {
                if (team.getFight() == null) {
                    unregisterTeam(player);
                } else {
                    player.gettTeam().removePlayer(player);
                }
            }
            player.setArenaQuantity(0);
        }
    }

    /**
     * Unregister through leaving game
     *
     * @param player
     */
    public void unregisterTeam(L2PcInstance player) {
        int qnt = 0;
        TTeam playerTeam = null;
        for (Map.Entry<Integer, List<TTeam>> team : teams.entrySet()) {
            qnt = team.getKey();
            List<TTeam> teamsInHash = team.getValue();
            for (TTeam tTeam : teamsInHash) {
                for (L2PcInstance tTeamPlayer : tTeam.getPlayers()) {
                    if (player == tTeamPlayer) {
                        playerTeam = tTeam;
                        break;
                    }
                }
                if (playerTeam != null) {
                    break;
                }
            }
        }
        if (playerTeam != null) {
            playerTeam.sendMessage(String.format("Player %s left the party. Your Tournament registration has been canceled", player.getName()));
            playerTeam.clean();
            teams.get(qnt).remove(playerTeam);
        }
    }

    private boolean checkIfAlreadyRegistered(L2PcInstance player, int qnt) {
        for (TTeam team : teams.get(qnt)) {
            for (L2PcInstance teamPlayer : team.getPlayers()) {
                if (Tools.isDualBox(teamPlayer, player)) {
                    if(qnt == 1){
                        player.sendMessage(player.getName() + " Is already registered with another acc");
                        return true;
                    }else{
                        for (L2PcInstance member : player.getParty().getMembers()) {
                            member.sendMessage(player.getName() + " Is already registered with another acc");
                            return true;
                        }
                    }

                }

            }
            if (team.getPlayers().contains(player))
                return true;
        }
        return false;
    }

    
    public TFight getFight(int quantity, int instanceId) {
        return fights.get(quantity).stream().filter(e -> e.getInstanceId() == instanceId).findAny().orElse(null);
    }

    
    public List<TFight> getFights(int qnt) {
        return fights.get(qnt);
    }

}
