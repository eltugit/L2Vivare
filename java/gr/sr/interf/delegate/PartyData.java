package gr.sr.interf.delegate;

import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.delegate.IPartyData;
import l2r.gameserver.enums.MessageType;
import l2r.gameserver.model.L2Party;
import l2r.gameserver.model.actor.instance.L2PcInstance;

import java.util.LinkedList;
import java.util.List;

public class PartyData
        implements IPartyData {
    private final L2Party _party;

    public PartyData(L2Party p) {
        this._party = p;
    }

    public PartyData(PlayerEventInfo leader) {
        leader.getOwner().setParty(new L2Party(leader.getOwner()));
        this._party = leader.getOwner().getParty();
    }

    public L2Party getParty() {
        return this._party;
    }

    public boolean exists() {
        return (this._party != null);
    }

    public void addPartyMember(PlayerEventInfo player) {
        player.getOwner().joinParty(this._party);
    }

    public void removePartyMember(PlayerEventInfo player) {
        this._party.removePartyMember(player.getOwner(), MessageType.None);
    }

    public PlayerEventInfo getLeader() {
        return this._party.getLeader().getEventInfo();
    }

    public PlayerEventInfo[] getPartyMembers() {
        List<PlayerEventInfo> players = new LinkedList<>();
        for (L2PcInstance player : this._party.getMembers()) {
            players.add(player.getEventInfo());
        }
        return players.<PlayerEventInfo>toArray(new PlayerEventInfo[players.size()]);
    }

    public int getMemberCount() {
        return this._party.getMemberCount();
    }

    public int getLeadersId() {
        return this._party.getLeaderObjectId();
    }
}


