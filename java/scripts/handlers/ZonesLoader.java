package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.zone.DragonValley.*;
import scripts.ai.zone.FantasyIsle.HandysBlockCheckerEvent;
import scripts.ai.zone.FantasyIsle.MC_Show;
import scripts.ai.zone.FantasyIsle.Parade;
import scripts.ai.zone.LairOfAntharas.BloodyBerserker;
import scripts.ai.zone.LairOfAntharas.BloodyKarik;
import scripts.ai.zone.LairOfAntharas.BloodyKarinness;
import scripts.ai.zone.LairOfAntharas.LairOfAntharas;
import scripts.ai.zone.PavelRuins.PavelArchaic;
import scripts.ai.zone.PlainsOfLizardman.PlainsOfLizardman;
import scripts.ai.zone.PlainsOfLizardman.SeerFlouros;
import scripts.ai.zone.PlainsOfLizardman.SeerUgoros;
import scripts.ai.zone.PlainsOfLizardman.TantaLizardmanSummoner;
import scripts.ai.zone.PrimevalIsle.PrimevalIsle;
import scripts.ai.zone.SelMahums.SelMahumDrill;
import scripts.ai.zone.SelMahums.SelMahumSquad;
import scripts.ai.zone.StakatoNest.StakatoNest;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ZonesLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		// Dragon Valley
		BlackdaggerWing.class,
		BleedingFly.class,
		DragonValley.class,
		DrakosWarrior.class,
		DustRider.class,
		EmeraldHorn.class,
		MuscleBomber.class,
		NecromancerOfTheValley.class,
		ShadowSummoner.class,
		
		// Fantasy Island
		HandysBlockCheckerEvent.class,
		MC_Show.class,
		Parade.class,
		
		// Antharas Lair
		BloodyBerserker.class,
		BloodyKarik.class,
		BloodyKarinness.class,
		LairOfAntharas.class,
		
		// Pavel Ruins
		PavelArchaic.class,
		
		// Plains of Lizardman
		PlainsOfLizardman.class,
		SeerFlouros.class,
		SeerUgoros.class,
		TantaLizardmanSummoner.class,
		
		// Primeval Island
		PrimevalIsle.class,
		
		// Sel Mahums
		SelMahumDrill.class,
		SelMahumSquad.class,
		
		// Stakato Nest
		StakatoNest.class,
	};
	
	public ZonesLoader()
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
