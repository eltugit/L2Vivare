package gabriel.events.challengerZone;


import l2r.gameserver.model.Location;

import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class ChallengerZone {
    private String name;
    private Location teleNpc;
    private int npcId;
    private String imageName;
    private List<Location> playerLocs;

    public ChallengerZone(String name, String imageName, int npcId, List<Location> playerLoc, Location teleNpc) {
        this.name = name;
        this.imageName = imageName;
        this.npcId = npcId;
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

    public List<Location> getPlayerLocs() {
        return playerLocs;
    }
}
