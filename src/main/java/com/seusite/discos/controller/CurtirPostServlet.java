package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.CurtidaService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** POST /curtir-post - alterna curtida e responde novo estado em JSON. */
@WebServlet("/curtir-post")
public class CurtirPostServlet extends HttpServlet {

    private final CurtidaService curtidaService = new CurtidaService();

    public static class CurtirRequest {
        public Integer id_post;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            JsonUtil.erro(response, 401, "nao-autenticado", "Faca login.");
            return;
        }

        CurtirRequest dados = JsonUtil.lerCorpo(request, CurtirRequest.class);
        if (dados == null || dados.id_post == null) {
            JsonUtil.erro(response, 400, "id-post-invalido", "id_post obrigatorio.");
            return;
        }

        try {
            boolean curtiu = curtidaService.alternarCurtida(usuario.getIdUsuario(), dados.id_post);
            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("curtiu", curtiu);
            corpo.put("totalCurtidas", curtidaService.contarPorPost(dados.id_post));
            JsonUtil.ok(response, corpo);
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(response, 404, "post-inexistente", "Post nao encontrado.");
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao curtir o post.");
        }
    }
}
