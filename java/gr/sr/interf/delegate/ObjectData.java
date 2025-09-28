package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.IObjectData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2FenceInstance;

public class ObjectData
        implements IObjectData {
    protected L2Object _owner;

    public ObjectData(L2Object cha) {
        this._owner = cha;
    }

    public L2Object getOwner() {
        return this._owner;
    }

    public int getObjectId() {
        return this._owner.getObjectId();
    }

    public boolean isPlayer() {
        return this._owner instanceof l2r.gameserver.model.actor.instance.L2PcInstance;
    }

    public boolean isSummon() {
        return this._owner instanceof l2r.gameserver.model.actor.L2Summon;
    }

    public boolean isFence() {
        return this._owner instanceof L2FenceInstance;
    }

    public FenceData getFence() {
        if (!isFence()) {
            return null;
        }
        return new FenceData((L2FenceInstance) this._owner);
    }

    public NpcData getNpc() {
        return new NpcData((L2Npc) this._owner);
    }

    public boolean isNpc() {
        return this._owner instanceof L2Npc;
    }
}


