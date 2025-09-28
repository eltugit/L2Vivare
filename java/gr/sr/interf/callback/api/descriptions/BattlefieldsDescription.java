package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class BattlefieldsDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs) {
        String text = getInt(configs, "teamsCount") + " teams fighting against each other. ";
        text = text + "The goal of this event is to capture all towers. ";
        text = text + "In order to capture a base, you need to have more players from your team near the base than the opposite team, for a certain ammount of time, ";
        text = text + "after one team captures a base , the tower will stay captured, even though there won't be any player from the owning team arround it, unless";
        text = text + " another team steals the ownage of the tower from the first team. ";
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


