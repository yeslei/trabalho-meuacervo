package com.seusite.discos.controller;

import com.seusite.discos.model.Faixa;
import com.seusite.discos.service.DiscogsService;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ver-tracklist")
public class VerTracklistServlet extends HttpServlet {

    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            int discogsId = Integer.parseInt(idStr.trim());
            List<Faixa> tracklist = discogsService.buscarTracklist(discogsId);

            response.setContentType("text/html; charset=UTF-8");
            if (tracklist.isEmpty()) {
                response.getWriter().write("<div class=\"track-empty\">Faixas indisponíveis no momento.</div>");
                return;
            }

            StringBuilder html = new StringBuilder("<ol class=\"track-items\">");
            for (Faixa faixa : tracklist) {
                html.append("<li>")
                        .append(escapeHtml(faixa.getTitulo()));
                if (faixa.getDuracao() != null && !faixa.getDuracao().isBlank()) {
                    html.append(" <span class=\"track-dur\">").append(escapeHtml(faixa.getDuracao())).append("</span>");
                }
                html.append("</li>");
            }
            html.append("</ol>");
            response.getWriter().write(html.toString());
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("mensagemErro", "Erro ao consultar as faixas: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
