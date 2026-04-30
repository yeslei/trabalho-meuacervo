package com.seusite.discos.security;

import com.seusite.discos.model.Usuario;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({
        "/minhas-colecoes",
        "/colecao",
        "/criar-colecao",
        "/adicionar-disco-colecao"
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