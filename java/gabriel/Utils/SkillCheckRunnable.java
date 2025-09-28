package gabriel.Utils;

import l2r.features.museum.MuseumManager;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkillCheckRunnable implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(SkillCheckRunnable.class);

    @Override
    public void run() {
        for (L2PcInstance player : L2World.getInstance().getPlayers()) {
            if (player != null && !player.isInsideZone(ZoneIdType.TOWN) && player.getClient() != null && !player.getClient().isDetached()) {
                player.checkAllowedSkillsRunning();
                player.clearDuppedBuffs();
            }
            MuseumManager.getInstance().updateDataForChar(player);
        }
        MuseumManager.getInstance().reload();

        ThreadPoolManager.getInstance().scheduleGeneral(this, 60000);
    }


    protected static SkillCheckRunnable instance;

    private SkillCheckRunnable() {
        ThreadPoolManager.getInstance().scheduleGeneral(this, 5000);
        _log.info("SkillCheck Runnable started! Run every 60 seconds!");
    }

    public static SkillCheckRunnable getInstance() {
        if (instance == null)
            instance = new SkillCheckRunnable();
        return instance;
    }
}
