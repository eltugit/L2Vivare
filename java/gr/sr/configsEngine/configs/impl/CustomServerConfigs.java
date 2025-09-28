package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;
import l2r.Config;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


public class CustomServerConfigs extends AbstractConfigs {
    public static boolean ALTERNATE_PAYMODE_SHOPS;
    public static boolean ALTERNATE_PAYMODE_MAILS;
    public static boolean ALTERNATE_PAYMODE_CLANHALLS;
    public static int ALTERNATE_PAYMENT_ID;
    public static int SHOP_MIN_RANGE_FROM_NPC;
    public static int SHOP_MIN_RANGE_FROM_PLAYER;
    public static boolean EXTRA_MESSAGES;
    public static boolean ANNOUNCE_HEROS_ON_LOGIN;
    public static boolean ALLOW_ONLINE_COMMAND;
    public static boolean ALLOW_REPAIR_COMMAND;
    public static boolean ALLOW_EXP_GAIN_COMMAND;
    public static boolean ALLOW_TELEPORTS_COMMAND;
    public static boolean GIVE_HELLBOUND_MAP;
    public static int TOP_LISTS_RELOAD_TIME;
    public static boolean AUTO_ACTIVATE_SHOTS;
    public static int AUTO_ACTIVATE_SHOTS_MIN;
    public static boolean PVP_SPREE_SYSTEM;
    public static boolean ALT_ALLOW_CLAN_LEADER_NAME;
    public static String CLAN_LEADER_NAME_COLOR;
    public static String CLAN_LEADER_TITLE_COLOR;
    public static int CLAN_LEVEL_ACTIVATION;
    public static boolean ANNOUNCE_CASTLE_LORDS;
    public static boolean ALT_ALLOW_REFINE_PVP_ITEM;
    public static boolean ALT_ALLOW_REFINE_HERO_ITEM;
    public static int MAX_REWARD_COUNT_FOR_STACK_ITEM1;
    public static int MAX_REWARD_COUNT_FOR_STACK_ITEM2;
    public static int DELAY_FOR_NEXT_REWARD;
    public static boolean VOTE_REWARD_HOPZONE_ENABLE;
    public static boolean VOTE_REWARD_TOPZONE_ENABLE;
    public static boolean EVENLY_DISTRIBUTED_ITEMS;
    public static boolean EVENLY_DISTRIBUTED_ITEMS_SEND_LIST;
    public static NpcHtmlMessage EVENLY_DISTRIBUTED_ITEMS_CACHED_HTML = null;
    public static boolean EVENLY_DISTRIBUTED_ITEMS_FORCED;
    public static boolean EVENLY_DISTRIBUTED_ITEMS_FOR_SPOIL_ENABLED;
    public static List<Integer> EVENLY_DISTRIBUTED_ITEMS_LIST;
    public static boolean ENABLE_SKILL_ENCHANT;
    public static boolean ENABLE_SKILL_MAX_ENCHANT_LIMIT;
    public static int SKILL_MAX_ENCHANT_LIMIT_LEVEL;
    public static boolean ENABLE_RUNE_BONUS;
    public static boolean ENABLE_CHARACTER_CONTROL_PANEL;
    public static boolean ENABLE_STARTING_TITLE;
    public static String STARTING_TITLE;
    public static boolean ANNOUNCE_DEATH_REVIVE_OF_RAIDS;
    public static int DUAL_BOX_IN_GAME;

    public CustomServerConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/Custom.ini");
        ALTERNATE_PAYMODE_SHOPS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AlternatePaymodeShops", "False"));
        ALTERNATE_PAYMODE_MAILS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AlternatePaymodeMails", "False"));
        ALTERNATE_PAYMODE_CLANHALLS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AlternatePaymodeClanHalls", "False"));
        ALTERNATE_PAYMENT_ID = Integer.parseInt(this.getString(this._settings, this._override, "AlternatePayment", "57"));
        SHOP_MIN_RANGE_FROM_PLAYER = Integer.parseInt(this.getString(this._settings, this._override, "ShopMinRangeFromPlayer", "0"));
        SHOP_MIN_RANGE_FROM_NPC = Integer.parseInt(this.getString(this._settings, this._override, "ShopMinRangeFromNpc", "0"));
        EXTRA_MESSAGES = Boolean.parseBoolean(this.getString(this._settings, this._override, "ExtraMessages", "false"));
        ANNOUNCE_HEROS_ON_LOGIN = Boolean.parseBoolean(this.getString(this._settings, this._override, "AnnounceHerosOnLogin", "false"));
        ALLOW_ONLINE_COMMAND = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowOnlineCommand", "False"));
        ALLOW_REPAIR_COMMAND = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowRepairCommand", "False"));
        ALLOW_EXP_GAIN_COMMAND = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowExpGainCommand", "False"));
        ALLOW_TELEPORTS_COMMAND = Boolean.parseBoolean(this.getString(this._settings, this._override, "AllowTeleportsCommand", "False"));
        GIVE_HELLBOUND_MAP = Boolean.parseBoolean(this.getString(this._settings, this._override, "GiveHellboundMap", "False"));
        TOP_LISTS_RELOAD_TIME = Integer.parseInt(this.getString(this._settings, this._override, "TopListsReloadTime", "20"));
        AUTO_ACTIVATE_SHOTS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AutoActivateShotsEnabled", "False"));
        AUTO_ACTIVATE_SHOTS_MIN = Integer.parseInt(this.getString(this._settings, this._override, "AutoActivateShotsMin", "200"));
        PVP_SPREE_SYSTEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "PvpSpreeSystem", "False"));
        ALT_ALLOW_CLAN_LEADER_NAME = Boolean.parseBoolean(this.getString(this._settings, this._override, "AltAllowClanLeaderName", "false"));
        CLAN_LEADER_NAME_COLOR = this.getString(this._settings, this._override, "ClanLeaderNameColor", "FFFF00");
        CLAN_LEADER_TITLE_COLOR = this.getString(this._settings, this._override, "ClanLeaderTitleColor", "FFFF00");
        CLAN_LEVEL_ACTIVATION = Integer.parseInt(this.getString(this._settings, this._override, "ClanLevelActivation", "0"));
        ANNOUNCE_CASTLE_LORDS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AnnounceCastleLords", "false"));
        ALT_ALLOW_REFINE_PVP_ITEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "AltAllowRefinePVPItem", "False"));
        ALT_ALLOW_REFINE_HERO_ITEM = Boolean.parseBoolean(this.getString(this._settings, this._override, "AltAllowRefineHEROItem", "False"));
        ENABLE_SKILL_ENCHANT = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableSkillEnchant", "True"));
        ENABLE_SKILL_MAX_ENCHANT_LIMIT = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableSkillMaxEnchantLimit", "False"));
        SKILL_MAX_ENCHANT_LIMIT_LEVEL = Integer.parseInt(this.getString(this._settings, this._override, "SkillMaxEnchantLimitLevel", "30"));
        ENABLE_RUNE_BONUS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableBonusManager", "False"));
        ENABLE_CHARACTER_CONTROL_PANEL = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableCharacterControlPanel", "False"));
        ENABLE_STARTING_TITLE = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableStartingTitle", "False"));
        STARTING_TITLE = this.getString(this._settings, this._override, "StartingTitle", "L2sunrise");
        EVENLY_DISTRIBUTED_ITEMS = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableEvenlyDistribution", "False"));
        EVENLY_DISTRIBUTED_ITEMS_SEND_LIST = Boolean.parseBoolean(this.getString(this._settings, this._override, "SendEvenlyDistributionList", "True"));
        EVENLY_DISTRIBUTED_ITEMS_FORCED = Boolean.parseBoolean(this.getString(this._settings, this._override, "ForceEvenlyDistribution", "False"));
        EVENLY_DISTRIBUTED_ITEMS_FOR_SPOIL_ENABLED = Boolean.parseBoolean(this.getString(this._settings, this._override, "EnableEvenlyDistributionForSpoil", "False"));
        if (EVENLY_DISTRIBUTED_ITEMS) {
            String[] itemsToEvenlyDistributes = this.getString(this._settings, this._override, "ItemsToEvenlyDistribute", "").split(";");
            EVENLY_DISTRIBUTED_ITEMS_LIST = new ArrayList<>(itemsToEvenlyDistributes.length);

            for (String itemsId : itemsToEvenlyDistributes) {
                try {
                    EVENLY_DISTRIBUTED_ITEMS_LIST.add(Integer.parseInt(itemsId.trim()));
                } catch (NumberFormatException var29) {
                    _log.info("Loot Mod: Error parsing item id. Skiping " + itemsId + ".");
                }
            }

            if (EVENLY_DISTRIBUTED_ITEMS_SEND_LIST) {
                StringBuilder sb = StringUtil.startAppend(1000, "<table>");
                ItemData itemData = ItemData.getInstance();
                for (int itemID : EVENLY_DISTRIBUTED_ITEMS_LIST) {
                    L2Item item = itemData.getTemplate(itemID);
                    StringUtil.append(sb, "<tr><td>", "<img src=\"" + item.getIcon() + "\" height=\"32\" width=\"32\" /></td><td>", item.getName(), "</td></tr>");
                }

                StringUtil.append(sb, "</table>");
                String html = "" ;

                try{
                    File file = new File(Config.DATAPACK_ROOT, "data/html/mods/EvenlyDistributeItems.htm");

                    FileInputStream fis = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);
                    byte[] bytes = new byte[bufferedInputStream.available()];
                    bufferedInputStream.read(bytes);
                    html = (new String(bytes, "UTF-8")).replaceAll("\r\n", "\n");

                    fis.close();
                } catch (Exception var33) {
                    _log.warn("EXTRA: Evenly distributed error: " + var33.getMessage());
                }

                if (!html.equals("")) {
                    (EVENLY_DISTRIBUTED_ITEMS_CACHED_HTML = new NpcHtmlMessage()).setHtml(html);
                    EVENLY_DISTRIBUTED_ITEMS_CACHED_HTML.replace("%list%", sb.toString());
                }
            }
        }

        ANNOUNCE_DEATH_REVIVE_OF_RAIDS = Boolean.parseBoolean(this.getString(this._settings, this._override, "AnnounceDeathAndReviveOfRaids", "False"));
        DUAL_BOX_IN_GAME = Integer.parseInt(this.getString(this._settings, this._override, "DualBoxesInGame", "0"));
    }

    protected static CustomServerConfigs instance;

    public static CustomServerConfigs getInstance() {
        if (instance == null)
            instance = new CustomServerConfigs();
        return instance;
    }
}
