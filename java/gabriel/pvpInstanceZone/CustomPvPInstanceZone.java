package gabriel.pvpInstanceZone;


import l2r.gameserver.model.Location;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class CustomPvPInstanceZone {
    private String name;
    private Location[] spawnLocs;
    private Integer[] boss;

    public CustomPvPInstanceZone(String name, Location[] spawnLocs, Integer[] boss) {
        this.name = name;
        this.spawnLocs = spawnLocs;
        this.boss = boss.length == 0 ? null : boss;
    }

    public String getName() {
        return name;
    }


    public Location[] getSpawnLocs() {
        return spawnLocs;
    }

    public Integer[] getBoss() {
        return boss;
    }
}
