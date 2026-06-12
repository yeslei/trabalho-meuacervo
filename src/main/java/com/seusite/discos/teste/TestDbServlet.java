package com.seusite.discos.teste;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.db.DatabaseInitializer;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

@WebServlet("/testar")
public class TestDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DatabaseInitializer.init();

            try (Connection conn = ConnectionFactory.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count(*) AS total FROM usuario")) {

                int totalUsuarios = 0;
                if (rs.next()) {
                    totalUsuarios = rs.getInt("total");
                }

                JsonUtil.ok(resp, Map.of(
                        "status", "ok",
                        "mensagem", "Banco conectado e tabelas verificadas.",
                        "totalUsuarios", totalUsuarios
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.escreverJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "erro", "banco",
                    "mensagem", "Erro ao conectar ao banco.",
                    "tipo", e.getClass().getSimpleName(),
                    "detalhe", e.getMessage() == null ? "sem detalhe" : e.getMessage(),
                    "jdbcUrl", ConnectionFactory.getUrlSanitizada(),
                    "dbUser", ConnectionFactory.getUsuarioConfigurado(),
                    "usandoFallbackLocal", ConnectionFactory.usandoFallbackLocal()
            ));
        }
    }
}
