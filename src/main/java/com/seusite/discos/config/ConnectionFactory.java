package com.seusite.discos.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL = EnvConfig.get("DB_JDBC_URL", "jdbc:postgresql://localhost:5432/site_discos");
    private static final String USER = EnvConfig.get("DB_USER", "postgres");
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD", "123456");

    public static Connection getConnection() {
        try {
            // ESTA É A LINHA QUE ESTAVA FALTANDO!
            // Ela força o Tomcat a carregar o "tradutor" do PostgreSQL
            Class.forName("org.postgresql.Driver");

            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao conectar com PostgreSQL", e);
        }
    }
}
