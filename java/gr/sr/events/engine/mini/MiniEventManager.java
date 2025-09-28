package gr.sr.events.engine.mini;

import gr.sr.events.Configurable;
import gr.sr.events.engine.*;
import gr.sr.events.engine.base.SpawnType;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.mini.features.AbstractFeature;
import gr.sr.events.engine.mini.features.DelaysFeature;
import gr.sr.events.engine.mini.features.StrenghtChecksFeature;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.PartyData;
import gr.sr.l2j.CallBack;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public abstract class MiniEventManager extends Event implements Runnable, Configurable
{
    protected static Logger _log;
    protected List<RegistrationData> _parties;
    protected int _lastGameId;
    protected List<MiniEventGame> _games;
    protected Map<Integer, Long> _loggedPlayers;
    protected boolean _locked;
    protected boolean _canRun;
    protected boolean _tournamentActive;
    protected EventMode _mode;
    private final Map<String, ConfigModel> _configs;
    private final Map<String, ConfigModel> _mapConfigs;
    private final List<String> _configCategories;
    protected String _htmlDescription;
    private final Comparator<RegistrationData> _compareByLevel;
    
    public MiniEventManager(final EventType type) {
        super(type);
        this._htmlDescription = null;
        this._compareByLevel = ((p1, p2) -> {
            int level1 = p1.getAverageLevel();
            int level2 = p2.getAverageLevel();
            return (level1 < level2) ? -1 : ((level1 == level2) ? 0 : 1);
        });
        this._tournamentActive = false;
        this._parties = new CopyOnWriteArrayList<RegistrationData>();
        this._games = new LinkedList<MiniEventGame>();
        this._loggedPlayers = new ConcurrentHashMap<Integer, Long>();
        this._mode = new EventMode(this.getEventType());
        this._configs = new ConcurrentHashMap<String, ConfigModel>();
        this._mapConfigs = new ConcurrentHashMap<String, ConfigModel>();
        this._configCategories = new LinkedList<String>();
        this.loadConfigs();
        this._lastGameId = 0;
        this._canRun = false;
    }
    
    @Override
    public void loadConfigs() {
        this.addConfig(new ConfigModel("DelayToWaitSinceLastMatchMs", "600000", "The delay the player has to wait to join this event again, after the his last event ended. In miliseconds."));
        this.addConfig(new ConfigModel("TimeLimitMs", "600000", "The delay after the match will be automatically aborted. In ms (miliseconds)."));
        this.addConfig(new ConfigModel("MaxLevelDifference", "5", "Maximum level difference between opponents in the event."));
        this.addConfig(new ConfigModel("MinLevelToJoin", "0", "Minimum level for players participating the event (playerLevel >= value)."));
        this.addConfig(new ConfigModel("MaxLevelToJoin", "100", "Maximum level for players participating the event (playerLevel <= value)."));
        this.addConfig(new ConfigModel("notAllowedSkills", "", "Put here skills that won't be available for use in this event <font color=7f7f7f>(write one skill's ID and click Add, to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("notAllowedItems", "", "Put here items that won't be available for use in this event <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("setOffensiveSkills", "", "Skills written here will be usable only on player's opponents/enemies (not teammates) during events. <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("setNotOffensiveSkills", "", "Skills written here will be usable only on player's teammates (not opponents/enemies) during events. <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list).", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("setNeutralSkills", "994", "Skills written here will be usable on both teammates and enemies Useful for example for skill Rush (ID 994), which is by default not offensive, and thus the engine doesn't allow the player to cast it on his opponent <font color=7f7f7f>(write one skill's ID and click Add; to remove the skill, simply click on it's ID in the list)</font>", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("allowPotions", "false", "Put false if you want to disable potions on this event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowSummons", "true", "Put false if you want to disable summons on this event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowPets", "true", "Put false if you want to disable pets on this event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowHealers", "true", "Put false if you want to disable healers/buffers on this event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("removeCubics", "false", "Put true to remove cubics upon teleportation to the event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("dualboxCheckForEnemies", "true", "If enabled, only players with different IPs can be enemies in this event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("maxPlayersPerIp", "1", "You can specify here how many players with the same IP are allowed to be in the event. Put -1 to disable this feature."));
        this.addConfig(new ConfigModel("removeBuffsOnStart", "true", "If 'true', all buffs will be removed from players on first teleport to the event.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("removeBuffsOnRespawn", "false", "If 'true', all buffs will be removed from players when they respawn (or when the next round starts).", ConfigModel.InputType.Boolean));
    }
    
    public abstract boolean checkCanFight(final PlayerEventInfo p0, final RegistrationData[] p1);
    
    public void check() {
        if (!this.checkCanRun()) {
            this.cleanMe(false);
            CallBack.getInstance().getOut().scheduleGeneral(() -> this.check(), 30000L);
            return;
        }
        if (this.getStartGameInterval() > 0) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> this.createGame(), this.getStartGameInterval());
        }
    }
    
    public boolean checkCanRun() {
        int workingMapsCount = 0;
        for (final EventMap map : EventMapSystem.getInstance().getMaps(this.getEventType()).values()) {
            if (!this._mode.getDisMaps().contains(map.getGlobalId()) && this.canRun(map)) {
                ++workingMapsCount;
            }
        }
        return this._canRun = (workingMapsCount > 0);
    }
    
    @Override
    public void run() {
        this.check();
    }
    
    public void createGame() {
    }
    
    protected RegistrationData findOpponent(final RegistrationData team) {
        for (final RegistrationData opponent : this._parties) {
            if (!opponent.isChosen() && opponent.getKeyPlayer().getPlayersId() != team.getKeyPlayer().getPlayersId() && this.strenghtChecks(team, opponent) && this.ipChecks(team, opponent)) {
                return opponent;
            }
        }
        return null;
    }
    
    public boolean launchGame(final RegistrationData[] teams, final EventMap map) {
        return false;
    }
    
    public void cleanMe(final boolean abortMatches) {
        this._locked = true;
        if (abortMatches) {
            for (final MiniEventGame game : this._games) {
                game.abortDueToError(LanguageEngine.getMsg("game_aborted"));
            }
        }
        for (final RegistrationData data : this._parties) {
            data.message(LanguageEngine.getMsg("game_unregistered", this.getEventName()), false);
            data.register(false, null);
        }
        this._games.clear();
        this._parties.clear();
        this._loggedPlayers.clear();
        this._locked = false;
    }
    
    protected int getStartGameInterval() {
        return 30000;
    }
    
    public int getDefaultPartySizeToJoin() {
        return 5;
    }
    
    protected int getNextGameId() {
        return ++this._lastGameId;
    }
    
    public int getJoinTimeRestriction() {
        for (final AbstractFeature f : this._mode.getFeatures()) {
            if (f.getType() == EventMode.FeatureType.Delays) {
                return ((DelaysFeature)f).getRejoinDealy();
            }
        }
        return this.getInt("DelayToWaitSinceLastMatchMs");
    }
    
    public boolean registerTeam(final PlayerEventInfo player) {
        if (player == null) {
            return false;
        }
        if (!EventManager.getInstance().canRegister(player)) {
            player.sendMessage(LanguageEngine.getMsg("registering_status"));
            return false;
        }
        if (player.isRegistered()) {
            player.sendMessage(LanguageEngine.getMsg("registering_alreadyRegistered"));
            return false;
        }
        final int i = EventWarnings.getInstance().getPoints(player);
        if (i >= EventWarnings.MAX_WARNINGS) {
            player.sendMessage(LanguageEngine.getMsg("registering_warningPoints", EventWarnings.MAX_WARNINGS, i));
            return false;
        }
        if (EventConfig.getInstance().getGlobalConfigBoolean("eventSchemeBuffer")) {
            if (!EventBuffer.getInstance().hasBuffs(player)) {
                player.sendMessage(LanguageEngine.getMsg("registering_buffs"));
            }
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "mini", this.getEventType().getAltTitle());
        }
        if (!this._mode.checkPlayer(player)) {
            player.sendMessage(LanguageEngine.getMsg("registering_notAllowed"));
            return false;
        }
        final int playerLevel = player.getLevel();
        final int maxLevel = this.getInt("MaxLevelToJoin");
        final int minLevel = this.getInt("MinLevelToJoin");
        if (playerLevel < minLevel || playerLevel > maxLevel) {
            if (playerLevel < minLevel) {
                player.sendMessage(LanguageEngine.getMsg("registering_lowLevel"));
            }
            else {
                player.sendMessage(LanguageEngine.getMsg("registering_highLevel"));
            }
            return false;
        }
        if (this.isTemporaryLocked()) {
            player.sendMessage("Try it again in few seconds. If this thing keeps showing up, then there's propably something fucked up with this event, contact a GameMaster for fix.");
            return false;
        }
        if (!this.timeChecks(player)) {
            player.sendMessage(LanguageEngine.getMsg("registering_timeCheckFailed"));
            return false;
        }
        if (!this.ipChecks2(player)) {
            return false;
        }
        if (this.requireParty()) {
            if (player.getParty() == null) {
                player.sendMessage("You must have a party to join the event.");
                return false;
            }
            if (player.getParty().getLeadersId() != player.getPlayersId()) {
                player.sendMessage(LanguageEngine.getMsg("registering_partyLeader"));
                return false;
            }
            if (player.getParty().getMemberCount() != this.getDefaultPartySizeToJoin()) {
                player.sendMessage(LanguageEngine.getMsg("registering_partyMembers", this.getDefaultPartySizeToJoin()));
                return false;
            }
            if (!this.checkPartyStatus(player.getParty())) {
                player.sendMessage(LanguageEngine.getMsg("registering_partyCantRegister"));
                return false;
            }
        }
        return true;
    }
    
    protected void addParty(final RegistrationData playerData) {
        synchronized (this._parties) {
            this._parties.add(playerData);
        }
    }
    
    public boolean unregisterTeam(final PlayerEventInfo player) {
        if (player == null || !player.isOnline()) {
            return false;
        }
        if (player.getRegisteredMiniEvent() == null || player.getRegisteredMiniEvent().getEventType() != this.getEventType()) {
            player.sendMessage(LanguageEngine.getMsg("unregistering_notRegistered"));
            return false;
        }
        if (this._locked) {
            player.sendMessage("Try it again in few seconds. If this thing keeps showing up, then there's propably something fucked up with this event, contact GameMaster for fix.");
            return false;
        }
        if (this.requireParty()) {
            if (player.getParty() == null) {
                player.sendMessage(LanguageEngine.getMsg("registering_noParty"));
                return false;
            }
            if (player.getParty().getLeadersId() != player.getPlayersId()) {
                player.sendMessage(LanguageEngine.getMsg("registering_partyLeader_unregister"));
                return false;
            }
        }
        return true;
    }
    
    public void deleteTeam(final RegistrationData team) {
        team.message(LanguageEngine.getMsg("unregistering_unregistered2", this.getEventType().getHtmlTitle()), false);
        team.register(false, null);
        synchronized (this._parties) {
            this._parties.remove(team);
        }
    }
    
    private boolean checkPartyStatus(final PartyData party) {
        final boolean buffs = EventConfig.getInstance().getGlobalConfigBoolean("eventSchemeBuffer");
        for (final PlayerEventInfo member : party.getPartyMembers()) {
            if (member != null) {
                if (member.isRegistered()) {
                    party.getLeader().sendMessage(LanguageEngine.getMsg("registering_party_memberAlreadyRegistered", member.getPlayersName()));
                    return false;
                }
                if (!this.timeChecks(member)) {
                    party.getLeader().sendMessage(LanguageEngine.getMsg("registering_party_timeCheckFail", member.getPlayersName()));
                    return false;
                }
                if (!this.allowHealers() && member.isPriest()) {
                    party.getLeader().sendMessage(LanguageEngine.getMsg("registering_party_noHealer"));
                    return false;
                }
                if (buffs) {
                    if (!EventBuffer.getInstance().hasBuffs(member)) {
                        member.sendMessage(LanguageEngine.getMsg("registering_buffs"));
                    }
                    EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(member, "mini", this.getEventType().getAltTitle());
                }
            }
        }
        return true;
    }
    
    protected boolean timeChecks(final PlayerEventInfo player) {
        final int delay = this.getJoinTimeRestriction();
        final int id = player.getPlayersId();
        final long time = System.currentTimeMillis();
        for (final Map.Entry<Integer, Long> e : this._loggedPlayers.entrySet()) {
            if (e.getKey() == id) {
                if (time - delay > e.getValue()) {
                    this._loggedPlayers.remove(e.getKey());
                    return true;
                }
                if (!player.isGM()) {
                    player.sendMessage(LanguageEngine.getMsg("registering_timeCheckFail", (e.getValue() + delay - time) / 60000L));
                    return false;
                }
                return true;
            }
        }
        return true;
    }
    
    public int getDelayHaveToWaitToJoinAgain(final PlayerEventInfo player) {
        final int delay = this.getJoinTimeRestriction();
        final int id = player.getPlayersId();
        final long time = System.currentTimeMillis();
        for (final Map.Entry<Integer, Long> e : this._loggedPlayers.entrySet()) {
            if (e.getKey() == id) {
                if (time - delay > e.getValue()) {
                    this._loggedPlayers.remove(e.getKey());
                    return 0;
                }
                return (int)(e.getValue() - (time - delay));
            }
        }
        return 0;
    }
    
    protected void removeInactiveTeams() {
        int playersAmmount = 0;
        for (final RegistrationData data : this._parties) {
            playersAmmount = 0;
            if (!data.getKeyPlayer().isOnline(true)) {
                this.deleteTeam(data);
            }
            else if (!this.checkPlayer(data.getKeyPlayer())) {
                this.deleteTeam(data);
            }
            else {
                if (!this.requireParty()) {
                    continue;
                }
                if (data.getParty() == null) {
                    this.deleteTeam(data);
                }
                else if (data.getParty().getMemberCount() > this.getDefaultPartySizeToJoin()) {
                    data.message(LanguageEngine.getMsg("unregistering_unregistered_partyBig", this.getEventType().getHtmlTitle()), false);
                    this.deleteTeam(data);
                }
                else {
                    for (final PlayerEventInfo pi : data.getPlayers()) {
                        if (!pi.isOnline(true)) {
                            data.getPlayers().remove(pi);
                        }
                        else if (!pi.isRegistered() || pi.getRegisteredMiniEvent().getEventType() != this.getEventType()) {
                            data.getPlayers().remove(pi);
                            data.getKeyPlayer().sendMessage(LanguageEngine.getMsg("unregistering_memberKicked_anotherEvent", pi.getPlayersName()));
                        }
                        else if (pi.getParty() == null || pi.getParty().getLeadersId() != data.getParty().getLeadersId()) {
                            data.getPlayers().remove(pi);
                            data.getKeyPlayer().sendMessage(LanguageEngine.getMsg("unregistering_memberKicked_leftParty", pi.getPlayersName()));
                        }
                        else {
                            if (!this.checkPlayer(pi)) {
                                data.getPlayers().remove(pi);
                                data.getKeyPlayer().sendMessage(LanguageEngine.getMsg("unregistering_memberKicked", pi.getPlayersName()));
                            }
                            ++playersAmmount;
                        }
                    }
                    if (playersAmmount >= this.getDefaultPartySizeToJoin() / 2) {
                        continue;
                    }
                    this.deleteTeam(data);
                }
            }
        }
    }
    
    private boolean checkPlayer(final PlayerEventInfo pi) {
        if (!EventManager.getInstance().canRegister(pi)) {
            pi.sendMessage(LanguageEngine.getMsg("unregistering_unregistered"));
            return false;
        }
        if (!this._mode.checkPlayer(pi)) {
            pi.sendMessage(LanguageEngine.getMsg("unregistering_unregistered"));
            return false;
        }
        if (!this.allowHealers() && pi.isPriest()) {
            pi.sendMessage(LanguageEngine.getMsg("unregistering_memberKicked"));
            return false;
        }
        return true;
    }
    
    protected boolean strenghtChecks(final RegistrationData t1, final RegistrationData t2) {
        for (final AbstractFeature feature : this.getMode().getFeatures()) {
            if (feature.getType() == EventMode.FeatureType.StrenghtChecks) {
                return ((StrenghtChecksFeature)feature).canFight(t1, t2);
            }
        }
        return Math.abs(t1.getAverageLevel() - t2.getAverageLevel()) <= this.getMaxLevelDifference();
    }
    
    protected boolean ipChecks(final RegistrationData p1, final RegistrationData p2) {
        if (this.getBoolean("dualboxCheckForEnemies")) {
            for (final PlayerEventInfo player : p1.getPlayers()) {
                if (player != null && player.isOnline() && !player.isGM()) {
                    for (final PlayerEventInfo player2 : p2.getPlayers()) {
                        if (player2 != null && player2.isOnline() && !player2.isGM() && isDualBox(player, player2)) {
                            if (p1.getPlayers().size() > 1) {
                                p1.message("Player " + player.getPlayersName() + " has the same IP as someone in " + p2.getKeyPlayer().getPlayersName() + "'s team.", false);
                                p2.message("Player " + player2.getPlayersName() + " has the same IP as someone in " + p1.getKeyPlayer().getPlayersName() + "'s team.", false);
                            }
                            else {
                                p1.message("Your IP appears to be same as " + p2.getKeyPlayer().getPlayersName() + "'s IP. You can't go against him.", false);
                                p2.message("Your IP appears to be same as " + p1.getKeyPlayer().getPlayersName() + "'s IP. You can't go against him.", false);
                            }
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean isDualBox(PlayerEventInfo player1, PlayerEventInfo player2) {
        try {
            String ip_net1 = player1.getOwner().getClient().getConnectionAddress().getHostAddress();
            String ip_net2 = player2.getOwner().getClient().getConnectionAddress().getHostAddress();
            String ip_pc1 = "";
            String ip_pc2 = "";
            int[][] trace1 = player1.getOwner().getClient().getTrace();
            for (int o = 0; o < (trace1[0]).length; o++) {
                ip_pc1 = ip_pc1 + trace1[0][o];
                if (o != (trace1[0]).length - 1) {
                    ip_pc1 = ip_pc1 + ".";
                }
            }
            int[][] trace2 = player2.getOwner().getClient().getTrace();
            for (int u = 0; u < (trace2[0]).length; u++) {
                ip_pc2 = ip_pc2 + trace2[0][u];
                if (u != (trace2[0]).length - 1) {
                    ip_pc2 = ip_pc2 + ".";
                }
            }
            if (ip_net1.equals(ip_net2) && ip_pc1.equals(ip_pc2)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    protected boolean ipChecks2(final PlayerEventInfo player) {
        final int i = this.getInt("maxPlayersPerIp");
        if (i == -1 || player.isGM()) {
            return true;
        }
        if (!player.isOnline(true)) {
            return false;
        }
        int occurences = 0;
        if (i <= 1) {
            for (final RegistrationData data : this._parties) {
                for (final PlayerEventInfo p : data.getPlayers()) {
                    if (isDualBox(player, p)) {
                        return false;
                    }
                }
            }
        }
        else {
            final String ip1 = player.getIp();
            if (ip1 == null) {
                return false;
            }
            for (final RegistrationData data2 : this._parties) {
                for (final PlayerEventInfo p2 : data2.getPlayers()) {
                    if (p2 != null && p2.isOnline() && ip1.equals(p2.getIp())) {
                        ++occurences;
                    }
                }
            }
        }
        if (occurences >= i) {
            player.sendMessage("There is already " + i + " players using your IP. You may not register. Try it again later.");
            return false;
        }
        return true;
    }
    
    public void logPlayer(final PlayerEventInfo pi, final int position) {
        long time = System.currentTimeMillis();
        int rejoin = this.getJoinTimeRestriction() / 60000;
        if (position > 1) {
            time -= this.getJoinTimeRestriction() / position;
            rejoin /= position;
        }
        this._loggedPlayers.put(pi.getPlayersId(), time);
        if (pi.isOnline()) {
            pi.sendMessage(LanguageEngine.getMsg("game_delayMsg", rejoin));
        }
    }
    
    public void notifyDisconnect(final PlayerEventInfo player) {
    }
    
    public EventMode getMode() {
        return this._mode;
    }
    
    public boolean isTemporaryLocked() {
        return this._locked;
    }
    
    public void setIsTemporaryLocked(final boolean b) {
        this._locked = b;
    }
    
    public final void notifyGameEnd(final MiniEventGame game) {
        this._games.remove(game);
    }
    
    public String getString(final String propName) {
        if (this._configs.containsKey(propName)) {
            final String value = this._configs.get(propName).getValue();
            return value;
        }
        this.debug("Wrong String config for event " + this.getEventType().getAltTitle() + ", name " + propName);
        return "";
    }
    
    public int getInt(final String propName) {
        if (this._configs.containsKey(propName)) {
            final int value = this._configs.get(propName).getValueInt();
            return value;
        }
        this.debug("Wrong int config for event " + this.getEventType().getAltTitle() + ", name " + propName);
        return 0;
    }
    
    public boolean getBoolean(final String propName) {
        if (this._configs.containsKey(propName)) {
            return this._configs.get(propName).getValueBoolean();
        }
        this.debug("Wrong boolean config for event " + this.getEventType().getAltTitle() + ", name " + propName);
        return false;
    }
    
    protected void addConfig(final ConfigModel model) {
        this._configs.put(model.getKey(), model);
    }
    
    protected void removeConfig(final String key) {
        this._configs.remove(key);
    }
    
    protected void addConfig(final String category, final ConfigModel model) {
        if (!this._configCategories.contains(category)) {
            this._configCategories.add(category);
        }
        this._configs.put(model.getKey(), model.setCategory(category));
    }
    
    protected void addMapConfig(final ConfigModel model) {
        this._mapConfigs.put(model.getKey(), model);
    }
    
    protected void removeMapConfigs() {
        this._mapConfigs.clear();
    }
    
    protected void removeConfigs() {
        this._configCategories.clear();
        this._configs.clear();
    }
    
    @Override
    public Map<String, ConfigModel> getConfigs() {
        return this._configs;
    }
    
    @Override
    public void clearConfigs() {
        this.removeConfigs();
        this.removeMapConfigs();
    }
    
    @Override
    public List<String> getCategories() {
        return this._configCategories;
    }
    
    @Override
    public void setConfig(final String key, final String value, final boolean addToValue) {
        if (!this._configs.containsKey(key)) {
            return;
        }
        if (!addToValue) {
            this._configs.get(key).setValue(value);
        }
        else {
            this._configs.get(key).addToValue(value);
        }
    }
    
    @Override
    public Map<String, ConfigModel> getMapConfigs() {
        return this._mapConfigs;
    }
    
    public boolean canRun() {
        return this._canRun;
    }
    
    @Override
    public boolean canRun(final EventMap map) {
        return this.getMissingSpawns(map).length() == 0;
    }
    
    protected String addMissingSpawn(final SpawnType type, final int team, final int count) {
        return "<font color=bfbfbf>" + this.getMode().getModeName() + " </font><font color=696969>mode</font> -> <font color=9f9f9f>No</font> <font color=B46F6B>" + type.toString().toUpperCase() + "</font> <font color=9f9f9f>spawn for team " + team + " " + ((team == 0) ? "(team doesn't matter)" : "") + " count " + count + " (or more)</font><br1>";
    }
    
    public String getMapConfig(final EventMap map, final String name) {
        return EventConfig.getInstance().getMapConfig(map, this.getEventType(), name);
    }
    
    public int getMapConfigInt(final EventMap map, final String name) {
        return EventConfig.getInstance().getMapConfigInt(map, this.getEventType(), name);
    }
    
    public boolean getMapConfigBoolean(final EventMap map, final String name) {
        return EventConfig.getInstance().getMapConfigBoolean(map, this.getEventType(), name);
    }
    
    protected int getMaxLevelDifference() {
        return this.getInt("MaxLevelDifference");
    }
    
    @Override
    public String getDescriptionForReward(final RewardPosition reward) {
        return null;
    }
    
    public boolean isTournamentActive() {
        return this._tournamentActive;
    }
    
    public void setTournamentActive(final boolean b) {
        this._tournamentActive = b;
    }
    
    public abstract String getHtmlDescription();
    
    public List<MiniEventGame> getActiveGames() {
        return this._games;
    }
    
    public int getRegisteredTeamsCount() {
        if (this._parties == null) {
            return 0;
        }
        return this._parties.size();
    }
    
    public List<RegistrationData> getRegistered() {
        return this._parties;
    }
    
    @Override
    public String getEventName() {
        return this.getEventType().getAltTitle();
    }
    
    public boolean requireParty() {
        return true;
    }
    
    public boolean allowTournament() {
        return true;
    }
    
    public int getMaxGamesCount() {
        return 99;
    }
    
    protected boolean allowHealers() {
        return this.getBoolean("allowHealers");
    }
    
    static {
        MiniEventManager._log = Logger.getLogger(MiniEventManager.class.getName());
    }
}
