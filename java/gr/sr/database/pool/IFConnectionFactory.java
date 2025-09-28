package gr.sr.database.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public interface IFConnectionFactory {
    Logger LOG = LoggerFactory.getLogger(IFConnectionFactory.class);

    DataSource getDataSource();

    void close();

    default Connection getConnection() {
        Connection var1 = null;

        while(var1 == null) {
            try {
                var1 = this.getDataSource().getConnection();
            } catch (SQLException e) {
                LOG.warn("{}: Unable to get a connection!", this.getClass().getSimpleName(), e);
            }
        }

        return var1;
    }
}
