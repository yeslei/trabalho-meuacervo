<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MeuAcervo - Detalhes do disco</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Sora:wght@400;600;700&family=Space+Grotesk:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/avaliarDisco.css">
</head>
<body data-disco-id="<c:out value='${disco.idDisco}'/>">
<header class="topbar">
    <div class="brand">MeuAcervo</div>
    <form class="search" method="get" action="${pageContext.request.contextPath}/buscar-discos">
        <input type="text" name="q" placeholder="Procure por artistas, albuns e mais...">
        <button type="submit">Buscar</button>
    </form>
    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/colecao/ver">Minha colecao</a>
        <a href="${pageContext.request.contextPath}/wishlist/listar">Wishlist</a>
    </nav>
</header>

<main class="container">
    <div class="alerts">
        <c:if test="${param.sucesso == '1'}">
            <div class="alert success">Avaliacao salva com sucesso.</div>
        </c:if>
        <c:if test="${param.sucesso == 'post-criado'}">
            <div class="alert success">Post publicado com sucesso.</div>
        </c:if>
        <c:if test="${not empty param.erro}">
            <div class="alert error">
                <c:choose>
                    <c:when test="${param.erro == 'nota-invalida'}">Nota invalida. Use de 1 a 5.</c:when>
                    <c:when test="${param.erro == 'disco-inexistente'}">Disco nao encontrado.</c:when>
                    <c:when test="${param.erro == 'id-disco-invalido'}">ID do disco invalido.</c:when>
                    <c:when test="${param.erro == 'titulo-conteudo-obrigatorios'}">Titulo e conteudo sao obrigatorios.</c:when>
                    <c:when test="${param.erro == 'validacao'}">Erro de validacao ao criar o post.</c:when>
                    <c:when test="${param.erro == 'banco'}">Erro ao acessar o banco de dados.</c:when>
                    <c:otherwise>Erro ao processar sua solicitacao.</c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty disco}">
            <div class="empty-state">Disco nao encontrado.</div>
        </c:when>
        <c:otherwise>
            <section class="detail-grid">
                <div>
                    <div class="album-card">
                        <div class="album-cover">
                            <c:choose>
                                <c:when test="${not empty disco.imagemCapa}">
                                    <img src="<c:out value='${disco.imagemCapa}'/>" alt="Capa do disco">
                                </c:when>
                                <c:otherwise>
                                    <div class="cover-fallback">Capa indisponivel</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div>
                            <div class="album-label">Album</div>
                            <div class="album-title"><c:out value='${disco.titulo}'/></div>
                            <div class="album-meta">
                                <span><c:out value='${disco.artista}'/></span>
                                <span>-</span>
                                <span><c:out value='${disco.anoLancamento}'/></span>
                                <span>-</span>
                                <span><c:out value='${disco.genero}'/></span>
                                <span>-</span>
                                <span><c:out value='${disco.formato}'/></span>
                            </div>
                        </div>
                    </div>

                    <div class="tracklist">
                        <h3>Lista de faixas</h3>
                        <div class="track-empty">Faixas indisponiveis no momento.</div>
                    </div>
                </div>

                <aside class="sidebar">
                    <div class="stats-card">
                        <h3>Estatisticas</h3>
                        <div class="stats-grid">
                            <div class="stat-row"><span>Possuem o disco</span><strong id="stat-colecao">--</strong></div>
                            <div class="stat-row"><span>Querem o disco</span><strong id="stat-wishlist">--</strong></div>
                            <div class="stat-row"><span>Avaliacoes</span><strong id="stat-avaliacoes">--</strong></div>
                            <div class="stat-row"><span>Nota media</span><strong id="stat-media">--</strong></div>
                        </div>
                    </div>

                    <div class="actions-card">
                        <c:choose>
                            <c:when test="${not empty disco.discogsId}">
                                <form method="post" action="${pageContext.request.contextPath}/colecao/adicionar">
                                    <input type="hidden" name="discogsId" value="<c:out value='${disco.discogsId}'/>">
                                    <input type="hidden" name="titulo" value="<c:out value='${disco.titulo}'/>">
                                    <input type="hidden" name="artista" value="<c:out value='${disco.artista}'/>">
                                    <input type="hidden" name="ano" value="<c:out value='${disco.anoLancamento}'/>">
                                    <input type="hidden" name="genero" value="<c:out value='${disco.genero}'/>">
                                    <input type="hidden" name="formato" value="<c:out value='${disco.formato}'/>">
                                    <input type="hidden" name="capa" value="<c:out value='${disco.imagemCapa}'/>">
                                    <input type="hidden" name="estado" value="Muito bom">
                                    <input type="hidden" name="observacao" value="">
                                    <button class="btn" type="submit">Adicionar a colecao</button>
                                </form>
                                <form method="post" action="${pageContext.request.contextPath}/wishlist/adicionar">
                                    <input type="hidden" name="discogsId" value="<c:out value='${disco.discogsId}'/>">
                                    <input type="hidden" name="titulo" value="<c:out value='${disco.titulo}'/>">
                                    <input type="hidden" name="artista" value="<c:out value='${disco.artista}'/>">
                                    <input type="hidden" name="ano" value="<c:out value='${disco.anoLancamento}'/>">
                                    <input type="hidden" name="genero" value="<c:out value='${disco.genero}'/>">
                                    <input type="hidden" name="formato" value="<c:out value='${disco.formato}'/>">
                                    <input type="hidden" name="capa" value="<c:out value='${disco.imagemCapa}'/>">
                                    <button class="btn secondary" type="submit">Adicionar na wishlist</button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <div class="action-muted">Disco sem referencia do Discogs para adicionar.</div>
                            </c:otherwise>
                        </c:choose>
                        <a class="btn secondary" href="#criar-post">Criar post</a>
                    </div>

                    <div class="rating-card">
                        <h3>Avaliar</h3>
                        <form method="post" action="${pageContext.request.contextPath}/avaliar-disco">
                            <input type="hidden" name="id_disco" value="<c:out value='${disco.idDisco}'/>">
                            <div class="stars">
                                <input type="radio" id="nota5" name="nota" value="5">
                                <label for="nota5">&#9733;</label>
                                <input type="radio" id="nota4" name="nota" value="4">
                                <label for="nota4">&#9733;</label>
                                <input type="radio" id="nota3" name="nota" value="3">
                                <label for="nota3">&#9733;</label>
                                <input type="radio" id="nota2" name="nota" value="2">
                                <label for="nota2">&#9733;</label>
                                <input type="radio" id="nota1" name="nota" value="1">
                                <label for="nota1">&#9733;</label>
                            </div>
                            <textarea name="comentario" placeholder="Escreva um comentario (opcional)"></textarea>
                            <button class="btn" type="submit">Salvar avaliacao</button>
                        </form>
                    </div>
                </aside>
            </section>

            <section class="comments" id="criar-post">
                <h3>Posts</h3>
                <c:if test="${mensagemErroPosts == 'banco'}">
                    <div class="alert error">Erro ao carregar posts.</div>
                </c:if>

                <form class="post-form" method="post" action="${pageContext.request.contextPath}/criar-post">
                    <input type="hidden" name="id_disco" value="<c:out value='${disco.idDisco}'/>">
                    <input type="hidden" name="voltar" value="/avaliar-disco?id_disco=${disco.idDisco}">
                    <input name="titulo" type="text" placeholder="Titulo do post" required>
                    <textarea name="conteudo" placeholder="Compartilhe sua opiniao sobre este disco" required></textarea>
                    <button class="btn" type="submit">Publicar post</button>
                </form>

                <c:choose>
                    <c:when test="${empty posts}">
                        <div class="empty">Ainda nao ha posts para este disco.</div>
                    </c:when>
                    <c:otherwise>
                        <div class="post-list">
                            <c:forEach var="post" items="${posts}">
                                <article class="post-card">
                                    <div class="post-header">
                                        <span><c:out value='${post.nomeUsuario}'/></span>
                                        <span><c:out value='${post.dataPostagem}'/></span>
                                    </div>
                                    <div class="post-title"><c:out value='${post.titulo}'/></div>
                                    <div class="post-content"><c:out value='${post.conteudo}'/></div>
                                    <div class="post-footer">
                                        <a href="${pageContext.request.contextPath}/post?id=${post.idPost}">Ver post</a>
                                    </div>
                                </article>
                            </c:forEach>
                        </div>

                        <div class="posts-pagination">
                            <c:if test="${paginaAtual gt 1}">
                                <a href="${pageContext.request.contextPath}/avaliar-disco?id_disco=${disco.idDisco}&pagina=${paginaAtual - 1}">Anterior</a>
                            </c:if>
                            <span>Pagina <c:out value='${paginaAtual}'/></span>
                            <c:if test="${temProxima}">
                                <a href="${pageContext.request.contextPath}/avaliar-disco?id_disco=${disco.idDisco}&pagina=${paginaAtual + 1}">Proxima</a>
                            </c:if>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </c:otherwise>
    </c:choose>
</main>

<script>
    (function () {
        var idDisco = Number(document.body.getAttribute("data-disco-id"));
        if (!idDisco) {
            return;
        }
        var ctx = "<c:out value='${pageContext.request.contextPath}'/>";
        fetch(ctx + "/disco/metricas?id_disco=" + idDisco)
            .then(function (response) {
                if (!response.ok) {
                    return null;
                }
                return response.json();
            })
            .then(function (data) {
                if (!data) {
                    return;
                }
                var colecao = document.getElementById("stat-colecao");
                var wishlist = document.getElementById("stat-wishlist");
                var avaliacoes = document.getElementById("stat-avaliacoes");
                var media = document.getElementById("stat-media");
                if (colecao) {
                    colecao.textContent = data.totalColecao;
                }
                if (wishlist) {
                    wishlist.textContent = data.totalWishlist;
                }
                if (avaliacoes) {
                    avaliacoes.textContent = data.totalAvaliacoes;
                }
                if (media) {
                    media.textContent = data.mediaAvaliacao;
                }
            })
            .catch(function () {
                // ignore fetch errors
            });
    })();
</script>
</body>
</html>
