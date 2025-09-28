package gabriel.Utils;


import gabriel.cbbCertif.CertiData;
import gabriel.config.GabConfig;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.instancemanager.CursedWeaponsManager;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;


public class SafeSkill {


    public static boolean isSafeSkill(L2Skill skill, L2PcInstance player) {
        boolean foundskill = false;
        int skillid = skill.getId();
        int skillLv = skill.getLevel();

        if (SkillTreesData.getInstance().isSkillAllowed(player, skill)) {
            foundskill = true;
        }

        if (SkillTreesData.getInstance().getFishingSkillTree().values().stream().anyMatch(e -> e.getSkillId() == skillid)) {
            foundskill = true;
        }
        // exclude noble skills
        if ((skillid >= 325) && (skillid <= 397)) {
            foundskill = true;
        }

        // transformation skills
        if ((skillid >= 59506) && (skillid <= 59537)) {
            foundskill = true;
        }

        // exclude Class Path skills
        if ((skillid >= 9421) && (skillid <= 9534)) {
            foundskill = true;
        }

        if (skillid == GabConfig.CLASS_HEAVY_NERF_SKILL) {
            foundskill = true;
        }


        if ((skillid >= 10660) && (skillid <= 10666)) {
            foundskill = true;
        }
        if (player.isTransformed() && ((skillid >= 29000) && (skillid <= 29013))) {
            foundskill = true;
        }

        // exclude noble skills
        if ((skillid >= 1523) && (skillid <= 1528)) {
            foundskill = true;
        }

        // exclude untransformation skills
        if ((skillid >= 814) && (skillid <= 817)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if ((skillid >= 956) && (skillid <= 958)) {
            foundskill = true;
        }
        // exclude noble skills
        if ((skillid >= 1323) && (skillid <= 1327)) {
            foundskill = true;
        }

        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 619)) {
            foundskill = true;
        }
        // exclude dismount skills
        if (player.isTransformed() && (skillid == 839)) {
            foundskill = true;
        }
        // exclude dismount skills
        if (skillid == 838) {
            foundskill = true;
        }
        // exclude dismount skills
        if (skillid == 144) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 911)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 28)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 18)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 196)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 293)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 197)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 838)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 86)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 114)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 283)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 67)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 10)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 22)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 33)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 289)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 144)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 1043)) {
            foundskill = true;
        }
        // exclude untransformation skills
        if (player.isTransformed() && (skillid == 1059)) {
            foundskill = true;
        }
        // diviny
        if (player.isTransformed() && (skillid >= 1523) && (skillid <= 1528)) {
            foundskill = true;
        }
        // exclude transformation skills
        if (player.isTransformed() && (skillid >= 869) && (skillid <= 910)) {
            foundskill = true;
        }
        // exclude transformation skills
        if (player.isTransformed() && (skillid >= 629) && (skillid <= 630)) {
            foundskill = true;
        }
        // exclude transformation skills
        if (player.isTransformed() && (skillid >= 936) && (skillid <= 981)) {
            foundskill = true;
        }

        // mercerany
        if (player.isTransformed() && (skillid >= 539) && (skillid <= 540)) {
            foundskill = true;
        }
        // certification transformation skills
        if (player.isTransformed() && (skillid >= 675) && (skillid <= 754)) {
            foundskill = true;
        }
        // certification transformation skills
        if (player.isTransformed() && (skillid >= 795) && (skillid <= 817)) {
            foundskill = true;
        }
        // mercerany
        if (player.isTransformed() && (skillid >= 1471) && (skillid <= 1472)) {
            foundskill = true;
        }
        // mercerany
        if (player.isTransformed() && (skillid >= 1544) && (skillid <= 1545)) {
            foundskill = true;
        }

        // exclude transformation skills
        if (player.isTransformed() && (skillid >= 559) && (skillid <= 589)) {
            foundskill = true;
        }
        // exclude transformation skills 2
        if (player.isTransformed() && (skillid >= 783) && (skillid <= 784)) {
            foundskill = true;
        }

        // exclude hero skills
        // if (isHero() && (skillid >= 395) && (skillid <= 396)) {
        if ((skillid >= 395) && (skillid <= 396)) {
            foundskill = true;
        }

        // if (isHero() && (skillid >= 1374) && (skillid <= 1376)) {
        if ((skillid >= 1374) && (skillid <= 1376)) {
            foundskill = true;
        }
        if (player.isGM() && (skillid == 7029)) {
            foundskill = true;
        }

        // exclude cursed weapon skills
        if (player.isCursedWeaponEquipped() && (skillid == CursedWeaponsManager.getInstance().getCursedWeapon(player.getCursedWeaponEquippedId()).getSkillId())) {
            foundskill = true;
        }

        // exclude clan skills
        if ((player.getClan() != null) && (skillid >= 370) && (skillid <= 391)) {
            foundskill = true;
        }
        //squad skill
        if ((player.getClan() != null) && (skillid >= 611) && (skillid <= 616)) {
            foundskill = true;
        }
        //fortress skill
        if ((player.getClan() != null) && (skillid >= 590) && (skillid <= 610)) {
            foundskill = true;
        }
        //castle skill
        if ((player.getClan() != null) && (skillid >= 848) && (skillid <= 856)) {
            foundskill = true;
        }

        //certific
        if (((skillid >= 631) && (skillid <= 662)) || ((skillid >= 799) && (skillid <= 804)) || ((skillid >= 1489) && (skillid <= 1491))) {
            foundskill = true;
            certificationSkillIsSafe(skillid, skillLv, player);
        }

        // exclude seal of ruler / build siege hq
        if ((player.getClan() != null) && (skillid == 247)) {
            if (player.getClan().getLeaderId() == player.getObjectId()) {
                foundskill = true;
            }
        }
        if (skillid == 246) {
            if (((player.getClan() != null) && player.getClan().getLeaderId() == player.getObjectId())) {
                foundskill = true;
            }
        }

        // exclude fishing skills and common skills + dwarfen craft
        if ((skillid >= 1312) && (skillid <= 1322)) {
            foundskill = true;
        }
        //territory
        if (skillid == 844) {
            foundskill = true;
        }
        if (skillid == 845) {
            foundskill = true;
        }
        if (skillid == 846) {
            foundskill = true;
        }
        if (skillid == 847) {
            foundskill = true;
        }
        if ((skillid >= 1368) && (skillid <= 1373)) {
            foundskill = true;
        }

        if ((skillid >= 511660) && (skillid <= 513000)) {
            foundskill = true;
        }

        if ((skillid >= 610000) && (skillid <= 610005)) {
            foundskill = true;
        }


        // exclude sa / enchant bonus / penality etc. skills62010 7000
        // if ((skillid >= 3000) && (skillid < 7000)) {
        if ((skillid >= 3000) && (skillid < 65535)) {
            foundskill = true;
        }

        return foundskill;
    }

    private static void certificationSkillIsSafe(int skillId, int skillLv, L2PcInstance player) {
        CertiData data = player.getCertiData();
        checkCertEmergents(skillId, skillLv, data, player);
        checkCertSkills(skillId, skillLv, data, player);
        checkCertTransform(skillId, skillLv, data, player);
    }

    public static void checkCertEmergents(int skillId, int skillLv, CertiData data, L2PcInstance player) {
        if (((skillId >= 631) && (skillId <= 634)) && !data.containsEmergent(skillId, skillLv)) {
            L2Skill skill = SkillData.getInstance().getInfo(skillId, skillLv);
            player.removeSkill(skill);
            player.sendSkillList();
        }
    }

    public static void checkCertSkills(int skillId, int skillLv, CertiData data, L2PcInstance player) {
        if ((((skillId >= 637) && (skillId <= 648)) || ((skillId >= 650) && (skillId <= 655)) || ((skillId >= 801) && (skillId <= 804)) || ((skillId >= 1489) && (skillId <= 1491))) && !data.containsSkill(skillId)) {
            L2Skill skill = SkillData.getInstance().getInfo(skillId, skillLv);
            player.removeSkill(skill);
            player.sendSkillList();
        }
    }

    public static void checkCertTransform(int skillId, int skillLv, CertiData data, L2PcInstance player) {
        if (((skillId >= 656) && (skillId <= 662)) && !data.containsTransform(skillId, skillLv)) {
            L2Skill skill = SkillData.getInstance().getInfo(skillId, skillLv);
            player.removeSkill(skill);
            player.sendSkillList();
        }
    }

}
