package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AvaliacaoService;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Recebe a submissão do formulário de avaliação dentro da página de detalhes do disco.
 * Após salvar, retorna para a mesma página de detalhes.
 */
@WebServlet("/avaliarDiscoServlet")
public class AvaliarDiscoServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();

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

        int nota;
        try {
            nota = Integer.parseInt(request.getParameter("nota").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&erro=nota-invalida");
            return;
        }

        String comentario = request.getParameter("comentario");

        try {
            avaliacaoService.salvarAvaliacao(usuario.getIdUsuario(), idDisco, nota, comentario);
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&sucesso=avaliado");
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&erro=" + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/detalhesDiscoServlet?id=" + idDisco + "&erro=banco");
        }
    }
}
