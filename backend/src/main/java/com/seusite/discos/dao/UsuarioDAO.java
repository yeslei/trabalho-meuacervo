package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    // Método para criar um novo usuário
    public void criarUsuario(Usuario usuario) throws SQLException {
        String sql = """
            INSERT INTO usuario (nome, email, senha, username)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getUsername());

            stmt.executeUpdate();
        }
    }

    // Método para buscar um usuário pelo email
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = """
            SELECT id_usuario, nome, email, senha, username, data_criacao
            FROM usuario
            WHERE email = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenha(rs.getString("senha"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());

                    return usuario;
                }
            }
        }

        return null;
    }

    // Método para verificar se o email já existe
    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public boolean usernameExiste(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}