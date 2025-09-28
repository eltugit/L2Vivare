package gabriel.customAgathion.Runnable;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.clientpackets.L2GameClientPacket;
import l2r.gameserver.network.serverpackets.UserInfo;

import java.util.logging.Level;
import java.util.logging.Logger;


public class RequestPreviewAgathion extends L2GameClientPacket {
    private static final String _C__C7_REQUESTPREVIEWITEM = "[C] C7 RequestPreviewItem";
    protected static final Logger _log = Logger.getLogger(RequestPreviewAgathion.class.getName());

    @SuppressWarnings("unused")
    private int _unk;
    private int _listId;
    private int _count;
    private int[] _items;


    public static class RemoveWearItemsTask implements Runnable {
        private final L2PcInstance activeChar;

        public RemoveWearItemsTask(L2PcInstance player) {
            activeChar = player;
        }

        @Override
        public void run() {
            try {
                activeChar.deleteQuickVar("tryAgathion");
                activeChar.sendPacket(SystemMessageId.NO_LONGER_TRYING_ON);
                activeChar.sendPacket(new UserInfo(activeChar));
                refreshVisual(activeChar);
            } catch (Exception e) {
                _log.log(Level.SEVERE, "", e);
            }
        }
    }

    @Override
    protected void readImpl() {
        _unk = readD();
        _listId = readD();
        _count = readD();

        if (_count < 0) {
            _count = 0;
        }
        if (_count > 100) {
            return; // prevent too long lists
        }

        // Create _items table that will contain all ItemID to Wear
        _items = new int[_count];

        // Fill _items table with all ItemID to Wear
        for (int i = 0; i < _count; i++) {
            _items[i] = readD();
        }
    }

    @Override
    protected void runImpl() {

    }

    public static void refreshVisual(L2PcInstance activeChar) {
        activeChar.startAbnormalEffect(AbnormalEffect.E_VESPER_1);
        activeChar.stopAbnormalEffect(AbnormalEffect.E_VESPER_1);
    }

    @Override
    public String getType() {
        return _C__C7_REQUESTPREVIEWITEM;
    }
}

