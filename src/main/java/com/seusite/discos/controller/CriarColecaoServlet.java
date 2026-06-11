package com.seusite.discos.controller;

import com.seusite.discos.model.Colecao;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** POST /colecao/criar — garante a colecao principal do usuario. */
@WebServlet("/colecao/criar")
public class CriarColecaoServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        try {
            Colecao minha = colecaoService.obterOuCriarColecaoDoUsuario(usuario.getIdUsuario());
            JsonUtil.ok(response, minha);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao configurar a colecao.");
        }
    }
}
