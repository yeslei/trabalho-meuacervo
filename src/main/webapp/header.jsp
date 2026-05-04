<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<header class="top-header">
    <a href="${pageContext.request.contextPath}/index.jsp" class="logo-container">
        <i class="fa-solid fa-record-vinyl logo-icon"></i> MeuAcervo
    </a>

    <div class="search-bar">
        <form action="${pageContext.request.contextPath}/buscar-discos" method="GET">
            <i class="fa-solid fa-magnifying-glass"
               style="position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: var(--secondary-text);"></i>
            <input type="text" name="q" placeholder="Procure por artistas, Ã¡lbuns e mais..." value="${param.q}">
        </form>
    </div>

    <nav class="nav-icons">
        <a href="${pageContext.request.contextPath}/index.jsp" title="Home">
            <i class="fa-solid fa-house"></i>
        </a>

        <c:choose>
            <c:when test="${not empty sessionScope.usuarioLogado}">
                <a href="${pageContext.request.contextPath}/colecao/ver" title="Minha ColeÃ§Ã£o">
                    <i class="fa-solid fa-compact-disc"></i>
                </a>
                <a href="${pageContext.request.contextPath}/feed" title="Meus Reviews">
                    <i class="fa-solid fa-pen"></i>
                </a>
                <a href="${pageContext.request.contextPath}/wishlist/listar" title="Favoritos">
                    <i class="fa-regular fa-heart"></i>
                </a>
                <a href="${pageContext.request.contextPath}/colecao/ver" title="${sessionScope.usuarioLogado.nome}">
                    <i class="fa-regular fa-user"></i>
                </a>
                <a href="${pageContext.request.contextPath}/logout"
                   title="Sair"
                   style="background: var(--primary-accent); color: #1a1a1a; margin-left: 0.4rem;">
                    <i class="fa-solid fa-right-from-bracket"></i>
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login.jsp" title="Entrar">
                    <i class="fa-solid fa-right-to-bracket"></i>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>
<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
