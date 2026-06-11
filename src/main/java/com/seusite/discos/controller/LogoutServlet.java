package com.seusite.discos.controller;

import com.seusite.discos.util.JsonUtil;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * POST /logout — encerra a sessao e limpa os cookies de "lembrar".
 * Responde JSON. (Aceita tambem GET por conveniencia.)
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        encerrar(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        encerrar(request, response);
    }

    private void encerrar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();
        apagarCookie(response, "usuarioEmail", path);
        apagarCookie(response, "usuarioId", path);
        JsonUtil.sucesso(response, "Sessao encerrada.");
    }

    private void apagarCookie(HttpServletResponse response, String nome, String path) {
        Cookie c = new Cookie(nome, "");
        c.setMaxAge(0);
        c.setPath(path);
        c.setHttpOnly(true);
        response.addCookie(c);
    }
}
