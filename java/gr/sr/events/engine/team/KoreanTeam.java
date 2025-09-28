package gr.sr.events.engine.team;

import gr.sr.interf.PlayerEventInfo;

import java.util.LinkedHashMap;
import java.util.Map;

public class KoreanTeam extends EventTeam
{
    private final Map<Integer, Integer> _players;
    private int _order;
    private int _nextPlayer;
    private PlayerEventInfo _fighting;
    
    public KoreanTeam(final int teamId, final String teamName) {
        super(teamId, teamName);
        this._players = new LinkedHashMap<Integer, Integer>(this.getTeamSize());
        this._order = 0;
        this._nextPlayer = 0;
        this._fighting = null;
    }
    
    public boolean isFighting(final PlayerEventInfo player) {
        return this._fighting != null && this._fighting.getPlayersId() == player.getPlayersId();
    }
    
    @Override
    protected int getTeamSize() {
        return 4;
    }
    
    @Override
    public void addPlayer(final PlayerEventInfo pi, final boolean init) {
        super.addPlayer(pi, init);
        ++this._order;
        this._players.put(this._order, pi.getPlayersId());
    }
    
    public PlayerEventInfo getNextPlayer() {
        if (this.getPlayers().isEmpty()) {
            return null;
        }
        int next = 0;
        do {
            ++this._nextPlayer;
            next = this._players.get(this._nextPlayer);
        } while (next == 0);
        for (final PlayerEventInfo pi : this.getPlayers()) {
            if (pi.getPlayersId() == next) {
                return this._fighting = pi;
            }
        }
        return null;
    }
}
