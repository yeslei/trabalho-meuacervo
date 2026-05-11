package com.seusite.discos.controller;

import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.EstatisticaDisco;
import com.seusite.discos.model.Faixa;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.service.WishlistService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/avaliar-disco")
public class AvaliarDiscoServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final DiscoDAO discoDAO = new DiscoDAO();
    private final ColecaoService colecaoService = new ColecaoService();
    private final WishlistService wishlistService = new WishlistService();
    private final DiscogsService discogsService = new DiscogsService();

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
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            Disco disco = discoDAO.buscarPorId(idDisco);
            if (disco == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }
            request.setAttribute("disco", disco);

            // estatísticas de avaliação (média e total)
            EstatisticaDisco estatistica = avaliacaoService.buscarEstatisticas(idDisco);
            request.setAttribute("estatistica", estatistica);

            // nota atual do usuário logado para este disco
            Integer notaUsuario = avaliacaoService.buscarNota(usuario.getIdUsuario(), idDisco);
            request.setAttribute("notaUsuario", notaUsuario);

            // lista de avaliações com username para os cards de comentários
            List<AvaliacaoDisco> reviews = avaliacaoService.buscarReviews(idDisco);
            request.setAttribute("reviews", reviews);

            // verifica se o disco já está na coleção do usuário
            boolean estaNaColecao = colecaoService.possuiDisco(usuario.getIdUsuario(), idDisco);
            request.setAttribute("estaNaColecao", estaNaColecao);

            // verifica se o disco já está na wishlist do usuário
            boolean estaNaWishlist = wishlistService.possuiDisco(usuario.getIdUsuario(), idDisco);
            request.setAttribute("estaNaWishlist", estaNaWishlist);

            // tracklist via API do Discogs (não crítico — falha silenciosa)
            if (disco.getDiscogsId() != null) {
                try {
                    List<Faixa> faixas = discogsService.buscarTracklist(disco.getDiscogsId());
                    request.setAttribute("faixas", faixas);
                } catch (Exception e) {
                    request.setAttribute("faixas", Collections.emptyList());
                }
            } else {
                request.setAttribute("faixas", Collections.emptyList());
            }

            request.getRequestDispatcher("/detalhes.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=banco");
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
            response.sendRedirect(request.getContextPath() + "/index.jsp");
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
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&sucesso=avaliado");
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=" + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=banco");
        }
    }
}
