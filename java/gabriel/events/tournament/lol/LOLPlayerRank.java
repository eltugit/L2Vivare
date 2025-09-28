package gabriel.events.tournament.lol;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class LOLPlayerRank {
    private final int ownerId;
    private String ownerName;
    private int eloSoloDuo;
    private int eloTeams;

    public LOLPlayerRank(int ownerId, String ownerName, int eloSoloDuo, int eloTeams) {
        this.ownerId = ownerId;
        this.eloSoloDuo = eloSoloDuo;
        this.eloTeams = eloTeams;
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getElo(boolean team) {
        if(team)
            return eloTeams;
        else
            return eloSoloDuo;
    }

    public void incrementEloBy(int elo, boolean team){
        if(team){
            this.eloTeams += elo;
        }else{
            this.eloSoloDuo += elo;
        }
        LOLRankDAO.getInstance().update(this);
    }

    public void declineEloBy(int elo, boolean team){
        if(team){
            this.eloTeams -= elo;
            if(this.eloTeams < 0)
                this.eloTeams = 0;
        }else{
            this.eloSoloDuo -= elo;
            if(this.eloSoloDuo < 0)
                this.eloSoloDuo = 0;
        }
        LOLRankDAO.getInstance().update(this);
    }
}
