/*
 * Copyright (C) 2004-2015 L2J Server
 *
 * This file is part of L2J Server.
 *
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.network.serverpackets;

import gabriel.Utils.GabUtils;
import gabriel.dressme.ArmorSetVisualiser;
import gabriel.pvpInstanceZone.PvPZoneManager;
import gabriel.pvpInstanceZone.utils.Anonymity;
import l2r.Config;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.data.xml.impl.ExperienceData;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.instancemanager.CursedWeaponsManager;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.model.itemcontainer.Inventory;

/**
 * This is what you will see your self
 */
public final class UserInfo extends L2GameServerPacket {
    private final L2PcInstance _activeChar;
    private int _relation;
    private int _airShipHelm;

    private final int _runSpd, _walkSpd;
    private final int _swimRunSpd, _swimWalkSpd;
    private final int _flyRunSpd, _flyWalkSpd;
    private final double _moveMultiplier;


    private static final int[] PAPERDOLL_ORDER_UP = new int[]
            {
                    Inventory.PAPERDOLL_UNDER,
                    Inventory.PAPERDOLL_REAR,
                    Inventory.PAPERDOLL_LEAR,
                    Inventory.PAPERDOLL_NECK,
                    Inventory.PAPERDOLL_RFINGER,
                    Inventory.PAPERDOLL_LFINGER,
                    Inventory.PAPERDOLL_HEAD,
                    Inventory.PAPERDOLL_RHAND,
                    Inventory.PAPERDOLL_LHAND

            };

    private static final int[] PAPERDOLL_ORDER_DOWN = new int[]
            {
                    Inventory.PAPERDOLL_CLOAK,
                    Inventory.PAPERDOLL_RHAND,
                    Inventory.PAPERDOLL_HAIR,
                    Inventory.PAPERDOLL_HAIR2,
                    Inventory.PAPERDOLL_RBRACELET,
                    Inventory.PAPERDOLL_LBRACELET,
                    Inventory.PAPERDOLL_DECO1,
                    Inventory.PAPERDOLL_DECO2,
                    Inventory.PAPERDOLL_DECO3,
                    Inventory.PAPERDOLL_DECO4,
                    Inventory.PAPERDOLL_DECO5,
                    Inventory.PAPERDOLL_DECO6,
                    Inventory.PAPERDOLL_BELT
            };

    public UserInfo(L2PcInstance cha) {
        _activeChar = cha;

        int _territoryId = TerritoryWarManager.getInstance().getRegisteredTerritoryId(cha);
        _relation = _activeChar.isClanLeader() ? 0x40 : 0;
        if (_activeChar.getSiegeState() == 1) {
            if (_territoryId == 0) {
                _relation |= 0x180;
            } else {
                _relation |= 0x1000;
            }
        }
        if (_activeChar.getSiegeState() == 2) {
            _relation |= 0x80;
        }
        // _isDisguised = TerritoryWarManager.getInstance().isDisguised(character.getObjectId());
        if (_activeChar.isInAirShip() && _activeChar.getAirShip().isCaptain(_activeChar)) {
            _airShipHelm = _activeChar.getAirShip().getHelmItemId();
        } else {
            _airShipHelm = 0;
        }

        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
    }

    // This is what you will see from yourself (Only you see this)
    @Override
    protected final void writeImpl() {

        try {
            writeC(0x32);

            writeD(_activeChar.getX());
            writeD(_activeChar.getY());
            writeD(_activeChar.getZ() + Config.CLIENT_SHIFTZ);
            writeD(_activeChar.getVehicle() != null ? _activeChar.getVehicle().getObjectId() : 0);

            writeD(_activeChar.getObjectId());
            writeS(_activeChar.getAppearance().getVisibleName());
            writeD(_activeChar.getRace().ordinal());
            writeD(_activeChar.getAppearance().getSex() ? 1 : 0);

            writeD(_activeChar.getBaseClass());

            writeD(_activeChar.getLevel());
            writeQ(_activeChar.getExp());
            writeF((float) (_activeChar.getExp() - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel())) / (ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - ExperienceData.getInstance().getExpForLevel(_activeChar.getLevel()))); // High Five exp %
            writeD(_activeChar.getSTR());
            writeD(_activeChar.getDEX());
            writeD(_activeChar.getCON());
            writeD(_activeChar.getINT());
            writeD(_activeChar.getWIT());
            writeD(_activeChar.getMEN());
            writeD(_activeChar.getMaxHp());
            writeD((int) _activeChar.getCurrentHp());
            writeD(_activeChar.getMaxMp());
            writeD((int) _activeChar.getCurrentMp());
            writeD(_activeChar.getSp());
            writeD(_activeChar.getCurrentLoad());
            writeD(_activeChar.getMaxLoad());

            writeD(_activeChar.getActiveWeaponItem() != null ? 40 : 20); // 20 no weapon, 40 weapon equipped

            for (int slot : getPaperdollOrder()) {
                writeD(_activeChar.getInventory().getPaperdollObjectId(slot));
            }


            for (int slot : PAPERDOLL_ORDER_UP) {
                int itemId = _activeChar.getQuickVarI("DressMeTry", 0);
                int hairDressSlot = _activeChar.getQuickVarI("hairslotDressMeTry", 0);
                if (slot == Inventory.PAPERDOLL_RHAND && itemId != 0 && hairDressSlot == 0) {
                    writeD(itemId);
                } else {
                    writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(slot));
                }
            }


//        if(_activeChar.isInOlympiadMode() || this.getClient().getActiveChar().getVarB("showVisualChange")){
            if (ArmorSetVisualiser.hasArmorEquipped(_activeChar) || ArmorSetVisualiser.isSuitEquipped(Inventory.PAPERDOLL_CHEST, _activeChar)) {
                int[] ids = ArmorSetVisualiser.getRightDressForChest(_activeChar);
                writeD(ids[0]);
                writeD(ids[1]);
                writeD(ids[2]);
                writeD(ids[3]);
            } else {
                writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(Inventory.PAPERDOLL_GLOVES));
                writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(Inventory.PAPERDOLL_CHEST));
                writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(Inventory.PAPERDOLL_LEGS));
                writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(Inventory.PAPERDOLL_FEET));
            }
//        }else{
//            writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_GLOVES));
//            writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_CHEST));
//            writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_LEGS));
//            writeD(_activeChar.getInventory().getPaperdollItemDisplayId(Inventory.PAPERDOLL_FEET));
//        }


            for (int slot : PAPERDOLL_ORDER_DOWN) {
                int itemId = _activeChar.getQuickVarI("DressMeTry", 0);
                int hairDressSlot = _activeChar.getQuickVarI("hairslotDressMeTry", 0);

                if (slot == Inventory.PAPERDOLL_RHAND && itemId != 0 && hairDressSlot == 0) {
                    writeD(itemId);
                } else if (slot == Inventory.PAPERDOLL_HAIR && itemId != 0 && (hairDressSlot == 1 || hairDressSlot == 3)) {
                    writeD(itemId);
                } else if (slot == Inventory.PAPERDOLL_HAIR2 && itemId != 0 && hairDressSlot != 0) {
                    if (hairDressSlot == 3) {
                        writeD(0);
                    }
                    if (hairDressSlot == 2) {
                        writeD(itemId);
                    }
                } else {
                    writeD(_activeChar.getInventory().getPaperdollItemVisualDisplayId(slot));
                }
            }


            for (int slot : getPaperdollOrder()) {
                writeD(_activeChar.getInventory().getPaperdollAugmentationId(slot));
            }

            writeD(_activeChar.getInventory().getTalismanSlots());
            writeD(_activeChar.getInventory().canEquipCloak() ? 1 : 0);
            writeD((int) _activeChar.getPAtk(null));
            writeD((int) _activeChar.getPAtkSpd());
            writeD((int) _activeChar.getPDef(null));
            writeD(_activeChar.getEvasionRate(null));
            writeD(_activeChar.getAccuracy());
            writeD(_activeChar.getCriticalHit(null, null));
            writeD((int) _activeChar.getMAtk(null, null));

            writeD(_activeChar.getMAtkSpd());
            writeD((int) _activeChar.getPAtkSpd());

            writeD((int) _activeChar.getMDef(null, null));

            writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violet name
            writeD(_activeChar.getKarma());

            writeD(_runSpd);
            writeD(_walkSpd);
            writeD(_swimRunSpd);
            writeD(_swimWalkSpd);
            writeD(_flyRunSpd);
            writeD(_flyWalkSpd);
            writeD(_flyRunSpd);
            writeD(_flyWalkSpd);
            writeF(_moveMultiplier);
            writeF(_activeChar.getAttackSpeedMultiplier());

            writeF(_activeChar.getCollisionRadius());
            writeF(_activeChar.getCollisionHeight());

            writeD(_activeChar.getAppearance().getHairStyle());
            writeD(_activeChar.getAppearance().getHairColor());
            writeD(_activeChar.getAppearance().getFace());
            writeD(_activeChar.isGM() ? 1 : 0); // builder level

            String title = _activeChar.getTitle();
            if (_activeChar.isGM() && _activeChar.isInvisible()) {
                title = "Invisible";
            }
            if (_activeChar.getPoly().isMorphed()) {
                L2NpcTemplate polyObj = NpcTable.getInstance().getTemplate(_activeChar.getPoly().getPolyId());
                if (polyObj != null) {
                    title += " - " + polyObj.getName();
                }
            }
            writeS(title);

            writeD(_activeChar.getClanId());
            writeD(_activeChar.getClanCrestId());
            writeD(_activeChar.getAllyId());
            writeD(_activeChar.getAllyCrestId()); // ally crest id
            // 0x40 leader rights
            // siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
            writeD(_relation);
            writeC(_activeChar.getMountType().ordinal()); // mount type
            writeC(_activeChar.getPrivateStoreType().getId());
            writeC(_activeChar.hasCrystallization() ? 1 : 0);
            writeD(_activeChar.getPkKills());
            writeD(_activeChar.getPvpKills());

            writeH(_activeChar.getCubics().size());
            for (int id : _activeChar.getCubics().keySet()) {
                writeH(id);
            }

            writeC(_activeChar.isInPartyMatchRoom() || (GabUtils.isInPvPInstance(_activeChar) && !PvPZoneManager.isAnnonym() && _activeChar.getParty() == null) ? 1 : 0);

            writeD(_activeChar.isInvisible() ? _activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask() : _activeChar.getAbnormalEffect());
            writeC(_activeChar.isInsideZone(ZoneIdType.WATER) ? 1 : _activeChar.isFlyingMounted() ? 2 : 0);

            writeD(_activeChar.getClanPrivileges().getBitmask());

            writeH(_activeChar.getRecomLeft()); // c2 recommendations remaining
            writeH(_activeChar.getRecomHave()); // c2 recommendations received
            writeD(_activeChar.getMountNpcId() > 0 ? _activeChar.getMountNpcId() + 1000000 : 0);
            writeH(_activeChar.getInventoryLimit());

            writeD(_activeChar.getClassId().getId());
            writeD(0x00); // special effects? circles around player...
            writeD(_activeChar.getMaxCp());
            writeD((int) _activeChar.getCurrentCp());
            writeC(_activeChar.isMounted() || (_airShipHelm != 0) ? 0 :
                    _activeChar.getQuickVarI("tryEnchant", 0) == 0 ?
                            Integer.parseInt(_activeChar.getVar("customEnchantColor", "0")) == 0 ?
                                    _activeChar.getEnchantEffect() : Integer.parseInt(_activeChar.getVar("customEnchantColor", "0"))
                            : _activeChar.getQuickVarI("tryEnchant", 0));

            writeC(_activeChar.getTeam().getId());

            writeD(_activeChar.getClanCrestLargeId());
            writeC(_activeChar.isNoble() ? 1 : 0); // 0x01: symbol on char menu ctrl+I
            writeC(_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) ? 1 : 0); // 0x01: Hero Aura

            writeC(_activeChar.getFishingEx().isFishing() ? 1 : 0); // Fishing Mode
            writeD(_activeChar.getFishingEx().getFishx()); // fishing x
            writeD(_activeChar.getFishingEx().getFishy()); // fishing y
            writeD(_activeChar.getFishingEx().getFishz()); // fishing z

            boolean isTvTvTvT = GabUtils.isInPvPInstance(_activeChar) && PvPZoneManager.isTvTvTvT();
            if (isTvTvTvT) {
                writeD(Anonymity.handleColorForTvTvTvT(_activeChar));
            } else {
                writeD(_activeChar.getQuickVar("tryColor", "").isEmpty() ?
                        _activeChar.getVar("cNameColor", "").isEmpty() ?
                                _activeChar.getAppearance().getNameColor() :
                                Integer.decode("0x" + _activeChar.getVar("cNameColor", "")) :
                        Integer.decode("0x"+_activeChar.getQuickVar("tryColor", "")));
            }

            // new c5
            writeC(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window

            writeD(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
            writeD(_activeChar.getPledgeType());

            if (isTvTvTvT) {
                writeD(Anonymity.handleColorForTvTvTvT(_activeChar));
            } else {
                writeD(_activeChar.getQuickVar("tryColor", "").isEmpty() ?
                        _activeChar.getVar("cTitleColor", "").isEmpty() ?
                                _activeChar.getAppearance().getTitleColor() :
                                Integer.decode("0x" + _activeChar.getVar("cTitleColor", "")) :
                        Integer.decode("0x"+_activeChar.getQuickVar("tryColor", "")));
            }

            writeD(_activeChar.isCursedWeaponEquipped() ? CursedWeaponsManager.getInstance().getLevel(_activeChar.getCursedWeaponEquippedId()) : 0);

            // T1 Starts
            writeD(_activeChar.getTransformationDisplayId());

            byte attackAttribute = _activeChar.getAttackElement();
            writeH(attackAttribute);
            writeH(_activeChar.getAttackElementValue(attackAttribute));
            writeH(_activeChar.getDefenseElementValue(Elementals.FIRE));
            writeH(_activeChar.getDefenseElementValue(Elementals.WATER));
            writeH(_activeChar.getDefenseElementValue(Elementals.WIND));
            writeH(_activeChar.getDefenseElementValue(Elementals.EARTH));
            writeH(_activeChar.getDefenseElementValue(Elementals.HOLY));
            writeH(_activeChar.getDefenseElementValue(Elementals.DARK));

            writeD(_activeChar.getQuickVarI("tryAgathion", 0) == 0 ?
                    _activeChar.getVar("agathionCustom", "").isEmpty() ? _activeChar.getAgathionId() : Integer.parseInt(_activeChar.getVar("agathionCustom", "")) :
                    _activeChar.getQuickVarI("tryAgathion", 0));

            // T2 Starts
            writeD(_activeChar.getFame()); // Fame
            writeD(_activeChar.isMinimapAllowed() ? 1 : 0); // Minimap on Hellbound
            writeD(_activeChar.getVitalityPoints()); // Vitality Points
            writeD(_activeChar.getSpecialEffect());
            // writeD(_territoryId); // CT2.3
            // writeD((_isDisguised ? 0x01: 0x00)); // CT2.3
            // writeD(_territoryId); // CT2.3
        } catch (Exception e) {
            //
        }
    }
}
