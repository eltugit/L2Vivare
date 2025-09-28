package gabriel.events.weeklyRank.objects;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PlayerAssistRankObject {
    private final int rankPlace;
    private final int charId;
    private final String charName;
    private int charAssists;
    private final boolean received;

    public PlayerAssistRankObject(int rankPlace, int charId, String charName, int charAssists, boolean received) {
        this.rankPlace = rankPlace;
        this.charId = charId;
        this.charName = charName;
        this.charAssists = charAssists;
        this.received = received;
    }

    public int getRankPlace() {
        return rankPlace;
    }

    public int getCharId() {
        return charId;
    }

    public String getCharName() {
        return charName;
    }

    public int getCharAssists() {
        return charAssists;
    }

    public boolean isReceived() {
        return received;
    }

    public void increaseCharAssists() {
        setCharAssists(getCharAssists() + 1);
    }

    private void setCharAssists(int charAssists) {
        this.charAssists = charAssists;
    }
}
