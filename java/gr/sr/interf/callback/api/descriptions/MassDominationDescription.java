package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class MassDominationDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = getInt(configs, "teamsCount") + " teams fighting against each other. ";
        text = text + "There are " + getInt(configs, "countOfZones") + " zones, each represented by an NPC. ";
        text = text + "In order to gain a score, your team must own at least " + getInt(configs, "zonesToOwnToScore") + " zones. ";
        text = text + "To own a zone, your team must get close to each of these zones and kill all other enemies standing near the zone too. ";
        if (getInt(configs, "killsForReward") > 0) {
            text = text + "At least " + getInt(configs, "killsForReward") + " kill(s) is required to receive a reward. ";
        }
        if (getInt(configs, "scoreForReward") > 0) {
            text = text + "At least " + getInt(configs, "scoreForReward") + " score (obtained when your team owns the zone and you stand near it) is required to receive a reward. ";
        }
        if (getBoolean(configs, "waweRespawn")) {
            text = text + "Dead players are resurrected by an advanced wawe-spawn engine each " + getInt(configs, "resDelay") + " seconds. ";
        } else {
            text = text + "If you die, you will get resurrected in " + getInt(configs, "resDelay") + " seconds. ";
        }
        if (getBoolean(configs, "createParties")) {
            text = text + "The event automatically creates parties on start.";
        }
        return text;
    }
}


