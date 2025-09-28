package l2r.geoserver.geodata.geometry;




public class Point2D implements Cloneable {
    public static final Point2D[] EMPTY_ARRAY = new Point2D[0];
    public int x;
    public int y;

    public Point2D() {
    }

    public Point2D(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point2D clone() {
        return new Point2D(this.x, this.y);
    }

    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o.getClass() == this.getClass() && this.equals((Point2D) o));
    }

    public boolean equals(final Point2D point) {
        return this.equals(point.x, point.y);
    }

    public boolean equals(int n, int n2) {
        return this.x == n && this.y == n2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "[x: " + this.x + " y: " + this.y + "]";
    }

}
