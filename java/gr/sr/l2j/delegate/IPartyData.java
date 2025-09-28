package gr.sr.l2j.delegate;

import gr.sr.interf.PlayerEventInfo;

public interface IPartyData {
    void addPartyMember(PlayerEventInfo paramPlayerEventInfo);

    void removePartyMember(PlayerEventInfo paramPlayerEventInfo);

    PlayerEventInfo getLeader();

    int getLeadersId();

    PlayerEventInfo[] getPartyMembers();

    int getMemberCount();
}


