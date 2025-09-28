package l2r.geoserver.geodata.loader;

import java.io.File;

public interface GeoLoader {
    boolean isAcceptable(final File file);

    GeoFileInfo readFile(final File file);
}
