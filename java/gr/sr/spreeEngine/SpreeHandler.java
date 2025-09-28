package gr.sr.spreeEngine;


import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.network.clientpackets.Say2;
import l2r.gameserver.network.serverpackets.CreatureSay;
import l2r.gameserver.network.serverpackets.ExShowScreenMessage;
import l2r.gameserver.util.Broadcast;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */

public class SpreeHandler
{
	public SpreeHandler()
	{
		// Dummy default
	}


	public void spreeSystem(L2PcInstance player, int spreeKills)
	{
		String announceMessage = null;
		String msgCaseS = null;
		boolean global = false;

		//entendeu aki ne? aquele time ali qur dizer oq achei q era o tempo q tinha pra fazer as 30kill kkk pq so vai ter q para a contagem se o ppl morrer ai reseta
		// é assim msm q funfa. so ligar a funcao no custom.ini ta

		switch (spreeKills) // quantia de kills subsequentes
		{
			case 1:
				break;
			case 2:
				break;
			case 5:
				msgCaseS = "%you% Ultra Kill! ( 5 Kills )";
				announceMessage = "Ultra Kill! ( 5 Kills )";
				global = true;
				break;
			case 10:
				msgCaseS = "%you% Rampage! ( 10 Kills )";
				announceMessage = "Rampage! ( 10 Kills )";
				global = true;
				break;
			case 15:
				msgCaseS = "%you% Unstoppable! ( 15 Kills )";
				announceMessage = "Unstoppable! ( 15 Kills )";
				global = true;
				break;
			case 20: // 20 kill
				msgCaseS = "%you% Dominating! ( 20 Kills )";
				announceMessage = "Dominating! ( 20 Kills )";
				global = true;
				break;
			case 25: // 25 kill
				msgCaseS = "%you% is Godlike! ( 25 Kills )";
				announceMessage = "is Godlike! ( 25 Kills )";
				global = true;
				break;
			case 30:
				msgCaseS = "%you% Monster Kill! ( 30 Kills )"; // mensagem do screen
				announceMessage = "Monster Kill! ( 30 Kills )"; // msg do player na janela de msgs
				global = true;
				break;
			default:
		}
		
		//como q vc quer os anuncios? quais globavis e quais somente pro char? o global fica so de 20kill e 30kill
		
		if(global){
			if ((msgCaseS != null) && (announceMessage != null))
			{
				Broadcast.toAllOnlinePlayers(new CreatureSay(1, Say2.CRITICAL_ANNOUNCE, "", "PvP Manager: " + player.getName() + " " + announceMessage));
				Broadcast.toAllOnlinePlayers(new ExShowScreenMessage(msgCaseS.replace("%you%", player.getName()), 5000)); // tempo q vai ficar na tela ata
			}
		}else{
			if ((msgCaseS != null) && (announceMessage != null))
			{
				player.sendPacket(new ExShowScreenMessage(msgCaseS.replace("%you%", "You've"), 5000));
				player.sendPacket(new CreatureSay(1, Say2.CRITICAL_ANNOUNCE, "", "PvP Manager: " + player.getName() + " " + announceMessage));
			}
		}
		
		
	}


	public static SpreeHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final SpreeHandler _instance = new SpreeHandler();
	}
}