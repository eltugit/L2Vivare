package gabriel.epicRaid;

import l2r.gameserver.model.Location;

import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: gabsoncs
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class EpicRaid {
    private String name;
    private Location locOfRaidBoss;
    private List<Location> playerTeleports;
    private Location teleToZone;
    private Location teleNpc;
    private int npcId;
    private String imageName;

    public EpicRaid(String name, String imageName, int npcId, Location locOfRaidBoss, List<Location> locToTeleport, Location teleZone, Location teleNpc) {
        this.name = name;
        this.imageName = imageName;
        this.npcId = npcId;
        this.locOfRaidBoss = locOfRaidBoss;
        this.playerTeleports = locToTeleport;
        this.teleToZone = teleZone;
        this.teleNpc = teleNpc;
    }

    public Location getTeleNpc() {
        return teleNpc;
    }

    public Location getTeleToZone() {
        return teleToZone;
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

    public List<Location> getPlayerTeleports() {
        return playerTeleports;
    }
}
