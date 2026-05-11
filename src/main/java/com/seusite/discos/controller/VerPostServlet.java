package com.seusite.discos.controller;

import com.seusite.discos.model.Post;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.CurtidaService;
import com.seusite.discos.service.PostService;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/post")
public class VerPostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    private final CurtidaService curtidaService = new CurtidaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        int idPost;
        try {
            idPost = Integer.parseInt(request.getParameter("id").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/feed?erro=id-post-invalido");
            return;
        }

        try {
            Post post = postService.buscarDetalhe(idPost);
            if (post == null) {
                response.sendRedirect(request.getContextPath() + "/feed?erro=post-inexistente");
                return;
            }
            int totalCurtidas = curtidaService.contarPorPost(idPost);
            boolean usuarioJaCurtiu = curtidaService.usuarioCurtiu(usuario.getIdUsuario(), idPost);
            request.setAttribute("post", post);
            request.setAttribute("totalCurtidas", totalCurtidas);
            request.setAttribute("usuarioJaCurtiu", usuarioJaCurtiu);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/feed?erro=banco");
        }
    }
}
