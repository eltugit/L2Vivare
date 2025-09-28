package l2r.gameserver.model.zone.type;

import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.zone.L2ZoneType;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class L2PrivateStoreZone extends L2ZoneType {
    public L2PrivateStoreZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character) {
        character.setInsideZone(ZoneIdType.ZONE_PRIVATE_STORE, true);
    }

    @Override
    protected void onExit(L2Character character) {
        character.setInsideZone(ZoneIdType.ZONE_PRIVATE_STORE, false);
    }

    @Override
    public void onDieInside(L2Character character) {
    }

    @Override
    public void onReviveInside(L2Character character) {
    }

}
