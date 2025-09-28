package gr.sr.javaBuffer.xml.dataHolder;

import gr.sr.data.xml.AbstractHolder;
import gr.sr.javaBuffer.BuffsInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuffsHolder extends AbstractHolder {
    private static final BuffsHolder holder = new BuffsHolder();
    public final Map<Integer, BuffsInstance> _buffs = new ConcurrentHashMap();

    public BuffsHolder() {
    }

    public static BuffsHolder getInstance() {
        return holder;
    }

    public BuffsInstance getBuff(int id) {
        return (BuffsInstance)this._buffs.get(id);
    }

    public Map<Integer, BuffsInstance> getBuffs() {
        return this._buffs;
    }

    public int size() {
        return this._buffs.size();
    }

    public void clear() {
        this._buffs.clear();
    }
}
