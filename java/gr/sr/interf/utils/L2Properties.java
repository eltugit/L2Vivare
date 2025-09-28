package gr.sr.interf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public final class L2Properties
        extends Properties {
    private static final long serialVersionUID = 1L;
    private static Logger _log = LoggerFactory.getLogger(L2Properties.class);

    public L2Properties() {
    }

    public L2Properties(String name) throws IOException {
        try (FileInputStream fis = new FileInputStream(name)) {
            load(fis);
        }
    }

    public L2Properties(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            load(fis);
        }
    }

    public L2Properties(InputStream inStream) throws IOException {
        load(inStream);
    }

    public L2Properties(Reader reader) throws IOException {
        load(reader);
    }

    public void load(String name) throws IOException {
        try (FileInputStream fis = new FileInputStream(name)) {
            load(fis);
        }
    }

    public void load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            load(fis);
        }
    }

    public void load(InputStream inStream) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(inStream, Charset.defaultCharset())) {
            super.load(isr);
        } finally {
            inStream.close();
        }
    }

    public void load(Reader reader) throws IOException {
        try {
            super.load(reader);
        } finally {
            reader.close();
        }
    }

    public String getProperty(String key) {
        String property = super.getProperty(key);
        if (property == null) {
            _log.info("L2Properties: Missing property for key - " + key);
            return null;
        }
        return property.trim();
    }

    public String getProperty(String key, String defaultValue) {
        String property = super.getProperty(key, defaultValue);
        if (property == null) {
            _log.warn("L2Properties: Missing defaultValue for key - " + key);
            return null;
        }
        return property.trim();
    }
}
