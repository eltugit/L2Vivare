package l2r.geoserver.model;


import l2r.geoserver.geodata.geometry.Shape;


public interface GeoCollision {
    Shape getShape();

    byte[][] getGeoAround();

    void setGeoAround(final byte[][] geo);

    boolean isConcrete();
}
