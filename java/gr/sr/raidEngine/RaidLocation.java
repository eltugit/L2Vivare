package gr.sr.raidEngine;

import l2r.gameserver.model.Location;

public class RaidLocation {
    private final String name;
    private final Location loc;

    public RaidLocation(String name, Location location) {
        this.name = name;
        this.loc = location;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.loc;
    }
}
