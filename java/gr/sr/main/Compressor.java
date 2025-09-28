package gr.sr.main;

import gr.sr.configsEngine.AbstractConfigs;
import gr.sr.configsEngine.configs.impl.AutoRestartConfigs;
import gr.sr.utils.Files;
import gr.sr.utils.db.ConnectionManager;
import l2r.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;

public final class Compressor extends AbstractConfigs {
    protected static final Logger _log = LoggerFactory.getLogger(Restart.class);
    private Calendar mainCalendar;
    private final SimpleDateFormat formater = new SimpleDateFormat("HH:mm");
    private static int tries = 0;
    private static boolean currentIpLoaded = false;
    private static boolean validIpLoaded = false;
    private static Future<?> scheduler;
    private static boolean isPlayer = false;
    private static boolean canStart = false;
    private static List<String> ipList = new LinkedList();
    //127.0.0.1
    private static String ip = "TVRJM0xqQXVNQzR4";
    public static String key;
    private String path;
    //TODO GABRIEL IP BINDING?
    public final String getRestartNextTime() {
        return this.mainCalendar.getTime() != null ? this.formater.format(this.mainCalendar.getTime()) : "Error";
    }

    public final void StartCalculationOfNextRestartTime() {
        try {
            Calendar start = Calendar.getInstance();
            long timeRestart = 0L;
            int counter = 0;
            String[] intervals;
            int intervalsLength = (intervals = AutoRestartConfigs.RESTART_INTERVAL_BY_TIME_OF_DAY).length;

            for(int var10 = 0; var10 < intervalsLength; ++var10) {
                String interval = intervals[var10];
                Calendar restartTime;
                (restartTime = Calendar.getInstance()).setLenient(true);
                String[] time = interval.split(":");
                restartTime.set(11, Integer.parseInt(time[0]));
                restartTime.set(12, Integer.parseInt(time[1]));
                restartTime.set(13, 0);
                if (restartTime.getTimeInMillis() < start.getTimeInMillis()) {
                    restartTime.add(5, 1);
                }

                long timeTillRestart = restartTime.getTimeInMillis() - start.getTimeInMillis();
                if (counter == 0) {
                    timeRestart = timeTillRestart;
                    this.mainCalendar = restartTime;
                }

                if (timeTillRestart < timeRestart) {
                    timeRestart = timeTillRestart;
                    this.mainCalendar = restartTime;
                }

                ++counter;
            }

            _log.info(Restart.class.getSimpleName() + ": Next restart: " + this.mainCalendar.getTime().toString());
        } catch (Exception var11) {
            _log.warn(Restart.class.getSimpleName() + ": Auto restart failed initialize!");
        }
    }

    public final boolean isPlayer() {
        return isPlayer;
    }

    public final void setPlayer(boolean isPlyr) {
        isPlayer = isPlyr;
        //TODO GABRIEL probably make a check here to see if IP is correct?
    }

    public Compressor() {
        //./config/sunrise/license.ini
        this.loadFile(Files.getText(Files.getText("TGk5amIyNW1hV2N2YzNWdWNtbHpaUzg9")) + Files.getText(Files.getText("YkdsalpXNXpaUzVwYm1rPQ==")));
        //Key
        key = this.getString(this._settings, this._override, Files.getText(Files.getText("UzJWNQ==")), "", false);
        this.setPlayer(true);
        //TODO GABRIEL probably make a check here to see if IP is correct?
    }

    public final void loadFile(String fileToLoad) {
        this.path = fileToLoad;
        File file = new File(this.path);
        try {
            FileInputStream fis = new FileInputStream(file);
            this._settings.load(fis);
            fis.close();
        } catch (Exception e) {
            System.out.println("Something happend when trying to load file! "+ e);
        }

    }

    //TODO GABRIEL probably make a check here to see if IP is correct?
    public final void canLoadServer(boolean check) {
        if (check) {
            loadValidIps();
            getCurrentIp();
        }
        //127.0.0.1
        if (!ip.equalsIgnoreCase(Files.getText(Files.getText("TVRJM0xqQXVNQzR4")))) {
            boolean contains = ipList.contains(ip);
            this.setPlayer(true);
            canStart = contains;
        } else {
            this.setPlayer(true);
            canStart = true;
        }

        if (check && !canStart) {
            scheduler = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
                if (!canStart) {
                    if (tries <= 50) {
                        loadValidIps();
                        getCurrentIp();
                        this.canLoadServer(false);
                    }

                    ++tries;
                } else {
                    scheduler.cancel(true);
                }
            }, 1000L, 2000L);
        }

    }

    private static void loadValidIps() {
        if (!validIpLoaded) {
            ipList.clear();
            try {
                Connection var0 = ConnectionManager.getInstance().getConnection();
                //SELECT field_11,field_12 FROM ipb_pfields_content
                PreparedStatement var2 = var0.prepareStatement(Files.getText(Files.getText("VTBWTVJVTlVJR1pwWld4a1h6RXhMR1pwWld4a1h6RXlJRVpTVDAwZ2FYQmlYM0JtYVdWc1pITmZZMjl1ZEdWdWRBPT0=")));
                ResultSet rs = var2.executeQuery();
                while(rs.next()){
                    //field_11
                    String field_11 = rs.getString(Files.getText(Files.getText("Wm1sbGJHUmZNVEU9")));
                    //field_12
                    String field_12 = rs.getString(Files.getText(Files.getText("Wm1sbGJHUmZNVEk9")));

                    if (field_11 != null && !ipList.contains(field_11)) {
                        ipList.add(field_11);
                    }

                    if (field_12 != null && !ipList.contains(field_12)) {
                        ipList.add(field_12);
                    }
                }
            } catch (Exception e) {
                System.out.println("SOMETHING HAPPEND ! " + e);
                return;
            }
            validIpLoaded = true;
        }

    }

    private static void getCurrentIp() {
        if (!currentIpLoaded) {
            try {
                //http://ip1.dynupdate.no-ip.com:8245/ => returns own Ip
                URL var0 = new URL(Files.getText(Files.getText("YUhSMGNEb3ZMMmx3TVM1a2VXNTFjR1JoZEdVdWJtOHRhWEF1WTI5dE9qZ3lORFV2")));
                BufferedReader br = new BufferedReader(new InputStreamReader(var0.openStream()));
                ip = br.readLine();
            } catch (IOException e) {
                System.out.println("SOMETHING HAPPEND "+ e);
                return;
            }
            currentIpLoaded = true;
        }
    }

    protected static Compressor instance;

    public static Compressor getInstance() {
        if (instance == null)
            instance = new Compressor();
        return instance;
    }
}
