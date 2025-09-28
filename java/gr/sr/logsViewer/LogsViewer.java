package gr.sr.logsViewer;


import gr.sr.logsViewer.runnable.Capture;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ShowBoard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class LogsViewer {
    private static int baseLineCount = 23;

    public LogsViewer() {
    }

    private static int getLineCount(String filePath) {
        int counter = 0;
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(filePath));
            while (bfr.readLine() != null) {
                ++counter;
            }

            bfr.close();
        } catch (IOException e) {
            System.out.println("Error: LogViewer.java error: " + e);
        }

        return counter;
    }

    private static String generate(String filePath, int lineCount) {
        int count = 0;
        String html = "";

        try {
            BufferedReader bfr = new BufferedReader(new FileReader(filePath));
            while (true) {
                String line;
                if ((line = bfr.readLine()) == null) {
                    break;
                }
                if (lineCount < count) {
                    html = html + "<tr><td align=left valign=top><font color=66FFFF>" + line + "</font></td></tr>";
                    ++lineCount;
                }
                ++count;
            }
            bfr.close();
        } catch (IOException e) {
            System.out.println("Error: LogViewer.java error: " + e);
        }
        return html;
    }


    public static void startLogViewer(L2PcInstance p, String html) {
        sendCbWindow(p, html);
        p._captureTask = ThreadPoolManager.getInstance().scheduleGeneral(new Capture(p, html), 500L);
    }


    public static void stopLogViewer(L2PcInstance p, String html) {
        if (p._captureTask != null) {
            p._captureTask.cancel(true);
        }

        sendCbWindow(p, html);
    }


    public static void sendCbWindow(L2PcInstance player, String htmlToChange) {
        String html = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), "data/html/sunrise/logViewer/main.htm").replace("%file%", htmlToChange);
        if (player._captureTask != null && !player._captureTask.isDone()) {
            html = html.replace("%stop_button_fore%", "L2UI_CT1.Button_DF.Gauge_DF_Attribute_Fire").replace("%stop_button_back%", "L2UI_CT1.Button_DF.Gauge_DF_Attribute_Fire_bg");
        } else {
            html = html.replace("%stop_button_fore%", "L2UI_CT1.Button_DF.Button_DF").replace("%stop_button_back%", "L2UI_CT1.Button_DF.Button_DF_Down");
        }

        separateAndSend(html.replace("%logs%", generate(getLogPath(htmlToChange), getLineCount(getLogPath(htmlToChange)) - baseLineCount)), player);
    }

    protected static void separateAndSend(String html, L2PcInstance p) {
        if (html != null) {
            if (html.length() < 4096) {
                p.sendPacket(new ShowBoard(html, "101"));
                p.sendPacket(new ShowBoard((String) null, "102"));
                p.sendPacket(new ShowBoard((String) null, "103"));
            } else if (html.length() < 8192) {
                p.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
                p.sendPacket(new ShowBoard(html.substring(4096), "102"));
                p.sendPacket(new ShowBoard((String) null, "103"));
            } else {
                if (html.length() < 16384) {
                    p.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
                    p.sendPacket(new ShowBoard(html.substring(4096, 8192), "102"));
                    p.sendPacket(new ShowBoard(html.substring(8192), "103"));
                }

            }
        }
    }

    private static String getLogPath(String fileName) {
        switch (fileName) {
            case "warnings.log":
                fileName = "./log/main/" + fileName;
                break;
            case "game.log":
                fileName = "./log/main/" + fileName;
                break;
            case "java.log":
                fileName = "./log/main/" + fileName;
                break;
            case "errors.log":
                fileName = "./log/main/" + fileName;
                break;
            default:
                fileName = "./log/" + fileName;
                break;
        }
        return fileName;
    }
}
