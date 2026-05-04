<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Home | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<main class="container">

    <h2 style="text-align: center; font-weight: 400; color: var(--secondary-text); margin-bottom: 2.5rem;">
        E aí,
        <span style="color: var(--primary-text);">
            <c:choose>
                <c:when test="${not empty sessionScope.usuarioLogado}">${sessionScope.usuarioLogado.nome}</c:when>
                <c:otherwise>Visitante</c:otherwise>
            </c:choose>
        </span>!
        Confira as novidades que acabaram de sair
    </h2>

    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-error">${mensagemErro}</div>
    </c:if>

    <%-- ===== Seção 1: Mais vendidos ===== --%>
    <h3 class="section-title">Os discos e CDs mais vendidos desta semana</h3>
    <div class="card-grid" style="margin-bottom: 2.5rem;">
        <c:choose>
            <c:when test="${empty discos}">
                <p class="empty-state">Nenhum disco cadastrado ainda. Quando os primeiros discos forem adicionados, eles aparecerão aqui.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="disco" items="${discos}" begin="0" end="5">
                    <article class="album-card"
                             onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${disco.idDisco}'">
                        <c:choose>
                            <c:when test="${not empty disco.imagemCapa}">
                                <img src="${disco.imagemCapa}" alt="${disco.titulo}" class="album-cover">
                            </c:when>
                            <c:otherwise>
                                <div class="album-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                    <i class="fa-solid fa-compact-disc" style="font-size:3rem;"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="album-title"><c:out value="${disco.titulo}"/></div>
                        <div class="album-artist"><c:out value="${disco.artista}"/></div>
                        <div class="album-meta">
                            <c:if test="${not empty disco.anoLancamento}">
                                <span style="color: var(--secondary-text);"><c:out value="${disco.anoLancamento}"/></span>
                            </c:if>
                        </div>
                    </article>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- ===== Seção 2: Mais desejados ===== --%>
    <c:if test="${not empty discos}">
        <h3 class="section-title">Os discos e CDs mais desejados desta semana</h3>
        <div class="card-grid" style="margin-bottom: 2.5rem;">
            <c:forEach var="disco" items="${discos}" begin="6" end="11">
                <article class="album-card"
                         onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${disco.idDisco}'">
                    <c:choose>
                        <c:when test="${not empty disco.imagemCapa}">
                            <img src="${disco.imagemCapa}" alt="${disco.titulo}" class="album-cover">
                        </c:when>
                        <c:otherwise>
                            <div class="album-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                <i class="fa-solid fa-compact-disc" style="font-size:3rem;"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="album-title"><c:out value="${disco.titulo}"/></div>
                    <div class="album-artist"><c:out value="${disco.artista}"/></div>
                    <div class="album-meta">
                        <c:if test="${not empty disco.anoLancamento}">
                            <span style="color: var(--secondary-text);"><c:out value="${disco.anoLancamento}"/></span>
                        </c:if>
                    </div>
                </article>
            </c:forEach>
        </div>

        <%-- ===== Seção 3: Mais colecionados ===== --%>
        <h3 class="section-title">Os discos e CDs mais colecionados desta semana</h3>
        <div class="card-grid">
            <c:forEach var="disco" items="${discos}" begin="12" end="17">
                <article class="album-card"
                         onclick="window.location.href='${pageContext.request.contextPath}/detalhesDiscoServlet?id=${disco.idDisco}'">
                    <c:choose>
                        <c:when test="${not empty disco.imagemCapa}">
                            <img src="${disco.imagemCapa}" alt="${disco.titulo}" class="album-cover">
                        </c:when>
                        <c:otherwise>
                            <div class="album-cover" style="display:flex;align-items:center;justify-content:center;color:var(--secondary-text);">
                                <i class="fa-solid fa-compact-disc" style="font-size:3rem;"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <div class="album-title"><c:out value="${disco.titulo}"/></div>
                    <div class="album-artist"><c:out value="${disco.artista}"/></div>
                    <div class="album-meta">
                        <c:if test="${not empty disco.anoLancamento}">
                            <span style="color: var(--secondary-text);"><c:out value="${disco.anoLancamento}"/></span>
                        </c:if>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:if>

</main>

</body>
</html>
