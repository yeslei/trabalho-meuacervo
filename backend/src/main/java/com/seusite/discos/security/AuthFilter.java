package com.seusite.discos.security;

import com.seusite.discos.model.Usuario;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter({
        "/minhas-colecoes",
        "/colecao",
        "/criar-colecao",
        "/adicionar-disco-colecao",
        "/feed",
        "/post",
        "/criar-post",
        "/curtir-post",
        "/avaliar-disco",
        "/feed.jsp",
        "/postDetalhe.jsp",
        "/novoPost.jsp",
        "/avaliarDisco.jsp"
})
public class AuthFilter extends HttpFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização opcional
    }

    @Override
    protected void doFilter(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        Usuario usuarioLogado = null;

        if (session != null) {
            usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        }

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Finalização opcional
    }
}