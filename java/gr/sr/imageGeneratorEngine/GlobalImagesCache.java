package gr.sr.imageGeneratorEngine;


import gr.sr.utils.DDSConverter;
import l2r.Config;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.instancemanager.ServerVariables;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.PledgeCrest;
import l2r.gameserver.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;


public class GlobalImagesCache {
    private static final Logger log = LoggerFactory.getLogger(GlobalImagesCache.class);
    private static final String CREST;
    private static final int[] c;
    private static final int d;


    public static final Pattern HTML_PATTERN;
    private static final GlobalImagesCache GLOBAL_IMAGES_CACHE;
    private final Map<String, Integer> f = new ConcurrentHashMap();
    private final Map<Integer, byte[]> images = new ConcurrentHashMap();


    public static final GlobalImagesCache getInstance() {
        return GLOBAL_IMAGES_CACHE;
    }

    private GlobalImagesCache() {
        this.load("data/sunrise/images");
    }

    public void load(String var1) {
        File file;
        if ((file = new File(Config.DATAPACK_ROOT, var1)).exists() && file.isDirectory()) {
            int var3 = 0;
            File[] files;
            int var4 = (files = file.listFiles()).length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File var6;
                if (!(var6 = files[var5]).isDirectory() && (var6 = resize(var6)) != null) {
                    ++var3;
                    String var7 = var6.getName();
                    byte[] var8 = DDSConverter.convertToDDS(var6).array();
                    int var9 = ServerVariables.getInt(var6.getName(), IdFactory.getInstance().getNextId());
                    if (!var6.getName().startsWith("captcha")) {
                        ServerVariables.set(var6.getName(), var9);
                    }

                    this.f.put(var7.toLowerCase(), var9);
                    this.images.put(var9, var8);
                }
            }

            log.info(this.getClass().getSimpleName() + ": Loaded " + var3 + " images (" + var1 + ").");
        } else {
            log.info(this.getClass().getSimpleName() + ": Files missing, loading aborted.");
        }
    }

    private static File resize(File var0) {
        BufferedImage var1;
        try {
            var1 = ImageIO.read(var0);
        } catch (IOException var10) {
            log.error(GlobalImagesCache.class.getSimpleName() + ": Error while resizing " + var0.getName() + " image.", var10);
            return null;
        }

        if (var1 == null) {
            return null;
        } else {
            int var2 = var1.getWidth();
            int var3 = var1.getHeight();
            boolean var4 = true;
            if (var2 > d) {
                var1 = var1.getSubimage(0, 0, d, var3);
                var4 = false;
            }

            boolean var5 = true;
            if (var3 > d) {
                var1 = var1.getSubimage(0, 0, var2, d);
                var5 = false;
            }

            int var6 = var2;
            int var7;
            int var8;
            if (var4) {
                int[] var11 = c;

                for(var7 = 0; var7 < 11; ++var7) {
                    if ((var8 = var11[var7]) >= var2) {
                        var6 = var8;
                        break;
                    }
                }
            }

            int var12 = var3;
            if (var5) {
                int[] var13 = c;

                for(var8 = 0; var8 < 11; ++var8) {
                    if ((var7 = var13[var8]) >= var3) {
                        var12 = var7;
                        break;
                    }
                }
            }

            if (var6 != var2 || var12 != var3) {
                for(int var14 = 0; var14 < var6; ++var14) {
                    for(var7 = 0; var7 < var12; ++var7) {
                        var1.setRGB(var14, var7, Color.BLACK.getRGB());
                    }
                }

                String var15;
                String var16 = (var15 = var0.getName()).substring(var15.lastIndexOf("."));

                try {
                    ImageIO.write(var1, var16, var0);
                } catch (IOException var9) {
                    log.error(GlobalImagesCache.class.getSimpleName() + ": Error while resizing " + var0.getName() + " image.", var9);
                    return null;
                }
            }

            return var0;
        }
    }


    public void sendUsedImages(String var1, L2PcInstance var2) {
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
            if (this.images.containsKey(var2)) {
                var1.sendPacket(new PledgeCrest(var2, (byte[])this.images.get(var2)));
            }

        }
    }

    public Set<Integer> getAllImages() {
        return this.images.keySet();
    }


    public int getImageId(String var1) {
        int var2 = 0;
        if (this.f.get(var1.toLowerCase()) != null) {
            var2 = (Integer)this.f.get(var1.toLowerCase());
        }

        return var2;
    }

    public byte[] getImage(int var1) {
        return (byte[])this.images.get(var1);
    }

    static {
        CREST = "Crest.crest_" + Config.SERVER_ID + "_";
        d = (c = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024})[10];
        HTML_PATTERN = Pattern.compile("%image:(.*?)%", 32);
        GLOBAL_IMAGES_CACHE = new GlobalImagesCache();
    }
}
