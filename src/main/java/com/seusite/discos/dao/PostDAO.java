package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {

    public int inserir(Post post) throws SQLException {
        String sql = """
            INSERT INTO post (id_usuario, id_disco, titulo, conteudo)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, post.getIdUsuario());   
            stmt.setInt(2, post.getIdDisco());
            stmt.setString(3, post.getTitulo());
            stmt.setString(4, post.getConteudo());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("id_post não gerado");
    }

    public boolean existePorId(int idPost) throws SQLException {
        String sql = "SELECT 1 FROM post WHERE id_post = ? LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPost);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Post> listarFeed(int offset, int limite, Integer idDisco) throws SQLException {
        String sqlBase = """
            SELECT p.id_post, p.id_usuario, p.id_disco, p.titulo, p.conteudo, p.data_postagem,
                   u.nome AS nome_usuario,
                   d.titulo AS titulo_disco, d.artista AS artista_disco
            FROM post p
            INNER JOIN usuario u ON u.id_usuario = p.id_usuario
            INNER JOIN disco d ON d.id_disco = p.id_disco
            """;
        String sql = sqlBase + (idDisco != null
                ? " WHERE p.id_disco = ? ORDER BY p.data_postagem DESC LIMIT ? OFFSET ? "
                : " ORDER BY p.data_postagem DESC LIMIT ? OFFSET ? ");
        List<Post> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int i = 1;
            if (idDisco != null) {
                stmt.setInt(i++, idDisco);
            }
            stmt.setInt(i++, limite);
            stmt.setInt(i++, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPostCompleto(rs));
                }
            }
        }
        return lista;
    }

    public Post buscarPorIdComDetalhes(int idPost) throws SQLException {
        String sql = """
            SELECT p.id_post, p.id_usuario, p.id_disco, p.titulo, p.conteudo, p.data_postagem,
                   u.nome AS nome_usuario,
                   d.titulo AS titulo_disco, d.artista AS artista_disco
            FROM post p
            INNER JOIN usuario u ON u.id_usuario = p.id_usuario
            INNER JOIN disco d ON d.id_disco = p.id_disco
            WHERE p.id_post = ?
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPost);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapearPostCompleto(rs);
            }
        }
    }

    private Post mapearPostCompleto(ResultSet rs) throws SQLException {
        Post p = new Post();
        p.setIdPost(rs.getInt("id_post"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setIdDisco(rs.getInt("id_disco"));
        p.setTitulo(rs.getString("titulo"));
        p.setConteudo(rs.getString("conteudo"));
        p.setDataPostagem(rs.getTimestamp("data_postagem").toLocalDateTime());
        p.setNomeUsuario(rs.getString("nome_usuario"));
        p.setTituloDisco(rs.getString("titulo_disco"));
        p.setArtistaDisco(rs.getString("artista_disco"));
        return p;
    }
}
