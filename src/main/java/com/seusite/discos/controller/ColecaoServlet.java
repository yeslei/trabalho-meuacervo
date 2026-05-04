package com.seusite.discos.controller;

import com.seusite.discos.dao.ColecaoDAO;
import com.seusite.discos.model.Usuario;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Adiciona/remove um disco da coleção do usuário logado.
 *
 * POST /colecaoServlet?acao=adicionar&id_disco=123
 * POST /colecaoServlet?acao=remover&id_disco=123
 */
@WebServlet("/colecaoServlet")
public class ColecaoServlet extends HttpServlet {

    private final ColecaoDAO colecaoDAO = new ColecaoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        int idDisco;
        try {
            idDisco = Integer.parseInt(request.getParameter("id_disco").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/listarFeedServlet?erro=disco-invalido");
            return;
        }

        String acao = request.getParameter("acao");
        if (acao == null) acao = "adicionar";

        try {
            if ("remover".equalsIgnoreCase(acao)) {
                colecaoDAO.removerDisco(usuario.getIdUsuario(), idDisco);
            } else {
                colecaoDAO.adicionarDisco(usuario.getIdUsuario(), idDisco);
            }
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&sucesso=1");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&erro=banco");
        }
    }
}
