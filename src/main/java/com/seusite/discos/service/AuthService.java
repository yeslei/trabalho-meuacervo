package com.seusite.discos.service;

import com.seusite.discos.dao.UsuarioDAO;
import com.seusite.discos.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Cadastro de usuário
    public void cadastrarUsuario(Usuario usuario) throws SQLException {
        if (usuarioDAO.emailExiste(usuario.getEmail())) {
            throw new IllegalArgumentException("email-existente");
        }
        if (usuario.getUsername() != null && !usuario.getUsername().isBlank()
                && usuarioDAO.usernameExiste(usuario.getUsername())) {
            throw new IllegalArgumentException("username-existente");
        }

        // Criptografa a senha
        String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaCriptografada);

        // Salva no banco
        usuarioDAO.criarUsuario(usuario);
    }

    // Autenticação de usuário (verificação de login)
    public Usuario autenticarUsuario(String email, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }
}