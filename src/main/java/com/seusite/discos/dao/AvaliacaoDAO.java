package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AvaliacaoDAO {

    public void salvarOuAtualizar(int idUsuario, int idDisco, int nota, String comentario) throws SQLException {
        String sql = """
                INSERT INTO avaliacao_disco (id_usuario, id_disco, nota, comentario)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (id_usuario, id_disco)
                DO UPDATE SET nota = EXCLUDED.nota,
                              comentario = EXCLUDED.comentario,
                              data_avaliacao = CURRENT_TIMESTAMP
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            stmt.setInt(3, nota);
            stmt.setString(4, comentario);
            stmt.executeUpdate();
        }
    }

    public Integer notaDoUsuario(int idUsuario, int idDisco) throws SQLException {
        String sql = "SELECT nota FROM avaliacao_disco WHERE id_usuario = ? AND id_disco = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("nota");
                }
            }
        }
        return null;
    }

    /**
     * Estatística geral de um disco: média + total.
     */
    public EstatisticaDisco estatisticasDoDisco(int idDisco) throws SQLException {
        String sql = """
                SELECT COALESCE(AVG(nota), 0) AS media, COUNT(*) AS total
                FROM avaliacao_disco
                WHERE id_disco = ?
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new EstatisticaDisco(rs.getDouble("media"), rs.getInt("total"));
                }
            }
        }
        return new EstatisticaDisco(0.0, 0);
    }

    /**
     * Lista todas as reviews (avaliações com comentário ou não) de um usuário,
     * juntando dados do disco para exibição.
     */
    public List<ReviewExibicao> listarReviewsDoUsuario(int idUsuario) throws SQLException {
        String sql = """
                SELECT a.id_avaliacao, a.id_usuario, a.id_disco, a.nota, a.comentario, a.data_avaliacao,
                       d.titulo, d.artista, d.ano_lancamento, d.imagem_capa,
                       u.username
                FROM avaliacao_disco a
                INNER JOIN disco d ON d.id_disco = a.id_disco
                INNER JOIN usuario u ON u.id_usuario = a.id_usuario
                WHERE a.id_usuario = ?
                ORDER BY a.data_avaliacao DESC
                """;
        List<ReviewExibicao> resultado = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearReview(rs));
                }
            }
        }
        return resultado;
    }

    /**
     * Lista todas as reviews de um disco (de todos os usuários).
     */
    public List<ReviewExibicao> listarReviewsDoDisco(int idDisco) throws SQLException {
        String sql = """
                SELECT a.id_avaliacao, a.id_usuario, a.id_disco, a.nota, a.comentario, a.data_avaliacao,
                       d.titulo, d.artista, d.ano_lancamento, d.imagem_capa,
                       u.username
                FROM avaliacao_disco a
                INNER JOIN disco d ON d.id_disco = a.id_disco
                INNER JOIN usuario u ON u.id_usuario = a.id_usuario
                WHERE a.id_disco = ?
                ORDER BY a.data_avaliacao DESC
                """;
        List<ReviewExibicao> resultado = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado.add(mapearReview(rs));
                }
            }
        }
        return resultado;
    }

    public int contarReviewsDoUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avaliacao_disco WHERE id_usuario = ?";
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

    private ReviewExibicao mapearReview(ResultSet rs) throws SQLException {
        Disco d = new Disco();
        d.setIdDisco(rs.getInt("id_disco"));
        d.setTitulo(rs.getString("titulo"));
        d.setArtista(rs.getString("artista"));
        int ano = rs.getInt("ano_lancamento");
        d.setAnoLancamento(rs.wasNull() ? null : ano);
        d.setImagemCapa(rs.getString("imagem_capa"));

        ReviewExibicao r = new ReviewExibicao();
        r.setIdAvaliacao(rs.getInt("id_avaliacao"));
        r.setIdUsuario(rs.getInt("id_usuario"));
        r.setUsername(rs.getString("username"));
        r.setNota(rs.getInt("nota"));
        r.setComentario(rs.getString("comentario"));
        r.setDataAvaliacao(rs.getTimestamp("data_avaliacao").toLocalDateTime());
        r.setDisco(d);
        return r;
    }

    public static class EstatisticaDisco {
        private final double media;
        private final int total;

        public EstatisticaDisco(double media, int total) {
            this.media = media;
            this.total = total;
        }

        public double getMedia() {
            return media;
        }

        public int getMediaArredondada() {
            return (int) Math.round(media);
        }

        public int getTotal() {
            return total;
        }
    }

    /**
     * DTO de exibição que combina avaliação + disco + autor.
     */
    public static class ReviewExibicao {
        private int idAvaliacao;
        private int idUsuario;
        private String username;
        private int nota;
        private String comentario;
        private LocalDateTime dataAvaliacao;
        private Disco disco;

        public int getIdAvaliacao() { return idAvaliacao; }
        public void setIdAvaliacao(int idAvaliacao) { this.idAvaliacao = idAvaliacao; }
        public int getIdUsuario() { return idUsuario; }
        public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public int getNota() { return nota; }
        public void setNota(int nota) { this.nota = nota; }
        public String getComentario() { return comentario; }
        public void setComentario(String comentario) { this.comentario = comentario; }
        public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
        public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }
        public Disco getDisco() { return disco; }
        public void setDisco(Disco disco) { this.disco = disco; }
    }
}
