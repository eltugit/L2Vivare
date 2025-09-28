package gr.sr.events.engine.base;

import gr.sr.events.SunriseLoader;
import gr.sr.l2j.CallBack;

public abstract class Event {
    protected EventType _type;

    public Event(EventType type) {
        this._type = type;
    }

    public final EventType getEventType() {
        return this._type;
    }

    public String getEventName() {
        return this._type.getAltTitle();
    }

    public void announce(String text) {
        CallBack.getInstance().getOut().announceToAllScreenMessage(text, getEventType().getAltTitle());
    }

    public void debug(String text) {
        SunriseLoader.debug(text);
    }

    public void print(String msg) {
        SunriseLoader.detailedDebug(msg);
    }
}


