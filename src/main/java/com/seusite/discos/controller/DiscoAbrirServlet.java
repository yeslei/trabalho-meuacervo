package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.service.DiscoService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * POST /disco/abrir — persiste um disco vindo do Discogs (se inedito) e
 * devolve o disco ja com o id interno, para o front navegar aos detalhes.
 */
@WebServlet("/disco/abrir")
public class DiscoAbrirServlet extends HttpServlet {

    private final DiscoService discoService = new DiscoService();

    public static class AbrirRequest {
        public Integer discogsId;
        public String titulo;
        public String artista;
        public String genero;
        public String formato;
        public String capa;
        public Integer ano;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AbrirRequest dados = JsonUtil.lerCorpo(request, AbrirRequest.class);
        if (dados == null || dados.discogsId == null) {
            JsonUtil.erro(response, HttpServletResponse.SC_BAD_REQUEST, "disco-invalido", "discogsId obrigatorio.");
            return;
        }

        Disco disco = new Disco();
        disco.setDiscogsId(dados.discogsId);
        disco.setTitulo(dados.titulo);
        disco.setArtista(dados.artista);
        disco.setGenero(dados.genero);
        disco.setFormato(dados.formato);
        disco.setImagemCapa(dados.capa);
        disco.setAnoLancamento(dados.ano);

        try {
            Disco salvo = discoService.obterOuSalvarDisco(disco);
            JsonUtil.ok(response, salvo);
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "banco", "Erro ao salvar o disco.");
        }
    }
}
