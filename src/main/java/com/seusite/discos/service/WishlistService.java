package com.seusite.discos.service;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.dao.WishlistDAO;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class WishlistService {

    private DiscoService discoService;

    public WishlistService() {
        this.discoService = new DiscoService();
    }

    /** Adiciona disco da API (salva no banco se inédito) e depois adiciona à wishlist. */
    public void adicionarDiscoNaWishlist(int idUsuario, Disco discoDaApi) throws SQLException {
        Disco discoGarantido = discoService.obterOuSalvarDisco(discoDaApi);
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            wishlistDAO.adicionar(idUsuario, discoGarantido.getIdDisco());
        }
    }

    /** Adiciona disco já existente no banco pelo ID interno. */
    public void adicionarPorId(int idUsuario, int idDisco) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            wishlistDAO.adicionar(idUsuario, idDisco);
        }
    }

    public void removerDiscoDaWishlist(int idUsuario, int idDisco) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            wishlistDAO.remover(idUsuario, idDisco);
        }
    }

    public boolean possuiDisco(int idUsuario, int idDisco) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            return wishlistDAO.existe(idUsuario, idDisco);
        }
    }

    public List<Disco> listarWishlistDoUsuario(int idUsuario) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            return wishlistDAO.listarPorUsuario(idUsuario);
        }
    }

    public int contarWishlist(int idUsuario) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            return wishlistDAO.contarPorUsuario(idUsuario);
        }
    }
}
