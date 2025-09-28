/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://l2jpsproject.eu/>.
 */
package gabriel.scriptsGab.classBase;


import gabriel.config.GabConfig;
import gr.sr.interf.SunriseEvents;
import javolution.text.TextBuilder;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.Managers.BaseBBSManager;
import l2r.gameserver.data.xml.impl.ClassListData;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.AcquireSkillType;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.model.items.L2Item;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.AcquireSkillList;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Util;

import java.util.List;
import java.util.StringTokenizer;

/**
 * @RobikBobik
 */
public class ClassBBSManager extends BaseBBSManager {
    private static ClassBBSManager _Instance = null;


    public static ClassBBSManager getInstance() {
        if (_Instance == null) {
            _Instance = new ClassBBSManager();
        }
        return _Instance;
    }

    @Override
    public void cbByPass(String command, L2PcInstance activeChar) {
        if (SunriseEvents.isRegistered(activeChar) || SunriseEvents.isInEvent(activeChar) || activeChar.isInTournament() || (activeChar.isInArenaEvent())) {
            activeChar.sendMessage("Cannot use this service right now");
            return;
        }
        if (!activeChar.isInsideZone(ZoneIdType.TOWN)) {
            activeChar.sendMessage("Classes may not be changed while you are not in peaceful zone.");
            return;
        }

        ClassId classId = activeChar.getClassId();
        int jobLevel = classId.level();
        int level = activeChar.getLevel();
        TextBuilder html = new TextBuilder("");
        html.append("<br>");
        html.append("<center>");
        if (GabConfig.ALLOW_CLASS_MASTERS_LISTCB.isEmpty() || !GabConfig.ALLOW_CLASS_MASTERS_LISTCB.contains(jobLevel)) {
            jobLevel = 3;
        }
        if ((((level >= 20) && (jobLevel == 0)) || ((level >= 40) && (jobLevel == 1)) || ((level >= 76) && (jobLevel == 2))) && GabConfig.ALLOW_CLASS_MASTERS_LISTCB.contains(jobLevel)) {
            L2Item item = ItemData.getInstance().getTemplate(GabConfig.CLASS_MASTERS_PRICE_ITEMCB);
            html.append(createTextField("Price for class is: <font color=\"LEVEL\"> " + Util.formatAdena(GabConfig.CLASS_MASTERS_PRICE_LISTCB[jobLevel]) + "</font> <font color=\"LEVEL\">" + item.getName() + "</font> Really want one click class?<br>"));
            for (ClassId cid : ClassId.values()) {
                if (cid == ClassId.inspector) {
                    continue;
                }
                if (cid.childOf(classId) && (cid.level() == (classId.level() + 1))) {
                    html.append(createButton(ClassListData.getInstance().getClass(cid).getClientCode(), "gab__bbsclass;change_class;" + cid.getId() + ";" + GabConfig.CLASS_MASTERS_PRICE_LISTCB[jobLevel], "Button_DF_Down", "Button_DF"));
                }
            }
            html.append("</center>");
        } else {
            switch (jobLevel) {
                case 0:
                case 2:
                    html.append(createTextField("Hello " + activeChar.getName() + "! Your class is <font color=F2C202>" + activeChar.getClassId().name().toUpperCase() + "</font>."));
                    html.append(createTextField("Class level at: <font color=F2C202>20 level.</font>"));
                    html.append(createTextField("Class level at: <font color=F2C202>40 level.</font>"));
                    html.append(createTextField("Class level at:  <font color=F2C202>76 level.</font>"));
                    break;
                case 1:
                    html.append(createTextField("Congratulation " + activeChar.getName() + "! you are <font color=F2C202>" + activeChar.getClassId().name().toUpperCase() + "</font>now."));
                    break;
                case 3:
                    html.append(createTextField(("Hello " + activeChar.getName() + "! Your class is <font color=F2C202>" + activeChar.getClassId().name().toUpperCase() + "</font>.")));
                    html.append(createTextField(("Congratulation!")));

                    if (level >= 76) {
                        html.append(createTextField(("Your level is <font color=F2C202>76</font>! and Higher.")));
                    }
                    break;
            }
        }
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/scripts/services/gabriel/indexTemplate.htm");
        String template = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/template/sideTemplate.htm");

        adminReply.replace("%template%", template);
        adminReply.replace("%CHANGE%", html.toString());
        adminReply.replace("%backBypass%", "gab__bbsclass;");
        separateAndSend(adminReply.getHtml(), activeChar);

        if (command.startsWith("_bbsclass;change_class;")) {
            StringTokenizer stk = new StringTokenizer(command, ";");
            stk.nextToken();
            stk.nextToken();
            short val = Short.parseShort(stk.nextToken());
            int price = Integer.parseInt(stk.nextToken());
            L2Item item = ItemData.getInstance().getTemplate(GabConfig.CLASS_MASTERS_PRICE_ITEMCB);
            L2ItemInstance pay = activeChar.getInventory().getItemByItemId(item.getId());
            if ((pay != null) && (pay.getCount() >= price)) {
                activeChar.destroyItem("ClassMaster", pay, price, activeChar, true);
                changeClass(activeChar, val);
                cbByPass("_bbsclass;", activeChar);
            } else if (GabConfig.CLASS_MASTERS_PRICE_ITEMCB == 57) {
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
            } else {
                activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_NOT_ENOUGH_ADENA));
            }
        }
    }


    public static void showSubClassSkillList(L2PcInstance player) {
        final List<L2SkillLearn> subClassSkills = SkillTreesData.getInstance().getAvailableSubClassSkills(player);
        final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.SUBCLASS);
        int count = 0;

        for (L2SkillLearn s : subClassSkills) {
            if (SkillData.getInstance().getInfo(s.getSkillId(), s.getSkillLevel()) != null) {
                count++;
                asl.addSkill(s.getSkillId(), s.getSkillLevel(), s.getSkillLevel(), 0, 0);
            }
        }
        if (count > 0) {
            player.sendPacket(asl);
        } else {
            player.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
        }
    }

    private void changeClass(L2PcInstance activeChar, short val) {
        if (activeChar.getClassId().level() == ClassId.values()[val].level()) {
            return;
        }

        if (activeChar.getClassId().level() == 3) {
            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.THIRD_CLASS_TRANSFER));
        } else {
            activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CLASS_TRANSFER));
        }
        activeChar.setClassId(val);

        if (activeChar.getClassId().level() == 2) {
            activeChar.getInventory().addItem("bbsClassManager", 6622, 1L, activeChar, null);
        }

        if (activeChar.isSubClassActive()) {
            activeChar.getSubClasses().get(Integer.valueOf(activeChar.getClassIndex())).setClassId(activeChar.getActiveClass());
        } else {
            if (activeChar.getClassId().level() == 0) {
                activeChar.getInventory().addItem("bbsClassManager", 8869, 15L, activeChar, null);
            } else if (activeChar.getClassId().level() == 1) {
                activeChar.getInventory().addItem("bbsClassManager", 8870, 15L, activeChar, null);
            }

            activeChar.setBaseClass(activeChar.getActiveClass());
        }

        if (activeChar.getClassId().getId() == 97) {
            activeChar.getInventory().addItem("bbsClassManager", 15307, 1L, activeChar, null);
        } else if (activeChar.getClassId().getId() == 105) {
            activeChar.getInventory().addItem("bbsClassManager", 15308, 1L, activeChar, null);
        } else if (activeChar.getClassId().getId() == 112) {
            activeChar.getInventory().addItem("bbsClassManager", 15309, 4L, activeChar, null);
        }

        activeChar.broadcastUserInfo();
    }


    protected boolean checkVillageMasterRace(PlayerClass pclass) {
        return true;
    }

    protected boolean checkVillageMasterTeachType(PlayerClass pclass) {
        return true;
    }

    public final boolean checkVillageMaster(int classId) {
        return checkVillageMaster(PlayerClass.values()[classId]);
    }

    public final boolean checkVillageMaster(PlayerClass pclass) {
        if (Config.ALT_GAME_SUBCLASS_EVERYWHERE) {
            return true;
        }
        return checkVillageMasterRace(pclass) && checkVillageMasterTeachType(pclass);
    }


    protected boolean checkQuests(L2PcInstance player) {
        // Noble players can add Sub-Classes without quests
        if (player.isNoble()) {
            return true;
        }

        QuestState qs = player.getQuestState("234_FatesWhisper");
        if ((qs == null) || !qs.isCompleted()) {
            return false;
        }

        qs = player.getQuestState("235_MimirsElixir");
        if ((qs == null) || !qs.isCompleted()) {
            return false;
        }

        return true;
    }

    protected String getSubClassMenu(Race pRace) {
        if (Config.ALT_GAME_SUBCLASS_EVERYWHERE || (pRace != Race.KAMAEL)) {
            return "data/html/villagemaster/SubClass.htm";
        }

        return "data/html/villagemaster/SubClass_NoOther.htm";
    }

    @Override
    public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {
    }
}