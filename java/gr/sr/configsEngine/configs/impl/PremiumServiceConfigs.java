package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.HashMap;
import java.util.Map;


public class PremiumServiceConfigs extends AbstractConfigs {
    public static boolean USE_PREMIUM_SERVICE;
    public static float PREMIUM_RATE_XP;
    public static float PREMIUM_RATE_SP;
    public static Map<Integer, Float> PR_RATE_DROP_ITEMS_ID;
    public static float PREMIUM_RATE_DROP_ITEMS;
    public static boolean PR_ENABLE_MODIFY_SKILL_DURATION;
    public static Map<Integer, Integer> PR_SKILL_DURATION_LIST;
    public static boolean ALLOW_PREMIUM_CHAT;
    public static String PREMIUM_CHAT_PREFIX;
    public static String PREMIUM_NAME_PREFIX;
    public static int PREMIUM_MAX_SCHEME;

    public PremiumServiceConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/PremiumService.ini");
        USE_PREMIUM_SERVICE = Boolean.parseBoolean(this.getString(this._settings, this._override, "UsePremiumServices", "False"));
        PREMIUM_RATE_XP = Float.parseFloat(this.getString(this._settings, this._override, "PremiumRateXp", "2"));
        PREMIUM_RATE_SP = Float.parseFloat(this.getString(this._settings, this._override, "PremiumRateSp", "2"));
        PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(this.getString(this._settings, this._override, "PremiumRateDropItems", "2"));
        ALLOW_PREMIUM_CHAT = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowPremiumChat", "False"));
        PREMIUM_CHAT_PREFIX = this.getString(this._settings, this._override, "PremiumChatPrefix", "-");
        PREMIUM_NAME_PREFIX = this.getString(this._settings, this._override, "PremiumNamePrefix", "[PR]");
        String[] var1 = this.getString(this._settings, this._override, "PrRateDropItemsById", "").split(";");
        PR_RATE_DROP_ITEMS_ID = new HashMap(var1.length);
        int var3;
        if (!var1[0].isEmpty()) {
            int var2 = (var1 = var1).length;

            for(var3 = 0; var3 < var2; ++var3) {
                String var4;
                String[] var5;
                if ((var5 = (var4 = var1[var3]).split(",")).length != 2) {
                    _log.warn(this.getClass() + ": invalid config property -> PrRateDropItemsById \"", var4, "\"");
                } else {
                    try {
                        PR_RATE_DROP_ITEMS_ID.put(Integer.parseInt(var5[0]), Float.parseFloat(var5[1]));
                    } catch (NumberFormatException var7) {
                        if (!var4.isEmpty()) {
                            _log.warn(this.getClass() + ": invalid config property -> PrRateDropItemsById \"", var4, "\"");
                        }
                    }
                }
            }
        }

        if (!PR_RATE_DROP_ITEMS_ID.containsKey(57)) {
            PR_RATE_DROP_ITEMS_ID.put(57, PREMIUM_RATE_DROP_ITEMS);
        }

        if (PR_ENABLE_MODIFY_SKILL_DURATION = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnablePrModifySkillDuration", "false"))) {
            var1 = this.getString(this._settings, this._override, "PrSkillDurationList", "").split(";");
            PR_SKILL_DURATION_LIST = new HashMap(var1.length);
            String[] var8 = var1;
            var3 = var1.length;

            for(int var9 = 0; var9 < var3; ++var9) {
                String var10;
                if ((var1 = (var10 = var8[var9]).split(",")).length != 2) {
                    _log.warn(this.getClass() + ": invalid config property -> PrSkillDurationList \"", var10, "\"");
                } else {
                    try {
                        PR_SKILL_DURATION_LIST.put(Integer.parseInt(var1[0]), Integer.parseInt(var1[1]));
                    } catch (NumberFormatException var6) {
                        if (!var10.isEmpty()) {
                            _log.warn(this.getClass() + ": invalid config property ->  PrSkillDurationList \"" + var1[0], var1[1]);
                        }
                    }
                }
            }
        }

        PREMIUM_MAX_SCHEME = Integer.parseInt(this.getString(this._settings, this._override, "PremiumBufferScheme", "7"));
    }

    protected static PremiumServiceConfigs instance;

    public static PremiumServiceConfigs getInstance() {
        if (instance == null)
            instance = new PremiumServiceConfigs();
        return instance;
    }
}
