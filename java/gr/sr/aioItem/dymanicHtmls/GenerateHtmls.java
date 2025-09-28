package gr.sr.aioItem.dymanicHtmls;

import gr.sr.dataHolder.PlayersTopData;
import gr.sr.main.TopListsLoader;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.enums.QuickVarType;
import l2r.gameserver.instancemanager.GrandBossManager;
import l2r.gameserver.model.ClanPrivilege;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.NpcHtmlMessage;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList;
import l2r.gameserver.network.serverpackets.SortedWareHouseWithdrawalList.WarehouseListType;
import l2r.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class GenerateHtmls
{
	private static final int[] BOSSES =
	{
		29001,
		29006,
		29014,
		29019,
		29020,
		29022,
		29028,
		29118
	};
	
	/**
	 * Method to send the html to char
	 * @param player
	 * @param html
	 */
	public static void sendPacket(L2PcInstance player, String html)
	{
		NpcHtmlMessage msg = new NpcHtmlMessage();
		msg.setFile(player, player.getHtmlPrefix(), "/data/html/sunrise/AioItemNpcs/" + html);
		player.sendPacket(msg);
	}
	
	/**
	 * Method to show grand boss info
	 * @param player
	 */
	public static final void showRbInfo(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		StringBuilder tb = new StringBuilder();
		tb.append("<html><title>Rb Info</title><body>");
		tb.append("<br><br>");
		tb.append("<font color=00FFFF>Grand Boss Info</font>");
		tb.append("<center>");
		tb.append("<img src=L2UI.SquareGray width=280 height=1>");
		tb.append("<br><br>");
		tb.append("<table width = 280>");
		for (int boss : BOSSES)
		{
			String name = NpcTable.getInstance().getTemplate(boss).getName();
			long delay = GrandBossManager.getInstance().getStatsSet(boss).getLong("respawn_time");
			if (delay <= System.currentTimeMillis())
			{
				tb.append("<tr>");
				tb.append("<td><font color=\"00C3FF\">" + name + "</color>:</td> " + "<td><font color=\"9CC300\">Is Alive</color></td>" + "<br1>");
				tb.append("</tr>");
			}
			else
			{
				int hours = (int) ((delay - System.currentTimeMillis()) / 1000 / 60 / 60);
				int mins = (int) (((delay - (hours * 60 * 60 * 1000)) - System.currentTimeMillis()) / 1000 / 60);
				int seconts = (int) (((delay - ((hours * 60 * 60 * 1000) + (mins * 60 * 1000))) - System.currentTimeMillis()) / 1000);
				tb.append("<tr>");
				tb.append("<td><font color=\"00C3FF\">" + name + "</color></td>" + "<td><font color=\"FFFFFF\">" + " " + "Respawn in :</color></td>" + " " + "<td><font color=\"32C332\">" + hours + " : " + mins + " : " + seconts + "</color></td><br1>");
				tb.append("</tr>");
			}
		}
		tb.append("</table>");
		tb.append("<br><br>");
		tb.append("<img src=L2UI.SquareWhite width=280 height=1>");
		tb.append("<td><button value=\"Back\" action=\"bypass -h Aioitem_Chat_service/services.htm\" width=90 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		tb.append("</center>");
		tb.append("</body></html>");
		html.setHtml(tb.toString());
		player.sendPacket(html);
	}
	



	public static final void showCWithdrawWindow(L2PcInstance player, WarehouseListType itemtype, byte sortorder)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		
		if (!player.hasClanPrivilege(ClanPrivilege.CL_VIEW_WAREHOUSE))
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_CLAN_WAREHOUSE);
			return;
		}
		
		player.setActiveWarehouse(player.getClan().getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
		
		player.setQuickVar(QuickVarType.PORTAL_WH.getCommand(), true);
		if (itemtype != null)
		{
			player.sendPacket(new SortedWareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN, itemtype, sortorder));
		}
		else
		{
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.CLAN));
		}
	}
	
	public static final void showPWithdrawWindow(L2PcInstance player, WarehouseListType itemtype, byte sortorder)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		player.setActiveWarehouse(player.getWarehouse());
		
		if (player.getActiveWarehouse().getSize() == 0)
		{
			player.sendPacket(SystemMessageId.NO_ITEM_DEPOSITED_IN_WH);
			return;
		}
		
		player.setQuickVar(QuickVarType.PORTAL_WH.getCommand(), true);
		if (itemtype != null)
		{
			player.sendPacket(new SortedWareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE, itemtype, sortorder));
		}
		else
		{
			player.sendPacket(new WareHouseWithdrawalList(player, WareHouseWithdrawalList.PRIVATE));
		}
	}
	
	/**
	 * Method to show top festival adena player
	 * @param player
	 */
	public static void showTopFa(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Top Fa</title><body><center><br>");
		sb.append("<table border=1 width = 280>");
		sb.append("<tr>");
		sb.append("<td><font color=FFD700>No</font></td><td><font color=FFD700>Character Name:</font></td><td><font color=FFD700>Fa Count:</font></td>");
		sb.append("</tr>");
		int count = 1;
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopCurrency())
		{
			String name = playerData.getCharName();
			long countFa = playerData.getCurrencyCount();
			
			sb.append("<tr>");
			sb.append("<td align=center>" + count + "</td><td>" + name + "</td><td align=center>" + countFa + "</td>");
			sb.append("</tr>");
			sb.append("<br>");
			count = count + 1;
		}
		sb.append("</table>");
		sb.append("<br><center>");
		sb.append("<br><img src=L2UI.SquareWhite width=280 height=1>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h Aioitem_Chat_service/toplists.htm\" width=90 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</center>");
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Method to show top PvP player
	 * @param player
	 */
	public static void showTopPvp(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Top PvP</title><body><center><br>");
		sb.append("<table border=1 width = 280>");
		sb.append("<tr>");
		sb.append("<td><font color=FFD700>No</font></td><td><font color=FFD700>Character Name:</font></td><td><font color=FFD700>Clan Name:</font></td><td><font color=FFD700>PvP Kills:</font></td>");
		sb.append("</tr>");
		int count = 1;
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopPvp())
		{
			String name = playerData.getCharName();
			String cName = playerData.getClanName();
			int pvp = playerData.getPvp();
			
			sb.append("<tr>");
			sb.append("<td align=center>" + count + "</td><td>" + name + "</td><td align=center>" + cName + "</td><td align=center>" + pvp + "</td>");
			sb.append("</tr>");
			sb.append("<br>");
			count = count + 1;
		}
		sb.append("</table>");
		sb.append("<br><center>");
		sb.append("<br><img src=L2UI.SquareWhite width=280 height=1>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h Aioitem_Chat_service/toplists.htm\" width=90 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</center>");
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Method to show top Pk player
	 * @param player
	 */
	public static void showTopPk(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Top Pk</title><body><center><br>");
		sb.append("<table border=1 width = 280>");
		sb.append("<tr>");
		sb.append("<td><font color=FFD700>No</font></td><td><font color=FFD700>Character Name:</font></td><td><font color=FFD700>Clan Name:</font></td><td><font color=FFD700>Pk Kills:</font></td>");
		sb.append("</tr>");
		int count = 1;
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopPk())
		{
			String name = playerData.getCharName();
			String cName = playerData.getClanName();
			int pk = playerData.getPk();
			
			sb.append("<tr>");
			sb.append("<td align=center>" + count + "</td><td>" + name + "</td><td align=center>" + cName + "</td><td align=center>" + pk + "</td>");
			sb.append("</tr>");
			sb.append("<br>");
			count = count + 1;
		}
		sb.append("</table>");
		sb.append("<br><center>");
		sb.append("<br><img src=L2UI.SquareWhite width=280 height=1>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h Aioitem_Chat_service/toplists.htm\" width=90 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</center>");
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	
	/**
	 * Method to show top clan
	 * @param player
	 */
	public static void showTopClan(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage();
		StringBuilder sb = new StringBuilder();
		sb.append("<html><title>Top Clan</title><body><center><br>");
		sb.append("<table border=1 width = 280>");
		sb.append("<tr>");
		sb.append("<td><font color=FFD700>No</font></td><td><font color=FFD700>Leader Name:</font></td><td><font color=FFD700>Clan Name:</font></td><td><font color=FFD700>Clan Level:</font></td>");
		sb.append("</tr>");
		int count = 1;
		for (PlayersTopData playerData : TopListsLoader.getInstance().getTopClan())
		{
			String name = playerData.getCharName();
			String cName = playerData.getClanName();
			int cLevel = playerData.getClanLevel();
			
			sb.append("<tr>");
			sb.append("<td align=center>" + count + "</td><td>" + name + "</td><td align=center>" + cName + "</td><td align=center>" + cLevel + "</td>");
			sb.append("</tr>");
			sb.append("<br>");
			count = count + 1;
		}
		sb.append("</table>");
		sb.append("<br><center>");
		sb.append("<br><img src=L2UI.SquareWhite width=280 height=1>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h Aioitem_Chat_service/toplists.htm\" width=90 height=21 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</center>");
		sb.append("</body></html>");
		html.setHtml(sb.toString());
		player.sendPacket(html);
	}
	

	
	private static String getTableColor(int i)
	{
		if ((i % 2) == 0)
		{
			return "<table width=280 border=0 bgcolor=\"444444\">";
		}
		return "<table width=280 border=0>";
	}
	

}