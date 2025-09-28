package gabriel.events.extremeZone;


import l2r.gameserver.model.Location;

import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ExtremeZone {
    private String name;
    private Location locOfRaidBoss;
    private Location teleNpc;
    private int npcId;
    private String imageName;
    private List<Location> playerLocs;

    public ExtremeZone(String name, String imageName, int npcId, List<Location> playerLoc, Location locOfRaidBoss, Location teleNpc) {
        this.name = name;
        this.imageName = imageName;
        this.npcId = npcId;
        this.locOfRaidBoss = locOfRaidBoss;
        this.teleNpc = teleNpc;
        this.playerLocs = playerLoc;
    }

    public Location getTeleNpc() {
        return teleNpc;
    }

    public String getImageName() {
        return imageName;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocOfRaidBoss() {
        return locOfRaidBoss;
    }

    public void setLocOfRaidBoss(Location locOfRaidBoss) {
        this.locOfRaidBoss = locOfRaidBoss;
    }

    public List<Location> getPlayerLocs() {
        return playerLocs;
    }
}
