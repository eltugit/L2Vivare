package gr.sr.utils;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public final class Tools {
    protected static final Logger _log = LoggerFactory.getLogger(Tools.class);

    public Tools() {
    }

    public static long convertStringToDate(String var0) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        Date date;
        try {
            date = formater.parse(var0);
        } catch (ParseException e) {
            System.out.println(e);
            return 0L;
        }

        return date.getTime();
    }

    public static String convertDateToString(long time) {
        Date date = new Date(time);
        return (new SimpleDateFormat("dd/MM/yyyy HH:mm")).format(date);
    }

    public static String convertHourToString(long time) {
        Date date = new Date(time);
        return (new SimpleDateFormat("HH:mm")).format(date);
    }

    
    public static String convertMinuteToString(long tiem) {
        Date date = new Date(tiem);
        return (new SimpleDateFormat("mm:ss")).format(date);
    }

    
    public static boolean isDualBox(L2PcInstance player, L2PcInstance player2) {
        return isDualBox(player, player2, false);
    }

    public static boolean isDualBox(L2PcInstance player1, L2PcInstance player2, boolean debug) {
        if (player1.getClient() != null && !player1.getClient().isDetached() && player2.getClient() != null && !player2.getClient().isDetached()) {
            try {
                String netIpP1 = getNetIp(player1);
                String netIpP2 = getNetIp(player2);
                String pcIpP1 = getPcIp(player1);
                String pcIpP2 = getPcIp(player2);
//                System.out.println(String.format("getPcIp: %s getNetIp: %s", pcIpP1, netIpP1));
                if (netIpP1.equals(netIpP2) && pcIpP1.equals(pcIpP2)) {
                    if (debug) {
                        _log.warn("Dual Box System: " + player1 + " (" + netIpP1 + "/ " + pcIpP1 + ") Dual Box Detected!");
                        _log.warn("Dual Box System: " + player2 + " (" + netIpP2 + "/ " + pcIpP2 + ") Dual Box Detected!");
                    }
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    
    public static String getPcIp(L2PcInstance player) {
        if (player.getClient() != null && !player.getClient().isDetached()) {
            String pcIp = "";
            int[][] trace = player.getClient().getTrace();

            for(int i = 0; i < trace[0].length; ++i) {
                pcIp = pcIp + trace[0][i];
                if (i != trace[0].length - 1) {
                    pcIp = pcIp + ".";
                }
            }

            return pcIp;
        } else {
            return null;
        }
    }

    
    public static String getNetIp(L2PcInstance player) {
        return player.getClient() != null && !player.getClient().isDetached() ? player.getClient().getConnection().getInetAddress().getHostAddress() : null;
    }

    public static final void sleep(long interval) {
        try {
            Thread.sleep(interval);
        } catch (InterruptedException var2) {
        }
    }

    public static String calc(String calc) {
        return "";
    }
}
