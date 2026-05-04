<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Ops | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<main class="container">
    <div style="text-align: center; padding: 4rem 1rem;">
        <i class="fa-solid fa-record-vinyl" style="font-size: 5rem; color: var(--primary-accent); margin-bottom: 1.5rem;"></i>
        <h1 style="font-size: 2rem; margin-bottom: 1rem;">Ops, algo não deu certo</h1>
        <p style="color: var(--secondary-text); margin-bottom: 2rem;">
            Não conseguimos encontrar o que você procurava. Que tal voltar para a Home?
        </p>
        <a href="${pageContext.request.contextPath}/listarFeedServlet" class="btn-primary"
           style="display: inline-block; max-width: 200px; text-align: center; text-decoration: none;">
            Voltar para a Home
        </a>
    </div>
</main>

</body>
</html>
