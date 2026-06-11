package com.seusite.discos.controller;

import com.seusite.discos.service.DiscoService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

/** GET /disco/metricas?id_disco=X — metricas agregadas do disco (ja era JSON na Parte 1). */
@WebServlet("/disco/metricas")
public class DiscoMetricasServlet extends HttpServlet {

    private final DiscoService discoService = new DiscoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id_disco");
        if (idStr == null || idStr.trim().isEmpty()) {
            JsonUtil.erro(response, 400, "id-invalido", "Parametro id_disco obrigatorio."); return;
        }
        int idDisco;
        try { idDisco = Integer.parseInt(idStr.trim()); }
        catch (NumberFormatException e) { JsonUtil.erro(response, 400, "id-invalido", "id_disco invalido."); return; }

        try {
            Map<String, Object> metricas = discoService.obterMetricasPorDisco(idDisco);
            if (metricas == null) { JsonUtil.erro(response, 404, "disco-inexistente", "Disco nao encontrado."); return; }
            JsonUtil.ok(response, metricas);
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao consultar metricas.");
        }
    }
}
