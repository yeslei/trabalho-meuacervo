package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.CurtidaService;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/curtir-post")
public class CurtirPostServlet extends HttpServlet {

    private final CurtidaService curtidaService = new CurtidaService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        int idPost;
        try {
            idPost = Integer.parseInt(request.getParameter("id_post").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/feed?erro=id-post-invalido");
            return;
        }

        try {
            curtidaService.alternarCurtida(usuario.getIdUsuario(), idPost);
            response.sendRedirect(request.getContextPath() + "/post?id=" + idPost);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/feed?erro=post-inexistente");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/post?id=" + idPost + "&erro=banco");
        }
    }
}
