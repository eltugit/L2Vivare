package gabriel.pvpInstanceZone;


import gabriel.Utils.GabUtils;
import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.xml.PvPInstanceParser;
import l2r.gameserver.enums.Team;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.util.Rnd;

import java.util.*;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class PvPZoneManager {

    public static HashMap<String, Integer> scoreClan = new HashMap<String, Integer>();

    public static ArrayList<L2PcInstance> playerinInstance = new ArrayList<L2PcInstance>();
    public static PvPInstanceTeam BlueTeam;
    public static PvPInstanceTeam RedTeam;
    public static PvPInstanceTeam GreenTeam;
    public static PvPInstanceTeam YellowTeam;
    private long timeStarted = 0L;

    public void start() {
        timeStarted = Calendar.getInstance().getTimeInMillis();
    }

    public String getRemainingTime() {
        return timeStarted < Calendar.getInstance().getTimeInMillis() ? "---" : GabUtils.getTimeRemaining(timeStarted);
    }

    public static void setLocationIndex(int locationIndex) {
        PvPZoneManager.locationIndex = locationIndex;
    }

    private static ArrayList<L2PcInstance> getSmallestTeamTvT() {
        List<ArrayList<L2PcInstance>> teams = new ArrayList<>();
        teams.add(BlueTeam.getListPlayers());
        teams.add(RedTeam.getListPlayers());

        teams.sort(Comparator.comparingInt(ArrayList::size));

        ArrayList<L2PcInstance> smallest = teams.get(0);
        for (ArrayList<L2PcInstance> tm : teams) {
            if (tm.size() <= smallest.size()) {
                smallest = tm;
            }
        }
        return smallest;

    }

    private static ArrayList<L2PcInstance> getSmallestTeam() {
        List<ArrayList<L2PcInstance>> teams = new ArrayList<>();
        teams.add(YellowTeam.getListPlayers());
        teams.add(GreenTeam.getListPlayers());
        teams.add(RedTeam.getListPlayers());
        teams.add(BlueTeam.getListPlayers());

        teams.sort(Comparator.comparingInt(ArrayList::size));

        ArrayList<L2PcInstance> smallest = teams.get(0);
        for (ArrayList<L2PcInstance> tm : teams) {
            if (tm.size() <= smallest.size()) {
                smallest = tm;
            }
        }
        return smallest;

    }

    public static void addPlayerToTeam(L2PcInstance player) {
        if (isTvTvTvT()) {
            int playerTeam = playerTeamNummer(player);
            getSmallestTeam().add(player);
            player.setTeam4t(playerTeam);
        }
        if (isTvT()) {
            Team playerTeam = playerTeam(player);
            getSmallestTeamTvT().add(player);
            player.setTeam(playerTeam);
        }
    }

    public static PvPInstanceTeam getPlayerTeam(L2PcInstance player) {
        if (player.getTeam4t() == 1) {
            return BlueTeam;
        } else if (player.getTeam4t() == 2) {
            return RedTeam;
        } else if (player.getTeam4t() == 3) {
            return GreenTeam;
        } else {
            return YellowTeam;
        }
    }

    private static Team playerTeam(L2PcInstance player) {
        Team team = Team.NONE;
        ArrayList<L2PcInstance> smallest = getSmallestTeamTvT();
        if (smallest == BlueTeam.getListPlayers()) {
            team = Team.BLUE;
        } else if (smallest == RedTeam.getListPlayers()) {
            team = Team.RED;
        }
        return team;
    }

    public static PvPInstanceTeam getPlayerTeamTvT(L2PcInstance player) {
        if (player.getTeam() == Team.BLUE) {
            return BlueTeam;
        } else if (player.getTeam() == Team.RED) {
            return RedTeam;
        }
        return null;
    }

    public static void handleLeavePvPZone(L2PcInstance player, boolean disconnected) {
        if (GabUtils.isInPvPInstance(player)) {
            if (PvPZoneManager.isTvT()) {
                PvPInstanceTeam team = PvPZoneManager.getPlayerTeamTvT(player);
                if (team != null)
                    team.getListPlayers().remove(player);
                player.setTeam(Team.NONE);
            }
            if (PvPZoneManager.isTvTvTvT()) {
                PvPZoneManager.getPlayerTeam(player).getListPlayers().remove(player);
                player.setTeam4t(0);
            }
            player.setInstanceId(0);
            PvPZoneManager.playerinInstance.remove(player);
            if (disconnected)
                player.setXYZ(ConfigPvPInstance.PVP_INSTANCE_BACK_X, ConfigPvPInstance.PVP_INSTANCE_BACK_Y, ConfigPvPInstance.PVP_INSTANCE_BACK_Z);
        }
    }

    private static int playerTeamNummer(L2PcInstance player) {
        int team = 0;
        ArrayList<L2PcInstance> smallest = getSmallestTeam();
        if (smallest == BlueTeam.getListPlayers()) {
            team = 1;
        } else if (smallest == RedTeam.getListPlayers()) {
            team = 2;
        } else if (smallest == GreenTeam.getListPlayers()) {
            team = 3;
        } else if (smallest == YellowTeam.getListPlayers()) {
            team = 4;
        }
        return team;
    }


    public static final CustomPvPInstanceZone[] PVPINSTANCERESPAWNS = PvPInstanceParser.getInstance().getZonesArray();

    private static int locationIndex = 0;
    private static int PvPInstanceMode = 0;
    private static int lastPvPInstanceMode = 0;


    public static int getlocationindex() {
        return locationIndex;
    }

    public static int getPvPInstanceMode() {
        return PvPInstanceMode;
    }

    public static void incrementlocationIndex() {
        locationIndex++;
        if (locationIndex > (PVPINSTANCERESPAWNS.length - 1)) {
            locationIndex = 0;
        }
    }

    static int[] smallzones = {9, 14};

    public static void setRandomLocationindex() {
        locationIndex = Rnd.get(PVPINSTANCERESPAWNS.length);
        if (getPvPInstanceMode() == 0) {
            if (Arrays.stream(smallzones).anyMatch(i -> i == getlocationindex())) {
                setRandomLocationindex();
            }
        }
        if (locationIndex > (PVPINSTANCERESPAWNS.length - 1)) {
            locationIndex = 0;
        }
    }

    public static void setNormalOnly() {
        PvPInstanceMode = 0;
    }

    public static void setTvTOnly() {
        PvPInstanceMode = 1;
    }

    public static void setTvTvTvTOnly() {
        PvPInstanceMode = 2;
    }

    public static void setAnnonymOnly() {
        PvPInstanceMode = 3;
    }

    private static int previousNormal = 0;

    public static void gabriel2Normal1Annonym() {
//        if (previousNormal < 4) {
//            setNormalOnly();
//            previousNormal++;
//        } else {
//            setAnnonymOnly();
//            previousNormal = 0;
//        }
        switch (previousNormal){
            case 0:
            case 1:
                setNormalOnly();
                break;
            case 2:
                setTvTvTvTOnly();
                break;
            case 3:
                setAnnonymOnly();
                break;

        }
        previousNormal++;
        if(previousNormal > 3){
            previousNormal = 0;
        }
    }


    public static boolean isNormal() {
        return PvPInstanceMode == 0;
    }

    public static boolean isTvT() {
        return PvPInstanceMode == 1;
    }

    public static boolean isTvTvTvT() {
        return PvPInstanceMode == 2;
    }

    public static boolean isAnnonym() {
        return PvPInstanceMode == 3;
    }

    protected static PvPZoneManager instance;


    public static PvPZoneManager getInstance() {
        if (instance == null)
            instance = new PvPZoneManager();
        return instance;
    }

    public static void setRandomPvPInstanceMode() {
        PvPInstanceMode = Rnd.get(4); // 0-3
        //0 = normal pvp
        //1 Team pvp
        //2 TvTvTvT
        //3 Free for all ANONYM
        if (PvPInstanceMode > 3) {
            PvPInstanceMode = 0;
        }
    }

    public static void incrementPvPInstanceMode() {
        PvPInstanceMode++;
        //0 = normal pvp
        //1 Team pvp
        //2 TvTvTvT
        //3 Free for all ANONYM
        if (PvPInstanceMode > 3) {
            PvPInstanceMode = 0;
        }
    }
}
