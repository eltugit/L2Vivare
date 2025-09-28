package gr.sr.l2j.delegate;

import gr.sr.l2j.WeaponType;

public interface IItemData {
    boolean exists();

    boolean isEquipped();

    int getObjectId();

    int getItemId();

    String getItemName();

    int getEnchantLevel();

    int getCrystalType();

    int getBodyPart();

    boolean isArmor();

    boolean isWeapon();

    WeaponType getWeaponType();

    boolean isType2Armor();

    boolean isType2Weapon();

    boolean isType2Accessory();

    boolean isJewellery();

    boolean isPotion();

    boolean isScroll();

    boolean isPetCollar();
}


