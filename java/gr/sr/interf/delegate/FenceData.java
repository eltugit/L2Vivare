package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.IFenceData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.L2WorldRegion;
import l2r.gameserver.model.actor.instance.L2FenceInstance;

public class FenceData
        extends ObjectData
        implements IFenceData {
    private final L2FenceInstance _owner;

    public FenceData(L2FenceInstance cha) {
        super((L2Object) cha);
        this._owner = cha;
    }

    public L2FenceInstance getOwner() {
        return this._owner;
    }

    public void deleteMe() {
        L2WorldRegion region = this._owner.getWorldRegion();
        this._owner.decayMe();
        if (region != null) {
            region.removeVisibleObject((L2Object) this._owner);
        }
        this._owner.getKnownList().removeAllKnownObjects();
        L2World.getInstance().removeObject((L2Object) this._owner);
    }
}


