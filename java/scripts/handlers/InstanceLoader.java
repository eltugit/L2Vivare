package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.instances.CavernOfThePirateCaptain.CavernOfThePirateCaptain;
import scripts.instances.ChambersOfDelusion.*;
import scripts.instances.CrystalCaverns.CrystalCaverns;
import scripts.instances.DarkCloudMansion.DarkCloudMansion;
import scripts.instances.DisciplesNecropolisPast.DisciplesNecropolisPast;
import scripts.instances.ElcadiaTent.ElcadiaTent;
import scripts.instances.FinalEmperialTomb.FinalEmperialTomb;
import scripts.instances.HideoutOfTheDawn.HideoutOfTheDawn;
import scripts.instances.IceQueensCastle.IceQueensCastle;
import scripts.instances.IceQueensCastleNormalBattle.IceQueensCastleNormalBattle;
import scripts.instances.IceQueensCastleUltimateBattle.IceQueensCastleUltimateBattle;
import scripts.instances.JiniaGuildHideout1.JiniaGuildHideout1;
import scripts.instances.JiniaGuildHideout2.JiniaGuildHideout2;
import scripts.instances.JiniaGuildHideout3.JiniaGuildHideout3;
import scripts.instances.JiniaGuildHideout4.JiniaGuildHideout4;
import scripts.instances.Kamaloka.Kamaloka;
import scripts.instances.KrateiCube.KrateiCube;
import scripts.instances.LibraryOfSages.LibraryOfSages;
import scripts.instances.MithrilMine.MithrilMine;
import scripts.instances.NornilsGarden.NornilsGarden;
import scripts.instances.NornilsGardenQuest.NornilsGardenQuest;
import scripts.instances.PailakaDevilsLegacy.PailakaDevilsLegacy;
import scripts.instances.PailakaInjuredDragon.PailakaInjuredDragon;
import scripts.instances.PailakaSongOfIceAndFire.PailakaSongOfIceAndFire;
import scripts.instances.RimKamaloka.RimKamaloka;
import scripts.instances.SanctumOftheLordsOfDawn.SanctumOftheLordsOfDawn;
import scripts.instances.SecretAreaKeucereus.SecretAreaKeucereus;
import scripts.instances.ToTheMonastery.ToTheMonastery;
import scripts.instances.Zaken.Zaken;

;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class InstanceLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		CavernOfThePirateCaptain.class,
		ChamberOfDelusionEast.class,
		ChamberOfDelusionNorth.class,
		ChamberOfDelusionSouth.class,
		ChamberOfDelusionSquare.class,
		ChamberOfDelusionTower.class,
		ChamberOfDelusionWest.class,
		CrystalCaverns.class,
		DarkCloudMansion.class,
		DisciplesNecropolisPast.class,
		ElcadiaTent.class,
		FinalEmperialTomb.class,
		HideoutOfTheDawn.class,
		IceQueensCastle.class,
		IceQueensCastleNormalBattle.class,
		IceQueensCastleUltimateBattle.class,
		JiniaGuildHideout1.class,
		JiniaGuildHideout2.class,
		JiniaGuildHideout3.class,
		JiniaGuildHideout4.class,
		Kamaloka.class,
		KrateiCube.class,
		LibraryOfSages.class,
		MithrilMine.class,
		NornilsGarden.class,
		NornilsGardenQuest.class,
		PailakaDevilsLegacy.class,
		PailakaInjuredDragon.class,
		PailakaSongOfIceAndFire.class,
		RimKamaloka.class,
		SanctumOftheLordsOfDawn.class,
		SecretAreaKeucereus.class,
		ToTheMonastery.class,
		Zaken.class,
	};
	
	public InstanceLoader()
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
