package ru.javaops.masterjava.persist;

import ru.javaops.masterjava.config.Configs;

import static java.sql.DriverManager.getConnection;

public class DBITestProvider {
    public static void initDBI() {
        var config = Configs.getConfig("persist.conf", "db");
        initDBI(config.getString("url"), config.getString("user"), config.getString("password"));
    }

    private static void initDBI(String dbUrl, String dbUser, String dbPassword) {
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return getConnection(dbUrl, dbUser, dbPassword);
        });
    }
}