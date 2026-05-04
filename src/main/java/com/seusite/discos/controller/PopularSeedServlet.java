// src/main/java/com/seusite/discos/controller/PopularSeedServlet.java
package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.service.DiscogsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Endpoint de uso único para popular o banco com ~12 discos iniciais do Discogs.
 * Chame uma vez após zerar o banco: GET /popularSeedServlet
 * Não requer autenticação — proteja por firewall ou remova após o seed inicial.
 */
@WebServlet("/popularSeedServlet")
public class PopularSeedServlet extends HttpServlet {

    private static final String[] TERMOS_SEED = {
        "pink floyd", "beatles", "nirvana", "bruno mars", "taylor swift", "megadeth"
    };

    private final DiscogsService discogsService = new DiscogsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("=== PopularSeedServlet — iniciando seed ===");
        out.println();

        int totalImportados = 0;

        for (String termo : TERMOS_SEED) {
            try {
                // 2 resultados por termo => ~12 discos no total
                List<Disco> discos = discogsService.buscarOuImportar(termo, 2);
                out.println("OK  [" + termo + "] -> " + discos.size() + " disco(s)");
                totalImportados += discos.size();

                // Pequena pausa para não estourar o rate limit do Discogs (60 req/min)
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                out.println("INTERROMPIDO");
                break;
            } catch (Exception e) {
                out.println("ERRO [" + termo + "] -> " + e.getMessage());
            }
        }

        out.println();
        out.println("=== Seed concluído: " + totalImportados + " disco(s) importado(s) ===");
    }
}
