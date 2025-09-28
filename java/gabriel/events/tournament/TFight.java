package gabriel.events.tournament;



import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.events.tournament.lol.LOLRankDAO;
import gabriel.events.tournament.lol.LOLTournamentManager;
import l2r.Config;
import l2r.gameserver.instancemanager.InstanceManager;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Instance;
import l2r.gameserver.network.serverpackets.ExSendUIEvent;
import l2r.gameserver.util.Broadcast;
import l2r.util.Rnd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class TFight implements Runnable {
    private TTeam firstTeam;
    private TTeam secondTeam;
    private final int numberOfPlayers;
    private final int instanceId;
    private TTeam winner;
    private TTeam loser;
    private final List<Location> locations = new ArrayList<>();
    private long startTime;
    private long matchStart = 0L;
    private Instance inst = null;
    private boolean lol;
    public void setInst(Instance inst) {
        this.inst = inst;
    }

    private final int maxPointsPerWin = 5;

    public List<L2PcInstance> getSpectators() {
        return spectators;
    }

    private final List<L2PcInstance> spectators = new ArrayList<>();

    
    public List<L2PcInstance> getToRemoveSpectators() {
        return toRemoveSpectators;
    }

    private final List<L2PcInstance> toRemoveSpectators = new ArrayList<>();

    public TFight(int numberOfPlayers, int instanceId, boolean lol) {
        this.numberOfPlayers = numberOfPlayers;
        this.instanceId = instanceId;
        Location teamOne = new Location(Config.ARENA_TEAM1_X, Config.ARENA_TEAM1_Y, Config.ARENA_TEAM1_Z);
        Location teamTwo = new Location(Config.ARENA_TEAM2_X, Config.ARENA_TEAM2_Y, Config.ARENA_TEAM2_Z);
        locations.add(teamOne);
        locations.add(teamTwo);
        this.lol = lol;
    }

    @Override
    public void run() {
        //teleport to Arena
        startTime = System.currentTimeMillis();
        Location teamOne = locations.get(0);
        Location teamTwo = locations.get(1);
        teleportTeams(teamOne, teamTwo);
        immobilizeTeams(true);
        setTeams(1, 2);
        Broadcast.toAllOnlinePlayers(String.format("New %d vs %d  Fight Started (%s vs %s)", numberOfPlayers, numberOfPlayers, firstTeam.getLeaderName(), secondTeam.getLeaderName()));
        sendMessage("The Battle Start in " + Config.ARENA_WAIT_INTERVAL / 1000 + " Seconds");

        try {
            Thread.sleep(Config.ARENA_WAIT_INTERVAL);

        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        //unparalize and start game
        matchStart = System.currentTimeMillis();
        immobilizeTeams(false);
        sendMessage("The Battle Has Started!");

        inst.setDuration(Config.ARENA_TIME_RUN_MAX * 60000);
        inst.setShowTimer(true);
        inst.setTimerIncrase(false);
        inst.setTimerText("Match Ends In");
        updateTimer();
        while (checkWinner()) {
            // check players status each  seconds
            try {
                Thread.sleep(Config.ARENA_CHECK_INTERVAL);
            } catch (InterruptedException e) {
                if (Config.DEBUG)
                    e.printStackTrace();
                break;
            }
        }


        int minutes = GabUtils.getMinutesFromStart(matchStart);
        //remove minutes duration from max minutes
        minutes = maxPointsPerWin - minutes;
        if(minutes < 1)
            minutes = 1;

        if (winner == null && loser == null) {
            Broadcast.toAllOnlinePlayers(String.format("Fight %s vs %s Ended! Tie!", firstTeam.getLeaderName(), secondTeam.getLeaderName()));
        } else {
            if(lol) {
                for (L2PcInstance player : winner.getPlayers()) {
                    LOLRankDAO.getInstance().incrementWins(player, minutes, numberOfPlayers > 2);
                }

                for (L2PcInstance player : loser.getPlayers()) {
                    LOLRankDAO.getInstance().incrementLose(player, minutes, numberOfPlayers > 2);
                }
            }

            Broadcast.toAllOnlinePlayers(String.format("Fight %s vs %s Ended! %s Win!", firstTeam.getLeaderName(), secondTeam.getLeaderName(), winner.getLeaderName()));
        }

        rewardWinner();

        //teleport back
        try {
            Thread.sleep(Config.ARENA_CALL_INTERVAL);
        } catch (InterruptedException e) {
            if (Config.DEBUG)
                e.printStackTrace();
        }
        Location back = new Location(Config.ARENA_TELEPORTBACK_X, Config.ARENA_TELEPORTBACK_Y, Config.ARENA_TELEPORTBACK_Z);
        teleportTeams(back);
        setTeams(0, 0);
        cleanSpectators();
        if(lol){
            LOLTournamentManager.getInstance().getFights(numberOfPlayers).remove(this);
        }else {
            TTournamentManager.getInstance().getFights(numberOfPlayers).remove(this);
        }
        destroyInstance();
        clean();
    }

    private void updateTimer(){
        firstTeam.getPlayers().forEach(e-> sendInstanceUpdate(e, false));
        secondTeam.getPlayers().forEach(e-> sendInstanceUpdate(e, false));
    }

    private void sendInstanceUpdate(L2PcInstance pl, boolean hide){
        final int startTime = (int) ((System.currentTimeMillis() - inst.getInstanceStartTime()) / 1000);
        final int endTime = (int) ((inst.getInstanceEndTime() - inst.getInstanceStartTime()) / 1000);
        if (inst.isTimerIncrease()) {
            pl.sendPacket(new ExSendUIEvent(pl, hide, true, startTime, endTime, inst.getTimerText()));
        } else {
            pl.sendPacket(new ExSendUIEvent(pl, hide, false, endTime - startTime, 0, inst.getTimerText()));
        }
    }

    
    public int getInstanceId() {
        return instanceId;
    }

    int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setFirstTeam(TTeam firstTeam) {
        this.firstTeam = firstTeam;
    }

    public void setSecondTeam(TTeam secondTeam) {
        this.secondTeam = secondTeam;
    }

    
    public TTeam getFirstTeam() {
        return firstTeam;
    }

    
    public TTeam getSecondTeam() {
        return secondTeam;
    }

    private void immobilizeTeams(boolean val) {
        firstTeam.immobilizeTeam(val);
        secondTeam.immobilizeTeam(val);
    }

    private void setTeams(int val, int val2) {
        firstTeam.setTeam(val);
        secondTeam.setTeam(val2);
    }

    /**
     * Used to Teleport back
     *
     * @param loc
     */
    private void teleportTeams(Location loc) {
        firstTeam.teleportTeam(loc, 0);
        secondTeam.teleportTeam(loc, 0);
    }

    /**
     * Location to teleport to arena
     *
     * @param loc
     * @param loc2
     */
    private void teleportTeams(Location loc, Location loc2) {
        firstTeam.teleportTeam(loc, instanceId);
        secondTeam.teleportTeam(loc2, instanceId);
    }

    public void sendMessage(String msg) {
        firstTeam.sendMessage(msg);
        secondTeam.sendMessage(msg);
    }

    private boolean checkWinner() {
        long elapsedTime = System.currentTimeMillis() - startTime;

        if (firstTeam.isTeamDead() && !secondTeam.isTeamDead()) {
            winner = secondTeam;
            loser = firstTeam;
            return false;
        } else if (secondTeam.isTeamDead() && !firstTeam.isTeamDead()) {
            winner = firstTeam;
            loser = secondTeam;
            return false;
        } else return elapsedTime <= (Config.ARENA_TIME_RUN_MAX * 60000);
    }

    private void rewardWinner() {
        if (winner != null && loser != null) {
            winner.reward();
        }
    }

    
    public void addSpectator(L2PcInstance player) {
        Location loc = locations.get(Rnd.get(2));
        spectators.add(player);
        player.enterObserverTournamentMode(loc, instanceId, numberOfPlayers);
    }

    private void destroyInstance() {
        InstanceManager.getInstance().destroyInstance(instanceId);
    }

    private void clean() {
        firstTeam.clean();
        secondTeam.clean();
        firstTeam = null;
        secondTeam = null;
        winner = null;
        loser = null;
        locations.clear();
        spectators.clear();
    }

    private void cleanSpectators() {
        for (L2PcInstance spectator : spectators) {
            spectator.leaveTournamentObserverMode();
        }
        cleanToRemoveSpectators();
    }

    public void cleanToRemoveSpectators() {
        spectators.removeAll(toRemoveSpectators);
    }

}
