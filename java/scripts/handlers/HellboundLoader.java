package scripts.handlers;

import gr.sr.handler.ABLoader;
import l2r.Config;
import l2r.gameserver.handler.AdminCommandHandler;
import l2r.gameserver.handler.IAdminCommandHandler;
import l2r.gameserver.handler.IVoicedCommandHandler;
import l2r.gameserver.handler.VoicedCommandHandler;
import scripts.handlers.admincommandhandlers.AdminHellbound;
import scripts.handlers.voicedcommandhandlers.Hellbound;
import scripts.hellbound.AI.*;
import scripts.hellbound.AI.NPC.Bernarde.Bernarde;
import scripts.hellbound.AI.NPC.Budenka.Budenka;
import scripts.hellbound.AI.NPC.Buron.Buron;
import scripts.hellbound.AI.NPC.Deltuva.Deltuva;
import scripts.hellbound.AI.NPC.Falk.Falk;
import scripts.hellbound.AI.NPC.Galate.Galate;
import scripts.hellbound.AI.NPC.Hude.Hude;
import scripts.hellbound.AI.NPC.Jude.Jude;
import scripts.hellbound.AI.NPC.Kanaf.Kanaf;
import scripts.hellbound.AI.NPC.Kief.Kief;
import scripts.hellbound.AI.NPC.Natives.Natives;
import scripts.hellbound.AI.NPC.Quarry.Quarry;
import scripts.hellbound.AI.NPC.Shadai.Shadai;
import scripts.hellbound.AI.NPC.Solomon.Solomon;
import scripts.hellbound.AI.NPC.Warpgate.Warpgate;
import scripts.hellbound.AI.Zones.AnomicFoundry.AnomicFoundry;
import scripts.hellbound.AI.Zones.BaseTower.BaseTower;
import scripts.hellbound.AI.Zones.TowerOfInfinitum.TowerOfInfinitum;
import scripts.hellbound.AI.Zones.TowerOfNaia.TowerOfNaia;
import scripts.hellbound.AI.Zones.TullyWorkshop.TullyWorkshop;
import scripts.hellbound.HellboundEngine;
import scripts.hellbound.HellboundPointData;
import scripts.hellbound.HellboundSpawns;
import scripts.hellbound.Instances.DemonPrinceFloor.DemonPrinceFloor;
import scripts.hellbound.Instances.RankuFloor.RankuFloor;
import scripts.hellbound.Instances.UrbanArea.UrbanArea;
import scripts.quests.Q00130_PathToHellbound.Q00130_PathToHellbound;
import scripts.quests.Q00133_ThatsBloodyHot.Q00133_ThatsBloodyHot;

/**
 * Hellbound class-loader.
 * @author vGodFather
 */
public final class HellboundLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		// Commands
		AdminHellbound.class,
		Hellbound.class,
		// AIs
		Amaskari.class,
		Chimeras.class,
		DemonPrince.class,
		HellboundCore.class,
		Keltas.class,
		NaiaLock.class,
		OutpostCaptain.class,
		Ranku.class,
		RuinsGhosts.class,
		Slaves.class,
		Typhoon.class,
		// NPCs
		Bernarde.class,
		Budenka.class,
		Buron.class,
		Deltuva.class,
		Falk.class,
		Galate.class,
		Hude.class,
		Jude.class,
		Kanaf.class,
		Kief.class,
		Natives.class,
		Quarry.class,
		Shadai.class,
		Solomon.class,
		Warpgate.class,
		// Zones
		AnomicFoundry.class,
		BaseTower.class,
		TowerOfInfinitum.class,
		TowerOfNaia.class,
		TullyWorkshop.class,
		// Instances
		DemonPrinceFloor.class,
		UrbanArea.class,
		RankuFloor.class,
		// Quests
		Q00130_PathToHellbound.class,
		Q00133_ThatsBloodyHot.class,
	};
	
	@Override
	public void loadScripts()
	{
		final long startCache = System.currentTimeMillis();
		// Data
		HellboundPointData.getInstance();
		HellboundSpawns.getInstance();
		// Engine
		HellboundEngine.getInstance();
		for (Class<?> script : getScripts())
		{
			try
			{
				final Object instance = script.newInstance();
				if (instance instanceof IAdminCommandHandler)
				{
					AdminCommandHandler.getInstance().registerHandler((IAdminCommandHandler) instance);
				}
				else if (Config.L2JMOD_HELLBOUND_STATUS && (instance instanceof IVoicedCommandHandler))
				{
					VoicedCommandHandler.getInstance().registerHandler((IVoicedCommandHandler) instance);
				}
			}
			catch (Exception e)
			{
				_log.error(getClass().getSimpleName() + ": Failed loading " + script.getSimpleName() + ":" + e.getMessage());
			}
		}
		_log.info(getClass().getSimpleName() + " loaded. (GenTime: {} ms) ", (System.currentTimeMillis() - startCache));
	}
	
	public HellboundLoader()
	{
		loadScripts();
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
