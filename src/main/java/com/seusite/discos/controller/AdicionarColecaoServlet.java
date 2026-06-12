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
 * POST adiciona um disco na colecao com detalhes opcionais.
 * PUT atualiza estado e observacao privada do item.
 * DELETE remove o disco da colecao.
 */
@WebServlet("/colecao/adicionar")
public class AdicionarColecaoServlet extends HttpServlet {

    private final ColecaoService colecaoService = new ColecaoService();

    public static class ItemRequest {
        public Integer id_disco;
        public String estado;
        public String estado_conservacao;
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
            colecaoService.adicionarPorId(usuario.getIdUsuario(), dados.id_disco, estado(dados), dados.observacao);
            JsonUtil.sucesso(response, "Disco adicionado a colecao.");
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao adicionar a colecao.");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        if (dados == null || dados.id_disco == null) {
            JsonUtil.erro(response, 400, "id-invalido", "id_disco obrigatorio."); return;
        }

        try {
            colecaoService.atualizarDetalhesItem(usuario.getIdUsuario(), dados.id_disco, estado(dados), dados.observacao);
            JsonUtil.sucesso(response, "Detalhes da colecao atualizados.");
        } catch (IllegalArgumentException e) {
            String codigo = e.getMessage() == null ? "validacao" : e.getMessage();
            JsonUtil.erro(response, 400, codigo, mensagemPara(codigo));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao atualizar detalhes da colecao.");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        ItemRequest dados = JsonUtil.lerCorpo(request, ItemRequest.class);
        Integer idDisco = dados != null ? dados.id_disco : null;
        if (idDisco == null) {
            String q = request.getParameter("id_disco");
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

    private String estado(ItemRequest dados) {
        return dados.estado_conservacao != null ? dados.estado_conservacao : dados.estado;
    }

    private String mensagemPara(String codigo) {
        return switch (codigo) {
            case "estado-longo" -> "O estado deve ter no maximo 80 caracteres.";
            case "observacao-longa" -> "A observacao deve ter no maximo 500 caracteres.";
            case "item-inexistente" -> "Este disco nao esta na sua colecao.";
            default -> "Nao foi possivel atualizar os detalhes.";
        };
    }
}
