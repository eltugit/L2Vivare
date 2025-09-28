package l2r.geoserver.geodata.loader;

public class GeoFileInfo {
    private int x;
    private int y;
    private byte[][] data;

    public GeoFileInfo() {
    }

    public GeoFileInfo(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public void setX(final int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(final int y) {
        this.y = y;
    }

    public byte[][] getData() {
        return this.data;
    }

    public void setData(final byte[][] data) {
        this.data = data;
    }
}
