package gabriel.pvpInstanceZone.utils;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class RewardHolder 
{
	final int _itemId;
	final long _count;
	
	public RewardHolder(int id, long count)
	{
		_itemId = id;
		_count = count;
	}
	
	public int getItemId()
	{
		return _itemId;
	}
	
	public long getCount()
	{
		return _count;
	}
}
