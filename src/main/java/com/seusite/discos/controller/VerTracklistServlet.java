package com.seusite.discos.controller;

import com.seusite.discos.model.Faixa;
import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** GET /ver-tracklist?id=discogsId — tracklist via Discogs. Antes devolvia HTML; agora JSON. */
@WebServlet("/ver-tracklist")
public class VerTracklistServlet extends HttpServlet {

    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST, "id-invalido", "Parametro id obrigatorio.");
            return;
        }
        try {
            int discogsId = Integer.parseInt(idStr.trim());
            List<Faixa> tracklist = discogsService.buscarTracklist(discogsId);
            JsonUtil.ok(response, Map.of("faixas", tracklist));
        } catch (NumberFormatException e) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST, "id-invalido", "Id invalido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.ok(response, Map.of("faixas", Collections.emptyList())); // tracklist e nao-critica
        }
    }
}
