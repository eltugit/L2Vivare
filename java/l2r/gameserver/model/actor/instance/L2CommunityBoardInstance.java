package l2r.gameserver.model.actor.instance;

import gabriel.cbbCertif.CertificationManager;
import gabriel.scriptsGab.gab.GabrielCBB;
import l2r.gameserver.communitybbs.BoardsManager;
import l2r.gameserver.communitybbs.Managers.ServicesBBSManager;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.gameserver.network.serverpackets.*;


/**
 * @author vGodFather
 */
public final class L2CommunityBoardInstance extends L2NpcInstance
{

	public L2CommunityBoardInstance(L2NpcTemplate template)
	{
		super(template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{

	}
	
	@Override
	public void showChatWindow(L2PcInstance player)
	{
		if(this.getId() == 572){ //gmshop
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbsloc;gmshop");
        }else if(this.getId() == 573){ //Donate Manager
            BoardsManager.getInstance().handleCommands(player.getClient(), "_friendlist_0_");
        }else if(this.getId() == 576){ //Buffer Manager
            ServicesBBSManager.getInstance().cbByPass("_bbsloc_buffer", player);
        }else if(this.getId() == 558){ //aionpc -> Excluive shop
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbsloc;exclusiveShop");
        }else if(this.getId() == 571){ //services can stay

        }else if(this.getId() == 570){ //gk
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbsloc;gatekeeper;main_gk");
        }else if(this.getId() == 12000){ //Museum
            BoardsManager.getInstance().handleCommands(player.getClient(), "bbs_add_fav");
        }else if(this.getId() == 12001){ //Party
            BoardsManager.getInstance().handleCommands(player.getClient(), "_maillist_0_1_0_");
        }else if(this.getId() == 12002){ //Clan
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbsclan");
        }else if(this.getId() == 12003){ //Ranks
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbstop;toppvp");
        }else if(this.getId() == 12004){ //Certification
            CertificationManager.getInstance().parseCommand("_certi", player);
        }else if(this.getId() == 556){ //Events
            BoardsManager.getInstance().handleCommands(player.getClient(), "_bbsgetfav");
        }else if(this.getId() == 10999){ //Tournament
            GabrielCBB.getInstance().parseCommand("gab_tournamentPage;start", player);
        }else if(this.getId() == 70000){ //PvPInstance
            GabrielCBB.getInstance().parseCommand("gab_pvpInstancePage", player);
        }else if(this.getId() == 12005){ //Subclass
            GabrielCBB.getInstance().parseCommand("gab__bbssubclass;", player);
        }
		
		// Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}


}