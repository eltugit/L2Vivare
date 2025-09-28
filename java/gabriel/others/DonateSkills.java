package gabriel.others;


import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.Shortcut;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class DonateSkills {

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
        }
        player.sendSkillList();
    }
}
