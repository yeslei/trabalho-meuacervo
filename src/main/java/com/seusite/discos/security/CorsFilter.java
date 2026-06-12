package com.seusite.discos.security;

import com.seusite.discos.config.EnvConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro de CORS para a arquitetura desacoplada (React em outra origem).
 *
 * Como a autenticacao usa sessao via cookie, NAO podemos liberar "*":
 * o navegador exige uma origem especifica quando Allow-Credentials = true.
 * Por isso mantemos uma lista de origens confiaveis e ecoamos a origem
 * da requisicao quando ela estiver na lista.
 *
 * Roda em "/*" e responde imediatamente as requisicoes OPTIONS (preflight).
 */
public class CorsFilter extends HttpFilter {

    private static final String ORIGENS_PADRAO = String.join(",",
            "http://localhost:5173",
            "http://127.0.0.1:5173",
            "http://localhost:3000",
            "https://yeslei.github.io"
    );

    private static final Set<String> ORIGENS_PERMITIDAS = Arrays.stream(
                    EnvConfig.get("CORS_ALLOWED_ORIGINS", ORIGENS_PADRAO).split(","))
            .map(String::trim)
            .filter(origem -> !origem.isEmpty())
            .collect(Collectors.toUnmodifiableSet());

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String origem = request.getHeader("Origin");

        if (origem != null && ORIGENS_PERMITIDAS.contains(origem)) {
            response.setHeader("Access-Control-Allow-Origin", origem);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Origin, X-Requested-With");
            response.setHeader("Access-Control-Max-Age", "3600");
            // Necessario para caches/proxies tratarem origens diferentes corretamente
            response.setHeader("Vary", "Origin");
        }

        // Requisicao de preflight: responde 200 sem seguir a cadeia
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}
