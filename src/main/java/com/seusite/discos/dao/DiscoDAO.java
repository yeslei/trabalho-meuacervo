package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
                return mapear(rs);
            }
        }
    }

    /**
     * Lista os discos mais recentes (usado no feed da Home).
     */
    public List<Disco> listarRecentes(int limite) throws SQLException {
        String sql = """
                SELECT id_disco, discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa
                FROM disco
                ORDER BY id_disco DESC
                LIMIT ?
                """;
        List<Disco> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /**
     * Busca discos por título ou artista (usado no campo "buscar").
     */
    public List<Disco> buscarPorTermo(String termo) throws SQLException {
        String sql = """
                SELECT id_disco, discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa
                FROM disco
                WHERE LOWER(titulo) LIKE ? OR LOWER(artista) LIKE ?
                ORDER BY titulo
                LIMIT 50
                """;
        List<Disco> lista = new ArrayList<>();
        String like = "%" + termo.toLowerCase().trim() + "%";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, like);
            stmt.setString(2, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Disco buscarPorDiscogsId(int discogsId) throws SQLException {
        String sql = """
                SELECT id_disco, discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa
                FROM disco
                WHERE discogs_id = ?
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discogsId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return mapear(rs);
            }
        }
    }

    /**
     * Insere um novo disco e preenche idDisco no objeto passado.
     * Usa ON CONFLICT DO NOTHING para tolerar corridas concorrentes;
     * nesse caso faz SELECT para retornar o registro já existente.
     */
    public Disco inserir(Disco disco) throws SQLException {
        String sql = """
                INSERT INTO disco (discogs_id, titulo, artista, ano_lancamento, genero, formato, imagem_capa)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (discogs_id) DO NOTHING
                RETURNING id_disco
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                    disco.setIdDisco(rs.getInt("id_disco"));
                } else if (disco.getDiscogsId() != null) {
                    // conflito: disco já existia — busca o existente
                    Disco existente = buscarPorDiscogsId(disco.getDiscogsId());
                    if (existente != null) return existente;
                }
            }
        }
        return disco;
    }

    public void atualizarFormato(int idDisco, String formato) throws SQLException {
        String sql = "UPDATE disco SET formato = ? WHERE id_disco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, formato);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    public void atualizarImagemCapa(int idDisco, String imagemCapa) throws SQLException {
        String sql = "UPDATE disco SET imagem_capa = ? WHERE id_disco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, imagemCapa);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    private Disco mapear(ResultSet rs) throws SQLException {
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
