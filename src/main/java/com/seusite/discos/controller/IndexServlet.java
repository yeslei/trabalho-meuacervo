package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** GET /home — discos em destaque para a home. Responde { "discos": [...] }. */
@WebServlet("/home")
public class IndexServlet extends HttpServlet {

    private final DiscogsService discogsService = new DiscogsService();

    private static final String[] TERMOS = {
        "best selling vinyl albums",
        "most wanted classic records",
        "popular music collection"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Disco> discos = Collections.emptyList();
        try {
            List<Disco> resultados = discogsService.buscarDiscosPorTermo(TERMOS[0], 1);
            discos = resultados.subList(0, Math.min(18, resultados.size()));
        } catch (Exception e) {
            e.printStackTrace(); // home continua funcionando mesmo se a API falhar
        }
        JsonUtil.ok(response, Map.of("discos", discos));
    }
}
