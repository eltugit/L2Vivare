package gr.sr.events.engine.mini;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.PartyData;
import gr.sr.l2j.CallBack;

import java.util.List;

public class RegistrationData
{
    private final List<PlayerEventInfo> _players;
    private boolean _choosen;
    
    public RegistrationData(final List<PlayerEventInfo> players) {
        this._choosen = false;
        this._players = players;
    }
    
    public PlayerEventInfo getKeyPlayer() {
        return this._players.get(0);
    }
    
    public List<PlayerEventInfo> getPlayers() {
        return this._players;
    }
    
    public PartyData getParty() {
        if (this.getKeyPlayer().isOnline()) {
            return this.getKeyPlayer().getParty();
        }
        return null;
    }
    
    public void register(final boolean isRegistered, final MiniEventManager registeredEvent) {
        for (final PlayerEventInfo pi : this._players) {
            pi.setIsRegisteredToMiniEvent(isRegistered, registeredEvent);
            if (!isRegistered) {
                CallBack.getInstance().getPlayerBase().eventEnd(pi);
            }
        }
    }
    
    public void message(final String msg, final boolean screen) {
        for (final PlayerEventInfo pi : this._players) {
            if (screen) {
                pi.screenMessage(msg, "", true);
            }
            else {
                pi.sendMessage(msg);
            }
        }
    }
    
    public int getAverageLevel() {
        int i = 0;
        for (final PlayerEventInfo player : this._players) {
            i += player.getLevel();
        }
        i = Math.round((float)(i / this._players.size()));
        return i;
    }
    
    public boolean isChosen() {
        return this._choosen;
    }
    
    public void setIsChosen(final boolean b) {
        this._choosen = b;
    }
}
