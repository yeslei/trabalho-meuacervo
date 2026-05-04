package com.seusite.discos.service;

import com.seusite.discos.dao.DiscoDAO;
import com.seusite.discos.dao.PostDAO;
import com.seusite.discos.model.Post;
import com.seusite.discos.util.ValidadorUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostService {

    private static final int TAMANHO_PAGINA_PADRAO = 20;

    private final PostDAO postDAO = new PostDAO();
    private final DiscoDAO discoDAO = new DiscoDAO();

    public record FeedPagina(List<Post> posts, boolean temProxima) {
    }

    public int criarPost(int idUsuario, int idDisco, String titulo, String conteudo) throws SQLException {
        if (ValidadorUtil.campoVazio(titulo) || ValidadorUtil.campoVazio(conteudo)) {
            throw new IllegalArgumentException("titulo-conteudo-obrigatorios");
        }
        if (!discoDAO.existePorId(idDisco)) {
            throw new IllegalArgumentException("disco-inexistente");
        }
        Post p = new Post();
        p.setIdUsuario(idUsuario);
        p.setIdDisco(idDisco);
        p.setTitulo(titulo.trim());
        p.setConteudo(conteudo.trim());
        return postDAO.inserir(p);
    }

    public FeedPagina listarFeedPagina(int pagina, Integer idDisco) throws SQLException {
        if (pagina < 1) {
            pagina = 1;
        }
        int offset = (pagina - 1) * TAMANHO_PAGINA_PADRAO;
        List<Post> bruto = postDAO.listarFeed(offset, TAMANHO_PAGINA_PADRAO + 1, idDisco);
        boolean temProxima = bruto.size() > TAMANHO_PAGINA_PADRAO;
        List<Post> paginaLista = bruto;
        if (temProxima) {
            paginaLista = new ArrayList<>(bruto.subList(0, TAMANHO_PAGINA_PADRAO));
        }
        return new FeedPagina(paginaLista, temProxima);
    }

    public int getTamanhoPaginaPadrao() {
        return TAMANHO_PAGINA_PADRAO;
    }

    public Post buscarDetalhe(int idPost) throws SQLException {
        return postDAO.buscarPorIdComDetalhes(idPost);
    }
}
