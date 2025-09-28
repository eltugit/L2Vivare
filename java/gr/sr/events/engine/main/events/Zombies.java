package gr.sr.events.engine.main.events;

import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
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
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

public class Zombies extends HuntingGrounds {
    private static final int SURVIVOR_TEAM_ID = 1;
    private static final int ZOMBIE_TEAM_ID = 2;
    private final Map<Integer, Integer> _skillsForSurvivors;
    private final Map<Integer, Integer> _skillsForZombies;
    private String _zombiesCount;
    private int _zombieTransformId;
    protected int _zombieInactivityTime;
    private int _zombieMinLevel;
    private int _zombieMinPvps;
    private int _zombieKillScore;
    private int _survivorKillScore;
    protected int _zombiesInitialScore;

    public Zombies(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._skillsForSurvivors = new ConcurrentHashMap<Integer, Integer>();
        this._skillsForZombies = new ConcurrentHashMap<Integer, Integer>();
        this._zombieKillScore = 1;
        this._survivorKillScore = 1;
        this._zombiesInitialScore = 0;
        this.setRewardTypes(new RewardPosition[]{RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree});
    }

    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.removeConfig("skillsForAllPlayers");
        this.removeConfig("bowWeaponId");
        this.removeConfig("arrowItemId");
        this.removeConfig("teamsCount");
        this.removeConfig("createParties");
        this.removeConfig("maxPartySize");
        this.removeConfig("teamsCount");
        this.removeConfig("firstBloodMessage");
        this.removeConfig("waweRespawn");
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system for zombies.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("skillsForPlayers", "35101-1", "IDs of skills which will be given to every survivor (non zombie player) on the event. The purpose of this is to make all survivors equally strong. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35001-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("skillsForZombies", "35102-1", "IDs of skills which will be given to every zombie on the event. The purpose of this is to make all zombies equally strong. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35002-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("bowWeaponId", "271", "The ID of the bow item which will be given to the survivors (non zombies) and will be the only weapon most players will use during the event. This weapon kills zombies with just one hit."));
        this.addConfig(new ConfigModel("arrowItemId", "17", "The ID of the arrows which will be given to the player in the event."));
        this.addConfig(new ConfigModel("enableAmmoSystem", "true", "Enable/disable the ammo system based on player's mana. Player's max MP is defaultly modified by a custom passive skill and everytime a player shots and arrow, his MP decreases by a value which is calculated from the ammount of ammo. There is also a MP regeneration system - see the configs below.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("ammoAmmount", "10", "Works if ammo system is enabled. Specifies the max ammount of ammo every player can have."));
        this.addConfig(new ConfigModel("ammoRestoredPerTick", "1", "Works if ammo system is enabled. Defines the ammount of ammo given to every player each <font color=LEVEL>'ammoRegTickInterval'</font> (configurable) seconds."));
        this.addConfig(new ConfigModel("ammoRegTickInterval", "10", "Works if ammo system is enabled. Defines the interval of restoring player's ammo. The value is in seconds (eg. value 10 will give ammo every 10 seconds to every player - the ammount of restored ammo is configurable (config <font color=LEVEL>ammoRestoredPerTick</font>)."));
        this.addConfig(new ConfigModel("countOfZombies", "1/10", "Defines the count of players transformed to zombies in the start of the event. Format: #ZOMBIES/#PLAYERS - <font color=LEVEL>eg. 1/10</font> means there's <font color=LEVEL>1</font> zombie when there are <font color=LEVEL>10</font> players in the event (20 players - 2 zombies, 100 players - 10 zombies, ...). There's always at least one zombie in the event."));
        this.addConfig(new ConfigModel("zombieTransformId", "303", "The ID of transformation used to morph players into zombies."));
        this.addConfig(new ConfigModel("zombieInactivityTime", "300", "In seconds. If no player is killed (by zombie) during this time, one random player will be transformed into a zombie and respawned on Zombie respawn (away from other players). Write 0 to disable this feature."));
        this.addConfig(new ConfigModel("zombieMinLevel", "0", "The minimum level required to become a zombie IN THE START OF THE EVENT."));
        this.addConfig(new ConfigModel("zombieMinPvPs", "0", "The minimum count of pvps required to become a zombie IN THE START OF THE EVENT."));
        this.addConfig(new ConfigModel("zombieKillScore", "1", "The count of score points given to a zombie when he kills a player."));
        this.addConfig(new ConfigModel("survivorKillScore", "1", "The count of score points given to a survivor when he kills a zombie."));
        this.addConfig(new ConfigModel("zombiesInitialScore", "1", "The initial score given to every zombie who gets automatically transformed in the beginning of the event."));
    }

    @Override
    public void initEvent() {
        super.initEvent();
        this._bowItemId = this.getInt("bowWeaponId");
        this._arrowItemId = this.getInt("arrowItemId");
        this._ammoSystem = this.getBoolean("enableAmmoSystem");
        this._ammoAmmount = this.getInt("ammoAmmount");
        this._ammoRegPerTick = this.getInt("ammoRestoredPerTick");
        this._tickLength = this.getInt("ammoRegTickInterval");
        this._zombiesCount = this.getString("countOfZombies");
        this._zombieTransformId = this.getInt("zombieTransformId");
        this._zombieInactivityTime = this.getInt("zombieInactivityTime");
        this._zombieMinLevel = this.getInt("zombieMinLevel");
        this._zombieMinPvps = this.getInt("zombieMinPvPs");
        this._zombieKillScore = this.getInt("zombieKillScore");
        this._survivorKillScore = this.getInt("survivorKillScore");
        this._zombiesInitialScore = this.getInt("zombiesInitialScore");
        if (!this.getString("skillsForPlayers").equals("")) {
            final String[] splits = this.getString("skillsForPlayers").split(",");
            this._skillsForSurvivors.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForSurvivors.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            } catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForPlayers' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        if (!this.getString("skillsForZombies").equals("")) {
            final String[] splits = this.getString("skillsForZombies").split(",");
            this._skillsForZombies.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForZombies.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            } catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForZombies' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        this._tick = 0;
    }

    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this.createTeams(this._teamsCount = 2, type.getInstance().getId());
        return this._teamsCount;
    }

    @Override
    protected void createTeams(final int count, final int instanceId) {
        this.createNewTeam(instanceId, 1, "Survivors", "Survivors");
        this.createNewTeam(instanceId, 2, "Zombies", "Zombies");
    }

    @Override
    protected void dividePlayersToTeams(final int instanceId, final List<PlayerEventInfo> players, final int teamsCount) {
        for (final PlayerEventInfo pi : players) {
            pi.onEventStart(this);
            this._teams.get(instanceId).get(1).addPlayer(pi, true);
        }
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
            final TvTEventInstance match = this.createEventInstance(instance);
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

    protected void scheduleSelectZombies(final int instanceId, final long delay, final boolean firstRun, final int forceAddNewZombieCount) {
        if (delay == 0L) {
            CallBack.getInstance().getOut().executeTask(() -> {
                final List<PlayerEventInfo> newZombies = this.calculateZombies(instanceId, (forceAddNewZombieCount > 0) ? forceAddNewZombieCount : -1, firstRun);
                if (newZombies != null) {
                    for (PlayerEventInfo zombie : newZombies) {
                        this.transformToZombie(zombie);
                        try {
                            if (firstRun && this._zombiesInitialScore > 0) {
                                zombie.getEventTeam().raiseScore(this._zombiesInitialScore);
                                this.getPlayerData(zombie).raiseScore(this._zombiesInitialScore);
                            } else {
                                continue;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                final List<PlayerEventInfo> newZombies2 = this.calculateZombies(instanceId, (forceAddNewZombieCount > 0) ? forceAddNewZombieCount : -1, firstRun);
                if (newZombies2 != null) {
                    for (PlayerEventInfo zombie2 : newZombies2) {
                        this.transformToZombie(zombie2);
                        try {
                            if (firstRun && this._zombiesInitialScore > 0) {
                                zombie2.getEventTeam().raiseScore(this._zombiesInitialScore);
                                this.getPlayerData(zombie2).raiseScore(this._zombiesInitialScore);
                            } else {
                                continue;
                            }
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }, delay);
        }
    }

    protected List<PlayerEventInfo> calculateZombies(final int instanceId, int countToSpawn, final boolean start) {
        final int playersCount = this.getPlayers(instanceId).size();
        final int survivorsCount = this._teams.get(instanceId).get(1).getPlayers().size();
        final int zombiesCount = this._teams.get(instanceId).get(2).getPlayers().size();
        if (countToSpawn <= 0) {
            final int zombies = Integer.parseInt(this._zombiesCount.split("/")[0]);
            final int players = Integer.parseInt(this._zombiesCount.split("/")[1]);
            if (start) {
                countToSpawn = (int) Math.floor(playersCount / players * zombies);
                if (countToSpawn < 1) {
                    countToSpawn = 1;
                }
            } else {
                countToSpawn = (countToSpawn = (int) Math.floor(playersCount / players * zombies)) - zombiesCount;
            }
        }
        int i = 0;
        final List<PlayerEventInfo> newZombies = new LinkedList<PlayerEventInfo>();
        if (countToSpawn >= survivorsCount) {
            countToSpawn = survivorsCount - 1;
        }
        if (countToSpawn > 0) {
            for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                if (!start || (player.getLevel() >= this._zombieMinLevel && player.getPvpKills() >= this._zombieMinPvps)) {
                    newZombies.add(player);
                    if (++i >= countToSpawn) {
                        break;
                    }
                    continue;
                }
            }
        }
        return newZombies;
    }

    @Override
    protected void preparePlayers(final int instanceId, final boolean start) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            this.preparePlayer(player, start);
        }
    }

    protected void preparePlayer(final PlayerEventInfo player, final boolean start) {
        SkillData skill = null;
        if (player.getEventTeam().getTeamId() == 1) {
            if (start) {
                if (this._skillsForSurvivors != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForSurvivors.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
                ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                if (wpn != null) {
                    player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_R_HAND());
                }
                wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
                if (wpn != null) {
                    player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_L_HAND());
                }
                final ItemData flagItem = player.addItem(this._bowItemId, 1, false);
                player.equipItem(flagItem);
                player.addItem(this._arrowItemId, 400, false);
            } else {
                if (this._skillsForSurvivors != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForSurvivors.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.removeSkill(skill.getId());
                        }
                    }
                }
                final ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                if (wpn.exists()) {
                    final ItemData[] unequiped = player.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
                    player.destroyItemByItemId(this._bowItemId, 1);
                    player.inventoryUpdate(unequiped);
                }
            }
        } else if (player.getEventTeam().getTeamId() == 2) {
            if (start) {
                if (this._skillsForZombies != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForZombies.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
            } else if (this._skillsForZombies != null) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForZombies.entrySet()) {
                    skill = new SkillData(e.getKey(), e.getValue());
                    if (skill.exists()) {
                        player.removeSkill(skill.getId());
                    }
                }
            }
        }
    }

    protected void zombiesInactive(final int instanceId) {
        this.scheduleSelectZombies(instanceId, 0L, false, 1);
    }

    protected void transformToZombie(final PlayerEventInfo player) {
        this.preparePlayer(player, false);
        player.getEventTeam().removePlayer(player);
        this._teams.get(player.getInstanceId()).get(2).addPlayer(player, true);
        this.preparePlayer(player, true);
        player.transform(this._zombieTransformId);
        this.getEventData(player.getInstanceId()).setKillMade();
        if (this.checkIfAnyPlayersLeft(player.getInstanceId())) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                if (player.isOnline() && this.getMatch(player.getInstanceId()).isActive()) {
                    this.respawnPlayer(player, player.getInstanceId());
                    player.sendMessage("You will be respawned in 10 seconds.");
                }
                return;
            }, 10000L);
        }
        player.setTitle(this.getTitle(player), true);
    }

    protected void transformToPlayer(final PlayerEventInfo player, final boolean endOfEvent) {
        if (endOfEvent) {
            player.untransform(true);
        } else {
            try {
                if (player.getTeamId() == 2) {
                    this.preparePlayer(player, false);
                    player.untransform(true);
                    player.getEventTeam().removePlayer(player);
                    this._teams.get(player.getInstanceId()).get(1).addPlayer(player, true);
                    this.preparePlayer(player, true);
                    if (player.isDead()) {
                        CallBack.getInstance().getOut().scheduleGeneral(() -> {
                            if (player.isOnline()) {
                                this.respawnPlayer(player, player.getInstanceId());
                            }
                        }, 10000L);
                    }
                }
            } catch (Exception e) {
                SunriseLoader.debug("error while untransforming zombie:");
                this.clearEvent();
                e.printStackTrace();
            }
        }
    }

    protected void untransformAll(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (player.getTeamId() == 2) {
                this.transformToPlayer(player, true);
            }
        }
    }

    protected void setAllZombies(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            try {
                if (player.getTeamId() != 1) {
                    continue;
                }
                player.getEventTeam().removePlayer(player);
                this._teams.get(player.getInstanceId()).get(2).addPlayer(player, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfAnyPlayersLeft(final int instanceId) {
        synchronized (this._teams) {
            if (this._teams.get(instanceId).get(1).getPlayers().size() <= 0) {
                this.announce(instanceId, "All survivors have died!");
                this.endInstance(instanceId, true, true, false);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minScore = this.getInt("killsForReward");
        this.rewardAllPlayersFromTeam(-1, minScore, 0, 2);
    }

    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (pi.isAfk()) {
            return "AFK";
        }
        if (pi.getTeamId() == 2) {
            return "~ ZOMBIE ~";
        }
        return "Score: " + this.getPlayerData(pi).getScore();
    }

    @Override
    protected String getScorebar(final int instance) {
        final int count = this._teams.get(instance).size();
        final StringBuilder tb = new StringBuilder();
        for (final EventTeam team : this._teams.get(instance).values()) {
            tb.append(team.getTeamName() + ": " + team.getPlayers().size() + "  ");
        }
        if (count <= 3) {
            tb.append(LanguageEngine.getMsg("event_scorebar_time", this._matches.get(instance).getClock().getTime()));
        }
        return tb.toString();
    }

    @Override
    protected void clockTick() {
        ++this._tick;
        if (this._tick % this._tickLength != 0) {
            return;
        }
        if (this._ammoSystem) {
            int oneAmmoMp = 0;
            for (final TvTEventInstance match : this._matches.values()) {
                for (final PlayerEventInfo player : this.getPlayers(match.getInstance().getId())) {
                    if (player.getTeamId() == 1) {
                        try {
                            oneAmmoMp = player.getMaxMp() / this._ammoAmmount;
                            final int mpToRegenerate = this._ammoRegPerTick * oneAmmoMp;
                            final int currentMp = (int) player.getCurrentMp();
                            if (currentMp >= player.getMaxMp()) {
                                continue;
                            }
                            int toAdd = mpToRegenerate;
                            if (currentMp + mpToRegenerate > player.getMaxMp()) {
                                toAdd = player.getMaxMp() - currentMp;
                            }
                            player.setCurrentMp(currentMp + toAdd);
                        } catch (NullPointerException e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        if (player.getPlayersId() != target.getObjectId()) {
            if (player.getTeamId() == 2) {
                player.getEventTeam().raiseScore(this._zombieKillScore);
                player.getEventTeam().raiseKills(this._zombieKillScore);
                this.getPlayerData(player).raiseScore(this._zombieKillScore);
                this.getPlayerData(player).raiseKills(this._zombieKillScore);
                this.getPlayerData(player).raiseSpree(1);
            } else if (player.getTeamId() == 1) {
                player.getEventTeam().raiseScore(this._survivorKillScore);
                player.getEventTeam().raiseKills(this._survivorKillScore);
                this.getPlayerData(player).raiseScore(this._survivorKillScore);
                this.getPlayerData(player).raiseKills(this._survivorKillScore);
                this.getPlayerData(player).raiseSpree(1);
            }
            this.giveKillingSpreeReward(this.getPlayerData(player));
            if (player.isTitleUpdated()) {
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
            this.setScoreStats(player, this.getPlayerData(player).getScore());
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
        if (player.getTeamId() == 2) {
            if (this._waweRespawn) {
                this._waweScheduler.addPlayer(player);
            } else {
                this.scheduleRevive(player, this.getInt("resDelay") * 1000);
            }
        } else {
            this.transformToZombie(player);
        }
    }

    @Override
    public boolean onAttack(final CharacterData cha, final CharacterData target) {
        if (this._ammoSystem && cha.isPlayer() && target.isPlayer()) {
            final PlayerEventInfo player = cha.getEventInfo();
            if (player.getTeamId() == 1) {
                final int oneShotMp = player.getMaxMp() / this._ammoAmmount;
                if (player.getCurrentMp() < oneShotMp) {
                    player.sendMessage("Not enought MP.");
                    return false;
                }
                player.setCurrentMp((int) (player.getCurrentMp() - oneShotMp));
            }
        }
        return true;
    }

    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        return player.getTeamId() != 2 && (item.getItemId() != this._bowItemId || !item.isEquipped()) && !item.isWeapon() && super.canUseItem(player, item);
    }

    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        return this.getEventType() != EventType.Zombies && super.canUseSkill(player, skill);
    }

    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
        try {
            if (cha.isPlayer() && target.isPlayer()) {
                final PlayerEventInfo targetPlayer = target.getEventInfo();
                final PlayerEventInfo player = cha.getEventInfo();
                if (player.getTeamId() != targetPlayer.getTeamId()) {
                    targetPlayer.abortCasting();
                    targetPlayer.doDie(cha);
                }
            }
        } catch (NullPointerException ex) {
        }
    }

    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return item.getItemId() != this._bowItemId && player.getTeamId() != 2 && super.canDestroyItem(player, item);
    }

    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return false;
    }

    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        super.onDisconnect(player);
        this.scheduleSelectZombies(player.getInstanceId(), 0L, false, 0);
    }

    @Override
    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        int survivors = 0;
        int zombies = 0;
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            if (team.getTeamId() == 1) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    ++survivors;
                }
            }
            if (team.getTeamId() == 2) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    ++zombies;
                }
            }
        }
        if (zombies == 0) {
            return survivors >= 2;
        } else return zombies >= 1 && survivors >= 1;
    }

    @Override
    public void teleportPlayers(final int instanceId, final SpawnType type, final boolean ffa) {
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: ========================================");
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: STARTING TO TELEPORT PLAYERS (ffa = " + ffa + ")");
        }
        final boolean removeBuffs = this.getBoolean("removeBuffsOnStart");
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: removeBuffs = " + removeBuffs);
        }
        int i = 0;
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            final EventSpawn spawn = this.getSpawn(type, -1);
            if (spawn == null) {
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: ! Missing spawn for team " + ((this._teams.get(instanceId).size() == 1) ? -1 : player.getTeamId()) + ", map " + this._manager.getMap().getMapName() + ", event " + this.getEventType().getAltTitle() + " !!");
                }
                SunriseLoader.debug("Missing spawn for team " + ((this._teams.get(instanceId).size() == 1) ? -1 : player.getTeamId()) + ", map " + this._manager.getMap().getMapName() + ", event " + this.getEventType().getAltTitle() + " !!", Level.SEVERE);
            }
            if (spawn != null) {
                int radius = spawn.getRadius();
                if (radius == -1) {
                    radius = 50;
                }
                final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
                loc.addRadius(radius);
                player.teleport(loc, 0, false, instanceId);
                if (player.getOwner().isProcessingRequest()) {
                    player.getOwner().setActiveRequester(null);
                    player.getOwner().onTransactionResponse();
                }
                player.getOwner().leaveParty();
                if (SunriseLoader.detailedDebug) {
                    this.print("AbstractMainEvent: /// player " + player.getPlayersName() + " teleported to " + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + " (radius = " + radius + "), SPAWN ID " + spawn.getSpawnId() + ", SPAWN TEAM " + spawn.getSpawnTeam());
                }
            }
            if (removeBuffs) {
                player.removeBuffs();
            }
            ++i;
        }
        if (SunriseLoader.detailedDebug) {
            this.print("AbstractMainEvent: " + i + " PLAYERS TELEPORTED");
        }
        this.clearMapHistory(-1, type);
    }

    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new ZombiesEventPlayerData(player, this);
        return d;
    }

    @Override
    public ZombiesEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (ZombiesEventPlayerData) player.getEventData();
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
                        this.preparePlayers(match.getInstance().getId(), false);
                        this.untransformAll(match.getInstance().getId());
                    }
                }
            }
        } catch (Exception e) {
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
                player.setInstanceId(0);
                if (this._removeBuffsOnEnd) {
                    player.removeBuffs();
                }
                player.restoreData();
                player.destroyItemByItemId(this._bowItemId, 1);
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
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
        if (SunriseLoader.detailedDebug) {
            this.print("/// Event: respawning player " + pi.getPlayersName() + ", instance " + instance);
        }
        EventSpawn spawn = null;
        if (pi.getTeamId() == 1) {
            spawn = this.getSpawn(SpawnType.Regular, -1);
        } else if (pi.getTeamId() == 2) {
            spawn = this.getSpawn(SpawnType.Zombie, -1);
        }
        if (spawn != null) {
            final Loc loc = new Loc(spawn.getLoc().getX(), spawn.getLoc().getY(), spawn.getLoc().getZ());
            loc.addRadius(spawn.getRadius());
            pi.teleport(loc, 0, true, instance);
            pi.sendMessage(LanguageEngine.getMsg("event_respawned"));
        } else {
            this.debug("Error on respawnPlayer - no spawn type REGULAR/ZOMBIE, team " + pi.getTeamId() + " has been found. Event aborted.");
        }
    }

    @Override
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            } else {
                this._htmlDescription = "No information about this event yet.";
            }
        }
        return this._htmlDescription;
    }

    @Override
    public boolean allowsRejoinOnDisconnect() {
        return false;
    }

    @Override
    protected TvTEventData createEventData(final int instanceId) {
        return new ZombiesEventData(instanceId);
    }

    @Override
    protected ZombiesEventInstance createEventInstance(final InstanceData instance) {
        return new ZombiesEventInstance(instance);
    }

    @Override
    protected ZombiesEventData getEventData(final int instance) {
        return (ZombiesEventData) this._matches.get(instance)._data;
    }

    public class ZombiesEventPlayerData extends PvPEventPlayerData {
        public ZombiesEventPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event, new GlobalStatsModel(Zombies.this.getEventType()));
        }
    }

    protected class ZombiesEventInstance extends HGEventInstance {
        protected ZombiesEventInstance(final InstanceData instance) {
            super(instance);
        }

        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    Zombies.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (Zombies.this.checkPlayers(this._instance.getId())) {
                            Zombies.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            Zombies.this.setupTitles(this._instance.getId());
                            Zombies.this.removeStaticDoors(this._instance.getId());
                            Zombies.this.enableMarkers(this._instance.getId(), true);
                            Zombies.this.preparePlayers(this._instance.getId(), true);
                            Zombies.this.scheduleSelectZombies(this._instance.getId(), 10000L, true, 0);
                            Zombies.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Zombies.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        this._clock.startClock(Zombies.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        Zombies.this.untransformAll(this._instance.getId());
                        Zombies.this.setAllZombies(this._instance.getId());
                        if (!Zombies.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                final int minScore = Zombies.this.getInt("killsForReward");
                                Zombies.this.rewardAllPlayersFromTeam(this._instance.getId(), minScore, 0, 2);
                            }
                            Zombies.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Zombies.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            } catch (Throwable e) {
                e.printStackTrace();
                Zombies.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }

    private class InactivityTimer implements Runnable {
        final int _instanceId;
        ScheduledFuture<?> _future;

        InactivityTimer(final int instanceId) {
            this._future = null;
            this._instanceId = instanceId;
        }

        @Override
        public void run() {
            Zombies.this.zombiesInactive(this._instanceId);
        }

        public void schedule() {
            if (this._future != null) {
                this.abort();
            }
            this._future = CallBack.getInstance().getOut().scheduleGeneral(this, Zombies.this._zombieInactivityTime * 1000);
        }

        private void abort() {
            if (this._future != null) {
                this._future.cancel(false);
                this._future = null;
            }
        }
    }

    protected class ZombiesEventData extends HGEventData {
        protected InactivityTimer _inactivityTimer;

        public ZombiesEventData(final int instance) {
            super(instance);
            this._inactivityTimer = null;
        }

        private synchronized void startTimer() {
            if (this._inactivityTimer == null) {
                this._inactivityTimer = new InactivityTimer(this._instanceId);
            }
            this._inactivityTimer.schedule();
        }

        public void setKillMade() {
            if (Zombies.this._zombieInactivityTime > 0) {
                this.startTimer();
            }
        }
    }
}
