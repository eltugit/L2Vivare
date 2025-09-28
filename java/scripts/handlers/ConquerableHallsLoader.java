package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.conquerablehalls.DevastatedCastle.DevastatedCastle;
import scripts.conquerablehalls.FortressOfResistance.FortressOfResistance;
import scripts.conquerablehalls.FortressOfTheDead.FortressOfTheDead;
import scripts.conquerablehalls.RainbowSpringsChateau.RainbowSpringsChateau;
import scripts.conquerablehalls.flagwar.BanditStronghold.BanditStronghold;
import scripts.conquerablehalls.flagwar.WildBeastReserve.WildBeastReserve;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ConquerableHallsLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
        BanditStronghold.class,
        WildBeastReserve.class,
		DevastatedCastle.class,
		FortressOfResistance.class,
		FortressOfTheDead.class,
		RainbowSpringsChateau.class,
	};

	public ConquerableHallsLoader()
	{
		loadScripts();
        _log.info("ConquerableHalls loaded!");
	}

	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
