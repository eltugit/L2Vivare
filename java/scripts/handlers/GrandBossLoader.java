package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.grandboss.Antharas.Antharas;
import scripts.ai.grandboss.Baium.Baium;
import scripts.ai.grandboss.*;
import scripts.ai.grandboss.Sailren.Sailren;
import scripts.ai.grandboss.Valakas.Valakas;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class GrandBossLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		Beleth.class,
		Core.class,
		Orfen.class,
		QueenAnt.class,
		VanHalter.class,
		Antharas.class,
		Baium.class,
		Sailren.class,
		Valakas.class,
	};
	
	public GrandBossLoader()
	{
		loadScripts();
        _log.info("GrandBosses loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
