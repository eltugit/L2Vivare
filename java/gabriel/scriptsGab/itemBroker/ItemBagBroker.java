package gabriel.scriptsGab.itemBroker;

import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;

public class ItemBagBroker {
    private L2Item item;
    private L2PcInstance owner;
    private long price;

    public ItemBagBroker(L2Item item, L2PcInstance owner, long price) {
        this.item = item;
        this.owner = owner;
        this.price = price;
    }

    public L2Item getItem() {
        return item;
    }

    public L2PcInstance getOwner() {
        return owner;
    }

    public long getPrice() {
        return price;
    }
}
