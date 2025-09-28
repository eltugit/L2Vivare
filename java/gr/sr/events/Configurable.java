package gr.sr.events;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.RewardPosition;
import gr.sr.events.engine.base.SpawnType;

import java.util.List;
import java.util.Map;

public interface Configurable {
    void loadConfigs();

    void clearConfigs();

    List<String> getCategories();

    Map<String, ConfigModel> getConfigs();

    Map<String, ConfigModel> getMapConfigs();

    RewardPosition[] getRewardTypes();

    Map<SpawnType, String> getAvailableSpawnTypes();

    void setConfig(String paramString1, String paramString2, boolean paramBoolean);

    String getDescriptionForReward(RewardPosition paramRewardPosition);

    int getTeamsCount();

    boolean canRun(EventMap paramEventMap);

    String getMissingSpawns(EventMap paramEventMap);
}


