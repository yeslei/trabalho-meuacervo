package com.seusite.discos.controller;

import com.seusite.discos.dao.UsuarioDAO;
import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.WishlistService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** GET /perfil/publico?username=... — dados publicos do perfil de um usuario. */
@WebServlet("/perfil/publico")
public class PerfilPublicoServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ColecaoService colecaoService = new ColecaoService();
    private final WishlistService wishlistService = new WishlistService();
    private final AvaliacaoService avaliacaoService = new AvaliacaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        if (username == null || username.isBlank()) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST,
                    "username-ausente", "Informe o username do perfil.");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorUsername(username.trim());
            if (usuario == null) {
                JsonUtil.erro(response, HttpServletResponse.SC_NOT_FOUND,
                        "perfil-nao-encontrado", "Perfil nao encontrado.");
                return;
            }

            int idUsuario = usuario.getIdUsuario();
            List<Disco> colecao = colecaoService.listarDiscosDoUsuario(idUsuario);
            List<Disco> favoritos = wishlistService.listarWishlistDoUsuario(idUsuario);
            List<AvaliacaoDisco> reviews = avaliacaoService.buscarReviewsDoUsuario(idUsuario);

            Map<String, Object> usuarioPublico = new LinkedHashMap<>();
            usuarioPublico.put("idUsuario", usuario.getIdUsuario());
            usuarioPublico.put("nome", usuario.getNome());
            usuarioPublico.put("username", usuario.getUsername());
            usuarioPublico.put("dataCriacao", usuario.getDataCriacao());

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalDiscos", colecao.size());
            stats.put("totalFavoritos", favoritos.size());
            stats.put("totalReviews", reviews.size());

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("usuario", usuarioPublico);
            corpo.put("stats", stats);
            corpo.put("colecao", colecao);
            corpo.put("favoritos", favoritos);
            corpo.put("reviews", reviews);

            JsonUtil.ok(response, corpo);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "banco", "Erro ao carregar o perfil publico.");
        }
    }
}
