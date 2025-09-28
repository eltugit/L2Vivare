package gabriel.events.weeklyRank.objects;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PlayerRankObject {
    private final int rankPlace;
    private final int charId;
    private final String charName;
    private int charKills;
    private final boolean received;

    public PlayerRankObject(int rankPlace, int charId, String charName, int charKills, boolean received) {
        this.rankPlace = rankPlace;
        this.charId = charId;
        this.charName = charName;
        this.charKills = charKills;
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

    public int getCharKills() {
        return charKills;
    }

    public boolean isReceived() {
        return received;
    }

    public void increaseCharKills() {
        setCharKills(getCharKills() + 1);
    }

    private void setCharKills(int charKills) {
        this.charKills = charKills;
    }
}
