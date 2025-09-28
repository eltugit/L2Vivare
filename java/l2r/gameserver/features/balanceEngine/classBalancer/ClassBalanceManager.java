package l2r.gameserver.features.balanceEngine.classBalancer;

import l2r.L2DatabaseFactory;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.features.balanceEngine.BalancerConfigs;
import l2r.gameserver.model.base.ClassId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class ClassBalanceManager
{
	public static final Logger _log = LoggerFactory.getLogger(ClassBalanceManager.class);
	
	private final Map<Integer, double[]> _balances = new HashMap<>();
	private final Map<Integer, double[]> _olympiadBalances = new HashMap<>();
	private final Map<Integer, Integer> _updates = new HashMap<>();
	private final Map<Integer, Integer> _olympiadUpdates = new HashMap<>();
	private final Map<Integer, Integer> _secondProffessions = new HashMap<>();
	private final Map<Boolean, HashMap<Integer, ArrayList<Integer>>> _dataForIngameBalancer;
	private ScheduledFuture<?> _updateThread;
	
	public ClassBalanceManager()
	{
		(_dataForIngameBalancer = new HashMap<>()).put(true, new HashMap<Integer, ArrayList<Integer>>());
		_dataForIngameBalancer.put(false, new HashMap<Integer, ArrayList<Integer>>());
		
		loadBalances();
		loadSecondProffessions();
	}
	
	public void loadBalances()
	{
		_balances.clear();
		_olympiadBalances.clear();
		_dataForIngameBalancer.get(true).clear();
		_dataForIngameBalancer.get(false).clear();
		_updates.clear();
		_olympiadUpdates.clear();
		try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
			final PreparedStatement statement = con.prepareStatement("SELECT * FROM class_balance");
			final ResultSet rset = statement.executeQuery())
		{
			while (rset.next())
			{
				final int key = rset.getInt("key");
				final int classId = rset.getInt("classId");
				final int targetClassId = rset.getInt("targetClassId");
				final boolean forOlympiad = rset.getInt("forOlympiad") == 1;
				final double[] values =
				{
					rset.getDouble("normal"),
					rset.getDouble("normalCrit"),
					rset.getDouble("magic"),
					rset.getDouble("magicCrit"),
					rset.getDouble("blow"),
					rset.getDouble("physSkill"),
					rset.getDouble("physSkillCrit"),
					classId,
					targetClassId
				};
				if (!forOlympiad)
				{
					_balances.put(key, values);
				}
				else
				{
					_olympiadBalances.put(key, values);
				}
				if (!_dataForIngameBalancer.get(forOlympiad).containsKey(classId))
				{
					_dataForIngameBalancer.get(forOlympiad).put(classId, new ArrayList<Integer>());
				}
				if (targetClassId >= 0)
				{
					if (!_dataForIngameBalancer.get(forOlympiad).containsKey(targetClassId))
					{
						_dataForIngameBalancer.get(forOlympiad).put(targetClassId, new ArrayList<Integer>());
					}
					if (!_dataForIngameBalancer.get(forOlympiad).get(targetClassId).contains(key))
					{
						_dataForIngameBalancer.get(forOlympiad).get(targetClassId).add(key);
					}
				}
				if (!_dataForIngameBalancer.get(forOlympiad).get(classId).contains(key))
				{
					_dataForIngameBalancer.get(forOlympiad).get(classId).add(key);
				}
			}
		}
		catch (Exception e)
		{
			_log.error("Failed loading class balances.", e);
		}
		_log.info("Successfully loaded " + (_balances.size() + _olympiadBalances.size()) + " balances.");
		
		if (_updateThread == null)
		{
			_updateThread = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(() -> updateBalances(), BalancerConfigs.CLASS_BALANCER_UPDATE_DELAY, BalancerConfigs.CLASS_BALANCER_UPDATE_DELAY);
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
		if (!BalancerConfigs.CLASS_BALANCER_AFFECTS_SECOND_PROFFESION)
		{
			return cId;
		}
		if (_secondProffessions.containsKey(cId))
		{
			return _secondProffessions.get(cId);
		}
		return cId;
	}
	
	public double getBalance(int classId, int targetClassId, final int type, final boolean forOlympiad)
	{
		classId = getClassId(classId);
		targetClassId = getClassId(targetClassId);
		if (!forOlympiad)
		{
			if (_balances.containsKey((classId * 256) + targetClassId))
			{
				return _balances.get((classId * 256) + targetClassId)[type];
			}
		}
		else if (_olympiadBalances.containsKey((classId * 256) + targetClassId))
		{
			return _olympiadBalances.get((classId * 256) + targetClassId)[type];
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
			if (_balances.containsKey(classId * -256))
			{
				return _balances.get(classId * -256)[type];
			}
		}
		else if (_olympiadBalances.containsKey(classId * -256))
		{
			return _olympiadBalances.get(classId * -256)[type];
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
	
	public ArrayList<Integer> getBalanceForIngame(int classId, final boolean forOlympiad)
	{
		classId = getClassId(classId);
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(classId))
		{
			return _dataForIngameBalancer.get(forOlympiad).get(classId);
		}
		return null;
	}
	
	public void updateBalance(final int key, final int classId, final int targetClassId, final int type, final double value, final boolean forOlympiad)
	{
		final Map<Integer, double[]> balances = forOlympiad ? _olympiadBalances : _balances;
		if (!balances.containsKey(key))
		{
			final double[] data =
			{
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				1.0,
				(classId < 0) ? (-classId) : classId,
				targetClassId
			};
			data[type] = value;
			balances.put(key, data);
			if (!_dataForIngameBalancer.get(forOlympiad).containsKey(classId))
			{
				_dataForIngameBalancer.get(forOlympiad).put(classId, new ArrayList<Integer>());
			}
			if (targetClassId >= 0)
			{
				if (!_dataForIngameBalancer.get(forOlympiad).containsKey(targetClassId))
				{
					_dataForIngameBalancer.get(forOlympiad).put(targetClassId, new ArrayList<Integer>());
				}
				if (!_dataForIngameBalancer.get(forOlympiad).get(targetClassId).contains(key))
				{
					_dataForIngameBalancer.get(forOlympiad).get(targetClassId).add(key);
				}
			}
			if (!_dataForIngameBalancer.get(forOlympiad).get(classId).contains(key))
			{
				_dataForIngameBalancer.get(forOlympiad).get(classId).add(key);
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
	
	public void updateBalance(final int key, final int classId, final int targetClassId, final double[] values, final boolean forOlympiad)
	{
		final Map<Integer, double[]> balances = forOlympiad ? _olympiadBalances : _balances;
		balances.put(key, values);
		if (!_dataForIngameBalancer.get(forOlympiad).containsKey(classId))
		{
			_dataForIngameBalancer.get(forOlympiad).put(classId, new ArrayList<Integer>());
		}
		if (targetClassId >= 0)
		{
			if (!_dataForIngameBalancer.get(forOlympiad).containsKey(targetClassId))
			{
				_dataForIngameBalancer.get(forOlympiad).put(targetClassId, new ArrayList<Integer>());
			}
			if (!_dataForIngameBalancer.get(forOlympiad).get(targetClassId).contains(key))
			{
				_dataForIngameBalancer.get(forOlympiad).get(targetClassId).add(key);
			}
		}
		if (!_dataForIngameBalancer.get(forOlympiad).get(classId).contains(key))
		{
			_dataForIngameBalancer.get(forOlympiad).get(classId).add(key);
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
	
	public void removeBalance(final int key, final int classId, final int targetClassId, final boolean forOlympiad)
	{
		final int rClassId = (classId < 0) ? (-classId) : classId;
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
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(rClassId) && _dataForIngameBalancer.get(forOlympiad).get(rClassId).contains(key))
		{
			final int i = _dataForIngameBalancer.get(forOlympiad).get(rClassId).indexOf(key);
			_dataForIngameBalancer.get(forOlympiad).get(rClassId).remove(i);
		}
		if (_dataForIngameBalancer.get(forOlympiad).containsKey(targetClassId) && _dataForIngameBalancer.get(forOlympiad).get(targetClassId).contains(key))
		{
			final int i = _dataForIngameBalancer.get(forOlympiad).get(targetClassId).indexOf(key);
			_dataForIngameBalancer.get(forOlympiad).get(targetClassId).remove(i);
		}
	}
	
	public void updateBalances()
	{
		_log.info("Class balances updating to database!");
		for (final Map.Entry<Integer, Integer> entry : _updates.entrySet())
		{
			if (entry.getValue() == 0)
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("REPLACE INTO class_balance (class_balance.key, forOlympiad, normal, normalCrit, magic, magicCrit, blow, physSkill, physSkillCrit, classId, targetClassId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))
				{
					final double[] data = _balances.get(entry.getKey());
					statement.setInt(1, entry.getKey());
					statement.setInt(2, 0);
					statement.setDouble(3, data[0]);
					statement.setDouble(4, data[1]);
					statement.setDouble(5, data[2]);
					statement.setDouble(6, data[3]);
					statement.setDouble(7, data[4]);
					statement.setDouble(8, data[5]);
					statement.setDouble(9, data[6]);
					statement.setInt(10, (int) data[7]);
					statement.setInt(11, (int) data[8]);
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not update class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
			else
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("DELETE FROM class_balance WHERE class_balance.key=?"))
				{
					statement.setInt(1, entry.getKey());
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not delete class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
		}
		for (final Map.Entry<Integer, Integer> entry : _olympiadUpdates.entrySet())
		{
			if (entry.getValue() == 0)
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("REPLACE INTO class_balance (class_balance.key, forOlympiad, normal, normalCrit, magic, magicCrit, blow, physSkill, physSkillCrit, classId, targetClassId) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"))
				{
					final double[] data = _olympiadBalances.get(entry.getKey());
					statement.setInt(1, entry.getKey());
					statement.setInt(2, 1);
					statement.setDouble(3, data[0]);
					statement.setDouble(4, data[1]);
					statement.setDouble(5, data[2]);
					statement.setDouble(6, data[3]);
					statement.setDouble(7, data[4]);
					statement.setDouble(8, data[5]);
					statement.setDouble(9, data[6]);
					statement.setInt(10, (int) data[7]);
					statement.setInt(11, (int) data[8]);
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not update class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
			else
			{
				try (final Connection con = L2DatabaseFactory.getInstance().getConnection();
					final PreparedStatement statement = con.prepareStatement("DELETE FROM class_balance WHERE class_balance.key=?"))
				{
					statement.setInt(1, entry.getKey());
					statement.executeUpdate();
				}
				catch (Exception e)
				{
					_log.error("Could not delete class balances[" + entry.getKey() + "]: " + e.getMessage(), e);
				}
			}
		}
		_updates.clear();
		_olympiadUpdates.clear();
	}
	
	public static ClassBalanceManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ClassBalanceManager _instance;
		
		static
		{
			_instance = new ClassBalanceManager();
		}
	}
}