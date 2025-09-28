package gr.sr.events.engine.mini.tournament;

import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.interf.PlayerEventInfo;

import java.util.List;

public class Tournament
{
    private static boolean _active;
    private static Tournament _tournament;
    private static MiniEventManager _event;

    public static Tournament getTournament() {
        return Tournament._tournament;
    }

    public static void setTournamentEvent(final MiniEventManager event) {
        Tournament._event = event;
    }

    public static void startTournament(final PlayerEventInfo gm) {
        if (Tournament._event != null && !Tournament._event.isTournamentActive()) {
            final Tournament tournament = Tournament._tournament = new Tournament();
            Tournament._event.setTournamentActive(true);
            Tournament._active = true;
        }
        else {
            gm.sendMessage("You must first select an event.");
        }
    }

    public static void register(final PlayerEventInfo player) {
        if (Tournament._active) {
            Tournament._event.registerTeam(player);
        }
    }

    public List<RegistrationData> getRegistered() {
        return Tournament._event.getRegistered();
    }

    public MiniEventManager getEvent() {
        return Tournament._event;
    }

    static {
        Tournament._active = false;
        Tournament._tournament = null;
        Tournament._event = null;
    }
}
