package l2r.geoserver.geodata.loader;

import l2r.geoserver.geodata.GeoEngine;

import java.util.regex.Pattern;


public class L2JGeoLoader extends AbstractGeoLoader {
    private static final Pattern PATTERN;

    @Override
    protected byte[][] parse(final byte[] array) {
        if (array.length <= 196608) {
            return null;
        }
        final byte[][] array2 = new byte[65536][];
        int i = 0;
        for (int j = 0; j < array2.length; ++j) {
            final byte k = array[i];
            ++i;
            switch (k) {
                case GeoEngine.BLOCKTYPE_FLAT: {
                    array2[j] = new byte[]{k, array[i], array[i + 1]};
                    i += 2;
                    break;
                }
                case GeoEngine.BLOCKTYPE_COMPLEX: {
                    if (i < array.length) {
                        final byte[] array3 = new byte[129];
                        array3[0] = k;
                        System.arraycopy(array, i, array3, 1, 128);
                        i += 128;
                        array2[j] = array3;
                        break;
                    }
                    log.warn("L2JGeoLoader.parse BLOCKTYPE_COMPLEX format corrupt, skipped. index=" + i + " data.length=" + array.length);
                    break;
                }
                case GeoEngine.BLOCKTYPE_MULTILEVEL: {
                    final int n = i;
                    for (int l = 0; l < 64; ++l) {
                        i += (array[i] << 1) + 1;
                    }
                    final int n2 = i - n;
                    final byte[] array4 = new byte[n2 + 1];
                    array4[0] = k;
                    System.arraycopy(array, n, array4, 1, n2);
                    array2[j] = array4;
                    break;
                }
                default: {
                    log.error("GeoEngine: invalid block type: " + k);
                    break;
                }
            }
        }
        return array2;
    }

    @Override
    public Pattern getPattern() {
        return L2JGeoLoader.PATTERN;
    }

    @Override
    public byte[] convert(final byte[] array) {
        return array;
    }

    static {
        PATTERN = Pattern.compile("[\\d]{2}_[\\d]{2}.l2j");
    }
}
