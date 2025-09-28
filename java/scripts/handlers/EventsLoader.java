package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.events.CharacterBirthday.CharacterBirthday;
import scripts.events.GiftOfVitality.GiftOfVitality;
import scripts.events.NewEra.NewEra;
import scripts.events.SquashEvent.SquashEvent;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class EventsLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		CharacterBirthday.class,
		
		// Disabled by default events
		// FreyaCelebration.class,
		GiftOfVitality.class,
		// HeavyMedal.class,
		// LoveYourGatekeeper.class,
		// MasterOfEnchanting.class,
		// SavingSanta.class,
		SquashEvent.class,
		NewEra.class,
		// TheValentineEvent.class,
	};
	
	public EventsLoader()
	{
		loadScripts();
		_log.info("Events loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
