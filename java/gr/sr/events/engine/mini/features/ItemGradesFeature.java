package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.ItemData;
import gr.sr.l2j.CallBack;


public class ItemGradesFeature extends AbstractFeature
{
    private int[] allowedGrades;
    
    public ItemGradesFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.addConfig("GradesAvailable", "Write the letters of all allowed item grades here. Separate by SPACE. Eg. <font color=LEVEL>a s s80</font>.", 1);
        if (parametersString == null) {
            parametersString = "no d c b a s s80 s84";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            final String[] splitted = params[0].split(" ");
            this.allowedGrades = new int[splitted.length];
            for (int i = 0; i < splitted.length; ++i) {
                this.allowedGrades[i] = CallBack.getInstance().getOut().getGradeFromFirstLetter(splitted[i]);
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public int[] getGrades() {
        return this.allowedGrades;
    }
    
    public boolean checkItem(final PlayerEventInfo player, final ItemData item) {
        final int type = item.getCrystalType();
        boolean allowed = false;
        if (item.isArmor() || item.isWeapon()) {
            for (final int grade : this.allowedGrades) {
                if (type == grade) {
                    allowed = true;
                }
            }
        }
        else {
            allowed = true;
        }
        return allowed;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        boolean canJoin = true;
        for (final ItemData item : player.getItems()) {
            if (!this.checkItem(player, item)) {
                canJoin = false;
                player.sendMessage("(G)Please put item " + item.getItemName() + " to your warehouse before participating. It is not allowed for this event.");
            }
        }
        return canJoin;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.ItemGrades;
    }
}
