package gr.sr.interf.callback;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;

import java.util.Collection;

public class EventAchievments
        implements ICallback {
    public void eventStarts(int instance, EventType event, Collection<? extends EventTeam> teams) {
    }

    public void playerKills(EventType event, PlayerEventInfo player, PlayerEventInfo target) {
    }

    public void playerScores(EventType event, PlayerEventInfo player, int count) {
    }

    public void playerFlagScores(EventType event, PlayerEventInfo player) {
    }

    public void playerKillsVip(EventType event, PlayerEventInfo player, PlayerEventInfo vip) {
    }

    public void eventEnded(int instance, EventType event, Collection<? extends EventTeam> teams) {
    }

    public static final EventAchievments getInstance() {
        if (SingletonHolder._instance == null) {
            SingletonHolder.register();
        }
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static EventAchievments _instance;

        protected static void register() {
            _instance = new EventAchievments();
            CallbackManager.getInstance().registerCallback(_instance);
        }
    }
}


