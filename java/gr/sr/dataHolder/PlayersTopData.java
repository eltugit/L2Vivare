package gr.sr.dataHolder;




public class PlayersTopData {
    private final String charName;
    private final String clanName;
    private final int pvp;
    private final int pk;
    private final long currencyCount;
    private final int clanLevel;
    private final int onlineTime;

    public PlayersTopData(String charName, String clanName, int pvp, int pk, long currencyCount, int clanLevel, int onlineTime) {
        this.charName = charName;
        this.clanName = clanName;
        this.pvp = pvp;
        this.pk = pk;
        this.currencyCount = currencyCount;
        this.clanLevel = clanLevel;
        this.onlineTime = onlineTime;
    }
    
    public String getCharName() {
        return this.charName;
    }
    
    public String getClanName() {
        return this.clanName;
    }
    
    public int getPvp() {
        return this.pvp;
    }
    
    public int getPk() {
        return this.pk;
    }
    
    public long getCurrencyCount() {
        return this.currencyCount;
    }
    
    public int getClanLevel() {
        return this.clanLevel;
    }
    
    public int getOnlineTime() {
        return this.onlineTime;
    }
}
