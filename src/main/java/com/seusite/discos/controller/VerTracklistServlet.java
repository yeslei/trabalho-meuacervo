package com.seusite.discos.controller;

import com.seusite.discos.service.DiscogsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ver-tracklist")
public class VerTracklistServlet extends HttpServlet {

    private DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int discogsId = Integer.parseInt(idStr);
                
                // Chama o serviço para buscar a lista de faixas
                List<String> tracklist = discogsService.buscarTracklist(discogsId);
                
                // Retorna os dados para a página JSP responsável por exibi-la
                request.setAttribute("tracklist", tracklist);
                request.setAttribute("discoId", discogsId);
                request.getRequestDispatcher("/tracklist.jsp").forward(request, response);
                
            } catch (Exception e) {
                request.setAttribute("erro", "Erro ao consultar as faixas: " + e.getMessage());
                request.getRequestDispatcher("/erro.jsp").forward(request, response);
            }
        } else {
            // Se vier sem ID, voltamos para a raiz
            response.sendRedirect("index.jsp");
        }
    }
}