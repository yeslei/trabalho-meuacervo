package com.seusite.discos.controller;

import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/** GET /buscar-discos?q=termo&page=N — busca paginada no Discogs. Responde JSON. */
@WebServlet("/buscar-discos")
public class BuscarDiscosServlet extends HttpServlet {

    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String termo = request.getParameter("q");
        if (termo == null || termo.trim().isEmpty()) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "termo-vazio", "Informe um termo de busca.");
            return;
        }

        int pagina = 1;
        String paginaStr = request.getParameter("page");
        if (paginaStr != null && !paginaStr.trim().isEmpty()) {
            try { pagina = Integer.parseInt(paginaStr.trim()); } catch (NumberFormatException ignored) { pagina = 1; }
        }
        if (pagina < 1) pagina = 1;

        try {
            DiscogsService.ResultadoBusca resultado = discogsService.buscarDiscosPorTermoPaginado(termo, pagina);
            int paginaAtual = resultado.paginaAtual();
            int totalPaginas = Math.max(resultado.totalPaginas(), 1);

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("discos", resultado.discos());
            corpo.put("termoBusca", termo);
            corpo.put("paginaAtual", paginaAtual);
            corpo.put("totalPaginas", totalPaginas);
            corpo.put("totalItens", resultado.totalItens());
            corpo.put("itensPorPagina", resultado.itensPorPagina());
            corpo.put("temAnterior", paginaAtual > 1);
            corpo.put("temProxima", paginaAtual < totalPaginas);
            JsonUtil.ok(response, corpo);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_GATEWAY,
                    "api-discogs", "Erro ao consultar a API do Discogs.");
        }
    }
}
