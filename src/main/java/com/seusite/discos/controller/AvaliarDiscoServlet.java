package com.seusite.discos.controller;

import com.seusite.discos.model.AvaliacaoDisco;
import com.seusite.discos.model.Disco;
import com.seusite.discos.model.EstatisticaDisco;
import com.seusite.discos.model.Faixa;
import com.seusite.discos.model.Usuario;
import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.service.AvaliacaoService;
import com.seusite.discos.service.ColecaoService;
import com.seusite.discos.service.DiscogsService;
import com.seusite.discos.service.WishlistService;
import com.seusite.discos.util.JsonUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * /avaliar-disco
 *   GET  ?id_disco=X  — detalhes do disco + estatisticas + reviews + faixas + flags.
 *   POST              — cria/registra a avaliacao do usuario (corpo JSON).
 *   PUT               — atualiza a avaliacao do usuario (mesmo upsert do service).
 */
@WebServlet("/avaliar-disco")
public class AvaliarDiscoServlet extends HttpServlet {

    private final AvaliacaoService avaliacaoService = new AvaliacaoService();
    private final DiscoDAO discoDAO = new DiscoDAO();
    private final ColecaoService colecaoService = new ColecaoService();
    private final WishlistService wishlistService = new WishlistService();
    private final DiscogsService discogsService = new DiscogsService();

    public static class AvaliacaoRequest {
        public Integer id_disco;
        public Integer nota;
        public String comentario;
    }

    private Usuario logado(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        return s == null ? null : (Usuario) s.getAttribute("usuarioLogado");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // GET é público: devolve detalhes do disco + estatisticas públicas + reviews.
        // Dados privados (nota do usuario, flags de colecao/wishlist) só são preenchidos
        // se houver sessao ativa; caso contrario retornam nulos/false.

        HttpSession session = request.getSession(false);
        Usuario usuario = session == null ? null : (Usuario) session.getAttribute("usuarioLogado");

        int idDisco;
        try { idDisco = Integer.parseInt(request.getParameter("id_disco").trim()); }
        catch (Exception e) { JsonUtil.erro(response, 400, "id-invalido", "id_disco invalido."); return; }

        try {
            Disco disco = discoDAO.buscarPorId(idDisco);
            if (disco == null) { JsonUtil.erro(response, 404, "disco-inexistente", "Disco nao encontrado."); return; }

            EstatisticaDisco estatistica = avaliacaoService.buscarEstatisticas(idDisco);
            Integer notaUsuario = null;
            boolean estaNaColecao = false;
            boolean estaNaWishlist = false;

            if (usuario != null) {
                notaUsuario = avaliacaoService.buscarNota(usuario.getIdUsuario(), idDisco);
                estaNaColecao = colecaoService.possuiDisco(usuario.getIdUsuario(), idDisco);
                estaNaWishlist = wishlistService.possuiDisco(usuario.getIdUsuario(), idDisco);
            }

            List<AvaliacaoDisco> reviews = avaliacaoService.buscarReviews(idDisco);

            List<Faixa> faixas = Collections.emptyList();
            if (disco.getDiscogsId() != null) {
                try { faixas = discogsService.buscarTracklist(disco.getDiscogsId()); }
                catch (Exception ignored) { /* nao-critico */ }
            }

            Map<String, Object> corpo = new LinkedHashMap<>();
            corpo.put("disco", disco);
            corpo.put("estatistica", estatistica);
            corpo.put("notaUsuario", notaUsuario);
            corpo.put("reviews", reviews);
            corpo.put("estaNaColecao", estaNaColecao);
            corpo.put("estaNaWishlist", estaNaWishlist);
            corpo.put("faixas", faixas);
            JsonUtil.ok(response, corpo);
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao carregar o disco.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        salvar(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        salvar(request, response); // o service ja faz upsert (salvarOuAtualizar)
    }

    private void salvar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Usuario usuario = logado(request);
        if (usuario == null) { JsonUtil.erro(response, 401, "nao-autenticado", "Faca login."); return; }

        AvaliacaoRequest dados = JsonUtil.lerCorpo(request, AvaliacaoRequest.class);
        if (dados == null || dados.id_disco == null || dados.nota == null) {
            JsonUtil.erro(response, 400, "campos-vazios", "id_disco e nota sao obrigatorios.");
            return;
        }

        try {
            avaliacaoService.salvarAvaliacao(usuario.getIdUsuario(), dados.id_disco, dados.nota, dados.comentario);
            JsonUtil.sucesso(response, "Avaliacao registrada.");
        } catch (IllegalArgumentException e) {
            JsonUtil.erro(response, 400, e.getMessage() == null ? "validacao" : e.getMessage(), "Dados invalidos.");
        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.erro(response, 500, "banco", "Erro ao salvar a avaliacao.");
        }
    }
}
