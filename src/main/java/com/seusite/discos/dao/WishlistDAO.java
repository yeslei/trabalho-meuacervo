package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Wishlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    private Connection conn;

    // Recebe a conexão do Service para permitir transações (Commit/Rollback)
    public WishlistDAO(Connection conn) {
        this.conn = conn;
    }

    //adiciona disco na Wishlist
    public void adicionar(int idUsuario, int idDisco) throws SQLException {
        String sql = "INSERT INTO wishlist (id_usuario, id_disco) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    // remove disco da Wishlist
    public void remover(int idUsuario, int idDisco) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE id_usuario = ? AND id_disco = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    public boolean existe(int idUsuario, int idDisco) throws SQLException {
        String sql = "SELECT 1 FROM wishlist WHERE id_usuario = ? AND id_disco = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // lista todos os discos da wishlist de um usuário específico
    public List<Disco> listarPorUsuario(int idUsuario) throws SQLException {
        List<Disco> discos = new ArrayList<>();
        
        // INNER JOIN: Busca na tabela de discos (d) cruzando com os IDs da tabela wishlist (w)
        String sql = """
            SELECT d.id_disco, d.discogs_id, d.titulo, d.artista, d.ano_lancamento, d.genero, d.formato, d.imagem_capa
            FROM disco d
            INNER JOIN wishlist w ON d.id_disco = w.id_disco
            WHERE w.id_usuario = ?
            ORDER BY w.data_adicao DESC
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Disco d = new Disco();
                    d.setIdDisco(rs.getInt("id_disco"));
                    
                    // tratamento seguro de nulos
                    int discogs = rs.getInt("discogs_id");
                    d.setDiscogsId(rs.wasNull() ? null : discogs);
                    
                    d.setTitulo(rs.getString("titulo"));
                    d.setArtista(rs.getString("artista"));
                    
                    int ano = rs.getInt("ano_lancamento");
                    d.setAnoLancamento(rs.wasNull() ? null : ano);
                    
                    d.setGenero(rs.getString("genero"));
                    d.setFormato(rs.getString("formato"));
                    d.setImagemCapa(rs.getString("imagem_capa"));
                    
                    // Adiciona o disco preenchido na lista
                    discos.add(d);
                }
            }
        }
        
        return discos; // Retorna a lista pronta para a tela exibir
    }

    public int contarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE id_usuario = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}