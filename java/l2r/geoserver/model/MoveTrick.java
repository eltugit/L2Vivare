package l2r.geoserver.model;



import java.io.Serializable;


public final class MoveTrick implements Serializable {
    private static final long serialVersionUID = 8691569322628819033L;
    public final int _dist;
    public final int _height;

    public MoveTrick(final int dist, final int height) {
        this._dist = dist;
        this._height = height;
    }
}
