package gr.sr.interf;

import gr.sr.l2j.CallBack;
import gr.sr.l2j.IPlayerBase;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerBase
        implements IPlayerBase {
    private final Map<Integer, PlayerEventInfo> players = new ConcurrentHashMap<>();

    public void load() {
        CallBack.getInstance().setPlayerBase(this);
    }

    public PlayerEventInfo getPlayer(int id) {
        return this.players.get(Integer.valueOf(id));
    }

    public Map<Integer, PlayerEventInfo> getPs() {
        return this.players;
    }

    protected PlayerEventInfo getPlayer(L2PcInstance player) {
        return player.getEventInfo();
    }

    public PlayerEventInfo addInfo(PlayerEventInfo player) {
        this.players.put(Integer.valueOf(player.getPlayersId()), player);
        return player;
    }

    public void eventEnd(PlayerEventInfo player) {
        deleteInfo(player.getOwner());
    }

    public void playerDisconnected(PlayerEventInfo player) {
        eventEnd(player);
    }

    public void deleteInfo(int player) {
        this.players.remove(Integer.valueOf(player));
    }

    protected void deleteInfo(L2PcInstance player) {
        this.players.remove(Integer.valueOf(player.getObjectId()));
    }

    public static final PlayerBase getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final PlayerBase _instance = new PlayerBase();
    }
}


