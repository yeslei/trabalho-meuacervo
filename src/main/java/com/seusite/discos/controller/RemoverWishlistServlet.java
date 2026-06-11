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

/** DELETE /wishlist/remover?idDisco=X — alias de remocao da wishlist. */
@WebServlet("/wishlist/remover")
public class RemoverWishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        remover(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        remover(request, response);
    }

    private void remover(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession s = request.getSession(false);
        Usuario usuario = s == null ? null : (Usuario) s.getAttribute("usuarioLogado");
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        String idStr = request.getParameter("idDisco");
        if (idStr == null || idStr.isEmpty()) {
            JsonUtil.erro(response, 400, "id-invalido", "idDisco obrigatorio."); return;
        }
        try {
            int idDisco = Integer.parseInt(idStr.trim());
            wishlistService.removerDiscoDaWishlist(usuario.getIdUsuario(), idDisco);
            JsonUtil.sucesso(response, "Disco removido dos favoritos.");
        } catch (NumberFormatException e) {
            JsonUtil.erro(response, 400, "id-invalido", "idDisco invalido.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Falha ao remover o disco.");
        }
    }
}
