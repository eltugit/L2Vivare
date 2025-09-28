package gr.sr.interf.callback;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;

import java.util.Collection;

public interface ICallback {
    void eventStarts(int paramInt, EventType paramEventType, Collection<? extends EventTeam> paramCollection);

    void playerKills(EventType paramEventType, PlayerEventInfo paramPlayerEventInfo1, PlayerEventInfo paramPlayerEventInfo2);

    void playerScores(EventType paramEventType, PlayerEventInfo paramPlayerEventInfo, int paramInt);

    void playerFlagScores(EventType paramEventType, PlayerEventInfo paramPlayerEventInfo);

    void playerKillsVip(EventType paramEventType, PlayerEventInfo paramPlayerEventInfo1, PlayerEventInfo paramPlayerEventInfo2);

    void eventEnded(int paramInt, EventType paramEventType, Collection<? extends EventTeam> paramCollection);
}


