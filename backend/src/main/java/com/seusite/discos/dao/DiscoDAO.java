package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscoDAO {

    public boolean existePorId(int idDisco) throws SQLException {
        String sql = "SELECT 1 FROM disco WHERE id_disco = ? LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Disco buscarPorId(int idDisco) throws SQLException {
        String sql = """
            SELECT id_disco, discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa
            FROM disco
            WHERE id_disco = ?
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Disco d = new Disco();
                d.setIdDisco(rs.getInt("id_disco"));
                int discogs = rs.getInt("discogs_id");
                if (rs.wasNull()) {
                    d.setDiscogsId(null);
                } else {
                    d.setDiscogsId(discogs);
                }
                d.setTitulo(rs.getString("titulo"));
                d.setArtista(rs.getString("artista"));
                int ano = rs.getInt("ano_lancamento");
                if (rs.wasNull()) {
                    d.setAnoLancamento(null);
                } else {
                    d.setAnoLancamento(ano);
                }
                d.setGenero(rs.getString("genero"));
                d.setFormato(rs.getString("formato"));
                d.setImagemCapa(rs.getString("imagem_capa"));
                return d;
            }
        }
    }
}
