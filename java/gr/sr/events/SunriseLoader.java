package gr.sr.events;

import gr.sr.events.engine.*;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.OldStats;
import gr.sr.events.engine.main.base.MainEventInstanceTypeManager;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.SunriseEvents;
import gr.sr.playervalue.PlayerValueEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class SunriseLoader
{
    private static final Logger _log;
    public static final String version = "2.21";
    private static FileWriter fileWriter;
    private static final SimpleDateFormat _toFileFormat;
    public static boolean detailedDebug;
    public static boolean detailedDebugToConsole;
    public static boolean logToFile;
    private static Branch _branch;
    private static double _interfaceVersion;
    private static String _desc;
    private static boolean _instances;
    private static String _libsFolder;
    private static boolean _limitedHtml;
    private static boolean loaded;
    private static boolean loading;
    private static boolean _gmsDebugging;
    private static Set<PlayerEventInfo> _gmsDebuggingSet;
    public static int DEBUG_CHAT_CHANNEL_CLASSIC;
    public static int DEBUG_CHAT_CHANNEL;
    private static File debugFile;
    private static File detailedDebugFile;

    public static final void init(final Branch l2branch, final double interfaceVersion, final String desc, final boolean allowInstances, final String libsFolder, final boolean limitedHtml, final boolean cracked) {
        SunriseLoader._branch = l2branch;
        SunriseLoader._interfaceVersion = interfaceVersion;
        SunriseLoader._desc = desc;
        SunriseLoader._libsFolder = libsFolder;
        startLoading(SunriseLoader._branch, SunriseLoader._interfaceVersion, SunriseLoader._desc, allowInstances, SunriseLoader._libsFolder, limitedHtml);
    }

    private static void startLoading(final Branch l2branch, final double interfaceVersion, final String desc, final boolean allowInstances, final String libsFolder, final boolean limitedHtml) {
        SunriseLoader.loading = true;
        EventConfig.getInstance().loadGlobalConfigs();
        final String fileName = createDebugFile();
        if (fileName != null) {
            debug("Debug messages are stored in '" + fileName + "'");
        }
        debug("Thanks for using a legal version.");
        SunriseLoader._desc = desc;
        SunriseLoader._instances = allowInstances;
        SunriseLoader._libsFolder = libsFolder;
        SunriseLoader._limitedHtml = limitedHtml;
        debug("Loading engine version 2.2...");
        debug("Using " + SunriseLoader._desc + " interface (for engine v" + interfaceVersion + ").");
        if (interfaceVersion != l2branch._newestVersion) {
            debug("Your interface is outdated for this engine!!! Please update it.", Level.SEVERE);
        }
        OldStats.getInstance();
        SunriseEvents.loadHtmlManager();
        SunriseLoader.logToFile = EventConfig.getInstance().getGlobalConfigBoolean("logToFile");
        SunriseLoader.detailedDebug = EventConfig.getInstance().getGlobalConfigBoolean("detailedDebug");
        SunriseLoader.detailedDebugToConsole = EventConfig.getInstance().getGlobalConfigBoolean("detailedDebugToConsole");
        LanguageEngine.init();
        EventManager.getInstance();
        EventConfig.getInstance().loadEventConfigs();
        EventMapSystem.getInstance().loadMaps();
        EventRewardSystem.getInstance();
        EventManager.getInstance().getMainEventManager().loadScheduleData();
        MainEventInstanceTypeManager.getInstance();
        EventStatsManager.getInstance();
        EventWarnings.getInstance();
        PlayerValueEngine.getInstance();
        SunriseLoader.loaded = true;
        debug("Version 2.2 successfully loaded.");
    }

    public static final boolean isDebugging(final PlayerEventInfo gm) {
        return SunriseLoader._gmsDebugging && SunriseLoader._gmsDebuggingSet.contains(gm);
    }

    public static final void addGmDebug(final PlayerEventInfo gm) {
        if (!SunriseLoader._gmsDebugging) {
            SunriseLoader._gmsDebugging = true;
        }
        SunriseLoader._gmsDebuggingSet.add(gm);
    }

    public static final void removeGmDebug(final PlayerEventInfo gm) {
        if (!SunriseLoader._gmsDebugging) {
            return;
        }
        SunriseLoader._gmsDebuggingSet.remove(gm);
        if (SunriseLoader._gmsDebuggingSet.isEmpty()) {
            SunriseLoader._gmsDebugging = false;
        }
    }

    public static final void debug(String msg, final Level level) {
        msg = "Event Engine: " + msg;
        final String value = String.valueOf(level);
        switch (value) {
            case "INFO": {
                SunriseLoader._log.info(msg);
                break;
            }
            case "WARNING": {
                SunriseLoader._log.warn(msg);
                break;
            }
            case "SEVERE": {
                SunriseLoader._log.error(msg);
                break;
            }
        }
        if (SunriseLoader._gmsDebugging) {
            sendToGms(msg, level, false);
        }
        writeToFile(level, msg, false);
    }

    public static final void debug(String msg) {
        if (!msg.startsWith("Nexus ") && !msg.startsWith("nexus")) {
            msg = "Event Engine: " + msg;
        }
        SunriseLoader._log.info(msg);
        if (SunriseLoader._gmsDebugging) {
            sendToGms(msg, Level.INFO, false);
        }
        writeToFile(Level.INFO, msg, false);
    }

    public static final void sendToGms(final String msg, final Level level, final boolean detailed) {
        try {
            for (final PlayerEventInfo gm : SunriseLoader._gmsDebuggingSet) {
                gm.creatureSay("*" + (detailed ? msg : msg.substring(14)) + "  (" + level.toString() + ")", detailed ? "DD" : "DEBUG", detailed ? SunriseLoader.DEBUG_CHAT_CHANNEL : SunriseLoader.DEBUG_CHAT_CHANNEL_CLASSIC);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void detailedDebug(String msg) {
        if (!msg.startsWith("DD ")) {
            msg = "DD:  " + msg;
        }
        if (SunriseLoader._gmsDebugging) {
            sendToGms(msg, Level.INFO, true);
        }
        writeToFile(Level.INFO, msg, true);
    }

    public static final boolean allowInstances() {
        return SunriseLoader._instances;
    }

    public static final String getLibsFolderName() {
        return SunriseLoader._libsFolder;
    }

    public static final boolean isLimitedHtml() {
        return SunriseLoader._limitedHtml;
    }

    private static final String createDebugFile() {
        String path = "log/EventEngine";
        final File folder = new File(path);
        if (!folder.exists() && !folder.mkdir()) {
            path = "log";
        }
        SunriseLoader.debugFile = new File(path + "/EventEngine.log");
        if (!SunriseLoader.debugFile.exists()) {
            try {
                SunriseLoader.debugFile.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        int id = 0;
        for (final File f : folder.listFiles()) {
            if (f.getName().startsWith("EventEngine_detailed")) {
                try {
                    final String name = f.getName().substring(0, f.getName().length() - 4);
                    final int id2 = Integer.getInteger(name.substring(21));
                    if (id2 > id) {
                        id = id2;
                    }
                }
                catch (Exception ex) {}
            }
        }
        ++id;
        SunriseLoader.detailedDebugFile = new File(path + "/EventEngine_detailed_" + id + ".log");
        if (SunriseLoader.detailedDebugFile.exists()) {
            try {
                SunriseLoader.detailedDebugFile.delete();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        if (!SunriseLoader.detailedDebugFile.exists()) {
            try {
                SunriseLoader.detailedDebugFile.createNewFile();
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return SunriseLoader.detailedDebugFile.getPath();
    }

    public static void writeToFile(final Level level, final String msg, final boolean detailed) {
        if (!detailed && !SunriseLoader.logToFile) {
            return;
        }
        try {
            if (!detailed) {
                SunriseLoader.fileWriter = new FileWriter(SunriseLoader.debugFile, true);
            }
            else {
                SunriseLoader.fileWriter = new FileWriter(SunriseLoader.detailedDebugFile, true);
            }
            SunriseLoader.fileWriter.write(SunriseLoader._toFileFormat.format(new Date()) + ":  " + msg + " (" + level.getLocalizedName() + ")\r\n");
        }
        catch (Exception ex) {}
        finally {
            try {
                SunriseLoader.fileWriter.close();
            }
            catch (Exception ex2) {}
        }
    }

    public static String getTraceString(final StackTraceElement[] trace) {
        final StringBuilder sbString = new StringBuilder();
        for (final StackTraceElement element : trace) {
            sbString.append(element.toString()).append("\n");
        }
        final String result = sbString.toString();
        return result;
    }

    public static void shutdown() {
        EventWarnings.getInstance().saveData();
    }

    public static boolean loaded() {
        return SunriseLoader.loaded;
    }

    public static boolean loadedOrBeingLoaded() {
        return SunriseLoader.loading;
    }

    static {
        _log = LoggerFactory.getLogger("sunrise");
        _toFileFormat = new SimpleDateFormat("dd/MM/yyyy H:mm:ss");
        SunriseLoader.detailedDebug = false;
        SunriseLoader.detailedDebugToConsole = false;
        SunriseLoader.logToFile = false;
        SunriseLoader.loaded = false;
        SunriseLoader.loading = false;
        SunriseLoader._gmsDebugging = false;
        SunriseLoader._gmsDebuggingSet = ConcurrentHashMap.newKeySet();
        SunriseLoader.DEBUG_CHAT_CHANNEL_CLASSIC = 7;
        SunriseLoader.DEBUG_CHAT_CHANNEL = 6;
    }

    public enum Branch
    {
        Freya(2.1),
        Hi5(2.1),
        Hi5Priv(2.1),
        Final(2.1);

        public double _newestVersion;

        private Branch(final double interfaceVersion) {
            this._newestVersion = interfaceVersion;
        }
    }
}
