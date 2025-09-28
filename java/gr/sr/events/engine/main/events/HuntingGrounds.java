package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.RewardPosition;
import gr.sr.events.engine.base.SpawnType;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class HuntingGrounds extends TeamVsTeam
{
    protected int _tick;
    protected int _bowItemId;
    protected int _arrowItemId;
    protected boolean _ammoSystem;
    protected int _ammoAmmount;
    protected int _ammoRegPerTick;
    protected int _tickLength;
    private final Map<Integer, Integer> _skillsForAll;
    
    public HuntingGrounds(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._skillsForAll = new ConcurrentHashMap<Integer, Integer>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Winner, RewardPosition.Looser, RewardPosition.Tie, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.OnKill, RewardPosition.KillingSpree });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("skillsForAllPlayers", "35100-1", "IDs of skills which will be given to players on the event. The purpose of this is to make all players equally strong. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35000-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("bowWeaponId", "271", "The ID of the bow item which will be given to all players and will be the only weapon most players will use during the event. This weapon kills players with just one hit."));
        this.addConfig(new ConfigModel("arrowItemId", "17", "The ID of the arrows which will be given to the player in the event."));
        this.addConfig(new ConfigModel("enableAmmoSystem", "true", "Enable/disable the ammo system based on player's mana. Player's max MP is defaultly modified by a custom passive skill and everytime a player shots and arrow, his MP decreases by a value which is calculated from the ammount of ammo. There is also a MP regeneration system - see the configs below.", ConfigModel.InputType.Boolean));
        this.addConfig(new ConfigModel("ammoAmmount", "10", "Works if ammo system is enabled. Specifies the max ammount of ammo every player can have."));
        this.addConfig(new ConfigModel("ammoRestoredPerTick", "1", "Works if ammo system is enabled. Defines the ammount of ammo given to every player each <font color=LEVEL>'ammoRegTickInterval'</font> (configurable) seconds."));
        this.addConfig(new ConfigModel("ammoRegTickInterval", "10", "Works if ammo system is enabled. Defines the interval of restoring player's ammo. The value is in seconds (eg. value 10 will give ammo every 10 seconds to every player - the ammount of restored ammo is configurable (config <font color=LEVEL>ammoRestoredPerTick</font>)."));
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
        if (!this.getString("skillsForAllPlayers").equals("")) {
            final String[] splits = this.getString("skillsForAllPlayers").split(",");
            this._skillsForAll.clear();
            try {
                for (final String split : splits) {
                    final String id = split.split("-")[0];
                    final String level = split.split("-")[1];
                    this._skillsForAll.put(Integer.parseInt(id), Integer.parseInt(level));
                }
            }
            catch (Exception e) {
                SunriseLoader.debug("Error while loading config 'skillsForAllPlayers' for event " + this.getEventName() + " - " + e.toString(), Level.SEVERE);
            }
        }
        this._tick = 0;
    }
    
    protected void preparePlayers(final int instanceId, final boolean start) {
    }
    
    protected void handleSkills(final int instanceId, final boolean add) {
        if (this._skillsForAll != null) {
            SkillData skill = null;
            for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
                final PlayerEventInfo element = player;
                if (add) {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.addSkill(skill, false);
                        }
                    }
                    player.sendSkillList();
                }
                else {
                    for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                        skill = new SkillData(e.getKey(), e.getValue());
                        if (skill.exists()) {
                            player.removeSkill(skill.getId());
                        }
                    }
                }
            }
        }
    }
    
    protected void handleWeapons(final int instanceId, final boolean equip) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (equip) {
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
            }
            else {
                final ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                if (!wpn.exists()) {
                    continue;
                }
                final ItemData[] unequiped = player.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
                player.destroyItemByItemId(this._bowItemId, 1);
                player.inventoryUpdate(unequiped);
            }
        }
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
                    try {
                        oneAmmoMp = player.getMaxMp() / this._ammoAmmount;
                        final int mpToRegenerate = this._ammoRegPerTick * oneAmmoMp;
                        final int currentMp = (int)player.getCurrentMp();
                        if (currentMp >= player.getMaxMp()) {
                            continue;
                        }
                        int toAdd = mpToRegenerate;
                        if (currentMp + mpToRegenerate > player.getMaxMp()) {
                            toAdd = player.getMaxMp() - currentMp;
                        }
                        player.setCurrentMp(currentMp + toAdd);
                    }
                    catch (NullPointerException e) {}
                }
            }
        }
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        if (player.getTeamId() != target.getEventInfo().getTeamId()) {
            this.tryFirstBlood(player);
            this.giveOnKillReward(player);
            player.getEventTeam().raiseScore(1);
            player.getEventTeam().raiseKills(1);
            this.getPlayerData(player).raiseScore(1);
            this.getPlayerData(player).raiseKills(1);
            this.getPlayerData(player).raiseSpree(1);
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
        if (this._waweRespawn) {
            this._waweScheduler.addPlayer(player);
        }
        else {
            this.scheduleRevive(player, this.getInt("resDelay") * 1000);
        }
    }
    
    @Override
    public boolean onAttack(final CharacterData cha, final CharacterData target) {
        if (this._ammoSystem && cha.isPlayer() && target.isPlayer()) {
            final PlayerEventInfo player = cha.getEventInfo();
            final int oneShotMp = player.getMaxMp() / this._ammoAmmount;
            if (player.getCurrentMp() < oneShotMp) {
                player.sendMessage("Not enought MP.");
                return false;
            }
            player.setCurrentMp((int)(player.getCurrentMp() - oneShotMp));
        }
        return true;
    }
    
    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        return (item.getItemId() != this._bowItemId || !item.isEquipped()) && !item.isWeapon() && super.canUseItem(player, item);
    }
    
    @Override
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
        try {
            if (cha.isPlayer() && target.isPlayer()) {
                final PlayerEventInfo targetPlayer = target.getEventInfo();
                targetPlayer.abortCasting();
                targetPlayer.doDie(cha);
            }
        }
        catch (NullPointerException ex) {}
    }
    
    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        return item.getItemId() != this._bowItemId && super.canDestroyItem(player, item);
    }
    
    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return false;
    }
    
    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        return false;
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
                        this.handleWeapons(match.getInstance().getId(), false);
                        this.handleSkills(match.getInstance().getId(), false);
                        this.preparePlayers(match.getInstance().getId(), false);
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
    public String getHtmlDescription() {
        if (this._htmlDescription == null) {
            final EventDescription desc = EventDescriptionSystem.getInstance().getDescription(this.getEventType());
            if (desc != null) {
                this._htmlDescription = desc.getDescription(this.getConfigs());
            }
            else {
                this._htmlDescription = this.getInt("teamsCount") + " teams fighting against each other. ";
                this._htmlDescription += "Gain score by killing your opponents";
                if (this.getInt("killsForReward") > 0) {
                    this._htmlDescription = this._htmlDescription + " (at least " + this.getInt("killsForReward") + " kill(s) is required to receive a reward)";
                }
                if (this.getBoolean("waweRespawn")) {
                    this._htmlDescription = this._htmlDescription + " and dead players are resurrected by an advanced wawe-spawn engine each " + this.getInt("resDelay") + " seconds";
                }
                else {
                    this._htmlDescription = this._htmlDescription + " and if you die, you will be resurrected in " + this.getInt("resDelay") + " seconds";
                }
                if (this.getBoolean("createParties")) {
                    this._htmlDescription += ". The event automatically creates parties on start";
                }
                this._htmlDescription += ".";
            }
        }
        return this._htmlDescription;
    }
    
    @Override
    protected TvTEventData createEventData(final int instanceId) {
        return new HGEventData(instanceId);
    }
    
    @Override
    protected HGEventInstance createEventInstance(final InstanceData instance) {
        return new HGEventInstance(instance);
    }
    
    @Override
    protected HGEventData getEventData(final int instance) {
        return (HGEventData)this._matches.get(instance)._data;
    }
    
    protected class HGEventInstance extends TvTEventInstance
    {
        protected HGEventInstance(final InstanceData instance) {
            super(instance);
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    HuntingGrounds.this.print("Event: running task of state " + this._state.toString() + "...");
                }
                switch (this._state) {
                    case START: {
                        if (HuntingGrounds.this.checkPlayers(this._instance.getId())) {
                            HuntingGrounds.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, false);
                            HuntingGrounds.this.setupTitles(this._instance.getId());
                            HuntingGrounds.this.enableMarkers(this._instance.getId(), true);
                            HuntingGrounds.this.handleWeapons(this._instance.getId(), true);
                            HuntingGrounds.this.handleSkills(this._instance.getId(), true);
                            HuntingGrounds.this.preparePlayers(this._instance.getId(), true);
                            HuntingGrounds.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        HuntingGrounds.this.forceStandAll(this._instance.getId());
                        if (HuntingGrounds.this.getBoolean("createParties")) {
                            HuntingGrounds.this.createParties(HuntingGrounds.this.getInt("maxPartySize"));
                        }
                        this.setNextState(EventState.END);
                        this._clock.startClock(HuntingGrounds.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        this.setNextState(EventState.INACTIVE);
                        if (!HuntingGrounds.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                HuntingGrounds.this.rewardAllTeams(this._instance.getId(), HuntingGrounds.this.getInt("killsForReward"), HuntingGrounds.this.getInt("killsForReward"));
                            }
                            HuntingGrounds.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    HuntingGrounds.this.print("Event: ... finished running task. next state " + this._state.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                HuntingGrounds.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class HGEventData extends TvTEventData
    {
        public HGEventData(final int instance) {
            super(instance);
        }
    }
}
