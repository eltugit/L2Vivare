package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeShieldData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeShieldHolder extends AbstractHolder {
    private static final DressMeShieldHolder _instance = new DressMeShieldHolder();


    public static DressMeShieldHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeShieldData> _shield = new ArrayList<>();

    public void addShield(gabriel.dressmeEngine.data.DressMeShieldData shield) {
        _shield.add(shield);
    }

    public List<gabriel.dressmeEngine.data.DressMeShieldData> getAllShields() {
        return _shield;
    }

    public gabriel.dressmeEngine.data.DressMeShieldData getShield(int id) {
        for (gabriel.dressmeEngine.data.DressMeShieldData shield : _shield) {
            if (shield.getId() == id) {
                return shield;
            }
        }

        return null;
    }


    public gabriel.dressmeEngine.data.DressMeShieldData getShieldByItemId(int id) {
        for (DressMeShieldData shield : _shield) {
            if (shield.getShieldId() == id)
                return shield;
        }

        return null;
    }

    @Override
    public int size() {
        return _shield.size();
    }

    @Override
    public void clear() {
        _shield.clear();
    }
}
