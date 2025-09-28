package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

public class AutoRestartConfigs extends AbstractConfigs {
    public static boolean RESTART_BY_TIME_OF_DAY;
    public static int RESTART_SECONDS;
    public static String[] RESTART_INTERVAL_BY_TIME_OF_DAY;

    public AutoRestartConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/AutoRestart.ini");
        RESTART_BY_TIME_OF_DAY = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableRestartSystem", "false"));
        RESTART_SECONDS = Integer.parseInt(this.getString(this._settings, this._override, "RestartSeconds", "360"));
        RESTART_INTERVAL_BY_TIME_OF_DAY = this.getString(this._settings, this._override, "RestartByTimeOfDay", "20:00").split(",");
    }

    protected static AutoRestartConfigs instance;

    public static AutoRestartConfigs getInstance() {
        if (instance == null)
            instance = new AutoRestartConfigs();
        return instance;
    }
}
