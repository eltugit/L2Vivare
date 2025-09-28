package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class DMDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = "This is a free-for-all event, don't expect any help from teammates. Gain score by killing your opponents";
        text = text + " and if you die, you will be resurrected within " + getInt(configs, "resDelay") + " seconds. ";
        if (getBoolean(configs, "waweRespawn")) {
            text = text + "Also, wawe-spawn system ensures that all dead players are spawned in the same moment (but in different spots). ";
        }
        if (getBoolean(configs, "antifeedProtection")) {
            text = text + "This event has a protection, which completely changes the appearance of all players and temporary removes their title and clan/ally crests. ";
        }
        if (getInt(configs, "killsForReward") > 0) {
            text = text + "In the end, you need at least " + getInt(configs, "killsForReward") + " kills to receive a reward.";
        }
        return text;
    }
}


