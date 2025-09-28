package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.sunriseNpc.AchievementManager.AchievementManager;
import scripts.ai.sunriseNpc.BetaManager.BetaManager;
import scripts.ai.sunriseNpc.CasinoManager.CasinoManager;
import scripts.ai.sunriseNpc.CastleManager.CastleManager;
import scripts.ai.sunriseNpc.DelevelManager.DelevelManager;
import scripts.ai.sunriseNpc.GrandBossManager.GrandBossManager;
import scripts.ai.sunriseNpc.NoblesseManager.NoblesseManager;
import scripts.ai.sunriseNpc.PointsManager.PointsManager;
import scripts.ai.sunriseNpc.PremiumManager.PremiumManager;
import scripts.ai.sunriseNpc.ReportManager.ReportManager;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public class SunriseNpcsLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		AchievementManager.class,
		BetaManager.class,
		CasinoManager.class,
		CastleManager.class,
		DelevelManager.class,
		GrandBossManager.class,
		NoblesseManager.class,
		PointsManager.class,
		PremiumManager.class,
		ReportManager.class,
	};
	
	public SunriseNpcsLoader()
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
