package gr.sr.events.engine.mini.events;

import gr.sr.events.engine.EventMapSystem;
import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.EventMap;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.SpawnType;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.events.engine.mini.RegistrationData;
import gr.sr.events.engine.mini.features.*;
import gr.sr.interf.PlayerEventInfo;

import java.util.LinkedList;
import java.util.List;

public class MiniTvTManager extends OnevsOneManager
{
    private final int MAX_GAMES_COUNT = 3;
    
    public MiniTvTManager(final EventType type) {
        super(type);
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.removeConfig("TeamsAmmount");
        this.addConfig(new ConfigModel("TeamSize", "10", "The count of players in one team."));
    }
    
    @Override
    public void createGame() {
        if (this._locked) {
            return;
        }
        this.removeInactiveTeams();
        final int teamsSize = this.getPlayersInTeam() * 2;
        if (this._parties.size() < teamsSize || this._games.size() >= this.getMaxGamesCount()) {
            this.check();
            return;
        }
        final List<RegistrationData> players = new LinkedList<RegistrationData>();
        this.setIsTemporaryLocked(true);
        try {
            for (final RegistrationData team : this._parties) {
                if (team.isChosen()) {
                    continue;
                }
                if (players.size() >= teamsSize) {
                    continue;
                }
                team.setIsChosen(true);
                players.add(team);
                if (players.size() >= teamsSize) {
                    this.launchGame(players.toArray(new RegistrationData[players.size()]), null);
                    this.setIsTemporaryLocked(false);
                    break;
                }
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
    public boolean launchGame(final RegistrationData[] players, EventMap map) {
        if (map == null) {
            map = EventMapSystem.getInstance().getNextMap(this, this._lastMapIndex, this.getMode());
        }
        if (map == null) {
            this.cleanMe(true);
            this._mode.setAllowed(false);
            MiniTvTManager._log.warning("No map available for event " + this.getEventType().getAltTitle() + " !!! Mode has been disabled.");
            return false;
        }
        this._lastMapIndex = EventMapSystem.getInstance().getMapIndex(this.getEventType(), map);
        this.getNextGameId();
        final MiniTvTGame game = new MiniTvTGame(this._lastGameId, map, this, players);
        new Thread(game, this.getEventName() + " ID" + this._lastGameId).start();
        this._games.add(game);
        return true;
    }
    
    @Override
    public boolean checkCanFight(final PlayerEventInfo gm, final RegistrationData[] team) {
        if (team.length != 2) {
            gm.sendMessage("2 teams are required.");
            return false;
        }
        return true;
    }
    
    public int getPlayersInTeam() {
        for (final AbstractFeature feature : this.getMode().getFeatures()) {
            if (feature.getType() == EventMode.FeatureType.TeamSize && ((TeamSizeFeature)feature).getTeamSize() > 0) {
                return ((TeamSizeFeature)feature).getTeamSize();
            }
        }
        return this.getInt("TeamSize");
    }
    
    @Override
    public int getTeamsCount() {
        return 2;
    }
    
    @Override
    protected int getStartGameInterval() {
        return 10000;
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
        return 3;
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            int roundsCount = this.getInt("RoundsAmmount");
            int teamsCount = this.getInt("TeamsAmmount");
            int teamSize = this.getInt("TeamSize");
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
                    teamSize = ((TeamSizeFeature)feature).getTeamSize();
                }
            }
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs(), roundsCount, teamsCount, teamSize, rejoinDelay, timeLimit);
            }
            else {
                this._htmlDescription = "This is a team-based mini event. This event is similar to Party fights, but you don't need any party here - ";
                this._htmlDescription = this._htmlDescription + " the event will automatically put you to one of " + teamsCount + " teams, which will fight against each other. Each team has " + teamSize + " players.<br1> ";
                if (roundsCount > 1) {
                    if (teamsCount == 2) {
                        this._htmlDescription = this._htmlDescription + "The match has " + roundsCount + " rounds. Round ends when all players from one team are dead (they will be resurrected in start of the next round). ";
                        this._htmlDescription += "The winner of the match is in the end of all rounds the team, which won the most of rounds. ";
                    }
                    else {
                        this._htmlDescription = this._htmlDescription + "The match has " + roundsCount + " rounds. Round ends when one team kills all it's opponents (dead players will be resurrected in start of the next round). ";
                        this._htmlDescription += "The winner of the match is in the end of all rounds the team, which won the most of rounds. ";
                    }
                }
                else if (teamsCount == 2) {
                    this._htmlDescription += "This event has only one round. If you die, the event ends for you. ";
                    this._htmlDescription += "The match ends when all players of one team are dead. ";
                }
                else {
                    this._htmlDescription += "This event has only one round. If you die, the event ends for you. ";
                    this._htmlDescription += "The winner of the match is the team, who kills all it's opponents. ";
                }
                this._htmlDescription += "Your opponents will be selected automatically and don't worry, there's a protection, which will ensure that you will always fight only players whose level is similar to yours. ";
                this._htmlDescription = this._htmlDescription + "If the match doesn't end within " + timeLimit / 60000 + " minutes, it will be aborted automatically. ";
                this._htmlDescription = this._htmlDescription + "Also, after you visit this event, you will have to wait at least " + rejoinDelay / 60000 + " minutes to join this event again. ";
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    protected String addMissingSpawn(final SpawnType type, final int team, final int count) {
        return "<font color=bfbfbf>" + this.getMode().getModeName() + " </font><font color=696969>mode</font> -> <font color=9f9f9f>No</font> <font color=B46F6B>" + type.toString().toUpperCase() + "</font> <font color=9f9f9f>spawn for team " + team + " count " + count + " (or more)</font><br1>";
    }
}
