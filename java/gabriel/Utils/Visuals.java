package gabriel.Utils;


import l2r.gameserver.enums.PrivateStoreType;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.CharInfo;
import l2r.gameserver.network.serverpackets.DeleteObject;


public class Visuals {

    public static void refreshAppStatus(L2PcInstance player) {
        for (L2PcInstance plr : player.getKnownList().getKnownPlayers().values()) {
            if (plr == null) {
                continue;
            }
            plr.broadcastUserInfo();
            player.sendPacket(new CharInfo(plr));
            player.broadcastUserInfo();
        }
//        player.broadcastUserInfo();
//        player.decayMe();
//        player.spawnMe();
    }


    public static void refreshStoreStatus(L2PcInstance player, boolean delete) {
        for (L2PcInstance plr : player.getKnownList().getKnownPlayers().values()) {
            if (plr == null || plr.getPrivateStoreType() == PrivateStoreType.NONE) {
                continue;
            }
            if (delete) {
                player.sendPacket(new DeleteObject(plr));
            } else {
                plr.broadcastUserInfo();
                player.sendPacket(new CharInfo(plr));
                player.broadcastUserInfo();
                plr.broadcastUserInfo();
            }
        }
        player.broadcastUserInfo();
//        player.decayMe();
//        player.spawnMe();
    }


}
