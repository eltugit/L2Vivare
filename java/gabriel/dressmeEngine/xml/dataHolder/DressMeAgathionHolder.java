package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeAgathionData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeAgathionHolder extends AbstractHolder {
    private static final DressMeAgathionHolder _instance = new DressMeAgathionHolder();


    public static DressMeAgathionHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeAgathionData> _agathions = new ArrayList<>();

    public void addAgathion(gabriel.dressmeEngine.data.DressMeAgathionData aga) {
        _agathions.add(aga);
    }

    public List<gabriel.dressmeEngine.data.DressMeAgathionData> getAllAgathions() {
        return _agathions;
    }

    public gabriel.dressmeEngine.data.DressMeAgathionData getAgathion(int id) {
        for (gabriel.dressmeEngine.data.DressMeAgathionData agathion : _agathions) {
            if (agathion.getId() == id) {
                return agathion;
            }
        }

        return null;
    }


    public gabriel.dressmeEngine.data.DressMeAgathionData getAgathionById(int id) {
        for (DressMeAgathionData agathion : _agathions) {
            if (agathion.getAgathionId() == id)
                return agathion;
        }

        return null;
    }

    @Override
    public int size() {
        return _agathions.size();
    }

    @Override
    public void clear() {
        _agathions.clear();
    }
}
