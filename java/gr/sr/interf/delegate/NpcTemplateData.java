package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.INpcTemplateData;
import l2r.gameserver.data.sql.NpcTable;
import l2r.gameserver.model.L2Spawn;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.templates.L2NpcTemplate;

public class NpcTemplateData
        implements INpcTemplateData {
    private final L2NpcTemplate _template;
    private String _spawnName = null;
    private String _spawnTitle = null;

    public NpcTemplateData(int id) {
        this._template = NpcTable.getInstance().getTemplate(id);
    }

    public void setSpawnName(String name) {
        this._spawnName = name;
    }

    public void setSpawnTitle(String title) {
        this._spawnTitle = title;
    }

    public boolean exists() {
        return (this._template != null);
    }

    public NpcData doSpawn(int x, int y, int z, int ammount, int instanceId) {
        return doSpawn(x, y, z, ammount, 0, instanceId);
    }

    public NpcData doSpawn(int x, int y, int z, int ammount, int heading, int instanceId) {
        return doSpawn(x, y, z, ammount, heading, 0, instanceId);
    }

    public NpcData doSpawn(int x, int y, int z, int ammount, int heading, int respawn, int instanceId) {
        if (this._template == null) {
            return null;
        }
        try {
            L2Spawn spawn = new L2Spawn(this._template);
            spawn.setX(x);
            spawn.setY(y);
            spawn.setZ(z);
            spawn.setAmount(1);
            spawn.setHeading(heading);
            spawn.setRespawnDelay(respawn);
            spawn.setInstanceId(instanceId);
            L2Npc npc = spawn.doSpawn();
            NpcData npcData = new NpcData(npc);
            boolean update = false;
            if (this._spawnName != null) {
                npc.setName(this._spawnName);
                update = true;
            }
            if (this._spawnTitle != null) {
                npc.setTitle(this._spawnTitle);
                update = true;
            }
            if (update) {
                npcData.broadcastNpcInfo();
            }
            return npcData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


