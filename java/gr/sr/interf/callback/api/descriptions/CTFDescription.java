package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class CTFDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = "There are " + getInt(configs, "teamsCount") + " teams; in order to score you need to steal enemy team's flag and bring it back your team's base (to the flag holder). ";
        if (getInt(configs, "flagReturnTime") > -1) {
            text = text + "If you hold the flag and don't manage to score within " + (getInt(configs, "flagReturnTime") / 1000) + " seconds, the flag will be returned back to enemy's flag holder. ";
        }
        if (getBoolean(configs, "waweRespawn")) {
            text = text + "Dead players are resurrected by an advanced wawe-spawn engine each " + getInt(configs, "resDelay") + " seconds.";
        } else {
            text = text + "If you die, you will be resurrected in " + getInt(configs, "resDelay") + " seconds. ";
        }
        if (getBoolean(configs, "createParties")) {
            text = text + "The event automatically creates parties on start.";
        }
        return text;
    }
}


