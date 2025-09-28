//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.database.pool.impl;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import l2r.Config;
import l2r.L2DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.SQLException;

public class C3P0ConnectionFactory extends L2DatabaseFactory {
    private final ComboPooledDataSource dataSource;
    private Logger LOG = LoggerFactory.getLogger(C3P0ConnectionFactory.class);

    public C3P0ConnectionFactory() {
        if (Config.DATABASE_MAX_CONNECTIONS < 2) {
            Config.DATABASE_MAX_CONNECTIONS = 2;
            LOG.warn("A minimum of {} database connections are required.", Config.DATABASE_MAX_CONNECTIONS);
        }

        this.dataSource = new ComboPooledDataSource();
        this.dataSource.setAutoCommitOnClose(true);
        this.dataSource.setInitialPoolSize(10);
        this.dataSource.setMinPoolSize(10);
        this.dataSource.setMaxPoolSize(Math.max(10, Config.DATABASE_MAX_CONNECTIONS));
        this.dataSource.setAcquireRetryAttempts(0);
        this.dataSource.setAcquireRetryDelay(500);
        this.dataSource.setCheckoutTimeout(0);
        this.dataSource.setAcquireIncrement(5);
        this.dataSource.setAutomaticTestTable("connection_test_table");
        this.dataSource.setTestConnectionOnCheckin(false);
        this.dataSource.setIdleConnectionTestPeriod(3600);
        this.dataSource.setMaxIdleTime(Config.DATABASE_MAX_IDLE_TIME);
        this.dataSource.setMaxStatementsPerConnection(100);
        this.dataSource.setBreakAfterAcquireFailure(false);

        try {
            this.dataSource.setDriverClass(Config.DATABASE_DRIVER);
        } catch (PropertyVetoException var3) {
            LOG.error("There has been a problem setting the driver class!", var3);
        }

        this.dataSource.setJdbcUrl(Config.DATABASE_URL);
        this.dataSource.setUser(Config.DATABASE_LOGIN);
        this.dataSource.setPassword(Config.DATABASE_PASSWORD);

        try {
            this.dataSource.getConnection().close();
        } catch (SQLException var2) {
            LOG.warn("There has been a problem closing the test connection!", var2);
        }

        LOG.debug("Database connection working.");
    }

    public void close() {
        try {
            this.dataSource.close();
        } catch (Exception var2) {
            LOG.warn("There has been a problem closing the data source!", var2);
        }
    }

    public DataSource getDataSource() {
        return this.dataSource;
    }
}
