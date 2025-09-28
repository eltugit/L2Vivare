package gr.sr.events.engine.team;

import gr.sr.interf.PlayerEventInfo;

public class OnePlayerTeam extends EventTeam
{
    public OnePlayerTeam(final int teamId, final String teamName) {
        super(teamId, teamName);
    }
    
    public PlayerEventInfo getPlayer() {
        if (this.getPlayers().isEmpty()) {
            return null;
        }
        return this.getPlayers().get(0);
    }
    
    @Override
    protected int getTeamSize() {
        return 1;
    }
}
