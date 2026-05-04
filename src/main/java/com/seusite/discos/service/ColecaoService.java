package com.seusite.discos.service;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.dao.ColecaoDAO;
import com.seusite.discos.dao.ItemColecaoDAO;
import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ColecaoService {

    private DiscoService discoService;

    public ColecaoService() {
        this.discoService = new DiscoService();
    }

    public Colecao obterOuCriarColecaoDoUsuario(int idUsuario) throws Exception {
        try (Connection conn = ConnectionFactory.getConnection()) {
            ColecaoDAO colecaoDAO = new ColecaoDAO(conn);
            Colecao colecaoExistente = colecaoDAO.buscarPorUsuario(idUsuario);
            if (colecaoExistente != null) {
                return colecaoExistente;
            }
            Colecao novaColecao = new Colecao();
            novaColecao.setNome("Meu Acervo");
            novaColecao.setDescricao("Coleção principal de discos");
            novaColecao.setIdUsuario(idUsuario);
            int novoId = colecaoDAO.criar(novaColecao);
            novaColecao.setIdColecao(novoId);
            return novaColecao;
        }
    }

    /** Adiciona disco da API (salva no banco se inédito). */
    public void adicionarDiscoNaColecaoUnica(int idUsuarioLogado, Disco discoDaApi, String estado, String obs) throws Exception {
        Disco discoGarantido = discoService.obterOuSalvarDisco(discoDaApi);
        try (Connection conn = ConnectionFactory.getConnection()) {
            ColecaoDAO colecaoDAO = new ColecaoDAO(conn);
            Colecao minhaColecao = colecaoDAO.buscarPorUsuario(idUsuarioLogado);
            if (minhaColecao == null) {
                minhaColecao = obterOuCriarColecaoDoUsuario(idUsuarioLogado);
            }
            ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
            itemDAO.adicionar(minhaColecao.getIdColecao(), discoGarantido.getIdDisco(), estado, obs);
        }
    }

    /** Adiciona disco já existente no banco pelo ID interno. */
    public void adicionarPorId(int idUsuario, int idDisco, String estado, String obs) throws Exception {
        try (Connection conn = ConnectionFactory.getConnection()) {
            ColecaoDAO colecaoDAO = new ColecaoDAO(conn);
            Colecao colecao = colecaoDAO.buscarPorUsuario(idUsuario);
            if (colecao == null) {
                colecao = obterOuCriarColecaoDoUsuario(idUsuario);
            }
            ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
            itemDAO.adicionar(colecao.getIdColecao(), idDisco, estado, obs);
        }
    }

    /** Remove disco da coleção do usuário. */
    public void removerDaColecaoUnica(int idUsuario, int idDisco) throws Exception {
        try (Connection conn = ConnectionFactory.getConnection()) {
            ColecaoDAO colecaoDAO = new ColecaoDAO(conn);
            Colecao colecao = colecaoDAO.buscarPorUsuario(idUsuario);
            if (colecao != null) {
                ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
                itemDAO.remover(colecao.getIdColecao(), idDisco);
            }
        }
    }

    /** Verifica se o usuário possui o disco em sua coleção. */
    public boolean possuiDisco(int idUsuario, int idDisco) throws Exception {
        String sql = """
            SELECT 1 FROM item_colecao ic
            JOIN colecao c ON ic.id_colecao = c.id_colecao
            WHERE c.id_usuario = ? AND ic.id_disco = ? LIMIT 1
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idDisco);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int contarDiscosNaColecao(int idUsuario) throws Exception {
        String sql = """
            SELECT COUNT(*) FROM item_colecao ic
            JOIN colecao c ON ic.id_colecao = c.id_colecao
            WHERE c.id_usuario = ?
            """;
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}
