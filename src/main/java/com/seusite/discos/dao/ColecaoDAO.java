package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelas operações de Coleção do usuário.
 * Cada usuário tem uma "coleção principal" (auto-criada na primeira inserção).
 * Os discos ficam em item_colecao -> colecao -> usuario.
 */
public class ColecaoDAO {

    /**
     * Garante que o usuário tenha uma coleção principal e devolve seu ID.
     */
    public int garantirColecaoPrincipal(int idUsuario) throws SQLException {
        String sqlSelect = "SELECT id_colecao FROM colecao WHERE id_usuario = ? ORDER BY id_colecao ASC LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {

            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_colecao");
                }
            }
        }

        // Cria a coleção principal
        String sqlInsert = """
                INSERT INTO colecao (nome, descricao, id_usuario)
                VALUES (?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, "Minha Coleção");
            stmt.setString(2, "Coleção principal");
            stmt.setInt(3, idUsuario);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Não foi possível criar a coleção principal");
    }

    /**
     * Adiciona um disco à coleção principal do usuário (idempotente).
     */
    public void adicionarDisco(int idUsuario, int idDisco) throws SQLException {
        int idColecao = garantirColecaoPrincipal(idUsuario);
        String sql = """
                INSERT INTO item_colecao (id_colecao, id_disco)
                VALUES (?, ?)
                ON CONFLICT (id_colecao, id_disco) DO NOTHING
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColecao);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    public void removerDisco(int idUsuario, int idDisco) throws SQLException {
        String sql = """
                DELETE FROM item_colecao
                WHERE id_disco = ?
                  AND id_colecao IN (SELECT id_colecao FROM colecao WHERE id_usuario = ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            stmt.setInt(2, idUsuario);
            stmt.executeUpdate();
        }
    }

    public boolean possuiDisco(int idUsuario, int idDisco) throws SQLException {
        String sql = """
                SELECT 1 FROM item_colecao ic
                INNER JOIN colecao c ON c.id_colecao = ic.id_colecao
                WHERE c.id_usuario = ? AND ic.id_disco = ?
                LIMIT 1
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Lista todos os discos da coleção do usuário, com a nota dada (se existir).
     */
    public List<DiscoComNota> listarDiscosDaColecao(int idUsuario) throws SQLException {
        String sql = """
                SELECT d.id_disco, d.discogs_id, d.titulo, d.artista, d.ano_lancamento,
                       d.genero, d.formato, d.imagem_capa,
                       a.nota AS nota_usuario
                FROM item_colecao ic
                INNER JOIN colecao c ON c.id_colecao = ic.id_colecao
                INNER JOIN disco d ON d.id_disco = ic.id_disco
                LEFT JOIN avaliacao_disco a
                       ON a.id_disco = d.id_disco AND a.id_usuario = c.id_usuario
                WHERE c.id_usuario = ?
                ORDER BY ic.data_adicao DESC
                """;
        List<DiscoComNota> resultado = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disco d = mapearDisco(rs);
                    int nota = rs.getInt("nota_usuario");
                    Integer notaWrap = rs.wasNull() ? null : nota;
                    resultado.add(new DiscoComNota(d, notaWrap));
                }
            }
        }
        return resultado;
    }

    public int contarDiscosDaColecao(int idUsuario) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM item_colecao ic
                INNER JOIN colecao c ON c.id_colecao = ic.id_colecao
                WHERE c.id_usuario = ?
                """;
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

    private Disco mapearDisco(ResultSet rs) throws SQLException {
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

    /**
     * DTO para listagem com a nota do próprio usuário.
     */
    public static class DiscoComNota {
        private final Disco disco;
        private final Integer nota;

        public DiscoComNota(Disco disco, Integer nota) {
            this.disco = disco;
            this.nota = nota;
        }

        public Disco getDisco() {
            return disco;
        }

        public Integer getNota() {
            return nota;
        }
    }
}
