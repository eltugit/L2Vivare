//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.imageGeneratorEngine;

import gr.sr.utils.DDSConverter;
import l2r.Config;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.PledgeCrest;
import l2r.gameserver.util.Util;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ImagesCache {
    private static final Logger log = LoggerFactory.getLogger(ImagesCache.class);
    private static final String CREST;
    private static final ImagesCache IMAGES_CACHE;
    private final Map<String, Integer> images = new ConcurrentHashMap();
    private final Map<Integer, byte[]> image = new ConcurrentHashMap();

    public static final ImagesCache getInstance() {
        return IMAGES_CACHE;
    }

    private ImagesCache() {
        this.load("data/sunrise/images/id_by_name");
    }

    private void load(String path) {
        Map var2;

        for (Object o : (var2 = parsePath(path)).entrySet()) {
            Entry var4;
            byte[] var5 = DDSConverter.convertToDDS((File) (var4 = (Entry) o).getValue()).array();
            this.image.put((Integer) var4.getKey(), var5);
        }

        log.info(ImagesCache.class.getSimpleName() + ": Loaded " + var2.size() + " images (" + path + ").");
    }

    private static Map<Integer, File> parsePath(String path) {
        HashMap var1 = new HashMap();
        File var2;
        if ((var2 = new File(Config.DATAPACK_ROOT, path)).exists()) {
            File[] var7;
            if ((var7 = var2.listFiles()) == null) {
                return var1;
            }

            int var8 = (var7 = var7).length;

            for(int var3 = 0; var3 < var8; ++var3) {
                File var4;
                if ((var4 = var7[var3]).getName().endsWith(".png")) {
                    try {
                        String var5 = FilenameUtils.getBaseName(var4.getName());
                        var1.put(Integer.parseInt(var5), var4);
                    } catch (NumberFormatException var6) {
                        log.error(ImagesCache.class.getSimpleName() + ": File " + var4.getName() + " in id_by_name folder has invalid name!", var6);
                    }
                }
            }
        } else {
            log.error(ImagesCache.class.getSimpleName() + ": Path " + path + " doesn't exist!");
        }

        return var1;
    }

    public void sendHtmlImages(String var1, L2PcInstance var2) {
        char[] var3 = var1.toCharArray();
        int var4 = 0;

        while(true) {
            do {
                if (var4 == -1) {
                    return;
                }
            } while((var4 = var1.indexOf(CREST, var4)) == -1);

            int var5 = var4 + CREST.length();
            char[] var7 = var3;

            int var6;
            for(var6 = var5; var6 < var7.length && Util.isInteger(var7[var6]); ++var6) {
            }

            var4 = var6;
            var5 = Integer.parseInt(var1.substring(var5, var6));
            this.sendImageToPlayer(var2, var5);
        }
    }

    public void sendImageToPlayer(L2PcInstance var1, int var2) {
        if (!var1.wasImageLoaded(var2)) {
            var1.addLoadedImage(var2);
            if (this.image.containsKey(var2)) {
                var1.sendPacket(new PledgeCrest(var2, (byte[])this.image.get(var2)));
            }

        }
    }

    public Set<Integer> getAllImages() {
        return this.image.keySet();
    }

    public int getImageId(String var1) {
        int var2 = 0;
        if (this.images.get(var1.toLowerCase()) != null) {
            var2 = (Integer)this.images.get(var1.toLowerCase());
        }

        return var2;
    }

    public byte[] getImage(int var1) {
        return (byte[])this.image.get(var1);
    }

    static {
        CREST = "Crest.crest_" + Config.SERVER_ID + "_";
        IMAGES_CACHE = new ImagesCache();
    }
}
