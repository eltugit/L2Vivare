package gr.sr.l2j;

import gr.sr.events.EventGame;
import gr.sr.events.engine.base.EventPlayerData;
import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.base.Loc;
import gr.sr.events.engine.main.events.AbstractMainEvent;
import gr.sr.events.engine.mini.MiniEventGame;
import gr.sr.events.engine.mini.MiniEventManager;
import gr.sr.events.engine.team.EventTeam;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.*;
import l2r.gameserver.enums.audio.IAudio;
import l2r.gameserver.model.actor.L2Summon;

import java.util.List;

public interface IPlayerEventInfo {
    void initOrigInfo();

    void restoreData();

    void onEventStart(EventGame paramEventGame);

    void clean();

    void teleport(Loc paramLoc, int paramInt1, boolean paramBoolean, int paramInt2);

    void teleToLocation(Loc paramLoc, boolean paramBoolean);

    void teleToLocation(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean);

    void teleToLocation(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean);

    void setXYZInvisible(int paramInt1, int paramInt2, int paramInt3);

    void setFame(int paramInt);

    int getFame();

    void setInstanceId(int paramInt);

    void sendPacket(String paramString);

    void screenMessage(String paramString1, String paramString2, boolean paramBoolean);

    void creatureSay(String paramString1, String paramString2, int paramInt);

    void sendMessage(String paramString);

    void sendEventScoreBar(String paramString);

    void broadcastUserInfo();

    void broadcastTitleInfo();

    void sendSkillList();

    void transform(int paramInt);

    boolean isTransformed();

    void untransform(boolean paramBoolean);

    ItemData addItem(int paramInt1, int paramInt2, boolean paramBoolean);

    void addExpAndSp(long paramLong, int paramInt);

    void doDie();

    void doDie(CharacterData paramCharacterData);

    ItemData[] getItems();

    void getSkillEffects(int paramInt1, int paramInt2);

    void getPetSkillEffects(int paramInt1, int paramInt2);

    void addSkill(SkillData paramSkillData, boolean paramBoolean);

    void removeSkill(int paramInt);

    void removeCubics();

    void removeSummon();

    boolean hasPet();

    void removeBuffs();

    void removeBuffsFromPet();

    void removeBuff(int paramInt);

    int getBuffsCount();

    int getDancesCount();

    int getMaxBuffCount();

    int getMaxDanceCount();

    int getPetBuffCount();

    int getPetDanceCount();

    void abortCasting();

    void playSound(IAudio paramIAudio);

    void setVisible();

    void rebuffPlayer();

    void enableAllSkills();

    void sendSetupGauge(int paramInt);

    void root();

    void unroot();

    void paralizeEffect(boolean paramBoolean);

    void setIsParalyzed(boolean paramBoolean);

    void setIsInvul(boolean paramBoolean);

    void setCanInviteToParty(boolean paramBoolean);

    boolean canInviteToParty();

    void setIsSitForced(boolean paramBoolean);

    boolean isSitForced();

    boolean hasSummon();

    L2Summon getSummon();

    void showEventEscapeEffect();

    void broadcastSkillUse(CharacterData paramCharacterData1, CharacterData paramCharacterData2, int paramInt1, int paramInt2);

    void broadcastSkillLaunched(CharacterData paramCharacterData1, CharacterData paramCharacterData2, int paramInt1, int paramInt2);

    void enterObserverMode(int paramInt1, int paramInt2, int paramInt3);

    void removeObserveMode();

    void sendStaticPacket();

    void sendHtmlText(String paramString);

    void sendHtmlPage(String paramString);

    void startAbnormalEffect(int paramInt);

    void stopAbnormalEffect(int paramInt);

    void startAntifeedProtection(boolean paramBoolean);

    void stopAntifeedProtection(boolean paramBoolean);

    boolean hasAntifeedProtection();

    void removeOriginalShortcuts();

    void restoreOriginalShortcuts();

    void removeCustomShortcuts();

    void registerShortcut(ShortCutData paramShortCutData, boolean paramBoolean);

    void removeShortCut(ShortCutData paramShortCutData, boolean paramBoolean);

    ShortCutData createItemShortcut(int paramInt1, int paramInt2, ItemData paramItemData);

    ShortCutData createSkillShortcut(int paramInt1, int paramInt2, SkillData paramSkillData);

    ShortCutData createActionShortcut(int paramInt1, int paramInt2, int paramInt3);

    boolean isOnline();

    boolean isOnline(boolean paramBoolean);

    boolean isDead();

    boolean isVisible();

    boolean isHero();

    void doRevive();

    CharacterData getTarget();

    String getPlayersName();

    int getLevel();

    int getPvpKills();

    int getPkKills();

    int getMaxHp();

    int getMaxCp();

    int getMaxMp();

    void setCurrentHp(int paramInt);

    void setCurrentCp(int paramInt);

    void setCurrentMp(int paramInt);

    double getCurrentHp();

    double getCurrentCp();

    double getCurrentMp();

    void healPet();

    void setTitle(String paramString, boolean paramBoolean);

    boolean isMageClass();

    int getClassIndex();

    int getActiveClass();

    String getClassName();

    PartyData getParty();

    boolean isFighter();

    boolean isPriest();

    boolean isMystic();

    boolean isDominator();

    boolean isTank();

    ClassType getClassType();

    int getX();

    int getY();

    int getZ();

    int getHeading();

    int getInstanceId();

    int getClanId();

    boolean isGM();

    String getIp();

    boolean isInJail();

    boolean isInSiege();

    boolean isInDuel();

    boolean isInOlympiad();

    boolean isInOlympiadMode();

    int getKarma();

    boolean isCursedWeaponEquipped();

    boolean isImmobilized();

    boolean isParalyzed();

    boolean isAfraid();

    boolean isInStoreMode();

    boolean isOlympiadRegistered();

    void sitDown();

    void standUp();

    List<SkillData> getSkills();

    List<Integer> getSkillIds();

    double getPlanDistanceSq(int paramInt1, int paramInt2);

    double getDistanceSq(int paramInt1, int paramInt2, int paramInt3);

    boolean isRegistered();

    boolean isInEvent();

    EventPlayerData getEventData();

    void setNameColor(int paramInt);

    void setCanBuff(boolean paramBoolean);

    boolean canBuff();

    int getPlayersId();

    int getKills();

    int getDeaths();

    int getScore();

    int getStatus();

    void raiseKills(int paramInt);

    void raiseDeaths(int paramInt);

    void raiseScore(int paramInt);

    void setScore(int paramInt);

    void setStatus(int paramInt);

    void setKills(int paramInt);

    void setDeaths(int paramInt);

    boolean isInFFAEvent();

    void setIsRegisteredToMiniEvent(boolean paramBoolean, MiniEventManager paramMiniEventManager);

    MiniEventManager getRegisteredMiniEvent();

    void setIsRegisteredToMainEvent(boolean paramBoolean, EventType paramEventType);

    EventType getRegisteredMainEvent();

    MiniEventGame getActiveGame();

    AbstractMainEvent getActiveEvent();

    EventGame getEvent();

    void setActiveGame(MiniEventGame paramMiniEventGame);

    void setEventTeam(EventTeam paramEventTeam);

    EventTeam getEventTeam();

    int getTeamId();

    Loc getOrigLoc();

    void setIsSpectator(boolean paramBoolean);

    boolean isSpectator();

    boolean isEventRooted();

    boolean isTitleUpdated();

    void setTitleUpdated(boolean paramBoolean);

    ItemData getPaperdollItem(int paramInt);

    void equipItem(ItemData paramItemData);

    ItemData[] unEquipItemInBodySlotAndRecord(int paramInt);

    void destroyItemByItemId(int paramInt1, int paramInt2);

    void inventoryUpdate(ItemData[] paramArrayOfItemData);

    void addRadarMarker(int paramInt1, int paramInt2, int paramInt3);

    void removeRadarMarker(int paramInt1, int paramInt2, int paramInt3);

    void removeRadarAllMarkers();

    void createRadar();

    PlayerEventInfo.Radar getRadar();

    void disableAfkCheck(boolean paramBoolean);

    int getTotalTimeAfk();

    boolean isAfk();

    PlayerEventInfo.AfkChecker getAfkChecker();

    CharacterData getCharacterData();
}


