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
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package scripts.handlers.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l2r.L2DatabaseFactory;

/**
 * @author rapfersan92
 */
public class ChatBanManager
{
	private static final Logger _log = Logger.getLogger(ChatBanManager.class.getName());
	
	protected final Map<String, Long> _login;
	protected final Map<String, Long> _hwid;
	
	public static ChatBanManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected ChatBanManager()
	{
		_login = new ConcurrentHashMap<>();
		_hwid = new ConcurrentHashMap<>();
		load();
	}
	
	public void reload()
	{
		_login.clear();
		_hwid.clear();
		load();
	}
	
	public void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT login, duration FROM ban_chat_login ORDER BY login");
			ResultSet rs = statement.executeQuery();
			while (rs.next())
			{
				_login.put(rs.getString("login"), rs.getLong("duration"));
			}
			rs.close();
			statement.close();
			
			statement = con.prepareStatement("SELECT hwid, duration FROM ban_chat_hwid ORDER BY hwid");
			rs = statement.executeQuery();
			while (rs.next())
			{
				_hwid.put(rs.getString("hwid"), rs.getLong("duration"));
			}
			rs.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.warning("Exception: " + e.getMessage());
		}
		
		_log.info("ChatBanManager: Loaded " + _login.size() + " banned logins.");
		_log.info("ChatBanManager: Loaded " + _hwid.size() + " banned hwids.");
	}
	
	public void addChatBan(String login, String hwid, long duration)
	{
		if (checkLogin(login))
		{
			updateChatBan(login, hwid, duration, true);
		}
		else
		{
			_login.put(login, duration);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("INSERT INTO ban_chat_login (login, duration) VALUES (?,?)");
				statement.setString(1, login);
				statement.setLong(2, duration);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
		
		if (checkHwid(hwid))
		{
			updateChatBan(login, hwid, duration, false);
		}
		else
		{
			_hwid.put(hwid, duration);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("INSERT INTO ban_chat_hwid (hwid, duration) VALUES (?,?)");
				statement.setString(1, hwid);
				statement.setLong(2, duration);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
	}
	
	public void updateChatBan(String login, String hwid, long duration, boolean savelogin)
	{
		if (checkLogin(login) && savelogin)
		{
			_login.put(login, duration);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE ban_chat_login SET duration = ? WHERE login = ?");
				statement.setLong(1, duration);
				statement.setString(2, login);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
		
		if (checkHwid(hwid))
		{
			_hwid.put(hwid, duration);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("UPDATE ban_chat_hwid SET duration = ? WHERE hwid = ?");
				statement.setLong(1, duration);
				statement.setString(2, hwid);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
	}
	
	public void removeLogin(String login)
	{
		if (checkLogin(login))
		{
			_login.remove(login);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("DELETE FROM ban_chat_login WHERE login = ?");
				statement.setString(1, login);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
	}
	
	public void removeHwid(String hwid)
	{
		if (checkHwid(hwid))
		{
			_hwid.remove(hwid);
			
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("DELETE FROM ban_chat_hwid WHERE hwid = ?");
				statement.setString(1, hwid);
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				_log.warning("Exception: " + e.getMessage());
			}
		}
	}
	
	public boolean checkBannedTime(String login, String hwid)
	{
		return (_login.containsKey(login) && ((_login.get(login) - 1500) > System.currentTimeMillis())) || (_hwid.containsKey(hwid) && ((_hwid.get(hwid) - 1500) > System.currentTimeMillis()));
	}
	
	public long getTimeInfo(String login, String hwid)
	{
		if (checkLogin(login))
		{
			return _login.get(login);
		}
		
		if (checkHwid(hwid))
		{
			return _hwid.get(hwid);
		}
		
		return 0;
	}
	
	public boolean checkLogin(String login)
	{
		return _login.containsKey(login);
	}
	
	public long getLoginDuration(String login)
	{
		return _login.get(login);
	}
	
	public boolean checkHwid(String hwid)
	{
		return _hwid.containsKey(hwid);
	}
	
	public long getHwidDuration(String hwid)
	{
		return _hwid.get(hwid);
	}
	
	public static String banTime(long time)
	{
		long milliToEnd = (time - System.currentTimeMillis()) / 1000L;
		double countDown = (milliToEnd - (milliToEnd % 60L)) / 60L;
		int numMins = (int) Math.floor(countDown % 60D);
		countDown = (countDown - numMins) / 60D;
		int numHours = (int) Math.floor(countDown % 24D);
		int numDays = (int) Math.floor((countDown - numHours) / 24D);
		
		int hours = (int) ((time - System.currentTimeMillis()) / 1000 / 60 / 60);
		int mins = (int) (((time - (hours * 60 * 60 * 1000)) - System.currentTimeMillis()) / 1000 / 60);
		int seconts = (int) (((time - ((hours * 60 * 60 * 1000) + (mins * 60 * 1000))) - System.currentTimeMillis()) / 1000);
		
		if (numDays > 0)
		{
			return numDays + " d. " + numHours + " hr. " + numMins + " min. " + seconts + " sec.";
		}
		
		if (numHours > 0)
		{
			return numHours + " hr. " + numMins + " min. " + seconts + " sec.";
		}
		
		if (numMins > 0)
		{
			return numMins + " min. " + seconts + " sec.";
		}
		if (numDays > 180)
		{
			return "Undetermined time!";
		}
		
		return seconts + " sec.";
	}
	
	private static class SingletonHolder
	{
		protected static final ChatBanManager _instance = new ChatBanManager();
	}
}