package l2r.gameserver.network.serverpackets;

import gabriel.interServerExchange.InterServerExchangeManager;
import l2r.Config;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;

public final class WareHouseWithdrawalList extends AbstractItemPacket {
	public static final int PRIVATE = 1;
	public static final int CLAN = 4;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	private final L2PcInstance _activeChar;
	private final long _playerAdena;
	private L2ItemInstance[] _items;
	private final boolean interServer;
	/**
	 * <ul>
	 * <li>0x01-Private Warehouse</li>
	 * <li>0x02-Clan Warehouse</li>
	 * <li>0x03-Castle Warehouse</li>
	 * <li>0x04-Warehouse</li>
	 * </ul>
	 */
	private final int _whType;

	public WareHouseWithdrawalList(L2PcInstance player, int type) {
		this(player, type, false);
	}

	public WareHouseWithdrawalList(L2PcInstance player, int type, boolean interServer) {
		_activeChar = player;
		_whType = type;
		this.interServer = interServer;

		_playerAdena = _activeChar.getAdena();


		if(interServer){
			_items = InterServerExchangeManager.getInstance().getItems(player).toArray(new L2ItemInstance[0]);
			if(_items.length == 0){
				player.sendMessage("No items processed");
				return;
			}
		}else{

			if (_activeChar.getActiveWarehouse() == null) {
				// Something went wrong!
				_log.warn("error while sending withdraw request to: " + _activeChar.getName());
				return;
			}

			_items = _activeChar.getActiveWarehouse().getItems();
			if (Config.DEBUG) {
				for (L2ItemInstance item : _items) {
					_log.info("item:" + item.getItem().getName() + " type1:" + item.getItem().getType1() + " type2:" + item.getItem().getType2());
				}
			}
		}
	}

	@Override
	protected final void writeImpl() {
		writeC(0x42);

		writeH(_whType);
		writeQ(_playerAdena);
		writeH(_items.length);

		for (L2ItemInstance item : _items) {
			writeItem(item);
			writeD(item.getObjectId());
		}
	}
}
