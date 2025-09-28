package gabriel.others;


import gabriel.config.GabConfig;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.instancemanager.TerritoryWarManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import java.util.List;


public class WardLimiter {


    public static boolean canSummonWard(L2PcInstance player) {
        Castle castle = CastleManager.getInstance().getCastleByOwner(player.getClan());
        if (castle == null) return false;
        return TerritoryWarManager.getInstance().getTerritory(castle.getResidenceId()).getOwnedWardIds().size() < GabConfig.MAX_WARD_CLAN;
    }


    public static boolean clanHasWardAlready(L2PcInstance player) {
        if (TerritoryWarManager.getInstance().isTWInProgress()) {
            List<L2PcInstance> players = player.getClan().getOnlineMembers(1);
            if (players.isEmpty()) return false;
            for (L2PcInstance onlineMember : players) {
                L2ItemInstance item = onlineMember.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
                if (onlineMember.isCombatFlagEquipped() && ((item.getId() >= 13560) && (item.getId() <= 13568))) {
                    return true;
                }
            }
        }
        return false;
    }

}
