package gr.sr.raidEngine.xml.dataHolder;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.raidEngine.RaidLocation;

import java.util.LinkedList;
import java.util.List;

public class RaidLocationsHolder extends AbstractHolder {
    private static final RaidLocationsHolder holder = new RaidLocationsHolder();
    public final List<RaidLocation> _locations = new LinkedList();

    public RaidLocationsHolder() {
    }

    public static RaidLocationsHolder getInstance() {
        return holder;
    }

    public List<RaidLocation> getLocations() {
        return this._locations;
    }

    public int size() {
        return this._locations.size();
    }

    public void clear() {
        this._locations.clear();
    }
}
