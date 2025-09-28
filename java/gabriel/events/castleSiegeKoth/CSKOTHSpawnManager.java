package gabriel.events.castleSiegeKoth;


import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.Location;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;
import l2r.util.Rnd;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Gabriel Costa Souza
 * Discord: Gabriel 'GCS'#2589
 * Skype - email: gabriel_costa25@hotmail.com
 * website: l2jgabdev.com
 */
public class CSKOTHSpawnManager {
    public static final Logger _log = Logger.getLogger(CSKOTHSpawnManager.class.getName());

    public L2Npc addSpawn(int npcId, L2Character cha) {
        return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0, false);
    }

    /**
     * Add a temporary (quest) spawn
     * Return instance of newly spawned npc
     * with summon animation
     */
    public L2Npc addSpawn(int npcId, L2Character cha, boolean isSummonSpawn) {
        return addSpawn(npcId, cha.getX(), cha.getY(), cha.getZ(), cha.getHeading(), false, 0, isSummonSpawn);
    }

    public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffSet,
                          long despawnDelay) {
        return addSpawn(npcId, x, y, z, heading, randomOffSet, despawnDelay, false);
    }

    /**
     * @param npcId
     * @param loc
     * @param randomOffSet
     * @param despawnDelay
     * @return
     */
    public L2Npc addSpawn(int npcId, Location loc, boolean randomOffSet, long despawnDelay) {
        return addSpawn(npcId, loc.getX(), loc.getY(), loc.getZ(), loc.getHeading(), randomOffSet, despawnDelay, false, 0);
    }

    public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset,
                          long despawnDelay, boolean isSummonSpawn) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, 0);
    }

    public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset,
                          long despawnDelay, boolean isSummonSpawn, int instanceId) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, instanceId, -1);
    }

    public L2Npc addSpawn(int npcId, int x, int y, int z, int heading, boolean randomOffset,
                          long despawnDelay, boolean isSummonSpawn, int instanceId, int onKillDelay) {
        //sometimes (for timed addspawn) when the spawn is called the instance not exists anymore
        //if (instanceId != 0 && !InstanceManager.getInstance().instanceExist(instanceId))
        //{
        //	return null;
        //}

        L2Npc result = null;
        try {
            L2NpcTemplate template = NpcTable.getInstance().getTemplate(npcId);
            if (template != null) {
                // Sometimes, even if the quest script specifies some xyz (for example npc.getX() etc) by the time the code
                // reaches here, xyz have become 0!  Also, a questdev might have purposely set xy to 0,0...however,
                // the spawn code is coded such that if x=y=0, it looks into location for the spawn loc!  This will NOT work
                // with quest spawns!  For both of the above cases, we need a fail-safe spawn.  For this, we use the
                // default spawn location, which is at the player's loc.
                if ((x == 0) && (y == 0)) {
                    _log.log(Level.SEVERE, "Failed to adjust bad locks for quest spawn!  Spawn aborted!");
                    return null;
                }
                if (randomOffset) {
                    int offset;

                    offset = Rnd.get(2); // Get the direction of the offset
                    if (offset == 0) {
                        offset = -1;
                    } // make offset negative
                    offset *= Rnd.get(50, 100);
                    x += offset;

                    offset = Rnd.get(2); // Get the direction of the offset
                    if (offset == 0) {
                        offset = -1;
                    } // make offset negative
                    offset *= Rnd.get(50, 100);
                    y += offset;
                }
                L2Spawn spawn = new L2Spawn(template);
                spawn.setInstanceId(instanceId);
                spawn.setHeading(heading);
                spawn.setLocation(new Location(x, y, z + 20));
                spawn.stopRespawn();
                result = spawn.spawnOne(isSummonSpawn);

                if (despawnDelay > 0)
                    result.scheduleDespawn(despawnDelay);

                return result;
            }
        } catch (Exception e1) {
            _log.warning("Could not spawn Npc " + npcId);
        }

        return null;
    }
}
