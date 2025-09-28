package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class TimeLimitFeature extends AbstractFeature
{
    private int timeLimit;
    
    public TimeLimitFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.timeLimit = 600000;
        this.addConfig("TimeLimit", "Event's time limit, after which the event will be automatically ended (in ms).", 1);
        if (parametersString == null) {
            parametersString = "600000";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.timeLimit = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getTimeLimit() {
        return this.timeLimit;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.TimeLimit;
    }
}
