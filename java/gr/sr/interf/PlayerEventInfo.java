package gr.sr.interf;


import gr.sr.events.EventGame;
import gr.sr.events.engine.EventConfig;
import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.EventPlayerData;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.Loc;
import gr.sr.events.engine.base.PvPEventPlayerData;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.mini.MiniEventGame;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.stats.EventStatsManager;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import gr.sr.l2j.CallBack;
import gr.sr.l2j.IPlayerEventInfo;
import gr.sr.l2j.IValues;
import l2r.Config;
import l2r.gameserver.GameTimeController;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.ClassListData;
import l2r.gameserver.data.xml.impl.TransformData;
import l2r.gameserver.enums.CtrlIntention;
import l2r.gameserver.enums.DuelState;
import l2r.gameserver.enums.audio.IAudio;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.Shortcut;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.L2Summon;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.base.ClassType;
import l2r.gameserver.model.base.PlayerClass;
import l2r.gameserver.model.cubic.CubicInstance;
import l2r.gameserver.model.effects.AbnormalEffect;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.entity.olympiad.OlympiadManager;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.serverpackets.*;
import l2r.gameserver.util.Broadcast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


public class PlayerEventInfo implements IPlayerEventInfo {
    public static final boolean AFK_CHECK_ENABLED = EventConfig.getInstance().getGlobalConfigBoolean("afkChecksEnabled");
    public static final int AFK_WARNING_DELAY = EventConfig.getInstance().getGlobalConfigInt("afkWarningDelay");
    public static final int AFK_KICK_DELAY = EventConfig.getInstance().getGlobalConfigInt("afkKickDelay");
    protected final L2PcInstance _owner;
    private final int _playersId;
    private boolean _isInEvent;
    private boolean _isRegistered;
    private boolean _isInFFAEvent;
    private boolean _isSpectator;
    private boolean _canBuff;
    private boolean _canParty = true;
    private boolean _isSitForced;
    private boolean _antifeedProtection;
    private boolean _titleUpdate;
    protected boolean _disableAfkCheck;
    private int _origNameColor;
    private Location _origLoc;
    private String _origTitle;
    private EventPlayerData _eventData;
    private int _status;
    protected EventGame _activeEvent;
    private EventTeam _eventTeam;
    private MiniEventManager _registeredMiniEvent;
    private EventType _registeredMainEvent;
    private PlayerEventInfo.AfkChecker _afkChecker;
    private PlayerEventInfo.Radar _radar;
    private boolean _hasMarkers;
    private final Set<ShortCutData> _customShortcuts = ConcurrentHashMap.newKeySet();

    public PlayerEventInfo(L2PcInstance owner) {
        this._owner = owner;
        this._playersId = owner == null ? -1 : owner.getObjectId();
        this._isRegistered = false;
        this._isInEvent = false;
        this._isInFFAEvent = false;
        this._status = 0;
        this._disableAfkCheck = false;
        this._titleUpdate = true;
        this._hasMarkers = false;
    }

    public void initOrigInfo() {
        this._origNameColor = this._owner.getAppearance().getNameColor();
        this._origTitle = this._owner.getTitle();
        this._origLoc = new Location(this._owner.getX(), this._owner.getY(), this._owner.getZ(), this._owner.getHeading());
    }

    public void restoreData() {
        this._owner.getAppearance().setNameColor(this._origNameColor);
        this._owner.setTitle(this._origTitle);
        this._owner.getAppearance().setVisibleTitle(this._origTitle);
        this._owner.broadcastUserInfo();
        this.clean();
    }

    public void onEventStart(EventGame event) {
        this.initOrigInfo();
        this._isInEvent = true;
        this._activeEvent = event;
        this._eventData = event.createPlayerData(this);
        if (AFK_CHECK_ENABLED) {
            this._afkChecker = new PlayerEventInfo.AfkChecker(this);
        }

    }

    public void clean() {
        if (this._afkChecker != null) {
            this._afkChecker.stop();
        }

        if (this._radar != null) {
            this._radar.disable();
        }

        this._isRegistered = false;
        this._isInEvent = false;
        this._isInFFAEvent = false;
        this._registeredMiniEvent = null;
        this._registeredMainEvent = null;
        this._hasMarkers = false;
        this._activeEvent = null;
        this._eventTeam = null;
        this._canParty = true;
        this._eventData = null;
        this._status = 0;
    }

    public void teleport(Loc loc, int delay, boolean randomOffset, int instanceId) {
        L2PcInstance player = this._owner;
        if (player != null) {
            L2Summon summon = player.getSummon();
            player.abortCast();
            if (summon != null) {
                summon.unSummon(player);
            }

            if (player.isInDuel()) {
                player.setDuelState(DuelState.INTERRUPTED);
            }

            player.doRevive();
            L2Effect[] var7 = player.getAllEffects();
            int var8 = var7.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                L2Effect e = var7[var9];
                if (e != null && e.getSkill() != null && e.getSkill().isDebuff()) {
                    e.exit();
                }
            }

            if (player.isSitting()) {
                player.standUp();
            }

            player.setTarget((L2Object) null);
            player.setInstanceId(0);
            player.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), randomOffset);
            if (instanceId != -1) {
                player.setInstanceId(instanceId);
            }

            player.setCurrentCp((double) player.getMaxCp());
            player.setCurrentHp((double) player.getMaxHp());
            player.setCurrentMp((double) player.getMaxMp());
            player.broadcastStatusUpdate();
            player.broadcastUserInfo();
        }
    }

    public void teleToLocation(Loc loc, boolean randomOffset) {
        this._owner.teleToLocation(loc.getX(), loc.getY(), loc.getZ(), randomOffset);
    }

    public void teleToLocation(int x, int y, int z, boolean randomOffset) {
        this._owner.teleToLocation(x, y, z, randomOffset);
    }

    public void teleToLocation(int x, int y, int z, int heading, boolean randomOffset) {
        this._owner.teleToLocation(x, y, z, heading, randomOffset);
    }

    public void setXYZInvisible(int x, int y, int z) {
        this._owner.setXYZInvisible(x, y, z);
    }

    public void setFame(int count) {
        this._owner.setFame(count);
    }

    public int getFame() {
        return this._owner.getFame();
    }

    protected void notifyKill(L2Character target) {
        if (this._activeEvent != null && !this._isSpectator) {
            this._activeEvent.onKill(this, new CharacterData(target));
        }

    }

    protected void notifyDie(L2Character killer) {
        if (this._activeEvent != null && !this._isSpectator) {
            this._activeEvent.onDie(this, new CharacterData(killer));
        }

    }

    protected void notifyDisconnect() {
        if (this._activeEvent != null && !this._isSpectator) {
            this._activeEvent.onDisconnect(this);
        }

        if (this._registeredMainEvent != null) {
            EventManager.getInstance().getMainEventManager().unregisterPlayer(this, true);
        } else if (this._registeredMiniEvent != null) {
        }

        EventStatsManager.getInstance().onDisconnect(this);
        PlayerBase.getInstance().eventEnd(this);
    }

    protected boolean canAttack(L2Character target) {
        return this._activeEvent != null && !this._isSpectator ? this._activeEvent.canAttack(this, new CharacterData(target)) : true;
    }

    protected boolean canSupport(L2Character target) {
        return this._activeEvent != null && !this._isSpectator ? this._activeEvent.canSupport(this, new CharacterData(target)) : true;
    }


    public void onAction() {
        if (this._afkChecker != null) {
            this._afkChecker.onAction();
        }

    }

    protected void onDamageGive(L2Character target, int ammount, boolean isDOT) {
        if (this._activeEvent != null && !this._isSpectator) {
            this._activeEvent.onDamageGive(this.getCharacterData(), new CharacterData(target), ammount, isDOT);
        }

    }

    protected boolean notifySay(String text, int channel) {
        return this._activeEvent != null ? this._activeEvent.onSay(this, text, channel) : true;
    }

    protected boolean notifyNpcAction(L2Npc npc) {
        if (this._isSpectator) {
            return true;
        } else if (EventManager.getInstance().showNpcHtml(this, new NpcData(npc))) {
            return true;
        } else {
            return this._activeEvent != null ? this._activeEvent.onNpcAction(this, new NpcData(npc)) : false;
        }
    }

    protected boolean canUseItem(L2ItemInstance item) {
        if (this._isSpectator) {
            return false;
        } else {
            return this._activeEvent != null ? this._activeEvent.canUseItem(this, new ItemData(item)) : true;
        }
    }

    protected void notifyItemUse(L2ItemInstance item) {
        if (this._activeEvent != null) {
            this._activeEvent.onItemUse(this, new ItemData(item));
        }

    }

    protected boolean canUseSkill(L2Skill skill) {
        if (this._isSpectator) {
            return false;
        } else {
            return this._activeEvent != null ? this._activeEvent.canUseSkill(this, new SkillData(skill)) : true;
        }
    }

    protected void onUseSkill(L2Skill skill) {
        if (this._activeEvent != null) {
            this._activeEvent.onSkillUse(this, new SkillData(skill));
        }

    }

    protected boolean canShowToVillageWindow() {
        return false;
    }

    protected boolean canDestroyItem(L2ItemInstance item) {
        return this._activeEvent != null ? this._activeEvent.canDestroyItem(this, new ItemData(item)) : true;
    }

    protected boolean canInviteToParty(PlayerEventInfo player, PlayerEventInfo target) {
        return this._activeEvent != null ? this._activeEvent.canInviteToParty(player, target) : true;
    }

    protected boolean canTransform(PlayerEventInfo player) {
        return this._activeEvent != null ? this._activeEvent.canTransform(player) : true;
    }

    protected boolean canBeDisarmed(PlayerEventInfo player) {
        return this._activeEvent != null ? this._activeEvent.canBeDisarmed(player) : true;
    }

    protected int allowTransformationSkill(L2Skill s) {
        return this._activeEvent != null ? this._activeEvent.allowTransformationSkill(this, new SkillData(s)) : 0;
    }

    protected boolean canSaveShortcuts() {
        return this._activeEvent != null ? this._activeEvent.canSaveShortcuts(this) : true;
    }

    public void setInstanceId(int id) {
        this._owner.setInstanceId(id);
    }

    public void sendPacket(String html) {
        this.sendHtmlText(html);
    }

    public void screenMessage(String message, String name, boolean special) {
        if (this._owner != null) {
            this._owner.sendPacket((L2GameServerPacket) (special ? new ExShowScreenMessage(message, 5000) : new CreatureSay(0, 15, name, message)));
        }

    }

    public void creatureSay(String message, String announcer, int channel) {
        if (this._owner != null) {
            this._owner.sendPacket(new CreatureSay(0, channel, announcer, message));
        }

    }

    public void sendMessage(String message) {
        if (this._owner != null) {
            this._owner.sendMessage(message);
        }

    }

    public void sendEventScoreBar(String text) {
        if (this._owner != null) {
            this._owner.sendPacket(new ExShowScreenMessage(1, -1, 3, 0, 1, 0, 0, true, 2000, false, text));
        }
    }

    public void broadcastUserInfo() {
        if (this._owner != null) {
            this._owner.broadcastUserInfo();
        }

    }

    public void broadcastTitleInfo() {
        if (this._owner != null) {
            this._owner.broadcastTitleInfo();
        }

    }

    public void sendSkillList() {
        this._owner.sendSkillList();
    }

    public void transform(int transformId) {
        if (this._owner != null) {
            TransformData.getInstance().transformPlayer(transformId, this._owner);
        }

    }

    public boolean isTransformed() {
        return this._owner != null && this._owner.isTransformed();
    }

    public void untransform(boolean removeEffects) {
        if (this._owner != null && this._owner.isTransformed()) {
            this._owner.stopTransformation(removeEffects);
        }

    }

    public ItemData addItem(int id, int ammount, boolean msg) {
        return new ItemData(this._owner.addItem("Event Reward", id, (long) ammount, (L2Object) null, msg));
    }

    public void addExpAndSp(long exp, int sp) {
        this._owner.addExpAndSp(exp, sp);
    }

    public void doDie() {
        this._owner.doDie(this._owner);
    }

    public void doDie(CharacterData killer) {
        this._owner.doDie(killer.getOwner());
    }

    public ItemData[] getItems() {
        List<ItemData> items = new LinkedList();
        L2ItemInstance[] var2 = this._owner.getInventory().getItems();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            L2ItemInstance item = var2[var4];
            items.add(new ItemData(item));
        }

        return (ItemData[]) items.toArray(new ItemData[items.size()]);
    }

    public void getPetSkillEffects(int skillId, int level) {
        if (this._owner.getSummon() != null) {
            L2Skill skill = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skillId, level);
            if (skill != null) {
                skill.getEffects(this._owner.getSummon(), this._owner.getSummon());
            }
        }

    }

    public void getSkillEffects(int skillId, int level) {
        L2Skill skill = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skillId, level);
        if (skill != null) {
            skill.getEffects(this._owner, this._owner);
        }

    }

    public void addSkill(SkillData skill, boolean store) {
        this.getOwner().addSkill(l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skill.getId(), skill.getLevel()), store);
    }

    public void removeSkill(int id) {
        this.getOwner().removeSkill(id);
    }

    public void removeCubics() {
        if (!this._owner.getCubics().isEmpty()) {

            for (CubicInstance cubic : this._owner.getCubics().values()) {
                cubic.deactivate();
            }

            this._owner.getCubics().clear();
        }

    }

    public void removeSummon() {
        if (this._owner.getSummon() != null) {
            this._owner.getSummon().unSummon(this._owner);
        }

    }

    public boolean hasPet() {
        return this._owner.getSummon() != null;
    }

    public void removeBuffsFromPet() {
        if (this._owner != null && this._owner.getSummon() != null) {
            this._owner.getSummon().stopAllEffects();
        }

    }

    public void removeBuffs() {
        if (this._owner != null) {
            this._owner.stopAllEffects();
        }

    }

    public int getBuffsCount() {
        return this._owner.getBuffCount();
    }

    public int getDancesCount() {
        return this._owner.getDanceCount();
    }

    public int getPetBuffCount() {
        return this._owner.getSummon() != null ? this._owner.getSummon().getBuffCount() : 0;
    }

    public int getPetDanceCount() {
        return this._owner.getSummon() != null ? this._owner.getSummon().getDanceCount() : 0;
    }

    public int getMaxBuffCount() {
        return this._owner.getStat().getMaxBuffCount();
    }

    public int getMaxDanceCount() {
        return Config.DANCES_MAX_AMOUNT;
    }

    public void removeBuff(int id) {
        if (this._owner != null) {
            this._owner.stopSkillEffects(id);
        }

    }

    public void abortCasting() {
        if (this._owner.isCastingNow()) {
            this._owner.abortCast();
        }

        if (this._owner.isAttackingNow()) {
            this._owner.abortAttack();
        }

    }

    public void playSound(IAudio file) {
        this._owner.sendPacket(file.getPacket());
    }

    public void setVisible() {
        this._owner.setInvisible(false);
    }

    public void rebuffPlayer() {
    }

    public void enableAllSkills() {
        Iterator var1 = this._owner.getAllSkills().iterator();

        while (var1.hasNext()) {
            L2Skill skill = (L2Skill) var1.next();
            if (skill.getReuseDelay() <= 900000) {
                this._owner.enableSkill(skill);
            }
        }

        this._owner.sendPacket(new SkillCoolTime(this._owner));
    }

    public void sendSetupGauge(int time) {
        SetupGauge sg = new SetupGauge(0, time);
        this._owner.sendPacket(sg);
    }

    public void root() {
        this._owner.setIsImmobilized(true);
        this._owner.startAbnormalEffect(AbnormalEffect.STEALTH);
    }

    public void unroot() {
        if (this._owner.isImmobilized()) {
            this._owner.setIsImmobilized(false);
        }

        this._owner.stopAbnormalEffect(AbnormalEffect.STEALTH);
    }

    public void paralizeEffect(boolean b) {
        if (b) {
            this.getOwner().startAbnormalEffect(AbnormalEffect.HOLD_1);
        } else {
            this.getOwner().stopAbnormalEffect(AbnormalEffect.HOLD_1);
        }

    }

    public void setIsParalyzed(boolean b) {
        this._owner.setIsParalyzed(b);
    }

    public void setIsInvul(boolean b) {
        this._owner.setIsInvul(b);
    }

    public void setCanInviteToParty(boolean b) {
        this._canParty = b;
    }

    public boolean canInviteToParty() {
        return this._canParty;
    }

    public void setIsSitForced(boolean b) {
        this._isSitForced = b;
    }


    public boolean isSitForced() {
        return this._isSitForced;
    }

    public boolean hasSummon() {
        return this._owner.hasSummon();
    }

    public L2Summon getSummon() {
        return this._owner.getSummon();
    }

    public void showEventEscapeEffect() {
        this._owner.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this._owner.setTarget(this._owner);
        this._owner.disableAllSkills();
        MagicSkillUse msk = new MagicSkillUse(this._owner, 1050, 1, 10000, 0);
        Broadcast.toSelfAndKnownPlayersInRadius(this._owner, msk, 810000);
        SetupGauge sg = new SetupGauge(0, 10000);
        this._owner.sendPacket(sg);
        this._owner.forceIsCasting(GameTimeController.getInstance().getGameTicks() + 100);
    }

    public void startAntifeedProtection(boolean broadcast) {
        this._owner.startAntifeedProtection(true, broadcast);
        this._antifeedProtection = true;
        if (broadcast) {
            this.broadcastUserInfo();
        }

    }

    public void stopAntifeedProtection(boolean broadcast) {
        this._owner.startAntifeedProtection(false, broadcast);
        this._antifeedProtection = false;
        if (broadcast) {
            this.broadcastUserInfo();
        }

    }


    public boolean hasAntifeedProtection() {
        return this._antifeedProtection;
    }

    public void broadcastSkillUse(CharacterData owner, CharacterData target, int skillId, int level) {
        L2Skill skill = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skillId, level);
        if (skill != null) {
            this.getOwner().broadcastPacket(new MagicSkillUse((L2Character) (owner == null ? this.getOwner() : owner.getOwner()), (L2Character) (target == null ? this.getOwner() : target.getOwner()), skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
        }

    }

    public void broadcastSkillLaunched(CharacterData owner, CharacterData target, int skillId, int level) {
        L2Skill skill = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skillId, level);
        if (skill != null) {
            this.getOwner().broadcastPacket(new MagicSkillLaunched((L2Character) (owner == null ? this.getOwner() : owner.getOwner()), skill.getId(), skill.getLevel(), new L2Object[]{target.getOwner()}));
        }

    }

    public void enterObserverMode(int x, int y, int z) {
        this._owner.enterOlympiadObserverMode(new Location(x, y, z), 0);
    }

    public void removeObserveMode() {
        this.setIsSpectator(false);
        this.setActiveGame((MiniEventGame) null);
        this._owner.leaveOlympiadObserverMode();
        this._owner.setInstanceId(0);
        this._owner.teleToLocation(this.getOrigLoc().getX(), this.getOrigLoc().getY(), this.getOrigLoc().getZ(), true);
    }

    public void sendStaticPacket() {
        this._owner.sendPacket(ActionFailed.STATIC_PACKET);
    }

    public void sendHtmlText(String text) {
        NpcHtmlMessage msg = new NpcHtmlMessage();
        msg.setHtml(text);
        this._owner.sendPacket(msg);
    }

    public void sendHtmlPage(String path) {
        NpcHtmlMessage html = new NpcHtmlMessage();
        html.setFile((L2PcInstance) null, (String) null, path);
        this._owner.sendPacket(html);
        this.sendStaticPacket();
    }

    public void startAbnormalEffect(int mask) {
        IValues val = CallBack.getInstance().getValues();
        if (mask == val.ABNORMAL_S_INVINCIBLE()) {
            this._owner.startSpecialEffect(mask);
        } else {
            this._owner.startAbnormalEffect(mask);
        }

    }

    public void stopAbnormalEffect(int mask) {
        IValues val = CallBack.getInstance().getValues();
        if (mask == val.ABNORMAL_S_INVINCIBLE()) {
            this._owner.stopSpecialEffect(mask);
        } else {
            this._owner.stopAbnormalEffect(mask);
        }

    }

    public void removeOriginalShortcuts() {
        if (this._owner != null) {
            this._owner.removeAllShortcuts();
            this._owner.sendPacket(new ShortCutInit(this._owner));
        }
    }

    public void restoreOriginalShortcuts() {
        if (this._owner != null) {
            this._owner.restoreShortCuts();
            this._owner.sendPacket(new ShortCutInit(this._owner));
        }
    }

    public void removeCustomShortcuts() {
        if (this._owner != null) {
            Iterator var1 = this._customShortcuts.iterator();

            while (var1.hasNext()) {
                ShortCutData sh = (ShortCutData) var1.next();
                this._owner.deleteShortCut(sh.getSlot(), sh.getPage(), false);
            }

            this._customShortcuts.clear();
        }
    }

    public void registerShortcut(ShortCutData shortcut, boolean eventShortcut) {
        if (eventShortcut) {
            this._customShortcuts.add(shortcut);
        }

        if (this._owner != null) {
            Shortcut sh = new Shortcut(shortcut.getSlot(), shortcut.getPage(), shortcut.getType(), shortcut.getId(), shortcut.getLevel(), shortcut.getCharacterType());
            this._owner.sendPacket(new ShortCutRegister(sh));
            this._owner.registerShortCut(sh, !eventShortcut);
        }

    }

    public void removeShortCut(ShortCutData shortcut, boolean eventShortcut) {
        if (eventShortcut && this._customShortcuts.contains(shortcut)) {
            this._customShortcuts.remove(shortcut);
        }

        if (this._owner != null) {
            this._owner.deleteShortCut(shortcut.getSlot(), shortcut.getPage(), !eventShortcut);
        }

    }

    public ShortCutData createItemShortcut(int slotId, int pageId, ItemData item) {
        ShortCutData shortcut = new ShortCutData(slotId, pageId, Values.getInstance().TYPE_ITEM(), item.getObjectId(), 0, 1);
        return shortcut;
    }

    public ShortCutData createSkillShortcut(int slotId, int pageId, SkillData skill) {
        ShortCutData shortcut = new ShortCutData(slotId, pageId, Values.getInstance().TYPE_SKILL(), skill.getId(), skill.getLevel(), 1);
        return shortcut;
    }

    public ShortCutData createActionShortcut(int slotId, int pageId, int actionId) {
        ShortCutData shortcut = new ShortCutData(slotId, pageId, Values.getInstance().TYPE_ACTION(), actionId, 0, 1);
        return shortcut;
    }

    public L2PcInstance getOwner() {
        return this._owner;
    }

    public boolean isOnline() {
        return this.isOnline(false);
    }

    public boolean isOnline(boolean strict) {
        if (!strict) {
            return this._owner != null;
        } else {
            return this._owner != null && this._owner.isOnline();
        }
    }

    public boolean isDead() {
        return this._owner.isDead();
    }

    public boolean isVisible() {
        return this._owner.isVisible();
    }

    public boolean isHero() {
        return this._owner.isHero();
    }

    public void doRevive() {
        this._owner.doRevive();
    }

    public CharacterData getTarget() {
        return this._owner.getTarget() != null && this._owner.getTarget() instanceof L2Character ? new CharacterData((L2Character) this._owner.getTarget()) : null;
    }

    public String getPlayersName() {
        return this._owner != null ? this._owner.getName() : "";
    }

    public int getLevel() {
        return this._owner != null ? this._owner.getLevel() : 0;
    }

    public int getPvpKills() {
        return this._owner.getPvpKills();
    }

    public int getPkKills() {
        return this._owner.getPkKills();
    }

    public int getMaxHp() {
        return this._owner.getMaxHp();
    }

    public int getMaxCp() {
        return this._owner.getMaxCp();
    }

    public int getMaxMp() {
        return this._owner.getMaxMp();
    }

    public void setCurrentHp(int hp) {
        this._owner.setCurrentHp((double) hp);
    }

    public void setCurrentCp(int cp) {
        this._owner.setCurrentCp((double) cp);
    }

    public void setCurrentMp(int mp) {
        this._owner.setCurrentMp((double) mp);
    }

    public double getCurrentHp() {
        return this._owner.getCurrentHp();
    }

    public double getCurrentCp() {
        return this._owner.getCurrentCp();
    }

    public double getCurrentMp() {
        return this._owner.getCurrentMp();
    }

    public void healPet() {
        if (this._owner != null && this._owner.getSummon() != null) {
            this._owner.getSummon().setCurrentHp((double) this._owner.getSummon().getMaxHp());
            this._owner.getSummon().setCurrentMp((double) this._owner.getSummon().getMaxMp());
        }

    }

    public void setTitle(String title, boolean updateVisible) {
        this._owner.setTitle(title);
        if (updateVisible) {
            this._owner.getAppearance().setVisibleTitle(this._owner.getTitle());
        }

    }

    public boolean isMageClass() {
        return this._owner.isMageClass();
    }

    public int getClassIndex() {
        return this._owner != null ? this._owner.getClassIndex() : 0;
    }

    public int getActiveClass() {
        return this._owner != null ? this._owner.getActiveClass() : 0;
    }

    public String getClassName() {
        return ClassListData.getInstance().getClass(this._owner.getClassId()).getClassName();
    }

    public PartyData getParty() {
        return this._owner.getParty() == null ? null : new PartyData(this._owner.getParty());
    }

    public boolean isFighter() {
        return PlayerClass.values()[this._owner.getActiveClass()].isOfType(ClassType.Fighter) && !this.isTank();
    }

    public boolean isPriest() {
        return PlayerClass.values()[this._owner.getActiveClass()].isOfType(ClassType.Priest);
    }

    public boolean isMystic() {
        return PlayerClass.values()[this._owner.getActiveClass()].isOfType(ClassType.Mystic) && !this.isDominator();
    }

    public boolean isDominator() {
        return PlayerClass.values()[this._owner.getActiveClass()].isOfType(ClassType.Mystic) && this._owner.getActiveClass() == 115;
    }

    public boolean isTank() {
        return PlayerClass.values()[this._owner.getActiveClass()].isOfType(ClassType.Fighter) && (this._owner.getActiveClass() == 90 || this._owner.getActiveClass() == 91 || this._owner.getActiveClass() == 99 || this._owner.getActiveClass() == 106 || this._owner.getActiveClass() == 100 || this._owner.getActiveClass() == 107);
    }

    public gr.sr.l2j.ClassType getClassType() {
        if (this.isFighter()) {
            return gr.sr.l2j.ClassType.Fighter;
        } else if (this.isTank()) {
            return gr.sr.l2j.ClassType.Tank;
        } else if (this.isMystic()) {
            return gr.sr.l2j.ClassType.Mystic;
        } else if (this.isPriest()) {
            return gr.sr.l2j.ClassType.Priest;
        } else {
            return this.isDominator() ? gr.sr.l2j.ClassType.Dominator : gr.sr.l2j.ClassType.Fighter;
        }
    }

    public int getX() {
        return this._owner.getX();
    }

    public int getY() {
        return this._owner.getY();
    }

    public int getZ() {
        return this._owner.getZ();
    }

    public int getHeading() {
        return this._owner.getHeading();
    }

    public int getInstanceId() {
        return this._owner.getInstanceId();
    }

    public int getClanId() {
        return this._owner.getClanId();
    }

    public boolean isGM() {
        return this._owner.isGM();
    }

    public String getIp() {
        return this._owner.getClient().getConnection().getInetAddress().getHostAddress();
    }

    public boolean isInJail() {
        return this._owner.isJailed();
    }

    public boolean isInSiege() {
        return this._owner.isInSiege();
    }

    public boolean isInDuel() {
        return this._owner.isInDuel();
    }

    public boolean isInOlympiad() {
        return this._owner.isInOlympiad();
    }

    public boolean isInOlympiadMode() {
        return this._owner.isInOlympiadMode();
    }

    public int getKarma() {
        return this._owner.getKarma();
    }

    public boolean isCursedWeaponEquipped() {
        return this._owner.isCursedWeaponEquipped();
    }

    public boolean isImmobilized() {
        return this._owner.isImmobilized();
    }

    public boolean isParalyzed() {
        return this._owner.isParalyzed();
    }

    public boolean isAfraid() {
        return this._owner.isAfraid();
    }

    public boolean isOlympiadRegistered() {
        return OlympiadManager.getInstance().isRegistered(this._owner);
    }

    public boolean isInStoreMode() {
        return this._owner.isInStoreMode();
    }

    public void sitDown() {
        if (this._owner != null) {
            this.setIsSitForced(true);
            this._owner.sitDown();
        }
    }

    public void standUp() {
        if (this._owner != null) {
            this.setIsSitForced(false);
            this._owner.standUp();
        }
    }

    public List<SkillData> getSkills() {
        List<SkillData> list = new LinkedList();
        Iterator var2 = this.getOwner().getAllSkills().iterator();

        while (var2.hasNext()) {
            L2Skill skill = (L2Skill) var2.next();
            list.add(new SkillData(skill));
        }

        return list;
    }

    public List<Integer> getSkillIds() {
        List<Integer> list = new LinkedList();
        Iterator var2 = this.getOwner().getAllSkills().iterator();

        while (var2.hasNext()) {
            L2Skill skill = (L2Skill) var2.next();
            list.add(skill.getId());
        }

        return list;
    }

    public double getPlanDistanceSq(int targetX, int targetY) {
        return this._owner.getPlanDistanceSq(targetX, targetY);
    }

    public double getDistanceSq(int targetX, int targetY, int targetZ) {
        return this._owner.getDistanceSq(targetX, targetY, targetZ);
    }

    public boolean isRegistered() {
        return this._isRegistered;
    }


    public boolean isInEvent() {
        return this._isInEvent;
    }

    public EventPlayerData getEventData() {
        return this._eventData;
    }

    public void setNameColor(int color) {
        this._owner.getAppearance().setNameColor(color);
        this._owner.broadcastUserInfo();
    }

    public void setCanBuff(boolean canBuff) {
        this._canBuff = canBuff;
    }

    public boolean canBuff() {
        return this._canBuff;
    }

    public int getPlayersId() {
        return this._playersId;
    }

    public int getKills() {
        return this._eventData instanceof PvPEventPlayerData ? ((PvPEventPlayerData) this._eventData).getKills() : 0;
    }

    public int getDeaths() {
        return this._eventData instanceof PvPEventPlayerData ? ((PvPEventPlayerData) this._eventData).getDeaths() : 0;
    }

    public int getScore() {
        return this._eventData.getScore();
    }

    public int getStatus() {
        return this._status;
    }

    public void raiseKills(int count) {
        if (this._eventData instanceof PvPEventPlayerData) {
            ((PvPEventPlayerData) this._eventData).raiseKills(count);
        }

    }

    public void raiseDeaths(int count) {
        if (this._eventData instanceof PvPEventPlayerData) {
            ((PvPEventPlayerData) this._eventData).raiseDeaths(count);
        }

    }

    public void raiseScore(int count) {
        this._eventData.raiseScore(count);
    }

    public void setScore(int count) {
        this._eventData.setScore(count);
    }

    public void setStatus(int count) {
        this._status = count;
    }

    public void setKills(int count) {
        if (this._eventData instanceof PvPEventPlayerData) {
            ((PvPEventPlayerData) this._eventData).setKills(count);
        }

    }

    public void setDeaths(int count) {
        if (this._eventData instanceof PvPEventPlayerData) {
            ((PvPEventPlayerData) this._eventData).setDeaths(count);
        }

    }

    public boolean isInFFAEvent() {
        return this._isInFFAEvent;
    }

    public void setIsRegisteredToMiniEvent(boolean b, MiniEventManager minievent) {
        this._isRegistered = b;
        this._registeredMiniEvent = minievent;
    }

    public MiniEventManager getRegisteredMiniEvent() {
        return this._registeredMiniEvent;
    }

    public void setIsRegisteredToMainEvent(boolean b, EventType event) {
        this._isRegistered = b;
        this._registeredMainEvent = event;
    }

    public EventType getRegisteredMainEvent() {
        return this._registeredMainEvent;
    }

    public MiniEventGame getActiveGame() {
        return this._activeEvent instanceof MiniEventGame ? (MiniEventGame) this._activeEvent : null;
    }

    public AbstractMainEvent getActiveEvent() {
        return this._activeEvent instanceof AbstractMainEvent ? (AbstractMainEvent) this._activeEvent : null;
    }

    public EventGame getEvent() {
        return this._activeEvent;
    }

    public void setActiveGame(MiniEventGame game) {
        this._activeEvent = game;
    }

    public void setEventTeam(EventTeam team) {
        this._eventTeam = team;
    }

    public EventTeam getEventTeam() {
        return this._eventTeam;
    }

    public int getTeamId() {
        return this._eventTeam != null ? this._eventTeam.getTeamId() : -1;
    }

    public Loc getOrigLoc() {
        return new Loc(this._origLoc.getX(), this._origLoc.getY(), this._origLoc.getZ());
    }

    public void setIsSpectator(boolean isSpectator) {
        this._isSpectator = isSpectator;
    }

    public boolean isSpectator() {
        return this._isSpectator;
    }

    public boolean isEventRooted() {
        return this._disableAfkCheck;
    }

    public boolean isTitleUpdated() {
        return this._titleUpdate;
    }

    public void setTitleUpdated(boolean b) {
        this._titleUpdate = b;
    }

    public ItemData getPaperdollItem(int slot) {
        return new ItemData(this.getOwner().getInventory().getPaperdollItem(slot));
    }

    public void equipItem(ItemData item) {
        this.getOwner().getInventory().equipItemAndRecord(item.getOwner());
    }

    public ItemData[] unEquipItemInBodySlotAndRecord(int slot) {
        L2ItemInstance[] is = this.getOwner().getInventory().unEquipItemInBodySlotAndRecord(slot);
        ItemData[] items = new ItemData[is.length];

        for (int i = 0; i < is.length; ++i) {
            items[i] = new ItemData(is[i]);
        }

        return items;
    }

    public void destroyItemByItemId(int id, int count) {
        this.getOwner().getInventory().destroyItemByItemId("", id, (long) count, (L2PcInstance) null, (Object) null);
    }

    public void inventoryUpdate(ItemData[] items) {
        InventoryUpdate iu = new InventoryUpdate();
        ItemData[] var3 = items;
        int var4 = items.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            ItemData element = var3[var5];
            iu.addModifiedItem(element.getOwner());
        }

        this.getOwner().sendPacket(iu);
        this.getOwner().sendPacket(new ItemList(this.getOwner(), false));
        this.getOwner().broadcastUserInfo();
    }

    public PlayerEventInfo.Radar getRadar() {
        return this._radar;
    }

    public void createRadar() {
        this._radar = new PlayerEventInfo.Radar(this);
    }

    public void addRadarMarker(int x, int y, int z) {
        if (this._owner != null) {
            this._owner.getRadar().addMarker(x, y, z);
            this._hasMarkers = true;
        }

    }

    public void removeRadarMarker(int x, int y, int z) {
        if (this._owner != null) {
            this._owner.getRadar().removeMarker(x, y, z);
        }

    }

    public void removeRadarAllMarkers() {
        if (this._owner != null && this._hasMarkers) {
            this._owner.getRadar().removeAllMarkers();
            this._hasMarkers = false;
        }

    }

    public void disableAfkCheck(boolean b) {
        this._disableAfkCheck = b;
        if (!b && this._afkChecker != null) {
            this._afkChecker.check();
        }

    }

    public int getTotalTimeAfk() {
        return this._afkChecker == null ? 0 : Math.max(0, this._afkChecker.totalTimeAfk);
    }

    public boolean isAfk() {
        return this._afkChecker != null ? this._afkChecker.isAfk : false;
    }

    public PlayerEventInfo.AfkChecker getAfkChecker() {
        return this._afkChecker;
    }

    public CharacterData getCharacterData() {
        return new CharacterData(this.getOwner());
    }

    public class AfkChecker implements Runnable {
        private final PlayerEventInfo _player;
        private ScheduledFuture<?> _nextTask;
        protected boolean isAfk;
        protected int totalTimeAfk;
        private int tempTimeAfk;
        private boolean isWarned;

        public AfkChecker(PlayerEventInfo player) {
            this._player = player;
            this.isWarned = false;
            this.isAfk = false;
            this.totalTimeAfk = 0;
            this.tempTimeAfk = 0;
            this.check();
        }

        public void onAction() {
            if (PlayerEventInfo.this.isInEvent()) {
                if (this._nextTask != null) {
                    this._nextTask.cancel(false);
                }

                this.tempTimeAfk = 0;
                this.isWarned = false;
                if (this.isAfk) {
                    PlayerEventInfo.this._owner.sendMessage("Welcome back. Total time spent AFK so far: " + this.totalTimeAfk);
                    this.isAfk = false;
                    if (PlayerEventInfo.this._activeEvent != null) {
                        PlayerEventInfo.this._activeEvent.playerReturnedFromAfk(this._player);
                    }
                }

                this.check();
            }
        }

        public synchronized void run() {
            if (PlayerEventInfo.this.isInEvent()) {
                if (this.isWarned) {
                    if (!PlayerEventInfo.this._disableAfkCheck && !PlayerEventInfo.this._owner.isDead()) {
                        if (this.isAfk) {
                            this.totalTimeAfk += 10;
                            this.tempTimeAfk += 10;
                        } else {
                            this.isAfk = true;
                        }

                        if (PlayerEventInfo.this._activeEvent != null) {
                            PlayerEventInfo.this._activeEvent.playerWentAfk(this._player, false, this.tempTimeAfk);
                        }
                    }

                    this.check(10000L);
                } else {
                    if (!PlayerEventInfo.this._disableAfkCheck && !PlayerEventInfo.this._owner.isDead()) {
                        this.isWarned = true;
                        if (PlayerEventInfo.this.getActiveGame() != null) {
                            PlayerEventInfo.this.getActiveGame().playerWentAfk(this._player, true, 0);
                        }

                        if (PlayerEventInfo.this.getActiveEvent() != null) {
                            PlayerEventInfo.this.getActiveEvent().playerWentAfk(this._player, true, 0);
                        }
                    }

                    this.check();
                }

            }
        }

        protected synchronized void check() {
            if (!PlayerEventInfo.this._disableAfkCheck) {
                if (this._nextTask != null) {
                    this._nextTask.cancel(false);
                }

                this._nextTask = ThreadPoolManager.getInstance().scheduleGeneral(this, this.isWarned ? (long) PlayerEventInfo.AFK_KICK_DELAY : (long) PlayerEventInfo.AFK_WARNING_DELAY);
            }
        }

        private synchronized void check(long delay) {
            if (!PlayerEventInfo.this._disableAfkCheck) {
                if (this._nextTask != null) {
                    this._nextTask.cancel(false);
                }

                if (this.isAfk) {
                    this._nextTask = ThreadPoolManager.getInstance().scheduleGeneral(this, delay);
                }

            }
        }

        public void stop() {
            if (this._nextTask != null) {
                this._nextTask.cancel(false);
            }

            this._nextTask = null;
            this.isAfk = false;
            this.isWarned = false;
            this.totalTimeAfk = 0;
            this.tempTimeAfk = 0;
        }
    }

    public class Radar {
        private final PlayerEventInfo _player;
        private ScheduledFuture<?> _refresh;
        private boolean _enabled;
        private boolean _repeat = false;
        private int _newX;
        private int _newY;
        private int _newZ;
        private int _currentX;
        private int _currentY;
        private int _currentZ;
        private boolean hasRadar;

        public Radar(PlayerEventInfo player) {
            this._player = player;
            this._refresh = null;
            this._enabled = false;
            this.hasRadar = false;
        }

        public void setLoc(int x, int y, int z) {
            this._newX = x;
            this._newY = y;
            this._newZ = z;
        }

        public void enable() {
            this._enabled = true;
            this.applyRadar();
        }

        public void disable() {
            this._enabled = false;
            if (this.hasRadar) {
                this._player.removeRadarMarker(this._currentX, this._currentY, this._currentZ);
                this.hasRadar = false;
            }

        }

        public void setRepeat(boolean nextRepeatPolicy) {
            if (!this._enabled || this._repeat && !nextRepeatPolicy) {
                if (this._refresh != null) {
                    this._refresh.cancel(false);
                    this._refresh = null;
                }
            } else if (!this._repeat && nextRepeatPolicy) {
                if (this._refresh != null) {
                    this._refresh.cancel(false);
                    this._refresh = null;
                }

                this._refresh = CallBack.getInstance().getOut().scheduleGeneral(() -> {
                    this.applyRadar();
                }, 10000L);
            }

            this._repeat = nextRepeatPolicy;
        }

        protected void applyRadar() {
            if (this._enabled) {
                if (this.hasRadar) {
                    this._player.removeRadarMarker(this._currentX, this._currentY, this._currentZ);
                    this.hasRadar = false;
                }

                this._player.addRadarMarker(this._newX, this._newY, this._newZ);
                this._currentX = this._newX;
                this._currentY = this._newY;
                this._currentZ = this._newZ;
                this.hasRadar = true;
                if (this._repeat) {
                    this.schedule();
                }
            }

        }

        private void schedule() {
            this._refresh = CallBack.getInstance().getOut().scheduleGeneral(() -> {
                this.applyRadar();
            }, 10000L);
        }

        public boolean isEnabled() {
            return this._enabled;
        }

        public boolean isRepeating() {
            return this._repeat;
        }
    }
}
