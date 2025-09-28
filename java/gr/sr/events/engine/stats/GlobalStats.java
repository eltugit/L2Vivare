package gr.sr.events.engine.stats;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.base.EventType;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

public class GlobalStats extends EventStats
{
    private final Map<PlayerEventInfo, Map<EventType, GlobalStatsModel>> _playerGlobalStats;
    private final Map<PlayerEventInfo, String> _playerGlobalStatsHtml;
    private Map<SortType, Map<Integer, String>> _globalStatsHtml;
    private Map<Integer, GlobalStatsSum> _data;
    private ScheduledFuture<?> _globalStatsReload;
    private long _lastLoad;
    private String _statsSorting;
    private int _playersPerPage;
    private int _statsRefresh;
    private boolean _ignoreBannedPlayers;
    private boolean _ignoreGMs;
    public boolean _enableStatistics;
    public boolean _enableGlobalStatistics;
    private boolean _showDetailedPlayerInfo;
    private boolean _showPkCount;
    private boolean globalStatsLoaded;
    
    public GlobalStats() {
        this._playerGlobalStats = new ConcurrentHashMap<PlayerEventInfo, Map<EventType, GlobalStatsModel>>();
        this._playerGlobalStatsHtml = new ConcurrentHashMap<PlayerEventInfo, String>();
        this._globalStatsHtml = new ConcurrentHashMap<SortType, Map<Integer, String>>();
        this._data = new ConcurrentHashMap<Integer, GlobalStatsSum>();
        this._playersPerPage = 8;
        this._statsRefresh = 1800;
        this._ignoreBannedPlayers = true;
        this._ignoreGMs = false;
        this._showDetailedPlayerInfo = true;
        this._showPkCount = true;
        this.globalStatsLoaded = false;
    }
    
    private void loadConfigs() {
        this._enableStatistics = EventConfig.getInstance().getGlobalConfigBoolean("enableStatistics");
        this._enableGlobalStatistics = EventConfig.getInstance().getGlobalConfigBoolean("enableGlobalStatistics");
        this._statsRefresh = EventConfig.getInstance().getGlobalConfigInt("globalStatisticsRefresh");
        this._statsSorting = EventConfig.getInstance().getGlobalConfigValue("statsSorting");
        this._ignoreBannedPlayers = EventConfig.getInstance().getGlobalConfigBoolean("statsIgnoreBanned");
        this._ignoreGMs = EventConfig.getInstance().getGlobalConfigBoolean("statsIgnoreGMs");
        this._playersPerPage = EventConfig.getInstance().getGlobalConfigInt("statsPlayersPerPage");
        this._showDetailedPlayerInfo = EventConfig.getInstance().getGlobalConfigBoolean("statsDetailedPlayerInfo");
        this._showPkCount = EventConfig.getInstance().getGlobalConfigBoolean("statsShowPkCount");
    }
    
    public GlobalStatsModel getPlayerGlobalStatsCopy(final PlayerEventInfo player, final EventType type) {
        final GlobalStatsModel oldModel = this._playerGlobalStats.get(player).get(type);
        final Map<GlobalStatType, Integer> stats = new LinkedHashMap<GlobalStatType, Integer>();
        stats.putAll(oldModel._stats);
        final GlobalStatsModel newModel = new GlobalStatsModel(type, stats);
        return newModel;
    }
    
    public GlobalStatsModel getPlayerGlobalStats(final PlayerEventInfo player, final EventType type) {
        return this._playerGlobalStats.get(player).get(type);
    }
    
    public void setPlayerGlobalStats(final PlayerEventInfo player, final EventType type, final GlobalStatsModel stats) {
        this._playerGlobalStats.get(player).put(type, stats);
    }
    
    @Override
    public void load() {
        this._playerGlobalStats.clear();
        this._playerGlobalStatsHtml.clear();
        this.loadConfigs();
        this.loadGlobalStats();
        SunriseLoader.debug("Global statistics engine loaded.");
    }
    
    @Override
    public void onLogin(final PlayerEventInfo player) {
        this.loadPlayer(player);
    }
    
    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        this.forgetPlayerGlobalStats(player);
    }
    
    @Override
    public void statsChanged(final PlayerEventInfo player) {
        this._playerGlobalStatsHtml.remove(player);
    }
    
    @Override
    public void onCommand(final PlayerEventInfo player, final String command) {
        if (command.startsWith("oneplayer")) {
            PlayerEventInfo target = null;
            String name = null;
            String sortType = null;
            String page = null;
            final StringTokenizer st = new StringTokenizer(command);
            st.nextToken();
            if (st.hasMoreTokens()) {
                name = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                sortType = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                page = st.nextToken();
            }
            boolean backToCbMenu = false;
            if (name == null) {
                target = player;
            }
            else if (name.equals("cbmenu")) {
                target = player;
                backToCbMenu = true;
            }
            else {
                target = CallBack.getInstance().getOut().getPlayer(name);
            }
            if (target != null) {
                this.showPlayersGlobalStats(player, target, sortType, page, backToCbMenu);
            }
            else {
                player.screenMessage("This player is either offline or doesn't exist.", "Statistics", false);
                player.sendMessage("This player is either offline or doesn't exist.");
            }
        }
        else if (command.startsWith("topplayers")) {
            final String params = command.substring(11);
            this.showGlobalStats(player, params);
        }
    }
    
    private void showGlobalStats(final PlayerEventInfo player, final String params) {
        if (!this.globalStatsLoaded || !this._enableStatistics || !this._enableGlobalStatistics) {
            player.sendMessage("Statistics engine is turned off.");
            return;
        }
        final StringTokenizer st = new StringTokenizer(params);
        int page = Integer.parseInt(st.nextToken());
        if (page == 0) {
            page = 1;
        }
        final SortType sort = st.hasMoreTokens() ? SortType.valueOf(st.nextToken()) : null;
        boolean backToCbMenu = false;
        backToCbMenu = true;
        if (sort != null) {
            String text = null;
            try {
                text = this._globalStatsHtml.get(sort).get(page);
            }
            catch (Exception e) {
                if (player != null) {
                    player.sendMessage("Statistics engine will become functional as soon as some events are runned.");
                }
                return;
            }
            if (text != null) {
                text = this.updateStatuses(text, sort.toString(), page);
                if (backToCbMenu) {
                    text = text.replaceAll("%back%", "<button value=\"Back\" width=60 action=\"bypass nxs_showstats_cbmenu\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
                }
                else {
                    text = text.replaceAll("%back%", "");
                }
                this.showHtmlText(player, text);
            }
        }
    }
    
    private void showPlayersGlobalStats(final PlayerEventInfo player, final PlayerEventInfo target, final String sortType, final String page, final boolean backToCbMenu) {
        if (!this._enableStatistics) {
            player.sendMessage("Statistics engine is turned off.");
            return;
        }
        this.statsChanged(target);
        if (this._ignoreGMs && target.isGM() && !player.isGM()) {
            player.sendMessage("GM's stats are uber secret.");
            return;
        }
        String text = null;
        if (!this._playerGlobalStatsHtml.containsKey(target)) {
            text = this.generatePlayersGlobalStatsHtml(target);
        }
        else {
            text = this._playerGlobalStatsHtml.get(target);
        }
        text = this.addExtraData(text, sortType, page, backToCbMenu);
        if (text != null) {
            this.showHtmlText(player, text);
            player.sendStaticPacket();
        }
    }
    
    private String addExtraData(String text, final String sortType, final String page, final boolean backToCbMenu) {
        if (backToCbMenu) {
            text = text.replaceAll("%data%", "<button value=\"Back\" width=60 action=\"bypass nxs_showstats_cbmenu\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        }
        else if (sortType != null && page != null) {
            final int pageNumber = Integer.parseInt(page);
            text = text.replaceAll("%data%", "<button value=\"Back\" width=60 action=\"bypass nxs_showstats_global_topplayers " + pageNumber + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        }
        else {
            text = text.replaceAll("%data%", "");
        }
        return text;
    }
    
    private String generatePlayersGlobalStatsHtml(final PlayerEventInfo player) {
        final StringBuilder tb = new StringBuilder();
        tb.append("<html><body><br><center>");
        if (this._showDetailedPlayerInfo) {
            final GlobalStatsSum sum = this._data.get(player.getPlayersId());
            if (sum != null) {
                tb.append("<font color=ac9887>" + player.getPlayersName() + " </font><font color=9f9f9f>(Lvl " + player.getLevel() + " " + player.getClassName() + ")</font><br>");
                tb.append("<center><table width=430 bgcolor=2E2E2E>");
                final String clan = CallBack.getInstance().getOut().getClanName(player.getClanId());
                final String ally = CallBack.getInstance().getOut().getAllyName(player.getClanId());
                tb.append("<tr><td width=90><font color=B09D8E>Clan name:</font></td><td width=155 align=left><font color=A9A8A7>" + ((clan == null) ? "<font color=6f6f6f>No clan</font>" : clan) + "</font></td>");
                tb.append("<td width=80><font color=B09D8E>Ally name:</font></td><td width=120 align=left><font color=A9A8A7>" + ((ally == null) ? "<font color=6f6f6f>No ally</font>" : ally) + "</font></td></tr>");
                final String pvps = String.valueOf(player.getPvpKills());
                String pks = String.valueOf(player.getPkKills());
                if (!this._showPkCount) {
                    pks = "<font color=6f6f6f>-secret-</font>";
                }
                tb.append("<tr><td width=90><font color=B09D8E>PvP kills:</font></td><td width=155 align=left><font color=B3AA9D>" + pvps + "</font></td>");
                tb.append("<td width=80><font color=B09D8E>PK count:</font></td><td width=120 align=left><font color=B3AA9D>" + pks + "</font></td></tr>");
                tb.append("<tr></tr><tr><td width=90><font color=B09D8E>Won:</font></td><td width=155 align=left><font color=A9A8A7>" + sum.get(GlobalStatType.WINS) + " </font><font color=8f8f8f>events</font></td>");
                tb.append("<td width=80><font color=B09D8E>Lost:</font></td><td width=120 align=left><font color=A9A8A7>" + sum.get(GlobalStatType.LOSES) + " <font color=8f8f8f>events</font></td></tr>");
                tb.append("<tr><td width=86><font color=B09D8E>Participated:</font></td><td width=120 align=left><font color=A9A8A7><font color=8f8f8f>in</font> " + sum.get(GlobalStatType.COUNT_PLAYED) + " <font color=8f8f8f>events</font></td>");
                tb.append("<td width=80><font color=B09D8E>K:D ratio:</font></td><td width=155 align=left><font color=A9A8A7>" + sum.kdRatio + "</font></font></td></tr>");
                tb.append("<tr><td width=90><font color=B09D8E>Kills/Deaths:</font></td><td width=155 align=left><font color=A9A8A7>" + sum.get(GlobalStatType.KILLS) + " / " + sum.get(GlobalStatType.DEATHS) + "</font></font></td>");
                tb.append("<td width=80><font color=B09D8E>Score:</font></td><td width=120 align=left><font color=A9A8A7>" + sum.get(GlobalStatType.SCORE) + "</font></td></tr>");
                tb.append("</table><br><br><br>");
            }
        }
        tb.append("<font color=ac9887>" + player.getPlayersName() + "'s event statistics</font><br1>");
        tb.append("<font color=6f6f6f>(click on event for more info)</font><br>");
        tb.append("<table width=740 bgcolor=4E4E4E><tr> <td width=130><font color=B09D8E>Event</font></td><td width=90 align=center><font color=A9A8A7>Times played</font></td><td width=65 align=center><font color=A9A8A7>Win %</font></td><td width=65 align=center><font color=A9A8A7>K:D ratio</font></td><td width=65 align=center><font color=A9A8A7>Wins</font></td><td width=65 align=center><font color=A9A8A7>Loses</font></td><td width=65 align=center><font color=A9A8A7>Kills</font></td><td width=65 align=center><font color=A9A8A7>Deaths</font></td><td width=65 align=center><font color=A9A8A7>Score</font></td></tr></table>");
        tb.append("<img src=\"L2UI.SquareBlank\" width=740 height=6>");
        boolean bg = false;
        for (final EventType event : EventType.values()) {
            if (this._playerGlobalStats.get(player) == null) {
                tb.append("<table width=740><tr><td>Event data not available.</td></tr></table>");
                break;
            }
            final GlobalStatsModel stats = this._playerGlobalStats.get(player).get(event);
            if (stats != null) {
                final int kills = stats.get(GlobalStatType.KILLS);
                final int deaths = stats.get(GlobalStatType.DEATHS);
                final int timesPlayed = stats.get(GlobalStatType.COUNT_PLAYED);
                final int wins = stats.get(GlobalStatType.WINS);
                String kdRatio = String.valueOf((deaths == 0) ? kills : (kills / deaths));
                String success = String.valueOf((int)(wins / timesPlayed * 100.0));
                kdRatio = kdRatio.substring(0, Math.min(3, kdRatio.length()));
                success = success.substring(0, Math.min(5, success.length())) + "%";
                tb.append("<table width=740 bgcolor=" + (bg ? "3E3E3E" : "2E2E2E") + "><tr><td width=130><font color=B09D8E>" + event.getHtmlTitle() + "</font> </td><td width=90 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.COUNT_PLAYED) + "</font></td><td width=65 align=center><font color=B3AA9D>" + success + "</font></td><td width=65 align=center><font color=B3AA9D>" + kdRatio + "</font></td><td width=65 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.WINS) + "</font></td><td width=65 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.LOSES) + "</font></td><td width=65 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.KILLS) + "</font></td><td width=65 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.DEATHS) + "</font></td><td width=65 align=center><font color=B3AA9D>" + stats.get(GlobalStatType.SCORE) + "</font></td></tr>");
                tb.append("</table><img src=\"L2UI.SquareBlank\" width=740 height=3>");
                bg = !bg;
            }
        }
        tb.append("<br>%data%");
        tb.append("</center></body></html>");
        this._playerGlobalStatsHtml.put(player, tb.toString());
        return tb.toString();
    }
    
    private void loadPlayer(final PlayerEventInfo player) {
        if (!this._playerGlobalStats.containsKey(player)) {
            synchronized (this._playerGlobalStats) {
                this._playerGlobalStats.put(player, new LinkedHashMap<EventType, GlobalStatsModel>());
            }
        }
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("SELECT event, count_played, wins, loses, kills, deaths, score FROM sunrise_stats_global WHERE player = " + player.getPlayersId());
             final ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                final EventType type = EventType.getType(rset.getString("event"));
                if (type != null) {
                    final Map<GlobalStatType, Integer> map = new ConcurrentHashMap<GlobalStatType, Integer>();
                    map.put(GlobalStatType.COUNT_PLAYED, rset.getInt("count_played"));
                    map.put(GlobalStatType.WINS, rset.getInt("wins"));
                    map.put(GlobalStatType.LOSES, rset.getInt("loses"));
                    map.put(GlobalStatType.KILLS, rset.getInt("kills"));
                    map.put(GlobalStatType.DEATHS, rset.getInt("deaths"));
                    map.put(GlobalStatType.SCORE, rset.getInt("score"));
                    final GlobalStatsModel stats = new GlobalStatsModel(type, map);
                    try {
                        synchronized (this._playerGlobalStats) {
                            this._playerGlobalStats.get(player).put(type, stats);
                        }
                    }
                    catch (Exception e) {
                        try {
                            SunriseLoader.debug("An error occured while running GlobalStas.loadPlayer for player " + player.getPlayersName(), Level.WARNING);
                        }
                        catch (NullPointerException e3) {
                            SunriseLoader.debug("An error occured while running GlobalStas.loadPlayer, player is null", Level.WARNING);
                        }
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (SQLException e2) {
            e2.printStackTrace();
        }
    }
    
    public void updateGlobalStats(final Map<PlayerEventInfo, GlobalStatsModel> data) {
        try (final Connection con = CallBack.getInstance().getOut().getConnection()) {
            for (final Map.Entry<PlayerEventInfo, GlobalStatsModel> e : data.entrySet()) {
                this.statsChanged(e.getKey());
                GlobalStatsModel stats = this.getPlayerGlobalStats(e.getKey(), e.getValue().getEvent());
                if (stats == null) {
                    stats = e.getValue();
                    this.setPlayerGlobalStats(e.getKey(), stats.getEvent(), stats);
                }
                else {
                    stats.add(e.getValue());
                }
                try (final PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_stats_global VALUES (?,?,?,?,?,?,?,?,?)")) {
                    statement.setInt(1, e.getKey().getPlayersId());
                    statement.setString(2, stats.getEvent().getAltTitle());
                    statement.setInt(3, stats.get(GlobalStatType.COUNT_PLAYED));
                    statement.setInt(4, stats.get(GlobalStatType.WINS));
                    statement.setInt(5, stats.get(GlobalStatType.LOSES));
                    statement.setInt(6, stats.get(GlobalStatType.KILLS));
                    statement.setInt(7, stats.get(GlobalStatType.DEATHS));
                    statement.setInt(8, stats.get(GlobalStatType.SCORE));
                    statement.setString(9, stats.getFavoriteEvent());
                    statement.executeUpdate();
                }
            }
        }
        catch (SQLException e2) {
            e2.printStackTrace();
        }
    }
    
    private void forgetPlayerGlobalStats(final PlayerEventInfo player) {
        synchronized (this._playerGlobalStats) {
            this._playerGlobalStats.remove(player);
        }
    }
    
    protected String updateStatuses(final String text, final String sortType, final int page) {
        String updated = text;
        int start = 0;
        int end = 0;
        while (true) {
            start = updated.indexOf("<i>");
            if (start == -1) {
                break;
            }
            start += 3;
            end = updated.indexOf("</i>");
            final String name = updated.substring(start, end);
            final PlayerEventInfo player = CallBack.getInstance().getOut().getPlayer(name);
            if (player != null) {
                updated = updated.replaceFirst("<i>", "<font color=9EB39D><a action=\"bypass -h nxs_showstats_global_oneplayer " + name + " " + sortType + " " + page + "\">");
                updated = updated.replaceFirst("</i>", "</a></font>");
            }
            else {
                updated = updated.replaceFirst("<i>", "<font color=A9A8A7>");
                updated = updated.replaceFirst("</i>", "</font>");
            }
        }
        updated = updated.replaceAll("%reloaded%", this.calcLastLoadedTime());
        return updated;
    }
    
    protected void loadGlobalStats() {
        this.loadConfigs();
        if (!this._enableStatistics || !this._enableGlobalStatistics) {
            return;
        }
        this._globalStatsHtml = new ConcurrentHashMap<SortType, Map<Integer, String>>();
        final StringBuilder tb = new StringBuilder();
        String charName = null;
        final int playersPerPage = this._playersPerPage;
        String condition = "";
        if (this._ignoreGMs && this._ignoreBannedPlayers) {
            condition = "WHERE characters.accesslevel = 0";
        }
        else if (this._ignoreGMs) {
            condition = "WHERE characters.accesslevel <= 0";
        }
        else if (this._ignoreBannedPlayers) {
            condition = "WHERE characters.accesslevel >= 0";
        }
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("SELECT characters.char_name, characters.charId, characters.online, characters.level, characters.pvpkills, characters.pkkills, characters.clanid, characters.classid, sunrise_stats_global.* FROM sunrise_stats_global INNER JOIN characters ON characters.charId = sunrise_stats_global.player " + condition + " ORDER BY characters.char_name")) {
            this._data = new ConcurrentHashMap<Integer, GlobalStatsSum>();
            try (final ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    final int charId = rset.getInt("charId");
                    if (!this._data.containsKey(charId)) {
                        charName = rset.getString("char_name");
                        final int level = rset.getInt("level");
                        final int pvpkills = rset.getInt("pvpkills");
                        final int pkkills = rset.getInt("pkkills");
                        final int clanid = rset.getInt("clanid");
                        final int classid = rset.getInt("classid");
                        this._data.put(charId, new GlobalStatsSum(charName, level, pvpkills, pkkills, clanid, classid));
                    }
                    final int timesPlayed = rset.getInt("count_played");
                    if (timesPlayed > this._data.get(charId).mostPlayedCount) {
                        this._data.get(charId).mostPlayedCount = timesPlayed;
                        this._data.get(charId).mostPlayedEvent = EventType.getType(rset.getString("event"));
                    }
                    final int wins = rset.getInt("wins");
                    final int loses = rset.getInt("loses");
                    final int kills = rset.getInt("kills");
                    final int deaths = rset.getInt("deaths");
                    final int score = rset.getInt("score");
                    this._data.get(charId).raise(GlobalStatType.COUNT_PLAYED, timesPlayed);
                    this._data.get(charId).raise(GlobalStatType.WINS, wins);
                    this._data.get(charId).raise(GlobalStatType.LOSES, loses);
                    this._data.get(charId).raise(GlobalStatType.KILLS, kills);
                    this._data.get(charId).raise(GlobalStatType.DEATHS, deaths);
                    this._data.get(charId).raise(GlobalStatType.SCORE, score);
                }
            }
            int type = 1;
            if (this._statsSorting.equals("advanced")) {
                type = 2;
            }
            else if (this._statsSorting.equals("full")) {
                type = 3;
            }
            for (final SortType sortType : SortType.values()) {
                Label_2848: {
                    if (type == 1) {
                        if (sortType != SortType.NAME && sortType != SortType.LEVEL) {
                            break Label_2848;
                        }
                    }
                    else if (type == 2 && sortType != SortType.NAME && sortType != SortType.LEVEL && sortType != SortType.COUNTPLAYED && sortType != SortType.KDRATIO) {
                        break Label_2848;
                    }
                    final List<GlobalStatsSum> sorted = new LinkedList<GlobalStatsSum>();
                    sorted.addAll(this._data.values());
                    switch (sortType) {
                        case NAME: {
                            Collections.sort(sorted, (stats1, stats2) -> stats1._name.compareTo(stats2._name));
                            break;
                        }
                        case LEVEL: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int level2 = stats1._level;
                                int level3 = stats2._level;
                                return (level2 == level3) ? 0 : ((level2 < level3) ? 1 : -1);
                            });
                            break;
                        }
                        case WINS: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int wins2 = stats1.get(GlobalStatType.WINS);
                                int wins3 = stats2.get(GlobalStatType.WINS);
                                return (wins2 == wins3) ? 0 : ((wins2 < wins3) ? 1 : -1);
                            });
                            break;
                        }
                        case DEATHS: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int deaths2 = stats1.get(GlobalStatType.DEATHS);
                                int deaths3 = stats2.get(GlobalStatType.DEATHS);
                                return (deaths2 == deaths3) ? 0 : ((deaths2 < deaths3) ? 1 : -1);
                            });
                            break;
                        }
                        case SCORE: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int score2 = stats1.get(GlobalStatType.SCORE);
                                int score3 = stats2.get(GlobalStatType.SCORE);
                                return (score2 == score3) ? 0 : ((score2 < score3) ? 1 : -1);
                            });
                            break;
                        }
                        case COUNTPLAYED: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int count1 = stats1.get(GlobalStatType.COUNT_PLAYED);
                                int count2 = stats2.get(GlobalStatType.COUNT_PLAYED);
                                return (count1 == count2) ? 0 : ((count1 < count2) ? 1 : -1);
                            });
                            break;
                        }
                        case LOSES: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                int loses2 = stats1.get(GlobalStatType.LOSES);
                                int loses3 = stats2.get(GlobalStatType.LOSES);
                                return (loses2 == loses3) ? 0 : ((loses2 < loses3) ? 1 : -1);
                            });
                            break;
                        }
                        case KDRATIO: {
                            Collections.sort(sorted, (stats1, stats2) -> {
                                try {
                                    double ratio1 = Double.valueOf(stats1.kdRatio);
                                    double ratio2 = Double.valueOf(stats2.kdRatio);
                                    return (ratio1 == ratio2) ? 0 : ((ratio1 < ratio2) ? 1 : -1);
                                }
                                catch (Exception e2) {
                                    return 0;
                                }
                            });
                            break;
                        }
                    }
                    final int size = this._data.size();
                    int count3 = 0;
                    boolean bg = false;
                    for (final GlobalStatsSum stats3 : sorted) {
                        if (++count3 % playersPerPage == 1) {
                            tb.append("<html><body><br><center><font color=ac9887>Server event statistics</font><br1><font color=7f7f7f>(reloaded: %reloaded%)</font><br><br>");
                            tb.append("<table width=725><tr><td width=70 align=left><button value=\"Refresh\" width=70 action=\"bypass nxs_showstats_global_topplayers " + ((count3 - 1) / playersPerPage + 1) + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td><td width=500 align=center><font color=7f7f7f>(click on column name to sort the players)</font></td><td width=60><font color=ac9887>Name:</font></td><td width=100 align=left><edit var=\"name\" width=100 height=14></td><td width=65 align=right><button value=\"Find\" width=60 action=\"bypass nxs_showstats_global_oneplayer $name " + sortType.toString() + " " + ((count3 - 1) / playersPerPage + 1) + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr></table>");
                            if (this._statsSorting.equals("simple")) {
                                tb.append("<br><center><table width=747 bgcolor=5A5A5A><tr><td width=25 ><font color=B09D8E>&nbsp;#</font></td><td width=125><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.NAME.toString() + "\">Name</a>&nbsp;(<a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.LEVEL.toString() + "\">lvl</a>)</font></td><td width=55><font color=A9A8A7>Clan</font></td><td width=55><font color=A9A8A7>Played ev.</font></td><td width=50><center><font color=A9A8A7>&nbsp;Win%</font></td><td width=50><center><font color=A9A8A7>K:D</font></td><td width=50><center><font color=A9A8A7>Wins</font></td><td width=55><center><font color=A9A8A7>Loses</font></td><td width=55><center><font color=A9A8A7>Score</font></td><td width=55><center><font color=A9A8A7>Deaths</font></td><td width=95><center><font color=A9A8A7>Favorite Event</font></td></tr></table><br><center>");
                            }
                            else if (this._statsSorting.equals("advanced")) {
                                tb.append("<br><center><table width=747 bgcolor=5A5A5A><tr><td width=25 ><font color=B09D8E>&nbsp;#</font></td><td width=125><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.NAME.toString() + "\">Name</a>&nbsp;(<a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.LEVEL.toString() + "\">lvl</a>)</font></td><td width=55><font color=A9A8A7>Clan</font></td><td width=55><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.COUNTPLAYED.toString() + "\">Played ev.</a></font></td><td width=50><center><font color=A9A8A7>&nbsp;Win%</font></td><td width=50><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.KDRATIO.toString() + "\">K:D</a></font></td><td width=50><center><font color=A9A8A7>Wins</font></td><td width=55><center><font color=A9A8A7>Loses</font></td><td width=55><center><font color=A9A8A7>Score</font></td><td width=55><center><font color=A9A8A7>Deaths</font></td><td width=95><center><font color=A9A8A7>Favorite Event</font></td></tr></table><br><center>");
                            }
                            else if (this._statsSorting.equals("full")) {
                                tb.append("<br><center><table width=747 bgcolor=5A5A5A><tr><td width=25 ><font color=B09D8E>&nbsp;#</font></td><td width=125><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.NAME.toString() + "\">Name</a>&nbsp;(<a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.LEVEL.toString() + "\">lvl</a>)</font></td><td width=55><font color=A9A8A7>Clan</font></td><td width=55><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.COUNTPLAYED.toString() + "\">Played ev.</a></font></td><td width=50><center><font color=A9A8A7>&nbsp;Win%</font></td><td width=50><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.KDRATIO.toString() + "\">K:D</a></font></td><td width=50><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.WINS.toString() + "\">Wins</a></font></td><td width=55><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.LOSES.toString() + "\">Loses</a></font></td><td width=55><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.SCORE.toString() + "\">Score</a></font></td><td width=55><center><font color=A9A8A7><a action=\"bypass -h nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + SortType.DEATHS.toString() + "\">Deaths</a></font></td><td width=95><center><font color=A9A8A7>Favorite Event</font></td></tr></table><br><center>");
                            }
                        }
                        tb.append("<center><table width=740 " + (bg ? "bgcolor=3E3E3E" : "bgcolor=2E2E2E") + "><tr><td width=30 align=left><font color=B09D8E>&nbsp;" + count3 + ".</font></td>");
                        bg = !bg;
                        String clan = CallBack.getInstance().getOut().getClanName(stats3._clan);
                        if (clan == null) {
                            clan = "";
                        }
                        else if (clan.length() > 15) {
                            clan = clan.substring(0, 12) + "..";
                        }
                        tb.append("<td width=115><i>" + stats3._name + "</i> <font color=A9A8A7>(" + stats3._level + ")</font></td><td width=108 align=left><font color=B09D8E>" + clan + "</font></td>");
                        final int timesPlayed = stats3.get(GlobalStatType.COUNT_PLAYED);
                        final int wins = stats3.get(GlobalStatType.WINS);
                        final int loses = stats3.get(GlobalStatType.LOSES);
                        final int kills = stats3.get(GlobalStatType.KILLS);
                        final int deaths = stats3.get(GlobalStatType.DEATHS);
                        final int score = stats3.get(GlobalStatType.SCORE);
                        final String kdRatio = stats3.kdRatio;
                        String success = String.valueOf((int)(wins / (double)timesPlayed * 100.0));
                        success = success.substring(0, Math.min(5, success.length()));
                        tb.append("<td width=53 align=left><font color=B3AA9D>" + timesPlayed + "</font></td><td width=53><font color=B3AA9D>" + success + "%</font></td><td width=45><font color=B3AA9D>&nbsp;" + kdRatio + "</font></td><td width=57><center><font color=B3AA9D>" + wins + "</font></td><td width=55><center><font color=B3AA9D>" + loses + "</font></td><td width=55><center>&nbsp;&nbsp;<font color=B3AA9D>" + score + "</font></td><td width=55><center>&nbsp;&nbsp;&nbsp;<font color=B3AA9D>" + deaths + "</font></td><td width=120><center><font color=B3AA9D>" + ((stats3.mostPlayedEvent != null) ? stats3.mostPlayedEvent.getAltTitle() : "N/A") + "</font> <font color=7B7A79>(" + stats3.mostPlayedCount + "x)</font></td></tr></table><img src=\"L2UI.SquareBlank\" width=740 height=3>");
                        if (count3 % playersPerPage == 0) {
                            tb.append("<center><br><br><table width=140><tr><td width=70 align=left>" + (((count3 - 1) / playersPerPage != 0) ? ("<button value=\"Page " + (count3 - 1) / playersPerPage + "\" width=60 action=\"bypass nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">") : ("<font color=ac9887>Page " + ((count3 - 1) / playersPerPage + 1) + "</font>")) + "</td><td width=70 align=right>" + (((count3 - 1) / playersPerPage != size / playersPerPage) ? ("<button value=\"Page " + ((count3 - 1) / playersPerPage + 2) + "\" width=60 action=\"bypass nxs_showstats_global_topplayers " + ((count3 - 1) / playersPerPage + 2) + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">") : ("<font color=ac9887>Page " + ((count3 - 1) / playersPerPage + 2) + "</font>")) + "</td></tr></table>");
                            tb.append("<center><br>%back%</center></body></html>");
                            if (!this._globalStatsHtml.containsKey(sortType)) {
                                this._globalStatsHtml.put(sortType, new LinkedHashMap<Integer, String>());
                            }
                            this._globalStatsHtml.get(sortType).put(count3 / playersPerPage, tb.toString());
                            tb.setLength(0);
                        }
                    }
                    if (count3 % playersPerPage != 0) {
                        tb.append("<center><br><br><table width=140><tr><td width=70 align=left>" + (((count3 - 1) / playersPerPage != 0) ? ("<button value=\"Page " + (count3 - 1) / playersPerPage + "\" width=60 action=\"bypass nxs_showstats_global_topplayers " + (count3 - 1) / playersPerPage + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">") : ("<font color=ac9887>Page " + ((count3 - 1) / playersPerPage + 1) + "</font>")) + "</td><td width=70 align=right>" + (((count3 - 1) / playersPerPage != size / playersPerPage) ? ("<button value=\"Page " + ((count3 - 1) / playersPerPage + 2) + "\" width=60 action=\"bypass nxs_showstats_global_topplayers " + ((count3 - 1) / playersPerPage + 2) + " " + sortType.toString() + "\" height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">") : ("<font color=ac9887>Page " + ((count3 - 1) / playersPerPage + 1) + "</font>")) + "</td></tr></table>");
                        tb.append("<center><br>%back%</center></body></html>");
                        if (!this._globalStatsHtml.containsKey(sortType)) {
                            this._globalStatsHtml.put(sortType, new LinkedHashMap<Integer, String>());
                        }
                        if (!this._globalStatsHtml.get(sortType).containsKey(count3 / playersPerPage + 1)) {
                            this._globalStatsHtml.get(sortType).put(count3 / playersPerPage + 1, tb.toString());
                            tb.setLength(0);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.globalStatsLoaded = true;
        this._lastLoad = System.currentTimeMillis();
        SunriseLoader.debug("Global statistics reloaded.");
        this.scheduleReloadGlobalStats();
    }
    
    private String calcLastLoadedTime() {
        final long time = System.currentTimeMillis();
        final long diff = (time - this._lastLoad) / 1000L;
        if (diff > 3600L) {
            return diff / 3600L + " hours ago";
        }
        if (diff > 60L) {
            return diff / 60L + " minutes ago";
        }
        return diff + " seconds ago";
    }
    
    private synchronized void scheduleReloadGlobalStats() {
        if (this._globalStatsReload != null) {
            this._globalStatsReload.cancel(false);
            this._globalStatsReload = null;
        }
        this._globalStatsReload = CallBack.getInstance().getOut().scheduleGeneral(() -> this.loadGlobalStats(), this._statsRefresh * 1000);
    }
    
    private class GlobalStatsSum
    {
        protected Map<GlobalStatType, Integer> stats;
        protected String _name;
        protected int _level;
        protected int _clan;
        protected EventType mostPlayedEvent;
        protected int mostPlayedCount;
        protected String kdRatio;
        
        public GlobalStatsSum(final String name, final int level, final int pvp, final int pk, final int clan, final int classId) {
            this.stats = new ConcurrentHashMap<GlobalStatType, Integer>();
            this.mostPlayedCount = 0;
            this._name = name;
            this._level = level;
            this._clan = clan;
            this.stats.clear();
            for (final GlobalStatType t : GlobalStatType.values()) {
                this.stats.put(t, 0);
            }
        }
        
        public int get(final GlobalStatType type) {
            return this.stats.get(type);
        }
        
        public void set(final GlobalStatType type, final int value) {
            this.stats.put(type, value);
        }
        
        public void raise(final GlobalStatType type, final int value) {
            this.set(type, this.get(type) + value);
            if (type == GlobalStatType.KILLS || type == GlobalStatType.DEATHS) {
                this.updateKdRatio();
            }
        }
        
        private void updateKdRatio() {
            final int kills = this.get(GlobalStatType.KILLS);
            final int deaths = this.get(GlobalStatType.DEATHS);
            this.kdRatio = String.valueOf((deaths == 0) ? kills : (kills / deaths));
            this.kdRatio = this.kdRatio.substring(0, Math.min(3, this.kdRatio.length()));
        }
    }
    
    private enum SortType
    {
        NAME("characters.char_name"), 
        COUNTPLAYED("sunrise_stats_global.count_played DESC"), 
        WINS("sunrise_stats_global.wins DESC"), 
        LOSES("sunrise_stats_global.loses DESC"), 
        SCORE("sunrise_stats_global.score DESC"), 
        DEATHS("sunrise_stats_global.deaths DESC"), 
        LEVEL("characters.level DESC"), 
        KDRATIO("");
        
        private SortType(final String dbName) {
        }
    }
    
    public enum GlobalStatType
    {
        COUNT_PLAYED("count played"), 
        WINS("wins"), 
        LOSES("loses"), 
        KILLS("kills"), 
        DEATHS("deaths"), 
        SCORE("score");
        
        String _name;
        
        private GlobalStatType(final String name) {
            this._name = name;
        }
    }
}
