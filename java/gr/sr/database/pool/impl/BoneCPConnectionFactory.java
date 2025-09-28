//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package gr.sr.database.pool.impl;

import com.jolbox.bonecp.BoneCPDataSource;
import l2r.Config;
import l2r.L2DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

public class BoneCPConnectionFactory extends L2DatabaseFactory {
    private final BoneCPDataSource dataSource = new BoneCPDataSource();
    private Logger LOG = LoggerFactory.getLogger(BoneCPConnectionFactory.class);

    public BoneCPConnectionFactory() {
        this.dataSource.setJdbcUrl(Config.DATABASE_URL);
        this.dataSource.setUsername(Config.DATABASE_LOGIN);
        this.dataSource.setPassword(Config.DATABASE_PASSWORD);
        this.dataSource.setPartitionCount(5);
        this.dataSource.setMaxConnectionsPerPartition(Config.DATABASE_MAX_CONNECTIONS);
        this.dataSource.setIdleConnectionTestPeriod((long)Config.DATABASE_MAX_IDLE_TIME, TimeUnit.SECONDS);
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
