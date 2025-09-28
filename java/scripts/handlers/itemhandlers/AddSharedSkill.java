package scripts.handlers.itemhandlers;

import gabriel.listener.actor.player.OnAnswerListener;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.handler.IItemHandler;
import l2r.gameserver.model.actor.L2Playable;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.items.instance.L2ItemInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.SystemMessageId;
import l2r.gameserver.network.serverpackets.ConfirmDlg;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class AddSharedSkill implements IItemHandler {

    @Override
    public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) {
        if (!(playable instanceof L2PcInstance))
            return false;

        final L2PcInstance activeChar = (L2PcInstance) playable;
        return useItem(activeChar, item);
    }

    public static boolean useItem(final L2PcInstance castor, L2ItemInstance item) {
        String sharedSkill = castor.getVar("sharedSkill" + item.getId(), null);
        if (item.getItem().getSharedSkill() == null) {
            castor.sendMessage("Item missconfigurated. Contact the admin. Missing SharedSkillId parameter (id-level)");
            return false;
        }
        if (sharedSkill != null) {
            castor.sendMessage("You already own this item.");
            return false;
        }
        String[] params = item.getItem().getSharedSkill().split("-");
        L2Skill skill = SkillData.getInstance().getSkill(Integer.parseInt(params[0]), Integer.parseInt(params[1]));
        if(skill == null){
            castor.sendMessage("Requested skill does not exist. Report to admin.");
            return false;
        }

        ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.S1);
        dlg.addString("Do you wish to use this item and learn an shared skill?");
        dlg.addTime(15 * 1000);
        dlg.addRequesterId(castor.getObjectId());
        castor.ask(dlg, new OnAnswerListener() {
            @Override
            public void sayYes() {
                if (castor.destroyItem("Consume", item, 1, castor, true)) {
                    castor.addSkill(skill, false);
                    castor.sendSkillList();
                    castor.sendMessage("You used " + item.getName() + " and added a new skill!");
                    castor.setVar("sharedSkill" + item.getId(), item.getItem().getSharedSkill());
                }
            }

            @Override
            public void sayNo() {

            }
        });
        return true;
    }

}

