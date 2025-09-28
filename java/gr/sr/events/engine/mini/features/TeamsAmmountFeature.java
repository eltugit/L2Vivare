package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class TeamsAmmountFeature extends AbstractFeature
{
    private int teamsAmmount;
    
    public TeamsAmmountFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.teamsAmmount = 2;
        this.addConfig("TeamsAmmount", "The ammount of teams fighting in matches started under this mode (overrides the value from general configs). The value must be > 1 otherwise this config will be ignored.", 1);
        if (parametersString == null) {
            parametersString = "2";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.teamsAmmount = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getTeamsAmmount() {
        return this.teamsAmmount;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.TeamsAmmount;
    }
}
