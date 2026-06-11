package com.seusite.discos.controller;

import com.seusite.discos.service.PostService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /feed?pagina=N&id_disco=X - feed social paginado em JSON. */
@WebServlet("/feed")
public class ListarFeedServlet extends HttpServlet {

    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int pagina = 1;
        String p = request.getParameter("pagina");
        if (p != null) {
            try {
                pagina = Integer.parseInt(p.trim());
            } catch (NumberFormatException ignored) {
                pagina = 1;
            }
        }

        Integer idDisco = null;
        String idDiscoStr = request.getParameter("id_disco");
        if (idDiscoStr != null && !idDiscoStr.isBlank()) {
            try {
                idDisco = Integer.parseInt(idDiscoStr.trim());
            } catch (NumberFormatException ignored) {
                idDisco = null;
            }
        }

        try {
            PostService.FeedPagina fp = postService.listarFeedPagina(pagina, idDisco);
            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("posts", fp.posts());
            corpo.put("paginaAtual", pagina);
            corpo.put("temProxima", fp.temProxima());
            corpo.put("idDiscoFiltro", idDisco);
            corpo.put("tamanhoPagina", postService.getTamanhoPaginaPadrao());
            JsonUtil.ok(response, corpo);
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar o feed.");
        }
    }
}
