package com.seusite.discos.controller;

import com.seusite.discos.dao.AvaliacaoDAO;
import com.seusite.discos.dao.ColecaoDAO;
import com.seusite.discos.dao.UsuarioDAO;
import com.seusite.discos.dao.WishlistDAO;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet do perfil do usuário. Renderiza perfil.jsp com 3 abas dinâmicas:
 *
 *   ?aba=colecao  (default)
 *   ?aba=reviews
 *   ?aba=favoritos
 *
 * Pode receber também ?username=xxx para visualizar perfil de outro usuário.
 * Sem username, mostra o perfil do usuário logado.
 */
@WebServlet("/perfilServlet")
public class PerfilServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ColecaoDAO colecaoDAO = new ColecaoDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();
    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario logado = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (logado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        // Determina de quem é o perfil que será exibido
        Usuario alvo = logado;
        String username = request.getParameter("username");
        if (username != null && !username.isBlank() && !username.equalsIgnoreCase(logado.getUsername())) {
            try {
                Usuario encontrado = usuarioDAO.buscarPorUsername(username.trim());
                if (encontrado != null) {
                    alvo = encontrado;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String aba = request.getParameter("aba");
        if (aba == null || aba.isBlank()) {
            aba = "colecao";
        }

        try {
            // Estatísticas (sempre carregadas - aparecem nos cards do topo)
            int totalDiscos = colecaoDAO.contarDiscosDaColecao(alvo.getIdUsuario());
            int totalReviews = avaliacaoDAO.contarReviewsDoUsuario(alvo.getIdUsuario());
            int totalFavoritos = wishlistDAO.contar(alvo.getIdUsuario());

            request.setAttribute("totalDiscos", totalDiscos);
            request.setAttribute("totalReviews", totalReviews);
            request.setAttribute("totalFavoritos", totalFavoritos);

            switch (aba) {
                case "reviews": {
                    List<AvaliacaoDAO.ReviewExibicao> reviews =
                            avaliacaoDAO.listarReviewsDoUsuario(alvo.getIdUsuario());
                    request.setAttribute("reviews", reviews);
                    break;
                }
                case "favoritos": {
                    List<Disco> favoritos = wishlistDAO.listar(alvo.getIdUsuario());
                    request.setAttribute("favoritos", favoritos);
                    break;
                }
                case "colecao":
                default: {
                    List<ColecaoDAO.DiscoComNota> colecao =
                            colecaoDAO.listarDiscosDaColecao(alvo.getIdUsuario());
                    request.setAttribute("colecao", colecao);
                    aba = "colecao";
                    break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("colecao", Collections.emptyList());
            request.setAttribute("reviews", Collections.emptyList());
            request.setAttribute("favoritos", Collections.emptyList());
            request.setAttribute("totalDiscos", 0);
            request.setAttribute("totalReviews", 0);
            request.setAttribute("totalFavoritos", 0);
            request.setAttribute("mensagemErro", "Erro ao carregar dados do perfil.");
        }

        request.setAttribute("usuarioLogado", logado);
        request.setAttribute("usuarioPerfil", alvo);
        request.setAttribute("ehProprioPerfil", alvo.getIdUsuario() == logado.getIdUsuario());
        request.setAttribute("abaAtiva", aba);

        request.getRequestDispatcher("/perfil.jsp").forward(request, response);
    }
}
