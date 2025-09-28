package gr.sr.events.engine.base.description;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.EventType;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

public class EventDescriptionSystem {
    private final Map<EventType, EventDescription> _descriptions = new LinkedHashMap<>();

    public EventDescriptionSystem() {
        SunriseLoader.debug("Loaded editable Event Description system.", Level.INFO);
    }

    public void addDescription(EventType type, EventDescription description) {
        this._descriptions.put(type, description);
    }

    public EventDescription getDescription(EventType type) {
        if (this._descriptions.containsKey(type)) {
            return this._descriptions.get(type);
        }
        return null;
    }

    public static final EventDescriptionSystem getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final EventDescriptionSystem _instance = new EventDescriptionSystem();
    }
}


