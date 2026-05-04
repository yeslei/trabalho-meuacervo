package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.AuthService;
import com.seusite.discos.util.ValidadorUtil;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome     = request.getParameter("nome");
        String email    = request.getParameter("email");
        String senha    = request.getParameter("senha");
        String username = request.getParameter("username");

        if (ValidadorUtil.campoVazio(nome) || ValidadorUtil.campoVazio(email) || ValidadorUtil.campoVazio(senha)) {
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
        usuario.setUsername(username == null ? null : username.trim());

        try {
            authService.cadastrarUsuario(usuario);
            Usuario criado = authService.autenticarUsuario(email.trim(), senha);
            if (criado != null) {
                HttpSession session = request.getSession();
                session.setAttribute("usuarioLogado", criado);
                session.setMaxInactiveInterval(60 * 30);
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?sucesso=cadastro-realizado");
            }
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "banco" : e.getMessage();
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=" + codigo);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/cadastro.jsp?erro=banco");
        }
    }
}
