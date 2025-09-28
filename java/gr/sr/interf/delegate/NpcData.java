package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.INpcData;
import l2r.gameserver.data.xml.impl.SkillData;
import l2r.gameserver.model.L2Object;
import l2r.gameserver.model.actor.L2Character;
import l2r.gameserver.model.actor.L2Npc;
import l2r.gameserver.model.actor.instance.L2PcInstance;
import l2r.gameserver.model.skills.L2Skill;
import l2r.gameserver.network.serverpackets.L2GameServerPacket;
import l2r.gameserver.network.serverpackets.MagicSkillUse;

import java.util.Collection;

public class NpcData extends CharacterData implements INpcData {
    private int _team;
    private boolean deleted = false;

    public NpcData(L2Npc npc) {
        super((L2Character) npc);
    }

    public void deleteMe() {
        if (!this.deleted) {
            ((L2Npc) this._owner).deleteMe();
        }
        this.deleted = true;
    }

    public ObjectData getObjectData() {
        return new ObjectData((L2Object) this._owner);
    }

    public void setName(String name) {
        this._owner.setName(name);
    }

    public void setTitle(String t) {
        this._owner.setTitle(t);
    }

    public int getNpcId() {
        return ((L2Npc) this._owner).getId();
    }

    public void setEventTeam(int team) {
        this._team = team;
    }

    public int getEventTeam() {
        return this._team;
    }

    public void broadcastNpcInfo() {
        Collection<L2PcInstance> plrs = this._owner.getKnownList().getKnownPlayers().values();
        for (L2PcInstance player : plrs) {
            ((L2Npc) this._owner).sendInfo(player);
        }
    }

    public void broadcastSkillUse(CharacterData owner, CharacterData target, int skillId, int level) {
        L2Skill skill = SkillData.getInstance().getInfo(skillId, level);
        if (skill != null) {
            getOwner().broadcastPacket((L2GameServerPacket) new MagicSkillUse(owner.getOwner(), target.getOwner(), skill.getId(), skill.getLevel(), skill.getHitTime(), skill.getReuseDelay()));
        }
    }
}


