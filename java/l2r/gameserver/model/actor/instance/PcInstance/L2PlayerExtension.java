package l2r.gameserver.model.actor.instance.PcInstance;

import l2r.gameserver.model.actor.instance.L2PcInstance;

public class L2PlayerExtension
{
	private L2PcInstance _activeChar = null;
	
	public L2PlayerExtension(L2PcInstance activeChar)
	{
		_activeChar = activeChar;
	}
	
	protected L2PcInstance getChar()
	{
		return _activeChar;
	}
	
	/**
	 * This method should be used to delete all references to the L2PcInstance and initiate further cleanup tasks (e.g. on player logout)
	 */
	public void destroy()
	{
		_activeChar = null;
	}
}
