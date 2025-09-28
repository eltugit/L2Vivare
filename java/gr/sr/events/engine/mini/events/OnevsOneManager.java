package gr.sr.events.engine.mini.events;

import gr.sr.events.engine.EventMapSystem;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.events.engine.mini.features.*;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.l2j.CallBack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OnevsOneManager extends MiniEventManager
{
    private final int MAX_GAMES_COUNT = 10;
    protected int _lastMapIndex;
    private final List<RegistrationData> tempPlayers;
    protected static RewardPosition[] _rewardTypes;
    protected Map<SpawnType, String> _spawnTypes;
    
    public OnevsOneManager(final EventType type) {
        super(type);
        this.tempPlayers = new LinkedList<RegistrationData>();
        this._spawnTypes = new ConcurrentHashMap<SpawnType, String>();
        this._lastMapIndex = 0;
        this._spawnTypes.clear();
        this._spawnTypes.put(SpawnType.Regular, "Defines where the players will be spawned.");
        this._spawnTypes.put(SpawnType.Buffer, "Defines where the buffer(s) will be spawned.");
        this._spawnTypes.put(SpawnType.Fence, "Defines where fences will be spawned.");
        this.check();
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("TeamsAmmount", "2", "The count of fighting players in the event."));
        this.addConfig(new ConfigModel("RoundsAmmount", "3", "The count of rounds the event has."));
        this.addMapConfig(new ConfigModel("FirstRoundWaitDelay", "30000", "The delay the player has to wait when he is teleported to the map (first round). During this time, he will be preparing himself for the fight and getting buffs. In miliseconds."));
        this.addMapConfig(new ConfigModel("RoundWaitDelay", "20000", "The waiting delay for player to prepare for the match before all rounds' (except for the first round) start. In miliseconds."));
        this.addMapConfig(new ConfigModel("RootPlayers", "true", "Put 'true' if you want event to root all players on all rounds' start during the wait time (putting false is good for maps such as Coliseum, with closeable doors where the players can't leave the first place and attack directly the opponent before the fight itself starts, etc.)", ConfigModel.InputType.Boolean));
    }
    
    @Override
    public void run() {
        this.check();
    }
    
    @Override
    public void createGame() {
        if (this._locked) {
            return;
        }
        this.removeInactiveTeams();
        if (this._games.size() >= this.getMaxGamesCount()) {
            this.check();
            return;
        }
        int limit = 3;
        int iterateLimit = this._parties.size();
        final List<RegistrationData> tempData = new LinkedList<RegistrationData>();
        this.setIsTemporaryLocked(true);
        try {
            while (limit != 0 && iterateLimit != 0) {
                this.tempPlayers.clear();
                for (final RegistrationData player : this._parties) {
                    if (player.isChosen()) {
                        continue;
                    }
                    if (this.tempPlayers.isEmpty()) {
                        if (tempData.contains(player)) {
                            continue;
                        }
                        this.tempPlayers.add(player);
                        tempData.add(player);
                    }
                    else if (!this.tempPlayers.contains(player)) {
                        if (!this.strenghtChecks(player, this.tempPlayers.get(0)) || !this.ipChecks(player, this.tempPlayers.get(0))) {
                            continue;
                        }
                        this.tempPlayers.add(player);
                    }
                    if (this.tempPlayers.size() >= this.getTeamsCount()) {
                        for (final RegistrationData d : this.tempPlayers) {
                            d.setIsChosen(true);
                        }
                        this.launchGame(this.tempPlayers.toArray(new RegistrationData[this.tempPlayers.size()]), null);
                        --limit;
                        break;
                    }
                }
                --iterateLimit;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final RegistrationData p : this._parties) {
            if (p.isChosen()) {
                this._parties.remove(p);
            }
        }
        this.setIsTemporaryLocked(false);
        this.check();
    }
    
    @Override
    public boolean launchGame(final RegistrationData[] teams, EventMap map) {
        if (map == null) {
            map = EventMapSystem.getInstance().getNextMap(this, this._lastMapIndex, this.getMode());
        }
        if (map == null) {
            this.cleanMe(true);
            this._mode.setAllowed(false);
            OnevsOneManager._log.warning("No map available for event " + this.getEventType().getAltTitle() + " !!! Mode has been disabled.");
            return false;
        }
        this._lastMapIndex = EventMapSystem.getInstance().getMapIndex(this.getEventType(), map);
        this.getNextGameId();
        final OnevsOneGame game = new OnevsOneGame(this._lastGameId, map, this, teams);
        new Thread(game, this.getEventName() + " ID" + this._lastGameId).start();
        this._games.add(game);
        return true;
    }
    
    @Override
    public boolean registerTeam(final PlayerEventInfo player) {
        if (!super.registerTeam(player)) {
            return false;
        }
        final List<PlayerEventInfo> playersCollection = new LinkedList<PlayerEventInfo>();
        playersCollection.add(CallBack.getInstance().getPlayerBase().addInfo(player));
        final RegistrationData p = new RegistrationData(playersCollection);
        p.register(true, this);
        this.addParty(p);
        p.message(LanguageEngine.getMsg("registering_registered2", this.getEventName()), true);
        return true;
    }
    
    @Override
    public synchronized boolean unregisterTeam(final PlayerEventInfo player) {
        if (!super.unregisterTeam(player)) {
            return false;
        }
        for (final RegistrationData data : this._parties) {
            if (data.getKeyPlayer().getPlayersId() == player.getPlayersId()) {
                this.deleteTeam(data);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void notifyDisconnect(final PlayerEventInfo player) {
        for (final RegistrationData data : this._parties) {
            if (data.getKeyPlayer().getPlayersId() == player.getPlayersId()) {
                this.deleteTeam(data);
                break;
            }
        }
    }
    
    public List<PlayerEventInfo> getTeams() {
        final List<PlayerEventInfo> list = new LinkedList<PlayerEventInfo>();
        for (final RegistrationData player : this._parties) {
            list.add(player.getKeyPlayer());
        }
        return list;
    }
    
    @Override
    public boolean checkCanFight(final PlayerEventInfo gm, final RegistrationData[] team) {
        if (team.length != 2) {
            gm.sendMessage("2 teams are required.");
            return false;
        }
        if (team[0].getKeyPlayer() != null && team[1].getKeyPlayer() != null) {
            return true;
        }
        gm.sendMessage("No player available for one of the teams.");
        return false;
    }
    
    @Override
    protected int getStartGameInterval() {
        return 30000;
    }
    
    @Override
    public int getDefaultPartySizeToJoin() {
        return 1;
    }
    
    @Override
    public boolean requireParty() {
        return false;
    }
    
    @Override
    public int getMaxGamesCount() {
        return 10;
    }
    
    @Override
    public RewardPosition[] getRewardTypes() {
        return OnevsOneManager._rewardTypes;
    }
    
    @Override
    public Map<SpawnType, String> getAvailableSpawnTypes() {
        return this._spawnTypes;
    }
    
    @Override
    public int getTeamsCount() {
        for (final AbstractFeature feature : this.getMode().getFeatures()) {
            if (feature.getType() == EventMode.FeatureType.TeamsAmmount && ((TeamsAmmountFeature)feature).getTeamsAmmount() > 0) {
                return ((TeamsAmmountFeature)feature).getTeamsAmmount();
            }
        }
        return this.getInt("TeamsAmmount");
    }
    
    public int getRoundsAmmount() {
        for (final AbstractFeature feature : this.getMode().getFeatures()) {
            if (feature.getType() == EventMode.FeatureType.Rounds && ((RoundsFeature)feature).getRoundsAmmount() > 0) {
                return ((RoundsFeature)feature).getRoundsAmmount();
            }
        }
        return this.getInt("RoundsAmmount");
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            int roundsCount = this.getInt("RoundsAmmount");
            int teamsCount = this.getInt("TeamsAmmount");
            int rejoinDelay = this.getInt("DelayToWaitSinceLastMatchMs");
            int timeLimit = this.getInt("TimeLimitMs");
            for (final AbstractFeature feature : this.getMode().getFeatures()) {
                if (feature instanceof RoundsFeature) {
                    roundsCount = ((RoundsFeature)feature).getRoundsAmmount();
                }
                else if (feature instanceof TeamsAmmountFeature) {
                    teamsCount = ((TeamsAmmountFeature)feature).getTeamsAmmount();
                }
                else if (feature instanceof DelaysFeature) {
                    rejoinDelay = ((DelaysFeature)feature).getRejoinDealy();
                }
                else {
                    if (!(feature instanceof TimeLimitFeature)) {
                        continue;
                    }
                    timeLimit = ((TimeLimitFeature)feature).getTimeLimit();
                }
            }
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs(), roundsCount, teamsCount, 1, rejoinDelay, timeLimit);
            }
            else {
                this._htmlDescription = "This is a free-for-all mini event. ";
                this._htmlDescription = this._htmlDescription + "You will fight against " + (teamsCount - 1) + " enemy player" + ((teamsCount > 2) ? "s" : "") + " in a randomly chosen map. ";
                if (roundsCount > 1) {
                    this._htmlDescription = this._htmlDescription + "Each match has " + roundsCount + " rounds, the winner of round (the player, who kills all his opponents) receives  1 score. ";
                    this._htmlDescription += "The player, who has the biggest score in the end of all rounds, wins the match. ";
                }
                else {
                    this._htmlDescription += "This event has only one round. If you die, the event ends for you. ";
                    this._htmlDescription += "The winner of the match is the player, who kills all his opponents. ";
                }
                this._htmlDescription += "Your opponents will be selected automatically and don't worry, there's a protection, which will ensure that you will always fight only players whose level is similar to yours. ";
                this._htmlDescription = this._htmlDescription + "If the match doesn't end within " + timeLimit / 60000 + " minutes, it will be aborted automatically. ";
                this._htmlDescription = this._htmlDescription + "Also, after you visit this event, you will have to wait at least " + rejoinDelay / 60000 + " minutes to join this event again. ";
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    public String getMissingSpawns(final EventMap map) {
        final StringBuilder tb = new StringBuilder();
        for (int i = 0; i < this.getTeamsCount(); ++i) {
            if (!map.checkForSpawns(SpawnType.Regular, i + 1, 1)) {
                tb.append(this.addMissingSpawn(SpawnType.Regular, i + 1, 1));
            }
        }
        return tb.toString();
    }
    
    @Override
    protected String addMissingSpawn(final SpawnType type, final int team, final int count) {
        return "<font color=bfbfbf>" + this.getMode().getModeName() + " </font><font color=696969>mode</font> -> <font color=9f9f9f>No</font> <font color=B46F6B>" + type.toString().toUpperCase() + "</font> <font color=9f9f9f>spawn for player " + team + " count " + count + " (or more)</font><br1>";
    }
    
    static {
        OnevsOneManager._rewardTypes = new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie_TimeLimit, RewardPosition.Tie };
    }
}
