package com.seusite.discos.security;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Protege as rotas que exigem usuario autenticado.
 *
 * Mudanca da Parte 2: numa API REST nao faz sentido redirecionar para uma
 * pagina de login (o React nao quer HTML, quer saber que a requisicao falhou).
 * Por isso, sem sessao valida, respondemos HTTP 401 + JSON e encerramos.
 *
 * O mapeamento de URLs e a ordem em relacao ao CorsFilter/EncodingFilter
 * sao definidos no web.xml (este filtro NAO usa @WebFilter de proposito).
 */
public class AuthFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        Usuario usuarioLogado = (session == null) ? null : (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "nao-autenticado", "Voce precisa estar logado para acessar este recurso.");
            return;
        }

        chain.doFilter(request, response);
    }
}
