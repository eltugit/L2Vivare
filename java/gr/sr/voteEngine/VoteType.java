package gr.sr.voteEngine;

import gr.sr.configsEngine.configs.impl.GetRewardVoteSystemConfigs;

public enum VoteType {
    TOPCO(GetRewardVoteSystemConfigs.ENABLE_CHECK_TOPCO, GetRewardVoteSystemConfigs.TOP_LINK, "reward/VoteCheck.php?id=" + GetRewardVoteSystemConfigs.TOP_SERVER_ID + "&ip=%ip%", "TRUE"),
//    TOPZONE(GetRewardVoteSystemConfigs.ENABLE_CHECK_TOPZONE, GetRewardVoteSystemConfigs.TOPZONE_LINK, "api.php?API_KEY=" + GetRewardVoteSystemConfigs.TOPZONE_API_KEY + "&SERVER_ID=" + GetRewardVoteSystemConfigs.TOPZONE_SERVER_ID + "&IP=%ip%", "TRUE"),
    TOPZONE(GetRewardVoteSystemConfigs.ENABLE_CHECK_TOPZONE, GetRewardVoteSystemConfigs.TOPZONE_LINK, "/v1/vote?token=" + GetRewardVoteSystemConfigs.TOPZONE_API_KEY + "&ip=%ip%", "TRUE"),
    HOPZONE(GetRewardVoteSystemConfigs.ENABLE_CHECK_HOPZONE, GetRewardVoteSystemConfigs.HOPZONE_LINK, "lineage2/vote?token=" + GetRewardVoteSystemConfigs.HOPZONE_HRA_TOKEN + "&ip_address=%ip%", "true"),
    NETWORK(GetRewardVoteSystemConfigs.ENABLE_CHECK_NETWORK, GetRewardVoteSystemConfigs.NETWORK_LINK, "index.php?a=in&u=" + GetRewardVoteSystemConfigs.NETWORK_SERVER_ID + "&ipc=%ip%", "1"),
    GAMEBYTES(GetRewardVoteSystemConfigs.ENABLE_CHECK_GAMEBYTES, GetRewardVoteSystemConfigs.GAMEBYTES_LINK, "index.php?a=in&u=" + GetRewardVoteSystemConfigs.GAMEBYTES_SERVER_ID + "&api_key=" + GetRewardVoteSystemConfigs.GAMEBYTES_API_KEY + "&ip_address=%ip%", "TRUE"),
    L2NET(GetRewardVoteSystemConfigs.ENABLE_CHECK_L2_NET, GetRewardVoteSystemConfigs.L2_NET_LINK, "pages/votecheck.php?id=" + GetRewardVoteSystemConfigs.L2_NET_SERVER_ID + "&ip=%ip%", "TRUE"),
    L2JBRASIL(GetRewardVoteSystemConfigs.ENABLE_CHECK_L2JBRASIL, GetRewardVoteSystemConfigs.L2JBRASIL_LINK, "votesystem/?ip=%ip%&username=" + GetRewardVoteSystemConfigs.L2JBRASIL_SERVER_ID, "<status>1</status>"),
    L2TOPSERVER(GetRewardVoteSystemConfigs.ENABLE_CHECK_L2TOPSERVER, GetRewardVoteSystemConfigs.L2TOPSERVER_LINK, "votes?token=" + GetRewardVoteSystemConfigs.L2TOPSERVER_API_KEY + "&ip=%ip%", "TRUE");

    private boolean enabled;
    private String link;
    private String voteLink;
    private String word;

    private VoteType(boolean enabled, String link, String voteLink, String word) {
        this.enabled = enabled;
        this.link = link;
        this.voteLink = voteLink;
        this.word = word;
    }

    public final boolean isEnabled() {
        return this.enabled;
    }

    public final String getLink() {
        return this.link;
    }

    public final String getVoteLink() {
        return this.link + this.voteLink;
    }

    public final String getWord() {
        return this.word;
    }
}
