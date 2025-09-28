package gabriel.dressmeEngine.xml.dataHolder;


import gabriel.dressmeEngine.data.DressMeEnchantData;
import gr.sr.data.xml.AbstractHolder;

import java.util.ArrayList;
import java.util.List;


public final class DressMeEnchantHolder extends AbstractHolder {
    private static final DressMeEnchantHolder _instance = new DressMeEnchantHolder();


    public static DressMeEnchantHolder getInstance() {
        return _instance;
    }

    private final List<gabriel.dressmeEngine.data.DressMeEnchantData> _enchants = new ArrayList<>();

    public void addEnchant(gabriel.dressmeEngine.data.DressMeEnchantData shield) {
        _enchants.add(shield);
    }

    public List<gabriel.dressmeEngine.data.DressMeEnchantData> getAllEnchants() {
        return _enchants;
    }

    public gabriel.dressmeEngine.data.DressMeEnchantData getEnchant(int id) {
        for (gabriel.dressmeEngine.data.DressMeEnchantData enchant : _enchants) {
            if (enchant.getId() == id) {
                return enchant;
            }
        }

        return null;
    }


    public gabriel.dressmeEngine.data.DressMeEnchantData getEnchantById(int id) {
        for (DressMeEnchantData enchant : _enchants) {
            if (enchant.getEnchantId() == id)
                return enchant;
        }

        return null;
    }

    @Override
    public int size() {
        return _enchants.size();
    }

    @Override
    public void clear() {
        _enchants.clear();
    }
}
