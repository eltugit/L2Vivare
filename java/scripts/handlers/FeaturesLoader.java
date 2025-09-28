package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.features.SkillTransfer.SkillTransfer;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class FeaturesLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		SkillTransfer.class,
	};
	
	public FeaturesLoader()
	{
		loadScripts();
        _log.info("Features loaded!");
	}
	
	@Override
	public Class<?>[] getScripts()
	{
		return SCRIPTS;
	}
}
