package gr.sr.l2j.delegate;

import gr.sr.events.engine.base.Loc;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.DoorData;

public interface ICharacterData {
    String getName();

    int getObjectId();

    Loc getLoc();

    double getPlanDistanceSq(int paramInt1, int paramInt2);

    boolean isDoor();

    DoorData getDoorData();

    void startAbnormalEffect(int paramInt);

    void stopAbnormalEffect(int paramInt);

    void creatureSay(int paramInt, String paramString1, String paramString2);

    void doDie(CharacterData paramCharacterData);

    boolean isDead();

    PlayerEventInfo getEventInfo();
}


