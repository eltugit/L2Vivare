package gabriel.restrictions;


import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class BasicGabsRestrictions {

    /**
     * @param activeChar
     * @param target
     * @return return true if active Char is in the same clan or ally
     */
    public static boolean checkSameClan(L2Character activeChar, L2Character target) {
        if (activeChar == null || target == null)
            return false;

        int activeCharClan = activeChar.getActingPlayer().getClanId();
        int targetCharClan = target.getActingPlayer().getClanId();
        int activeCharAlly = 0;
        int targetCharAlly = 0;

        if (activeCharClan != 0 && targetCharClan != 0) {
            activeCharAlly = activeChar.getActingPlayer().getClan().getAllyId();
            targetCharAlly = target.getActingPlayer().getClan().getAllyId();

            if (activeCharClan == targetCharClan) {
                return true;
            }

            if (activeCharAlly != 0 && targetCharAlly != 0) {
                return activeCharAlly == targetCharAlly;
            }
        }
        return false;
    }

    public static boolean checkSameTeam(L2Character activeChar, L2Character target) {
        if (target instanceof L2Summon) {
            if ((activeChar instanceof L2Summon)) {
                if ((((L2Summon) activeChar).getOwner().getTeam() == ((L2Summon) target).getOwner().getTeam())) {
                    return true;
                }
            }
            if ((activeChar instanceof L2PcInstance)) {
                if ((activeChar.getActingPlayer().getTeam() == ((L2Summon) target).getOwner().getTeam())) {
                    return true;
                }
            }
        }
        if (target instanceof L2PcInstance) {
            if ((activeChar instanceof L2Summon)) {
                if ((((L2Summon) activeChar).getOwner().getTeam() == (target.getActingPlayer().getTeam()))) {
                    return true;
                }
            }
            if ((activeChar instanceof L2PcInstance)) {
                if ((activeChar.getActingPlayer().getTeam() == (target.getActingPlayer().getTeam()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkSame4Team(L2Character activeChar, L2Character target) {
        if (target instanceof L2Summon) {
            if ((activeChar instanceof L2Summon)) {
                if ((((L2Summon) activeChar).getOwner().getTeam4t() == ((L2Summon) target).getOwner().getTeam4t())) {
                    return true;
                }
            }
            if ((activeChar instanceof L2PcInstance)) {
                if ((activeChar.getActingPlayer().getTeam4t() == ((L2Summon) target).getOwner().getTeam4t())) {
                    return true;
                }
            }
        }
        if (target instanceof L2PcInstance) {
            if ((activeChar instanceof L2Summon)) {
                if ((((L2Summon) activeChar).getOwner().getTeam4t() == (target.getActingPlayer().getTeam4t()))) {
                    return true;
                }
            }
            if ((activeChar instanceof L2PcInstance)) {
                if ((activeChar.getActingPlayer().getTeam4t() == (target.getActingPlayer().getTeam4t()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkSameParty(L2Character player, L2Character target) {
        L2Party party = player.getParty();
        if (party != null) {
            for (L2PcInstance partyMember : party.getMembers()) {
                if (partyMember == target)
                    return true;
            }
        }
        return false;
    }

    public static boolean checkInTWAttack(L2PcInstance player, L2PcInstance targetPlayer) {
        if (player != null && targetPlayer != null && player.getSiegeState() > 0 && player.isInsideZone(ZoneIdType.SIEGE)
                && player.getSiegeState() == targetPlayer.getSiegeState()
                && player.getSiegeSide() == targetPlayer.getSiegeSide()
                && player != targetPlayer) {
            //return true if in same clan or ally
            return checkSameClan(player, targetPlayer);
        }
        return false;
    }
}
