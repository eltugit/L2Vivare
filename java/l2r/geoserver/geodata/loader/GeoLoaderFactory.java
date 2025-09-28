package l2r.geoserver.geodata.loader;

import java.io.File;

public class GeoLoaderFactory {
    private static GeoLoaderFactory instance;
    private final GeoLoader[] geoLoaders;

    public static GeoLoaderFactory getInstance() {
        if (GeoLoaderFactory.instance == null) {
            GeoLoaderFactory.instance = new GeoLoaderFactory();
        }
        return GeoLoaderFactory.instance;
    }

    private GeoLoaderFactory() {
        this.geoLoaders = new GeoLoader[]{new L2JGeoLoader(), new OffGeoLoader()};
    }

    public GeoLoader getGeoLoader(final File file) {
        if (file == null) {
            return null;
        }
        for (final GeoLoader c : this.geoLoaders) {
            if (c.isAcceptable(file)) {
                return c;
            }
        }
        return null;
    }
}
