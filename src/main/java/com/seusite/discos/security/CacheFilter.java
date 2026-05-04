package com.seusite.discos.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Aplica cabeçalhos HTTP de cache:
 *  - Arquivos estáticos (/assets/): cache público por 1 hora.
 *  - Páginas dinâmicas: no-store para evitar cache de conteúdo sensível.
 */
public class CacheFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        String uri = request.getRequestURI();

        if (uri.contains("/assets/")) {
            response.setHeader("Cache-Control", "public, max-age=3600");
        } else {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        }

        chain.doFilter(request, response);
    }
}
