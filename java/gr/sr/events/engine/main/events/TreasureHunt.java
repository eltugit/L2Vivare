package gr.sr.events.engine.main.events;

import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.EventRewardSystem;
import gr.sr.events.engine.base.*;
import gr.sr.events.engine.base.description.EventDescription;
import gr.sr.events.engine.base.description.EventDescriptionSystem;
import gr.sr.events.engine.lang.LanguageEngine;
import gr.sr.events.engine.main.MainEventManager;
import gr.sr.events.engine.main.base.MainEventInstanceType;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class TreasureHunt extends Deathmatch
{
    protected Map<Integer, THEventInstance> _matches;
    protected boolean _waweRespawn;
    protected boolean _antifeed;
    protected int _normalChestChance;
    protected int _fakeChestChance;
    protected int _luckyChestChance;
    protected int _ancientChestChance;
    protected int _unluckyChestChance;
    protected int _explodingChestChance;
    protected int _nukeChestChance;
    protected int _normalChestNpcId;
    protected int _fakeChestNpcId;
    protected int _luckyChestNpcId;
    protected int _ancientChestNpcId;
    protected int _unluckyChestNpcId;
    protected int _explodingChestNpcId;
    protected int _nukeChestNpcId;
    private int _endCheckInterval;
    protected boolean _allowPvp;
    protected int _countOfChests;
    
    public TreasureHunt(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._matches = new ConcurrentHashMap<Integer, THEventInstance>();
        this._allowPvp = false;
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstBlood, RewardPosition.FirstRegistered, RewardPosition.ChestReward, RewardPosition.ChestRewardLucky, RewardPosition.ChestRewardAncient });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        this.addConfig(new ConfigModel("normalChestChance", "75000", "The chance in percent to spawn a normal chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("luckyChestChance", "10000", "The chance in percent to spawn a lucky chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("ancientChestChance", "2000", "The chance in percent to spawn an ancient chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("unluckyChestChance", "2500", "The chance in percent to spawn an unlucky chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("fakeChestChance", "2500", "The chance in percent to spawn a fake chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("explodingChestChance", "7500", "The chance in percent to spawn a exploding chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("nukeChestChance", "500", "The chance in percent to spawn a nuke chest. 100 000 equals 100%."));
        this.addConfig(new ConfigModel("normalChestNpcId", "8989", "The NpcId in percent to spawn a normal chest. "));
        this.addConfig(new ConfigModel("luckyChestNpcId", "8988", "The NpcId in percent to spawn a lucky chest. "));
        this.addConfig(new ConfigModel("ancientChestNpcId", "8987", "The NpcId in percent to spawn a ancient chest. "));
        this.addConfig(new ConfigModel("unluckyChestNpcId", "8986", "The NpcId in percent to spawn a unlucky chest. "));
        this.addConfig(new ConfigModel("fakeChestNpcId", "8985", "The NpcId in percent to spawn a fake chest. "));
        this.addConfig(new ConfigModel("explodingChestNpcId", "8984", "The NpcId in percent to spawn a exploding chest. "));
        this.addConfig(new ConfigModel("nukeChestNpcId", "8983", "The NpcId in percent to spawn a nuke chest. "));
        this.addConfig(new ConfigModel("checkInactiveDelay", "300", "In seconds. If no chests are opened within this time, the event will be aborted. Eg. if you set this 120 and nobody manages to find and open a chest for 120 seconds, the event will be ended. Disable this by setting 0."));
        this.addConfig(new ConfigModel("scoreForReward", "0", "The minimum of score required to get a reward (includes all possible rewards). Score is gained by killing chests."));
        this.addConfig(new ConfigModel("resDelay", "15", "The delay after which a dead player is resurrected. In seconds."));
        if (this._allowPvp) {
            this.addConfig(new ConfigModel("waweRespawn", "true", "Enables the wawe-style respawn system.", ConfigModel.InputType.Boolean));
            this.addConfig(new ConfigModel("firstBloodMessage", "true", "You can turn off/on the first blood announce in the event (first kill made in the event). This is also rewardable - check out reward type FirstBlood.", ConfigModel.InputType.Boolean));
            this.addConfig(new ConfigModel("antifeedProtection", "true", "Enables the special anti-feed protection. This protection changes player's name, title, race, clan/ally crest, class and basically all of his apperance, sometimes also gender.", ConfigModel.InputType.Boolean));
        }
        else {
            this.removeConfig("killsForReward");
            this.removeConfig("waweRespawn");
        }
    }
    
    @Override
    public void initEvent() {
        super.initEvent();
        if (this._allowPvp) {
            this._waweRespawn = this.getBoolean("waweRespawn");
            this._antifeed = this.getBoolean("antifeedProtection");
            if (this._waweRespawn) {
                this.initWaweRespawns(this.getInt("resDelay"));
            }
        }
        this._normalChestChance = this.getInt("normalChestChance");
        this._fakeChestChance = this.getInt("fakeChestChance");
        this._luckyChestChance = this.getInt("luckyChestChance");
        this._ancientChestChance = this.getInt("ancientChestChance");
        this._unluckyChestChance = this.getInt("unluckyChestChance");
        this._explodingChestChance = this.getInt("explodingChestChance");
        this._nukeChestChance = this.getInt("nukeChestChance");
        this._normalChestNpcId = this.getInt("normalChestNpcId");
        this._fakeChestNpcId = this.getInt("fakeChestNpcId");
        this._luckyChestNpcId = this.getInt("luckyChestNpcId");
        this._ancientChestNpcId = this.getInt("ancientChestNpcId");
        this._unluckyChestNpcId = this.getInt("unluckyChestNpcId");
        this._explodingChestNpcId = this.getInt("explodingChestNpcId");
        this._nukeChestNpcId = this.getInt("nukeChestNpcId");
        this._endCheckInterval = this.getInt("checkInactiveDelay");
        this._countOfChests = this.getInt("countOfChests");
        this._runningInstances = 0;
    }
    
    @Override
    protected int initInstanceTeams(final MainEventInstanceType type) {
        this.createTeams(1, type.getInstance().getId());
        return 1;
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
            final THEventInstance match = this.createEventInstance(instance);
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
    
    public void spawnChest(final int instanceId, final ChestType type, final EventSpawn sp) {
        if (sp == null) {
            return;
        }
        final Loc loc = sp.getLoc();
        loc.addRadius(sp.getRadius());
        final int npcId = this.getChestId(type);
        final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), npcId, instanceId, null, null);
        this.getEventData(instanceId).addChest(npc);
    }
    
    public void disequipWeapons(final int instanceId) {
        if (this._allowPvp) {
            return;
        }
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
            if (wpn != null) {
                player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_R_HAND());
            }
            wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
            if (wpn != null) {
                player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_L_HAND());
            }
        }
    }
    
    public void spawnChests(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: spawning " + this._countOfChests + " chests");
        }
        for (final EventSpawn spawn : this._manager.getMap().getSpawns(-1, SpawnType.Chest)) {
            if (this.random() < this._nukeChestChance) {
                this.spawnChest(instanceId, ChestType.NUKE, spawn);
            }
            else if (this.random() < this._explodingChestChance) {
                this.spawnChest(instanceId, ChestType.EXPLODING, spawn);
            }
            else if (this.random() < this._unluckyChestChance) {
                this.spawnChest(instanceId, ChestType.UNLUCKY, spawn);
            }
            else if (this.random() < this._fakeChestChance) {
                this.spawnChest(instanceId, ChestType.FAKE, spawn);
            }
            else if (this.random() < this._ancientChestChance) {
                this.spawnChest(instanceId, ChestType.ANCIENT, spawn);
            }
            else if (this.random() < this._luckyChestChance) {
                this.spawnChest(instanceId, ChestType.LUCKY, spawn);
            }
            else {
                this.spawnChest(instanceId, ChestType.NORMAL, spawn);
            }
        }
    }
    
    private int random() {
        return CallBack.getInstance().getOut().random(100000);
    }
    
    public void unspawnChests(final int instanceId) {
        for (final NpcData npc : this.getEventData(instanceId)._chests) {
            if (npc != null) {
                npc.deleteMe();
                this.getEventData(instanceId).removeChest(npc);
            }
        }
    }
    
    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minScore = this.getInt("scoreForReward");
        final int minKills = this.getInt("killsForReward");
        this.rewardAllPlayers(-1, minScore, minKills);
        if (this._allowPvp && this._antifeed) {
            for (final PlayerEventInfo player : this.getPlayers(0)) {
                player.stopAntifeedProtection(false);
            }
        }
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final StringBuilder tb = new StringBuilder();
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getScore() > top) {
                top = this.getPlayerData(player).getScore();
            }
        }
        tb.append("Top score: " + top);
        tb.append("   Time: " + this._matches.get(instance).getClock().getTime());
        return tb.toString();
    }
    
    @Override
    protected String getTitle(final PlayerEventInfo pi) {
        if (pi.isAfk()) {
            return "AFK";
        }
        return "Score: " + this.getPlayerData(pi).getScore();
    }
    
    protected void checkEventEnd(final int instance) {
        if (this.getEventData(instance)._endCheckerFuture != null) {
            this.getEventData(instance)._endCheckerFuture.cancel(false);
            this.getEventData(instance)._endCheckerFuture = null;
        }
        if (this.getEventData(instance)._chests.isEmpty()) {
            this.announce("All chests were killed. Event has ended.");
            this.endInstance(instance, true, false, false);
        }
        else {
            this.chestOpened(instance);
        }
    }
    
    private void chestOpened(final int instance) {
        if (this._endCheckInterval > 0) {
            this.getEventData(instance)._endCheckerFuture = CallBack.getInstance().getOut().scheduleGeneral(new EndChecker(instance), this._endCheckInterval * 1000);
        }
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            if (target.getNpc() != null) {
                this.selectChestOnKillAction(player.getInstanceId(), player, target.getNpc());
                this.getEventData(player.getInstanceId()).removeChest(target.getNpc());
                this.checkEventEnd(player.getInstanceId());
            }
        }
        else if (this._allowPvp) {
            this.tryFirstBlood(player);
            this.giveOnKillReward(player);
            this.getPlayerData(player).raiseScore(1);
            this.getPlayerData(player).raiseKills(1);
            this.getPlayerData(player).raiseSpree(1);
            if (player.isTitleUpdated()) {
                player.setTitle(this.getTitle(player), true);
                player.broadcastTitleInfo();
            }
            CallbackManager.getInstance().playerKills(this.getEventType(), player, target.getEventInfo());
            this.setKillsStats(player, this.getPlayerData(player).getKills());
        }
    }
    
    protected void selectChestOnKillAction(final int instanceId, final PlayerEventInfo player, final NpcData npc) {
        final ChestType type = this.getChestType(npc);
        if (type != null) {
            switch (type) {
                case NORMAL: {
                    this.getPlayerData(player).raiseScore(1);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    player.screenMessage("You have scored!", this.getEventName(), false);
                    EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.ChestReward, null, player.getTotalTimeAfk(), 0, 0);
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    break;
                }
                case LUCKY: {
                    this.getPlayerData(player).raiseScore(2);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    player.screenMessage("You have scored! Double points!", this.getEventName(), false);
                    EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.ChestRewardLucky, null, player.getTotalTimeAfk(), 0, 0);
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    break;
                }
                case UNLUCKY: {
                    if (this.getPlayerData(player).getScore() > 0) {
                        this.getPlayerData(player).raiseScore(-1);
                        if (player.isTitleUpdated()) {
                            player.setTitle(this.getTitle(player), true);
                            player.broadcastTitleInfo();
                        }
                        player.screenMessage("Bad chest, -1 score.", this.getEventName(), false);
                        this.setScoreStats(player, this.getPlayerData(player).getScore());
                        break;
                    }
                    break;
                }
                case FAKE: {
                    player.screenMessage("This chest wasn't real.", this.getEventName(), false);
                    break;
                }
                case ANCIENT: {
                    this.getPlayerData(player).raiseScore(5);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    player.screenMessage("You have opened an ancient chest and it gave you 5 points.", this.getEventName(), false);
                    EventRewardSystem.getInstance().rewardPlayer(this.getEventType(), 1, player, RewardPosition.ChestRewardAncient, null, player.getTotalTimeAfk(), 0, 0);
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                }
                case NUKE: {
                    player.screenMessage("You opened the chest, there was a nuke.", this.getEventName(), false);
                    for (final PlayerEventInfo pl : this.getPlayers(instanceId)) {
                        if (!pl.isDead()) {
                            pl.doDie();
                            if (pl.getPlayersId() == player.getPlayersId()) {
                                continue;
                            }
                            pl.screenMessage("You have been nuked. Thanks go to " + player.getPlayersName() + ".", "THunt", false);
                        }
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public boolean allowKill(final CharacterData target, final CharacterData killer) {
        if (target.isNpc() && killer.isPlayer()) {
            final NpcData npc = target.getNpc();
            final PlayerEventInfo player = killer.getEventInfo();
            final ChestType type = this.getChestType(npc);
            if (type != null && type == ChestType.EXPLODING) {
                this.explosionAnimation(npc, player);
                return false;
            }
        }
        return true;
    }
    
    protected void explosionAnimation(final NpcData npc, final PlayerEventInfo player) {
        npc.broadcastSkillUse(npc, player.getCharacterData(), 5430, 1);
        player.doDie();
        CallBack.getInstance().getOut().scheduleGeneral(() -> npc.deleteMe(), 250L);
    }
    
    protected ChestType getChestType(final NpcData npc) {
        if (npc != null) {
            ChestType type = null;
            if (npc.getNpcId() == this._normalChestNpcId) {
                type = ChestType.NORMAL;
            }
            else if (npc.getNpcId() == this._luckyChestNpcId) {
                type = ChestType.LUCKY;
            }
            else if (npc.getNpcId() == this._unluckyChestNpcId) {
                type = ChestType.UNLUCKY;
            }
            else if (npc.getNpcId() == this._fakeChestNpcId) {
                type = ChestType.FAKE;
            }
            else if (npc.getNpcId() == this._ancientChestNpcId) {
                type = ChestType.ANCIENT;
            }
            else if (npc.getNpcId() == this._explodingChestNpcId) {
                type = ChestType.EXPLODING;
            }
            else if (npc.getNpcId() == this._nukeChestNpcId) {
                type = ChestType.NUKE;
            }
            return type;
        }
        return null;
    }
    
    protected int getChestId(final ChestType type) {
        int npcId = 0;
        switch (type) {
            case NORMAL: {
                npcId = this._normalChestNpcId;
                break;
            }
            case LUCKY: {
                npcId = this._luckyChestNpcId;
                break;
            }
            case UNLUCKY: {
                npcId = this._unluckyChestNpcId;
                break;
            }
            case FAKE: {
                npcId = this._fakeChestNpcId;
                break;
            }
            case ANCIENT: {
                npcId = this._ancientChestNpcId;
                break;
            }
            case EXPLODING: {
                npcId = this._explodingChestNpcId;
                break;
            }
            case NUKE: {
                npcId = this._nukeChestNpcId;
                break;
            }
        }
        return npcId;
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
        if (this._allowPvp && this._waweRespawn) {
            this._waweScheduler.addPlayer(player);
        }
        else {
            this.scheduleRevive(player, this.getInt("resDelay") * 1000);
        }
    }
    
    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return player.getPlayersId() == target.getObjectId() || !player.hasSummon() || !target.isSummon() || player.getSummon() != target.getOwner() || true;
    }
    
    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        return target.getEventInfo() == null || (target.getEventInfo().getEvent() == player.getEvent() && this._allowPvp);
    }
    
    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        if (text.equals(".scheme")) {
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "none", this.getEventType().getAltTitle());
            return false;
        }
        if (this._allowPvp && this._antifeed) {
            player.sendMessage(LanguageEngine.getMsg("dm_cantChat"));
            return false;
        }
        return true;
    }
    
    @Override
    public boolean canInviteToParty(final PlayerEventInfo player, final PlayerEventInfo target) {
        return false;
    }
    
    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        if (!this._allowPvp) {
            player.sendMessage("Weapons are not allowed in this event.");
            return false;
        }
        return true;
    }
    
    @Override
    public synchronized void clearEvent(final int instanceId) {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: called CLEAREVENT for instance " + instanceId);
        }
        try {
            if (this._matches != null) {
                for (final DMEventInstance match : this._matches.values()) {
                    if (instanceId == 0 || instanceId == match.getInstance().getId()) {
                        this.unspawnChests(match.getInstance().getId());
                        match.abort();
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (player.isOnline()) {
                if (player.hasAntifeedProtection()) {
                    player.stopAntifeedProtection(false);
                }
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
    public String getMissingSpawns(final EventMap map) {
        if (!map.checkForSpawns(SpawnType.Regular, -1, 1)) {
            return this.addMissingSpawn(SpawnType.Regular, 0, 1);
        }
        if (!map.checkForSpawns(SpawnType.Chest, -1, 1)) {
            return this.addMissingSpawn(SpawnType.Chest, 0, 1);
        }
        return "";
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getScore() > top) {
                top = this.getPlayerData(player).getScore();
            }
        }
        final String status = "<font color=ac9887>Top score count: </font><font color=7f7f7f>" + top + "</font>";
        return "<table width=510 bgcolor=3E3E3E><tr><td width=510 align=center>" + status + "</td></tr></table>";
    }
    
    @Override
    public boolean isInEvent(final CharacterData ch) {
        if (ch.isNpc()) {
            final NpcData npc = ch.getNpc();
            if (this.getChestType(npc) != null) {
                return true;
            }
        }
        return false;
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
    protected THEventInstance createEventInstance(final InstanceData instance) {
        return new THEventInstance(instance);
    }
    
    @Override
    protected THData createEventData(final int instance) {
        return new THData(instance);
    }
    
    @Override
    protected THData getEventData(final int instance) {
        return this._matches.get(instance)._data;
    }
    
    private class EndChecker implements Runnable
    {
        int instance;
        
        public EndChecker(final int instance) {
            this.instance = instance;
        }
        
        @Override
        public void run() {
            TreasureHunt.this.announce("Some chests hided so well that nobody managed to find them. Event has ended.");
            TreasureHunt.this.endInstance(this.instance, true, false, false);
        }
    }
    
    protected enum EventState
    {
        START, 
        FIGHT, 
        END, 
        TELEPORT, 
        INACTIVE;
    }
    
    protected class THEventInstance extends DMEventInstance
    {
        protected EventState _nextState;
        protected THData _data;
        
        public THEventInstance(final InstanceData instance) {
            super(instance);
            this._nextState = EventState.START;
            this._data = TreasureHunt.this.createEventData(this._instance.getId());
        }
        
        protected void setNextState(final EventState state) {
            this._nextState = state;
        }
        
        @Override
        public boolean isActive() {
            return this._nextState != EventState.INACTIVE;
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    TreasureHunt.this.print("Event: running task of state " + this._nextState.toString() + "...");
                }
                switch (this._nextState) {
                    case START: {
                        if (TreasureHunt.this.checkPlayers(this._instance.getId())) {
                            if (TreasureHunt.this._allowPvp && TreasureHunt.this._antifeed) {
                                for (final PlayerEventInfo player : TreasureHunt.this.getPlayers(this._instance.getId())) {
                                    player.startAntifeedProtection(false);
                                }
                            }
                            TreasureHunt.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, true);
                            TreasureHunt.this.spawnChests(this._instance.getId());
                            TreasureHunt.this.disequipWeapons(this._instance.getId());
                            TreasureHunt.this.setupTitles(this._instance.getId());
                            TreasureHunt.this.enableMarkers(this._instance.getId(), true);
                            TreasureHunt.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        TreasureHunt.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        this._clock.startClock(TreasureHunt.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        TreasureHunt.this.unspawnChests(this._instance.getId());
                        this.setNextState(EventState.INACTIVE);
                        if (!TreasureHunt.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                TreasureHunt.this.rewardAllPlayers(this._instance.getId(), 0, TreasureHunt.this.getInt("killsForReward"));
                            }
                            TreasureHunt.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    TreasureHunt.this.print("Event: ... finished running task. next state " + this._nextState.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                TreasureHunt.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class THData extends DMData
    {
        public List<NpcData> _chests;
        protected ScheduledFuture<?> _endCheckerFuture;
        
        protected THData(final int instance) {
            super(instance);
            this._chests = Collections.synchronizedList(new LinkedList<NpcData>());
            this._endCheckerFuture = null;
            this._chests.clear();
        }
        
        public void addChest(final NpcData ch) {
            this._chests.add(ch);
        }
        
        public void removeChest(final NpcData ch) {
            if (this._chests.contains(ch)) {
                this._chests.remove(ch);
            }
        }
    }
    
    public enum ChestType
    {
        NORMAL, 
        FAKE, 
        LUCKY, 
        ANCIENT, 
        UNLUCKY, 
        EXPLODING, 
        NUKE;
    }
}
