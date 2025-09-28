package gr.sr.interf.delegate;

import gr.sr.events.engine.base.Loc;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.delegate.ICharacterData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2DoorInstance;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

public class CharacterData
        extends ObjectData implements ICharacterData {
    protected L2Character _owner;

    public CharacterData(L2Character cha) {
        super((L2Object) cha);
        this._owner = cha;
    }

    public L2Character getOwner() {
        return this._owner;
    }

    public double getPlanDistanceSq(int targetX, int targetY) {
        return this._owner.getPlanDistanceSq(targetX, targetY);
    }

    public Loc getLoc() {
        return new Loc(this._owner.getX(), this._owner.getY(), this._owner.getZ(), this._owner.getHeading());
    }

    public int getObjectId() {
        return this._owner.getObjectId();
    }

    public boolean isDoor() {
        return this._owner instanceof L2DoorInstance;
    }

    public DoorData getDoorData() {
        return isDoor() ? new DoorData((L2DoorInstance) this._owner) : null;
    }

    public void startAbnormalEffect(int mask) {
        this._owner.startAbnormalEffect(mask);
    }

    public void stopAbnormalEffect(int mask) {
        this._owner.stopAbnormalEffect(mask);
    }

    public PlayerEventInfo getEventInfo() {
        if (this._owner instanceof L2Playable) {
            return ((L2Playable) this._owner).getActingPlayer().getEventInfo();
        }
        return null;
    }

    public String getName() {
        return this._owner.getName();
    }

    public void creatureSay(int channel, String charName, String text) {
        this._owner.broadcastPacket((L2GameServerPacket) new CreatureSay(this._owner.getObjectId(), channel, charName, text));
    }

    public void doDie(CharacterData killer) {
        this._owner.reduceCurrentHp(this._owner.getCurrentHp() * 2.0D, killer.getOwner(), null);
    }

    public boolean isDead() {
        return this._owner.isDead();
    }
}


