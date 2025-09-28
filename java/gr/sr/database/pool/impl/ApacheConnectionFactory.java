package gr.sr.database.pool.impl;

import l2r.Config;
import l2r.L2DatabaseFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class ApacheConnectionFactory extends L2DatabaseFactory {
    private final PoolingDataSource dataSource;
    private final ObjectPool objectPool;
    private Logger LOG = LoggerFactory.getLogger(ApacheConnectionFactory.class);

    public ApacheConnectionFactory() {
        GenericObjectPool genericObjectPool;
        (genericObjectPool = new GenericObjectPool((PoolableObjectFactory)null)).setMaxActive(Config.DATABASE_MAX_CONNECTIONS);
        genericObjectPool.setMaxIdle(Config.DATABASE_MAX_CONNECTIONS);
        genericObjectPool.setMinIdle(1);
        genericObjectPool.setMaxWait(-1L);
        genericObjectPool.setWhenExhaustedAction((byte)2);
        genericObjectPool.setTestOnBorrow(false);
        genericObjectPool.setTestWhileIdle(true);
        genericObjectPool.setTimeBetweenEvictionRunsMillis((long)Config.DATABASE_IDLE_TEST_PERIOD * 1000L);
        genericObjectPool.setNumTestsPerEvictionRun(Config.DATABASE_MAX_CONNECTIONS);
        genericObjectPool.setMinEvictableIdleTimeMillis((long)Config.DATABASE_MAX_IDLE_TIME * 1000L);
        Properties props;
        (props = new Properties()).put("user", Config.DATABASE_LOGIN);
        props.put("password", Config.DATABASE_PASSWORD);
        DriverManagerConnectionFactory dmcf = new DriverManagerConnectionFactory(Config.DATABASE_URL, props);
        new PoolableConnectionFactory(dmcf, genericObjectPool, (KeyedObjectPoolFactory)null, "SELECT 1", false, true);
        PoolingDataSource poolingDataSource = new PoolingDataSource(genericObjectPool);
        this.objectPool = genericObjectPool;
        this.dataSource = poolingDataSource;
    }

    public void close() {
        try {
            this.objectPool.close();
        } catch (Exception var2) {
            LOG.warn("There has been a problem closing the data source!", var2);
        }
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
