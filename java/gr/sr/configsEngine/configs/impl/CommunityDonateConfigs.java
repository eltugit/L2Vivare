package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class CommunityDonateConfigs extends AbstractConfigs {

    public static boolean COMMUNITY_DONATE_ALLOW;

    public static String BYPASS_COMMAND;

    public static boolean COMMUNITY_DONATE_NAME_CHANGE_ALLOW;

    public static boolean COMMUNITY_DONATE_NAME_CHANGE_NONPEACE;

    public static int COMMUNITY_DONATE_NAME_CHANGE_PRICE;

    public static int COMMUNITY_DONATE_NAME_CHANGE_ID;

    public static boolean COMMUNITY_DONATE_CLAN_NAME_CHANGE_ALLOW;

    public static boolean COMMUNITY_DONATE_CLAN_NAME_CHANGE_NONPEACE;

    public static int COMMUNITY_DONATE_CLAN_NAME_CHANGE_PRICE;

    public static int COMMUNITY_DONATE_CLAN_NAME_CHANGE_ID;

    public static boolean COMMUNITY_DONATE_FULL_CLAN_ALLOW;

    public static boolean COMMUNITY_DONATE_FULL_CLAN_NONPEACE;

    public static int COMMUNITY_DONATE_FULL_CLAN_PRICE;

    public static int COMMUNITY_DONATE_FULL_CLAN_ID;

    public static int COMMUNITY_DONATE_FULL_CLAN_REP_AMOUNT;

    public static boolean COMMUNITY_DONATE_REC_ALLOW;

    public static boolean COMMUNITY_DONATE_REC_NONPEACE;

    public static int COMMUNITY_DONATE_REC_PRICE;

    public static int COMMUNITY_DONATE_REC_ID;

    public static boolean COMMUNITY_DONATE_FAME_ALLOW;

    public static boolean COMMUNITY_DONATE_FAME_NONPEACE;

    public static int COMMUNITY_DONATE_FAME_PRICE;

    public static int COMMUNITY_DONATE_FAME_ID;

    public static int COMMUNITY_DONATE_FAME_AMOUNT;

    public static boolean COMMUNITY_DONATE_NOBLE_ALLOW;

    public static boolean COMMUNITY_DONATE_NOBLE_NONPEACE;

    public static int COMMUNITY_DONATE_NOBLE_PRICE;

    public static int COMMUNITY_DONATE_NOBLE_ID;

    public static boolean COMMUNITY_DONATE_PREMIUM_ALLOW;

    public static boolean COMMUNITY_DONATE_PREMIUM_NONPEACE;

    public static int COMMUNITY_DONATE_PREMIUM_PRICE_1_MONTH;

    public static int COMMUNITY_DONATE_PREMIUM_PRICE_2_MONTH;

    public static int COMMUNITY_DONATE_PREMIUM_PRICE_3_MONTH;

    public static int COMMUNITY_DONATE_PREMIUM_ID;

    public static boolean COMMUNITY_DONATE_AUGMENT_ALLOW;

    public static boolean COMMUNITY_DONATE_AUGMENT_NONPEACE;

    public static int COMMUNITY_DONATE_AUGMENT_PRICE;

    public static int COMMUNITY_DONATE_AUGMENT_ID;

    public static String COMMUNITY_DONATE_AUGMENT_SKILL;

    public static boolean COMMUNITY_DONATE_SHOP_ALLOW;

    public static boolean COMMUNITY_DONATE_SHOP_NONPEACE;

    public static List<Integer> MULTISELL_LIST;

    public CommunityDonateConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/CommunityDonate.ini");
        COMMUNITY_DONATE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowCommunityDonate", "false"));
        BYPASS_COMMAND = this.getString(this._settings, this._override, "BypassCommand", "_bbsloc");
        COMMUNITY_DONATE_NAME_CHANGE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeName", "false"));
        COMMUNITY_DONATE_NAME_CHANGE_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeNameNonPeace", "false"));
        COMMUNITY_DONATE_NAME_CHANGE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNamePrice", "57"));
        COMMUNITY_DONATE_NAME_CHANGE_ID = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNameCoin", "57"));
        COMMUNITY_DONATE_CLAN_NAME_CHANGE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeClanName", "false"));
        COMMUNITY_DONATE_CLAN_NAME_CHANGE_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowChangeClanNameNonPeace", "false"));
        COMMUNITY_DONATE_CLAN_NAME_CHANGE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNamePrice", "57"));
        COMMUNITY_DONATE_CLAN_NAME_CHANGE_ID = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNameCoin", "57"));
        COMMUNITY_DONATE_FULL_CLAN_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowFullClan", "false"));
        COMMUNITY_DONATE_FULL_CLAN_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowFullClanNonPeace", "false"));
        COMMUNITY_DONATE_FULL_CLAN_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "FullClanPrice", "57"));
        COMMUNITY_DONATE_FULL_CLAN_ID = Integer.parseInt(this.getString(this._settings, this._override, "FullClanCoin", "57"));
        COMMUNITY_DONATE_FULL_CLAN_REP_AMOUNT = Integer.parseInt(this.getString(this._settings, this._override, "FullClanRepAmount", "57"));
        COMMUNITY_DONATE_REC_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowRecommends", "false"));
        COMMUNITY_DONATE_REC_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowRecommendsNonPeace", "false"));
        COMMUNITY_DONATE_REC_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "RecommendsPrice", "57"));
        COMMUNITY_DONATE_REC_ID = Integer.parseInt(this.getString(this._settings, this._override, "RecommendsCoin", "57"));
        COMMUNITY_DONATE_FAME_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowFame", "false"));
        COMMUNITY_DONATE_FAME_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowFameNonPeace", "false"));
        COMMUNITY_DONATE_FAME_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "FamePrice", "57"));
        COMMUNITY_DONATE_FAME_ID = Integer.parseInt(this.getString(this._settings, this._override, "FameCoin", "57"));
        COMMUNITY_DONATE_FAME_AMOUNT = Integer.parseInt(this.getString(this._settings, this._override, "FameAmount", "57"));
        COMMUNITY_DONATE_NOBLE_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowNoble", "false"));
        COMMUNITY_DONATE_NOBLE_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowNobleNonPeace", "false"));
        COMMUNITY_DONATE_NOBLE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "NoblePrice", "57"));
        COMMUNITY_DONATE_NOBLE_ID = Integer.parseInt(this.getString(this._settings, this._override, "NobleCoin", "57"));
        COMMUNITY_DONATE_PREMIUM_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowPremium", "false"));
        COMMUNITY_DONATE_PREMIUM_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowPremiumNonPeace", "false"));
        COMMUNITY_DONATE_PREMIUM_PRICE_1_MONTH = Integer.parseInt(this.getString(this._settings, this._override, "PremiumPrice1Month", "57"));
        COMMUNITY_DONATE_PREMIUM_PRICE_2_MONTH = Integer.parseInt(this.getString(this._settings, this._override, "PremiumPrice2Month", "57"));
        COMMUNITY_DONATE_PREMIUM_PRICE_3_MONTH = Integer.parseInt(this.getString(this._settings, this._override, "PremiumPrice3Month", "57"));
        COMMUNITY_DONATE_PREMIUM_ID = Integer.parseInt(this.getString(this._settings, this._override, "PremiumCoin", "57"));
        COMMUNITY_DONATE_AUGMENT_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowAugment", "false"));
        COMMUNITY_DONATE_AUGMENT_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowAugmentNonPeace", "false"));
        COMMUNITY_DONATE_AUGMENT_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "AugmentPrice", "57"));
        COMMUNITY_DONATE_AUGMENT_ID = Integer.parseInt(this.getString(this._settings, this._override, "AugmentCoin", "57"));
        COMMUNITY_DONATE_AUGMENT_SKILL = this.getString(this._settings, this._override, "AugmentSkill", "Heal Empower,16279;Prayer,16280;Empower,16281;Magic Barrier,16282;Might,16283;Shield,16284;Duel Might,16285;Agility,16332;Focus,16333;Reflect Damage,16334;Guidance,16335;Wild Magic,16336;Refresh,16287;Spell Refresh,16301;Skill Refresh,16297;Celestial Shield,16293;Heal,16195;Stone,16184;Prominence,16186;Solar Flare,16192;Shadow Flare,16233;Hydro Blast,16236;Tempest,16237;Aura Flare,16205;Medusa,16324;Stun,16323;Paralyze,16321;Fear,16318;Sleep,16271;Hold,16257;Bleed,16252;Cheer,16262;Blessed Body,16263;Blessed Soul,16264");
        COMMUNITY_DONATE_SHOP_ALLOW = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowShop", "false"));
        COMMUNITY_DONATE_SHOP_NONPEACE = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowShopInNonPeace", "false"));
        String[] multisellList = this.getString(this._settings, this._override, "MultisellList", "1204;1035;1048").trim().split(";");
        MULTISELL_LIST = new ArrayList(multisellList.length);
        int multiSellLength = multisellList.length;

        for(int i = 0; i < multiSellLength; ++i) {
            String multisellId = multisellList[i];
            try {
                MULTISELL_LIST.add(Integer.parseInt(multisellId));
            } catch (NumberFormatException var6) {
                _log.warn(CommunityDonateConfigs.class.getSimpleName() + ": Wrong Multisell Id passed: " + multisellId);
                _log.warn(var6.getMessage());
            }
        }

    }

    protected static CommunityDonateConfigs instance;

    public static CommunityDonateConfigs getInstance() {
        if (instance == null)
            instance = new CommunityDonateConfigs();
        return instance;
    }
}
