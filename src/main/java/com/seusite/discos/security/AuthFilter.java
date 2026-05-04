package com.seusite.discos.security;

import com.seusite.discos.model.Usuario;

import java.io.IOException;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Verifica autenticação (sessão com usuário logado) antes de conceder acesso.
 * Rotas de administrador exigem perfil "admin" (Security Role).
 */
@WebFilter({
        "/perfilServlet",
        "/colecaoServlet",
        "/wishlistServlet",
        "/avaliarDiscoServlet",
        "/criar-post",
        "/curtir-post",
        "/post",
        "/popularSeedServlet",
        "/testar"
})
public class AuthFilter extends HttpFilter implements Filter {

    private static final Set<String> ROTAS_ADMIN = Set.of("/popularSeedServlet", "/testar");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = session == null
                ? null
                : (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        // Verifica perfil (role) para rotas restritas a administradores
        String servletPath = request.getServletPath();
        if (ROTAS_ADMIN.contains(servletPath) && !usuarioLogado.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Acesso restrito a administradores.");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}
}
