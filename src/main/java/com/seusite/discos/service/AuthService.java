package com.seusite.discos.service;

import com.seusite.discos.dao.UsuarioDAO;
import com.seusite.discos.model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class AuthService {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Cadastra o usuário e retorna o objeto já com o id gerado e a senha
     * em hash, pronto para ser jogado em sessão.
     */
    public Usuario cadastrarUsuario(Usuario usuario) throws SQLException {
        if (usuarioDAO.emailExiste(usuario.getEmail())) {
            throw new SQLException("email-existente");
        }

        if (usuario.getUsername() != null && !usuario.getUsername().isBlank()
                && usuarioDAO.usernameExiste(usuario.getUsername())) {
            throw new SQLException("username-existente");
        }

        String senhaCriptografada = BCrypt.hashpw(usuario.getSenha(), BCrypt.gensalt());
        usuario.setSenha(senhaCriptografada);

        usuarioDAO.criarUsuario(usuario);
        return usuario;
    }

    public Usuario autenticarUsuario(String email, String senha) throws SQLException {
        Usuario usuario = usuarioDAO.buscarPorEmail(email);

        if (usuario != null && BCrypt.checkpw(senha, usuario.getSenha())) {
            return usuario;
        }
        return null;
    }
}
