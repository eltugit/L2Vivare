package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.npc.Teleports.CrumaTower.CrumaTower;
import scripts.ai.npc.Teleports.DelusionTeleport.DelusionTeleport;
import scripts.ai.npc.Teleports.ElrokiTeleporters.ElrokiTeleporters;
import scripts.ai.npc.Teleports.GatekeeperSpirit.GatekeeperSpirit;
import scripts.ai.npc.Teleports.GhostChamberlainOfElmoreden.GhostChamberlainOfElmoreden;
import scripts.ai.npc.Teleports.HuntingGroundsTeleport.HuntingGroundsTeleport;
import scripts.ai.npc.Teleports.Klein.Klein;
import scripts.ai.npc.Teleports.Klemis.Klemis;
import scripts.ai.npc.Teleports.MithrilMinesTeleporter.MithrilMinesTeleporter;
import scripts.ai.npc.Teleports.NewbieTravelToken.NewbieTravelToken;
import scripts.ai.npc.Teleports.NoblesseTeleport.NoblesseTeleport;
import scripts.ai.npc.Teleports.OracleTeleport.OracleTeleport;
import scripts.ai.npc.Teleports.PaganTeleporters.PaganTeleporters;
import scripts.ai.npc.Teleports.SeparatedSoul.SeparatedSoul;
import scripts.ai.npc.Teleports.StakatoNestTeleporter.StakatoNestTeleporter;
import scripts.ai.npc.Teleports.SteelCitadelTeleport.SteelCitadelTeleport;
import scripts.ai.npc.Teleports.StrongholdsTeleports.StrongholdsTeleports;
import scripts.ai.npc.Teleports.Survivor.Survivor;
import scripts.ai.npc.Teleports.TeleportToFantasy.TeleportToFantasy;
import scripts.ai.npc.Teleports.TeleportToRaceTrack.TeleportToRaceTrack;
import scripts.ai.npc.Teleports.TeleportToUndergroundColiseum.TeleportToUndergroundColiseum;
import scripts.ai.npc.Teleports.TeleportWithCharm.TeleportWithCharm;
import scripts.ai.npc.Teleports.ToIVortex.ToIVortex;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class TeleportersLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		CrumaTower.class,
		DelusionTeleport.class,
		ElrokiTeleporters.class,
		GatekeeperSpirit.class,
		GhostChamberlainOfElmoreden.class,
		HuntingGroundsTeleport.class,
		Klein.class,
		Klemis.class,
		MithrilMinesTeleporter.class,
		NewbieTravelToken.class,
		NoblesseTeleport.class,
		OracleTeleport.class,
		PaganTeleporters.class,
		SeparatedSoul.class,
		StakatoNestTeleporter.class,
		SteelCitadelTeleport.class,
		StrongholdsTeleports.class,
		Survivor.class,
		TeleportToFantasy.class,
		TeleportToRaceTrack.class,
		TeleportToUndergroundColiseum.class,
		TeleportWithCharm.class,
		ToIVortex.class,
	};
	
	public TeleportersLoader()
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
