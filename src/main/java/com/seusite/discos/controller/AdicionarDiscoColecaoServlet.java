package com.seusite.discos.controller;

import com.seusite.discos.model.Usuario;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.util.JsonUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * /colecao/adicionar
 *   POST   — adiciona o disco a colecao do usuario (corpo JSON).
 *   DELETE — remove o disco da colecao.
 */
@WebServlet("/colecao/adicionar")
public class AdicionarDiscoColecaoServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    public static class ItemRequest {
        public Integer id_disco;
        public String estado;
        public String observacao;
    }

    private Usuario logado(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        return s == null ? null : (Usuario) s.getAttribute("usuarioLogado");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        if (dados == null || dados.id_disco == null) {
            JsonUtil.erro(response, 400, "id-invalido", "id_disco obrigatorio."); return;
        }
        try {
            colecaoService.adicionarPorId(usuario.getIdUsuario(), dados.id_disco, dados.estado, dados.observacao);
            JsonUtil.sucesso(response, "Disco adicionado a colecao.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao adicionar a colecao.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        Integer idDisco = dados != null ? dados.id_disco : null;
        if (idDisco == null) {
            String q = request.getParameter("id_disco"); // permite DELETE ?id_disco=X
            if (q != null) try { idDisco = Integer.parseInt(q.trim()); } catch (Exception ignored) {}
        }
        if (idDisco == null) { JsonUtil.erro(response, 400, "id-invalido", "id_disco obrigatorio."); return; }

        try {
            colecaoService.removerDaColecaoUnica(usuario.getIdUsuario(), idDisco);
            JsonUtil.sucesso(response, "Disco removido da colecao.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao remover da colecao.");
        }
    }
}
