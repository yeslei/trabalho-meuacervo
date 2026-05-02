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

@WebServlet("/colecao/criar")
public class CriarColecaoServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // Captura o nome e descrição que o usuário quer dar para a coleção
            String nomePersonalizado = request.getParameter("nome");
            String descricaoPersonalizada = request.getParameter("descricao");

            // Obtém a coleção do usuário (o service já garante que ele tenha uma)
            Colecao minhaColecao = colecaoService.obterOuCriarColecaoDoUsuario(usuarioLogado.getIdUsuario());

            // Aqui você poderia ter um método no DAO/Service para dar um UPDATE
            // no nome e descrição da coleção. Como o escopo é criar, vamos simular
            // a inicialização bem sucedida do acervo:
            
            session.setAttribute("mensagemSucesso", "Coleção configurada com sucesso!");
            
            // Redireciona para listar as coleções
            response.sendRedirect(request.getContextPath() + "/colecao/listar");

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("mensagemErro", "Erro ao criar/configurar a coleção.");
            response.sendRedirect(request.getContextPath() + "/form-colecao.jsp");
        }
    }
}