package l2r.features.museum;

import l2r.gameserver.model.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuseumCategory
{
	int _categoryId;
	int _typeId;
	String _categoryName;
	String _typeName;
	String _type;
	String _additionalText;
	RefreshTime _refreshTime;
	boolean _timer;
	HashMap<Integer, TopPlayer> _players;
	HashMap<Integer, TopPlayer> _totalTopPlayers;
	HashMap<Integer, TopPlayer> _statuePlayers;
	ArrayList<L2MuseumStatueInstance> _spawnedStatues;
	ArrayList<Location> _statueSpawns;
	Map<Integer, List<MuseumReward>> _rewards;
//	ArrayList<MuseumReward> _rewards;
	
	public MuseumCategory( int categoryId,  int typeId,  String categoryName,  String typeName,  String type,  String refreshTime,  boolean timer,  String additionalText,  ArrayList<Location> statueSpawns,  Map<Integer, List<MuseumReward>> rewards)
	{
		_players = new HashMap<>();
		_totalTopPlayers = new HashMap<>();
		_statuePlayers = new HashMap<>();
		_spawnedStatues = new ArrayList<>();
		_categoryId = categoryId;
		_typeId = typeId;
		_categoryName = categoryName;
		_typeName = typeName;
		_type = type;
		_timer = timer;
		_additionalText = additionalText;
		_statueSpawns = statueSpawns;
		_rewards = rewards;
		for (final RefreshTime time : RefreshTime.values())
		{
			if (time.name().toLowerCase().equals(refreshTime))
			{
				_refreshTime = time;
				break;
			}
		}
	}
	
	public int getCategoryId()
	{
		return _categoryId;
	}
	
	public int getTypeId()
	{
		return _typeId;
	}
	
	public String getCategoryName()
	{
		return _categoryName;
	}
	
	public String getTypeName()
	{
		return _typeName;
	}
	
	public String getType()
	{
		return _type;
	}
	
	public String getAdditionalText()
	{
		return _additionalText;
	}
	
	public RefreshTime getRefreshTime()
	{
		return _refreshTime;
	}
	
	public boolean isTimer()
	{
		return _timer;
	}
	
	public ArrayList<Location> getStatueSpawns()
	{
		return _statueSpawns;
	}
	
	public Map<Integer, List<MuseumReward>> getRewards()
	{
		return _rewards;
	}
	
	public HashMap<Integer, TopPlayer> getAllTops()
	{
		return _players;
	}
	
	public HashMap<Integer, TopPlayer> getAllTotalTops()
	{
		return _totalTopPlayers;
	}
	
	public HashMap<Integer, TopPlayer> getAllStatuePlayers()
	{
		return _statuePlayers;
	}
	
	public ArrayList<L2MuseumStatueInstance> getAllSpawnedStatues()
	{
		return _spawnedStatues;
	}
}
