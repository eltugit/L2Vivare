package gabriel.pvpInstanceZone;

import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PVPInstance.PVPInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PvPInstanceInit {
    protected static PvPInstanceInit instance;
    private static final Logger _log = LoggerFactory.getLogger(PvPInstanceInit.class);

    private PvPInstanceInit() {
        init();
    }

    private void init() {
        ConfigPvPInstance.getInstance();
        PVPInstance.main(null);
        _log.info("PVP Instance By Gabriel Has been Initialized!");
    }

    public static PvPInstanceInit getInstance() {
        if (instance == null)
            instance = new PvPInstanceInit();
        return instance;
    }
}
