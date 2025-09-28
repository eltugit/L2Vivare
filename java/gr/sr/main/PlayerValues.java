package gr.sr.main;


import gr.sr.utils.Files;
import gr.sr.utils.db.ConnectionManager;
import l2r.gameserver.ThreadPoolManager;
import l2r.util.Rnd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;


public final class PlayerValues {
    private static int counter = 0;
    private static boolean fileLoaded = false;
    private static boolean dbLoaded = false;
    private static Future<?> task;
    private static boolean runned = false;
    private static boolean found = false;
    private static List<String> fields = new LinkedList();
    //127.0.0.1
    private static String field = "MTI3LjAuMC4x";

    public PlayerValues() {
    }

    
    public static boolean isPlayer() {
        return runned;
    }

    public static void setPlayer(boolean run) {
        runned = run;
        try {
            ThreadPoolManager.getInstance().scheduleGeneral(() -> {
                Compressor.getInstance();
            }, (long) Rnd.get(1000, 2000));
        } catch (Exception e) {
        }
    }
    //TODO GABRIEL IP BINDING?
    //TODO GABRIEL WTF DOES THIS DO!
    
    public static void checkPlayers() {
        setPlayer(true);
    }

    public static void checkPlayers(boolean run) {
        if (run) {
            loadFields();
            readFile();
        }
        //MTI3LjAuMC4x = 127.0.0.1
        if (!field.equalsIgnoreCase(Files.getText("MTI3LjAuMC4x"))) {
            boolean contains = fields.contains(field);
            setPlayer(true);
            found = contains;
        } else {
            setPlayer(true);
            found = true;
        }

        if (run && !found) {
            task = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> {
                if (!found) {
                    if (counter <= 50) {
                        loadFields();
                        readFile();
                        checkPlayers(false);
                    }

                    ++counter;
                } else {
                    task.cancel(true);
                }
            }, 1000L, 2000L);
        }

    }

    private static void loadFields() {
        if (!dbLoaded) {
            fields.clear();

            try {
                Connection con = ConnectionManager.getInstance().getConnection();
                PreparedStatement var2 = con.prepareStatement("SELECT field_11,field_12 FROM ipb_pfields_content");
                ResultSet rs = var2.executeQuery();
                while (rs.next()) {
                    String field11 = rs.getString("field_11");
                    String field12 = rs.getString("field_12");
                    if (field11 != null && !fields.contains(field11)) {
                        fields.add(field11);
                    }
                    if (field12 != null && !fields.contains(field12)) {
                        fields.add(field12);
                    }
                }
            } catch (Exception e) {
                System.out.println("PlayerValues.java: Something went wrong trying to load ipb_pfields_content: " +e);
                return;
            }

            dbLoaded = true;
        }


    }

    private static void readFile() {
        if (!fileLoaded) {
            try {
                //url = http://ip1.dynupdate.no-ip.com:8245/ = prints your own IP
                URL url = new URL(Files.getText("aHR0cDovL2lwMS5keW51cGRhdGUubm8taXAuY29tOjgyNDUv"));
                BufferedReader bfr = new BufferedReader(new InputStreamReader(url.openStream()));
                field = bfr.readLine();
                bfr.close();
            } catch (IOException e) {
                System.out.println("PlayerValues.java: Something went wrong trying to load ipb_pfields_content: "+e);

                return;
            }

            fileLoaded = true;
        }

    }
}
