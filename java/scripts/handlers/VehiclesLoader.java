package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.vehicles.BoatGludinRune;
import scripts.vehicles.BoatInnadrilTour;
import scripts.vehicles.BoatRunePrimeval;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class VehiclesLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
//		BoatGiranTalking.class,
//		BoatGludinRune.class,
//		BoatInnadrilTour.class,
//		BoatRunePrimeval.class,
//		BoatTalkingGludin.class
	};
	
	public VehiclesLoader()
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
