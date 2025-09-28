package gabriel.events.weeklyRank.objects;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class ClanRankObject {
    private final int rankPlace;
    private final int clanId;
    private final String clanName;
    private int clanKills;
    private final boolean received;

    public ClanRankObject(int rankPlace, int clanId, String clanName, int clanKills, boolean received) {
        this.rankPlace = rankPlace;
        this.clanId = clanId;
        this.clanName = clanName;
        this.clanKills = clanKills;
        this.received = received;
    }

    public int getRankPlace() {
        return rankPlace;
    }

    public int getClanId() {
        return clanId;
    }

    public String getClanName() {
        return clanName;
    }

    public int getClanKills() {
        return clanKills;
    }

    public boolean isReceived() {
        return received;
    }

    public void increaseClanKills() {
        setClanKills(getClanKills() + 1);
    }

    private void setClanKills(int clanKills) {
        this.clanKills = clanKills;
    }
}
