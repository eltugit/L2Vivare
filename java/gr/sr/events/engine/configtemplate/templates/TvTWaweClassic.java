package gr.sr.events.engine.configtemplate.templates;

import gr.sr.events.engine.base.EventType;
import gr.sr.events.engine.configtemplate.ConfigTemplate;

public class TvTWaweClassic
        extends ConfigTemplate {
    private final SetConfig[] configs = new SetConfig[]{new SetConfig("killsForReward", "1"), new SetConfig("resDelay", "25"), new SetConfig("waweRespawn", "true"), new SetConfig("createParties", "true"), new SetConfig("maxPartySize", "10"), new SetConfig("teamsCount", "2"), new SetConfig("allowScreenScoreBar", "true"), new SetConfig("divideToTeamsMethod", "LevelOnly"), new SetConfig("balanceHealersInTeams", "true"), new SetConfig("minLvl", "20"), new SetConfig("maxLvl", "85"), new SetConfig("minPlayers", "4"), new SetConfig("maxPlayers", "500"), new SetConfig("playersInInstance", "0"), new SetConfig("allowPotions", "false"), new SetConfig("removeBuffsOnStart", "true"), new SetConfig("removeBuffsOnRespawn", "false"), new SetConfig("notAllowedSkills", "0")};

    public String getName() {
        return "Team vs Team classic wawe";
    }

    public EventType getEventType() {
        return EventType.TvT;
    }

    public String getDescription() {
        return "Classic settings for a regular TvT event, with wawe-style spawn. Don't forget to setup apropriate InstanceTypes for your server, to make sure all players can play in a balanced event.";
    }

    public SetConfig[] getConfigs() {
        return this.configs;
    }
}


