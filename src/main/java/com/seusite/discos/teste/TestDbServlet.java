package com.seusite.discos.teste; // Note que o pacote é o 'teste'

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.db.DatabaseInitializer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet("/testar") // Rota bem curtinha para facilitar
public class TestDbServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {
            DatabaseInitializer.init(); // Cria as tabelas se não existirem

            try (Connection conn = ConnectionFactory.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT count(*) AS total FROM usuario")) {

                if (rs.next()) {
                    out.println("<h1>Sucesso!</h1>");
                    out.println("<p>Banco conectado e tabelas verificadas.</p>");
                }
            }
        } catch (Exception e) {
            out.println("<h1>Erro ao conectar!</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
        }
    }
}