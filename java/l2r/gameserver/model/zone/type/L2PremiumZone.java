package l2r.gameserver.model.zone.type;

import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.zone.L2ZoneType;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * PremiumZone é a zona. n lembro se é l2premium ou PremiumZone creio q PremiumZone.
 */
public class L2PremiumZone extends L2ZoneType {
    public L2PremiumZone(int id) {
        super(id);
    }

    private final int GIRAN_X = 83425; // editar
    private final int GIRAN_Y = 148585; // editar
    private final int GIRAN_Z = -3406; // editar

    @Override
    protected void onEnter(L2Character character) {
        if(character.isPlayer()){
            L2PcInstance pl = (L2PcInstance) character;
            if(!pl.isPremium()){
                pl.teleToLocation(GIRAN_X,GIRAN_Y,GIRAN_Z);
            }
        }

        character.setInsideZone(ZoneIdType.PREMIUM_ZONE, true);
    }

    @Override
    protected void onExit(L2Character character) {
        character.setInsideZone(ZoneIdType.PREMIUM_ZONE, false);
    }

    @Override
    public void onDieInside(L2Character character) {
    }

    @Override
    public void onReviveInside(L2Character character) {
    }

}
