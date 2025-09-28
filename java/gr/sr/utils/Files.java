package gr.sr.utils;

import l2r.Config;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.io.*;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Files {
    private static final Map<String, String> cache = new ConcurrentHashMap();

    public Files() {
    }

    public static String read(String path) {
        if (path == null) {
            return null;
        } else if (Config.LAZY_CACHE && cache.containsKey(path)) {
            return (String) cache.get(path);
        } else {
            File file;
            if (!(file = new File("./" + path)).exists()) {
                return null;
            } else {
                String finalString = null;
                BufferedReader br;
                try {
                    br = new BufferedReader(new UnicodeReader(new FileInputStream(file), "UTF-8"));
                    StringBuffer stringBuffer = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null) {
                        stringBuffer.append(line).append("\n");
                    }
                    finalString = stringBuffer.toString();

                    if (Config.LAZY_CACHE) {
                        cache.put(path, finalString);
                    }
                    br.close();

                }catch (Exception e) {
                    System.out.println(e);
                }
                return finalString;
            }
        }
    }

    public static void cacheClean() {
        cache.clear();
    }

    public static long lastModified(String file) {
        return file == null ? 0L : (new File(file)).lastModified();
    }

    public static String read(String file, L2PcInstance player) {
        return player == null ? "" : read(file);
    }

    public static void writeFile(String filePath, String initialFile) {
        if (initialFile != null && initialFile.length() != 0) {
            File file;
            if (!(file = new File(filePath)).exists()) {
                try {
                    file.createNewFile();
                } catch (IOException var11) {
                    var11.printStackTrace(System.err);
                }
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(initialFile.getBytes("UTF-8"));
                fos.close();
            } catch (IOException var13) {
                var13.printStackTrace(System.err);
            }

        }
    }

    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
