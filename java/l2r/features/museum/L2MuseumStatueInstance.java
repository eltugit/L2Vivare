package l2r.features.museum;

import l2r.L2DatabaseFactory;
import l2r.gameserver.communitybbs.Managers.MuseumBBSManager;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.CharSelectInfoPackage;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.ActionFailed;
import l2r.gameserver.network.serverpackets.CharSelectionInfo;
import l2r.gameserver.network.serverpackets.ShowBoard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class L2MuseumStatueInstance extends L2Npc
{
	public static final Logger _log = LoggerFactory.getLogger(L2MuseumStatueInstance.class);
	int _type;
	int _playerObjectId;
	CharSelectInfoPackage _charLooks;
	MuseumCategory _category;
	
	public L2MuseumStatueInstance(final L2NpcTemplate template, final int playerObjectId, final int type)
	{
		super(template);
		setInstanceType(InstanceType.L2MuseumStatueInstance);
		_playerObjectId = playerObjectId;
		_type = type;
		restoreCharLooks();
		_category = MuseumManager.getInstance().getAllCategories().get(type);
		setTitle(_category.getTypeName());
	}
	
	public void restoreCharLooks()
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM characters WHERE charId=?"))
		{
			statement.setInt(1, _playerObjectId);
			try (final ResultSet rset = statement.executeQuery())
			{
				if (rset.next())
				{
					_charLooks = CharSelectionInfo.restoreChar(rset);
					if (_charLooks == null)
					{
						System.out.println("Player with id[" + _playerObjectId + "] not found.");
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.warn("Could not restore char info: " + e.getMessage(), e);
		}
	}
	
	public CharSelectInfoPackage getCharLooks()
	{
		return _charLooks;
	}
	
	@Override
	public void onBypassFeedback(final L2PcInstance player, final String command)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
		super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(final L2PcInstance player)
	{
		String html = "<html><body scroll=no>";
		html += showStatue();
		html += "</body></html>";
		separateAndSend(html, player);
	}
	
	public String showStatue()
	{
		String html = "";
		html += "<br><br><br><center><table><tr><td width=25></td><td><table border=1 bgcolor=3b3c34><tr><td>";
		html = html + "<br><center><font name=\"ScreenMessageLarge\" color=b7b8b2>" + _category.getTypeName() + "</font></center>";
		html += "<table><tr>";
		if (!_category.getRefreshTime().equals(RefreshTime.Total))
		{
			html += "<td align=center width=260>";
			html = html + "<button value=\"" + _category.getRefreshTime().name() + " Rankings\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"257\" height=\"24\"/>";
			html += "</td>";
		}
		html = html + "<td align=center width=" + (_category.getRefreshTime().equals(RefreshTime.Total) ? 520 : 260) + ">";
		html += "<button value=\"Total Rankings\" action=\"\" fore=\"L2UI_CT1.Button_DF_Calculator\" back=\"L2UI_CT1.Button_DF_Calculator_Down\" width=\"257\" height=\"24\"/>";
		html += "</td>";
		html += "</tr><tr>";
		if (!_category.getRefreshTime().equals(RefreshTime.Total))
		{
			html += "<td align=center width=260>";
			for (int i = 0; i < 5; ++i)
			{
				String name = "No information.";
				String value = "No information.";
				int cellSpacing = -1;
				if (_category.getAllStatuePlayers().size() > i)
				{
					final TopPlayer player = _category.getAllStatuePlayers().get(i + 1);
					if (player != null)
					{
						name = player.getName();
						final long count = player.getCount();
						value = MuseumBBSManager.getInstance().convertToValue(count, _category.isTimer(), _category.getAdditionalText());
						cellSpacing = ((count > 999L) ? -3 : -2);
					}
				}
				final String bgColor = (i == 0) ? "746833" : (((i % 2) == 1) ? "171612" : "23221e");
				final String numberColor = (i == 0) ? "ffca37" : "dededf";
				final String nameColor = (i == 0) ? "eac842" : "e2e2e0";
				final String valueColor = (i == 0) ? "eee79f" : "a78d6c";
				html = html + "<table width=250 bgcolor=" + bgColor + " height=42><tr>";
				html = html + "<td width=50 align=center><font color=" + numberColor + " name=ScreenMessageLarge />" + ((i < 1) ? ("{" + (i + 1) + "}") : (i + 1)) + "</font></td>";
				html += "<td width=200 align=left>";
				html = html + "<table cellspacing=" + cellSpacing + "><tr><td width=200><font color=" + nameColor + " name=ScreenMessageSmall>" + name + "</font></td></tr><tr><td width=200><font color=" + valueColor + " name=ScreenMessageSmall>" + value + "</font></td></tr></table>";
				html += "<img src=\"L2UI.SquareBlank\" width=1 height=5/></td>";
				html += "";
				html += "</tr></table><img src=\"L2UI.SquareGray\" width=250 height=1/>";
			}
			html += "</td>";
		}
		html = html + "<td align=center width=" + (_category.getRefreshTime().equals(RefreshTime.Total) ? 520 : 260) + ">";
		for (int i = 0; i < 5; ++i)
		{
			String name = "No information.";
			String value = "No information.";
			int cellSpacing = -1;
			if (_category.getAllTotalTops().size() > i)
			{
				final TopPlayer player = _category.getAllTotalTops().get(i + 1);
				if (player != null)
				{
					name = player.getName();
					final long count = player.getCount();
					value = MuseumBBSManager.getInstance().convertToValue(count, _category.isTimer(), _category.getAdditionalText());
					cellSpacing = ((count > 999L) ? -3 : -2);
				}
			}
			final String bgColor = (i == 0) ? "746833" : (((i % 2) == 1) ? "171612" : "23221e");
			final String numberColor = (i == 0) ? "ffca37" : "dededf";
			final String nameColor = (i == 0) ? "eac842" : "e2e2e0";
			final String valueColor = (i == 0) ? "eee79f" : "a78d6c";
			html = html + "<table width=250 bgcolor=" + bgColor + " height=42><tr>";
			html = html + "<td width=50 align=center><font color=" + numberColor + " name=ScreenMessageLarge />" + ((i < 1) ? ("{" + (i + 1) + "}") : (i + 1)) + "</font></td>";
			html += "<td width=200 align=left>";
			html = html + "<table cellspacing=" + cellSpacing + "><tr><td width=200><font color=" + nameColor + " name=ScreenMessageSmall>" + name + "</font></td></tr><tr><td width=200><font color=" + valueColor + " name=ScreenMessageSmall>" + value + "</font></td></tr></table>";
			html += "<img src=\"L2UI.SquareBlank\" width=1 height=5/></td>";
			html += "";
			html += "</tr></table><img src=\"L2UI.SquareGray\" width=250 height=1/>";
		}
		html += "</td>";
		html += "</tr></table><br><br></td></tr></table></td></tr></table>";
		html += "</center>";
		return html;
	}
	
	protected void separateAndSend(final String html, final L2PcInstance acha)
	{
		if (html == null)
		{
			return;
		}
		if (html.length() < 4096)
		{
			acha.sendPacket(new ShowBoard(html, "101"));
			acha.sendPacket(new ShowBoard((String) null, "102"));
			acha.sendPacket(new ShowBoard((String) null, "103"));
		}
		else if (html.length() < 8192)
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
			acha.sendPacket(new ShowBoard(html.substring(4096), "102"));
			acha.sendPacket(new ShowBoard((String) null, "103"));
		}
		else if (html.length() < 16384)
		{
			acha.sendPacket(new ShowBoard(html.substring(0, 4096), "101"));
			acha.sendPacket(new ShowBoard(html.substring(4096, 8192), "102"));
			acha.sendPacket(new ShowBoard(html.substring(8192), "103"));
		}
	}
}
