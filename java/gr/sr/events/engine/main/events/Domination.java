package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.delegate.NpcData;
import gr.sr.interf.delegate.PartyData;
import gr.sr.l2j.CallBack;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Domination extends AbstractMainEvent
{
    protected Map<Integer, DominationEventInstance> _matches;
    protected boolean _waweRespawn;
    protected int _teamsCount;
    protected int _zoneNpcId;
    protected int _zoneRadius;
    protected int _zoneCheckInterval;
    protected int _scoreForCapturingZone;
    private int _holdZoneFor;
    protected int _percentMajorityToScore;
    protected int _tick;
    
    public Domination(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, DominationEventInstance>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("scoreForReward", "0", "The minimum score required to get a reward (includes all possible rewards). Score in this event is gained by standing near the zone, if the player wasn't afk, he should always have some score."));
        this.addConfig(new ConfigModel("killsForReward", "0", "The minimum kills count required to get a reward (includes all possible rewards)."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which the player is resurrected. In seconds."));
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system."));
        this.addConfig(new ConfigModel("zoneNpcId", "8992", "The ID of NPC that symbolizes the zone."));
        this.addConfig(new ConfigModel("zoneRadius", "180", "The radius of zone to count players inside."));
        this.addConfig(new ConfigModel("allowZoneNpcEffects", "true", "Enables Zone NPC's special effects, if blue or red team owns it. Due to client limitations, this will only work if the event has 2 teams.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowFireworkOnScore", "true", "Enables Zone NPC's small firework effect, when a team scores. Working only if <font color=LEVEL>holdZoneFor</font> is higher than 5 (to prevent spamming this skill).", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowPlayerEffects", "true", "Enables special effects for players from the team owning the zone and standing near the Zone NPC (in <font color=LEVEL>zoneRadius</font>). Only works if the event has 2 teams.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("zoneCheckInterval", "1", "In seconds. The time after it checks and counts players near the zone(s) and adds score to the team, that has more players inside the zone. Setting this to 1 is usually good (higher values make this event less expensive for cpu :)"));
        this.addConfig(new ConfigModel("scoreForCapturingZone", "1", "The ammount of points team gets each <font color=LEVEL>scoreCheckInterval</font> seconds if owns the zone."));
        this.addConfig(new ConfigModel("holdZoneFor", "0", "In seconds. The team needs to own this zone for this time to get <font color=LEVEL>scoreForCapturingZone</font> points. "));
        this.addConfig(new ConfigModel("percentMajorityToScore", "50", "In percent. In order to score a point, the team must have more players near the zone NPC in <font color=LEVEL>zoneRadius</font> radius, than the other team(s). The ammount of players from the scoring team must be higher than the ammount of players from the other teams by this percent value. Put 100 to make that all other team(s)' players in <font color=LEVEL>zoneRadius</font> must be dead to score; or put 0 to make that it will give score to the team that has more players and not care about any percent counting (eg. if team A has 15 players and team B has 16, it will simply reward team B)."));
        this.addConfig(new ConfigModel("createParties", "true", "Put 'True' if you want this event to automatically create parties for players in each team."));
        this.addConfig(new ConfigModel("maxPartySize", "9", "The maximum size of party, that can be created. Works only if <font color=LEVEL>createParties</font> is true."));
        this.addConfig(new ConfigModel("teamsCount", "2", "The ammount of teams in the event. Max is 5. <font color=FF0000>In order to change the count of teams in the event, you must also edit this config in the Instance's configuration.</font>"));
        this.addConfig(new ConfigModel("firstBloodMessage", "true", "You can turn off/on the first blood announce in the event (first kill made in the event). This is also rewardable - check out reward type FirstBlood.", ConfigModel.InputType.Boolean));
        this.addInstanceTypeConfig(new ConfigModel("teamsCount", "2", "You may specify the count of teams only for this instance. This config overrides events default teams count."));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._waweRespawn = this.getBoolean("waweRespawn");
        if (this._waweRespawn) {
            this.initWaweRespawns(this.getInt("resDelay"));
        }
        this._zoneNpcId = this.getInt("zoneNpcId");
        this._zoneRadius = (int)Math.pow(this.getInt("zoneRadius"), 2.0);
        this._zoneCheckInterval = this.getInt("zoneCheckInterval");
        this._holdZoneFor = this.getInt("holdZoneFor");
        this._scoreForCapturingZone = this.getInt("scoreForCapturingZone");
        this._percentMajorityToScore = this.getInt("percentMajorityToScore");
        this._runningInstances = 0;
        this._tick = 0;
    }
    
    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this._teamsCount = type.getConfigInt("teamsCount");
        if (this._teamsCount < 2 || this._teamsCount > 5) {
            this._teamsCount = this.getInt("teamsCount");
        }
        if (this._teamsCount < 2 || this._teamsCount > 5) {
            this._teamsCount = 2;
        }
        this.createTeams(this._teamsCount, type.getInstance().getId());
        return this._teamsCount;
    }
    
    @Override
    public void runEvent() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: started runEvent()");
        }
        if (!this.dividePlayers()) {
            this.clearEvent();
            return;
        }
        this._matches.clear();
        for (final InstanceData instance : this._instances) {
            if (SunriseLoader.detailedDebug) {
                this.print("Event: creating eventinstance for instance " + instance.getId());
            }
            final DominationEventInstance match = this.createEventInstance(instance);
            this._matches.put(instance.getId(), match);
            ++this._runningInstances;
            match.scheduleNextTask(0);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: event instance started");
            }
        }
        if (SunriseLoader.detailedDebug) {
            this.print("Event: finished runEvent()");
        }
    }
    
    protected void spawnZone(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: spawning Zone for instance " + instanceId);
        }
        this.clearMapHistory(-1, SpawnType.Zone);
        final EventSpawn sp = this.getSpawn(SpawnType.Zone, -1);
        final NpcData zone = this.spawnNPC(sp.getLoc().getX(), sp.getLoc().getY(), sp.getLoc().getZ(), this._zoneNpcId, instanceId, "Zone", "Domination event");
        final int radius = sp.getRadius();
        if (radius > 0) {
            this._zoneRadius = (int)Math.pow(radius, 2.0);
        }
        this.getEventData(instanceId).addZone(zone, this._zoneRadius);
        this.getEventData(instanceId)._zone.getNpc().setTitle("No owner");
        this.getEventData(instanceId)._zone.getNpc().broadcastNpcInfo();
    }
    
    protected void unspawnZone(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: unspawning zone for instance " + instanceId);
        }
        if (this.getEventData(instanceId)._zone != null) {
            this.getEventData(instanceId)._zone.deleteMe();
            if (SunriseLoader.detailedDebug) {
                this.print("Event: zone is not null and was deleted");
            }
        }
        else if (SunriseLoader.detailedDebug) {
            this.print("Event: ... zone is already null!!!");
        }
    }
    
    protected void setZoneEffects(final int teamId, final NpcData zoneNpc) {
        if (this.getBoolean("allowZoneNpcEffects") && this._teamsCount == 2) {
            if (teamId == 1) {
                zoneNpc.stopAbnormalEffect(4);
                zoneNpc.startAbnormalEffect(2097152);
            }
            else if (teamId == 2) {
                zoneNpc.stopAbnormalEffect(2097152);
                zoneNpc.startAbnormalEffect(4);
            }
            else {
                zoneNpc.stopAbnormalEffect(4);
                zoneNpc.stopAbnormalEffect(2097152);
            }
        }
    }
    
    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minKills = this.getInt("killsForReward");
        final int minScore = this.getInt("scoreForReward");
        this.rewardAllTeams(-1, minScore, minKills);
    }
    
    @Override
    protected synchronized boolean instanceEnded() {
        --this._runningInstances;
        if (SunriseLoader.detailedDebug) {
            this.print("Event: notifying instance ended: runningInstances = " + this._runningInstances);
        }
        if (this._runningInstances == 0) {
            this._manager.end();
            return true;
        }
        return false;
    }
    
    @Override
    protected synchronized void endInstance(final int instance, final boolean canBeAborted, final boolean canRewardIfAborted, final boolean forceNotReward) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: endInstance() " + instance + ", canBeAborted " + canBeAborted + ", canReward.. " + canRewardIfAborted + " forceNotReward " + forceNotReward);
        }
        if (forceNotReward) {
            this._matches.get(instance).forceNotRewardThisInstance();
        }
        this._matches.get(instance).setNextState(EventState.END);
        if (canBeAborted) {
            this._matches.get(instance).setCanBeAborted();
        }
        if (canRewardIfAborted) {
            this._matches.get(instance).setCanRewardIfAborted();
        }
        this._matches.get(instance).scheduleNextTask(0);
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final int count = this._teams.get(instance).size();
        final StringBuilder tb = new StringBuilder();
        for (final EventTeam team : this._teams.get(instance).values()) {
            if (count <= 4) {
                tb.append(team.getTeamName() + ": " + team.getScore() + "  ");
            }
            else {
                tb.append(team.getTeamName().substring(0, 1) + ": " + team.getScore() + "  ");
            }
        }
        if (count <= 3) {
            tb.append(LanguageEngine.getMsg("event_scorebar_time", this._matches.get(instance).getClock().getTime()));
        }
        return tb.toString();
    }
    
    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (this._hideTitles) {
            return "";
        }
        if (pi.isAfk()) {
            return "AFK";
        }
        return "Score: " + this.getPlayerData(pi).getScore();
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        if (player.getTeamId() != target.getEventInfo().getTeamId()) {
            this.tryFirstBlood(player);
            this.giveOnKillReward(player);
            player.getEventTeam().raiseKills(1);
            this.getPlayerData(player).raiseKills(1);
            this.getPlayerData(player).raiseSpree(1);
            this.giveKillingSpreeReward(this.getPlayerData(player));
            if (player.isTitleUpdated()) {
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
            this.setKillsStats(player, this.getPlayerData(player).getKills());
        }
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: onDie - player " + player.getPlayersName() + " (instance " + player.getInstanceId() + "), killer " + killer.getName());
        }
        this.getPlayerData(player).raiseDeaths(1);
        this.getPlayerData(player).setSpree(0);
        this.setDeathsStats(player, this.getPlayerData(player).getDeaths());
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
        if (this._waweRespawn) {
            this._waweScheduler.addPlayer(player);
        }
        else {
            this.scheduleRevive(player, this.getInt("resDelay") * 1000);
        }
    }
    
    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new PvPEventPlayerData(player, this, new GlobalStatsModel(this.getEventType()));
        return d;
    }
    
    @Override
    public PvPEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (PvPEventPlayerData)player.getEventData();
    }
    
    @Override
    public synchronized void clearEvent(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called CLEAREVENT for instance " + instanceId);
        }
        try {
            if (this._matches != null) {
                for (final DominationEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        match.abort();
                        this.unspawnZone(match.getInstance().getId());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (player.isOnline()) {
                if (player.isParalyzed()) {
                    player.setIsParalyzed(false);
                }
                if (player.isImmobilized()) {
                    player.unroot();
                }
                if (!player.isGM()) {
                    player.setIsInvul(false);
                }
                player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
                player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                player.removeRadarAllMarkers();
                player.setInstanceId(0);
                if (this._removeBuffsOnEnd) {
                    player.removeBuffs();
                }
                player.restoreData();
                player.teleport(player.getOrigLoc(), 0, true, 0);
                player.sendMessage(LanguageEngine.getMsg("event_teleportBack"));
                if (player.getParty() != null) {
                    final PartyData party = player.getParty();
                    party.removePartyMember(player);
                }
                player.broadcastUserInfo();
            }
        }
        this.clearPlayers(true, instanceId);
    }
    
    @Override
    public synchronized void clearEvent() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called global clearEvent()");
        }
        this.clearEvent(0);
    }
    
    @Override
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: respawning player " + pi.getPlayersName() + ", instance " + instance);
        }
        final EventSpawn spawn = this.getSpawn(SpawnType.Regular, pi.getTeamId());
        if (spawn != null) {
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            pi.teleport(loc, 0, true, instance);
            pi.sendMessage(LanguageEngine.getMsg("event_respawned"));
        }
        else {
            this.debug("Error on respawnPlayer - no spawn type REGULAR, team " + pi.getTeamId() + " has been found. Event aborted.");
        }
    }
    
    @Override
    protected void clockTick() {
        ++this._tick;
        if (this._tick % this._zoneCheckInterval != 0) {
            return;
        }
        final Map<Integer, List<PlayerEventInfo>> players = new LinkedHashMap<Integer, List<PlayerEventInfo>>(this._teamsCount);
        for (final DominationEventInstance match : this._matches.values()) {
            final int instanceId = match.getInstance().getId();
            final int zoneX = this.getEventData(instanceId)._zone.getLoc().getX();
            final int zoneY = this.getEventData(instanceId)._zone.getLoc().getY();
            final int zoneZ = this.getEventData(instanceId)._zone.getLoc().getZ();
            for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                if (player.getDistanceSq(zoneX, zoneY, zoneZ) <= this._zoneRadius && player.isVisible() && !player.isDead()) {
                    if (!players.containsKey(player.getTeamId())) {
                        players.put(player.getTeamId(), new LinkedList<PlayerEventInfo>());
                    }
                    players.get(player.getTeamId()).add(player);
                }
            }
            int highestCount = 0;
            int team = 0;
            boolean isThereMajorityTeam = true;
            for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData : players.entrySet()) {
                if (teamData.getValue().size() > highestCount) {
                    highestCount = teamData.getValue().size();
                    team = teamData.getKey();
                }
                else {
                    if (highestCount != 0 && teamData.getValue().size() == highestCount) {
                        isThereMajorityTeam = false;
                        break;
                    }
                    continue;
                }
            }
            if (isThereMajorityTeam && team != 0) {
                boolean ownsZone = false;
                if (this._percentMajorityToScore == 0) {
                    ownsZone = true;
                }
                else if (this._percentMajorityToScore == 100) {
                    boolean teamWithMorePlayers = false;
                    for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData2 : players.entrySet()) {
                        if (teamData2.getKey() == team) {
                            continue;
                        }
                        if (teamData2.getValue().size() > 0) {
                            teamWithMorePlayers = true;
                            break;
                        }
                    }
                    if (!teamWithMorePlayers) {
                        ownsZone = true;
                    }
                }
                else {
                    final int majorityTeamPlayers = players.get(team).size();
                    boolean teamWithMorePlayers2 = false;
                    for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData3 : players.entrySet()) {
                        if (teamData3.getKey() == team) {
                            continue;
                        }
                        final double d = teamData3.getValue().size() / (double)majorityTeamPlayers;
                        final int percent = 100 - (int)(d * 100.0);
                        if (percent < this._percentMajorityToScore) {
                            teamWithMorePlayers2 = true;
                            break;
                        }
                    }
                    if (!teamWithMorePlayers2) {
                        ownsZone = true;
                    }
                }
                if (ownsZone) {
                    if (this.getEventData(instanceId)._holdingTeam != team) {
                        if (this.getEventData(instanceId)._holdingTeam != 0 && this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                            for (final PlayerEventInfo player2 : this._teams.get(instanceId).get(this.getEventData(instanceId)._holdingTeam).getPlayers()) {
                                player2.stopAbnormalEffect((player2.getTeamId() == 1) ? 2097152 : 4);
                            }
                        }
                        this.announce(instanceId, LanguageEngine.getMsg("dom_gainedZone", this._teams.get(instanceId).get(team).getFullName()));
                        this.getEventData(instanceId)._zone.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_owner", this._teams.get(instanceId).get(team).getTeamName()));
                        this.getEventData(instanceId)._zone.getNpc().broadcastNpcInfo();
                        this.getEventData(instanceId)._holdingTeam = team;
                        this.getEventData(instanceId)._holdingTime = 0;
                        this.setZoneEffects(team, this.getEventData(instanceId)._zone);
                    }
                    else {
                        final ZoneData eventData = this.getEventData(instanceId);
                        eventData._holdingTime += this._zoneCheckInterval;
                    }
                    if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                        for (final PlayerEventInfo player2 : this._teams.get(instanceId).get(team).getPlayers()) {
                            if (player2.getDistanceSq(zoneX, zoneY, zoneZ) <= this._zoneRadius && player2.isVisible() && !player2.isDead()) {
                                player2.startAbnormalEffect((player2.getTeamId() == 1) ? 2097152 : 4);
                            }
                            else {
                                player2.stopAbnormalEffect((player2.getTeamId() == 1) ? 2097152 : 4);
                            }
                        }
                    }
                    if (this.getEventData(instanceId)._holdingTime >= this._holdZoneFor) {
                        this._teams.get(instanceId).get(team).raiseScore(this._scoreForCapturingZone);
                        for (final PlayerEventInfo player2 : players.get(team)) {
                            this.getPlayerData(player2).raiseScore(this._scoreForCapturingZone);
                            this.setScoreStats(player2, this.getPlayerData(player2).getScore());
                            if (player2.isTitleUpdated()) {
                                player2.setTitle(this.getTitle(player2), true);
                                player2.broadcastTitleInfo();
                            }
                            CallbackManager.getInstance().playerScores(this.getEventType(), player2, this._scoreForCapturingZone);
                        }
                        this.getEventData(instanceId)._holdingTime = 0;
                        if (this._holdZoneFor <= 5) {
                            continue;
                        }
                        this.announce(instanceId, "*** " + LanguageEngine.getMsg("dom_score", this._teams.get(instanceId).get(team).getFullName()));
                        if (!this.getBoolean("allowFireworkOnScore")) {
                            continue;
                        }
                        this.getEventData(instanceId)._zone.broadcastSkillUse(this.getEventData(instanceId)._zone, this.getEventData(instanceId)._zone, 2024, 1);
                    }
                    else {
                        final int toHold = this._holdZoneFor - this.getEventData(instanceId)._holdingTime;
                        boolean announce = false;
                        if (this.getEventData(instanceId)._holdingTime == 0) {
                            announce = true;
                        }
                        else if (toHold >= 60 && toHold % 60 == 0) {
                            announce = true;
                        }
                        else {
                            switch (toHold) {
                                case 5:
                                case 10:
                                case 20:
                                case 30:
                                case 45: {
                                    announce = true;
                                    break;
                                }
                            }
                        }
                        if (!announce) {
                            continue;
                        }
                        final boolean min = false;
                        this.announce(instanceId, "* " + LanguageEngine.getMsg("dom_leftToScore", toHold, min ? "minute" : "second", this._teams.get(instanceId).get(team).getFullName()));
                    }
                }
                else {
                    if (this.getEventData(instanceId)._holdingTeam != 0) {
                        this.announce(instanceId, LanguageEngine.getMsg("dom_lostZone", this._teams.get(instanceId).get(this.getEventData(instanceId)._holdingTeam).getFullName()));
                        this.getEventData(instanceId)._zone.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_noOwner"));
                        this.getEventData(instanceId)._zone.getNpc().broadcastNpcInfo();
                        this.setZoneEffects(0, this.getEventData(instanceId)._zone);
                        if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                            for (final PlayerEventInfo player2 : this._teams.get(instanceId).get(this.getEventData(instanceId)._holdingTeam).getPlayers()) {
                                player2.stopAbnormalEffect((player2.getTeamId() == 1) ? 2097152 : 4);
                            }
                        }
                    }
                    this.getEventData(instanceId)._holdingTime = 0;
                    this.getEventData(instanceId)._holdingTeam = 0;
                }
            }
            else {
                if (this.getEventData(instanceId)._holdingTeam != 0) {
                    this.announce(instanceId, LanguageEngine.getMsg("dom_lostZone", this._teams.get(instanceId).get(this.getEventData(instanceId)._holdingTeam).getFullName()));
                    this.getEventData(instanceId)._zone.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_noOwner"));
                    this.getEventData(instanceId)._zone.getNpc().broadcastNpcInfo();
                    this.setZoneEffects(0, this.getEventData(instanceId)._zone);
                    if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                        for (final PlayerEventInfo player3 : this._teams.get(instanceId).get(this.getEventData(instanceId)._holdingTeam).getPlayers()) {
                            player3.stopAbnormalEffect((player3.getTeamId() == 1) ? 2097152 : 4);
                        }
                    }
                }
                this.getEventData(instanceId)._holdingTime = 0;
                this.getEventData(instanceId)._holdingTeam = 0;
            }
        }
    }
    
    @Override
    public String getEstimatedTimeLeft() {
        if (this._matches == null) {
            return "Starting";
        }
        for (final DominationEventInstance match : this._matches.values()) {
            if (match.isActive()) {
                return match.getClock().getTime();
            }
        }
        return "N/A";
    }
    
    @Override
    public int getTeamsCount() {
        return this.getInt("teamsCount");
    }
    
    @Override
    public String getMissingSpawns(final EventMap map) {
        final StringBuilder tb = new StringBuilder();
        for (int i = 0; i < this.getTeamsCount(); ++i) {
            if (!map.checkForSpawns(SpawnType.Regular, i + 1, 1)) {
                tb.append(this.addMissingSpawn(SpawnType.Regular, i + 1, 1));
            }
        }
        if (!map.checkForSpawns(SpawnType.Zone, -1, 1)) {
            tb.append(this.addMissingSpawn(SpawnType.Zone, 0, 1));
        }
        return tb.toString();
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        final int owningTeam = this._matches.get(instance)._zoneData._holdingTeam;
        final String status = "<font color=ac9887>Zone owned by:</font> <font color=" + EventManager.getInstance().getDarkColorForHtml(owningTeam) + ">" + EventManager.getInstance().getTeamName(owningTeam) + " team</font>";
        return "<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center>" + status + "</td></tr></table>";
    }
    
    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            }
            else {
                this._htmlDescription = this.getInt("teamsCount") + " teams fighting against each other. ";
                this._htmlDescription += "The goal of this event is to capture and hold ";
                this._htmlDescription += "a zone. The zone is represented by an NPC and to capture it, you need to stand near the NPC and ensure that no other enemies are standing near the zone too. ";
                if (this.getInt("killsForReward") > 0) {
                    this._htmlDescription = this._htmlDescription + "At least " + this.getInt("killsForReward") + " kill(s) is required to receive a reward. ";
                }
                if (this.getInt("scoreForReward") > 0) {
                    this._htmlDescription = this._htmlDescription + "At least " + this.getInt("scoreForReward") + " score (obtained when your team owns the zone and you stand near it) is required to receive a reward. ";
                }
                if (this.getBoolean("waweRespawn")) {
                    this._htmlDescription = this._htmlDescription + "Dead players are resurrected by an advanced wawe-spawn engine each " + this.getInt("resDelay") + " seconds. ";
                }
                else {
                    this._htmlDescription = this._htmlDescription + "If you die, you will get resurrected in " + this.getInt("resDelay") + " seconds. ";
                }
                if (this.getBoolean("createParties")) {
                    this._htmlDescription += "The event automatically creates parties on start.";
                }
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }
    
    @Override
    protected ZoneData createEventData(final int instance) {
        return new ZoneData(instance);
    }
    
    @Override
    protected DominationEventInstance createEventInstance(final InstanceData instance) {
        return new DominationEventInstance(instance);
    }
    
    @Override
    protected ZoneData getEventData(final int instance) {
        return this._matches.get(instance)._zoneData;
    }
    
    protected enum EventState
    {
        START, 
        FIGHT, 
        END, 
        TELEPORT, 
        INACTIVE;
    }
    
    protected class DominationEventInstance extends AbstractEventInstance
    {
        protected EventState _state;
        protected ZoneData _zoneData;
        
        protected DominationEventInstance(final InstanceData instance) {
            super(instance);
            this._state = EventState.START;
            this._zoneData = Domination.this.createEventData(instance.getId());
        }
        
        protected void setNextState(final EventState state) {
            this._state = state;
        }
        
        @Override
        public boolean isActive() {
            return this._state != EventState.INACTIVE;
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    Domination.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (Domination.this.checkPlayers(this._instance.getId())) {
                            Domination.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            Domination.this.setupTitles(this._instance.getId());
                            Domination.this.enableMarkers(this._instance.getId(), true);
                            Domination.this.spawnZone(this._instance.getId());
                            Domination.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Domination.this.forceStandAll(this._instance.getId());
                        if (Domination.this.getBoolean("createParties")) {
                            Domination.this.createParties(Domination.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(Domination.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        Domination.this.unspawnZone(this._instance.getId());
                        this.setNextState(EventState.INACTIVE);
                        if (!Domination.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                Domination.this.rewardAllTeams(this._instance.getId(), Domination.this.getInt("scoreForReward"), Domination.this.getInt("killsForReward"));
                            }
                            Domination.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Domination.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                Domination.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class ZoneData extends AbstractEventData
    {
        protected NpcData _zone;
        protected int _holdingTeam;
        protected int _holdingTime;
        
        protected ZoneData(final int instance) {
            super(instance);
        }
        
        protected void addZone(final NpcData zone, final int radius) {
            this._zone = zone;
        }
    }
}
