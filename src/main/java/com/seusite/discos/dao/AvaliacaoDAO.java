package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.EstatisticaDisco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public EstatisticaDisco buscarEstatisticas(int idDisco) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS total, COALESCE(AVG(nota), 0) AS media
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
        return new EstatisticaDisco(0, 0);
    }

    public Integer buscarNotaDoUsuario(int idUsuario, int idDisco) throws SQLException {
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

    public List<AvaliacaoDisco> buscarAvaliacoesComUsuario(int idDisco) throws SQLException {
        List<AvaliacaoDisco> lista = new ArrayList<>();
        String sql = """
            SELECT ad.id_avaliacao, ad.id_usuario, ad.id_disco, ad.nota, ad.comentario,
                   ad.data_avaliacao, u.username
            FROM avaliacao_disco ad
            JOIN usuario u ON ad.id_usuario = u.id_usuario
            WHERE ad.id_disco = ?
            ORDER BY ad.data_avaliacao DESC
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AvaliacaoDisco av = new AvaliacaoDisco();
                    av.setIdAvaliacao(rs.getInt("id_avaliacao"));
                    av.setIdUsuario(rs.getInt("id_usuario"));
                    av.setIdDisco(rs.getInt("id_disco"));
                    av.setNota(rs.getInt("nota"));
                    av.setComentario(rs.getString("comentario"));
                    if (rs.getTimestamp("data_avaliacao") != null) {
                        av.setDataAvaliacao(rs.getTimestamp("data_avaliacao").toLocalDateTime());
                    }
                    av.setUsername(rs.getString("username"));
                    lista.add(av);
                }
            }
        }
        return lista;
    }

    public List<AvaliacaoDisco> buscarAvaliacoesComDiscoDoUsuario(int idUsuario) throws SQLException {
        List<AvaliacaoDisco> lista = new ArrayList<>();
        String sql = """
            SELECT ad.id_avaliacao, ad.id_usuario, ad.id_disco, ad.nota, ad.comentario,
                   ad.data_avaliacao, u.username,
                   d.id_disco AS d_id, d.titulo, d.artista, d.ano_lancamento, d.imagem_capa
            FROM avaliacao_disco ad
            JOIN usuario u ON ad.id_usuario = u.id_usuario
            JOIN disco d ON ad.id_disco = d.id_disco
            WHERE ad.id_usuario = ?
            ORDER BY ad.data_avaliacao DESC
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AvaliacaoDisco av = new AvaliacaoDisco();
                    av.setIdAvaliacao(rs.getInt("id_avaliacao"));
                    av.setIdUsuario(rs.getInt("id_usuario"));
                    av.setIdDisco(rs.getInt("id_disco"));
                    av.setNota(rs.getInt("nota"));
                    av.setComentario(rs.getString("comentario"));
                    if (rs.getTimestamp("data_avaliacao") != null) {
                        av.setDataAvaliacao(rs.getTimestamp("data_avaliacao").toLocalDateTime());
                    }
                    av.setUsername(rs.getString("username"));
                    Disco d = new Disco();
                    d.setIdDisco(rs.getInt("d_id"));
                    d.setTitulo(rs.getString("titulo"));
                    d.setArtista(rs.getString("artista"));
                    int ano = rs.getInt("ano_lancamento");
                    d.setAnoLancamento(rs.wasNull() ? null : ano);
                    d.setImagemCapa(rs.getString("imagem_capa"));
                    av.setDisco(d);
                    lista.add(av);
                }
            }
        }
        return lista;
    }

    public int contarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM avaliacao_disco WHERE id_usuario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
