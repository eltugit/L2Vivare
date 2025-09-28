package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeArmorData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeArmorHolder extends AbstractHolder {
    private static final DressMeArmorHolder _instance = new DressMeArmorHolder();


    public static DressMeArmorHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeArmorData> _dress = new ArrayList<>();

    public void addDress(gabriel.dressmeEngine.data.DressMeArmorData armorset) {
        _dress.add(armorset);
    }

    public List<gabriel.dressmeEngine.data.DressMeArmorData> getAllDress() {
        return _dress;
    }

    public gabriel.dressmeEngine.data.DressMeArmorData getArmor(int id) {
        for (gabriel.dressmeEngine.data.DressMeArmorData dress : _dress) {
            if (dress.getId() == id) {
                return dress;
            }
        }

        return null;
    }


    public gabriel.dressmeEngine.data.DressMeArmorData getArmorByPartId(int partId) {
        for (DressMeArmorData dress : _dress) {
            if (dress.getChest() == partId || dress.getLegs() == partId || dress.getGloves() == partId || dress.getFeet() == partId)
                return dress;
        }

        return null;
    }

    @Override
    public int size() {
        return _dress.size();
    }

    @Override
    public void clear() {
        _dress.clear();
    }
}
