package com.seusite.discos.dao;

import com.seusite.discos.model.Colecao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ColecaoDAO {

    private Connection conn;

    public ColecaoDAO(Connection conn) {
        this.conn = conn;
    }

    // cria nova coleção e retornar o ID gerado
    public int criar(Colecao colecao) throws SQLException {
        String sql = "INSERT INTO colecao (nome, descricao, id_usuario) VALUES (?, ?, ?) RETURNING id_colecao";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, colecao.getNome());
            stmt.setString(2, colecao.getDescricao());
            stmt.setInt(3, colecao.getIdUsuario());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_colecao");
                }
            }
        }
        throw new SQLException("Falha ao criar coleção.");
    }

    // busca os dados de uma coleção específica
    public Colecao buscarPorId(int idColecao) throws SQLException {
        String sql = "SELECT * FROM colecao WHERE id_colecao = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idColecao);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Colecao c = new Colecao();
                    c.setIdColecao(rs.getInt("id_colecao"));
                    c.setNome(rs.getString("nome"));
                    c.setDescricao(rs.getString("descricao"));
                    c.setIdUsuario(rs.getInt("id_usuario"));
                    c.setDataCriacao(rs.getTimestamp("data_criacao"));
                    return c;
                }
            }
        }
        return null;
    }

    // lista colecao do usuario logado- Busca Única (1 Usuário = 1 Coleção)
    public Colecao buscarPorUsuario(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM colecao WHERE id_usuario = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Colecao c = new Colecao();
                    c.setIdColecao(rs.getInt("id_colecao"));
                    c.setNome(rs.getString("nome"));
                    c.setDescricao(rs.getString("descricao"));
                    c.setIdUsuario(rs.getInt("id_usuario"));
                    c.setDataCriacao(rs.getTimestamp("data_criacao"));
                    return c;
                }
            }
        }
        return null; // Retorna null se o usuário ainda não tiver ativado sua coleção
    }
}