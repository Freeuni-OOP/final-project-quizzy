package quizzy.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DBUtil {

    private static final String URL = config("db.url", "DB_URL", "jdbc:mysql://localhost:3306/quizzy");
    private static final String USER = config("db.user", "DB_USER", "root");
    private static final String PASSWORD = config("db.pass", "DB_PASS", "");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL JDBC driver not found on classpath", e);
        }
    }

    private DBUtil() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String config(String sysProp, String envVar, String fallback) {
        String value = System.getProperty(sysProp);
        if (value == null || value.isEmpty()) {
            value = System.getenv(envVar);
        }
        return (value == null || value.isEmpty()) ? fallback : value;
    }
}
