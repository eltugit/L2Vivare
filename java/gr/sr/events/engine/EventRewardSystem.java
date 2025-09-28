package gr.sr.events.engine;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.RewardPosition;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class EventRewardSystem
{
    private final Map<EventType, Map<Integer, EventRewards>> _rewards;
    private int count;
    private int notEnoughtScore;

    public EventRewardSystem() {
        this._rewards = new ConcurrentHashMap<EventType, Map<Integer, EventRewards>>();
        this.count = 0;
        this.notEnoughtScore = 0;
        for (final EventType t : EventType.values()) {
            this._rewards.put(t, new LinkedHashMap<Integer, EventRewards>());
        }
        this.loadRewards();
    }

    private EventType getType(final String s) {
        for (final EventType t : EventType.values()) {
            if (t.getAltTitle().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }

    public EventRewards getAllRewardsFor(final EventType event, final int modeId) {
        if (this._rewards.get(event).get(modeId) == null) {
            this._rewards.get(event).put(modeId, new EventRewards());
        }
        final EventRewards er = this._rewards.get(event).get(modeId);
        return er;
    }

    public void loadRewards() {
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("SELECT eventType, modeId, position, parameter, item_id, min, max, chance FROM sunrise_rewards");
             final ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                EventRewards rewards = null;
                final EventType type = this.getType(rset.getString("eventType"));
                final int modeId = rset.getInt("modeId");
                if (!this._rewards.get(type).containsKey(modeId)) {
                    rewards = new EventRewards();
                    this._rewards.get(type).put(modeId, rewards);
                }
                else {
                    rewards = this._rewards.get(type).get(modeId);
                }
                rewards.addItem(RewardPosition.getPosition(rset.getString("position")), rset.getString("parameter"), rset.getInt("item_id"), rset.getInt("min"), rset.getInt("max"), rset.getInt("chance"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        SunriseLoader.debug("Reward System Loaded.");
    }

    public int addRewardToDb(final EventType type, final RewardPosition position, final String parameter, final int modeId, final int id, final int minAmmount, final int maxAmmount, final int chance, final boolean updateOnly) {
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        final EventRewards rewards = this._rewards.get(type).get(modeId);
        int newId = 0;
        if (!updateOnly) {
            newId = rewards.addItem(position, parameter, id, minAmmount, maxAmmount, chance);
        }
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("REPLACE INTO sunrise_rewards VALUES (?,?,?,?,?,?,?,?)")) {
            statement.setString(1, type.getAltTitle());
            statement.setInt(2, modeId);
            statement.setString(3, position.toString());
            statement.setString(4, (parameter == null) ? "" : parameter);
            statement.setInt(5, id);
            statement.setInt(6, minAmmount);
            statement.setInt(7, maxAmmount);
            statement.setInt(8, chance);
            statement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return newId;
    }

    public int createReward(final EventType type, final RewardPosition position, final String parameter, final int modeId) {
        return this.addRewardToDb(type, position, parameter, modeId, 57, 1, 1, 100, false);
    }

    public boolean setPositionRewarded(final EventType type, final int modeId, final RewardPosition position, final String parameter) {
        if (this._rewards.get(type).get(modeId) == null) {
            return false;
        }
        if (this._rewards.get(type).get(modeId).getContainer(position, parameter) != null) {
            return false;
        }
        this._rewards.get(type).get(modeId).getOrCreateContainer(position, parameter);
        return true;
    }

    public boolean removePositionRewarded(final EventType type, final int modeId, final RewardPosition position, final String parameter) {
        if (this._rewards.get(type).get(modeId) == null) {
            return false;
        }
        if (this._rewards.get(type).get(modeId).getContainer(position, parameter) == null) {
            return false;
        }
        final PositionContainer container = this._rewards.get(type).get(modeId).getContainer(position, parameter);
        final Map<Integer, RewardItem> map = this._rewards.get(type).get(modeId).getAllRewards().get(container);
        for (final Map.Entry<Integer, RewardItem> e : map.entrySet()) {
            this.removeRewardFromDb(type, e.getKey(), modeId);
        }
        this._rewards.get(type).get(modeId).getAllRewards().remove(container);
        return true;
    }

    public void updateRewardInDb(final EventType type, final int rewardId, final int modeId) {
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        final EventRewards rewards = this._rewards.get(type).get(modeId);
        final RewardItem item = rewards.getItem(rewardId);
        if (item == null) {
            return;
        }
        final PositionContainer position = this.getRewardPosition(type, modeId, rewardId);
        this.addRewardToDb(type, position._position, position._parameter, modeId, item._id, item._minAmmount, item._maxAmmount, item._chance, true);
    }

    public void removeFromDb(final EventType type, final RewardPosition position, final String parameter, final int modeId, final int itemId, final int min, final int max, final int chance) {
        try (final Connection con = CallBack.getInstance().getOut().getConnection();
             final PreparedStatement statement = con.prepareStatement("DELETE FROM sunrise_rewards WHERE eventType = '" + type.getAltTitle() + "' AND position = '" + position.toString() + "' AND parameter = '" + ((parameter == null) ? "" : parameter) + "' AND modeId = " + modeId + " AND item_id = " + itemId + " AND min = " + min + " AND max = " + max + " AND chance = " + chance)) {
            statement.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeRewardFromDb(final EventType type, final int rewardId, final int modeId) {
        final PositionContainer container = this.getRewardPosition(type, modeId, rewardId);
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        final EventRewards rewards = this._rewards.get(type).get(modeId);
        final RewardItem item = rewards.getItem(rewardId);
        rewards.removeItem(container._position, container._parameter, rewardId);
        this.removeFromDb(type, container._position, container._parameter, modeId, item._id, item._minAmmount, item._maxAmmount, item._chance);
    }

    public Map<Integer, RewardItem> getRewards(final EventType type, final int modeId, final RewardPosition position, final String parameter) {
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        final Map<Integer, RewardItem> map = this._rewards.get(type).get(modeId).getRewards(position, parameter);
        if (map != null) {
            return map;
        }
        return new LinkedHashMap<Integer, RewardItem>();
    }

    public RewardItem getReward(final EventType type, final int modeId, final int rewardId) {
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        return this._rewards.get(type).get(modeId).getItem(rewardId);
    }

    public PositionContainer getRewardPosition(final EventType type, final int modeId, final int rewardId) {
        if (this._rewards.get(type).get(modeId) == null) {
            this._rewards.get(type).put(modeId, new EventRewards());
        }
        for (final Map.Entry<PositionContainer, Map<Integer, RewardItem>> e : this._rewards.get(type).get(modeId)._rewards.entrySet()) {
            final Map.Entry<PositionContainer, Map<Integer, RewardItem>> entry = e;
            for (final Integer element : e.getValue().keySet()) {
                final int i = element;
                if (i == rewardId) {
                    return e.getKey();
                }
            }
        }
        return new PositionContainer(RewardPosition.None, null);
    }

    public Map<Integer, List<EventTeam>> rewardTeams(final Map<EventTeam, Integer> teams, final EventType event, final int modeId, final int minScore, final int halfRewardAfkTime, final int noRewardAfkTime) {
        this.count = 0;
        this.notEnoughtScore = 0;
        final int totalCount = teams.size();
        final Map<Integer, List<EventTeam>> scores = new LinkedHashMap<Integer, List<EventTeam>>();
        for (final Map.Entry<EventTeam, Integer> e : teams.entrySet()) {
            final EventTeam team = e.getKey();
            final int score = e.getValue();
            if (!scores.containsKey(score)) {
                scores.put(score, new LinkedList<EventTeam>());
            }
            scores.get(score).add(team);
        }
        int position = 1;
        for (final Map.Entry<Integer, List<EventTeam>> e2 : scores.entrySet()) {
            final int score = e2.getKey();
            final int count = e2.getValue().size();
            if (position == 1) {
                if (count == 1) {
                    PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, "1");
                    if (temp != null) {
                        this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsRangeReward(event, modeId, position);
                        if (temp != null) {
                            this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                        else {
                            temp = this.existsReward(event, modeId, RewardPosition.Winner, null);
                            if (temp != null) {
                                this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                            }
                        }
                    }
                }
                else if (totalCount > count) {
                    PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, "1");
                    if (temp != null) {
                        this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsRangeReward(event, modeId, position);
                        if (temp != null) {
                            this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                        else {
                            temp = this.existsReward(event, modeId, RewardPosition.Winner, null);
                            if (temp != null) {
                                this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                            }
                        }
                    }
                }
                else {
                    final PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Tie, null);
                    if (temp != null) {
                        this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                }
            }
            else {
                PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, String.valueOf(position));
                if (temp != null) {
                    this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                }
                else {
                    temp = this.existsRangeReward(event, modeId, position);
                    if (temp != null) {
                        this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsReward(event, modeId, RewardPosition.Looser, null);
                        if (temp != null) {
                            this.giveRewardsToTeams(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                    }
                }
            }
            ++position;
        }
        try {
            if (event.isRegularEvent()) {
                final AbstractMainEvent ev = EventManager.getInstance().getMainEvent(event);
                if (ev != null) {
                    this.dump(ev.getPlayers(0).size());
                }
            }
        }
        catch (Exception ex) {}
        return scores;
    }

    private void dump(final int total) {
        SunriseLoader.debug(total + " was the count of players in the event.");
        SunriseLoader.debug(this.count + " players were rewarded.");
        SunriseLoader.debug(this.notEnoughtScore + " players were not rewarded because they didn't have enought score.");
        if (SunriseLoader.detailedDebug) {
            SunriseLoader.detailedDebug(total + " was the count of players in the event.");
        }
        if (SunriseLoader.detailedDebug) {
            SunriseLoader.detailedDebug(this.count + " players were rewarded.");
        }
        if (SunriseLoader.detailedDebug) {
            SunriseLoader.detailedDebug(this.notEnoughtScore + " players were not rewarded because they didn't have enought score.");
        }
    }

    public Map<Integer, List<PlayerEventInfo>> rewardPlayers(final Map<PlayerEventInfo, Integer> players, final EventType event, final int modeId, final int minScore, final int halfRewardAfkTime, final int noRewardAfkTime) {
        this.count = 0;
        this.notEnoughtScore = 0;
        final int totalCount = players.size();
        final Map<Integer, List<PlayerEventInfo>> scores = new LinkedHashMap<Integer, List<PlayerEventInfo>>();
        for (final Map.Entry<PlayerEventInfo, Integer> e : players.entrySet()) {
            final PlayerEventInfo player = e.getKey();
            final int score = e.getValue();
            if (!scores.containsKey(score)) {
                scores.put(score, new LinkedList<PlayerEventInfo>());
            }
            scores.get(score).add(player);
        }
        int position = 1;
        for (final Map.Entry<Integer, List<PlayerEventInfo>> e2 : scores.entrySet()) {
            final int score = e2.getKey();
            final int count = e2.getValue().size();
            if (position == 1) {
                if (count == 1) {
                    PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, "1");
                    if (temp != null) {
                        this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsRangeReward(event, modeId, position);
                        if (temp != null) {
                            this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                        else {
                            temp = this.existsReward(event, modeId, RewardPosition.Winner, null);
                            if (temp != null) {
                                this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                            }
                        }
                    }
                }
                else if (totalCount > count) {
                    PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, "1");
                    if (temp != null) {
                        this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsRangeReward(event, modeId, position);
                        if (temp != null) {
                            this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                        else {
                            temp = this.existsReward(event, modeId, RewardPosition.Winner, null);
                            if (temp != null) {
                                this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                            }
                        }
                    }
                }
                else {
                    final PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Tie, null);
                    if (temp != null) {
                        this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                }
            }
            else {
                PositionContainer temp = this.existsReward(event, modeId, RewardPosition.Numbered, String.valueOf(position));
                if (temp != null) {
                    this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                }
                else {
                    temp = this.existsRangeReward(event, modeId, position);
                    if (temp != null) {
                        this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        temp = this.existsReward(event, modeId, RewardPosition.Looser, null);
                        if (temp != null) {
                            this.giveRewardsToPlayers(temp, e2.getValue(), event, modeId, minScore, halfRewardAfkTime, noRewardAfkTime);
                        }
                    }
                }
            }
            ++position;
        }
        return scores;
    }

    private void giveRewardsToPlayers(final PositionContainer container, final List<PlayerEventInfo> players, final EventType event, final int modeId, final int minScore, final int halfRewardAfkTime, final int noRewardAfkTime) {
        for (final PlayerEventInfo player : players) {
            if (player.isOnline()) {
                if (player.getEventData().getScore() >= minScore) {
                    ++this.count;
                    this.rewardPlayer(event, modeId, player, container._position, container._parameter, player.getTotalTimeAfk(), halfRewardAfkTime, noRewardAfkTime);
                }
                else {
                    ++this.notEnoughtScore;
                    if (minScore <= 0 || player.getScore() >= minScore) {
                        continue;
                    }
                    player.sendMessage(LanguageEngine.getMsg("event_notEnoughtScore", minScore));
                }
            }
            else {
                SunriseLoader.debug("trying to reward player " + player.getPlayersName() + " (player) which is not online()", Level.WARNING);
            }
        }
    }

    private void giveRewardsToTeams(final PositionContainer container, final List<EventTeam> teams, final EventType event, final int modeId, final int minScore, final int halfRewardAfkTime, final int noRewardAfkTime) {
        for (final EventTeam team : teams) {
            for (final PlayerEventInfo player : team.getPlayers()) {
                if (player.isOnline()) {
                    if (player.getEventData().getScore() >= minScore) {
                        ++this.count;
                        this.rewardPlayer(event, modeId, player, container._position, container._parameter, player.getTotalTimeAfk(), halfRewardAfkTime, noRewardAfkTime);
                    }
                    else {
                        ++this.notEnoughtScore;
                        if (minScore <= 0 || player.getScore() >= minScore) {
                            continue;
                        }
                        player.sendMessage(LanguageEngine.getMsg("event_notEnoughtScore", minScore));
                    }
                }
                else {
                    SunriseLoader.debug("trying to reward player " + player.getPlayersName() + " (team) which is not online()", Level.WARNING);
                }
            }
        }
    }

    private PositionContainer existsReward(final EventType event, final int modeId, final RewardPosition pos, final String parameter) {
        if (this._rewards.get(event).get(modeId) == null) {
            return null;
        }
        final PositionContainer c = this._rewards.get(event).get(modeId).getContainer(pos, parameter);
        if (c == null || this._rewards.get(event).get(modeId).getAllRewards().get(c).isEmpty()) {
            return null;
        }
        return c;
    }

    private PositionContainer existsRangeReward(final EventType event, final int modeId, final int position) {
        if (this._rewards.get(event).get(modeId) == null) {
            return null;
        }
        for (final Map.Entry<PositionContainer, Map<Integer, RewardItem>> e : this._rewards.get(event).get(modeId).getAllRewards().entrySet()) {
            if (e.getValue() != null && !e.getValue().isEmpty() && e.getKey()._position.posType != null && e.getKey()._position.posType == RewardPosition.PositionType.Range) {
                final int from = Integer.parseInt(e.getKey()._parameter.split("-")[0]);
                final int to = Integer.parseInt(e.getKey()._parameter.split("-")[1]);
                if (position >= from && position <= to) {
                    return e.getKey();
                }
                continue;
            }
        }
        return null;
    }

    public boolean rewardPlayer(final EventType event, final int modeId, final PlayerEventInfo player, final RewardPosition position, final String parameter, final int afkTime, final int halfRewardAfkTime, final int noRewardAfkTime) {
        if (player == null) {
            return false;
        }
        if (this._rewards.get(event).get(modeId) == null) {
            this._rewards.get(event).put(modeId, new EventRewards());
        }
        if (this._rewards.get(event).get(modeId).getRewards(position, parameter) == null) {
            return false;
        }
        if (noRewardAfkTime > 0 && afkTime >= noRewardAfkTime) {
            player.sendMessage("You receive no reward because you were afk too much.");
            return false;
        }
        if (halfRewardAfkTime > 0 && afkTime >= halfRewardAfkTime) {
            player.sendMessage("You receive half reward because you were afk too much.");
        }
        boolean given = false;
        for (final RewardItem item : this._rewards.get(event).get(modeId).getRewards(position, parameter).values()) {
            int ammount = item.getAmmount(player);
            if (ammount > 0) {
                if (ammount > 1 && halfRewardAfkTime > 0 && afkTime >= halfRewardAfkTime) {
                    ammount /= 2;
                }
                if (item._id == -1) {
                    player.addExpAndSp(ammount, 0);
                }
                else if (item._id == -2) {
                    player.addExpAndSp(0L, ammount);
                }
                else if (item._id == -3) {
                    player.setFame(player.getFame() + ammount);
                }
                else {
                    player.addItem(item._id, ammount, true);
                }
                given = true;
            }
        }
        return given;
    }

    public static final EventRewardSystem getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final EventRewardSystem _instance;

        static {
            _instance = new EventRewardSystem();
        }
    }

    public class RewardItem
    {
        public int _id;
        public int _minAmmount;
        public int _maxAmmount;
        public int _chance;
        public int _pvpRequired;
        public int _levelRequired;

        public RewardItem(final int id, final int minAmmount, final int maxAmmount, final int chance, final int pvpRequired, final int levelRequired) {
            this._id = id;
            this._minAmmount = minAmmount;
            this._maxAmmount = maxAmmount;
            this._chance = chance;
            this._pvpRequired = pvpRequired;
            this._levelRequired = levelRequired;
        }

        public int getAmmount(final PlayerEventInfo player) {
            if (CallBack.getInstance().getOut().random(100) < this._chance) {
                return CallBack.getInstance().getOut().random(this._minAmmount, this._maxAmmount);
            }
            SunriseLoader.debug("chance check for reward failed for player " + player.getPlayersName() + ", reward item " + this._id);
            return 0;
        }
    }

    public class EventRewards
    {
        private int _lastId;
        protected final Map<PositionContainer, Map<Integer, RewardItem>> _rewards;

        public EventRewards() {
            this._lastId = 0;
            this._rewards = new ConcurrentHashMap<PositionContainer, Map<Integer, RewardItem>>();
        }

        public PositionContainer getOrCreateContainer(final RewardPosition position, final String posParameter) {
            PositionContainer container = null;
            container = this.getContainer(position, posParameter);
            if (container == null) {
                container = new PositionContainer(position, posParameter);
            }
            if (!this._rewards.containsKey(container)) {
                this._rewards.put(container, new LinkedHashMap<Integer, RewardItem>());
            }
            return container;
        }

        public int addItem(final RewardPosition position, String posParameter, final int id, final int minAmmount, final int maxAmmount, final int chance) {
            if (position == null) {
                SunriseLoader.debug("Null RewardPosition for item ID " + id + ", minAmmount " + minAmmount + " maxAmmount " + maxAmmount + " chance " + chance, Level.WARNING);
                return this._lastId++;
            }
            if ("".equals(posParameter)) {
                posParameter = null;
            }
            final PositionContainer container = this.getOrCreateContainer(position, posParameter);
            ++this._lastId;
            final RewardItem item = new RewardItem(id, minAmmount, maxAmmount, chance, 0, 0);
            this._rewards.get(container).put(this._lastId, item);
            return this._lastId;
        }

        public PositionContainer getContainer(final RewardPosition position, final String parameter) {
            for (final PositionContainer ps : this._rewards.keySet()) {
                if (ps._position != null && ps._position.toString().equals(position.toString()) && (parameter == null || parameter.equals("null") || parameter.equals(ps._parameter))) {
                    return ps;
                }
            }
            return null;
        }

        public void removeItem(final RewardPosition position, final String parameter, final int rewardId) {
            final PositionContainer ps = this.getContainer(position, parameter);
            if (ps != null && this._rewards.containsKey(ps)) {
                this._rewards.get(ps).remove(rewardId);
            }
        }

        public Map<Integer, RewardItem> getRewards(final RewardPosition position, final String parameter) {
            final PositionContainer ps = this.getContainer(position, parameter);
            if (ps != null) {
                return this._rewards.get(ps);
            }
            return null;
        }

        public Map<PositionContainer, Map<Integer, RewardItem>> getAllRewards() {
            return this._rewards;
        }

        public RewardItem getItem(final int rewardId) {
            for (final Map<Integer, RewardItem> i : this._rewards.values()) {
                for (final Map.Entry<Integer, RewardItem> e : i.entrySet()) {
                    if (e.getKey() == rewardId) {
                        return e.getValue();
                    }
                }
            }
            return null;
        }
    }

    public class PositionContainer
    {
        public RewardPosition _position;
        public String _parameter;
        public boolean _rewarded;

        PositionContainer(final RewardPosition position, final String parameter) {
            this._position = position;
            this._parameter = parameter;
        }

        public void setRewarded(final boolean b) {
            this._rewarded = b;
        }

        public boolean isRewarded() {
            return this._rewarded;
        }
    }
}


