package gabriel.balancer;

import gabriel.balancer.VCCommands.BalancerVCCmd;
import l2r.gameserver.handler.AdminCommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class GabrielBalancerLoader {
    private static final Logger _log = LoggerFactory.getLogger(GabrielBalancerLoader.class);

    private GabrielBalancerLoader() {
        ClassBalance.getInstance();
        ClassBalanceOly.getInstance();
        AdminCommandHandler.getInstance().registerHandler(new BalancerVCCmd());
        _log.info("Gabriel Balancer loaded with success!");
    }

    protected static GabrielBalancerLoader instance;

    public static GabrielBalancerLoader getInstance() {
        if (instance == null)
            instance = new GabrielBalancerLoader();
        return instance;
    }

    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
