package mbanking.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/gunadarma_bank?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        loadDriver();
        String url = firstNonBlank(
                System.getProperty("db.url"),
                System.getenv("GUNADARMA_DB_URL"),
                System.getenv("DB_URL"),
                DEFAULT_URL
        );
        String user = firstNonBlank(
                System.getProperty("db.user"),
                System.getenv("GUNADARMA_DB_USER"),
                System.getenv("DB_USER"),
                DEFAULT_USER
        );
        String password = firstNonBlank(
                System.getProperty("db.password"),
                System.getenv("GUNADARMA_DB_PASSWORD"),
                System.getenv("DB_PASSWORD"),
                DEFAULT_PASSWORD
        );
        return DriverManager.getConnection(url, user, password);
    }

    private static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MySQL JDBC Driver tidak ditemukan. Tambahkan mysql-connector-j ke classpath.",
                    e
            );
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return "";
    }
}
