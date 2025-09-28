package l2r.geoserver.geodata.geometry;

public class Circle extends AbstractShape {
    protected final Point2D c;
    protected final int radius;

    public Circle(final Point2D center, final int radius) {
        this.c = center;
        this.radius = radius;
        min.x = (c.x - radius);
        max.x = (c.x + radius);
        min.y = (c.y - radius);
        max.y = (c.y + radius);
    }

    public Circle(int x, int y, int radius) {
        this(new Point2D(x, y), radius);
    }


    @Override
    public Circle setZmax(final int z) {
        max.z = z;
        return this;
    }

    @Override
    public Circle setZmin(final int z) {
        min.z = z;
        return this;
    }

    @Override
    public boolean isInside(int x, int y) {
        return (x - c.x) * (c.x - x) + (y - c.y) * (c.y - y) <= radius * radius;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(this.c).append("{ radius: ").append(this.c).append("}");
        sb.append("]");
        return sb.toString();
    }
}
