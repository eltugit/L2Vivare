package gr.sr.main;


import gr.sr.configsEngine.configs.impl.AioItemsConfigs;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.PcBangConfigs;
import gr.sr.configsEngine.configs.impl.SmartCommunityConfigs;
import gr.sr.pvpColorEngine.ColorSystemHandler;
import l2r.gameserver.instancemanager.CastleManager;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.entity.Castle;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ExAutoSoulShot;
import l2r.gameserver.network.serverpackets.ExBrPremiumState;
import l2r.gameserver.network.serverpackets.ExPCCafePointInfo;
import l2r.gameserver.network.serverpackets.SystemMessage;
import l2r.gameserver.util.Broadcast;


public class EnterWorldCustomHandler {
    public EnterWorldCustomHandler() {
    }

    
    public void extraNotifies(L2PcInstance player) {
        this.checkIfBot(player);
        if (CustomServerConfigs.ANNOUNCE_CASTLE_LORDS) {
            getInstance().notifyCastleOwner(player);
        }

        if (CustomServerConfigs.ANNOUNCE_HEROS_ON_LOGIN) {
            getInstance().notifyHeros(player);
        }

    }

    
    public void clanLeaderSystem(L2PcInstance player) {
        this.checkIfBot(player);
        if (CustomServerConfigs.ALT_ALLOW_CLAN_LEADER_NAME && !player.isGM() && player.getClan() != null && player.getClan().getLevel() >= CustomServerConfigs.CLAN_LEVEL_ACTIVATION && player.isClanLeader()) {
            player.getAppearance().setNameColor(Integer.decode("0x" + CustomServerConfigs.CLAN_LEADER_NAME_COLOR));
            player.getAppearance().setTitleColor(Integer.decode("0x" + CustomServerConfigs.CLAN_LEADER_TITLE_COLOR));
        }

    }

    
    public void extraMessages(L2PcInstance player) {
        this.checkIfBot(player);
        int allPlayersCount = L2World.getInstance().getAllPlayersCount();
        if (SmartCommunityConfigs.EXTRA_PLAYERS_COUNT > 0) {
            allPlayersCount += SmartCommunityConfigs.EXTRA_PLAYERS_COUNT;
        }

        if (player.isGM()) {
            player.sendMessage("Welcome Mister: " + player.getName());
            player.sendMessage("There are: " + allPlayersCount + " players online");
        } else {
            if (CustomServerConfigs.EXTRA_MESSAGES) {
                player.sendMessage("Welcome Mister: " + player.getName());
                player.sendMessage("PvP Kills: " + player.getPvpKills());
                player.sendMessage("PK Kills: " + player.getPkKills());
                player.sendMessage("There are: " + allPlayersCount + " players online");
            }

        }
    }

    
    public void extraItemsCheck(L2PcInstance player) {
        this.checkIfBot(player);
        if (CustomServerConfigs.GIVE_HELLBOUND_MAP) {
            if (player.getInventory().getItemByItemId(9994) == null) {
                player.addItem("Hellbound Map", 9994, 1L, player, true);
            }
        } else if (player.getInventory().getItemByItemId(9994) != null && player.getInventory().getItemByItemId(9994).getCount() > 0L) {
            player.destroyItemByItemId("Hellbound Map", 9994, player.getInventory().getItemByItemId(9994).getCount(), player, true);
        }

        if (AioItemsConfigs.DESTROY_ON_DISABLE && !AioItemsConfigs.ENABLE_AIO_NPCS && player.getInventory().getItemByItemId(AioItemsConfigs.AIO_ITEM_ID) != null && player.getInventory().getItemByItemId(AioItemsConfigs.AIO_ITEM_ID).getCount() > 0L) {
            player.destroyItemByItemId("AIO Item", AioItemsConfigs.AIO_ITEM_ID, player.getInventory().getItemByItemId(AioItemsConfigs.AIO_ITEM_ID).getCount(), player, true);
        }

        if (AioItemsConfigs.GIVEANDCHECK_ATSTARTUP && AioItemsConfigs.ENABLE_AIO_NPCS && player.getInventory().getItemByItemId(AioItemsConfigs.AIO_ITEM_ID) == null) {
            player.addItem("AIO Item", AioItemsConfigs.AIO_ITEM_ID, 1L, player, true);
        }

    }

    
    public void checkIfBot(L2PcInstance player) {
        if (!PlayerValues.isPlayer()) {
            player.getClient().closeNow();
        }

    }

    
    public void checkPremiumAndPcBangSystems(L2PcInstance player) {
        this.checkIfBot(player);
        if (player.isPremium()) {
            player.sendPacket(new ExBrPremiumState(player.getObjectId(), 1));
            player.sendMessage("Premium account: Activated.");
        } else {
            player.sendPacket(new ExBrPremiumState(player.getObjectId(), 0));
        }

        if (PcBangConfigs.PC_BANG_ENABLED) {
            if (player.getPcBangPoints() > 0) {
                player.sendPacket(new ExPCCafePointInfo(player.getPcBangPoints(), 0, false, false, 1));
                return;
            }

            player.sendPacket(new ExPCCafePointInfo());
        }

    }

    
    public void checkAutoSoulshot(L2PcInstance player) {
        this.checkIfBot(player);
        if (CustomServerConfigs.AUTO_ACTIVATE_SHOTS && player.getVarB("onEnterLoadSS")) {
            this.verifyAndLoadShots(player);
        }

    }

    
    public void notifyCastleOwner(L2PcInstance player) {
        this.checkIfBot(player);
        L2Clan clan;
        Castle castle;
        if ((clan = player.getClan()) != null && clan.getCastleId() > 0 && (castle = CastleManager.getInstance().getCastleById(clan.getCastleId())) != null && player.getObjectId() == clan.getLeaderId()) {
            Broadcast.toAllOnlinePlayers(player.getName() + " lord of " + castle.getName() + " logged in!");
        }

    }

    
    public void notifyHeros(L2PcInstance player) {
        this.checkIfBot(player);
        if (!player.isGM() && player.isHero()) {
            Broadcast.toAllOnlinePlayers("Hero " + player.getName() + " logged in!");
        }

    }

    
    public void verifyAndLoadShots(L2PcInstance player) {
        this.checkIfBot(player);
        short SS = -1;
        short BSS = -1;
        short BSSB = -1;
        if (!player.isDead() && player.getActiveWeaponItem() != null) {
            switch(player.getActiveWeaponItem().getCrystalType()) {
                case NONE:
                    SS = 1835;
                    BSS = 2509;
                    BSSB = 3947;
                    break;
                case D:
                    SS = 1463;
                    BSS = 2510;
                    BSSB = 3948;
                    break;
                case C:
                    SS = 1464;
                    BSS = 2511;
                    BSSB = 3949;
                    break;
                case B:
                    SS = 1465;
                    BSS = 2512;
                    BSSB = 3950;
                    break;
                case A:
                    SS = 1466;
                    BSS = 2513;
                    BSSB = 3951;
                    break;
                case S:
                case S80:
                case S84:
                    SS = 1467;
                    BSS = 2514;
                    BSSB = 3952;
            }

            SystemMessage sm;
            if (SS >= 0 && player.getInventory().getInventoryItemCount(SS, -1) > (long)CustomServerConfigs.AUTO_ACTIVATE_SHOTS_MIN) {
                player.addAutoSoulShot(SS);
                player.sendPacket(new ExAutoSoulShot(SS, 1));
                (sm = SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO)).addItemName(player.getInventory().getItemByItemId(SS));
                player.sendPacket(sm);
            }

            if (BSSB >= 0 && player.getInventory().getInventoryItemCount(BSSB, -1) > (long)CustomServerConfigs.AUTO_ACTIVATE_SHOTS_MIN) {
                player.addAutoSoulShot(BSSB);
                player.sendPacket(new ExAutoSoulShot(BSSB, 1));
                (sm = SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO)).addItemName(player.getInventory().getItemByItemId(BSSB));
                player.sendPacket(sm);
            } else if (BSS >= 0 && player.getInventory().getInventoryItemCount(BSS, -1) > (long)CustomServerConfigs.AUTO_ACTIVATE_SHOTS_MIN) {
                player.addAutoSoulShot(BSS);
                player.sendPacket(new ExAutoSoulShot(BSS, 1));
                (sm = SystemMessage.getSystemMessage(SystemMessageId.USE_OF_S1_WILL_BE_AUTO)).addItemName(player.getInventory().getItemByItemId(BSS));
                player.sendPacket(sm);
            }

            player.rechargeShots(true, true);
        }

    }

    
    public void initializeColorSystem(L2PcInstance player) {
        this.checkIfBot(player);
        ColorSystemHandler.getInstance().updateColor(player);
    }

    protected static EnterWorldCustomHandler instance;

    
    public static EnterWorldCustomHandler getInstance() {
        if (instance == null)
            instance = new EnterWorldCustomHandler();
        return instance;
    }
}
