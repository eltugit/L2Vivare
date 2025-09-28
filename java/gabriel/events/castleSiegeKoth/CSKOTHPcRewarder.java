package gabriel.events.castleSiegeKoth;

import gabriel.config.GabConfig;
import l2r.gameserver.data.xml.impl.ItemData;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.itemcontainer.PcInventory;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */

public class CSKOTHPcRewarder {
    
    public static void reward(L2PcInstance player) {

        if (player == null)
            return;

        if (player._inGabAbstractEvent && GabConfig.CSKOTH_ARENA_KILL_ENABLE) {
            if (GabConfig.CSKOTH_ARENA_FAME_ENABLE) {
                player.setFame(player.getFame() + GabConfig.CSKOTH_EVENT_FAMA_KILL);
            }
            if (GabConfig.CSKOTH_ARENA_REWARDKILL_ENABLE) {
                SystemMessage systemMessage = null;
                for (int[] reward : GabConfig.CSKOTH_EVENT_REWARDS_KILL) {
                    PcInventory inv = player.getInventory();

                    // Check for stackable item, non stackabe items need to be added one by one
                    if (ItemData.getInstance().createDummyItem(reward[0]).isStackable()) {
                        inv.addItem("KOTH Reward Per Kill", reward[0], reward[1], player, player);

                        if (reward[1] > 1) {
                            systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S);
                            systemMessage.addItemName(reward[0]);
                            systemMessage.addInt(reward[1]);
                        } else {
                            systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
                            systemMessage.addItemName(reward[0]);
                        }

                        player.sendPacket(systemMessage);
                    } else {
                        for (int i = 0; i < reward[1]; ++i) {
                            inv.addItem("KOTH Reward Per Kill", reward[0], 1, player, player);
                            systemMessage = SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1);
                            systemMessage.addItemName(reward[0]);
                            player.sendPacket(systemMessage);
                        }
                    }
                }
            }

        }
    }
}
