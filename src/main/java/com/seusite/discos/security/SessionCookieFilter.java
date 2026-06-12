package com.seusite.discos.security;

import com.seusite.discos.config.EnvConfig;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class SessionCookieFilter extends HttpFilter {

    private static final boolean SAME_SITE_NONE = Boolean.parseBoolean(
            EnvConfig.get("SESSION_COOKIE_SAMESITE_NONE", "true"));

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(request, new CookieResponse(response, request));
    }

    private static class CookieResponse extends HttpServletResponseWrapper {
        private final HttpServletRequest request;

        CookieResponse(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public void addHeader(String name, String value) {
            super.addHeader(name, ajustarCookie(name, value));
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, ajustarCookie(name, value));
        }

        private String ajustarCookie(String name, String value) {
            if (!SAME_SITE_NONE || value == null || !"Set-Cookie".equalsIgnoreCase(name)
                    || !value.startsWith("JSESSIONID=")) {
                return value;
            }

            String cookie = value;
            if (!cookie.toLowerCase().contains("samesite=")) {
                cookie += "; SameSite=None";
            }
            if (deveUsarSecure() && !cookie.toLowerCase().contains("secure")) {
                cookie += "; Secure";
            }
            return cookie;
        }

        private boolean deveUsarSecure() {
            String config = EnvConfig.get("SESSION_COOKIE_SECURE", "auto");
            if ("true".equalsIgnoreCase(config)) {
                return true;
            }
            if ("false".equalsIgnoreCase(config)) {
                return false;
            }
            String forwardedProto = request.getHeader("X-Forwarded-Proto");
            return request.isSecure() || "https".equalsIgnoreCase(forwardedProto);
        }
    }
}
