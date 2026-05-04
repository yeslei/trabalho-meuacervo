// src/main/java/com/seusite/discos/controller/BuscarServlet.java
package com.seusite.discos.controller;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.DiscogsService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Busca de discos a partir do campo de busca do header.
 * GET /buscarServlet?q=termo
 * Tenta buscar no Discogs primeiro; se não configurado ou fora, cai para busca local.
 */
@WebServlet("/buscarServlet")
public class BuscarServlet extends HttpServlet {

    private final DiscoDAO discoDAO = new DiscoDAO();
    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");

        String termo = request.getParameter("q");
        List<Disco> resultados = Collections.emptyList();

        if (termo != null && !termo.isBlank()) {
            resultados = buscarResultados(request, termo);
        }

        request.setAttribute("termo", termo == null ? "" : termo);
        request.setAttribute("resultados", resultados);
        request.setAttribute("usuarioLogado", usuario);
        request.getRequestDispatcher("/busca.jsp").forward(request, response);
    }

    private List<Disco> buscarResultados(HttpServletRequest request, String termo) {
        try {
            return discogsService.buscarOuImportar(termo);
        } catch (IllegalStateException e) {
            request.setAttribute("avisoDiscogs", "Discogs não configurado, mostrando apenas resultados locais");
        } catch (RuntimeException e) {
            request.setAttribute("avisoDiscogs", "Discogs indisponível no momento, mostrando resultados do cache local");
            e.printStackTrace();
        } catch (SQLException e) {
            request.setAttribute("mensagemErro", "Erro ao acessar o banco de dados.");
            e.printStackTrace();
            return Collections.emptyList();
        }

        // fallback para busca local
        try {
            return discoDAO.buscarPorTermo(termo);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("mensagemErro", "Erro ao buscar discos.");
            return Collections.emptyList();
        }
    }
}
