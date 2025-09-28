package gr.sr.raidEngine.xml.dataHolder;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.raidEngine.RaidGroup;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class RaidAndDropsHolder extends AbstractHolder {
    private static final RaidAndDropsHolder holder = new RaidAndDropsHolder();
    public final List<RaidGroup> _raids = new LinkedList();

    public Comparator<RaidGroup> compareByChance = (raidgroup1, raidGroup2) -> {
        double gp1 = (double)raidgroup1.getGroupChance();
        double gp2 = (double)raidGroup2.getGroupChance();
        if (gp1 > gp2) {
            return 1;
        } else {
            return gp1 == gp2 ? 0 : -1;
        }
    };

    public RaidAndDropsHolder() {
    }

    public static RaidAndDropsHolder getInstance() {
        return holder;
    }

    public List<RaidGroup> getRaidGroups() {
        return this._raids;
    }

    public int size() {
        return this._raids.size();
    }

    public void clear() {
        this._raids.clear();
    }
}
