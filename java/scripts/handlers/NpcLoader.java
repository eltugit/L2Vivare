package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.npc.Abercrombie.Abercrombie;
import scripts.ai.npc.Alarm.Alarm;
import scripts.ai.npc.Alexandria.Alexandria;
import scripts.ai.npc.ArenaManager.ArenaManager;
import scripts.ai.npc.Asamah.Asamah;
import scripts.ai.npc.AvantGarde.AvantGarde;
import scripts.ai.npc.BlackJudge.BlackJudge;
import scripts.ai.npc.BlackMarketeerOfMammon.BlackMarketeerOfMammon;
import scripts.ai.npc.CastleAmbassador.CastleAmbassador;
import scripts.ai.npc.CastleBlacksmith.CastleBlacksmith;
import scripts.ai.npc.CastleChamberlain.CastleChamberlain;
import scripts.ai.npc.CastleCourtMagician.CastleCourtMagician;
import scripts.ai.npc.CastleMercenaryManager.CastleMercenaryManager;
import scripts.ai.npc.CastleSiegeManager.CastleSiegeManager;
import scripts.ai.npc.CastleTeleporter.CastleTeleporter;
import scripts.ai.npc.CastleWarehouse.CastleWarehouse;
import scripts.ai.npc.ClanTrader.ClanTrader;
import scripts.ai.npc.DimensionalMerchant.DimensionalMerchant;
import scripts.ai.npc.Dorian.Dorian;
import scripts.ai.npc.DragonVortexRetail.DragonVortexRetail;
import scripts.ai.npc.EkimusMouth.EkimusMouth;
import scripts.ai.npc.FameManager.FameManager;
import scripts.ai.npc.Fisherman.Fisherman;
import scripts.ai.npc.ForgeOfTheGods.ForgeOfTheGods;
import scripts.ai.npc.ForgeOfTheGods.Rooney;
import scripts.ai.npc.ForgeOfTheGods.TarBeetle;
import scripts.ai.npc.FortressArcherCaptain.FortressArcherCaptain;
import scripts.ai.npc.FortressSiegeManager.FortressSiegeManager;
import scripts.ai.npc.FreyasSteward.FreyasSteward;
import scripts.ai.npc.Jinia.Jinia;
import scripts.ai.npc.Katenar.Katenar;
import scripts.ai.npc.KetraOrcSupport.KetraOrcSupport;
import scripts.ai.npc.ManorManager.ManorManager;
import scripts.ai.npc.MercenaryCaptain.MercenaryCaptain;
import scripts.ai.npc.Minigame.Minigame;
import scripts.ai.npc.MonumentOfHeroes.MonumentOfHeroes;
import scripts.ai.npc.NevitsHerald.NevitsHerald;
import scripts.ai.npc.NpcBuffers.NpcBuffers;
import scripts.ai.npc.NpcBuffers.impl.CabaleBuffer;
import scripts.ai.npc.PcBangPoint.PcBangPoint;
import scripts.ai.npc.PriestOfBlessing.PriestOfBlessing;
import scripts.ai.npc.Rafforty.Rafforty;
import scripts.ai.npc.Rignos.Rignos;
import scripts.ai.npc.Selina.Selina;
import scripts.ai.npc.Sirra.Sirra;
import scripts.ai.npc.Summons.MerchantGolem.GolemTrader;
import scripts.ai.npc.SupportUnitCaptain.SupportUnitCaptain;
import scripts.ai.npc.SymbolMaker.SymbolMaker;
import scripts.ai.npc.TerritoryManagers.TerritoryManagers;
import scripts.ai.npc.TownPets.TownPets;
import scripts.ai.npc.Trainers.HealerTrainer.HealerTrainer;
import scripts.ai.npc.Tunatun.Tunatun;
import scripts.ai.npc.VarkaSilenosSupport.VarkaSilenosSupport;
import scripts.ai.npc.WeaverOlf.WeaverOlf;
import scripts.ai.npc.WyvernManager.WyvernManager;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class NpcLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		Abercrombie.class,
		Alarm.class,
		Alexandria.class,
		ArenaManager.class,
		Asamah.class,
		AvantGarde.class,
		BlackJudge.class,
		BlackMarketeerOfMammon.class,
		CastleAmbassador.class,
		CastleBlacksmith.class,
		CastleChamberlain.class,
		CastleCourtMagician.class,
		CastleMercenaryManager.class,
		CastleSiegeManager.class,
		CastleTeleporter.class,
		CastleWarehouse.class,
		ClanTrader.class,
		DimensionalMerchant.class,
		Dorian.class,
		// DragonVortex.class,
//		DragonVortexRetail.class,
		EkimusMouth.class,
		FameManager.class,
		Fisherman.class,
		ForgeOfTheGods.class,
		Rooney.class,
		TarBeetle.class,
		FortressArcherCaptain.class,
		FortressSiegeManager.class,
		FreyasSteward.class,
		Jinia.class,
		Katenar.class,
		KetraOrcSupport.class,
		ManorManager.class,
		MercenaryCaptain.class,
		Minigame.class,
		MonumentOfHeroes.class,
		NevitsHerald.class,
		NpcBuffers.class,
		CabaleBuffer.class,
		PcBangPoint.class,
		PriestOfBlessing.class,
		Rafforty.class,
		Rignos.class,
		Selina.class,
		Sirra.class,
		GolemTrader.class,
		SupportUnitCaptain.class,
		SymbolMaker.class,
		TerritoryManagers.class,
		TownPets.class,
		HealerTrainer.class,
		Tunatun.class,
		VarkaSilenosSupport.class,
		WeaverOlf.class,
		WyvernManager.class,
	};
	
	public NpcLoader()
	{
		loadScripts();
        _log.info(getClass().getSimpleName() + " loaded.");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
