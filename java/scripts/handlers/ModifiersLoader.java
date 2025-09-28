package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.modifier.*;
import scripts.ai.modifier.dropEngine.FortressReward;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class ModifiersLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		FlyingNpcs.class,
		NoChampionMobs.class,
		NoMovingNpcs.class,
		NonAttackingNpcs.class,
		NonLethalableNpcs.class,
		NonTalkingNpcs.class,
		NoRandomAnimation.class,
		NoRandomWalkMobs.class,
		RunningNpcs.class,
		SeeThroughSilentMove.class,
		
		// Drop Modifiers
		FortressReward.class,
	};
	
	public ModifiersLoader()
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
