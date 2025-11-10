package org.gestion.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionManager {

    private static final String URL = "jdbc:mysql://localhost:3306/gestion";
    private static final String USER = "root";
    private static final String PASS = "root";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Error cargando driver MySQL", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar a la BD", e);
        }
    }
}
