package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.service.DiscogsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
// Servlet para lidar com buscas de discos via API do Discogs
@WebServlet("/buscar-discos")
public class BuscarDiscosServlet extends HttpServlet {

    private DiscogsService discogsService = new DiscogsService();

    @Override
    // Recebe requisições GET com o termo de busca e a página desejada, chama o serviço e encaminha os resultados para um JSP
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String termo = request.getParameter("q");
        String paginaStr = request.getParameter("page");
        String erroParam = request.getParameter("erro");

        String erroMsg = null;
        if (erroParam != null && !erroParam.trim().isEmpty()) {
            if ("disco-invalido".equals(erroParam)) {
                erroMsg = "Disco invalido para abrir.";
            } else if ("banco".equals(erroParam)) {
                erroMsg = "Erro ao acessar o banco de dados.";
            } else {
                erroMsg = "Erro ao processar a solicitacao.";
            }
        }

        int pagina = 1;
        if (paginaStr != null && !paginaStr.trim().isEmpty()) {
            try {
                pagina = Integer.parseInt(paginaStr.trim());
            } catch (NumberFormatException ignored) {
                pagina = 1;
            }
        }

        if (termo != null && !termo.trim().isEmpty()) {
            try {
                // Chama o service que já testado no main
                List<Disco> resultados = discogsService.buscarDiscosPorTermo(termo, pagina);
                
                // Pendura a lista e os metadados na requisição para o JSP usar
                request.setAttribute("discos", resultados);
                request.setAttribute("termoBusca", termo);
                request.setAttribute("paginaAtual", pagina);
                if (erroMsg != null) {
                    request.setAttribute("erro", erroMsg);
                }
                
                // Redireciona para a página de resultados
                request.getRequestDispatcher("/busca.jsp").forward(request, response);
                
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("mensagemErro", "Erro ao consultar a API: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/index.jsp");
            }
        } else {
            // Se não houver busca, apenas volta para a home ou exibe vazio
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}