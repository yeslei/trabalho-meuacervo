package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.service.DiscogsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet("/home")
public class IndexServlet extends HttpServlet {

    private final DiscogsService discogsService = new DiscogsService();

    // termos usados para popular cada seção da home
    private static final String[] TERMOS = {
        "best selling vinyl albums",
        "most wanted classic records",
        "popular music collection"
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Disco> discos = Collections.emptyList();
        try {
            // busca com o primeiro termo e retorna até 18 resultados
            List<Disco> resultados = discogsService.buscarDiscosPorTermo(TERMOS[0], 1);
            discos = resultados.subList(0, Math.min(18, resultados.size()));
        } catch (Exception e) {
            // home continua funcionando mesmo se a API falhar
            e.printStackTrace();
        }

        request.setAttribute("discos", discos);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
