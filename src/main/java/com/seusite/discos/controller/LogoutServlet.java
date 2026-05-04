package com.seusite.discos.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/logoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        encerrarSessao(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        encerrarSessao(request, response);
    }

    private void encerrarSessao(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Limpa os cookies "Manter conectado"
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();
            for (Cookie c : cookies) {
                if ("usuarioEmail".equals(c.getName()) || "usuarioId".equals(c.getName())) {
                    Cookie remover = new Cookie(c.getName(), "");
                    remover.setMaxAge(0);
                    remover.setPath(path);
                    response.addCookie(remover);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/login.jsp?sucesso=logout");
    }
}
