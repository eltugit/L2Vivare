package gr.sr.events.engine.mini.features;


import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.mini.EventMode;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.SkillData;

import java.util.Arrays;


public class SkillsFeature extends AbstractFeature
{
    private boolean disableSkills;
    private boolean allowResSkills;
    private boolean allowHealSkills;
    private int[] disabledSkills;
    
    public SkillsFeature(final EventType event, final PlayerEventInfo gm, String parametersString) {
        super(event);
        this.disableSkills = false;
        this.allowResSkills = false;
        this.allowHealSkills = false;
        this.disabledSkills = null;
        this.addConfig("DisableSkills", "If 'true', then all skills will be disabled for this mode. Put 'false' to enable them.", 1);
        this.addConfig("AllowResurrections", "Put 'false' to disable all resurrection-type skills. Put 'true' to enable them.", 1);
        this.addConfig("AllowHeals", "Put 'false' to disable all heal-type skills. Put 'true' to enable them. This config doesn't affect self-heals.", 1);
        this.addConfig("DisabledSkills", "Specify here which skills will be disabled for this mode. Write their IDs and separate by SPACE. Eg. <font color=LEVEL>50 150 556</font>. Put <font color=LEVEL>0</font> to disable this config.", 2);
        if (parametersString == null) {
            parametersString = "false,false,true,0";
        }
        this._params = parametersString;
        this.initValues();
    }
    
    @Override
    protected void initValues() {
        final String[] params = this.splitParams(this._params);
        try {
            this.disableSkills = Boolean.parseBoolean(params[0]);
            this.allowResSkills = Boolean.parseBoolean(params[1]);
            this.allowHealSkills = Boolean.parseBoolean(params[2]);
            final String[] splitted = params[3].split(" ");
            this.disabledSkills = new int[splitted.length];
            for (int i = 0; i < splitted.length; ++i) {
                this.disabledSkills[i] = Integer.parseInt(splitted[i]);
            }
            Arrays.sort(this.disabledSkills);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
    
    public boolean checkSkill(final PlayerEventInfo player, final SkillData skill) {
        return !this.disableSkills && (!skill.isResSkill() || this.allowResSkills) && (!skill.isHealSkill() || this.allowHealSkills) && Arrays.binarySearch(this.disabledSkills, skill.getId()) < 0;
    }
    
    @Override
    public boolean checkPlayer(final PlayerEventInfo player) {
        return true;
    }
    
    @Override
    public EventMode.FeatureType getType() {
        return EventMode.FeatureType.Skills;
    }
}
