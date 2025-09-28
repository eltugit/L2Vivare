package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class DelaysFeature extends AbstractFeature
{
    private int rejoinDelay;
    
    public DelaysFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.rejoinDelay = 600000;
        this.addConfig("RejoinDelay", "The delay player has to wait to rejoin this mode again (in ms). This delay is divided by 2 if the player has lost his last match.", 1);
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
            this.rejoinDelay = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getRejoinDealy() {
        return this.rejoinDelay;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Delays;
    }
}
