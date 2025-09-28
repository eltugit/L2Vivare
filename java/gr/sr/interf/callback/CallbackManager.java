package gr.sr.interf.callback;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CallbackManager
        implements ICallback {
    public Set<ICallback> _list = ConcurrentHashMap.newKeySet();

    public void registerCallback(ICallback c) {
        this._list.add(c);
    }

    public void eventStarts(int instance, EventType event, Collection<? extends EventTeam> teams) {
        for (ICallback cb : this._list) {
            try {
                cb.eventStarts(instance, event, teams);
            } catch (Exception exception) {
            }
        }
    }

    public void playerKills(EventType event, PlayerEventInfo player, PlayerEventInfo target) {
        for (ICallback cb : this._list) {
            try {
                cb.playerKills(event, player, target);
            } catch (Exception exception) {
            }
        }
    }

    public void playerScores(EventType event, PlayerEventInfo player, int count) {
        for (ICallback cb : this._list) {
            try {
                cb.playerScores(event, player, count);
            } catch (Exception exception) {
            }
        }
    }

    public void playerFlagScores(EventType event, PlayerEventInfo player) {
        for (ICallback cb : this._list) {
            try {
                cb.playerFlagScores(event, player);
            } catch (Exception exception) {
            }
        }
    }

    public void playerKillsVip(EventType event, PlayerEventInfo player, PlayerEventInfo vip) {
        for (ICallback cb : this._list) {
            try {
                cb.playerKillsVip(event, player, vip);
            } catch (Exception exception) {
            }
        }
    }

    public void eventEnded(int instance, EventType event, Collection<? extends EventTeam> teams) {
        for (ICallback cb : this._list) {
            try {
                cb.eventEnded(instance, event, teams);
            } catch (Exception exception) {
            }
        }
    }

    public static final CallbackManager getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final CallbackManager _instance = new CallbackManager();
    }
}


