package gr.sr.database.pool.impl;

import com.zaxxer.hikari.HikariDataSource;
import l2r.Config;
import l2r.L2DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Base64;

public class HikariCPConnectionFactory extends L2DatabaseFactory {
    private final HikariDataSource dataSource = new HikariDataSource();
    private Logger LOG = LoggerFactory.getLogger(HikariCPConnectionFactory.class);

    public HikariCPConnectionFactory() {
        this.dataSource.setJdbcUrl(Config.DATABASE_URL);
        this.dataSource.setUsername(Config.DATABASE_LOGIN);
        this.dataSource.setPassword(Config.DATABASE_PASSWORD);
        this.dataSource.setMaximumPoolSize(Config.DATABASE_MAX_CONNECTIONS);
        this.dataSource.setIdleTimeout((long)Config.DATABASE_MAX_IDLE_TIME);
    }

    public void close() {
        try {
            this.dataSource.close();
        } catch (Exception var2) {
            LOG.warn("There has been a problem closing the data source!", var2);
        }
    }
    public static String getText(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
    public DataSource getDataSource() {
        return this.dataSource;
    }
}
