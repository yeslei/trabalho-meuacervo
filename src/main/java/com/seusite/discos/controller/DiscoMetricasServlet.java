package com.seusite.discos.controller;

import com.google.gson.Gson;
import com.seusite.discos.service.DiscoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/disco/metricas")
public class DiscoMetricasServlet extends HttpServlet {

    private final DiscoService discoService = new DiscoService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id_disco");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id_disco obrigatorio");
            return;
        }

        int idDisco;
        try {
            idDisco = Integer.parseInt(idStr.trim());
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parametro id_disco invalido");
            return;
        }

        try {
            Map<String, Object> metricas = discoService.obterMetricasPorDisco(idDisco);
            if (metricas == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Disco nao encontrado");
                return;
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(metricas));
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao consultar metricas");
        }
    }
}
