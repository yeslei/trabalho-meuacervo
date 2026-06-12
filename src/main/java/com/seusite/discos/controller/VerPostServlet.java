package com.seusite.discos.controller;

import com.seusite.discos.model.Post;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.CurtidaService;
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
import jakarta.servlet.http.HttpSession;

/** GET /post?id=X - detalhe de um post + curtidas em JSON. */
@WebServlet("/post")
public class VerPostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    private final CurtidaService curtidaService = new CurtidaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            JsonUtil.erro(response, 401, "nao-autenticado", "Faca login.");
            return;
        }

        int idPost;
        try {
            idPost = Integer.parseInt(request.getParameter("id").trim());
        } catch (Exception e) {
            JsonUtil.erro(response, 400, "id-post-invalido", "id obrigatorio.");
            return;
        }

        try {
            Post post = postService.buscarDetalhe(idPost);
            if (post == null) {
                JsonUtil.erro(response, 404, "post-inexistente", "Post nao encontrado.");
                return;
            }

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("post", post);
            corpo.put("totalCurtidas", curtidaService.contarPorPost(idPost));
            corpo.put("usuarioJaCurtiu", curtidaService.usuarioCurtiu(usuario.getIdUsuario(), idPost));
            JsonUtil.ok(response, corpo);
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar o post.");
        }
    }
}
