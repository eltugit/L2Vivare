package gr.sr.playervalue;


import gr.sr.events.SunriseLoader;
import gr.sr.events.engine.EventConfig;
import gr.sr.interf.PlayerEventInfo;
import gr.sr.playervalue.criteria.*;

import java.util.LinkedList;
import java.util.List;

public class PlayerValueEngine {
    private final List<ICriteria> criterias = new LinkedList<>();

    public PlayerValueEngine() {
        load();
        SunriseLoader.debug("Loaded PlayerValue engine.");
    }

    private void load() {
        this.criterias.add(GearScore.getInstance());
        this.criterias.add(PlayerClass.getInstance());
        this.criterias.add(PlayerLevel.getInstance());
        this.criterias.add(PlayerSkills.getInstance());
    }

    public void addCriteria(ICriteria c) {
        this.criterias.add(c);
    }

    public int getPlayerValue(PlayerEventInfo player) {
        if (!EventConfig.getInstance().getGlobalConfigBoolean("GearScore", "enableGearScore")) {
            return 0;
        }
        int value = 0;
        for (ICriteria i : this.criterias) {
            value += i.getPoints(player);
        }
        return value;
    }

    public static final PlayerValueEngine getInstance() {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder {
        protected static final PlayerValueEngine _instance = new PlayerValueEngine();
    }
}


