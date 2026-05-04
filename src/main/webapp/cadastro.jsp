<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Cadastro | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<div class="split-layout">
    <div class="split-left">
        <a href="${pageContext.request.contextPath}/listarFeedServlet"
           style="display: flex; align-items: center; gap: 0.5rem; font-weight: 700; font-size: 1.5rem;
                  margin-bottom: 4rem; color: var(--primary-text); text-decoration: none;">
            <i class="fa-solid fa-record-vinyl" style="color: var(--primary-accent); font-size: 2rem;"></i> MeuAcervo
        </a>

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
            <p style="color: var(--primary-accent); font-size: 0.75rem; font-weight: 600;
                      letter-spacing: 1px; margin-bottom: 0.5rem; text-transform: uppercase;">Bem-vindo</p>
            <h2 style="font-size: 1.8rem; margin-bottom: 0.5rem; font-weight: 500;">Crie sua conta</h2>
            <p style="color: var(--secondary-text); font-size: 0.9rem; margin-bottom: 2rem;">
                É rápido. Em menos de um minuto você estará pronto para montar seu acervo.
            </p>

            <c:if test="${param.erro == 'campos-vazios'}">
                <div class="alert alert-error">Preencha todos os campos obrigatórios.</div>
            </c:if>
            <c:if test="${param.erro == 'email-invalido'}">
                <div class="alert alert-error">Formato de e-mail inválido.</div>
            </c:if>
            <c:if test="${param.erro == 'senha-curta'}">
                <div class="alert alert-error">A senha precisa ter pelo menos 8 caracteres.</div>
            </c:if>
            <c:if test="${param.erro == 'email-existente'}">
                <div class="alert alert-error">Este e-mail já está cadastrado. Que tal fazer login?</div>
            </c:if>
            <c:if test="${param.erro == 'username-existente'}">
                <div class="alert alert-error">Este nome de usuário já está em uso.</div>
            </c:if>
            <c:if test="${param.erro == 'banco'}">
                <div class="alert alert-error">Erro ao acessar o banco. Tente novamente em instantes.</div>
            </c:if>

            <form id="form-cadastro" action="${pageContext.request.contextPath}/cadastroServlet" method="POST"
                  data-single-submit>
                <div class="form-group">
                    <label for="nome">Nome completo</label>
                    <input type="text" id="nome" name="nome" placeholder="Seu nome completo">
                </div>
                <div class="form-group">
                    <label for="username">
                        Nome de usuário
                        <span id="username-counter" class="char-counter">0/15</span>
                    </label>
                    <input type="text" id="username" name="username"
                           placeholder="seu_username" maxlength="15">
                </div>
                <div class="form-group">
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" placeholder="seu@email.com">
                </div>
                <div class="form-group">
                    <label for="senha">Senha</label>
                    <div class="input-senha-wrapper">
                        <input type="password" id="senha" name="senha"
                               placeholder="Mínimo 8 caracteres" minlength="8">
                        <button type="button" class="btn-toggle-senha" title="Mostrar senha">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                    </div>
                </div>

                <button type="submit" class="btn-primary">Criar minha conta</button>

                <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--secondary-text);">
                    Já tem conta?
                    <a href="${pageContext.request.contextPath}/login.jsp"
                       style="color: var(--primary-accent);">Entrar</a>
                </p>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
</body>
</html>