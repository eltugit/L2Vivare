package gabriel.interServerExchange;



import l2r.Config;
import l2r.util.dbutils.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ISEDatabaseFactory extends BasicDataSource {
    public ISEDatabaseFactory() {
        super(Config.DATABASE_DRIVER, ISEConfig.URL, ISEConfig.LOGIN, ISEConfig.PASSWORD, Config.DATABASE_MAX_CONNECTIONS,
                Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIME, Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null);
    }

    public static ISEDatabaseFactory getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ISEDatabaseFactory instance = new ISEDatabaseFactory();
    }
}