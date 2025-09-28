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

public class PartyvsPartyManager extends MiniEventManager
{
    protected int _lastMapIndex;
    private final List<RegistrationData> tempTeams;
    private final int MAX_GAMES_COUNT = 3;
    private final Map<SpawnType, String> _spawnTypes;
    private static RewardPosition[] _rewardTypes;
    
    public PartyvsPartyManager(final EventType type) {
        super(type);
        this.tempTeams = new LinkedList<RegistrationData>();
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
        this.addConfig(new ConfigModel("PartySize", "6", "The exact size of registered party. If the party has lower or higher # of players, it won't be able to join."));
        this.addConfig(new ConfigModel("TeamsAmmount", "2", "The count of teams (parties) fighting in the event."));
        this.addConfig(new ConfigModel("RoundsAmmount", "3", "The count of rounds the event has."));
        this.addMapConfig(new ConfigModel("FirstRoundWaitDelay", "30000", "The delay the players has to wait when he is teleported to the map (first round). During this time, he will be preparing himself for the fight and getting buffs. In miliseconds."));
        this.addMapConfig(new ConfigModel("RoundWaitDelay", "20000", "The waiting delay for players to prepare for the match before all rounds' (except for the first round) start. In miliseconds."));
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
        int iterateLimit = this._parties.size();
        int limit = 1;
        final List<RegistrationData> tempData = new LinkedList<RegistrationData>();
        this.setIsTemporaryLocked(true);
        try {
            while (limit != 0 && iterateLimit != 0) {
                this.tempTeams.clear();
                for (final RegistrationData team : this._parties) {
                    if (team.isChosen()) {
                        continue;
                    }
                    if (this.tempTeams.isEmpty()) {
                        if (tempData.contains(team)) {
                            continue;
                        }
                        this.tempTeams.add(team);
                        tempData.add(team);
                    }
                    else if (!this.tempTeams.contains(team)) {
                        if (!this.strenghtChecks(team, this.tempTeams.get(0)) || !this.ipChecks(team, this.tempTeams.get(0))) {
                            continue;
                        }
                        this.tempTeams.add(team);
                    }
                    if (this.tempTeams.size() >= this.getTeamsCount()) {
                        for (final RegistrationData d : this.tempTeams) {
                            d.setIsChosen(true);
                        }
                        this.launchGame(this.tempTeams.toArray(new RegistrationData[this.tempTeams.size()]), null);
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
            PartyvsPartyManager._log.warning("No map available for event " + this.getEventType().getAltTitle() + " !!! Mode has been disabled.");
            return false;
        }
        this._lastMapIndex = EventMapSystem.getInstance().getMapIndex(this.getEventType(), map);
        this.getNextGameId();
        final PartyvsPartyGame game = new PartyvsPartyGame(this._lastGameId, map, this, teams);
        new Thread(game, this.getEventName() + " ID" + this._lastGameId).start();
        this._games.add(game);
        return true;
    }
    
    @Override
    public boolean registerTeam(final PlayerEventInfo player) {
        if (!super.registerTeam(player)) {
            return false;
        }
        final List<PlayerEventInfo> partyPlayers = new LinkedList<PlayerEventInfo>();
        for (final PlayerEventInfo p : player.getParty().getPartyMembers()) {
            if (p != null) {
                CallBack.getInstance().getPlayerBase().addInfo(p);
                partyPlayers.add(p);
            }
        }
        final RegistrationData regData = new RegistrationData(partyPlayers);
        regData.register(true, this);
        regData.message(LanguageEngine.getMsg("registering_registered2", this.getEventName()), true);
        this.addParty(regData);
        return true;
    }
    
    @Override
    public synchronized boolean unregisterTeam(final PlayerEventInfo player) {
        if (!super.unregisterTeam(player)) {
            return false;
        }
        for (final RegistrationData t : this._parties) {
            if (t.getKeyPlayer().getPlayersId() == player.getPlayersId()) {
                this.deleteTeam(t);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean checkCanFight(final PlayerEventInfo gm, final RegistrationData[] teams) {
        if (teams.length != 2) {
            gm.sendMessage("2 teams are required.");
            return false;
        }
        if (teams[0].getPlayers().size() < this.getDefaultPartySizeToJoin() / 2 || teams[1].getPlayers().size() < this.getDefaultPartySizeToJoin() / 2) {
            gm.sendMessage("Not enought players in one of the teams, minimal # of players registered is " + this.getDefaultPartySizeToJoin() / 2 + ".");
            return false;
        }
        return true;
    }
    
    @Override
    protected int getStartGameInterval() {
        return 30000;
    }
    
    @Override
    public int getDefaultPartySizeToJoin() {
        for (final AbstractFeature feature : this.getMode().getFeatures()) {
            if (feature.getType() == EventMode.FeatureType.TeamSize && ((TeamSizeFeature)feature).getTeamSize() > 0) {
                return ((TeamSizeFeature)feature).getTeamSize();
            }
        }
        return this.getInt("PartySize");
    }
    
    @Override
    public boolean requireParty() {
        return true;
    }
    
    @Override
    public int getMaxGamesCount() {
        return 3;
    }
    
    @Override
    public RewardPosition[] getRewardTypes() {
        return PartyvsPartyManager._rewardTypes;
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
            int partySize = this.getInt("PartySize");
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
                else if (feature instanceof TimeLimitFeature) {
                    timeLimit = ((TimeLimitFeature)feature).getTimeLimit();
                }
                else {
                    if (!(feature instanceof TeamSizeFeature)) {
                        continue;
                    }
                    partySize = ((TeamSizeFeature)feature).getTeamSize();
                }
            }
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs(), roundsCount, teamsCount, partySize, rejoinDelay, timeLimit);
            }
            else {
                this._htmlDescription = "This is a team-based mini event. You need a party of exactly " + partySize + " players (and be the party leader) to register. ";
                this._htmlDescription = this._htmlDescription + "You will fight against " + (teamsCount - 1) + " enemy part" + ((teamsCount > 2) ? "ies" : "y") + " in a randomly chosen map. ";
                if (roundsCount > 1) {
                    this._htmlDescription = this._htmlDescription + "Each match has " + roundsCount + " rounds, the winner of round (the party, who kills all it's opponents) receives  1 score. ";
                    this._htmlDescription += "The party, who has the biggest score in the end of all rounds, wins the match. ";
                }
                else {
                    this._htmlDescription += "This match has only one round. If you die, you can get revived only by your party-mate. ";
                    this._htmlDescription += "The winner of the match is the party, who kills all it's opponents. ";
                }
                this._htmlDescription += "Your opponent(s) will be selected automatically and don't worry, there's a protection, which will ensure that you will always fight only players whose level is similar to yours. ";
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
    
    static {
        PartyvsPartyManager._rewardTypes = new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie_TimeLimit, RewardPosition.Tie };
    }
}
