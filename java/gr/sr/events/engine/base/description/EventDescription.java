package gr.sr.events.engine.base.description;

import gr.sr.events.engine.base.ConfigModel;

import java.util.Map;

public abstract class EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        return "";
    }

    public String getDescription(Map<String, ConfigModel> configs, int roundsCount, int teamsCount, int teamSize, int rejoinDelay, int timeLimit) {
        return "";
    }

    public final String getString(Map<String, ConfigModel> configs, String propName) {
        if (configs.containsKey(propName)) {
            String value = ((ConfigModel) configs.get(propName)).getValue();
            return value;
        }
        return "";
    }

    public final int getInt(Map<String, ConfigModel> configs, String propName) {
        if (configs.containsKey(propName)) {
            int value = ((ConfigModel) configs.get(propName)).getValueInt();
            return value;
        }
        return 0;
    }

    public final boolean getBoolean(Map<String, ConfigModel> configs, String propName) {
        if (configs.containsKey(propName)) {
            return ((ConfigModel) configs.get(propName)).getValueBoolean();
        }
        return false;
    }
}


