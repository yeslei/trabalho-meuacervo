package com.seusite.discos.service;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.model.Disco;

import java.sql.SQLException;
import java.util.Map;

public class DiscoService {

    private DiscoDAO discoDAO;

    public DiscoService() {
        this.discoDAO = new DiscoDAO(); // Instancia o DAO que fará a comunicação com o banco
    }

    /**
     * Recebe um disco vindo da API. Verifica se ele já existe no banco.
     * Se existir, retorna o disco com o ID interno. Se não, salva e retorna com o novo ID.
     */
    public Disco obterOuSalvarDisco(Disco disco) throws SQLException {
        
        // 1. Tenta buscar pelo ID único da API externa
        Disco discoExistente = discoDAO.buscarPorDiscogsId(disco.getDiscogsId());
        
        if (discoExistente != null) {
            // Completa o registro existente com os dados mais novos vindos da API.
            if (disco.getDiscogsId() != null) {
                discoExistente.setDiscogsId(disco.getDiscogsId());
            }
            if (disco.getTitulo() != null) {
                discoExistente.setTitulo(disco.getTitulo());
            }
            if (disco.getArtista() != null) {
                discoExistente.setArtista(disco.getArtista());
            }
            if (disco.getAnoLancamento() != null) {
                discoExistente.setAnoLancamento(disco.getAnoLancamento());
            }
            if (disco.getGenero() != null) {
                discoExistente.setGenero(disco.getGenero());
            }
            if (disco.getFormato() != null) {
                discoExistente.setFormato(disco.getFormato());
            }
            if (disco.getImagemCapa() != null) {
                discoExistente.setImagemCapa(disco.getImagemCapa());
            }

            discoDAO.atualizar(discoExistente);
            return discoExistente;
        } else {
            // O disco é inédito. Salvamos no banco e injetamos o ID gerado no objeto
            int novoId = discoDAO.salvar(disco);
            disco.setIdDisco(novoId);
            return disco;
        }
    }

    /**
     * Busca um disco específico pelo nosso ID interno (Chave Primária).
     */
    public Disco obterDiscoPorId(int idDisco) throws SQLException {
        // Assume que você tem um método buscarPorId() no DiscoDAO.
        // Se ainda não tiver, é um simples SELECT * FROM disco WHERE id_disco = ?
        return discoDAO.buscarPorId(idDisco);
    }

    public Map<String, Object> obterMetricasPorDisco(int idDisco) throws SQLException {
        if (!discoDAO.existePorId(idDisco)) {
            return null;
        }
        return discoDAO.buscarMetricas(idDisco);
    }
}