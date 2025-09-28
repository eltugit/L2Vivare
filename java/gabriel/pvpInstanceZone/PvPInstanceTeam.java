package gabriel.pvpInstanceZone;


import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PvPInstanceTeam {
    public static final Logger log = Logger.getLogger(PvPInstanceTeam.class.getName());

    private ArrayList<L2PcInstance> listPlayers = new ArrayList<L2PcInstance>();

    public ArrayList<L2PcInstance> getListPlayers() {
        return listPlayers;
    }
}
