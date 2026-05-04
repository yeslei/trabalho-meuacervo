<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${disco.titulo} | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<main class="container">

    <c:if test="${param.sucesso == 'avaliado'}">
        <div class="alert alert-success">Avaliação salva com sucesso!</div>
    </c:if>
    <c:if test="${param.sucesso == '1'}">
        <div class="alert alert-success">Atualizado com sucesso!</div>
    </c:if>
    <c:if test="${param.erro == 'nota-invalida'}">
        <div class="alert alert-error">A nota precisa estar entre 1 e 5.</div>
    </c:if>
    <c:if test="${param.erro == 'banco'}">
        <div class="alert alert-error">Erro ao acessar o banco de dados.</div>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-error">${mensagemErro}</div>
    </c:if>

    <%-- ====================================================================
         Bloco principal: capa + info + ações
         ==================================================================== --%>
    <section class="detalhes-container">

        <div>
            <c:choose>
                <c:when test="${not empty disco.imagemCapa}">
                    <img src="${disco.imagemCapa}" alt="${disco.titulo}" class="detalhes-cover">
                </c:when>
                <c:otherwise>
                    <div class="detalhes-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                        <i class="fa-solid fa-compact-disc" style="font-size:5rem;"></i>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="detalhes-info">

            <h1><c:out value="${disco.titulo}"/></h1>
            <div class="detalhes-artista"><c:out value="${disco.artista}"/></div>

            <div class="detalhes-meta">
                <c:if test="${not empty disco.anoLancamento}">
                    <span><i class="fa-regular fa-calendar"></i> <c:out value="${disco.anoLancamento}"/></span>
                </c:if>
                <c:if test="${not empty disco.genero}">
                    <span><i class="fa-solid fa-music"></i> <c:out value="${disco.genero}"/></span>
                </c:if>
                <c:if test="${not empty disco.formato}">
                    <span><i class="fa-solid fa-record-vinyl"></i> <c:out value="${disco.formato}"/></span>
                </c:if>
            </div>

            <%-- Estatísticas --%>
            <div class="detalhes-rating">
                <span class="stars">
                    <c:forEach begin="1" end="5" var="i">
                        <c:choose>
                            <c:when test="${i <= estatistica.mediaArredondada}">★</c:when>
                            <c:otherwise><span class="star-empty">★</span></c:otherwise>
                        </c:choose>
                    </c:forEach>
                </span>
                <span class="rating-numero">
                    <fmt:formatNumber value="${estatistica.media}" maxFractionDigits="1" minFractionDigits="1"/>
                </span>
                <span class="rating-total">
                    (${estatistica.total}
                    <c:choose>
                        <c:when test="${estatistica.total == 1}">avaliação</c:when>
                        <c:otherwise>avaliações</c:otherwise>
                    </c:choose>)
                </span>
            </div>

            <%-- Botões de ação --%>
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogado}">
                    <div class="detalhes-acoes">

                        <%-- Adicionar / remover da Coleção --%>
                        <c:choose>
                            <c:when test="${estaNaColecao}">
                                <form action="${pageContext.request.contextPath}/colecao/adicionar" method="POST">
                                    <input type="hidden" name="id_disco" value="${disco.idDisco}">
                                    <input type="hidden" name="acao" value="remover">
                                    <button type="submit" class="btn-acao active">
                                        <i class="fa-solid fa-check"></i> Na sua coleção
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/colecao/adicionar" method="POST">
                                    <input type="hidden" name="id_disco" value="${disco.idDisco}">
                                    <input type="hidden" name="acao" value="adicionar">
                                    <button type="submit" class="btn-acao">
                                        <i class="fa-solid fa-plus"></i> Adicionar à Coleção
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>

                        <%-- Adicionar / remover da Wishlist --%>
                        <c:choose>
                            <c:when test="${estaNaWishlist}">
                                <form action="${pageContext.request.contextPath}/wishlist/adicionar" method="POST">
                                    <input type="hidden" name="id_disco" value="${disco.idDisco}">
                                    <input type="hidden" name="acao" value="remover">
                                    <button type="submit" class="btn-acao active">
                                        <i class="fa-solid fa-heart"></i> Nos seus favoritos
                                    </button>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <form action="${pageContext.request.contextPath}/wishlist/adicionar" method="POST">
                                    <input type="hidden" name="id_disco" value="${disco.idDisco}">
                                    <input type="hidden" name="acao" value="adicionar">
                                    <button type="submit" class="btn-acao">
                                        <i class="fa-regular fa-heart"></i> Adicionar aos Favoritos
                                    </button>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-error" style="max-width: 500px;">
                        <a href="${pageContext.request.contextPath}/login.jsp" style="color: var(--primary-accent);">Faça login</a>
                        para adicionar este disco à sua coleção ou aos favoritos.
                    </div>
                </c:otherwise>
            </c:choose>

            <%-- Tracklist (placeholder até existir tabela faixa no banco) --%>
            <div class="tracklist">
                <h3><i class="fa-solid fa-list-ol"></i> Tracklist</h3>
                <c:choose>
                    <c:when test="${not empty faixas}">
                        <c:forEach var="f" items="${faixas}" varStatus="loop">
                            <div class="track-row">
                                <span class="track-num">${loop.count}.</span>
                                <span class="track-titulo"><c:out value="${f.titulo}"/></span>
                                <span class="track-duracao"><c:out value="${f.duracao}"/></span>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p style="color: var(--secondary-text); font-size: 0.85rem; padding: 0.5rem 0;">
                            A tracklist deste disco ainda não foi cadastrada.
                        </p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>

    <%-- ====================================================================
         Formulário de avaliação
         ==================================================================== --%>
    <c:if test="${not empty sessionScope.usuarioLogado}">
        <section class="form-avaliar">
            <h3>
                <c:choose>
                    <c:when test="${not empty notaUsuario}">Atualizar minha avaliação</c:when>
                    <c:otherwise>Deixar minha avaliação</c:otherwise>
                </c:choose>
            </h3>

            <form action="${pageContext.request.contextPath}/avaliar-disco" method="POST">
                <input type="hidden" name="id_disco" value="${disco.idDisco}">

                <%-- 5 radios em RTL para que o hover acenda da direita pra esquerda --%>
                <div class="star-input">
                    <input type="radio" id="nota5" name="nota" value="5" <c:if test="${notaUsuario == 5}">checked</c:if>>
                    <label for="nota5">★</label>
                    <input type="radio" id="nota4" name="nota" value="4" <c:if test="${notaUsuario == 4}">checked</c:if>>
                    <label for="nota4">★</label>
                    <input type="radio" id="nota3" name="nota" value="3" <c:if test="${notaUsuario == 3}">checked</c:if>>
                    <label for="nota3">★</label>
                    <input type="radio" id="nota2" name="nota" value="2" <c:if test="${notaUsuario == 2}">checked</c:if>>
                    <label for="nota2">★</label>
                    <input type="radio" id="nota1" name="nota" value="1" <c:if test="${notaUsuario == 1}">checked</c:if>>
                    <label for="nota1">★</label>
                </div>

                <div class="form-group">
                    <label for="comentario">Comentário (opcional)</label>
                    <textarea id="comentario" name="comentario" placeholder="O que você achou deste disco?"></textarea>
                </div>

                <button type="submit" class="btn-primary" style="max-width: 200px;">Salvar avaliação</button>
            </form>
        </section>
    </c:if>

    <%-- ====================================================================
         Reviews / comentários do disco
         ==================================================================== --%>
    <section>
        <h2 class="section-heading" style="text-align: left; margin-top: 2rem;">
            <i class="fa-regular fa-comments"></i> Comentários
            <span class="count">${estatistica.total}</span>
        </h2>

        <c:choose>
            <c:when test="${empty reviews}">
                <p class="empty-state">Ainda não há comentários sobre este disco. Seja o primeiro!</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="r" items="${reviews}">
                    <article class="review-card">
                        <c:choose>
                            <c:when test="${not empty disco.imagemCapa}">
                                <img src="${disco.imagemCapa}" alt="${disco.titulo}" class="review-cover">
                            </c:when>
                            <c:otherwise>
                                <div class="review-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                    <i class="fa-regular fa-user" style="font-size:2rem;"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="review-body">
                            <div class="review-header">
                                <div>
                                    <span class="review-disco-titulo">@<c:out value="${r.username}"/></span>
                                </div>
                                <div class="review-stars">
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${i <= r.nota}">★</c:when>
                                            <c:otherwise><span class="star-empty">★</span></c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                            </div>
                            <c:if test="${not empty r.comentario}">
                                <p class="review-comentario"><c:out value="${r.comentario}"/></p>
                            </c:if>
                        </div>
                    </article>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </section>

</main>

</body>
</html>
