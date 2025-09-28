package gr.sr.javaBuffer;


import gr.sr.configsEngine.configs.impl.BufferConfigs;
import gr.sr.javaBuffer.buffItem.dynamicHtmls.GenerateHtmls;
import gr.sr.javaBuffer.runnable.BuffSaver;
import gr.sr.javaBuffer.xml.dataHolder.BuffsHolder;
import gr.sr.securityEngine.SecurityActions;
import gr.sr.securityEngine.SecurityType;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;

import java.util.Iterator;
import java.util.List;


public class JavaBufferBypass {
    public JavaBufferBypass() {
    }

    
    public static void callBuffCommand(L2PcInstance player, String buffId, String page, int type) {
        if (PlayerMethods.checkDelay(player)) {
            if (PlayerMethods.checkPriceConsume(player, 1)) {
                int buff = Integer.parseInt(buffId);
                BuffsInstance buffsInstance = BuffsHolder.getInstance().getBuff(buff);

                if (buffsInstance == null) {
                    switch(type) {
                        case 0:
                            SecurityActions.startSecurity(player, SecurityType.AIO_ITEM_BUFFER);
                            return;
                        case 1:
                            SecurityActions.startSecurity(player, SecurityType.COMMUNITY_SYSTEM);
                            return;
                        default:
                            SecurityActions.startSecurity(player, SecurityType.NPC_BUFFER);
                    }
                } else {
                    L2Skill skill;
                    if (player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null) {
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getCustomLevel());
                    } else {
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getLevel());
                    }
                    if (skill != null) {
                        skill.getEffects(player, player);
                        returnPage(player, page, type);
                        PlayerMethods.addDelay(player);
                    }else{
                        switch(type) {
                            case 0:
                                SecurityActions.startSecurity(player, SecurityType.AIO_ITEM_BUFFER);
                                return;
                            case 1:
                                SecurityActions.startSecurity(player, SecurityType.COMMUNITY_SYSTEM);
                                return;
                            default:
                                SecurityActions.startSecurity(player, SecurityType.NPC_BUFFER);
                        }
                    }
                }
            }
        }
    }

    
    public static void callPetBuffCommand(L2PcInstance player, String profile) {
        List buffList;
        int sizeOfBuff = (buffList = PlayerMethods.getProfileBuffs(profile, player)).size();
        if (PlayerMethods.checkDelay(player)) {
            L2Summon summon;
            if ((summon = player.getSummon()) == null) {
                player.sendMessage("Summon your pet first.");
            } else if (PlayerMethods.checkPriceConsume(player, sizeOfBuff)) {
                Iterator iterator = buffList.iterator();
                BuffsInstance buffInstance;

                while(iterator.hasNext()){
                    if(player.isInsideRadius(summon, 300, false, false)){
                        int buffId = (Integer)iterator.next();
                        buffInstance = BuffsHolder.getInstance().getBuff(buffId);
                        L2Skill skill;
                        if(player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null){
                            skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getCustomLevel());
                        }else{
                            skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getLevel());
                        }
                        if (skill != null) {
                            skill.getEffects(player, summon);
                        }
                    }
                }

                if (BufferConfigs.HEAL_PLAYER_AFTER_ACTIONS) {
                    player.getSummon().setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                    player.getSummon().setCurrentCp((double)player.getMaxCp());
                }

                PlayerMethods.addDelay(player);
            }
        }
    }

    
    public static void callPartyBuffCommand(L2PcInstance player, String profile) {
        List buffList;
        int var2 = (buffList = PlayerMethods.getProfileBuffs(profile, player)).size();
        if (PlayerMethods.checkDelay(player)) {
            if (player.getParty() == null) {
                player.sendMessage("Your are not in a party.");
            } else if (PlayerMethods.checkPriceConsume(player, var2)) {
                Iterator partyMembers = player.getParty().getMembers().iterator();
                L2PcInstance partyMember;
                while(partyMembers.hasNext()){
                    partyMember = (L2PcInstance)partyMembers.next();
                    if(player.isInsideRadius(partyMember, 300, false, false)){
                        Iterator buffIterator = buffList.iterator();
                        BuffsInstance buffInstance;
                        L2Skill skill;
                        while(buffIterator.hasNext()){
                            int buffId = (Integer)buffIterator.next();
                            buffInstance = BuffsHolder.getInstance().getBuff(buffId);

                            if (player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null) {
                                skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getCustomLevel());
                            } else {
                                skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getLevel());
                            }
                            if (skill != null) {
                                skill.getEffects(partyMember, partyMember);
                            }
                        }
                        if (BufferConfigs.HEAL_PLAYER_AFTER_ACTIONS) {
                            partyMember.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                            partyMember.setCurrentCp((double)player.getMaxCp());
                        }
                    }
                }
                PlayerMethods.addDelay(player);
            }
        }
    }

    
    public static void callSelfBuffCommand(L2PcInstance player, String profile) {
        List buffList;
        if ((buffList = PlayerMethods.getProfileBuffs(profile, player)) != null) {
            if (PlayerMethods.checkPriceConsume(player, buffList.size())) {
                Iterator iterator = buffList.iterator();
                BuffsInstance buffInstance;

                while(iterator.hasNext()){
                        int buffId = (Integer)iterator.next();
                        buffInstance = BuffsHolder.getInstance().getBuff(buffId);
                        L2Skill skill;
                        if (player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null) {
                            skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getCustomLevel());
                        } else {
                            skill = SkillData.getInstance().getInfo(buffInstance.getId(), buffInstance.getLevel());
                        }
                        if (skill != null) {
                            skill.getEffects(player, player);
                        }
                }

                if (BufferConfigs.HEAL_PLAYER_AFTER_ACTIONS) {
                    player.getSummon().setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                    player.getSummon().setCurrentCp((double)player.getMaxCp());
                }

                PlayerMethods.addDelay(player);


            }
        }
    }

    
    public static void callSaveProfile(L2PcInstance player, String profile, int type) {
        if (PlayerMethods.createProfile(profile, player)) {
            switch(type) {
                case 0:
                    BufferPacketSender.sendPacket(player, "scheme.htm", BufferPacketCategories.FILE, 0);
                    return;
                case 1:
                    BufferPacketSender.sendPacket(player, "scheme.htm", BufferPacketCategories.COMMUNITY, 1);
                    return;
                default:
                    BufferPacketSender.sendPacket(player, "scheme.htm", BufferPacketCategories.FILE, type);
            }
        }
    }

    
    public static void callAvailableCommand(L2PcInstance player, String command, String subCommand, int npcObjId) {
        switch(command) {
            case "showAvaliableMisc":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.MISC, "addMisc");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.MISC, "addMisc");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.MISC, "addMisc", npcObjId);
                        return;
                }
            case "showAvaliableOver":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.OVERLORD, "addOver");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.OVERLORD, "addOver");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.OVERLORD, "addOver", npcObjId);
                        return;
                }
            case "showAvaliableProp":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.PROPHET, "addProp");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.PROPHET, "addProp");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.PROPHET, "addProp", npcObjId);
                        return;
                }
            case "showAvaliableSong":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.SONG, "addSong");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.SONG, "addSong");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.SONG, "addSong", npcObjId);
                        return;
                }
            case "showAvaliableChant":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.CHANT, "addChant");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.CHANT, "addChant");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.CHANT, "addChant", npcObjId);
                        return;
                }
            case "showAvaliableDance":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DANCE, "addDance");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DANCE, "addDance");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DANCE, "addDance", npcObjId);
                        return;
                }
            case "showAvaliableDwarf":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DWARF, "addDwarf");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DWARF, "addDwarf");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.DWARF, "addDwarf", npcObjId);
                        return;
                }
            case "showAvaliableElder":
                switch(npcObjId) {
                    case 0:
                        GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.ELDER, "addElder");
                        return;
                    case 1:
                        gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.ELDER, "addElder");
                        return;
                    default:
                        gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, subCommand, BufferMenuCategories.ELDER, "addElder", npcObjId);
                        return;
                }
        }
    }

    public static void callBuffToAdd(BufferMenuCategories categories, L2PcInstance player, String profile, int mode) {
        String property = "addProp";
        switch(categories) {
            case DANCE:
                property = "addDance";
                break;
            case SONG:
                property = "addSong";
                break;
            case MISC:
                property = "addMisc";
                break;
            case ELDER:
                property = "addElder";
                break;
            case OVERLORD:
                property = "addOver";
                break;
            case PROPHET:
                property = "addProp";
                break;
            case DWARF:
                property = "addDwarf";
                break;
            case CHANT:
                property = "addChant";
                break;
            case NONE:
                property = "removeBuffs";
        }

        switch(mode) {
            case 0:
                GenerateHtmls.showBuffsToAdd(player, profile, categories, property);
                return;
            case 1:
                gr.sr.javaBuffer.buffCommunity.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, profile, categories, property);
                return;
            default:
                gr.sr.javaBuffer.buffNpc.dynamicHtmls.GenerateHtmls.showBuffsToAdd(player, profile, categories, property, mode);
        }
    }

    
    public static void callAddCommand(L2PcInstance player, String command, String profile, String buffId, int mode) {
        BufferMenuCategories bufferMenuCategories = BufferMenuCategories.PROPHET;
        switch(command) {
            case "addChant":
                bufferMenuCategories = BufferMenuCategories.CHANT;
                break;
            case "addDance":
                bufferMenuCategories = BufferMenuCategories.DANCE;
                break;
            case "addDwarf":
                bufferMenuCategories = BufferMenuCategories.DWARF;
                break;
            case "addElder":
                bufferMenuCategories = BufferMenuCategories.ELDER;
                break;
            case "addMisc":
                bufferMenuCategories = BufferMenuCategories.MISC;
                break;
            case "addOver":
                bufferMenuCategories = BufferMenuCategories.OVERLORD;
                break;
            case "addProp":
                bufferMenuCategories = BufferMenuCategories.PROPHET;
                break;
            case "addSong":
                bufferMenuCategories = BufferMenuCategories.SONG;
        }

        if (bufferMenuCategories != BufferMenuCategories.DANCE && bufferMenuCategories != BufferMenuCategories.SONG) {
            if (!PlayerMethods.checkBuffsAmount(player, profile, bufferMenuCategories, mode)) {
                return;
            }
        } else if (!PlayerMethods.checkDanceAmount(player, profile, bufferMenuCategories, mode)) {
            return;
        }

        ThreadPoolManager.getInstance().executeGeneral(new BuffSaver(player, bufferMenuCategories, profile, Integer.parseInt(buffId), mode));
    }

    public static void returnPage(L2PcInstance player, String command, int mode) {
        switch(mode) {
            case 0:
                switch(command) {
                    case "buffmisc":
                        BufferPacketSender.sendPacket(player, "misc.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffover":
                        BufferPacketSender.sendPacket(player, "overlord.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffprop":
                        BufferPacketSender.sendPacket(player, "prophet.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffsong":
                        BufferPacketSender.sendPacket(player, "songs.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffdance":
                        BufferPacketSender.sendPacket(player, "dances.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffdwarf":
                        BufferPacketSender.sendPacket(player, "warsmith.htm", BufferPacketCategories.FILE, 0);
                        return;
                    case "buffelder":
                        BufferPacketSender.sendPacket(player, "elder.htm", BufferPacketCategories.FILE, 0);
                        break;
                    case "buffwar":
                        BufferPacketSender.sendPacket(player, "warcryer.htm", BufferPacketCategories.FILE, 0);
                        return;
                }

            case 1:
                switch(command) {
                    case "buffmisc":
                        BufferPacketSender.sendPacket(player, "misc.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffover":
                        BufferPacketSender.sendPacket(player, "overlord.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffprop":
                        BufferPacketSender.sendPacket(player, "prophet.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffsong":
                        BufferPacketSender.sendPacket(player, "songs.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffdance":
                        BufferPacketSender.sendPacket(player, "dances.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffdwarf":
                        BufferPacketSender.sendPacket(player, "warsmith.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                    case "buffelder":
                        BufferPacketSender.sendPacket(player, "elder.htm", BufferPacketCategories.COMMUNITY, 1);
                        break;
                    case "buffwar":
                        BufferPacketSender.sendPacket(player, "warcryer.htm", BufferPacketCategories.COMMUNITY, 1);
                        return;
                }
            default:
                switch(command) {
                    case "buffmisc":
                        BufferPacketSender.sendPacket(player, "misc.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffover":
                        BufferPacketSender.sendPacket(player, "overlord.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffprop":
                        BufferPacketSender.sendPacket(player, "prophet.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffsong":
                        BufferPacketSender.sendPacket(player, "songs.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffdance":
                        BufferPacketSender.sendPacket(player, "dances.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffdwarf":
                        BufferPacketSender.sendPacket(player, "warsmith.htm", BufferPacketCategories.FILE, mode);
                        return;
                    case "buffelder":
                        BufferPacketSender.sendPacket(player, "elder.htm", BufferPacketCategories.FILE, mode);
                        break;
                    case "buffwar":
                        BufferPacketSender.sendPacket(player, "warcryer.htm", BufferPacketCategories.FILE, mode);
                        return;
                }
        }

    }
}
