// src/main/java/com/seusite/discos/dao/FaixaDAO.java
package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.model.Faixa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FaixaDAO {

    public void inserir(Faixa faixa) throws SQLException {
        String sql = """
                INSERT INTO faixa (id_disco, numero, titulo, duracao, ordem)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, faixa.getIdDisco());
            stmt.setString(2, faixa.getNumero());
            stmt.setString(3, faixa.getTitulo());
            stmt.setString(4, faixa.getDuracao());
            stmt.setInt(5, faixa.getOrdem());
            stmt.executeUpdate();
        }
    }

    public void inserirLote(List<Faixa> faixas) throws SQLException {
        if (faixas == null || faixas.isEmpty()) return;
        String sql = """
                INSERT INTO faixa (id_disco, numero, titulo, duracao, ordem)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Faixa faixa : faixas) {
                stmt.setInt(1, faixa.getIdDisco());
                stmt.setString(2, faixa.getNumero());
                stmt.setString(3, faixa.getTitulo());
                stmt.setString(4, faixa.getDuracao());
                stmt.setInt(5, faixa.getOrdem());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public List<Faixa> listarPorDisco(int idDisco) throws SQLException {
        String sql = """
                SELECT id_faixa, id_disco, numero, titulo, duracao, ordem
                FROM faixa
                WHERE id_disco = ?
                ORDER BY ordem
                """;
        List<Faixa> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean existemPorDisco(int idDisco) throws SQLException {
        String sql = "SELECT 1 FROM faixa WHERE id_disco = ? LIMIT 1";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Faixa mapear(ResultSet rs) throws SQLException {
        Faixa f = new Faixa();
        f.setIdFaixa(rs.getInt("id_faixa"));
        f.setIdDisco(rs.getInt("id_disco"));
        f.setNumero(rs.getString("numero"));
        f.setTitulo(rs.getString("titulo"));
        f.setDuracao(rs.getString("duracao"));
        f.setOrdem(rs.getInt("ordem"));
        return f;
    }
}
