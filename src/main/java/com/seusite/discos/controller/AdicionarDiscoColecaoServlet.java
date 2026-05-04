package com.seusite.discos.controller;

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

    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=nao-autenticado");
            return;
        }

        int idDisco;
        try {
            idDisco = Integer.parseInt(request.getParameter("id_disco").trim());
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String acao = request.getParameter("acao");
        String estado = request.getParameter("estado");
        String obs = request.getParameter("observacao");

        try {
            if ("remover".equals(acao)) {
                colecaoService.removerDaColecaoUnica(usuario.getIdUsuario(), idDisco);
            } else {
                colecaoService.adicionarPorId(usuario.getIdUsuario(), idDisco, estado, obs);
            }
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/avaliar-disco?id_disco=" + idDisco + "&erro=banco");
        }
    }
}
