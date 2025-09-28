package gr.sr.l2j.delegate;

import gr.sr.interf.delegate.FenceData;
import gr.sr.interf.delegate.NpcData;

public interface IObjectData {
    int getObjectId();

    boolean isPlayer();

    boolean isSummon();

    boolean isFence();

    FenceData getFence();

    boolean isNpc();

    NpcData getNpc();
}


