<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

<header class="top-header">
    <a href="listarFeedServlet" class="logo-container">
        <i class="fa-solid fa-record-vinyl logo-icon"></i> MeuAcervo
    </a>

    <div class="search-bar">
        <form action="buscar" method="GET">
            <i class="fa-solid fa-magnifying-glass" style="position: absolute; left: 1rem; top: 50%; transform: translateY(-50%); color: var(--secondary-text);"></i>
            <input type="text" name="q" placeholder="Procure por artistas, álbuns e mais...">
        </form>
    </div>

    <nav class="nav-icons">
        <a href="listarFeedServlet" title="Home"><i class="fa-solid fa-house"></i></a>
        <a href="favoritos.jsp" title="Favoritos"><i class="fa-regular fa-heart"></i></a>
        <a href="colecao.jsp" title="Minha Coleção"><i class="fa-solid fa-compact-disc"></i></a>

        <c:choose>
            <c:when test="${not empty sessionScope.usuarioLogado}">
                <a href="perfil.jsp" title="${sessionScope.usuarioLogado.nome}">
                    <i class="fa-regular fa-user"></i>
                </a>
                <a href="logoutServlet" title="Sair" style="background: var(--primary-accent); color: #000; margin-left: 10px;">
                    <i class="fa-solid fa-right-from-bracket"></i>
                </a>
            </c:when>
            <c:otherwise>
                <a href="login.jsp" title="Entrar"><i class="fa-solid fa-sign-in-alt"></i></a>
            </c:otherwise>
        </c:choose>
    </nav>
</header>