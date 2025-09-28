package scripts.handlers;

import l2r.Config;
import l2r.gameserver.handler.ActionHandler;
import l2r.gameserver.handler.ActionShiftHandler;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.handler.BypassHandler;
import l2r.gameserver.handler.ChatHandler;
import l2r.gameserver.handler.IHandler;
import l2r.gameserver.handler.ItemHandler;
import l2r.gameserver.handler.PlayerActionHandler;
import l2r.gameserver.handler.PunishmentHandler;
import l2r.gameserver.handler.SkillHandler;
import l2r.gameserver.handler.TargetHandler;
import l2r.gameserver.handler.TelnetHandler;
import l2r.gameserver.handler.UserCommandHandler;
import l2r.gameserver.handler.VoicedCommandHandler;

import gr.sr.configsEngine.configs.impl.AioItemsConfigs;
import gr.sr.configsEngine.configs.impl.AntibotConfigs;
import gr.sr.configsEngine.configs.impl.BufferConfigs;
import gr.sr.configsEngine.configs.impl.ChaoticZoneConfigs;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.GetRewardVoteSystemConfigs;
import gr.sr.configsEngine.configs.impl.PremiumServiceConfigs;
import gr.sr.voteEngine.RewardVote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scripts.handlers.actionhandlers.L2ArtefactInstanceAction;
import scripts.handlers.actionhandlers.L2DecoyAction;
import scripts.handlers.actionhandlers.L2DoorInstanceAction;
import scripts.handlers.actionhandlers.L2ItemInstanceAction;
import scripts.handlers.actionhandlers.L2NpcAction;
import scripts.handlers.actionhandlers.L2PcInstanceAction;
import scripts.handlers.actionhandlers.L2PetInstanceAction;
import scripts.handlers.actionhandlers.L2StaticObjectInstanceAction;
import scripts.handlers.actionhandlers.L2SummonAction;
import scripts.handlers.actionhandlers.L2TrapAction;
import scripts.handlers.actionshifthandlers.L2DoorInstanceActionShift;
import scripts.handlers.actionshifthandlers.L2ItemInstanceActionShift;
import scripts.handlers.actionshifthandlers.L2NpcActionShift;
import scripts.handlers.actionshifthandlers.L2PcInstanceActionShift;
import scripts.handlers.actionshifthandlers.L2StaticObjectInstanceActionShift;
import scripts.handlers.actionshifthandlers.L2SummonActionShift;
import scripts.handlers.admincommandhandlers.AdminAdmin;
import scripts.handlers.admincommandhandlers.AdminAnnouncements;
import scripts.handlers.admincommandhandlers.AdminBBS;
import scripts.handlers.admincommandhandlers.AdminBanHwid;
import scripts.handlers.admincommandhandlers.AdminBuffs;
import scripts.handlers.admincommandhandlers.AdminCHSiege;
import scripts.handlers.admincommandhandlers.AdminCamera;
import scripts.handlers.admincommandhandlers.AdminChangeAccessLevel;
import scripts.handlers.admincommandhandlers.AdminCheckBots;
import scripts.handlers.admincommandhandlers.AdminClan;
import scripts.handlers.admincommandhandlers.AdminCreateItem;
import scripts.handlers.admincommandhandlers.AdminCursedWeapons;
import scripts.handlers.admincommandhandlers.AdminCustomCreateItem;
import scripts.handlers.admincommandhandlers.AdminDebug;
import scripts.handlers.admincommandhandlers.AdminDelete;
import scripts.handlers.admincommandhandlers.AdminDisconnect;
import scripts.handlers.admincommandhandlers.AdminDoorControl;
import scripts.handlers.admincommandhandlers.AdminEditChar;
import scripts.handlers.admincommandhandlers.AdminEffects;
import scripts.handlers.admincommandhandlers.AdminElement;
import scripts.handlers.admincommandhandlers.AdminEnchant;
import scripts.handlers.admincommandhandlers.AdminExpSp;
import scripts.handlers.admincommandhandlers.AdminFightCalculator;
import scripts.handlers.admincommandhandlers.AdminFortSiege;
import scripts.handlers.admincommandhandlers.AdminGamePoints;
import scripts.handlers.admincommandhandlers.AdminGm;
import scripts.handlers.admincommandhandlers.AdminGmChat;
import scripts.handlers.admincommandhandlers.AdminGraciaSeeds;
import scripts.handlers.admincommandhandlers.AdminGrandBoss;
import scripts.handlers.admincommandhandlers.AdminHWIDBan;
import scripts.handlers.admincommandhandlers.AdminHeal;
import scripts.handlers.admincommandhandlers.AdminHellbound;
import scripts.handlers.admincommandhandlers.AdminHtml;
import scripts.handlers.admincommandhandlers.AdminInstance;
import scripts.handlers.admincommandhandlers.AdminInstanceZone;
import scripts.handlers.admincommandhandlers.AdminInventory;
import scripts.handlers.admincommandhandlers.AdminInvul;
import scripts.handlers.admincommandhandlers.AdminKick;
import scripts.handlers.admincommandhandlers.AdminKill;
import scripts.handlers.admincommandhandlers.AdminLevel;
import scripts.handlers.admincommandhandlers.AdminLogin;
import scripts.handlers.admincommandhandlers.AdminLogsViewer;
import scripts.handlers.admincommandhandlers.AdminMammon;
import scripts.handlers.admincommandhandlers.AdminManor;
import scripts.handlers.admincommandhandlers.AdminMenu;
import scripts.handlers.admincommandhandlers.AdminMessages;
import scripts.handlers.admincommandhandlers.AdminMobGroup;
import scripts.handlers.admincommandhandlers.AdminMonsterRace;
import scripts.handlers.admincommandhandlers.AdminOlympiad;
import scripts.handlers.admincommandhandlers.AdminPForge;
import scripts.handlers.admincommandhandlers.AdminPcCondOverride;
import scripts.handlers.admincommandhandlers.AdminPetition;
import scripts.handlers.admincommandhandlers.AdminPledge;
import scripts.handlers.admincommandhandlers.AdminPolymorph;
import scripts.handlers.admincommandhandlers.AdminPremium;
import scripts.handlers.admincommandhandlers.AdminPunishment;
import scripts.handlers.admincommandhandlers.AdminQuest;
import scripts.handlers.admincommandhandlers.AdminReload;
import scripts.handlers.admincommandhandlers.AdminRepairChar;
import scripts.handlers.admincommandhandlers.AdminRes;
import scripts.handlers.admincommandhandlers.AdminRide;
import scripts.handlers.admincommandhandlers.AdminScan;
import scripts.handlers.admincommandhandlers.AdminShop;
import scripts.handlers.admincommandhandlers.AdminShowQuests;
import scripts.handlers.admincommandhandlers.AdminShutdown;
import scripts.handlers.admincommandhandlers.AdminSiege;
import scripts.handlers.admincommandhandlers.AdminSkill;
import scripts.handlers.admincommandhandlers.AdminSpawn;
import scripts.handlers.admincommandhandlers.AdminSummon;
import scripts.handlers.admincommandhandlers.AdminTarget;
import scripts.handlers.admincommandhandlers.AdminTargetSay;
import scripts.handlers.admincommandhandlers.AdminTeleport;
import scripts.handlers.admincommandhandlers.AdminTerritoryWar;
import scripts.handlers.admincommandhandlers.AdminTest;
import scripts.handlers.admincommandhandlers.AdminUnblockIp;
import scripts.handlers.admincommandhandlers.AdminVitality;
import scripts.handlers.admincommandhandlers.AdminZone;
import scripts.handlers.bypasshandlers.ArenaBuff;
import scripts.handlers.bypasshandlers.Augment;
import scripts.handlers.bypasshandlers.Buy;
import scripts.handlers.bypasshandlers.BuyShadowItem;
import scripts.handlers.bypasshandlers.ChatLink;
import scripts.handlers.bypasshandlers.ClanWarehouse;
import scripts.handlers.bypasshandlers.ElcardiaBuff;
import scripts.handlers.bypasshandlers.Festival;
import scripts.handlers.bypasshandlers.Freight;
import scripts.handlers.bypasshandlers.ItemAuctionLink;
import scripts.handlers.bypasshandlers.Link;
import scripts.handlers.bypasshandlers.Loto;
import scripts.handlers.bypasshandlers.Multisell;
import scripts.handlers.bypasshandlers.NpcViewMod;
import scripts.handlers.bypasshandlers.Observation;
import scripts.handlers.bypasshandlers.OlympiadManagerLink;
import scripts.handlers.bypasshandlers.OlympiadObservation;
import scripts.handlers.bypasshandlers.PlayerHelp;
import scripts.handlers.bypasshandlers.PrivateWarehouse;
import scripts.handlers.bypasshandlers.QuestLink;
import scripts.handlers.bypasshandlers.QuestList;
import scripts.handlers.bypasshandlers.ReleaseAttribute;
import scripts.handlers.bypasshandlers.RemoveDeathPenalty;
import scripts.handlers.bypasshandlers.RentPet;
import scripts.handlers.bypasshandlers.Rift;
import scripts.handlers.bypasshandlers.SkillList;
import scripts.handlers.bypasshandlers.SupportBlessing;
import scripts.handlers.bypasshandlers.SupportMagic;
import scripts.handlers.bypasshandlers.TerritoryStatus;
import scripts.handlers.bypasshandlers.VoiceCommand;
import scripts.handlers.bypasshandlers.Wear;
import scripts.handlers.chathandlers.ChatAll;
import scripts.handlers.chathandlers.ChatAlliance;
import scripts.handlers.chathandlers.ChatBattlefield;
import scripts.handlers.chathandlers.ChatClan;
import scripts.handlers.chathandlers.ChatHeroVoice;
import scripts.handlers.chathandlers.ChatParty;
import scripts.handlers.chathandlers.ChatPartyMatchRoom;
import scripts.handlers.chathandlers.ChatPartyRoomAll;
import scripts.handlers.chathandlers.ChatPartyRoomCommander;
import scripts.handlers.chathandlers.ChatPetition;
import scripts.handlers.chathandlers.ChatShout;
import scripts.handlers.chathandlers.ChatTell;
import scripts.handlers.chathandlers.ChatTrade;
import scripts.handlers.itemhandlers.*;
import scripts.handlers.playeractions.AirshipAction;
import scripts.handlers.playeractions.BotReport;
import scripts.handlers.playeractions.PetAttack;
import scripts.handlers.playeractions.PetHold;
import scripts.handlers.playeractions.PetMove;
import scripts.handlers.playeractions.PetSkillUse;
import scripts.handlers.playeractions.PetStop;
import scripts.handlers.playeractions.PrivateStore;
import scripts.handlers.playeractions.Ride;
import scripts.handlers.playeractions.RunWalk;
import scripts.handlers.playeractions.ServitorAttack;
import scripts.handlers.playeractions.ServitorHold;
import scripts.handlers.playeractions.ServitorMove;
import scripts.handlers.playeractions.ServitorSkillUse;
import scripts.handlers.playeractions.ServitorStop;
import scripts.handlers.playeractions.SitStand;
import scripts.handlers.playeractions.SocialAction;
import scripts.handlers.playeractions.UnsummonPet;
import scripts.handlers.playeractions.UnsummonServitor;
import scripts.handlers.punishmenthandlers.BanHandler;
import scripts.handlers.punishmenthandlers.ChatBanHandler;
import scripts.handlers.punishmenthandlers.JailHandler;
import scripts.handlers.skillhandlers.Blow;
import scripts.handlers.skillhandlers.ChainHeal;
import scripts.handlers.skillhandlers.Continuous;
import scripts.handlers.skillhandlers.Disablers;
import scripts.handlers.skillhandlers.Dummy;
import scripts.handlers.skillhandlers.Mdam;
import scripts.handlers.skillhandlers.Pdam;
import scripts.handlers.skillhandlers.Unlock;
import scripts.handlers.targethandlers.*;
import scripts.handlers.telnethandlers.ChatsHandler;
import scripts.handlers.telnethandlers.DebugHandler;
import scripts.handlers.telnethandlers.HelpHandler;
import scripts.handlers.telnethandlers.PlayerHandler;
import scripts.handlers.telnethandlers.ReloadHandler;
import scripts.handlers.telnethandlers.ServerHandler;
import scripts.handlers.telnethandlers.StatusHandler;
import scripts.handlers.telnethandlers.ThreadHandler;
import scripts.handlers.usercommandhandlers.ChannelDelete;
import scripts.handlers.usercommandhandlers.ChannelInfo;
import scripts.handlers.usercommandhandlers.ChannelLeave;
import scripts.handlers.usercommandhandlers.ClanPenalty;
import scripts.handlers.usercommandhandlers.ClanWarsList;
import scripts.handlers.usercommandhandlers.Dismount;
import scripts.handlers.usercommandhandlers.InstanceZone;
import scripts.handlers.usercommandhandlers.Loc;
import scripts.handlers.usercommandhandlers.Mount;
import scripts.handlers.usercommandhandlers.MyBirthday;
import scripts.handlers.usercommandhandlers.OlympiadStat;
import scripts.handlers.usercommandhandlers.PartyInfo;
import scripts.handlers.usercommandhandlers.SiegeStatus;
import scripts.handlers.usercommandhandlers.Time;
import scripts.handlers.usercommandhandlers.Unstuck;
import scripts.handlers.voicedcommandhandlers.AioItemVCmd;
import scripts.handlers.voicedcommandhandlers.Antibot;
import scripts.handlers.voicedcommandhandlers.Banking;
import scripts.handlers.voicedcommandhandlers.CcpVCmd;
import scripts.handlers.voicedcommandhandlers.ChangePassword;
import scripts.handlers.voicedcommandhandlers.ChatAdmin;
import scripts.handlers.voicedcommandhandlers.Debug;
import scripts.handlers.voicedcommandhandlers.EvenlyDistributeItems;
import scripts.handlers.voicedcommandhandlers.Hellbound;
import scripts.handlers.voicedcommandhandlers.ItemBufferVCmd;
import scripts.handlers.voicedcommandhandlers.Lang;
import scripts.handlers.voicedcommandhandlers.OnlineVCmd;
import scripts.handlers.voicedcommandhandlers.PremiumVCmd;
import scripts.handlers.voicedcommandhandlers.PvpZoneVCmd;
import scripts.handlers.voicedcommandhandlers.RepairVCmd;
import scripts.handlers.voicedcommandhandlers.TeleportsVCmd;
import scripts.handlers.voicedcommandhandlers.Wedding;

/**
 * Master handler.
 * @author vGodFather
 */
public class MasterHandler
{
	private static final Logger _log = LoggerFactory.getLogger(MasterHandler.class);
	
	private static final Class<?>[] ACTION_HANDLERS =
	{
		// Action Handlers
		L2ArtefactInstanceAction.class,
		L2DecoyAction.class,
		L2DoorInstanceAction.class,
		L2ItemInstanceAction.class,
		L2NpcAction.class,
		L2PcInstanceAction.class,
		L2PetInstanceAction.class,
		L2StaticObjectInstanceAction.class,
		L2SummonAction.class,
		L2TrapAction.class,
	};
	
	private static final Class<?>[] ACTION_SHIFT_HANDLERS =
	{
		// Action Shift Handlers
		L2DoorInstanceActionShift.class,
		L2ItemInstanceActionShift.class,
		L2NpcActionShift.class,
		L2PcInstanceActionShift.class,
		L2StaticObjectInstanceActionShift.class,
		L2SummonActionShift.class,
	};
	
	private static final Class<?>[] ADMIN_HANDLERS =
	{
		// Admin Command Handlers
		AdminAdmin.class,
		AdminAnnouncements.class,
		AdminBBS.class,
		AdminBuffs.class,
		AdminCamera.class,
		AdminChangeAccessLevel.class,
		AdminCheckBots.class,
		AdminCHSiege.class,
		AdminClan.class,
		AdminCreateItem.class,
		AdminCursedWeapons.class,
		AdminCustomCreateItem.class,
		AdminDebug.class,
		AdminDelete.class,
		AdminDisconnect.class,
		AdminDoorControl.class,
		AdminEditChar.class,
		AdminEffects.class,
		AdminElement.class,
		AdminEnchant.class,
		AdminExpSp.class,
		AdminFightCalculator.class,
		AdminFortSiege.class,
		AdminGamePoints.class,
		// AdminGeodata.class,
		AdminGm.class,
		AdminGmChat.class,
		AdminGraciaSeeds.class,
		AdminGrandBoss.class,
		AdminHeal.class,
		AdminHellbound.class,
		AdminHtml.class,
		AdminHWIDBan.class,
		AdminInstance.class,
		AdminInstanceZone.class,
		AdminInventory.class,
		AdminInvul.class,
		AdminKick.class,
		AdminKill.class,
		AdminLevel.class,
		AdminLogin.class,
		AdminMammon.class,
		AdminManor.class,
		AdminMenu.class,
		AdminMessages.class,
		AdminMobGroup.class,
		AdminMonsterRace.class,
		AdminOlympiad.class,
		// AdminPathNode.class,
		AdminPcCondOverride.class,
		AdminPetition.class,
		AdminPForge.class,
		AdminPledge.class,
		AdminPolymorph.class,
		AdminPremium.class,
		AdminPunishment.class,
		AdminQuest.class,
		AdminReload.class,
		AdminRepairChar.class,
		AdminRes.class,
		AdminRide.class,
		AdminScan.class,
		AdminShop.class,
		AdminShowQuests.class,
		AdminShutdown.class,
		AdminSiege.class,
		AdminSkill.class,
		AdminSpawn.class,
		AdminSummon.class,
		AdminTarget.class,
		AdminTargetSay.class,
		AdminTeleport.class,
		AdminTerritoryWar.class,
		AdminTest.class,
		AdminUnblockIp.class,
		AdminVitality.class,
		AdminZone.class,
		AdminLogsViewer.class,
		AdminBanHwid.class,
	};
	
	private static final Class<?>[] BYPASS_HANDLERS =
	{
		// Bypass Handlers
		ArenaBuff.class,
		Augment.class,
		Buy.class,
		BuyShadowItem.class,
		ChatLink.class,
		ClanWarehouse.class,
		ElcardiaBuff.class,
		Festival.class,
		Freight.class,
		ItemAuctionLink.class,
		Link.class,
		Loto.class,
		Multisell.class,
		NpcViewMod.class,
		Observation.class,
		OlympiadManagerLink.class,
		OlympiadObservation.class,
		PlayerHelp.class,
		PrivateWarehouse.class,
		QuestLink.class,
		QuestList.class,
		ReleaseAttribute.class,
		RemoveDeathPenalty.class,
		RentPet.class,
		Rift.class,
		SkillList.class,
		SupportBlessing.class,
		SupportMagic.class,
		TerritoryStatus.class,
		VoiceCommand.class,
		Wear.class,
	};
	
	private static final Class<?>[] CHAT_HANDLERS =
	{
		// Chat Handlers
		ChatAll.class,
		ChatAlliance.class,
		ChatBattlefield.class,
		ChatClan.class,
		ChatHeroVoice.class,
		ChatParty.class,
		ChatPartyMatchRoom.class,
		ChatPartyRoomAll.class,
		ChatPartyRoomCommander.class,
		ChatPetition.class,
		ChatShout.class,
		ChatTell.class,
		ChatTrade.class,
	};
	
	private static final Class<?>[] ITEM_HANDLERS =
	{
		// Item Handlers
		(BufferConfigs.ENABLE_ITEM_BUFFER ? AioItemBuff.class : null),
		(AioItemsConfigs.ENABLE_AIO_NPCS ? AioItemNpcs.class : null),
		BeastSoulShot.class,
		BeastSpiritShot.class,
		BlessedSpiritShot.class,
		Book.class,
		Bypass.class,
		Calculator.class,
		CharmOfCourage.class,
		ChristmasTree.class,
		Disguise.class,
		Elixir.class,
		EnchantAttribute.class,
		EnchantScrolls.class,
		EventItem.class,
		ExtractableItems.class,
		FishShots.class,
		Harvester.class,
		ItemSkills.class,
		ItemSkillsTemplate.class,
		ManaPotion.class,
		Maps.class,
		MercTicket.class,
		NicknameColor.class,
		PetFood.class,
		Recipes.class,
		RollingDice.class,
		Seed.class,
		SevenSignsRecord.class,
		SoulShots.class,
		SpecialXMas.class,
		SpiritShot.class,
		SummonItems.class,
		TeleportBookmark.class,
		DressMeItem.class,
		AddSharedSkill.class,
	};
	
	private static final Class<?>[] PUNISHMENT_HANDLERS =
	{
		// Punishment Handlers
		BanHandler.class,
		ChatBanHandler.class,
		JailHandler.class,
	};
	
	private static final Class<?>[] SKILL_HANDLERS =
	{
		// Skill Handlers
		Blow.class,
		ChainHeal.class,
		Continuous.class,
		Disablers.class,
		Dummy.class,
		Mdam.class,
		Pdam.class,
		Unlock.class,
	};
	
	private static final Class<?>[] USER_COMMAND_HANDLERS =
	{
		// User Command Handlers
		ChannelDelete.class,
		ChannelInfo.class,
		ChannelLeave.class,
		ClanPenalty.class,
		ClanWarsList.class,
		Dismount.class,
		InstanceZone.class,
		Loc.class,
		Mount.class,
		MyBirthday.class,
		OlympiadStat.class,
		PartyInfo.class,
		SiegeStatus.class,
		Time.class,
		Unstuck.class,
	};
	
	private static final Class<?>[] TARGET_HANDLERS =
	{
		// Target Handlers
		Area.class,
		AreaCorpseMob.class,
		AreaFriendly.class,
		AreaSummon.class,
		Aura.class,
		AuraCorpseMob.class,
		AuraFriendly.class,
		AuraUndeadEnemy.class,
		BehindArea.class,
		BehindAura.class,
		Clan.class,
		ClanMember.class,
		CommandChannel.class,
		CorpseClan.class,
		CorpseMob.class,
		CorpsePet.class,
		CorpsePlayer.class,
		CorpsePetMob.class,
		EnemySummon.class,
		FlagPole.class,
		FrontArea.class,
		FrontAura.class,
		Ground.class,
		Holy.class,
		One.class,
		OwnerPet.class,
		Party.class,
		PartyClan.class,
		PartyMember.class,
		PartyNotMe.class,
		PartyOther.class,
		PartyTarget.class,
		Pet.class,
		Self.class,
		Siege.class,
		Summon.class,
		TargetParty.class,
		Unlockable.class,
	};
	
	private static final Class<?>[] TELNET_HANDLERS =
	{
		// Telnet Handlers
		ChatsHandler.class,
		DebugHandler.class,
		HelpHandler.class,
		PlayerHandler.class,
		ReloadHandler.class,
		ServerHandler.class,
		StatusHandler.class,
		ThreadHandler.class,
	};
	
	private static final Class<?>[] PLAYER_ACTION_HANDLERS_ =
	{
		// Action Handlers
		AirshipAction.class,
		BotReport.class,
		PetAttack.class,
		PetHold.class,
		PetMove.class,
		PetSkillUse.class,
		PetStop.class,
		PrivateStore.class,
		Ride.class,
		RunWalk.class,
		ServitorAttack.class,
		ServitorHold.class,
		ServitorMove.class,
		ServitorSkillUse.class,
		ServitorStop.class,
		SitStand.class,
		SocialAction.class,
		UnsummonPet.class,
		UnsummonServitor.class,
	};
	
	private static final Class<?>[] VOICED_COMMAND_HANDLERS =
	{
		// Voiced Command Handlers
		(AioItemsConfigs.ALLOW_AIO_ITEM_COMMAND && AioItemsConfigs.ENABLE_AIO_NPCS ? AioItemVCmd.class : null),
		(AntibotConfigs.ENABLE_ANTIBOT_SYSTEMS ? Antibot.class : null),
		(Config.BANKING_SYSTEM_ENABLED ? Banking.class : null),
		(CustomServerConfigs.ENABLE_CHARACTER_CONTROL_PANEL ? CcpVCmd.class : null),
		(Config.L2JMOD_ALLOW_CHANGE_PASSWORD ? ChangePassword.class : null),
		(Config.L2JMOD_CHAT_ADMIN ? ChatAdmin.class : null),
		(Config.L2JMOD_DEBUG_VOICE_COMMAND ? Debug.class : null),
		(CustomServerConfigs.EVENLY_DISTRIBUTED_ITEMS ? EvenlyDistributeItems.class : null),
		(Config.L2JMOD_HELLBOUND_STATUS ? Hellbound.class : null),
		(BufferConfigs.ENABLE_ITEM_BUFFER && PremiumServiceConfigs.USE_PREMIUM_SERVICE ? ItemBufferVCmd.class : null),
		(Config.L2JMOD_MULTILANG_ENABLE && Config.L2JMOD_MULTILANG_VOICED_ALLOW ? Lang.class : null),
		(CustomServerConfigs.ALLOW_ONLINE_COMMAND ? OnlineVCmd.class : null),
		(PremiumServiceConfigs.USE_PREMIUM_SERVICE ? PremiumVCmd.class : null),
		(ChaoticZoneConfigs.ENABLE_CHAOTIC_ZONE ? PvpZoneVCmd.class : null),
		(CustomServerConfigs.ALLOW_REPAIR_COMMAND ? RepairVCmd.class : null),
		(CustomServerConfigs.ALLOW_TELEPORTS_COMMAND ? TeleportsVCmd.class : null),
		// PingVCmd.class,
		(Config.L2JMOD_ALLOW_WEDDING ? Wedding.class : null),
		(GetRewardVoteSystemConfigs.ENABLE_VOTE_SYSTEM ? RewardVote.class : null),
	};
	
	private void loadHandlers(IHandler<?, ?> handler, Class<?>[] classes)
	{
		for (Class<?> c : classes)
		{
			if (c == null)
			{
				continue;
			}
			
			try
			{
				handler.registerByClass(c);
			}
			catch (Exception ex)
			{
				_log.error("Failed loading handler {}!", c.getSimpleName(), ex);
			}
		}
		
		_log.info("{}: Loaded {} handlers.", handler.getClass().getSimpleName(), handler.size());
	}
	
	public MasterHandler()
	{
		final long startCache = System.currentTimeMillis();
		loadHandlers(VoicedCommandHandler.getInstance(), VOICED_COMMAND_HANDLERS);
		loadHandlers(ActionHandler.getInstance(), ACTION_HANDLERS);
		loadHandlers(ActionShiftHandler.getInstance(), ACTION_SHIFT_HANDLERS);
		loadHandlers(SkillHandler.getInstance(), SKILL_HANDLERS);
		loadHandlers(PlayerActionHandler.getInstance(), PLAYER_ACTION_HANDLERS_);
		loadHandlers(AdminCommandHandler.getInstance(), ADMIN_HANDLERS);
		loadHandlers(BypassHandler.getInstance(), BYPASS_HANDLERS);
		loadHandlers(ChatHandler.getInstance(), CHAT_HANDLERS);
		loadHandlers(ItemHandler.getInstance(), ITEM_HANDLERS);
		loadHandlers(PunishmentHandler.getInstance(), PUNISHMENT_HANDLERS);
		loadHandlers(UserCommandHandler.getInstance(), USER_COMMAND_HANDLERS);
		loadHandlers(TargetHandler.getInstance(), TARGET_HANDLERS);
		loadHandlers(TelnetHandler.getInstance(), TELNET_HANDLERS);
		_log.info(MasterHandler.class.getSimpleName() + " loaded. (GenTime: {} ms) ", (System.currentTimeMillis() - startCache));
	}
	
	public static void main(String[] args)
	{
		new MasterHandler();
	}
}