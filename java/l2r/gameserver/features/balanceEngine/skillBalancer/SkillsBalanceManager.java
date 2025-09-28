package l2r.gameserver.features.balanceEngine.skillBalancer;

import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.model.base.ClassId;
import l2r.gameserver.model.skills.L2Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class SkillsBalanceManager
{
	public static final Logger _log = LoggerFactory.getLogger(SkillsBalanceManager.class);
	
	private final Map<Integer, double[]> _olympiadBalances = new HashMap<>();
	private final Map<Integer, double[]> _balances = new HashMap<>();
	private final Map<Integer, Integer> _updates = new HashMap<>();
	private final Map<Integer, Integer> _olympiadUpdates = new HashMap<>();
	private final Map<Integer, Integer> _secondProffessions = new HashMap<>();
	private final Map<Boolean, HashMap<Integer, ArrayList<Integer>>> _dataForIngameBalancer;
	private final Map<Boolean, HashMap<Integer, String>> _usedSkillNames;
	private ScheduledFuture<?> _updateThread;
	
	public SkillsBalanceManager()
	{
		(_dataForIngameBalancer = new HashMap<>()).put(true, new HashMap<Integer, ArrayList<Integer>>());
		_dataForIngameBalancer.put(false, new HashMap<Integer, ArrayList<Integer>>());
		(_usedSkillNames = new HashMap<>()).put(true, new HashMap<Integer, String>());
		_usedSkillNames.put(false, new HashMap<Integer, String>());
		
		loadSecondProffessions();
		loadBalances();
	}
	
	public void loadBalances()
	{
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM skills_balance");
			final ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int key = rset.getInt("key");
				final int skillId = rset.getInt("skillId");
				final int targetClassId = rset.getInt("targetClassId");
				final boolean forOlympiad = rset.getInt("forOlympiad") == 1;
				final double[] values =
				{
					rset.getDouble("chance"),
					rset.getDouble("power"),
					skillId,
					targetClassId
				};
				if (forOlympiad)
				{
					_olympiadBalances.put(key, values);
				}
				else
				{
					_balances.put(key, values);
				}
				if (!_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
				{
					_dataForIngameBalancer.get(forOlympiad).put(skillId, new ArrayList<Integer>());
				}
				if (!_dataForIngameBalancer.get(forOlympiad).get(skillId).contains(key))
				{
					_dataForIngameBalancer.get(forOlympiad).get(skillId).add(key);
				}
				final L2Skill sk = SkillData.getInstance().getInfo(skillId, 1);
				if (sk != null)
				{
					_usedSkillNames.get(forOlympiad).put(skillId, sk.getName());
				}
			}
		}
		catch (Exception e)
		{
			_log.error("Failed loading skills balances.", e);
		}
		_log.info("Successfully loaded " + (_balances.size() + _olympiadBalances.size()) + " skills balances.");
		
		if (_updateThread == null)
		{
			_updateThread = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> updateBalances(), BalancerConfigs.SKILLS_BALANCER_UPDATE_DELAY, BalancerConfigs.SKILLS_BALANCER_UPDATE_DELAY);
		}
	}
	
	public void loadSecondProffessions()
	{
		for (final ClassId cId : ClassId.values())
		{
			if (cId.level() >= 3)
			{
				_secondProffessions.put(cId.getParent().getId(), cId.getId());
			}
		}
	}
	
	public int getClassId(final int cId)
	{
		if (!BalancerConfigs.SKILLS_BALANCER_AFFECTS_SECOND_PROFFESION)
		{
			return cId;
		}
		if (_secondProffessions.containsKey(cId))
		{
			return _secondProffessions.get(cId);
		}
		return cId;
	}
	
	public double getBalance(final int skillId, int classId, final int type, final boolean forOlympiad)
	{
		classId = getClassId(classId);
		if (!forOlympiad)
		{
			if (_balances.containsKey((skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536)))
			{
				return _balances.get((skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536))[type];
			}
		}
		else if (_olympiadBalances.containsKey((skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536)))
		{
			return _olympiadBalances.get((skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536))[type];
		}
		return 1.0;
	}
	
	public double[] getBalance(final int key, final boolean forOlympiad)
	{
		if (!forOlympiad)
		{
			if (_balances.containsKey(key))
			{
				return _balances.get(key);
			}
		}
		else if (_olympiadBalances.containsKey(key))
		{
			return _olympiadBalances.get(key);
		}
		return null;
	}
	
	public double getBalanceToAll(int classId, final int type, final boolean forOlympiad)
	{
		classId = getClassId(classId);
		if (!forOlympiad)
		{
			if (_balances.containsKey(classId * -1))
			{
				return _balances.get(classId * -1)[type];
			}
		}
		else if (_olympiadBalances.containsKey(classId * -1))
		{
			return _olympiadBalances.get(classId * -1)[type];
		}
		return 1.0;
	}
	
	public Map<Integer, double[]> getAllBalances(final boolean forOlympiad)
	{
		return forOlympiad ? _olympiadBalances : _balances;
	}
	
	public HashMap<Integer, ArrayList<Integer>> getAllBalancesForIngame(final boolean forOlympiad)
	{
		return _dataForIngameBalancer.get(forOlympiad);
	}
	
	public ArrayList<Integer> getBalanceForIngame(final int skillId, final boolean forOlympiad)
	{
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
		{
			return _dataForIngameBalancer.get(forOlympiad).get(skillId);
		}
		return null;
	}
	
	public void updateBalance(final int key, final int skillId, final int targetClassId, final int type, final double value, final boolean forOlympiad)
	{
		final Map<Integer, double[]> balances = forOlympiad ? _olympiadBalances : _balances;
		if (!balances.containsKey(key))
		{
			final double[] data =
			{
				1.0,
				1.0,
				(skillId < 0) ? (-skillId) : skillId,
				targetClassId
			};
			data[type] = value;
			balances.put(key, data);
			if (!_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
			{
				_dataForIngameBalancer.get(forOlympiad).put(skillId, new ArrayList<Integer>());
			}
			if (!_dataForIngameBalancer.get(forOlympiad).get(skillId).contains(key))
			{
				_dataForIngameBalancer.get(forOlympiad).get(skillId).add(key);
			}
			if (!_usedSkillNames.containsKey(skillId))
			{
				final L2Skill sk = SkillData.getInstance().getInfo(skillId, 1);
				if (sk != null)
				{
					_usedSkillNames.get(forOlympiad).put(skillId, sk.getName());
				}
			}
		}
		else
		{
			balances.get(key)[type] = value;
		}
		if (forOlympiad)
		{
			_olympiadUpdates.put(key, 0);
		}
		else
		{
			_updates.put(key, 0);
		}
	}
	
	public void updateBalance(final int key, final int skillId, final int targetClassId, final double[] values, final boolean forOlympiad)
	{
		final Map<Integer, double[]> balances = forOlympiad ? _olympiadBalances : _balances;
		balances.put(key, values);
		if (!_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
		{
			_dataForIngameBalancer.get(forOlympiad).put(skillId, new ArrayList<Integer>());
		}
		if (!_dataForIngameBalancer.get(forOlympiad).get(skillId).contains(key))
		{
			_dataForIngameBalancer.get(forOlympiad).get(skillId).add(key);
		}
		if (!_usedSkillNames.containsKey(skillId))
		{
			final L2Skill sk = SkillData.getInstance().getInfo(skillId, 1);
			if (sk != null)
			{
				_usedSkillNames.get(forOlympiad).put(skillId, sk.getName());
			}
		}
		if (forOlympiad)
		{
			_olympiadUpdates.put(key, 0);
		}
		else
		{
			_updates.put(key, 0);
		}
	}
	
	public void removeBalance(final int key, final int skillId, final int targetClassId, final boolean forOlympiad)
	{
		final int rSkillId = (skillId < 0) ? (-skillId) : skillId;
		if (!forOlympiad)
		{
			if (_balances.containsKey(key))
			{
				_balances.remove(key);
				_updates.put(key, 1);
			}
		}
		else if (_olympiadBalances.containsKey(key))
		{
			_olympiadBalances.remove(key);
			_olympiadUpdates.put(key, 1);
		}
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(rSkillId) && _dataForIngameBalancer.get(forOlympiad).get(rSkillId).contains(key))
		{
			final int i = _dataForIngameBalancer.get(forOlympiad).get(rSkillId).indexOf(key);
			_dataForIngameBalancer.get(forOlympiad).get(rSkillId).remove(i);
		}
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(rSkillId) && (_dataForIngameBalancer.get(forOlympiad).get(rSkillId).size() < 1))
		{
			_dataForIngameBalancer.get(forOlympiad).remove(rSkillId);
		}
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(rSkillId))
		{
			_usedSkillNames.remove(skillId);
		}
	}
	
	public HashMap<Integer, String> getSkillNames(final boolean forOlympiad)
	{
		return _usedSkillNames.get(forOlympiad);
	}
	
	public ArrayList<Integer> getSkillsByName(final boolean forOlympiad, String name, final int classId)
	{
		final ArrayList<Integer> skills = new ArrayList<>();
		name = name.toLowerCase();
		for (final Map.Entry<Integer, String> entry : _usedSkillNames.get(forOlympiad).entrySet())
		{
			if (entry.getValue().toLowerCase().contains(name))
			{
				skills.add(entry.getKey());
			}
		}
		final ArrayList<Integer> usedSkills = new ArrayList<>();
		for (final int skillId : skills)
		{
			if (classId >= 0)
			{
				final int key = (skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536);
				final ArrayList<Integer> keys = _dataForIngameBalancer.get(forOlympiad).get(skillId);
				if (!keys.contains(key))
				{
					continue;
				}
				usedSkills.add(key);
			}
			else
			{
				if (!_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
				{
					continue;
				}
				usedSkills.addAll(_dataForIngameBalancer.get(forOlympiad).get(skillId));
			}
		}
		return usedSkills;
	}
	
	public ArrayList<Integer> getUsedSkillsById(final boolean forOlympiad, final int skillId, final int classId)
	{
		if (!_dataForIngameBalancer.get(forOlympiad).containsKey(skillId))
		{
			return null;
		}
		if (classId == -1)
		{
			return _dataForIngameBalancer.get(forOlympiad).get(skillId);
		}
		final int key = (skillId * ((classId < 0) ? -1 : 1)) + (classId * 65536);
		if (_dataForIngameBalancer.get(forOlympiad).get(skillId).contains(key))
		{
			final ArrayList<Integer> r = new ArrayList<>();
			r.add(key);
			return r;
		}
		System.out.println("key nera " + key);
		return null;
	}
	
	public void updateBalances()
	{
		_log.info("Skills balances updating to database!");
		for (final Map.Entry<Integer, Integer> entry : _updates.entrySet())
		{
			if (entry.getValue() == 0)
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("REPLACE INTO skills_balance (skills_balance.key, forOlympiad, chance, power, skillId, targetClassId) values (?, ?, ?, ?, ?, ?)"))
				{
					final double[] data = _balances.get(entry.getKey());
					statement.setInt(1, entry.getKey());
					statement.setInt(2, 0);
					statement.setDouble(3, data[0]);
					statement.setDouble(4, data[1]);
					statement.setInt(5, (int) data[2]);
					statement.setInt(6, (int) data[3]);
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not update skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
			else
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("DELETE FROM skills_balance WHERE skills_balance.key=?"))
				{
					statement.setInt(1, entry.getKey());
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not delete skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
		}
		for (final Map.Entry<Integer, Integer> entry : _olympiadUpdates.entrySet())
		{
			if (entry.getValue() == 0)
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("REPLACE INTO skills_balance (skills_balance.key, forOlympiad, chance, power, skillId, targetClassId) values (?, ?, ?, ?, ?, ?)"))
				{
					final double[] data = _olympiadBalances.get(entry.getKey());
					statement.setInt(1, entry.getKey());
					statement.setInt(2, 1);
					statement.setDouble(3, data[0]);
					statement.setDouble(4, data[1]);
					statement.setInt(5, (int) data[2]);
					statement.setInt(6, (int) data[3]);
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not update skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
			else
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("DELETE FROM skills_balance WHERE skills_balance.key=?"))
				{
					statement.setInt(1, entry.getKey());
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not delete skill balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
		}
		_updates.clear();
		_olympiadUpdates.clear();
	}
	
	public static SkillsBalanceManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final SkillsBalanceManager _instance;
		
		static
		{
			_instance = new SkillsBalanceManager();
		}
	}
}
