package gr.sr.l2j;

import gr.sr.interf.PlayerEventInfo;

import java.util.Map;

public interface IPlayerBase {
    PlayerEventInfo addInfo(PlayerEventInfo paramPlayerEventInfo);

    PlayerEventInfo getPlayer(int paramInt);

    Map<Integer, PlayerEventInfo> getPs();

    void eventEnd(PlayerEventInfo paramPlayerEventInfo);

    void playerDisconnected(PlayerEventInfo paramPlayerEventInfo);

    void deleteInfo(int paramInt);
}


