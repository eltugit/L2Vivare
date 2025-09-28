package gr.sr.events.engine.configtemplate;

import gr.sr.events.Configurable;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.EventType;
import gr.sr.interf.PlayerEventInfo;

public abstract class ConfigTemplate {
    public abstract String getName();

    public abstract EventType getEventType();

    public abstract String getDescription();

    public abstract SetConfig[] getConfigs();

    public void applyTemplate(PlayerEventInfo gm, EventType type, Configurable event) {
        int changed = 0;
        for (SetConfig sc : getConfigs()) {
            if (event.getConfigs().containsKey(sc._key)) {
                if (!((ConfigModel) event.getConfigs().get(sc._key)).getValue().equals(sc._value)) {
                    ((ConfigModel) event.getConfigs().get(sc._key)).setValue(sc._value);
                    changed++;
                }
            }
        }
        int total = event.getConfigs().size();
        EventConfig.getInstance().updateInDb(type);
        gm.sendMessage("Applied template " + getName() + " to event " + type.getAltTitle() + ". " + changed + "/" + total + " configs have been changed.");
    }

    public class SetConfig {
        String _key;
        String _value;

        public SetConfig(String key, String value) {
            this._key = key;
            this._value = value;
        }
    }
}


