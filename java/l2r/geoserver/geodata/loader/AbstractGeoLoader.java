package l2r.geoserver.geodata.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public abstract class AbstractGeoLoader implements GeoLoader {
    static final Logger log = LoggerFactory.getLogger(AbstractGeoLoader.class);
    private static final Pattern PATTERN = Pattern.compile("([_|\\.]){1}");


    @Override
    public boolean isAcceptable(final File file) {
        if (!file.exists()) {
            log.info("Geo Engine: File " + file.getName() + " was not loaded!!! Reason: file doesn't exists.");
            return false;
        }
        if (file.isDirectory()) {
            log.info("Geo Engine: File " + file.getName() + " was not loaded!!! Reason: file is directory.");
            return false;
        }
        if (file.isHidden()) {
            log.info("Geo Engine: File " + file.getName() + " was not loaded!!! Reason: file is hidden.");
            return false;
        }
        if (file.length() > 2147483647L) {
            log.info("Geo Engine: File " + file.getName() + " was not loaded!!! Reason: file is to big.");
            return false;
        }
        if (!this.getPattern().matcher(file.getName()).matches()) {
            if (log.isDebugEnabled()) {
                log.info(this.getClass().getSimpleName() + ": can't load file: " + file.getName() + "!!! Reason: pattern missmatch");
            }
            return false;
        }
        return true;
    }

    @Override
    public GeoFileInfo readFile(final File file) {
        log.info(this.getClass().getSimpleName() + ": loading geodata file: " + file.getName());
        byte[] data = null;
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            data = new byte[fileInputStream.available()];
            if (fileInputStream.read(data) != data.length) {
                log.warn("Not fully readed file?");
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex2) {
            ex2.printStackTrace();
            return null;
        }
        final GeoFileInfo b2 = this.createGeoFileInfo(file);
        b2.setData(this.parse(this.convert(data)));
        return b2;
    }

    protected GeoFileInfo createGeoFileInfo(final File file) {
        final GeoFileInfo b = new GeoFileInfo();
        try (final Scanner scanner = new Scanner(file.getName())) {
            scanner.useDelimiter(PATTERN);
            final int nextInt = scanner.nextInt();
            final int nextInt2 = scanner.nextInt();
            b.setX(nextInt);
            b.setY(nextInt2);
        } catch (Exception ex) {
        }
        return b;
    }

    protected abstract byte[][] parse(final byte[] data);

    public abstract Pattern getPattern();

    public abstract byte[] convert(final byte[] data);

}
