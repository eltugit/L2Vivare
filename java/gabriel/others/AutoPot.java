package gabriel.others;


import gabriel.Utils.GabUtils;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.network.serverpackets.ExAutoSoulShot;
import l2r.gameserver.network.serverpackets.MagicSkillUse;
import scripts.handlers.itemhandlers.ItemSkills;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */

public class AutoPot implements Runnable {
    private final int id;
    private final L2PcInstance activeChar;
    L2ItemInstance item;
    final long cd;

    final static int[] PROTECTED_SKILLS_IDS = // Skill Ids that will pause auto potions
            {
                    922,
                    443,
                    837,
                    1418,
                    1427,
                    442
            };

    public AutoPot(int id, L2PcInstance activeChar, L2ItemInstance item, long cooldown) {
        this.id = id;
        this.activeChar = activeChar;
        this.item = item;
        this.cd = cooldown;
    }

    public void SkillAndItemUse(Integer skillId, Integer skillLvl, Integer idItem) {
        MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, skillId, skillLvl, 0, 100);
        activeChar.broadcastPacket(msu);
        ItemSkills is = new ItemSkills();
        is.useItem(activeChar, activeChar.getInventory().getItemByItemId(idItem), true);
        activeChar.lastUsedPot.put(idItem, System.currentTimeMillis() + (cd-1000));

    }

    @Override
    public void run() {
        if (activeChar.isDead()) {
            return;
        }
        if (activeChar.isInvisible() || activeChar.isInvul()) {
            return;
        }


        long lastUsed = activeChar.lastUsedPot.getOrDefault(id , 0L);
        if(lastUsed != 0L && lastUsed > System.currentTimeMillis()){
            return;
        }


        for (int skillId : PROTECTED_SKILLS_IDS) {
            if (activeChar.getEffectList().isAffectedBySkill(skillId)) {
                return;
            }
        }

        if (activeChar.getInventory().getItemByItemId(id) == null) {
            activeChar.sendPacket(new ExAutoSoulShot(id, 0));
            activeChar.sendMessage("Deactivated auto " + item.getItemName());
            activeChar.setAutoPot(id, null, false);
            return;
        }

        switch (id) {
            case 728: {
                if (activeChar.getCurrentMp() < (0.90 * activeChar.getMaxMp())) {
                    SkillAndItemUse(10001, 1, 728);
                }
            }
            break;
            case 1539: {
                if (activeChar.getCurrentHp() < (0.90 * activeChar.getMaxHp())) {
                    SkillAndItemUse(2037, 1, 1539);
                }
            }
            break;
            case 5592: {
                if (activeChar.getCurrentCp() < (0.90 * activeChar.getMaxCp())) {
                    SkillAndItemUse(2166, 2, 5592);
                }
            }
            break;
            case 5591: {
                if (activeChar.getCurrentCp() < (0.90 * activeChar.getMaxCp())) {
                    SkillAndItemUse(2166, 1, 5591);
                }
            }
            break;
            case 10410: {
                if (activeChar.getChargedSouls() < 40) {
                    SkillAndItemUse(2499, 1, 10410);
                }
            }
            break;
        }
    }
}