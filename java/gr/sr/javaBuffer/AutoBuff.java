package gr.sr.javaBuffer;


import gr.sr.configsEngine.configs.impl.BufferConfigs;
import gr.sr.javaBuffer.xml.dataHolder.BuffsHolder;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;


public class AutoBuff {
    private static int[] mageBuffs;
    private static int[] fightBuffs;
    private static int buffsLength;

    public AutoBuff() {
    }


    public static void autoBuff(L2PcInstance player) {
        if (PlayerMethods.checkDelay(player)) {
            int[] buffList;
            int length;
            int i;
            int buffId;
            BuffsInstance buffsInstance;
            L2Skill skill;
            if (player.isMageClass()) {
                buffsLength = mageBuffs.length;
                if (!PlayerMethods.checkPriceConsume(player, buffsLength)) {
                    return;
                }

                length = (buffList = mageBuffs).length;

                for(i = 0; i < length; ++i) {
                    buffId = buffList[i];
                    buffsInstance = BuffsHolder.getInstance().getBuff(buffId);
                    if(buffsInstance == null)
                        return;
                    if(player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null){
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getCustomLevel());
                    }else{
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getLevel());
                    }
                    if (skill != null) {
                        skill.getEffects(player, player);
                        if (player.hasSummon()) {
                            skill.getEffects(player, player.getSummon());
                        }
                    }
                }
            } else {
                buffsLength = fightBuffs.length;
                if (!PlayerMethods.checkPriceConsume(player, buffsLength)) {
                    return;
                }

                length = (buffList = fightBuffs).length;

                for(i = 0; i < length; ++i) {
                    buffId = buffList[i];
                    buffsInstance = BuffsHolder.getInstance().getBuff(buffId);
                    if(buffsInstance == null)
                        return;
                    if(player.isPremium() || (player.getClan() != null && player.getClan().isVip()) || player.getInventory().getItemByItemId(BufferConfigs.DONATE_BUFF_ITEM_ID) != null){
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getCustomLevel());
                    }else{
                        skill = SkillData.getInstance().getInfo(buffsInstance.getId(), buffsInstance.getLevel());
                    }
                    if (skill != null) {
                        skill.getEffects(player, player);
                        if (player.hasSummon()) {
                            skill.getEffects(player, player.getSummon());
                        }
                    }
                }
            }

            if (BufferConfigs.HEAL_PLAYER_AFTER_ACTIONS) {
                player.setCurrentHpMp((double)player.getMaxHp(), (double)player.getMaxMp());
                player.setCurrentCp((double)player.getMaxCp());
            }

            PlayerMethods.addDelay(player);
        }
    }

    static {
        mageBuffs = BufferConfigs.MAGE_BUFFS_LIST;
        fightBuffs = BufferConfigs.FIGHTER_BUFFS_LIST;
    }
}
