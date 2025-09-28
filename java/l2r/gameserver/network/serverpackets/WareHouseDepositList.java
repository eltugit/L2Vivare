package l2r.gameserver.network.serverpackets;

import gabriel.interServerExchange.ISEConfig;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;

import java.util.ArrayList;
import java.util.List;

public final class WareHouseDepositList extends AbstractItemPacket {
	public static final int PRIVATE = 1;
	public static final int CLAN = 4;
	public static final int CASTLE = 3; // not sure
	public static final int FREIGHT = 1;
	private final long _playerAdena;
	private final List<L2ItemInstance> _items = new ArrayList<>();
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

	public WareHouseDepositList(L2PcInstance player, int type) {
		this(player, type, false);
	}
	public WareHouseDepositList(L2PcInstance player, int type, boolean interServer) {
		_whType = type;
		_playerAdena = player.getAdena();
		this.interServer = interServer;

		final boolean isPrivate = _whType == PRIVATE;
		for (L2ItemInstance temp : player.getInventory().getAvailableItems(true, isPrivate, false)) {
			if(interServer && ISEConfig.BLOCKED_ITEMS.stream().anyMatch(e->e == temp.getId()))
				continue;

			boolean isItemOk = true;

			if(interServer) {
				if (!ISEConfig.ALLOW_AUG && temp.getAugmentation() != null)
					isItemOk = false;
				if (!ISEConfig.ALLOW_ELEMENTALS && temp.getElementals() != null)
					isItemOk = false;
				if (!ISEConfig.ALLOW_ENCHANT && temp.getEnchantLevel() > 0)
					isItemOk = false;

				if (!isItemOk)
					continue;

				if(temp.isHeroItem()){
					_items.add(temp);
					continue;
				}

			}

			if ((temp != null) && temp.isDepositable(isPrivate)) {
				_items.add(temp);
			}
		}
	}

	@Override
	protected final void writeImpl() {
		writeC(0x41);

		writeH(_whType);
		writeQ(_playerAdena);
		writeH(_items.size());

		for (L2ItemInstance item : _items) {
			writeItem(item);
			writeD(item.getObjectId());
		}
	}
}
