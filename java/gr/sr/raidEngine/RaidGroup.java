package gr.sr.raidEngine;

import l2r.util.Rnd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RaidGroup {
    private final float groupChance;
    private final Map<Integer, Integer> raids;
    private final List<RaidDrop> drops;
    private final RaidType raidType;

    public RaidGroup(float chance, RaidType type) {
        this.groupChance = chance;
        this.raids = new LinkedHashMap<>();
        this.drops = new ArrayList<>();
        this.raidType = type;
    }

    public float getGroupChance() {
        return this.groupChance;
    }

    public Map<Integer, Integer> getRaids() {
        return this.raids;
    }

    public List<RaidDrop> getDrops() {
        return this.drops;
    }

    public String getTypeName() {
        return this.raidType.getName();
    }

    public List<RaidDrop> getRandomDrops(int indx) {
        ArrayList<RaidDrop> temp = new ArrayList<>();
        this.drops.stream().filter((drop) -> drop.getMinOccurs() > 0).forEach((drp) -> {
            for(int i = 0; i < drp.getMinOccurs(); ++i) {
                temp.add(drp);
            }

        });
        indx = (Integer)this.raids.get(indx) + 1;
        if (temp.size() < indx) {
            indx = Rnd.get(temp.size(), indx);
        } else {
            indx = Rnd.get(temp.size(), temp.size() + 1);
        }

        while(temp.size() < indx) {
            RaidDrop raidDrop;
            if ((raidDrop = (RaidDrop)this.drops.get(Rnd.get(this.drops.size()))).getMaxOccurs() == 0) {
                temp.add(raidDrop);
            } else if (temp.stream().filter((drp) -> {
                return drp.getItemId() == raidDrop.getItemId();
            }).count() < (long)raidDrop.getMaxOccurs()) {
                temp.add(raidDrop);
            }
        }
        
        return temp;
    }
}
