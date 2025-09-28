package gr.sr.configsEngine.configs.impl;


import gr.sr.configsEngine.AbstractConfigs;

import java.util.ArrayList;
import java.util.List;


public class DonateManagerConfigs extends AbstractConfigs {
    public static int CHANGE_GENDER_DONATE_COIN;
    public static int CHANGE_GENDER_DONATE_PRICE;
    public static int CHANGE_NAME_COIN;
    public static int CHANGE_NAME_PRICE;
    public static int CHANGE_CNAME_COIN;
    public static int CHANGE_CNAME_PRICE;
    public static int REPUTATION_POINTS_TO_ADD;
    public static int[] CLAN_MAIN_SKILLS;
    public static int[] CLAN_SQUAD_SKILLS;
    public static int GET_FULL_CLAN_COIN;
    public static int GET_FULL_CLAN_PRICE;
    public static int AIO_EXCHANGE_ID;
    public static int AIO_EXCHANGE_PRICE;
    public static List<Integer> MULTISELL_LIST;

    public DonateManagerConfigs() {
    }

    public void loadConfigs() {
        this.loadFile("./config/sunrise/Donate.ini");
        CHANGE_GENDER_DONATE_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderDonateCoin", "40000"));
        CHANGE_GENDER_DONATE_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeGenderDonatePrice", "10"));
        CHANGE_NAME_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNameCoin", "40000"));
        CHANGE_NAME_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeNamePrice", "25000"));
        GET_FULL_CLAN_COIN = Integer.parseInt(this.getString(this._settings, this._override, "GetFullClanCoin", "40000"));
        GET_FULL_CLAN_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "GetFullClanPrice", "25"));
        CHANGE_CNAME_COIN = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNameCoin", "40000"));
        CHANGE_CNAME_PRICE = Integer.parseInt(this.getString(this._settings, this._override, "ChangeClanNamePrice", "8"));
        REPUTATION_POINTS_TO_ADD = Integer.parseInt(this.getString(this._settings, this._override, "ReputationPointsToAdd", "500000"));
        String[] clanMainSkills;
        CLAN_MAIN_SKILLS = new int[(clanMainSkills = this.getString(this._settings, this._override, "ClanMainSkills", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for (String clanSkill : clanMainSkills) {
                CLAN_MAIN_SKILLS[count++] = Integer.parseInt(clanSkill);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] clanSquadSkills;
        CLAN_SQUAD_SKILLS = new int[(clanSquadSkills = this.getString(this._settings, this._override, "ClanSquadSkills", "1204;1035;1048").trim().split(";")).length];

        try {
            int count = 0;
            for (String squadSkill : clanSquadSkills) {
                CLAN_SQUAD_SKILLS[count++] = Integer.parseInt(squadSkill);
            }
        } catch (NumberFormatException e) {
            _log.warn(e.getMessage(), e);
        }

        String[] multisellLists = this.getString(this._settings, this._override, "MultisellList", "1204;1035;1048").trim().split(";");
        MULTISELL_LIST = new ArrayList<>(multisellLists.length);

        for (String multisellId : multisellLists) {
            try {
                MULTISELL_LIST.add(Integer.parseInt(multisellId));
            } catch (NumberFormatException e) {
                _log.warn(DonateManagerConfigs.class.getSimpleName() + ": Wrong Multisell Id passed: " + multisellId);
                _log.warn(e.getMessage());
            }
        }

    }

    protected static DonateManagerConfigs instance;

    public static DonateManagerConfigs getInstance() {
        if (instance == null)
            instance = new DonateManagerConfigs();
        return instance;
    }
}
