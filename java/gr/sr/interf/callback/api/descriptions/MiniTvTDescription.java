package gr.sr.interf.callback.api.descriptions;

import gr.sr.events.engine.base.ConfigModel;
import gr.sr.events.engine.base.description.EventDescription;

import java.util.Map;

public class MiniTvTDescription
        extends EventDescription {
    public String getDescription(Map<String, ConfigModel> configs, int roundsCount, int teamsCount, int teamSize, int rejoinDelay, int timeLimit) {
        String text = "This is a team-based mini event. This event is similar to Party fights, but you don't need any party here - ";
        text = text + " the event will automatically put you to one of " + teamsCount + " teams, which will fight against each other. Each team has " + teamSize + " players.<br1> ";
        if (roundsCount > 1) {
            if (teamsCount == 2) {
                text = text + "The match has " + roundsCount + " rounds. Round ends when all players from one team are dead (they will be resurrected in start of the next round). ";
                text = text + "The winner of the match is in the end of all rounds the team, which won the most of rounds. ";
            } else {
                text = text + "The match has " + roundsCount + " rounds. Round ends when one team kills all it's opponents (dead players will be resurrected in start of the next round). ";
                text = text + "The winner of the match is in the end of all rounds the team, which won the most of rounds. ";
            }
        } else if (teamsCount == 2) {
            text = text + "This event has only one round. If you die, the event ends for you. ";
            text = text + "The match ends when all players of one team are dead. ";
        } else {
            text = text + "This event has only one round. If you die, the event ends for you. ";
            text = text + "The winner of the match is the team, who kills all it's opponents. ";
        }
        text = text + "Your opponents will be selected automatically and don't worry, there's a protection, which will ensure that you will always fight only players whose level is similar to yours. ";
        text = text + "If the match doesn't end within " + (timeLimit / 60000) + " minutes, it will be aborted automatically. ";
        text = text + "Also, after you visit this event, you will have to wait at least " + (rejoinDelay / 60000) + " minutes to join this event again. ";
        return text;
    }
}


