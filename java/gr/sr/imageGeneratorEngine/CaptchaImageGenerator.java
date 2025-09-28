package gr.sr.imageGeneratorEngine;


import gr.sr.utils.DDSConverter;
import l2r.Config;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.PledgeCrest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;


public class CaptchaImageGenerator {
    public StringBuilder finalString = new StringBuilder();

    public CaptchaImageGenerator() {
    }

    
    public StringBuilder getFinalString() {
        return this.finalString;
    }

    public BufferedImage generateCaptcha() {
        Color firstColor = new Color(98, 213, 43);
        Color secondColor = new Color(98, 213, 43);
        Font font = new Font("comic sans ms", 1, 24);
        BufferedImage bufferedImage;
        Graphics2D graphics2D;
        (graphics2D = (Graphics2D)(bufferedImage = new BufferedImage(256, 64, 1)).getGraphics()).setColor(new Color(30, 31, 31));
        graphics2D.fillRect(0, 0, 256, 64);
        graphics2D.setColor(secondColor);

        int var6;
        int var7;
        for(int var23 = 0; var23 < 8; ++var23) {
            var6 = (int)(Math.random() * 64.0D / 2.0D);
            var7 = (int)(Math.random() * 256.0D - (double)var6);
            int var8 = (int)(Math.random() * 64.0D - (double)var6);
            graphics2D.drawOval(var7, var8, var6 << 1, var6 << 1);
        }

        graphics2D.setColor(firstColor);
        graphics2D.setFont(font);
        FontMetrics var24;
        var6 = (var24 = graphics2D.getFontMetrics()).getMaxAdvance();
        var7 = var24.getHeight();
        String var25 = "0123456789";
        char[] var26 = "0123456789".toCharArray();

        for(int var9 = 0; var9 < 5; ++var9) {
            int var10 = (int)Math.round(Math.random() * (double)(var26.length - 1));
            char var27 = var26[var10];
            this.finalString.append(var27);
            int var11 = var24.charWidth(var27);
            int var12;
            int var13 = (var12 = Math.max(var6, var7)) / 2;
            BufferedImage bufferedImage1;
            Graphics2D graphics2D1;
            (graphics2D1 = (bufferedImage1 = new BufferedImage(var12, var12, 2)).createGraphics()).translate(var13, var13);
            double var21 = (Math.random() - 0.5D) * 0.7D;
            graphics2D1.transform(AffineTransform.getRotateInstance(var21));
            graphics2D1.translate(-var13, -var13);
            graphics2D1.setColor(firstColor);
            graphics2D1.setFont(font);
            var11 = (int)(0.5D * (double)var12 - 0.5D * (double)var11);
            graphics2D1.drawString("" + var27, var11, (var12 - var24.getAscent()) / 2 + var24.getAscent());
            float var28 = 20.0F + 54.0F * (float)var9 - (float)var12 / 2.0F;
            var11 = (64 - var12) / 2;
            graphics2D.drawImage(bufferedImage1, (int)var28, var11, var12, var12, (Color)null, (ImageObserver)null);
            graphics2D1.dispose();
        }

        graphics2D.dispose();
        return bufferedImage;
    }

    
    public void captchaLogo(L2PcInstance player, int var2) {
        try {
            File file = new File("data/sunrise/images/captcha.png");
            ImageIO.write(getInstance().generateCaptcha(), "png", file);
            PledgeCrest pledgeCrest = new PledgeCrest(var2, DDSConverter.convertToDDS(file).array());
            player.sendPacket(pledgeCrest);
        } catch (Exception e) {
            if (Config.DEBUG) {
                e.printStackTrace();
            }

        }
    }

    protected static CaptchaImageGenerator instance;

    
    public static CaptchaImageGenerator getInstance() {
        if (instance == null)
            instance = new CaptchaImageGenerator();
        return instance;
    }
}
