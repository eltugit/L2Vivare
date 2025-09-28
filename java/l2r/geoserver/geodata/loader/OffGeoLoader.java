package l2r.geoserver.geodata.loader;

import l2r.geoserver.geodata.GeoEngine;

import java.io.ByteArrayOutputStream;
import java.util.regex.Pattern;

public class OffGeoLoader extends AbstractGeoLoader {
    private static final Pattern a;

    @Override
    protected byte[][] parse(final byte[] array) {
        if (array.length <= 393234) {
            return null;
        }
        int i = 18;
        final byte[][] array2 = new byte[65536][];
        for (int j = 0; j < array2.length; ++j) {
            final short a = this.a(array[i + 1], array[i]);
            i += 2;
            if (a == 0) {
                array2[j] = new byte[]{GeoEngine.BLOCKTYPE_FLAT, array[i + 2], array[i + 3]};
                i += 4;
            } else if (a == 64) {
                if (i < array.length) {
                    final byte[] array3 = new byte[129];
                    array3[0] = GeoEngine.BLOCKTYPE_COMPLEX;
                    System.arraycopy(array, i, array3, 1, 128);
                    i += 128;
                    array2[j] = array3;
                } else {
                    log.warn("OffGeoLoader.parse BLOCKTYPE_COMPLEX(type == 0x0040) format corrupt, skipped. index=" + i + " data.length=" + array.length);
                }
            } else {
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(GeoEngine.BLOCKTYPE_MULTILEVEL);
                for (int k = 0; k < 64; ++k) {
                    final byte b = (byte) this.a(array[i + 1], array[i]);
                    i += 2;
                    byteArrayOutputStream.write(b);
                    for (int l = 0; l < b << 1; ++l) {
                        byteArrayOutputStream.write(array[i++]);
                    }
                }
                array2[j] = byteArrayOutputStream.toByteArray();
            }
        }
        return array2;
    }

    protected short a(final byte b, final byte b2) {
        return (short) (b << 8 | (b2 & 0xFF));
    }

    @Override
    public Pattern getPattern() {
        return OffGeoLoader.a;
    }

    @Override
    public byte[] convert(final byte[] array) {
        return array;
    }

    static {
        a = Pattern.compile("[\\d]{2}_[\\d]{2}_conv.dat");
    }
}
