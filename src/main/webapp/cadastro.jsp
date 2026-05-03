<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Cadastro | MeuAcervo</title>
    <link rel="stylesheet" href="assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<div class="split-layout">
    <div class="split-left">
        <div style="display: flex; align-items: center; gap: 0.5rem; font-weight: 700; font-size: 1.5rem; margin-bottom: 4rem; color: var(--primary-text);">
            <i class="fa-solid fa-record-vinyl" style="color: var(--primary-accent); font-size: 2rem;"></i> MeuAcervo
        </div>

        <h1 style="font-size: 2.5rem; margin-bottom: 1rem; line-height: 1.2;">
            Comece a montar<br>
            <span style="color: var(--primary-accent);">seu acervo</span>
        </h1>
        <p style="color: var(--primary-text); font-size: 1.1rem; max-width: 400px;">
            Registre seus discos, avalie álbuns e acompanhe sua evolução musical.
        </p>
    </div>

    <div class="split-right">
        <div class="auth-form-container">
            <p style="color: var(--primary-accent); font-size: 0.75rem; font-weight: 600; letter-spacing: 1px; margin-bottom: 0.5rem; text-transform: uppercase;">Bem-vindo</p>
            <h2 style="font-size: 1.8rem; margin-bottom: 0.5rem; font-weight: 500;">Crie sua conta</h2>
            <p style="color: var(--secondary-text); font-size: 0.9rem; margin-bottom: 2rem;">É rápido. Em menos de um minuto você estará pronto para montar seu acervo.</p>

            <form action="cadastroServlet" method="POST">
                <div class="form-group">
                    <label for="nome">Nome completo</label>
                    <input type="text" id="nome" name="nome" placeholder="Seu nome completo" required>
                </div>
                <div class="form-group">
                    <label for="username">Nome de usuário</label>
                    <input type="text" id="username" name="username" placeholder="seu_username" required>
                </div>
                <div class="form-group">
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" placeholder="seu@email.com" required>
                </div>
                <div class="form-group">
                    <label for="senha">Senha</label>
                    <input type="password" id="senha" name="senha" placeholder="Mínimo 8 caracteres" required>
                </div>

                <c:if test="${not empty erro}">
                    <p style="color: #ff4d4d; font-size: 0.85rem; margin-bottom: 1rem;">${erro}</p>
                </c:if>

                <button type="submit" class="btn-primary">Criar minha conta</button>
                <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--secondary-text);">
                    Já tem conta? <a href="login.jsp">Entrar</a>
                </p>
            </form>
        </div>
    </div>
</div>

</body>
</html>