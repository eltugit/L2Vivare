package gr.sr.pvpColorEngine;


import gr.sr.configsEngine.configs.impl.ColorSystemConfigs;
import gr.sr.interf.SunriseEvents;
import l2r.gameserver.model.actor.instance.L2PcInstance;


public class ColorSystemHandler {
    public ColorSystemHandler() {
    }

    
    public void updateColor(L2PcInstance player) {
        if (ColorSystemConfigs.ENABLE_COLOR_SYSTEM) {
            if (ColorSystemConfigs.ENABLE_NAME_COLOR) {
                getInstance().updateNameColor(player);
            }

            if (ColorSystemConfigs.ENABLE_TITLE_COLOR) {
                getInstance().updateTitleColor(player);
            }
        }
    }

    public void updateNameColor(L2PcInstance player) {
        if (!player.isGM() && !SunriseEvents.isInEvent(player)) {
            String depends = ColorSystemConfigs.COLOR_NAME_DEPENDS;
            switch (depends) {
                case "PK":
                    for (int color : ColorSystemConfigs.NAME_COLORS.keySet()) {
                        if (player.getPkKills() >= color) {
                            player.getAppearance().setNameColor((Integer) ColorSystemConfigs.NAME_COLORS.get(color));
                            player.broadcastUserInfo();
                        }
                    }
                    break;
                case "PVP":
                    for (int color : ColorSystemConfigs.NAME_COLORS.keySet()) {
                        if (player.getPvpKills() >= color) {
                            player.getAppearance().setNameColor((Integer) ColorSystemConfigs.NAME_COLORS.get(color));
                            player.broadcastUserInfo();
                        }
                    }
            }
        }
    }

    public void updateTitleColor(L2PcInstance player) {
        if (!player.isGM() && !SunriseEvents.isInEvent(player)) {
            String depends = ColorSystemConfigs.COLOR_TITLE_DEPENDS;
            byte var3 = -1;
            switch (depends) {
                case "PK":

                    for (int color : ColorSystemConfigs.TITLE_COLORS.keySet()) {
                        if (player.getPkKills() >= color) {
                            player.getAppearance().setTitleColor((Integer) ColorSystemConfigs.TITLE_COLORS.get(color));
                            player.broadcastUserInfo();
                        }
                    }
                    break;
                case "PVP":
                    for (int color : ColorSystemConfigs.TITLE_COLORS.keySet()) {
                        if (player.getPvpKills() >= color) {
                            player.getAppearance().setTitleColor((Integer) ColorSystemConfigs.TITLE_COLORS.get(color));
                            player.broadcastUserInfo();
                        }
                    }
            }
        }
    }

    protected static ColorSystemHandler instance;

    
    public static ColorSystemHandler getInstance() {
        if (instance == null)
            instance = new ColorSystemHandler();
        return instance;
    }
}
