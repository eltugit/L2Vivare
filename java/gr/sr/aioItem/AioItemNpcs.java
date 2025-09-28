package gr.sr.aioItem;


import gr.sr.aioItem.dymanicHtmls.GenerateHtmls;
import gr.sr.aioItem.runnable.AioItemDelay;
import gr.sr.aioItem.runnable.TransformFinalizer;
import gr.sr.configsEngine.configs.impl.AioItemsConfigs;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.LeaderboardsConfigs;
import gr.sr.donateEngine.DonateHandler;
import gr.sr.imageGeneratorEngine.CaptchaImageGenerator;
import gr.sr.leaderboards.ArenaLeaderboard;
import gr.sr.leaderboards.CraftLeaderboard;
import gr.sr.leaderboards.FishermanLeaderboard;
import gr.sr.leaderboards.TvTLeaderboard;
import gr.sr.main.Conditions;
import gr.sr.main.TopListsLoader;
import gr.sr.securityEngine.SecurityActions;
import gr.sr.securityEngine.SecurityType;
import l2r.Config;
import l2r.gameserver.GameTimeController;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.xml.impl.*;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.enums.Race;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.idfactory.IdFactory;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.instancemanager.SiegeManager;
import l2r.gameserver.instancemanager.TownManager;
import l2r.gameserver.model.Elementals;
import l2r.gameserver.model.L2Augmentation;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.model.base.SubClass;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.*;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList.WarehouseListType;
import l2r.gameserver.util.Broadcast;
import l2r.gameserver.util.Util;
import l2r.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;


public class AioItemNpcs {
    private static int itemId;
    private static int itemPrice;
    private static Logger log = LoggerFactory.getLogger(AioItemNpcs.class);
    private static final String[] commandList = new String[]{"withdrawp", "withdrawsortedp", "withdrawc", "withdrawsortedc"};

    private AioItemNpcs() {
    }

    protected static String getSubClassMenu(Race var0) {
        return !Config.ALT_GAME_SUBCLASS_EVERYWHERE && var0 == Race.KAMAEL ? "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoOther.htm" : "data/html/sunrise/AioItemNpcs/subclass/SubClass.htm";
    }

    protected static String getSubClassFail() {
        return "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail.htm";
    }


    public static void onBypassFeedback(L2PcInstance player, String command) {
        String[] commands = command.split("_");
        if (player != null) {
            if (Conditions.checkPlayerConditions(player)) {
                NpcHtmlMessage npcHtmlMessage;
                if (command.startsWith("Chat")) {
                    if (!commands[1].isEmpty() && commands[1] != null) {
                        (npcHtmlMessage = new NpcHtmlMessage()).setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/" + commands[1]);
                        player.sendPacket(npcHtmlMessage);
                    }
                } else if (command.toLowerCase().startsWith(commandList[0])) {
                    if (Config.L2JMOD_ENABLE_WAREHOUSESORTING_PRIVATE) {
                        (npcHtmlMessage = new NpcHtmlMessage()).setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/warehouse/WhSortedP.htm");
                        player.sendPacket(npcHtmlMessage);
                    } else {
                        GenerateHtmls.showPWithdrawWindow(player, (WarehouseListType) null, (byte) 0);
                    }
                } else {
                    String[] commandArgs = command.split(" ");
                    if (command.toLowerCase().startsWith(commandList[1])) {
                        if (commandArgs.length > 2) {
                            GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.valueOf(commandArgs[1]), SortedWareHouseWithdrawalList.getOrder(commandArgs[2]));
                        } else if (commandArgs.length > 1) {
                            GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.valueOf(commandArgs[1]), (byte) 1);
                        } else {
                            GenerateHtmls.showPWithdrawWindow(player, WarehouseListType.ALL, (byte) 1);
                        }
                    } else if (command.toLowerCase().startsWith(commandList[2])) {
                        if (Config.L2JMOD_ENABLE_WAREHOUSESORTING_CLAN) {
                            (npcHtmlMessage = new NpcHtmlMessage()).setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/warehouse/WhSortedC.htm");
                            player.sendPacket(npcHtmlMessage);
                        } else {
                            GenerateHtmls.showCWithdrawWindow(player, (WarehouseListType) null, (byte) 0);
                        }
                    } else if (command.toLowerCase().startsWith(commandList[3])) {
                        if ((commandArgs = command.split(" ")).length > 2) {
                            GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.valueOf(commandArgs[1]), SortedWareHouseWithdrawalList.getOrder(commandArgs[2]));
                        } else if (commandArgs.length > 1) {
                            GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.valueOf(commandArgs[1]), (byte) 1);
                        } else {
                            GenerateHtmls.showCWithdrawWindow(player, WarehouseListType.ALL, (byte) 1);
                        }
                    } else if (command.startsWith("ndeposit")) {
                        player.sendPacket(ActionFailed.STATIC_PACKET);
                        player.setActiveWarehouse(player.getWarehouse());
                        if (player.getWarehouse().getSize() == player.getWareHouseLimit()) {
                            player.sendPacket(SystemMessageId.WAREHOUSE_FULL);
                        } else {
                            player.tempInventoryDisable();
                            player.sendPacket(new WareHouseDepositList(player, 1));
                        }
                    } else if (command.startsWith("clandeposit")) {
                        if (player.getClan() == null) {
                            player.sendPacket(SystemMessageId.YOU_ARE_NOT_A_CLAN_MEMBER);
                        } else {
                            player.sendPacket(ActionFailed.STATIC_PACKET);
                            player.setActiveWarehouse(player.getClan().getWarehouse());
                            if (player.getClan().getLevel() == 0) {
                                player.sendPacket(SystemMessageId.ONLY_LEVEL_1_CLAN_OR_HIGHER_CAN_USE_WAREHOUSE);
                            } else {
                                player.setActiveWarehouse(player.getClan().getWarehouse());
                                player.tempInventoryDisable();
                                player.sendPacket(new WareHouseDepositList(player, 4));
                            }
                        }
                    }else {
                        StringTokenizer st;
                        if (command.startsWith("showAchievementInfo")) {
                        } else {
                            int npcTalkOption;
                            if (command.startsWith("achievementGetReward")) {
                                try {
                                    (st = new StringTokenizer(command, " ")).nextToken();
                                } catch (Exception e) {
                                    SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
                                }
                            } else if (command.startsWith("siege_")) {
                                byte castleChoise = 0;
                                if (command.startsWith("siege_gludio")) {
                                    castleChoise = 1;
                                } else if (command.startsWith("siege_dion")) {
                                    castleChoise = 2;
                                } else if (command.startsWith("siege_giran")) {
                                    castleChoise = 3;
                                } else if (command.startsWith("siege_oren")) {
                                    castleChoise = 4;
                                } else if (command.startsWith("siege_aden")) {
                                    castleChoise = 5;
                                } else if (command.startsWith("siege_innadril")) {
                                    castleChoise = 6;
                                } else if (command.startsWith("siege_goddard")) {
                                    castleChoise = 7;
                                } else if (command.startsWith("siege_rune")) {
                                    castleChoise = 8;
                                } else if (command.startsWith("siege_schuttgart")) {
                                    castleChoise = 9;
                                }

                                Castle castle;
                                if ((castle = CastleManager.getInstance().getCastleById(castleChoise)) != null && castleChoise != 0) {
                                    player.sendPacket(new SiegeInfo(castle));
                                }

                            } else {
                                int classIndex;
                                int conditionIndex;
                                if (command.startsWith("Subclass")) {
                                    if (player.isInParty()) {
                                        player.sendMessage("Sub classes may not be created or changed while being in party.");
                                        return;
                                    }
                                    if (player.getPvpFlag() != 0 && !player.isInsideZone(ZoneIdType.PEACE)) {
                                        player.sendMessage("Cannot use while have PvP flag.");
                                    } else if (!player.isCastingNow() && !player.isAllSkillsDisabled()) {
                                        if (player.isInCombat()) {
                                            player.sendMessage("Sub classes may not be created or changed while being in combat.");
                                        } else if (OlympiadManager.getInstance().isRegistered(player)) {
                                            player.sendMessage("You can not change subclass when registered for Olympiad.");
                                        } else if (player.isInParty()) {
                                            player.sendMessage("Sub classes may not be created or changed while being in party.");
                                        } else if (player.isCursedWeaponEquipped()) {
                                            player.sendMessage("You can`t change Subclass while Cursed weapon equiped!");
                                        } else {
                                            npcHtmlMessage = new NpcHtmlMessage();
                                            if (player.getTransformation() != null) {
                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoTransformed.htm");
                                                player.sendPacket(npcHtmlMessage);
                                            } else if (player.getSummon() != null) {
                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_NoSummon.htm");
                                                player.sendPacket(npcHtmlMessage);
                                            } else if (!player.isInventoryUnder90(true)) {
                                                player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_INVENTORY_FULL);
                                            } else if (player.getWeightPenalty() >= 2) {
                                                player.sendPacket(SystemMessageId.NOT_SUBCLASS_WHILE_OVERWEIGHT);
                                            } else {
                                                npcTalkOption = 0;
                                                classIndex = 0;
                                                int newSubClassIndex = 0;

                                                try {
                                                    npcTalkOption = Integer.parseInt(command.substring(9, 10).trim());
                                                    if ((conditionIndex = command.indexOf(32, 11)) == -1) {
                                                        conditionIndex = command.length();
                                                    }

                                                    if (command.length() > 11) {
                                                        classIndex = Integer.parseInt(command.substring(11, conditionIndex).trim());
                                                        if (command.length() > conditionIndex) {
                                                            newSubClassIndex = Integer.parseInt(command.substring(conditionIndex).trim());
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    log.warn(AioItemNpcs.class.getName() + ": Wrong numeric values for command " + command);
                                                }

                                                StringBuilder stringBuilder;
                                                Iterator iter;
                                                Set subClassesForPlayer;
                                                Iterator subclasses;
                                                switch (npcTalkOption) {
                                                    case 0:
                                                        npcHtmlMessage.setFile(player, player.getHtmlPrefix(), getSubClassMenu(player.getRace()));
                                                        break;
                                                    case 1:
                                                        if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), getSubClassFail());
                                                        } else {
                                                            if ((subClassesForPlayer = Conditions.getAvailableSubClasses(player)) == null || subClassesForPlayer.isEmpty()) {
                                                                if (player.getRace() != Race.ELF && player.getRace() != Race.DARK_ELF) {
                                                                    if (player.getRace() == Race.KAMAEL) {
                                                                        npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail_Kamael.htm");
                                                                        player.sendPacket(npcHtmlMessage);
                                                                        return;
                                                                    } else {
                                                                        player.sendMessage("There are no sub classes available at this time.");
                                                                        return;
                                                                    }
                                                                } else {
                                                                    npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Fail_Elves.htm");
                                                                    player.sendPacket(npcHtmlMessage);
                                                                    return;
                                                                }
                                                            }

                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Add.htm");
                                                            stringBuilder = StringUtil.startAppend(200, new String[0]);
                                                            iter = subClassesForPlayer.iterator();

                                                            while (iter.hasNext()) {
                                                                PlayerClass playerClass = (PlayerClass) iter.next();
                                                                StringUtil.append(stringBuilder, new String[]{"<a action=\"bypass -h Aioitem_Subclass 4 ", String.valueOf(playerClass.ordinal()), "\" msg=\"1268;", ClassListData.getInstance().getClass(playerClass.ordinal()).getClassName(), "\">", ClassListData.getInstance().getClass(playerClass.ordinal()).getClientCode(), "</a><br>"});
                                                            }

                                                            npcHtmlMessage.replace("%list%", stringBuilder.toString());
                                                        }
                                                        break;
                                                    case 2:
                                                        if (player.getSubClasses().isEmpty()) {
                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ChangeNo.htm");
                                                        } else {
                                                            stringBuilder = StringUtil.startAppend(200, new String[0]);
                                                            if (Conditions.checkVillageMaster(player.getBaseClass())) {
                                                                StringUtil.append(stringBuilder, new String[]{"<a action=\"bypass -h Aioitem_Subclass 5 0\">", ClassListData.getInstance().getClass(player.getBaseClass()).getClientCode(), "</a><br>"});
                                                            }

                                                            iter = Conditions.iterSubClasses(player);

                                                            while (iter.hasNext()) {
                                                                SubClass var61;
                                                                if (Conditions.checkVillageMaster((var61 = (SubClass) iter.next()).getClassDefinition())) {
                                                                    StringUtil.append(stringBuilder, new String[]{"<a action=\"bypass -h Aioitem_Subclass 5 ", String.valueOf(var61.getClassIndex()), "\">", ClassListData.getInstance().getClass(var61.getClassId()).getClientCode(), "</a><br>"});
                                                                }
                                                            }

                                                            if (stringBuilder.length() > 0) {
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Change.htm");
                                                                npcHtmlMessage.replace("%list%", stringBuilder.toString());
                                                            } else {
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ChangeNotFound.htm");
                                                            }
                                                        }
                                                        break;
                                                    case 3:
                                                        if (player.getSubClasses() != null && !player.getSubClasses().isEmpty()) {
                                                            if (player.getTotalSubClasses() <= 3 && Config.MAX_SUBCLASS <= 3) {
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Modify.htm");
                                                                if (player.getSubClasses().containsKey(1)) {
                                                                    npcHtmlMessage.replace("%sub1%", ClassListData.getInstance().getClass(((SubClass) player.getSubClasses().get(1)).getClassId()).getClientCode());
                                                                } else {
                                                                    npcHtmlMessage.replace("<a action=\"bypass -h Aioitem_Subclass 6 1\">%sub1%</a><br>", "");
                                                                }

                                                                if (player.getSubClasses().containsKey(2)) {
                                                                    npcHtmlMessage.replace("%sub2%", ClassListData.getInstance().getClass(((SubClass) player.getSubClasses().get(2)).getClassId()).getClientCode());
                                                                } else {
                                                                    npcHtmlMessage.replace("<a action=\"bypass -h Aioitem_Subclass 6 2\">%sub2%</a><br>", "");
                                                                }

                                                                if (player.getSubClasses().containsKey(3)) {
                                                                    npcHtmlMessage.replace("%sub3%", ClassListData.getInstance().getClass(((SubClass) player.getSubClasses().get(3)).getClassId()).getClientCode());
                                                                } else {
                                                                    npcHtmlMessage.replace("<a action=\"bypass -h Aioitem_Subclass 6 3\">%sub3%</a><br>", "");
                                                                }
                                                            } else {
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyCustom.htm");
                                                                stringBuilder = StringUtil.startAppend(200, new String[0]);
                                                                int var46 = 1;
                                                                subclasses = Conditions.iterSubClasses(player);

                                                                while (subclasses.hasNext()) {
                                                                    SubClass subClass = (SubClass) subclasses.next();
                                                                    StringUtil.append(stringBuilder, new String[]{"Sub-class ", String.valueOf(var46++), "<br>", "<a action=\"bypass -h Aioitem_Subclass 6 ", String.valueOf(subClass.getClassIndex()), "\">", ClassListData.getInstance().getClass(subClass.getClassId()).getClientCode(), "</a><br>"});
                                                                }

                                                                npcHtmlMessage.replace("%list%", stringBuilder.toString());
                                                            }
                                                        } else {
                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyEmpty.htm");
                                                        }
                                                        break;
                                                    case 4:
                                                        if (!player.getFloodProtectors().getSubclass().tryPerformAction("add subclass")) {
                                                            log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                                                            return;
                                                        }

                                                        boolean canGetSub = true;
                                                        if (player.getTotalSubClasses() >= Config.MAX_SUBCLASS) {
                                                            canGetSub = false;
                                                        }

                                                        if (player.getLevel() < 75) {
                                                            canGetSub = false;
                                                        }

                                                        if (canGetSub && !player.getSubClasses().isEmpty()) {
                                                            iter = Conditions.iterSubClasses(player);

                                                            while (iter.hasNext()) {
                                                                if (((SubClass) iter.next()).getLevel() < 75) {
                                                                    canGetSub = false;
                                                                    break;
                                                                }
                                                            }
                                                        }

                                                        if (canGetSub && !Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS) {
                                                            canGetSub = Conditions.checkQuests(player);
                                                        }

                                                        if (canGetSub && Conditions.isValidNewSubClass(player, classIndex)) {
                                                            if (!player.addSubClass(classIndex, player.getTotalSubClasses() + 1)) {
                                                                return;
                                                            }

                                                            player.setActiveClass(player.getTotalSubClasses());
                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_AddOk.htm");
                                                            player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS);
                                                        } else {
                                                            npcHtmlMessage.setFile(player, player.getHtmlPrefix(), getSubClassFail());
                                                        }
                                                        break;
                                                    case 5:
                                                        if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                                                            log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                                                            return;
                                                        }

                                                        if (player.getClassIndex() != classIndex) {
                                                            if (classIndex == 0) {
                                                                if (!Conditions.checkVillageMaster(player.getBaseClass())) {
                                                                    return;
                                                                }
                                                            } else {
                                                                try {
                                                                    if (!Conditions.checkVillageMaster(((SubClass) player.getSubClasses().get(classIndex)).getClassDefinition())) {
                                                                        return;
                                                                    }
                                                                } catch (NullPointerException e) {
                                                                    log.warn("Something went wrong with checking subclass master conditions :" + e);
                                                                    return;
                                                                }
                                                            }

                                                            player.setActiveClass(classIndex);
                                                            player.sendPacket(SystemMessageId.SUBCLASS_TRANSFER_COMPLETED);
                                                            return;
                                                        }

                                                        npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_Current.htm");
                                                        break;
                                                    case 6:
                                                        if (classIndex <= 0 || classIndex > Config.MAX_SUBCLASS) {
                                                            return;
                                                        }

                                                        if ((subClassesForPlayer = Conditions.getAvailableSubClasses(player)) == null || subClassesForPlayer.isEmpty()) {
                                                            player.sendMessage("There are no sub classes available at this time.");
                                                            return;
                                                        }

                                                        StringBuilder sb = StringUtil.startAppend(200, new String[0]);
                                                        subclasses = subClassesForPlayer.iterator();

                                                        while (subclasses.hasNext()) {
                                                            PlayerClass playerClass = (PlayerClass) subclasses.next();
                                                            StringUtil.append(sb, new String[]{"<a action=\"bypass -h Aioitem_Subclass 7 ", String.valueOf(classIndex), " ", String.valueOf(playerClass.ordinal()), "\" msg=\"1445;", "\">", ClassListData.getInstance().getClass(playerClass.ordinal()).getClientCode(), "</a><br>"});
                                                        }

                                                        switch (classIndex) {
                                                            case 1:
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice1.htm");
                                                                break;
                                                            case 2:
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice2.htm");
                                                                break;
                                                            case 3:
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice3.htm");
                                                                break;
                                                            default:
                                                                npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyChoice.htm");
                                                        }

                                                        npcHtmlMessage.replace("%list%", sb.toString());
                                                        break;
                                                    case 7:
                                                        if (!player.getFloodProtectors().getSubclass().tryPerformAction("change class")) {
                                                            log.warn(AioItemNpcs.class.getName() + ": Player " + player.getName() + " has performed a subclass change too fast");
                                                            return;
                                                        }

                                                        if (!Conditions.isValidNewSubClass(player, newSubClassIndex)) {
                                                            return;
                                                        }

                                                        if (!player.modifySubClass(classIndex, newSubClassIndex)) {
                                                            player.setActiveClass(0);
                                                            player.sendMessage("The sub class could not be added, you have been reverted to your base class.");
                                                            return;
                                                        }

                                                        player.abortCast();
                                                        player.stopAllEffectsExceptThoseThatLastThroughDeath();
                                                        player.stopAllEffectsNotStayOnSubclassChange();
                                                        player.stopCubics();
                                                        player.setActiveClass(classIndex);
                                                        npcHtmlMessage.setFile(player, player.getHtmlPrefix(), "data/html/sunrise/AioItemNpcs/subclass/SubClass_ModifyOk.htm");
                                                        npcHtmlMessage.replace("%name%", ClassListData.getInstance().getClass(newSubClassIndex).getClientCode());
                                                        player.sendPacket(SystemMessageId.ADD_NEW_SUBCLASS);
                                                }

                                                player.sendPacket(npcHtmlMessage);
                                            }
                                        }
                                    } else {
                                        player.sendPacket(SystemMessageId.SUBCLASS_NO_CHANGE_OR_CREATE_WHILE_SKILL_IN_USE);
                                    }
                                } else if (command.startsWith("createclan")) {
                                    commandArgs = command.split(" ");
                                    String clanName = "";
                                    if (commandArgs.length >= 2) {
                                        clanName = commandArgs[1];
                                    }

                                    if (!clanName.isEmpty()) {
                                        if (!Conditions.isValidName(clanName)) {
                                            player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
                                        } else {
                                            ClanTable.getInstance().createClan(player, clanName);
                                        }
                                    }
                                } else if (command.startsWith("clanskills")) {
                                    itemId = AioItemsConfigs.GET_FULL_CLAN_COIN;
                                    itemPrice = AioItemsConfigs.GET_FULL_CLAN_PRICE;
                                    if (player.getClan() != null && player.isClanLeader()) {
                                        if (Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                            player.destroyItemByItemId("Clan donate", itemId, (long) itemPrice, player, true);
                                            player.getClan().changeLevel(11);
                                            player.sendMessage("Clan level set to 11");
                                            player.getClan().addReputationScore(500000, true);
                                            player.getClan().addNewSkill(SkillData.getInstance().getInfo(391, 1));
                                            int[] squadSkills = new int[]{611, 612, 613, 614, 615, 616};
                                            int[] clanSkills = new int[]{370, 371, 372, 373, 374, 375, 376, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 387, 388, 389, 390};

                                            int toAddSkill;
                                            for (conditionIndex = 0; conditionIndex < 21; ++conditionIndex) {
                                                toAddSkill = clanSkills[conditionIndex];
                                                player.getClan().addNewSkill(SkillData.getInstance().getInfo(toAddSkill, 3));
                                            }

                                            clanSkills = squadSkills;

                                            for (conditionIndex = 0; conditionIndex < 6; ++conditionIndex) {
                                                toAddSkill = clanSkills[conditionIndex];
                                                player.getClan().addNewSkill(SkillData.getInstance().getInfo(toAddSkill, 3), 0);
                                            }

                                            player.sendMessage("You have successfully perform this action");
                                        }
                                    } else {
                                        player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                                    }
                                } else {
                                    String toChangeName;
                                    L2PcInstance partyMember;
                                    Iterator partyMembers;
                                    if (command.startsWith("changename")) {
                                        try {
                                            itemId = AioItemsConfigs.CHANGE_NAME_COIN;
                                            itemPrice = AioItemsConfigs.CHANGE_NAME_PRICE;
                                            if (!Util.isAlphaNumeric(toChangeName = command.substring(11))) {
                                                player.sendMessage("Invalid character name.");
                                            } else if (Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                if (CharNameTable.getInstance().getIdByName(toChangeName) > 0) {
                                                    player.sendMessage("Warning, name " + toChangeName + " already exists.");
                                                } else {
                                                    player.destroyItemByItemId("Name Change", itemId, (long) itemPrice, player, true);
                                                    player.setName(toChangeName);
                                                    player.getAppearance().setVisibleName(toChangeName);
                                                    player.store();
                                                    player.sendMessage("Your name has been changed to " + toChangeName);
                                                    player.broadcastUserInfo();
                                                    if (player.isInParty()) {
                                                        player.getParty().broadcastToPartyMembers(player, new PartySmallWindowDeleteAll());
                                                        partyMembers = player.getParty().getMembers().iterator();

                                                        while (partyMembers.hasNext()) {
                                                            if ((partyMember = (L2PcInstance) partyMembers.next()) != player) {
                                                                partyMember.sendPacket(new PartySmallWindowAll(partyMember, player.getParty()));
                                                            }
                                                        }
                                                    }

                                                    if (player.getClan() != null) {
                                                        player.getClan().broadcastClanStatus();
                                                    }

                                                }
                                            }
                                        } catch (StringIndexOutOfBoundsException e) {
                                            player.sendMessage("Player name box cannot be empty.");
                                        }
                                    } else if (command.startsWith("changeclanname")) {
                                        try {
                                            itemId = AioItemsConfigs.CHANGE_CNAME_COIN;
                                            itemPrice = AioItemsConfigs.CHANGE_CNAME_PRICE;
                                            toChangeName = command.substring(15);
                                            if (player.getClan() != null && player.isClanLeader()) {
                                                if (!Util.isAlphaNumeric(toChangeName)) {
                                                    player.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
                                                } else if (Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                    if (ClanTable.getInstance().getClanByName(toChangeName) != null) {
                                                        player.sendMessage("Warning, clan name " + toChangeName + " already exists.");
                                                    } else {
                                                        player.destroyItemByItemId("Clan Name Change", itemId, (long) itemPrice, player, true);
                                                        player.getClan().setName(toChangeName);
                                                        player.getClan().updateClanNameInDB();
                                                        player.sendMessage("Your clan name has been changed to " + toChangeName);
                                                        player.broadcastUserInfo();
                                                        if (player.isInParty()) {
                                                            player.getParty().broadcastToPartyMembers(player, new PartySmallWindowDeleteAll());
                                                            partyMembers = player.getParty().getMembers().iterator();

                                                            while (partyMembers.hasNext()) {
                                                                if ((partyMember = (L2PcInstance) partyMembers.next()) != player) {
                                                                    partyMember.sendPacket(new PartySmallWindowAll(partyMember, player.getParty()));
                                                                }
                                                            }
                                                        }

                                                        if (player.getClan() != null) {
                                                            player.getClan().broadcastClanStatus();
                                                        }

                                                    }
                                                }
                                            } else {
                                                player.sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                                            }
                                        } catch (StringIndexOutOfBoundsException var18) {
                                            player.sendMessage("Clan name box cannot be empty.");
                                        }
                                    } else {
                                        if (command.startsWith("element")) {
                                            byte elementSlot = -1;
                                            if (command.startsWith("elementhead")) {
                                                elementSlot = 1;
                                            } else if (command.startsWith("elementchest")) {
                                                elementSlot = 6;
                                            } else if (command.startsWith("elementgloves")) {
                                                elementSlot = 10;
                                            } else if (command.startsWith("elementboots")) {
                                                elementSlot = 12;
                                            } else if (command.startsWith("elementlegs")) {
                                                elementSlot = 11;
                                            } else if (command.startsWith("elementwep")) {
                                                elementSlot = 5;
                                            }

                                            itemId = AioItemsConfigs.ELEMENT_COIN;
                                            itemPrice = AioItemsConfigs.ELEMENT_PRICE;
                                            npcTalkOption = AioItemsConfigs.ELEMENT_VALUE_WEAPON;
                                            classIndex = AioItemsConfigs.ELEMENT_VALUE_ARMOR;
                                            if (Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                L2ItemInstance item;
                                                if ((item = player.getInventory().getPaperdollItem(elementSlot)) != null && item.getLocationSlot() == elementSlot) {
                                                    byte element = Elementals.getElementId(command.split(" ")[1]);
                                                    byte oppositeElement = Elementals.getOppositeElement(element);
                                                    Elementals itemElemental = item.getElemental(element);
                                                    switch (item.getItem().getCrystalType()) {
                                                        case NONE:
                                                        case A:
                                                        case B:
                                                        case C:
                                                        case D:
                                                            player.sendMessage("Invalid item grade.");
                                                            return;
                                                        default:
                                                            if (!AioItemsConfigs.ELEMENT_ALLOW_MORE_ATT_FOR_WEAPONS && (item.isWeapon() && item.getElementals() != null || item.isArmor() && itemElemental != null && item.getElementals() != null && item.getElementals().length >= 3)) {
                                                                player.sendPacket(SystemMessageId.ANOTHER_ELEMENTAL_POWER_ALREADY_ADDED);
                                                                return;
                                                            }

                                                            Elementals[] itemElementals = item.getElementals();
                                                            Elementals elemental;
                                                            if (item.isWeapon()) {
                                                                if (itemElemental != null && itemElemental.getValue() >= npcTalkOption) {
                                                                    player.sendMessage("You cannot add same attribute to item!");
                                                                    return;
                                                                }

                                                                if (item.getElementals() != null) {
                                                                    conditionIndex = itemElementals.length;

                                                                    for (int i = 0; i < conditionIndex; ++i) {
                                                                        elemental = itemElementals[i];
                                                                        if (item.isEquipped()) {
                                                                            item.getElemental(elemental.getElement()).removeBonus(player);
                                                                        }

                                                                        item.clearElementAttr(elemental.getElement());
                                                                    }
                                                                }
                                                            } else if (item.isArmor() && item.getElementals() != null) {
                                                                conditionIndex = (itemElementals = item.getElementals()).length;

                                                                for (int i = 0; i < conditionIndex; ++i) {
                                                                    if ((elemental = itemElementals[i]).getElement() == oppositeElement) {
                                                                        player.sendMessage("You cannot add opposite attribute to item!");
                                                                        return;
                                                                    }

                                                                    if (elemental.getElement() == element && elemental.getValue() >= classIndex) {
                                                                        player.sendMessage("You cannot add same attribute to item!");
                                                                        return;
                                                                    }
                                                                }
                                                            }

                                                            player.destroyItemByItemId("element", itemId, (long) itemPrice, player, true);
                                                            player.getInventory().unEquipItemInSlot(elementSlot);
                                                            item.setElementAttr(element, item.isWeapon() ? npcTalkOption : classIndex);
                                                            player.getInventory().equipItem(item);
                                                            player.sendMessage("Successfully added " + commandArgs[2] + " attribute to your item.");
                                                            InventoryUpdate iu;
                                                            (iu = new InventoryUpdate()).addModifiedItem(item);
                                                            player.sendPacket(iu);
                                                            return;
                                                    }
                                                }

                                                player.sendMessage("You cannot attribute items that are not equipped!");
                                            }

                                            return;
                                        } else {
                                            String augmentOption;
                                            if (command.startsWith("addaugment")) {
                                                short firstAug = 0;
                                                short secondAug = 0;
                                                String[] args = command.split(" ");
                                                augmentOption = args[1];
                                                switch (augmentOption) {
                                                    case "CON+1":
                                                        firstAug = 16342;
                                                        break;
                                                    case "INT+1":
                                                        firstAug = 16343;
                                                        break;
                                                    case "MEN+1":
                                                        firstAug = 16344;
                                                        break;
                                                    case "STR+1":
                                                        firstAug = 16341;
                                                        break;
                                                }

                                                augmentOption = args[2];
                                                switch (augmentOption) {
                                                    case "Shield":
                                                        secondAug = 16284;
                                                        break;
                                                    case "Heal_Empower":
                                                        secondAug = 16279;
                                                        break;
                                                    case "Duel_Might":
                                                        secondAug = 16285;
                                                        break;
                                                    case "Magic_Barrier":
                                                        secondAug = 16282;
                                                        break;
                                                    case "Wild_Magic":
                                                        secondAug = 16336;
                                                        break;
                                                    case "Empower":
                                                        secondAug = 16281;
                                                        break;
                                                    case "Might":
                                                        secondAug = 16283;
                                                }

                                                npcTalkOption = secondAug + 8358;
                                                itemId = AioItemsConfigs.AUGMENT_COIN;
                                                itemPrice = AioItemsConfigs.AUGMENT_PRICE;
                                                if (!Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                    return;
                                                }

                                                L2ItemInstance itemToAugment = null;
                                                L2ItemInstance weapon;
                                                if ((weapon = player.getInventory().getPaperdollItem(5)) == null) {
                                                    player.sendMessage("Equip the weapon for augmentation.");
                                                    return;
                                                }

                                                if (weapon.isAugmented()) {
                                                    player.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
                                                    return;
                                                }

                                                if (!weapon.isHeroItem() && !weapon.isShadowItem() && !weapon.isCommonItem() && !weapon.isEtcItem() && !weapon.isTimeLimitedItem()) {
                                                    if (weapon.getLocationSlot() == 5) {
                                                        itemToAugment = weapon;
                                                    }

                                                    if (itemToAugment != null) {
                                                        player.destroyItemByItemId("augment", itemId, (long) itemPrice, player, true);
                                                        player.getInventory().unEquipItemInSlot(5);
                                                        itemToAugment.setAugmentation(new L2Augmentation((npcTalkOption << 16) + firstAug));
                                                        player.getInventory().equipItem(itemToAugment);
                                                        player.sendPacket(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
                                                        InventoryUpdate iu;
                                                        (iu = new InventoryUpdate()).addModifiedItem(itemToAugment);
                                                        player.sendPacket(iu);
                                                        player.broadcastPacket(new CharInfo(player));
                                                        player.sendPacket(new UserInfo(player));
                                                        player.broadcastPacket(new ExBrExtraUserInfo(player));
                                                    }

                                                    return;
                                                }

                                                player.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
                                                return;
                                            }

                                            if (command.startsWith("teleportTo")) {
                                                if (AioItemsConfigs.AIO_ENABLE_TP_DELAY && AioItemDelay._delayers.contains(player)) {
                                                    if (AioItemsConfigs.AIO_DELAY_SENDMESSAGE) {
                                                        player.sendMessage("In order to use Aio Item teleport function again, you will have to wait " + AioItemsConfigs.AIO_DELAY + "!");
                                                    }

                                                    return;
                                                }

                                                try {
                                                    Integer[] teleport = new Integer[3];
                                                    teleport[0] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[0];
                                                    teleport[1] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[1];
                                                    teleport[2] = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[2];
                                                    boolean canTeleport = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[3] == 1;
                                                    itemId = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[4];
                                                    itemPrice = TopListsLoader.getInstance().getTeleportInfo(Integer.parseInt(commandArgs[1]))[5];
                                                    if (!AioItemsConfigs.ALLOW_TELEPORT_DURING_SIEGE) {
                                                        if (SiegeManager.getInstance().getSiege(teleport[0], teleport[1], teleport[2]) != null) {
                                                            player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
                                                            return;
                                                        }

                                                        if (TownManager.townHasCastleInSiege(teleport[0], teleport[1]) && player.isInsideZone(ZoneIdType.TOWN)) {
                                                            player.sendPacket(SystemMessageId.NO_PORT_THAT_IS_IN_SIGE);
                                                            return;
                                                        }
                                                    }

                                                    if (!Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                        return;
                                                    }

                                                    if (canTeleport && !player.isNoble() && !player.isGM()) {
                                                        player.sendMessage("Only noble chars can teleport there.");
                                                        return;
                                                    }

                                                    if (player.isTransformed() && (player.getTransformationId() == 9 || player.getTransformationId() == 8)) {
                                                        player.untransform();
                                                    }

                                                    if (!player.isInsideZone(ZoneIdType.PEACE) && !player.isGM()) {
                                                        doTeleportLong(player, teleport[0], teleport[1], teleport[2]);
                                                        if (AioItemsConfigs.AIO_ENABLE_TP_DELAY) {
                                                            ThreadPoolManager.getInstance().executeGeneral(new AioItemDelay(player));
                                                        }
                                                    } else {
                                                        player.teleToLocation(teleport[0], teleport[1], teleport[2]);
                                                    }

                                                    player.destroyItemByItemId("AIO Teleport", itemId, (long) itemPrice, player, true);
                                                    return;
                                                } catch (Exception var19) {
                                                    SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
                                                    return;
                                                }
                                            }

                                            if (command.startsWith("rankarenainfo")) {
                                                if (LeaderboardsConfigs.RANK_ARENA_ENABLED) {
                                                    (npcHtmlMessage = new NpcHtmlMessage()).setHtml(ArenaLeaderboard.getInstance().showHtm(player.getObjectId()));
                                                    player.sendPacket(npcHtmlMessage);
                                                    return;
                                                }

                                                player.sendMessage("This service is currently disabled.");
                                                return;
                                            }

                                            if (command.startsWith("rankfishermaninfo")) {
                                                if (LeaderboardsConfigs.RANK_FISHERMAN_ENABLED) {
                                                    (npcHtmlMessage = new NpcHtmlMessage()).setHtml(FishermanLeaderboard.getInstance().showHtm(player.getObjectId()));
                                                    player.sendPacket(npcHtmlMessage);
                                                    return;
                                                }

                                                player.sendMessage("This service is currently disabled.");
                                                return;
                                            }

                                            if (command.startsWith("rankcraftinfo")) {
                                                if (LeaderboardsConfigs.RANK_CRAFT_ENABLED) {
                                                    (npcHtmlMessage = new NpcHtmlMessage()).setHtml(CraftLeaderboard.getInstance().showHtm(player.getObjectId()));
                                                    player.sendPacket(npcHtmlMessage);
                                                    return;
                                                }

                                                player.sendMessage("This service is currently disabled.");
                                                return;
                                            }

                                            if (command.startsWith("ranktvtinfo")) {
                                                if (LeaderboardsConfigs.RANK_TVT_ENABLED) {
                                                    (npcHtmlMessage = new NpcHtmlMessage()).setHtml(TvTLeaderboard.getInstance().showHtm(player.getObjectId()));
                                                    player.sendPacket(npcHtmlMessage);
                                                    return;
                                                }

                                                player.sendMessage("This service is currently disabled.");
                                                return;
                                            }

                                            if (command.startsWith("changeGender")) {
                                                if (command.startsWith("changeGenderDonate")) {
                                                    itemId = AioItemsConfigs.CHANGE_GENDER_DONATE_COIN;
                                                    itemPrice = AioItemsConfigs.CHANGE_GENDER_DONATE_PRICE;
                                                } else if (command.startsWith("changeGenderNormal")) {
                                                    itemId = AioItemsConfigs.CHANGE_GENDER_NORMAL_COIN;
                                                    itemPrice = AioItemsConfigs.CHANGE_GENDER_NORMAL_PRICE;
                                                }

                                                if (!Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                    return;
                                                }

                                                player.destroyItemByItemId("changeGender", itemId, (long) itemPrice, player, true);
                                                player.getAppearance().setSex(!player.getAppearance().getSex());
                                                player.sendMessage("Your gender has been changed.");
                                                player.broadcastUserInfo();
                                                TransformData.getInstance().transformPlayer(105, player);
                                                player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new TransformFinalizer(player), 200L));
                                                return;
                                            }

                                            if (command.startsWith("showMultiSellWindow")) {
                                                try {
                                                    int var3 = Integer.valueOf(commandArgs[1]);
                                                    if (!AioItemsConfigs.MULTISELL_LIST.contains(var3)) {
                                                        SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
                                                        return;
                                                    }
                                                    MultisellData.getInstance().separateAndSend(var3, player, (L2Npc) null, false);
                                                } catch (Exception var16) {
                                                    SecurityActions.startSecurity(player, SecurityType.AIO_ITEM);
                                                    return;
                                                }
                                            } else {
                                                if (command.startsWith("donateFormMain")) {
                                                    npcHtmlMessage = new NpcHtmlMessage();
                                                    npcTalkOption = IdFactory.getInstance().getNextId();
                                                    CaptchaImageGenerator.getInstance().captchaLogo(player, npcTalkOption);
                                                    npcHtmlMessage.setHtml("<html><title>Donate Captcha System</title><body><center>Enter the 5-digits code below and click Confirm.<br><img src=\"Crest.crest_" + Config.SERVER_ID + "_" + npcTalkOption + "\" width=256 height=64><br><font color=\"888888\">(There are only english uppercase letters.)</font><br><edit var=\"captcha\" width=110><br><button value=\"Confirm\" action=\"bypass -h Aioitem_confirmDonateCode $captcha\" width=80 height=26 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center></body></html>");
                                                    player.sendPacket(npcHtmlMessage);
                                                    player.setDonateCode(CaptchaImageGenerator.getInstance().getFinalString());
                                                    CaptchaImageGenerator.getInstance().getFinalString().replace(0, 5, "");
                                                    return;
                                                }

                                                if (command.startsWith("confirmDonateCode")) {
                                                    toChangeName = command.substring(17);
                                                    StringTokenizer var4 = new StringTokenizer(toChangeName, " ");

                                                    try {
                                                        String var5 = null;
                                                        if (var4.hasMoreTokens()) {
                                                            var5 = var4.nextToken();
                                                        }

                                                        augmentOption = player.getDonateCode();
                                                        if (var5 != null && augmentOption != null && var5.equals(augmentOption)) {
                                                            GenerateHtmls.sendPacket(player, "donate/donateform.htm");
                                                            return;
                                                        }

                                                        if (var5 == null || !var5.equals(augmentOption)) {
                                                            player.sendMessage("Incorrect captcha code try again.");
                                                        }
                                                    } catch (Exception var20) {
                                                        player.sendMessage("A problem occured while adding captcha!");
                                                        log.warn(String.valueOf(var20));
                                                        return;
                                                    }
                                                } else {
                                                    if (command.startsWith("sendDonateForm")) {
                                                        DonateHandler.sendDonateForm(player, command);
                                                        return;
                                                    }

                                                    if (command.startsWith("topFa")) {
                                                        if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0) {
                                                            GenerateHtmls.showTopFa(player);
                                                            return;
                                                        }

                                                        player.sendMessage("Top Lists: Disabled.");
                                                        return;
                                                    }

                                                    if (command.startsWith("TopPvp")) {
                                                        if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0) {
                                                            GenerateHtmls.showTopPvp(player);
                                                            return;
                                                        }

                                                        player.sendMessage("Top Lists: Disabled.");
                                                        return;
                                                    }

                                                    if (command.startsWith("TopPk")) {
                                                        if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0) {
                                                            GenerateHtmls.showTopPk(player);
                                                            return;
                                                        }

                                                        player.sendMessage("Top Lists: Disabled.");
                                                        return;
                                                    }

                                                    if (command.startsWith("TopClan")) {
                                                        if (CustomServerConfigs.TOP_LISTS_RELOAD_TIME > 0) {
                                                            GenerateHtmls.showTopClan(player);
                                                            return;
                                                        }

                                                        player.sendMessage("Top Lists: Disabled.");
                                                        return;
                                                    }

                                                    if (command.startsWith("rbinfo")) {
                                                        GenerateHtmls.showRbInfo(player);
                                                        return;
                                                    }

                                                    if (command.startsWith("addAugment")) {
                                                        player.sendPacket(new ExShowVariationMakeWindow());
                                                        return;
                                                    }

                                                    if (command.startsWith("delAugment")) {
                                                        player.sendPacket(new ExShowVariationCancelWindow());
                                                        return;
                                                    }

                                                    if (command.startsWith("removeAtt")) {
                                                        player.sendPacket(new ExShowBaseAttributeCancelWindow(player));
                                                        return;
                                                    }

                                                    if (command.startsWith("drawSymbol")) {
                                                        List hennaList = HennaData.getInstance().getHennaList(player.getClassId());
                                                        player.sendPacket(new HennaEquipList(player, hennaList));
                                                        return;
                                                    }

                                                    if (command.startsWith("removeSymbol")) {
                                                        boolean found = false;

                                                        for (npcTalkOption = 1; npcTalkOption <= 3; ++npcTalkOption) {
                                                            if (player.getHennaEx().getHenna(npcTalkOption) != null) {
                                                                found = true;
                                                            }
                                                        }

                                                        if (!found) {
                                                            player.sendMessage("You do not have dyes.");
                                                            return;
                                                        }

                                                        player.sendPacket(new HennaRemoveList(player));
                                                    } else {
                                                        if (command.startsWith("adenaToItem")) {
                                                            if (player.getAdena() > (long) AioItemsConfigs.AIO_EXCHANGE_PRICE) {
                                                                player.destroyItemByItemId("Aio Item", 57, (long) AioItemsConfigs.AIO_EXCHANGE_PRICE, player, true);
                                                                player.addItem("Aio Item", AioItemsConfigs.AIO_EXCHANGE_ID, 1L, player, true);
                                                            } else {
                                                                player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
                                                            }

                                                            GenerateHtmls.sendPacket(player, "service/exchange.htm");
                                                            return;
                                                        }

                                                        if (command.startsWith("itemToAdena")) {
                                                            itemId = AioItemsConfigs.AIO_EXCHANGE_ID;
                                                            itemPrice = 1;
                                                            if (Conditions.checkPlayerItemCount(player, itemId, itemPrice)) {
                                                                player.destroyItemByItemId("Aio Item", AioItemsConfigs.AIO_EXCHANGE_ID, 1L, player, true);
                                                                player.addItem("Aio Item", 57, (long) AioItemsConfigs.AIO_EXCHANGE_PRICE, player, true);
                                                            } else {
                                                                player.sendMessage("Not enough " + ItemData.getInstance().getTemplate(AioItemsConfigs.AIO_EXCHANGE_ID).getName() + ".");
                                                            }

                                                            GenerateHtmls.sendPacket(player, "service/exchange.htm");
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void doTeleportLong(L2PcInstance player, int xLoc, int yLoc, int zLoc) {
        player.abortCast();
        player.abortAttack();
        player.sendPacket(ActionFailed.STATIC_PACKET);
        player.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        player.setTarget(player);
        player.disableAllSkills();
        Broadcast.toSelfAndKnownPlayers(player, new MagicSkillUse(player, 1050, 1, 30000, 0));
        player.sendPacket(new SetupGauge(0, 30000));
        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new AioNpcTeleporter(player, xLoc, yLoc, zLoc), 30000L));
        player.forceIsCasting(10 + GameTimeController.getInstance().getGameTicks() + 300);
    }
}
