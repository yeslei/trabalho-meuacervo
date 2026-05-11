package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.DiscoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/disco/abrir")
public class DiscoAbrirServlet extends HttpServlet {

    private final DiscoService discoService = new DiscoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        String discogsIdStr = request.getParameter("discogsId");
        if (discogsIdStr == null || discogsIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/buscar-discos?erro=disco-invalido");
            return;
        }

        int discogsId;
        try {
            discogsId = Integer.parseInt(discogsIdStr.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/buscar-discos?erro=disco-invalido");
            return;
        }

        Disco disco = new Disco();
        disco.setDiscogsId(discogsId);
        disco.setTitulo(request.getParameter("titulo"));
        disco.setArtista(request.getParameter("artista"));
        disco.setGenero(request.getParameter("genero"));
        disco.setFormato(request.getParameter("formato"));
        disco.setImagemCapa(request.getParameter("capa"));

        String anoStr = request.getParameter("ano");
        if (anoStr != null && !anoStr.trim().isEmpty()) {
            try {
                disco.setAnoLancamento(Integer.parseInt(anoStr.trim()));
            } catch (NumberFormatException ignored) {
                disco.setAnoLancamento(null);
            }
        }

        try {
            Disco salvo = discoService.obterOuSalvarDisco(disco);
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + salvo.getIdDisco());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/buscar-discos?erro=banco");
        }
    }
}
