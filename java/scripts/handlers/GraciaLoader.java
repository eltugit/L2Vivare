package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.gracia.AI.EnergySeeds;
import scripts.gracia.AI.Lindvior;
import scripts.gracia.AI.Maguen;
import scripts.gracia.AI.NPC.FortuneTelling.FortuneTelling;
import scripts.gracia.AI.NPC.GeneralDilios.GeneralDilios;
import scripts.gracia.AI.NPC.Lekon.Lekon;
import scripts.gracia.AI.NPC.Nemo.Nemo;
import scripts.gracia.AI.NPC.Nottingale.Nottingale;
import scripts.gracia.AI.NPC.Seyo.Seyo;
import scripts.gracia.AI.NPC.ZealotOfShilen.ZealotOfShilen;
import scripts.gracia.AI.SeedOfAnnihilation.SeedOfAnnihilation;
import scripts.gracia.AI.StarStones;
import scripts.gracia.instances.HallOfErosionAttack.HallOfErosionAttack;
import scripts.gracia.instances.HallOfErosionDefence.HallOfErosionDefence;
import scripts.gracia.instances.HallOfSufferingAttack.HallOfSufferingAttack;
import scripts.gracia.instances.HallOfSufferingDefence.HallOfSufferingDefence;
import scripts.gracia.instances.HeartInfinityAttack.HeartInfinityAttack;
import scripts.gracia.instances.HeartInfinityDefence.HeartInfinityDefence;
import scripts.gracia.instances.SecretArea.SecretArea;
import scripts.gracia.instances.SeedOfDestruction.SeedOfDestruction;
import scripts.gracia.vehicles.AirShipGludioGracia.AirShipGludioGracia;
import scripts.gracia.vehicles.KeucereusNorthController.KeucereusNorthController;
import scripts.gracia.vehicles.KeucereusSouthController.KeucereusSouthController;
import scripts.gracia.vehicles.SoDController.SoDController;
import scripts.gracia.vehicles.SoIController.SoIController;

/**
 * Gracia class-loader.
 * @author Pandragon
 */
public final class GraciaLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		// AIs
		EnergySeeds.class,
		Lindvior.class,
		Maguen.class,
		StarStones.class,
		// NPCs
		FortuneTelling.class,
		GeneralDilios.class,
		Lekon.class,
		Nemo.class,
		Nottingale.class,
		Seyo.class,
		ZealotOfShilen.class,
		// Seed of Annihilation
		SeedOfAnnihilation.class,
		// Instances
		HallOfErosionAttack.class,
		HallOfErosionDefence.class,
		HallOfSufferingAttack.class,
		HallOfSufferingDefence.class,
		HeartInfinityAttack.class,
		HeartInfinityDefence.class,
		SecretArea.class,
		SeedOfDestruction.class,
		// Vehicles
		AirShipGludioGracia.class,
		KeucereusNorthController.class,
		KeucereusSouthController.class,
		SoIController.class,
		SoDController.class,
	};
	
	public GraciaLoader()
	{
		loadScripts();
        _log.info("Gracia loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
