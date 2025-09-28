package gr.sr.database.pool;

public enum ConnectionPoolTypes {
    HIKARICP("HIKARICP"),
    C3P0("C3P0"),
    BONECP("BONECP"),
    APACHE("APACHE");

    private String name;

    private ConnectionPoolTypes(String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}
