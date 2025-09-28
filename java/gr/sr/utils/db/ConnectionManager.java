package gr.sr.utils.db;

import l2r.Config;

import java.sql.Connection;

public class ConnectionManager extends BasicDataSource {
    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
    private static int maxConnections = Config.DATABASE_MAX_CONNECTIONS;

    public static final ConnectionManager getInstance() {
        return CONNECTION_MANAGER;
    }
    //TODO GABRIEL IP BINDING?
    //TODO GABRIEL Change to own IP and make the own table
    public ConnectionManager() {
        //jdbc:mysql://www.l2jsunrise.com/ljsunris_community
        //ljsunris_ipBind
        //XPbs4ZVXZWWG5DyH
        super(Config.DATABASE_DRIVER, "amRiYzpteXNxbDovL3d3dy5sMmpzdW5yaXNlLmNvbS9sanN1bnJpc19jb21tdW5pdHk=", "bGpzdW5yaXNfaXBCaW5k", "WFBiczRaVlhaV1dHNUR5SA==", maxConnections, maxConnections, Config.DATABASE_MAX_IDLE_TIME, Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    public final String prepQuerySelect(String[] var1, String var2, String var3) {
        return "SELECT " + var1 + " FROM " + var2 + " WHERE " + var3;
    }

    public Connection getConnection() {
        return this.getConnection((Connection)null);
    }
}
