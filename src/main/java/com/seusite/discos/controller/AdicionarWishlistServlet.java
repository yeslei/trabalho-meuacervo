package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.WishlistService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * /wishlist/adicionar
 *   POST   — adiciona o disco a wishlist (corpo JSON).
 *   DELETE — remove o disco da wishlist.
 */
@WebServlet("/wishlist/adicionar")
public class AdicionarWishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    public static class ItemRequest { public Integer id_disco; }

    private Usuario logado(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        return s == null ? null : (Usuario) s.getAttribute("usuarioLogado");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        if (dados == null || dados.id_disco == null) {
            JsonUtil.erro(response, 400, "id-invalido", "id_disco obrigatorio."); return;
        }
        try {
            wishlistService.adicionarPorId(usuario.getIdUsuario(), dados.id_disco);
            JsonUtil.sucesso(response, "Disco adicionado aos favoritos.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao adicionar aos favoritos.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        Integer idDisco = dados != null ? dados.id_disco : null;
        if (idDisco == null) {
            String q = request.getParameter("id_disco");
            if (q != null) try { idDisco = Integer.parseInt(q.trim()); } catch (Exception ignored) {}
        }
        if (idDisco == null) { JsonUtil.erro(response, 400, "id-invalido", "id_disco obrigatorio."); return; }

        try {
            wishlistService.removerDiscoDaWishlist(usuario.getIdUsuario(), idDisco);
            JsonUtil.sucesso(response, "Disco removido dos favoritos.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao remover dos favoritos.");
        }
    }
}
