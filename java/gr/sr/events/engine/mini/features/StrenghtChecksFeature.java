package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.interf.PlayerEventInfo;


public class StrenghtChecksFeature extends AbstractFeature
{
    private int maxLevelDiff;
    
    public StrenghtChecksFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.maxLevelDiff = 5;
        this.addConfig("MaxLevelDiff", "The maximal acceptable level difference between players to start the match. For parties/teams, it calculates the average level of all players inside it. Put '0' to disable this config.", 1);
        if (parametersString == null) {
            parametersString = "5";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.maxLevelDiff = Integer.parseInt(params[0]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getMaxLevelDiff() {
        return this.maxLevelDiff;
    }
    
    public boolean canFight(final RegistrationData player, final RegistrationData opponent) {
        return Math.abs(player.getAverageLevel() - opponent.getAverageLevel()) <= this.maxLevelDiff;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.StrenghtChecks;
    }
}
