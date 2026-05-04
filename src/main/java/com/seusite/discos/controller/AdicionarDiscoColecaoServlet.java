package com.seusite.discos.controller;

import com.seusite.discos.model.Disco;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.ColecaoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/colecao/adicionar")
public class AdicionarDiscoColecaoServlet extends HttpServlet {

    private ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        //só adiciona se estiver logado
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        try {
            // captura os dados vindos do formulário no JSP
            Disco disco = new Disco();
            disco.setDiscogsId(Integer.parseInt(request.getParameter("discogsId")));
            disco.setTitulo(request.getParameter("titulo"));
            disco.setArtista(request.getParameter("artista"));
            disco.setAnoLancamento(Integer.parseInt(request.getParameter("ano")));
            disco.setGenero(request.getParameter("genero"));
            disco.setFormato(request.getParameter("formato"));
            disco.setImagemCapa(request.getParameter("capa"));

            String estado = request.getParameter("estado");
            String obs = request.getParameter("observacao");

            // service já cuida de criar a coleção se não existir
            colecaoService.adicionarDiscoNaColecaoUnica(usuarioLogado.getIdUsuario(), disco, estado, obs);

            response.sendRedirect(request.getContextPath() + "/colecao/ver");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/busca.jsp?erro=falha_ao_adicionar");
        }
    }
}