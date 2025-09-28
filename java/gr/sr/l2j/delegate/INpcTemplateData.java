package gr.sr.l2j.delegate;

import gr.sr.interf.delegate.NpcData;

public interface INpcTemplateData {
    void setSpawnName(String paramString);

    void setSpawnTitle(String paramString);

    boolean exists();

    NpcData doSpawn(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

    NpcData doSpawn(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);

    NpcData doSpawn(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7);
}


