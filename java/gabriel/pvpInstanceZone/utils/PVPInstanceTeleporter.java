package gabriel.pvpInstanceZone.utils;

import gabriel.pvpInstanceZone.ConfigPvPInstance.ConfigPvPInstance;
import gabriel.pvpInstanceZone.PvPZoneManager;
import l2r.gameserver.ThreadPoolManager;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.effects.L2Effect;
import l2r.gameserver.model.skills.L2Skill;
import l2r.util.Rnd;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class PVPInstanceTeleporter {
    private PVPInstanceTeleporter() {

    }

    protected static PVPInstanceTeleporter instance;

    public static PVPInstanceTeleporter getInstance() {
        if (instance == null)
            instance = new PVPInstanceTeleporter();
        return instance;
    }

    /**
     * Teleport player to/from instance
     *
     * @param player
     * @param loc
     * @param instanceId
     */
    public void teleportPlayer(L2PcInstance player, Location loc, int instanceId) {
        teleportPlayer(player, loc, instanceId, true);
    }

    /**
     * Teleport player to/from instance
     *
     * @param player
     * @param loc
     * @param instanceId
     * @param allowRandomOffset
     */
    public void teleportPlayer(L2PcInstance player, Location loc, int instanceId, boolean allowRandomOffset) {
        loc.setInstanceId(instanceId);
        player.teleToLocation(loc, allowRandomOffset);
    }

    public void healToMaxPvPInstance(L2PcInstance player) {
        player.setCurrentCp(player.getMaxCp());
        player.setCurrentHp(player.getMaxHp());
        player.setCurrentMp(player.getMaxMp());
    }

    public static ScheduledFuture<?> tpmScheduleGeneral(Runnable task, int time) {
        return ThreadPoolManager.getInstance().scheduleGeneral(task, time);
    }


    public void addToResurrectorPvPInstance(L2PcInstance player) {
        new ResurrectorTaskPvPInstance(player);
    }

    public class ResurrectorTaskPvPInstance implements Runnable {
        public L2PcInstance player;

        public ResurrectorTaskPvPInstance(L2PcInstance p) {
            player = p;
            tpmScheduleGeneral(this, ConfigPvPInstance.PVP_INSTANCE_RESS_DELAY * 1000);
        }

        @Override
        public void run() {
            if (player.getCurrentHp() > 0) {
                //player.sendMessage("No revival happend");
            } else {
                if (player.getInstanceId() != ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID)
                    return;
                if ((player != null) && (player != null)) {
                    player.doRevive();

                    healToMaxPvPInstance(player);
                    if (ConfigPvPInstance.ENABLE_PVP_INSTANCE_NOB_RES) {
                        L2Skill noblesse = SkillData.getInstance().getInfo(1323, 1);
                        noblesse.getEffects(player, player);
                    }
                    ;
                    // teleportPlayer(player, new Location(83289, 148610, -3408), ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
                    teleportPlayer(player, PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()].getSpawnLocs()[Rnd.get(PvPZoneManager.PVPINSTANCERESPAWNS[PvPZoneManager.getlocationindex()].getSpawnLocs().length)], ConfigPvPInstance.PVP_INSTANCE_INSTANCE_ID);
                    L2Effect[] effects = player.getAllEffects();

                    if (effects == null || effects.length == 0)
                        return;

                    for (L2Effect e : effects) {
                        if (e == null || !e.getSkill().isDebuff())
                            continue;
                        e.exit();
                    }
                }
            }

        }
    }
}
