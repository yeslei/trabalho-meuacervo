package com.seusite.discos.dao;

import com.seusite.discos.config.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}
