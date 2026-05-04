package com.seusite.discos.controller;

import com.seusite.discos.dao.AvaliacaoDAO;
import com.seusite.discos.dao.ColecaoDAO;
import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.dao.FaixaDAO;
import com.seusite.discos.dao.WishlistDAO;
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
 * Página de detalhes do disco. Acesso: /detalhesDiscoServlet?id=123
 */
@WebServlet("/detalhesDiscoServlet")
public class DetalhesDiscoServlet extends HttpServlet {

    private final DiscoDAO discoDAO = new DiscoDAO();
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final ColecaoDAO colecaoDAO = new ColecaoDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final FaixaDAO faixaDAO = new FaixaDAO();
    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        // Visitantes podem ver - mas não interagir.

        int idDisco;
        try {
            idDisco = Integer.parseInt(request.getParameter("id").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/listarFeedServlet?erro=disco-invalido");
            return;
        }

        try {
            Disco disco = discoDAO.buscarPorId(idDisco);
            if (disco == null) {
                response.sendRedirect(request.getContextPath() + "/listarFeedServlet?erro=disco-inexistente");
                return;
            }

            AvaliacaoDAO.EstatisticaDisco estat = avaliacaoDAO.estatisticasDoDisco(idDisco);
            List<AvaliacaoDAO.ReviewExibicao> reviews = avaliacaoDAO.listarReviewsDoDisco(idDisco);

            // Enriquece com tracklist do Discogs se ainda não estiver em cache
            if (disco.getDiscogsId() != null) {
                try {
                    discogsService.enriquecerComTracklist(idDisco);
                } catch (Exception e) {
                    System.err.println("[DetalhesDiscoServlet] Tracklist indisponível para disco " + idDisco + ": " + e.getMessage());
                }
            }

            request.setAttribute("disco", disco);
            request.setAttribute("estatistica", estat);
            request.setAttribute("reviews", reviews);
            request.setAttribute("faixas", faixaDAO.listarPorDisco(idDisco));

            if (usuario != null) {
                request.setAttribute("estaNaColecao", colecaoDAO.possuiDisco(usuario.getIdUsuario(), idDisco));
                request.setAttribute("estaNaWishlist", wishlistDAO.existe(usuario.getIdUsuario(), idDisco));
                request.setAttribute("notaUsuario", avaliacaoDAO.notaDoUsuario(usuario.getIdUsuario(), idDisco));
            } else {
                request.setAttribute("estaNaColecao", false);
                request.setAttribute("estaNaWishlist", false);
                request.setAttribute("notaUsuario", null);
            }

            request.setAttribute("usuarioLogado", usuario);
            request.getRequestDispatcher("/detalhes.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("reviews", Collections.emptyList());
            request.setAttribute("mensagemErro", "Erro ao carregar o disco.");
            request.getRequestDispatcher("/detalhes.jsp").forward(request, response);
        }
    }
}
