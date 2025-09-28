package gr.sr.configsEngine;


import gr.sr.utils.FileProperties;
import l2r.log.filter.Log;
import l2r.util.PropertiesParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;


public abstract class AbstractConfigs {

    public static final Logger _log = LoggerFactory.getLogger(AbstractConfigs.class);
    public static final String OVERRIDE_CONFIG_FILE = "./config/override.properties";

    protected FileProperties _settings = new FileProperties();

    protected Properties _override = null;

    public AbstractConfigs() {
    }


    public void loadFile(String path) {
        File file = new File(path);
        try {
            FileInputStream fiS = new FileInputStream(file);
            this._settings.load(fiS);
            fiS.close();
        } catch (Exception e) {
            Log.error("Error while loading " + path + " settings!", e);
        }

    }

    public void loadOverride() {
        File file = new File("./config/override.properties");
        try {
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                this._override = new Properties();
                this._override.load(fileInputStream);
            }

        } catch (Exception e) {
        }
    }


    public void loadConfigs() {
    }

    public void resetConfigs() {
    }


    public <T extends Enum<T>> T getEnum(PropertiesParser var1, Properties var2, String var3, Class<T> var4, T var5) {
        String var7;
        if ((var7 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null) {
            return var5;
        } else {
            try {
                return Enum.valueOf(var4, var7);
            } catch (IllegalArgumentException var6) {
                return var5;
            }
        }
    }


    public boolean getBoolean(PropertiesParser var1, Properties var2, String var3, boolean var4) {
        String var5;
        return (var5 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Boolean.parseBoolean(var5);
    }


    public long getLong(PropertiesParser var1, Properties var2, String var3, long var4) {
        String var6;
        return (var6 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Long.parseLong(var6);
    }


    public int getInt(PropertiesParser var1, Properties var2, String var3, int var4) {
        String var5;
        return (var5 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Integer.parseInt(var5);
    }


    public byte getByte(PropertiesParser var1, Properties var2, String var3, byte var4) {
        String var5;
        return (var5 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Byte.parseByte(var5);
    }


    public float getFloat(PropertiesParser var1, Properties var2, String var3, float var4) {
        String var5;
        return (var5 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Float.parseFloat(var5);
    }


    public double getDouble(PropertiesParser var1, Properties var2, String var3, double var4) {
        String var6;
        return (var6 = this.getString((PropertiesParser)var1, var2, var3, (String)null)) == null ? var4 : Double.parseDouble(var6);
    }


    public String getString(PropertiesParser var1, Properties var2, String var3, String var4) {
        String var5 = null;
        if (var2 != null) {
            var5 = var2.getProperty(var3);
        }

        if (var5 == null) {
            var5 = var1.getString(var3, (String)null);
        }

        return var5 == null ? var4 : var5;
    }


    public <T extends Enum<T>> T getEnum(FileProperties var1, Properties var2, String var3, Class<T> var4, T var5) {
        String var7;
        if ((var7 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null) {
            return var5;
        } else {
            try {
                return Enum.valueOf(var4, var7);
            } catch (IllegalArgumentException var6) {
                return var5;
            }
        }
    }


    public boolean getBoolean(FileProperties var1, Properties var2, String var3, boolean var4) {
        String var5;
        return (var5 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Boolean.parseBoolean(var5);
    }


    public long getLong(FileProperties var1, Properties var2, String var3, long var4) {
        String var6;
        return (var6 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Long.parseLong(var6);
    }


    public int getInt(FileProperties var1, Properties var2, String var3, int var4) {
        String var5;
        return (var5 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Integer.parseInt(var5);
    }


    public byte getByte(FileProperties var1, Properties var2, String var3, byte var4) {
        String var5;
        return (var5 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Byte.parseByte(var5);
    }


    public float getFloat(FileProperties var1, Properties var2, String var3, float var4) {
        String var5;
        return (var5 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Float.parseFloat(var5);
    }


    public double getDouble(FileProperties var1, Properties var2, String var3, double var4) {
        String var6;
        return (var6 = this.getString((FileProperties)var1, var2, var3, (String)null)) == null ? var4 : Double.parseDouble(var6);
    }


    public String getString(FileProperties var1, Properties var2, String var3, String var4) {
        String var5 = null;
        if (var2 != null) {
            var5 = var2.getProperty(var3);
        }

        if (var5 == null) {
            var5 = var1.getProperty(var3, (String)null);
        }

        return var5 == null ? var4 : var5;
    }


    public String getString(FileProperties var1, Properties var2, String var3, String var4, boolean var5) {
        String var6 = null;
        if (var2 != null) {
            var6 = var2.getProperty(var3);
        }

        if (var6 == null) {
            var6 = var1.getProperty(var3, (String)null, var5);
        }

        return var6 == null ? var4 : var6;
    }
}
