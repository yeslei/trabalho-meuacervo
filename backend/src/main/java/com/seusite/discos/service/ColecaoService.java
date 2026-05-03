package com.seusite.discos.service;

import com.seusite.discos.config.ConnectionFactory;
import com.seusite.discos.dao.ColecaoDAO;
import com.seusite.discos.dao.ItemColecaoDAO;
import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Disco;

import java.sql.Connection;
import java.sql.SQLException;

public class ColecaoService {

    private DiscoService discoService;

    public ColecaoService() {
        this.discoService = new DiscoService();
    }

    /**
     * Busca a coleção única do usuário. Se for o primeiro acesso, 
     * cria uma coleção padrão automaticamente
     * Regra de negócio: Cada usuário tem direito a apenas 1 coleção principal.
     */
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

    /**
     * Adiciona o disco direto na coleção única do usuário logado.
     */
    public void adicionarDiscoNaColecaoUnica(int idUsuarioLogado, Disco discoDaApi, String estado, String obs) throws Exception {
        
        // regra do disco delegada para o DiscoService centralizado
        Disco discoGarantido = discoService.obterOuSalvarDisco(discoDaApi);

        try (Connection conn = ConnectionFactory.getConnection()) {
            
            // descobre ou cria a coleção do usuário logado
            ColecaoDAO colecaoDAO = new ColecaoDAO(conn);
            Colecao minhaColecao = colecaoDAO.buscarPorUsuario(idUsuarioLogado);
            
            if (minhaColecao == null) {
                minhaColecao = obterOuCriarColecaoDoUsuario(idUsuarioLogado);
            }

            // vincular o disco à coleção única usando o ID garantido
            ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
            itemDAO.adicionar(minhaColecao.getIdColecao(), discoGarantido.getIdDisco(), estado, obs);
        }
    }
}