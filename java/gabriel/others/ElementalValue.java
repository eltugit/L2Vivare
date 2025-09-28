package gabriel.others;

import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.items.instance.L2ItemInstance;

public class ElementalValue {
    public static int getElementalValue(L2ItemInstance item, byte element) {
        if (item.getElementals() == null) {
            return 0;
        }
        for (Elementals elm : item.getElementals()) {
            if (elm.getElement() == element) {
                return elm.getValue();
            }
        }
        return 0;
    }
}
