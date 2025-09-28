package l2r.gameserver.model.actor.instance;

import gabriel.interServerExchange.ISEConfig;
import l2r.gameserver.cache.HtmCache;
import l2r.gameserver.enums.InstanceType;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.*;


/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class L2InterServerExchangeInstance extends L2Npc {
    private static final String htmlPath = "data/html/gabriel/events/InterServerExchange/";

    public L2InterServerExchangeInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2InterServerExchangeInstance);
    }

    @Override
    public void onBypassFeedback(L2PcInstance playerInstance, String command) {
        if (command.equals("deposit")) {
            if(!ISEConfig.ALLOW_DEPOSIT)
                return;
            playerInstance.setLastFolkNPC(this);
            playerInstance.tempInventoryDisable();
            playerInstance.sendPacket(new WareHouseDepositList(playerInstance, WareHouseDepositList.PRIVATE, true));
        }
        else if (command.equals("withdraw")) {
            if(!ISEConfig.ALLOW_WITHDRAW)
                return;
            playerInstance.setLastFolkNPC(this);
            playerInstance.sendPacket(new WareHouseWithdrawalList(playerInstance, WareHouseWithdrawalList.PRIVATE, true));
        }
    }

    @Override
    public void showChatWindow(L2PcInstance playerInstance, int val) {
        if (playerInstance == null || !ISEConfig.ENABLED)
            return;

        final String htmContent = HtmCache.getInstance().getHtm(playerInstance.getHtmlPrefix(), htmlPath + "index.htm");

        if (htmContent != null) {
            NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

            npcHtmlMessage.setHtml(htmContent);
            npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));

            playerInstance.sendPacket(npcHtmlMessage);
        }

        playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
    }
}
