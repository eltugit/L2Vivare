package gr.sr.utils.db;



import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DbUtils {
    public DbUtils() {
    }

    
    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    public static void close(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement st, ResultSet rs) {
        close(st);
        close(rs);
    }

    public static void closeQuietly(Connection con) {
        close(con);
    }

    public static void closeQuietly(Connection con, Statement st) {
        try {
            closeQuietly(st);
        } finally {
            closeQuietly(con);
        }

    }

    public static void closeQuietly(Statement st, ResultSet rs) {
        try {
            closeQuietly(st);
        } finally {
            closeQuietly(rs);
        }

    }

    public static void closeQuietly(Connection con, Statement st, ResultSet rs) {
        try {
            closeQuietly(rs);
        } finally {
            try {
                closeQuietly(st);
            } finally {
                closeQuietly(con);
            }
        }

    }

    public static void closeQuietly(ResultSet rs) {
        close(rs);
    }

    public static void closeQuietly(Statement st) {
        close(st);
    }
}
