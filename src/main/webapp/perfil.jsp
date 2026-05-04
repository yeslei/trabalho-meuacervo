<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>${usuarioPerfil.nome} | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<main class="container">

    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-error">${mensagemErro}</div>
    </c:if>

    <%-- ====================================================================
         Cabeçalho do perfil: avatar + nome + meta + bio + seguidores
         ==================================================================== --%>
    <section class="perfil-header">
        <div class="perfil-avatar">
            <c:out value="${fn:toUpperCase(fn:substring(usuarioPerfil.nome, 0, 1))}"/><%--
            --%><c:if test="${fn:contains(usuarioPerfil.nome, ' ')}"><%--
                --%><c:out value="${fn:toUpperCase(fn:substring(fn:substringAfter(usuarioPerfil.nome, ' '), 0, 1))}"/><%--
            --%></c:if>
        </div>

        <div class="perfil-info">
            <h1><c:out value="${usuarioPerfil.nome}"/></h1>

            <div class="perfil-meta">
                <span>@<c:out value="${usuarioPerfil.username}"/></span>
                <span>•</span>
                <c:choose>
                    <c:when test="${not empty usuarioPerfil.dataCriacao}">
                        <span>Membro desde
                            <fmt:parseDate value="${usuarioPerfil.dataCriacao}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="dataCriada" type="both"/>
                            <fmt:formatDate value="${dataCriada}" pattern="yyyy"/>
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span>Membro</span>
                    </c:otherwise>
                </c:choose>
            </div>

            <p class="perfil-bio">
                <c:out value="Colecionador apaixonado por música. Bem-vindo ao meu acervo!"/>
            </p>
        </div>
    </section>

    <%-- ====================================================================
         3 cards de estatísticas
         ==================================================================== --%>
    <section class="stats-grid">
        <a href="${pageContext.request.contextPath}/perfilServlet?aba=colecao&username=${usuarioPerfil.username}"
           class="stat-card" style="text-decoration: none;">
            <div class="stat-icon"><i class="fa-solid fa-compact-disc"></i></div>
            <div class="stat-numero">${totalDiscos}</div>
            <div class="stat-label">Discos na Coleção</div>
        </a>

        <a href="${pageContext.request.contextPath}/perfilServlet?aba=reviews&username=${usuarioPerfil.username}"
           class="stat-card" style="text-decoration: none;">
            <div class="stat-icon"><i class="fa-solid fa-pen"></i></div>
            <div class="stat-numero">${totalReviews}</div>
            <div class="stat-label">Reviews Escritos</div>
        </a>

        <a href="${pageContext.request.contextPath}/perfilServlet?aba=favoritos&username=${usuarioPerfil.username}"
           class="stat-card" style="text-decoration: none;">
            <div class="stat-icon"><i class="fa-solid fa-heart"></i></div>
            <div class="stat-numero">${totalFavoritos}</div>
            <div class="stat-label">Favoritos</div>
        </a>
    </section>

    <%-- ====================================================================
         Abas
         ==================================================================== --%>
    <nav class="tabs">
        <a href="${pageContext.request.contextPath}/perfilServlet?aba=colecao&username=${usuarioPerfil.username}"
           class="tab-link <c:if test='${abaAtiva == "colecao"}'>active</c:if>">Coleção</a>
        <a href="${pageContext.request.contextPath}/perfilServlet?aba=reviews&username=${usuarioPerfil.username}"
           class="tab-link <c:if test='${abaAtiva == "reviews"}'>active</c:if>">Reviews</a>
        <a href="${pageContext.request.contextPath}/perfilServlet?aba=favoritos&username=${usuarioPerfil.username}"
           class="tab-link <c:if test='${abaAtiva == "favoritos"}'>active</c:if>">Favoritos</a>
    </nav>

    <%-- ====================================================================
         Conteúdo dinâmico por aba
         ==================================================================== --%>
    <c:choose>

        <%-- ============= ABA COLEÇÃO ============= --%>
        <c:when test="${abaAtiva == 'colecao'}">
            <h2 class="section-heading">
                Coleção de <c:out value="${usuarioPerfil.nome}"/>
                <span class="count">${totalDiscos} discos</span>
            </h2>

            <div class="card-grid">
                <c:choose>
                    <c:when test="${empty colecao}">
                        <p class="empty-state">
                            <c:choose>
                                <c:when test="${ehProprioPerfil}">
                                    Você ainda não tem discos na coleção. Comece adicionando o primeiro!
                                </c:when>
                                <c:otherwise>
                                    Este usuário ainda não tem discos na coleção.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="item" items="${colecao}">
                            <article class="album-card"
                                     onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${item.disco.idDisco}'">
                                <c:choose>
                                    <c:when test="${not empty item.disco.imagemCapa}">
                                        <img src="${item.disco.imagemCapa}" alt="${item.disco.titulo}" class="album-cover">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="album-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                            <i class="fa-solid fa-compact-disc" style="font-size:3rem;"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="album-title"><c:out value="${item.disco.titulo}"/></div>
                                <div class="album-artist"><c:out value="${item.disco.artista}"/></div>
                                <div class="album-meta">
                                    <span class="rating-stars">
                                        <c:forEach begin="1" end="5" var="i">
                                            <c:choose>
                                                <c:when test="${not empty item.nota and i <= item.nota}">★</c:when>
                                                <c:otherwise><span class="star-empty">★</span></c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </span>
                                    <i class="fa-regular fa-heart heart-toggle" style="font-size: 0.9rem;"></i>
                                </div>
                            </article>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:when>

        <%-- ============= ABA REVIEWS ============= --%>
        <c:when test="${abaAtiva == 'reviews'}">
            <h2 class="section-heading">
                Confira as opiniões de <c:out value="${usuarioPerfil.nome}"/>
                <span class="count">${totalReviews} reviews</span>
            </h2>

            <c:choose>
                <c:when test="${empty reviews}">
                    <p class="empty-state">
                        <c:choose>
                            <c:when test="${ehProprioPerfil}">
                                Você ainda não escreveu nenhum review. Que tal avaliar o primeiro disco da sua coleção?
                            </c:when>
                            <c:otherwise>
                                Este usuário ainda não escreveu nenhum review.
                            </c:otherwise>
                        </c:choose>
                    </p>
                </c:when>
                <c:otherwise>
                    <c:forEach var="r" items="${reviews}">
                        <article class="review-card"
                                 onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${r.disco.idDisco}'"
                                 style="cursor: pointer;">
                            <c:choose>
                                <c:when test="${not empty r.disco.imagemCapa}">
                                    <img src="${r.disco.imagemCapa}" alt="${r.disco.titulo}" class="review-cover">
                                </c:when>
                                <c:otherwise>
                                    <div class="review-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                        <i class="fa-solid fa-compact-disc" style="font-size:2rem;"></i>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="review-body">
                                <div class="review-header">
                                    <div>
                                        <span class="review-disco-titulo"><c:out value="${r.disco.titulo}"/></span>
                                        <c:if test="${not empty r.disco.anoLancamento}">
                                            <span class="review-ano"><c:out value="${r.disco.anoLancamento}"/></span>
                                        </c:if>
                                        <div class="review-artista"><c:out value="${r.disco.artista}"/></div>
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

                                <div class="review-meta">
                                    <i class="fa-regular fa-user"></i>
                                    <span>@<c:out value="${r.username}"/></span>
                                </div>

                                <c:if test="${not empty r.comentario}">
                                    <p class="review-comentario"><c:out value="${r.comentario}"/></p>
                                </c:if>

                                <div class="review-footer">
                                    <span class="like-counter"><i class="fa-solid fa-heart"></i> 0</span>
                                    <span><i class="fa-solid fa-share"></i> Compartilhar</span>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </c:when>

        <%-- ============= ABA FAVORITOS ============= --%>
        <c:when test="${abaAtiva == 'favoritos'}">
            <h2 class="section-heading">
                Os favoritos de <c:out value="${usuarioPerfil.nome}"/>
                <span class="count">${totalFavoritos} favoritos</span>
            </h2>

            <div class="card-grid">
                <c:choose>
                    <c:when test="${empty favoritos}">
                        <p class="empty-state">
                            <c:choose>
                                <c:when test="${ehProprioPerfil}">
                                    Você ainda não favoritou nenhum disco.
                                </c:when>
                                <c:otherwise>
                                    Este usuário ainda não favoritou nenhum disco.
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="d" items="${favoritos}">
                            <article class="album-card"
                                     onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${d.idDisco}'">
                                <c:choose>
                                    <c:when test="${not empty d.imagemCapa}">
                                        <img src="${d.imagemCapa}" alt="${d.titulo}" class="album-cover">
                                    </c:when>
                                    <c:otherwise>
                                        <div class="album-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                            <i class="fa-solid fa-compact-disc" style="font-size:3rem;"></i>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div class="album-title"><c:out value="${d.titulo}"/></div>
                                <div class="album-artist"><c:out value="${d.artista}"/></div>
                                <div class="album-meta">
                                    <i class="fa-solid fa-heart heart-toggle active" style="font-size: 1rem;"></i>
                                </div>
                            </article>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:when>

    </c:choose>

</main>

</body>
</html>