package gabriel;


import scripts.conquerablehalls.flagwar.BanditStronghold.BanditStronghold;
import scripts.conquerablehalls.flagwar.WildBeastReserve.WildBeastReserve;
import scripts.handlers.loader.GlobalLoader;

import java.util.logging.Logger;


public class GabrielScriptsLoader {
    protected static final Logger _log = Logger.getLogger(GabrielScriptsLoader.class.getName());

    GabrielScriptsLoader() {
        GlobalLoader.main(null);
        new BanditStronghold();
        WildBeastReserve.main(null);
        _log.info("Gabriel Loader loaded!");
    }

    protected static GabrielScriptsLoader instance;


    public static GabrielScriptsLoader getInstance() {
        if (instance == null)
            instance = new GabrielScriptsLoader();
        return instance;
    }
}
