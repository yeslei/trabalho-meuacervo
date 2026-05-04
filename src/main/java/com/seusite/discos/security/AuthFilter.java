package com.seusite.discos.security;

import com.seusite.discos.dao.UsuarioDAO;
import com.seusite.discos.model.Usuario;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({
        "/home",
        "/colecao/ver",
        "/colecao/listar",
        "/colecao/criar",
        "/colecao/adicionar",
        "/feed",
        "/perfil/reviews",
        "/post",
        "/criar-post",
        "/curtir-post",
        "/avaliar-disco",
        "/buscar-discos",
        "/disco/metricas",
        "/wishlist/adicionar",
        "/wishlist/remover",
        "/wishlist/listar"
})
public class AuthFilter extends HttpFilter implements Filter {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            usuarioLogado = restaurarSessaoPorCookie(request, response);
        }

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        chain.doFilter(request, response);
    }

    private Usuario restaurarSessaoPorCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        String usuarioIdStr = null;
        for (Cookie c : cookies) {
            if ("usuarioId".equals(c.getName())) {
                usuarioIdStr = c.getValue();
                break;
            }
        }
        if (usuarioIdStr == null) return null;

        try {
            int idUsuario = Integer.parseInt(usuarioIdStr);
            Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
            if (usuario != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuarioLogado", usuario);
                session.setMaxInactiveInterval(60 * 60 * 24 * 7);
            }
            return usuario;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void destroy() {
    }
}
