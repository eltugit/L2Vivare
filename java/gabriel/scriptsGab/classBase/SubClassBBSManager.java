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


import gabriel.Utils.GabUtils;
import gabriel.cbbCertif.CertificationManager;
import gabriel.config.GabConfig;
import gr.sr.interf.SunriseEvents;
import gr.sr.utils.StringUtil;
import javolution.text.TextBuilder;
import l2r.Config;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.Managers.BaseBBSManager;
import l2r.gameserver.data.xml.impl.ClassListData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.SkillTreesData;
import l2r.gameserver.enums.Race;
import l2r.gameserver.instancemanager.QuestManager;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.L2SkillLearn;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.AcquireSkillType;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.model.base.SubClass;
import l2r.gameserver.model.holders.ItemHolder;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.quest.QuestState;
import l2r.gameserver.model.quest.State;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.AcquireSkillList;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Util;

import java.util.*;
import java.util.logging.Logger;

/**
 * @Gabriel Costa Souza
 */
public class SubClassBBSManager extends BaseBBSManager {
    private static SubClassBBSManager _Instance = null;
    private static Logger _log = Logger.getLogger(SubClassBBSManager.class.getName());
    private static String MAINHTML = "data/html/scripts/services/gabriel/indexTemplate.htm";
    private static String SIDEHTML = "data/html/scripts/services/gabriel/template/sideTemplate.htm";

    int COMMONITEM = 10280;
    int ENHANCEDITEM = 10612;
    int[] WARRIORCLASSES =
            {
                    3,
                    88,
                    2,
                    89,
                    46,
                    48,
                    113,
                    114,
                    55,
                    117,
                    56,
                    118,
                    127,
                    131,
                    128,
                    129,
                    132,
                    133
            };
    int[] ROGUECLASSES =
            {
                    9,
                    92,
                    24,
                    102,
                    37,
                    109,
                    130,
                    134,
                    8,
                    93,
                    23,
                    101,
                    36,
                    108
            };
    int[] KNIGHTCLASSES =
            {
                    5,
                    90,
                    6,
                    91,
                    20,
                    99,
                    33,
                    106
            };
    int[] SUMMONERCLASSES =
            {
                    14,
                    96,
                    28,
                    104,
                    41,
                    111
            };
    int[] WIZARDCLASSES =
            {
                    12,
                    94,
                    13,
                    95,
                    27,
                    103,
                    40,
                    110
            };
    int[] HEALERCLASSES =
            {
                    16,
                    97,
                    30,
                    105,
                    43,
                    112
            };
    int[] ENCHANTERCLASSES =
            {
                    17,
                    98,
                    21,
                    100,
                    34,
                    107,
                    51,
                    115,
                    52,
                    116,
                    135,
                    136
            };
    int[] CLASSITEMS =
            {
                    10281,
                    10282,
                    10283,
                    10287,
                    10284,
                    10286,
                    10285
            };
    int[] TRANSFORMITEMS =
            {
                    10289,
                    10288,
                    10290,
                    10293,
                    10292,
                    10294,
                    10291
            };
    public static final String[] _questVarNames =
            {
                    "EmergentAbility65-",
                    "EmergentAbility70-",
                    "ClassAbility75-",
                    "ClassAbility80-"
            };
    private static final int[] _itemsIds =
            {
                    10280,
                    10281,
                    10282,
                    10283,
                    10284,
                    10285,
                    10286,
                    10287,
                    10288,
                    10289,
                    10290,
                    10291,
                    10292,
                    10293,
                    10294,
                    10612
            };

    public static SubClassBBSManager getInstance() {
        if (_Instance == null) {
            _Instance = new SubClassBBSManager();
        }
        return _Instance;
    }

    @Override
    public void cbByPass(String command, L2PcInstance activeChar) {


        if (SunriseEvents.isRegistered(activeChar) || SunriseEvents.isInEvent(activeChar) || activeChar.isInTournament() || activeChar.isInArenaEvent()) {
            activeChar.sendMessage("Cannot use this service right now");
            return;
        }
//        if (!activeChar.isInsideZone(ZoneIdType.TOWN)) {
//            activeChar.sendMessage("Sub classes may not be created or changed while you are not in peaceful zone.");
//            _log.info("Player " + activeChar.getName() + " trying to cheating with Subclass. But this bug is not working on L2GABRI(wasnt inside peace zone) ");
//            return;
//        }


        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        // Subclasses may not be changed while a skill is in use.
        adminReply.setFile(activeChar, activeChar.getLang(), MAINHTML);
        String template = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), SIDEHTML);

        if (activeChar.isCastingNow() || activeChar.isAllSkillsDisabled()) {
            activeChar.sendPacket(SystemMessageId.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
            return;
        } else if (activeChar.isInCombat()) {
            activeChar.sendMessage("Sub classes may not be created or changed while being in combat.");
            return;
        }
//        else if (activeChar.isInParty()) {
//            activeChar.sendMessage("Sub classes may not be created or changed while being in party.");
//            return;
//        }
//        else if (!activeChar.isInsideZone(ZoneIdType.PEACE)) {
//            activeChar.sendMessage("Sub classes may not be created or changed while you are not in peaceful zone.");
//            return;
//        }
        if (activeChar.getInstanceId() > 0) {
            activeChar.sendMessage("Sub classes may not be created or changed while in Instance");
            return;
        }

        // Subclasses may not be changed while a transformated state.
        if (activeChar.getTransformation() != null) {
            String dummy = "<tr>\n" +
                    "<tr>\n" +
                    "<td><center>%subclass%</center></td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td><center>%list%</center></td>\n" +
                    "</tr>";
            adminReply.replace("%CHANGE%", dummy);
            adminReply.replace("%subclass%", "You cannot change class while transformed");
            adminReply.replace("%template%", template);
            adminReply.replace("%backBypass%", "gab__bbssubclass;");
            separateAndSend(adminReply.getHtml(), activeChar);
            activeChar.sendPacket(adminReply);

            return;
        }
        // Subclasses may not be changed while a summon is active.
        if (activeChar.hasPet()) {
            String dummy = "<tr>\n" +
                    "<tr>\n" +
                    "<td><center>%subclass%</center></td>\n" +
                    "</tr>\n" +
                    "<tr>\n" +
                    "<td><center>%list%</center></td>\n" +
                    "</tr>";
            adminReply.replace("%CHANGE%", dummy);
            adminReply.replace("%subclass%", "You cannot change class with a summon alive!");
            adminReply.replace("%template%", template);
            adminReply.replace("%backBypass%", "gab__bbssubclass;");
            separateAndSend(adminReply.getHtml(), activeChar);
            activeChar.sendPacket(adminReply);
            return;
        }


//        if (activeChar.getParty() != null) {
//            adminReply.setFile(activeChar, activeChar.getLang(), MAINHTML);
//            String dummy = "<tr>\n" +
//                    "<tr>\n" +
//                    "<td><center>%subclass%</center></td>\n" +
//                    "</tr>\n" +
//                    "<tr>\n" +
//                    "<td><center>%list%</center></td>\n" +
//                    "</tr>";
//            adminReply.replace("%CHANGE%", dummy);
//            adminReply.replace("%template%", template);
//            adminReply.replace("%backBypass%", "gab__bbssubclass;");
//            adminReply.replace("%subclass%", "It is impossible to change sub classes when you are in a party!");
//            separateAndSend(adminReply.getHtml(), activeChar);
//            return;
//        }

//        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassChanger.htm");
//        separateAndSend(adminReply.getHtml(), activeChar);


        if (command.startsWith("_bbssubclass;")) {
            String actualText = "";
            adminReply.setFile(activeChar, activeChar.getLang(), MAINHTML);
            adminReply.replace("%template%", template);
            int cmdChoice = 0;
            int paramOne = 0;
            int paramTwo = 0;

            TextBuilder htm = new TextBuilder("");
            TextBuilder htm2 = new TextBuilder("");
            try {
                // _bbssubclass; 13
                cmdChoice = Integer.parseInt(command.substring(14, 15).trim());

                int endIndex = command.indexOf(' ', 16);
                if (endIndex == -1) {
                    endIndex = command.length();
                }

                if (command.length() > 16) {
                    paramOne = Integer.parseInt(command.substring(15, endIndex).trim());
                    if (command.length() > endIndex) {
                        paramTwo = Integer.parseInt(command.substring(endIndex).trim());
                    }
                }
            } catch (Exception NumberFormatException) {
                // _log.warning(ClassBBSManager.class.getName() + ": Wrong numeric values for command " + command);
            }
            Set<PlayerClass> subsAvailable = null;
            switch (cmdChoice) {
                case 0: // Subclass change menu
                    clearVars(activeChar);
                    actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChanger.htm");
                    adminReply.replace("%CHANGE%", actualText);
                    adminReply.replace("%error%", "");
                    adminReply.replace("%backBypass%", "gab__bbssubclass;");
                    separateAndSend(adminReply.getHtml(), activeChar);
                    break;
                case 1: // Add Subclass - Initial
                    // Avoid giving player an option to add a new sub class, if they have max sub-classes already.
                    clearVars(activeChar);
                    if (activeChar.getLevel() < 75) {
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("You need to be level 75 to be able to access the sub-class manager"));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        break;
                    }
//                    else if (activeChar.getParty() != null) {
//
//                        adminReply.replace("%CHANGE%", "");
//                        adminReply.replace("%error%", getErrorMessage("It is impossible to change sub classes when you are in a party!"));
//                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
//                        separateAndSend(adminReply.getHtml(), activeChar);
//                        break;
//                    }
                    else if (activeChar.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("You aren't eligible to add a subclass at this time."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        break;
                    }

                    subsAvailable = getAvailableSubClasses(activeChar);
                    if ((subsAvailable != null) && !subsAvailable.isEmpty()) {

                        actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChangerList.htm");
                        int i = 0;
                        htm2.append("<tr>");
                        for (PlayerClass subClass : subsAvailable) {
                            htm2.append("<td width=200 align=center><button value=\"" + formatClassForDisplay(subClass) + "\"action=\"bypass -h gab__bbssubclass; 4 " + String.valueOf(subClass.ordinal()) + "\" msg=\"1268; " + formatClassForDisplay(subClass) + "\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");

                            if (i % 2 == 0) {
                                htm2.append("</tr>");
                                htm2.append("<tr>");
                            }
                            i++;
                        }
                        htm2.append("</tr>");
                        adminReply.replace("%CHANGE%", actualText);
                        adminReply.replace("%feedback%", "Which of the following would you like to add as a subclass?");
                        adminReply.replace("%list%", htm2.toString());
                        adminReply.replace("%error%", "");
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {
                        if (((activeChar.getRace() == Race.ELF)) || (activeChar.getRace() == Race.DARK_ELF)) {

                            adminReply.replace("%CHANGE%", "");
                            adminReply.replace("%error%", getErrorMessage("Elves and Dark Elves may not use each other's subclasses"));
                            adminReply.replace("%backBypass%", "gab__bbssubclass;");
                            separateAndSend(adminReply.getHtml(), activeChar);
                        } else if (activeChar.getRace() == Race.KAMAEL) {
                            adminReply.replace("%CHANGE%", "");
                            adminReply.replace("%error%", getErrorMessage("Unfortunately, you are not eligible to discuss sub class."));
                            adminReply.replace("%backBypass%", "gab__bbssubclass;");
                            separateAndSend(adminReply.getHtml(), activeChar);
                        } else {
                            adminReply.replace("%CHANGE%", "");
                            adminReply.replace("%error%", getErrorMessage("There are no sub classes available at this time."));
                            adminReply.replace("%backBypass%", "gab__bbssubclass;");
                            separateAndSend(adminReply.getHtml(), activeChar);

                        }
                        return;
                    }
                case 2: // Change Class - Initial
                    clearVars(activeChar);
                    if (activeChar.getSubClasses().isEmpty()) {
                        // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_ChangeNo.htm");

                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("It is impossible to change sub classes when you have no sub class. First, add a sub class."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        break;

                    }
//                    else if (activeChar.getParty() != null) {
//
//                        adminReply.replace("%CHANGE%", "");
//                        adminReply.replace("%error%", getErrorMessage("It is impossible to change sub classes when you are in a party!"));
//                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
//                        separateAndSend(adminReply.getHtml(), activeChar);
//
//                        break;
//                    }
                    else {
                        final StringBuilder content2 = StringUtil.startAppend(200);
                        StringUtil.append(content2, "<tr>");

                        if (checkVillageMaster(activeChar.getBaseClass())) {
                            StringUtil.append(content2, "<td><button value=\"", ClassListData.getInstance().getClass(activeChar.getBaseClass()).getClientCode(), "\"action=\"bypass -h gab__bbssubclass; 5 0\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td><br>");
                            StringUtil.append(content2, "</tr>");
                            StringUtil.append(content2, "<tr>");
                        }

                        for (Iterator<SubClass> subList = iterSubClasses(activeChar); subList.hasNext(); ) {
                            SubClass subClass = subList.next();
                            if (checkVillageMaster(subClass.getClassDefinition())) {
                                StringUtil.append(content2, "<td><button value=\"", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "\"action=\"bypass -h gab__bbssubclass; 5 " + String.valueOf(subClass.getClassIndex()), "\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td><br>");
                                StringUtil.append(content2, "</tr>");
                                StringUtil.append(content2, "<tr>");
                            }
                        }
                        StringUtil.append(content2, "</tr>");
                        if (content2.length() > 0) {

                            // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_Change.htm");
                            actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChangerList.htm");
                            adminReply.replace("%CHANGE%", actualText);
                            adminReply.replace("%feedback%", "Please select your new subclass.");
                            adminReply.replace("%list%", content2.toString());
                            adminReply.replace("%error%", "");
                            adminReply.replace("%backBypass%", "gab__bbssubclass;");
                            separateAndSend(adminReply.getHtml(), activeChar);

                        } else {
                            // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_ChangeNotFound.htm");

                            adminReply.replace("%CHANGE%", "");
                            adminReply.replace("%error%", getErrorMessage("It is not possible for me to change your Sub Class, I'm afraid. Why don't you go find a Master of your Sub Class ?"));
                            adminReply.replace("%backBypass%", "gab__bbssubclass;");
                            separateAndSend(adminReply.getHtml(), activeChar);

                        }
                    }
                    break;
                case 3: // Change/Cancel Subclass - Initial
                    if ((activeChar.getSubClasses() == null) || activeChar.getSubClasses().isEmpty()) {
                        // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_ChangeNotFound.htm");

                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("Your subclass list is empty!"));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        break;
                    }

                    // custom value
                    if (activeChar.getTotalSubClasses() > 3) {
                        // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_ModifyCustom.htm");
                        int i = 0;
                        final StringBuilder content3 = StringUtil.startAppend(200);

                        int classIndex = 1;

//                        String pageS = activeChar.getQuickVar("currPageSubBBS", "1");
//                        activeChar.setQuickVar("currPageSubBBS", pageS);
//                        int page = Integer.parseInt(pageS);
//                        final int itemsPerPage = 8;
//                        int counter = 0;
//                        int laterIndex = 0;
//
//                        if (i == 0) {
//                            StringUtil.append(content3, "<table><tr>");
//                        }
//                        for (i = (page - 1) * itemsPerPage; i < listSubClasses(activeChar).size(); i++) {
//                            SubClass subClass = listSubClasses(activeChar).get(i);
//                            laterIndex = i+1;
//                            if (subClass != null) {
//                                if (((i % 2) == 0)) {
//                                    StringUtil.append(content3, "<table><tr>");
//                                }
//                                StringUtil.append(content3, "<td>Sub-class ", String.valueOf(classIndex++), "</td><br>", "<td><button value=\"", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "\"action=\"bypass -h gab__bbssubclass; 6 ", String.valueOf(subClass.getClassIndex()), "\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td><br>");
//                                i++;
//                                if ((i == 2) || ((i % 4) == 0)) {
//                                    StringUtil.append(content3, "</tr></table>");
//                                }
//                            }
//                            counter++;
//                            if (counter >= itemsPerPage) {
//                                break;
//                            }
//                        }
//                        if ((i % 4) != 0) {
//                            StringUtil.append(content3, "</tr></table>");
//                        }
//                        boolean hasNextItem;
//                        try {
//                            SubClass sub = listSubClasses(activeChar).get(laterIndex);
//                            hasNextItem = true;
//                        }catch (IndexOutOfBoundsException e){
//                            hasNextItem = false;
//                        }


                        if (i == 0) {
                            StringUtil.append(content3, "<table><tr>");
                        }
                        for (Iterator<SubClass> subList = iterSubClasses(activeChar); subList.hasNext(); ) {
                            SubClass subClass = subList.next();
                            if (((i % 2) == 0)) {
                                StringUtil.append(content3, "<table><tr>");
                            }
                            StringUtil.append(content3, "<td>Sub-class ", String.valueOf(classIndex++), "</td><br>", "<td><button value=\"", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "\"action=\"bypass -h gab__bbssubclass; 6 ", String.valueOf(subClass.getClassIndex()), "\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td><br>");
                            i++;
                            if ((i == 2) || ((i % 4) == 0)) {
                                StringUtil.append(content3, "</tr></table>");
                            }
                        }
                        if ((i % 4) != 0) {
                            StringUtil.append(content3, "</tr></table>");
                        }

//                        StringUtil.append(content3, handleNavigation(hasNextItem, activeChar));


                        actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChangerSelect.htm");
                        adminReply.replace("%CHANGE%", actualText);
                        adminReply.replace("%feedback%", "Which of the following sub classes would you like to change?");
                        adminReply.replace("%list%", content3.toString());
                        adminReply.replace("%error%", "");
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {
                        // retail html contain only 3 subclasses
                        // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_Modify.htm");

                        actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChangerSelect.htm");
                        adminReply.replace("%template%", template);
                        adminReply.replace("%CHANGE%", actualText);
                        adminReply.replace("%feedback%", "Which of the following sub classes would you like to change?");


                        String tsub1 = createTextField("Sub-class 1");
                        String tsub2 = createTextField("Sub-class 2");
                        String tsub3 = createTextField("Sub-class 3");
                        String bsub1 = createButton("%sub1%", "gab__bbssubclass; 6 1", "Button_DF_Down", "button_df");
                        String bsub2 = createButton("%sub2%", "gab__bbssubclass; 6 2", "Button_DF_Down", "button_df");
                        String bsub3 = createButton("%sub3%", "gab__bbssubclass; 6 3", "Button_DF_Down", "button_df");

                        htm2.append("<table>");
                        htm2.append(tsub1);
                        htm2.append(bsub1);
                        htm2.append(tsub2);
                        htm2.append(bsub2);
                        htm2.append(tsub3);
                        htm2.append(bsub3);
                        htm2.append("</table>");
                        adminReply.replace("%list%", htm2.toString());

                        if (activeChar.getSubClasses().containsKey(1)) {
                            adminReply.replace("%sub1%", ClassListData.getInstance().getClass(activeChar.getSubClasses().get(1).getClassId()).getClientCode());
                        } else {
                            adminReply.replace(tsub1, "");
                            adminReply.replace(bsub1, "");
                        }

                        if (activeChar.getSubClasses().containsKey(2)) {
                            adminReply.replace("%sub2%", ClassListData.getInstance().getClass(activeChar.getSubClasses().get(2).getClassId()).getClientCode());
                        } else {
                            adminReply.replace(tsub2, "");
                            adminReply.replace(bsub2, "");
                        }

                        if (activeChar.getSubClasses().containsKey(3)) {
                            adminReply.replace("%sub3%", ClassListData.getInstance().getClass(activeChar.getSubClasses().get(3).getClassId()).getClientCode());
                        } else {
                            adminReply.replace(tsub3, "");
                            adminReply.replace(bsub3, "");
                        }

                        adminReply.replace("%error%", "");
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }

                case 4: // Add Subclass - Action (Subclass 4 x[x])
                    /**
                     * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice.
                     */
                    if (!activeChar.getFloodProtectors().getSubclass().tryPerformAction("add subclass")) {
//                        _log.warning(SubClassBBSManager.class.getName() + ": Player " + activeChar.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    boolean allowAddition = true;

                    if (activeChar.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                        allowAddition = false;
                    }

                    if (activeChar.getLevel() < 75) {
                        allowAddition = false;
                    }

                    if (allowAddition) {
                        if (!activeChar.getSubClasses().isEmpty()) {
                            for (Iterator<SubClass> subList = iterSubClasses(activeChar); subList.hasNext(); ) {
                                SubClass subClass = subList.next();

                                if (subClass.getLevel() < 75) {
                                    allowAddition = false;
                                    break;
                                }
                            }
                        }
                    }

                    /**
                     * If quest checking is enabled, verify if the character has completed the Mimir's Elixir (Path to Subclass) and Fate's Whisper (A Grade Weapon) quests by checking for instances of their unique reward items. If they both exist, remove both unique items and continue with adding
                     * the sub-class.
                     */
                    if (allowAddition && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS) {
                        allowAddition = checkQuests(activeChar);
                    }

                    if (allowAddition && isValidNewSubClass(activeChar, paramOne)) {
                        if (!activeChar.addSubClass(paramOne, activeChar.getTotalSubClasses() + 1)) {
                            return;
                        }

                        activeChar.setActiveClass(activeChar.getTotalSubClasses());

                        separateAndSend(adminReply.getHtml(), activeChar);
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("Congratulations! You've added a new subclass. Open the Character Information window (ALT + T) to confirm."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        activeChar.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass added.

                        return;
                    } else {
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("You aren't eligible to add a subclass at this time."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }

                case 5: // Change Class - Action
                    /**
                     * If the character is less than level 75 on any of their previously chosen classes then disallow them to change to their most recently added sub-class choice. Note: paramOne = classIndex
                     */
                    if (!activeChar.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
//                        _log.warning(SubClassBBSManager.class.getName() + ": Player " + activeChar.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    L2Party party = activeChar.getParty();

                    final int classToBe;
                    if (paramOne == 0) {
                        classToBe = activeChar.getBaseClass();
                    } else {
                        classToBe = activeChar.getSubClasses().get(paramOne).getClassId();
                    }

                    if (GabConfig.ALLOW_PARTY_LIMITATIONS && party != null) {
                        if (((Arrays.stream(GabUtils.BISHOPS).anyMatch(i -> i == classToBe))) && (party.getBishops() >= GabUtils.MAX_BISHOPS)) {
                            activeChar.sendMessage("Your Party already has " + GabUtils.MAX_BISHOPS + " Cardinal");
                            sendErrorMessage(activeChar, "Your Party already has " + GabUtils.MAX_BISHOPS + " Cardinal");
                            return;
                        }
                        if (((Arrays.stream(GabConfig.HEALLERS).anyMatch(i -> i == classToBe))) && (party.getHeallers() >= GabConfig.MAX_HEALERS)) {
                            activeChar.sendMessage("Your Party already has " + GabConfig.MAX_HEALERS + " Evas Saint, Shillien Saint or Judicator");
                            sendErrorMessage(activeChar, "Your Party already has " + GabConfig.MAX_HEALERS + " Evas Saint, Shillien Saint or Judicator");
                            return;
                        }
                        if (((Arrays.stream(GabConfig.TANKS).anyMatch(i -> i == classToBe))) && (party.getTanks() >= GabConfig.MAX_TANKS)) {
                            activeChar.sendMessage("Your Party already has " + GabConfig.MAX_TANKS + " Tanker");
                            sendErrorMessage(activeChar, "Your Party already has " + GabConfig.MAX_TANKS + " Tanker");
                            return;
                        }
                        if ((classToBe == 115 || classToBe == 51) && party.getDominator() >= GabConfig.MAX_DOMINATORS) {
                            activeChar.sendMessage("Your Party already has " + GabConfig.MAX_DOMINATORS + "Dominator");
                            sendErrorMessage(activeChar, "Your Party already has " + GabConfig.MAX_DOMINATORS + "Dominator");
                            return;
                        }
                    }

                    if (activeChar.getClassIndex() == paramOne) {

                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("Um, I don't think so. That is your current subclass... Select another subclass."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);

                        break;
                    }

                    if (paramOne == 0) {
                        if (!checkVillageMaster(activeChar.getBaseClass())) {
                            return;
                        }
                    } else {
                        try {
                            if (!checkVillageMaster(activeChar.getSubClasses().get(paramOne).getClassDefinition())) {
                                return;
                            }
                        } catch (NullPointerException e) {
                            return;
                        }
                    }

                    activeChar.setActiveClass(paramOne);
                    activeChar.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED); // Transfer completed.
                    adminReply.replace("%CHANGE%", "");
                    adminReply.replace("%error%", getErrorMessage("You've changed subclasses. Come see me if you wish to change subclasses again."));
                    adminReply.replace("%backBypass%", "gab__bbssubclass;");
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                case 6: // Change/Cancel Subclass - Choice
                    // validity check

                    if ((paramOne < 1) || (paramOne > Config.MAX_SUBCLASS)) {
                        return;
                    }
                    subsAvailable = getAvailableSubClasses(activeChar);
                    // another validity check
                    if ((subsAvailable == null) || subsAvailable.isEmpty()) {
                        activeChar.sendMessage("There are no sub classes available at this time.");
                        return;
                    }
                    int i = 0;

                    final StringBuilder content6 = StringUtil.startAppend(200);

                    StringUtil.append(content6, "<tr>");
                    for (PlayerClass subClass : subsAvailable) {
                        StringUtil.append(content6, "<td width=200 align=center><button value=\"", formatClassForDisplay(subClass), "\"action=\"bypass -h gab__bbssubclass; 7 ", String.valueOf(paramOne), " ", String.valueOf(subClass.ordinal()), "\" msg=\"1445;", "\" width=150 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
                        if (i % 2 == 0) {
                            StringUtil.append(content6, "</tr>");
                            StringUtil.append(content6, "<tr>");
                        }
                        i++;
                    }
                    StringUtil.append(content6, "</tr>");

                    actualText = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/scripts/services/gabriel/subclass/subclassChangerList.htm");
                    adminReply.replace("%CHANGE%", actualText);
                    switch (paramOne) {
                        case 1:
                            adminReply.replace("%feedback%", "Which of the following sub-classes would you like to add as your current first sub-class?");
                            break;
                        case 2:
                            adminReply.replace("%feedback%", "Which of the following sub-classes would you like to add as your current second sub-class?");
                            break;
                        case 3:
                            adminReply.replace("%feedback%", "Which of the following sub-classes would you like to add as your current third sub-class?");
                            break;
                        default:
                            adminReply.replace("%feedback%", "Please choose a sub class to change to. If the one you are looking for is not here, please seek out the appropriate master for that class.");
                    }
                    adminReply.replace("%list%", content6.toString());
                    adminReply.replace("%error%", "");
                    adminReply.replace("%backBypass%", "gab__bbssubclass;");
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                case 7: // Change Subclass - Action
                    /**
                     * Warning: the information about this subclass will be removed from the subclass list even if false!
                     */
                    if (!activeChar.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
//                        _log.warning(SubClassBBSManager.class.getName() + ": Player " + activeChar.getName() + " has performed a subclass change too fast");
                        return;
                    }

                    if (!isValidNewSubClass(activeChar, paramTwo)) {
                        return;
                    }

                    if (activeChar.modifySubClass(paramOne, paramTwo)) {
                        activeChar.abortCast();
                        activeChar.stopAllEffectsExceptThoseThatLastThroughDeath(); // all effects from old subclass stopped!
                        //activeChar.stopAllEffectsNotStayOnSubclassChange();
                        activeChar.stopCubics();
                        activeChar.setActiveClass(paramOne);

                        // html.setFile(activeChar.getHtmlPrefix(), "data/html/villagemaster/SubClass_ModifyOk.htm");
                        // adminReply.replace("%list%", CharTemplateTable.getInstance().getClass(paramTwo).getClientCode());


                        activeChar.setActiveClass(paramOne);
                        activeChar.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS); // Subclass completed.
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("You've changed subclasses. Come see me if you wish to change subclasses again."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {
                        /**
                         * This isn't good! modifySubClass() removed subclass from memory we must update _classIndex! Else IndexOutOfBoundsException can turn up some place down the line along with other seemingly unrelated problems.
                         */
                        activeChar.setActiveClass(0); // Also updates _classIndex plus switching _classid to baseclass.

                        activeChar.sendMessage("The sub class could not be added, you have been reverted to your base class.");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        adminReply.replace("%CHANGE%", "");
                        adminReply.replace("%error%", getErrorMessage("The sub class could not be added, you have been reverted to your base class."));
                        adminReply.replace("%backBypass%", "gab__bbssubclass;");
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
            }
            if (command.startsWith("_bbssubclass;obsNext")) {
                String pageS = activeChar.getQuickVar("currPageSubBBS", "1");
                int page = Integer.parseInt(pageS);
                page++;
                String toStore = String.valueOf(page);
                activeChar.setQuickVar("currPageTour", toStore);
                cbByPass("gab__bbssubclass; 3", activeChar);

            } else if (command.startsWith("_bbssubclass;obsPrev")) {
                String pageS = activeChar.getQuickVar("currPageSubBBS", "1");
                int page = Integer.parseInt(pageS);
                page--;
                if (page < 1) {
                    page = 1;
                }
                String toStore = String.valueOf(page);
                activeChar.setQuickVar("currPageTour", toStore);
                cbByPass("gab__bbssubclass; 3", activeChar);
            }

            if (command.startsWith("_bbssubclass;pomander;")) {
                if (!canGetPomander(activeChar)) {
                    activeChar.sendMessage("Your class cannot learn pomander skills.");
                    return;
                }
                activeChar.setQuickVar("pomanderUse", true);
                if ((activeChar.getLevel() < 76) || (activeChar.getClassId().level() < 3)) {
                    activeChar.sendMessage("Your level is too low");
                    return;
                }

                final AcquireSkillList asl = new AcquireSkillList(AcquireSkillType.TRANSFER);
                int count = 0;
                for (L2SkillLearn skillLearn : SkillTreesData.getInstance().getAvailableTransferSkills(activeChar)) {
                    if (SkillData.getInstance().getInfo(skillLearn.getSkillId(), skillLearn.getSkillLevel()) != null) {
                        count++;
                        asl.addSkill(skillLearn.getSkillId(), skillLearn.getSkillLevel(), skillLearn.getSkillLevel(), skillLearn.getLevelUpSp(), 0);
                    }
                }

                if (count > 0) {
                    activeChar.sendPacket(asl);
                } else {
                    activeChar.sendPacket(SystemMessageId.NO_MORE_SKILLS_TO_LEARN);
                }
                cbByPass("_bbssubclass;", activeChar);
            } else if (command.startsWith("_bbssubclass;pomanderRefresh;")) {

                if (!canGetPomander(activeChar)) {
                    activeChar.sendMessage("Your class cannot learn pomander skills.");
                    return;
                }

                if ((activeChar.getLevel() < 76) || (activeChar.getClassId().level() < 3)) {
                    activeChar.sendMessage("Your level is too low to be cleansed");
                    return;
                }

                if (activeChar.getAdena() < Config.FEE_DELETE_TRANSFER_SKILLS) {
                    activeChar.sendPacket(SystemMessageId.CANNOT_RESET_SKILL_LINK_BECAUSE_NOT_ENOUGH_ADENA);
                    return;
                }

                if (hasTransferSkillItems(activeChar)) {
                    activeChar.sendMessage("Come back when you have used all transfer skill items for this class.");
                    return;
                } else {
                    boolean hasSkills = false;
                    final Collection<L2SkillLearn> skills = SkillTreesData.getInstance().getTransferSkillTree(activeChar.getClassId()).values();
                    for (L2SkillLearn skillLearn : skills) {
                        final L2Skill skill = activeChar.getKnownSkill(skillLearn.getSkillId());
                        if (skill != null) {
                            activeChar.removeSkill(skill);
                            for (ItemHolder item : skillLearn.getRequiredItems()) {
                                activeChar.addItem("Cleanse", item.getId(), item.getCount(), null, true);
                            }
                            hasSkills = true;
                        }
                    }

                    // Adena gets reduced once.
                    if (hasSkills) {
                        activeChar.reduceAdena("Cleanse", Config.FEE_DELETE_TRANSFER_SKILLS, null, true);
                    }
                }

            }
            // separateAndSend(adminReply.getHtml(), activeChar);

            if (command.startsWith("_bbssubclass;certific;")) {

                if (GabConfig.ALLOW_COMMUNITY_CERTIFIC) {
                    CertificationManager.getInstance().parseCommand("_certi", activeChar);
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifStart.htm");
                    htm.append("Please, chose your option");
                    adminReply.replace("%feedback%", htm.toString());
                    adminReply.replace("%addCertif%", "<button value=\"Get Certifications\" action=\"bypass -h gab__bbssubclass;getcertified;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">");
                    adminReply.replace("%removeCert%", "<button value=\"Cancel Certifications\" action=\"bypass -h gab__bbssubclass;removecertified;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">");
                    adminReply.replace("%addSkillCertification%", "<button value=\"Add Skills Certifications\" action=\"bypass -h gab__bbssubclass;skillcertified;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">");

                    separateAndSend(adminReply.getHtml(), activeChar);
                }

            } else if (command.startsWith("_bbssubclass;getcertified;")) {
                if (activeChar.isSubClassActive()) {
                    QuestState st = activeChar.getQuestState("SubClassCertification");
                    if ((st == null) || !st.isCompleted()) {
                        st = QuestManager.getInstance().getQuest("SubClassCertification").newQuestState(activeChar);
                        st.setState(State.STARTED);
                        st.set("cond", "0");
                    }
                    if (Config.ALT_GAME_SUBCLASS_EVERYWHERE) {
                        if (activeChar.getLevel() >= 65) {

                            adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                            htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                            htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                            htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                            htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                            htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                            adminReply.replace("%list%", htm.toString());
                            adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                            separateAndSend(adminReply.getHtml(), activeChar);
                        } else {
                            adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                            adminReply.replace("%feedback%", htm.toString());
                            htm.append("Your level is not yet sufficient to obtain a certificate. (65, 70, 75, 80)");
                            separateAndSend(adminReply.getHtml(), activeChar);
                        }
                    } else {
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("I'm sorry, but I cannot give you a certificate for your current occupation. You should go to the Master of your Occupation");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("If you want to obtain the certificate, you should come as a form of subclass you wish to apply.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                }

            } else if (command.startsWith("_bbssubclass;obtain65;")) {
                String qn = "SubClassCertification";
                QuestState st = activeChar.getQuestState(qn);
                String prefix = "-" + String.valueOf(activeChar.getClassIndex());
                String var = "";
                String isAvailable65 = st.getGlobalQuestVar("EmergentAbility65" + prefix);
                int itemId = 0;
                int levelChar = 65;

                if ((isAvailable65 == "") || (isAvailable65.equals("0"))) {
                    if (activeChar.getLevel() > 64) {
                        itemId = COMMONITEM;
                        var = "EmergentAbility" + String.valueOf(levelChar) + prefix;
                        getCertified(activeChar, itemId, var);

                        // adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        // htm.append("Congratz, you got the Emergent for the level 65.");
                        // adminReply.replace("%feedback%", htm.toString());
                        // separateAndSend(adminReply.getHtml(), activeChar);
                        // return;
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                        htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                        adminReply.replace("%list%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {

                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("You are not yet ready to receive your level 65 certification. Work hard and come back later.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("You have already received the certificate for this skill.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                }

            } else if (command.startsWith("_bbssubclass;obtain70;")) {
                String qn = "SubClassCertification";
                QuestState st = activeChar.getQuestState(qn);
                String prefix = "-" + String.valueOf(activeChar.getClassIndex());
                String var = "";
                String isAvailable70 = st.getGlobalQuestVar("EmergentAbility70" + prefix);
                int itemId = 0;
                int levelChar = 70;
                if ((isAvailable70 == "") || (isAvailable70.equals("0"))) {
                    if (activeChar.getLevel() > 69) {
                        itemId = COMMONITEM;
                        var = "EmergentAbility" + String.valueOf(levelChar) + prefix;
                        getCertified(activeChar, itemId, var);

                        // adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        // htm.append("Congratz, you got the Emergent for the level 70.");
                        // adminReply.replace("%feedback%", htm.toString());
                        // separateAndSend(adminReply.getHtml(), activeChar);
                        // return;
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                        htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                        adminReply.replace("%list%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {

                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("You are not yet ready to receive your level 70 certification. Work hard and come back later.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("You have already received the certificate for this skill.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                }

            } else if (command.startsWith("_bbssubclass;obtain75master;")) {
                String qn = "SubClassCertification";
                QuestState st = activeChar.getQuestState(qn);
                String prefix = "-" + String.valueOf(activeChar.getClassIndex());
                String var = "";
                int itemId = 0;
                String isAvailable = st.getGlobalQuestVar("ClassAbility75" + prefix);
                int levelChar = 75;
                if ((isAvailable == "") || (isAvailable.equals("0"))) {
                    if (activeChar.getLevel() > 74) {
                        itemId = ENHANCEDITEM;
                        var = "ClassAbility" + String.valueOf(levelChar) + prefix;
                        getCertified(activeChar, itemId, var);

                        // adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        // htm.append("Congratz, you got the Emergent Master for the level 75.");
                        // adminReply.replace("%feedback%", htm.toString());
                        // separateAndSend(adminReply.getHtml(), activeChar);
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                        htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                        adminReply.replace("%list%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {

                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("You are not yet ready to receive your level 75 certification. Work hard and come back later.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("You have already received the certificate for this skill.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                }
            } else if (command.startsWith("_bbssubclass;obtain75class;")) {
                String qn = "SubClassCertification";
                QuestState st = activeChar.getQuestState(qn);
                String prefix = "-" + String.valueOf(activeChar.getClassIndex());
                String var = "";
                int itemId = 0;
                String isAvailable = st.getGlobalQuestVar("ClassAbility75" + prefix);
                int levelChar = 75;
                if ((isAvailable == "") || (isAvailable.equals("0"))) {
                    if (activeChar.getLevel() > 74) {
                        itemId = CLASSITEMS[getClassIndex(activeChar)];
                        var = "ClassAbility" + String.valueOf(levelChar) + prefix;
                        getCertified(activeChar, itemId, var);

                        // adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        // htm.append("Congratz, you got the Emergent Class for the level 75.");
                        // adminReply.replace("%feedback%", htm.toString());
                        // separateAndSend(adminReply.getHtml(), activeChar);
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                        htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                        adminReply.replace("%list%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;

                    } else {

                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("You are not yet ready to receive your level 75 certification. Work hard and come back later.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("You have already received the certificate for this skill.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                }
            } else if (command.startsWith("_bbssubclass;obtain80;")) {
                String qn = "SubClassCertification";
                QuestState st = activeChar.getQuestState(qn);
                String prefix = "-" + String.valueOf(activeChar.getClassIndex());
                String var = "";
                int itemId = 0;
                int levelChar = 80;
                String isAvailable = st.getGlobalQuestVar("ClassAbility80" + prefix);
                if ((isAvailable == "") || (isAvailable.equals("0"))) {
                    if (activeChar.getLevel() > 79) {
                        itemId = TRANSFORMITEMS[getClassIndex(activeChar)];
                        var = "ClassAbility" + String.valueOf(levelChar) + prefix;
                        getCertified(activeChar, itemId, var);

                        // adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        // htm.append("Congratz, you got the Emergent Transformation for the level 80.");
                        // adminReply.replace("%feedback%", htm.toString());
                        // separateAndSend(adminReply.getHtml(), activeChar);
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifList.htm");
                        htm.append("<center><button value=\"Level 65 Emergent\" action=\"bypass -h gab__bbssubclass;obtain65;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 70 Emergent\" action=\"bypass -h gab__bbssubclass;obtain70;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Class\" action=\"bypass -h gab__bbssubclass;obtain75class;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 75 Master\" action=\"bypass -h gab__bbssubclass;obtain75master;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        htm.append("<center><button value=\"Level 80 Class\" action=\"bypass -h gab__bbssubclass;obtain80;\" width=200 height=25 back=\"L2UI_ct1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
                        adminReply.replace("%feedback%", "Choose here the certification Requesites. Remember, you CANNOT have both Master and Class certificates!");
                        adminReply.replace("%list%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;

                    } else {
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("You are not yet ready to receive your level 80 certification. Work hard and come back later.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    }
                } else {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("You have already received the certificate for this skill.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                    return;
                }
            } else if (command.startsWith("_bbssubclass;removecertified;")) {


                if (activeChar.getSubClasses().size() == 0) {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("Visit a subclass master in order to certify your ability. Then Come back to me.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                } else if (activeChar.isSubClassActive()) {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("Change back to your Main Class and come back to me!");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                } else if (activeChar.getAdena() < Config.FEE_DELETE_SUBCLASS_SKILLS) {
                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("The Fee is " + Config.FEE_DELETE_SUBCLASS_SKILLS + ". Come back when you have it.");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                } else {
                    QuestState st2 = activeChar.getQuestState("SubClassSkills");
                    if (st2 == null) {
                        st2 = QuestManager.getInstance().getQuest("SubClassSkills").newQuestState(activeChar);
                    }

                    int activeCertifications = 0;
                    for (String varName : _questVarNames) {
                        for (int i = 1; i <= Config.MAX_SUBCLASS; i++) {
                            String qvar = st2.getGlobalQuestVar(varName + i);
                            if (!qvar.isEmpty() && (qvar.endsWith(";") || !qvar.equals("0"))) {
                                activeCertifications++;
                            }
                        }
                    }
                    if (activeCertifications == 0) {
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                        htm.append("What are you going to cancel? You have neither certification nor the related skills.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);
                        return;
                    } else {
                        for (String varName : _questVarNames) {
                            for (int i = 1; i <= Config.MAX_SUBCLASS; i++) {
                                final String qvarName = varName + i;
                                final String qvar = st2.getGlobalQuestVar(qvarName);
                                if (qvar.endsWith(";")) {
                                    final String skillIdVar = qvar.replace(";", "");
                                    if (Util.isDigit(skillIdVar)) {
                                        int skillId = Integer.parseInt(skillIdVar);
                                        final L2Skill sk = SkillData.getInstance().getInfo(skillId, 1);
                                        if (sk != null) {
                                            activeChar.removeSkill(sk);
                                            st2.saveGlobalQuestVar(qvarName, "0");
                                        }
                                    } else {
                                        _log.warning("Invalid Sub-Class Skill Id: " + skillIdVar + " for player " + activeChar.getName() + "!");
                                    }
                                } else if (!qvar.isEmpty() && !qvar.equals("0")) {
                                    if (Util.isDigit(qvar)) {
                                        final int itemObjId = Integer.parseInt(qvar);
                                        L2ItemInstance itemInstance = activeChar.getInventory().getItemByObjectId(itemObjId);
                                        if (itemInstance != null) {
                                            activeChar.destroyItem("CancelCertification", itemObjId, 1, activeChar, false);
                                        } else {
                                            itemInstance = activeChar.getWarehouse().getItemByObjectId(itemObjId);
                                            if (itemInstance != null) {
                                                _log.warning("Somehow " + activeChar.getName() + " put a certification book into warehouse!");
                                                activeChar.getWarehouse().destroyItem("CancelCertification", itemInstance, 1, activeChar, false);
                                            } else {
                                                _log.warning("Somehow " + activeChar.getName() + " deleted a certification book!");
                                            }
                                        }
                                        st2.saveGlobalQuestVar(qvarName, "0");
                                    } else {
                                        _log.warning("Invalid item object Id: " + qvar + " for player " + activeChar.getName() + "!");
                                    }
                                }
                            }
                        }


                        activeChar.reduceAdena("Cleanse", Config.FEE_DELETE_SUBCLASS_SKILLS, activeChar, true);
                        adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifOk.htm");
                        htm.append("Certifications Cancelled.");
                        adminReply.replace("%feedback%", htm.toString());
                        separateAndSend(adminReply.getHtml(), activeChar);

                        activeChar.sendSkillList();
                    }

                    // Let's consume all certification books, even those not present in database.
                    L2ItemInstance itemInstance = null;
                    for (int itemId : _itemsIds) {
                        itemInstance = activeChar.getInventory().getItemByItemId(itemId);
                        if (itemInstance != null) {
                            _log.warning(getClass().getName() + ": player " + activeChar + " had 'extra' certification skill books while cancelling sub-class certifications!");
                            activeChar.destroyItem("CancelCertificationExtraBooks", itemInstance, activeChar, false);
                        }
                    }
                }
                return;

            } else if (command.startsWith("_bbssubclass;skillcertified;")) {
                // TODO

                if (activeChar.isSubClassActive()) {

                    adminReply.setFile(activeChar, activeChar.getLang(), "data/html/CommunityBoard/subclassCertifError.htm");
                    htm.append("Change back to your Main Class and come back to me!");
                    adminReply.replace("%feedback%", htm.toString());
                    separateAndSend(adminReply.getHtml(), activeChar);
                } else {
                    showSubClassSkillList(activeChar);
                }
            } else if (command.startsWith("_bbssubclass;levelupskill;")) {
                //CustomMethodes.levelUpSkills(activeChar);
            }
        }
    }

    private boolean canGetPomander(L2PcInstance player) {
        switch (player.getClassId().getId()) {
            case 16:
            case 30:
            case 43:
            case 97:
            case 105:
            case 112:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar) {

    }

    private int getClassIndex(L2PcInstance player) {
        // java 8 method
        if (Arrays.stream(WARRIORCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 0;
        }

        if (Arrays.stream(KNIGHTCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 1;
        }

        if (Arrays.stream(ROGUECLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 2;
        }

        if (Arrays.stream(ENCHANTERCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 3;
        }
        if (Arrays.stream(WIZARDCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 4;
        }
        if (Arrays.stream(SUMMONERCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 5;
        }
        if (Arrays.stream(HEALERCLASSES).anyMatch(i -> i == player.getClassId().getId())) {
            return 6;
        }
        return -1;
    }

    private void getCertified(L2PcInstance player, int itemId, String var) {
        String qn = "SubClassCertification";
        QuestState st = player.getQuestState(qn);
        String qvar = st.getGlobalQuestVar(var);
        L2ItemInstance item = null;
        SystemMessage smsg = null;
        if ((qvar != "") && (!qvar.equals("0"))) {
            return;
        }

        item = player.getInventory().addItem("Quest", itemId, 1, player, player.getTarget());
        st.saveGlobalQuestVar(var, String.valueOf(item.getObjectId()));
        smsg = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
        smsg.addItemName(item);
        player.sendPacket(smsg);
        return;
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

    private final Set<PlayerClass> getAvailableSubClasses(L2PcInstance player) {
        // get player base class
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.values()[currentBaseId];

        // we need 2nd occupation ID
        final int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().ordinal();
        } else {
            baseClassId = currentBaseId;
        }

        /**
         * If the race of your main class is Elf or Dark Elf, you may not select each class as a subclass to the other class. If the race of your main class is Kamael, you may not subclass any other race If the race of your main class is NOT Kamael, you may not subclass any Kamael class You may not
         * select Overlord and Warsmith class as a subclass. You may not select a similar class as the subclass. The occupations classified as similar classes are as follows: Treasure Hunter, Plainswalker and Abyss Walker Hawkeye, Silver Ranger and Phantom Ranger Paladin, Dark Avenger, Temple Knight
         * and Shillien Knight Warlocks, Elemental Summoner and Phantom Summoner Elder and Shillien Elder Swordsinger and Bladedancer Sorcerer, Spellsinger and Spellhowler Also, Kamael have a special, hidden 4 subclass, the inspector, which can only be taken if you have already completed the other
         * two Kamael subclasses
         */
        Set<PlayerClass> availSubs = PlayerClass.values()[baseClassId].getAvailableSubclasses(player);

        if ((availSubs != null) && !availSubs.isEmpty()) {
            for (Iterator<PlayerClass> availSub = availSubs.iterator(); availSub.hasNext(); ) {
                PlayerClass pclass = availSub.next();

                // check for the village master
                if (!checkVillageMaster(pclass)) {
                    availSub.remove();
                    continue;
                }

                // scan for already used subclasses
                int availClassId = pclass.ordinal();
                ClassId cid = ClassId.values()[availClassId];
                SubClass prevSubClass;
                ClassId subClassId;
                for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
                    prevSubClass = subList.next();
                    subClassId = ClassId.values()[prevSubClass.getClassId()];

                    if (subClassId.equalsOrChildOf(cid)) {
                        availSub.remove();
                        break;
                    }
                }
            }
        }

        return availSubs;
    }

    private final boolean isValidNewSubClass(L2PcInstance player, int classId) {
        if (!checkVillageMaster(classId)) {
            return false;
        }

        final ClassId cid = ClassId.values()[classId];
        SubClass sub;
        ClassId subClassId;
        for (Iterator<SubClass> subList = iterSubClasses(player); subList.hasNext(); ) {
            sub = subList.next();
            subClassId = ClassId.values()[sub.getClassId()];

            if (subClassId.equalsOrChildOf(cid)) {
                return false;
            }
        }

        // get player base class
        final int currentBaseId = player.getBaseClass();
        final ClassId baseCID = ClassId.values()[currentBaseId];

        // we need 2nd occupation ID
        final int baseClassId;
        if (baseCID.level() > 2) {
            baseClassId = baseCID.getParent().ordinal();
        } else {
            baseClassId = currentBaseId;
        }

        Set<PlayerClass> availSubs = PlayerClass.values()[baseClassId].getAvailableSubclasses(player);
        if ((availSubs == null) || availSubs.isEmpty()) {
            return false;
        }

        boolean found = false;
        for (PlayerClass pclass : availSubs) {
            if (pclass.ordinal() == classId) {
                found = true;
                break;
            }
        }
        return found;
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

    private static final Iterator<SubClass> iterSubClasses(L2PcInstance player) {
        return player.getSubClasses().values().iterator();
    }

    private static final List<SubClass> listSubClasses(L2PcInstance player) {
        return new ArrayList<>(player.getSubClasses().values());
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

    private static final String formatClassForDisplay(PlayerClass className) {
        String classNameStr = className.toString();
        char[] charArray = classNameStr.toCharArray();

        for (int i = 1; i < charArray.length; i++) {
            if (Character.isUpperCase(charArray[i]))
                classNameStr = classNameStr.substring(0, i) + " " + classNameStr.substring(i);
        }

        return classNameStr;
    }

    protected String getSubClassMenu(Race pRace) {
        if (Config.ALT_GAME_SUBCLASS_EVERYWHERE || (pRace != Race.KAMAEL)) {
            return "data/html/villagemaster/SubClass.htm";
        }

        return "data/html/villagemaster/SubClass_NoOther.htm";
    }

    private static boolean hasTransferSkillItems(L2PcInstance player) {
        int itemId;
        switch (player.getClassId()) {
            case cardinal: {
                itemId = 15307;
                break;
            }
            case evaSaint: {
                itemId = 15308;
                break;
            }
            case shillienSaint: {
                itemId = 15309;
                break;
            }
            default: {
                itemId = -1;
            }
        }
        return (player.getInventory().getInventoryItemCount(itemId, -1) > 0);
    }

    private String handleNavigation(boolean hasNextItem, L2PcInstance player) {
        StringBuilder tb = new StringBuilder();
        String pageS = player.getQuickVar("currPageSubBBS", "1");
        int page = Integer.parseInt(pageS);

        tb.append("<table width=250 height=\"30\"><tr><td width=200 align=\"center\">");
        if (page != 1) {
            tb.append("<td width=100 align=\"center\"><button value=\"Previous\" action=\"bypass -h gab__bbssubclass;obsPrev\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        }
        if (hasNextItem)
            tb.append("<td width=100 align=\"center\"><button value=\"Next\" action=\"bypass -h gab__bbssubclass;obsNext\" width=140 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        tb.append("</tr></table>");
        return tb.toString();
    }

    private void clearVars(L2PcInstance player) {
        player.deleteQuickVar("currPageSubBBS");
    }

    private void sendErrorMessage(L2PcInstance activeChar, String msg) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        // Subclasses may not be changed while a skill is in use.
        adminReply.setFile(activeChar, activeChar.getLang(), MAINHTML);
        adminReply.replace("%CHANGE%", "");
        adminReply.replace("%error%", getErrorMessage(msg));
        adminReply.replace("%backBypass%", "gab__bbssubclass;");
        separateAndSend(adminReply.getHtml(), activeChar);
    }
}