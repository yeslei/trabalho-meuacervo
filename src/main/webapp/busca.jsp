<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MeuAcervo - Pesquisar discos</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Sora:wght@400;600;700&family=Space+Grotesk:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/busca.css">
</head>
<body>
<header class="topbar">
    <div class="brand">MeuAcervo</div>
    <form class="search" method="get" action="${pageContext.request.contextPath}/buscar-discos">
        <input type="text" name="q" placeholder="Procure por artistas, albuns e mais..." value="<c:out value='${termoBusca}'/>">
        <button type="submit">Buscar</button>
    </form>
    <nav class="nav-links">
        <a href="${pageContext.request.contextPath}/colecao/ver">Minha colecao</a>
        <a href="${pageContext.request.contextPath}/wishlist/listar">Wishlist</a>
    </nav>
</header>

<main class="container">
    <section class="hero">
        <div>
            <h1>Pesquisar discos</h1>
            <p>Encontre albums, artistas e edicoes direto do Discogs.</p>
        </div>
        <div class="hero-card">
            Digite um termo acima e refine sua pesquisa.
        </div>
    </section>

    <c:if test="${not empty erro}">
        <div class="alert"><c:out value='${erro}'/></div>
    </c:if>

    <c:choose>
        <c:when test="${empty discos}">
            <div class="empty">Digite um termo e clique em buscar para ver resultados.</div>
        </c:when>
        <c:otherwise>
            <div class="results-head">
                <h2>Resultados</h2>
                <div>Pagina <c:out value='${paginaAtual}'/></div>
            </div>
            <div class="grid">
                <c:forEach var="disco" items="${discos}">
                    <c:url var="detalheUrl" value="/disco/abrir">
                        <c:param name="discogsId" value="${disco.discogsId}" />
                        <c:param name="titulo" value="${disco.titulo}" />
                        <c:param name="artista" value="${disco.artista}" />
                        <c:param name="ano" value="${disco.anoLancamento}" />
                        <c:param name="genero" value="${disco.genero}" />
                        <c:param name="formato" value="${disco.formato}" />
                        <c:param name="capa" value="${disco.imagemCapa}" />
                    </c:url>
                    <article class="card">
                        <a class="card-link" href="${detalheUrl}">
                            <div class="card-cover">
                                <c:choose>
                                    <c:when test="${not empty disco.imagemCapa}">
                                        <img src="<c:out value='${disco.imagemCapa}'/>" alt="Capa do disco">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="cover-fallback">Capa indisponivel</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </a>
                        <div>
                            <a class="card-title-link" href="${detalheUrl}">
                                <div class="card-title"><c:out value='${disco.titulo}'/></div>
                            </a>
                            <div class="card-artist"><c:out value='${disco.artista}'/></div>
                        </div>
                        <div class="card-meta">
                            <span class="pill">Ano: <c:out value='${disco.anoLancamento}'/></span>
                            <span class="pill"><c:out value='${disco.genero}'/></span>
                            <span class="pill"><c:out value='${disco.formato}'/></span>
                        </div>
                        <div class="card-actions">
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
                                <button class="btn primary" type="submit">Adicionar a colecao</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/wishlist/adicionar">
                                <input type="hidden" name="discogsId" value="<c:out value='${disco.discogsId}'/>">
                                <input type="hidden" name="titulo" value="<c:out value='${disco.titulo}'/>">
                                <input type="hidden" name="artista" value="<c:out value='${disco.artista}'/>">
                                <input type="hidden" name="ano" value="<c:out value='${disco.anoLancamento}'/>">
                                <input type="hidden" name="genero" value="<c:out value='${disco.genero}'/>">
                                <input type="hidden" name="formato" value="<c:out value='${disco.formato}'/>">
                                <input type="hidden" name="capa" value="<c:out value='${disco.imagemCapa}'/>">
                                <button class="btn ghost" type="submit">Adicionar na wishlist</button>
                            </form>
                            <a class="btn link" href="${detalheUrl}">Ver detalhes</a>
                        </div>
                    </article>
                </c:forEach>
            </div>

            <div class="pagination">
                <c:if test="${paginaAtual gt 1}">
                    <a href="${pageContext.request.contextPath}/buscar-discos?q=<c:out value='${termoBusca}'/>&page=${paginaAtual - 1}">Anterior</a>
                </c:if>
                <span>Pagina <c:out value='${paginaAtual}'/></span>
                <a href="${pageContext.request.contextPath}/buscar-discos?q=<c:out value='${termoBusca}'/>&page=${paginaAtual + 1}">Proxima</a>
            </div>
        </c:otherwise>
    </c:choose>
</main>
</body>
</html>
