package gr.sr.interf.delegate;

import gr.sr.interf.Values;
import gr.sr.l2j.WeaponType;
import gr.sr.l2j.delegate.IItemData;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.EtcItemType;

public class ItemData
        implements IItemData {
    private final L2ItemInstance _item;
    private L2Item _itemTemplate;

    public ItemData(int id) {
        this._item = null;
        this._itemTemplate = l2r.gameserver.data.xml.impl.ItemData.getInstance().getTemplate(id);
    }

    public ItemData(L2ItemInstance cha) {
        this._item = cha;
        if (this._item != null) {
            this._itemTemplate = this._item.getItem();
        }
    }

    public ItemData(int itemId, int count) {
        this._item = l2r.gameserver.data.xml.impl.ItemData.getInstance().createItem("Event Engine ItemData", itemId, count, null);
        if (this._item != null) {
            this._itemTemplate = this._item.getItem();
        }
    }

    public L2ItemInstance getOwner() {
        return this._item;
    }

    public int getObjectId() {
        if (exists()) {
            return getOwner().getObjectId();
        }
        return -1;
    }

    public L2Item getTemplate() {
        return this._itemTemplate;
    }

    public boolean exists() {
        return (this._item != null);
    }

    public boolean isEquipped() {
        if (exists() && this._item.isEquipped()) {
            return true;
        }
        return false;
    }

    public int getItemId() {
        return this._itemTemplate.getId();
    }

    public String getItemName() {
        return this._itemTemplate.getName();
    }

    public int getEnchantLevel() {
        return (this._item != null) ? this._item.getEnchantLevel() : 0;
    }

    public int getCrystalType() {
        return this._itemTemplate.getCrystalType().getId();
    }

    public int getBodyPart() {
        return this._itemTemplate.getBodyPart();
    }

    public boolean isArmor() {
        return this._itemTemplate instanceof l2r.gameserver.model.items.L2Armor;
    }

    public boolean isWeapon() {
        return this._itemTemplate instanceof l2r.gameserver.model.items.L2Weapon;
    }

    public WeaponType getWeaponType() {
        if (isWeapon()) {
            return Values.getInstance().getWeaponType(this);
        }
        return null;
    }

    public boolean isType2Armor() {
        return (this._itemTemplate.getType2() == 1);
    }

    public boolean isType2Weapon() {
        return (this._itemTemplate.getType2() == 0);
    }

    public boolean isType2Accessory() {
        return (this._itemTemplate.getType2() == 2);
    }

    public boolean isJewellery() {
        return (this._itemTemplate.getType2() == 2);
    }

    public boolean isPotion() {
        return (this._itemTemplate.getItemType() == EtcItemType.POTION);
    }

    public boolean isScroll() {
        return (this._itemTemplate.getItemType() == EtcItemType.SCROLL);
    }

    public boolean isPetCollar() {
        if (this._item != null && this._item.isEtcItem() && this._item.getEtcItem().getItemType() == EtcItemType.PET_COLLAR) {
            return true;
        }
        return false;
    }
}


