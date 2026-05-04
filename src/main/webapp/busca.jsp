<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Busca | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<main class="container">

    <h2 class="section-heading" style="text-align: left;">
        <c:choose>
            <c:when test="${empty termo}">Resultados da busca</c:when>
            <c:otherwise>Resultados para "<c:out value="${termo}"/>"</c:otherwise>
        </c:choose>
        <span class="count">${fn:length(resultados)}</span>
    </h2>

    <c:if test="${not empty avisoDiscogs}">
        <div class="alert alert-error"><c:out value="${avisoDiscogs}"/></div>
    </c:if>
    <c:if test="${not empty mensagemErro}">
        <div class="alert alert-error">${mensagemErro}</div>
    </c:if>

    <div class="card-grid">
        <c:choose>
            <c:when test="${empty termo}">
                <p class="empty-state">Digite algo no campo de busca acima para encontrar discos.</p>
            </c:when>
            <c:when test="${empty resultados}">
                <p class="empty-state">Nenhum disco encontrado para "<c:out value="${termo}"/>".</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="d" items="${resultados}">
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
                            <c:if test="${not empty d.anoLancamento}">
                                <span style="color: var(--secondary-text);"><c:out value="${d.anoLancamento}"/></span>
                            </c:if>
                        </div>
                    </article>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

</main>

</body>
</html>