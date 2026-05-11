package com.seusite.discos.controller;

import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.WishlistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/perfil/reviews")
public class VerReviewsServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final WishlistService wishlistService = new WishlistService();
    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        try {
            int idUsuario = usuarioLogado.getIdUsuario();
            List<AvaliacaoDisco> reviews = avaliacaoService.buscarReviewsDoUsuario(idUsuario);
            int totalReviews = reviews.size();
            int totalFavoritos = wishlistService.contarWishlist(idUsuario);
            int totalDiscos = colecaoService.contarDiscosNaColecao(idUsuario);

            request.setAttribute("usuarioPerfil", usuarioLogado);
            request.setAttribute("abaAtiva", "reviews");
            request.setAttribute("ehProprioPerfil", Boolean.TRUE);
            request.setAttribute("reviews", reviews);
            request.setAttribute("colecao", java.util.Collections.emptyList());
            request.setAttribute("favoritos", java.util.Collections.emptyList());
            request.setAttribute("totalDiscos", totalDiscos);
            request.setAttribute("totalReviews", totalReviews);
            request.setAttribute("totalFavoritos", totalFavoritos);

            request.getRequestDispatcher("/perfil.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensagemErro", "Erro ao carregar seus reviews.");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }
}
