package gr.sr.l2j;

import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.html.EventHtmlManager;

public class CallBack {
    private IEventOut _out = null;
    private IPlayerBase _playerBase = null;
    private IValues _values = null;

    public IEventOut getOut() {
        return this._out;
    }

    public IPlayerBase getPlayerBase() {
        return this._playerBase;
    }

    public IValues getValues() {
        return this._values;
    }

    public void setHtmlManager(EventHtmlManager manager) {
        EventManager.getInstance().setHtmlManager(manager);
    }

    public void setSunriseOut(IEventOut out) {
        this._out = out;
    }

    public void setPlayerBase(IPlayerBase base) {
        this._playerBase = base;
    }

    public void setValues(IValues values) {
        this._values = values;
    }

    public static final CallBack getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final CallBack _instance = new CallBack();
    }
}


