package gabriel.pvpInstanceZone.utils;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class Anonymity {

    public static int handleColorForTvTvTvT(L2PcInstance _activeChar) {
        if (_activeChar.getTeam4t() == 1) {
            return 0xFF0000;
        } else if (_activeChar.getTeam4t() == 2) {
            return 0x0000FF;
        } else if (_activeChar.getTeam4t() == 3) {
            return 0x00FF00;
        } else if (_activeChar.getTeam4t() == 4) {
            return 0x00FFFF;
        } else {
            return -1;
        }
    }


    public static int handleAnonymityCharInfo(int slot, L2PcInstance _activeChar) {
        switch (slot) {
            case Inventory.PAPERDOLL_RHAND:
                return getRHand(_activeChar);
            case Inventory.PAPERDOLL_LHAND:
                return getLHand(_activeChar);
            case Inventory.PAPERDOLL_CHEST:
                return 6408;
            case Inventory.PAPERDOLL_GLOVES:
            case Inventory.PAPERDOLL_LEGS:
            case Inventory.PAPERDOLL_FEET:
            case Inventory.PAPERDOLL_CLOAK:
            case Inventory.PAPERDOLL_HAIR:
            case Inventory.PAPERDOLL_HAIR2:
                return 0;
            default:
                return -1;
        }
    }

    private static int getRHand(L2PcInstance _activeChar) {
        //Duelist
        if (_activeChar.getClassId().getId() == 88) {
            return 2582;
        }
        //Deadnought
        else if (_activeChar.getClassId().getId() == 89) {
            return 14133;
        }
        //Phoenix Knight
        else if (_activeChar.getClassId().getId() == 90) {
            return 14118;
        }
        //Hell Knight
        else if (_activeChar.getClassId().getId() == 91) {
            return 14118;
        }
        //Adventurer
        else if (_activeChar.getClassId().getId() == 93) {
            return 14127;
        }
        //Sagitarius
        else if (_activeChar.getClassId().getId() == 92) {
            return 14148;
        }
        //ArchMage
        else if (_activeChar.getClassId().getId() == 94) {
            return 14142;
        }
        //Soultaker
        else if (_activeChar.getClassId().getId() == 95) {
            return 14142;
        }
        //Arcana Lord
        else if (_activeChar.getClassId().getId() == 96) {
            return 14142;
        }
        //Cardinal
        else if (_activeChar.getClassId().getId() == 97) {
            return 14142;
        }
        //Hierophant
        else if (_activeChar.getClassId().getId() == 98) {
            return 14142;
        }
        //Eva's Templar
        else if (_activeChar.getClassId().getId() == 99) {
            return 14118;
        }
        //Sword Muse
        else if (_activeChar.getClassId().getId() == 100) {
            return 2582;
        }
        //Wind Rider
        else if (_activeChar.getClassId().getId() == 101) {
            return 14127;
        }
        //Moonlight Sentinel
        else if (_activeChar.getClassId().getId() == 102) {
            return 14148;
        }
        //Mystic Muse
        else if (_activeChar.getClassId().getId() == 103) {
            return 14142;
        }
        //Elemental Master
        else if (_activeChar.getClassId().getId() == 104) {
            return 14142;
        }
        //Eva's Saint
        else if (_activeChar.getClassId().getId() == 105) {
            return 14142;
        }
        //Shillen Templar
        else if (_activeChar.getClassId().getId() == 106) {
            return 14118;
        }
        //Spectral Dancer
        else if (_activeChar.getClassId().getId() == 107) {
            return 2582;
        }
        //Ghost Hunter
        else if (_activeChar.getClassId().getId() == 108) {
            return 14127;
        }
        //Ghost Sentinel
        else if (_activeChar.getClassId().getId() == 109) {
            return 14148;
        }
        //Storm Screamer
        else if (_activeChar.getClassId().getId() == 110) {
            return 14142;
        }
        //Spectral Master
        else if (_activeChar.getClassId().getId() == 111) {
            return 14142;
        }
        //Shillen Saint
        else if (_activeChar.getClassId().getId() == 112) {
            return 14142;
        }
        //Titan
        else if (_activeChar.getClassId().getId() == 113) {
            return 14121;
        }
        //Grand Khavatary
        else if (_activeChar.getClassId().getId() == 114) {
            return 14130;
        }
        //DoomCryer
        else if (_activeChar.getClassId().getId() == 116) {
            return 14142;
        }
        //Dominator
        else if (_activeChar.getClassId().getId() == 115) {
            return 14142;
        }
        //Maestro
        else if (_activeChar.getClassId().getId() == 118) {
            return 14136;
        }
        //Fortune Seeker
        else if (_activeChar.getClassId().getId() == 117) {
            return 14136;
        }
        //Doombringer
        else if (_activeChar.getClassId().getId() == 131) {
            return 14157;
        }
        //Male Soul Hound
        else if (_activeChar.getClassId().getId() == 132) {
            return 14151;
        }
        //Female Soul Hound
        else if (_activeChar.getClassId().getId() == 133) {
            return 14151;
        }
        //Trickster
        else if (_activeChar.getClassId().getId() == 134) {
            return 14154;
        }
        //judicator
        else if (_activeChar.getClassId().getId() == 136) {
            return 14151;
        }
        return -1;
    }

    private static int getLHand(L2PcInstance _activeChar) {
        if (_activeChar.getClassId().getId() == 88) {
            return 0;
        }
        //Deadnought
        else if (_activeChar.getClassId().getId() == 89) {
            return 0;
        }
        //Phoenix Knight
        else if (_activeChar.getClassId().getId() == 90) {
            return 641;
        }
        //Hell Knight
        else if (_activeChar.getClassId().getId() == 91) {
            return 641;
        }
        //Adventurer
        else if (_activeChar.getClassId().getId() == 93) {
            return 0;
        }
        //Sagitarius
        else if (_activeChar.getClassId().getId() == 92) {
            return 0;
        }
        //ArchMage
        else if (_activeChar.getClassId().getId() == 94) {
            return 641;
        }
        //Soultaker
        else if (_activeChar.getClassId().getId() == 95) {
            return 641;
        }
        //Arcana Lord
        else if (_activeChar.getClassId().getId() == 96) {
            return 641;
        }
        //Cardinal
        else if (_activeChar.getClassId().getId() == 97) {
            return 641;
        }
        //Hierophant
        else if (_activeChar.getClassId().getId() == 98) {
            return 641;
        }
        //Eva's Templar
        else if (_activeChar.getClassId().getId() == 99) {
            return 641;
        }
        //Sword Muse
        else if (_activeChar.getClassId().getId() == 100) {
            return 641;
        }
        //Wind Rider
        else if (_activeChar.getClassId().getId() == 101) {
            return 0;
        }
        //Moonlight Sentinel
        else if (_activeChar.getClassId().getId() == 102) {
            return 0;
        }
        //Mystic Muse
        else if (_activeChar.getClassId().getId() == 103) {
            return 641;
        }
        //Elemental Master
        else if (_activeChar.getClassId().getId() == 104) {
            return 641;
        }
        //Eva's Saint
        else if (_activeChar.getClassId().getId() == 105) {
            return 641;
        }
        //Shillen Templar
        else if (_activeChar.getClassId().getId() == 106) {
            return 641;
        }
        //Spectral Dancer
        else if (_activeChar.getClassId().getId() == 107) {
            return 0;
        }
        //Ghost Hunter
        else if (_activeChar.getClassId().getId() == 108) {
            return 0;
        }
        //Ghost Sentinel
        else if (_activeChar.getClassId().getId() == 109) {
            return 0;
        }
        //Storm Screamer
        else if (_activeChar.getClassId().getId() == 110) {
            return 641;
        }
        //Spectral Master
        else if (_activeChar.getClassId().getId() == 111) {
            return 641;
        }
        //Shillen Saint
        else if (_activeChar.getClassId().getId() == 112) {
            return 641;
        }
        //Titan
        else if (_activeChar.getClassId().getId() == 113) {
            return 0;
        }
        //Grand Khavatary
        else if (_activeChar.getClassId().getId() == 114) {
            return 0;
        }
        //DoomCryer
        else if (_activeChar.getClassId().getId() == 116) {
            return 641;
        }
        //Dominator
        else if (_activeChar.getClassId().getId() == 115) {
            return 641;
        }
        //Maestro
        else if (_activeChar.getClassId().getId() == 118) {
            return 641;
        }
        //Fortune Seeker
        else if (_activeChar.getClassId().getId() == 117) {
            return 0;
        }
        //Doombringer
        else if (_activeChar.getClassId().getId() == 131) {
            return 0;
        }
        //Male Soul Hound
        else if (_activeChar.getClassId().getId() == 132) {
            return 0;
        }
        //Female Soul Hound
        else if (_activeChar.getClassId().getId() == 133) {
            return 0;
        }
        //Trickster
        else if (_activeChar.getClassId().getId() == 134) {
            return 0;
        }
        //judicator
        else if (_activeChar.getClassId().getId() == 136) {
            return 0;
        }
        return -1;
    }
}
