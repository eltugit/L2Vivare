package l2r.gameserver.communitybbs.Managers;

import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.communitybbs.SunriseBoards.PartyMatchingBoard;
import l2r.gameserver.enums.PartyDistributionType;
import l2r.gameserver.enums.PcCondOverride;
import l2r.gameserver.model.BlockList;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.L2World;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.AskJoinParty;
import l2r.gameserver.network.serverpackets.ShowBoard;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * @author vGodFather
 */
public class PartyMatchingBBSManager extends BaseBBSManager
{
	public String _BBSCommand = "_maillist_0_1_0_";
	
	@Override
	public void cbByPass(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("_bbspartymatchinginvite"))
		{
			String targetName = command.substring(24);
			L2PcInstance receiver = L2World.getInstance().getPlayer(targetName);
			SystemMessage sm;
			
			if (receiver == null)
			{
				activeChar.sendPacket(SystemMessageId.FIRST_SELECT_USER_TO_INVITE_TO_PARTY);
			}
			else if ((receiver.getClient() == null) || receiver.getClient().isDetached())
			{
				activeChar.sendMessage("Player is in offline mode.");
			}
			else if (!activeChar.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS) && receiver.isInvisible())
			{
				activeChar.sendPacket(SystemMessageId.TARGET_IS_INCORRECT);
			}
			else if (receiver.isInParty())
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_IN_PARTY);
				sm.addString(receiver.getName());
				activeChar.sendPacket(sm);
			}
			else if (BlockList.isBlocked(receiver, activeChar))
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST);
				sm.addCharName(receiver);
				activeChar.sendPacket(sm);
			}
			else if (receiver == activeChar)
			{
				activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			}
			else if (receiver.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped())
			{
				receiver.sendPacket(SystemMessageId.INCORRECT_TARGET);
			}
			
			// else if (receiver.isInJail() || activeChar.isInJail())
			// {
			// activeChar.sendMessage("You cannot invite a player while is in Jail.");
			// }
			
			else if (receiver.isInOlympiadMode() || activeChar.isInOlympiadMode())
			{
				if ((receiver.isInOlympiadMode() != activeChar.isInOlympiadMode()) || (receiver.getOlympiadGameId() != activeChar.getOlympiadGameId()) || (receiver.getOlympiadSide() != activeChar.getOlympiadSide()))
				{
					activeChar.sendPacket(SystemMessageId.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS);
					return;
				}
			}
			else
			{
				sm = SystemMessage.getSystemMessage(SystemMessageId.C1_INVITED_TO_PARTY);
				sm.addCharName(receiver);
				activeChar.sendPacket(sm);
				
				if (!activeChar.isInParty())
				{
					createNewParty(receiver, activeChar);
				}
				else
				{
					if (activeChar.getParty().isInDimensionalRift())
					{
						activeChar.sendMessage("You cannot invite a player when you are in the Dimensional Rift.");
					}
					else
					{
						addTargetToParty(receiver, activeChar);
					}
				}
			}
			
			sendHtm(activeChar, "main", command);
		}
		else if (command.startsWith("_bbspartymatchinglist"))
		{
			String[] value = command.split(" ");
			String type = value[1];
			if (type.equals("on"))
			{
				if (activeChar.isInParty())
				{
					activeChar.sendMessage("You can't use this while you're in party!");
				}
				
				activeChar.setQuickVar("partyMatch", true);
				activeChar.sendMessage("You've entered the party matching list.");
			}
			else if (type.equals("off"))
			{
				if (activeChar.isInParty())
				{
					activeChar.sendMessage("You can't use this while you're in party!");
					return;
				}
				
				activeChar.deleteQuickVar("partyMatch");
				activeChar.sendMessage("You've left the party matching list.");
			}
			
			sendHtm(activeChar, "main", command);
		}
		else if (command.startsWith("_bbspartymatchingrefresh") || command.equals(_BBSCommand))
		{
			sendHtm(activeChar, "main", command);
		}
		else
		{
			ShowBoard sb = new ShowBoard("<html><body><br><br><center>The command: " + command + " is not implemented yet.</center><br><br></body></html>", "101");
			activeChar.sendPacket(sb);
			activeChar.sendPacket(new ShowBoard(null, "102"));
			activeChar.sendPacket(new ShowBoard(null, "103"));
		}
	}
	
	private void sendHtm(L2PcInstance activeChar, String string, String command)
	{
		String content = HtmCache.getInstance().getHtm(activeChar.getHtmlPrefix(), "data/html/CommunityBoard/partyMatching/" + string + ".htm");
		
		if (content == null)
		{
			content = "<html><body><br><br><center>404 :File not found: 'data/html/CommunityBoard/partyMatching/" + string + ".htm'</center></body></html>";
		}
		
		content = content.replace("%partyMatchingMembers%", PartyMatchingBoard.getInstance().getList());
		separateAndSend(content, activeChar);
	}
	
	private void addTargetToParty(L2PcInstance receiver, L2PcInstance requestor)
	{
		final L2Party party = requestor.getParty();
		
		if (!party.isLeader(requestor))
		{
			requestor.sendPacket(SystemMessageId.ONLY_LEADER_CAN_INVITE);
			return;
		}
		
		if (party.getMemberCount() >= 9)
		{
			requestor.sendPacket(SystemMessageId.PARTY_FULL);
			return;
		}
		
		if (party.getPendingInvitation() && !party.isInvitationRequestExpired())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (!receiver.isProcessingRequest())
		{
			requestor.onTransactionRequest(receiver);
			receiver.sendPacket(new AskJoinParty(requestor.getName(), party.getDistributionType()));
			party.setPendingInvitation(true);
			receiver.setQuickVar("partyMatch", false);
		}
		else
		{
			SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_BUSY_TRY_LATER);
			sm.addString(receiver.getName());
			requestor.sendPacket(sm);
		}
	}
	
	private void createNewParty(L2PcInstance receiver, L2PcInstance requestor)
	{
		if (!receiver.isProcessingRequest())
		{
			requestor.setParty(new L2Party(requestor, PartyDistributionType.RANDOM));
			
			requestor.onTransactionRequest(receiver);
			receiver.sendPacket(new AskJoinParty(requestor.getName(), PartyDistributionType.RANDOM));
			requestor.getParty().setPendingInvitation(true);
			receiver.setQuickVar("partyMatch", false);
		}
		else
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
		}
	}
	
	@Override
	public void parsewrite(String url, String ar1, String ar2, String ar3, String ar4, String ar5, L2PcInstance activeChar)
	{
		
	}
	
	public static PartyMatchingBBSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PartyMatchingBBSManager _instance = new PartyMatchingBBSManager();
	}
}