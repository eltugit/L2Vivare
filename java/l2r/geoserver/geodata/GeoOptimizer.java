package l2r.geoserver.geodata;

import l2r.Config;
import l2r.gameserver.model.L2World;
import l2r.log.filter.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class GeoOptimizer {
    protected static final Logger log;
    public static int[][][] checkSums;
    private static final byte version = 1;

    public static BlockLink[] loadBlockMatches(final String pathname) {
        final File file = new File(pathname);
        if (!file.exists()) {
            Log.warning("files not exists");
            return null;
        }
        try {
            final FileChannel channel = new RandomAccessFile(file, "r").getChannel();
            final int n = (int) ((channel.size() - 1L) / 6L);
            final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());
            channel.close();
            map.order(ByteOrder.LITTLE_ENDIAN);
            if (map.get() != version) {
                return null;
            }
            final BlockLink[] array = new BlockLink[n];
            for (int i = 0; i < array.length; ++i) {
                array[i] = new BlockLink(map.getShort(), map.get(), map.get(), map.getShort());
            }
            return array;
        } catch (FileNotFoundException ex) {
            log.error("Block Matches File not Found! ", ex);
            return null;
        } catch (IOException ex2) {
            log.error("Error while loading Block Matches! ", ex2);
            return null;
        }
    }

    static {
        log = LoggerFactory.getLogger(GeoBlocksMatchFinder.class);
    }

    public static class GeoBlocksMatchFinder implements Runnable {
        private final int geoX;
        private final int bgeoY;
        private final int rx;
        private final int ry;
        private final int maxScanRegions;
        private final String fileName;

        public GeoBlocksMatchFinder(int _geoX, int _geoY, int _maxScanRegions) {
            this.geoX = _geoX;
            this.bgeoY = _geoY;
            this.rx = this.geoX + Config.GEO_X_FIRST;
            this.ry = this.bgeoY + Config.GEO_Y_FIRST;
            this.maxScanRegions = _maxScanRegions;
            this.fileName = Config.GEODATA_DIR + "/matches/" + this.rx + "_" + this.ry + ".matches";
        }

        private boolean exists() {
            return new File(this.fileName).exists();
        }

        private void saveToFile(final BlockLink[] array) {
            GeoOptimizer.log.info("Saving matches to: " + this.fileName);
            try {
                final File file = new File(this.fileName);
                if (file.exists()) {
                    file.delete();
                }
                final FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
                final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0L, array.length * 6 + 1);
                map.order(ByteOrder.LITTLE_ENDIAN);
                map.put(version);
                for (final BlockLink a : array) {
                    map.putShort((short) a.blockIndex);
                    map.put(a.linkMapX);
                    map.put(a.linkMapY);
                    map.putShort((short) a.linkBlockIndex);
                }
                channel.close();
            } catch (FileNotFoundException ex) {
                GeoOptimizer.log.error("Geodata File not found while saving! ", ex);
            } catch (IOException ex2) {
                GeoOptimizer.log.error("Error while Saving Geodata File! ", ex2);
            }
        }

        private void saveToFile(final int[] array, final int mapX, final int mapY, final List<BlockLink> list, final boolean[] notready) {
            final int[] next_checksums = checkSums[mapX][mapY];
            if (next_checksums == null) {
                return;
            }
            for (int i = 0; i < 65536; ++i) {
                if (notready[i]) {
                    for (int j = (next_checksums == array) ? (i + 1) : 0; j < 65536; ++j) {
                        if (array[i] == next_checksums[j] && GeoEngine.compareGeoBlocks(this.geoX, this.bgeoY, i, mapX, mapY, j)) {
                            list.add(new BlockLink(i, (byte) mapX, (byte) mapY, j));
                            notready[i] = false;
                            break;
                        }
                    }
                }
            }
        }

        private BlockLink[] gen() {
            GeoOptimizer.log.info("Searching matches for " + this.rx + "_" + this.ry);
            final long currentTimeMillis = System.currentTimeMillis();
            final boolean[] array = new boolean[65536];
            for (int i = 0; i < 65536; ++i) {
                array[i] = true;
            }
            final ArrayList<BlockLink> list = new ArrayList<BlockLink>();
            final int[] array2 = checkSums[this.geoX][this.bgeoY];
            int n = 0;
            for (int j = this.geoX; j < L2World.WORLD_SIZE_X; ++j) {
                for (int k = (j == this.geoX) ? this.bgeoY : 0; k < L2World.WORLD_SIZE_Y; ++k) {
                    this.saveToFile(array2, j, k, list, array);
                    ++n;
                    if (this.maxScanRegions > 0 && this.maxScanRegions == n) {
                        return list.toArray(new BlockLink[list.size()]);
                    }
                }
            }
            GeoOptimizer.log.info("Founded " + list.size() + " matches for " + this.rx + "_" + this.ry + " in " + (System.currentTimeMillis() - currentTimeMillis) / 1000.0f + "s");
            return list.toArray(new BlockLink[list.size()]);
        }

        @Override
        public void run() {
            if (!this.exists()) {
                this.saveToFile(this.gen());
            }
        }
    }

    public static class CheckSumLoader implements Runnable {
        private final int geoX, geoY, rx, ry;
        private final byte[][][] region;
        private final String fileName;

        public CheckSumLoader(int _geoX, int _geoY, byte[][][] _region) {
            super();
            geoX = _geoX;
            geoY = _geoY;
            rx = geoX + Config.GEO_X_FIRST;
            ry = _geoY + Config.GEO_Y_FIRST;
            region = _region;
            fileName = "geodata/checksum/" + rx + "_" + ry + ".crc";
        }

        private boolean loadFromFile() {
            File GeoCrc = new File(Config.DATAPACK_ROOT, fileName);
            if (!GeoCrc.exists())
                return false;
            try {
                @SuppressWarnings("resource")
                FileChannel roChannel = new RandomAccessFile(GeoCrc, "r").getChannel();
                if (roChannel.size() != GeoEngine.BLOCKS_IN_MAP * 4) {
                    roChannel.close();
                    return false;
                }

                ByteBuffer buffer = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, roChannel.size());
                roChannel.close();
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                int[] _checkSums = new int[GeoEngine.BLOCKS_IN_MAP];
                for (int i = 0; i < GeoEngine.BLOCKS_IN_MAP; i++)
                    _checkSums[i] = buffer.getInt();
                checkSums[geoX][geoY] = _checkSums;
                return true;

            } catch (FileNotFoundException e) {
                log.error("Geodata File not Found! ", e);
                return false;
            } catch (IOException e) {
                log.error("Error while loading Geodata File", e);
                return false;
            }
        }

        @SuppressWarnings("resource")
        private void saveToFile() {
            log.info("Saving checksums to: " + fileName);
            FileChannel wChannel;
            try {
                File f = new File(Config.DATAPACK_ROOT, fileName);
                if (f.exists())
                    f.delete();
                wChannel = new RandomAccessFile(f, "rw").getChannel();
                ByteBuffer buffer = wChannel.map(FileChannel.MapMode.READ_WRITE, 0, GeoEngine.BLOCKS_IN_MAP * 4);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                int[] _checkSums = checkSums[geoX][geoY];
                for (int i = 0; i < GeoEngine.BLOCKS_IN_MAP; i++)
                    buffer.putInt(_checkSums[i]);
                wChannel.close();
            } catch (FileNotFoundException e) {
                log.error("Geodata file not Found! ", e);
            } catch (IOException e) {
                log.error("Error while loading Geodata File", e);
            }
        }

        private void gen() {
            log.info("Generating checksums for " + rx + "_" + ry);
            int[] _checkSums = new int[GeoEngine.BLOCKS_IN_MAP];
            CRC32 crc32 = new CRC32();
            for (int i = 0; i < GeoEngine.BLOCKS_IN_MAP; i++) {
                crc32.update(region[i][0]);
                _checkSums[i] = (int) (crc32.getValue() ^ 0xFFFFFFFF);
                crc32.reset();
            }
            checkSums[geoX][geoY] = _checkSums;
        }

        @Override
        public void run() {
            if (!loadFromFile()) {
                gen();
                saveToFile();
            }
        }
    }

    public static class BlockLink {
        public final int blockIndex;
        public final int linkBlockIndex;
        public final byte linkMapX;
        public final byte linkMapY;

        public BlockLink(short _blockIndex, byte _linkMapX, byte _linkMapY, short _linkBlockIndex) {
            blockIndex = _blockIndex & 0xFFFF;
            linkMapX = _linkMapX;
            linkMapY = _linkMapY;
            linkBlockIndex = _linkBlockIndex & 0xFFFF;
        }

        public BlockLink(int _blockIndex, byte _linkMapX, byte _linkMapY, int _linkBlockIndex) {
            blockIndex = _blockIndex & 0xFFFF;
            linkMapX = _linkMapX;
            linkMapY = _linkMapY;
            linkBlockIndex = _linkBlockIndex & 0xFFFF;
        }
    }
}
