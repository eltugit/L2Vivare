package l2r.geoserver.geodata.geometry;

public abstract class AbstractShape implements Shape {
    protected final Point3D max;
    protected final Point3D min;

    public AbstractShape() {
        this.max = new Point3D();
        this.min = new Point3D();
    }

    @Override
    public boolean isInside(int x, int y, int z) {
        return (min.z <= z) && (max.z >= z) && (isInside(x, y));
    }

    @Override
    public int getXmax() {
        return this.max.x;
    }

    @Override
    public int getXmin() {
        return this.min.x;
    }

    @Override
    public int getYmax() {
        return this.max.y;
    }

    @Override
    public int getYmin() {
        return this.min.y;
    }

    public AbstractShape setZmax(final int c) {
        this.max.z = c;
        return this;
    }

    public AbstractShape setZmin(final int c) {
        this.min.z = c;
        return this;
    }

    @Override
    public int getZmax() {
        return this.max.z;
    }

    @Override
    public int getZmin() {
        return this.min.z;
    }
}
