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

/**
 * Mapeado para "/loginServlet" para casar com o action="loginServlet" do JSP.
 * O doGet redireciona para a página login.jsp (caso alguém digite a URL direto).
 */
@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Se o usuário já está logado, manda para a home
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuarioLogado") != null) {
            response.sendRedirect(request.getContextPath() + "/listarFeedServlet");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String senha = request.getParameter("senha");

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=campos-vazios");
            return;
        }

        try {
            Usuario usuario = authService.autenticarUsuario(email.trim(), senha);

            if (usuario != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("usuarioLogado", usuario);
                session.setMaxInactiveInterval(60 * 30);

                String lembrar = request.getParameter("lembrar");

                if ("on".equalsIgnoreCase(lembrar) || "true".equalsIgnoreCase(lembrar)) {
                    String path = request.getContextPath().isEmpty() ? "/" : request.getContextPath();

                    Cookie cookieEmail = new Cookie("usuarioEmail", usuario.getEmail());
                    cookieEmail.setMaxAge(60 * 60 * 24 * 7);
                    cookieEmail.setPath(path);
                    cookieEmail.setHttpOnly(true);
                    response.addCookie(cookieEmail);

                    Cookie cookieId = new Cookie("usuarioId", String.valueOf(usuario.getIdUsuario()));
                    cookieId.setMaxAge(60 * 60 * 24 * 7);
                    cookieId.setPath(path);
                    cookieId.setHttpOnly(true);
                    response.addCookie(cookieId);
                }

                // Redireciona para a Home (feed)
                response.sendRedirect(request.getContextPath() + "/listarFeedServlet");

            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?erro=login-invalido");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=banco");
        }
    }
}
