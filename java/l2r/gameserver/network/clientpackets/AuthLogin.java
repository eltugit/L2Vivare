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
package l2r.gameserver.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l2r.Config;
import l2r.L2DatabaseFactory;
import l2r.gameserver.LoginServerThread;
import l2r.gameserver.LoginServerThread.SessionKey;
import l2r.gameserver.network.L2GameClient;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;

import gr.sr.protection.Protection;

/**
 * This class ...
 * @version $Revision: 1.9.2.3.2.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class AuthLogin extends L2GameClientPacket
{
	private static final String _C__2B_AUTHLOGIN = "[C] 2B AuthLogin";
	
	// loginName + keys must match what the loginserver used.
	private String _loginName;
	/*
	 * private final long _key1; private final long _key2; private final long _key3; private final long _key4;
	 */
	private int _playKey1;
	private int _playKey2;
	private int _loginKey1;
	private int _loginKey2;
	private final byte[] _data = new byte[48];
	
	@Override
	protected void readImpl()
	{
		_loginName = readS().toLowerCase();
		_playKey2 = readD();
		_playKey1 = readD();
		_loginKey1 = readD();
		_loginKey2 = readD();
		
		readD(); // lang
		
		L2GameClient client = getClient();
		if (client == null)
		{
			return;
		}
		
		if (_buf.remaining() >= 32)
		{
			{
				int[] id4 = new int[]
				{
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC(),
					this.readC()
				};
				client.setHWID(getID(id4));
			}
		}
	}
	
	private static String getID(int[] id)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < id.length; i++)
		{
			sb.append(String.format("%02X%s", id[i], (i < (id.length - 1)) ? "" : ""));
		}
		
		return sb.toString();
	}
	
	@Override
	protected void runImpl()
	{
		if (Protection.isProtectionOn())
		{
			// TODO GABRIEL PROTECTION
			if (!Protection.doAuthLogin(getClient(), _data, _loginName))
			{
				return;
			}
		}
		
		if ((getClient().getHWID().length() > 3) && banned_hwid(getClient().getHWID()))
		{
			_log.info("[AuthLogin] - HWID: [" + getClient().getHWID() + "] --> Banned !");
			getClient().closeNow();
			return;
		}
		
		final L2GameClient client = getClient();
		if (_loginName.isEmpty() || !client.isProtocolOk())
		{
			client.close((L2GameServerPacket) null);
			return;
		}
		
		SessionKey key = new SessionKey(_loginKey1, _loginKey2, _playKey1, _playKey2);
		if (Config.DEBUG)
		{
			_log.info("user:" + _loginName);
			_log.info("key:" + key);
		}
		
		// avoid potential exploits
		if (client.getAccountName() == null)
		{
			// Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet
			if (LoginServerThread.getInstance().addGameServerLogin(_loginName, client))
			{
				client.setAccountName(_loginName);
				LoginServerThread.getInstance().addWaitingClientAndSendRequest(_loginName, client, key);
			}
			else
			{
				client.close((L2GameServerPacket) null);
			}
		}
	}
	
	public synchronized static boolean banned_hwid(String hwid)
	{
		boolean result = true;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT hwid FROM banned_hwid WHERE hwid=?");
			statement.setString(1, hwid);
			ResultSet rset = statement.executeQuery();
			result = rset.next();
			rset.close();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println("banned_hwid: " + e.getMessage());
		}
		return result;
	}
	
	@Override
	public String getType()
	{
		return _C__2B_AUTHLOGIN;
	}
}
