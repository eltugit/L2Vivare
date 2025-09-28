package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;


public class PcBangConfigs extends AbstractConfigs {
    public static boolean PC_BANG_ENABLED;
    public static int MAX_PC_BANG_POINTS;
    public static boolean ENABLE_DOUBLE_PC_BANG_POINTS;
    public static int DOUBLE_PC_BANG_POINTS_CHANCE;
    public static double PC_BANG_POINT_RATE;
    public static boolean RANDOM_PC_BANG_POINT;

    public PcBangConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/PcCafe.ini");
        PC_BANG_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "Enabled", "false"));
        if ((MAX_PC_BANG_POINTS = Integer.parseInt(this.getString(this._settings, this._override, "MaxPcBangPoints", "200000"))) < 0) {
            MAX_PC_BANG_POINTS = 0;
        }

        ENABLE_DOUBLE_PC_BANG_POINTS = Boolean.parseBoolean(this.getString(this._settings, this._override, "DoublingAcquisitionPoints", "false"));
        if ((DOUBLE_PC_BANG_POINTS_CHANCE = Integer.parseInt(this.getString(this._settings, this._override, "DoublingAcquisitionPointsChance", "1"))) < 0 || DOUBLE_PC_BANG_POINTS_CHANCE > 100) {
            DOUBLE_PC_BANG_POINTS_CHANCE = 1;
        }

        if ((PC_BANG_POINT_RATE = Double.parseDouble(this.getString(this._settings, this._override, "AcquisitionPointsRate", "1.0"))) < 0.0D) {
            PC_BANG_POINT_RATE = 1.0D;
        }

        RANDOM_PC_BANG_POINT = Boolean.parseBoolean(this.getString(this._settings, this._override, "AcquisitionPointsRandom", "false"));
    }

    protected static PcBangConfigs instance;

    public static PcBangConfigs getInstance() {
        if (instance == null)
            instance = new PcBangConfigs();
        return instance;
    }
}
