package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeWeaponData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeWeaponHolder extends AbstractHolder {
    private static final DressMeWeaponHolder _instance = new DressMeWeaponHolder();


    public static DressMeWeaponHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeWeaponData> _weapons = new ArrayList<>();

    public void addWeapon(gabriel.dressmeEngine.data.DressMeWeaponData weapon) {
        _weapons.add(weapon);
    }

    public List<gabriel.dressmeEngine.data.DressMeWeaponData> getAllWeapons() {
        return _weapons;
    }


    public gabriel.dressmeEngine.data.DressMeWeaponData getWeapon(int id) {
        for (DressMeWeaponData weapon : _weapons) {
            if (weapon.getId() == id) {
                return weapon;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return _weapons.size();
    }

    @Override
    public void clear() {
        _weapons.clear();
    }
}
