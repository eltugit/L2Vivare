package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class LevelFeature extends AbstractFeature
{
    private int minLevel;
    private int maxLevel;
    
    public LevelFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.minLevel = 1;
        this.maxLevel = 85;
        this.addConfig("MinLevel", "The min level required to participate this event mode.", 1);
        this.addConfig("MaxLevel", "The max level to participate this event mode.", 1);
        if (parametersString == null) {
            parametersString = "1,85";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.minLevel = Integer.parseInt(params[0]);
            this.maxLevel = Integer.parseInt(params[1]);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int getMinLevel() {
        return this.minLevel;
    }
    
    public int getMaxLevel() {
        return this.maxLevel;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return player.getLevel() >= this.minLevel && player.getLevel() <= this.maxLevel;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Level;
    }
}
