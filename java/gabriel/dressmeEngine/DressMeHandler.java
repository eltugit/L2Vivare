package gabriel.dressmeEngine;


import gabriel.config.GabConfig;
import gabriel.customAgathion.Runnable.RequestPreviewAgathion;
import gabriel.dressmeEngine.data.DressMeArmorData;
import gabriel.dressmeEngine.data.DressMeHatData;
import gabriel.dressmeEngine.data.DressMeShieldData;
import gabriel.dressmeEngine.data.DressMeWeaponData;
import gabriel.dressmeEngine.util.Util;
import gabriel.enchantColor.runnable.RequestPreviewEnchantColor;
import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;

public class DressMeHandler {
    private static final Logger _log = LoggerFactory.getLogger(DressMeHandler.class);

    public static void visuality(L2PcInstance player, L2ItemInstance item, int visual) {

        if (item == null || player == null)
            return;

        item.setVisualItemId(visual);
        updateVisualInDb(item, visual);

        if (visual > 0) {
            player.sendMessage(item.getName() + " visual change to " + Util.getItemName(visual));
        } else {
            player.sendMessage("Visual removed from " + item.getName() + ".");
        }

        player.broadcastUserInfo();
    }

    public static void visualityCustom(L2PcInstance player, L2ItemInstance item, int visual) {
        item.setVisualItemId(visual);
        player.broadcastUserInfo();
    }

    public static void updateVisualInDb(L2ItemInstance item, int visual) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE items SET visual_item_id=? " + "WHERE object_id = ?")) {
            ps.setInt(1, visual);
            ps.setInt(2, item.getObjectId());
            ps.executeUpdate();
        } catch (Exception e) {
            if (Config.DEBUG) {
                _log.error("Could not update dress me item in DB: Reason: " + e.getMessage(), e);
            }
        }
    }

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeWeaponData> initWeaponMap(String type, Map<Integer, DressMeWeaponData> map, L2ItemInstance slot) {
        boolean isMagic = slot.getWeaponItem().isMagicWeapon();

        if (type.equals("SWORD") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND) && !isMagic) {
            return map = DressMeLoader.SWORD;
        } else if (type.equals("SWORD") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND) && isMagic) {
            return map = DressMeLoader.SWORDMAGE;
        } else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND) && !isMagic) {
            return map = DressMeLoader.BLUNT;
        } else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() != L2Item.SLOT_LR_HAND) && isMagic) {
            return map = DressMeLoader.BLUNTMAGE;
        } else if (type.equals("SWORD") && (slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND)) {
            return map = DressMeLoader.BIGSWORD;
        } else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND) && !isMagic) {
            return map = DressMeLoader.BIGBLUNT;
        } else if (type.equals("BLUNT") && (slot.getItem().getBodyPart() == L2Item.SLOT_LR_HAND) && isMagic) {
            return map = DressMeLoader.BIGBLUNTMAGE;
        } else if (type.equals("DAGGER")) {
            return map = DressMeLoader.DAGGER;
        } else if (type.equals("BOW")) {
            return map = DressMeLoader.BOW;
        } else if (type.equals("POLE")) {
            return map = DressMeLoader.POLE;
        } else if (type.equals("FIST")) {
            return map = DressMeLoader.FIST;
        } else if (type.equals("DUAL")) {
            return map = DressMeLoader.DUAL;
        } else if (type.equals("DUALFIST")) {
            return map = DressMeLoader.DUALFIST;
        } else if (type.equals("FISHINGROD")) {
            return map = DressMeLoader.ROD;
        } else if (type.equals("CROSSBOW")) {
            return map = DressMeLoader.CROSSBOW;
        } else if (type.equals("RAPIER")) {
            return map = DressMeLoader.RAPIER;
        } else if (type.equals("ANCIENTSWORD")) {
            return map = DressMeLoader.ANCIENTSWORD;
        } else if (type.equals("DUALDAGGER")) {
            return map = DressMeLoader.DUALDAGGER;
        } else {
            _log.error("Dress me system: Unknown weapon type: " + type);
            return null;
        }
    }

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeShieldData> initShieldMap(String type, Map<Integer, DressMeShieldData> map, L2ItemInstance slot) {
        if (type.equals("SIGIL")) {
            return map = DressMeLoader.SIGIL;
        } else if (type.equals("SHIELD")) {
            return map = DressMeLoader.SHIELD;
        } else return null;
    }

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeArmorData> initArmorMap(String type, Map<Integer, DressMeArmorData> map, L2ItemInstance slot) {
        if (GabConfig.ALLOW_ALL_SETS) {
            return map = DressMeLoader.ALL;
        } else {
            if (type.equals("LIGHT")) {
                return map = DressMeLoader.LIGHT;
            } else if (type.equals("HEAVY")) {
                return map = DressMeLoader.HEAVY;
            } else if (type.equals("ROBE")) {
                return map = DressMeLoader.ROBE;
            } else if (type.equals("SUIT")) {
                return map = DressMeLoader.SUIT;
            } else {
                _log.error("Dress me system: Unknown armor type: " + type);
                return null;
            }
        }
    }

    public static Map<Integer, gabriel.dressmeEngine.data.DressMeHatData> initHatMap(Map<Integer, DressMeHatData> map, L2ItemInstance slot) {
        if ((slot.getLocationSlot() == 2) && (slot.getItem().getBodyPart() != L2Item.SLOT_HAIRALL)) {
            return map = DressMeLoader.HAIR;
        } else if ((slot.getLocationSlot() == 2) && (slot.getItem().getBodyPart() == L2Item.SLOT_HAIRALL)) {
            return map = DressMeLoader.HAIR_FULL;
        } else if (slot.getLocationSlot() == 3) {
            return map = DressMeLoader.HAIR2;
        } else {
            _log.error("Dress me system: Unknown hat slot: " + slot.getLocationSlot());
            return null;
        }
    }

    public static void handleEnchantChange(L2PcInstance activeChar, int enchant, boolean trying) {

        if (!checkEnchantValue(enchant)) {
            activeChar.sendMessage("Non valid value entered! Contact the administration!");
            return;
        }

        if (trying) {
            activeChar.setQuickVar("tryEnchant", enchant);
            refreshVisual(activeChar);
            ThreadPoolManager.getInstance().scheduleGeneral(new RequestPreviewEnchantColor.RemoveWearItemsTask(activeChar), 5 * 1000);
            activeChar.sendMessage("You are now trying enchantment color");
        } else {
            if (enchant == 0) {
                activeChar.unsetVar("customEnchantColor");
                activeChar.sendMessage("You Reseted your Custom Enchant Color!");
                refreshVisual(activeChar);
                return;
            }
            activeChar.setVar("customEnchantColor", String.valueOf(enchant));
            refreshVisual(activeChar);
            activeChar.sendMessage("Custom Enchant Changed!");
        }
    }

    private static void refreshVisual(L2PcInstance activeChar) {
        activeChar.startAbnormalEffect(AbnormalEffect.E_VESPER_1);
        activeChar.stopAbnormalEffect(AbnormalEffect.E_VESPER_1);
    }

    private static boolean checkEnchantValue(int enchant) {
        return (enchant >= 0 && enchant <= 112);
    }

    public static void handleCustomAgathionChange(L2PcInstance activeChar, int agathionId, boolean trying) {

        if (!checkAgathionValue(agathionId)) {
            activeChar.sendMessage("Non valid value entered! Contact the administration!");
            return;
        }

        if (trying) {
            activeChar.setQuickVar("tryAgathion", agathionId);
            refreshVisual(activeChar);
            ThreadPoolManager.getInstance().scheduleGeneral(new RequestPreviewAgathion.RemoveWearItemsTask(activeChar), 5 * 1000);
            activeChar.sendMessage("You are now trying an Agathion");
        } else {
            if (agathionId == 0) {
                activeChar.unsetVar("agathionCustom");
                activeChar.sendMessage("You Reseted your Agathion");
                refreshVisual(activeChar);
                return;
            }
            activeChar.setVar("agathionCustom", String.valueOf(agathionId));
            refreshVisual(activeChar);
            activeChar.sendMessage("Custom Agathion Changed!");

        }
    }

    private static boolean checkAgathionValue(int agathionId) {
        return (agathionId >= 1606 && agathionId <= 1629) || agathionId == 1595 || agathionId == 51596 || agathionId == 0;
    }


}
