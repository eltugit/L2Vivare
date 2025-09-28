package gabriel.events.extremeZone;

import gr.sr.configsEngine.configs.impl.CommunityDonateConfigs;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.network.serverpackets.MagicSkillUse;

import java.util.Arrays;
import java.util.List;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 */
public class AdminExtremeEvent implements IAdminCommandHandler {
    private static final String[] ADMIN_COMMANDS =
            {
                    "admin_ex_advance",
                    "admin_clanfull"
            };
    private static List<Integer> clanSkillsLevel = Arrays.asList(372, 375, 378, 381, 389, 391, 374, 380, 382, 383, 384, 385, 386, 387, 388, 390, 371, 376, 377, 370, 373, 379 );
    private static List<Integer> clanSquadSkills = Arrays.asList( 611, 612, 613, 614, 615, 616 );

    public boolean useAdminCommand(String command, L2PcInstance activeChar) {
        if (activeChar == null || !activeChar.getPcAdmin().canUseAdminCommand())
            return false;

        if (command.equals("admin_ex_advance")) {
            ExtremeZoneManager.getInstance().skipDelay();
        }
        if (command.equals("admin_clanfull")) {
            L2PcInstance trt = activeChar.getTarget().getActingPlayer();
            if(trt == null || trt.getClan() == null || !trt.isClanLeader()){
                activeChar.sendMessage("Select leader target with clan");
                return false;
            }
            trt.getClan().changeLevel(11);
            trt.getClan().addReputationScore(CommunityDonateConfigs.COMMUNITY_DONATE_FULL_CLAN_REP_AMOUNT, true);
            trt.getClan().addNewSkill(SkillData.getInstance().getInfo(391, 1));

            clanSkillsLevel.forEach(id -> trt.getClan().addNewSkill(SkillData.getInstance().getInfo(id, 3)));
            clanSquadSkills.forEach(id -> trt.getClan().addNewSkill(SkillData.getInstance().getInfo(id, 3), 0));

            trt.broadcastPacket(new MagicSkillUse(activeChar, 6463, 1, 1000, 0));
            trt.sendMessage("You got clan level 11 with full skills.");
            trt.sendPacket(new ExShowScreenMessage("Your clan got level 11 full skills, Congratulations!", 5000));
        }

        return true;
    }

    public String[] getAdminCommandList() {
        return ADMIN_COMMANDS;
    }
}
