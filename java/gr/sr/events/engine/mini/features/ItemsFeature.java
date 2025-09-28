package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.ItemData;

import java.util.Arrays;


public class ItemsFeature extends AbstractFeature
{
    private boolean allowPotions;
    private boolean allowScrolls;
    private int[] disabledItems;
    
    public ItemsFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.allowPotions = true;
        this.allowScrolls = true;
        this.disabledItems = null;
        this.addConfig("AllowPotions", "Will the potions be enabled for this mode?", 1);
        this.addConfig("AllowScrolls", "Will the scrolls be enabled for this mode?", 1);
        this.addConfig("DisabledItems", "Specify here which items will be disabled (not usable/equipable) for this mode. Write their IDs and separate by SPACE. Eg. <font color=LEVEL>111 222 525</font>. Put <font color=LEVEL>0</font> to disable this config.", 2);
        if (parametersString == null || parametersString.split(",").length != 3) {
            parametersString = "true,false,0";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.allowPotions = Boolean.parseBoolean(params[0]);
            this.allowScrolls = Boolean.parseBoolean(params[1]);
            final String[] splitted = params[2].split(" ");
            this.disabledItems = new int[splitted.length];
            for (int i = 0; i < splitted.length; ++i) {
                this.disabledItems[i] = Integer.parseInt(splitted[i]);
            }
            Arrays.sort(this.disabledItems);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public boolean checkItem(final PlayerEventInfo player, final ItemData item) {
        return (this.allowPotions || !item.isPotion()) && (this.allowScrolls || !item.isScroll()) && Arrays.binarySearch(this.disabledItems, item.getItemId()) < 0;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        boolean canJoin = true;
        for (final ItemData item : player.getItems()) {
            if ((item.isWeapon() || item.isArmor()) && !this.checkItem(player, item)) {
                canJoin = false;
                player.sendMessage("(I)Please put item " + item.getItemName() + " to your warehouse before participating. It is not allowed for this event.");
            }
        }
        return canJoin;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Items;
    }
}
