package gr.sr.events.engine.main.events;

import gr.sr.events.EventGame;
import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class Mutant extends Zombies {
    private static final int PLAYERS_TEAM_ID = 1;
    private static final int MUTANT_TEAM_ID = 2;
    private final Map<Integer, Integer> _skillsForPlayers;
    private final Map<Integer, Integer> _skillsForMutant;
    private String _mutantCount;
    private int _mutantTransformId;
    private int _mutantMinLevel;
    private int _mutanteMinPvps;
    private int _mutantWeaponId;
    private int _mutantKillScore;
    private int _playerKillScore;

    public Mutant(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._skillsForPlayers = new ConcurrentHashMap<Integer, Integer>();
        this._skillsForMutant = new ConcurrentHashMap<Integer, Integer>();
        this._mutantKillScore = 1;
        this._playerKillScore = 1;
        this.setRewardTypes(new RewardPosition[]{RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill});
    }

    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.removeConfig("minPlayers");
        this.addConfig(new ConfigModel("minPlayers", "3", "The minimum count of players required to start one instance of the event. Min for Mutant is 3 (2 players and one mutant)."));
        this.addInstanceTypeConfig(new ConfigModel("minPlayers", "3", "Count of players required to start this instance. If there's less players, then the instance tries to divide it's players to stronger instances (check out config <font color=LEVEL>joinStrongerInstIfNeeded</font>) and if it doesn't success (the config is set to false or all possible stronger instances are full), it will unregister the players from the event. Check out other configs related to  Min for mutant is 3."));
        this.removeConfig("skillsForAllPlayers");
        this.removeConfig("bowWeaponId");
        this.removeConfig("arrowItemId");
        this.removeConfig("teamsCount");
        this.removeConfig("createParties");
        this.removeConfig("maxPartySize");
        this.removeConfig("teamsCount");
        this.removeConfig("firstBloodMessage");
        this.removeConfig("waweRespawn");
        this.removeConfig("countOfZombies");
        this.removeConfig("zombieTransformId");
        this.removeConfig("zombieInactivityTime");
        this.removeConfig("zombieMinLevel");
        this.removeConfig("zombieMinPvPs");
        this.removeConfig("zombieKillScore");
        this.removeConfig("survivorKillScore");
        this.removeConfig("zombiesInitialScore");
        this.removeConfig("bowWeaponId");
        this.removeConfig("arrowItemId");
        this.removeConfig("enableAmmoSystem");
        this.removeConfig("ammoAmmount");
        this.removeConfig("ammoRestoredPerTick");
        this.removeConfig("ammoRegTickInterval");
        this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system for zombies.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("skillsForPlayers", "", "IDs of skills which will be given to every player, who is not a mutant in the event. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35001-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("skillsForMutant", "35103-1", "IDs of skills which will be given to every mutant in the event. This skill should contain stats which make the mutant extra strong. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35002-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("mutantWeaponId", "271", "The ID of the weapon which will be given to all mutants and will be the only weapon most the mutant will be able to use."));
        this.addConfig(new ConfigModel("countOfMutants", "1/10", "Defines the count of mutants in the the event. Format: #MUTANTS/#PLAYERS - <font color=LEVEL>eg. 1/10</font> means there's <font color=LEVEL>1</font> mutant when there are <font color=LEVEL>10</font> players in the event (20 players - 2 mutants, 100 players - 10 mutants, ...). There's always at least one mutant in the event."));
        this.addConfig(new ConfigModel("mutantTransformId", "303", "The ID of transformation used to morph players into zombies."));
        this.addConfig(new ConfigModel("mutantMinLevel", "0", "The minimum level required to become a zombie IN THE START OF THE EVENT."));
        this.addConfig(new ConfigModel("mutantMinPvPs", "0", "The minimum count of pvps required to become a zombie IN THE START OF THE EVENT."));
        this.addConfig(new ConfigModel("mutantKillScore", "1", "The count of score points given to a zombie when he kills a player."));
        this.addConfig(new ConfigModel("playerKillScore", "1", "The count of score points given to a survivor when he kills a zombie."));
    }

    @Override
    public void initEvent() {
        super.initEvent();
        this._mutantWeaponId = this.getInt("mutantWeaponId");
        this._mutantCount = this.getString("countOfMutants");
        this._mutantTransformId = this.getInt("mutantTransformId");
        this._mutantMinLevel = this.getInt("mutantMinLevel");
        this._mutanteMinPvps = this.getInt("mutantMinPvPs");
        this._mutantKillScore = this.getInt("mutantKillScore");
        this._playerKillScore = this.getInt("playerKillScore");
        if (!this.getString("skillsForPlayers").equals("")) {
            final String[] splits = this.getString("skillsForPlayers").split(",");
            this._skillsForPlayers.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForPlayers.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            } catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForPlayers' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        if (!this.getString("skillsForMutant").equals("")) {
            final String[] splits = this.getString("skillsForMutant").split(",");
            this._skillsForMutant.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForMutant.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            } catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForMutant' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
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
        this.createNewTeam(instanceId, 1, "Players", "Players");
        this.createNewTeam(instanceId, 2, "Mutants", "Mutants");
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

    protected void scheduleSelectMutants(final int instanceId, final long delay, final boolean firstRun, final int forceAddNewMutantCount) {
        if (delay == 0L) {
            CallBack.getInstance().getOut().executeTask(() -> {
                final List<PlayerEventInfo> newZombies = this.calculateMutants(instanceId, (forceAddNewMutantCount > 0) ? forceAddNewMutantCount : -1, firstRun);
                if (newZombies != null) {
                    for (PlayerEventInfo zombie : newZombies) {
                        this.transformToMutant(zombie);
                    }
                }
            });
        } else {
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                final List<PlayerEventInfo> newZombies2 = this.calculateMutants(instanceId, (forceAddNewMutantCount > 0) ? forceAddNewMutantCount : -1, firstRun);
                if (newZombies2 != null) {
                    for (PlayerEventInfo zombie : newZombies2) {
                        this.transformToMutant(zombie);
                    }
                }
            }, delay);
        }
    }

    protected List<PlayerEventInfo> calculateMutants(final int instanceId, int countToSpawn, final boolean start) {
        final int playersCount = this.getPlayers(instanceId).size();
        final int survivorsCount = this._teams.get(instanceId).get(1).getPlayers().size();
        final int mutantCount = this._teams.get(instanceId).get(2).getPlayers().size();
        if (countToSpawn <= 0) {
            final int mutants = Integer.parseInt(this._mutantCount.split("/")[0]);
            final int players = Integer.parseInt(this._mutantCount.split("/")[1]);
            if (start) {
                countToSpawn = (int) Math.floor(playersCount / players * mutants);
                if (countToSpawn < 1) {
                    countToSpawn = 1;
                }
            } else {
                countToSpawn = (countToSpawn = (int) Math.floor(playersCount / players * mutants)) - mutantCount;
            }
        }
        int i = 0;
        final List<PlayerEventInfo> newMutants = new LinkedList<PlayerEventInfo>();
        if (survivorsCount >= 2) {
            if (countToSpawn >= survivorsCount) {
                countToSpawn = survivorsCount - 1;
            }
            if (countToSpawn > 0) {
                for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                    if (!start || (player.getLevel() >= this._mutantMinLevel && player.getPvpKills() >= this._mutanteMinPvps)) {
                        newMutants.add(player);
                        if (++i >= countToSpawn) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        return newMutants;
    }

    @Override
    protected void preparePlayer(final PlayerEventInfo player, final boolean start) {
        SkillData skill = null;
        if (player.getEventTeam().getTeamId() == 1) {
            if (start) {
                if (this._skillsForPlayers != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForPlayers.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
            } else if (this._skillsForPlayers != null) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForPlayers.entrySet()) {
                    skill = new SkillData(e.getKey(), e.getValue());
                    if (skill.exists()) {
                        player.removeSkill(skill.getId());
                    }
                }
            }
        } else if (player.getEventTeam().getTeamId() == 2) {
            if (start) {
                if (this._skillsForMutant != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForMutant.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
                if (this._mutantWeaponId > 0) {
                    ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                    if (wpn != null) {
                        player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_R_HAND());
                    }
                    wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
                    if (wpn != null) {
                        player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_L_HAND());
                    }
                    final ItemData flagItem = player.addItem(this._mutantWeaponId, 1, false);
                    player.equipItem(flagItem);
                }
            } else {
                if (this._skillsForMutant != null) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForMutant.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.removeSkill(skill.getId());
                        }
                    }
                }
                if (this._mutantWeaponId > 0) {
                    final ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                    if (wpn.exists()) {
                        final ItemData[] unequiped = player.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
                        player.destroyItemByItemId(this._mutantWeaponId, 1);
                        player.inventoryUpdate(unequiped);
                    }
                }
            }
        }
    }

    protected void transformToMutant(final PlayerEventInfo player) {
        this.preparePlayer(player, false);
        player.getEventTeam().removePlayer(player);
        this._teams.get(player.getInstanceId()).get(2).addPlayer(player, true);
        this.preparePlayer(player, true);
        player.transform(this._mutantTransformId);
        this.getEventData(player.getInstanceId()).setKillMade();
        if (player.isDead()) {
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

    @Override
    protected void transformToPlayer(final PlayerEventInfo player, final boolean endOfEvent) {
        if (endOfEvent) {
            player.untransform(true);
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        } else {
            try {
                if (player.getTeamId() == 2) {
                    this.preparePlayer(player, false);
                    player.untransform(true);
                    player.getEventTeam().removePlayer(player);
                    this._teams.get(player.getInstanceId()).get(1).addPlayer(player, true);
                    this.preparePlayer(player, true);
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                    if (player.isDead()) {
                        CallBack.getInstance().getOut().scheduleGeneral(() -> {
                            if (player.isOnline()) {
                                this.respawnPlayer(player, player.getInstanceId());
                            }
                        }, 10000L);
                    }
                }
            } catch (Exception e) {
                SunriseLoader.debug("error while untransforming mutant:");
                this.clearEvent();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void untransformAll(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (player.getTeamId() == 2) {
                this.transformToPlayer(player, true);
            }
        }
    }

    protected void setAllPlayers(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            try {
                if (player.getTeamId() != 2) {
                    continue;
                }
                player.getEventTeam().removePlayer(player);
                this._teams.get(player.getInstanceId()).get(1).addPlayer(player, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minScore = this.getInt("killsForReward");
        this.rewardAllPlayersFromTeam(-1, minScore, 0, 1);
    }

    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (pi.isAfk()) {
            return "AFK";
        }
        if (pi.getTeamId() == 2) {
            return "~ MUTANT ~";
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
    }

    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        if (player.getPlayersId() != target.getObjectId()) {
            if (player.getTeamId() == 2) {
                player.getEventTeam().raiseScore(this._mutantKillScore);
                player.getEventTeam().raiseKills(this._mutantKillScore);
                this.getPlayerData(player).raiseScore(this._mutantKillScore);
                this.getPlayerData(player).raiseKills(this._mutantKillScore);
                this.getPlayerData(player).raiseSpree(1);
            } else if (player.getTeamId() == 1) {
                player.getEventTeam().raiseScore(this._playerKillScore);
                player.getEventTeam().raiseKills(this._playerKillScore);
                this.getPlayerData(player).raiseScore(this._playerKillScore);
                this.getPlayerData(player).raiseKills(this._playerKillScore);
                this.getPlayerData(player).raiseSpree(1);
            }
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
        if (killer.getEventInfo() != null) {
            if (player.getTeamId() == 1) {
                if (this._waweRespawn) {
                    this._waweScheduler.addPlayer(player);
                } else {
                    this.scheduleRevive(player, this.getInt("resDelay") * 1000);
                }
            } else {
                this.transformToPlayer(player, false);
                final PlayerEventInfo killerInfo = killer.getEventInfo();
                this.transformToMutant(killerInfo);
            }
        }
    }

    @Override
    public boolean onAttack(final CharacterData cha, final CharacterData target) {
        return true;
    }

    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        if (player.getTeamId() == 2) {
            if (item.getItemId() == this._mutantWeaponId && item.isEquipped()) {
                return false;
            }
            if (this._mutantWeaponId > 0 && item.isWeapon()) {
                return false;
            }
        }
        if (this.notAllovedItems != null && Arrays.binarySearch(this.notAllovedItems, item.getItemId()) >= 0) {
            player.sendMessage(LanguageEngine.getMsg("event_itemNotAllowed"));
            return false;
        }
        if (item.isPotion() && !this.getBoolean("allowPotions")) {
            return false;
        }
        if (item.isScroll()) {
            return false;
        }
        if (item.isPetCollar() && !this._allowPets) {
            player.sendMessage(LanguageEngine.getMsg("event_petsNotAllowed"));
            return false;
        }
        return super.canUseItem(player, item);
    }

    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
    }

    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return item.getItemId() != this._mutantWeaponId && player.getTeamId() != 2 && super.canDestroyItem(player, item);
    }

    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return false;
    }

    @Override
    public void onDisconnect(final PlayerEventInfo player) {
        super.onDisconnect(player);
        this.scheduleSelectMutants(player.getInstanceId(), 0L, false, 0);
    }

    @Override
    protected boolean checkIfEventCanContinue(final int instanceId, final PlayerEventInfo disconnectedPlayer) {
        int currentPlayers = 0;
        int currentMutants = 0;
        for (final EventTeam team : this._teams.get(instanceId).values()) {
            if (team.getTeamId() == 1) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    ++currentPlayers;
                }
            }
            if (team.getTeamId() == 2) {
                for (final PlayerEventInfo pi : team.getPlayers()) {
                    ++currentMutants;
                }
            }
        }
        if (currentMutants == 0) {
            return currentPlayers >= 3;
        }
        if (currentMutants == 1) {
            return currentPlayers >= 2;
        }
        if (currentPlayers + currentMutants >= 3) {
            final int mutants = Integer.parseInt(this._mutantCount.split("/")[0]);
            final int players = Integer.parseInt(this._mutantCount.split("/")[1]);
            int countToHaveMutants = (int) Math.floor(currentPlayers / players * mutants);
            if (countToHaveMutants < 1) {
                countToHaveMutants = 1;
            }
            int toUntransform = 0;
            if (currentMutants > countToHaveMutants) {
                toUntransform = currentMutants - countToHaveMutants;
            }
            if (toUntransform > 0) {
                for (final PlayerEventInfo mutant : this._teams.get(instanceId).get(2).getPlayers()) {
                    if (toUntransform <= 0) {
                        break;
                    }
                    this.transformToPlayer(mutant, false);
                    --toUntransform;
                }
            }
            return true;
        }
        return false;
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
        final EventPlayerData d = new MutantEventPlayerData(player, this);
        return d;
    }

    @Override
    public MutantEventPlayerData getPlayerData(final PlayerEventInfo player) {
        return (MutantEventPlayerData) player.getEventData();
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
                player.destroyItemByItemId(this._mutantWeaponId, 1);
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
        return new MutantEventData(instanceId);
    }

    @Override
    protected MutantEventInstance createEventInstance(final InstanceData instance) {
        return new MutantEventInstance(instance);
    }

    @Override
    protected MutantEventData getEventData(final int instance) {
        return (MutantEventData) this._matches.get(instance)._data;
    }

    public class MutantEventPlayerData extends ZombiesEventPlayerData {
        public MutantEventPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event);
        }
    }

    protected class MutantEventInstance extends ZombiesEventInstance {
        protected MutantEventInstance(final InstanceData instance) {
            super(instance);
        }

        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    Mutant.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (Mutant.this.checkPlayers(this._instance.getId())) {
                            Mutant.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            Mutant.this.setupTitles(this._instance.getId());
                            Mutant.this.removeStaticDoors(this._instance.getId());
                            Mutant.this.enableMarkers(this._instance.getId(), true);
                            Mutant.this.preparePlayers(this._instance.getId(), true);
                            Mutant.this.scheduleSelectMutants(this._instance.getId(), 10000L, true, 0);
                            Mutant.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        Mutant.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        this._clock.startClock(Mutant.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        Mutant.this.untransformAll(this._instance.getId());
                        Mutant.this.setAllPlayers(this._instance.getId());
                        if (!Mutant.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                final int minScore = Mutant.this.getInt("killsForReward");
                                Mutant.this.rewardAllPlayersFromTeam(this._instance.getId(), minScore, 0, 1);
                            }
                            Mutant.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    Mutant.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            } catch (Throwable e) {
                e.printStackTrace();
                Mutant.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }

    protected class MutantEventData extends ZombiesEventData {
        public MutantEventData(final int instance) {
            super(instance);
        }

        @Override
        public void setKillMade() {
        }
    }
}
