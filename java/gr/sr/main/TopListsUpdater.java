package gr.sr.main;

import gr.sr.utils.Tools;
import l2r.gameserver.communitybbs.SunriseBoards.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public class TopListsUpdater implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TopListsUpdater.class);

    public TopListsUpdater() {
    }

    public void run() {
        long start = Calendar.getInstance().getTimeInMillis();
        TopListsLoader.getInstance().loadPvp();
        TopListsLoader.getInstance().loadPk();
        TopListsLoader.getInstance().loadTopCurrency();
        TopListsLoader.getInstance().loadClan();
        TopListsLoader.getInstance().loadOnlineTime();
        TopPvpPlayers.getInstance().load();
        TopPkPlayers.getInstance().load();
        TopClan.getInstance().load();
        TopOnlinePlayers.getInstance().load();
        HeroeList.getInstance().load();
        CastleStatus.getInstance().load();
        GrandBossList.getInstance().load();
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        log.info(TopListsUpdater.class.getSimpleName() + ": Data reloaded in " + (timeInMillis - start) + " ms.");
        long timeMlsNow;
        String timeStringNow = Tools.convertMinuteToString(timeMlsNow = System.currentTimeMillis());
        TopListsLoader.getInstance().setLastUpdate(timeStringNow);
        TopListsLoader.getInstance().setLastUpdateInMs(timeMlsNow);
    }
}
