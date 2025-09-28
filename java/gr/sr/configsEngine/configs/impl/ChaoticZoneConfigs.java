//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class ChaoticZoneConfigs extends AbstractConfigs {
    
    public static boolean ENABLE_CHAOTIC_ZONE;
    
    public static boolean ENABLE_CHAOTIC_ZONE_AUTO_REVIVE;
    
    public static boolean ENABLE_CHAOTIC_ZONE_SKILL;
    
    public static int CHAOTIC_ZONE_SKILL_ID;
    
    public static int CHAOTIC_ZONE_REVIVE_DELAY;
    
    public static int CHAOTIC_ZONE_AUTO_RES_LOCS_COUNT;
    
    public static int[] xCoords;
    
    public static int[] yCoords;
    
    public static int[] zCoords;

    public ChaoticZoneConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/zones/ChaoticZone.ini");
        ENABLE_CHAOTIC_ZONE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableChaoticZone", "False"));
        ENABLE_CHAOTIC_ZONE_AUTO_REVIVE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableChaoticZoneAutoRes", "False"));
        ENABLE_CHAOTIC_ZONE_SKILL = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableChaoticZoneExtraSkill", "False"));
        CHAOTIC_ZONE_SKILL_ID = Integer.parseInt(this.getString(this._settings, this._override, "ChaoticZoneExtraSkillId", "26074"));
        CHAOTIC_ZONE_REVIVE_DELAY = Integer.parseInt(this.getString(this._settings, this._override, "ChaoticZoneReviveDelay", "5"));
        CHAOTIC_ZONE_AUTO_RES_LOCS_COUNT = Integer.parseInt(this.getString(this._settings, this._override, "ChaoticZoneAutoResLocsCount", "5"));
        String[] coords;
        ChaoticZoneConfigs.xCoords = new int[(coords = this.getString(this._settings, this._override, "AutoResXCoords", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for(int i = 0; i < coords.length; ++i) {
                ChaoticZoneConfigs.xCoords[count++] = Integer.parseInt(coords[i]);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] ycoords;
        yCoords = new int[(ycoords = this.getString(this._settings, this._override, "AutoResYCoords", "1204;1035;1048").trim().split(";")).length];

        int var15;
        try {
            int count = 0;
            for(int i = 0; i < ycoords.length; ++i) {
                yCoords[count++] = Integer.parseInt(ycoords[i]);
            }
        } catch (NumberFormatException var7) {
            _log.warn(var7.getMessage(), var7);
        }
        String[] zcoords;

        zCoords = new int[(zcoords = this.getString(this._settings, this._override, "AutoResZCoords", "1204;1035;1048").trim().split(";")).length];
        try {
            int count = 0;
            for(int i = 0; i < zcoords.length; ++i) {
                zCoords[count++] = Integer.parseInt(zcoords[i]);
            }

        } catch (NumberFormatException var6) {
            _log.warn(var6.getMessage(), var6);
        }
    }
    protected static ChaoticZoneConfigs instance;

    public static ChaoticZoneConfigs getInstance() {
        if(instance == null)
            instance = new ChaoticZoneConfigs();
        return instance;
    }
}
