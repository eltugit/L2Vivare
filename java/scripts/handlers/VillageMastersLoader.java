package scripts.handlers;

import gr.sr.handler.ABLoader;
import scripts.ai.npc.VillageMasters.Alliance.Alliance;
import scripts.ai.npc.VillageMasters.Clan.Clan;
import scripts.ai.npc.VillageMasters.DarkElvenChange1.DarkElvenChange1;
import scripts.ai.npc.VillageMasters.DarkElvenChange2.DarkElvenChange2;
import scripts.ai.npc.VillageMasters.DwarvenOccupationChange.DwarvenOccupationChange;
import scripts.ai.npc.VillageMasters.ElvenHumanBuffers2.ElvenHumanBuffers2;
import scripts.ai.npc.VillageMasters.ElvenHumanFighters1.ElvenHumanFighters1;
import scripts.ai.npc.VillageMasters.ElvenHumanFighters2.ElvenHumanFighters2;
import scripts.ai.npc.VillageMasters.ElvenHumanMystics1.ElvenHumanMystics1;
import scripts.ai.npc.VillageMasters.ElvenHumanMystics2.ElvenHumanMystics2;
import scripts.ai.npc.VillageMasters.FirstClassTransferTalk.FirstClassTransferTalk;
import scripts.ai.npc.VillageMasters.KamaelChange1.KamaelChange1;
import scripts.ai.npc.VillageMasters.KamaelChange2.KamaelChange2;
import scripts.ai.npc.VillageMasters.OrcOccupationChange1.OrcOccupationChange1;
import scripts.ai.npc.VillageMasters.OrcOccupationChange2.OrcOccupationChange2;
import scripts.ai.npc.VillageMasters.SubclassCertification.SubclassCertification;

/**
 * @author L2jSunrise Team
 * @Website www.l2jsunrise.com
 */
public final class VillageMastersLoader extends ABLoader
{
	private final Class<?>[] SCRIPTS =
	{
		Alliance.class,
		Clan.class,
		DarkElvenChange1.class,
		DarkElvenChange2.class,
		DwarvenOccupationChange.class,
		ElvenHumanBuffers2.class,
		ElvenHumanFighters1.class,
		ElvenHumanFighters2.class,
		ElvenHumanMystics1.class,
		ElvenHumanMystics2.class,
		FirstClassTransferTalk.class,
		KamaelChange1.class,
		KamaelChange2.class,
		OrcOccupationChange1.class,
		OrcOccupationChange2.class,
		SubclassCertification.class,
	};
	
	public VillageMastersLoader()
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
