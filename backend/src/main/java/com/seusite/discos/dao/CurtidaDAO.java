package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CurtidaDAO {

    public void inserir(int idUsuario, int idPost) throws SQLException {
        String sql = """
            INSERT INTO curtida_post (id_usuario, id_post)
            VALUES (?, ?)
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idPost);
            stmt.executeUpdate();
        }
    }

    public int remover(int idUsuario, int idPost) throws SQLException {
        String sql = "DELETE FROM curtida_post WHERE id_usuario = ? AND id_post = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idPost);
            return stmt.executeUpdate();
        }
    }

    public boolean existe(int idUsuario, int idPost) throws SQLException {
        String sql = """
            SELECT 1 FROM curtida_post
            WHERE id_usuario = ? AND id_post = ?
            LIMIT 1
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idPost);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarPorPost(int idPost) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curtida_post WHERE id_post = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPost);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
