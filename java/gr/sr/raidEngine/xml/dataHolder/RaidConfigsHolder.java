package gr.sr.raidEngine.xml.dataHolder;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.raidEngine.RaidConfigs;

import java.util.LinkedList;
import java.util.List;

public class RaidConfigsHolder extends AbstractHolder {
    private static final RaidConfigsHolder holder = new RaidConfigsHolder();
    public final List<RaidConfigs> _configs = new LinkedList();

    public RaidConfigsHolder() {
    }

    public static RaidConfigsHolder getInstance() {
        return holder;
    }

    public List<RaidConfigs> getConfigs() {
        return this._configs;
    }

    public int size() {
        return this._configs.size();
    }

    public void clear() {
        this._configs.clear();
    }
}
