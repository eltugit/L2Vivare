package gr.sr.voteEngine.old.runnable;

import gr.sr.configsEngine.configs.impl.IndividualVoteSystemConfigs;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;

public class TriesResetTask {
    protected static final Logger _log = LoggerFactory.getLogger(TriesResetTask.class);

    public TriesResetTask() {
    }

    public static void getInstance() {
        ThreadPoolManager treadPoolManager = ThreadPoolManager.getInstance();
        Runnable runnable = () -> {
            try (Connection connection = L2DatabaseFactory.getInstance().getConnection();
                 PreparedStatement ps = connection.prepareStatement("UPDATE character_variables SET val=? WHERE var='vote_tries'")){
                {
                    ps.setInt(1, IndividualVoteSystemConfigs.TRIES_AMOUNT);
                    ps.execute();
                }
            } catch (Exception e) {
                _log.error(TriesResetTask.class.getSimpleName() + ": Could not vote tries: " + e);
            }

            for (L2PcInstance l2PcInstance : L2World.getInstance().getPlayers()) {
                L2PcInstance player;
                if ((player = l2PcInstance) != null && player.isOnline() && !player.isInOfflineMode()) {
                    player.setVar("vote_tries", String.valueOf(IndividualVoteSystemConfigs.TRIES_AMOUNT));
                }
            }

        };
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 1);

        long timeInMillis = cal.getTimeInMillis();
        treadPoolManager.scheduleGeneral(runnable, System.currentTimeMillis() - timeInMillis <= 0L ? cal.getTimeInMillis() - System.currentTimeMillis() : 0L);
    }
}
