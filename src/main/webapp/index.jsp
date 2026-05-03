<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Home | MeuAcervo</title>
    <link rel="stylesheet" href="assets/css/style.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h2 style="text-align: center; font-weight: 400; color: var(--secondary-text); margin-bottom: 3rem;">
        E aí, <c:out value="${not empty sessionScope.usuarioLogado ? sessionScope.usuarioLogado.nome : 'Visitante'}"/>! Confira as novidades que acabaram de sair
    </h2>

    <h3 class="section-title">Novidades no Feed</h3>

    <div class="card-grid">
        <c:forEach var="post" items="${posts}">
            <article class="album-card" onclick="window.location.href='verPostServlet?id=${post.id}'">
                <img src="<c:out value='${not empty post.disco.urlCapa ? post.disco.urlCapa : "assets/img/default-cover.png"}'/>"
                     alt="${post.disco.titulo}" class="album-cover">

                <div class="album-title"><c:out value="${post.disco.titulo}"/></div>
                <div class="album-artist"><c:out value="${post.disco.artista}"/></div>

                <div class="album-meta">
                    <span class="rating-stars">
                        <c:forEach begin="1" end="5" var="i">
                            <c:choose>
                                <c:when test="${i <= post.avaliacao}">★</c:when>
                                <c:otherwise>☆</c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </span>
                    <span style="color: var(--secondary-text)"><c:out value="${post.disco.anoLancamento}"/></span>
                </div>
            </article>
        </c:forEach>

        <c:if test="${empty posts}">
            <p style="grid-column: 1/-1; text-align: center; color: var(--secondary-text);">
                Nenhum álbum encontrado no momento.
            </p>
        </c:if>
    </div>
</main>

</body>
</html>