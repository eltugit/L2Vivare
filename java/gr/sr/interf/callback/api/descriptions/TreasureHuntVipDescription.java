package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class TreasureHuntVipDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = "No information about this event yet.";
        return text;
    }
}


