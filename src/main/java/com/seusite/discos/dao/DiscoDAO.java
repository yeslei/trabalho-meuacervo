package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

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


// Busca específica pelo discogs_id para evitar duplicidade de discos da API externa
    public Disco buscarPorDiscogsId(Integer discogsId) throws SQLException {
        if (discogsId == null) {
            return null;
        }
        
        String sql = """
            SELECT id_disco, discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa
            FROM disco
            WHERE discogs_id = ?
            """;
            
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setInt(1, discogsId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
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
                return d;
            }
        }
    }

    //  Salva o disco novo e já devolve o ID gerado pelo Postgres
    public int salvar(Disco disco) throws SQLException {
        String sql = """
            INSERT INTO disco (discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa) 
            VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id_disco
            """;
            
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Tratamento de segurança para tipos numéricos que podem vir nulos
            if (disco.getDiscogsId() != null) {
                stmt.setInt(1, disco.getDiscogsId());
            } else {
                stmt.setNull(1, java.sql.Types.INTEGER);
            }

            stmt.setString(2, disco.getTitulo());
            stmt.setString(3, disco.getArtista());

            if (disco.getAnoLancamento() != null) {
                stmt.setInt(4, disco.getAnoLancamento());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }

            stmt.setString(5, disco.getGenero());
            stmt.setString(6, disco.getFormato());
            stmt.setString(7, disco.getImagemCapa());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_disco");
                }
            }
        }
        throw new SQLException("Falha ao salvar disco, nenhum ID retornado.");
    }

    public Map<String, Object> buscarMetricas(int idDisco) throws SQLException {
        String sql = """
            SELECT
                (SELECT COUNT(*) FROM item_colecao WHERE id_disco = ?) AS total_colecao,
                (SELECT COUNT(*) FROM wishlist WHERE id_disco = ?) AS total_wishlist,
                (SELECT COUNT(*) FROM avaliacao_disco WHERE id_disco = ?) AS total_avaliacoes,
                (SELECT COALESCE(ROUND(AVG(nota)::numeric, 2), 0) FROM avaliacao_disco WHERE id_disco = ?) AS media_avaliacao
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idDisco);
            stmt.setInt(2, idDisco);
            stmt.setInt(3, idDisco);
            stmt.setInt(4, idDisco);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int totalColecao = rs.getInt("total_colecao");
                    int totalWishlist = rs.getInt("total_wishlist");
                    int totalAvaliacoes = rs.getInt("total_avaliacoes");
                    BigDecimal mediaRaw = rs.getBigDecimal("media_avaliacao");
                    double mediaAvaliacao = mediaRaw == null ? 0.0 : mediaRaw.doubleValue();

                    Map<String, Object> metricas = new LinkedHashMap<>();
                    metricas.put("idDisco", idDisco);
                    metricas.put("totalColecao", totalColecao);
                    metricas.put("totalWishlist", totalWishlist);
                    metricas.put("totalAvaliacoes", totalAvaliacoes);
                    metricas.put("mediaAvaliacao", mediaAvaliacao);
                    return metricas;
                }
            }
        }

        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("idDisco", idDisco);
        metricas.put("totalColecao", 0);
        metricas.put("totalWishlist", 0);
        metricas.put("totalAvaliacoes", 0);
        metricas.put("mediaAvaliacao", 0.0);
        return metricas;
    }
}