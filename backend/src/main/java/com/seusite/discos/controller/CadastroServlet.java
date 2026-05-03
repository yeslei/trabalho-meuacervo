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

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String senha = request.getParameter("senha");
        String username = request.getParameter("username");

        // Validação de campos
        if (ValidadorUtil.campoVazio(nome) || ValidadorUtil.campoVazio(email) || ValidadorUtil.campoVazio(senha)) {
            response.sendRedirect("cadastro.jsp?erro=campos-vazios");
            return;
        }

        if (!ValidadorUtil.emailValido(email)) {
            response.sendRedirect("cadastro.jsp?erro=email-invalido");
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setUsername(username);

        try {
            authService.cadastrarUsuario(usuario);
            response.sendRedirect("login.jsp?sucesso=cadastro-realizado");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("cadastro.jsp?erro=usuario-existente");
        }
    }
}