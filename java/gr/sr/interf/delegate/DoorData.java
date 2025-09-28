package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.IDoorData;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.instance.L2DoorInstance;

public class DoorData
        extends CharacterData implements IDoorData {
    protected L2DoorInstance _owner;

    public DoorData(L2DoorInstance d) {
        super((L2Character) d);
        this._owner = d;
    }

    public L2DoorInstance getOwner() {
        return this._owner;
    }

    public int getDoorId() {
        return this._owner.getId();
    }

    public boolean isOpened() {
        return this._owner.getOpen();
    }

    public void openMe() {
        this._owner.openMe();
    }

    public void closeMe() {
        this._owner.closeMe();
    }
}


