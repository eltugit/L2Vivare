package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class RoundsFeature extends AbstractFeature
{
    private int roundsAmmount;
    
    public RoundsFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.addConfig("RoundsAmmount", "The ammount of rounds for matches started under this mode (overrides the value from general configs). The value must be > 0 otherwise this config will be ignored.", this.roundsAmmount = 1);
        if (parametersString == null) {
            parametersString = "1";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.roundsAmmount = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getRoundsAmmount() {
        return this.roundsAmmount;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Rounds;
    }
}
