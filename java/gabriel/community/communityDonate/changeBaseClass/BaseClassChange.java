package gabriel.community.communityDonate.changeBaseClass;


import gabriel.config.GabConfig;
import gabriel.dressmeEngine.DressMeHandler;
import gabriel.scriptsGab.utils.BBS;
import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.data.xml.impl.TransformData;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.Shortcut;
import l2r.gameserver.model.StatsSet;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.base.ClassLevel;
import l2r.gameserver.model.base.SubClass;
import l2r.gameserver.model.cubic.CubicInstance;
import l2r.gameserver.model.entity.Hero;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.InventoryUpdate;
import l2r.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class BaseClassChange {
    protected static BaseClassChange instance;
    private static String BBS_HOME_DIR = "data/html/gabriel/services/Donate";


    public static BaseClassChange getInstance() {
        if (instance == null)
            instance = new BaseClassChange();
        return instance;
    }

    private static final ItemHolder[] PORMANDERS =
            {
                    // Cardinal (97)
                    new ItemHolder(15307, 1),
                    // Eva's Saint (105)
                    new ItemHolder(15308, 1),
                    // Shillen Saint (112)
                    new ItemHolder(15309, 4)
            };

    private void givePomander(L2PcInstance player) {
        for (ItemHolder pormander : PORMANDERS) {
            long count = player.getInventory().getInventoryItemCount(pormander.getId(), 0);
            for (int i = 0; i < count; i++) {
                L2ItemInstance items = player.getInventory().destroyItemByItemId("CLASSBASE Destroy", pormander.getId(), 1, player, null);
                if (items != null) {
                    InventoryUpdate playerIU = new InventoryUpdate();
                    playerIU.addRemovedItem(items);
                    player.sendPacket(playerIU);
                }
            }
        }

        ThreadPoolManager.getInstance().scheduleGeneral(() -> {
            switch (player.getClassId().getId()) {
                case 97:
                    if (PORMANDERS[0].getCount() > 1) {
                        for (int i = 0; i < PORMANDERS[0].getCount(); i++) {
                            player.addItem("POMANDERCLASSCHANGE", PORMANDERS[0].getId(), 1, player, true);
                        }
                    } else {
                        player.addItem("POMANDERCLASSCHANGE", PORMANDERS[0].getId(), 1, player, true);
                    }

                    break;
                case 105:
                    if (PORMANDERS[1].getCount() > 1) {
                        for (int i = 0; i < PORMANDERS[1].getCount(); i++) {
                            player.addItem("POMANDERCLASSCHANGE", PORMANDERS[1].getId(), 1, player, true);
                        }
                    } else {
                        player.addItem("POMANDERCLASSCHANGE", PORMANDERS[1].getId(), 1, player, true);
                    }
                    break;
                case 112:
                    if (PORMANDERS[2].getCount() > 1) {
                        for (int i = 0; i < PORMANDERS[2].getCount(); i++) {
                            player.addItem("POMANDERCLASSCHANGE", PORMANDERS[2].getId(), 1, player, true);
                        }
                    } else {
                        player.addItem("POMANDERCLASSCHANGE", PORMANDERS[2].getId(), 1, player, true);
                    }
                    break;
            }
        }, 200);
    }

    public void onBypassFeedback(final L2PcInstance player, final String command) {

        String html = HtmCache.getInstance().getHtm(player, player.getHtmlPrefix(), BBS_HOME_DIR + "/charMainClass.htm");

        if (!GabConfig.COMMUNITY_DONATE_CLASSCHANGE_ALLOW) {
            player.sendMessage("This function is disabled.");
            return;
        }

        if (!GabConfig.COMMUNITY_DONATE_CLASSCHANGE_NONPEACE && !player.isInsideZone(ZoneIdType.PEACE)) {
            player.sendMessage("This function is disabled outside a peace zone");
            return;
        }

        if (player.isInOlympiadMode()) {
            player.sendMessage("You cannot talk with me in olympiad");
            return;
        }
        if (player.isInTournament()) {
            player.sendMessage("You cannot talk with me in tournament");
            return;
        }
        if (player.getInstanceId() != 0) {
            player.sendMessage("You cannot talk with me in an instance");
            return;
        }
        if (player.isInCombat()) {
            player.sendMessage("You cannot talk with me in combat");
            return;
        }
        if (player.isInArenaEvent()) {
            player.sendMessage("You cannot talk with me in Event");
            return;
        }


        final StringTokenizer st = new StringTokenizer(command, " ");
        st.nextToken();


        if (command.startsWith("charMainClass_classRace") || command.equals("charMainClass")) {
            String val = "";
            if (!st.hasMoreTokens()) {
                val = "human";
            } else {
                val = st.nextToken();
            }
            html = html.replace("%price%", String.valueOf(GabConfig.COMMUNITY_DONATE_CLASSCHANGE_PRICE));
            html = html.replace("%list%", getClassesHtml(val, player));
            BBS.separateAndSend(html, player);

        } else if (command.startsWith("charMainClass_setClass")) {
            if (!GabConfig.COMMUNITY_DONATE_CLASSCHANGE_ALLOW) {
                player.sendMessage("This function is disabled.");
                return;
            }

            if (!GabConfig.COMMUNITY_DONATE_CLASSCHANGE_NONPEACE && !player.isInsideZone(ZoneIdType.PEACE)) {
                player.sendMessage("This function is disabled outside a peace zone");
                return;
            }

            if (!st.hasMoreTokens()) {
                return;
            }
            String val = st.nextToken();

            if (player.isSubClassActive()) {
                player.sendMessage("Come Back when you have your main class active!");
                return;
            }
            if (player.getActiveClass() != player.getBaseClass()) {
                player.sendMessage("Come Back when you have your main class active!");
                return;
            }
            if (isHeroFromOly(player)) {
                player.sendMessage("You are a Olympiad Hero, You can't change main class!");
                return;
            }

            if (player.getInventory().getItemByItemId(GabConfig.COMMUNITY_DONATE_CLASSCHANGE_ID) == null) {
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
                return;
            }
            if (player.getInventory().getItemByItemId(GabConfig.COMMUNITY_DONATE_CLASSCHANGE_ID).getCount() < GabConfig.COMMUNITY_DONATE_CLASSCHANGE_PRICE) {
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.NOT_ENOUGH_ITEMS));
                return;
            }
            if (player.destroyItemByItemId("SetClassService", GabConfig.COMMUNITY_DONATE_CLASSCHANGE_ID, GabConfig.COMMUNITY_DONATE_CLASSCHANGE_PRICE, player, true)) {
                changeClass(player, val);
            }
        }
    }

    private String getClassesHtml(String val, L2PcInstance player) {
        StringBuilder html = new StringBuilder();
        int i = 1;

        html.append("<tr>");
        for (Map.Entry<Integer, String> entry : getListForClass(val).entrySet()) {
            int classId = entry.getKey();
            String className = entry.getValue();
            if (checkClass(classId, player)) {
                continue;
            }
            html.append("<td align=center><button value=\"" + className + "\" action=\"bypass -h gab_charMainClass_setClass " + classId + "\" width=120 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
            if (i % 2 == 0) {
                html.append("</tr>");
                html.append("<tr>");
            }
            i++;

        }
        html.append("</tr>");

        return html.toString();
    }

    private boolean checkClass(int classId, L2PcInstance player) {

        if (player.getBaseClass() == classId)
            return true;

        if (player.getSubClasses() != null) {
            for (Map.Entry<Integer, SubClass> subs : player.getSubClasses().entrySet()) {
                //check if class is already one of the classId
                if (subs.getValue().getClassId() == classId)
                    return true;

                final ClassId toGetCID = ClassId.values()[classId];
                final ClassId sub = ClassId.values()[subs.getValue().getClassId()];

                if (toGetCID.equalsOrChildOf(sub))
                    return true;
            }
        }
        return false;
    }

    private boolean check4rthJobSubs(L2PcInstance player) {
        if (player.getSubClasses() != null) {
            for (Map.Entry<Integer, SubClass> subs : player.getSubClasses().entrySet()) {
                //check if class is already one of the classId
                if (subs.getValue().getClassDefinition().getLevel() != ClassLevel.Fourth) {
                    player.sendMessage(String.format("Your sub class %s is not at the 4th job transfer. Come back when it has transfered!",
                            subs.getValue()));
                    return true;
                }
            }
        }
        return false;
    }

    private Map<Integer, String> getListForClass(String val) {
        Map<Integer, String> temp = null;
        switch (val) {
            case "human":
                temp = ClassData.getInstance().getHuman();
                break;
            case "elf":
                temp = ClassData.getInstance().getElf();
                break;
            case "delf":
                temp = ClassData.getInstance().getDelf();
                break;
            case "orc":
                temp = ClassData.getInstance().getOrc();
                break;
            case "dwarf":
                temp = ClassData.getInstance().getDwarf();
                break;
            case "kamael":
                temp = ClassData.getInstance().getKamael();
                break;
        }
        return temp;
    }


    private void changeClass(L2PcInstance player, String val) {
        int classidval = Integer.parseInt(val);
        boolean deleteSubs = false;

        Race playerRace = player.getRace();
        if (playerRace == Race.KAMAEL) {
            if (!containsInKamael(classidval)) {
                deleteSubs = true;
                handleUnEquip(player);
            }
        } else {
            if (containsInKamael(classidval)) {
                deleteSubs = true;
                handleUnEquip(player);
            }
        }

        boolean valid = false;
        for (ClassId classid : ClassId.values())
            if (classidval == classid.getId())
                valid = true;

        if (valid && (player.getClassId().getId() != classidval)) {
            Collection<L2SkillLearn> skills = null;
            try {
                skills = SkillTreesData.getInstance().getTransferSkillTree(player.getClassId()).values();
            } catch (Exception e) {
                //
            }
            boolean foundPom = false;
            if (skills != null) {
                for (L2SkillLearn skillLearn : skills) {
                    final L2Skill skill = player.getKnownSkill(skillLearn.getSkillId());
                    if (skill != null) {
                        player.removeSkill(skill);
                        foundPom = true;
                    }
                }
            }


            if (foundPom) {
                String HOLY_POMANDER = "HOLY_POMANDER_";
                final String name = HOLY_POMANDER + player.getClassId().getId();
                player.getVariables().unset(name);
                player.getVariables().remove(name);
            }

            player.deleteCerti(0);
            player._certi_data.clear();
            deleteCerti(player.getObjectId());
            player.setClassId(classidval);
            player.setBaseClass(classidval);
            switch (classidval) {
                case 131:
                case 132:
                    player.getAppearance().setSex(false);
                    break;
                case 133:
                case 134:
                    player.getAppearance().setSex(true);
            }
            if (deleteSubs) {
                ThreadPoolManager.getInstance().scheduleGeneral(new DeleteSubClassRunnable(player), 1000);
            }
            handleChangeMainClass(player);
            String newclass = player.getTemplate().getClassId().name();
            player.store();
            player.sendMessage("Your class changed to " + newclass);
            player.broadcastUserInfo();
            player.sendMessage(player.getName() + " is a " + newclass);
            TransformData.getInstance().transformPlayer(105, player);
            player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(player::untransform, 200));

            givePomander(player);

            for (Shortcut sc : player.getAllShortCuts()) {
                if (sc != null) {
                    player.deleteShortCut(sc.getSlot(), sc.getPage());
                }
            }
        }
    }

    private static final String DELETE_CERTI = "DELETE FROM `character_certification` WHERE `object_id`=?";

    public void deleteCerti(int index) {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(DELETE_CERTI)) {
                ps.setInt(1, index);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean containsInKamael(int classId) {
        for (Map.Entry<Integer, String> entry : ClassData.getInstance().getKamael().entrySet()) {
            if (entry.getKey() == classId) {
                return true;
            }
        }
        return false;
    }

    private void handleChangeMainClass(L2PcInstance player) {
        refreshSkills(player);
        cleanHeroEntries(player);
        removeHenna(player);
        if (player.getSummon() != null)
            player.getSummon().unSummon(player);

        if (!player.getCubics().isEmpty()) {
            for (CubicInstance cubic : player.getCubics().values()) {
                cubic.deactivate();
            }
            player.getCubics().clear();
        }
        if (player.getParty() != null)
            player.leaveParty();

        player.broadcastUserInfo();
    }

    private void removeHenna(L2PcInstance player) {
        for (int i = 3; i >= 1; i--) {
            if (player.getHennaEx().getHenna(i) != null) {
                player.getHennaEx().removeHenna(i);
            }
        }
    }

    private void refreshSkills(L2PcInstance activeChar) {
        for (L2Skill skill : activeChar.getAllSkills())
            activeChar.removeSkill(skill);

        activeChar.giveAvailableSkills(true, true);
        activeChar.regiveTemporarySkills();
        activeChar.sendSkillList();
        activeChar.broadcastUserInfo();
    }

    private boolean isHeroFromOly(L2PcInstance player) {
        boolean found = false;
        StatsSet temp = Hero.getInstance().getHeroes().get(player.getObjectId());
        if (temp != null)
            found = true;

        return found;
    }

    private void cleanHeroEntries(L2PcInstance player) {

        String diary = "DELETE FROM heroes_diary WHERE heroes_diary.charId = " + player.getObjectId();
        String heroes = "DELETE FROM heroes WHERE heroes.charId = " + player.getObjectId();
        String olympiad_nobles = "DELETE FROM olympiad_nobles WHERE olympiad_nobles.charId = " + player.getObjectId();
        String olympiad_nobleseom = "DELETE FROM olympiad_nobles_eom WHERE olympiad_nobles_eom.charId = " + player.getObjectId();
        String updateFights = "UPDATE olympiad_fights SET charOneId = 0  WHERE charOneId = " + player.getObjectId();
        String updateFights2 = "UPDATE olympiad_fights SET charTwoId = 0 WHERE charTwoId = " + player.getObjectId();
        String delFights1 = "DELETE FROM olympiad_fights WHERE olympiad_fights.charOneId = 0 AND winner = 1";
        String delFights2 = "DELETE FROM olympiad_fights WHERE olympiad_fights.charTwoId = 0 AND winner = 2";

        try (Connection connection = L2DatabaseFactory.getInstance().getConnection();) {
            {
                Statement statement = connection.createStatement();
                statement.executeUpdate(updateFights);
                statement.executeUpdate(updateFights2);
                statement.executeUpdate(delFights1);
                statement.executeUpdate(delFights2);
                statement.executeUpdate(diary);
                statement.executeUpdate(heroes);
                statement.executeUpdate(olympiad_nobles);
                statement.executeUpdate(olympiad_nobleseom);
            }
        } catch (Exception e) {
            System.out.println("Error deleting hero entries for player " + player.getName() + " ---------" + e);
        }
        Hero.getInstance().reload();
    }

    private void handleUnEquip(L2PcInstance player) {
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_RHAND);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LHAND);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_CHEST);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_FEET);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_GLOVES);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_HEAD);
        player.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_LEGS);
        InventoryUpdate iu = new InventoryUpdate();
        player.sendPacket(iu);

        for (L2ItemInstance item : player.getInventory().getItems()) {
            if(item.getVisualItemId() > 0){
                item.setVisualItemId(0);
                item.setOldVisualItemId(0);
                DressMeHandler.updateVisualInDb(item, 0);
                player.broadcastUserInfo();
            }
        }
        for (L2ItemInstance item : player.getWarehouse().getItems()) {
            if(item.getVisualItemId() > 0){
                item.setVisualItemId(0);
                item.setOldVisualItemId(0);
                DressMeHandler.updateVisualInDb(item, 0);
                player.broadcastUserInfo();
            }
        }
    }

}
