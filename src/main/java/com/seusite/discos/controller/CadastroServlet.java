package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;
import com.seusite.discos.util.ValidadorUtil;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Cadastra o usuário e já o coloca em sessão (login automático),
 * redirecionando para a Home.
 */
@WebServlet("/cadastroServlet")
public class CadastroServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/cadastro.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String username = request.getParameter("username");

        if (ValidadorUtil.campoVazio(nome) || ValidadorUtil.campoVazio(email)
                || ValidadorUtil.campoVazio(senha) || ValidadorUtil.campoVazio(username)) {
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=campos-vazios");
            return;
        }

        if (!ValidadorUtil.emailValido(email)) {
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=email-invalido");
            return;
        }

        if (senha.length() < 8) {
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=senha-curta");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome.trim());
        usuario.setEmail(email.trim());
        usuario.setSenha(senha);
        usuario.setUsername(username.trim());

        try {
            Usuario salvo = authService.cadastrarUsuario(usuario);

            // Login automático após cadastro
            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioLogado", salvo);
            session.setMaxInactiveInterval(60 * 30);

            response.sendRedirect(request.getContextPath() + "/listarFeedServlet");

        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "banco" : e.getMessage();
            if ("email-existente".equals(msg)) {
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=email-existente");
            } else if ("username-existente".equals(msg)) {
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=username-existente");
            } else {
                e.printStackTrace();
                response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=banco");
            }
        }
    }
}
