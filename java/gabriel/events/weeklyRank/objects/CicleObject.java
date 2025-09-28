package gabriel.events.weeklyRank.objects;

import java.util.Map;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class CicleObject {
    private final int cicleId;
    private final Map<Integer, PlayerRankObject> playerRanks;
    private final Map<Integer, ClanRankObject> clanRanks;
    private final Map<Integer, PlayerAssistRankObject> assistRanks;

    public CicleObject(int cicleId, Map<Integer, PlayerRankObject> playerRanks, Map<Integer, ClanRankObject> clanRanks,Map<Integer, PlayerAssistRankObject> assistRanks) {
        this.cicleId = cicleId;
        this.playerRanks = playerRanks;
        this.clanRanks = clanRanks;
        this.assistRanks = assistRanks;
    }

    public int getCicleId() {
        return cicleId;
    }

    public Map<Integer, PlayerRankObject> getPlayerRanks() {
        return playerRanks;
    }

    public Map<Integer, ClanRankObject> getClanRanks() {
        return clanRanks;
    }

    public Map<Integer, PlayerAssistRankObject> getAssistRanks() {
        return assistRanks;
    }
}
