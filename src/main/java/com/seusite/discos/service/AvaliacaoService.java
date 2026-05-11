package com.seusite.discos.service;

import com.seusite.discos.dao.AvaliacaoDAO;
import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.EstatisticaDisco;

import java.sql.SQLException;
import java.util.List;

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

    public EstatisticaDisco buscarEstatisticas(int idDisco) throws SQLException {
        return avaliacaoDAO.buscarEstatisticas(idDisco);
    }

    public Integer buscarNota(int idUsuario, int idDisco) throws SQLException {
        return avaliacaoDAO.buscarNotaDoUsuario(idUsuario, idDisco);
    }

    public List<AvaliacaoDisco> buscarReviews(int idDisco) throws SQLException {
        return avaliacaoDAO.buscarAvaliacoesComUsuario(idDisco);
    }

    public List<AvaliacaoDisco> buscarReviewsDoUsuario(int idUsuario) throws SQLException {
        return avaliacaoDAO.buscarAvaliacoesComDiscoDoUsuario(idUsuario);
    }

    public int contarReviews(int idUsuario) throws SQLException {
        return avaliacaoDAO.contarPorUsuario(idUsuario);
    }
}
