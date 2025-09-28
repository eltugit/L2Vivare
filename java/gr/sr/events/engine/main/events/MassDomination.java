package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.NpcData;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MassDomination extends Domination
{
    protected int _zonesCount;
    private int _zonesToOwn;
    private int _holdZonesFor;
    
    public MassDomination(final EventType type, final MainEventManager manager) {
        super(type, manager);
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("countOfZones", "2", "Specifies how many zones will be in the event. In order to get score, one team must own all zones.", ConfigModel.InputType.Enum).addEnumOptions(new String[] { "2", "3", "4", "5" }));
        this.addConfig(new ConfigModel("zonesToOwnToScore", "2", "Count of zones the team needs to own in order to score. Obviously must be lower or equal to <font color=LEVEL>countOfZones</font>."));
        this.removeConfig("holdZoneFor");
        this.removeConfig("scoreForCapturingZone");
        this.removeConfig("percentMajorityToScore");
        this.addConfig(new ConfigModel("holdZonesFor", "10", "In seconds. The team needs to own <font color=LEVEL>zonesToOwnToScore</font> zones for this time to get <font color=LEVEL>scoreForCapturingZone</font> points. "));
        this.addConfig(new ConfigModel("scoreForCapturingZone", "1", "The ammount of points team gets each <font color=LEVEL>scoreCheckInterval</font> seconds if owns required zone(s)."));
        this.addConfig(new ConfigModel("percentMajorityToScore", "50", "In percent. In order to score a point, the team must have more players near at least <font color=LEVEL>zonesToOwnToScore</font> zones, than the other team(s). The ammount of players from the scoring team must be higher than ammount of players from the other team(s) by this percent value. Put 100 to make that all other team(s)' players in <font color=LEVEL>zoneRadius</font> must be dead to score; or put 0 to make that it will give score to the team that has more players and not care about any percent counting (eg. if team A has 15 players and team B has 16, it will simply reward team B)."));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._zonesCount = this.getInt("countOfZones");
        this._zonesToOwn = this.getInt("zonesToOwnToScore");
        this._holdZonesFor = this.getInt("holdZonesFor");
    }
    
    @Override
    protected void spawnZone(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: spawning zones for instanceId " + instanceId);
        }
        this.clearMapHistory(-1, SpawnType.Zone);
        for (int i = 0; i < this._zonesCount; ++i) {
            final EventSpawn sp = this.getSpawn(SpawnType.Zone, -1);
            final NpcData zone = this.spawnNPC(sp.getLoc().getX(), sp.getLoc().getY(), sp.getLoc().getZ(), this._zoneNpcId, instanceId, "Zone " + (i + 1), "Domination event");
            this.getEventData(instanceId).addZone(zone, sp.getRadius());
        }
    }
    
    @Override
    protected void unspawnZone(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: unspawning zones for instanceId " + instanceId);
        }
        for (final NpcData zoneNpc : this.getEventData(instanceId)._zones) {
            if (zoneNpc != null) {
                zoneNpc.deleteMe();
            }
        }
    }
    
    @Override
    protected void clockTick() {
        ++this._tick;
        if (this._tick % this._zoneCheckInterval != 0) {
            return;
        }
        for (final DominationEventInstance match : this._matches.values()) {
            final int instanceId = match.getInstance().getId();
            final MultipleZoneData zoneData = this.getEventData(instanceId);
            final Map<Integer, List<NpcData>> ownedZones = new ConcurrentHashMap<Integer, List<NpcData>>();
            final Map<Integer, List<PlayerEventInfo>> playersNearZones = new ConcurrentHashMap<Integer, List<PlayerEventInfo>>(this._teamsCount);
            final List<PlayerEventInfo> playersWithEffects = new LinkedList<PlayerEventInfo>();
            for (int i = 0; i < zoneData._zones.length; ++i) {
                final Map<Integer, List<PlayerEventInfo>> players = new ConcurrentHashMap<Integer, List<PlayerEventInfo>>(this._teamsCount);
                final NpcData zoneNpc = zoneData._zones[i];
                final int radius = zoneData._radiuses[i];
                final int zoneX = zoneNpc.getLoc().getX();
                final int zoneY = zoneNpc.getLoc().getY();
                final int zoneZ = zoneNpc.getLoc().getZ();
                for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                    if (player.getDistanceSq(zoneX, zoneY, zoneZ) <= radius && player.isVisible() && !player.isDead()) {
                        if (!players.containsKey(player.getTeamId())) {
                            players.put(player.getTeamId(), new LinkedList<PlayerEventInfo>());
                        }
                        if (!playersNearZones.containsKey(player.getTeamId())) {
                            playersNearZones.put(player.getTeamId(), new LinkedList<PlayerEventInfo>());
                        }
                        players.get(player.getTeamId()).add(player);
                        playersNearZones.get(player.getTeamId()).add(player);
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
                        if (zoneData._holdingTeams[i] != team) {
                            this.announce(instanceId, LanguageEngine.getMsg("mDom_gainedZone", this._teams.get(instanceId).get(team).getFullName(), i + 1));
                            zoneNpc.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_owner", this._teams.get(instanceId).get(team).getTeamName()));
                            zoneNpc.getNpc().broadcastNpcInfo();
                            zoneData._holdingTeams[i] = team;
                            zoneData._holdingTimes[i] = 0;
                            this.setZoneEffects(team, zoneNpc);
                        }
                        else {
                            final int[] holdingTimes = zoneData._holdingTimes;
                            final int n = i;
                            holdingTimes[n] += this._zoneCheckInterval;
                        }
                        if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                            for (final PlayerEventInfo player2 : this._teams.get(instanceId).get(team).getPlayers()) {
                                if (player2.getDistanceSq(zoneX, zoneY, zoneZ) <= radius && player2.isVisible() && !player2.isDead()) {
                                    playersWithEffects.add(player2);
                                }
                            }
                        }
                        if (!ownedZones.containsKey(team)) {
                            ownedZones.put(team, new LinkedList<NpcData>());
                        }
                        ownedZones.get(team).add(zoneNpc);
                    }
                    else {
                        if (zoneData._holdingTeams[i] != 0) {
                            this.announce(instanceId, LanguageEngine.getMsg("mDom_lostZone", this._teams.get(instanceId).get(zoneData._holdingTeams[i]).getFullName(), i + 1));
                            zoneNpc.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_noOwner"));
                            zoneNpc.getNpc().broadcastNpcInfo();
                            this.setZoneEffects(0, zoneNpc);
                        }
                        zoneData._holdingTimes[i] = 0;
                        zoneData._holdingTeams[i] = 0;
                    }
                }
                else {
                    if (zoneData._holdingTeams[i] != 0) {
                        this.announce(instanceId, LanguageEngine.getMsg("mDom_lostZone", this._teams.get(instanceId).get(zoneData._holdingTeams[i]).getFullName(), i + 1));
                        zoneNpc.getNpc().setTitle(LanguageEngine.getMsg("dom_npcTitle_noOwner"));
                        zoneNpc.getNpc().broadcastNpcInfo();
                        this.setZoneEffects(0, zoneNpc);
                    }
                    zoneData._holdingTimes[i] = 0;
                    zoneData._holdingTeams[i] = 0;
                }
            }
            if (this.getBoolean("allowPlayerEffects") && this._teamsCount == 2) {
                for (final PlayerEventInfo player3 : this.getPlayers(instanceId)) {
                    if (playersWithEffects.contains(player3)) {
                        player3.startAbnormalEffect((player3.getTeamId() == 1) ? 2097152 : 4);
                    }
                    else {
                        player3.stopAbnormalEffect((player3.getTeamId() == 1) ? 2097152 : 4);
                    }
                }
            }
            boolean ownsAllZones = true;
            int teamWithMostZones = 0;
            int mostZones = 0;
            for (final Map.Entry<Integer, List<NpcData>> e : ownedZones.entrySet()) {
                if (e.getValue().size() > mostZones) {
                    teamWithMostZones = e.getKey();
                    mostZones = e.getValue().size();
                }
                else {
                    if (e.getValue().size() != 0 && e.getValue().size() == mostZones) {
                        ownsAllZones = false;
                        break;
                    }
                    continue;
                }
            }
            if (ownsAllZones) {
                ownsAllZones = (mostZones >= this._zonesToOwn);
            }
            if (ownsAllZones) {
                if (teamWithMostZones != zoneData._dominatingTeam) {
                    this.announce(instanceId, "++ " + LanguageEngine.getMsg("mDom_dominating", this._teams.get(instanceId).get(teamWithMostZones).getFullName(), mostZones));
                    zoneData._dominatingTeam = teamWithMostZones;
                    zoneData._holdingAllZonesFor = 0;
                }
                else {
                    final MultipleZoneData multipleZoneData = zoneData;
                    multipleZoneData._holdingAllZonesFor += this._zoneCheckInterval;
                }
                if (zoneData._holdingAllZonesFor >= this._holdZonesFor) {
                    this._teams.get(instanceId).get(teamWithMostZones).raiseScore(this._scoreForCapturingZone);
                    for (final PlayerEventInfo player4 : playersNearZones.get(teamWithMostZones)) {
                        this.getPlayerData(player4).raiseScore(this._scoreForCapturingZone);
                        this.setScoreStats(player4, this.getPlayerData(player4).getScore());
                        if (player4.isTitleUpdated()) {
                            player4.setTitle(this.getTitle(player4), true);
                            player4.broadcastTitleInfo();
                        }
                        CallbackManager.getInstance().playerScores(this.getEventType(), player4, this._scoreForCapturingZone);
                    }
                    zoneData._holdingAllZonesFor = 0;
                    if (this._holdZonesFor <= 5) {
                        continue;
                    }
                    this.announce(instanceId, "*** " + LanguageEngine.getMsg("mDom_score", this._teams.get(instanceId).get(teamWithMostZones).getTeamName(), mostZones));
                    if (!this.getBoolean("allowFireworkOnScore")) {
                        continue;
                    }
                    for (final NpcData npc : zoneData._zones) {
                        npc.broadcastSkillUse(npc, npc, 2024, 1);
                    }
                }
                else {
                    final int toHold = this._holdZonesFor - zoneData._holdingAllZonesFor;
                    boolean announce = false;
                    if (zoneData._holdingAllZonesFor == 0) {
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
                    this.announce(instanceId, "* " + LanguageEngine.getMsg("mDom_leftToScore", toHold, min ? "minutes" : "seconds", this._teams.get(instanceId).get(teamWithMostZones).getFullName()));
                }
            }
            else {
                if (zoneData._dominatingTeam != 0 && zoneData._holdingAllZonesFor > 0) {
                    this.announce(instanceId, "-- " + LanguageEngine.getMsg("mDom_lostDomination", this._teams.get(instanceId).get(zoneData._dominatingTeam).getFullName()));
                }
                zoneData._dominatingTeam = 0;
                zoneData._holdingAllZonesFor = 0;
            }
        }
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        final int owningTeam = this.getEventData(instance)._dominatingTeam;
        final int max = this.getEventData(instance)._holdingTeams.length;
        int count = 0;
        for (final int zone : this.getEventData(instance)._holdingTeams) {
            if (zone == owningTeam) {
                ++count;
            }
        }
        final String status = "<font color=ac9887>Zones dominated by:</font> <font color=" + EventManager.getInstance().getDarkColorForHtml(owningTeam) + ">" + EventManager.getInstance().getTeamName(owningTeam) + " team</font>" + ((owningTeam > 0) ? (" <font color=7f7f7f>(" + count + "/" + max + " zones)</font>") : "");
        return "<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center>" + status + "</td></tr></table>";
    }
    
    @Override
    public String getHtmlDescription() {
        final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
        if (desc != null) {
            this._htmlDescription = desc.getDescription(this.getConfigs());
        }
        else {
            this._htmlDescription = this.getInt("teamsCount") + " teams fighting against each other. ";
            this._htmlDescription = this._htmlDescription + "There are " + this.getInt("countOfZones") + " zones, each represented by an NPC. ";
            this._htmlDescription = this._htmlDescription + "In order to gain a score, your team must own at least " + this.getInt("zonesToOwnToScore") + " zones. ";
            this._htmlDescription += "To own a zone, your team must get close to each of these zones and kill all other enemies standing near the zone too. ";
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
        final int count = this.getInt("countOfZones");
        if (!map.checkForSpawns(SpawnType.Zone, -1, count)) {
            tb.append(this.addMissingSpawn(SpawnType.Zone, 0, count));
        }
        return tb.toString();
    }
    
    @Override
    protected ZoneData createEventData(final int instance) {
        return new MultipleZoneData(instance, this._zonesCount);
    }
    
    @Override
    protected MultipleZoneData getEventData(final int instance) {
        return (MultipleZoneData)this._matches.get(instance)._zoneData;
    }
    
    protected class MultipleZoneData extends ZoneData
    {
        private int _order;
        protected final NpcData[] _zones;
        protected final int[] _radiuses;
        protected final int[] _holdingTeams;
        protected final int[] _holdingTimes;
        protected int _holdingAllZonesFor;
        protected int _dominatingTeam;
        
        protected MultipleZoneData(final int instance, final int zonesCount) {
            super(instance);
            this._zones = new NpcData[zonesCount];
            this._radiuses = new int[zonesCount];
            this._holdingTeams = new int[zonesCount];
            this._holdingTimes = new int[zonesCount];
            this._dominatingTeam = 0;
            this._holdingAllZonesFor = 0;
            this._order = 0;
        }
        
        @Override
        protected void addZone(final NpcData zone, final int radius) {
            if (this._order < MassDomination.this._zonesCount) {
                this._zones[this._order] = zone;
                this._radiuses[this._order] = ((radius > 0) ? ((int)Math.pow(radius, 2.0)) : MassDomination.this._zoneRadius);
                this._holdingTeams[this._order] = 0;
                this._holdingTimes[this._order] = 0;
                ++this._order;
            }
            else {
                SunriseLoader.debug("too many zones for MultipleZoneData (" + this._order + "; " + MassDomination.this._zonesCount + ")");
            }
        }
    }
}
