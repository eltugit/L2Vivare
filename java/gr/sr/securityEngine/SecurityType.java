package gr.sr.securityEngine;




public enum SecurityType {
    ACHIEVEMENT_SYSTEM("Achievement System"),
    AIO_ITEM("Aio Item"),
    AIO_ITEM_BUFFER("Aio Buffer"),
    AIO_NPC("Aio Npc"),
    NPC_BUFFER("Npc Buffer"),
    CUSTON_GATEKEEPER("Sunrise Gatekeeper"),
    DONATE_MANAGER("Donate Manager"),
    VOTE_SYSTEM("vote system"),
    ENCHANT_EXPLOIT("enchant system"),
    ITEM_NPC_BUFFER_METHODS("Item - npc buffer methods"),
    ANTIBOT_SYSTEM("antibot system"),
    COMMUNITY_SYSTEM("Community System");

    private String text;

    private SecurityType(String text) {
        this.text = text;
    }

    public final String getText() {
        return this.text;
    }
}
