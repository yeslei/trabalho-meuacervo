<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Login | MeuAcervo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>

<div class="split-layout">
    <div class="split-left">
        <a href="${pageContext.request.contextPath}/index.jsp"
           style="display: flex; align-items: center; gap: 0.5rem; font-weight: 700; font-size: 1.5rem;
                  margin-bottom: 4rem; color: var(--primary-text); text-decoration: none;">
            <i class="fa-solid fa-record-vinyl" style="color: var(--primary-accent); font-size: 2rem;"></i> MeuAcervo
        </a>

        <h1 style="font-size: 2.5rem; margin-bottom: 1rem; line-height: 1.2;">
            Reviva seus <span style="color: var(--primary-accent);">álbuns</span><br>
            favoritos.
        </h1>
        <p style="color: var(--primary-text); font-size: 1.1rem; max-width: 400px;">
            Acompanhe sua coleção, registre momentos e descubra novos sons.
        </p>
    </div>

    <div class="split-right">
        <div class="auth-form-container">
            <p style="color: var(--primary-accent); font-size: 0.75rem; font-weight: 600;
                      letter-spacing: 1px; margin-bottom: 0.5rem; text-transform: uppercase;">Acesso</p>
            <h2 style="font-size: 1.8rem; margin-bottom: 0.5rem; font-weight: 500;">Entre na sua conta</h2>
            <p style="color: var(--secondary-text); font-size: 0.9rem; margin-bottom: 2rem;">
                Bem-vindo de volta, continue de onde parou
            </p>

            <c:if test="${param.sucesso == 'logout'}">
                <div class="alert alert-success">Você saiu da sua conta. Até logo!</div>
            </c:if>
            <c:if test="${param.erro == 'login-invalido'}">
                <div class="alert alert-error">E-mail ou senha incorretos.</div>
            </c:if>
            <c:if test="${param.erro == 'campos-vazios'}">
                <div class="alert alert-error">Preencha todos os campos.</div>
            </c:if>
            <c:if test="${param.erro == 'nao-autenticado'}">
                <div class="alert alert-error">Você precisa estar logado para acessar essa página.</div>
            </c:if>
            <c:if test="${param.erro == 'banco'}">
                <div class="alert alert-error">Erro ao acessar o banco. Tente novamente em instantes.</div>
            </c:if>

            <form id="form-login" action="${pageContext.request.contextPath}/login" method="POST"
                  data-single-submit>
                <div class="form-group">
                    <label for="email">E-mail</label>
                    <input type="email" id="email" name="email" placeholder="seu@email.com">
                </div>
                <div class="form-group">
                    <label for="senha">Senha</label>
                    <div class="input-senha-wrapper">
                        <input type="password" id="senha" name="senha" placeholder="••••••••">
                        <button type="button" class="btn-toggle-senha" title="Mostrar senha">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div style="display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1.5rem;">
                    <input type="checkbox" id="lembrar" name="lembrar"
                           style="accent-color: var(--primary-accent);">
                    <label for="lembrar" style="color: var(--secondary-text); font-size: 0.85rem;">Manter conectado</label>
                </div>

                <button type="submit" class="btn-primary">Entrar</button>

                <p style="text-align: center; margin-top: 1.5rem; font-size: 0.9rem; color: var(--secondary-text);">
                    Ainda não tem conta?
                    <a href="${pageContext.request.contextPath}/cadastro.jsp"
                       style="color: var(--primary-accent);">Criar conta</a>
                </p>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/js/validation.js"></script>
</body>
</html>
