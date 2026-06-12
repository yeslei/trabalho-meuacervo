package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.PostService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** POST /criar-post - cria um post no feed e responde JSON. */
@WebServlet("/criar-post")
public class CriarPostServlet extends HttpServlet {

    private final PostService postService = new PostService();

    public static class PostRequest {
        public Integer id_disco;
        public String titulo;
        public String conteudo;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            JsonUtil.erro(response, 401, "nao-autenticado", "Faca login.");
            return;
        }

        PostRequest dados = JsonUtil.lerCorpo(request, PostRequest.class);
        if (dados == null || dados.id_disco == null) {
            JsonUtil.erro(response, 400, "id-disco-invalido", "id_disco obrigatorio.");
            return;
        }

        try {
            int idPost = postService.criarPost(usuario.getIdUsuario(), dados.id_disco, dados.titulo, dados.conteudo);
            JsonUtil.escreverJson(response, HttpServletResponse.SC_CREATED, Map.of("idPost", idPost));
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(response, 400, e.getMessage() == null ? "validacao" : e.getMessage(), "Dados invalidos.");
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao criar o post.");
        }
    }
}
