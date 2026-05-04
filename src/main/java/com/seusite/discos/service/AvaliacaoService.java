package com.seusite.discos.service;

import com.seusite.discos.dao.AvaliacaoDAO;
import com.seusite.discos.dao.DiscoDAO;

import java.sql.SQLException;

public class AvaliacaoService {

    private final AvaliacaoDAO avaliacaoDAO = new AvaliacaoDAO();
    private final DiscoDAO discoDAO = new DiscoDAO();

    public void salvarAvaliacao(int idUsuario, int idDisco, int nota, String comentario) throws SQLException {
        if (nota < 1 || nota > 5) {
            throw new IllegalArgumentException("nota-invalida");
        }
        if (!discoDAO.existePorId(idDisco)) {
            throw new IllegalArgumentException("disco-inexistente");
        }
        String c = comentario == null ? null : comentario.trim();
        if (c != null && c.isEmpty()) {
            c = null;
        }
        avaliacaoDAO.salvarOuAtualizar(idUsuario, idDisco, nota, c);
    }
}
