package gr.sr.interf.delegate;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.delegate.IShowBoardData;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.gameserver.network.serverpackets.ShowBoard;

public class ShowBoardData
        implements IShowBoardData {
    private final ShowBoard _board;

    public ShowBoardData(ShowBoard sb) {
        this._board = sb;
    }

    public ShowBoardData(String text, String id) {
        this._board = new ShowBoard(text, id);
    }

    public void sendToPlayer(PlayerEventInfo player) {
        player.getOwner().sendPacket((L2GameServerPacket) this._board);
    }
}


