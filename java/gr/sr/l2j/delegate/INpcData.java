package gr.sr.l2j.delegate;

import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.ObjectData;

public interface INpcData {
    ObjectData getObjectData();

    void setName(String paramString);

    void setTitle(String paramString);

    int getNpcId();

    void setEventTeam(int paramInt);

    int getEventTeam();

    void broadcastNpcInfo();

    void broadcastSkillUse(CharacterData paramCharacterData1, CharacterData paramCharacterData2, int paramInt1, int paramInt2);

    void deleteMe();
}


