package com.seusite.discos.controller;

import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.dao.ItemColecaoDAO;
import com.seusite.discos.config.ConnectionFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
// Servlet para exibir a coleção do usuário logado, garantindo que cada usuário tenha apenas uma coleção principal
@WebServlet("/colecao/ver")
public class VerColecaoServlet extends HttpServlet {

    private ColecaoService colecaoService = new ColecaoService();
    private DiscogsService discogsService = new DiscogsService();

    @Override
    // recebe requisições GET, verifica o usuário logado, chama o serviço para obter ou criar a coleção e encaminha os dados para um JSP de visualização
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // verificação de Segurança 
        if (usuarioLogado == null) {
            session.setAttribute("mensagemErro", "Você precisa estar logado para acessar esta página.");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // chama o Service para obter a coleção (ou criar se for nova)
            Colecao colecao = colecaoService.obterOuCriarColecaoDoUsuario(usuarioLogado.getIdUsuario());

            //busca os discos que estão dentro dessa coleção
            // DAO de itens pega a lista completa
            try (Connection conn = ConnectionFactory.getConnection()) {
                ItemColecaoDAO itemDAO = new ItemColecaoDAO(conn);
                List<Disco> meusDiscos = itemDAO.listarDiscosDaColecao(colecao.getIdColecao());

                // envia dados para o JSP
                request.setAttribute("colecao", colecao);
                request.setAttribute("listaDiscos", meusDiscos);
                
                // Verifica se há busca de discos
                String buscar = request.getParameter("buscar");
                String termoBusca = request.getParameter("q");
                
                if (buscar != null && termoBusca != null && !termoBusca.trim().isEmpty()) {
                    try {
                        List<Disco> resultados = discogsService.buscarDiscosPorTermo(termoBusca.trim(), 1);
                        request.setAttribute("resultados", resultados);
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.setAttribute("resultados", java.util.Collections.emptyList());
                    }
                }
                
                //despacha para a página visual
                request.getRequestDispatcher("/minha-colecao.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensagemErro", "Erro ao carregar sua coleção.");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}