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
import java.util.concurrent.CopyOnWriteArrayList;

public class Battlefield extends AbstractMainEvent
{
    protected Map<Integer, BattlefieldEventInstance> _matches;
    protected boolean _waweRespawn;
    protected int _teamsCount;
    protected int _towerNpcId;
    protected int _towerRadius;
    protected int _towerCheckInterval;
    protected int _scoreForCapturingTower;
    protected int _timeToHoldTowerToCapture;
    private int _holdAllTowersFor;
    protected int _percentMajorityToCapture;
    protected String _scoreType;
    protected int _minPlayersToCaptureTheBase;
    protected boolean isMinPlayersToCaptureTheBaseInPercent;
    protected int _minTowersToOwnToScore;
    protected int tick;
    protected int countOfTowers;
    
    public Battlefield(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, BattlefieldEventInstance>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("scoreForReward", "0", "The minimum score required to get a reward (includes all possible rewards). Score in this event is gained by capturing bases."));
        this.addConfig(new ConfigModel("killsForReward", "0", "The minimum kills count required to get a reward (includes all possible rewards)."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which the player is resurrected. In seconds."));
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system."));
        this.addConfig(new ConfigModel("countOfBases", "2", "Specifies how many bases will be in the event. In order to score, one team must capture more bases than the other team(s). If you have 2 or 4 teams set in this event, you should only use odd numbers for the count of towers, such as 3, 5, 7 or 9. Don't forget to create a same count of Base spawns in the map you are running this event in. ", ConfigModel.InputType.Enum).addEnumOptions(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        this.addConfig(new ConfigModel("baseNpcId", "8998", "The ID of NPC that symbolizes the base."));
        this.addConfig(new ConfigModel("baseRadius", "180", "The radius of base to count players inside."));
        this.addConfig(new ConfigModel("allowBaseNpcEffects", "true", "Enables Base NPC's special effects, if blue or red team owns it. Due to client limitations, this will only work if the event has 2 teams.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowFireworkOnScore", "true", "Enables Base NPC's small firework effect, when a team scores. Working only if <font color=LEVEL>holdBaseFor</font> is higher than 5 (to prevent spamming this skill).", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("allowPlayerEffects", "true", "Enables special effects for players from the team owning the base and standing near the Base NPC (in <font color=LEVEL>baseRadius</font>). Only works if the event has 2 teams.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("baseCheckInterval", "1", "In seconds. The time after it checks and counts players near the base(s) and adds score to the team, that has more players inside the base. Setting this to 1 is usually good (higher values make this event less expensive for cpu)"));
        this.addConfig(new ConfigModel("minPlayersToCaptureBase", "25%", "The min count of players the team must have near the base in order to capture it. You can set this value in percent by adding % (eg. 5%) - this will calculate the min count of players from the size of the team (eg. 20% and 50 players in the team = at least 10 players are needed to capture a base)."));
        this.addConfig(new ConfigModel("typeOfScoring", "AllTeams", "Define the way the event will give score to teams for capturing bases. If you select 'AllTeams', the event will score to all teams based on the count of bases they own (eg. team A has 2 bases - will receive 2 score, team B has 1 base - will receive 1 score). Setting 'DominatingTeam' will make it so that only the team which has MORE bases than the other teams will be receiving score points.", ConfigModel.InputType.Enum).addEnumOptions(new String[] { "AllTeams", "DominatingTeam" }));
        this.addConfig(new ConfigModel("scoreForCapturingBase", "1", "The ammount of points team gets each <font color=LEVEL>scoreCheckInterval</font> seconds if owns the base."));
        this.addConfig(new ConfigModel("holdBaseToCapture", "0", "In seconds. In order to capture a single base, the team needs to stay for this time near it."));
        this.addConfig(new ConfigModel("holdAllBasesToScore", "0", "In seconds. If the team captures enought bases to score, they will still need to hold them for this time in order to get <font color=LEVEL>scoreForCapturingBase</font> score."));
        this.addConfig(new ConfigModel("minTowersToOwnToScore", "1", "The min count of towers one team must own in order to get any score."));
        this.addConfig(new ConfigModel("percentMajorityToScore", "50", "In percent. In order to score a point, the team must have more players near the base NPC in <font color=LEVEL>baseRadius</font> radius, than the other team(s). The ammount of players from the scoring team must be higher than the ammount of players from the other teams by this percent value. Put 100 to make that all other team(s)' players in <font color=LEVEL>baseRadius</font> must be dead to score; or put 0 to make that it will give score to the team that has more players and not care about any percent counting (eg. if team A has 15 players and team B has 16, it will simply reward team B)."));
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
        this._towerNpcId = this.getInt("baseNpcId");
        this._towerRadius = (int)Math.pow(this.getInt("baseRadius"), 2.0);
        this._towerCheckInterval = this.getInt("baseCheckInterval");
        final String s = this.getString("minPlayersToCaptureBase");
        if (s.endsWith("%")) {
            this._minPlayersToCaptureTheBase = Integer.parseInt(s.substring(0, s.length() - 1));
            this.isMinPlayersToCaptureTheBaseInPercent = true;
        }
        else {
            this._minPlayersToCaptureTheBase = Integer.parseInt(s);
            this.isMinPlayersToCaptureTheBaseInPercent = false;
        }
        this._scoreType = this.getString("typeOfScoring");
        this._minTowersToOwnToScore = this.getInt("minTowersToOwnToScore");
        this._timeToHoldTowerToCapture = this.getInt("holdBaseToCapture");
        this._holdAllTowersFor = this.getInt("holdBaseFor");
        this._scoreForCapturingTower = this.getInt("scoreForCapturingBase");
        this._percentMajorityToCapture = this.getInt("percentMajorityToScore");
        this.countOfTowers = this._manager.getMap().getSpawns(-1, SpawnType.Base).size();
        this._runningInstances = 0;
        this.tick = 0;
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
            final BattlefieldEventInstance match = this.createEventInstance(instance);
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
    
    @Override
    protected void enableMarkers(final int instanceId, final boolean createEventSpawnMarkers) {
        if (!this._enableRadar) {
            return;
        }
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            for (final PlayerEventInfo pi : team.getPlayers()) {
                pi.createRadar();
            }
            this.startRadar(instanceId, team);
        }
    }
    
    private void startRadar(final int instanceId, final EventTeam team) {
        try {
            if (!this._enableRadar) {
                return;
            }
            final EventSpawn zone = this.selectZoneForRadar(instanceId, team);
            if (zone != null) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    pi.getRadar().setLoc(zone.getLoc().getX(), zone.getLoc().getY(), zone.getLoc().getZ());
                    pi.getRadar().setRepeat(true);
                    pi.getRadar().enable();
                }
            }
            else {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    pi.getRadar().setRepeat(false);
                    pi.getRadar().disable();
                }
            }
        }
        catch (Exception ex) {}
    }
    
    private EventSpawn selectZoneForRadar(final int instanceId, final EventTeam team) {
        EventSpawn zone = null;
        final int teamId = team.getTeamId();
        int topImportance = Integer.MAX_VALUE;
        Tower tempTopImportance = null;
        for (final Tower tower : this.getEventData(instanceId)._towers) {
            if (tower != null && tower.getOwningTeam() == 0 && tower.getSpawn().getSpawnTeam() == teamId && tower.getSpawn().getImportance() < topImportance) {
                topImportance = tower.getSpawn().getImportance();
                tempTopImportance = tower;
            }
        }
        if (tempTopImportance == null) {
            topImportance = Integer.MAX_VALUE;
            for (final Tower tower : this.getEventData(instanceId)._towers) {
                if (tower != null && tower.getOwningTeam() == 0 && tower.getSpawn().getImportance() < topImportance) {
                    topImportance = tower.getSpawn().getImportance();
                    tempTopImportance = tower;
                }
            }
        }
        if (tempTopImportance == null) {
            topImportance = Integer.MAX_VALUE;
            for (final Tower tower : this.getEventData(instanceId)._towers) {
                if (tower != null && tower.getSpawn().getSpawnTeam() == teamId && tower.getOwningTeam() != teamId && tower.getSpawn().getImportance() < topImportance) {
                    topImportance = tower.getSpawn().getImportance();
                    tempTopImportance = tower;
                }
            }
        }
        if (tempTopImportance == null) {
            topImportance = 0;
            for (final Tower tower : this.getEventData(instanceId)._towers) {
                if (tower != null && tower.getOwningTeam() != teamId && tower.getSpawn().getImportance() > topImportance) {
                    topImportance = tower.getSpawn().getImportance();
                    tempTopImportance = tower;
                }
            }
        }
        if (tempTopImportance != null) {
            zone = tempTopImportance.getSpawn();
        }
        return zone;
    }
    
    protected void spawnTowers(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: spawning bases for instance " + instanceId);
        }
        this.clearMapHistory(-1, SpawnType.Base);
        int i = 0;
        for (final EventSpawn sp : this._manager.getMap().getSpawns(-1, SpawnType.Base)) {
            ++i;
            final NpcData base = this.spawnNPC(sp.getLoc().getX(), sp.getLoc().getY(), sp.getLoc().getZ(), this._towerNpcId, instanceId, "Base " + i, "Domination event");
            this.getEventData(instanceId).addTower(base, sp.getRadius(), sp);
        }
    }
    
    protected void unspawnTowers(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: unspawning base for instance " + instanceId);
        }
        for (final Tower tower : this.getEventData(instanceId)._towers) {
            if (tower.getNpc() != null) {
                tower.setOwningTeam(0, false);
                tower.getNpc().deleteMe();
            }
        }
    }
    
    protected void setBaseEffects(final int teamId, final NpcData baseNpc) {
        if (this.getBoolean("allowBaseNpcEffects") && this._teamsCount == 2) {
            if (teamId == 1) {
                baseNpc.stopAbnormalEffect(4);
                baseNpc.startAbnormalEffect(2097152);
            }
            else if (teamId == 2) {
                baseNpc.stopAbnormalEffect(2097152);
                baseNpc.startAbnormalEffect(4);
            }
            else {
                baseNpc.stopAbnormalEffect(4);
                baseNpc.stopAbnormalEffect(2097152);
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
                for (final BattlefieldEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        match.abort();
                        this.unspawnTowers(match.getInstance().getId());
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
                player.removeRadarAllMarkers();
                player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
                player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
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
        ++this.tick;
        if (this.tick % this._towerCheckInterval != 0) {
            return;
        }
        for (final BattlefieldEventInstance instance : this._matches.values()) {
            final int instanceId = instance.getInstance().getId();
            if (this.tick % 10 == 0) {
                for (final EventTeam team : this._teams.get(instanceId).values()) {
                    this.startRadar(instanceId, team);
                }
            }
            final TowerData towerData = this.getEventData(instanceId);
            final Map<Integer, List<NpcData>> ownedTowers = new LinkedHashMap<Integer, List<NpcData>>();
            for (int i = 0; i < towerData._towers.length; ++i) {
                final Map<Integer, List<PlayerEventInfo>> players = new LinkedHashMap<Integer, List<PlayerEventInfo>>(this._teamsCount);
                final Tower tower = towerData._towers[i];
                final NpcData towerNpc = towerData._towers[i].getNpc();
                final int radius = towerData._towers[i].getRadius();
                final int baseX = towerNpc.getLoc().getX();
                final int baseY = towerNpc.getLoc().getY();
                final int baseZ = towerNpc.getLoc().getZ();
                for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                    if (player.getDistanceSq(baseX, baseY, baseZ) <= radius && player.isVisible() && !player.isDead()) {
                        if (!players.containsKey(player.getTeamId())) {
                            players.put(player.getTeamId(), new LinkedList<PlayerEventInfo>());
                        }
                        players.get(player.getTeamId()).add(player);
                    }
                }
                int highestCount = 0;
                int team2 = 0;
                boolean isThereMajorityTeam = true;
                for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData : players.entrySet()) {
                    if (teamData.getValue().size() > highestCount) {
                        highestCount = teamData.getValue().size();
                        team2 = teamData.getKey();
                    }
                    else {
                        if (highestCount != 0 && teamData.getValue().size() == highestCount) {
                            isThereMajorityTeam = false;
                            break;
                        }
                        continue;
                    }
                }
                if (isThereMajorityTeam && team2 != 0) {
                    final int majorityTeamPlayersCount = players.get(team2).size();
                    boolean dominatesBase = false;
                    if (this._percentMajorityToCapture == 0) {
                        dominatesBase = true;
                    }
                    else if (this._percentMajorityToCapture == 100) {
                        boolean teamWithMorePlayers = false;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData2 : players.entrySet()) {
                            if (teamData2.getKey() == team2) {
                                continue;
                            }
                            if (teamData2.getValue().size() > 0) {
                                teamWithMorePlayers = true;
                                break;
                            }
                        }
                        if (!teamWithMorePlayers) {
                            dominatesBase = true;
                        }
                    }
                    else {
                        boolean teamWithMorePlayers = false;
                        for (final Map.Entry<Integer, List<PlayerEventInfo>> teamData2 : players.entrySet()) {
                            if (teamData2.getKey() == team2) {
                                continue;
                            }
                            final double d = teamData2.getValue().size() / (double)majorityTeamPlayersCount;
                            final int percent = 100 - (int)(d * 100.0);
                            if (percent < this._percentMajorityToCapture) {
                                teamWithMorePlayers = true;
                                break;
                            }
                        }
                        if (!teamWithMorePlayers) {
                            dominatesBase = true;
                        }
                    }
                    if (dominatesBase) {
                        final int countInTeam = this._teams.get(instanceId).get(team2).getPlayers().size();
                        int minCountOfPlayersNearTheBase;
                        if (this.isMinPlayersToCaptureTheBaseInPercent) {
                            minCountOfPlayersNearTheBase = (int)Math.round(countInTeam * (this._minPlayersToCaptureTheBase * 0.01));
                        }
                        else {
                            minCountOfPlayersNearTheBase = this._minPlayersToCaptureTheBase;
                        }
                        if (minCountOfPlayersNearTheBase < 1) {
                            minCountOfPlayersNearTheBase = 1;
                        }
                        if (majorityTeamPlayersCount < minCountOfPlayersNearTheBase) {
                            if (this.tick % 2 == 0) {
                                for (final PlayerEventInfo player2 : players.get(team2)) {
                                    if (player2 != null && player2.isOnline()) {
                                        player2.sendMessage("At least " + minCountOfPlayersNearTheBase + " players from your team are required to capture a base.");
                                    }
                                }
                            }
                            dominatesBase = false;
                        }
                    }
                    if (dominatesBase) {
                        if (tower.getOwningTeam() == 0) {
                            if (tower.setCapturingTime(tower.getCapturingTime() + this._towerCheckInterval)) {
                                this.announce(instanceId, this._teams.get(instanceId).get(team2).getTeamName() + " has gained the contol over base " + (i + 1));
                                if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                                    for (final PlayerEventInfo player3 : tower.getEffectedPlayers()) {
                                        if (player3 != null) {
                                            tower.removeEffectedPlayer(player3);
                                            player3.stopAbnormalEffect((player3.getTeamId() == 1) ? CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1() : CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                                        }
                                    }
                                    tower.resetEffectedPlayers();
                                }
                                tower.setOwningTeam(team2, true);
                                this.setBaseEffects(team2, towerNpc);
                                towerNpc.setTitle("Owner: " + this._teams.get(instanceId).get(team2).getTeamName());
                                towerNpc.broadcastNpcInfo();
                                for (final PlayerEventInfo player3 : players.get(team2)) {
                                    this.getPlayerData(player3).raiseScore(this._scoreForCapturingTower);
                                    this.setScoreStats(player3, this.getPlayerData(player3).getScore());
                                    if (player3.isTitleUpdated()) {
                                        player3.setTitle(this.getTitle(player3), true);
                                        player3.broadcastTitleInfo();
                                    }
                                    CallbackManager.getInstance().playerScores(this.getEventType(), player3, this._scoreForCapturingTower);
                                }
                            }
                            else if (tower.getCapturingTime() == this._towerCheckInterval) {
                                this.announce(instanceId, this._teams.get(instanceId).get(team2).getTeamName() + " is now capturing base " + (i + 1));
                            }
                        }
                        else if (tower.getOwningTeam() != team2) {
                            if (tower.setCapturingTime(tower.getCapturingTime() + this._towerCheckInterval)) {
                                this.announce(instanceId, this._teams.get(instanceId).get(team2).getTeamName() + " has gained the contol over base " + (i + 1));
                                if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                                    for (final PlayerEventInfo player3 : tower.getEffectedPlayers()) {
                                        if (player3 != null) {
                                            tower.removeEffectedPlayer(player3);
                                            player3.stopAbnormalEffect((player3.getTeamId() == 1) ? CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1() : CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                                        }
                                    }
                                    tower.resetEffectedPlayers();
                                }
                                tower.setOwningTeam(team2, true);
                                this.setBaseEffects(team2, towerNpc);
                                towerNpc.setTitle("Owner: " + this._teams.get(instanceId).get(team2).getTeamName());
                                towerNpc.broadcastNpcInfo();
                                for (final PlayerEventInfo player3 : players.get(team2)) {
                                    this.getPlayerData(player3).raiseScore(this._scoreForCapturingTower);
                                    this.setScoreStats(player3, this.getPlayerData(player3).getScore());
                                    if (player3.isTitleUpdated()) {
                                        player3.setTitle(this.getTitle(player3), true);
                                        player3.broadcastTitleInfo();
                                    }
                                    CallbackManager.getInstance().playerScores(this.getEventType(), player3, this._scoreForCapturingTower);
                                }
                            }
                            else if (tower.getCapturingTime() == this._towerCheckInterval) {
                                this.announce(instanceId, this._teams.get(instanceId).get(team2).getTeamName() + " is now capturing base " + (i + 1));
                            }
                        }
                    }
                    else if (tower.getCapturingTime() > 0) {
                        tower.setCapturingTime(0);
                    }
                }
                else if (tower.getCapturingTime() > 0) {
                    tower.setCapturingTime(0);
                }
                if (tower.getOwningTeam() > 0) {
                    if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                        if (players.containsKey(tower.getOwningTeam())) {
                            for (final PlayerEventInfo player4 : players.get(tower.getOwningTeam())) {
                                if (!tower.containsEffectedPlayer(player4)) {
                                    tower.addEffectedPlayer(player4);
                                    player4.startAbnormalEffect((player4.getTeamId() == 1) ? CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1() : CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                                }
                            }
                        }
                        for (final PlayerEventInfo player4 : tower.getEffectedPlayers()) {
                            if (!players.containsKey(tower.getOwningTeam()) || !players.get(tower.getOwningTeam()).contains(player4)) {
                                tower.removeEffectedPlayer(player4);
                                player4.stopAbnormalEffect((player4.getTeamId() == 1) ? CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1() : CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                            }
                        }
                    }
                    tower.raiseOwnedTime(this._towerCheckInterval);
                    if (!ownedTowers.containsKey(tower.getOwningTeam())) {
                        ownedTowers.put(tower.getOwningTeam(), new LinkedList<NpcData>());
                    }
                    ownedTowers.get(tower.getOwningTeam()).add(towerNpc);
                }
            }
            if (this._scoreType.equals("AllTeams")) {
                this._minTowersToOwnToScore = 1;
                for (final Map.Entry<Integer, List<NpcData>> e : ownedTowers.entrySet()) {
                    final int team3 = e.getKey();
                    final int countOfTowers = e.getValue().size();
                    if (countOfTowers >= this._minTowersToOwnToScore && countOfTowers > 0) {
                        this._teams.get(instanceId).get(team3).raiseScore(countOfTowers);
                    }
                }
            }
            else {
                if (!this._scoreType.equals("DominatingTeam")) {
                    continue;
                }
                boolean ownsRequiredCountOfBases = false;
                int teamWithMostBases = 0;
                int mostBasesCount = 0;
                for (final Map.Entry<Integer, List<NpcData>> e2 : ownedTowers.entrySet()) {
                    if (e2.getValue().size() > mostBasesCount) {
                        teamWithMostBases = e2.getKey();
                        mostBasesCount = e2.getValue().size();
                        ownsRequiredCountOfBases = true;
                    }
                    else {
                        if (e2.getValue().size() != 0 && e2.getValue().size() == mostBasesCount) {
                            ownsRequiredCountOfBases = false;
                            break;
                        }
                        continue;
                    }
                }
                if (ownsRequiredCountOfBases) {
                    ownsRequiredCountOfBases = (mostBasesCount >= this._minTowersToOwnToScore);
                }
                if (ownsRequiredCountOfBases) {
                    if (teamWithMostBases != towerData._dominatingTeam) {
                        this.announce(instanceId, "++ " + this._teams.get(instanceId).get(teamWithMostBases).getFullName() + " is dominating " + mostBasesCount + " bases!");
                        towerData.setDominatingTeam(teamWithMostBases);
                        towerData.resetDominatingTime();
                    }
                    else {
                        towerData.raiseDominatingTime(this._towerCheckInterval);
                    }
                    if (towerData.getDominatingTime() >= this._holdAllTowersFor) {
                        this._teams.get(instanceId).get(teamWithMostBases).raiseScore(this._scoreForCapturingTower);
                        towerData.resetDominatingTime();
                        if (this._holdAllTowersFor <= 5) {
                            continue;
                        }
                        this.announce(instanceId, "*** " + this._teams.get(instanceId).get(teamWithMostBases).getTeamName() + "s scored for owning " + mostBasesCount + " bases!");
                        if (!this.getBoolean("allowFireworkOnScore")) {
                            continue;
                        }
                        for (final Tower tow : towerData._towers) {
                            if (tow.getNpc() != null && tow.getOwningTeam() == towerData.getDominatingTeam()) {
                                tow.getNpc().broadcastSkillUse(tow.getNpc(), tow.getNpc(), 2024, 1);
                            }
                        }
                    }
                    else {
                        final int toHold = this._holdAllTowersFor - towerData._holdingAllTowersFor;
                        boolean announce = false;
                        if (towerData._holdingAllTowersFor == 0) {
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
                        this.announce(instanceId, "* " + LanguageEngine.getMsg("mDom_leftToScore", toHold, min ? "minutes" : "seconds", this._teams.get(instanceId).get(teamWithMostBases).getFullName()));
                    }
                }
                else {
                    if (towerData.getDominatingTeam() != 0 && towerData.getDominatingTime() > 0) {
                        this.announce(instanceId, "-- " + this._teams.get(instanceId).get(towerData._dominatingTeam).getFullName() + " has lost base domination.");
                    }
                    towerData.setDominatingTeam(0);
                    towerData.resetDominatingTime();
                }
            }
        }
    }
    
    @Override
    public String getEstimatedTimeLeft() {
        if (this._matches == null) {
            return "Starting";
        }
        for (final BattlefieldEventInstance match : this._matches.values()) {
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
        if (!map.checkForSpawns(SpawnType.Base, -1, 1)) {
            tb.append(this.addMissingSpawn(SpawnType.Base, 0, 1));
        }
        return tb.toString();
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        final int owningTeam = this._matches.get(instance)._towerData._dominatingTeam;
        final String status = "<font color=ac9887>Dominates:</font> <font color=" + EventManager.getInstance().getDarkColorForHtml(owningTeam) + ">" + EventManager.getInstance().getTeamName(owningTeam) + " team</font>";
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
                this._htmlDescription = "No information about this event yet.";
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    protected AbstractEventInstance getMatch(final int instanceId) {
        return this._matches.get(instanceId);
    }
    
    @Override
    protected TowerData createEventData(final int instance) {
        return new TowerData(instance);
    }
    
    @Override
    protected BattlefieldEventInstance createEventInstance(final InstanceData instance) {
        return new BattlefieldEventInstance(instance);
    }
    
    @Override
    protected TowerData getEventData(final int instance) {
        return this._matches.get(instance)._towerData;
    }
    
    protected enum EventState
    {
        START, 
        FIGHT, 
        END, 
        TELEPORT, 
        INACTIVE;
    }
    
    protected class BattlefieldEventInstance extends AbstractEventInstance
    {
        protected EventState _state;
        protected TowerData _towerData;
        
        protected BattlefieldEventInstance(final InstanceData instance) {
            super(instance);
            this._state = EventState.START;
            this._towerData = Battlefield.this.createEventData(instance.getId());
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
                    Battlefield.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (Battlefield.this.checkPlayers(this._instance.getId())) {
                            Battlefield.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            Battlefield.this.setupTitles(this._instance.getId());
                            Battlefield.this.spawnTowers(this._instance.getId());
                            Battlefield.this.enableMarkers(this._instance.getId(), true);
                            Battlefield.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Battlefield.this.forceStandAll(this._instance.getId());
                        if (Battlefield.this.getBoolean("createParties")) {
                            Battlefield.this.createParties(Battlefield.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(Battlefield.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        Battlefield.this.unspawnTowers(this._instance.getId());
                        this.setNextState(EventState.INACTIVE);
                        if (!Battlefield.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                Battlefield.this.rewardAllTeams(this._instance.getId(), Battlefield.this.getInt("scoreForReward"), Battlefield.this.getInt("killsForReward"));
                            }
                            Battlefield.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Battlefield.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                Battlefield.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class TowerData extends AbstractEventData
    {
        protected final Tower[] _towers;
        private int _order;
        protected int _dominatingTeam;
        protected int _holdingAllTowersFor;
        
        protected TowerData(final int instance) {
            super(instance);
            this._towers = new Tower[Battlefield.this.countOfTowers];
            this._dominatingTeam = 0;
            this._holdingAllTowersFor = 0;
            this._order = 0;
        }
        
        protected void addTower(final NpcData base, final int radius, final EventSpawn spawn) {
            if (this._order < Battlefield.this.countOfTowers) {
                this._towers[this._order] = new Tower(spawn, base, (radius > 0) ? ((int)Math.pow(radius, 2.0)) : Battlefield.this._towerRadius);
                ++this._order;
            }
            else {
                SunriseLoader.debug("too many towers for TowerData (" + this._order + "; " + Battlefield.this.countOfTowers + ")");
            }
        }
        
        protected void setDominatingTeam(final int team) {
            this._dominatingTeam = team;
        }
        
        protected int getDominatingTeam() {
            return this._dominatingTeam;
        }
        
        protected int raiseDominatingTime(final int time) {
            return this._holdingAllTowersFor += time;
        }
        
        protected int getDominatingTime() {
            return this._holdingAllTowersFor;
        }
        
        protected void resetDominatingTime() {
            this._holdingAllTowersFor = 0;
        }
        
        protected Tower getTower(final int index) {
            return this._towers[index];
        }
    }
    
    protected class Tower
    {
        private final NpcData _npc;
        private final EventSpawn _spawn;
        private final int _radius;
        private int _owningTeam;
        private int _ownedTime;
        private int _capturingTime;
        private final List<PlayerEventInfo> _effects;
        
        public Tower(final EventSpawn spawn, final NpcData npc, final int radius) {
            this._effects = new CopyOnWriteArrayList<PlayerEventInfo>();
            this._spawn = spawn;
            this._npc = npc;
            this._radius = radius;
            this._owningTeam = 0;
            this._capturingTime = 0;
            this._effects.clear();
        }
        
        public void setOwningTeam(final int team, final boolean updateTime) {
            this._owningTeam = team;
            if (updateTime) {
                this.setOwnedTime(0);
            }
        }
        
        public boolean setCapturingTime(final int i) {
            this._capturingTime = i;
            return this._capturingTime >= Battlefield.this._timeToHoldTowerToCapture;
        }
        
        public int getCapturingTime() {
            return this._capturingTime;
        }
        
        public void addEffectedPlayer(final PlayerEventInfo player) {
            this._effects.add(player);
        }
        
        public void removeEffectedPlayer(final PlayerEventInfo player) {
            this._effects.remove(player);
        }
        
        public boolean containsEffectedPlayer(final PlayerEventInfo player) {
            return this._effects.contains(player);
        }
        
        public List<PlayerEventInfo> getEffectedPlayers() {
            return this._effects;
        }
        
        public void resetEffectedPlayers() {
            this._effects.clear();
        }
        
        public int getOwningTeam() {
            return this._owningTeam;
        }
        
        public int getOwnedTime() {
            return this._ownedTime;
        }
        
        public void setOwnedTime(final int i) {
            this._ownedTime = i;
        }
        
        public void raiseOwnedTime(final int count) {
            this._ownedTime += count;
        }
        
        public NpcData getNpc() {
            return this._npc;
        }
        
        public int getRadius() {
            return this._radius;
        }
        
        public EventSpawn getSpawn() {
            return this._spawn;
        }
        
        public Loc getLoc() {
            return this._npc.getLoc();
        }
    }
}
