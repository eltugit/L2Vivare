package gabriel.epicRaid;

import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;

public class EpicRaidHolder extends AbstractHolder {
    private static final EpicRaidHolder _instance = new EpicRaidHolder();

    public static EpicRaidHolder getInstance() {
        return _instance;
    }

    private final List<EpicRaid> _raids = new ArrayList<>();

    public void addRaid(EpicRaid raid) {
        _raids.add(raid);
    }

    public List<EpicRaid> getAllRaids() {
        return _raids;
    }

    public EpicRaid getRaid(int id) {
        for (EpicRaid raid : _raids) {
            if (raid.getNpcId() == id) {
                return raid;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return _raids.size();
    }

    @Override
    public void clear() {
        _raids.clear();
    }
}
