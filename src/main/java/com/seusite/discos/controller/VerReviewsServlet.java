package com.seusite.discos.controller;

import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.WishlistService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** GET /perfil/reviews — reviews escritas pelo usuario + contadores. */
@WebServlet("/perfil/reviews")
public class VerReviewsServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final WishlistService wishlistService = new WishlistService();
    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        try {
            int idUsuario = usuario.getIdUsuario();
            List<AvaliacaoDisco> reviews = avaliacaoService.buscarReviewsDoUsuario(idUsuario);

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("reviews", reviews);
            corpo.put("totalReviews", reviews.size());
            corpo.put("totalFavoritos", wishlistService.contarWishlist(idUsuario));
            corpo.put("totalDiscos", colecaoService.contarDiscosNaColecao(idUsuario));
            JsonUtil.ok(response, corpo);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar os reviews.");
        }
    }
}
