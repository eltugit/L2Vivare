package gabriel.dressmeEngine;


import gabriel.config.GabConfig;
import gabriel.dressmeEngine.data.DressMeCloakData;
import gabriel.dressmeEngine.handler.DressMeVCmd;
import gabriel.dressmeEngine.xml.dataHolder.DressMeArmorHolder;
import gabriel.dressmeEngine.xml.dataParser.*;
import l2r.gameserver.handler.VoicedCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class DressMeLoader {
    private static final Logger _log = LoggerFactory.getLogger(DressMeLoader.class);

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> SWORD;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> SWORDMAGE;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BLUNT;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BLUNTMAGE;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> DAGGER;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BOW;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> POLE;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> FIST;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> DUAL;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> DUALFIST;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BIGSWORD;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> ROD;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BIGBLUNT;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> BIGBLUNTMAGE;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> CROSSBOW;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> RAPIER;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> ANCIENTSWORD;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> DUALDAGGER;

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> LIGHT;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> HEAVY;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> ROBE;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> ALL;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> SUIT;


    public static Map<Integer, gabriel.dressmeEngine.data.DressMeHatData> HAIR;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeHatData> HAIR2;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeHatData> HAIR_FULL;

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeShieldData> SIGIL;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeShieldData> SHIELD;

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeEnchantData> ENCHANT;
    public static Map<Integer, gabriel.dressmeEngine.data.DressMeAgathionData> AGATHION;

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeCloakData> CLOAKS;


    public static void load() {
        DressMeArmorParser.getInstance().load();
        DressMeCloakParser.getInstance().load();
        DressMeHatParser.getInstance().load();
        DressMeShieldParser.getInstance().load();
        DressMeWeaponParser.getInstance().load();
        DressMeEnchantParser.getInstance().load();
        DressMeAgathionParser.getInstance().load();

        SWORD = new LinkedHashMap<>();
        BLUNT = new LinkedHashMap<>();
        DAGGER = new LinkedHashMap<>();
        BOW = new LinkedHashMap<>();
        POLE = new LinkedHashMap<>();
        FIST = new LinkedHashMap<>();
        DUAL = new LinkedHashMap<>();
        DUALFIST = new LinkedHashMap<>();
        BIGSWORD = new LinkedHashMap<>();
        ROD = new LinkedHashMap<>();
        BIGBLUNT = new LinkedHashMap<>();
        CROSSBOW = new LinkedHashMap<>();
        RAPIER = new LinkedHashMap<>();
        ANCIENTSWORD = new LinkedHashMap<>();
        DUALDAGGER = new LinkedHashMap<>();

        SWORDMAGE = new LinkedHashMap<>();
        BLUNTMAGE = new LinkedHashMap<>();
        BIGBLUNTMAGE = new LinkedHashMap<>();

        LIGHT = new LinkedHashMap<>();
        HEAVY = new LinkedHashMap<>();
        ROBE = new LinkedHashMap<>();
        ALL = new LinkedHashMap<>();
        SUIT = new LinkedHashMap<>();

        HAIR = new LinkedHashMap<>();
        HAIR2 = new LinkedHashMap<>();
        HAIR_FULL = new LinkedHashMap<>();

        SIGIL = new LinkedHashMap<>();
        SHIELD = new LinkedHashMap<>();

        ENCHANT = new LinkedHashMap<>();
        AGATHION = new LinkedHashMap<>();

        CLOAKS = new LinkedHashMap<>();

        parseWeapon();
        parseArmor();
        parseHat();
        parseShields();
        parseEnchants();
        parseAgathions();
        parseCloaks();
        VoicedCommandHandler.getInstance().registerHandler(new DressMeVCmd());
    }

    private static int parseWeapon() {
        int Sword = 1, Blunt = 1, Dagger = 1, Bow = 1, Pole = 1, Fist = 1, DualSword = 1, DualFist = 1, BigSword = 1, Rod = 1, BigBlunt = 1, Crossbow = 1, Rapier = 1, AncientSword = 1, DualDagger = 1,
                SwordMage = 1, BluntMage = 1, BigBluntMage = 1;

        for (gabriel.dressmeEngine.data.DressMeWeaponData weapon : gabriel.dressmeEngine.xml.dataHolder.DressMeWeaponHolder.getInstance().getAllWeapons()) {
            if (weapon.getType().equals("SWORD") && !weapon.isBig() && !weapon.isMagic()) {
                SWORD.put(Sword, weapon);
                Sword++;
            } else if (weapon.getType().equals("SWORD") && !weapon.isBig() && weapon.isMagic()) {
                SWORDMAGE.put(SwordMage, weapon);
                SwordMage++;
            } else if (weapon.getType().equals("BLUNT") && !weapon.isBig() && !weapon.isMagic()) {
                BLUNT.put(Blunt, weapon);
                Blunt++;
            } else if (weapon.getType().equals("BLUNT") && !weapon.isBig() && weapon.isMagic()) {
                BLUNTMAGE.put(BluntMage, weapon);
                BluntMage++;
            } else if (weapon.getType().equals("SWORD") && weapon.isBig() && !weapon.isMagic()) {
                BIGSWORD.put(BigSword, weapon);
                BigSword++;
            } else if (weapon.getType().equals("BLUNT") && weapon.isBig() && weapon.isMagic()) {
                BIGBLUNTMAGE.put(BigBluntMage, weapon);
                BigBluntMage++;
            } else if (weapon.getType().equals("BLUNT") && weapon.isBig() && !weapon.isMagic()) {
                BIGBLUNT.put(BigBlunt, weapon);
                BigBlunt++;
            } else if (weapon.getType().equals("DAGGER")) {
                DAGGER.put(Dagger, weapon);
                Dagger++;
            } else if (weapon.getType().equals("BOW")) {
                BOW.put(Bow, weapon);
                Bow++;
            } else if (weapon.getType().equals("POLE")) {
                POLE.put(Pole, weapon);
                Pole++;
            } else if (weapon.getType().equals("FIST")) {
                FIST.put(Fist, weapon);
                Fist++;
            } else if (weapon.getType().equals("DUAL")) {
                DUAL.put(DualSword, weapon);
                DualSword++;
            } else if (weapon.getType().equals("DUALFIST")) {
                DUALFIST.put(DualFist, weapon);
                DualFist++;
            } else if (weapon.getType().equals("FISHINGROD")) {
                ROD.put(Rod, weapon);
                Rod++;
            } else if (weapon.getType().equals("CROSSBOW")) {
                CROSSBOW.put(Crossbow, weapon);
                Crossbow++;
            } else if (weapon.getType().equals("RAPIER")) {
                RAPIER.put(Rapier, weapon);
                Rapier++;
            } else if (weapon.getType().equals("ANCIENTSWORD")) {
                ANCIENTSWORD.put(AncientSword, weapon);
                AncientSword++;
            } else if (weapon.getType().equals("DUALDAGGER")) {
                DUALDAGGER.put(DualDagger, weapon);
                DualDagger++;
            } else {
                _log.error("Dress me system: Can't find type: " + weapon.getType());
            }
        }

        _log.info("Dress me system: Loaded " + (Sword - 1) + " Sword(s).");
        _log.info("Dress me system: Loaded " + (Blunt - 1) + " Blunt(s).");
        _log.info("Dress me system: Loaded " + (Dagger - 1) + " Dagger(s).");
        _log.info("Dress me system: Loaded " + (Bow - 1) + " Bow(s).");
        _log.info("Dress me system: Loaded " + (Pole - 1) + " Pole(s).");
        _log.info("Dress me system: Loaded " + (Fist - 1) + " Fist(s).");
        _log.info("Dress me system: Loaded " + (DualSword - 1) + " Dual Sword(s).");
        _log.info("Dress me system: Loaded " + (DualFist - 1) + " Dual Fist(s).");
        _log.info("Dress me system: Loaded " + (BigSword - 1) + " Big Sword(s).");
        _log.info("Dress me system: Loaded " + (Rod - 1) + " Rod(s).");
        _log.info("Dress me system: Loaded " + (BigBlunt - 1) + " Big Blunt(s).");
        _log.info("Dress me system: Loaded " + (Crossbow - 1) + " Crossbow(s).");
        _log.info("Dress me system: Loaded " + (Rapier - 1) + " Rapier(s).");
        _log.info("Dress me system: Loaded " + (AncientSword - 1) + " Ancient Sword(s).");
        _log.info("Dress me system: Loaded " + (DualDagger - 1) + " Dual Dagger(s).");
        _log.info("Dress me system: Loaded " + (SwordMage - 1) + " Buster(s).");
        _log.info("Dress me system: Loaded " + (BluntMage - 1) + " Caster(s).");
        _log.info("Dress me system: Loaded " + (BigBluntMage - 1) + " Staff(s).");

        return 0;
    }

    private static int parseArmor() {
        int light = 1, heavy = 1, robe = 1, suit = 1, all = 1;

        for (gabriel.dressmeEngine.data.DressMeArmorData armor : DressMeArmorHolder.getInstance().getAllDress()) {

            if (GabConfig.ALLOW_ALL_SETS) {
                ALL.put(all, armor);
                all++;
            } else {

                if (armor.getType().equals("LIGHT") && !armor.isSuit()) {
                    LIGHT.put(light, armor);
                    light++;
                } else if (armor.getType().equals("HEAVY") && !armor.isSuit()) {
                    HEAVY.put(heavy, armor);
                    heavy++;
                } else if (armor.getType().equals("ROBE") && !armor.isSuit()) {
                    ROBE.put(robe, armor);
                    robe++;
                } else if (armor.isSuit()) {
                    SUIT.put(suit, armor);
                    suit++;
                } else {
                    _log.error("Dress me system: Can't find type: " + armor.getType());
                }
            }
        }
        if (GabConfig.ALLOW_ALL_SETS) {
            _log.info("Dress me system: Loaded " + (all - 1) + " Armor(s).");

        } else {
            _log.info("Dress me system: Loaded " + (light - 1) + " Light Armor(s).");
            _log.info("Dress me system: Loaded " + (heavy - 1) + " Heavy Armor(s).");
            _log.info("Dress me system: Loaded " + (robe - 1) + " Robe Armor(s).");
            _log.info("Dress me system: Loaded " + (suit - 1) + " Suit(s).");
        }

        return 0;
    }

    private static int parseHat() {
        int hair = 1, hair2 = 1, full_hair = 1;

        for (gabriel.dressmeEngine.data.DressMeHatData hat : gabriel.dressmeEngine.xml.dataHolder.DressMeHatHolder.getInstance().getAllHats()) {
            if (hat.getSlot() == 1) {
                HAIR.put(hair, hat);
                hair++;
            } else if (hat.getSlot() == 2) {
                HAIR2.put(hair2, hat);
                hair2++;
            } else if (hat.getSlot() == 3) {
                HAIR_FULL.put(full_hair, hat);
                full_hair++;
            } else {
                _log.error("Dress me system: Can't find slot: " + hat.getSlot());
            }
        }

        _log.info("Dress me system: Loaded " + (hair - 1) + " Hair(s).");
        _log.info("Dress me system: Loaded " + (hair2 - 1) + " Hair2(s).");
        _log.info("Dress me system: Loaded " + (full_hair - 1) + " Full Hair(s).");

        return 0;
    }

    private static int parseShields() {
        int shield = 1, sigil = 1;

        for (gabriel.dressmeEngine.data.DressMeShieldData shld : gabriel.dressmeEngine.xml.dataHolder.DressMeShieldHolder.getInstance().getAllShields()) {
            if (shld.isShield()) {
                SHIELD.put(shield, shld);
                shield++;
            } else {
                SIGIL.put(sigil, shld);
                sigil++;
            }
        }
        _log.info("Dress me system: Loaded " + (shield - 1) + " Shield(s).");
        _log.info("Dress me system: Loaded " + (sigil - 1) + " Sigil(s).");

        return 0;
    }

    private static int parseEnchants() {
        int enchant = 1;

        for (gabriel.dressmeEngine.data.DressMeEnchantData ench : gabriel.dressmeEngine.xml.dataHolder.DressMeEnchantHolder.getInstance().getAllEnchants()) {
            ENCHANT.put(enchant, ench);
            enchant++;

        }
        _log.info("Dress me system: Loaded " + (enchant - 1) + " Enchant(s).");

        return 0;
    }

    private static int parseAgathions() {
        int agathion = 1;

        for (gabriel.dressmeEngine.data.DressMeAgathionData aga : gabriel.dressmeEngine.xml.dataHolder.DressMeAgathionHolder.getInstance().getAllAgathions()) {
            AGATHION.put(agathion, aga);
            agathion++;

        }
        _log.info("Dress me system: Loaded " + (agathion - 1) + " Agathion(s).");

        return 0;
    }

    private static int parseCloaks() {
        int cloak = 1;

        for (DressMeCloakData cloak_data : gabriel.dressmeEngine.xml.dataHolder.DressMeCloakHolder.getInstance().getAllCloaks()) {
            CLOAKS.put(cloak, cloak_data);
            cloak++;

        }
        _log.info("Dress me system: Loaded " + (cloak - 1) + " Cloaks(s).");

        return 0;
    }
}
