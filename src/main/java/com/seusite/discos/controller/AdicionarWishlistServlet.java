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

@WebServlet("/wishlist/adicionar")
public class AdicionarWishlistServlet extends HttpServlet {

    private final WishlistService wishlistService = new WishlistService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // se não estiver logado, bloqueia a execução imediatamente
        if (usuarioLogado == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Você precisa estar logado para adicionar à wishlist.");
            return;
        }

        try {
            // validação defensiva do ID (campo obrigatório da API)
            String discogsIdStr = request.getParameter("discogsId");
            if (discogsIdStr == null || discogsIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("ID do Discogs é obrigatório.");
            }

            // construção segura do objeto (tratando nulos em números)
            Disco disco = new Disco();
            disco.setDiscogsId(Integer.parseInt(discogsIdStr));
            disco.setTitulo(request.getParameter("titulo"));
            disco.setArtista(request.getParameter("artista"));
            disco.setGenero(request.getParameter("genero"));
            disco.setFormato(request.getParameter("formato"));
            disco.setImagemCapa(request.getParameter("capa"));
            
            String anoStr = request.getParameter("ano");
            if (anoStr != null && !anoStr.trim().isEmpty()) {
                disco.setAnoLancamento(Integer.parseInt(anoStr));
            }

            // delega a lógica pesada para o Service
            wishlistService.adicionarDiscoNaWishlist(usuarioLogado.getIdUsuario(), disco);

            // padrão PRG (Post/Redirect/Get): Redireciona em vez de fazer Forward
            // evita que um "F5" reenvie o disco pro banco
            session.setAttribute("mensagemSucesso", "Disco adicionado à sua Wishlist!");
            response.sendRedirect(request.getContextPath() + "/wishlist/listar");

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de número inválido nos dados do disco.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensagemErro", "Erro ao adicionar o disco: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/busca.jsp");
        }
    }
}