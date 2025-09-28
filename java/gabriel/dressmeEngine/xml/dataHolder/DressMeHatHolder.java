package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeHatData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeHatHolder extends AbstractHolder {
    private static final DressMeHatHolder _instance = new DressMeHatHolder();


    public static DressMeHatHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeHatData> _hat = new ArrayList<>();

    public void addHat(gabriel.dressmeEngine.data.DressMeHatData hat) {
        _hat.add(hat);
    }

    public List<gabriel.dressmeEngine.data.DressMeHatData> getAllHats() {
        return _hat;
    }

    public gabriel.dressmeEngine.data.DressMeHatData getHat(int id) {
        for (gabriel.dressmeEngine.data.DressMeHatData hat : _hat) {
            if (hat.getId() == id) {
                return hat;
            }
        }
        return null;
    }


    public gabriel.dressmeEngine.data.DressMeHatData getHatById(int id) {
        for (DressMeHatData hat : _hat) {
            if (hat.getHatId() == id) {
                return hat;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return _hat.size();
    }

    @Override
    public void clear() {
        _hat.clear();
    }
}
