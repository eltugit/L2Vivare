package l2r.geoserver.geodata.geometry;




public class Point3D extends Point2D {
    public static final Point3D[] EMPTY_ARRAY;
    public int z;

    public Point3D() {
    }

    public Point3D(int x, int y, int z) {
        super(x, y);
        this.z = z;
    }


    public int getZ() {
        return this.z;
    }

    @Override
    public Point3D clone() {
        return new Point3D(this.x, this.y, this.z);
    }

    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o.getClass() == this.getClass() && this.equals((Point3D) o));
    }

    public boolean equals(Point3D p) {
        return equals(p.x, p.y, p.z);
    }


    public boolean equals(int x, int y, int z) {
        return (this.x == x) && (this.y == y) && (this.z == z);
    }


    @Override
    public String toString() {
        return "[x: " + this.x + " y: " + this.y + " z: " + this.z + "]";
    }

    static {
        EMPTY_ARRAY = new Point3D[0];
    }
}
