package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.WishlistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;



// Servlet para listar a coleção de desejos do usuário logado
@WebServlet("/wishlist/listar")
public class ListarWishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    // Recebe requisições GET, verifica o usuário logado, chama o serviço para obter a wishlist e encaminha os dados para um JSP de visualização
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            session.setAttribute("mensagemErro", "Você precisa estar logado para acessar esta página.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Busca a coleção de desejos baseada na inteligência do Service/DAO
            List<Disco> minhaWishlist = wishlistService.listarWishlistDoUsuario(usuarioLogado.getIdUsuario());

            request.setAttribute("perfilUsuario", usuarioLogado);
            request.setAttribute("abaAtual", "favoritos");
            request.setAttribute("ehProprioPerfil", Boolean.TRUE);
            request.setAttribute("favoritos", minhaWishlist);
            request.setAttribute("colecao", java.util.Collections.emptyList());
            request.setAttribute("reviews", java.util.Collections.emptyList());
            request.setAttribute("totalDiscos", 0);
            request.setAttribute("totalReviews", 0);
            request.setAttribute("totalFavoritos", minhaWishlist.size());
            
            // Repassa para a View fazer o trabalho visual
            request.getRequestDispatcher("/perfil.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirecionamento com mensagem de erro na sessão para evitar tela de erro do servidor
            session.setAttribute("mensagemErro", "Não foi possível carregar a sua lista de desejos no momento.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
