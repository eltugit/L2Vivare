package gr.sr.l2j.delegate;

public interface ISkillData {
    int getId();

    String getName();

    int getLevel();

    boolean exists();

    String getSkillType();

    boolean isHealSkill();

    boolean isResSkill();

    int getHitTime();

    int getReuseDelay();
}


