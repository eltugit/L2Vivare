package gr.sr.configsEngine.configs.impl;

import gr.sr.configsEngine.AbstractConfigs;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColorSystemConfigs extends AbstractConfigs {
    public static boolean ENABLE_COLOR_SYSTEM;
    public static boolean ENABLE_NAME_COLOR;
    public static String COLOR_NAME_DEPENDS;
    public static Map<Integer, Integer> NAME_COLORS = new LinkedHashMap();
    public static boolean ENABLE_TITLE_COLOR;
    public static String COLOR_TITLE_DEPENDS;
    public static Map<Integer, Integer> TITLE_COLORS = new LinkedHashMap();

    public ColorSystemConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/ColorSystem.ini");
        NAME_COLORS.clear();
        TITLE_COLORS.clear();
        ENABLE_COLOR_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableColorSystem", "false"));
        ENABLE_NAME_COLOR = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableNameColorSystem", "false"));
        COLOR_NAME_DEPENDS = this.getString(this._settings, this._override, "NameColorDepends", "PVP");
        String[] title;
        int titles = (title = this.getString(this._settings, this._override, "NameColor", "100,00FF00;200,FF0000;300,0000FF;").split(";")).length;

        for(int var3 = 0; var3 < titles; ++var3) {
            String[] var5 = title[var3].split(",");
            NAME_COLORS.put(Integer.parseInt(var5[0]), Integer.decode("0x" + var5[1]));
        }

        ENABLE_TITLE_COLOR = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableTitleColorSystem", "false"));
        COLOR_TITLE_DEPENDS = this.getString(this._settings, this._override, "TitleColorDepends", "PK");
        String[] titleColors;
        int titleCount = (titleColors = this.getString(this._settings, this._override, "TitleColor", "100,00FF00;200,FF0000;300,0000FF;").split(";")).length;

        for(int i = 0; i < titleCount; ++i) {
            title = titleColors[i].split(",");
            TITLE_COLORS.put(Integer.parseInt(title[0]), Integer.decode("0x" + title[1]));
        }

    }

    protected static ColorSystemConfigs instance;

    public static ColorSystemConfigs getInstance() {
        if (instance == null)
            instance = new ColorSystemConfigs();
        return instance;
    }
}