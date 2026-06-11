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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** GET /colecao/listar — metadados da(s) colecao(oes) do usuario. */
@WebServlet("/colecao/listar")
public class ListarColecoesServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        try {
            Colecao minha = colecaoService.obterOuCriarColecaoDoUsuario(usuario.getIdUsuario());
            List<Colecao> lista = new ArrayList<>();
            if (minha != null) lista.add(minha);
            JsonUtil.ok(response, Map.of("colecoes", lista));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar as colecoes.");
        }
    }
}
