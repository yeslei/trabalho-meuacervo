package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.service.AvaliacaoService;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/avaliar-disco")
public class AvaliarDiscoServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final DiscoDAO discoDAO = new DiscoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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
            response.sendRedirect(request.getContextPath() + "/avaliarDisco.jsp?erro=id-disco-invalido");
            return;
        }

        try {
            Disco disco = discoDAO.buscarPorId(idDisco);
            if (disco == null) {
                response.sendRedirect(request.getContextPath() + "/avaliarDisco.jsp?erro=disco-inexistente");
                return;
            }
            request.setAttribute("disco", disco);
            request.setAttribute("usuarioLogado", usuario);
            request.getRequestDispatcher("/avaliarDisco.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/avaliarDisco.jsp?erro=banco");
        }
    }

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
            response.sendRedirect(request.getContextPath() + "/avaliarDisco.jsp?erro=id-disco-invalido");
            return;
        }

        int nota;
        try {
            nota = Integer.parseInt(request.getParameter("nota").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=nota-invalida");
            return;
        }

        String comentario = request.getParameter("comentario");

        try {
            avaliacaoService.salvarAvaliacao(usuario.getIdUsuario(), idDisco, nota, comentario);
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&sucesso=1");
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=" + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=banco");
        }
    }
}
