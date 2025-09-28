package gr.sr.events;

import gr.sr.events.engine.EventManager;
import gr.sr.events.engine.base.EventPlayerData;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.CharacterData;
import gr.sr.interf.delegate.ItemData;
import gr.sr.interf.delegate.NpcData;
import gr.sr.interf.delegate.SkillData;

public interface EventGame {
    EventPlayerData createPlayerData(PlayerEventInfo paramPlayerEventInfo);

    EventPlayerData getPlayerData(PlayerEventInfo paramPlayerEventInfo);

    void clearEvent();

    boolean canAttack(PlayerEventInfo paramPlayerEventInfo, CharacterData paramCharacterData);

    boolean onAttack(CharacterData paramCharacterData1, CharacterData paramCharacterData2);

    boolean canSupport(PlayerEventInfo paramPlayerEventInfo, CharacterData paramCharacterData);

    void onKill(PlayerEventInfo paramPlayerEventInfo, CharacterData paramCharacterData);

    void onDie(PlayerEventInfo paramPlayerEventInfo, CharacterData paramCharacterData);

    void onDamageGive(CharacterData paramCharacterData1, CharacterData paramCharacterData2, int paramInt, boolean paramBoolean);

    void onDisconnect(PlayerEventInfo paramPlayerEventInfo);

    boolean addDisconnectedPlayer(PlayerEventInfo paramPlayerEventInfo, EventManager.DisconnectedPlayerData paramDisconnectedPlayerData);

    boolean onSay(PlayerEventInfo paramPlayerEventInfo, String paramString, int paramInt);

    boolean onNpcAction(PlayerEventInfo paramPlayerEventInfo, NpcData paramNpcData);

    boolean canUseItem(PlayerEventInfo paramPlayerEventInfo, ItemData paramItemData);

    void onItemUse(PlayerEventInfo paramPlayerEventInfo, ItemData paramItemData);

    boolean canUseSkill(PlayerEventInfo paramPlayerEventInfo, SkillData paramSkillData);

    void onSkillUse(PlayerEventInfo paramPlayerEventInfo, SkillData paramSkillData);

    boolean canDestroyItem(PlayerEventInfo paramPlayerEventInfo, ItemData paramItemData);

    boolean canInviteToParty(PlayerEventInfo paramPlayerEventInfo1, PlayerEventInfo paramPlayerEventInfo2);

    boolean canTransform(PlayerEventInfo paramPlayerEventInfo);

    boolean canBeDisarmed(PlayerEventInfo paramPlayerEventInfo);

    int allowTransformationSkill(PlayerEventInfo paramPlayerEventInfo, SkillData paramSkillData);

    boolean canSaveShortcuts(PlayerEventInfo paramPlayerEventInfo);

    int isSkillOffensive(SkillData paramSkillData);

    boolean isSkillNeutral(SkillData paramSkillData);

    void playerWentAfk(PlayerEventInfo paramPlayerEventInfo, boolean paramBoolean, int paramInt);

    void playerReturnedFromAfk(PlayerEventInfo paramPlayerEventInfo);
}


