package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeCloakData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeCloakHolder extends AbstractHolder {
    private static final DressMeCloakHolder _instance = new DressMeCloakHolder();


    public static DressMeCloakHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeCloakData> _cloak = new ArrayList<>();

    public void addCloak(gabriel.dressmeEngine.data.DressMeCloakData cloak) {
        _cloak.add(cloak);
    }

    public List<gabriel.dressmeEngine.data.DressMeCloakData> getAllCloaks() {
        return _cloak;
    }

    public gabriel.dressmeEngine.data.DressMeCloakData getCloak(int id) {
        for (gabriel.dressmeEngine.data.DressMeCloakData cloak : _cloak) {
            if (cloak.getId() == id) {
                return cloak;
            }
        }

        return null;
    }


    public gabriel.dressmeEngine.data.DressMeCloakData getCloakByItemId(int id) {
        for (DressMeCloakData cloak : _cloak) {
            if (cloak.getCloakId() == id)
                return cloak;
        }
        return null;
    }

    @Override
    public int size() {
        return _cloak.size();
    }

    @Override
    public void clear() {
        _cloak.clear();
    }
}
