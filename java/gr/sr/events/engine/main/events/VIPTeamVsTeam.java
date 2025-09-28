package gr.sr.events.engine.main.events;

import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.stats.GlobalStatsModel;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.InstanceData;
import gr.sr.interf.delegate.PartyData;
import gr.sr.interf.delegate.SkillData;
import gr.sr.l2j.CallBack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class VIPTeamVsTeam extends TeamVsTeam
{
    private int _vipsCount;
    private int _pointsForKillingVip;
    private int _pointsForKillingNonVip;
    private int _chooseFromTopPercent;
    private String _transformId;
    private int _healingRadius;
    private int _healingInterval;
    private boolean _healingVisualEffect;
    private String _healingPowerHp;
    private String _healingPowerMp;
    private String _healingPowerCp;
    private boolean _isHealInPercentHp;
    private boolean _isHealInPercentMp;
    private boolean _isHealInPercentCp;
    private int _vipRespawnDelay;
    private Map<Integer, Integer> _skillsForVip;
    public VIPTvTPlayerData data;
    int tick;
    List<PlayerEventInfo> playersEffects;
    
    public VIPTeamVsTeam(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this.tick = 0;
        this.playersEffects = new LinkedList<PlayerEventInfo>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("vipsCount", "3", "The number of VIP players in each team."));
        this.addConfig(new ConfigModel("pointsForKillingVip", "5", "The number of score points obtained by killing a VIP player."));
        this.addConfig(new ConfigModel("pointsForKillingNonVip", "1", "The number of score points obtained by killing a NON VIP player. Useful when you want this event to be based only on killing VIPs."));
        this.addConfig(new ConfigModel("chooseVipFromTopPercent", "30", "The VIP players will be randomly selected from the top players (Level or PvPs, depends on <font color=LEVEL>divideToTeamsMethod</font> config) in the team. Use this config to specify (in percent) how many players will be 'marked as TOP'. FOr example, if you set this value to '30' and the team has 100 players, the VIPs will be randomly selected from the top 30 players in the team."));
        this.addConfig(new ConfigModel("transformationId", "0", "You can specify if the player, who becames VIP, will be transformed into a transformation (eg. Zariche). Use this format to select the transformation ID per each team: <font color=5C8D5F>TEAM_ID</font>-<font color=635694>TRANSFORMATION_ID</font>,<font color=5C8D5F>TEAM_ID</font>-<font color=635694>TRANSFORMATION_ID</font> (eg. <font color=5C8D5F>1</font>-<font color=635694>301</font>,<font color=5C8D5F>2</font>-<font color=635694>302</font> will make team 1 (blue) VIPs to transform into Zariches and team 2 (red) VIPs to transform into Akamanahs). Put 0 to disable this feature."));
        this.addConfig(new ConfigModel("minPlayers", "4", "The minimum count of players required to start one instance of the event. <font color=FF0000>Minimum 4 is required for this event, otherwise this event will not start!</font>"));
        this.addConfig(new ConfigModel("vipHealRadius", "800", "The max. radius in which the VIP player can heal all nearby players. Each player can be healed only by one VIP."));
        this.addConfig(new ConfigModel("healInterval", "3", "Put here how often will the player be healed by the VIP (HP/MP/CP heal). Value in seconds - setting it to eg. 3 will heal player each 3 seconds, if he's standing near the VIP. Put 0 to turn the healing off."));
        this.addConfig(new ConfigModel("healVisualEffect", "true", "Put true to show some visual effects for players standing near the VIP. Works only if teams count = 2.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("vipHpHealPower", "0.5%", "Put here how much will the player's HP be healed, if the player stands near his team's VIP. Value can be a decimal and can also end with % - that will make the value in percent."));
        this.addConfig(new ConfigModel("vipMpHealPower", "1%", "Put here how much will the player's MP be healed, if the player stands near his team's VIP. Value can be a decimal and can also end with % - that will make the value in percent."));
        this.addConfig(new ConfigModel("vipCpHealPower", "10", "Put here how much will the player's CP be healed, if the player stands near his team's VIP. Value can be a decimal and can also end with % - that will make the value in percent."));
        this.addConfig(new ConfigModel("vipSpecialSkills", "395-1,396-1,1374-1,1375-1,1376-1,7065-1", "You can specify which skills will be given to all VIPs here. Format - SKILLID-LEVEL (eg. 25-2 (skill id 25, lvl 2). Default: All hero skills <font color=4f4f4f>(395, 396, 1374, 1375, 1376)</font>; custom skill to slow to 110 speed + lower the power of heal skills done on the VIP (by 75%) + raise max CP (+30000) + CP reg rate (x2) <font color=4f4f4f>(7065)</font>.", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("vipRespawnDelay", "10", "You can specify the delay after which new VIPs will be selected, if the old vips died. In seconds."));
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        this._vipsCount = this.getInt("vipsCount");
        this._pointsForKillingVip = this.getInt("pointsForKillingVip");
        this._pointsForKillingNonVip = this.getInt("pointsForKillingNonVip");
        this._chooseFromTopPercent = this.getInt("chooseVipFromTopPercent");
        this._transformId = this.getString("transformationId");
        this._healingRadius = this.getInt("vipHealRadius");
        this._healingInterval = this.getInt("healInterval");
        if (this._teamsCount == 2) {
            this._healingVisualEffect = this.getBoolean("healVisualEffect");
        }
        else {
            this._healingVisualEffect = false;
        }
        this._healingPowerHp = this.getString("vipHpHealPower");
        this._healingPowerMp = this.getString("vipMpHealPower");
        this._healingPowerCp = this.getString("vipCpHealPower");
        this._isHealInPercentHp = this._healingPowerHp.endsWith("%");
        this._isHealInPercentMp = this._healingPowerMp.endsWith("%");
        this._isHealInPercentCp = this._healingPowerCp.endsWith("%");
        this._vipRespawnDelay = this.getInt("vipRespawnDelay") * 1000;
        final String skills = this.getString("vipSpecialSkills");
        this._skillsForVip.clear();
        if (skills != null && !skills.isEmpty()) {
            for (final String skill : skills.split(",")) {
                try {
                    this._skillsForVip.put(Integer.parseInt(skill.split("-")[0]), Integer.parseInt(skill.split("-")[1]));
                }
                catch (Exception e) {
                    SunriseLoader.debug("Wrong format for the vipSpecialSkills config of TvTA event.", Level.WARNING);
                    e.printStackTrace();
                    this._skillsForVip = null;
                    break;
                }
            }
        }
    }
    
    @Override
    public void runEvent() {
        super.runEvent();
    }
    
    protected void scheduleSelectVips(final int instance, final int teamId, final boolean eventStart, final boolean shortDelay) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: scheduling select vips for team id " + teamId + " in instance " + instance + ". event start = " + eventStart + ", short delay = " + shortDelay);
        }
        if (eventStart) {
            this.announce(instance, LanguageEngine.getMsg("vip_selectNew", this._vipRespawnDelay / 1000));
        }
        int delay = this._vipRespawnDelay;
        if (shortDelay) {
            delay /= 2;
        }
        CallBack.getInstance().getOut().scheduleGeneral(new SelectVipsTask(instance, teamId), delay);
    }
    
    protected synchronized void selectVips(final int instanceId, final int teamId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: selecting VIPs of instanceId " + instanceId + " for team " + teamId);
        }
        final List<PlayerEventInfo> newVips = new LinkedList<PlayerEventInfo>();
        final List<PlayerEventInfo> temp = new LinkedList<PlayerEventInfo>();
        List<PlayerEventInfo> possibleVips = new LinkedList<PlayerEventInfo>();
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            if ((teamId == -1 || teamId == team.getTeamId()) && !team.getPlayers().isEmpty()) {
                int currentVipsCount = 0;
                for (final PlayerEventInfo player : team.getPlayers()) {
                    if (this.getPlayerData(player).isVIP) {
                        ++currentVipsCount;
                    }
                    else {
                        temp.add(player);
                    }
                }
                int count = this._vipsCount - currentVipsCount;
                if (SunriseLoader.detailedDebug) {
                    this.print("Event: selecting vips: team " + team.getTeamName() + "(" + team.getTeamId() + ") needs " + count + " VIPs.");
                }
                final String s = this.getString("divideToTeamsMethod");
                Collections.sort(temp, EventManager.getInstance().compareByLevels);
                if (s.startsWith("PvPs")) {
                    Collections.sort(temp, EventManager.getInstance().compareByPvps);
                }
                int from = 0;
                int to = (int)Math.ceil(temp.size() * (this._chooseFromTopPercent / 100.0));
                for (int i = 0; count > 0 && i < temp.size(); ++i) {
                    possibleVips = temp.subList(from, Math.min(to + i, temp.size()));
                    Collections.shuffle(possibleVips);
                    for (final PlayerEventInfo possibleVip : possibleVips) {
                        if (possibleVip != null && !possibleVip.isDead() && !possibleVip.isAfk() && !this.getPlayerData(possibleVip).wasVIP) {
                            temp.remove(possibleVip);
                            newVips.add(possibleVip);
                            if (--count <= 0) {
                                break;
                            }
                            continue;
                        }
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("Event: selecting vips part 2, count = " + count);
                }
                if (count > 0) {
                    for (final PlayerEventInfo player2 : temp) {
                        this.getPlayerData(player2).wasVIP = false;
                    }
                    from = 0;
                    to = (int)Math.ceil(temp.size() * (this._chooseFromTopPercent / 100.0));
                    for (int i = 0; count > 0 && i < temp.size(); ++i) {
                        possibleVips = temp.subList(from, Math.min(to + i, temp.size()));
                        Collections.shuffle(possibleVips);
                        for (final PlayerEventInfo possibleVip : possibleVips) {
                            if (possibleVip != null && !possibleVip.isDead() && !possibleVip.isAfk() && !this.getPlayerData(possibleVip).wasVIP) {
                                temp.remove(possibleVip);
                                newVips.add(possibleVip);
                                if (--count <= 0) {
                                    break;
                                }
                                continue;
                            }
                        }
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    this.print("Event: selecting vips part 3, count = " + count);
                }
                if (count > 0) {
                    this.scheduleSelectVips(instanceId, team.getTeamId(), false, true);
                }
                temp.clear();
            }
        }
        for (final PlayerEventInfo player3 : newVips) {
            this.markVip(player3);
            final EventSpawn spawn = this.getSpawn(SpawnType.VIP, player3.getTeamId());
            if (spawn == null) {
                SunriseLoader.debug("Missing spawn VIP for team " + ((this._teams.get(instanceId).size() == 1) ? -1 : player3.getTeamId()) + ", map " + this._manager.getMap().getMapName() + ", event " + this.getEventType().getAltTitle() + " !!", Level.SEVERE);
            }
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            player3.teleport(loc, 0, true, instanceId);
            if (this.getBoolean("removeBuffsOnStart")) {
                player3.removeBuffs();
            }
        }
    }
    
    private void transform(final PlayerEventInfo player) {
        if (this._transformId == null || this._transformId.equals("0")) {
            return;
        }
        int id = 0;
        try {
            id = Integer.parseInt(this._transformId);
        }
        catch (Exception e) {
            id = 0;
        }
        if (id > 0) {
            player.transform(id);
            return;
        }
        final String[] split;
        final String[] s = split = this._transformId.split(",");
        for (final String d : split) {
            try {
                if (Integer.parseInt(d.split("-")[0]) == player.getTeamId()) {
                    player.transform(Integer.parseInt(d.split("-")[1]));
                }
            }
            catch (Exception ex) {}
        }
    }
    
    protected void markVip(final PlayerEventInfo player) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: marking " + player.getPlayersName() + " in instance " + player.getInstanceId() + " as VIP.");
        }
        if (!this.getPlayerData(player).isVip()) {
            this.transform(player);
            this.vipSkills(player, true);
            player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
            this.getPlayerData(player).setVIP(true);
            if (!this.getEventData(player.getInstanceId())._vips.containsKey(player.getTeamId())) {
                this.getEventData(player.getInstanceId())._vips.put(player.getTeamId(), new LinkedList<PlayerEventInfo>());
            }
            this.getEventData(player.getInstanceId())._vips.get(player.getTeamId()).add(player);
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
            this.announce(player.getInstanceId(), "* " + LanguageEngine.getMsg("vip_becomeVip", player.getPlayersName()), player.getTeamId());
        }
    }
    
    protected void vipSkills(final PlayerEventInfo player, final boolean add) {
        if (this._skillsForVip != null) {
            for (final Map.Entry<Integer, Integer> sk : this._skillsForVip.entrySet()) {
                final SkillData skill = new SkillData(sk.getKey(), sk.getValue());
                if (add) {
                    player.addSkill(skill, false);
                }
                else {
                    if (player.isHero()) {
                        if (skill.getId() == 395 || skill.getId() == 396 || skill.getId() == 1374 || skill.getId() == 1375) {
                            continue;
                        }
                        if (skill.getId() == 1376) {
                            continue;
                        }
                    }
                    player.removeSkill(skill.getId());
                }
            }
        }
        if (add) {
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }
    
    protected void cleanVip(final PlayerEventInfo player) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: removing/cleaning " + player.getPlayersName() + " in instance " + player.getInstanceId() + " from VIP.");
        }
        if (this.getPlayerData(player).isVip()) {
            this.vipSkills(player, false);
            player.untransform(true);
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
            try {
                this.getPlayerData(player).setVIP(false);
                if (this.getEventData(player.getInstanceId())._vips.containsKey(player.getTeamId())) {
                    this.getEventData(player.getInstanceId())._vips.get(player.getTeamId()).remove(player);
                }
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void onEventEnd() {
        super.onEventEnd();
    }
    
    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (this._hideTitles) {
            return "";
        }
        if (this.getPlayerData(pi).isVip()) {
            return "[VIP]";
        }
        if (pi.isAfk()) {
            return "AFK";
        }
        return LanguageEngine.getMsg("event_title_scoredeath", this.getPlayerData(pi).getScore(), this.getPlayerData(pi).getDeaths());
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        final PlayerEventInfo targetInfo = target.getEventInfo();
        if (player.getTeamId() != targetInfo.getTeamId()) {
            this.tryFirstBlood(player);
            if (this.getPlayerData(targetInfo).isVip()) {
                this.giveOnKillReward(player);
                player.getEventTeam().raiseScore(this._pointsForKillingVip);
                player.getEventTeam().raiseKills(this._pointsForKillingVip);
                this.getPlayerData(player).raiseScore(this._pointsForKillingVip);
                this.getPlayerData(player).raiseKills(this._pointsForKillingVip);
                this.getPlayerData(player).raiseSpree(1);
                this.giveKillingSpreeReward(this.getPlayerData(player));
                CallbackManager.getInstance().playerKillsVip(this.getEventType(), player, target.getEventInfo());
            }
            else {
                this.giveOnKillReward(player);
                player.getEventTeam().raiseScore(this._pointsForKillingNonVip);
                player.getEventTeam().raiseKills(this._pointsForKillingNonVip);
                this.getPlayerData(player).raiseScore(this._pointsForKillingNonVip);
                this.getPlayerData(player).raiseKills(this._pointsForKillingNonVip);
                this.getPlayerData(player).raiseSpree(1);
                this.giveKillingSpreeReward(this.getPlayerData(player));
                CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
            }
            if (player.isTitleUpdated()) {
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            this.setScoreStats(player, this.getPlayerData(player).getScore());
            this.setKillsStats(player, this.getPlayerData(player).getKills());
        }
        if (this.getPlayerData(targetInfo).isVip()) {
            this.announceToAllTeamsBut(targetInfo.getInstanceId(), "[+] " + LanguageEngine.getMsg("vip_vipDied", targetInfo.getPlayersName(), targetInfo.getEventTeam().getTeamName()), targetInfo.getTeamId());
            this.announce(targetInfo.getInstanceId(), "[-] " + LanguageEngine.getMsg("vip_vipKilled", targetInfo.getPlayersName()), targetInfo.getTeamId());
            this.cleanVip(targetInfo);
            this.scheduleSelectVips(targetInfo.getInstanceId(), targetInfo.getTeamId(), false, false);
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
    public int allowTransformationSkill(final PlayerEventInfo playerEventInfo, final SkillData skillData) {
        if (this._skillsForVip.containsKey(skillData.getId())) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public void playerWentAfk(final PlayerEventInfo player, final boolean warningOnly, final int afkTime) {
        if (warningOnly) {
            player.sendMessage(LanguageEngine.getMsg("event_afkWarning_kill", PlayerEventInfo.AFK_WARNING_DELAY / 1000, PlayerEventInfo.AFK_KICK_DELAY / 1000));
        }
        else if (this._matches.get(player.getInstanceId())._state == EventState.END && this.getPlayerData(player).isVIP) {
            this.announce(player.getInstanceId(), "* " + LanguageEngine.getMsg("vip_vipAfk", player.getPlayersName()), player.getTeamId());
            this.announceToAllTeamsBut(player.getInstanceId(), "* " + LanguageEngine.getMsg("vip_enemyVipAfk", player.getPlayersName()), player.getTeamId());
            player.doDie();
        }
    }
    
    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new VIPTvTPlayerData(player, this);
        return d;
    }
    
    @Override
    public VIPTvTPlayerData getPlayerData(final PlayerEventInfo player) {
        return (VIPTvTPlayerData)player.getEventData();
    }
    
    @Override
    public synchronized void clearEvent(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called CLEAREVENT for instance " + instanceId);
        }
        try {
            if (this._matches != null) {
                for (final TvTEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        match.abort();
                        for (final PlayerEventInfo player : this.getPlayers(match.getInstance().getId())) {
                            if (this.getPlayerData(player).isVip()) {
                                this.cleanVip(player);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final PlayerEventInfo player2 : this.getPlayers(instanceId)) {
            if (player2.isOnline()) {
                if (player2.isParalyzed()) {
                    player2.setIsParalyzed(false);
                }
                if (player2.isImmobilized()) {
                    player2.unroot();
                }
                if (!player2.isGM()) {
                    player2.setIsInvul(false);
                }
                player2.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
                player2.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
                player2.removeRadarAllMarkers();
                player2.setInstanceId(0);
                if (this._removeBuffsOnEnd) {
                    player2.removeBuffs();
                }
                player2.restoreData();
                player2.teleport(player2.getOrigLoc(), 0, true, 0);
                player2.sendMessage(LanguageEngine.getMsg("event_teleportBack"));
                if (player2.getParty() != null) {
                    final PartyData party = player2.getParty();
                    party.removePartyMember(player2);
                }
                player2.broadcastUserInfo();
            }
        }
        this.clearPlayers(true, instanceId);
    }
    
    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        if (this.getPlayerData(player).isVip()) {
            this.cleanVip(player);
            this.announce(player.getInstanceId(), "* " + LanguageEngine.getMsg("vip_vipDisconnected", player.getPlayersName(), player.getEventTeam().getTeamName()));
            this.scheduleSelectVips(player.getInstanceId(), player.getTeamId(), false, true);
        }
        super.onDisconnect(player);
    }
    
    @Override
    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        int teamsOn = 0;
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            int temp = 0;
            for (final PlayerEventInfo pi : team.getPlayers()) {
                if (pi != null && pi.isOnline()) {
                    ++temp;
                }
            }
            if (temp >= 2) {
                ++teamsOn;
            }
        }
        return teamsOn >= 2;
    }
    
    @Override
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: respawning player " + pi.getPlayersName() + ", instance " + instance);
        }
        EventSpawn spawn;
        if (this.getPlayerData(pi).isVip()) {
            spawn = this.getSpawn(SpawnType.VIP, pi.getTeamId());
        }
        else {
            spawn = this.getSpawn(SpawnType.Regular, pi.getTeamId());
        }
        if (spawn != null) {
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            pi.teleport(loc, 0, true, instance);
            pi.sendMessage(LanguageEngine.getMsg("event_respawned"));
        }
        else {
            this.debug("Error on respawnPlayer - no spawn type REGULAR or VIP, team " + pi.getTeamId() + " has been found. Event aborted.");
        }
    }
    
    @Override
    protected void clockTick() {
        final int healingRadius = (int)Math.pow(this._healingRadius, 2.0);
        ++this.tick;
        for (final TvTEventInstance match : this._matches.values()) {
            for (final Map.Entry<Integer, List<PlayerEventInfo>> e : this.getEventData(match.getInstance().getId())._vips.entrySet()) {
                final int teamId = e.getKey();
                for (final PlayerEventInfo vip : e.getValue()) {
                    for (final PlayerEventInfo player : this.getPlayers(match.getInstance().getId())) {
                        if (player.getTeamId() == teamId && !this.getPlayerData(player).isVIP && this.getPlayerData(player).canHeal() && !player.isDead() && player.getPlanDistanceSq(vip.getX(), vip.getY()) <= healingRadius) {
                            if (this._healingVisualEffect) {
                                this.playersEffects.add(player);
                            }
                            if (this._healingInterval <= 0 || this.tick % this._healingInterval != 0) {
                                continue;
                            }
                            if (this._isHealInPercentHp) {
                                double value = Double.parseDouble(this._healingPowerHp.substring(0, this._healingPowerHp.length() - 1));
                                if (value > 0.0 && player.getCurrentHp() < player.getMaxHp()) {
                                    value *= player.getMaxHp() / 100;
                                    player.setCurrentHp((int)(player.getCurrentHp() + value));
                                }
                            }
                            else {
                                final double value = Double.parseDouble(this._healingPowerHp);
                                if (value > 0.0 && player.getCurrentHp() < player.getMaxHp()) {
                                    player.setCurrentHp((int)(player.getCurrentHp() + value));
                                }
                            }
                            if (this._isHealInPercentMp) {
                                double value = Double.parseDouble(this._healingPowerMp.substring(0, this._healingPowerMp.length() - 1));
                                if (value > 0.0 && player.getCurrentMp() < player.getMaxMp()) {
                                    value *= player.getMaxMp() / 100;
                                    player.setCurrentMp((int)(player.getCurrentMp() + value));
                                }
                            }
                            else {
                                final double value = Double.parseDouble(this._healingPowerMp);
                                if (value > 0.0 && player.getCurrentMp() < player.getMaxMp()) {
                                    player.setCurrentMp((int)(player.getCurrentMp() + value));
                                }
                            }
                            if (this._isHealInPercentCp) {
                                double value = Double.parseDouble(this._healingPowerCp.substring(0, this._healingPowerCp.length() - 1));
                                if (value <= 0.0 || player.getCurrentCp() >= player.getMaxCp()) {
                                    continue;
                                }
                                value *= player.getMaxCp() / 100;
                                player.setCurrentCp((int)(player.getCurrentCp() + value));
                            }
                            else {
                                final double value = Double.parseDouble(this._healingPowerCp);
                                if (value <= 0.0 || player.getCurrentCp() >= player.getMaxCp()) {
                                    continue;
                                }
                                player.setCurrentCp((int)(player.getCurrentCp() + value));
                            }
                        }
                    }
                }
            }
            for (final PlayerEventInfo player2 : this.getPlayers(match.getInstance().getId())) {
                this.getPlayerData(player2).tickEnd();
                if (this.playersEffects.contains(player2) || this.getPlayerData(player2).isVip()) {
                    this.startPlayerEffects(player2, player2.getTeamId());
                }
                else {
                    this.startPlayerEffects(player2, 0);
                }
            }
            this.playersEffects.clear();
        }
    }
    
    private void startPlayerEffects(final PlayerEventInfo player, final int teamId) {
        if (teamId == 1) {
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
            player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
        }
        else if (teamId == 2) {
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
            player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
        }
        else {
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REDCIRCLE());
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_IMPRISIONING_1());
        }
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
    public String getMissingSpawns(final EventMap map) {
        final StringBuilder tb = new StringBuilder();
        for (int i = 0; i < this.getTeamsCount(); ++i) {
            if (!map.checkForSpawns(SpawnType.Regular, i + 1, 1)) {
                tb.append(this.addMissingSpawn(SpawnType.Regular, i + 1, 1));
            }
            if (!map.checkForSpawns(SpawnType.VIP, i + 1, 1)) {
                tb.append(this.addMissingSpawn(SpawnType.VIP, i + 1, 1));
            }
        }
        return tb.toString();
    }
    
    @Override
    protected TvTEventData createEventData(final int instanceId) {
        return new TvTVIPEventData(instanceId);
    }
    
    @Override
    protected VIPEventInstance createEventInstance(final InstanceData instance) {
        return new VIPEventInstance(instance);
    }
    
    @Override
    protected TvTVIPEventData getEventData(final int instance) {
        return (TvTVIPEventData)this._matches.get(instance)._data;
    }
    
    public class VIPTvTPlayerData extends PvPEventPlayerData
    {
        protected boolean isVIP;
        protected boolean wasVIP;
        private boolean tickHealed;
        
        public VIPTvTPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event, new GlobalStatsModel(VIPTeamVsTeam.this.getEventType()));
            this.isVIP = false;
            this.wasVIP = false;
            this.tickHealed = false;
        }
        
        public boolean isVip() {
            return this.isVIP;
        }
        
        public boolean wasVIP() {
            return this.wasVIP;
        }
        
        public void setVIP(final boolean b) {
            if (this.isVIP && !b) {
                this.wasVIP = true;
            }
            this.isVIP = b;
        }
        
        public boolean canHeal() {
            return !this.tickHealed && (this.tickHealed = true);
        }
        
        public void tickEnd() {
            this.tickHealed = false;
        }
    }
    
    private class SelectVipsTask implements Runnable
    {
        final int instance;
        final int teamId;
        
        public SelectVipsTask(final int instance, final int teamId) {
            this.instance = instance;
            this.teamId = teamId;
        }
        
        @Override
        public void run() {
            if (VIPTeamVsTeam.this._matches.get(this.instance)._state == EventState.END) {
                try {
                    VIPTeamVsTeam.this.selectVips(this.instance, this.teamId);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    if (SunriseLoader.detailedDebug) {
                        VIPTeamVsTeam.this.print("Event: error while selecting new vips: " + SunriseLoader.getTraceString(e.getStackTrace()));
                    }
                    VIPTeamVsTeam.this.announce("Sorry, an error occured in this event.");
                    VIPTeamVsTeam.this.clearEvent();
                }
            }
        }
    }
    
    protected class VIPEventInstance extends TvTEventInstance
    {
        protected VIPEventInstance(final InstanceData instance) {
            super(instance);
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    VIPTeamVsTeam.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (VIPTeamVsTeam.this.checkPlayers(this._instance.getId())) {
                            VIPTeamVsTeam.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            VIPTeamVsTeam.this.setupTitles(this._instance.getId());
                            VIPTeamVsTeam.this.enableMarkers(this._instance.getId(), true);
                            VIPTeamVsTeam.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        VIPTeamVsTeam.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        VIPTeamVsTeam.this.scheduleSelectVips(this._instance.getId(), -1, true, false);
                        this._clock.startClock(VIPTeamVsTeam.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        for (final PlayerEventInfo player : VIPTeamVsTeam.this.getPlayers(this._instance.getId())) {
                            if (VIPTeamVsTeam.this.getPlayerData(player).isVip()) {
                                VIPTeamVsTeam.this.cleanVip(player);
                            }
                        }
                        this.setNextState(EventState.INACTIVE);
                        if (!VIPTeamVsTeam.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                VIPTeamVsTeam.this.rewardAllTeams(this._instance.getId(), 0, VIPTeamVsTeam.this.getInt("killsForReward"));
                            }
                            VIPTeamVsTeam.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    VIPTeamVsTeam.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                VIPTeamVsTeam.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class TvTVIPEventData extends TvTEventData
    {
        protected final Map<Integer, List<PlayerEventInfo>> _vips;
        
        public TvTVIPEventData(final int instance) {
            super(instance);
            (this._vips = new ConcurrentHashMap<Integer, List<PlayerEventInfo>>()).clear();
        }
    }
}
