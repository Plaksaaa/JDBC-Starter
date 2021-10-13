package test.database.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Утилитные классы - final и приватный конструктор
// connection наследует автоклоузбал поэтому при вызове нужно с ресурсами catch делать
public final class ConnectionManager {
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Mimimimaks";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/new_schema";

    private ConnectionManager() {
        throw new IllegalStateException("Utility Class!");
    }

    public static Connection getDbConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
   /* public static final String DB_USER = "db.username";
    //  database password
    public static final String DB_PASS = "db.password";
    //  database name
    public static final String DB_URL = "db.url";
    public static Connection getDbConnection() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(DB_URL),
                    PropertiesUtil.get(DB_USER),
                    PropertiesUtil.get(DB_PASS));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
