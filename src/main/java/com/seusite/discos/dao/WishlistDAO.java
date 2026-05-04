package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a tabela wishlist (Favoritos do usuário).
 */
public class WishlistDAO {

    public void adicionar(int idUsuario, int idDisco) throws SQLException {
        String sql = """
                INSERT INTO wishlist (id_usuario, id_disco)
                VALUES (?, ?)
                ON CONFLICT (id_usuario, id_disco) DO NOTHING
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    public void remover(int idUsuario, int idDisco) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE id_usuario = ? AND id_disco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    public boolean existe(int idUsuario, int idDisco) throws SQLException {
        String sql = "SELECT 1 FROM wishlist WHERE id_usuario = ? AND id_disco = ? LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contar(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public List<Disco> listar(int idUsuario) throws SQLException {
        String sql = """
                SELECT d.id_disco, d.discogs_id, d.titulo, d.artista, d.ano_lancamento,
                       d.genero, d.formato, d.imagem_capa
                FROM wishlist w
                INNER JOIN disco d ON d.id_disco = w.id_disco
                WHERE w.id_usuario = ?
                ORDER BY w.data_adicao DESC
                """;
        List<Disco> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disco d = new Disco();
                    d.setIdDisco(rs.getInt("id_disco"));
                    int discogs = rs.getInt("discogs_id");
                    d.setDiscogsId(rs.wasNull() ? null : discogs);
                    d.setTitulo(rs.getString("titulo"));
                    d.setArtista(rs.getString("artista"));
                    int ano = rs.getInt("ano_lancamento");
                    d.setAnoLancamento(rs.wasNull() ? null : ano);
                    d.setGenero(rs.getString("genero"));
                    d.setFormato(rs.getString("formato"));
                    d.setImagemCapa(rs.getString("imagem_capa"));
                    lista.add(d);
                }
            }
        }
        return lista;
    }
}
