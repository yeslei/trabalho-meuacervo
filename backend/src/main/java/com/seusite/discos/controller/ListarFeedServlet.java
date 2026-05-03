package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.PostService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/feed")
public class ListarFeedServlet extends HttpServlet {

    private final PostService postService = new PostService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

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
            request.setAttribute("posts", fp.posts());
            request.setAttribute("paginaAtual", pagina);
            request.setAttribute("temProxima", fp.temProxima());
            request.setAttribute("idDiscoFiltro", idDisco);
            request.setAttribute("usuarioLogado", usuario);
            request.getRequestDispatcher("/feed.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("posts", Collections.emptyList());
            request.setAttribute("paginaAtual", pagina);
            request.setAttribute("temProxima", Boolean.FALSE);
            request.setAttribute("usuarioLogado", usuario);
            request.setAttribute("mensagemErro", "banco");
            request.getRequestDispatcher("/feed.jsp").forward(request, response);
        }
    }
}
