package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.group_template.*;
import scripts.ai.group_template.extra.*;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class GroupTemplatesLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		BeastFarm.class,
		DenOfEvil.class,
		FeedableBeasts.class,
		FleeMonsters.class,
		FrozenLabyrinth.class,
		GiantsCave.class,
		HotSprings.class,
		IsleOfPrayer.class,
		MinionSpawnManager.class,
		MonasteryOfSilence.class,
		PlainsOfDion.class,
		PolymorphingAngel.class,
		PolymorphingOnAttack.class,
		PrisonGuards.class,
		RaidBossCancel.class,
		RandomSpawn.class,
		RangeGuard.class,
		Sandstorms.class,
		SilentValley.class,
		SummonPc.class,
		TreasureChest.class,
		TurekOrcs.class,
		VarkaKetra.class,
		WarriorFishingBlock.class,
		
		// Extras
		Chests.class,
		BrekaOrcOverlord.class,
		CryptsOfDisgrace.class,
		FieldOfWhispersSilence.class,
		KarulBugbear.class,
		LuckyPig.class,
		Mutation.class,
		OlMahumGeneral.class,
		TimakOrcOverlord.class,
		TimakOrcTroopLeader.class,
		TomlanKamos.class,
		WarriorMonk.class,
		ZombieGatekeepers.class,
	};
	
	public GroupTemplatesLoader()
	{
		loadScripts();
        _log.info("Group Templates loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
