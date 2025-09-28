package gr.sr.premiumEngine.runnable;

import gr.sr.premiumEngine.PremiumHandler;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Iterator;

public class PremiumChecker implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(PremiumChecker.class);

    public PremiumChecker() {
    }

    public void run() {
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        Iterator iterator = L2World.getInstance().getPlayers().iterator();

        while(iterator.hasNext()) {
            L2PcInstance player;
            if ((player = (L2PcInstance)iterator.next()).isOnline() && player.getClient() != null && !player.getClient().isDetached()) {
                PremiumHandler.restorePremServiceData(player, player.getAccountName());
            }
        }

        long loaded = Calendar.getInstance().getTimeInMillis();
        log.info(PremiumChecker.class.getSimpleName() + ": Services reloaded in " + (loaded - timeInMillis) + " ms.");
    }
}
