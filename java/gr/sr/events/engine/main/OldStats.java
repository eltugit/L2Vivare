package gr.sr.events.engine.main;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.EventType;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.ShowBoardData;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OldStats {
    private final Map<Integer, Map<Integer, StatModell>> stats = new ConcurrentHashMap<>();
    public Map<Integer, int[]> tempTable = (Map) new ConcurrentHashMap<>();
    private final Map<Integer, ShowBoardData> htmls = new ConcurrentHashMap<>();
    private final Map<Integer, int[]> statSums = (Map) new ConcurrentHashMap<>();
    private final boolean enabled = false;

    public static OldStats getInstance() {
        return SingletonHolder._instance;
    }

    protected OldStats() {
        this.stats.clear();
        this.tempTable.clear();
        this.htmls.clear();
        this.statSums.clear();
        loadSQL();
    }

    protected void applyChanges() {
    }

    public void applyMiniEventStatsChanges(int eventId, Map<Integer, int[]> statsTable) {
    }

    private void createHtmls() {
        this.htmls.clear();
        StringBuilder sb = new StringBuilder();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT characters.char_name, sunrise_stats_full.* FROM sunrise_stats_full INNER JOIN characters ON characters.charId = sunrise_stats_full.player ORDER BY sunrise_stats_full.wins DESC");
             ResultSet rset = statement.executeQuery()) {
            rset.last();
            int size = rset.getRow();
            rset.beforeFirst();
            int count = 0;
            while (rset.next()) {
                count++;
                if (count % 10 == 1) {
                    sb.append("<html><body><br><br><center><table width=150><tr><td width=50><center>" + (((count - 1) / 10 != 0) ? ("<a action=\"bypass -h eventstats " + ((count - 1) / 10) + "\">Prev</a>") : "Prev") + "</td><td width=50><center>" + ((count - 1) / 10 + 1) + "</td><td width=50><center>" + (((count - 1) / 10 != size / 10) ? ("<a action=\"bypass -h eventstats " + ((count - 1) / 10 + 2) + "\">Next</a>") : "Next") + "</td></tr></table><br><br><center><table width=700 bgcolor=5A5A5A><tr><td width=30><center>Rank</td><td width=100><center>Name</td><td width=65><center>Events</td><td width=65><center>Win%</td><td width=65><center>K:D</td><td width=65><center>Wins</td><td width=65><center>Losses</td><td width=65><center>Kills</td><td width=65><center>Deaths</td><td width=100><center>Favourite Event</td></tr></table><br><center><table width=720>");
                }
                sb.append("<tr><td width=30><center>" + count + ".</td><td width=100><a action=\"bypass -h eventstats_show " + rset.getInt("player") + "\">" + rset.getString("char_name") + "</a></td><td width=65><center>" + rset.getInt("num") + "</td><td width=65><center>" + rset.getDouble("winpercent") + "%</td><td width=65><center>" + rset.getDouble("kdratio") + "</td><td width=65><center>" + rset.getInt("wins") + "</td><td width=65><center>" + rset.getInt("losses") + "</td><td width=65><center>" + rset.getInt("kills") + "</td><td width=65><center>" + rset.getInt("deaths") + "</td><td width=120><center>" + EventType.getEventByMainId(rset.getInt("favevent")).getHtmlTitle() + "</td></tr>");
                if (count % 10 == 0) {
                    sb.append("</table></body></html>");
                    this.htmls.put(Integer.valueOf(count / 10), new ShowBoardData(sb.toString(), "101"));
                    sb.setLength(0);
                }
            }
            if (count % 10 != 0 && !this.htmls.containsKey(Integer.valueOf(count / 10 + 1))) {
                sb.append("</table></body></html>");
                this.htmls.put(Integer.valueOf(count / 10 + 1), new ShowBoardData(sb.toString(), "101"));
                sb.setLength(0);
            }
        } catch (Exception e) {
            System.out.println("create SQL exception.");
        }
        SunriseLoader.debug("createHtmls finished");
    }

    private void loadSQL() {
    }

    public void showHtml(int id, PlayerEventInfo player) {
        player.sendMessage("The stat tracking is disabled.");
    }

    public void showPlayerStats(int playerId, PlayerEventInfo player) {
        StringBuilder tb = new StringBuilder();
        tb.append("<html><body><br><br><center><table width=640 bgcolor=5A5A5A><tr><td width=120><center>Event</td><td width=65><center>Count</td><td width=65><center>Win%</td><td width=65><center>K:D</td><td width=65><center>Wins</td><td width=65><center>Losses</td><td width=65><center>Kills</td><td width=65><center>Deaths</td><td width=65><center>Scores</td></tr></table><br><center><table width=640>");
        if (this.stats.containsKey(Integer.valueOf(playerId))) {
            for (Map.Entry<Integer, StatModell> event : (Iterable<Map.Entry<Integer, StatModell>>) ((Map) this.stats.get(Integer.valueOf(playerId))).entrySet()) {
                StatModell stats = event.getValue();
                if (EventType.getEventByMainId(((Integer) event.getKey()).intValue()) != null) {
                    String kdRatio = String.valueOf((stats._deaths == 0) ? stats._kills : (stats._kills / stats._deaths));
                    String winPercent = String.valueOf((stats._wins / stats._num) * 100.0D);
                    kdRatio = kdRatio.substring(0, Math.min(3, kdRatio.length()));
                    winPercent = winPercent.substring(0, Math.min(5, winPercent.length()));
                    tb.append("<tr><td width=120>" + EventType.getEventByMainId(((Integer) event.getKey()).intValue()).getHtmlTitle() + "</td><td width=65><center>" + stats._num + "</td><td width=65><center>" + winPercent + "%</td><td width=65><center>" + kdRatio + "</td><td width=65><center>" + stats._wins + "</td><td width=65><center>" + stats._losses + "</td><td width=65><center>" + stats._kills + "</td><td width=65><center>" + stats._deaths + "</td><td width=65><center>" + stats._scores + "</td></tr>");
                }
            }
        }
        tb.append("</table></body></html>");
        ShowBoardData sb = new ShowBoardData(tb.toString(), "101");
        sb.sendToPlayer(player);
        sb = new ShowBoardData(null, "102");
        sb.sendToPlayer(player);
        sb = new ShowBoardData(null, "103");
        sb.sendToPlayer(player);
    }

    private void sumPlayerStats() {
    }

    public void updateSQL(Set<PlayerEventInfo> players, int eventId) {
    }

    private class StatModell {
        protected int _num;
        protected int _wins;
        protected int _losses;
        protected int _kills;
        protected int _deaths;
        protected int _scores;

        protected StatModell(int num, int wins, int losses, int kills, int deaths, int scores) {
            this._num = num;
            this._wins = wins;
            this._losses = losses;
            this._kills = kills;
            this._deaths = deaths;
            this._scores = scores;
        }
    }

    private static class SingletonHolder {
        protected static final OldStats _instance = new OldStats();
    }
}


