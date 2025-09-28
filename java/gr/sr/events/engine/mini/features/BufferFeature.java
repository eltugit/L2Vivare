package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;


public class BufferFeature extends AbstractFeature
{
    private boolean autoApplySchemeBuffs;
    private boolean spawnNpcBuffer;
    private int customNpcBuffer;
    private int[] autoBuffsFighterIds;
    private int[] autoBuffsFighterLevels;
    private int[] autoBuffsMageIds;
    private int[] autoBuffsMageLevels;
    
    public BufferFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.autoApplySchemeBuffs = true;
        this.spawnNpcBuffer = false;
        this.customNpcBuffer = 0;
        this.autoBuffsFighterIds = null;
        this.autoBuffsFighterLevels = null;
        this.autoBuffsMageIds = null;
        this.autoBuffsMageLevels = null;
        this.addConfig("ApplyEventBuffs", "If 'true', all players will be rebuffed on start of event/round by their specified scheme. Doesn't work if the auto scheme buffer is disabled from Events.xml.", 1);
        this.addConfig("SpawnNpcBuffer", "If 'true', then the event will spawn NPC Buffer to each spawn of type Buffer at start of the event/round and the Buffer disappears at the end of wait time.", 1);
        this.addConfig("CustomBufferId", "You can specify the ID of buffer (or another NPC which will be available near players during the wait-time) for this mode. Put '0' to disable.", 1);
        this.addConfig("AutoBuffIdsFighter", "Fighter classes will be buffed with those buffs at start of event/round. Format as 'BUFF_ID-Level'. Separate IDs by SPACE, Eg. <font color=LEVEL>312-1 256-3</font>. Put <font color=LEVEL>0-0</font> to disable this config.", 2);
        this.addConfig("AutoBuffIdsMage", "Mage classes will be buffed with those buffs at start of event/round.  Format as 'BUFF_ID-Level'. Separate IDs by SPACE, Eg. <font color=LEVEL>312-1 256-3</font>. Put <font color=LEVEL>0-0</font> to disable this config.", 2);
        if (parametersString == null) {
            parametersString = "true,true,0,0-0,0-0";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.autoApplySchemeBuffs = Boolean.parseBoolean(params[0]);
            this.spawnNpcBuffer = Boolean.parseBoolean(params[1]);
            this.customNpcBuffer = Integer.parseInt(params[2]);
            String[] splitted = params[3].split(" ");
            this.autoBuffsFighterIds = new int[splitted.length];
            this.autoBuffsFighterLevels = new int[splitted.length];
            int i = 0;
            for (final String s : splitted) {
                this.autoBuffsFighterIds[i] = Integer.parseInt(s.split("-")[0]);
                this.autoBuffsFighterLevels[i] = Integer.parseInt(s.split("-")[1]);
                ++i;
            }
            splitted = params[4].split(" ");
            this.autoBuffsMageIds = new int[splitted.length];
            this.autoBuffsMageLevels = new int[splitted.length];
            i = 0;
            for (final String s : splitted) {
                this.autoBuffsMageIds[i] = Integer.parseInt(s.split("-")[0]);
                this.autoBuffsMageLevels[i] = Integer.parseInt(s.split("-")[1]);
                ++i;
            }
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public void buffPlayer(final PlayerEventInfo player) {
        if (player.isMageClass()) {
            if (this.autoBuffsMageIds[0] == 0) {
                return;
            }
            for (int i = 0; i < this.autoBuffsMageIds.length; ++i) {
                player.getSkillEffects(this.autoBuffsMageIds[i], this.autoBuffsMageLevels[i]);
            }
        }
        else {
            if (this.autoBuffsFighterIds[0] == 0) {
                return;
            }
            for (int i = 0; i < this.autoBuffsFighterIds.length; ++i) {
                player.getSkillEffects(this.autoBuffsFighterIds[i], this.autoBuffsFighterLevels[i]);
            }
        }
    }
    
    public boolean canRebuff() {
        return this.autoApplySchemeBuffs;
    }
    
    public boolean canSpawnBuffer() {
        return this.spawnNpcBuffer;
    }
    
    public int getCustomNpcBufferId() {
        return this.customNpcBuffer;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Buffer;
    }
}
