package com.seusite.discos.dao;

import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemColecaoDAO {

    private Connection conn;

    public ItemColecaoDAO(Connection conn) {
        this.conn = conn;
    }

    //  um disco na coleção com detalhes do estado
    public void adicionar(int idColecao, int idDisco, String estadoConservacao, String observacao) throws SQLException {
        String sql = "INSERT INTO item_colecao (id_colecao, id_disco, estado_conservacao, observacao) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColecao);
            stmt.setInt(2, idDisco);
            stmt.setString(3, estadoConservacao);
            stmt.setString(4, observacao);
            stmt.executeUpdate();
        }
    }

    // remove  o disco da coleção
    public void remover(int idColecao, int idDisco) throws SQLException {
        String sql = "DELETE FROM item_colecao WHERE id_colecao = ? AND id_disco = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColecao);
            stmt.setInt(2, idDisco);
            stmt.executeUpdate();
        }
    }

    // lista os discos que estão dentro dessa coleção
    public List<Disco> listarDiscosDaColecao(int idColecao) throws SQLException {
        List<Disco> discos = new ArrayList<>();
        
        String sql = """
            SELECT d.id_disco, d.discogs_id, d.titulo, d.artista, d.ano_lancamento, d.genero, d.formato, d.imagem_capa
            FROM disco d
            INNER JOIN item_colecao ic ON d.id_disco = ic.id_disco
            WHERE ic.id_colecao = ?
            ORDER BY ic.data_adicao DESC
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColecao);
            
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
                    discos.add(d);
                }
            }
        }
        return discos;
    }
}