package com.seusite.discos.controller;

import com.seusite.discos.model.Post;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogado") == null) {
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
            request.setAttribute("post", post);
            request.getRequestDispatcher("/postDetalhe.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/feed?erro=banco");
        }
    }
}
