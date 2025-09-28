package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.custom.EchoCrystals.EchoCrystals;
import scripts.custom.FifthAnniversary.FifthAnniversary;
import scripts.custom.NewbieCoupons.NewbieCoupons;
import scripts.custom.NpcLocationInfo.NpcLocationInfo;
import scripts.custom.PinsAndPouchUnseal.PinsAndPouchUnseal;
import scripts.custom.RaidbossInfo.RaidbossInfo;
import scripts.custom.ShadowWeapons.ShadowWeapons;
import scripts.custom.Validators.SubClassSkills;
import scripts.custom.events.Wedding.Wedding;
import scripts.handlers.custom.CustomAnnouncePkPvP;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class CustomsLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		CustomAnnouncePkPvP.class,
		// AutoAdenaToGoldBar.class,
		EchoCrystals.class,
		FifthAnniversary.class,
		NewbieCoupons.class,
		NpcLocationInfo.class,
		PinsAndPouchUnseal.class,
		RaidbossInfo.class,
		ShadowWeapons.class,
		SubClassSkills.class,
		Wedding.class,
	};
	
	public CustomsLoader()
	{
		loadScripts();
        _log.info("Custom Loader loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
