package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.individual.*;
import scripts.ai.individual.Venom.Venom;
import scripts.ai.individual.extra.*;
import scripts.ai.individual.extra.ToiRaids.Golkonda;
import scripts.ai.individual.extra.ToiRaids.Hallate;
import scripts.ai.individual.extra.ToiRaids.Kernon;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class IndividualLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		Anais.class,
		Ballista.class,
		CrimsonHatuOtis.class,
		DarkWaterDragon.class,
		DivineBeast.class,
		Epidos.class,
		EvasGiftBox.class,
		FrightenedRagnaOrc.class,
		GiganticGolem.class,
		Gordon.class,
		GraveRobbers.class,
		QueenShyeed.class,
		RagnaOrcCommander.class,
		RagnaOrcHero.class,
		RagnaOrcSeer.class,
		SinEater.class,
		SinWardens.class,
		
		// Extras
		Aenkinel.class,
		Barakiel.class,
		BladeOtis.class,
		EtisEtina.class,
		FollowerOfAllosce.class,
		FollowerOfMontagnar.class,
		Gargos.class,
		Hellenark.class,
		HolyBrazier.class,
		KaimAbigore.class,
		Kechi.class,
		KelBilette.class,
		OlAriosh.class,
		SelfExplosiveKamikaze.class,
		ValakasMinions.class,
		VenomousStorace.class,
		WeirdBunei.class,
		WhiteAllosce.class,
		
		// Extra Toi Raids
		Golkonda.class,
		Hallate.class,
		Kernon.class,
		
		// Other
		Venom.class,
	};
	
	public IndividualLoader()
	{
		loadScripts();
        _log.info("Individual loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
