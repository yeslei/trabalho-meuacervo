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
        // Instancia o novo serviço que cuida exclusivamente dos discos
        this.discoService = new DiscoService();
    }

    /**
     * Orquestra a adição de um disco vindo da API para a Wishlist do usuário.
     */
    public void adicionarDiscoNaWishlist(int idUsuario, Disco discoDaApi) throws SQLException {
        
        // responsabilidade de descobrir se já existe ou se precisa salvar um novo delegada para DiscoService
        
        Disco discoGarantido = discoService.obterOuSalvarDisco(discoDaApi);
        
        // 2. Com a certeza do ID interno, adicionamos na Wishlist
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            wishlistDAO.adicionar(idUsuario, discoGarantido.getIdDisco());
        }
    }

    /**
     * Remove um disco específico da Wishlist do usuário.
     * pega ID interno do banco, interacao do botao de remover da wishlist
     */
    public void removerDiscoDaWishlist(int idUsuario, int idDisco) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            wishlistDAO.remover(idUsuario, idDisco);
        }
    }

    /**
     * Busca todos os discos que estão na Wishlist do usuário.
     */
    public List<Disco> listarWishlistDoUsuario(int idUsuario) throws SQLException {
        try (Connection conn = ConnectionFactory.getConnection()) {
            WishlistDAO wishlistDAO = new WishlistDAO(conn);
            return wishlistDAO.listarPorUsuario(idUsuario);
        }
    }
}