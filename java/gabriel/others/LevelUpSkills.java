package gabriel.others;


import gabriel.config.GabConfig;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.Shortcut;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;


public class LevelUpSkills {

    private static int getSkillId(L2PcInstance player, int skill) {
        return player.getSkillLevel(skill);
    }


    public static void levelUpSkills(L2PcInstance player) {
        L2Skill skill;
        Shortcut shortcut;
        int skillid;
        //Deadnought
        if (player.getClassId().getId() == 89) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(216, 230);//Plearm Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(227, 130);//Light Armor Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(231, 130);//Heavy Armor Mastery  power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(36, 730);//Whirlwind Duel
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(48, 130);//Thunder Storm Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(116, 130);//Howl power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(290, 130);//Final Frenzy power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(211, 130);//Boost HP power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(320, 130);//Wrath power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(421, 130);//Fell Swoop power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(422, 130);//Polearm Accuracy power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(359, 115);//Eye Of Hunter power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(361, 315);//Shock Blast Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(339, 115);//Parry Stance power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(347, 115);//Earthquake power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(360, 115);//Eye of Slayer power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(440, 115);//Braveheart power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(100, 130);//Stun Attack power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(452, 130);//Shock Stomp power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(121, 230);//Battle Roar Attack
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(130, 130);//Thrill Fight Cost
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy Cost
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 286;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 995;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 994;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 48;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 452;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 361;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 347;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 116;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 440;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 181;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 920;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 921;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 36;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 320;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 421;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 78;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 130;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 917;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 287;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 121;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 422;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 317;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 339;
                shortcut = new Shortcut(9, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Duelist
        else if (player.getClassId().getId() == 88) {

            skill = SkillData.getInstance().getInfo(227, 130);//Light Armor Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(231, 130);//Heavy Armor Mastery  power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(144, 130);//Dual Weapon Mastery  power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy Cost
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(290, 130);//Final Frenzy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(360, 115);//Eye of Slayer power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(359, 115);//Eye Of Hunter power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(440, 115);//Braveheart power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(100, 130);//Stun Attack power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(297, 230);//Duelist Spirit 230
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1, 730);//Tripple slash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(260, 130);//Hammer Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(261, 730);//Triple Sonic Slash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(5, 730);//Double Sonic Slash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(6, 730);//Sonic Blaster
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(7, 730);//Sonic Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(9, 730);//Sonic Buster
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(190, 130);//Fatal Strike
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 345;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 995;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 994;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 7;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 6;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 5;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 261;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 9;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 8;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 919;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 440;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 775;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 451;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 297;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 442;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 78;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 917;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 121;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 287;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 340;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Phoenix Knight
        else if (player.getClassId().getId() == 90) {

            skill = SkillData.getInstance().getInfo(232, 230);//Heavy Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(217, 130);//Sword Blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(153, 130);//Shield Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(429, 115);//Knighthood
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(291, 130);//Final Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(18, 130);//Aura Hate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(28, 230);//Aggression
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(262, 130);//Divine Blessing
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(49, 130);//Divine Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(69, 230);//Sacrifice
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(528, 115);//Shield of Faith
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(72, 230);//Iron Will
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(406, 430);//Angelic Icon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(82, 230);//Majesty
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(341, 215);//Touch of Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(110, 330);//Ultimate Defence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(112, 230);//Deflect Arrow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(368, 215);//Vengeance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(400, 230);//Tribunal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(403, 230);//Shacke
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(404, 230);//Mass Shackle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(405, 230);//Banish Undead
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(92, 130);//Shield Stun
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(97, 130);//Sanctuary
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(353, 115);//Shield Slam
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(318, 130);//Aegis Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(322, 130);//Shield Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 28;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 18;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 984;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 985;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 92;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 353;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 400;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 403;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 404;
                shortcut = new Shortcut(9, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 97;
                shortcut = new Shortcut(10, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 262;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 69;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 49;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 341;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 527;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 913;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 82;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 982;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 72;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 350;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 912;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 784;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 785;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 406;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 438;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 916;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 528;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 368;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 760;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 197;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 196;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 322;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 318;
                shortcut = new Shortcut(9, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Hell Knight
        else if (player.getClassId().getId() == 91) {

            skill = SkillData.getInstance().getInfo(232, 230);//Heavy Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(217, 130);//Sword Blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(153, 130);//Shield Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(429, 115);//Knighthood
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(291, 130);//Final Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(18, 130);//Aura Hate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(28, 230);//Aggression
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(283, 130);//Summon Dark Panther
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(46, 130);//Life Scavenge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(70, 330);//Drain Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(528, 115);//Shield of Faith
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(72, 230);//Iron Will
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(82, 230);//Majesty
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(86, 230);//Reflect Damage
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(110, 330);//Ultimate Defence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(112, 230);//Deflect Arrow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(368, 215);//Vengeance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(401, 130);//Judgment
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(403, 230);//Shacke
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(450, 230);//Banish Seraph
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(342, 215);//Touch Of Death
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(92, 130);//Shield Stun
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(127, 130);//Hamstring
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(353, 115);//Shield Slam
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(318, 130);//Aegis Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(322, 130);//Shield Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 28;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 18;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 984;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 985;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 92;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 353;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 401;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 762;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 763;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 342;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 403;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 127;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 318;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 322;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 283;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 528;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 368;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 760;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 761;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 527;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 913;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 439;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 350;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 86;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 982;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 82;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 72;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Adventurer
        else if (player.getClassId().getId() == 93) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(209, 330);//Dagger Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(168, 130);//Boost Attack Speed
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(263, 730);//Deadly Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(30, 730);//Backstab
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(198, 130);//Boost Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(432, 115);//Assassination
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(409, 730);//Critical Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(344, 715);//Lethal Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(411, 230);//Stealth
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(356, 115);//Focus Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(357, 115);//Focus Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(111, 130);//Ultimate Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(412, 230);//Sand Bomb
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(51, 130);//Lure
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(96, 230);//Bleed
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(358, 115);//Bluff
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(221, 130);//Silent Move
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 821;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 358;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 30;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 928;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 409;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 344;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 263;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 93;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 991;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 531;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 51;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 412;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 453;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 445;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 768;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 356;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 357;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 922;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Sagitarius
        else if (player.getClassId().getId() == 92) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(208, 130);//Bow Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(431, 115);//Archery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(19, 730);//Double Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(24, 130);//Burst Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(417, 130);//Pain of Sagitarius
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(343, 715);//Lethal Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(101, 130);//Stun Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(131, 130);//HawkEye
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(303, 130);//Soul of Sagitarius
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(313, 130);//Snipe
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(99, 130);//Rapid Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(354, 215);//Hamstring Shot
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                skillid = 101;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 343;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 924;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 771;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 354;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 4;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 987;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 990;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 19;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 131;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 313;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 99;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 415;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 416;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 303;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 111;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //ArchMage
        else if (player.getClassId().getId() == 94) {

            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(285, 130);//Higher Mana Gain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(433, 115);//Arcana Roar
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1288, 330);//Aura Symphony
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1289, 330);//Inferno
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1417, 230);//Aura Flash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1292, 330);//Elemental Assault
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1296, 130);//Rain of Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1171, 130);//Blazing Circle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1451, 115);//Fire Vortex Buster
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1452, 115);//Count of Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1339, 315);//Fire Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1230, 430);//Prominence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1231, 130);//Aura Flare
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1275, 330);//Aura Bolt
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1232, 330);//Blazking Skin
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1078, 130);//Concentration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1160, 130);//Slow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1169, 230);//Curse Fear
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1072, 130);//Sleeping Cloud
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1074, 130);//Surrender to wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1338, 115);//Arcane Chaos
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(337, 115);//Arcane POower
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1083, 130);//Surrender to Fire
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1083;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1230;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1554;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1555;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1339;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1451;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1171;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1169;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1056;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1467;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1452;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1289;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1288;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1292;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1296;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1157;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1417;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1556;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1232;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1285;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1492;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 337;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1532;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Soultaker
        else if (player.getClassId().getId() == 95) {

            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(285, 130);//Higher Mana Gain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(434, 115);//Necromancy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1154, 130);//Summon Corrupted Man
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1155, 130);//Corpse Burst
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1157, 230);//Body To Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1159, 130);//Curse Death Link
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1334, 130);//Summon CUrsed Man
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1343, 315);//Dark Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1234, 330);//Vampiric Claw
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1129, 130);//Summon Reanimated Man
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1148, 130);//Death Spike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1151, 130);//Corpse Life Drain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1164, 130);//Curse Weakness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1167, 330);//Poisonous Cloud
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1169, 230);//Curse Fear
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1298, 130);//Mass Slow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1170, 130);//Anchor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1064, 130);//Silence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1336, 115);//Curse of Doom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1337, 115);//Curse of Abyss
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1222, 130);//Curse Chaos
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1381, 230);//Mass Fear
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1382, 230);//Mass Gloom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1263, 130);//Curse Gloom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1269, 130);//Curse Disease
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(337, 115);//Arcane POower
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1262, 130);//Transfer Pain
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1263;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1148;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1234;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1343;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1170;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1269;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1337;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1336;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1064;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1344;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1345;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1169;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1381;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1382;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1129;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1154;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1334;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1495;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1467;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1532;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 337;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1262;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Arcana Lord
        else if (player.getClassId().getId() == 96) {
            skill = SkillData.getInstance().getInfo(258, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(435, 115);//Summon Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(10, 130);//Summon Storm Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1328, 130);//Mass Summon Storm Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1331, 130);//Summon Feline Queen
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1225, 130);//Summon Mew the Cat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1111, 130);//Summon Kat the Cat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1126, 130);//Servitor Recharge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1127, 130);//Servitor Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1276, 130);//Summon Kai the Cat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1406, 115);//Summon Feline King
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1279, 130);//Summon Binding Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1299, 230);//Servitor Empowerment
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1346, 115);//Warrior Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1349, 115);//Final Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1139, 130);//Servitor Magic Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1140, 130);//Servitor Physical Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1141, 130);//Servitor Haste
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1144, 130);//Servitor Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1383, 330);//Mass Surrender to Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1386, 330);//Arcane Disruption
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(338, 215);//Arcane Agility
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1262, 130);//Transfer Pain
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1558;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1127;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1126;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1351;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1350;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1380;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1383;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1386;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1083;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1331;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1276;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1406;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1547;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1299;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1557;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1346;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 338;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1262;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 10;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1328;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1279;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 781;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Cardinal
        else if (player.getClassId().getId() == 97) {
            skill = SkillData.getInstance().getInfo(236, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(235, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1028, 130);//Might of Heaven
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1311, 130);//Body of Avatar
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1217, 130);//Greater Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1218, 130);//Greater Battle Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1219, 130);//Greater Group Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1254, 130);//Mass Resurrection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1258, 130);//Restore Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1395, 130);//Erase
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1398, 130);//Mana Burn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1271, 230);//Benediction
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1399, 130);//Mana Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1401, 130);//Major Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1402, 130);//Major Group Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1020, 130);//Vitalize
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1043, 130);//Holy Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1307, 130);//Prayer
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1459, 115);//Divine Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1204, 130);//Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1353, 115);//Divine Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1034, 130);//Repose
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1042, 130);//Hold Undead
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1360, 115);//Mass Block Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1361, 115);//Mass Block Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1394, 230);//Trance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1396, 230);//Magical BackFire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1400, 130);//Turn Undead
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(336, 115);//Arcane Wisdom
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1553;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1335;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1409;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1218;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1402;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1401;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1258;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1399;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1398;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1396;
                shortcut = new Shortcut(10, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1394;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1254;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1016;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1311;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1219;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1217;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1360;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1361;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1418;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1271;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1426;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1410;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1425;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1505;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1459;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Hierophant
        else if (player.getClassId().getId() == 98) {
            skill = SkillData.getInstance().getInfo(236, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(235, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(259, 130);//Heavy Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1395, 130);//Erase
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1398, 130);//Mana Burn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1032, 330);//Invigor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1033, 330);//Resist Poison
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1035, 230);//Mental Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1548, 330);//Resist Earth
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1036, 230);//Magic Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1040, 230);//Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1043, 230);//Holy Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1044, 230);//Regeneration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1045, 230);//Bless the Body
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1048, 230);//Bless the Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1182, 330);//Resist Aqua
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1189, 330);//Resist Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1062, 230);//Berserker Spirit
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1191, 330);//Resist Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1068, 230);//Might
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1073, 230);//Kiss of Eva
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1204, 230);//Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1077, 230);//Focus
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1078, 230);//Concentration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1085, 230);//Acumen
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1086, 230);//Haste
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1352, 215);//Elemental Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1356, 315);//Prophecy of Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1240, 230);//Guidance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1242, 230);//Death Whisper
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1388, 130);//Greater Might
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1389, 130);//Greater Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1392, 130);//Resist Holy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1393, 130);//Resist Dark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1201, 130);//Dryad Root
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1358, 115);//Block Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1359, 115);//Block Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(336, 115);//Arcane Wisdom
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)


                skillid = 1358;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1359;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1015;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1398;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1016;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1411;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1540;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 336;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Eva's Templar
        else if (player.getClassId().getId() == 99) {

            skill = SkillData.getInstance().getInfo(232, 230);//Heavy Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(217, 130);//Sword Blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(153, 130);//Shield Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(429, 115);//Knighthood
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(291, 130);//Final Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(18, 130);//Aura Hate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(28, 230);//Aggression
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(528, 115);//Shield of Faith
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(341, 215);//Touch of Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(110, 330);//Ultimate Defence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(112, 230);//Deflect Arrow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(368, 215);//Vengeance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(123, 230);//Spirit Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(230, 430);//Sprint
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(400, 230);//Tribunal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(402, 230);//Arrest
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(352, 115);//Shield Bash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(102, 130);//Entangle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(107, 130);//Divine Aura
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(288, 130);//Guard Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(322, 130);//Shield Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(10, 130);//Summon Storm Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(58, 130);//Elemental Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(449, 130);//Summon Attractive Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(67, 130);//Summon Life Cubic
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 28;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 18;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 985;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 984;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 352;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 402;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 400;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 102;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 58;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 786;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 787;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 341;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 527;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 913;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 351;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 982;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 916;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 528;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 368;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 760;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 10;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 779;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 449;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 67;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 322;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 288;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 197;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }

        }
        //Sword Muse
        else if (player.getClassId().getId() == 100) {
            skill = SkillData.getInstance().getInfo(217, 130);//Sword Blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(428, 115);//Inner Rhythm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(437, 115);//Song of SIlence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(58, 130);//Elemental Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(264, 230);//Song of Earth
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(265, 230);//Song of Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(266, 230);//Song of Water
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(267, 230);//Song of Warding
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(268, 230);//Song of Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(269, 230);//Song of Hunter
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(270, 230);//Song of Invocation
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(529, 215);//Song of Elemental
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(304, 230);//Song of Vitality
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(305, 230);//Song of Vengeance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(306, 115);//Song of Flame GUard
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(308, 130);//Song of Storm Guard
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(349, 215);//Song of Renewal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(230, 430);//Sprint
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(364, 215);//Song of Champion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(363, 215);//Song of Meditation
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(123, 230);//Spirit Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(764, 130);//Song of Wind Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(402, 230);//Arrest
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(102, 130);//Entangle
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 58;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 28;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 437;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 402;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 102;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 455;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 913;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 764;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 988;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 196;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Wind Rider
        else if (player.getClassId().getId() == 101) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(209, 330);//Dagger Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(198, 130);//Boost Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(432, 115);//Assassination
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(137, 130);//Critical Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(221, 130);//Silent Move
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(58, 130);//Elemental Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(263, 730);//Deadly Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(30, 730);//Backstab
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(321, 130);//Blinding Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(344, 715);//Lethal Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(356, 115);//Focus Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(355, 115);//Focus Death
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(410, 130);//Mortal Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(111, 130);//Ultimate Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(123, 230);//Spirit Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(412, 230);//Sand Bomb
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(51, 130);//Lure
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(96, 230);//Bleed
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(358, 115);//Bluff
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(102, 130);//Entangle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(230, 430);//Sprint
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 821;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 358;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 30;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 928;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 321;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 344;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 991;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 263;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 531;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 58;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 355;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 356;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 410;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 111;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 769;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 922;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 446;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Moonlight Sentinel
        else if (player.getClassId().getId() == 102) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(208, 130);//Bow Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(431, 115);//Archery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(19, 730);//Double Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(24, 130);//Burst Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(343, 715);//Lethal Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(101, 130);//Stun Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(369, 215);//Evade Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(58, 130);//Elemental Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(303, 130);//Soul of Sagitarius
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(99, 130);//Rapid Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(413, 130);//Rapid Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(354, 215);//Hamstring Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(230, 430);//Sprint
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(123, 230);//Spirit Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(102, 130);//Entangle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 101;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 369;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 354;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 343;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 19;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 990;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 987;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 924;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 772;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 111;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 413;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 99;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 303;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 415;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 416;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Mystic Muse
        else if (player.getClassId().getId() == 103) {
            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(285, 130);//Higher Mana Gain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(433, 115);//Arcana Roar
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1288, 330);//Aura Symphony
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1290, 330);//Blizzard
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1417, 230);//Aura Flash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1293, 330);//Elemental Symphony
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1295, 130);//Aqua Flash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1174, 130);//Frost Walll
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1453, 115);//Ice Vortex Crusher
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1455, 115);//Throne of Ice
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1340, 315);//Ice Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1342, 315);//Light Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1235, 430);//Prominence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1231, 130);//Aura Flare
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1275, 330);//Aura Bolt
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1265, 130);//Solar Flare
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1238, 330);//Freezing Skin
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1047, 130);//Mana Regeneration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1182, 330);//Resist Aqua
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1071, 130);//Surrender to Water
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1164, 130);//Curse Weakness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1169, 230);//Curse Fear
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1072, 130);//Sleeping Cloud
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1454, 115);//Diamond DUst
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1223, 130);//Surrender to Earth
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1236, 330);//Frost Bolt
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1237, 330);//Ice Dagger
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1338, 115);//Arcane Chaos
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(337, 115);//Arcane POower
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1071;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1256;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1235;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1554;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1555;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1340;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1453;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1342;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1231;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1056;
                shortcut = new Shortcut(10, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1169;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1293;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1290;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1288;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1455;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1236;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1237;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1183;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1338;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1532;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1238;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1556;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1493;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1286;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 337;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Elemental Master
        else if (player.getClassId().getId() == 104) {
            skill = SkillData.getInstance().getInfo(258, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(435, 115);//Summon Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1280, 130);//Summon Aqua Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1329, 130);//Mass Summon Aqua Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1332, 130);//Summon Seraphim the unicorn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1226, 130);//Summon Boxer the unicorn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1227, 130);//Summon Mirage the unicorn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1126, 130);//Servitor Recharge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1127, 130);//Servitor Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1277, 130);//Summon Merrow the unicorn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1407, 115);//Summon Magnus the unicorn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(67, 130);//Summon life Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1299, 230);//Servitor Empowerment
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1347, 115);//Wizard Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1349, 115);//Final Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1139, 130);//Servitor Magic Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1140, 130);//Servitor Physical Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1141, 130);//Servitor Haste
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1145, 130);//Bright Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1384, 330);//Mass Surrender to Water
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1206, 130);//Wind Shakle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(338, 215);//Arcane Agility
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1262, 130);//Transfer Pain
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1558;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1127;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1126;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1384;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1350;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1223;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1380;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1332;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1277;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1407;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1547;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1557;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1349;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1349;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1347;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1262;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 338;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 782;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1280;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 67;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1329;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1403;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Eva's Saint
        else if (player.getClassId().getId() == 105) {
            skill = SkillData.getInstance().getInfo(236, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(235, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1028, 130);//Might of Heaven
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1217, 130);//Greater Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1219, 130);//Greater Group Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1013, 130);//Recharge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1395, 130);//Erase
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1398, 130);//Mana Burn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1020, 230);//Vitalize
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1401, 130);//Major Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1043, 130);//Holy Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1033, 130);//Resist Poison
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1035, 130);//Mental Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1040, 130);//Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1044, 130);//Regeneration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1303, 130);//Wild Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1304, 130);//Advanced Block
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1068, 130);//Might
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1073, 130);//Kiss of Eva
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1204, 130);//Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1353, 115);//Divine Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1354, 115);//Arcane Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1355, 315);//Prophecy of Water
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1243, 130);//Bless SHield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1257, 130);//Decrease Weight
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1259, 330);//Resist Shock
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1393, 130);//Resist Dark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1397, 130);//Clarity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1359, 115);//Block Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1394, 230);//Trance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1400, 130);//Turn Undead
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(336, 115);//Arcane Wisdom
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1553;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1401;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1217;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1219;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1359;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1394;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1016;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1550;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1020;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1552;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1506;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1397;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1259;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1013;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1540;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 336;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1255;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Shillen Templar
        else if (player.getClassId().getId() == 106) {
            skill = SkillData.getInstance().getInfo(232, 230);//Heavy Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(217, 130);//Sword Blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(153, 130);//Shield Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(429, 115);//Knighthood
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(291, 130);//Final Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(18, 130);//Aura Hate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(28, 230);//Aggression
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(223, 130);//Sting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(278, 130);//Summon Viper Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(22, 130);//Summon Vampiric Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(33, 130);//Summon Phantom Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(289, 330);//Life Leech
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(70, 330);//Drain Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(528, 115);//Shield of Faith
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(110, 330);//Ultimate Defence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(112, 230);//Deflect Arrow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(368, 215);//Vengeance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(401, 230);//Tribunal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(402, 230);//Arrest
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(352, 115);//Shield Bash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(279, 330);//Lightning Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(450, 230);//Banish Seraph
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(342, 215);//Touch of Death
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(105, 330);//Freezing Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(115, 130);//Power Break
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(122, 130);//Hex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(288, 130);//Guard Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(322, 130);//Shield Fortress
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 28;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 18;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 985;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 984;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 352;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 401;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 342;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 402;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 122;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 115;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 279;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 105;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 913;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 527;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 351;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 98;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 728;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 729;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 916;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 528;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 368;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 760;
                shortcut = new Shortcut(7, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 278;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 780;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 22;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 33;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 322;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 288;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Spectral Dancer
        else if (player.getClassId().getId() == 107) {
            skill = SkillData.getInstance().getInfo(144, 130);//Dual Weapon Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 215);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(147, 130);//M. Def
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(191, 130);//Focus Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(428, 115);//Inner Rhythm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(223, 130);//Sting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(70, 330);//Drain Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(367, 115);//Dance of Medusa
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(271, 230);//Dance of the Warrior
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(272, 230);//Dance of Inspiration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(273, 230);//Dance of the Mystic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(274, 230);//Dance of Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(530, 215);//Dance of Alignment
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(275, 230);//Dance of Fury
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(276, 230);//Dance of Concentration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(277, 230);//Dance of Light
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(307, 230);//Dance of Aqua Guard
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(309, 230);//Dance of Earth Guard 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(310, 230);//Dance of the Vampire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(311, 230);//Dance of Protection 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(365, 215);//Dance of Siren
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(366, 215);//Dance of Shadow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(765, 130);//Dance of Blade Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(402, 230);//Arrest
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(84, 130);//Poison Blade Dance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(105, 330);//Freezing Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(115, 130);//Power Break
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(122, 130);//Hex
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {

                skillid = 367;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 223;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 115;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 122;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 455;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 402;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 105;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 986;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 84;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 110;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 112;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 989;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 765;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));
            }
        }

        //Ghost Hunter
        else if (player.getClassId().getId() == 108) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(209, 330);//Dagger Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(198, 130);//Boost Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(432, 115);//Assassination
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(137, 130);//Critical Chance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(263, 730);//Deadly Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(30, 730);//Backstab
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(321, 130);//Blinding Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(344, 715);//Lethal Blow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(223, 130);//Sting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(70, 330);//Drain Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(357, 115);//Focus Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(355, 115);//Focus Death
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(410, 130);//Mortal Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(111, 130);//Ultimate Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(412, 230);//Sand Bomb
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(51, 130);//Lure
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(96, 230);//Bleed
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(358, 115);//Bluff
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(105, 330);//Freezing Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(115, 130);//Power Break
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(122, 130);//Hex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(221, 130);//Silent Move
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 821;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 30;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 358;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 928;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 321;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 263;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 344;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 991;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 96;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 122;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 115;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 51;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 105;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 453;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 355;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 357;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 410;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 111;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 922;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 770;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 447;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 312;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Ghost Sentinel
        else if (player.getClassId().getId() == 109) {
            skill = SkillData.getInstance().getInfo(233, 230);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(208, 130);//Bow Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(169, 130);//Quick Step
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(431, 115);//Archery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(19, 730);//Double Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(417, 130);//Pain of Sagitarius
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(314, 730);//Fatal Counter
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(343, 715);//Lethal Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(223, 130);//Sting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(101, 130);//Stun Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(369, 215);//Evade Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(70, 330);//Drain Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(303, 130);//Soul of Sagitarius
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(99, 130);//Rapid Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(414, 130);//Dead Eye
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(354, 215);//Hamstring Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(105, 330);//Freezing Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(115, 130);//Power Break
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(122, 130);//Hex		
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 101;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 369;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 924;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 343;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 987;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 354;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 19;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 314;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 990;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 773;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 122;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 115;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 105;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 99;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 414;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 415;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 303;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 111;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 334;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }

        //Storm Screamer
        else if (player.getClassId().getId() == 110) {
            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(285, 130);//Higher Mana Gain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(433, 115);//Arcana Roar
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1148, 130);//Death Spike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1157, 230);//Body To Mind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1159, 130);//Curse Death Link
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1288, 330);//Aura Symphony
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1291, 330);//Demon Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1417, 230);//Aura Flash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1294, 330);//Elemental Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1176, 130);//Tempest
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1456, 115);//Wind Vortex Slug
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1458, 115);//Throne of Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1341, 315);//Wind Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1343, 315);//Dark Vortex
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1239, 330);//Hurricane
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1234, 330);//Vampiric Claw
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1267, 330);//Shadow Flare
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1151, 130);//Corpse Life Drain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1457, 215);//Empowering Echo
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1160, 130);//Slow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1167, 330);//Poisonous Cloud
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1169, 230);//Curse Fear
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1064, 130);//Silence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1074, 130);//Surrender to Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1069, 130);//Sleep
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1338, 115);//Arcane Chaos
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1222, 130);//Curse Chaos
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1224, 130);//Surrender To poison
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(337, 115);//Arcane Power
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1074;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1239;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1555;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1554;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1341;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1456;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1468;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1343;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1159;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1234;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1148;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1291;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1294;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1288;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1458;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1064;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1169;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1556;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1457;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1287;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1494;
                shortcut = new Shortcut(7, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 337;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1532;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Spectral Master
        else if (player.getClassId().getId() == 111) {
            skill = SkillData.getInstance().getInfo(258, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(234, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(435, 115);//Summon Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(33, 130);//Summon Phantom Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1330, 130);//Mass Summon Phantom Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1128, 130);//Summon Shadow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1228, 130);//Summon Silhouette
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1278, 130);//Summon Soulless
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1126, 130);//Servitor Recharge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1127, 130);//Servitor Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1281, 130);//Summon Spark Cubic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1333, 130);//Summon Nightshade
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1408, 115);//Summon Spectral Lord
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1530, 330);//Death Spike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1299, 230);//Servitor Empowerment
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1348, 115);//Assassin Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1349, 115);//Final Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1139, 130);//Servitor Magic Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1140, 130);//Servitor Physical Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1141, 130);//Servitor Haste
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1146, 130);//Mighty Servitor
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1385, 330);//Mass Surrender to Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1206, 130);//Wind Shakle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(338, 215);//Arcane Agility
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1262, 130);//Transfer Pain
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1530;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1127;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1351;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1385;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1126;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1333;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1278;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1408;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1547;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1348;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1349;
                shortcut = new Shortcut(3, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1496;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1557;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1262;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 339;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 783;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1281;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 33;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1403;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Shillen Saint
        else if (player.getClassId().getId() == 112) {
            skill = SkillData.getInstance().getInfo(236, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(235, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1217, 130);//Greater Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1013, 130);//Recharge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1395, 130);//Erase
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1398, 130);//Mana Burn
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1035, 130);//Mental Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1040, 130);//Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1303, 130);//Wild Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1059, 230);//Empwoer
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1401, 130);//Major Heal
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1043, 130);//Holy Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1033, 130);//Resist Poison
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1189, 330);//Resist Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1068, 130);//Might
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1073, 130);//Kiss of Eva
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1204, 130);//Wind Walk
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1460, 130);//Mana Gain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1077, 130);//Focus
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1078, 130);//Concentration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1354, 115);//Arcane Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1357, 315);//Prophecy of Wind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1240, 130);//Guidance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1242, 130);//Death Whisper
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1392, 330);//Resist Holy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1268, 130);//Vampire Rage
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1531, 130);//Blessed Blood
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1358, 115);//Block Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1206, 130);//Wind Shackle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1201, 130);//Dryad Root
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1539, 130);//Stigma
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(336, 115);//Arcane Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1259, 330);//Resist Shock
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1218, 130);//Greater Battle Heal
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1553;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1551;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1219;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1217;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1539;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1398;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1539;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1358;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1016;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1551;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1018;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1507;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1013;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1540;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 336;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1259;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1218;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Titan
        else if (player.getClassId().getId() == 113) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(216, 230);//Plearm Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(293, 130);//2h Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(227, 130);//Light Armor Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(231, 130);//Heavy Armor Mastery  power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(211, 130);//Boost HP power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(134, 130);//Toughness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(36, 730);//Whirlwind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(320, 130);//Wrath power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(315, 730);//Crush of Doom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(190, 130);//Fatal Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(440, 115);//Braveheart 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(347, 115);//Earthquake 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(260, 130);//Hammer Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(100, 130);//Stun Attack 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(420, 230);//Zealot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(121, 230);//Battle Roar
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(94, 230);//Rage
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(362, 315);//Armor Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(312, 130);//Vicious Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(422, 130);//Polearm Accuracy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(339, 115);//Parry Stance
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(256, 130);//Accuracy
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 995;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 994;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 315;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 362;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 777;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 260;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 190;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 347;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 245;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 320;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 36;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 420;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 176;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 139;
                shortcut = new Shortcut(2, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 121;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 94;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 287;
                shortcut = new Shortcut(6, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 536;
                shortcut = new Shortcut(7, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 917;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 423;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 256;
                shortcut = new Shortcut(4, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 422;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 312;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(8, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 339;
                shortcut = new Shortcut(9, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));
            }
        }
        //Grand Khavatary
        else if (player.getClassId().getId() == 114) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(210, 130);//Fist Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(233, 130);//Light Armor Mastery power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(134, 130);//Toughness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(168, 130);//Boost Attack Speed
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(319, 230);//Agile Movement
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(17, 730);//Force Burst
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(280, 130);//Burning Fist
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(284, 730);//Hurricane Assault
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(35, 730);//Force Storm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(54, 730);//Force Blaster
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(81, 130);//Punch of Doom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(420, 230);//Zealot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(281, 130);//Soul Breaker
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(95, 130);//Cripple
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 54;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 346;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 995;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 994;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 120;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 81;
                shortcut = new Shortcut(6, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 281;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 17;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 280;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 284;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 776;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 461;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 425;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 298;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 292;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 109;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 918;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 50;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 423;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 917;
                shortcut = new Shortcut(5, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 420;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 443;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 340;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 222;
                shortcut = new Shortcut(5, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 335;
                shortcut = new Shortcut(6, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(7, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //DoomCryer
        else if (player.getClassId().getId() == 116) {
            skill = SkillData.getInstance().getInfo(253, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(252, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(251, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(134, 130);//Toughness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(927, 130);//Burning Chop
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1229, 130);//Chant of Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1245, 330);//Steal Essence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1284, 230);//Chant of Revenge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1413, 215);//Magnus' Chant
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1308, 230);//Chant of Predator
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1309, 230);//Chant of Eagle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1310, 230);//Chant of Vampire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1461, 215);//Chant of Protection
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1362, 215);//Chant of Spirit
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1363, 315);//Chant of Victory
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1251, 230);//Chant of Fury 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1252, 230);//Chant of Evasion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1253, 230);//Chant of Rage
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1002, 230);//Flame Chant
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1006, 230);//Chant of Fire
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1390, 230);//War Chant
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1007, 230);//Chant of Battle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1391, 230);//Earth Chant
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1009, 230);//Chant of Shielding
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1010, 230);//Soul Shield 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1092, 130);//Fear 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1097, 130);//Dreaming Spirit 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(336, 115);//Arcane Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1001, 130);//Soul Cry 
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1245;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 927;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 260;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1107;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1229;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1097;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1208;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1105;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1099;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1096;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1092;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1095;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1363;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1461;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1391;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1390;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1429;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(4, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1001;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 336;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }
        //Dominator
        else if (player.getClassId().getId() == 115) {
            skill = SkillData.getInstance().getInfo(253, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(252, 130);//Light Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(251, 130);//Robe Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(146, 130);//Anti Magic
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(328, 315);//Wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(213, 130);//Boost Mana
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(228, 130);//Fast Spell Casting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(229, 130);//Fast Mana Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(436, 115);//Divine Lore
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(134, 130);//Toughness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(927, 130);//Burning Chop
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1305, 130);//The Honor of Pa'agrio
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1306, 130);//Ritual of Life
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1245, 330);//Steal Essence
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1256, 130);//The Heart of Pa'agrio
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1282, 230);//Pa'agrian Haste
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1414, 315);//Victory of Pa'agrio 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1415, 215);//Pa'agrio's Emblem 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1416, 115);//Pa'agrio's Fist 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1364, 215);//Eye of Pa'agrio 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1365, 215);//Soul of Pa'agrio 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1249, 230);//The Vision of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1250, 230);//Shield of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1003, 230);//Pa'agrian Gift 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1004, 230);//The Wisdom of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1260, 230);//The Tact of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1005, 230);//Blessings of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1261, 230);//Rage of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1008, 230);//The Glory of Pa'agrio 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1010, 230);//Soul Shield 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1462, 115);//Seal of Blockade
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1208, 130);//Seal of Binding 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1092, 130);//Fear 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1096, 130);//Seal of Chaos 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1097, 130);//Dreaming Spirit 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1099, 130);//Seal of Slow 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1104, 130);//Seal of Winter 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1366, 115);//Seal of Despair
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1367, 115);//Seal of Disease
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1246, 130);//Seal of Silence 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1247, 130);//Seal of Scourge 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1248, 130);//Seal of Suspension 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1283, 130);//Soul Guard 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(337, 115);//Arcane Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1001, 130);//Soul Cry 
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                skillid = 1245;
                shortcut = new Shortcut(0, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1256;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1553;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1305;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1306;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1097;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1283;
                shortcut = new Shortcut(9, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1001;
                shortcut = new Shortcut(10, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 337;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 1462;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1208;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1096;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1104;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1099;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1105;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1366;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1367;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1246;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1247;
                shortcut = new Shortcut(9, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1248;
                shortcut = new Shortcut(10, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1509;
                shortcut = new Shortcut(11, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 927;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 260;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1540;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1533;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1427;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

            }
        }

        //Maestro
        else if (player.getClassId().getId() == 118) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(211, 130);//Boost HP
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(216, 230);//Plearm Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(205, 130);//Sword/blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(227, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(231, 130);//Heavy Armor Mastery 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(36, 730);//Whirlwind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(320, 130);//Wrath
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(440, 115);//Braveheart 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(347, 115);//Earthquake 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(260, 130);//Hammer Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(100, 130);//Stun Attack 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(362, 315);//Armor Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(190, 130);//Fatal Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(42, 230);//Sweeper
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(13, 130);//Summon Siege Golem
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(25, 130);//Summon Mechanic Golem
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(299, 130);//Summon Wild Hog Cannon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(301, 130);//Summon Big Boom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(448, 130);//Summon Swoop Cannon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(422, 130);//Polearm Accuracy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(339, 115);//Parry Stance
            player.addSkill(skill, true);

            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)


                skillid = 362;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 995;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 994;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 100;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 260;
                shortcut = new Shortcut(5, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 440;
                shortcut = new Shortcut(0, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 347;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 320;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 245;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 36;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 917;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1561;
                shortcut = new Shortcut(1, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 422;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 339;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 340;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));
            }
        }
        //Fortune Seeker
        else if (player.getClassId().getId() == 117) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(211, 130);//Boost HP
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(212, 130);//Fast Hp Recovery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(209, 130);//Dagger Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(216, 230);//Plearm Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(205, 130);//Sword/blunt Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(227, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(231, 130);//Heavy Armor Mastery 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(430, 115);//Master of Combat
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(36, 730);//Whirlwind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(320, 130);//Wrath
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(263, 730);//Deadly Blow 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(30, 730);//Backstab
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(440, 115);//Braveheart 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(347, 115);//Earthquake 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(260, 130);//Hammer Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(100, 130);//Stun Attack 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(362, 315);//Armor Crush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(190, 130);//Fatal Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(42, 130);//Sweeper
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(302, 130);//Spoil Festival
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(254, 230);//Spoil
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(422, 130);//Polearm Accuracy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(424, 130);//War Frenzy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(339, 115);//Parry Stance
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                skillid = 998;
                shortcut = new Shortcut(1, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 30;
                shortcut = new Shortcut(2, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 1560;
                shortcut = new Shortcut(3, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 263;
                shortcut = new Shortcut(4, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 348;
                shortcut = new Shortcut(7, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 254;
                shortcut = new Shortcut(8, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 42;
                shortcut = new Shortcut(9, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 302;
                shortcut = new Shortcut(10, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 444;
                shortcut = new Shortcut(11, 0, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 347;
                shortcut = new Shortcut(1, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 997;
                shortcut = new Shortcut(2, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 320;
                shortcut = new Shortcut(3, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 36;
                shortcut = new Shortcut(4, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 245;
                shortcut = new Shortcut(5, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 100;
                shortcut = new Shortcut(6, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 362;
                shortcut = new Shortcut(7, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 260;
                shortcut = new Shortcut(8, 1, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));


                skillid = 440;
                shortcut = new Shortcut(0, 2, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 422;
                shortcut = new Shortcut(0, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 424;
                shortcut = new Shortcut(1, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 339;
                shortcut = new Shortcut(2, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));

                skillid = 340;
                shortcut = new Shortcut(3, 3, 2, skillid, getSkillId(player, skillid), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(skillid, getSkillId(player, skillid));
            }
        }

        //Doombringer
        else if (player.getClassId().getId() == 131) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(465, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(472, 130);//Ancient Sword Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(466, 130);//Magic Immunity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(626, 230);//Critical Sense
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(526, 415);//Enuma Elish 15 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(793, 315);//Rush Impact 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(477, 430);//Dark Smash
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(492, 130);//Spread Wing
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(493, 730);//Storm Assault
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(494, 730);//Shoulder CHarge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(495, 430);//Blade Rush
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(496, 730);//Slashing Blade
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(625, 230);//Soul Gathering
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(497, 730);//Crush of Pain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(498, 330);//Contagion
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(502, 130);//Life to Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(483, 130);//Sword Shield
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1514, 230);//Soul Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(794, 115);//Mass Disarm 15
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(485, 130);//Disarm
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(501, 130);//Violent Temper
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(335, 115);//Fortitude
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(500, 130);//True Berserker
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                shortcut = new Shortcut(1, 0, 2, 1435, getSkillId(player, 1435), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1435, getSkillId(player, 1435));
                shortcut = new Shortcut(2, 0, 2, 793, getSkillId(player, 793), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(793, getSkillId(player, 793));
                shortcut = new Shortcut(3, 0, 2, 501, getSkillId(player, 501), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(501, getSkillId(player, 501));
                shortcut = new Shortcut(4, 0, 2, 485, getSkillId(player, 485), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(485, getSkillId(player, 485));
                shortcut = new Shortcut(5, 0, 2, 794, getSkillId(player, 794), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(794, getSkillId(player, 794));
                shortcut = new Shortcut(6, 0, 2, 1510, getSkillId(player, 1510), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1510, getSkillId(player, 1510));
                shortcut = new Shortcut(7, 0, 2, 503, getSkillId(player, 503), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(503, getSkillId(player, 503));
                shortcut = new Shortcut(8, 0, 2, 484, getSkillId(player, 484), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(484, getSkillId(player, 484));

                shortcut = new Shortcut(0, 1, 2, 494, getSkillId(player, 494), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(494, getSkillId(player, 494));
                shortcut = new Shortcut(1, 1, 2, 493, getSkillId(player, 493), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(493, getSkillId(player, 493));
                shortcut = new Shortcut(2, 1, 2, 497, getSkillId(player, 497), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(497, getSkillId(player, 497));
                shortcut = new Shortcut(3, 1, 2, 498, getSkillId(player, 498), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(498, getSkillId(player, 498));
                shortcut = new Shortcut(4, 1, 2, 496, getSkillId(player, 496), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(496, getSkillId(player, 496));
                shortcut = new Shortcut(5, 1, 2, 495, getSkillId(player, 495), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(495, getSkillId(player, 495));
                shortcut = new Shortcut(10, 1, 2, 482, getSkillId(player, 482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(482, getSkillId(player, 482));
                shortcut = new Shortcut(11, 1, 2, 834, getSkillId(player, 834), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(834, getSkillId(player, 834));


                shortcut = new Shortcut(0, 2, 2, 483, getSkillId(player, 483), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(483, getSkillId(player, 483));

                shortcut = new Shortcut(1, 2, 2, 499, getSkillId(player, 499), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(499, getSkillId(player, 499));

                shortcut = new Shortcut(2, 2, 2, 1514, getSkillId(player, 1514), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1514, getSkillId(player, 1514));

                shortcut = new Shortcut(3, 2, 2, 917, getSkillId(player, 917), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(917, getSkillId(player, 917));

                shortcut = new Shortcut(4, 2, 2, 948, getSkillId(player, 948), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(948, getSkillId(player, 948));

                shortcut = new Shortcut(5, 2, 2, 20006, getSkillId(player, 20006), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(20006, getSkillId(player, 20006));

                shortcut = new Shortcut(6, 2, 2, 833, getSkillId(player, 833), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(833, getSkillId(player, 833));


                shortcut = new Shortcut(0, 3, 2, 625, getSkillId(player, 625), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(625, getSkillId(player, 625));

                shortcut = new Shortcut(1, 3, 2, 502, getSkillId(player, 502), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(502, getSkillId(player, 502));

                shortcut = new Shortcut(2, 3, 2, 538, getSkillId(player, 538), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(538, getSkillId(player, 538));

                shortcut = new Shortcut(4, 3, 2, 500, getSkillId(player, 500), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(500, getSkillId(player, 500));

                shortcut = new Shortcut(5, 3, 2, 481, getSkillId(player, 481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(481, getSkillId(player, 481));

                shortcut = new Shortcut(6, 3, 2, 480, getSkillId(player, 480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(480, getSkillId(player, 480));

                shortcut = new Shortcut(7, 3, 2, 479, getSkillId(player, 479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(479, getSkillId(player, 479));

                shortcut = new Shortcut(8, 3, 2, 475, getSkillId(player, 475), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(475, getSkillId(player, 475));

                shortcut = new Shortcut(9, 3, 2, 335, getSkillId(player, 335), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(335, getSkillId(player, 335));

            }
        }

        //Male Soul Hound
        else if (player.getClassId().getId() == 132) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(465, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(474, 130);//Rapier Sword Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1565, 130);//Mana Pump
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(626, 230);//Critical Sense
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(466, 130);//Magic Immunity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1474, 130);//Abyssal Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(492, 130);//Spread Wing
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(625, 230);//Soul Gathering
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(502, 130);//Life to Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(504, 730);//Triple Thrust
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(505, 730);//Shining Edge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(506, 430);//Checkmate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1436, 430);//Soul of Pain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1438, 430);//Annihilation Circle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1469, 415);//Leopold 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1512, 315);//Soul Vortex 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1513, 315);//Soul Vortex Extinction 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1516, 215);//Soul Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1442, 130);//Protection from Darkness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1443, 130);//Dark Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1444, 230);//Pride of Kamael
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1515, 315);//Lightning Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1435, 230);//Death Mark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1437, 330);//Dark Flame
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1445, 230);//Surrender to Dark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1446, 130);//Shadow Bind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1447, 130);//Voice Bind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1448, 230);//Blink
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1511, 230);//Curse of Life FLow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1529, 230);//Soul Web
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                shortcut = new Shortcut(0, 0, 2, 1435, getSkillId(player, 1435), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1435, getSkillId(player, 1435));

                shortcut = new Shortcut(1, 0, 2, 1439, getSkillId(player, 1439), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1439, getSkillId(player, 1439));

                shortcut = new Shortcut(2, 0, 2, 1436, getSkillId(player, 1436), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1436, getSkillId(player, 1436));

                shortcut = new Shortcut(3, 0, 2, 1469, getSkillId(player, 1469), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1469, getSkillId(player, 1469));

                shortcut = new Shortcut(4, 0, 2, 1438, getSkillId(player, 1438), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1438, getSkillId(player, 1438));

                shortcut = new Shortcut(5, 0, 2, 1512, getSkillId(player, 1512), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1512, getSkillId(player, 1512));

                shortcut = new Shortcut(6, 0, 2, 1513, getSkillId(player, 1513), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1513, getSkillId(player, 1513));

                shortcut = new Shortcut(7, 0, 2, 1516, getSkillId(player, 1516), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1516, getSkillId(player, 1516));

                shortcut = new Shortcut(8, 0, 2, 791, getSkillId(player, 791), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(791, getSkillId(player, 791));

                shortcut = new Shortcut(11, 0, 2, 1440, getSkillId(player, 1440), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1440, getSkillId(player, 1440));


                shortcut = new Shortcut(0, 1, 2, 1448, getSkillId(player, 1448), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1448, getSkillId(player, 1448));

                shortcut = new Shortcut(1, 1, 2, 628, getSkillId(player, 628), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(628, getSkillId(player, 628));

                shortcut = new Shortcut(2, 1, 2, 1437, getSkillId(player, 1437), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1437, getSkillId(player, 1437));

                shortcut = new Shortcut(3, 1, 2, 1446, getSkillId(player, 1446), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1446, getSkillId(player, 1446));

                shortcut = new Shortcut(4, 1, 2, 1447, getSkillId(player, 1447), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1447, getSkillId(player, 1447));

                shortcut = new Shortcut(6, 1, 2, 1532, getSkillId(player, 1532), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1532, getSkillId(player, 1532));

                shortcut = new Shortcut(7, 1, 2, 1556, getSkillId(player, 1556), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1556, getSkillId(player, 1556));

                shortcut = new Shortcut(8, 1, 2, 622, getSkillId(player, 622), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(622, getSkillId(player, 622));

                shortcut = new Shortcut(9, 1, 2, 837, getSkillId(player, 837), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(837, getSkillId(player, 837));

                shortcut = new Shortcut(0, 2, 2, 1515, getSkillId(player, 1515), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1515, getSkillId(player, 1515));

                shortcut = new Shortcut(1, 2, 2, 482, getSkillId(player, 482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(482, getSkillId(player, 482));

                shortcut = new Shortcut(2, 2, 2, 538, getSkillId(player, 538), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(538, getSkillId(player, 538));

                shortcut = new Shortcut(3, 2, 2, 1444, getSkillId(player, 1444), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1444, getSkillId(player, 1444));

                shortcut = new Shortcut(5, 2, 2, 485, getSkillId(player, 485), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(485, getSkillId(player, 485));

                shortcut = new Shortcut(6, 2, 2, 483, getSkillId(player, 483), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(483, getSkillId(player, 483));
                shortcut = new Shortcut(7, 2, 2, 499, getSkillId(player, 499), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(499, getSkillId(player, 499));

                shortcut = new Shortcut(0, 3, 2, 625, getSkillId(player, 625), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(625, getSkillId(player, 625));

                shortcut = new Shortcut(1, 3, 2, 502, getSkillId(player, 502), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(502, getSkillId(player, 502));

                shortcut = new Shortcut(4, 3, 2, 479, getSkillId(player, 479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(479, getSkillId(player, 479));

                shortcut = new Shortcut(5, 3, 2, 480, getSkillId(player, 480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(480, getSkillId(player, 480));

                shortcut = new Shortcut(6, 3, 2, 481, getSkillId(player, 481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(481, getSkillId(player, 481));

                shortcut = new Shortcut(7, 3, 2, 475, getSkillId(player, 475), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(475, getSkillId(player, 475));
            }
        }

        //Female Soul Hound
        else if (player.getClassId().getId() == 133) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(465, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(474, 130);//Rapier Sword Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1565, 130);//Mana Pump
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(626, 230);//Critical Sense
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(466, 130);//Magic Immunity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1474, 130);//Abyssal Power
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(492, 130);//Spread Wing
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(625, 230);//Soul Gathering
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(502, 130);//Life to Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(504, 730);//Triple Thrust
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(505, 730);//Shining Edge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(506, 430);//Checkmate
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1436, 430);//Soul of Pain
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1438, 430);//Annihilation Circle
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1469, 415);//Leopold 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1512, 315);//Soul Vortex 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1513, 315);//Soul Vortex Extinction 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1516, 215);//Soul Strike
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1442, 130);//Protection from Darkness
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1443, 130);//Dark Weapon
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1444, 230);//Pride of Kamael
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1515, 315);//Lightning Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1435, 230);//Death Mark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1437, 330);//Dark Flame
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1445, 230);//Surrender to Dark
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1446, 130);//Shadow Bind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1447, 130);//Voice Bind
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1448, 230);//Blink
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1511, 230);//Curse of Life FLow
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1529, 230);//Soul Web
            player.addSkill(skill, true);
            player.sendMessage("Skills Leveled with succes!");
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)
                shortcut = new Shortcut(0, 0, 2, 1435, getSkillId(player, 1435), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1435, getSkillId(player, 1435));

                shortcut = new Shortcut(1, 0, 2, 1439, getSkillId(player, 1439), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1439, getSkillId(player, 1439));

                shortcut = new Shortcut(2, 0, 2, 1436, getSkillId(player, 1436), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1436, getSkillId(player, 1436));

                shortcut = new Shortcut(3, 0, 2, 1469, getSkillId(player, 1469), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1469, getSkillId(player, 1469));

                shortcut = new Shortcut(4, 0, 2, 1438, getSkillId(player, 1438), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1438, getSkillId(player, 1438));

                shortcut = new Shortcut(5, 0, 2, 1512, getSkillId(player, 1512), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1512, getSkillId(player, 1512));

                shortcut = new Shortcut(6, 0, 2, 1513, getSkillId(player, 1513), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1513, getSkillId(player, 1513));

                shortcut = new Shortcut(7, 0, 2, 1516, getSkillId(player, 1516), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1516, getSkillId(player, 1516));

                shortcut = new Shortcut(8, 0, 2, 791, getSkillId(player, 791), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(791, getSkillId(player, 791));

                shortcut = new Shortcut(11, 0, 2, 1440, getSkillId(player, 1440), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1440, getSkillId(player, 1440));


                shortcut = new Shortcut(0, 1, 2, 1448, getSkillId(player, 1448), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1448, getSkillId(player, 1448));

                shortcut = new Shortcut(1, 1, 2, 628, getSkillId(player, 628), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(628, getSkillId(player, 628));

                shortcut = new Shortcut(2, 1, 2, 1437, getSkillId(player, 1437), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1437, getSkillId(player, 1437));

                shortcut = new Shortcut(3, 1, 2, 1446, getSkillId(player, 1446), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1446, getSkillId(player, 1446));

                shortcut = new Shortcut(4, 1, 2, 1447, getSkillId(player, 1447), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1447, getSkillId(player, 1447));

                shortcut = new Shortcut(6, 1, 2, 1532, getSkillId(player, 1532), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1532, getSkillId(player, 1532));

                shortcut = new Shortcut(7, 1, 2, 1556, getSkillId(player, 1556), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1556, getSkillId(player, 1556));

                shortcut = new Shortcut(8, 1, 2, 622, getSkillId(player, 622), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(622, getSkillId(player, 622));

                shortcut = new Shortcut(9, 1, 2, 837, getSkillId(player, 837), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(837, getSkillId(player, 837));

                shortcut = new Shortcut(0, 2, 2, 1515, getSkillId(player, 1515), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1515, getSkillId(player, 1515));

                shortcut = new Shortcut(1, 2, 2, 482, getSkillId(player, 482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(482, getSkillId(player, 482));

                shortcut = new Shortcut(2, 2, 2, 538, getSkillId(player, 538), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(538, getSkillId(player, 538));

                shortcut = new Shortcut(3, 2, 2, 1444, getSkillId(player, 1444), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1444, getSkillId(player, 1444));


                shortcut = new Shortcut(0, 3, 2, 625, getSkillId(player, 625), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(625, getSkillId(player, 625));

                shortcut = new Shortcut(1, 3, 2, 502, getSkillId(player, 502), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(502, getSkillId(player, 502));

                shortcut = new Shortcut(4, 3, 2, 479, getSkillId(player, 479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(479, getSkillId(player, 479));

                shortcut = new Shortcut(5, 3, 2, 480, getSkillId(player, 480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(480, getSkillId(player, 480));

                shortcut = new Shortcut(6, 3, 2, 481, getSkillId(player, 481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(481, getSkillId(player, 481));
            }
        }
        //Trickster
        else if (player.getClassId().getId() == 134) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(465, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(473, 130);//CrossBow Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(626, 230);//Critical Sense
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(466, 130);//Magic Immunity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(625, 230);//Soul Gathering
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(502, 130);//Life to Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(514, 130);//Fire Trap
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(515, 130);//Poison Trap
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(507, 730);//Twin Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(508, 730);//Rising Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(510, 130);//Deadly Roulette
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(525, 230);//Decoy
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(521, 130);//Sharpshooting
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1470, 315);//Prahnah
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(490, 230);//Fast Shot
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1514, 230);//Soul Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(516, 130);//Slow Trap
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(517, 130);//Flash Trap
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(518, 130);//Binding Trap
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(522, 130);//Real Target
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(627, 430);//Soul Shock
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(509, 330);//Bleeding Shot
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                //int slotId, int pageId, int shortcutType, int shortcutId, int shortcutLevel, int characterType)

                shortcut = new Shortcut(1, 0, 2, 522, getSkillId(player, 522), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(522, getSkillId(player, 522));

                shortcut = new Shortcut(2, 0, 2, 1435, getSkillId(player, 1435), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1435, getSkillId(player, 1435));

                shortcut = new Shortcut(3, 0, 2, 1510, getSkillId(player, 1510), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1510, getSkillId(player, 1510));

                shortcut = new Shortcut(4, 0, 2, 628, getSkillId(player, 628), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(628, getSkillId(player, 628));

                shortcut = new Shortcut(5, 0, 2, 510, getSkillId(player, 510), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(510, getSkillId(player, 510));

                shortcut = new Shortcut(6, 0, 2, 523, getSkillId(player, 523), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(523, getSkillId(player, 523));

                shortcut = new Shortcut(0, 1, 2, 507, getSkillId(player, 507), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(507, getSkillId(player, 507));

                shortcut = new Shortcut(1, 1, 2, 508, getSkillId(player, 508), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(508, getSkillId(player, 508));
                shortcut = new Shortcut(2, 1, 2, 790, getSkillId(player, 790), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(790, getSkillId(player, 790));
                shortcut = new Shortcut(3, 1, 2, 990, getSkillId(player, 990), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(990, getSkillId(player, 990));
                shortcut = new Shortcut(4, 1, 2, 987, getSkillId(player, 987), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(987, getSkillId(player, 987));
                shortcut = new Shortcut(5, 1, 2, 508, getSkillId(player, 508), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(509, getSkillId(player, 509));

                shortcut = new Shortcut(8, 1, 2, 525, getSkillId(player, 525), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(525, getSkillId(player, 525));

                shortcut = new Shortcut(10, 1, 2, 513, getSkillId(player, 513), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(513, getSkillId(player, 513));

                shortcut = new Shortcut(11, 1, 2, 621, getSkillId(player, 621), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(621, getSkillId(player, 621));


                shortcut = new Shortcut(0, 2, 2, 1470, getSkillId(player, 1470), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1470, getSkillId(player, 1470));

                shortcut = new Shortcut(1, 2, 2, 1514, getSkillId(player, 1514), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1514, getSkillId(player, 1514));

                shortcut = new Shortcut(2, 2, 2, 622, getSkillId(player, 622), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(622, getSkillId(player, 622));

                shortcut = new Shortcut(5, 2, 2, 482, getSkillId(player, 482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(482, getSkillId(player, 482));

                shortcut = new Shortcut(6, 2, 2, 521, getSkillId(player, 521), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(521, getSkillId(player, 521));

                shortcut = new Shortcut(7, 2, 2, 490, getSkillId(player, 490), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(490, getSkillId(player, 490));


                shortcut = new Shortcut(0, 3, 2, 625, getSkillId(player, 625), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(625, getSkillId(player, 625));

                shortcut = new Shortcut(1, 3, 2, 502, getSkillId(player, 502), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(502, getSkillId(player, 502));

                shortcut = new Shortcut(2, 3, 2, 538, getSkillId(player, 538), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(538, getSkillId(player, 538));

                shortcut = new Shortcut(4, 3, 2, 334, getSkillId(player, 334), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(334, getSkillId(player, 334));

                shortcut = new Shortcut(5, 3, 2, 479, getSkillId(player, 479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(479, getSkillId(player, 479));

                shortcut = new Shortcut(6, 3, 2, 480, getSkillId(player, 480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(480, getSkillId(player, 480));

                shortcut = new Shortcut(7, 3, 2, 481, getSkillId(player, 481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(481, getSkillId(player, 481));
            }
        }
        //judicator
        else if (player.getClassId().getId() == 136) {
            skill = SkillData.getInstance().getInfo(328, 315);//wisdom
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(329, 115);//Health
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(465, 130);//Light Armor Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(474, 130);//Rapier Sword Mastery
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(626, 230);//Critical Sense
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(466, 130);//Magic Immunity
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(492, 130);//Spread Wing
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(625, 230);//Soul Gathering
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(502, 130);//Life to Soul
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(504, 730);//Triple Thrust
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(505, 730);//Shining Edge
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1488, 130);//Restoration Impact 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1476, 230);//Appetite for Destruction 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1477, 230);//Vampiric Impulse
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1478, 230);//Protection Instinct 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1479, 230);//Magic Impulse
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1480, 230);//Soul Harmony 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1487, 130);//Restoration
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1515, 315);//Lightning Barrier
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1481, 230);//Oblivion 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1482, 230);//Weak Constitution 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1483, 230);//Thin Skin 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1484, 230);//Enervation 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1485, 230);//Spite 
            player.addSkill(skill, true);
            skill = SkillData.getInstance().getInfo(1486, 230);//Mental Impoverish 
            player.addSkill(skill, true);
            if (GabConfig.AUTO_SHORTCUT_SKILLS && !player.isGM()) {
                shortcut = new Shortcut(1, 0, 2, 1435, getSkillId(player, 1435), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1435, getSkillId(player, 1435));

                shortcut = new Shortcut(2, 0, 2, 1488, getSkillId(player, 1488), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1488, getSkillId(player, 1488));

                shortcut = new Shortcut(3, 0, 2, 1510, getSkillId(player, 1510), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1510, getSkillId(player, 1510));

                shortcut = new Shortcut(4, 0, 2, 628, getSkillId(player, 628), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(628, getSkillId(player, 628));

                shortcut = new Shortcut(9, 0, 2, 1482, getSkillId(player, 1482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1482, getSkillId(player, 1482));

                shortcut = new Shortcut(10, 0, 2, 1483, getSkillId(player, 1483), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1483, getSkillId(player, 1483));

                shortcut = new Shortcut(11, 0, 2, 1484, getSkillId(player, 1484), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1484, getSkillId(player, 1484));


                shortcut = new Shortcut(0, 1, 2, 1515, getSkillId(player, 1515), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1515, getSkillId(player, 1515));

                shortcut = new Shortcut(1, 1, 2, 482, getSkillId(player, 482), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(482, getSkillId(player, 482));

                shortcut = new Shortcut(5, 1, 2, 837, getSkillId(player, 837), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(837, getSkillId(player, 837));

                shortcut = new Shortcut(9, 1, 2, 1486, getSkillId(player, 1486), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1486, getSkillId(player, 1486));

                shortcut = new Shortcut(10, 1, 2, 1485, getSkillId(player, 1485), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1485, getSkillId(player, 1485));

                shortcut = new Shortcut(11, 1, 2, 1481, getSkillId(player, 1481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1481, getSkillId(player, 1481));


                shortcut = new Shortcut(0, 2, 2, 1476, getSkillId(player, 1476), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1476, getSkillId(player, 1476));

                shortcut = new Shortcut(1, 2, 2, 1478, getSkillId(player, 1478), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1478, getSkillId(player, 1478));

                shortcut = new Shortcut(2, 2, 2, 1479, getSkillId(player, 1479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1479, getSkillId(player, 1479));

                shortcut = new Shortcut(4, 2, 2, 1477, getSkillId(player, 1477), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1477, getSkillId(player, 1477));

                shortcut = new Shortcut(5, 2, 2, 1480, getSkillId(player, 1480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1480, getSkillId(player, 1480));

                shortcut = new Shortcut(6, 2, 2, 1487, getSkillId(player, 1487), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(1487, getSkillId(player, 1487));


                shortcut = new Shortcut(0, 3, 2, 625, getSkillId(player, 625), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(625, getSkillId(player, 625));

                shortcut = new Shortcut(1, 3, 2, 502, getSkillId(player, 502), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(502, getSkillId(player, 502));

                shortcut = new Shortcut(2, 3, 2, 538, getSkillId(player, 538), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(538, getSkillId(player, 538));

                shortcut = new Shortcut(4, 3, 2, 479, getSkillId(player, 479), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(479, getSkillId(player, 479));

                shortcut = new Shortcut(5, 3, 2, 480, getSkillId(player, 480), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(480, getSkillId(player, 480));

                shortcut = new Shortcut(6, 3, 2, 481, getSkillId(player, 481), 1);
                player.registerShortCut(shortcut);
                player.updateShortCuts(481, getSkillId(player, 481));
            }
        }
        player.sendSkillList();
    }
}
