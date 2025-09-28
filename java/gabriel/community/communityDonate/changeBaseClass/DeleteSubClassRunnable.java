package gabriel.community.communityDonate.changeBaseClass;

import l2r.L2DatabaseFactory;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.SubClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class DeleteSubClassRunnable implements Runnable {
    L2PcInstance p;
    List<Integer> toDeleteIndex = new ArrayList<>();

    public DeleteSubClassRunnable(L2PcInstance player) {
        p = player;
    }

    @Override
    public void run() {
        for (Map.Entry<Integer, SubClass> subs : p.getSubClasses().entrySet()) {
            int index = subs.getKey();
            toDeleteIndex.add(index);
        }
        deleteCerti(p.getObjectId());
        for (Integer deleteIndex : toDeleteIndex) {
            p.deleteSubClass(deleteIndex);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static final String DELETE_CERTI = "DELETE FROM `character_certification` WHERE `object_id`=?";

    public void deleteCerti(int objID) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(DELETE_CERTI)) {
                ps.setInt(1, objID);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
