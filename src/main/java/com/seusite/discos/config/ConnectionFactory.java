package com.seusite.discos.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String DB_JDBC_URL = EnvConfig.get("DB_JDBC_URL", null);
    private static final String DATABASE_URL = EnvConfig.get("DATABASE_URL", null);
    private static final String URL = resolverJdbcUrl();
    private static final String USER = EnvConfig.get("DB_USER", extrairUsuario("postgres"));
    private static final String PASSWORD = EnvConfig.get("DB_PASSWORD", extrairSenha("123456"));

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL nao encontrado.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String resolverJdbcUrl() {
        if (DB_JDBC_URL != null) {
            return normalizarJdbcUrl(DB_JDBC_URL);
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

    private static String extrairUsuario(String padrao) {
        String usuario = extrairParametroJdbc(DB_JDBC_URL, "user");
        if (usuario != null) {
            return usuario;
        }

        return extrairUserInfo(DATABASE_URL, 0, padrao);
    }

    private static String extrairSenha(String padrao) {
        String senha = extrairParametroJdbc(DB_JDBC_URL, "password");
        if (senha != null) {
            return senha;
        }

        return extrairUserInfo(DATABASE_URL, 1, padrao);
    }

    private static String extrairParametroJdbc(String jdbcUrl, String nome) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:postgresql://") || !jdbcUrl.contains("?")) {
            return null;
        }

        String query = jdbcUrl.substring(jdbcUrl.indexOf('?') + 1);
        for (String parametro : query.split("&")) {
            String[] partes = parametro.split("=", 2);
            if (partes.length == 2 && partes[0].equals(nome) && !partes[1].isBlank()) {
                return URLDecoder.decode(partes[1], StandardCharsets.UTF_8);
            }
        }

        return null;
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
