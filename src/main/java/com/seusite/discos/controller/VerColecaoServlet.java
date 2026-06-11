package com.seusite.discos.controller;

import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.WishlistService;
import com.seusite.discos.dao.ItemColecaoDAO;
import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** GET /colecao/ver — discos da colecao do usuario + contadores do perfil. */
@WebServlet("/colecao/ver")
public class VerColecaoServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        try {
            Colecao colecao = colecaoService.obterOuCriarColecaoDoUsuario(usuario.getIdUsuario());
            try (Connection conn = ConnectionFactory.getConnection()) {
                ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
                List<Disco> meusDiscos = itemDAO.listarDiscosDaColecao(colecao.getIdColecao());

                Map<String, Object> corpo = new LinkedHashMap<>();
                corpo.put("colecao", meusDiscos);
                corpo.put("totalDiscos", meusDiscos.size());
                corpo.put("totalReviews", avaliacaoService.contarReviews(usuario.getIdUsuario()));
                corpo.put("totalFavoritos", wishlistService.contarWishlist(usuario.getIdUsuario()));
                JsonUtil.ok(response, corpo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar a colecao.");
        }
    }
}
