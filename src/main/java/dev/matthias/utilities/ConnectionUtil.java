package dev.matthias.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtil {

    private ConnectionUtil(){}

    public static Connection createConnection() {
        try {
            return DriverManager.getConnection(System.getenv("project1db"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
