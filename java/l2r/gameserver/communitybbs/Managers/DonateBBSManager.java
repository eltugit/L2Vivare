/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package l2r.gameserver.communitybbs.Managers;

import gabriel.Utils.GabUtils;
import gabriel.config.GabConfig;
import gabriel.others.CharCustomHeroTable;
import gabriel.others.DonateSkills;
import gabriel.scriptsGab.forge.Forge;
import gabriel.scriptsGab.Scripts;
import gabriel.scriptsGab.utils.BBS;
import gr.sr.aioItem.runnable.TransformFinalizer;
import gr.sr.configsEngine.configs.impl.CommunityDonateConfigs;
import gr.sr.configsEngine.configs.impl.CustomServerConfigs;
import gr.sr.configsEngine.configs.impl.DonateManagerConfigs;
import gr.sr.main.Conditions;
import gr.sr.premiumEngine.PremiumDuration;
import gr.sr.premiumEngine.PremiumHandler;
import gr.sr.securityEngine.SecurityActions;
import gr.sr.securityEngine.SecurityType;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.BoardsManager;
import l2r.gameserver.data.sql.CharNameTable;
import l2r.gameserver.data.sql.ClanTable;
import l2r.gameserver.data.xml.impl.MultisellData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.data.xml.impl.TransformData;
import l2r.gameserver.enums.ZoneIdType;
import l2r.gameserver.model.L2Augmentation;
import l2r.gameserver.model.L2Clan;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.Inventory;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.*;
import l2r.gameserver.util.Util;

import java.io.File;
import java.util.*;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class DonateBBSManager extends BaseBBSManager
{
	public DonateBBSManager()
	{
		loadAugmentBaseStats();
		loadAugmentSecondaryStats();
	}
	
	public String _donationBBSCommand = CommunityDonateConfigs.BYPASS_COMMAND;
	
	private static String AUGMENT_BASE_STAT = "STR +1,24699;INT +1,24701;CON +1,24700;MEN +1,24702";
	private static String AUGMENT_SECONDARY_STAT = CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_SKILL;
	public static Map<String, Integer> AUGMENT_BASE_STATS = new LinkedHashMap<>();
	public static Map<String, Integer> AUGMENT_SECONDARY_STATS = new LinkedHashMap<>();
	
	@Override
	public void cbByPass(String command, L2PcInstance activeChar)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			return;
		}
		
		if (command.equals(_donationBBSCommand + ""))
		{
			sendHtm(activeChar, "main", command);
		}
		// navigation handler
		else if (command.startsWith(_donationBBSCommand + "_nav"))
		{
			sendHtm(activeChar, commandSeperator(command, ";"), command);
		}
		// multisell handler
		else if (command.startsWith(_donationBBSCommand + "_multisell"))
		{
			sendMultisell(activeChar, commandSeperator(command, ";"));
		}
		// change name
		else if (command.startsWith(_donationBBSCommand + ";donatename"))
		{
			changeCharName(activeChar, command);
		}
		// change clan name
		else if (command.startsWith(_donationBBSCommand + ";donateClanName"))
		{
			changeClanName(activeChar, command);
		}// change sex
		else if (command.startsWith(_donationBBSCommand + ";changeGenderDonate"))
		{
            changeGenderDonate(activeChar);
		}
		else if (command.startsWith(_donationBBSCommand + ";trycolors")) {
			String color = command.split(" ")[1];
			String[] colors = color.split("");

			activeChar.setQuickVar("tryColor", (colors[4]+colors[5]+colors[2]+colors[3]+colors[0]+colors[1]));
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.broadcastUserInfo();
			activeChar.startTryColorTask();
		}
		else if (command.startsWith(_donationBBSCommand + ";buycolors")) {
			String color = command.split(" ")[1];

			giveColor(activeChar, color, false);
		}
		else if (command.startsWith(_donationBBSCommand + ";buytcolors")) {
			String color = command.split(" ")[1];
			giveColor(activeChar, color, true);

		}
		else if (command.startsWith(_donationBBSCommand + ";colors")) {
			String HTML_REDEEM = "data/html/CommunityBoard/donate/color.htm";
			Map<Integer, String> tpls = Util.parseTemplate2(HtmCache.getInstance().getHtm(activeChar, HTML_REDEEM));
			String html = tpls.get(0);
			String template = tpls.get(1);

			StringBuilder sb = new StringBuilder();
			for (String s : GabConfig.COMMUNITY_DONATE_COLOR_PRICE.keySet()) {
				String tm = template;
				int price = GabConfig.COMMUNITY_DONATE_COLOR_PRICE.getOrDefault(s, -1);
				if(price < 0 || s.equals("reset"))
					continue;
				tm = tm.replace("%co%", s);
				tm = tm.replace("%price%", String.valueOf(price));
				sb.append(tm);
			}
			html = html.replace("%list%", sb.toString());
			html = html.replace("%coinName%", GabUtils.getItemName(GabConfig.COMMUNITY_DONATE_COLOR_ID));
			BBS.separateAndSend(html, activeChar);
//            changeCharName(activeChar, command);
		}
		// buy noble
		else if (command.startsWith(_donationBBSCommand + ";donateNoble"))
		{
			giveNoble(activeChar);
		}// buy Hero
		else if (command.startsWith(_donationBBSCommand + ";donateHero"))
		{
			giveHero(activeChar);
		}//buy heroVip
		else if (command.startsWith(_donationBBSCommand + ";donateClanVip"))
		{
            giveClanVip(activeChar);
		}
		else if (command.startsWith(_donationBBSCommand + ";donateFullSkill"))
		{
            giveFullSKill(activeChar);
		}
		// donate full clan
		else if (command.startsWith(_donationBBSCommand + ";donateFullClan"))
		{
			getFulClan(activeChar);
		}
		// get fame
		else if (command.startsWith(_donationBBSCommand + ";donateFame"))
		{
			getFame(activeChar);
		}
		// get recommends
		else if (command.startsWith(_donationBBSCommand + ";donateRec"))
		{
			getRec(activeChar);
		}
		// become premium
		else if (command.startsWith(_donationBBSCommand + ";donatePremium"))
		{
			givePremium(activeChar, commandSeperator(command, ";"));
		}
		// add augment to weapon
		else if (command.startsWith(_donationBBSCommand + ";donateAugment"))
		{
			giveAugment(activeChar, commandSeperator(command, ";"));
		}
		else if (command.startsWith(_donationBBSCommand + ";scripts_"))
		{
            Scripts.getInstance().parseCommand(commandSeperator(command, ";"), activeChar);
		}else if (command.startsWith(_donationBBSCommand + ";_bbsforge"))
		{
            Forge.getInstance().parseCommand(commandSeperator(command, ";"), activeChar);
		}
		else
		{
			separateAndSend("<html><body><br><br><center>Command : " + command + " needs core development</center><br><br></body></html>", activeChar);
		}
	}
	
	private void sendMultisell(L2PcInstance activeChar, String multisell)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_SHOP_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_SHOP_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		try
		{
			int multi = Integer.valueOf(multisell);
			
			if (CommunityDonateConfigs.MULTISELL_LIST.contains(multi))
			{
				sendHtm(activeChar, "main", _donationBBSCommand);
				MultisellData.getInstance().separateAndSend(multi, activeChar, null, false);
			}
			else
			{
				SecurityActions.startSecurity(activeChar, SecurityType.COMMUNITY_SYSTEM);
			}
		}
		catch (Exception e)
		{
			SecurityActions.startSecurity(activeChar, SecurityType.COMMUNITY_SYSTEM);
		}
		
	}
	
	private void giveAugment(L2PcInstance activeChar, String command)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
			return;
		}
		
		String temp_stat = "";
		String[] stats = command.split(" ");
		switch (stats.length)
		{
			case 4:
				temp_stat = stats[3];
				break;
			case 5:
				temp_stat = stats[3] + " " + stats[4];
				break;
			case 6:
				temp_stat = stats[3] + " " + stats[4] + " " + stats[5];
				break;
		}
		
		int baseStat = AUGMENT_BASE_STATS.get(stats[1] + " " + stats[2]);
		int secondarySkill = AUGMENT_SECONDARY_STATS.get(temp_stat) + 8358;
		
		L2ItemInstance parmorInstance = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		if ((parmorInstance == null) || !parmorInstance.isWeapon() || (parmorInstance.isHeroItem() && !CustomServerConfigs.ALT_ALLOW_REFINE_HERO_ITEM) || (parmorInstance.isCommonItem() || (parmorInstance.isEtcItem() || (parmorInstance.isTimeLimitedItem()))))
		{
			activeChar.sendPacket(SystemMessageId.THIS_IS_NOT_A_SUITABLE_ITEM);
			sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
			return;
		}
		
		if (parmorInstance.isAugmented())
		{
			activeChar.sendPacket(SystemMessageId.ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN);
			sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
			return;
		}
		
		if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_ID, CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_PRICE))
		{
			sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
			return;
		}
		
		// set augment skill
		activeChar.getInventory().unEquipItemInSlot(Inventory.PAPERDOLL_RHAND);
		parmorInstance.setAugmentation(new L2Augmentation(((secondarySkill << 16) + baseStat)));
		activeChar.getInventory().equipItem(parmorInstance);
		activeChar.sendPacket(SystemMessageId.THE_ITEM_WAS_SUCCESSFULLY_AUGMENTED);
		activeChar.sendPacket(new ExShowScreenMessage("You got " + stats[1] + " " + stats[2] + " and " + temp_stat + "", 5000));
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
		
		// send packets
		final InventoryUpdate iu = new InventoryUpdate();
		iu.addModifiedItem(parmorInstance);
		activeChar.sendInventoryUpdate(iu);
		activeChar.broadcastUserInfo();
		
		sendHtm(activeChar, "augment", _donationBBSCommand + "_nav;augment");
	}
	
	private void givePremium(L2PcInstance activeChar, String command)
	{
		int _premium_price = 0;
		String type = commandSeperator(command, " ");
		switch (type)
		{
			case "1":
				_premium_price = CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_1_MONTH;
				break;
			case "2":
				_premium_price = CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_2_MONTH;
				break;
			case "3":
				_premium_price = CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_3_MONTH;
				break;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "premium", _donationBBSCommand + "_nav;premium");
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_ALLOW && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "premium", _donationBBSCommand + "_nav;premium");
			return;
		}
		
		if (activeChar.isPremium())
		{
			activeChar.sendMessage("You are already premium user.");
			sendHtm(activeChar, "premium", _donationBBSCommand + "_nav;premium");
			return;
		}

		if (_premium_price > 0)
		{
			if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_ID, _premium_price)) {
				sendHtm(activeChar, "premium", _donationBBSCommand + "_nav;premium");
				return;
			}
		}

		if (type.equals("0"))
		{
			if (activeChar.getAccountVariables().getBoolean("FREE_VIP_USED", false))
			{
				return;
			}
			activeChar.getAccountVariables().set("FREE_VIP_USED", true);
			PremiumHandler.addPremiumServices(2, activeChar, PremiumDuration.DAYS);
		}
		else
		{
			PremiumHandler.addPremiumServices(Integer.parseInt(commandSeperator(command, " ")), activeChar);
		}

		activeChar.sendMessage("Cogratulations, you are a premium user.");
		activeChar.sendPacket(new ExShowScreenMessage("Cogratulations, you are a premium user.", 5000));
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
		
		sendHtm(activeChar, "premium", _donationBBSCommand + "_nav;premium");
	}
	
	private void getRec(L2PcInstance activeChar)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_REC_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_REC_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (activeChar.getRecomHave() < 255)
		{
			if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_REC_ID, CommunityDonateConfigs.COMMUNITY_DONATE_REC_PRICE))
			{
				sendHtm(activeChar, "main", _donationBBSCommand);
				return;
			}
			
			activeChar.setRecomHave(255);
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Your recommends have been increased to maximum.");
		}
		else
		{
			activeChar.sendMessage("You already have " + activeChar.getRecomHave() + " Recommends.");
		}
		sendHtm(activeChar, "main", _donationBBSCommand);
	}
	
	private void getFame(L2PcInstance activeChar)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_FAME_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_FAME_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (activeChar.getFame() < 100000)
		{
			if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_FAME_ID, CommunityDonateConfigs.COMMUNITY_DONATE_FAME_PRICE))
			{
				sendHtm(activeChar, "main", _donationBBSCommand);
				return;
			}
			
			activeChar.setFame(activeChar.getFame() + CommunityDonateConfigs.COMMUNITY_DONATE_FAME_AMOUNT);
			activeChar.sendUserInfo(true);
			activeChar.sendMessage("Your Fame has been increased to " + activeChar.getFame() + ".");
		}
		else
		{
			activeChar.sendMessage("You already have " + activeChar.getFame() + " Fame.");
		}
		sendHtm(activeChar, "main", _donationBBSCommand);
	}
	
	//@formatter:off
	private static List<Integer> clanSkillsLevel = Arrays.asList(372, 375, 378, 381, 389, 391, 374, 380, 382, 383, 384, 385, 386, 387, 388, 390, 371, 376, 377, 370, 373, 379 );
	private static List<Integer> clanSquadSkills = Arrays.asList( 611, 612, 613, 614, 615, 616 );
	//@formatter:on
	
	private void getFulClan(L2PcInstance activeChar)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		
		if (activeChar.getClan() == null)
		{
			activeChar.sendMessage("You don't have a clan.");
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		else if ((activeChar.getClan() == null) || (!activeChar.isClanLeader()))
		{
			activeChar.sendMessage("You are not a clan leader.");
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		else if (activeChar.getClan().getLevel() == 11)
		{
			activeChar.sendMessage("Your clan is already Level 11.");
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		
		if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_ID, CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_PRICE))
		{
//			sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
			return;
		}
		
		activeChar.getClan().changeLevel(11);
		activeChar.getClan().addReputationScore(CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_REP_AMOUNT, true);
		activeChar.getClan().addNewSkill(SkillData.getInstance().getInfo(391, 1));
		
		clanSkillsLevel.forEach(id -> activeChar.getClan().addNewSkill(SkillData.getInstance().getInfo(id, 3)));
		clanSquadSkills.forEach(id -> activeChar.getClan().addNewSkill(SkillData.getInstance().getInfo(id, 3), 0));
		
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
		activeChar.sendMessage("You purchased clan level 11 with full skills.");
		activeChar.sendPacket(new ExShowScreenMessage("Acquired clan level 11 full skills, Congratulations!", 5000));
		
//		sendHtm(activeChar, "clanServices", _donationBBSCommand + "_nav;clanServices");
	}
	
	private void changeCharName(L2PcInstance activeChar, String command)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_NAME_CHANGE_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_NAME_CHANGE_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		String val = "";
		
		try
		{
			val = commandSeperator(command, " ");
		}
		catch (Exception e) // case of empty character name
		{
			activeChar.sendMessage("New name box cannot be empty.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		if (!Util.isAlphaNumeric(val))
		{
			activeChar.sendMessage("Invalid character name.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		if (val.length() > 16)
		{
			activeChar.sendMessage("Max character length is 16.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		if (CharNameTable.getInstance().getIdByName(val) > 0)
		{
			activeChar.sendMessage("Warning, name " + val + " already exists.");
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_NAME_CHANGE_ID, CommunityDonateConfigs.COMMUNITY_DONATE_NAME_CHANGE_PRICE))
		{
			sendHtm(activeChar, "charServices", command);
			return;
		}
		
		activeChar.setName(val);
		activeChar.getAppearance().setVisibleName(val);
		activeChar.store();
		activeChar.broadcastUserInfo();
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
		activeChar.sendMessage("Your name has been changed to " + val + ".");
		activeChar.sendPacket(new ExShowScreenMessage("Your new character name: " + val, 5000));
		
		if (activeChar.isInParty())
		{
			// Delete party window for other party members
			activeChar.getParty().broadcastToPartyMembers(activeChar, new PartySmallWindowDeleteAll());
			for (L2PcInstance member : activeChar.getParty().getMembers())
			{
				// And re-add
				if (member != activeChar)
				{
					member.sendPacket(new PartySmallWindowAll(member, activeChar.getParty()));
				}
			}
		}
		if (activeChar.getClan() != null)
		{
			activeChar.getClan().broadcastClanStatus();
		}
		
		sendHtm(activeChar, "charServices", command);
	}

	private void changeGenderDonate(L2PcInstance player){
	    int itemIdToGet = DonateManagerConfigs.CHANGE_GENDER_DONATE_COIN;
        int price = DonateManagerConfigs.CHANGE_GENDER_DONATE_PRICE;

        if (!Conditions.checkPlayerItemCount(player, itemIdToGet, price))
        {
            return;
        }

        player.destroyItemByItemId("changeGender", itemIdToGet, price, player, true);
        player.getAppearance().setSex(player.getAppearance().getSex() ? false : true);
        player.sendMessage("Your gender has been changed.");
        player.broadcastUserInfo();
        // Transform-untransorm player quickly to force the client to reload the character textures
        TransformData.getInstance().transformPlayer(105, player);
        player.setSkillCast(ThreadPoolManager.getInstance().scheduleGeneral(new TransformFinalizer(player), 200));
    }

	private void changeClanName(L2PcInstance activeChar, String command)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_CLAN_NAME_CHANGE_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_CLAN_NAME_CHANGE_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if ((activeChar.getClan() == null) || (!activeChar.isClanLeader()))
		{
			activeChar.sendMessage("You do not own a clan.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		String val = "";
		
		try
		{
			val = commandSeperator(command, " ");
		}
		catch (Exception e)
		{
			// Case of empty character name
			activeChar.sendMessage("New name box cannot be empty.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if (!Util.isAlphaNumeric(val) && (val.length() > 16))
		{
			activeChar.sendPacket(SystemMessageId.CLAN_NAME_INCORRECT);
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if (val.length() > 16)
		{
			activeChar.sendMessage("Max character length is 16.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if (ClanTable.getInstance().getClanByName(val) != null)
		{
			activeChar.sendMessage("Warning, clan name " + val + " already exists.");
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_CLAN_NAME_CHANGE_ID, CommunityDonateConfigs.COMMUNITY_DONATE_CLAN_NAME_CHANGE_PRICE))
		{
			sendHtm(activeChar, "clanServices", command);
			return;
		}
		
		activeChar.getClan().setName(val);
		activeChar.getClan().updateClanNameInDB();
		activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
		activeChar.sendMessage("Your clan name has been changed to " + val + ".");
		activeChar.sendPacket(new ExShowScreenMessage("Your new clan name: " + val, 5000));
		activeChar.getClan().broadcastClanStatus();
		activeChar.sendPacket(new PledgeInfo(activeChar.getClan()));
		activeChar.broadcastUserInfo();
		
		if (activeChar.isInParty())
		{
			// Delete party window for other party members
			activeChar.getParty().broadcastToPartyMembers(activeChar, new PartySmallWindowDeleteAll());
			for (L2PcInstance member : activeChar.getParty().getMembers())
			{
				// And re-add
				if (member != activeChar)
				{
					member.sendPacket(new PartySmallWindowAll(member, activeChar.getParty()));
				}
			}
		}
		
		sendHtm(activeChar, "clanServices", command);
	}

	private void giveHero(L2PcInstance activeChar){
        if (!GabConfig.COMMUNITY_DONATE_HERO_ALLOW)
        {
            activeChar.sendMessage("This function is disabled by admin.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        if (!GabConfig.COMMUNITY_DONATE_HERO_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
        {
            activeChar.sendMessage("You cannot use this function outside peace zone.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        if (!activeChar.isHero())
        {
            if (!getPayment(activeChar, GabConfig.COMMUNITY_DONATE_HERO_ID, GabConfig.COMMUNITY_DONATE_HERO_PRICE))
            {
                sendHtm(activeChar, "main", _donationBBSCommand);
                return;
            }
            long time = 31;
            CharCustomHeroTable.getInstance().add(activeChar, 1, System.currentTimeMillis(), (time * 24 * 60 * 60 * 1000));
            activeChar.sendMessage(activeChar.getName() + " is a hero now for " + String.valueOf(time) + " days.");
            activeChar.broadcastPacket(new MagicSkillUse(activeChar, 5103, 1, 1000, 0));
            activeChar.broadcastUserInfo();
        }
        else
        {
            activeChar.sendMessage("You already have Hero Status.");
        }
        sendHtm(activeChar, "main", _donationBBSCommand);
    }

    private void giveFullSKill(L2PcInstance activeChar){
        if (!GabConfig.COMMUNITY_DONATE_FULLSKILL_ALLOW)
        {
            activeChar.sendMessage("This function is disabled by admin.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        if (!GabConfig.COMMUNITY_DONATE_FULLSKILL_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
        {
            activeChar.sendMessage("You cannot use this function outside peace zone.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }
        if (!getPayment(activeChar, GabConfig.COMMUNITY_DONATE_FULLSKILL_ID, GabConfig.COMMUNITY_DONATE_FULLSKILL_PRICE))
        {
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }
        DonateSkills.levelUpSkills(activeChar);
        sendHtm(activeChar, "main", _donationBBSCommand);
    }

	private void giveColor(L2PcInstance activeChar, String color, boolean title) {
		if (!GabConfig.COMMUNITY_DONATE_COLOR_ALLOW) {
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}

		if (!GabConfig.COMMUNITY_DONATE_COLOR_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE)) {
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}


		String var = title ? "cTitleColor"+color : "cNameColor"+color;
		boolean haveAlready = Boolean.parseBoolean(activeChar.getVar(var, "false"));


		int amount = GabConfig.COMMUNITY_DONATE_COLOR_PRICE.getOrDefault(color, -1);

		if (amount == -1) {
			return;
		}
		if(color.equals("reset")){
			if(title) {
				activeChar.unsetVar("cTitleColor");
			}else{
				activeChar.unsetVar("cNameColor");
			}
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.broadcastUserInfo();
			return;
		}

		if(!haveAlready) {
			if (!getPayment(activeChar, GabConfig.COMMUNITY_DONATE_COLOR_ID, amount)) {
				sendHtm(activeChar, "main", _donationBBSCommand);
				return;
			}
		}

		String[] colors = color.split("");
		if(title){
			activeChar.setVar("cTitleColor"+color, "true");
			activeChar.setVar("cTitleColor", (colors[4]+colors[5]+colors[2]+colors[3]+colors[0]+colors[1]));
		}else{
			activeChar.setVar("cNameColor"+color, "true");
			activeChar.setVar("cNameColor", (colors[4]+colors[5]+colors[2]+colors[3]+colors[0]+colors[1]));
		}

		activeChar.sendPacket(new UserInfo(activeChar));
		activeChar.broadcastUserInfo();

	}

    private void giveClanVip(L2PcInstance activeChar){
        if (!GabConfig.COMMUNITY_DONATE_CLANVIP_ALLOW)
        {
            activeChar.sendMessage("This function is disabled by admin.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        if (!GabConfig.COMMUNITY_DONATE_CLANVIP_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
        {
            activeChar.sendMessage("You cannot use this function outside peace zone.");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        L2Clan playerClan = activeChar.getClan();
        if(playerClan == null){
            activeChar.sendMessage("Player doesn't have a clan!");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }else if(playerClan.getLeaderId() != activeChar.getObjectId()){
            activeChar.sendMessage("Player isn't the clan leader");
            sendHtm(activeChar, "main", _donationBBSCommand);
            return;
        }

        if (!playerClan.isVip())
        {
            if (!getPayment(activeChar, GabConfig.COMMUNITY_DONATE_CLANVIP_ID, GabConfig.COMMUNITY_DONATE_CLANVIP_PRICE))
            {
                sendHtm(activeChar, "main", _donationBBSCommand);
                return;
            }
            int time = 31;
            doVip(activeChar, time, playerClan);
        }
        else
        {
            activeChar.sendMessage("Your clan already have Status.");
        }
        sendHtm(activeChar, "main", _donationBBSCommand);
    }

    private void doVip(L2PcInstance _player, int _time, L2Clan playerClan) {
        if (_player == null) {
            return;
        }
        if (_time > 0) {
            playerClan.setVip(true);
            playerClan.setEndTime("vip", _time);
            playerClan.updateClanVIPInDB();
            _player.sendMessage("Your clan have earned Vip Status! You can get enchanted buffs from the npc!");
        } else {
            removeVip(playerClan);
        }
    }

    private void removeVip(L2Clan playerClan){
        playerClan.setVip(false);
        playerClan.setEndTime("vip", 0);
        playerClan.updateClanVIPInDB();
    }

    private void giveNoble(L2PcInstance activeChar)
	{
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_ALLOW)
		{
			activeChar.sendMessage("This function is disabled by admin.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (!CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_NONPEACE && !activeChar.isInsideZone(ZoneIdType.PEACE))
		{
			activeChar.sendMessage("You cannot use this function outside peace zone.");
			sendHtm(activeChar, "main", _donationBBSCommand);
			return;
		}
		
		if (!activeChar.isNoble())
		{
			if (!getPayment(activeChar, CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_ID, CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_PRICE))
			{
				sendHtm(activeChar, "main", _donationBBSCommand);
				return;
			}
			
			activeChar.setNoble(true);
			activeChar.addItem("Tiara", 7694, 1, null, true);
			activeChar.setTarget(activeChar);
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
			activeChar.broadcastUserInfo();
			activeChar.sendPacket(new ExShowScreenMessage("You obtained Noblesse Status, Congratulations!", 5000));
			activeChar.sendMessage("You have obtained Noblesse Status.");
		}
		else
		{
			activeChar.sendMessage("You already have Noblesse Status.");
		}
//		sendHtm(activeChar, "main", _donationBBSCommand);
	}
	
	private String commandSeperator(String command, String sumbol)
	{
		StringTokenizer st = new StringTokenizer(command, sumbol);
		st.nextToken();
		String dat = st.nextToken();
		return dat;
	}
	
	private boolean getPayment(L2PcInstance activeChar, int id, int amount)
	{
		if (activeChar.destroyItemByItemId("DonateShop", id, amount, activeChar, true))
		{
			return true;
		}
		return false;
	}
	
	private void sendHtm(L2PcInstance activeChar, String file, String command)
	{
		BoardsManager.getInstance().addBypass(activeChar, "Donation Command", command);
		
		String content = "";
		String filepath = "data/html/CommunityBoard/donate/" + file + ".htm";
		
		File filecom = new File(filepath);
		
		if (!filecom.exists())
		{
			content = "<html><body><br><br><center>The command " + command + " points to file(" + filepath + ") that NOT exists.</center></body></html>";
			separateAndSend(content, activeChar);
			return;
		}
		
		content = HtmCache.getInstance().getHtm(activeChar, activeChar.getHtmlPrefix(), filepath);
		content = content.replace("%ccn_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_NAME_CHANGE_PRICE));
		content = content.replace("%ccc_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_CLAN_NAME_CHANGE_PRICE));
		content = content.replace("%nbl_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_PRICE));
		content = content.replace("%maxc_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_PRICE));
		content = content.replace("%vipc_pr%", String.valueOf(GabConfig.COMMUNITY_DONATE_CLANVIP_PRICE));
		content = content.replace("%hero_pr%", String.valueOf(GabConfig.COMMUNITY_DONATE_HERO_PRICE));
		content = content.replace("%fullSkill%", String.valueOf(GabConfig.COMMUNITY_DONATE_FULLSKILL_PRICE));
		content = content.replace("%fame_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_FAME_PRICE));
		content = content.replace("%rec_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_REC_PRICE));
		content = content.replace("%aug_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_AUGMENT_PRICE));
		content = content.replace("%prem_pr1%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_1_MONTH));
		content = content.replace("%prem_pr2%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_2_MONTH));
		content = content.replace("%prem_pr3%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_PREMIUM_PRICE_3_MONTH));
		content = content.replace("%nobbc_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_NOBLE_PRICE));
		content = content.replace("%recc_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_REC_PRICE));
		content = content.replace("%famec_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_FAME_PRICE));
		content = content.replace("%famec_pr%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_FAME_PRICE));
		content = content.replace("%fameA_pc%", String.valueOf(CommunityDonateConfigs.COMMUNITY_DONATE_FAME_AMOUNT));
		content = content.replace("%sex_pr%", String.valueOf(DonateManagerConfigs.CHANGE_GENDER_DONATE_PRICE));

		separateAndSend(content, activeChar);
	}
	
	public static void loadAugmentBaseStats()
	{
		for (String s : AUGMENT_BASE_STAT.split(";"))
		{
			String[] i = s.split(",");
			AUGMENT_BASE_STATS.put(i[0], Integer.parseInt(i[1]));
		}
	}
	
	public static void loadAugmentSecondaryStats()
	{
		for (String s : AUGMENT_SECONDARY_STAT.split(";"))
		{
			String[] i = s.split(",");
			AUGMENT_SECONDARY_STATS.put(i[0], Integer.parseInt(i[1]));
		}
	}
	
	@Override
	protected void separateAndSend(String html, L2PcInstance acha)
	{
		html = html.replace("\t", "");
		if (html.contains("%free_vip%"))
		{
			String text = "";
			if (!acha.getAccountVariables().getBoolean("FREE_VIP_USED", false))
			{
				text += "<table height=74 background=l2ui_ct1.Windows_DF_TooltipBG>";
				text += "<tr>";
				text += "<td>";
				text += "<table width=500 border=0 cellspacing=4 cellpadding=4 bgcolor=6E7B8B>";
				text += "<tr>";
				text += "<td FIXWIDTH=10 align=right valign=top bgcolor=8B0000>";
				text += "<img src=branchsys2.br_vitality_day_i00 width=32 height=32>";
				text += "</td>";
				text += "<td FIXWIDTH=230 align=left valign=top>";
				text += "<font color=B59A75 name=hs12>VIP Free: 48 Hours</font> &nbsp;<br><font color=B5B5B5 name=CreditTextSmall>2x drop Adena, Knight's Epaulette and +10% bonus in enchant.</font>";
				text += "</td>";
				text += "<td FIXWIDTH=155 align=right valign=top bgcolor=8B0000>";
				text += "Price: <font color=BCEE68>Free!</font>";
				text += "<button value=Buy action=\"bypass %command%;donatePremium 0\" back=l2ui_ct1.button.button_df_small_down fore=l2ui_ct1.button.button_df_small width=82 height=32/>";
				text += "</td>";
				text += "</tr>";
				text += "</table>";
				text += "</td>";
				text += "</tr>";
				text += "</table>";
			}
			html = html.replace("%free_vip%", text);
		}
		html = html.replace("%command%", _donationBBSCommand);
		if (html.length() < 8180)
		{
			acha.sendPacket(new ShowBoard(html, "101"));
			acha.sendPacket(new ShowBoard(null, "102"));
			acha.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < (8180 * 2))
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			acha.sendPacket(new ShowBoard(html.substring(8180, html.length()), "102"));
			acha.sendPacket(new ShowBoard(null, "103"));
		}
		else if (html.length() < (8180 * 3))
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 8180), "101"));
			acha.sendPacket(new ShowBoard(html.substring(8180, 8180 * 2), "102"));
			acha.sendPacket(new ShowBoard(html.substring(8180 * 2, html.length()), "103"));
		}
	}
	
	@Override
	public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
	
	}
	
	public static DonateBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DonateBBSManager _instance = new DonateBBSManager();
	}
}