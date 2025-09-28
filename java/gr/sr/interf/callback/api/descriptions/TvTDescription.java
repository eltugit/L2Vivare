package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class TvTDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = getInt(configs, "teamsCount") + " teams fighting against each other. ";
        text = text + "Gain score by killing your opponents";
        if (getInt(configs, "killsForReward") > 0) {
            text = text + " (at least " + getInt(configs, "killsForReward") + " kill(s) is required to receive a reward)";
        }
        if (getBoolean(configs, "waweRespawn")) {
            text = text + " and dead players are resurrected by an advanced wawe-spawn engine each " + getInt(configs, "resDelay") + " seconds";
        } else {
            text = text + " and if you die, you will be resurrected in " + getInt(configs, "resDelay") + " seconds";
        }
        if (getBoolean(configs, "createParties")) {
            text = text + ". The event automatically creates parties on start";
        }
        text = text + ".";
        return text;
    }
}


