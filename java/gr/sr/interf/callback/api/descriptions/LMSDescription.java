package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class LMSDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = "This is a free-for-all event, don't expect any help from teammates. ";
        text = text + "This event has " + getInt(configs, "maxRounds") + " rounds. You can gain score by killing your opponents (1 kill = 1 score), but if you die, you won't get resurrected until the next round starts. ";
        text = text + "The player, who wins the round (when all other players are dead) receives additional " + getInt(configs, "scoreForRoundWinner") + " score points. ";
        if (getBoolean(configs, "antifeedProtection")) {
            text = text + "This event has a protection, which completely changes the appearance of all players and temporary removes their title and clan/ally crests. ";
        }
        if (getInt(configs, "killsForReward") > 0) {
            text = text + "In the end, you need at least " + getInt(configs, "killsForReward") + " kills to receive a reward.";
        }
        return text;
    }
}


