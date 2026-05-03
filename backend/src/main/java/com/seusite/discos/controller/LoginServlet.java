package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        try {
            Usuario usuario = authService.autenticarUsuario(email, senha);

            if (usuario != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogado", usuario);
                session.setMaxInactiveInterval(60 * 30);

                String lembrar = request.getParameter("lembrar");

                if ("true".equals(lembrar)) {
                    Cookie cookieEmail = new Cookie("usuarioEmail", usuario.getEmail());
                    cookieEmail.setMaxAge(60 * 60 * 24 * 7);
                    cookieEmail.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                    cookieEmail.setHttpOnly(true);
                    response.addCookie(cookieEmail);

                    Cookie cookieId = new Cookie("usuarioId", String.valueOf(usuario.getIdUsuario()));
                    cookieId.setMaxAge(60 * 60 * 24 * 7);
                    cookieId.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
                    cookieId.setHttpOnly(true);
                    response.addCookie(cookieId);
                }

                response.sendRedirect(request.getContextPath() + "/minhas-colecoes");

            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?erro=login-invalido");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=banco");
        }
    }
}