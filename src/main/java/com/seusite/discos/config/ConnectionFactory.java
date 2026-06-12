package com.seusite.discos.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String DATABASE_URL = EnvConfig.get("DATABASE_URL", null);
    private static final String URL = resolverJdbcUrl();
    private static final String USER = EnvConfig.get("DB_USER", extrairUsuario(DATABASE_URL, "postgres"));
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD", extrairSenha(DATABASE_URL, "123456"));

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL nao encontrado.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String resolverJdbcUrl() {
        String dbJdbcUrl = EnvConfig.get("DB_JDBC_URL", null);
        if (dbJdbcUrl != null) {
            return normalizarJdbcUrl(dbJdbcUrl);
        }

        if (DATABASE_URL != null) {
            return converterDatabaseUrl(DATABASE_URL);
        }

        return "jdbc:postgresql://localhost:5432/site_discos";
    }

    private static String normalizarJdbcUrl(String valor) {
        if (valor.startsWith("jdbc:postgresql://")) {
            return garantirSsl(valor);
        }

        if (valor.startsWith("postgresql://") || valor.startsWith("postgres://")) {
            return converterDatabaseUrl(valor);
        }

        return valor;
    }

    private static String converterDatabaseUrl(String databaseUrl) {
        URI uri = URI.create(databaseUrl.replaceFirst("^postgresql://", "postgres://"));
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String path = uri.getPath() == null || uri.getPath().isBlank() ? "/postgres" : uri.getPath();
        String query = uri.getQuery();

        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
        if (query != null && !query.isBlank()) {
            jdbcUrl += "?" + query;
        }

        return garantirSsl(jdbcUrl);
    }

    private static String garantirSsl(String jdbcUrl) {
        if (jdbcUrl.contains("sslmode=")) {
            return jdbcUrl;
        }

        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "sslmode=require";
    }

    private static String extrairUsuario(String databaseUrl, String padrao) {
        return extrairUserInfo(databaseUrl, 0, padrao);
    }

    private static String extrairSenha(String databaseUrl, String padrao) {
        return extrairUserInfo(databaseUrl, 1, padrao);
    }

    private static String extrairUserInfo(String databaseUrl, int indice, String padrao) {
        if (databaseUrl == null) {
            return padrao;
        }

        try {
            URI uri = URI.create(databaseUrl.replaceFirst("^postgresql://", "postgres://"));
            String userInfo = uri.getUserInfo();
            if (userInfo == null) {
                return padrao;
            }

            String[] partes = userInfo.split(":", 2);
            if (partes.length <= indice || partes[indice].isBlank()) {
                return padrao;
            }

            return URLDecoder.decode(partes[indice], StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return padrao;
        }
    }
}
