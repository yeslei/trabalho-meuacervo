package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.WishlistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
// Servlet para remover um disco da wishlist do usuário logado

@WebServlet("/wishlist/remover")
public class RemoverWishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    // Recebe requisições POST para remover um disco da wishlist, verifica o usuário logado, valida o ID do disco e chama o serviço para realizar a remoção
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        // se não estiver logado, bloqueia a execução imediatamente
        if (usuarioLogado == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Acesso negado.");
            return;
        }
        // validação defensiva do ID do disco a ser removido
        try {
            String idDiscoStr = request.getParameter("idDisco");
            if (idDiscoStr == null || idDiscoStr.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do disco não fornecido.");
                return;
            }

            int idDiscoInterno = Integer.parseInt(idDiscoStr);

            // chama o Service usando o ID da tabela 'disco', não da API Discogs
            wishlistService.removerDiscoDaWishlist(usuarioLogado.getIdUsuario(), idDiscoInterno);

            session.setAttribute("mensagemSucesso", "Disco removido da Wishlist.");
            response.sendRedirect(request.getContextPath() + "/wishlist/listar");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensagemErro", "Falha ao remover o disco.");
            response.sendRedirect(request.getContextPath() + "/wishlist/listar");
        }
    }
}