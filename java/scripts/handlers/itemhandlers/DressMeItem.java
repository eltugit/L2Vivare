package scripts.handlers.itemhandlers;

import gabriel.dressme.HandleDressMeDb;
import gabriel.dressmeEngine.data.*;
import gabriel.dressmeEngine.xml.dataHolder.*;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ConfirmDlg;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DressMeItem implements IItemHandler
{
    private static final Logger _log = Logger.getLogger(DressMeItem.class.getName());


    @Override
    public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
    {
        if(!(playable instanceof L2PcInstance))
            return false;

        final L2PcInstance activeChar = (L2PcInstance)playable;
        if(useItem(activeChar, item, false))
            return true;
        return false;
    }

    public static boolean useItem(final L2PcInstance castor, L2ItemInstance item, boolean dlgConfirmed)
    {
        boolean ok;
        boolean returnValue = false;
        int l2Item = item.getItem().getBodyPart();
        switch (l2Item){
            case L2Item.SLOT_FULL_ARMOR:
            case L2Item.SLOT_CHEST:
            case L2Item.SLOT_ALLDRESS:
                ok = checkArmor(castor, item);
                break;
            case L2Item.SLOT_LR_HAND:
            case L2Item.SLOT_R_HAND:
                ok = checkWeapon(castor, item);
                break;
            case L2Item.SLOT_L_HAND: //escudo/sigil
                ok = checkShield(castor, item);
                break;
            case L2Item.SLOT_BACK:
                ok = checkCloak(castor,item);
                break;
            case L2Item.SLOT_HAIR:
            case L2Item.SLOT_HAIR2:
            case L2Item.SLOT_HAIRALL:
                ok = checkHat(castor,item);
                break;
            case L2Item.SLOT_L_BRACELET:
                if(item.getItem().getDressMeEnchant() != 0){
                    ok = checkEnchant(castor,item);
                }else if(item.getItem().getDressMeAgathion() != 0){
                    ok = checkAgathion(castor,item);
                }else{
                    ok = false;
                }
                break;

            default:
                ok = false;
                _log.warning("Non registered bodypart used for DressMeItemHandler: " + l2Item);
                break;
        }



        if(!ok){
            castor.sendMessage("Your wardrobe already contain the requested dress!");
            return false;
        }

        //TODO GABRIEL example of ConfirmDlg
        if(!dlgConfirmed) {
            ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
            dlg.addString("Are you sure you want to use this dress and send it to your wardrobe?");
//                    dlg.addTime(15 * 1000);
            dlg.addRequesterId(castor.getObjectId());
            castor.sendPacket(dlg);
            castor.setItemDlg(item);
            return false;
        }else{
            castor.setItemDlg(null);
        }


        if(ok && castor.destroyItem("Consume", item, 1, castor, true)){
            switch (l2Item){
                case L2Item.SLOT_FULL_ARMOR:
                case L2Item.SLOT_CHEST:
                case L2Item.SLOT_ALLDRESS:
                    returnValue = saveArmor(castor, item);
                    break;
                case L2Item.SLOT_LR_HAND:
                case L2Item.SLOT_R_HAND:
                    returnValue = saveWeapon(castor, item);
                    break;
                case L2Item.SLOT_L_HAND: //escudo/sigil
                    returnValue = saveShield(castor, item);
                    break;
                case L2Item.SLOT_BACK:
                    returnValue = saveCloak(castor,item);
                    break;
                case L2Item.SLOT_HAIR:
                case L2Item.SLOT_HAIR2:
                case L2Item.SLOT_HAIRALL:
                    returnValue = saveHat(castor,item);
                    break;
                case L2Item.SLOT_L_BRACELET:
                    if(item.getItem().getDressMeEnchant() != 0){
                        returnValue = saveEnchant(castor,item);
                    }else if(item.getItem().getDressMeAgathion() != 0){
                        returnValue = saveAgathion(castor,item);
                    }else{
                        returnValue = false;
                    }
                    break;
            }
            castor.sendMessage("Your dress has been added to your wardrobe! Find it using .dressme and apply it!");
        }
        return returnValue;
    }

    private static boolean checkCloak(L2PcInstance player, L2ItemInstance item){
        DressMeCloakData cloak = DressMeCloakHolder.getInstance().getCloakByItemId(item.getId());
        if(cloak == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Cloak Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeCloakInside(player, cloak);
    }

    private static boolean checkArmor(L2PcInstance player, L2ItemInstance item){

        DressMeArmorData dress = DressMeArmorHolder.getInstance().getArmorByPartId(item.getId());
        if(dress == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Armor Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeArmorInside(player, dress);
    }
    private static boolean checkShield(L2PcInstance player, L2ItemInstance item){
        DressMeShieldData shield = DressMeShieldHolder.getInstance().getShieldByItemId(item.getId());
        if(shield == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Shield Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeShieldInside(player, shield);
    }
    private static boolean checkWeapon(L2PcInstance player, L2ItemInstance item){

        DressMeWeaponData weapon = DressMeWeaponHolder.getInstance().getWeapon(item.getId());
        if(weapon == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Weapon Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeWeaponInside(player, weapon);
    }

    private static boolean checkHat(L2PcInstance player, L2ItemInstance item){
        DressMeHatData hat = DressMeHatHolder.getInstance().getHatById(item.getId());
        if(hat == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Weapon Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeHatInside(player, hat);
    }
    
    private static boolean checkEnchant(L2PcInstance player, L2ItemInstance item){
        DressMeEnchantData enchant = DressMeEnchantHolder.getInstance().getEnchantById(item.getId());
        if(enchant == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Weapon Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeEnchantInside(player, enchant);
    }
    private static boolean checkAgathion(L2PcInstance player, L2ItemInstance item){
        DressMeAgathionData agathion = DressMeAgathionHolder.getInstance().getAgathionById(item.getId());
        if(agathion == null){
            _log.log(Level.WARNING, String.format("DressMeItemHandler: Could not find Weapon Dress for item id: %d", item.getId()));
            return false;
        }
        return !HandleDressMeDb.dressMeAgathionInside(player, agathion);
    }
    private static boolean saveEnchant(L2PcInstance player, L2ItemInstance item){
        DressMeEnchantData enchant = DressMeEnchantHolder.getInstance().getEnchantById(item.getId());
        if(enchant == null) return false;
        return HandleDressMeDb.insertDressMeEnchant(player, enchant);
    }
    private static boolean saveAgathion(L2PcInstance player, L2ItemInstance item){
        DressMeAgathionData agathion = DressMeAgathionHolder.getInstance().getAgathionById(item.getId());
        if(agathion == null) return false;
        return HandleDressMeDb.insertDressMeAgathion(player, agathion);
    }
    private static boolean saveCloak(L2PcInstance player, L2ItemInstance item){
        DressMeCloakData cloak = DressMeCloakHolder.getInstance().getCloakByItemId(item.getId());
        if(cloak == null) return false;
        return HandleDressMeDb.insertDressMeCloak(player, cloak);
    }
    private static boolean saveArmor(L2PcInstance player, L2ItemInstance item){
        DressMeArmorData dress = DressMeArmorHolder.getInstance().getArmorByPartId(item.getId());
        if(dress == null) return false;
        return HandleDressMeDb.insertDressMeArmor(player, dress);
    }

    private static boolean saveShield(L2PcInstance player, L2ItemInstance item){
        DressMeShieldData shield = DressMeShieldHolder.getInstance().getShieldByItemId(item.getId());
        if(shield == null) return false;
        return HandleDressMeDb.insertDressMeShield(player, shield);
    }

    private static boolean saveWeapon(L2PcInstance player, L2ItemInstance item){
        DressMeWeaponData weapon = DressMeWeaponHolder.getInstance().getWeapon(item.getId());
        if(weapon == null) return false;
        return HandleDressMeDb.insertDressMeWeapon(player, weapon);
    }

    private static boolean saveHat(L2PcInstance player, L2ItemInstance item){
        DressMeHatData hat = DressMeHatHolder.getInstance().getHatById(item.getId());
        if(hat == null) return false;
        return HandleDressMeDb.insertDressMeHat(player, hat);
    }

}

