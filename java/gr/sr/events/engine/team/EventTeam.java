package gr.sr.events.engine.team;

import gr.sr.events.engine.EventManager;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.PartyData;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventTeam
{
    protected int _teamId;
    private String _teamName;
    private String _fullName;
    private List<PlayerEventInfo> _players;
    private int _levelSum;
    private int _averageLevel;
    public int _nameColor;
    private int _kills;
    private int _deaths;
    private int _score;
    private int _finalPosition;
    
    public EventTeam(final int teamId) {
        this._teamId = teamId;
        this.initializePlayers();
    }
    
    public EventTeam(final int teamId, final String teamName, final String fullName) {
        this._teamId = teamId;
        this._teamName = teamName;
        this._fullName = fullName;
        this._nameColor = EventManager.getInstance().getTeamColorForName(teamId);
        this._levelSum = 0;
        this._averageLevel = 0;
        this._kills = 0;
        this._deaths = 0;
        this._score = 0;
        this._finalPosition = -1;
        this.initializePlayers();
    }
    
    public EventTeam(final int teamId, final String teamName) {
        this(teamId, teamName, teamName);
    }
    
    protected void initializePlayers() {
        this._players = new CopyOnWriteArrayList<PlayerEventInfo>();
    }
    
    public boolean removePlayer(final PlayerEventInfo pi) {
        return this._players.remove(pi);
    }
    
    public void addPlayer(final PlayerEventInfo pi, final boolean init) {
        this._players.add(pi);
        if (init) {
            this.initPlayer(pi);
        }
        this._levelSum += pi.getLevel();
    }
    
    public void calcAverageLevel() {
        this._averageLevel = (int)(this._levelSum / (double)this._players.size());
    }
    
    public int getAverageLevel() {
        return this._averageLevel;
    }
    
    protected void initPlayer(final PlayerEventInfo pi) {
        pi.setEventTeam(this);
        pi.setNameColor(this.getNameColor());
        pi.broadcastUserInfo();
    }
    
    public void message(final String msg, final String name, final boolean special) {
        for (final PlayerEventInfo pi : this._players) {
            pi.screenMessage(msg, name, special);
        }
    }
    
    public void createParties() {
        int count = 0;
        final int size = this.getPlayers().size();
        PartyData party = null;
        if (size <= 1) {
            return;
        }
        for (final PlayerEventInfo player : this.getPlayers()) {
            if (count % 9 == 0 && size - count != 1) {
                party = new PartyData(player);
            }
            else if (count % 9 < 9 && party != null) {
                party.addPartyMember(player);
            }
            ++count;
        }
    }
    
    public List<PlayerEventInfo> getPlayers() {
        return this._players;
    }
    
    public int getTeamId() {
        return this._teamId;
    }
    
    public int getDeaths() {
        return this._deaths;
    }
    
    public int getKills() {
        return this._kills;
    }
    
    public int getScore() {
        return this._score;
    }
    
    public void raiseScore(final int count) {
        this._score += count;
    }
    
    public int getNameColor() {
        return this._nameColor;
    }
    
    public String getTeamName() {
        return this._teamName;
    }
    
    public String getFullName() {
        return this._fullName;
    }
    
    public void raiseDeaths(final int count) {
        this._deaths += count;
    }
    
    public void raiseKills(final int count) {
        this._kills += count;
    }
    
    public void resetDeaths() {
        this._deaths = 0;
    }
    
    public void resetScore() {
        this._score = 0;
    }
    
    protected int getTeamSize() {
        return -1;
    }
    
    public String getNameColorInString() {
        return EventManager.getInstance().getTeamColorForHtml(this._teamId);
    }
    
    public void setFinalPosition(final int pos) {
        this._finalPosition = pos;
    }
    
    public int getFinalPosition() {
        return this._finalPosition;
    }
}
