<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Home | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
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

    <%-- Macro para renderizar um card de disco (evita repetição) --%>

    <%-- ===== Seção 1: Mais vendidos ===== --%>
    <h3 class="section-title">Os discos e CDs mais vendidos desta semana</h3>
    <div class="card-grid" style="margin-bottom: 2.5rem;">
        <c:choose>
            <c:when test="${empty discos}">
                <p class="empty-state">Nenhum disco disponível no momento. Tente buscar por artistas ou álbuns.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="d" items="${discos}" begin="0" end="5">
                    <c:url var="detalheUrl" value="/disco/abrir">
                        <c:param name="discogsId" value="${d.discogsId}"/>
                        <c:param name="titulo"    value="${d.titulo}"/>
                        <c:param name="artista"   value="${d.artista}"/>
                        <c:param name="ano"       value="${d.anoLancamento}"/>
                        <c:param name="genero"    value="${d.genero}"/>
                        <c:param name="formato"   value="${d.formato}"/>
                        <c:param name="capa"      value="${d.imagemCapa}"/>
                    </c:url>
                    <article class="album-card" onclick="window.location.href='${detalheUrl}'">
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

    <%-- ===== Seção 2: Mais desejados ===== --%>
    <c:if test="${not empty discos}">
        <h3 class="section-title">Os discos e CDs mais desejados desta semana</h3>
        <div class="card-grid" style="margin-bottom: 2.5rem;">
            <c:forEach var="d" items="${discos}" begin="6" end="11">
                <c:url var="detalheUrl" value="/disco/abrir">
                    <c:param name="discogsId" value="${d.discogsId}"/>
                    <c:param name="titulo"    value="${d.titulo}"/>
                    <c:param name="artista"   value="${d.artista}"/>
                    <c:param name="ano"       value="${d.anoLancamento}"/>
                    <c:param name="genero"    value="${d.genero}"/>
                    <c:param name="formato"   value="${d.formato}"/>
                    <c:param name="capa"      value="${d.imagemCapa}"/>
                </c:url>
                <article class="album-card" onclick="window.location.href='${detalheUrl}'">
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
        </div>

        <%-- ===== Seção 3: Mais colecionados ===== --%>
        <h3 class="section-title">Os discos e CDs mais colecionados desta semana</h3>
        <div class="card-grid">
            <c:forEach var="d" items="${discos}" begin="12" end="17">
                <c:url var="detalheUrl" value="/disco/abrir">
                    <c:param name="discogsId" value="${d.discogsId}"/>
                    <c:param name="titulo"    value="${d.titulo}"/>
                    <c:param name="artista"   value="${d.artista}"/>
                    <c:param name="ano"       value="${d.anoLancamento}"/>
                    <c:param name="genero"    value="${d.genero}"/>
                    <c:param name="formato"   value="${d.formato}"/>
                    <c:param name="capa"      value="${d.imagemCapa}"/>
                </c:url>
                <article class="album-card" onclick="window.location.href='${detalheUrl}'">
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
        </div>
    </c:if>

</main>

</body>
</html>
