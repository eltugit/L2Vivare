package gr.sr.utils.db;

import gr.sr.utils.Files;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

public class BasicDataSource implements DataSource {
    private final PoolingDataSource poolingDataSource;
    private final ObjectPool objectPool;

    public BasicDataSource(String driver, String sqlUrl, String user, String password, int maxActive, int maxIdle, int setMinEvictableIdleTimeMillis, int timeBetweenEvictionRunsMillis, boolean newFactory) {
        GenericObjectPool genericObjectPool;
        (genericObjectPool = new GenericObjectPool((PoolableObjectFactory)null)).setMaxActive(maxActive);
        genericObjectPool.setMaxIdle(maxIdle);
        genericObjectPool.setMinIdle(1);
        genericObjectPool.setMaxWait(-1L);
        genericObjectPool.setWhenExhaustedAction((byte)2);
        genericObjectPool.setTestOnBorrow(false);
        genericObjectPool.setTestWhileIdle(true);
        genericObjectPool.setTimeBetweenEvictionRunsMillis((long)timeBetweenEvictionRunsMillis * 1000L);
        genericObjectPool.setNumTestsPerEvictionRun(maxActive);
        genericObjectPool.setMinEvictableIdleTimeMillis((long)setMinEvictableIdleTimeMillis * 1000L);
        GenericKeyedObjectPoolFactory genericKeyedObjectPoolFactory = null;
        if (newFactory) {
            genericKeyedObjectPoolFactory = new GenericKeyedObjectPoolFactory((KeyedPoolableObjectFactory)null, -1, (byte)0, 0L, 1, -1);
        }

        Properties properties;
        (properties = new Properties()).put("user", Files.getText(user));
        properties.put("password", Files.getText(password));
        DriverManagerConnectionFactory driverManagerConnectionFactory = new DriverManagerConnectionFactory(Files.getText(sqlUrl), properties);
        new PoolableConnectionFactory(driverManagerConnectionFactory, genericObjectPool, genericKeyedObjectPoolFactory, "SELECT 1", false, true);
        PoolingDataSource poolingDataSource = new PoolingDataSource(genericObjectPool);
        this.objectPool = genericObjectPool;
        this.poolingDataSource = poolingDataSource;
    }

    public Connection getConnection(Connection con) {
        try {
            return con != null && !con.isClosed() ? con : this.poolingDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getBusyConnectionCount() {
        return this.objectPool.getNumActive();
    }

    public int getIdleConnectionCount() {
        return this.objectPool.getNumIdle();
    }

    public void shutdown() {
        try {
            this.objectPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getLogWriter() {
        return this.poolingDataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter var1) {
        this.poolingDataSource.setLogWriter(var1);
    }

    public void setLoginTimeout(int var1) {
        throw new UnsupportedOperationException();
    }

    public int getLoginTimeout() {
        throw new UnsupportedOperationException();
    }

    public Logger getParentLogger() {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap(Class<T> var1) {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> var1) {
        return false;
    }

    public Connection getConnection() {
        try {
            return this.poolingDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Connection getConnection(String var1, String var2) {
        throw new UnsupportedOperationException();
    }
}
