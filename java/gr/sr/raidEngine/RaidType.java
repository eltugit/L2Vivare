package gr.sr.raidEngine;

public enum RaidType {
    WEAK("Weak"),
    STRONG("Strong"),
    SUPER_STRONG("Super Strong");

    private String name;

    private RaidType(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}
