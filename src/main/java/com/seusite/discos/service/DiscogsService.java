// src/main/java/com/seusite/discos/service/DiscogsService.java
package com.seusite.discos.service;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.dao.FaixaDAO;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Faixa;
import com.seusite.discos.service.dto.DiscogsRelease;
import com.seusite.discos.service.dto.DiscogsReleaseDetalhado;
import com.seusite.discos.service.dto.DiscogsTrack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscogsService {

    private final DiscoDAO discoDAO = new DiscoDAO();
    private final FaixaDAO faixaDAO = new FaixaDAO();

    // Criação lazy para que a falha de token só estoure na chamada, não no construtor do Service
    private DiscogsClient client;

    private DiscogsClient getClient() {
        if (client == null) {
            client = new DiscogsClient(); // lança IllegalStateException se DISCOGS_TOKEN não estiver set
        }
        return client;
    }

    /**
     * Busca releases no Discogs, importa os que ainda não estão no banco e retorna a lista de Disco local.
     * Lança IllegalStateException se DISCOGS_TOKEN não estiver configurado.
     * Lança RuntimeException se a API estiver fora.
     */
    public List<Disco> buscarOuImportar(String termo) throws SQLException {
        return buscarOuImportar(termo, 25);
    }

    public List<Disco> buscarOuImportar(String termo, int limite) throws SQLException {
        List<DiscogsRelease> releases;
        try {
            releases = getClient().buscar(termo, limite);
        } catch (IllegalStateException e) {
            throw e; // sem token — servlet decide o que fazer
        } catch (Exception e) {
            throw new RuntimeException("Discogs indisponível: " + e.getMessage(), e);
        }

        List<Disco> resultado = new ArrayList<>();
        for (DiscogsRelease release : releases) {
            if (release.getId() == 0) continue;

            Disco existente = discoDAO.buscarPorDiscogsId(release.getId());
            if (existente != null) {
                resultado.add(existente);
            } else {
                Disco novo = new Disco();
                novo.setDiscogsId(release.getId());
                novo.setTitulo(release.getTitle());
                novo.setArtista(release.getArtist());
                novo.setAnoLancamento(release.getYear());
                novo.setGenero(primeiroOuNull(release.getGenres()));
                novo.setFormato(null); // preenchido ao enriquecer com detalhe
                // preferir capa grande; se estiver vazia, usar thumb
                String capa = release.getCoverImage();
                if (capa == null || capa.isBlank()) capa = release.getThumb();
                novo.setImagemCapa(capa);
                discoDAO.inserir(novo);
                resultado.add(novo);
            }
        }
        return resultado;
    }

    /**
     * Busca a tracklist no Discogs (se ainda não estiver em cache) e persiste no banco.
     * Silencia erros de API para não derrubar a página de detalhes.
     */
    public Disco enriquecerComTracklist(int idDisco) throws SQLException {
        Disco disco = discoDAO.buscarPorId(idDisco);
        if (disco == null || disco.getDiscogsId() == null) return disco;
        if (faixaDAO.existemPorDisco(idDisco)) return disco;

        try {
            DiscogsReleaseDetalhado detalhe = getClient().buscarDetalhe(disco.getDiscogsId());

            List<DiscogsTrack> tracks = detalhe.getTracklist();
            if (tracks != null && !tracks.isEmpty()) {
                List<Faixa> faixas = new ArrayList<>();
                for (int i = 0; i < tracks.size(); i++) {
                    DiscogsTrack t = tracks.get(i);
                    if (t.getTitle() == null || t.getTitle().isBlank()) continue;
                    Faixa f = new Faixa();
                    f.setIdDisco(idDisco);
                    f.setNumero(t.getPosition());
                    f.setTitulo(t.getTitle());
                    f.setDuracao(t.getDuration());
                    f.setOrdem(i + 1);
                    faixas.add(f);
                }
                faixaDAO.inserirLote(faixas);
            }

            // Atualiza formato no disco se ainda não tiver
            if (disco.getFormato() == null && detalhe.getFormato() != null && !detalhe.getFormato().isBlank()) {
                discoDAO.atualizarFormato(idDisco, detalhe.getFormato());
                disco.setFormato(detalhe.getFormato());
            }

            // Atualiza capa se ainda não tiver (pode ter vindo thumb vazio na busca)
            String capaAtual = disco.getImagemCapa();
            String capaDetalhe = detalhe.getCoverImage();
            if ((capaAtual == null || capaAtual.isBlank()) && capaDetalhe != null && !capaDetalhe.isBlank()) {
                discoDAO.atualizarImagemCapa(idDisco, capaDetalhe);
                disco.setImagemCapa(capaDetalhe);
            }

        } catch (IllegalStateException e) {
            System.err.println("[DiscogsService] Token não configurado — tracklist não carregada para disco " + idDisco);
        } catch (Exception e) {
            System.err.println("[DiscogsService] Erro ao enriquecer disco " + idDisco + ": " + e.getMessage());
        }

        return disco;
    }

    private String primeiroOuNull(List<String> lista) {
        return (lista != null && !lista.isEmpty()) ? lista.get(0) : null;
    }
}
