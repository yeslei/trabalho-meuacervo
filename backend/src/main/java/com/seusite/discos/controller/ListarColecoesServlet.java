package com.seusite.discos.controller;

import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.ColecaoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Servlet para listar as coleções do usuário logado, mesmo que ele tenha apenas uma coleção principal
@WebServlet("/colecao/listar")
public class ListarColecoesServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Busca a coleção do usuário
            Colecao minhaColecao = colecaoService.obterOuCriarColecaoDoUsuario(usuarioLogado.getIdUsuario());
            
            // Coloca dentro de uma lista para manter o padrão do nome "ListarColecoes"
            List<Colecao> listaDeColecoes = new ArrayList<>();
            if (minhaColecao != null) {
                listaDeColecoes.add(minhaColecao);
            }

            // Envia a lista para a view
            request.setAttribute("colecoes", listaDeColecoes);
            
            // Despacha para a página que exibe os cards de coleções
            request.getRequestDispatcher("/listar-colecoes.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao carregar suas coleções.");
        }
    }
}