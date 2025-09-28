package gabriel.scriptsGab.forge;


import gabriel.config.GabConfig;
import gabriel.scriptsGab.forge.xml.FoundationHolder;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.items.type.ArmorType;
import l2r.gameserver.model.items.type.CrystalType;
import l2r.gameserver.model.items.type.EtcItemType;

public class ForgeElement {
    protected static String[] generateAttribution(L2ItemInstance item, int slot, L2PcInstance player, boolean hasBonus) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String immposible = "<font color=FF0000>Attribution of this item is not possible!</font>";
        String maxenchant = "<font color=FF0000>You can no more attribute.</font>";
        String heronot = "<font color=FF0000>Insert attribute in the hero weapons impossible.</font>";
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getItem().getIcon();
            data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
            if ((item.isElementable()) && (itemCheckGrade(hasBonus, item))) {
                if (item.isHeroItem()) {
                    data[2] = heronot;
                    data[3] = slotclose;
                } else if ((((item.isArmor()) && (item.getElementCount() >= GabConfig.BBS_FORGE_ARMOR_ATTRIBUTE_MAX)))
                        || ((item.isWeapon()) && (item.getElementCount() >= GabConfig.BBS_FORGE_WEAPON_ATTRIBUTE_MAX))
                        || item.getItemType() == ArmorType.SHIELD
                        || item.getItemType() == ArmorType.SIGIL) {
                    data[2] = maxenchant;
                    data[3] = slotclose;
                } else {
                    data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:attribute:item:").append(slot).append("\" value=\"").append("Insert Attribute").append("\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = immposible;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = "An item is not Equipped!";
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    protected static String[] generateEnchant(L2ItemInstance item, int max, int slot, L2PcInstance player) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String maxenchant = "<font color=FF0000>Enchanted to maximum</font>";
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getItem().getIcon();
            data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
            if (!(item.getItemType() == EtcItemType.ARROW)) {
                if ((item.getEnchantLevel() >= max) || (!(item.isEnchantable() == 1))) {
                    data[2] = maxenchant;
                    data[3] = slotclose;
                } else {
                    data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:enchant:item:").append(slot).append("\" value=\"").append("Enchant").append("\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = dot;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = "Something isnt equipped!";
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    protected static String[] generateFoundation(L2ItemInstance item, int slot, L2PcInstance player) {
        String[] data = new String[4];

        String noicon = "icon.NOIMAGE";
        String slotclose = "L2UI_CT1.ItemWindow_DF_SlotBox_Disable";
        String dot = "<font color=\"FF0000\">...</font>";
        String no = "<font color=FF0000>Can't change</font>";
        String picenchant = "l2ui_ch3.multisell_plusicon";
        String pvp = "icon.pvp_tab";

        if (item != null) {
            data[0] = item.getItem().getIcon();
            data[1] = new StringBuilder().append(item.getName()).append(" ").append(item.getEnchantLevel() > 0 ? new StringBuilder().append("+").append(item.getEnchantLevel()).toString() : "").toString();
            if (!(item.getItemType() == EtcItemType.ARROW)) {
                int found = FoundationHolder.getInstance().getFoundation(item.getId());
                if (found == -1) {
                    data[2] = no;
                    data[3] = slotclose;
                } else {
                    data[2] = new StringBuilder().append("<button action=\"bypass _bbsforge:foundation:item:").append(slot).append("\" value=\"").append("Exchange").append("\"width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">").toString();
					/*
					if (item.getTemplate().isPvP())
						data[3] = pvp;
					else
					*/
                    data[3] = picenchant;
                }
            } else {
                data[2] = dot;
                data[3] = slotclose;
            }
        } else {
            data[0] = noicon;
            data[1] = "Something is not equipped!";
            data[2] = dot;
            data[3] = slotclose;
        }

        return data;
    }

    protected static String page(L2PcInstance player) {
        return HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/scripts/services/forge/page_template.htm");
    }

    protected static boolean itemCheckGrade(boolean hasBonus, L2ItemInstance item) {
        CrystalType type = item.getItem().getCrystalType();

        switch (type) {
//			case NONE:
//				return hasBonus;
//			case D:
//				return hasBonus;
//			case C:
//				return hasBonus;
//			case B:
//				return hasBonus;
//			case A:
//				return hasBonus;
//			case S:
//				return hasBonus;
            case S80:
                return hasBonus;
            case S84:
                return hasBonus;
        }
        return false;
    }

    protected static boolean canEnchantArmorAttribute(int attr, L2ItemInstance item) {
        switch (attr) {
            case 0:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.FIRE)) == null)
                    break;
                return false;
            case 1:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.WATER)) == null)
                    break;
                return false;
            case 2:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.WIND)) == null)
                    break;
                return false;
            case 3:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.EARTH)) == null)
                    break;
                return false;
            case 4:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.HOLY)) == null)
                    break;
                return false;
            case 5:
                if (item.getElemental(Elementals.getOppositeElement(Elementals.DARK)) == null)
                    break;
                return false;
        }

        return true;
    }
}
