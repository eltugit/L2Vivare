package gr.sr.javaBuffer;

public class BuffsInstance {
    private int id;
    private int level;
    private int customLevel;
    private String name;
    private String description;
    private BufferMenuCategories category;

    public BuffsInstance() {
    }

    public BuffsInstance(int id, int level, int customLevel, String name, String desc, BufferMenuCategories category) {
        if (customLevel == 0) {
            customLevel = level;
        }

        this.id = id;
        this.level = level;
        this.customLevel = customLevel;
        this.name = name;
        this.description = desc;
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public int getCustomLevel() {
        return this.customLevel;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public BufferMenuCategories getCategory() {
        return this.category;
    }
}
