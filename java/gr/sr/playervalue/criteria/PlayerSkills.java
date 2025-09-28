package gr.sr.playervalue.criteria;


import gr.sr.events.engine.EventConfig;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.interf.delegate.SkillData;
import gr.sr.l2j.CallBack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSkills
        implements ICriteria {
    private final Map<Integer, Levels> _skills = new ConcurrentHashMap<>();

    public PlayerSkills() {
        loadData();
    }

    private void loadData() {
        this._skills.clear();
        try (Connection con = CallBack.getInstance().getOut().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT skillId, level, score FROM sunrise_playervalue_skills");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                int skillId = rset.getInt("skillId");
                int level = rset.getInt("level");
                int score = rset.getInt("score");
                if (this._skills.containsKey(Integer.valueOf(skillId))) {
                    ((Levels) this._skills.get(Integer.valueOf(skillId))).add(level, score);
                    continue;
                }
                this._skills.put(Integer.valueOf(skillId), new Levels(level, score));
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventConfig.getInstance().getGlobalConfig("GearScore", "enableGearScore").setValue("false");
        }
    }

    public int getScoreForSkill(int skillId, int level) {
        if (this._skills.containsKey(Integer.valueOf(skillId))) {
            return ((Levels) this._skills.get(Integer.valueOf(skillId))).get(level);
        }
        return 0;
    }

    public int getPoints(PlayerEventInfo player) {
        int points = 0;
        for (SkillData skill : player.getSkills()) {
            points += getScoreForSkill(skill.getId(), skill.getLevel());
        }
        return points;
    }

    public static final PlayerSkills getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final PlayerSkills _instance = new PlayerSkills();
    }

    private class Levels {
        public Map<Integer, Integer> levels = new ConcurrentHashMap<>();

        public Levels(int level, int points) {
            add(level, points);
        }

        public void add(int level, int points) {
            this.levels.put(Integer.valueOf(level), Integer.valueOf(points));
        }

        public int get(int level) {
            if (level == -1) {
                int top = 0;
                for (Integer element : this.levels.values()) {
                    int points = element.intValue();
                    if (points > top) {
                        top = points;
                    }
                }
                return top;
            }
            if (this.levels.containsKey(Integer.valueOf(level))) {
                return ((Integer) this.levels.get(Integer.valueOf(level))).intValue();
            }
            if (level >= 0) {
                level--;
                return get(level);
            }
            if (this.levels.containsKey(Integer.valueOf(-1))) {
                return ((Integer) this.levels.get(Integer.valueOf(-1))).intValue();
            }
            return 0;
        }
    }
}


