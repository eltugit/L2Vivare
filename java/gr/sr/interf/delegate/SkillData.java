package gr.sr.interf.delegate;

import gr.sr.l2j.delegate.ISkillData;
import l2r.gameserver.model.skills.L2Skill;

public class SkillData
        implements ISkillData {
    private final L2Skill _skill;

    public SkillData(L2Skill cha) {
        this._skill = cha;
    }

    public SkillData(int skillId, int level) {
        this._skill = l2r.gameserver.data.xml.impl.SkillData.getInstance().getInfo(skillId, level);
    }

    public L2Skill getOwner() {
        return this._skill;
    }

    public String getName() {
        return this._skill.getName();
    }

    public int getLevel() {
        return this._skill.getLevel();
    }

    public boolean exists() {
        return (this._skill != null);
    }

    public String getSkillType() {
        return this._skill.getSkillType().toString();
    }

    public boolean isHealSkill() {
        if (getSkillType().equals("BALANCE_LIFE") || getSkillType().equals("CPHEAL_PERCENT") || getSkillType().equals("COMBATPOINTHEAL") || getSkillType().equals("CPHOT") || getSkillType().equals("HEAL") || getSkillType().equals("HEAL_PERCENT") || getSkillType().equals("HEAL_STATIC") || getSkillType().equals("HOT") || getSkillType().equals("MANAHEAL") || getSkillType().equals("MANAHEAL_PERCENT") || getSkillType().equals("MANARECHARGE") || getSkillType().equals("MPHOT") || getSkillType().equals("MANA_BY_LEVEL")) {
            return true;
        }
        return false;
    }

    public boolean isResSkill() {
        if (getSkillType().equals("RESURRECT")) {
            return true;
        }
        return false;
    }

    public int getHitTime() {
        return this._skill.getHitTime();
    }

    public int getReuseDelay() {
        return this._skill.getReuseDelay();
    }

    public int getId() {
        return this._skill.getId();
    }
}


