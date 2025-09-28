package gr.sr.events.engine.team;

import gr.sr.events.engine.EventManager;
import gr.sr.interf.PlayerEventInfo;

public class FixedPartyTeam extends EventTeam
{
    private final int _teamSize;
    
    public FixedPartyTeam(final int teamId, final String teamName, final int size) {
        super(teamId, teamName);
        this._teamSize = size;
    }
    
    public FixedPartyTeam(final int teamId, final int size) {
        super(teamId, EventManager.getInstance().getTeamName(teamId) + " team");
        this._teamSize = size;
    }
    
    public PlayerEventInfo getLeader() {
        if (this.getPlayers().isEmpty()) {
            return null;
        }
        return this.getPlayers().get(0);
    }
    
    @Override
    protected int getTeamSize() {
        return this._teamSize;
    }
}
