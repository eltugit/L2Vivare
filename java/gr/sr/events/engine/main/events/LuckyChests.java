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
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.callback.CallbackManager;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class LuckyChests extends Deathmatch
{
    private String _scorebarInfo;
    private boolean _customShortcuts;
    private int _classicChestId;
    private int _shabbyChestId;
    private int _luxuriousChestId;
    private int _boxChestId;
    private int _nexusedChestId;
    private int _classicChestsCountMin;
    private int _classicChestsCountMax;
    private int _shabbyChestsCountMin;
    private int _shabbyChestsCountMax;
    private int _luxuriousChestsCountMin;
    private int _luxuriousChestsCountMax;
    private int _nexusedChestsCountMin;
    private int _nexusedChestsCountMax;
    private int _boxChestsCountMin;
    private int _boxChestsCountMax;
    private boolean _shabbyChestEnabled;
    private boolean _luxuriousChestEnabled;
    private boolean _boxChestEnabled;
    private boolean _nexusedChestEnabled;
    private int _gladiusItemId;
    private int _dirkItemId;
    private int _bowItemId;
    private int _bowArrowId;
    private int _lanceItemId;
    private int _hammerItemId;
    private int _zweihanderItemId;
    private int _shieldItemId;
    private int _knightswordItemId;
    private int _craftedDaggerItemId;
    private int _longBowItemId;
    private int _longBowArrowItemId;
    private int _pikeItemId;
    private int _heavyswordItemId;
    private int _reinforcedBowItemId;
    private int _reinforcedArrowItemId;
    private int _heavyHammerItemId;
    private int _saberItemId;
    private int _arrowCount;
    private boolean _allowTransformations;
    private int _bunnyTransformId;
    private int _frogTransformId;
    private int _pigTransformId;
    private int _yetiTransformId;
    private int _bunnyTransformDuration;
    private int _frogTransformDuration;
    private int _pigTransformDuration;
    private int _yetiTransformDuration;
    private boolean _transformationHalfResTime;
    private int _jokerChanceOnHit;
    private int _jokerTeleportChance;
    private int _bunnyKilledScore;
    private int _frogKilledScore;
    private int _pigKilledScore;
    private int _yetiKilledScore;
    private int classicPositiveEffect;
    private int classicObtainWeaponChance;
    private int luxPositiveEffect;
    private int luxObtainWeaponChance;
    private int shabbyTransformChance;
    private int shabbyShieldChance;
    private int nexusedMainEffectChance;
    private boolean _enableFearFireworking;
    private boolean _explosionShieldResetKillstreak;
    private boolean _bombShieldProtectsParalyzation;
    private boolean _bombShieldProtectsFear;
    private int _aggressionSkillId;
    private int _whirlwindSkill;
    private int _rushSkill;
    private int _stunSkill;
    private int _backstabSkill;
    private String _jokerChestName;
    private String[] _jokerChestTexts;
    private final Map<Integer, Integer> _skillsForAll;
    private final Map<LuckyItem, Map<Integer, Integer>> _skillsForItems;
    
    public LuckyChests(final EventType type, final MainEventManager manager) {
        super(type, manager);
        this._bombShieldProtectsFear = true;
        this._skillsForAll = new ConcurrentHashMap<Integer, Integer>();
        this._skillsForItems = new ConcurrentHashMap<LuckyItem, Map<Integer, Integer>>();
        this.setRewardTypes(new RewardPosition[] { RewardPosition.Looser, RewardPosition.Tie, RewardPosition.Numbered, RewardPosition.Range, RewardPosition.FirstRegistered });
    }
    
    @Override
    public void loadConfigs() {
        super.loadConfigs();
        final String weaponTypes = "GLADIUS, DIRK, BOW, LANCE, HAMMER, ZWEIHANDER, SHIELD, KNIGHTSWORD, DAGGER_CRAFTED, LONGBOW, PIKE, HEAVYSWORD, REINFORCED_BOW, HEAVYHAMMER, SABER";
        final String weaponSkillsDefaultVal = "DIRK-35003-1,BOW-35001-1,LANCE-35002-1,HAMMER-35004-1,ZWEIHANDER-35005-1,SHIELD-35006-1,KNIGHTSWORD-35007-1,DAGGER_CRAFTED-35008-1,LONGBOW-35009-1,PIKE-35010-1,HEAVYSWORD-35011-1,REINFORCED_BOW-35012-1,HEAVYHAMMER-35013-1,SABER-35014-1";
        final String jokerDefault = "hahahaha,hahahahahaha,hehehe,ha... ha... ha...,hihihi,ahahaha,ehehehe,eki eki,keh keh,muhahaha,nihaha,puhahaha,uhahahaha,zuhahaha,eheheh,moaha ha,kahkahkah,tee hee hee!,LOL!";
        this.addConfig(new ConfigModel("scoreForReward", "0", "The minimum score required to get a reward (includes all possible rewards)."));
        this.removeConfig("killsForReward");
        this.removeConfig("antifeedProtection");
        this.removeConfig("waweRespawn");
        this.removeConfig("firstBloodMessage");
        this.removeConfig("allowScreenScoreBar");
        this.addConfig("Chests", new ConfigModel("classicChestId", "8993", "The NPC ID of the classic chest in this event."));
        this.addConfig("Chests", new ConfigModel("shabbyChestId", "8994", "The NPC ID of the shabby chest in this event. Put 0 to disable this chest type on this event."));
        this.addConfig("Chests", new ConfigModel("luxuriousChestId", "8995", "The NPC ID of the luxurious chest in this event. Put 0 to disable this chest type on this event."));
        this.addConfig("Chests", new ConfigModel("boxChestId", "8996", "The NPC ID of the bonus crate in this event. Put 0 to disable this chest type on this event."));
        this.addConfig("Chests", new ConfigModel("nexusedChestId", "8997", "The NPC ID of the nexused chest in this event. Put 0 to disable this chest type on this event."));
        this.addConfig("Chests", new ConfigModel("classicChestCount", "40-60", "Count of Classic chests spawned in the event. Format: MIN-MAX. For example: '30-60' will choose randomly value between 30 and 60."));
        this.addConfig("Chests", new ConfigModel("shabbyChestCount", "15-25", "Count of Shabby chests spawned in the event. Format: MIN-MAX. For example: '15-25' will choose randomly value between 15 and 25."));
        this.addConfig("Chests", new ConfigModel("luxuriousChestCount", "8-12", "Count of Luxurious chests spawned in the event. Format: MIN-MAX. For example: '8-12' will choose randomly value between 8 and 12."));
        this.addConfig("Chests", new ConfigModel("boxChestCount", "5-10", "Count of Box chests spawned in the event. Format: MIN-MAX. For example: '5-10' will choose randomly value between 5 and 10."));
        this.addConfig("Chests", new ConfigModel("nexusedChestCount", "1-3", "Count of Nexused chests spawned in the event. Format: MIN-MAX. For example: '1-3' will choose randomly value between 1 and 3."));
        this.addConfig("Chests", new ConfigModel("classicPositiveEffect", "70", "The chance (in percent) for positive effect (effect, which gives score) when the player kills a classic chest."));
        this.addConfig("Chests", new ConfigModel("classicObtainWeaponChance", "3", "When the player kills a classic chest and positive effect is selected, this defines the chance (in percent) to receive a new weapon."));
        this.addConfig("Chests", new ConfigModel("luxPositiveEffect", "85", "The chance (in percent) for positive effect (effect, which gives score) when the player kills a luxurious chest."));
        this.addConfig("Chests", new ConfigModel("luxObtainWeaponChance", "5", "When the player kills a luxurious chest and positive effect is selected, this defines the chance (in percent) to receive a new weapon."));
        this.addConfig("Chests", new ConfigModel("shabbyTransformChance", "20", "The chance (in percent) that the Shabby chest will transform the player when he kills it."));
        this.addConfig("Chests", new ConfigModel("shabbyShieldChance", "30", "The chance (in percent) that the Shabby chest will give the player a one-bomb shield."));
        this.addConfig("Chests", new ConfigModel("nexusedMainEffectChance", "60", "The chance (in percent) that Nexused chest will reward the player with either Rush skill or x4 score."));
        this.addConfig(new ConfigModel("skillsForAllPlayers", "35000-1", "IDs of skills which will be given to every player on the event. The purpose of this is to make all players equally strong. Format: <font color=LEVEL>SKILLID-LEVEL</font> (eg. '35000-1').", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("scorebarInfoType", "TopScore", "You can specify what kind of information (beside Time) will be shown in the scorebar in player's screen.", ConfigModel.InputType.Enum).addEnumOptions(new String[] { "TopScore", "ChestsLeft" }));
        this.addConfig(new ConfigModel("customShortcuts", "true", "True to turn on the custom shortcuts engine, which deletes all player's shortcuts during the event run time and puts there it's own custom shortcuts. When the event ends, player's shortcuts will be restored back.", ConfigModel.InputType.Boolean));
        this.addConfig("Transforms", new ConfigModel("allowTransformations", "true", "Enable/disable random transformations on this event. Sometimes, when a player kills a chest, he gets transformed to a randomly chosen transformation and is usually allowed to kill the other players (and gets score for it).", ConfigModel.InputType.Boolean));
        this.addConfig("Transforms", new ConfigModel("transformShortResTime", "true", "If true, resurrection delay will be /2 if the player died transformed. It will make players less emo.", ConfigModel.InputType.Boolean));
        this.addConfig("Chests", new ConfigModel("fearLaunchesFireworks", "true", "If true, all players feared by a chest will continously launch fireworks as they run away.", ConfigModel.InputType.Boolean));
        this.addConfig("Chests", new ConfigModel("explosionShieldResetKillstreak", "false", "If enabled, the player's killstreak will be reset after the chest he kills explodes, and the fact if the player was protected by a shield or not doesn't matter. Basically, setting this to true will make killstreaks harder.", ConfigModel.InputType.Boolean));
        this.addConfig("Chests", new ConfigModel("bombShieldProtectsParalyzation", "true", "If enabled, the bomb shield will protect player from being paralysed by a chest.", ConfigModel.InputType.Boolean));
        this.addConfig("Chests", new ConfigModel("bombShieldProtectsFear", "true", "If enabled, the bomb shield will protect player from being feared by a chest.", ConfigModel.InputType.Boolean));
        this.addConfig("Chests", new ConfigModel("jokerChestName", "Joker Chest", "The name of the joker chest visible in its title."));
        this.addConfig("Chests", new ConfigModel("jokerActivationChance", "10", "The chance for activating joker - the chest will laugh and disappear and possibly also stun and teleport the player away, giving him no score. <font color=LEVEL>Activated everytime the player hits a chest. 10 equals 1% (1000 equals 100%).</font>"));
        this.addConfig("Chests", new ConfigModel("jokerTeleportChance", "50", "When the joker (config 'jokerActivationChance') is activated, this is the chance that the chest will teleport (blink) player away and stun him for a few seconds. <font color=LEVEL>In percent - 50 equals 50%. Doesn't work for Interlude version.</font>"));
        this.addConfig("Chests", new ConfigModel("jokerPhrases", "hahahaha,hahahahahaha,hehehe,ha... ha... ha...,hihihi,ahahaha,ehehehe,eki eki,keh keh,muhahaha,nihaha,puhahaha,uhahahaha,zuhahaha,eheheh,moaha ha,kahkahkah,tee hee hee!,LOL!", "Write here things that might be said by the chest if the joker is activated.", ConfigModel.InputType.MultiAdd));
        this.addConfig(new ConfigModel("aggressionSkillId", "980", "The ID of Aggression skill ID given to the player."));
        this.addConfig(new ConfigModel("whirlwindSkillId", "36", "The ID of Whirlwind skill ID given to the player (if he's obtained polearm)."));
        this.addConfig(new ConfigModel("rushSkillId", "484", "The ID of Rush skill ID given to the player from Nexused chest."));
        this.addConfig(new ConfigModel("stunSkillId", "260", "The ID of Stun skill ID given to the player with hammer."));
        this.addConfig(new ConfigModel("backstabSkillId", "30", "The ID of Backstab skill ID given to the player with dagger."));
        this.addConfig("Transforms", new ConfigModel("bunnyKillScore", "2", "Score given to the player for killing a bunny. If the player is Yeti, he will always receive only 1 point."));
        this.addConfig("Transforms", new ConfigModel("pigKillScore", "3", "Score given to the player for killing a pig. If the player is Yeti, he will always receive only 1 point."));
        this.addConfig("Transforms", new ConfigModel("yetiKillScore", "6", "Score given to the player for killing a yeti."));
        this.addConfig("Transforms", new ConfigModel("frogKillScore", "3", "Score given to the player for killing a frog. The frog dies on 1 hit only, but it is difficult to catch it."));
        this.addConfig("Transforms", new ConfigModel("bunnyTransformId", "105", "The ID of the bunny transformation. Default value is fine unless your core is super-modified."));
        this.addConfig("Transforms", new ConfigModel("pigTransformId", "104", "The ID of the bunny transformation. Default value is fine unless your core is super-modified."));
        this.addConfig("Transforms", new ConfigModel("yetiTransformId", "102", "The ID of the bunny transformation. Default value is fine unless your core is super-modified."));
        this.addConfig("Transforms", new ConfigModel("frogTransformId", "111", "The ID of the bunny transformation. Default value is fine unless your core is super-modified."));
        this.addConfig("Transforms", new ConfigModel("bunnyTransformDuration", "60", "Specify how long will the bunny transformation last on the player till it gets removed. In seconds. Note that it will also disappear if the player dies."));
        this.addConfig("Transforms", new ConfigModel("pigTransformDuration", "45", "Specify how long will the pig transformation last on the player till it gets removed. In seconds. Note that it will also disappear if the player dies."));
        this.addConfig("Transforms", new ConfigModel("yetiTransformDuration", "60", "Specify how long will the yeti transformation last on the player till it gets removed. In seconds. Note that it will also disappear if the player dies."));
        this.addConfig("Transforms", new ConfigModel("frogTransformDuration", "7", "Specify how long will the frog transformation last on the player till it gets removed. In seconds. Note that it will also disappear if the player dies."));
        this.addConfig("Weapons", new ConfigModel("weaponSkills", "DIRK-35003-1,BOW-35001-1,LANCE-35002-1,HAMMER-35004-1,ZWEIHANDER-35005-1,SHIELD-35006-1,KNIGHTSWORD-35007-1,DAGGER_CRAFTED-35008-1,LONGBOW-35009-1,PIKE-35010-1,HEAVYSWORD-35011-1,REINFORCED_BOW-35012-1,HEAVYHAMMER-35013-1,SABER-35014-1", "IDs of skills which will be given to players holding certain weapon type (<font color=7f7f7f>GLADIUS, DIRK, BOW, LANCE, HAMMER, ZWEIHANDER, SHIELD, KNIGHTSWORD, DAGGER_CRAFTED, LONGBOW, PIKE, HEAVYSWORD, REINFORCED_BOW, HEAVYHAMMER, SABER</font>). Format: <font color=LEVEL>WEAPON_TYPE-SKILL_ID-LEVEL</font> (eg. 'SWORD-255-2'). Also, all skills written in this list will be allowed to use in this event (all other skills are disabled on this event, including heals).", ConfigModel.InputType.MultiAdd));
        this.addConfig("Weapons", new ConfigModel("gladiusItemId", "66", "The item ID of the sword weapon. This is the basic weapon owned by all players in the event."));
        this.addConfig("Weapons", new ConfigModel("dirkItemId", "216", "The item ID of the dirk weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("bowItemId", "14-17", "The item ID of the bow weapon. Format <font color=LEVEL>BOW_ID-ARROW_ID</font> (eg. '14-17'). Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("lanceItemId", "97", "The item ID of the lance weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("hammerItemId", "87", "The item ID of the hammer weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("zweihanderItemId", "5284", "The item ID of the zweihander weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("shieldItemId", "102", "The item ID of the shield weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("knightswordItemId", "128", "The item ID of the knight sword weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("craftedDaggerItemId", "220", "The item ID of the crafted dagger weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("longBowItemId", "275-1341", "The item ID of the long bow weapon. Format <font color=LEVEL>BOW_ID-ARROW_ID</font> (eg. '14-17'). Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("pikeItemId", "292", "The item ID of the pike weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("heavyswordItemId", "5285", "The item ID of the heavy sword weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("reinforcedBowItemId", "279-1341", "The item ID of the reinforced bow weapon. Format <font color=LEVEL>BOW_ID-ARROW_ID</font> (eg. '14-17'). Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("heavyHammerItemId", "187", "The item ID of the heavy hammer weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("saberItemId", "123", "The item ID of the saber weapon. Put -1 to disable this weapon on the event."));
        this.addConfig("Weapons", new ConfigModel("arrowCount", "300", "The count of arrows given to the player, when he obtains a bow."));
    }
    
    @Override
    public void initEvent() {
        this._waweRespawn = false;
        this._antifeed = false;
        this._allowSchemeBuffer = false;
        super.initEvent();
        this._allowScoreBar = false;
        this._scorebarInfo = this.getString("scorebarInfoType");
        this._customShortcuts = this.getBoolean("customShortcuts");
        this._transformationHalfResTime = this.getBoolean("transformShortResTime");
        this._explosionShieldResetKillstreak = this.getBoolean("explosionShieldResetKillstreak");
        this._bombShieldProtectsParalyzation = this.getBoolean("bombShieldProtectsParalyzation");
        this._bombShieldProtectsFear = this.getBoolean("bombShieldProtectsFear");
        this._aggressionSkillId = this.getInt("aggressionSkillId");
        this._whirlwindSkill = this.getInt("whirlwindSkillId");
        this._rushSkill = this.getInt("rushSkillId");
        this._stunSkill = this.getInt("stunSkillId");
        this._backstabSkill = this.getInt("backstabSkillId");
        this._jokerChestName = this.getString("jokerChestName");
        this._jokerChanceOnHit = this.getInt("jokerActivationChance");
        this._jokerTeleportChance = this.getInt("jokerTeleportChance");
        this._enableFearFireworking = this.getBoolean("fearLaunchesFireworks");
        this._allowTransformations = this.getBoolean("allowTransformations");
        this.classicPositiveEffect = this.getInt("classicPositiveEffect");
        this.classicObtainWeaponChance = this.getInt("classicObtainWeaponChance");
        this.luxPositiveEffect = this.getInt("luxPositiveEffect");
        this.luxObtainWeaponChance = this.getInt("luxObtainWeaponChance");
        this.shabbyTransformChance = this.getInt("shabbyTransformChance");
        this.shabbyShieldChance = this.getInt("shabbyShieldChance");
        this.nexusedMainEffectChance = this.getInt("nexusedMainEffectChance");
        this._bunnyKilledScore = this.getInt("bunnyKillScore");
        this._frogKilledScore = this.getInt("pigKillScore");
        this._pigKilledScore = this.getInt("yetiKillScore");
        this._yetiKilledScore = this.getInt("frogKillScore");
        this._bunnyTransformId = this.getInt("bunnyTransformId");
        this._pigTransformId = this.getInt("pigTransformId");
        this._yetiTransformId = this.getInt("yetiTransformId");
        this._frogTransformId = this.getInt("frogTransformId");
        this._bunnyTransformDuration = this.getInt("bunnyTransformDuration");
        this._pigTransformDuration = this.getInt("pigTransformDuration");
        this._yetiTransformDuration = this.getInt("yetiTransformDuration");
        this._frogTransformDuration = this.getInt("frogTransformDuration");
        this._classicChestId = this.getInt("classicChestId");
        this._shabbyChestId = this.getInt("shabbyChestId");
        this._luxuriousChestId = this.getInt("luxuriousChestId");
        this._boxChestId = this.getInt("boxChestId");
        this._nexusedChestId = this.getInt("nexusedChestId");
        try {
            String s = this.getString("bowItemId");
            this._bowItemId = Integer.parseInt(s.split("-")[0]);
            this._bowArrowId = Integer.parseInt(s.split("-")[1]);
            s = this.getString("longBowItemId");
            this._longBowItemId = Integer.parseInt(s.split("-")[0]);
            this._longBowArrowItemId = Integer.parseInt(s.split("-")[1]);
            s = this.getString("reinforcedBowItemId");
            this._reinforcedBowItemId = Integer.parseInt(s.split("-")[0]);
            this._reinforcedArrowItemId = Integer.parseInt(s.split("-")[1]);
        }
        catch (Exception e) {
            SunriseLoader.debug("Error while loading Bows in Lucky chests event. Check out their configs - " + e.toString(), Level.WARNING);
            this._bowItemId = -1;
            this._longBowItemId = -1;
            this._reinforcedBowItemId = -1;
        }
        this._gladiusItemId = this.getInt("gladiusItemId");
        this._dirkItemId = this.getInt("dirkItemId");
        this._lanceItemId = this.getInt("lanceItemId");
        this._hammerItemId = this.getInt("hammerItemId");
        this._zweihanderItemId = this.getInt("zweihanderItemId");
        this._shieldItemId = this.getInt("shieldItemId");
        this._knightswordItemId = this.getInt("knightswordItemId");
        this._craftedDaggerItemId = this.getInt("craftedDaggerItemId");
        this._pikeItemId = this.getInt("pikeItemId");
        this._heavyswordItemId = this.getInt("heavyswordItemId");
        this._heavyHammerItemId = this.getInt("heavyHammerItemId");
        this._saberItemId = this.getInt("saberItemId");
        this._arrowCount = this.getInt("arrowCount");
        try {
            String s = this.getString("classicChestCount");
            this._classicChestsCountMin = Integer.parseInt(s.split("-")[0]);
            this._classicChestsCountMax = Integer.parseInt(s.split("-")[1]);
            s = this.getString("shabbyChestCount");
            this._shabbyChestsCountMin = Integer.parseInt(s.split("-")[0]);
            this._shabbyChestsCountMax = Integer.parseInt(s.split("-")[1]);
            s = this.getString("luxuriousChestCount");
            this._luxuriousChestsCountMin = Integer.parseInt(s.split("-")[0]);
            this._luxuriousChestsCountMax = Integer.parseInt(s.split("-")[1]);
            s = this.getString("boxChestCount");
            this._boxChestsCountMin = Integer.parseInt(s.split("-")[0]);
            this._boxChestsCountMax = Integer.parseInt(s.split("-")[1]);
            s = this.getString("nexusedChestCount");
            this._nexusedChestsCountMin = Integer.parseInt(s.split("-")[0]);
            this._nexusedChestsCountMax = Integer.parseInt(s.split("-")[1]);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.clearEvent();
            SunriseLoader.debug("Event: wrong format for a config that specifies count of chests.");
            if (SunriseLoader.detailedDebug) {
                this.print("Event: wrong format for a config that specifies count of chests.");
            }
        }
        this._shabbyChestEnabled = (this._shabbyChestId > 0);
        this._luxuriousChestEnabled = (this._luxuriousChestId > 0);
        this._boxChestEnabled = (this._boxChestId > 0);
        this._nexusedChestEnabled = (this._nexusedChestId > 0);
        if (!this.checkNpcs()) {
            this.clearEvent();
            this.announce("Missing NPC Templates for chests event.");
        }
        if (!this.getString("jokerPhrases").equals("")) {
            final String[] splits = this.getString("jokerPhrases").split(",");
            this._jokerChestTexts = splits;
        }
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
            catch (Exception e2) {
                SunriseLoader.debug("Error while loading config 'skillsForAllPlayers' for event " + this.getEventName() + " - " + e2.toString(), Level.SEVERE);
            }
        }
        if (!this.getString("weaponSkills").equals("")) {
            final String[] splits = this.getString("weaponSkills").split(",");
            this._skillsForItems.clear();
            try {
                for (final String split2 : splits) {
                    final String type = split2.split("-")[0];
                    final String id2 = split2.split("-")[1];
                    final String level2 = split2.split("-")[2];
                    Label_1436: {
                        LuckyItem wType;
                        try {
                            wType = LuckyItem.valueOf(type);
                            if (wType == null) {
                                SunriseLoader.debug("LuckyItem type " + wType + " doesn't exist (lucky chests config).");
                                break Label_1436;
                            }
                        }
                        catch (Exception e3) {
                            break Label_1436;
                        }
                        if (!this._skillsForItems.containsKey(wType)) {
                            this._skillsForItems.put(wType, new LinkedHashMap<Integer, Integer>());
                        }
                        this._skillsForItems.get(wType).put(Integer.parseInt(id2), Integer.parseInt(level2));
                    }
                }
            }
            catch (Exception e2) {
                SunriseLoader.debug("Error while loading config 'weaponSkills' for event " + this.getEventName() + " - " + e2.toString(), Level.SEVERE);
            }
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
            final DMEventInstance match = this.createEventInstance(instance);
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
    public void onDamageGive(final CharacterData cha, final CharacterData target, final int damage, final boolean isDOT) {
        if (cha.isPlayer() && this.getPlayerData(cha.getEventInfo()).getTransformation() == null) {
            final PlayerEventInfo player = cha.getEventInfo();
            if (CallBack.getInstance().getOut().random(1000) < this._jokerChanceOnHit && target.isNpc() && this.getType(target.getNpc().getNpcId()) != null && this.effect(player, target.getNpc(), EffectType.Laugh, null, null).success && CallBack.getInstance().getOut().random(100) < this._jokerTeleportChance) {
                player.broadcastSkillUse(null, null, 628, 1);
                player.getSkillEffects(35015, 1);
            }
        }
    }
    
    @Override
    public boolean isInEvent(final CharacterData ch) {
        if (ch.isNpc()) {
            final NpcData npc = ch.getNpc();
            if (this.getType(npc.getNpcId()) != null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean allowKill(final CharacterData target, final CharacterData killer) {
        if (target.isNpc() && killer.isPlayer()) {
            final NpcData npc = target.getNpc();
            final PlayerEventInfo player = killer.getEventInfo();
            if (!this.canServerKillChest(npc, player)) {
                return false;
            }
        }
        return true;
    }
    
    private void giveSkill(final PlayerEventInfo player, final SkillData skill) {
        player.addSkill(skill, false);
        player.sendSkillList();
        if (this._customShortcuts && this.getPlayerData(player).getSkillShortcut(skill) == null) {
            final SlotInfo slot = this.getPlayerData(player).getNextFreeShortcutSlot(false);
            if (slot != null) {
                final ShortCutData sh = player.createSkillShortcut(slot.slot, slot.page, skill);
                this.getPlayerData(player).addSkillShortcut(skill, sh);
                player.registerShortcut(sh, true);
            }
        }
    }
    
    private void giveSkillForWeapon(final PlayerEventInfo player, final LuckyItem item, final int id, final int level) {
        this.getPlayerData(player).addSkillForWeapon(id, level, item);
        this.updateSkillsForWeapon(player);
    }
    
    private void updateSkillsForWeapon(final PlayerEventInfo player) {
        final LuckyItem currentWeapon = this.getPlayerData(player).getActiveWeapon();
        if (currentWeapon == this.getPlayerData(player).getActiveWeaponWithSkills()) {
            return;
        }
        SkillData skill = null;
        if (this.getPlayerData(player).getActiveWeaponWithSkills() != null) {
            for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkillsForWeapon(this.getPlayerData(player).getActiveWeaponWithSkills()).entrySet()) {
                skill = new SkillData(e.getKey(), e.getValue());
                if (skill.exists()) {
                    player.removeBuff(skill.getId());
                    player.removeSkill(skill.getId());
                    player.sendSkillList();
                    if (!this._customShortcuts) {
                        continue;
                    }
                    final ShortCutData sh = this.getPlayerData(player).getSkillShortcut(skill);
                    if (sh == null) {
                        continue;
                    }
                    player.removeShortCut(sh, true);
                    this.getPlayerData(player).removeSkillShortcut(skill, sh);
                }
            }
        }
        this.getPlayerData(player).setActiveWeaponWithSkills(currentWeapon);
        for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkillsForWeapon(currentWeapon).entrySet()) {
            skill = new SkillData(e.getKey(), e.getValue());
            if (skill.exists()) {
                player.addSkill(skill, false);
                player.sendSkillList();
                if (!this._customShortcuts || this.getPlayerData(player).getSkillShortcut(skill) != null) {
                    continue;
                }
                final SlotInfo slot = this.getPlayerData(player).getNextFreeShortcutSlot(false);
                if (slot == null) {
                    continue;
                }
                final ShortCutData sh2 = player.createSkillShortcut(slot.slot, slot.page, skill);
                this.getPlayerData(player).addSkillShortcut(skill, sh2);
                player.registerShortcut(sh2, true);
            }
        }
    }
    
    private void giveWeapon(final PlayerEventInfo player, final LuckyItem itemType, final boolean equip, final boolean forceOverride) {
        if (!this.getPlayerData(player).hasWeapon(itemType)) {
            final LuckyItem stackingItem = this.getPlayerData(player).getWeaponOfType(itemType._type);
            if (stackingItem != null) {
                if (stackingItem._grade >= itemType._grade && !forceOverride) {
                    return;
                }
                this.removeWeapon(player, stackingItem);
            }
            final int itemId = this.getItemId(itemType);
            if (itemId == -1) {
                return;
            }
            final ItemData item = player.addItem(itemId, 1, true);
            if (item == null) {
                SunriseLoader.debug("Item ID " + itemId + " for lucky chests event doesn't exist. Please edit it in configs!!");
                return;
            }
            if (itemType == LuckyItem.BOW) {
                player.addItem(this._bowArrowId, this._arrowCount, true);
            }
            else if (itemType == LuckyItem.LONGBOW) {
                player.addItem(this._longBowArrowItemId, this._arrowCount, true);
            }
            else if (itemType == LuckyItem.REINFORCED_BOW) {
                player.addItem(this._reinforcedArrowItemId, this._arrowCount, true);
            }
            if (equip) {
                if (itemType != LuckyItem.SHIELD) {
                    final ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
                    if (wpn != null) {
                        player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_R_HAND());
                    }
                }
                final ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
                if (wpn != null) {
                    player.unEquipItemInBodySlotAndRecord(CallBack.getInstance().getValues().SLOT_L_HAND());
                }
                player.equipItem(item);
                final LuckyItem oldWeapon = this.getPlayerData(player).getActiveWeapon();
                if (itemType.isWeapon()) {
                    this.getPlayerData(player).setActiveWeapon(itemType);
                    this.weaponSkills(player, oldWeapon, itemType);
                }
                else {
                    this.weaponSkills(player, null, itemType);
                }
            }
            player.broadcastUserInfo();
            this.getPlayerData(player).addWeapon(itemType);
            if (this._customShortcuts) {
                final SlotInfo slot = this.getPlayerData(player).getNextFreeShortcutSlot(true);
                if (slot != null) {
                    final ShortCutData sh = player.createItemShortcut(slot.slot, slot.page, item);
                    this.getPlayerData(player).addWeaponShortcut(itemType, sh);
                    player.registerShortcut(sh, true);
                }
            }
            if (this._skillsForItems != null && this._skillsForItems.containsKey(itemType)) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForItems.get(itemType).entrySet()) {
                    final SkillData sk = new SkillData(e.getKey(), e.getValue());
                    if (sk.exists()) {
                        player.addSkill(sk, false);
                    }
                }
                player.sendSkillList();
            }
            this.checkShield(player);
        }
    }
    
    private void removeSkill(final PlayerEventInfo player, final SkillData skill, final SkillType type, final boolean updateSkillList) {
        if (this.getPlayerData(player).hasSkill(skill.getId(), type)) {
            player.removeBuff(skill.getId());
            player.removeSkill(skill.getId());
            if (updateSkillList) {
                player.sendSkillList();
            }
            if (this._customShortcuts && updateSkillList) {
                final ShortCutData sh = this.getPlayerData(player).getSkillShortcut(skill);
                if (sh != null) {
                    player.removeShortCut(sh, true);
                    this.getPlayerData(player).removeSkillShortcut(skill, sh);
                }
            }
        }
    }
    
    private void removeWeapon(final PlayerEventInfo player, final LuckyItem type) {
        if (this.getPlayerData(player).hasWeapon(type)) {
            final int itemId = this.getItemId(type);
            ItemData wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
            if (wpn.exists() && wpn.getItemId() == itemId) {
                final ItemData[] unequiped = player.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
                player.inventoryUpdate(unequiped);
            }
            wpn = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
            if (wpn.exists() && wpn.getItemId() == itemId) {
                final ItemData[] unequiped = player.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
                player.inventoryUpdate(unequiped);
            }
            if (this._customShortcuts) {
                final ShortCutData sh = this.getPlayerData(player).getWeaponShortCut(type);
                if (sh != null) {
                    player.removeShortCut(sh, true);
                    this.getPlayerData(player).removeWeaponShortcut(type, sh);
                }
            }
            this.getPlayerData(player).removeWeapon(type);
            if (this.getPlayerData(player).getActiveWeapon() == type) {
                this.getPlayerData(player).setActiveWeapon(null);
            }
            player.destroyItemByItemId(itemId, 1);
            if (this._skillsForItems != null && this._skillsForItems.containsKey(type)) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForItems.get(type).entrySet()) {
                    final SkillData sk = new SkillData(e.getKey(), e.getValue());
                    if (sk.exists()) {
                        player.removeSkill(sk.getId());
                    }
                }
                player.sendSkillList();
            }
            this.checkShield(player);
        }
    }
    
    @Override
    public void onItemUse(final PlayerEventInfo player, final ItemData item) {
        final LuckyItem itemType = this.getWeaponType(item.getItemId());
        if (itemType == null) {
            return;
        }
        if (itemType.isWeapon()) {
            if (item.isEquipped() && item.isWeapon()) {
                final LuckyItem oldWeapon = this.getPlayerData(player).getActiveWeapon();
                if (oldWeapon == itemType) {
                    this.checkShield(player);
                    return;
                }
                this.getPlayerData(player).setActiveWeapon(itemType);
                this.weaponSkills(player, oldWeapon, itemType);
            }
        }
        else if (item.isEquipped()) {
            this.weaponSkills(player, null, itemType);
        }
        else {
            this.weaponSkills(player, itemType, null);
        }
        this.updateSkillsForWeapon(player);
        this.checkShield(player);
        this.checkEventEnd(player.getInstanceId());
    }
    
    private void checkShield(final PlayerEventInfo player) {
        if (this.hasShieldEquipped(player)) {
            if (!this.getPlayerData(player).hasShield()) {
                this.weaponSkills(player, null, LuckyItem.SHIELD);
                this.getPlayerData(player).setHasShield(true);
            }
        }
        else if (this.getPlayerData(player).hasShield()) {
            this.weaponSkills(player, LuckyItem.SHIELD, null);
            this.getPlayerData(player).setHasShield(false);
        }
        if (this.getPlayerData(player).getActiveWeapon() != null && !this.hasWeaponEquipped(player, this.getPlayerData(player).getActiveWeapon())) {
            this.weaponSkills(player, this.getPlayerData(player).getActiveWeapon(), null);
        }
    }
    
    private boolean hasWeaponEquipped(final PlayerEventInfo player, final LuckyItem item) {
        final ItemData i = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_RHAND());
        return i.exists() && this.getWeaponType(i.getItemId()) == item;
    }
    
    private boolean hasShieldEquipped(final PlayerEventInfo player) {
        final ItemData shield = player.getPaperdollItem(CallBack.getInstance().getValues().PAPERDOLL_LHAND());
        return shield.exists() && this.getWeaponType(shield.getItemId()) == LuckyItem.SHIELD;
    }
    
    private void weaponSkills(final PlayerEventInfo player, final LuckyItem oldWeapon, final LuckyItem newWeapon) {
        if (this._skillsForItems != null) {
            if (oldWeapon != null && this._skillsForItems.containsKey(oldWeapon)) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForItems.get(oldWeapon).entrySet()) {
                    final SkillData sk = new SkillData(e.getKey(), e.getValue());
                    if (sk.exists()) {
                        player.removeSkill(sk.getId());
                    }
                }
            }
            if (newWeapon != null && this._skillsForItems.containsKey(newWeapon)) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForItems.get(newWeapon).entrySet()) {
                    final SkillData sk = new SkillData(e.getKey(), e.getValue());
                    if (sk.exists()) {
                        player.addSkill(sk, false);
                    }
                }
            }
            player.sendSkillList();
        }
    }
    
    protected boolean hasWeapon(final PlayerEventInfo player, final LuckyItem type) {
        return this.getPlayerData(player).hasWeapon(type);
    }
    
    private int random(final int max) {
        return CallBack.getInstance().getOut().random(max);
    }
    
    private int random(final int min, final int max) {
        return CallBack.getInstance().getOut().random(min, max);
    }
    
    private synchronized ActionData selectAction(final PlayerEventInfo player, final NpcData npc, final ChestType type) {
        switch (type) {
            case CLASSIC: {
                if (this.random(100) >= this.classicPositiveEffect) {
                    final int chance = this.random(100);
                    if (chance < 4) {
                        final EffectResult result = this.effect(player, npc, EffectType.FearNoPoint, null, null);
                        if (result.success) {
                            return new ActionData(true, false, false, result.resetKillstreak);
                        }
                    }
                    else if (chance >= 4 && chance < 8) {
                        final EffectResult result = this.effect(player, npc, EffectType.ParalyzeNoPoint, null, null);
                        if (result.success) {
                            return new ActionData(true, false, false, result.resetKillstreak);
                        }
                    }
                    else if (chance >= 8 && chance < 18 && this._allowTransformations) {
                        final EffectResult result = this.effect(player, npc, EffectType.TransformToFrog, null, null);
                        if (result.success) {
                            return new ActionData(true, false, false, true);
                        }
                    }
                    else {
                        final EffectResult result = this.effect(player, npc, EffectType.Explode, null, null);
                        if (result.success) {
                            return new ActionData(false, false, false, result.resetKillstreak);
                        }
                    }
                    break;
                }
                if (this.random(100) >= this.classicObtainWeaponChance) {
                    final int chance = this.random(100);
                    if (chance < 2) {
                        if (this.effect(player, npc, EffectType.SpawnBonusChests, this.random(2, 3), true).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 2 && chance < 12) {
                        if (this.effect(player, npc, EffectType.BombShieldOneBomb, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 12 && chance < 16) {
                        if (this.effect(player, npc, EffectType.WindWalkTillDie, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 16 && chance < 20) {
                        if (this.effect(player, npc, EffectType.ScoreLargeFirework, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 20 && chance < 30) {
                        if (this.effect(player, npc, EffectType.ScoreFirework, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (this.effect(player, npc, EffectType.Score, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    break;
                }
                boolean given = false;
                if (this.random(3) == 0) {
                    final LuckyItem item = LuckyItem.HAMMER;
                    if (!this.getPlayerData(player).hasWeapon(item)) {
                        this.giveWeapon(player, item, true, false);
                        this.giveSkillForWeapon(player, item, this._stunSkill, 20);
                        player.screenMessage(LanguageEngine.getMsg("chests_obtainedWeapon_hammer"), this.getEventName(), false);
                        given = true;
                    }
                }
                if (!given) {
                    final LuckyItem item = LuckyItem.BOW;
                    if (!this.getPlayerData(player).hasWeapon(item)) {
                        this.giveWeapon(player, item, true, false);
                        player.screenMessage(LanguageEngine.getMsg("chests_obtainedWeapon_bow"), this.getEventName(), false);
                        given = true;
                    }
                }
                if (this.effect(player, npc, EffectType.Score, null, null).success) {
                    return new ActionData(true, true, true, false);
                }
                break;
            }
            case SHABBY: {
                if (this.random(100) < this.shabbyTransformChance && this._allowTransformations) {
                    final int chance = this.random(100);
                    if (chance < 40) {
                        if (this.effect(player, npc, EffectType.TransformToPig, null, null).success) {
                            return new ActionData(true, true, false, false);
                        }
                    }
                    else if (chance >= 40 && chance < 80) {
                        if (this.effect(player, npc, EffectType.TransformToBunny, null, null).success) {
                            return new ActionData(true, true, false, false);
                        }
                    }
                    else if (this.effect(player, npc, EffectType.TransformToYeti, null, null).success) {
                        return new ActionData(true, true, false, false);
                    }
                    break;
                }
                if (this.random(100) < this.shabbyShieldChance) {
                    if (this.effect(player, npc, EffectType.BombShieldOneBomb, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    break;
                }
                else {
                    if (this.effect(player, npc, EffectType.Score, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    break;
                }
            }
            case LUXURIOUS: {
                if (this.random(100) >= this.luxPositiveEffect) {
                    final int chance = this.random(100);
                    if (chance < 30 && this._allowTransformations) {
                        final EffectResult result = this.effect(player, npc, EffectType.TransformToFrog, null, null);
                        if (result.success) {
                            return new ActionData(true, false, false, true);
                        }
                    }
                    else {
                        final EffectResult result = this.effect(player, npc, EffectType.Explode, null, null);
                        if (result.success) {
                            return new ActionData(false, false, false, result.resetKillstreak);
                        }
                    }
                    break;
                }
                if (this.random(100) >= this.luxObtainWeaponChance) {
                    final int chance = this.random(100);
                    if (chance < 5) {
                        if (this.effect(player, npc, EffectType.SpawnBonusChests, this.random(2, 3), true).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 5 && chance < 10) {
                        if (this.effect(player, npc, EffectType.IncreaseCritRate, 4, true).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 10 && chance < 14) {
                        if (this.effect(player, npc, EffectType.BombShieldOneBomb, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 14 && chance < 22) {
                        if (this.effect(player, npc, EffectType.WindWalkTillDie, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 22 && chance < 30) {
                        if (this.effect(player, npc, EffectType.ScoreLargeFirework, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (chance >= 30 && chance < 50) {
                        if (this.effect(player, npc, EffectType.ScoreFirework, null, null).success) {
                            return new ActionData(true, true, true, false);
                        }
                    }
                    else if (this.effect(player, npc, EffectType.Score, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    break;
                }
                boolean given = false;
                if (this.random(3) == 0) {
                    final LuckyItem item = LuckyItem.HAMMER;
                    if (!this.getPlayerData(player).hasWeapon(item)) {
                        this.giveWeapon(player, item, true, false);
                        this.giveSkillForWeapon(player, item, this._stunSkill, 20);
                        player.screenMessage(LanguageEngine.getMsg("chests_obtainedWeapon_hammer"), this.getEventName(), false);
                        given = true;
                    }
                }
                if (!given) {
                    final LuckyItem item = LuckyItem.BOW;
                    if (!this.getPlayerData(player).hasWeapon(item)) {
                        this.giveWeapon(player, item, true, false);
                        player.screenMessage(LanguageEngine.getMsg("chests_obtainedWeapon_bow"), this.getEventName(), false);
                        given = true;
                    }
                }
                if (this.effect(player, npc, EffectType.Score, null, null).success) {
                    return new ActionData(true, true, true, false);
                }
                break;
            }
            case BOX: {
                switch (this.getPlayerData(player).getActiveWeapon()) {
                    case GLADIUS: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.KNIGHTSWORD, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case DIRK: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.DAGGER_CRAFTED, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case BOW: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.LONGBOW, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case LANCE: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.PIKE, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case HAMMER: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.HEAVYHAMMER, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case ZWEIHANDER: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.HEAVYSWORD, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                    }
                    case SABER: {
                        if (this.effect(player, npc, EffectType.Weapon, LuckyItem.SHIELD, null).success) {
                            return new ActionData(true, false, false, false);
                        }
                        break;
                    }
                }
                final int chance = this.random(100);
                boolean given2 = false;
                if (chance < 5 && this.effect(player, npc, EffectType.Weapon, LuckyItem.REINFORCED_BOW, null).success) {
                    given2 = true;
                    return new ActionData(true, false, false, false);
                }
                if (!given2 && chance >= 5 && chance < 15 && this.effect(player, npc, EffectType.Weapon, LuckyItem.SABER, null).success) {
                    given2 = true;
                    return new ActionData(true, false, false, false);
                }
                if (!given2 && this.effect(player, npc, EffectType.SpawnBonusChests, this.random(3, 4), false).success) {
                    return new ActionData(true, false, false, false);
                }
                break;
            }
            case NEXUSED: {
                player.setCurrentHp(player.getMaxHp());
                player.setCurrentMp(player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
                final int chance = this.random(100);
                if (chance < this.nexusedMainEffectChance) {
                    if (this.effect(player, npc, EffectType.SkillRush, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    if (this.effect(player, npc, EffectType.ScoreLargeFirework, null, null).success) {
                        return new ActionData(true, true, true, false);
                    }
                    break;
                }
                else {
                    if (this.effect(player, npc, EffectType.SpawnBonusChests, 3, false).success) {
                        return new ActionData(true, false, false, false);
                    }
                    break;
                }
            }
        }
        return null;
    }
    
    private EffectResult effect(final PlayerEventInfo player, final NpcData npc, final EffectType type, final Object param, final Object param2) {
        switch (type) {
            case Score: {
                this.getPlayerData(player).raiseScore(1);
                player.screenMessage(LanguageEngine.getMsg("chests_player_scored"), this.getEventName(), true);
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                return new EffectResult(true);
            }
            case ScoreFirework: {
                final SkillData skill = new SkillData(5965, 1);
                if (skill.exists()) {
                    player.broadcastSkillUse(null, null, skill.getId(), skill.getLevel());
                }
                player.screenMessage(LanguageEngine.getMsg("chests_player_scored_x2"), this.getEventName(), true);
                this.getPlayerData(player).raiseScore(2);
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 2);
                return new EffectResult(true);
            }
            case ScoreLargeFirework: {
                final SkillData skill = new SkillData(5966, 1);
                if (skill.exists()) {
                    player.broadcastSkillUse(null, null, skill.getId(), skill.getLevel());
                }
                player.screenMessage(LanguageEngine.getMsg("chests_player_scored_x4"), this.getEventName(), true);
                this.getPlayerData(player).raiseScore(4);
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 4);
                return new EffectResult(true);
            }
            case WindWalkTillDie: {
                final int id = 35018;
                final boolean raiseLevel = param2 != null && (boolean)param2;
                if (!this.getPlayerData(player).hasSkill(35018, SkillType.TILL_DIE)) {
                    int level;
                    if (raiseLevel) {
                        level = 1;
                    }
                    else {
                        level = (int)((param == null) ? 1 : param);
                    }
                    final SkillData skill2 = new SkillData(35018, level);
                    if (skill2.exists()) {
                        player.getSkillEffects(skill2.getId(), skill2.getLevel());
                    }
                    player.screenMessage(LanguageEngine.getMsg("chests_player_windWalk", level), this.getEventName(), true);
                    this.getPlayerData(player).addSkill(35018, level, SkillType.TILL_DIE);
                }
                else {
                    boolean addedBuff = false;
                    if (raiseLevel && param != null && param instanceof Integer) {
                        final int newLevel = this.getPlayerData(player).getLevel(35018, SkillType.TILL_DIE) + 1;
                        final int maxLevel = (int)param;
                        if (newLevel <= maxLevel && this.getPlayerData(player).getLevel(35018, SkillType.TILL_DIE) < newLevel) {
                            final SkillData skill3 = new SkillData(35018, newLevel);
                            if (skill3.exists()) {
                                player.removeBuff(skill3.getId());
                                player.getSkillEffects(skill3.getId(), skill3.getLevel());
                                player.screenMessage(LanguageEngine.getMsg("chests_player_windWalk_upgrade", skill3.getLevel()), this.getEventName(), false);
                                this.getPlayerData(player).addSkill(35018, newLevel, SkillType.TILL_DIE);
                                addedBuff = true;
                            }
                        }
                    }
                    if (!addedBuff) {
                        break;
                    }
                }
                this.getPlayerData(player).raiseScore(1);
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                return new EffectResult(true);
            }
            case SkillAggression: {
                final int id = this._aggressionSkillId;
                if (!this.getPlayerData(player).hasSkill(id, SkillType.PERMANENT) && (this.getPlayerData(player).hasWeapon(LuckyItem.LANCE) || this.getPlayerData(player).hasWeapon(LuckyItem.PIKE))) {
                    final int level2 = 1;
                    final SkillData skill4 = new SkillData(id, 1);
                    if (skill4.exists()) {
                        this.giveSkill(player, skill4);
                    }
                    player.screenMessage(LanguageEngine.getMsg("chests_player_hateAura"), this.getEventName(), true);
                    this.getPlayerData(player).addSkill(id, 1, SkillType.PERMANENT);
                    this.getPlayerData(player).raiseScore(1);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                    return new EffectResult(true);
                }
                break;
            }
            case SkillWhirlwind: {
                final int id = this._whirlwindSkill;
                if (!this.getPlayerData(player).hasSkill(id, SkillType.PERMANENT) && (this.getPlayerData(player).hasWeapon(LuckyItem.LANCE) || this.getPlayerData(player).hasWeapon(LuckyItem.PIKE))) {
                    final int level2 = 20;
                    final SkillData skill4 = new SkillData(id, 20);
                    if (skill4.exists()) {
                        this.giveSkill(player, skill4);
                    }
                    player.screenMessage(LanguageEngine.getMsg("chests_player_whirlWind"), this.getEventName(), true);
                    this.getPlayerData(player).addSkill(id, 20, SkillType.PERMANENT);
                    this.getPlayerData(player).raiseScore(1);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                    return new EffectResult(true);
                }
                break;
            }
            case SkillRush: {
                final int id = this._rushSkill;
                if (!this.getPlayerData(player).hasSkill(id, SkillType.PERMANENT)) {
                    final int level2 = 1;
                    final SkillData skill4 = new SkillData(id, 1);
                    if (skill4.exists()) {
                        this.giveSkill(player, skill4);
                    }
                    player.screenMessage(LanguageEngine.getMsg("chests_player_rushSkill"), this.getEventName(), true);
                    this.getPlayerData(player).addSkill(id, 1, SkillType.PERMANENT);
                    this.getPlayerData(player).raiseScore(1);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                    return new EffectResult(true);
                }
                break;
            }
            case BombShieldOneBomb: {
                this.getPlayerData(player).raiseScore(1);
                if (this.getPlayerData(player).hasBombShield() <= 3) {
                    this.getPlayerData(player).raiseBombShield(1);
                    this.updateBombShield(player);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield"), this.getEventName(), true);
                    if (this.getPlayerData(player).hasBombShield() > 1) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield_info", this.getPlayerData(player).hasBombShield()), "Shield info", false);
                    }
                }
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                return new EffectResult(true);
            }
            case IncreaseCritRate: {
                final int id = 35019;
                final boolean raiseLevel = param2 != null && (boolean)param2;
                if (!this.getPlayerData(player).hasSkill(35019, SkillType.PERMANENT)) {
                    int level;
                    if (raiseLevel) {
                        level = 1;
                    }
                    else {
                        level = (int)((param == null) ? 1 : param);
                    }
                    final SkillData skill2 = new SkillData(35019, level);
                    if (skill2.exists()) {
                        player.getSkillEffects(skill2.getId(), skill2.getLevel());
                    }
                    player.screenMessage(LanguageEngine.getMsg("chests_player_criticalRateBuff", level), this.getEventName(), true);
                    this.getPlayerData(player).addSkill(35019, level, SkillType.PERMANENT);
                }
                else {
                    boolean addedBuff = false;
                    if (raiseLevel && param != null && param instanceof Integer) {
                        final int newLevel = this.getPlayerData(player).getLevel(35019, SkillType.PERMANENT) + 1;
                        final int maxLevel = (int)param;
                        if (newLevel <= maxLevel && this.getPlayerData(player).getLevel(35019, SkillType.PERMANENT) < newLevel) {
                            final SkillData skill3 = new SkillData(35019, newLevel);
                            if (skill3.exists()) {
                                player.removeBuff(skill3.getId());
                                player.getSkillEffects(skill3.getId(), skill3.getLevel());
                                player.screenMessage(LanguageEngine.getMsg("chests_player_criticalRateBuff_levelUp", skill3.getLevel()), this.getEventName(), false);
                                this.getPlayerData(player).addSkill(35019, newLevel, SkillType.PERMANENT);
                                addedBuff = true;
                            }
                        }
                    }
                    if (!addedBuff) {
                        break;
                    }
                }
                this.getPlayerData(player).raiseScore(1);
                if (player.isTitleUpdated()) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                this.setScoreStats(player, this.getPlayerData(player).getScore());
                CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                return new EffectResult(true);
            }
            case SpawnBonusChests: {
                final boolean score = param2 == null || !(param2 instanceof Boolean) || (boolean)param2;
                if (score) {
                    this.getPlayerData(player).raiseScore(1);
                    if (player.isTitleUpdated()) {
                        player.setTitle(this.getTitle(player), true);
                        player.broadcastTitleInfo();
                    }
                    this.setScoreStats(player, this.getPlayerData(player).getScore());
                    CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
                }
                final int instanceId = player.getInstanceId();
                final int count = (int)((param != null && param instanceof Integer) ? param : this.random(2, 5));
                final int chestId = this._classicChestId;
                for (int i = 0; i < count; ++i) {
                    final Loc loc = new Loc(player.getX(), player.getY(), player.getZ());
                    loc.addRadius((param2 != null && param2 instanceof Integer) ? ((int)param2) : 150);
                    final NpcData newChest = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), chestId, instanceId, null, null);
                    newChest.broadcastSkillUse(newChest, newChest, 5965, 1);
                    this.getEventData(instanceId)._chests.get(this.getType(chestId)).add(newChest);
                }
                return new EffectResult(true);
            }
            case Weapon: {
                if (param == null || !(param instanceof LuckyItem)) {
                    break;
                }
                final LuckyItem item = (LuckyItem)param;
                if (this.getPlayerData(player).hasWeapon(item)) {
                    return new EffectResult(false);
                }
                final Boolean equip = (param instanceof Boolean) ? ((Boolean)param) : null;
                this.giveWeapon(player, item, equip == null || equip, false);
                return new EffectResult(true);
            }
            case Laugh: {
                if (!npc.isDead()) {
                    final String s = this._jokerChestTexts[CallBack.getInstance().getOut().random(this._jokerChestTexts.length)];
                    npc.creatureSay(0, this._jokerChestName, s);
                    player.screenMessage(s, this.getEventName(), true);
                    final ChestType chestType = this.getType(npc.getNpcId());
                    try {
                        for (final NpcData ch : this.getEventData(player.getInstanceId())._chests.get(chestType)) {
                            if (ch != null && ch.getObjectId() == npc.getObjectId()) {
                                synchronized (this.getEventData(player.getInstanceId())._chests) {
                                    this.getEventData(player.getInstanceId())._chests.get(chestType).remove(ch);
                                }
                                this.checkEventEnd(player.getInstanceId());
                                break;
                            }
                        }
                    }
                    catch (Exception ex) {}
                    npc.deleteMe();
                    return new EffectResult(true);
                }
                break;
            }
            case Explode: {
                npc.broadcastSkillUse(npc, player.getCharacterData(), 5430, 1);
                boolean wasProtected = false;
                if (this.getPlayerData(player).hasWeapon(LuckyItem.BOW) || this.getPlayerData(player).hasWeapon(LuckyItem.LONGBOW) || this.getPlayerData(player).hasWeapon(LuckyItem.REINFORCED_BOW)) {
                    wasProtected = true;
                }
                else if (this.getPlayerData(player).hasShield()) {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_itemShieldSuccess"), this.getEventName(), true);
                    wasProtected = true;
                }
                else if (this.getPlayerData(player).hasBombShield() > 0) {
                    this.getPlayerData(player).decreaseBombShield(1);
                    this.updateBombShield(player);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_bombShieldSuccess"), this.getEventName(), true);
                    wasProtected = true;
                    if (this.getPlayerData(player).hasBombShield() > 0) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield_info2", this.getPlayerData(player).hasBombShield()), this.getEventName(), false);
                    }
                }
                else if (this.getPlayerData(player).hasDeathstreakShield()) {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_deathStreakShieldSuccess"), this.getEventName(), true);
                    wasProtected = true;
                }
                else {
                    player.doDie();
                }
                CallBack.getInstance().getOut().scheduleGeneral(() -> npc.deleteMe(), 200L);
                if (wasProtected) {
                    return new EffectResult(true, false, false, this._explosionShieldResetKillstreak);
                }
                return new EffectResult(true, false, false, true);
            }
            case BigHead: {
                if (!this.getPlayerData(player).hasBigHead()) {
                    this.getPlayerData(player).setHasBigHead(true);
                    player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_BIG_HEAD());
                    if (player.isTitleUpdated()) {
                        player.setTitle("owned", true);
                        player.broadcastTitleInfo();
                    }
                    CallBack.getInstance().getOut().scheduleGeneral(() -> {
                        if (player.isOnline()) {
                            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_BIG_HEAD());
                            this.getPlayerData(player).setHasBigHead(false);
                            if (player.isTitleUpdated()) {
                                player.setTitle(this.getTitle(player), true);
                                player.broadcastTitleInfo();
                            }
                        }
                        return;
                    }, 60000L);
                    return new EffectResult(true);
                }
                break;
            }
            case ParalyzeNoPoint: {
                boolean wasProtected = false;
                if (this.getPlayerData(player).hasBombShield() > 0 && this._bombShieldProtectsParalyzation) {
                    this.getPlayerData(player).decreaseBombShield(1);
                    this.updateBombShield(player);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_bombShieldSuccess_curse"), this.getEventName(), true);
                    wasProtected = true;
                    if (this.getPlayerData(player).hasBombShield() > 0) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield_info2", this.getPlayerData(player).hasBombShield()), this.getEventName(), false);
                    }
                }
                else if (this.getPlayerData(player).hasDeathstreakShield()) {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_deathStreakShieldSuccess_curse"), this.getEventName(), true);
                    wasProtected = true;
                }
                else {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_paralyzed"), this.getEventName(), true);
                    player.getSkillEffects(35016, 1);
                }
                if (wasProtected) {
                    return new EffectResult(true, false, false, false);
                }
                return new EffectResult(true, false, false, true);
            }
            case FearNoPoint: {
                boolean wasProtected = false;
                npc.setName(LanguageEngine.getMsg("chests_player_horrifyingChest"));
                npc.broadcastNpcInfo();
                if (this.getPlayerData(player).hasBombShield() > 0 && this._bombShieldProtectsFear) {
                    this.getPlayerData(player).decreaseBombShield(1);
                    this.updateBombShield(player);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_bombShieldSuccess_curse"), this.getEventName(), true);
                    wasProtected = true;
                    if (this.getPlayerData(player).hasBombShield() > 0) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield_info2", this.getPlayerData(player).hasBombShield()), this.getEventName(), false);
                    }
                }
                else if (this.getPlayerData(player).hasDeathstreakShield()) {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_deathStreakShieldSuccess_curse"), this.getEventName(), true);
                    wasProtected = true;
                }
                else {
                    player.screenMessage("!!!", this.getEventName(), true);
                    player.getSkillEffects(35017, 1);
                    player.getSkillEffects(1092, 1);
                    if (this._enableFearFireworking) {
                        for (int j = 1; j <= 4; ++j) {
                            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                                if (player.isOnline() && this.getMatch(player.getInstanceId()).isActive() && !player.isDead() && player.isAfraid()) {
                                    player.broadcastSkillUse(null, null, 5965, 1);
                                }
                                return;
                            }, j * 2000);
                        }
                    }
                }
                if (wasProtected) {
                    return new EffectResult(true, false, false, false);
                }
                return new EffectResult(true, false, false, true);
            }
            case TransformToBunny: {
                final int transformLasts = this._bunnyTransformDuration;
                final int transformId = this._bunnyTransformId;
                final int rabbitStatsSkill = 35021;
                if (transformId > 0 && this.getPlayerData(player).getTransformation() == null) {
                    this.getPlayerData(player).setTransformed(TransformType.BUNNY);
                    this.transformPlayer(player, transformId);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_bunny1"), this.getEventName(), true);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_bunny2"), this.getEventName(), false);
                    player.sendMessage("* " + LanguageEngine.getMsg("chests_player_transform_bunny3", transformLasts));
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                    this.sysMsgToAll(player.getInstanceId(), LanguageEngine.getMsg("chests_player_transform_bunny_announce", player.getPlayersName()));
                    final SkillData skill2 = new SkillData(35021, 1);
                    if (skill2.exists()) {
                        player.addSkill(skill2, false);
                    }
                    this.getPlayerData(player).addSkill(35021, 1, SkillType.TRANSFORM);
                    player.sendSkillList();
                    player.setCurrentHp(player.getMaxHp());
                    player.setCurrentMp(player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                    this.scheduleUntransform(player, TransformType.BUNNY, transformLasts * 1000);
                    return new EffectResult(true);
                }
                break;
            }
            case TransformToFrog: {
                final int transformLasts = this._frogTransformDuration;
                final int transformId = this._frogTransformId;
                final int frogStatsSkill = 35022;
                if (transformId > 0 && this.getPlayerData(player).getTransformation() == null) {
                    this.getPlayerData(player).setTransformed(TransformType.FROG);
                    this.transformPlayer(player, transformId);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_frog1"), this.getEventName(), true);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_frog2"), this.getEventName(), false);
                    player.sendMessage("* " + LanguageEngine.getMsg("chests_player_transform_frog3", transformLasts));
                    player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                    this.sysMsgToAll(player.getInstanceId(), LanguageEngine.getMsg("chests_player_transform_frog_announce", player.getPlayersName()));
                    final SkillData skill2 = new SkillData(35022, 1);
                    if (skill2.exists()) {
                        player.addSkill(skill2, false);
                    }
                    this.getPlayerData(player).addSkill(35022, 1, SkillType.TRANSFORM);
                    player.sendSkillList();
                    player.setCurrentHp(player.getMaxHp());
                    player.setCurrentMp(player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                    this.startFrogTask(player, transformLasts * 1000);
                    this.scheduleUntransform(player, TransformType.FROG, transformLasts * 1000);
                    return new EffectResult(true);
                }
                break;
            }
            case TransformToPig: {
                final int transformLasts = this._pigTransformDuration;
                final int transformId = this._pigTransformId;
                final int pigStatsSkill = 35023;
                if (transformId > 0 && this.getPlayerData(player).getTransformation() == null) {
                    this.getPlayerData(player).setTransformed(TransformType.PIG);
                    this.transformPlayer(player, transformId);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_pig1"), this.getEventName(), true);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_pig2"), this.getEventName(), false);
                    player.sendMessage("* " + LanguageEngine.getMsg("chests_player_transform_pig3", transformLasts));
                    player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                    this.sysMsgToAll(player.getInstanceId(), LanguageEngine.getMsg("chests_player_transform_pig_announce", player.getPlayersName()));
                    final SkillData skill2 = new SkillData(35023, 1);
                    if (skill2.exists()) {
                        player.addSkill(skill2, false);
                    }
                    this.getPlayerData(player).addSkill(35023, 1, SkillType.TRANSFORM);
                    player.sendSkillList();
                    player.setCurrentHp(player.getMaxHp());
                    player.setCurrentMp(player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                    this.scheduleUntransform(player, TransformType.PIG, transformLasts * 1000);
                    return new EffectResult(true);
                }
                break;
            }
            case TransformToYeti: {
                final int transformLasts = this._yetiTransformDuration;
                final int transformId = this._yetiTransformId;
                final int yetiStatsSkill = 35024;
                if (transformId > 0 && this.getPlayerData(player).getTransformation() == null) {
                    this.getPlayerData(player).setTransformed(TransformType.YETI);
                    this.transformPlayer(player, transformId);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_yeti1"), this.getEventName(), true);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_yeti2"), this.getEventName(), false);
                    player.sendMessage("* " + LanguageEngine.getMsg("chests_player_transform_yeti3", transformLasts));
                    player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                    this.announce(player.getInstanceId(), LanguageEngine.getMsg("chests_player_transform_yeti_announce", player.getPlayersName()));
                    final SkillData skill2 = new SkillData(35024, 1);
                    if (skill2.exists()) {
                        player.addSkill(skill2, false);
                    }
                    this.getPlayerData(player).addSkill(35024, 1, SkillType.TRANSFORM);
                    player.sendSkillList();
                    player.setCurrentHp(player.getMaxHp());
                    player.setCurrentMp(player.getMaxMp());
                    player.setCurrentCp(player.getMaxCp());
                    this.startYetiTask(player, transformLasts * 1000);
                    this.scheduleUntransform(player, TransformType.YETI, transformLasts * 1000);
                    return new EffectResult(true);
                }
                break;
            }
        }
        return new EffectResult(false);
    }
    
    protected void checkEventEnd(final int instance) {
        boolean empty = true;
        for (final List<NpcData> e : this.getEventData(instance)._chests.values()) {
            if (!e.isEmpty()) {
                empty = false;
                break;
            }
        }
        if (empty) {
            this.announce("All chests were killed. Event has ended.");
            this.endInstance(instance, true, false, false);
        }
    }
    
    private void scheduleUntransform(final PlayerEventInfo player, final TransformType type, final int delay) {
        CallBack.getInstance().getOut().scheduleGeneral(() -> {
            if (player.isOnline() && !player.isDead() && this.getPlayerData(player) != null && this.getPlayerData(player).getTransformation() == type) {
                this.untransformPlayer(player);
                player.setCurrentHp(player.getMaxHp());
                player.setCurrentMp(player.getMaxMp());
                player.setCurrentCp(player.getMaxCp());
            }
        }, delay);
    }
    
    private void startYetiTask(final PlayerEventInfo player, final int duration) {
        final int interval = 2500;
        for (int i = 2500; i < duration; i += 2500) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                if (player.isOnline() && !player.isDead() && this.getPlayerData(player).getTransformation() == TransformType.YETI) {
                    player.setTitle(this.getTitle(player), true);
                    player.broadcastTitleInfo();
                }
                return;
            }, i);
        }
    }
    
    private void startFrogTask(final PlayerEventInfo player, final int duration) {
        final int interval = 3000;
        for (int i = 3000; i < duration; i += 3000) {
            CallBack.getInstance().getOut().scheduleGeneral(() -> {
                if (player.isOnline() && !player.isDead() && this.getPlayerData(player).getTransformation() == TransformType.FROG) {
                    player.broadcastSkillUse(null, null, 5965, 1);
                }
                return;
            }, i);
        }
    }
    
    private void transformPlayer(final PlayerEventInfo player, final int transformId) {
        player.untransform(true);
        player.transform(transformId);
    }
    
    private void untransformPlayer(final PlayerEventInfo player) {
        for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkills(SkillType.TRANSFORM).entrySet()) {
            final SkillData skill = new SkillData(e.getKey(), e.getValue());
            if (skill.exists()) {
                this.removeSkill(player, skill, SkillType.TRANSFORM, true);
            }
        }
        this.getPlayerData(player).removeSkills(SkillType.TRANSFORM);
        this.getPlayerData(player).setTransformed(null);
        player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_REAL_TARGET());
        player.untransform(true);
        player.setTitle(this.getTitle(player), true);
        player.broadcastTitleInfo();
    }
    
    @Override
    public int allowTransformationSkill(final PlayerEventInfo player, final SkillData skill) {
        if (this.getPlayerData(player).getSkills(SkillType.TRANSFORM).containsKey(skill.getId())) {
            return 1;
        }
        return 0;
    }
    
    private void updateBombShield(final PlayerEventInfo player) {
        if (this.getPlayerData(player).hasDeathstreakShield()) {
            player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_S_INVINCIBLE());
        }
        else {
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_S_INVINCIBLE());
            if (this.getPlayerData(player).hasBombShield() > 0) {
                player.startAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_INVULNERABLE());
            }
            else {
                player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_INVULNERABLE());
            }
        }
    }
    
    private int getItemId(final LuckyItem type) {
        final int itemId = -1;
        switch (type) {
            case GLADIUS: {
                return this._gladiusItemId;
            }
            case DIRK: {
                return this._dirkItemId;
            }
            case BOW: {
                return this._bowItemId;
            }
            case LANCE: {
                return this._lanceItemId;
            }
            case HAMMER: {
                return this._hammerItemId;
            }
            case ZWEIHANDER: {
                return this._zweihanderItemId;
            }
            case SHIELD: {
                return this._shieldItemId;
            }
            case KNIGHTSWORD: {
                return this._knightswordItemId;
            }
            case SABER: {
                return this._saberItemId;
            }
            case LONGBOW: {
                return this._longBowItemId;
            }
            case REINFORCED_BOW: {
                return this._reinforcedBowItemId;
            }
            case PIKE: {
                return this._pikeItemId;
            }
            case DAGGER_CRAFTED: {
                return this._craftedDaggerItemId;
            }
            case HEAVYSWORD: {
                return this._heavyswordItemId;
            }
            case HEAVYHAMMER: {
                return this._heavyHammerItemId;
            }
            default: {
                return itemId;
            }
        }
    }
    
    private LuckyItem getWeaponType(final int itemId) {
        for (final LuckyItem t : LuckyItem.values()) {
            if (this.getItemId(t) == itemId) {
                return t;
            }
        }
        SunriseLoader.debug("Wrong weapon type for LuckyChests event: ItemId: " + String.valueOf(itemId));
        return null;
    }
    
    protected void spawnChests(final int instance) {
        int count = CallBack.getInstance().getOut().random(this._classicChestsCountMin, this._classicChestsCountMax);
        if (SunriseLoader.detailedDebug) {
            this.print("Event: spawning " + count + " classic chests");
        }
        for (int i = 0; i < count; ++i) {
            final EventSpawn sp = this.getSpawn(SpawnType.Chest, -1);
            final Loc loc = sp.getLoc();
            loc.addRadius(sp.getRadius());
            final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), this._classicChestId, instance, null, null);
            this.getEventData(instance)._chests.get(ChestType.CLASSIC).add(npc);
        }
        if (this._shabbyChestEnabled) {
            count = CallBack.getInstance().getOut().random(this._shabbyChestsCountMin, this._shabbyChestsCountMax);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: spawning " + count + " shabby chests");
            }
            for (int i = 0; i < count; ++i) {
                final EventSpawn sp = this.getSpawn(SpawnType.Chest, -1);
                final Loc loc = sp.getLoc();
                loc.addRadius(sp.getRadius());
                final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), this._shabbyChestId, instance, null, null);
                this.getEventData(instance)._chests.get(ChestType.SHABBY).add(npc);
            }
        }
        if (this._luxuriousChestEnabled) {
            count = CallBack.getInstance().getOut().random(this._luxuriousChestsCountMin, this._luxuriousChestsCountMax);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: spawning " + count + " luxurious chests");
            }
            for (int i = 0; i < count; ++i) {
                final EventSpawn sp = this.getSpawn(SpawnType.Chest, -1);
                final Loc loc = sp.getLoc();
                loc.addRadius(sp.getRadius());
                final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), this._luxuriousChestId, instance, null, null);
                this.getEventData(instance)._chests.get(ChestType.LUXURIOUS).add(npc);
            }
        }
        if (this._boxChestEnabled) {
            count = CallBack.getInstance().getOut().random(this._boxChestsCountMin, this._boxChestsCountMax);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: spawning " + count + " box chests");
            }
            for (int i = 0; i < count; ++i) {
                final EventSpawn sp = this.getSpawn(SpawnType.Chest, -1);
                final Loc loc = sp.getLoc();
                loc.addRadius(sp.getRadius());
                final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), this._boxChestId, instance, null, null);
                this.getEventData(instance)._chests.get(ChestType.BOX).add(npc);
            }
        }
        if (this._nexusedChestEnabled) {
            count = CallBack.getInstance().getOut().random(this._nexusedChestsCountMin, this._nexusedChestsCountMax);
            if (SunriseLoader.detailedDebug) {
                this.print("Event: spawning " + count + " nexused chests");
            }
            for (int i = 0; i < count; ++i) {
                final EventSpawn sp = this.getSpawn(SpawnType.Chest, -1);
                final Loc loc = sp.getLoc();
                loc.addRadius(sp.getRadius());
                final NpcData npc = this.spawnNPC(loc.getX(), loc.getY(), loc.getZ(), this._nexusedChestId, instance, null, null);
                this.getEventData(instance)._chests.get(ChestType.NEXUSED).add(npc);
            }
        }
    }
    
    protected void unspawnChests(final int instance) {
        if (this.getEventData(instance)._chests == null) {
            return;
        }
        for (final Map.Entry<ChestType, List<NpcData>> e : this.getEventData(instance)._chests.entrySet()) {
            for (final NpcData ch : e.getValue()) {
                if (ch != null) {
                    ch.deleteMe();
                }
            }
        }
        this.getEventData(instance)._chests.clear();
        this.getEventData(instance)._chests = null;
    }
    
    protected void preparePlayers(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            this.giveWeapon(player, LuckyItem.GLADIUS, true, false);
            if (this._skillsForAll != null) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                    final SkillData skill = new SkillData(e.getKey(), e.getValue());
                    if (skill.exists()) {
                        player.addSkill(skill, false);
                    }
                }
                player.sendSkillList();
            }
            player.untransform(true);
            player.removeBuffs();
            player.removeCubics();
            player.removeSummon();
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }
    
    protected void clearShortcuts(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            player.removeOriginalShortcuts();
        }
    }
    
    protected void restorePlayers(final int instanceId) {
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            for (final LuckyItem t : LuckyItem.values()) {
                this.removeWeapon(player, t);
            }
            if (this._skillsForAll != null) {
                for (final Map.Entry<Integer, Integer> e : this._skillsForAll.entrySet()) {
                    final SkillData skill = new SkillData(e.getKey(), e.getValue());
                    if (skill.exists()) {
                        player.removeSkill(skill.getId());
                    }
                }
            }
            for (final Map.Entry<SkillType, Map<Integer, Integer>> customSkills : this.getPlayerData(player).getAllSkills().entrySet()) {
                for (final Map.Entry<Integer, Integer> e2 : customSkills.getValue().entrySet()) {
                    final SkillData skill = new SkillData(e2.getKey(), e2.getValue());
                    if (skill.exists()) {
                        this.removeSkill(player, skill, customSkills.getKey(), false);
                    }
                }
            }
            for (final Map.Entry<LuckyItem, Map<Integer, Integer>> customSkills2 : this.getPlayerData(player).getAllSkillsForWeapons().entrySet()) {
                for (final Map.Entry<Integer, Integer> e2 : customSkills2.getValue().entrySet()) {
                    final SkillData skill = new SkillData(e2.getKey(), e2.getValue());
                    if (skill.exists()) {
                        player.removeBuff(skill.getId());
                        player.removeSkill(skill.getId());
                    }
                }
            }
            player.sendSkillList();
            player.restoreOriginalShortcuts();
            this.untransformPlayer(player);
            player.setCurrentHp(player.getMaxHp());
            player.setCurrentMp(player.getMaxMp());
            player.setCurrentCp(player.getMaxCp());
        }
    }
    
    @Override
    public void onEventEnd() {
        if (SunriseLoader.detailedDebug) {
            this.print("Event: onEventEnd()");
        }
        final int minScore = this.getInt("scoreForReward");
        this.rewardAllPlayers(-1, minScore, 0);
    }
    
    @Override
    protected String getScorebar(final int instance) {
        final StringBuilder tb = new StringBuilder();
        if (this._scorebarInfo.equals("TopScore")) {
            int top = 0;
            for (final PlayerEventInfo player : this.getPlayers(instance)) {
                if (this.getPlayerData(player).getScore() > top) {
                    top = this.getPlayerData(player).getScore();
                }
            }
            tb.append(LanguageEngine.getMsg("chests_scorebar_topScore") + " " + top + " ");
        }
        else {
            int count = 0;
            for (final List<NpcData> e : this.getEventData(instance)._chests.values()) {
                count += e.size();
            }
            tb.append(LanguageEngine.getMsg("chests_scorebar_chestsLeft") + " " + count);
        }
        tb.append("   " + LanguageEngine.getMsg("event_scorebar_time", this._matches.get(instance).getClock().getTime()));
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
        if (this.getPlayerData(pi).getTransformation() != null) {
            switch (this.getPlayerData(pi).getTransformation()) {
                case BUNNY: {
                    return LanguageEngine.getMsg("chests_player_transform_bunny_title");
                }
                case FROG: {
                    return LanguageEngine.getMsg("chests_player_transform_frog_title");
                }
                case PIG: {
                    return LanguageEngine.getMsg("chests_player_transform_pig_title");
                }
                case YETI: {
                    final int hp = (int)Math.round(pi.getCurrentHp() / pi.getMaxHp() * 100.0);
                    return LanguageEngine.getMsg("chests_player_transform_yeti_title", hp);
                }
            }
        }
        return "Score: " + this.getPlayerData(pi).getScore();
    }
    
    private ChestType getType(final int id) {
        if (id == this._classicChestId) {
            return ChestType.CLASSIC;
        }
        if (id == this._shabbyChestId) {
            return ChestType.SHABBY;
        }
        if (id == this._luxuriousChestId) {
            return ChestType.LUXURIOUS;
        }
        if (id == this._boxChestId) {
            return ChestType.BOX;
        }
        if (id == this._nexusedChestId) {
            return ChestType.NEXUSED;
        }
        return null;
    }
    
    private void resetDeathStreak(final PlayerEventInfo player) {
        if (this.getPlayerData(player).hasDeathstreakShield()) {
            this.getPlayerData(player).removeDeathstreakShield();
            player.sendMessage(LanguageEngine.getMsg("chests_player_deathStreakShieldRemoved"));
            this.updateBombShield(player);
        }
        for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkills(SkillType.TILL_KILL).entrySet()) {
            final SkillData skill = new SkillData(e.getKey(), e.getValue());
            if (skill.exists()) {
                player.screenMessage(LanguageEngine.getMsg("chests_player_buffRemoved", skill.getName(), skill.getLevel()), this.getEventName(), false);
                this.removeSkill(player, skill, SkillType.TILL_KILL, true);
            }
        }
        this.getPlayerData(player).removeSkills(SkillType.TILL_KILL);
        if (this.getPlayerData(player).getDeathStreak() > 0) {
            player.sendMessage(LanguageEngine.getMsg("chests_player_deathstreakReset"));
        }
        this.getPlayerData(player).resetDeathStreak();
    }
    
    private void addKillStreak(final PlayerEventInfo player, final NpcData npc) {
        this.getPlayerData(player).addKillStreak(1);
        final int streak = this.getPlayerData(player).getKillStreak();
        if (streak >= 3) {
            switch (streak) {
                case 3: {
                    if (this.criticalRateBuff(player, 1)) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_killedInRow", 3), this.getEventName(), true);
                        break;
                    }
                    break;
                }
                case 5: {
                    this.getPlayerData(player).raiseBombShield(1);
                    this.updateBombShield(player);
                    player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_bombShield"), this.getEventName(), true);
                    if (this.getPlayerData(player).hasBombShield() > 1) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_bombShield_info", this.getPlayerData(player).hasBombShield()), "Shield info", false);
                        break;
                    }
                    break;
                }
                case 6: {
                    final int rnd = CallBack.getInstance().getOut().random(100);
                    boolean given = false;
                    if (rnd < 30) {
                        final LuckyItem item = LuckyItem.LANCE;
                        if (!this.getPlayerData(player).hasWeapon(item)) {
                            this.giveWeapon(player, item, true, false);
                            this.giveSkillForWeapon(player, item, this._whirlwindSkill, 20);
                            given = true;
                        }
                    }
                    if (!given && rnd >= 30 && rnd < 80) {
                        final LuckyItem item = LuckyItem.DIRK;
                        if (!this.getPlayerData(player).hasWeapon(item)) {
                            this.giveWeapon(player, item, true, false);
                            this.giveSkillForWeapon(player, item, 30, 20);
                            given = true;
                        }
                    }
                    if (!given) {
                        final LuckyItem item = LuckyItem.SABER;
                        if (!this.getPlayerData(player).hasWeapon(item)) {
                            this.giveWeapon(player, item, true, false);
                            given = true;
                        }
                    }
                    if (given) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_weapon"), this.getEventName(), true);
                        break;
                    }
                    break;
                }
                case 8: {
                    if (this.criticalRateBuff(player, 2)) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_killedInRow2", 8), this.getEventName(), true);
                        break;
                    }
                    break;
                }
                case 10: {
                    boolean givenSword = false;
                    final LuckyItem item2 = LuckyItem.ZWEIHANDER;
                    if (!this.getPlayerData(player).hasWeapon(item2)) {
                        this.giveWeapon(player, item2, true, false);
                        givenSword = true;
                    }
                    if (givenSword) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_sword"), this.getEventName(), true);
                        break;
                    }
                    break;
                }
                case 13: {
                    final boolean givenBow = false;
                    final LuckyItem bow = LuckyItem.REINFORCED_BOW;
                    if (!this.getPlayerData(player).hasWeapon(bow)) {
                        this.giveWeapon(player, bow, true, false);
                    }
                    if (givenBow) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_bow"), this.getEventName(), true);
                        break;
                    }
                    break;
                }
                case 15: {
                    boolean givenShield = false;
                    final LuckyItem shield = LuckyItem.SHIELD;
                    if (!this.getPlayerData(player).hasWeapon(shield)) {
                        this.giveWeapon(player, shield, true, false);
                        givenShield = true;
                    }
                    if (givenShield) {
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_itemShield"), this.getEventName(), true);
                        player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_killedInRow3", streak), this.getEventName(), false);
                        break;
                    }
                    break;
                }
                default: {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_killStreak_killedInRow", streak), this.getEventName(), false);
                    break;
                }
            }
        }
    }
    
    private boolean criticalRateBuff(final PlayerEventInfo player, final int maxLevel) {
        final int id = 35019;
        if (this.getPlayerData(player).hasSkill(35019, SkillType.PERMANENT)) {
            boolean addedBuff = false;
            final int newLevel = this.getPlayerData(player).getLevel(35019, SkillType.PERMANENT) + 1;
            if (newLevel <= maxLevel && this.getPlayerData(player).getLevel(35019, SkillType.PERMANENT) < newLevel) {
                final SkillData skill = new SkillData(35019, newLevel);
                if (skill.exists()) {
                    player.removeBuff(skill.getId());
                    player.getSkillEffects(skill.getId(), skill.getLevel());
                    player.screenMessage(LanguageEngine.getMsg("chests_player_criticalRateBuff_levelUp", skill.getLevel()), this.getEventName(), false);
                    this.getPlayerData(player).addSkill(35019, newLevel, SkillType.PERMANENT);
                    addedBuff = true;
                }
            }
            return addedBuff;
        }
        final int level = 1;
        final SkillData skill2 = new SkillData(35019, level);
        if (skill2.exists()) {
            player.getSkillEffects(skill2.getId(), skill2.getLevel());
            player.screenMessage(LanguageEngine.getMsg("chests_player_criticalRateBuff", level), this.getEventName(), true);
            this.getPlayerData(player).addSkill(35019, level, SkillType.PERMANENT);
            return true;
        }
        return false;
    }
    
    private void resetKillStreak(final PlayerEventInfo player, final NpcData npc) {
        if (this.getPlayerData(player).getKillStreak() > 1) {
            player.sendMessage(LanguageEngine.getMsg("chests_player_killStreakReset"));
        }
        this.getPlayerData(player).resetKillStreak();
        for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkills(SkillType.KILLSTREAK).entrySet()) {
            final SkillData skill = new SkillData(e.getKey(), e.getValue());
            if (skill.exists()) {
                this.removeSkill(player, skill, SkillType.KILLSTREAK, true);
            }
        }
        this.getPlayerData(player).removeSkills(SkillType.KILLSTREAK);
    }
    
    public boolean canServerKillChest(final NpcData npc, final PlayerEventInfo killer) {
        if (this.getMatch(killer.getInstanceId()).isActive()) {
            final ChestType type = this.getType(npc.getNpcId());
            if (type == null) {
                return true;
            }
            boolean chestAlive = false;
            for (final NpcData ch : this.getEventData(killer.getInstanceId())._chests.get(type)) {
                if (ch != null && ch.getObjectId() == npc.getObjectId()) {
                    chestAlive = true;
                    break;
                }
            }
            if (!chestAlive) {
                return false;
            }
            final ActionData data = this.selectAction(killer, npc, type);
            for (final NpcData ch2 : this.getEventData(killer.getInstanceId())._chests.get(type)) {
                if (ch2 != null && ch2.getObjectId() == npc.getObjectId()) {
                    synchronized (this.getEventData(killer.getInstanceId())._chests) {
                        this.getEventData(killer.getInstanceId())._chests.get(type).remove(ch2);
                    }
                    this.checkEventEnd(killer.getInstanceId());
                    break;
                }
            }
            if (data != null) {
                if (data.resetDeathstreak) {
                    this.resetDeathStreak(killer);
                }
                if (data.addKillStreak) {
                    this.addKillStreak(killer, npc);
                }
                else if (data.resetKillStreak) {
                    this.resetKillStreak(killer, npc);
                }
                return data.canServerKill;
            }
        }
        return true;
    }
    
    @Override
    public void onKill(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() == null) {
            return;
        }
        int score = 1;
        if (this.getPlayerData(player).getTransformation() != null) {
            switch (this.getPlayerData(player).getTransformation()) {
                case BUNNY: {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_bunny_scoreMsg"), this.getEventName(), true);
                    break;
                }
                case PIG: {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_pig_scoreMsg"), this.getEventName(), true);
                    break;
                }
                case YETI: {
                    player.screenMessage(LanguageEngine.getMsg("chests_player_transform_yeti_scoreMsg"), this.getEventName(), true);
                    break;
                }
                case FROG: {
                    return;
                }
            }
        }
        if (this.getPlayerData(player).getTransformation() != TransformType.YETI && this.getPlayerData(target.getEventInfo()).getTransformation() != null) {
            switch (this.getPlayerData(target.getEventInfo()).getTransformation()) {
                case BUNNY: {
                    player.sendMessage(LanguageEngine.getMsg("chests_player_transform_bunny_diedMsg", this._bunnyKilledScore));
                    score = this._bunnyKilledScore;
                    break;
                }
                case PIG: {
                    player.sendMessage(LanguageEngine.getMsg("chests_player_transform_pig_diedMsg", this._pigKilledScore));
                    score = this._pigKilledScore;
                    break;
                }
                case YETI: {
                    player.sendMessage(LanguageEngine.getMsg("chests_player_transform_yeti_diedMsg", this._yetiKilledScore));
                    score = this._yetiKilledScore;
                    break;
                }
                case FROG: {
                    player.sendMessage(LanguageEngine.getMsg("chests_player_transform_frog_diedMsg", this._frogKilledScore));
                    score = this._frogKilledScore;
                    return;
                }
            }
        }
        this.getPlayerData(player).raiseScore(score);
        this.getPlayerData(player).raiseKills(score);
        if (player.isTitleUpdated()) {
            player.setTitle(this.getTitle(player), true);
            player.broadcastTitleInfo();
        }
        this.setScoreStats(player, this.getPlayerData(player).getScore());
        this.setKillsStats(player, this.getPlayerData(player).getKills());
        CallbackManager.getInstance().playerScores(this.getEventType(), player, 1);
    }
    
    @Override
    public void onDie(final PlayerEventInfo player, final CharacterData killer) {
        int resDelay = this.getInt("resDelay") * 1000;
        if (this.getPlayerData(player).getTransformation() != null) {
            this.untransformPlayer(player);
            if (this._transformationHalfResTime) {
                resDelay /= 2;
            }
        }
        for (final Map.Entry<Integer, Integer> e : this.getPlayerData(player).getSkills(SkillType.TILL_DIE).entrySet()) {
            final SkillData skill = new SkillData(e.getKey(), e.getValue());
            if (skill.exists()) {
                player.screenMessage(LanguageEngine.getMsg("chests_player_buffRemoved", skill.getName(), skill.getLevel()), this.getEventName(), false);
                this.removeSkill(player, skill, SkillType.TILL_DIE, true);
            }
        }
        this.getPlayerData(player).removeSkills(SkillType.TILL_DIE);
        this.getPlayerData(player).raiseDeaths(1);
        this.setDeathsStats(player, this.getPlayerData(player).getDeaths());
        final int ds = this.getPlayerData(player).getDeathStreak();
        if (ds == 1) {
            player.screenMessage(LanguageEngine.getMsg("chests_player_respawnDelayDecreased", "50%"), this.getEventName(), false);
            resDelay *= (int)0.5f;
        }
        else if (ds == 2) {
            player.screenMessage(LanguageEngine.getMsg("chests_player_respawnDelayDecreased", "75"), this.getEventName(), false);
            resDelay *= (int)0.25f;
        }
        else if (ds == 3) {
            player.screenMessage(LanguageEngine.getMsg("chests_player_respawnDelayDecreased", "75%"), this.getEventName(), false);
            resDelay *= (int)0.25f;
        }
        this.getPlayerData(player).addDeathStreak();
        this.scheduleRevive(player, resDelay);
    }
    
    @Override
    protected void respawnPlayer(final PlayerEventInfo pi, final int instance) {
        super.respawnPlayer(pi, instance);
        switch (this.getPlayerData(pi).getDeathStreak()) {
            case 3: {
                final int id = 35020;
                final SkillData skill = new SkillData(35020, 1);
                if (skill.exists()) {
                    pi.getSkillEffects(skill.getId(), skill.getLevel());
                }
                this.getPlayerData(pi).addSkill(35020, 1, SkillType.TILL_KILL);
                pi.screenMessage(LanguageEngine.getMsg("chests_player_deathStreakWindWalk", 3), this.getEventName(), false);
                break;
            }
            case 4: {
                this.getPlayerData(pi).giveDeathstreakShield();
                this.updateBombShield(pi);
                pi.screenMessage(LanguageEngine.getMsg("chests_player_deathStreakSuperiorShield", 4), this.getEventName(), false);
                break;
            }
        }
    }
    
    @Override
    public boolean canSupport(final PlayerEventInfo player, final CharacterData target) {
        return player.getPlayersId() == target.getObjectId() || (player.hasSummon() && target.isSummon() && player.getSummon() == target.getOwner());
    }
    
    @Override
    public boolean canAttack(final PlayerEventInfo player, final CharacterData target) {
        if (target.getEventInfo() != null) {
            boolean playerTransformed;
            boolean targetTransformed;
            try {
                playerTransformed = (this.getPlayerData(player).getTransformation() != null && this.getPlayerData(player).getTransformation() != TransformType.FROG);
                targetTransformed = (this.getPlayerData(target.getEventInfo()).getTransformation() != null);
            }
            catch (Exception e) {
                playerTransformed = false;
                targetTransformed = false;
            }
            if (playerTransformed && targetTransformed && this.getPlayerData(player).getTransformation() == this.getPlayerData(target.getEventInfo()).getTransformation()) {
                player.sendMessage(LanguageEngine.getMsg("chests_player_sameSpecie"));
                return false;
            }
            if (playerTransformed) {
                return true;
            }
            if (targetTransformed) {
                return true;
            }
            player.sendMessage(LanguageEngine.getMsg("chests_player_cantAttackPlayers"));
            return false;
        }
        else {
            if (this.getPlayerData(player).getTransformation() != null) {
                player.sendMessage(LanguageEngine.getMsg("chests_player_cantAttackChests"));
                return false;
            }
            return true;
        }
    }
    
    @Override
    public boolean onSay(final PlayerEventInfo player, final String text, final int channel) {
        if (text.equals(".scheme")) {
            EventManager.getInstance().getHtmlManager().showSelectSchemeForEventWindow(player, "none", this.getEventType().getAltTitle());
            return false;
        }
        if (player.isGM()) {
            try {
                final LuckyItem it = LuckyItem.valueOf(text.toUpperCase());
                if (it != null) {
                    if (!this.getPlayerData(player).hasWeapon(it)) {
                        this.giveWeapon(player, it, true, true);
                    }
                    else {
                        this.removeWeapon(player, it);
                    }
                    this.updateSkillsForWeapon(player);
                }
            }
            catch (Exception ex) {}
        }
        return true;
    }
    
    @Override
    public boolean canUseItem(final PlayerEventInfo player, final ItemData item) {
        if (item.isWeapon() || item.getBodyPart() == CallBack.getInstance().getValues().SLOT_L_HAND()) {
            if (this.getWeaponType(item.getItemId()) != null && !item.isEquipped()) {
                if (!this.getPlayerData(player).hasWeapon(this.getWeaponType(item.getItemId()))) {
                    player.sendMessage(LanguageEngine.getMsg("chests_player_cantUseWeapon"));
                    return false;
                }
                return true;
            }
            else if (this.getWeaponType(item.getItemId()) != LuckyItem.SHIELD) {
                player.sendMessage(LanguageEngine.getMsg("event_itemNotAllowed"));
                return false;
            }
        }
        return super.canUseItem(player, item);
    }
    
    @Override
    public boolean canDestroyItem(final PlayerEventInfo player, final ItemData item) {
        player.sendMessage(LanguageEngine.getMsg("chests_player_cantDestroyWeapon"));
        return false;
    }
    
    @Override
    public boolean canUseSkill(final PlayerEventInfo player, final SkillData skill) {
        if (this._skillsForAll != null && this._skillsForAll.containsKey(skill.getId())) {
            return true;
        }
        if (skill.getId() == this._rushSkill) {
            return this.getPlayerData(player).hasSkill(this._rushSkill, SkillType.PERMANENT);
        }
        if (skill.getId() == this._aggressionSkillId || skill.getId() == this._whirlwindSkill) {
            if (this.getPlayerData(player).getActiveWeapon() == LuckyItem.PIKE || this.getPlayerData(player).getActiveWeapon() == LuckyItem.LANCE) {
                return true;
            }
            player.sendMessage(LanguageEngine.getMsg("chests_player_skillOnlyWith", "polarm"));
            return false;
        }
        else if (skill.getId() == this._backstabSkill) {
            if (this.getPlayerData(player).getActiveWeapon() == LuckyItem.DAGGER_CRAFTED || this.getPlayerData(player).getActiveWeapon() == LuckyItem.DIRK) {
                return true;
            }
            player.sendMessage(LanguageEngine.getMsg("chests_player_skillOnlyWith", "dagger"));
            return false;
        }
        else {
            if (skill.getId() != this._stunSkill) {
                if (this._skillsForItems != null) {
                    final LuckyItem activeWeapon = this.getPlayerData(player).getActiveWeapon();
                    for (final Map.Entry<LuckyItem, Map<Integer, Integer>> e : this._skillsForItems.entrySet()) {
                        if (e.getValue().containsKey(skill.getId())) {
                            if (activeWeapon == e.getKey()) {
                                return true;
                            }
                            player.sendMessage(LanguageEngine.getMsg("chests_player_skillOnlyWith", e.getKey().toString().toLowerCase()));
                            return false;
                        }
                    }
                }
                player.sendMessage(LanguageEngine.getMsg("event_skillNotAllowed"));
                return false;
            }
            if (this.getPlayerData(player).getActiveWeapon() == LuckyItem.HAMMER) {
                return true;
            }
            player.sendMessage(LanguageEngine.getMsg("chests_player_skillOnlyWith", "hammer"));
            return false;
        }
    }
    
    @Override
    public boolean canBeDisarmed(final PlayerEventInfo player) {
        return false;
    }
    
    @Override
    public boolean canSaveShortcuts(final PlayerEventInfo player) {
        return !this._customShortcuts;
    }
    
    @Override
    public EventPlayerData createPlayerData(final PlayerEventInfo player) {
        final EventPlayerData d = new LuckyChestsPlayerData(player, this);
        return d;
    }
    
    @Override
    public LuckyChestsPlayerData getPlayerData(final PlayerEventInfo player) {
        return (LuckyChestsPlayerData)player.getEventData();
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
                        match.abort();
                        this.unspawnChests(match.getInstance().getId());
                        this.restorePlayers(match.getInstance().getId());
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        for (final PlayerEventInfo player : this.getPlayers(instanceId)) {
            if (!player.isOnline()) {
                continue;
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
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_BIG_HEAD());
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_INVULNERABLE());
            player.stopAbnormalEffect(CallBack.getInstance().getValues().ABNORMAL_S_INVINCIBLE());
            player.setInstanceId(0);
            if (this._removeBuffsOnEnd) {
                player.removeBuffs();
            }
            player.restoreData();
            EventManager.getInstance().removeEventSkills(player);
            player.teleport(player.getOrigLoc(), 0, true, 0);
            player.sendMessage(LanguageEngine.getMsg("event_teleportBack"));
            if (player.getParty() != null) {
                final PartyData party = player.getParty();
                party.removePartyMember(player);
            }
            player.broadcastUserInfo();
        }
        this.clearPlayers(true, instanceId);
    }
    
    @Override
    public int getTeamsCount() {
        return 1;
    }
    
    @Override
    public String getMissingSpawns(final EventMap map) {
        final StringBuilder tb = new StringBuilder();
        if (!map.checkForSpawns(SpawnType.Regular, -1, 1)) {
            tb.append(this.addMissingSpawn(SpawnType.Regular, 0, 1));
        }
        if (!map.checkForSpawns(SpawnType.Chest, -1, 1)) {
            tb.append(this.addMissingSpawn(SpawnType.Chest, 0, 1));
        }
        return tb.toString();
    }
    
    private boolean checkNpcs() {
        NpcTemplateData template = new NpcTemplateData(this._classicChestId);
        if (!template.exists()) {
            SunriseLoader.debug("Lucky chests: missing template for CLASSIC CHEST - ID " + this._classicChestId);
            if (SunriseLoader.detailedDebug) {
                this.print("Lucky Chests: missing template for CLASSIC CHEST - ID " + this._classicChestId);
            }
            return false;
        }
        if (this._shabbyChestEnabled) {
            template = new NpcTemplateData(this._shabbyChestId);
            if (!template.exists()) {
                SunriseLoader.debug("Lucky chests: missing template for SHABBY CHEST - ID " + this._shabbyChestId);
                if (SunriseLoader.detailedDebug) {
                    this.print("Lucky Chests: missing template for SHABBY CHEST - ID " + this._shabbyChestId);
                }
                return false;
            }
        }
        if (this._luxuriousChestEnabled) {
            template = new NpcTemplateData(this._luxuriousChestId);
            if (!template.exists()) {
                SunriseLoader.debug("Lucky chests: missing template for LUXURIOUS CHEST - ID " + this._luxuriousChestId);
                if (SunriseLoader.detailedDebug) {
                    this.print("Lucky Chests: missing template for LUXURIOUS CHEST - ID " + this._luxuriousChestId);
                }
                return false;
            }
        }
        if (this._boxChestEnabled) {
            template = new NpcTemplateData(this._boxChestId);
            if (!template.exists()) {
                SunriseLoader.debug("Lucky chests: missing template for BOX CHEST - ID " + this._boxChestId);
                if (SunriseLoader.detailedDebug) {
                    this.print("Lucky Chests: missing template for BOX CHEST - ID " + this._boxChestId);
                }
                return false;
            }
        }
        if (this._nexusedChestEnabled) {
            template = new NpcTemplateData(this._nexusedChestId);
            if (!template.exists()) {
                SunriseLoader.debug("Lucky chests: missing template for NEXUSED CHEST - ID " + this._nexusedChestId);
                if (SunriseLoader.detailedDebug) {
                    this.print("Lucky Chests: missing template for NEXUSED CHEST - ID " + this._nexusedChestId);
                }
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected String addExtraEventInfoCb(final int instance) {
        int top = 0;
        for (final PlayerEventInfo player : this.getPlayers(instance)) {
            if (this.getPlayerData(player).getScore() > top) {
                top = this.getPlayerData(player).getScore();
            }
        }
        final String status = "<font color=ac9887>Top score: </font><font color=7f7f7f>" + top + "</font>";
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
    protected LuckyChestsData createEventData(final int instance) {
        return new LuckyChestsData(instance);
    }
    
    @Override
    protected LuckyChestsEventInstance createEventInstance(final InstanceData instance) {
        return new LuckyChestsEventInstance(instance);
    }
    
    @Override
    protected LuckyChestsData getEventData(final int instance) {
        try {
            return (LuckyChestsData)this._matches.get(instance)._data;
        }
        catch (Exception e) {
            SunriseLoader.debug("Error on getEventData for instance " + instance);
            e.printStackTrace();
            return null;
        }
    }
    
    protected void heavySwordAttack(final PlayerEventInfo player, final CharacterData target) {
        player.broadcastSkillLaunched(null, player.getTarget(), 315, 1);
    }
    
    public class LuckyChestsPlayerData extends PvPEventPlayerData
    {
        List<LuckyItem> _weapons;
        Map<LuckyItem, ShortCutData> _weaponShortcuts;
        Map<Integer, ShortCutData> _skillShortcuts;
        Map<SkillType, Map<Integer, Integer>> _skills;
        Map<LuckyItem, Map<Integer, Integer>> _skillsForWeapons;
        private LuckyItem _activeWeapon;
        private LuckyItem _lastSkillsOfWeapon;
        private boolean _hasShield;
        private boolean _bigHead;
        private int _deathStreak;
        private int _killStreak;
        private int _bombShield;
        private boolean _deathstreakShield;
        private TransformType _transformed;
        
        public LuckyChestsPlayerData(final PlayerEventInfo owner, final EventGame event) {
            super(owner, event, new GlobalStatsModel(LuckyChests.this.getEventType()));
            this._weapons = new LinkedList<LuckyItem>();
            this._weaponShortcuts = new ConcurrentHashMap<LuckyItem, ShortCutData>();
            this._skillShortcuts = new ConcurrentHashMap<Integer, ShortCutData>();
            this._skills = new ConcurrentHashMap<SkillType, Map<Integer, Integer>>();
            this._skillsForWeapons = new ConcurrentHashMap<LuckyItem, Map<Integer, Integer>>();
            this._activeWeapon = null;
            this._lastSkillsOfWeapon = null;
            this._hasShield = false;
            this._bigHead = false;
            this._deathStreak = 0;
            this._killStreak = 0;
            this._bombShield = 0;
            this._deathstreakShield = false;
            this._transformed = null;
            for (final SkillType type : SkillType.values()) {
                this._skills.put(type, new LinkedHashMap<Integer, Integer>());
            }
        }
        
        protected void setTransformed(final TransformType type) {
            this._transformed = type;
        }
        
        protected TransformType getTransformation() {
            return this._transformed;
        }
        
        protected void addKillStreak(final int i) {
            this._killStreak += i;
        }
        
        protected void decreaseKillStreak(final int i) {
            this._killStreak -= i;
            if (this._killStreak < 0) {
                this._killStreak = 0;
            }
        }
        
        protected void resetKillStreak() {
            this._killStreak = 0;
        }
        
        protected int getKillStreak() {
            return this._killStreak;
        }
        
        protected void addSkill(final int id, final int level, final SkillType type) {
            this._skills.get(type).put(id, level);
        }
        
        protected void addSkillForWeapon(final int id, final int level, final LuckyItem item) {
            if (!this._skillsForWeapons.containsKey(item)) {
                this._skillsForWeapons.put(item, new LinkedHashMap<Integer, Integer>());
            }
            this._skillsForWeapons.get(item).put(id, level);
        }
        
        protected void setActiveWeaponWithSkills(final LuckyItem item) {
            this._lastSkillsOfWeapon = item;
        }
        
        protected LuckyItem getActiveWeaponWithSkills() {
            return this._lastSkillsOfWeapon;
        }
        
        protected Map<Integer, Integer> getSkillsForWeapon(final LuckyItem item) {
            if (!this._skillsForWeapons.containsKey(item)) {
                return new LinkedHashMap<Integer, Integer>();
            }
            return this._skillsForWeapons.get(item);
        }
        
        protected Map<LuckyItem, Map<Integer, Integer>> getAllSkillsForWeapons() {
            return this._skillsForWeapons;
        }
        
        protected void removeBuff(final int id, final SkillType type) {
            this._skills.get(type).remove(id);
        }
        
        protected void removeSkillsForWeapon(final LuckyItem item) {
            if (this._skillsForWeapons.containsKey(item)) {
                this._skillsForWeapons.get(item).clear();
            }
        }
        
        protected boolean hasSkillForWeapon(final int id, final LuckyItem item) {
            return this.getLevelOfSkillForWeapon(id, item) > 0;
        }
        
        protected int getLevelOfSkillForWeapon(final int id, final LuckyItem item) {
            if (!this._skillsForWeapons.containsKey(item)) {
                return 0;
            }
            return this._skillsForWeapons.get(item).containsKey(id) ? this._skillsForWeapons.get(item).get(id) : 0;
        }
        
        protected void removeSkills(final SkillType type) {
            this._skills.get(type).clear();
        }
        
        protected int getLevel(final int id, final SkillType type) {
            return this._skills.get(type).containsKey(id) ? this._skills.get(type).get(id) : 0;
        }
        
        protected Map<Integer, Integer> getSkills(final SkillType type) {
            return this._skills.get(type);
        }
        
        protected Map<SkillType, Map<Integer, Integer>> getAllSkills() {
            return this._skills;
        }
        
        protected boolean hasSkill(final int id, final SkillType type) {
            return this.getLevel(id, type) > 0;
        }
        
        protected void giveDeathstreakShield() {
            this._deathstreakShield = true;
        }
        
        protected boolean hasDeathstreakShield() {
            return this._deathstreakShield;
        }
        
        protected void removeDeathstreakShield() {
            this._deathstreakShield = false;
        }
        
        protected void raiseBombShield(final int i) {
            this._bombShield += i;
        }
        
        protected int hasBombShield() {
            return this._bombShield;
        }
        
        protected void decreaseBombShield(final int i) {
            this._bombShield -= i;
        }
        
        protected boolean hasBigHead() {
            return this._bigHead;
        }
        
        protected void setHasBigHead(final boolean b) {
            this._bigHead = b;
        }
        
        protected void setHasShield(final boolean b) {
            this._hasShield = b;
        }
        
        protected boolean hasShield() {
            return this._hasShield;
        }
        
        protected void addWeapon(final LuckyItem w) {
            this._weapons.add(w);
        }
        
        protected void removeWeapon(final LuckyItem w) {
            this._weapons.remove(w);
        }
        
        protected boolean hasWeapon(final LuckyItem w) {
            return this._weapons.contains(w);
        }
        
        protected LuckyItem getWeaponOfType(final WeaponType type) {
            for (final LuckyItem it : this._weapons) {
                if (it._type == type) {
                    return it;
                }
            }
            return null;
        }
        
        protected void addWeaponShortcut(final LuckyItem type, final ShortCutData sh) {
            this._weaponShortcuts.put(type, sh);
        }
        
        protected void addSkillShortcut(final SkillData skill, final ShortCutData sh) {
            this._skillShortcuts.put(skill.getId(), sh);
        }
        
        protected void removeWeaponShortcut(final LuckyItem type, final ShortCutData sh) {
            this._weaponShortcuts.remove(type);
        }
        
        protected void removeSkillShortcut(final SkillData skill, final ShortCutData sh) {
            this._skillShortcuts.remove(skill.getId());
        }
        
        protected ShortCutData getShortCut(final int slot) {
            for (final ShortCutData sh : this._weaponShortcuts.values()) {
                if (sh.getSlot() == slot) {
                    return sh;
                }
            }
            return null;
        }
        
        protected ShortCutData getWeaponShortCut(final LuckyItem type) {
            for (final Map.Entry<LuckyItem, ShortCutData> sh : this._weaponShortcuts.entrySet()) {
                if (sh.getKey() == type) {
                    return sh.getValue();
                }
            }
            return null;
        }
        
        protected ShortCutData getSkillShortcut(final SkillData skill) {
            for (final Map.Entry<Integer, ShortCutData> sh : this._skillShortcuts.entrySet()) {
                if (sh.getKey() == skill.getId()) {
                    return sh.getValue();
                }
            }
            return null;
        }
        
        protected void addDeathStreak() {
            ++this._deathStreak;
        }
        
        protected void resetDeathStreak() {
            this._deathStreak = 0;
        }
        
        protected int getDeathStreak() {
            return this._deathStreak;
        }
        
        protected LuckyItem getActiveWeapon() {
            return this._activeWeapon;
        }
        
        protected void setActiveWeapon(final LuckyItem w) {
            this._activeWeapon = w;
        }
        
        protected SlotInfo getNextFreeShortcutSlot(final boolean weapon) {
            final int maxPages = 9;
            SlotInfo freeSlot = null;
            for (int page = 0; page < 9 && freeSlot == null; ++page) {
                int slot = weapon ? 0 : 11;
                while (true) {
                    if (weapon) {
                        if (slot >= 11) {
                            break;
                        }
                    }
                    else if (slot <= 0) {
                        break;
                    }
                    boolean existsInSlot = false;
                    for (final Map.Entry<LuckyItem, ShortCutData> sh : this._weaponShortcuts.entrySet()) {
                        if (sh.getValue().getPage() == page && sh.getValue().getSlot() == slot) {
                            existsInSlot = true;
                            break;
                        }
                    }
                    for (final Map.Entry<Integer, ShortCutData> sh2 : this._skillShortcuts.entrySet()) {
                        if (sh2.getValue().getPage() == page && sh2.getValue().getSlot() == slot) {
                            existsInSlot = true;
                            break;
                        }
                    }
                    if (!existsInSlot) {
                        freeSlot = new SlotInfo(slot, page);
                        break;
                    }
                    if (weapon) {
                        ++slot;
                    }
                    else {
                        --slot;
                    }
                }
            }
            return freeSlot;
        }
    }
    
    protected class SlotInfo
    {
        public int slot;
        public int page;
        
        public SlotInfo(final int slot, final int page) {
            this.slot = slot;
            this.page = page;
        }
    }
    
    private enum SkillType
    {
        TILL_DIE, 
        TILL_KILL, 
        KILLSTREAK, 
        PERMANENT, 
        TRANSFORM, 
        WEAPON;
    }
    
    protected class EffectResult
    {
        boolean success;
        boolean resetDeathstreak;
        boolean addKillstreak;
        boolean resetKillstreak;
        
        public EffectResult(final boolean success, final boolean resetDeathstreak, final boolean addKillstreak, final boolean resetKillstreak) {
            this.success = success;
            this.resetDeathstreak = resetDeathstreak;
            this.addKillstreak = addKillstreak;
            this.resetDeathstreak = resetDeathstreak;
        }
        
        public EffectResult(final boolean success) {
            this.success = success;
        }
    }
    
    private class ActionData
    {
        protected final boolean canServerKill;
        protected final boolean resetDeathstreak;
        protected final boolean addKillStreak;
        protected final boolean resetKillStreak;
        
        public ActionData(final boolean canServerKillTheChest, final boolean resetDeathstreak, final boolean addKillStreak, final boolean resetKillStreak) {
            this.canServerKill = canServerKillTheChest;
            this.resetDeathstreak = resetDeathstreak;
            this.addKillStreak = addKillStreak;
            this.resetKillStreak = resetKillStreak;
        }
    }
    
    private enum EffectType
    {
        Score, 
        ScoreFirework, 
        ScoreLargeFirework, 
        WindWalkTillDie, 
        SkillAggression, 
        SkillWhirlwind, 
        SkillRush, 
        BombShieldOneBomb, 
        IncreaseCritRate, 
        SpawnBonusChests, 
        Weapon, 
        Laugh, 
        Explode, 
        BigHead, 
        ParalyzeNoPoint, 
        FearNoPoint, 
        AggressiveBunny, 
        TransformToBunny, 
        TransformToPig, 
        TransformToYeti, 
        TransformToFrog;
    }
    
    private enum LuckyItem
    {
        GLADIUS(WeaponType.SWORD, 1), 
        DIRK(WeaponType.DAGGER, 1), 
        BOW(WeaponType.BOW, 1), 
        LANCE(WeaponType.POLEARM, 1), 
        HAMMER(WeaponType.HAMMER, 1), 
        ZWEIHANDER(WeaponType.BIGSWORD, 1), 
        SHIELD((WeaponType)null, 1), 
        KNIGHTSWORD(WeaponType.SWORD, 2), 
        DAGGER_CRAFTED(WeaponType.DAGGER, 2), 
        LONGBOW(WeaponType.BOW, 2), 
        PIKE(WeaponType.POLEARM, 2), 
        HEAVYSWORD(WeaponType.BIGSWORD, 2), 
        REINFORCED_BOW(WeaponType.SUPERBOW, 1), 
        HEAVYHAMMER(WeaponType.BIGHAMMER, 1), 
        SABER(WeaponType.FASTSWORD, 1);
        
        public WeaponType _type;
        public int _grade;
        
        private LuckyItem(final WeaponType type, final int grade) {
            this._type = type;
            this._grade = grade;
        }
        
        protected boolean isWeapon() {
            return this != LuckyItem.SHIELD;
        }
    }
    
    private enum WeaponType
    {
        SWORD, 
        FASTSWORD, 
        BOW, 
        SUPERBOW, 
        POLEARM, 
        DAGGER, 
        HAMMER, 
        BIGHAMMER, 
        BIGSWORD;
    }
    
    private enum TransformType
    {
        BUNNY, 
        FROG, 
        PIG, 
        YETI;
    }
    
    private enum ChestType
    {
        CLASSIC, 
        SHABBY, 
        LUXURIOUS, 
        BOX, 
        NEXUSED;
    }
    
    protected class LuckyChestsEventInstance extends DMEventInstance
    {
        public LuckyChestsEventInstance(final InstanceData instance) {
            super(instance);
        }
        
        @Override
        public void run() {
            try {
                if (SunriseLoader.detailedDebug) {
                    LuckyChests.this.print("Event: running task of state " + this._nextState.toString() + "...");
                }
                switch (this._nextState) {
                    case START: {
                        if (LuckyChests.this.checkPlayers(this._instance.getId())) {
                            LuckyChests.this.teleportPlayers(this._instance.getId(), SpawnType.Regular, true);
                            LuckyChests.this.setupTitles(this._instance.getId());
                            LuckyChests.this.enableMarkers(this._instance.getId(), true);
                            LuckyChests.this.clearShortcuts(this._instance.getId());
                            LuckyChests.this.preparePlayers(this._instance.getId());
                            LuckyChests.this.spawnChests(this._instance.getId());
                            LuckyChests.this.forceSitAll(this._instance.getId());
                            this.setNextState(EventState.FIGHT);
                            this.scheduleNextTask(10000);
                            break;
                        }
                        break;
                    }
                    case FIGHT: {
                        LuckyChests.this.forceStandAll(this._instance.getId());
                        this.setNextState(EventState.END);
                        this._clock.startClock(LuckyChests.this._manager.getRunTime());
                        break;
                    }
                    case END: {
                        this._clock.setTime(0, true);
                        LuckyChests.this.unspawnChests(this._instance.getId());
                        this.setNextState(EventState.INACTIVE);
                        if (!LuckyChests.this.instanceEnded() && this._canBeAborted) {
                            if (this._canRewardIfAborted) {
                                LuckyChests.this.rewardAllPlayers(this._instance.getId(), LuckyChests.this.getInt("scoreForReward"), 0);
                            }
                            LuckyChests.this.clearEvent(this._instance.getId());
                            break;
                        }
                        break;
                    }
                }
                if (SunriseLoader.detailedDebug) {
                    LuckyChests.this.print("Event: ... finished running task. next state " + this._nextState.toString());
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
                LuckyChests.this._manager.endDueToError(LanguageEngine.getMsg("event_error"));
            }
        }
    }
    
    protected class LuckyChestsData extends DMData
    {
        protected Map<ChestType, List<NpcData>> _chests;
        
        protected LuckyChestsData(final int instance) {
            super(instance);
            (this._chests = new ConcurrentHashMap<ChestType, List<NpcData>>()).clear();
            for (final ChestType ch : ChestType.values()) {
                this._chests.put(ch, new LinkedList<NpcData>());
            }
        }
    }
}
