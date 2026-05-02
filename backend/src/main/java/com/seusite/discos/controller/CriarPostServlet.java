package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.PostService;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/criar-post")
public class CriarPostServlet extends HttpServlet {

    private final PostService postService = new PostService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        String titulo = request.getParameter("titulo");
        String conteudo = request.getParameter("conteudo");
        String idDiscoStr = request.getParameter("id_disco");

        int idDisco;
        try {
            idDisco = Integer.parseInt(idDiscoStr.trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=id-disco-invalido");
            return;
        }

        try {
            postService.criarPost(usuario.getIdUsuario(), idDisco, titulo, conteudo);
            response.sendRedirect(request.getContextPath() + "/feed?sucesso=post-criado");
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=" + codigo + "&id_disco=" + idDisco);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/novoPost.jsp?erro=banco&id_disco=" + idDisco);
        }
    }
}
