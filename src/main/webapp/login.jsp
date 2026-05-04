<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MeuAcervo - Entrar</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
</head>
<body>
<div class="page">
    <section class="hero">
        <div class="brand">
            <span class="brand-mark" aria-hidden="true"></span>
            <span>MeuAcervo</span>
        </div>

        <div class="hero-copy">
            <h1>Reviva seus <span>albuns</span> favoritos.</h1>
            <p>Acompanhe sua colecao, registre momentos e descubra novos sons.</p>

            <div class="hero-points">
                <div class="point">
                    <span class="point-icon">&#9733;</span>
                    <div>
                        <strong>Catalogue cada disco</strong>
                        Organize sua colecao e veja tudo com detalhes rapidos.
                    </div>
                </div>
                <div class="point">
                    <span class="point-icon">&#9829;</span>
                    <div>
                        <strong>Conecte com a comunidade</strong>
                        Reviews, favoritos e descobertas compartilhadas com outros colecionadores.
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="panel">
        <div class="panel-card">
            <div class="eyebrow">ACESSO</div>
            <h2>Entre na sua conta</h2>
            <p>Bem-vindo de volta, continue de onde parou.</p>

            <c:if test="${param.sucesso == 'cadastro-realizado'}">
                <div class="alert success">Cadastro realizado. Agora faca seu login.</div>
            </c:if>
            <c:if test="${param.erro == 'login-invalido'}">
                <div class="alert error">E-mail ou senha incorretos.</div>
            </c:if>
            <c:if test="${param.erro == 'banco'}">
                <div class="alert error">Erro ao acessar o banco de dados.</div>
            </c:if>

            <form method="post" action="<%= request.getContextPath() %>/login">
                <div class="field">
                    <label for="email">E-mail</label>
                    <input id="email" name="email" type="email" placeholder="seu@email.com" required>
                </div>
                <div class="field">
                    <label for="senha">Senha</label>
                    <input id="senha" name="senha" type="password" placeholder="Sua senha" required>
                </div>

                <label class="helper">
                    <input type="checkbox" name="lembrar" value="true">
                    Manter conectado
                </label>

                <button class="submit" type="submit">Entrar</button>
            </form>

            <div class="form-footer">
                Ainda nao tem conta? <a href="<%= request.getContextPath() %>/cadastro.jsp">Criar conta</a>
            </div>
        </div>
    </section>
</div>
</body>
</html>
